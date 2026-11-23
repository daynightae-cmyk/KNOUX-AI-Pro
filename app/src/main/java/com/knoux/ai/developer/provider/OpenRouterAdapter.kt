package com.knoux.ai.developer.provider

import android.content.Context
import com.knoux.ai.cloud.CloudApiException
import com.knoux.ai.cloud.CloudHttp
import com.knoux.ai.developer.model.AiModel
import com.knoux.ai.developer.model.AiProvider
import com.knoux.ai.developer.model.ProviderConnectionState
import org.json.JSONObject

/**
 * OpenRouter adapter. Unified OpenAI-compatible endpoint with model discovery
 * at /api/v1/models.
 *
 * Security contract: the API key is only ever sent as an Authorization header
 * over https via [CloudHttp] (no redirects, retries on transient statuses only).
 * Provider error bodies are never echoed — [CloudApiException] carries a
 * redacted message with the HTTP status only.
 */
class OpenRouterAdapter(private val context: Context) : ProviderAdapter {
    override val providerId = "openrouter"
    override val displayName = "OpenRouter"
    override val authScheme = "bearer"
    override val supportsModelDiscovery = true
    override val supportsStreaming = true
    override val supportsToolCalling = true
    override val supportsVision = true
    override val supportsReasoning = true

    private val baseUrl = "https://openrouter.ai/api/v1"

    override suspend fun testConnection(apiKey: String): ConnectionTestResult {
        return try {
            val models = discoverModels(apiKey)
            ConnectionTestResult(
                success = true,
                state = ProviderConnectionState.Connected,
                modelsDiscovered = models.models.size,
                latencyMs = 0,
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
        val json = CloudHttp.getJson("$baseUrl/models", apiKey)
        val data = json.optJSONArray("data")
            ?: throw CloudApiException(200, null, "Malformed models response from $providerId")
        val models = mutableListOf<AiModel>()
        for (i in 0 until data.length()) {
            val item = data.optJSONObject(i) ?: continue
            val id = item.optString("id", "")
            if (id.isBlank()) continue
            val name = item.optString("name", id)
            val context = item.optInt("context_length", 0)
            val pricing = item.optJSONObject("pricing")
            val inputPrice = if (pricing != null) pricing.optDouble("prompt", -1.0) else null
            val outputPrice = if (pricing != null) pricing.optDouble("completion", -1.0) else null
            val arch = item.optJSONObject("architecture")
            val inputModalities = jsonArrayToList(arch?.optJSONArray("input_modalities"))
            val outputModalities = jsonArrayToList(arch?.optJSONArray("output_modalities"))

            models.add(AiModel(
                modelId = id,
                providerId = providerId,
                displayName = name.takeIf { it.isNotBlank() },
                contextWindow = if (context > 0) context else null,
                inputPricePer1M = if (inputPrice != null && inputPrice >= 0) inputPrice * 1_000_000.0 else null,
                outputPricePer1M = if (outputPrice != null && outputPrice >= 0) outputPrice * 1_000_000.0 else null,
                supportsVision = inputModalities.contains("image"),
                supportsToolCalling = true,
                supportsStreaming = true,
                supportsReasoning = id.contains("reasoning", ignoreCase = true) || id.contains("o1", ignoreCase = true),
            ))
        }
        return ModelDiscoveryResult(models = models.toList())
    }

    override suspend fun getPublicCatalog(): ModelDiscoveryResult? = null

    override fun normalizeError(throwable: Throwable): ProviderConnectionState {
        if (throwable is CloudApiException) return when (throwable.status) {
            401, 403 -> ProviderConnectionState.InvalidApiKey
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

    private fun jsonArrayToList(array: org.json.JSONArray?): List<String> {
        if (array == null) return emptyList()
        return (0 until array.length()).map { array.optString(it, "") }
    }
}
