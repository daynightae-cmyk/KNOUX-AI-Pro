package com.knoux.ai.auth

import org.json.JSONObject

object AuthSettingsParser {

    data class AuthSettings(
        val emailEnabled: Boolean,
        val googleEnabled: Boolean,
        val passkeysEnabled: Boolean,
        val disableSignup: Boolean,
    )

    fun parse(payload: JSONObject): AuthSettings {
        val external = payload.optJSONObject("external")
        return AuthSettings(
            emailEnabled = external?.optBoolean("email", true) ?: true,
            googleEnabled = external?.optBoolean("google", false) ?: false,
            passkeysEnabled = payload.optBoolean("passkeys_enabled", false),
            disableSignup = payload.optBoolean("disable_signup", false),
        )
    }

    val failSafe: AuthSettings = AuthSettings(
        emailEnabled = true,
        googleEnabled = false,
        passkeysEnabled = false,
        disableSignup = false,
    )
}