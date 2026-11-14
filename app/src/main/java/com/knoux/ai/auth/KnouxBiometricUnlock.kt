package com.knoux.ai.auth

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/** Local presence gate ONLY. Cloud account remains tied to server-verified Supabase session. */
class KnouxBiometricUnlock(private val api:KnouxAuthApi) {
    suspend fun unlock(activity:FragmentActivity):Boolean {
        if(!api.hasSession()) throw KnouxAuthException("no_session","Sign in first using Google, Passkey or OTP")
        val authenticators=BiometricManager.Authenticators.BIOMETRIC_STRONG
        if(BiometricManager.from(activity).canAuthenticate(authenticators)!=BiometricManager.BIOMETRIC_SUCCESS)
            throw KnouxAuthException("biometric_unavailable","Strong biometric not enrolled/available")
        val biometricOk=suspendCancellableCoroutine<Boolean> { cont ->
            val prompt=BiometricPrompt(activity,ContextCompat.getMainExecutor(activity),
                object:BiometricPrompt.AuthenticationCallback(){
                    override fun onAuthenticationSucceeded(result:BiometricPrompt.AuthenticationResult){
                        if(cont.isActive) cont.resume(true)
                    }
                    override fun onAuthenticationError(errorCode:Int,errString:CharSequence){
                        if(cont.isActive) cont.resume(false)
                    }
                })
            val info=BiometricPrompt.PromptInfo.Builder()
                .setTitle("Unlock KNOUX AI")
                .setSubtitle("Verify locally before resuming your account")
                .setAllowedAuthenticators(authenticators)
                .setNegativeButtonText("Use account sign-in")
                .build()
            cont.invokeOnCancellation { prompt.cancelAuthentication() }
            prompt.authenticate(info)
        }
        if(!biometricOk) return false
        // Server check is mandatory before showing authenticated cloud features.
        return api.restoreIfValid()
    }
}
