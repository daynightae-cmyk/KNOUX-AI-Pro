package com.knoux.ai.google.auth

import android.content.Context
import com.knoux.ai.cloud.model.CloudAccount
import com.knoux.ai.cloud.model.CloudProvider
import com.knoux.ai.developer.vault.CredentialVault
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Manages connected Google accounts and their OAuth tokens.
 */
class GoogleAccountManager(private val context: Context, private val vault: CredentialVault) {

    private companion object {
        const val PREFS_NAME = "knoux_google_accounts"
        const val ACCOUNT_PREFIX = "acc_"
        const val TOKEN_PREFIX = "token_"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var accounts: MutableMap<String, CloudAccount> = mutableMapOf()
    private var tokens: MutableMap<String, GoogleAuthProvider.GoogleTokenResponse> = mutableMapOf()

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        loadAccounts()
    }

    private fun loadAccounts() {
        scope.launch {
            withContext(Dispatchers.IO) {
                val allKeys = prefs.all.keys
                val accountKeys = allKeys.filter { it.startsWith(ACCOUNT_PREFIX) }
                for (key in accountKeys) {
                    val email = key.removePrefix(ACCOUNT_PREFIX)
                    val json = prefs.getString(key, "") ?: ""
                    val account = parseAccountFromJson(email, json)
                    if (account != null) {
                        accounts[account.id] = account
                        val tokenJson = prefs.getString("$TOKEN_PREFIX$email", null)
                        if (tokenJson != null) {
                            tokens[account.id] = parseTokenFromJson(tokenJson)
                        }
                    }
                }
            }
        }
    }

    fun getConnectedAccounts(): List<CloudAccount> = accounts.values.toList()

    fun getAccount(email: String): CloudAccount? = accounts.values.firstOrNull { it.email == email }

    suspend fun getValidAccessToken(account: CloudAccount): String? {
        return withContext(Dispatchers.IO) {
            val token = tokens[account.id] ?: return@withContext null
            if (token.isExpired) {
                val refreshed = refreshToken(account, token.refreshToken ?: "")
                if (refreshed != null) {
                    tokens[account.id] = refreshed
                    saveToken(account.email, refreshed)
                    return@withContext refreshed.accessToken
                } else {
                    return@withContext null
                }
            } else {
                return@withContext token.accessToken
            }
        }
    }

    suspend fun saveAccount(
        account: CloudAccount,
        tokenResponse: GoogleAuthProvider.GoogleTokenResponse,
    ) {
        withContext(Dispatchers.IO) {
            accounts[account.id] = account
            tokens[account.id] = tokenResponse

            prefs.edit()
                .putString("$ACCOUNT_PREFIX${account.email}", serializeAccount(account))
                .putString("$TOKEN_PREFIX${account.email}", serializeToken(tokenResponse))
                .apply()

            vault.storeCredential(
                com.knoux.ai.developer.model.ProviderConfig(
                    providerId = account.provider.toString(),
                    keystoreAlias = "google_${account.email}",
                    maskedKey = vault.maskKey(tokenResponse.accessToken),
                    isConfigured = true,
                ),
                tokenResponse.accessToken,
            )
        }
    }

    private fun saveToken(email: String, token: GoogleAuthProvider.GoogleTokenResponse) {
        prefs.edit()
            .putString("$TOKEN_PREFIX$email", serializeToken(token))
            .apply()
    }

    suspend fun removeAccount(account: CloudAccount, authProvider: GoogleAuthProvider) {
        withContext(Dispatchers.IO) {
            val token = tokens[account.id]
            if (token != null) {
                authProvider.revokeToken(token.accessToken)
            }

            accounts.remove(account.id)
            tokens.remove(account.id)

            prefs.edit()
                .remove("$ACCOUNT_PREFIX${account.email}")
                .remove("$TOKEN_PREFIX${account.email}")
                .apply()

            vault.removeCredential(account.provider.toString())
        }
    }

