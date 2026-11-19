package com.knoux.ai.developer.model

/**
 * A single model discovered from a provider. All fields are optional because
 * different providers expose different metadata; missing values are shown as
 * "—" rather than invented.
 */
data class AiModel(
    /** Provider-assigned model ID (e.g. "openrouter/anthropic/claude-3.5-sonnet"). */
    val modelId: String,
    val providerId: String,
    /** Human-readable name from the provider catalog. */
    val displayName: String? = null,
    /** Context window in tokens, when known. */
    val contextWindow: Int? = null,
    /** Maximum output tokens, when known. */
    val maxOutput: Int? = null,
    /** Input price per 1M tokens, when known. */
    val inputPricePer1M: Double? = null,
    /** Output price per 1M tokens, when known. */
    val outputPricePer1M: Double? = null,
    /** Whether the provider lists this as free-tier. */
    val isFree: Boolean = false,
    /** Whether vision/image input is supported. */
    val supportsVision: Boolean = false,
    /** Whether audio input/output is supported. */
    val supportsAudio: Boolean = false,
    /** Whether image generation is supported. */
    val supportsImageGeneration: Boolean = false,
    /** Whether video generation is supported. */
    val supportsVideoGeneration: Boolean = false,
    /** Whether tool calling is supported. */
    val supportsToolCalling: Boolean = false,
    /** Whether structured output (JSON mode) is supported. */
    val supportsStructuredOutput: Boolean = false,
    /** Whether reasoning mode is supported. */
    val supportsReasoning: Boolean = false,
    /** Whether streaming is supported. */
    val supportsStreaming: Boolean = false,
    /** Measured average latency in ms (from actual tests). */
    val measuredLatencyMs: Long? = null,
    /** Last successful connection test timestamp. */
    val lastConnectionTest: Long = 0L,
    /** Whether this is the user's primary model for this provider. */
    val isPrimary: Boolean = false,
    /** Whether this is the user's fallback model. */
    val isFallback: Boolean = false,
)

/** Capability tags used for routing and classification. */
enum class ModelCapability {
    Coding, UI_UX, SoftwareEngineering, BugFixing, Architecture,
    Research, Reasoning, Vision, ImageGeneration, Audio,
    Free, LowCost, Fast, ToolEnabled, StructuredOutput,
}

fun ModelCapability.displayName(): String = when (this) {
    ModelCapability.Coding -> "التطوير"
    ModelCapability.UI_UX -> "تجربة المستخدم والواجهة"
    ModelCapability.SoftwareEngineering -> "هندسة البرمجيات"
    ModelCapability.BugFixing -> "إصلاح الأخطاء"
    ModelCapability.Architecture -> "الهندسة المعمارية"
    ModelCapability.Research -> "البحث"
    ModelCapability.Reasoning -> "الاستدلال"
    ModelCapability.Vision -> "الرؤية الحاسوبية"
    ModelCapability.ImageGeneration -> "توليد الصور"
    ModelCapability.Audio -> "الصوت"
    ModelCapability.Free -> "مجاني"
    ModelCapability.LowCost -> "ذا تكلفة منخفضة"
    ModelCapability.Fast -> "سريع"
    ModelCapability.ToolEnabled -> "يدعم الأدوات"
    ModelCapability.StructuredOutput -> "مخرجات منظمة"
}