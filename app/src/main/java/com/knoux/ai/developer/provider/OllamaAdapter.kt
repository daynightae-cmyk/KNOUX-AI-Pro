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

/** Ollama local runtime adapter: no auth, /api/tags discovery. */
class OllamaAdapter(private val context: Context) : ProviderAdapter {
    override val providerId = "ollama"
    override val displayName = "Ollama (Local)"
    override val authScheme = "none"
    override val supportsModelDiscovery = true
    override val supportsStreaming = true
    override val supportsToolCalling = false
    override val supportsVision = false
    override val supportsReasoning = false

    // The base URL is configurable per device; default to localhost.
    private var baseUrl = "http://10.0.2.2:11434"

    fun setBaseUrl(url: String) { baseUrl = url }

    override suspend fun testConnection(apiKey: String): ConnectionTestResult {
        return withContext(Dispatchers.IO) {
            try {
                val models = discoverModels("")
                ConnectionTestResult(
                    success = true,
                    state = ProviderConnectionState.Connected,
                    modelsDiscovered = models.models.size,
                )
            } catch (e: Exception) {
                ConnectionTestResult(
                    success = false,
                    state = ProviderConnectionState.ProviderUnavailable,
                    errorMessage = e.message,
                )
            }
        }
    }

    override suspend fun discoverModels(apiKey: String): ModelDiscoveryResult {
        return withContext(Dispatchers.IO) {
            val url = URL("$baseUrl/api/tags")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 10_000
            conn.readTimeout = 15_000
            conn.setRequestProperty("Accept", "application/json")

            val status = conn.responseCode
            val stream = if (status in 200..299) conn.inputStream else conn.errorStream
            val payload = stream?.readBytes()?.let { String(it, charset = java.nio.charset.StandardCharsets.UTF_8) } ?: ""
            conn.disconnect()

            if (status !in 200..299) throw RuntimeException("HTTP $status: $payload")

            val json = JSONObject(payload)
            val modelsArray = json.optJSONArray("models")
            val models = mutableListOf<AiModel>()
            if (modelsArray != null) {
                for (i in 0 until modelsArray.length()) {
                    val item = modelsArray.getJSONObject(i)
                    val name = item.optString("name", "")
                    models.add(AiModel(
                        modelId = name,
                        providerId = providerId,
                        displayName = name,
                        supportsStreaming = true,
                    ))
                }
            }
            ModelDiscoveryResult(models = models.toList())
        }
    }

    override suspend fun getPublicCatalog(): ModelDiscoveryResult? = null

    override fun normalizeError(throwable: Throwable): ProviderConnectionState =
        ProviderConnectionState.ProviderUnavailable

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
