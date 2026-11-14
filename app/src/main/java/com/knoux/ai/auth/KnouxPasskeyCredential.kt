package com.knoux.ai.auth

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PublicKeyCredential
import org.json.JSONObject

/** Supabase experimental two-step WebAuthn + native Credential Manager (never a fake local PIN). */
class KnouxPasskeyCredential(private val api:KnouxAuthApi) {
    suspend fun register(activity:Activity):Boolean {
        // Caller must have an existing confirmed non-anonymous Supabase user.
        api.currentUserVerified()
        val start=api.passkeyRegistrationOptions()
        val challengeId=start.getString("challenge_id")
        val options=start.getJSONObject("options").toString()
        val result=CredentialManager.create(activity).createCredential(activity,
            CreatePublicKeyCredentialRequest(requestJson=options))
        val key=result as? CreatePublicKeyCredentialResponse
            ?: throw KnouxAuthException("unexpected_credential","Passkey creation failed")
        val credential=JSONObject(key.registrationResponseJson)
        api.verifyPasskeyRegistration(challengeId,credential)
        // Registration adds a credential; it must NOT create a new authenticated session.
        return true
    }
    suspend fun signIn(activity:Activity):Boolean {
        val start=api.passkeyAuthenticationOptions()
        val challengeId=start.getString("challenge_id")
        val options=start.getJSONObject("options").toString()
        val response=CredentialManager.create(activity).getCredential(activity,
            GetCredentialRequest(listOf(GetPublicKeyCredentialOption(requestJson=options))))
        val credential=response.credential as? PublicKeyCredential
            ?: throw KnouxAuthException("unexpected_credential","No passkey assertion returned")
        return api.verifyPasskeyAuthentication(challengeId,JSONObject(credential.authenticationResponseJson))
    }
}
