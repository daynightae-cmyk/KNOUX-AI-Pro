package com.knoux.ai.developer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudQueue
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.developer.model.AiModel
import com.knoux.ai.developer.model.ProviderConnectionState
import com.knoux.ai.developer.provider.ProviderRegistry
import com.knoux.ai.developer.provider.ProviderRegistryHolder
import com.knoux.ai.developer.vault.CredentialVault
import com.knoux.ai.ui.v2.KText
import com.knoux.ai.ui.v2.V2Amber
import com.knoux.ai.ui.v2.V2Blue
import com.knoux.ai.ui.v2.V2Cyan
import com.knoux.ai.ui.v2.V2GlassCard
import com.knoux.ai.ui.v2.V2GoldText
import com.knoux.ai.ui.v2.V2Green
import com.knoux.ai.ui.v2.V2LuxuryIdentity
import com.knoux.ai.ui.v2.V2Muted
import com.knoux.ai.ui.v2.V2Pink
import com.knoux.ai.ui.v2.V2Red
import com.knoux.ai.ui.v2.V2Soft
import kotlinx.coroutines.launch

private fun providerStateColor(state: ProviderConnectionState): Color = when (state) {
    ProviderConnectionState.Connected -> V2Green
    ProviderConnectionState.Connecting, ProviderConnectionState.Authenticating,
    ProviderConnectionState.FetchingModels -> V2Blue
    ProviderConnectionState.InvalidApiKey, ProviderConnectionState.UnauthorizedModel -> V2Red
    ProviderConnectionState.RateLimited, ProviderConnectionState.InsufficientCredits -> V2Amber
    ProviderConnectionState.ProviderUnavailable, ProviderConnectionState.Offline -> V2Pink
    ProviderConnectionState.DiscoveryUnsupported -> V2Soft
    ProviderConnectionState.NotConfigured -> V2Muted
}

private fun providerStateText(state: ProviderConnectionState): String = when (state) {
    ProviderConnectionState.Connected -> "متصل"
    ProviderConnectionState.Connecting -> "جاري الاتصال…"
    ProviderConnectionState.Authenticating -> "جاري المصادقة…"
    ProviderConnectionState.FetchingModels -> "جاري جلب النماذج…"
    ProviderConnectionState.InvalidApiKey -> "مفتاح API غير صالح"
    ProviderConnectionState.UnauthorizedModel -> "النموذج غير مصرح به"
    ProviderConnectionState.RateLimited -> "تجاوز الحد الأقصى للطلبات"
    ProviderConnectionState.InsufficientCredits -> "رصيد غير كافٍ"
    ProviderConnectionState.ProviderUnavailable -> "مزود غير متاح"
    ProviderConnectionState.Offline -> "غير متصل"
    ProviderConnectionState.DiscoveryUnsupported -> "الاكتشاف غير مدعوم"
    ProviderConnectionState.NotConfigured -> "غير مهيأ"
}

private data class DiscoveryState(
    val providerId: String,
    val providerName: String,
    val state: ProviderConnectionState,
    val detail: String,
)

/**
 * Model Explorer (DEV-04).
 *
 * Discovers models from every provider that has a stored credential, using the
 * provider's own catalog endpoint. Nothing is hard-coded: when no provider is
 * configured, or discovery fails, the screen shows the exact error instead of
 * an invented model list. The "test" action runs a real connection test.
 */
