package com.knoux.ai.developer.provider

import com.knoux.ai.developer.model.AiModel
import com.knoux.ai.developer.model.AiProvider
import com.knoux.ai.developer.model.ProviderConnectionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Result of a model discovery call. Contains the discovered models plus
 * provider-specific metadata (pagination cursor, rate limit info, etc.).
 */
data class ModelDiscoveryResult(
    val models: List<AiModel>,
    val hasMorePages: Boolean = false,
    val nextCursor: String? = null,
    val providerMetadata: Map<String, String> = emptyMap(),
)

/**
 * Result of testing an API key / connection.
 */
data class ConnectionTestResult(
    val success: Boolean,
    val state: ProviderConnectionState,
    val errorMessage: String? = null,
    val modelsDiscovered: Int = 0,
    val latencyMs: Long = 0,
)

/**
 * Every provider adapter implements this interface. No provider is assumed to
 * share a wire protocol with another — each adapter maps its own auth, model
 * discovery, request format, streaming, errors, tool calling, and rate limits.
 */
interface ProviderAdapter {
    /** Unique provider ID (e.g. "openrouter", "nvidia-nim", "ollama"). */
    val providerId: String
    /** Human-readable display name. */
    val displayName: String
    /** Auth scheme: "bearer", "api_key", "oauth2", "none", "local". */
    val authScheme: String
    val supportsModelDiscovery: Boolean
    val supportsStreaming: Boolean
    val supportsToolCalling: Boolean
    val supportsVision: Boolean
    val supportsReasoning: Boolean

    /**
     * Test the configured credentials against the provider. Does NOT discover
     * models unless the provider supports it and the caller requests it.
     */
    suspend fun testConnection(apiKey: String): ConnectionTestResult

    /**
     * Discover available models for the account. Handles pagination internally
     * and returns all pages. Throws if the provider doesn't support discovery.
     */
    suspend fun discoverModels(apiKey: String): ModelDiscoveryResult

    /**
     * Get the base model catalog (public, unauthenticated) when the provider
     * publishes one. Used as a fallback when authenticated discovery fails.
     */
    suspend fun getPublicCatalog(): ModelDiscoveryResult?

    /** Normalize a provider error into a ProviderConnectionState. */
    fun normalizeError(throwable: Throwable): ProviderConnectionState
}

/**
 * Registry of all provider adapters. Owns adapter lifecycle and routes
 * requests to the correct adapter based on provider ID.
 */
class ProviderRegistry {
    private val adapters = mutableMapOf<String, ProviderAdapter>()

    fun register(adapter: ProviderAdapter) {
        adapters[adapter.providerId] = adapter
    }

    fun getAdapter(providerId: String): ProviderAdapter? = adapters[providerId]

    val allProviders: List<ProviderAdapter> get() = adapters.values.toList()

    /** Check whether an adapter exists for the given provider ID. */
    fun isSupported(providerId: String): Boolean = adapters.containsKey(providerId)
}

/**
 * Singleton registry instance.
 */
object ProviderRegistryHolder {
    @Volatile var registry: ProviderRegistry? = null
        private set

    fun init(): ProviderRegistry {
        return registry ?: synchronized(this) {
            val reg = ProviderRegistry().also { registry = it }
            reg
        }
    }

    fun registerBuiltInAdapters() {
        val reg = init() ?: return
        // Built-in adapters are registered by the DI module / startup.
        // Each adapter is registered via registerBuiltInAdapter() calls.
    }
}

/**
 * Holds the live connection state for each provider. Observed by the UI.
 */
class ProviderConnectionStateHolder {
    private val states = mutableMapOf<String, MutableStateFlow<ProviderConnectionState>>()

    fun getStateFlow(providerId: String): MutableStateFlow<ProviderConnectionState> {
        return states.getOrPut(providerId) {
            MutableStateFlow(ProviderConnectionState.NotConfigured)
        }
    }

    fun setState(providerId: String, state: ProviderConnectionState) {
        getStateFlow(providerId).value = state
    }

    val allStates: Map<String, StateFlow<ProviderConnectionState>> get() =
        states.mapValues { (_, flow) -> flow.asStateFlow() }
}