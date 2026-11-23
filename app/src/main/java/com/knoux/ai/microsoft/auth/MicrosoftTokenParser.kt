package com.knoux.ai.microsoft.auth

import org.json.JSONObject

/**
 * Pure Microsoft OAuth token-response parsing. No Android dependencies, so
 * the exact production rules are executable in JVM contract tests.
 * AAD returns expires_in as a number OR a quoted string; both are handled.
 * A response without an access_token is rejected, never a blank "success".
 */
internal object MicrosoftTokenParser {
    fun parse(json: String, nowMs: Long): MicrosoftAuthProvider.MicrosoftTokenResponse {
        val obj = JSONObject(json)
        val accessToken = obj.optString("access_token")
        require(accessToken.isNotBlank()) { "Token response contained no access_token" }
        return MicrosoftAuthProvider.MicrosoftTokenResponse(
            accessToken = accessToken,
            refreshToken = obj.optString("refresh_token").takeIf { it.isNotBlank() },
            expiresInSeconds = obj.opt("expires_in")?.toString()?.toLongOrNull() ?: 3600L,
            tokenType = obj.optString("token_type", "Bearer"),
            scope = obj.optString("scope", ""),
            obtainedAtMs = nowMs,
        )
    }
}
