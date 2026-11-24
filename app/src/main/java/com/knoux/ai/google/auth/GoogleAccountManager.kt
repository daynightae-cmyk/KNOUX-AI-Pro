package com.knoux.ai.google.auth

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
 * Manages connected Google accounts and their OAuth tokens.
 *
 * Only non-secret account metadata (email, display name, scopes, timestamps)
 * is kept in SharedPreferences. Access and refresh tokens are stored as one
 * AES-256-GCM blob in [CredentialVault]; the Keystore key never leaves the
 * device. Legacy plaintext `token_*` entries from older builds are deleted on
 * first construction.
 */
class GoogleAccountManager(
    private val context: Context,
    private val vault: CredentialVault,
    private val clientId: String = BuildConfig.GOOGLE_ANDROID_CLIENT_ID,
) {

    private companion object {
        const val PREFS_NAME = "knoux_google_accounts"
        const val ACCOUNT_PREFIX = "acc_"
        const val LEGACY_TOKEN_PREFIX = "token_"
        const val TOKEN_KEY_PREFIX = "oauth_google_"
        const val TOKEN_ENDPOINT = "https://oauth2.googleapis.com/token"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val accounts = LinkedHashMap<String, CloudAccount>()

    init {
        loadAccounts()
        purgeLegacyPlaintextTokens()
    }

    /** True only when a real installed-app OAuth client is configured. */
    val isConfigured: Boolean
        get() = clientId.isNotBlank() &&
            clientId.endsWith(".apps.googleusercontent.com") &&
            !clientId.contains("placeholder", ignoreCase = true)

    private fun loadAccounts() {
        prefs.all.forEach { (key, value) ->
            if (key.startsWith(ACCOUNT_PREFIX) && value is String) {
                // Always read the e-mail from the persisted payload, never by
                // stripping the slot prefix off the key: e-mails may contain a
                // leading "gmail_"/"drive_" and that would corrupt the address.
                val email = runCatching { JSONObject(value).optString("email") }
                    .getOrNull()
                    ?.takeIf { it.isNotEmpty() }
                    ?: key.removePrefix(ACCOUNT_PREFIX)
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
        tokenResponse: GoogleAuthProvider.GoogleTokenResponse,
    ) {
        withContext(Dispatchers.IO) {
            accounts[account.id] = account
            val slotKey = "$ACCOUNT_PREFIX${GoogleAccountPolicy.slotKey(account.provider)}_${account.email}"
            prefs.edit()
                .putString(slotKey, serializeAccount(account))
                // Drop an unslotted legacy row for the same e-mail so a reload
                // cannot resurrect the pre-split single Gmail slot.
                .remove("$ACCOUNT_PREFIX${account.email}")
                .commit()
            saveToken(account.email, tokenResponse)
        }
    }

    suspend fun removeAccount(account: CloudAccount, authProvider: GoogleAuthProvider) {
        withContext(Dispatchers.IO) {
            loadToken(account.email)?.let { token ->
                authProvider.revokeToken(token.refreshToken ?: token.accessToken)
            }
            accounts.remove(account.id)
            val slotKeys = prefs.all.keys.filter { key ->
                key.startsWith(ACCOUNT_PREFIX) && key.endsWith("_${account.email}")
            }
            val editor = prefs.edit()
            slotKeys.forEach { editor.remove(it) }
            editor.remove("$ACCOUNT_PREFIX${account.email}")
            editor.commit()
            // One consent row per provider; only the token is shared by e-mail.
            if (accounts.values.none { it.email == account.email }) {
                vault.removeCredential(tokenKey(account.email))
            }
        }
    }

    private fun tokenKey(email: String): String = "$TOKEN_KEY_PREFIX$email"

    /**
     * Provider of a persisted account row. Rows written before the per-provider
     * split had no `provider` field and are Gmail rows.
     */
    private fun slotProvider(raw: String): CloudProvider = when (raw) {
        CloudProvider.GoogleDrive.name -> CloudProvider.GoogleDrive
        else -> CloudProvider.Gmail
    }

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

    private suspend fun saveToken(email: String, token: GoogleAuthProvider.GoogleTokenResponse) {
        val json = JSONObject()
            .put("accessToken", token.accessToken)
            .put("refreshToken", token.refreshToken ?: "")
            .put("expiresInSeconds", token.expiresInSeconds)
            .put("obtainedAtMs", token.obtainedAtMs)
            .toString()
        vault.storeCredential(
            ProviderConfig(
                providerId = tokenKey(email),
                keystoreAlias = "google_oauth_$email",
                maskedKey = "oauth-token",
                isConfigured = true,
            ),
            json,
        )
    }

    private suspend fun refreshToken(refreshToken: String): GoogleAuthProvider.GoogleTokenResponse? {
        return withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            try {
                connection = (URL(TOKEN_ENDPOINT).openConnection() as HttpURLConnection).apply {
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
                }
                connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }

                val status = connection.responseCode
                val stream = if (status in 200..299) connection.inputStream else connection.errorStream
                val payload = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
                if (status !in 200..299) return@withContext null

                val json = JSONObject(payload)
                val accessToken = json.optString("access_token")
                if (accessToken.isBlank()) return@withContext null
                GoogleAuthProvider.GoogleTokenResponse(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
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
        val provider = slotProvider(obj.optString("provider"))
        CloudAccount(
            id = GoogleAccountPolicy.accountId(email, provider),
            provider = provider,
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
