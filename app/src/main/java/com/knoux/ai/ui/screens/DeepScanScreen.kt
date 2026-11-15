package com.knoux.ai.ui.screens

import com.knoux.ai.R
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.ui.components.GlassPanel
import com.knoux.ai.ui.components.GradientActionButton
import com.knoux.ai.ui.components.GradientProgressBar
import com.knoux.ai.ui.components.GhostButton
import com.knoux.ai.ui.components.HeroKind
import com.knoux.ai.ui.components.InsightBanner
import com.knoux.ai.ui.components.ReferenceSectionLabel
import com.knoux.ai.ui.components.SignalTile
import com.knoux.ai.ui.components.SignalTileGrid
import com.knoux.ai.ui.components.MiniStatStrip
import com.knoux.ai.ui.components.ProgressDonut
import com.knoux.ai.ui.components.SummaryLine
import com.knoux.ai.ui.data.Fixtures
import com.knoux.ai.ui.theme.ProWarning
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProTextSecondary
import com.knoux.ai.ui.theme.ProTextPrimary
import com.knoux.ai.ui.theme.ProBlue
import com.knoux.ai.ui.theme.ProSafe
import com.knoux.ai.ui.theme.ProDivider
import com.knoux.ai.ui.theme.ProNavy
import com.knoux.ai.ui.theme.ProElevated
import com.knoux.ai.ui.theme.ProDeepInk
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

/**
 * Deep Scan — FIGMA FRAME 12:45 LAYOUT ANCESTOR
 *
 * Geometry from Figma 390×844 (360dp baseline):
 * - Large central radial instrument ~300×325dp
 * - Principal circular gauge ~210–270dp width
 * - Fine arc/tick geometries
 * - Supporting control rail below
 * - Grouped action and condition tiles
 */
@Composable
fun DeepScanScreen(onBack: () -> Unit, onOpenSummary: () -> Unit) {
    var running by remember { mutableStateOf(true) }
    var overallProgress by remember { mutableStateOf(0.72f) }
    var scanPhase by remember { mutableStateOf("Analyzing storage...") }

    androidx.compose.runtime.LaunchedEffect(running) {
        if (running) {
            while (running && overallProgress < 1f) {
                delay(3000)
                if (!running) break
                overallProgress = (overallProgress + 0.05f).coerceAtMost(1f)
                scanPhase = when {
                    overallProgress < 0.3f -> "Analyzing storage..."
                    overallProgress < 0.6f -> "Scanning for duplicates..."
                    overallProgress < 0.85f -> "Checking privacy signals..."
                    else -> "Finalizing report..."
                }
            }
        }
    }

    ToolScreenScaffold(
        hero = HeroKind.Radar,
        title = "Deep ",
        accent = "Scan",
        subtitle = "End-to-end device scan — junk, duplicates, privacy, system health.",
        rightIcon = Icons.Rounded.Groups,
        onBack = onBack,
        heroImage = R.drawable.knoux_hero_deepscan,
        bottomActions = {
            Box(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    GhostButton(
                        text = if (running) "Pause Scan" else "Resume Scan",
                        onClick = { running = !running },
                        icon = if (running) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    )
                    GradientActionButton(
                        text = if (overallProgress >= 1f) "View Summary" else "Scan in progress...",
                        onClick = { if (overallProgress >= 1f) onOpenSummary() },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        },
    ) {
        // FIGMA FRAME 12:45 — CENTRAL RADIAL INSTRUMENT
        item {
            Spacer(Modifier.height(8.dp))
            GlassPanel(Modifier.fillMaxWidth(), padding = 16.dp) {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ScanRadialGauge(
                        progress = overallProgress,
                        phase = scanPhase,
                        sizeDp = 280.dp,
                        onTap = { running = !running },
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        scanPhase,
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = ProTextSecondary,
                        ),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        // SUPPORTING CONTROL RAIL — Figma 12:45: below gauge
        item {
            Spacer(Modifier.height(12.dp))
            GlassPanel(Modifier.fillMaxWidth(), padding = 16.dp) {
                Column {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        ControlTile(
                            icon = Icons.Rounded.Radar,
                            label = "Modules",
                            value = "5 of 5",
                            accent = ProArcticCyan,
                        )
                        ControlTile(
                            icon = Icons.Rounded.Storage,
                            label = "Reclaimable",
                            value = "2.4 GB",
                            accent = ProBlue,
                        )
                        ControlTile(
                            icon = Icons.Rounded.Shield,
                            label = "Privacy",
                            value = "7 alerts",
                            accent = ProWarning,
                        )
                        ControlTile(
                            icon = Icons.Rounded.Schedule,
                            label = "Time left",
                            value = "~1m 24s",
                            accent = ProBlue,
                        )
                    }
                }
            }
        }

        // MODULE LIST — real scan modules
        item {
            Spacer(Modifier.height(16.dp))
            Text(
                "Scan Modules (5)",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ProTextPrimary,
                ),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            )
        }

        items(Fixtures.scanModules.size) { index ->
            val module = Fixtures.scanModules[index]
            val moduleProgress = (overallProgress * 100f).coerceIn(module.progress - 10f, module.progress.toFloat())
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xE61C1140), Color(0xE6120B33))
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.fillMaxWidth()) {
                    Text(
                        module.name,
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ProTextPrimary)
                    )
                    Text(
                        module.description,
                        style = TextStyle(fontSize = 10.sp, color = ProTextSecondary),
                        maxLines = 1
                    )
                    Spacer(Modifier.height(7.dp))
                    GradientProgressBar(moduleProgress / 100f, height = 6.dp)
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    "${moduleProgress.toInt()}%",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ProArcticCyan)
                )
            }
        }

        // COMPLETION INSIGHT
        item {
            Spacer(Modifier.height(16.dp))
            InsightBanner(
                title = "Scan Complete",
                message = "All 12 modules finished. Junk files, duplicates and privacy signals are ready for review in Smart Clean.",
                tint = ProSafe,
                badge = "COMPLETE",
            )
        }
    }
}

