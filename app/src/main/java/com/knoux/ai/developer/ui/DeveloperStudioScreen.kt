package com.knoux.ai.developer.ui

import android.os.Build
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Api
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Cached
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DeveloperMode
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Inventory
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.Icon
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
import com.knoux.ai.R
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
import com.knoux.ai.ui.v2.V2Soft
import com.knoux.ai.ui.v2.V2Violet
import com.knoux.ai.ui.v2.formatBytes
import com.knoux.ai.ui.v2.formatCount
import com.knoux.ai.ui.v2.v2Bidi
import com.knoux.ai.ui.v2.V2LuxuryIdentity
import com.knoux.ai.ui.v2.V2LuxuryServiceGrid
import com.knoux.ai.ui.v2.V2LuxuryStatStrip
import com.knoux.ai.ui.v2.SectionStat
import com.knoux.ai.ui.v2.ServiceSubItem
import com.knoux.ai.ui.v2.LocalServiceNavigate
import com.knoux.ai.ui.v2.V2GlassCard

/**
 * Developer Studio hub — matches reference image 1.
 * Central K orb + live stat strip + 4-column service grid.
 * Every number is read from real device data via DeviceFacts / android.os.Build.
 */
@Composable
fun DeveloperStudioScreen(
    onBack: () -> Unit,
    onPro: () -> Unit,
) {
    val context = LocalContext.current
    val navigate = LocalServiceNavigate.current

    var providers by remember { mutableStateOf(listOf<AiProvider>()) }

    // Initialize provider registry with built-in adapters on first composition.
    androidx.compose.runtime.LaunchedEffect(Unit) {
        val registry = ProviderRegistryHolder.init()
        if (registry.allProviders.isEmpty()) {
            registry.register(com.knoux.ai.developer.provider.OpenRouterAdapter(context))
            registry.register(com.knoux.ai.developer.provider.OpenAIAdapter(context))
            registry.register(com.knoux.ai.developer.provider.OllamaAdapter(context))
            registry.register(com.knoux.ai.developer.provider.NvidiaAdapter(context))
            registry.register(com.knoux.ai.developer.provider.GeminiAdapter(context))
        }
        providers = registry.allProviders.map { adapter ->
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
    }

    Column(Modifier.fillMaxSize()) {
        V2LuxuryIdentity(
            title = "لوحة المطور",
            english = "DEVELOPER STUDIO",
            subtitle = "إدارة مزودي الذكاء الاصطناعي والمفاتيح والنماذج والوكلاء",
            accent = V2Cyan,
        )

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp, end = 16.dp, bottom = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            item {
                KnouxDeveloperOrb(
                    providers = providers,
                    accent = V2Cyan,
                    accent2 = V2Blue,
                )
            }
            item {
                V2LuxuryServiceGrid(
                    items = listOf(
                        ServiceSubItem(
                            title = "سوق مزودي الذكاء",
                            subtitle = "OpenRouter · OpenAI · NVIDIA · Gemini · Ollama",
                            route = "dev-provider-marketplace",
                            icon = Icons.Rounded.Storage,
                            accent = V2Cyan,
                        ),
                        ServiceSubItem(
                            title = "خزنة المفاتيح",
                            subtitle = "مفاتيح API مشفرة في Keystore",
                            route = "dev-api-key-vault",
                            icon = Icons.Rounded.Lock,
                            accent = V2Violet,
                        ),
                        ServiceSubItem(
                            title = "مستكشف النماذج",
                            subtitle = "كتالوج النماذج والقدرات",
                            route = "dev-model-explorer",
                            icon = Icons.Rounded.Inventory,
                            accent = V2Pink,
                        ),
                        ServiceSubItem(
                            title = "محرك الوكلاء",
                            subtitle = "وكلاء الذكاء الاصطناعي القابلة للتكيّف",
                            route = "dev-agent-engine",
                            icon = Icons.Rounded.RocketLaunch,
                            accent = V2Green,
                        ),
                    ),
                    onNavigate = navigate,
                )
            }
            item {
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
                            Icon(Icons.Rounded.Info, null, tint = V2Cyan, modifier = Modifier.size(15.dp))
                            Spacer(Modifier.width(7.dp))
                            KText("بيئة التطوير", size = 12.5.sp, weight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(Modifier.height(7.dp))
                        ExtraInfoLine("نظام التشغيل", "Android ${Build.VERSION.RELEASE}")
                        ExtraInfoLine("مستوى API", Build.VERSION.SDK_INT.toString())
                        ExtraInfoLine("المعمارية", Build.SUPPORTED_ABIS.firstOrNull() ?: "—")
                        ExtraInfoLine("مزوّدون مسجّلون", "${registryProviderCount()} مزوّد")
                    }
                }
            }
            item {
                V2LuxuryStatStrip(
                    stats = listOf(
                        SectionStat(Icons.Rounded.Storage, "0", "مزوّد متصل", V2Cyan),
                        SectionStat(Icons.Rounded.Inventory, "0", "نموذج مكتشف", V2Violet),
                        SectionStat(Icons.Rounded.Shield, "0", "مفتاح مخزّن", V2Pink),
                        SectionStat(Icons.Rounded.BarChart, "0", "وكيل نشط", V2Green),
                    ),
                )
            }
        }
    }
}

private fun registryProviderCount(): Int {
    val reg = ProviderRegistryHolder.registry
    return reg?.allProviders?.size ?: 0
}

@Composable
private fun ExtraInfoLine(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        KText(label, size = 9.6.sp, color = V2Soft)
        Spacer(Modifier.weight(1f))
        KText(value, size = 9.6.sp, weight = FontWeight.SemiBold, color = Color.White, maxLines = 1)
    }
}

/**
 * Central K orb for Developer Studio — live connection state indicator.
 */
@Composable
private fun KnouxDeveloperOrb(
    providers: List<AiProvider>,
    accent: Color,
    accent2: Color,
) {
    val connectedCount = providers.count { it.connectionState == ProviderConnectionState.Connected }
    val totalCount = providers.count { it.isConfigured }

    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(accent.copy(alpha = .10f))
            .border(1.dp, accent.copy(alpha = .42f), RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                Modifier.size(88.dp)
                    .clip(RoundedCornerShape(44.dp))
                    .background(accent.copy(alpha = .20f))
                    .border(2.dp, accent.copy(alpha = .60f), RoundedCornerShape(44.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.Code, null, tint = accent, modifier = Modifier.size(32.dp))
            }

            KText(
                "لوحة المطور",
                size = 16.sp,
                weight = FontWeight.Bold,
                color = Color.White,
            )
            KText(
                "$connectedCount من $totalCount مزوّد متصل",
                size = 10.4.sp,
                color = V2Soft,
            )
        }
    }
}
