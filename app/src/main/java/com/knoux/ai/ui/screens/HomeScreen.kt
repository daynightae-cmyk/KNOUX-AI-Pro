package com.knoux.ai.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.R
import com.knoux.ai.ui.components.PremiumGlassCard
import com.knoux.ai.ui.components.RasterGlowIcon
import com.knoux.ai.ui.components.ScreenTitle
import com.knoux.ai.ui.components.rememberMotionEnabled
import com.knoux.ai.ui.theme.KnouxCyan
import com.knoux.ai.ui.theme.KnouxMagenta
import com.knoux.ai.ui.theme.KnouxMutedText
import com.knoux.ai.ui.theme.KnouxPanelDeep
import com.knoux.ai.ui.theme.KnouxSoftText
import com.knoux.ai.ui.theme.KnouxText
import com.knoux.ai.ui.theme.KnouxViolet

/**
 * Home — "Smart Device Hub".
 *
 * The composition follows the approved reference: a holographic globe hero,
 * a centred title block, a two-column service grid where every card carries
 * its own 3D artwork, a quick-action strip and an honest preview banner.
 */
@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    val motion = rememberMotionEnabled()

    val services = listOf(
        HomeServiceCardModel(
            title = "Smart Clean",
            subtitle = "Remove junk, duplicates and app residue.",
            route = "smartclean",
            art = R.drawable.knoux_home_icon_clean,
            accent = KnouxMagenta,
        ),
        HomeServiceCardModel(
            title = "AI Optimization",
            subtitle = "Boost performance, tune system and save battery.",
            route = "ai",
            art = R.drawable.knoux_home_icon_ai,
            accent = KnouxCyan,
        ),
        HomeServiceCardModel(
            title = "Privacy Sweep",
            subtitle = "Review trackers, risky permissions and exposure.",
            route = "privacy",
            art = R.drawable.knoux_home_icon_privacy,
            accent = KnouxMagenta,
        ),
        HomeServiceCardModel(
            title = "Storage Analyzer",
            subtitle = "See what's using space and what's reclaimable.",
            route = "storage",
            art = R.drawable.knoux_home_icon_storage,
            accent = KnouxCyan,
        ),
        HomeServiceCardModel(
            title = "App Manager",
            subtitle = "Manage installed apps, usage and heavy weights.",
            route = "appmanager",
            art = R.drawable.knoux_svc_apps,
            accent = KnouxMagenta,
        ),
        HomeServiceCardModel(
            title = "Reports & Insights",
            subtitle = "Track cleanups, optimizations and device health trends.",
            route = "reports",
            art = R.drawable.knoux_hero_reports,
            accent = KnouxCyan,
        ),
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            HomeHeroGlobe(motion = motion)
        }

        item {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ScreenTitle(
                    "Smart ",
                    accent = "Device Hub",
                    accentColors = listOf(KnouxMagenta, KnouxCyan),
                    fontSize = 28.sp,
                    lineHeight = 33.sp,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "Device intelligence, optimization and cleanup — with a clear preview before any changes.",
                    style = TextStyle(fontSize = 11.sp, lineHeight = 15.sp, color = KnouxSoftText),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 14.dp),
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                services.chunked(2).forEachIndexed { rowIndex, pair ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        pair.forEach { service ->
                            HomeServiceTile(
                                service = service,
                                onClick = { onNavigate(service.route) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        if (pair.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            Column {
                Text(
                    "Quick Actions",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = KnouxText,
                    ),
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                QuickActionStrip(onNavigate = onNavigate)
            }
        }

        item {
            PreviewBanner()
        }
    }
}

private data class HomeServiceCardModel(
    val title: String,
    val subtitle: String,
    val route: String,
    val art: Int,
    val accent: Color,
)

/**
 * The holographic globe hero.
 *
 * The artwork keeps its original aspect ratio and fades into the app canvas
 * through Screen blending, so no rectangular crop is ever visible.
 */
@Composable
private fun HomeHeroGlobe(motion: Boolean) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(208.dp),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(KnouxMagenta.copy(alpha = 0.20f), Color.Transparent),
                    center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.50f),
                    radius = size.width * 0.48f,
                ),
                center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.50f),
                radius = size.width * 0.48f,
            )
        }
        RasterGlowIcon(
            image = R.drawable.knoux_home_hero,
            size = 208.dp,
            glow = KnouxMagenta,
        )
    }
}

/**
 * One service tile: title, description, its own 3D artwork and a compact
 * chevron affordance — the composition used by the approved reference.
 */
@Composable
private fun HomeServiceTile(
    service: HomeServiceCardModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PremiumGlassCard(
        modifier = modifier.clickable(onClick = onClick),
        padding = 12.dp,
        radius = 18.dp,
        accent = service.accent,
        borderAlpha = 0.40f,
    ) {
        Column {
            Text(
                service.title,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    lineHeight = 17.sp,
                    color = KnouxText,
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                service.subtitle,
                style = TextStyle(fontSize = 9.sp, lineHeight = 12.sp, color = KnouxSoftText),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(6.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RasterGlowIcon(
                    image = service.art,
                    size = 64.dp,
                    glow = service.accent,
                    modifier = Modifier.weight(1f),
                )
                Box(
                    Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    service.accent.copy(alpha = 0.30f),
                                    Color.White.copy(alpha = 0.06f),
                                )
                            )
                        )
                        .border(1.dp, service.accent.copy(alpha = 0.45f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        tint = KnouxText,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }
    }
}

/** Three quick actions in one strip, matching the approved reference. */
@Composable
private fun QuickActionStrip(onNavigate: (String) -> Unit) {
    val actions = listOf(
        Triple("Deep Scan", Icons.Rounded.Radar, "deepscan"),
        Triple("Save Battery", Icons.Rounded.Bolt, "battery"),
        Triple("Secure Device", Icons.Rounded.Security, "privacy"),
    )
    PremiumGlassCard(
        modifier = Modifier.fillMaxWidth(),
        padding = 10.dp,
        radius = 18.dp,
        accent = KnouxViolet,
        borderAlpha = 0.34f,
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            actions.forEachIndexed { index, (label, icon, route) ->
                if (index > 0) {
                    Box(
                        Modifier
                            .width(1.dp)
                            .height(32.dp)
                            .background(Color.White.copy(alpha = 0.12f)),
                    )
                }
                Column(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onNavigate(route) }
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = KnouxCyan,
                        modifier = Modifier.size(21.dp),
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(
                        label,
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                            color = KnouxText,
                        ),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

/** Honest banner: every number in the build is a design preview. */
@Composable
private fun PreviewBanner() {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .border(1.dp, Color.White.copy(alpha = 0.09f), RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Rounded.Info,
            contentDescription = null,
            tint = KnouxMutedText,
            modifier = Modifier.size(15.dp),
        )
        Spacer(Modifier.width(9.dp))
        Text(
            "Preview / Demo Sample Only — these are example values and do not represent live results from your device.",
            style = TextStyle(fontSize = 9.sp, lineHeight = 12.5.sp, color = KnouxSoftText),
        )
    }
}
