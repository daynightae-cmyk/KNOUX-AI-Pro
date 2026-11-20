package com.knoux.ai.microsoft.auth

import android.content.Context
import com.knoux.ai.BuildConfig
import com.knoux.ai.cloud.model.CloudAccount
import com.knoux.ai.cloud.model.CloudProvider
import com.knoux.ai.developer.model.ProviderConfig
import com.knoux.ai.developer.vault.CredentialVault
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * Manages connected Microsoft accounts and OAuth tokens.
 *
 * Account metadata is non-secret. Access and refresh tokens live encrypted in
 * [CredentialVault] (AES-256-GCM, Android Keystore). Legacy plaintext
 * `token_*` preference entries are purged on construction.
 */
class MicrosoftAccountManager(
    private val context: Context,
    private val vault: CredentialVault,
    private val clientId: String = BuildConfig.MICROSOFT_CLIENT_ID,
    private val tenantId: String = "common",
) {

    private companion object {
        const val PREFS_NAME = "knoux_microsoft_accounts"
        const val ACCOUNT_PREFIX = "acc_"
        const val LEGACY_TOKEN_PREFIX = "token_"
        const val TOKEN_KEY_PREFIX = "oauth_microsoft_"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val accounts = LinkedHashMap<String, CloudAccount>()

    init {
        loadAccounts()
        purgeLegacyPlaintextTokens()
    }

    /** True only when a real Entra application (client) ID is configured. */
    val isConfigured: Boolean
        get() = clientId.isNotBlank() && !clientId.contains("placeholder", ignoreCase = true)

    private fun loadAccounts() {
        prefs.all.forEach { (key, value) ->
            if (key.startsWith(ACCOUNT_PREFIX) && value is String) {
                val email = key.removePrefix(ACCOUNT_PREFIX)
                parseAccount(email, value)?.let { accounts[it.id] = it }
            }
        }
    }

    private fun purgeLegacyPlaintextTokens() {
        val legacy = prefs.all.keys.filter { it.startsWith(LEGACY_TOKEN_PREFIX) }
        if (legacy.isEmpty()) return
        val editor = prefs.edit()
        legacy.forEach { editor.remove(it) }
        editor.commit()
    }

    fun getConnectedAccounts(): List<CloudAccount> = accounts.values.toList()

    fun getAccount(email: String): CloudAccount? = accounts.values.firstOrNull { it.email == email }

    suspend fun getValidAccessToken(account: CloudAccount): String? {
        val token = loadToken(account.email) ?: return null
        if (!token.isExpired) return token.accessToken

        val refreshToken = token.refreshToken ?: return null
        if (!isConfigured) return null

        val refreshed = refreshToken(refreshToken) ?: return null
        saveToken(account.email, refreshed)
        return refreshed.accessToken
    }

    suspend fun saveAccount(
        account: CloudAccount,
        tokenResponse: MicrosoftAuthProvider.MicrosoftTokenResponse,
    ) {
        withContext(Dispatchers.IO) {
            accounts[account.id] = account
            prefs.edit()
                .putString("$ACCOUNT_PREFIX${account.email}", serializeAccount(account))
                .commit()
            saveToken(account.email, tokenResponse)
        }
    }

    suspend fun removeAccount(account: CloudAccount, authProvider: MicrosoftAuthProvider) {
        withContext(Dispatchers.IO) {
            loadToken(account.email)?.let { token ->
                authProvider.revokeToken(token.refreshToken ?: token.accessToken)
            }
            accounts.remove(account.id)
            prefs.edit().remove("$ACCOUNT_PREFIX${account.email}").commit()
            vault.removeCredential(tokenKey(account.email))
        }
    }

    private fun tokenKey(email: String): String = "$TOKEN_KEY_PREFIX$email"

    private suspend fun loadToken(email: String): StoredToken? {
        val raw = vault.getCredential(tokenKey(email)) ?: return null
        return runCatching {
            val json = JSONObject(raw)
            StoredToken(
                accessToken = json.getString("accessToken"),
                refreshToken = json.optString("refreshToken").takeIf { it.isNotEmpty() },
                expiresInSeconds = json.optLong("expiresInSeconds", 3600L),
                obtainedAtMs = json.optLong("obtainedAtMs", 0L),
            )
        }.getOrNull()
    }

    private suspend fun saveToken(email: String, token: MicrosoftAuthProvider.MicrosoftTokenResponse) {
        val json = JSONObject()
            .put("accessToken", token.accessToken)
            .put("refreshToken", token.refreshToken ?: "")
            .put("expiresInSeconds", token.expiresInSeconds)
            .put("obtainedAtMs", token.obtainedAtMs)
            .toString()
        vault.storeCredential(
            ProviderConfig(
                providerId = tokenKey(email),
                keystoreAlias = "microsoft_oauth_$email",
                maskedKey = "oauth-token",
                isConfigured = true,
            ),
            json,
        )
    }

    private suspend fun refreshToken(refreshToken: String): MicrosoftAuthProvider.MicrosoftTokenResponse? {
        return withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            try {
                val endpoint = "https://login.microsoftonline.com/$tenantId/oauth2/v2.0/token"
                connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    connectTimeout = 15_000
                    readTimeout = 20_000
                    instanceFollowRedirects = false
                    setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                }
                val body = buildString {
                    append("grant_type=refresh_token")
                    append("&refresh_token=").append(URLEncoder.encode(refreshToken, "UTF-8"))
                    append("&client_id=").append(URLEncoder.encode(clientId, "UTF-8"))
                    append("&scope=").append(
                        URLEncoder.encode("offline_access Mail.ReadWrite Files.ReadWrite User.Read", "UTF-8"),
                    )
                }
                connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }

                val status = connection.responseCode
                val stream = if (status in 200..299) connection.inputStream else connection.errorStream
                val payload = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
                if (status !in 200..299) return@withContext null

                val json = JSONObject(payload)
                val accessToken = json.optString("access_token")
                if (accessToken.isBlank()) return@withContext null
                MicrosoftAuthProvider.MicrosoftTokenResponse(
                    accessToken = accessToken,
                    refreshToken = json.optString("refresh_token").takeIf { it.isNotEmpty() } ?: refreshToken,
                    expiresInSeconds = json.optLong("expires_in", 3600L),
                    tokenType = json.optString("token_type", "Bearer"),
                    scope = json.optString("scope", ""),
                    obtainedAtMs = System.currentTimeMillis(),
                )
            } catch (_: Exception) {
                null
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun serializeAccount(account: CloudAccount): String {
        val scopes = JSONArray()
        account.scopesGranted.forEach { scopes.put(it) }
        return JSONObject()
            .put("email", account.email)
            .put("displayName", account.displayName ?: "")
            .put("provider", account.provider.name)
            .put("connectedAt", account.connectedAt)
            .put("scopes", scopes)
            .toString()
    }

    private fun parseAccount(email: String, json: String): CloudAccount? = runCatching {
        val obj = JSONObject(json)
        val scopes = obj.optJSONArray("scopes")?.let { array ->
            buildList { for (i in 0 until array.length()) add(array.optString(i)) }
        }.orEmpty()
        CloudAccount(
            id = "microsoft_$email",
            provider = CloudProvider.Outlook,
            email = email,
            displayName = obj.optString("displayName").takeIf { it.isNotEmpty() },
            isConnected = true,
            scopesGranted = scopes,
            tokenExpiresAt = 0L,
            connectedAt = obj.optLong("connectedAt", System.currentTimeMillis()),
        )
    }.getOrNull()

    fun isConnected(email: String): Boolean = accounts.values.any { it.email == email }

    fun primaryAccount(): CloudAccount? = accounts.values.firstOrNull()

    private data class StoredToken(
        val accessToken: String,
        val refreshToken: String?,
        val expiresInSeconds: Long,
        val obtainedAtMs: Long,
    ) {
        val isExpired: Boolean
            get() = System.currentTimeMillis() > obtainedAtMs + expiresInSeconds * 1000 - 60_000
    }
}
