package com.knoux.ai.developer.provider

import android.content.Context
import com.knoux.ai.developer.model.AiModel
import com.knoux.ai.developer.model.AiProvider
import com.knoux.ai.developer.model.ProviderConnectionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/** Google Gemini adapter: generativelanguage.googleapis.com. */
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
        return withContext(Dispatchers.IO) {
            try {
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
    }

    override suspend fun discoverModels(apiKey: String): ModelDiscoveryResult {
        return withContext(Dispatchers.IO) {
            val url = URL("$baseUrl/models?key=$apiKey")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 15_000
            conn.readTimeout = 20_000
            conn.setRequestProperty("Accept", "application/json")

            val status = conn.responseCode
            val stream = if (status in 200..299) conn.inputStream else conn.errorStream
            val payload = stream?.readBytes()?.let { String(it, charset = java.nio.charset.StandardCharsets.UTF_8) } ?: ""
            conn.disconnect()

            if (status !in 200..299) throw RuntimeException("HTTP $status: $payload")

            val json = JSONObject(payload)
            val modelsArray = json.getJSONArray("models")
            val models = mutableListOf<AiModel>()
            for (i in 0 until modelsArray.length()) {
                val item = modelsArray.getJSONObject(i)
                val name = item.optString("name", "").removePrefix("models/")
                models.add(AiModel(
                    modelId = name,
                    providerId = providerId,
                    displayName = item.optString("displayName", name),
                    supportsVision = name.contains("vision", ignoreCase = true) || name.contains("pro", ignoreCase = true),
                    supportsToolCalling = true,
                    supportsStreaming = true,
                ))
            }
            ModelDiscoveryResult(models = models.toList())
        }
    }

    override suspend fun getPublicCatalog(): ModelDiscoveryResult? = null

    override fun normalizeError(throwable: Throwable): ProviderConnectionState {
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
