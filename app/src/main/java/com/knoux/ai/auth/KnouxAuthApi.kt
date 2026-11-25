package com.knoux.ai.auth

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import com.knoux.ai.security.HttpStatusException
import com.knoux.ai.security.ProductionSecurity
import com.knoux.ai.security.secureRequest

/** No external session impersonation: every cloud sign-in is verified by Supabase Auth. */
class KnouxAuthException(val code: String, message: String): Exception(message)

class KnouxAuthApi(context: Context, val config: KnouxAuthConfig) {
    private val store=KnouxSessionStore(context)
    private val gate=Any()
    internal fun tokenOrNull(): String? = store.read()?.accessToken
    internal fun hasSession(): Boolean=store.read()!=null

    /** Rate limiter: tracks last request time per endpoint to prevent abuse. */
    private val lastRequestTime = ConcurrentHashMap<String, Long>()
    private val requestMutex = Mutex()
    private val MIN_REQUEST_INTERVAL_MS = 500L // 2 requests per second max per endpoint

    /** Rate limit check: ensures we don't hammer the auth endpoint. */
    private suspend fun rateLimitCheck(endpoint: String) {
        requestMutex.withLock {
            val now = System.currentTimeMillis()
            val last = lastRequestTime[endpoint] ?: 0L
            val elapsed = now - last
            if (elapsed < MIN_REQUEST_INTERVAL_MS) {
                lastRequestTime[endpoint] = now + (MIN_REQUEST_INTERVAL_MS - elapsed)
            } else {
                lastRequestTime[endpoint] = now
            }
        }
        val now = System.currentTimeMillis()
        val last = lastRequestTime[endpoint]
        if (last != null && last > now) {
            kotlinx.coroutines.delay(last - now)
        }
    }

    /** Validate email input before sending to server. */
    private fun validateEmail(email: String): String {
        val trimmed = email.trim()
        require(trimmed.isNotBlank()) { "Email is required" }
        require(ProductionSecurity.isValidEmail(trimmed)) { "Invalid email format" }
        require(trimmed.length <= 254) { "Email too long" }
        return trimmed
    }

    /** Validate password input. */
    private fun validatePassword(password: String): String {
        require(password.isNotBlank()) { "Password is required" }
        require(password.length >= 8) { "Password must be at least 8 characters" }
        require(password.length <= 128) { "Password too long" }
        return password
    }

    /** Validate idempotency key. */
    private fun validateIdempotencyKey(key: String?): String {
        if (key.isNullOrBlank()) return ProductionSecurity.generateIdempotencyKey()
        require(key.length <= 128) { "Idempotency key too long" }
        return key
    }

    /** Fast local-only validity: encrypted token present and not yet expired. No network. */
    internal suspend fun localSessionValid(): Boolean = withContext(Dispatchers.IO) {
        val saved = store.read() ?: return@withContext false
        SessionResponseParser.isLocallyValid(saved.expiresAtMs, System.currentTimeMillis())
    }

