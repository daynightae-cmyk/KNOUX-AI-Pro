package com.knoux.ai.developer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.developer.model.AiProvider
import com.knoux.ai.developer.model.ProviderConnectionState
import com.knoux.ai.developer.provider.ProviderRegistryHolder
import com.knoux.ai.ui.v2.KText
import com.knoux.ai.ui.v2.V2Amber
import com.knoux.ai.ui.v2.V2Background
import com.knoux.ai.ui.v2.V2Blue
import com.knoux.ai.ui.v2.V2Cyan
import com.knoux.ai.ui.v2.V2Deep
import com.knoux.ai.ui.v2.V2GoldText
import com.knoux.ai.ui.v2.V2Green
import com.knoux.ai.ui.v2.V2Pink
import com.knoux.ai.ui.v2.V2Red
import com.knoux.ai.ui.v2.V2Soft
import com.knoux.ai.ui.v2.V2Violet
import com.knoux.ai.ui.v2.V2Muted
import com.knoux.ai.ui.v2.formatCount
import com.knoux.ai.ui.v2.v2Bidi
import com.knoux.ai.ui.v2.V2LuxuryIdentity
import com.knoux.ai.ui.v2.V2GlassCard

/**
 * AI Provider Marketplace (DEV-01).
 * Lists every registered provider with its real connection state.
 * Tapping a provider opens configuration / API key entry.
 */
@Composable
fun ProviderMarketplaceScreen(
    onBack: () -> Unit,
    onPro: () -> Unit,
) {
    val context = LocalContext.current
    val registry = ProviderRegistryHolder.init()

    var providers by remember { mutableStateOf(listOf<AiProvider>()) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        val list = registry.allProviders.map { adapter ->
            AiProvider(
                id = adapter.providerId,
                name = adapter.displayName,
                baseUrl = null,
                authScheme = adapter.authScheme,
                supportsModelDiscovery = adapter.supportsModelDiscovery,
                supportsStreaming = adapter.supportsStreaming,
                supportsToolCalling = adapter.supportsToolCalling,
                supportsVision = adapter.supportsVision,
                supportsReasoning = adapter.supportsReasoning,
                isConfigured = false,
                connectionState = ProviderConnectionState.NotConfigured,
            )
        }
        providers = list
    }

    Column(Modifier.fillMaxSize()) {
        V2LuxuryIdentity(
            title = "سوق مزودي الذكاء الاصطناعي",
            english = "AI PROVIDER MARKETPLACE",
            subtitle = "إدارة مزودي النماذج والاتصال بهم عبر محوّلات مستقلة",
            accent = V2Cyan,
        )

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp, end = 16.dp, bottom = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            items(providers) { provider ->
                ProviderCard(
                    provider = provider,
                    onConfigure = { /* open key entry dialog */ },
                    onTest = { /* test connection */ },
                    onDiscoverModels = { /* discover models */ },
                )
            }
        }
    }
}

@Composable
private fun ProviderCard(
    provider: AiProvider,
    onConfigure: () -> Unit,
    onTest: () -> Unit,
    onDiscoverModels: () -> Unit,
) {
    val stateColor = when (provider.connectionState) {
        ProviderConnectionState.Connected -> V2Green
        ProviderConnectionState.Connecting, ProviderConnectionState.Authenticating,
        ProviderConnectionState.FetchingModels -> V2Blue
        ProviderConnectionState.InvalidApiKey, ProviderConnectionState.UnauthorizedModel -> V2Red
        ProviderConnectionState.RateLimited, ProviderConnectionState.InsufficientCredits -> V2Amber
        ProviderConnectionState.ProviderUnavailable, ProviderConnectionState.Offline -> V2Pink
        ProviderConnectionState.DiscoveryUnsupported -> V2Soft
        ProviderConnectionState.NotConfigured -> V2Muted
    }

    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 18.dp,
        glow = stateColor,
        borderAlpha = .38f,
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(36.dp).clip(RoundedCornerShape(12.dp))
                        .background(stateColor.copy(alpha = .16f))
                        .border(1.dp, stateColor.copy(alpha = .42f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Rounded.CloudQueue,
                        contentDescription = null,
                        tint = stateColor,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    KText(provider.name, size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                    KText(
                        provider.authScheme.v2Bidi(),
                        size = 8.6.sp,
                        color = V2Soft,
                        maxLines = 1,
                    )
                }
                Spacer(Modifier.weight(1f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    if (provider.supportsModelDiscovery) {
                        TextButton(onClick = onDiscoverModels, enabled = provider.isConfigured) {
                            KText("اكتشاف النماذج", size = 9.4.sp, color = V2Violet)
                        }
                    }
                    TextButton(onClick = onTest, enabled = provider.connectionState != ProviderConnectionState.Connecting) {
                        KText("اختبار الاتصال", size = 9.4.sp, color = V2Blue)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    KText(
                        when (provider.connectionState) {
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
                        }.v2Bidi(),
                        size = 10.4.sp,
                        weight = FontWeight.Bold,
                        color = stateColor
                    )
                    Spacer(Modifier.height(4.dp))
                    KText(
                        if (provider.isConfigured) "مكوّن" else "غير مكوّن",
                        size = 8.4.sp,
                        color = V2Soft,
                    )
                }
                Spacer(Modifier.weight(1f))
                TextButton(
                    onClick = onConfigure,
                    enabled = provider.connectionState != ProviderConnectionState.Connecting,
                ) {
                    KText("تهيئة", size = 10.sp, color = V2Cyan)
                }
            }
        }
    }
}
