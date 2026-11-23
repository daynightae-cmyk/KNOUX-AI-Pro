package com.knoux.ai.developer.provider

import android.content.Context
import com.knoux.ai.cloud.CloudApiException
import com.knoux.ai.cloud.CloudHttp
import com.knoux.ai.developer.model.AiModel
import com.knoux.ai.developer.model.AiProvider
import com.knoux.ai.developer.model.ProviderConnectionState

/** NVIDIA NIM / Build adapter: integrate.api.nvidia.com/v1, bearer auth via [CloudHttp]. */
class NvidiaAdapter(private val context: Context) : ProviderAdapter {
    override val providerId = "nvidia-nim"
    override val displayName = "NVIDIA NIM"
    override val authScheme = "bearer"
    override val supportsModelDiscovery = true
    override val supportsStreaming = true
    override val supportsToolCalling = true
    override val supportsVision = true
    override val supportsReasoning = false

    private val baseUrl = "https://integrate.api.nvidia.com/v1"

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
        val json = CloudHttp.getJson("$baseUrl/models", apiKey)
        val data = json.optJSONArray("data")
        val models = mutableListOf<AiModel>()
        if (data != null) {
            for (i in 0 until data.length()) {
                val item = data.optJSONObject(i) ?: continue
                val id = item.optString("id", "")
                if (id.isBlank()) continue
                models.add(AiModel(
                    modelId = id,
                    providerId = providerId,
                    displayName = item.optString("name", id),
                    supportsToolCalling = true,
                    supportsStreaming = true,
                ))
            }
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
}
