package com.knoux.ai.developer.provider

import android.content.Context
import com.knoux.ai.cloud.CloudApiException
import com.knoux.ai.cloud.CloudHttp
import com.knoux.ai.developer.model.AiModel
import com.knoux.ai.developer.model.AiProvider
import com.knoux.ai.developer.model.ProviderConnectionState

/**
 * Google Gemini adapter: generativelanguage.googleapis.com.
 * The API key is sent ONLY as the `x-goog-api-key` header — never in the URL,
 * where it would leak into logs, proxies and error messages.
 */
class GeminiAdapter(private val context: Context) : ProviderAdapter {
    override val providerId = "google-gemini"
    override val displayName = "Google Gemini"
    override val authScheme = "api_key"
    override val supportsModelDiscovery = true
    override val supportsStreaming = true
    override val supportsToolCalling = true
    override val supportsVision = true
    override val supportsReasoning = false

    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta"

    override suspend fun testConnection(apiKey: String): ConnectionTestResult {
        return try {
            val models = discoverModels(apiKey)
            ConnectionTestResult(
                success = true,
                state = ProviderConnectionState.Connected,
                modelsDiscovered = models.models.size,
            )
        } catch (e: Exception) {
            ConnectionTestResult(
                success = false,
                state = normalizeError(e),
                errorMessage = e.message,
            )
        }
    }

    override suspend fun discoverModels(apiKey: String): ModelDiscoveryResult {
        val json = CloudHttp.getJson("$baseUrl/models", apiKey, headerName = "x-goog-api-key")
        val modelsArray = json.optJSONArray("models")
            ?: throw CloudApiException(200, null, "Malformed models response from $providerId")
        val models = mutableListOf<AiModel>()
        for (i in 0 until modelsArray.length()) {
            val item = modelsArray.optJSONObject(i) ?: continue
            val name = item.optString("name", "").removePrefix("models/")
            if (name.isBlank()) continue
            models.add(AiModel(
                modelId = name,
                providerId = providerId,
                displayName = item.optString("displayName", name),
                supportsVision = name.contains("vision", ignoreCase = true) || name.contains("pro", ignoreCase = true),
                supportsToolCalling = true,
                supportsStreaming = true,
            ))
        }
        return ModelDiscoveryResult(models = models.toList())
    }

    override suspend fun getPublicCatalog(): ModelDiscoveryResult? = null

    override fun normalizeError(throwable: Throwable): ProviderConnectionState {
        if (throwable is CloudApiException) return when (throwable.status) {
            400, 401, 403 -> ProviderConnectionState.InvalidApiKey
            429 -> ProviderConnectionState.RateLimited
            in 500..599 -> ProviderConnectionState.ProviderUnavailable
            else -> ProviderConnectionState.Offline
        }
        val msg = throwable.message ?: ""
        return when {
            msg.contains("401") || msg.contains("403") -> ProviderConnectionState.InvalidApiKey
            msg.contains("429") -> ProviderConnectionState.RateLimited
            msg.contains("502") || msg.contains("503") -> ProviderConnectionState.ProviderUnavailable
            else -> ProviderConnectionState.Offline
        }
    }

    fun toProvider(isConfigured: Boolean, state: ProviderConnectionState): AiProvider =
        AiProvider(
            id = providerId,
            name = displayName,
            baseUrl = baseUrl,
            authScheme = authScheme,
            supportsModelDiscovery = supportsModelDiscovery,
            supportsStreaming = supportsStreaming,
            supportsToolCalling = supportsToolCalling,
            supportsVision = supportsVision,
            supportsReasoning = supportsReasoning,
            isConfigured = isConfigured,
            connectionState = state,
        )
}
