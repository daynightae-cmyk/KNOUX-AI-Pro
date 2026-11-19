package com.knoux.ai.developer.model

/**
 * A single AI provider (e.g. OpenRouter, NVIDIA NIM, Google Gemini, Ollama).
 * Each provider has its own auth scheme, model discovery endpoint, and request
 * mapping; none are assumed to share a wire protocol.
 */
data class AiProvider(
    val id: String,
    /** Human-readable display name. */
    val name: String,
    /** Base API URL (may be null for local runtimes like Ollama). */
    val baseUrl: String?,
    /** Auth scheme: "bearer", "api_key", "oauth2", "none", "local". */
    val authScheme: String,
    /** Whether model discovery is supported via a /models endpoint. */
    val supportsModelDiscovery: Boolean,
    /** Whether streaming responses are supported. */
    val supportsStreaming: Boolean,
    /** Whether tool calling is supported. */
    val supportsToolCalling: Boolean,
    /** Whether vision (image input) is supported. */
    val supportsVision: Boolean,
    /** Whether reasoning mode is supported. */
    val supportsReasoning: Boolean,
    /** Whether the provider is configured (has valid credentials). */
    val isConfigured: Boolean = false,
    /** Current connection state. */
    val connectionState: ProviderConnectionState = ProviderConnectionState.NotConfigured,
    /** Last error message, if any. */
    val lastError: String? = null,
    /** When the model catalog was last refreshed. */
    val modelsLastUpdated: Long = 0L,
    /** Number of models discovered. */
    val discoveredModelCount: Int = 0,
    /** Pricing metadata URL or JSON blob, when available from the provider. */
    val pricingMetadata: String? = null,
)

enum class ProviderConnectionState {
    NotConfigured,
    Connecting,
    Authenticating,
    FetchingModels,
    Connected,
    InvalidApiKey,
    UnauthorizedModel,
    RateLimited,
    InsufficientCredits,
    ProviderUnavailable,
    Offline,
    DiscoveryUnsupported,
}

fun ProviderConnectionState.displayName(): String = when (this) {
    ProviderConnectionState.NotConfigured -> "غير مُهيأ"
    ProviderConnectionState.Connecting -> "جاري الاتصال…"
    ProviderConnectionState.Authenticating -> "جاري المصادقة…"
    ProviderConnectionState.FetchingModels -> "جاري جلب النماذج…"
    ProviderConnectionState.Connected -> "متصل"
    ProviderConnectionState.InvalidApiKey -> "مفتاح API غير صالح"
    ProviderConnectionState.UnauthorizedModel -> "النموذج غير مصرح به"
    ProviderConnectionState.RateLimited -> "تجاوز الحد الأقصى للطلبات"
    ProviderConnectionState.InsufficientCredits -> "رصيد غير كافٍ"
    ProviderConnectionState.ProviderUnavailable -> "مزود غير متاح"
    ProviderConnectionState.Offline -> "غير متصل"
    ProviderConnectionState.DiscoveryUnsupported -> "الاكتشاف غير مدعوم"
}