    private suspend fun request(
        method: String,
        path: String,
        body: JSONObject? = null,
        bearer: String? = null,
        idempotent: Boolean = method == "GET",
    ): JSONObject =
        secureRequest(maxRetries = 3, idempotent = idempotent) {
            rateLimitCheck(path)
            ProductionSecurity.validateRequestPath(path)
            ProductionSecurity.validateSupabaseUrl(config.baseAuthUrl)
            withContext(Dispatchers.IO) {
                val address=config.baseAuthUrl+path
                val url=URL(address)
                require(url.protocol=="https" && url.host==URL(config.supabaseUrl).host)
                val c=(url.openConnection() as HttpURLConnection).apply {
                    requestMethod=method;connectTimeout=15_000;readTimeout=20_000
                    instanceFollowRedirects=false
                    setRequestProperty("apikey",config.supabasePublishableKey)
                    setRequestProperty("Accept","application/json")
                    setRequestProperty("Content-Type","application/json")
                    if(bearer!=null) setRequestProperty("Authorization","Bearer $bearer")
                    if(body!=null){doOutput=true;outputStream.use{ it.write(body.toString().toByteArray(Charsets.UTF_8)) }}
                }
                try {
                    val status=c.responseCode
                    val stream=if(status in 200..299)c.inputStream else c.errorStream
                    val payload=stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
                    val result=runCatching { JSONObject(payload) }.getOrElse { JSONObject() }
                    if(status !in 200..299) {
                        val code = result.optString("error_code", result.optString("code", "http_$status"))
                        // Transient statuses (408/425/429/5xx) become HttpStatusException so
                        // secureRequest can retry idempotent calls; permanent 4xx surface as
                        // KnouxAuthException and are never replayed.
                        if (ProductionSecurity.isRetryableStatus(status)) {
                            throw HttpStatusException(status, "Auth provider transient failure (HTTP $status)")
                        }
                        // Known permanent "feature disabled" rejections keep their provider
                        // code and get an explicit message, so the UI can stop offering the
                        // method instead of showing a raw HTTP status (e.g. passkeys on a
                        // project with passkeys_enabled=false answer 404 passkey_disabled).
                        // Still never retried, still never treated as success.
                        val message = when (code) {
                            "passkey_disabled" -> "Passkeys are not enabled for this project"
                            "provider_disabled" -> "This sign-in provider is not enabled for this project"
                            else -> "Auth provider rejected the request (HTTP $status)"
                        }
                        throw KnouxAuthException(
                            code,
                            // Avoid returning provider message with tokens/PII to logs.
                            message)
                    }
                    result
                } finally { c.disconnect() }
            }
        }
    private fun acceptSession(obj:JSONObject):Boolean {
        val accepted = SessionResponseParser.parse(obj, System.currentTimeMillis()) ?: return false
        store.save(KnouxTokens(accepted.accessToken, accepted.refreshToken, accepted.expiresAtMs))
        return true
    }
    private suspend fun acceptAndVerify(obj:JSONObject): Boolean {
        if(!acceptSession(obj)) return false
        return try { currentUserVerified();true } catch(e:Exception){store.clear();throw e}
    }
    suspend fun signInWithPassword(email:String,password:String):Boolean = acceptAndVerify(request(
        "POST","/token?grant_type=password",JSONObject().put("email",validateEmail(email)).put("password",validatePassword(password))))
    suspend fun signUp(email:String,password:String):Boolean = acceptAndVerify(request(
        "POST","/signup",JSONObject().put("email",validateEmail(email)).put("password",validatePassword(password))))
    suspend fun sendEmailOtp(email:String,createUser:Boolean=false) {
        request("POST","/otp",JSONObject().put("email",validateEmail(email)).put("create_user",createUser))
    }
    suspend fun verifyEmailOtp(email:String,otp:String,otpType:String="email"):Boolean {
        require(otpType in setOf("email","signup","recovery")) { "Invalid OTP type" }
        require(otp.isNotBlank() && otp.length <= 10) { "Invalid OTP" }
        return acceptAndVerify(request("POST","/verify",JSONObject()
            .put("email",validateEmail(email)).put("token",otp.trim()).put("type",otpType)))
    }
    suspend fun requestPasswordReset(email:String) { request("POST","/recover",JSONObject().put("email",validateEmail(email))) }
    suspend fun updatePassword(password:String) {
        val t=verifiedToken() ?: throw KnouxAuthException("not_authenticated","Sign-in required")
        request("PUT","/user",JSONObject().put("password",validatePassword(password)),t)
    }
    suspend fun signInWithGoogleIdToken(idToken:String,nonce:String?=null):Boolean = acceptAndVerify(request(
        "POST","/token?grant_type=id_token",JSONObject()
            .put("provider","google").put("id_token",idToken).also { if(nonce!=null) it.put("nonce",nonce) }))

    /** Serializes refresh so parallel callers cannot spend the same rotating refresh token twice. */
    private val refreshMutex = Mutex()

