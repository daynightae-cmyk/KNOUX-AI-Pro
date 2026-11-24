package com.knoux.ai.developer.ui

import android.os.Build
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
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
import com.knoux.ai.ui.v2.V2TopBar
import com.knoux.ai.ui.v2.V2TopMode
import com.knoux.ai.ui.v2.V2LuxuryServiceGrid
import com.knoux.ai.ui.v2.V2LuxuryStatStrip
import com.knoux.ai.ui.v2.SectionStat
import com.knoux.ai.ui.v2.ServiceSubItem
import com.knoux.ai.ui.v2.LocalServiceNavigate
import com.knoux.ai.ui.v2.V2GlassCard
import com.knoux.ai.ui.v2.FixedServiceScreen
import com.knoux.ai.ui.v2.ServiceSpecs
import com.knoux.ai.ui.v2.KnouxRunOrbBlock
import com.knoux.ai.ui.components.rememberMotionEnabled

/**
 * Developer Studio hub — matches reference image 1.
 * Central K orb (cosmic) + live stat strip + 4-column service grid.
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

    val connectedCount = providers.count { it.connectionState == ProviderConnectionState.Connected }
    val totalCount = providers.count { it.isConfigured }
    val status = "$connectedCount من $totalCount مزوّد متصل"
    val statusColor = if (connectedCount > 0) V2Green else V2Amber
    val metric = "${registryProviderCount()} مزوّد مسجّل"

    Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)) {
        Column(Modifier.fillMaxSize()) {
            V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = onPro)
            FixedServiceScreen(
                spec = ServiceSpecs.developer,
                status = status,
                statusColor = statusColor,
                metric = metric,
                scene = { modifier -> DeveloperCosmicOrb(modifier, V2Cyan, V2Violet, status) },
                action = null,
                listState = rememberLazyListState(),
            ) {
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
                V2LuxuryStatStrip(
                    stats = listOf(
                        SectionStat(Icons.Rounded.Storage, connectedCount.toString(), "مزوّد متصل", V2Cyan),
                        SectionStat(Icons.Rounded.Inventory, "0", "نموذج مكتشف", V2Violet),
                        SectionStat(Icons.Rounded.Shield, totalCount.toString(), "مفتاح مخزّن", V2Pink),
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
 * Cosmic K orb for Developer Studio hero — matches the reference:
 * the stag floats behind a living K jewel with orbiting rings.
 */
@Composable
private fun DeveloperCosmicOrb(
    modifier: Modifier,
    accent: Color,
    accent2: Color,
    status: String,
) {
    val motion = rememberMotionEnabled()
    val transition = rememberInfiniteTransition(label = "devStudioOrb")
    val orbit by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (motion) 360f else 0f,
        animationSpec = infiniteRepeatable(tween(9000, easing = androidx.compose.animation.core.LinearEasing), androidx.compose.animation.core.RepeatMode.Restart),
        label = "devOrbit",
    )
    val counter by transition.animateFloat(
        initialValue = 360f,
        targetValue = if (motion) 0f else 360f,
        animationSpec = infiniteRepeatable(tween(14000, easing = androidx.compose.animation.core.LinearEasing), androidx.compose.animation.core.RepeatMode.Restart),
        label = "devCounter",
    )
    val breath by transition.animateFloat(
        initialValue = 1f,
        targetValue = if (motion) 1.04f else 1f,
        animationSpec = infiniteRepeatable(tween(2600, easing = androidx.compose.animation.core.FastOutSlowInEasing), androidx.compose.animation.core.RepeatMode.Reverse),
        label = "devBreath",
    )

    Box(
        modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(listOf(
                    accent.copy(alpha = .18f),
                    accent2.copy(alpha = .08f),
                    Color(0xFF07051C),
                )),
            )
            .border(1.dp, accent.copy(alpha = .35f), androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // The stag floats behind the orb
            Box(
                Modifier
                    .size(120.dp)
                    .graphicsLayer {
                        scaleX = 1f + breath * .02f
                        scaleY = 1f + breath * .02f
                        alpha = .6f + breath * .12f
                    }
                    .background(
                        Brush.radialGradient(
                            listOf(
                                accent2.copy(alpha = .35f),
                                accent.copy(alpha = .15f),
                                Color.Transparent,
                            ),
                        ),
                        androidx.compose.foundation.shape.CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(R.drawable.knoux_stag_official),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .graphicsLayer { translationY = -8f },
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                )
            }
            // The K jewel
            Box(
                Modifier.size(88.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(44.dp))
                    .background(accent.copy(alpha = .20f))
                    .border(2.dp, accent.copy(alpha = .60f), androidx.compose.foundation.shape.RoundedCornerShape(44.dp))
                    .graphicsLayer { scaleX = breath; scaleY = breath },
                contentAlignment = Alignment.Center,
            ) {
                androidx.compose.material3.Icon(
                    androidx.compose.material.icons.Icons.Rounded.Code,
                    null,
                    tint = accent,
                    modifier = Modifier.size(32.dp),
                )
            }
            // Orbiting rings
            Box(
                Modifier.size(120.dp)
                    .graphicsLayer { rotationZ = orbit },
                contentAlignment = Alignment.Center,
            ) {
                androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
                    val radius = size.width / 2f
                    val center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)
                    drawArc(
                        brush = androidx.compose.ui.graphics.Brush.sweepGradient(
                            listOf(
                                Color.Transparent,
                                accent.copy(alpha = .85f),
                                accent2.copy(alpha = .75f),
                                Color.Transparent,
                            ),
                            center = center,
                        ),
                        startAngle = orbit,
                        sweepAngle = 230f,
                        useCenter = false,
                        topLeft = androidx.compose.ui.geometry.Offset(center.x - radius * .95f, center.y - radius * .95f),
                        size = androidx.compose.ui.geometry.Size(radius * 1.9f, radius * 1.9f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round),
                    )
                }
            }
            Box(
                Modifier.size(110.dp)
                    .graphicsLayer { rotationZ = -counter * .7f },
                contentAlignment = Alignment.Center,
            ) {
                androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
                    val radius = size.width / 2f
                    val center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)
                    drawArc(
                        brush = androidx.compose.ui.graphics.Brush.sweepGradient(
                            listOf(
                                Color.Transparent,
                                accent2.copy(alpha = .70f),
                                accent.copy(alpha = .70f),
                                Color.Transparent,
                            ),
                            center = center,
                        ),
                        startAngle = -counter * .7f,
                        sweepAngle = 190f,
                        useCenter = false,
                        topLeft = androidx.compose.ui.geometry.Offset(center.x - radius * .86f, center.y - radius * .86f),
                        size = androidx.compose.ui.geometry.Size(radius * 1.72f, radius * 1.72f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round),
                    )
                }
            }
            KText(
                "لوحة المطور",
                size = 16.sp,
                weight = FontWeight.Bold,
                color = Color.White,
            )
            KText(
                status,
                size = 10.4.sp,
                color = V2Soft,
            )
        }
    }
}
