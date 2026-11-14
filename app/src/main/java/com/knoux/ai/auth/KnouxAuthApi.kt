package com.knoux.ai.auth

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/** No external session impersonation: every cloud sign-in is verified by Supabase Auth. */
class KnouxAuthException(val code: String, message: String): Exception(message)

class KnouxAuthApi(context: Context, val config: KnouxAuthConfig) {
    private val store=KnouxSessionStore(context)
    private val gate=Any()
    internal fun tokenOrNull(): String? = store.read()?.accessToken
    internal fun hasSession(): Boolean=store.read()!=null

    private suspend fun request(method:String, path:String, body:JSONObject?=null, bearer:String?=null):JSONObject =
        withContext(Dispatchers.IO) {
            require(path.startsWith('/') && !path.startsWith("//") && !path.contains(".."))
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
                if(status !in 200..299) throw KnouxAuthException(
                    result.optString("error_code",result.optString("code","http_$status")),
                    // Avoid returning provider message with tokens/PII to logs.
                    "Auth provider rejected the request (HTTP $status)")
                result
            } finally { c.disconnect() }
        }
    private fun acceptSession(obj:JSONObject):Boolean {
        val data=obj.optJSONObject("session")?:obj
        val a=data.optString("access_token")
        val r=data.optString("refresh_token")
        if(a.isBlank()||r.isBlank()) return false // Signup may require email confirmation.
        val expiry=data.optLong("expires_in",3600).coerceIn(1,86_400)
        store.save(KnouxTokens(a,r,System.currentTimeMillis()+expiry*1000))
        return true
    }
    private suspend fun acceptAndVerify(obj:JSONObject): Boolean {
        if(!acceptSession(obj)) return false
        return try { currentUserVerified();true } catch(e:Exception){store.clear();throw e}
    }
    suspend fun signInWithPassword(email:String,password:String):Boolean = acceptAndVerify(request(
        "POST","/token?grant_type=password",JSONObject().put("email",email.trim()).put("password",password)))
    suspend fun signUp(email:String,password:String):Boolean = acceptAndVerify(request(
        "POST","/signup",JSONObject().put("email",email.trim()).put("password",password)))
    suspend fun sendEmailOtp(email:String,createUser:Boolean=false) {
        request("POST","/otp",JSONObject().put("email",email.trim()).put("create_user",createUser))
    }
    suspend fun verifyEmailOtp(email:String,otp:String,otpType:String="email"):Boolean {
        require(otpType in setOf("email","signup","recovery"))
        return acceptAndVerify(request("POST","/verify",JSONObject()
            .put("email",email.trim()).put("token",otp.trim()).put("type",otpType)))
    }
    suspend fun requestPasswordReset(email:String) { request("POST","/recover",JSONObject().put("email",email.trim())) }
    suspend fun updatePassword(password:String) {
        val t=verifiedToken() ?: throw KnouxAuthException("not_authenticated","Sign-in required")
        request("PUT","/user",JSONObject().put("password",password),t)
    }
    suspend fun signInWithGoogleIdToken(idToken:String,nonce:String?=null):Boolean = acceptAndVerify(request(
        "POST","/token?grant_type=id_token",JSONObject()
            .put("provider","google").put("id_token",idToken).also { if(nonce!=null) it.put("nonce",nonce) }))

    private suspend fun verifiedToken():String? {
        val saved=store.read()?:return null
        if(System.currentTimeMillis()<saved.expiresAtMs-60_000) return saved.accessToken
        val refreshed=request("POST","/token?grant_type=refresh_token",JSONObject().put("refresh_token",saved.refreshToken))
        if(!acceptSession(refreshed)) throw KnouxAuthException("invalid_refresh","No session after refresh")
        return store.read()?.accessToken
    }
    suspend fun currentUserVerified():JSONObject {
        val token=verifiedToken()?:throw KnouxAuthException("not_authenticated","Account login required")
        return request("GET","/user",bearer=token).also {
            if(it.optString("id").isBlank()) throw KnouxAuthException("no_user","No valid user from server")
        }
    }
    suspend fun restoreIfValid():Boolean = try {currentUserVerified();true} catch(_:Exception){false}
    suspend fun signOut() {
        val token=store.read()?.accessToken
        try {if(token!=null) request("POST","/logout",JSONObject(),token)} finally {store.clear()}
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
