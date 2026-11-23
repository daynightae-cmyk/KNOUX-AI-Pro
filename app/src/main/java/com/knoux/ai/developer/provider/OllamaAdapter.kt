package com.knoux.ai.developer.provider

import android.content.Context
import com.knoux.ai.cloud.CloudApiException
import com.knoux.ai.cloud.CloudHttp
import com.knoux.ai.developer.model.AiModel
import com.knoux.ai.developer.model.AiProvider
import com.knoux.ai.developer.model.ProviderConnectionState

/**
 * Validates a user-supplied Ollama base URL. Cleartext HTTP is allowed ONLY
 * for loopback hosts (local development); remote hosts must use HTTPS so
 * prompts and responses never travel in cleartext. Pure JVM — contract-tested.
 */
object OllamaEndpointPolicy {
    fun validateBaseUrl(raw: String): String {
        val trimmed = raw.trim().trimEnd('/')
        val uri = try {
            java.net.URI(trimmed)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid Ollama URL")
        }
        val scheme = uri.scheme?.lowercase()
            ?: throw IllegalArgumentException("Ollama URL must include a scheme (http or https)")
        var host = (uri.host ?: "").lowercase().removePrefix("[").removeSuffix("]")
        if (host.isBlank()) throw IllegalArgumentException("Ollama URL must include a host")
        when (scheme) {
            "https" -> Unit
            "http" -> {
                val loopback = host == "localhost" || host == "::1" ||
                    host.startsWith("127.") || host == "10.0.2.2" // emulator->host loopback
                require(loopback) {
                    "Cleartext HTTP is allowed only for a LOCAL Ollama (loopback). Use https:// for remote hosts."
                }
            }
            else -> throw IllegalArgumentException("Unsupported scheme '$scheme' — use http (local only) or https")
        }
        if (uri.port != -1 && uri.port !in 1..65535) throw IllegalArgumentException("Invalid port")
        return trimmed
    }
}

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

    // The base URL is configurable per device; default to the emulator's
    // loopback alias for the host machine. Validated: cleartext only on loopback.
    private var baseUrl = OllamaEndpointPolicy.validateBaseUrl("http://10.0.2.2:11434")

    fun setBaseUrl(url: String) { baseUrl = OllamaEndpointPolicy.validateBaseUrl(url) }

    override suspend fun testConnection(apiKey: String): ConnectionTestResult {
        return try {
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

    override suspend fun discoverModels(apiKey: String): ModelDiscoveryResult {
        // No credential: CloudHttp sends no auth header for an empty key.
        val json = CloudHttp.getJson("$baseUrl/api/tags", "")
        val modelsArray = json.optJSONArray("models")
        val models = mutableListOf<AiModel>()
        if (modelsArray != null) {
            for (i in 0 until modelsArray.length()) {
                val item = modelsArray.optJSONObject(i) ?: continue
                val name = item.optString("name", "")
                if (name.isBlank()) continue
                models.add(AiModel(
                    modelId = name,
                    providerId = providerId,
                    displayName = name,
                    supportsStreaming = true,
                ))
            }
        }
        return ModelDiscoveryResult(models = models.toList())
    }

    override suspend fun getPublicCatalog(): ModelDiscoveryResult? = null

    override fun normalizeError(throwable: Throwable): ProviderConnectionState {
        if (throwable is CloudApiException && throwable.status in 500..599) {
            return ProviderConnectionState.ProviderUnavailable
        }
        return ProviderConnectionState.ProviderUnavailable
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
