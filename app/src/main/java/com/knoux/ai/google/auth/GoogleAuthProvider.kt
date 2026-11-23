package com.knoux.ai.google.auth

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
import java.security.SecureRandom
import java.net.URLEncoder

/**
 * Google OAuth 2.0 authentication provider for Android.
 * Uses system browser / Chrome Custom Tabs for the authorization flow.
 * Supports incremental authorization and least-privilege scopes.
 */
class GoogleAuthProvider(
    private val context: Context,
    private val clientId: String,
    private val redirectUri: String,
) {

    private companion object {
        const val AUTHORIZATION_ENDPOINT = "https://accounts.google.com/o/oauth2/v2/auth"
        const val TOKEN_ENDPOINT = "https://oauth2.googleapis.com/token"
        const val REVOKE_ENDPOINT = "https://oauth2.googleapis.com/revoke"
    }

    /** Scopes for Gmail access. */
    val gmailScopes = listOf(
        "https://www.googleapis.com/auth/gmail.readonly",
        "https://www.googleapis.com/auth/gmail.modify",
    )

    /** Scopes for Google Drive access. */
    val driveScopes = listOf(
        "https://www.googleapis.com/auth/drive.file",
        "https://www.googleapis.com/auth/drive.readonly",
    )

    /** Scopes for Google Photos Picker (no OAuth needed, uses Picker API). */
    val photosScopes = emptyList<String>()

    private var state: String? = null
    private var codeVerifier: String? = null
    private var currentAuthActivity: Activity? = null
    private var authCallback: ((GoogleAuthResult) -> Unit)? = null

    /**
     * Start OAuth 2.0 authorization code flow with PKCE.
     */
    fun authorize(
        activity: Activity,
        scopes: List<String>,
        onResult: (GoogleAuthResult) -> Unit,
    ) {
        currentAuthActivity = activity
        authCallback = onResult
        state = generateState()
        codeVerifier = generateCodeVerifier()
        OAuthRedirectRelay.register(state!!) { redirect -> handleRedirect(redirect) }

        val scopeString = scopes.distinct().joinToString(" ")
        val challenge = generateCodeChallenge(codeVerifier!!)

        val authUrl = Uri.Builder()
            .scheme("https")
            .authority("accounts.google.com")
            .path("/o/oauth2/v2/auth")
            .appendQueryParameter("client_id", clientId)
            .appendQueryParameter("redirect_uri", redirectUri)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("scope", scopeString)
            .appendQueryParameter("access_type", "offline")
            .appendQueryParameter("include_granted_scopes", "true")
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

    /** Handle the redirect from Google OAuth. Call this from onNewIntent / ActivityResult. */
    fun handleRedirect(intent: Intent?): Boolean {
        val data = intent?.data ?: return false
        val returnedCode = data.getQueryParameter("code")
        val returnedState = data.getQueryParameter("state")
        val error = data.getQueryParameter("error")

        if (error != null) {
            authCallback?.invoke(GoogleAuthResult.Error("Auth error: $error"))
            return true
        }

        if (returnedState != state || returnedCode == null) {
            authCallback?.invoke(GoogleAuthResult.Error("Invalid state or missing code"))
            return true
        }

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                try {
                    val tokenResponse = exchangeCodeForTokens(returnedCode!!)
                    authCallback?.invoke(GoogleAuthResult.Success(tokenResponse))
                } catch (e: Exception) {
                    authCallback?.invoke(GoogleAuthResult.Error(e.message ?: "Token exchange failed"))
                }
            }
        }

        return true
    }

    private suspend fun exchangeCodeForTokens(code: String): GoogleTokenResponse {
        val url = java.net.URL(TOKEN_ENDPOINT)
        val client = url.openConnection() as java.net.HttpURLConnection
        client.requestMethod = "POST"
        client.doOutput = true
        client.connectTimeout = 15_000
        client.readTimeout = 20_000
        client.instanceFollowRedirects = false
        client.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

        val body = "code=${URLEncoder.encode(code, "UTF-8")}" +
            "&client_id=${URLEncoder.encode(clientId, "UTF-8")}" +
            "&redirect_uri=${URLEncoder.encode(redirectUri, "UTF-8")}" +
            "&grant_type=authorization_code" +
            "&code_verifier=${URLEncoder.encode(codeVerifier ?: "", "UTF-8")}"

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

    private fun parseTokenResponse(json: String): GoogleTokenResponse =
        GoogleTokenParser.parse(json, System.currentTimeMillis())

    /** Revoke an access token. */
    suspend fun revokeToken(token: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val client = java.net.URL(REVOKE_ENDPOINT).openConnection() as java.net.HttpURLConnection
                client.requestMethod = "POST"
                client.doOutput = true
                client.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                val body = "token=${URLEncoder.encode(token, "UTF-8")}"
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
        val random = SecureRandom()
        val bytes = ByteArray(16)
        random.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun generateCodeVerifier(): String {
        val random = SecureRandom()
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

    data class GoogleAuthResult(
        val success: Boolean = false,
        val tokenResponse: GoogleTokenResponse? = null,
        val error: String? = null,
    ) {
        companion object {
            fun Success(token: GoogleTokenResponse) = GoogleAuthResult(success = true, tokenResponse = token)
            fun Error(message: String) = GoogleAuthResult(success = false, error = message)
        }
    }

    data class GoogleTokenResponse(
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
     * Read the signed-in account identity from the OIDC userinfo endpoint.
     * Returns null on any failure; callers must not invent an email.
     */
    suspend fun fetchUserInfo(accessToken: String): GoogleUserInfo? = withContext(Dispatchers.IO) {
        var connection: java.net.HttpURLConnection? = null
        try {
            connection = (java.net.URL("https://openidconnect.googleapis.com/v1/userinfo")
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
            val email = json.optString("email").takeIf { it.isNotBlank() } ?: return@withContext null
            GoogleUserInfo(
                email = email,
                displayName = json.optString("name").takeIf { it.isNotBlank() },
            )
        } catch (_: Exception) {
            null
        } finally {
            connection?.disconnect()
        }
    }

    data class GoogleUserInfo(val email: String, val displayName: String?)

    /** Convert a successful auth result to a CloudAccount entity. */
    fun accountFromResult(
        email: String,
        displayName: String?,
        tokenResponse: GoogleTokenResponse,
    ): CloudAccount {
        return CloudAccount(
            id = "google_$email",
            provider = CloudProvider.Gmail,
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
 * ActivityResultLauncher contract for Google OAuth redirects.
 */
class GoogleAuthResultContract : ActivityResultContract<Unit, Intent?>() {
    override fun createIntent(context: android.content.Context, input: Unit): Intent {
        return Intent(context, GoogleAuthRedirectActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Intent? = intent
}

/**
 * Transparent activity that captures the OAuth redirect and finishes.
 */
class GoogleAuthRedirectActivity : Activity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        OAuthRedirectRelay.dispatch(intent)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        OAuthRedirectRelay.dispatch(intent)
        setResult(RESULT_OK, intent)
        finish()
    }
}