/** FIGMA 12:45 — Central Radial Gauge (210-270dp) */
@Composable
fun ScanRadialGauge(
    progress: Float,
    phase: String,
    sizeDp: androidx.compose.ui.unit.Dp = 280.dp,
    onTap: () -> Unit,
) {
    val sweepAngle = 360f * progress
    val radiusPx = sizeDp.value / 2f

    Box(
        Modifier
            .size(sizeDp)
            .clickable(onClick = onTap)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(Color.Transparent),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
            val centerX = this.size.width / 2f
            val centerY = this.size.height / 2f
            val radius = radiusPx

            // Background track (full circle, low opacity)
            drawArc(
                color = ProNavy.copy(alpha = 0.6f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(
                    centerX - radius * 0.82f, centerY - radius * 0.82f
                ),
                size = androidx.compose.ui.geometry.Size(radius * 1.64f, radius * 1.64f),
                style = Stroke(
                    width = 8.dp.toPx(),
                    cap = StrokeCap.Round,
                ),
            )

            // Progress arc — ProArcticCyan (Frostline accent)
            if (progress > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            ProArcticCyan.copy(alpha = 0.6f),
                            ProArcticCyan,
                            ProBlue,
                        ),
                        center = androidx.compose.ui.geometry.Offset(centerX, centerY),
                    ),
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        centerX - radius * 0.82f, centerY - radius * 0.82f
                    ),
                    size = androidx.compose.ui.geometry.Size(radius * 1.64f, radius * 1.64f),
                    style = Stroke(
                        width = 8.dp.toPx(),
                        cap = StrokeCap.Round,
                    ),
                )
            }

            // Frostline highlight — thin inner sweep (1-2dp)
            drawArc(
                color = ProArcticCyan.copy(alpha = 0.35f),
                startAngle = -90f + sweepAngle - 8f,
                sweepAngle = 8f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(
                    centerX - radius * 0.82f, centerY - radius * 0.82f
                ),
                size = androidx.compose.ui.geometry.Size(radius * 1.64f, radius * 1.64f),
                style = Stroke(
                    width = 1.5.dp.toPx(),
                    cap = StrokeCap.Round,
                ),
            )

            // Center background
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(ProNavy, ProDeepInk),
                    center = androidx.compose.ui.geometry.Offset(centerX, centerY),
                    radius = radius * 0.7f,
                ),
                center = androidx.compose.ui.geometry.Offset(centerX, centerY),
                radius = radius * 0.7f,
            )
        }
        // Box content - center text overlay
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                "${(progress * 100).toInt()}%",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    lineHeight = 38.sp,
                    color = ProTextPrimary,
                ),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                phase,
                style = TextStyle(fontSize = 11.sp, color = ProTextSecondary),
            )
        }
    }
}

/** Supporting control tile — Figma 12:45 control rail */
@Composable
fun ControlTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    accent: Color,
) {
    GlassPanel(
        modifier = Modifier.fillMaxWidth(),
        padding = 12.dp,
        radius = 14.dp,
        borderColor = accent.copy(alpha = 0.25f),
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(4.dp))
            Text(value, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ProTextPrimary))
            Spacer(Modifier.height(2.dp))
            Text(label, style = TextStyle(fontSize = 9.5.sp, color = ProTextSecondary))
        }
    }
}