    private suspend fun verifiedToken():String? = refreshMutex.withLock {
        val saved=store.read()?:return@withLock null
        if(SessionResponseParser.isLocallyValid(saved.expiresAtMs, System.currentTimeMillis())) return@withLock saved.accessToken
        val refreshed=try {
            request("POST","/token?grant_type=refresh_token",JSONObject().put("refresh_token",saved.refreshToken), idempotent = true)
        } catch(e:KnouxAuthException) {
            // Server permanently rejected the refresh token: the local session is
            // dead. Clear it so the UI returns to sign-in instead of looping.
            store.clear()
            throw e
        }
        if(!acceptSession(refreshed)) {
            store.clear()
            throw KnouxAuthException("invalid_refresh","No session after refresh")
        }
        store.read()?.accessToken
    }
    suspend fun currentUserVerified():JSONObject {
        val token=verifiedToken()?:throw KnouxAuthException("not_authenticated","Account login required")
        return request("GET","/user",bearer=token).also {
            if(it.optString("id").isBlank()) throw KnouxAuthException("no_user","No valid user from server")
        }
    }
    suspend fun restoreIfValid():Boolean = try {currentUserVerified();true} catch(_:Exception){false}

    // ---------------------------------------------------------- public settings
    // GET /auth/v1/settings is public (apikey only, no session): it reports which
    // sign-in methods this project has enabled. The login UI uses it so a disabled
    // provider is never offered — its routes answer permanent rejections.
    private var cachedSettings: AuthSettingsParser.AuthSettings? = null
    private var settingsFetchedAtMs = 0L

    suspend fun authSettings(forceRefresh: Boolean = false): AuthSettingsParser.AuthSettings {
        val now = System.currentTimeMillis()
        cachedSettings?.let { cached ->
            if (!forceRefresh && now - settingsFetchedAtMs < SETTINGS_CACHE_MS) return cached
        }
        return try {
            val parsed = AuthSettingsParser.parse(request("GET", "/settings"))
            cachedSettings = parsed
            settingsFetchedAtMs = now
            parsed
        } catch (_: Exception) {
            // Fail-safe: when the settings call itself fails we must not offer
            // providers we cannot confirm. Email stays the always-available path.
            AuthSettingsParser.failSafe
        }
    }

    private companion object {
        const val SETTINGS_CACHE_MS = 5 * 60_000L
    }
    /**
     * Clears the encrypted local session and asks Supabase to revoke the
     * refresh token. Returns true only when the server revocation succeeded;
     * the local token is always cleared because a local sign-out must not be
     * held hostage by a network failure. Callers must re-read [hasSession]
     * before claiming the user is signed out.
     */
    suspend fun signOut():Boolean {
        val token=store.read()?.accessToken
        var revoked=false
        try {
            if(token!=null){ request("POST","/logout",JSONObject(),token); revoked=true } else revoked=true
        } catch(_:Exception) {
            revoked=false
        } finally {
            store.clear()
        }
        return revoked
    }

    /** Experimental Supabase Auth passkey routes; server provides challenge and verifies WebAuthn. */
    internal suspend fun passkeyRegistrationOptions():JSONObject {
        val token=verifiedToken()?:throw KnouxAuthException("not_authenticated","Register only after login")
        return request("POST","/passkeys/registration/options",JSONObject(),token)
    }
    internal suspend fun verifyPasskeyRegistration(challengeId:String, credential:JSONObject):JSONObject {
        val token=verifiedToken()?:throw KnouxAuthException("not_authenticated","Login required")
        return request("POST","/passkeys/registration/verify",JSONObject()
            .put("challenge_id",challengeId).put("credential",credential),token)
    }
    internal suspend fun passkeyAuthenticationOptions():JSONObject = request(
        "POST","/passkeys/authentication/options",JSONObject())
    internal suspend fun verifyPasskeyAuthentication(challengeId:String,credential:JSONObject):Boolean =
        acceptAndVerify(request("POST","/passkeys/authentication/verify",JSONObject()
            .put("challenge_id",challengeId).put("credential",credential)))
    suspend fun listPasskeys():JSONObject {
        val token=verifiedToken()?:throw KnouxAuthException("not_authenticated","Login required")
        return request("GET","/passkeys",bearer=token)
    }
}