    private suspend fun refreshToken(
        account: CloudAccount,
        refreshToken: String,
    ): GoogleAuthProvider.GoogleTokenResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val client = java.net.URL("https://oauth2.googleapis.com/token").openConnection() as java.net.HttpURLConnection
                client.requestMethod = "POST"
                client.doOutput = true
                client.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                val body = "grant_type=refresh_token" +
                    "&refresh_token=$refreshToken" +
                    "&client_id=placeholder"

                client.outputStream.use { it.write(body.toByteArray()) }
                val responseCode = client.responseCode
                val stream = if (responseCode in 200..299) client.inputStream else client.errorStream
                val bodyText = stream?.bufferedReader(java.nio.charset.StandardCharsets.UTF_8)?.use { it.readText() } ?: ""
                client.disconnect()

                if (responseCode !in 200..299) return@withContext null

                val accessToken = extractField(bodyText, "access_token") ?: ""
                val expiresIn = extractField(bodyText, "expires_in")?.toLongOrNull() ?: 3600L

                GoogleAuthProvider.GoogleTokenResponse(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    expiresInSeconds = expiresIn,
                    tokenType = "Bearer",
                    scope = "",
                    obtainedAtMs = System.currentTimeMillis(),
                )
            } catch (_: Exception) {
                null
            }
        }
    }

    private fun serializeAccount(account: CloudAccount): String {
        return """
        |{
        |  "email": "${account.email}",
        |  "displayName": "${account.displayName ?: ""}",
        |  "provider": "${account.provider}",
        |  "connectedAt": ${account.connectedAt},
        |  "scopes": [${account.scopesGranted.joinToString { "\"$it\"" }}]
        |}
        """.trimMargin()
    }

    private fun serializeToken(token: GoogleAuthProvider.GoogleTokenResponse): String {
        return """
        |{
        |  "accessToken": "${token.accessToken}",
        |  "refreshToken": "${token.refreshToken ?: ""}",
        |  "expiresInSeconds": ${token.expiresInSeconds},
        |  "obtainedAtMs": ${token.obtainedAtMs}
        |}
        """.trimMargin()
    }

    private fun parseAccountFromJson(email: String, json: String): CloudAccount? {
        try {
            return CloudAccount(
                id = "google_$email",
                provider = CloudProvider.Gmail,
                email = email,
                displayName = extractField(json, "displayName")?.takeIf { it.isNotEmpty() },
                isConnected = true,
                scopesGranted = extractField(json, "scopes")
                    ?.removeSurrounding("[", "]")
                    ?.split(",")
                    ?.map { it.trim().removeSurrounding("\"") }
                    ?.filter { it.isNotEmpty() } ?: emptyList(),
                tokenExpiresAt = extractField(json, "tokenExpiresAt")?.toLongOrNull() ?: 0L,
                connectedAt = extractField(json, "connectedAt")?.toLongOrNull() ?: System.currentTimeMillis(),
            )
        } catch (_: Exception) {
            return null
        }
    }

    private fun parseTokenFromJson(json: String): GoogleAuthProvider.GoogleTokenResponse {
        return GoogleAuthProvider.GoogleTokenResponse(
            accessToken = extractField(json, "accessToken") ?: "",
            refreshToken = extractField(json, "refreshToken")?.takeIf { it.isNotEmpty() },
            expiresInSeconds = extractField(json, "expiresInSeconds")?.toLongOrNull() ?: 3600L,
            tokenType = "Bearer",
            scope = "",
            obtainedAtMs = extractField(json, "obtainedAtMs")?.toLongOrNull() ?: System.currentTimeMillis(),
        )
    }

    private fun extractField(json: String, field: String): String? {
        val regex = kotlin.text.Regex("\"$field\"\\s*:\\s*\"([^\"]*)\"")
        return regex.find(json)?.groupValues?.getOrNull(1)
    }

    fun isConnected(email: String): Boolean = accounts.values.any { it.email == email }

    fun primaryAccount(): CloudAccount? = accounts.values.firstOrNull()
}