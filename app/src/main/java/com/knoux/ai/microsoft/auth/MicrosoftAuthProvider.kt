package com.knoux.ai.microsoft.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.knoux.ai.auth.OAuthRedirectRelay
import com.knoux.ai.cloud.model.CloudAccount
import com.knoux.ai.cloud.model.CloudProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Microsoft OAuth 2.0 authentication provider using MSAL Android.
 * Supports Outlook.com, Hotmail, Live, Microsoft 365, and School accounts.
 * Uses Microsoft Graph v1.0 for all API access.
 */
class MicrosoftAuthProvider(
    private val context: Context,
    private val clientId: String,
    private val tenantId: String = "common",
    private val redirectUri: String,
) {

    private companion object {
        const val GRAPH_BASE = "https://graph.microsoft.com/v1.0"
    }

    /** Scopes for Outlook email access. */
    val mailScopes = listOf(
        "Mail.Read",
        "Mail.ReadWrite",
        "User.Read",
        "offline_access",
    )

    /** Scopes for OneDrive access. */
    val driveScopes = listOf(
        "Files.Read",
        "Files.ReadWrite",
        "User.Read",
        "offline_access",
    )

    private var authCallback: ((MicrosoftAuthResult) -> Unit)? = null
    private var state: String? = null
    private var codeVerifier: String? = null

    /**
     * Start MSAL authorization code flow with PKCE.
     */
    fun authorize(
        activity: Activity,
        scopes: List<String>,
        onResult: (MicrosoftAuthResult) -> Unit,
    ) {
        authCallback = onResult
        state = generateState()
        codeVerifier = generateCodeVerifier()
        OAuthRedirectRelay.register(state!!) { redirect -> handleRedirect(redirect) }

        val scopeString = scopes.distinct().joinToString(" ")
        val challenge = generateCodeChallenge(codeVerifier!!)

        val authUrl = Uri.Builder()
            .scheme("https")
            .authority("login.microsoftonline.com")
            .path("/$tenantId/oauth2/v2.0/authorize")
            .appendQueryParameter("client_id", clientId)
            .appendQueryParameter("redirect_uri", redirectUri)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("scope", scopeString)
            .appendQueryParameter("response_mode", "query")
            .appendQueryParameter("state", state!!)
            .appendQueryParameter("code_challenge", challenge)
            .appendQueryParameter("code_challenge_method", "S256")
            .appendQueryParameter("prompt", "consent")
            .build()

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setData(authUrl)
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
        }

        activity.startActivity(intent)
    }

    /** Handle the redirect from Microsoft OAuth. */
    fun handleRedirect(intent: Intent?): Boolean {
        val data = intent?.data ?: return false
        val returnedCode = data.getQueryParameter("code")
        val returnedState = data.getQueryParameter("state")
        val error = data.getQueryParameter("error")

        if (error != null) {
            authCallback?.invoke(MicrosoftAuthResult.Error("Auth error: $error"))
            return true
        }

        if (returnedState != state || returnedCode == null) {
            authCallback?.invoke(MicrosoftAuthResult.Error("Invalid state or missing code"))
            return true
        }

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                try {
                    val tokenResponse = exchangeCodeForTokens(returnedCode!!)
                    authCallback?.invoke(MicrosoftAuthResult.Success(tokenResponse))
                } catch (e: Exception) {
                    authCallback?.invoke(MicrosoftAuthResult.Error(e.message ?: "Token exchange failed"))
                }
            }
        }

        return true
    }

    private suspend fun exchangeCodeForTokens(code: String): MicrosoftTokenResponse {
        val tokenEndpoint = "https://login.microsoftonline.com/$tenantId/oauth2/v2.0/token"
        val url = java.net.URL(tokenEndpoint)
        val client = url.openConnection() as java.net.HttpURLConnection
        client.requestMethod = "POST"
        client.doOutput = true
        client.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

        val scopeString = (mailScopes + driveScopes).distinct().joinToString(" ")
        val body = "client_id=${java.net.URLEncoder.encode(clientId, "UTF-8")}" +
            "&scope=${java.net.URLEncoder.encode(scopeString, "UTF-8")}" +
            "&code=${java.net.URLEncoder.encode(code, "UTF-8")}" +
            "&redirect_uri=${java.net.URLEncoder.encode(redirectUri, "UTF-8")}" +
            "&grant_type=authorization_code" +
            "&code_verifier=${java.net.URLEncoder.encode(codeVerifier ?: "", "UTF-8")}"

        client.outputStream.use { it.write(body.toByteArray()) }

        val responseCode = client.responseCode
        val responseStream = if (responseCode in 200..299) client.inputStream else client.errorStream
        val responseBody = responseStream?.bufferedReader(java.nio.charset.StandardCharsets.UTF_8)?.use { it.readText() } ?: ""

        client.disconnect()

        if (responseCode !in 200..299) {
            // Never echo the raw provider body: it can contain request PII.
            throw Exception("Token exchange failed (HTTP $responseCode)")
        }

        return parseTokenResponse(responseBody)
    }

    private fun parseTokenResponse(json: String): MicrosoftTokenResponse =
        MicrosoftTokenParser.parse(json, System.currentTimeMillis())

    /** Revoke a refresh token. */
    suspend fun revokeToken(token: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val logoutUrl = "https://login.microsoftonline.com/$tenantId/oauth2/v2.0/logout"
                val client = java.net.URL(logoutUrl).openConnection() as java.net.HttpURLConnection
                client.requestMethod = "POST"
                client.doOutput = true
                client.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                val body = "refresh_token=$token"
                client.outputStream.use { it.write(body.toByteArray()) }

                val responseCode = client.responseCode
                client.disconnect()
                responseCode in 200..299
            } catch (_: Exception) {
                false
            }
        }
    }

    private fun generateState(): String {
        val random = java.security.SecureRandom()
        val bytes = ByteArray(16)
        random.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun generateCodeVerifier(): String {
        val random = java.security.SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun generateCodeChallenge(verifier: String): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(verifier.toByteArray())
        return java.util.Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(hash)
    }

    data class MicrosoftAuthResult(
        val success: Boolean = false,
        val tokenResponse: MicrosoftTokenResponse? = null,
        val error: String? = null,
    ) {
        companion object {
            fun Success(token: MicrosoftTokenResponse) = MicrosoftAuthResult(success = true, tokenResponse = token)
            fun Error(message: String) = MicrosoftAuthResult(success = false, error = message)
        }
    }

    data class MicrosoftTokenResponse(
        val accessToken: String,
        val refreshToken: String?,
        val expiresInSeconds: Long,
        val tokenType: String,
        val scope: String,
        val obtainedAtMs: Long,
    ) {
        val isExpired: Boolean get() =
            System.currentTimeMillis() > obtainedAtMs + (expiresInSeconds * 1000) - 60_000

        val expiresAtMs: Long get() = obtainedAtMs + (expiresInSeconds * 1000)
    }

    /**
     * Read the signed-in account identity from Microsoft Graph. Returns null
     * on failure; callers must not invent an email.
     */
    suspend fun fetchUserInfo(accessToken: String): MicrosoftUserInfo? = withContext(Dispatchers.IO) {
        var connection: java.net.HttpURLConnection? = null
        try {
            connection = (java.net.URL("$GRAPH_BASE/me?%24select=mail,userPrincipalName,displayName")
                .openConnection() as java.net.HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 15_000
                readTimeout = 20_000
                instanceFollowRedirects = false
                setRequestProperty("Authorization", "Bearer $accessToken")
                setRequestProperty("Accept", "application/json")
            }
            if (connection.responseCode !in 200..299) return@withContext null
            val payload = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            val json = org.json.JSONObject(payload)
            val email = json.optString("mail").takeIf { it.isNotBlank() }
                ?: json.optString("userPrincipalName").takeIf { it.isNotBlank() }
                ?: return@withContext null
            MicrosoftUserInfo(
                email = email,
                displayName = json.optString("displayName").takeIf { it.isNotBlank() },
            )
        } catch (_: Exception) {
            null
        } finally {
            connection?.disconnect()
        }
    }

    data class MicrosoftUserInfo(val email: String, val displayName: String?)

    /** Convert auth result to CloudAccount. */
    fun accountFromResult(
        email: String,
        displayName: String?,
        tokenResponse: MicrosoftTokenResponse,
    ): CloudAccount {
        return CloudAccount(
            id = "microsoft_$email",
            provider = CloudProvider.Outlook,
            email = email,
            displayName = displayName,
            isConnected = true,
            scopesGranted = tokenResponse.scope.split(" ").filter { it.isNotEmpty() },
            tokenExpiresAt = tokenResponse.expiresAtMs,
            connectedAt = System.currentTimeMillis(),
        )
    }
}

/**
 * ActivityResultLauncher contract for Microsoft OAuth redirects.
 */
class MicrosoftAuthResultContract : ActivityResultContract<Unit, Intent?>() {
    override fun createIntent(context: Context, input: Unit): Intent {
        return Intent(context, MicrosoftAuthRedirectActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Intent? = intent
}

class MicrosoftAuthRedirectActivity : Activity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        OAuthRedirectRelay.dispatch(intent)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        OAuthRedirectRelay.dispatch(intent)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}