@Composable
fun ModelExplorerScreen(
    onBack: () -> Unit,
    onPro: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val vault = remember { CredentialVault(context) }
    val registry: ProviderRegistry = remember {
        ProviderRegistryHolder.registerBuiltInAdapters(context)
        ProviderRegistryHolder.init()
    }

    var models by remember { mutableStateOf(listOf<AiModel>()) }
    var discoveries by remember { mutableStateOf(listOf<DiscoveryState>()) }
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    fun discoverAll() {
        if (loading) return
        scope.launch {
            loading = true
            message = null
            val found = mutableListOf<AiModel>()
            var states: List<DiscoveryState> = emptyList()
            val configured = registry.allProviders.filter { vault.hasCredential(it.providerId) }
            if (configured.isEmpty()) {
                discoveries = emptyList()
                models = emptyList()
                loading = false
                message = "لا مزوّد مهيأ. أضف مفتاح API من «خزنة المفاتيح» أولًا."
                return@launch
            }
            configured.forEach { adapter ->
                states += DiscoveryState(
                    providerId = adapter.providerId,
                    providerName = adapter.displayName,
                    state = ProviderConnectionState.FetchingModels,
                    detail = "جارٍ الاكتشاف…",
                )
            }
            discoveries = states
            configured.forEach { adapter ->
                val key = vault.getCredential(adapter.providerId).orEmpty()
                try {
                    val result = adapter.discoverModels(key)
                    found += result.models
                    states = states.map { existing ->
                        if (existing.providerId != adapter.providerId) existing else existing.copy(
                            state = ProviderConnectionState.Connected,
                            detail = "${result.models.size} نموذجًا",
                        )
                    }
                } catch (failure: Exception) {
                    val state = adapter.normalizeError(failure)
                    states = states.map { existing ->
                        if (existing.providerId != adapter.providerId) existing else existing.copy(
                            state = state,
                            detail = failure.message ?: providerStateText(state),
                        )
                    }
                }
                discoveries = states
            }
            models = found.distinctBy { it.providerId to it.modelId }
            loading = false
            message = if (models.isEmpty()) {
                "لم يُعِد أي مزوّد نماذج. راجع حالة الاكتشاف أدناه."
            } else {
                "تم اكتشاف ${models.size} نموذجًا من ${configured.size} مزوّدًا."
            }
        }
    }

    fun testProvider(providerId: String) {
        val adapter = registry.getAdapter(providerId) ?: return
        scope.launch {
            discoveries = discoveries.map { existing ->
                if (existing.providerId != providerId) existing else existing.copy(
                    state = ProviderConnectionState.Connecting,
                    detail = "جارٍ اختبار الاتصال…",
                )
            }
            val result = adapter.testConnection(vault.getCredential(providerId).orEmpty())
            discoveries = discoveries.map { existing ->
                if (existing.providerId != providerId) existing else existing.copy(
                    state = result.state,
                    detail = if (result.success) {
                        "✓ ${result.modelsDiscovered} نموذجًا · ${result.latencyMs}ms"
                    } else {
                        "✗ ${result.errorMessage ?: providerStateText(result.state)} · ${result.latencyMs}ms"
                    },
                )
            }
        }
    }

    LaunchedEffect(Unit) { discoverAll() }

    Column(Modifier.fillMaxSize()) {
        V2LuxuryIdentity(
            title = "مستكشف النماذج",
            english = "MODEL EXPLORER",
            subtitle = "اكتشاف حقيقي من كتالوج كل مزوّد — دون أسماء مُختلقة",
            accent = V2Cyan,
        )

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            item {
                V2GlassCard(
                    Modifier.fillMaxWidth(),
                    padding = 11.dp,
                    radius = 14.dp,
                    glow = V2Cyan,
                    borderAlpha = .30f,
                ) {
                    Column {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            KText(
                                if (loading) "جارٍ الاكتشاف…" else "${models.size} نموذجًا مكتشفًا",
                                size = 10.5.sp,
                                weight = FontWeight.SemiBold,
                                color = Color.White,
                            )
                            Spacer(Modifier.weight(1f))
                            TextButton(onClick = ::discoverAll, enabled = !loading) {
                                KText("إعادة الاكتشاف", size = 9.4.sp, color = V2Cyan)
                            }
                        }
                        message?.let { text ->
                            KText(text, size = 9.2.sp, color = V2GoldText, maxLines = 3)
                        }
                    }
                }
            }

            if (discoveries.isNotEmpty()) {
                item {
                    V2GlassCard(
                        Modifier.fillMaxWidth(),
                        padding = 13.dp,
                        radius = 18.dp,
                        glow = V2Blue,
                        borderAlpha = .34f,
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            KText("حالة المزوّدين", size = 12.sp, weight = FontWeight.Bold, color = Color.White)
                            discoveries.forEach { discovery ->
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        Modifier.size(9.dp).clip(RoundedCornerShape(5.dp))
                                            .background(providerStateColor(discovery.state)),
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Column(Modifier.weight(1f)) {
                                        KText(discovery.providerName, size = 10.6.sp, weight = FontWeight.SemiBold, color = Color.White, maxLines = 1)
                                        KText(discovery.detail, size = 8.8.sp, color = V2Soft, maxLines = 2)
                                    }
                                    TextButton(onClick = { testProvider(discovery.providerId) }) {
                                        KText("اختبار", size = 9.sp, color = V2Blue)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (models.isEmpty() && !loading) {
                item {
                    V2GlassCard(
                        Modifier.fillMaxWidth(),
                        padding = 13.dp,
                        radius = 18.dp,
                        glow = V2Soft,
                        borderAlpha = .26f,
                    ) {
                        KText(
                            "لا نماذج لعرضها. أضف مفتاح مزوّد من «خزنة المفاتيح» ثم أعد الاكتشاف. " +
                                "لا تُعرض نماذج ثابتة أو تجريبية.",
                            size = 9.4.sp,
                            color = V2Soft,
                            maxLines = 4,
                        )
                    }
                }
            }

            items(models, key = { "${it.providerId}:${it.modelId}" }) { model ->
                ModelCard(model = model, onTest = { testProvider(model.providerId) })
            }
        }
    }
}

@Composable
private fun ModelCard(
    model: AiModel,
    onTest: () -> Unit,
) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 18.dp,
        glow = V2Cyan,
        borderAlpha = .38f,
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(36.dp).clip(RoundedCornerShape(12.dp))
                        .background(V2Cyan.copy(alpha = .16f))
                        .border(1.dp, V2Cyan.copy(alpha = .42f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Rounded.CloudQueue,
                        contentDescription = null,
                        tint = V2Cyan,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    KText(model.modelId, size = 13.sp, weight = FontWeight.Bold, color = Color.White, maxLines = 1)
                    KText(
                        model.displayName ?: model.providerId,
                        size = 8.6.sp,
                        color = V2Soft,
                        maxLines = 1,
                    )
                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onTest) {
                    KText("اختبار المزوّد", size = 9.4.sp, color = V2Blue)
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    KText("نافذة السياق", size = 10.4.sp, weight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(4.dp))
                    KText(
                        model.contextWindow?.toString() ?: "—",
                        size = 10.4.sp,
                        weight = FontWeight.SemiBold,
                        color = V2GoldText,
                        maxLines = 1,
                    )
                }
                Spacer(Modifier.weight(1f))
                Column {
                    KText("الحد الأقصى للمخرجات", size = 10.4.sp, weight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(4.dp))
                    KText(
                        model.maxOutput?.toString() ?: "—",
                        size = 10.4.sp,
                        weight = FontWeight.SemiBold,
                        color = V2GoldText,
                        maxLines = 1,
                    )
                }
                Spacer(Modifier.weight(1f))
                Column {
                    KText("السعر للإدخال", size = 10.4.sp, weight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(4.dp))
                    KText(
                        model.inputPricePer1M?.let { "\$${it} لكل 1M" } ?: "—",
                        size = 10.4.sp,
                        weight = FontWeight.SemiBold,
                        color = V2GoldText,
                        maxLines = 1,
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                CapabilityChip("رؤية", model.supportsVision)
                CapabilityChip("أدوات", model.supportsToolCalling)
                CapabilityChip("استدلال", model.supportsReasoning)
                CapabilityChip("بث", model.supportsStreaming)
                CapabilityChip("مجاني", model.isFree)
            }
        }
    }
}

@Composable
private fun CapabilityChip(label: String, active: Boolean) {
    Box(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (active) V2Green.copy(alpha = .20f) else Color.Transparent)
            .border(1.dp, if (active) V2Green else V2Muted, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        KText(
            label,
            size = 8.4.sp,
            weight = FontWeight.SemiBold,
            color = if (active) V2Green else V2Muted,
        )
    }
}
