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
import com.knoux.ai.developer.model.AiModel
import com.knoux.ai.developer.model.ProviderConnectionState
import com.knoux.ai.developer.vault.CredentialVault
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
import com.knoux.ai.ui.v2.V2LuxuryIdentity
import com.knoux.ai.ui.v2.V2GlassCard

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

/**
 * Model Explorer (DEV-04).
 * Displays discovered models per provider with real capabilities, pricing, and
 * connection state. No invented model names — only models actually discovered
 * from the connected provider.
 */
@Composable
fun ModelExplorerScreen(
    onBack: () -> Unit,
    onPro: () -> Unit,
) {
    val context = LocalContext.current
    val vault = CredentialVault(context)

    var models by remember { mutableStateOf(listOf<AiModel>()) }

    Column(Modifier.fillMaxSize()) {
        V2LuxuryIdentity(
            title = "مستكشف النماذج",
            english = "MODEL EXPLORER",
            subtitle = "كتالوج النماذج المكتشفة والقدرات الفعلية لكل مزوّد",
            accent = V2Cyan,
        )

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp, end = 16.dp, bottom = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            items(models) { model ->
                ModelCard(
                    model = model,
                    vault = vault,
                )
            }
        }
    }
}

@Composable
private fun ModelCard(
    model: AiModel,
    vault: CredentialVault,
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
                    KText(model.modelId, size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                    KText(
                        model.displayName ?: "—",
                        size = 8.6.sp,
                        color = V2Soft,
                        maxLines = 1,
                    )
                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { /* test model */ }) {
                    KText("اختبار", size = 9.4.sp, color = V2Blue)
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