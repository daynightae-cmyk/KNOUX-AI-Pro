package com.knoux.ai.auth

/** Public client configuration only. NEVER include Google client_secret, service_role or Management Token. */
data class KnouxAuthConfig(
    val supabaseUrl: String,
    val supabasePublishableKey: String,
    val googleWebClientId: String,
) {
    init {
        require(supabaseUrl.startsWith("https://") && supabaseUrl.endsWith(".supabase.co")) { "Expected a trusted Supabase HTTPS project URL" }
        require(supabasePublishableKey.isNotBlank() && !supabasePublishableKey.contains("service_role")) { "Missing publishable key" }
        require(googleWebClientId.endsWith(".apps.googleusercontent.com")) { "Web OAuth client ID required" }
    }
    val baseAuthUrl: String get() = supabaseUrl.trimEnd('/') + "/auth/v1"
}
