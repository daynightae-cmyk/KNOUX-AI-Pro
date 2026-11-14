package com.knoux.ai.auth

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption

class KnouxGoogleCredential(private val api:KnouxAuthApi) {
    suspend fun signIn(activity:Activity):Boolean {
        val cm=CredentialManager.create(activity)
        val option=GetSignInWithGoogleOption.Builder(api.config.googleWebClientId).build()
        val token: String
        try {
            val response=cm.getCredential(activity,GetCredentialRequest(listOf(option)))
            token = com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
                .createFrom(response.credential.data).idToken
        } catch(e:NoCredentialException) {
            throw KnouxAuthException("no_credential","No Google account available on this device")
        }
        return api.signInWithGoogleIdToken(token,null)
    }
}