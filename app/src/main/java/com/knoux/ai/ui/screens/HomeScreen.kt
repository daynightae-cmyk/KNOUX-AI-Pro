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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProBlue
import com.knoux.ai.ui.theme.ProDeepInk
import com.knoux.ai.ui.theme.ProDivider
import com.knoux.ai.ui.theme.ProElevated
import com.knoux.ai.ui.theme.ProNavy
import com.knoux.ai.ui.theme.ProTextPrimary
import com.knoux.ai.ui.theme.ProTextSecondary
import com.knoux.ai.ui.theme.ProSafe
import com.knoux.ai.ui.theme.ProWarning

/**
 * Home — "Smart Device Hub" — FIGMA FRAME 2:2 LAYOUT ANCESTOR
 *
 * Geometry from Figma 390×844 (360dp baseline):
 * - Top toolbar: 56dp — compact logo, avatar, actions (handled by Scaffold)
 * - Status panel: ~80dp — concise metrics (Storage, Battery, Last scan)
 * - 2-column service grid: balanced tile rhythm, 16dp horizontal, 12dp vertical
 * - Bottom nav: handled by Scaffold (4 tabs: Home/Clean/Tools/Reports)
 */
@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    val motion = rememberMotionEnabled()

    val services = listOf(
        HomeServiceCardModel(
            title = "Smart Clean",
            subtitle = "Junk, duplicates, residue — previewed",
            route = "smartclean",
            art = R.drawable.knoux_home_icon_clean,
            accent = ProArcticCyan,
        ),
        HomeServiceCardModel(
            title = "Duplicate Files",
            subtitle = "Hash-verified media & documents",
            route = "duplicates",
            art = R.drawable.knoux_home_icon_duplicate,
            accent = ProArcticCyan,
        ),
        HomeServiceCardModel(
            title = "App Residue",
            subtitle = "Leftovers & empty folders",
            route = "residue",
            art = R.drawable.knoux_svc_residue,
            accent = ProArcticCyan,
        ),
        HomeServiceCardModel(
            title = "Storage Analyzer",
            subtitle = "Category breakdown & large files",
            route = "storage",
            art = R.drawable.knoux_home_icon_storage,
            accent = ProArcticCyan,
        ),
        HomeServiceCardModel(
            title = "Privacy Sweep",
            subtitle = "Trackers, permissions, exposure",
            route = "privacy",
            art = R.drawable.knoux_home_icon_privacy,
            accent = ProArcticCyan,
        ),
        HomeServiceCardModel(
            title = "App Manager",
            subtitle = "Installed apps, usage, heavy weights",
            route = "appmanager",
            art = R.drawable.knoux_svc_apps,
            accent = ProArcticCyan,
        ),
        HomeServiceCardModel(
            title = "Battery Saver",
            subtitle = "Charge, temp, background control",
            route = "battery",
            art = R.drawable.knoux_home_icon_battery,
            accent = ProArcticCyan,
        ),
        HomeServiceCardModel(
            title = "AI Optimization",
            subtitle = "Evidence-backed tuning",
            route = "ai",
            art = R.drawable.knoux_home_icon_ai,
            accent = ProArcticCyan,
        ),
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 12.dp,
            end = 16.dp,
            bottom = 90.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { Spacer(Modifier.height(8.dp)) }

        // STATUS PANEL: 3 concise metrics
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StatusMetricCard(
                    label = "Storage",
                    value = "128 GB / 256 GB",
                    trend = "12% free",
                    icon = Icons.Rounded.Info,
                    accent = ProArcticCyan,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate("storage") },
                )
                StatusMetricCard(
                    label = "Battery",
                    value = "78%",
                    trend = "6h 42m left",
                    icon = Icons.Rounded.Bolt,
                    accent = ProWarning,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate("battery") },
                )
                StatusMetricCard(
                    label = "Last Scan",
                    value = "2h ago",
                    trend = "Clean",
                    icon = Icons.Rounded.Radar,
                    accent = ProSafe,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate("reports") },
                )
            }
        }

        // SECTION TITLE
        item {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    "Services",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        color = ProTextPrimary,
                    ),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Tap any service to open its workspace",
                    style = TextStyle(fontSize = 10.sp, lineHeight = 14.sp, color = ProTextSecondary),
                )
            }
        }

        // 2-COLUMN SERVICE GRID
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                services.chunked(2).forEach { pair ->
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
                        if (pair.size == 1) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // QUICK ACTIONS STRIP
        item {
            Column {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Quick Actions",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = ProTextPrimary,
                        ),
                    )
                    Text(
                        "Tap to start",
                        style = TextStyle(fontSize = 10.sp, color = ProTextSecondary),
                    )
                }
                Spacer(Modifier.height(8.dp))
                QuickActionStrip(onNavigate = onNavigate)
            }
        }

        // PREVIEW BANNER
        item {
            PreviewBanner()
        }

        item { Spacer(Modifier.height(20.dp)) }
    }
}

