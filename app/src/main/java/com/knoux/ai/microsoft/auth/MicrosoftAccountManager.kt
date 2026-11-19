package com.knoux.ai.microsoft.auth

import android.content.Context
import com.knoux.ai.cloud.model.CloudAccount
import com.knoux.ai.cloud.model.CloudProvider
import com.knoux.ai.developer.vault.CredentialVault
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Manages connected Microsoft accounts and OAuth tokens.
 * Tokens stored encrypted in Android Keystore via CredentialVault.
 */
class MicrosoftAccountManager(private val context: Context, private val vault: CredentialVault) {

    private companion object {
        const val PREFS_NAME = "knoux_microsoft_accounts"
        const val ACCOUNT_PREFIX = "acc_"
        const val TOKEN_PREFIX = "token_"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var accounts: MutableMap<String, CloudAccount> = mutableMapOf()
    private var tokens: MutableMap<String, MicrosoftAuthProvider.MicrosoftTokenResponse> = mutableMapOf()

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
        tokenResponse: MicrosoftAuthProvider.MicrosoftTokenResponse,
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
                    keystoreAlias = "microsoft_${account.email}",
                    maskedKey = vault.maskKey(tokenResponse.accessToken),
                    isConfigured = true,
                ),
                tokenResponse.accessToken,
            )
        }
    }

    private fun saveToken(email: String, token: MicrosoftAuthProvider.MicrosoftTokenResponse) {
        prefs.edit()
            .putString("$TOKEN_PREFIX$email", serializeToken(token))
            .apply()
    }

    suspend fun removeAccount(account: CloudAccount, authProvider: MicrosoftAuthProvider) {
        withContext(Dispatchers.IO) {
            val token = tokens[account.id]
            if (token != null) {
                authProvider.revokeToken(token.refreshToken ?: token.accessToken)
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
    ): MicrosoftAuthProvider.MicrosoftTokenResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val client = java.net.URL("https://login.microsoftonline.com/common/oauth2/v2.0/token").openConnection() as java.net.HttpURLConnection
                client.requestMethod = "POST"
                client.doOutput = true
                client.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                val body = "grant_type=refresh_token" +
                    "&refresh_token=$refreshToken" +
                    "&client_id=placeholder_client_id"

                client.outputStream.use { it.write(body.toByteArray()) }
                val responseCode = client.responseCode
                val stream = if (responseCode in 200..299) client.inputStream else client.errorStream
                val bodyText = stream?.bufferedReader(java.nio.charset.StandardCharsets.UTF_8)?.use { it.readText() } ?: ""
                client.disconnect()

                if (responseCode !in 200..299) return@withContext null

                val accessToken = extractField(bodyText, "access_token")
                val expiresIn = extractField(bodyText, "expires_in")?.toLongOrNull() ?: 3600L

                MicrosoftAuthProvider.MicrosoftTokenResponse(
                    accessToken = accessToken ?: "",
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

    private fun serializeToken(token: MicrosoftAuthProvider.MicrosoftTokenResponse): String {
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
                id = "microsoft_$email",
                provider = CloudProvider.Outlook,
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

    private fun parseTokenFromJson(json: String): MicrosoftAuthProvider.MicrosoftTokenResponse {
        return MicrosoftAuthProvider.MicrosoftTokenResponse(
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