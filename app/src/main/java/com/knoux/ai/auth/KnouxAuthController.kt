package com.knoux.ai.auth

import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentActivity
import org.json.JSONObject

/** Connect existing KNOUX UI callbacks to this controller; no redesign or demo verified state. */
class KnouxAuthController(context:Context, config:KnouxAuthConfig) {
    private val api=KnouxAuthApi(context,config)
    private val google=KnouxGoogleCredential(api)
    private val passkey=KnouxPasskeyCredential(api)
    private val biometric=KnouxBiometricUnlock(api)
    suspend fun google(activity:Activity):Boolean=google.signIn(activity)
    suspend fun login(email:String,password:String):Boolean=api.signInWithPassword(email,password)
    suspend fun signup(email:String,password:String):Boolean=api.signUp(email,password)
    suspend fun sendOtp(email:String,createUser:Boolean=false)=api.sendEmailOtp(email,createUser)
    suspend fun verifyOtp(email:String,code:String,type:String="email"):Boolean=api.verifyEmailOtp(email,code,type)
    suspend fun recover(email:String)=api.requestPasswordReset(email)
    suspend fun resetPassword(newPassword:String)=api.updatePassword(newPassword)
    suspend fun createPasskey(activity:Activity):Boolean=passkey.register(activity)
    suspend fun passkey(activity:Activity):Boolean=passkey.signIn(activity)
    suspend fun biometric(activity:FragmentActivity):Boolean=biometric.unlock(activity)
    suspend fun currentUser():JSONObject=api.currentUserVerified()
    suspend fun resumeIfVerified():Boolean=api.restoreIfValid()
    suspend fun signOut()=api.signOut()
    suspend fun listPasskeys():JSONObject=api.listPasskeys()
    // Guest mode is deliberately not exposed as a Supabase Auth success.
}