data class HomeServiceCardModel(
    val title: String,
    val subtitle: String,
    val route: String,
    val art: Int,
    val accent: Color,
)

/** Compact status metric card — Figma top tracker style */
@Composable
fun StatusMetricCard(
    label: String,
    value: String,
    trend: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    PremiumGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        padding = 12.dp,
        radius = 16.dp,
        accent = accent,
        borderAlpha = 0.30f,
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    label,
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        color = ProTextSecondary,
                    ),
                )
                Icon(
                    icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(18.dp),
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                value,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    color = ProTextPrimary,
                ),
                maxLines = 1,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                trend,
                style = TextStyle(fontSize = 9.5.sp, lineHeight = 12.sp, color = ProTextSecondary),
            )
        }
    }
}

/** Service tile — Figma 2-column card grammar */
@Composable
fun HomeServiceTile(
    service: HomeServiceCardModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PremiumGlassCard(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp),
        padding = 12.dp,
        radius = 16.dp,
        accent = service.accent,
        borderAlpha = 0.30f,
    ) {
        Column {
            Text(
                service.title,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                    color = ProTextPrimary,
                ),
                maxLines = 2,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                service.subtitle,
                style = TextStyle(fontSize = 9.5.sp, lineHeight = 12.sp, color = ProTextSecondary),
                maxLines = 2,
            )
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                com.knoux.ai.ui.components.RasterGlowIcon(
                    image = service.art,
                    size = 56.dp,
                    glow = service.accent,
                    modifier = Modifier.weight(1f),
                )
                Box(
                    Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    service.accent.copy(alpha = 0.30f),
                                    Color.White.copy(alpha = 0.06f),
                                )
                            )
                        )
                        .border(1.dp, service.accent.copy(alpha = 0.40f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        tint = ProTextPrimary,
                        modifier = Modifier.size(13.dp),
                    )
                }
            }
        }
    }
}

/** Three quick actions in one strip */
@Composable
fun QuickActionStrip(onNavigate: (String) -> Unit) {
    val actions = listOf(
        Triple("Deep Scan", Icons.Rounded.Radar, "deepscan"),
        Triple("Save Battery", Icons.Rounded.Bolt, "battery"),
        Triple("Secure Device", Icons.Rounded.Security, "privacy"),
    )
    PremiumGlassCard(
        modifier = Modifier.fillMaxWidth(),
        padding = 10.dp,
        radius = 16.dp,
        accent = ProBlue,
        borderAlpha = 0.28f,
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
                            .height(28.dp)
                            .background(Color.White.copy(alpha = 0.10f)),
                    )
                }
                Column(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigate(route) }
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = ProArcticCyan,
                        modifier = Modifier.size(19.dp),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        label,
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 9.5.sp,
                            color = ProTextPrimary,
                        ),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

/** Honest banner: every number in the build is a design preview. */
@Composable
fun PreviewBanner() {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .border(1.dp, Color.White.copy(alpha = 0.07f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Rounded.Info,
            contentDescription = null,
            tint = ProTextSecondary,
            modifier = Modifier.size(14.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "Preview / Demo Sample Only — these are example values and do not represent live results from your device.",
            style = TextStyle(fontSize = 9.sp, lineHeight = 11.5.sp, color = ProTextSecondary),
        )
    }
}