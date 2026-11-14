package com.knoux.ai.ui.screens

import com.knoux.ai.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Bolt
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
import com.knoux.ai.ui.theme.KnouxAmber
import com.knoux.ai.ui.theme.KnouxCyan
import com.knoux.ai.ui.theme.KnouxMagenta
import com.knoux.ai.ui.theme.KnouxSoftText
import com.knoux.ai.ui.theme.KnouxText
import com.knoux.ai.ui.theme.KnouxViolet

@Composable
fun DeepScanScreen(onBack: () -> Unit, onOpenSummary: () -> Unit) {
    var running by remember { mutableStateOf(true) }

    ToolScreenScaffold(
        hero = HeroKind.Radar,
        title = "Deep ",
        accent = "Scan",
        subtitle = "Run an end-to-end device scan for junk, duplicates, privacy issues, and system health.",
        rightIcon = Icons.Rounded.Groups,
        onBack = onBack,
        heroImage = R.drawable.knoux_hero_deepscan,
        bottomActions = {
            Box(Modifier.weight(1f)) {
                GhostButton(
                    text = if (running) "Pause Scan" else "Resume Scan",
                    onClick = { running = !running },
                    icon = if (running) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                )
            }
            Box(Modifier.weight(1f)) {
                GradientActionButton(
                    text = "View Summary",
                    onClick = onOpenSummary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    ) {
        item {
            GlassPanel(Modifier.fillMaxWidth(), padding = 16.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressDonut(
                        progress = 0.72f,
                        centerText = "72%",
                        caption = "Scan\nProgress",
                        arcColors = listOf(KnouxCyan, KnouxMagenta),
                        size = 96.dp,
                    )
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Analyzing device state",
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp, color = KnouxText)
                        )
                        Spacer(Modifier.height(10.dp))
                        GradientProgressBar(0.72f)
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
            GlassPanel(Modifier.fillMaxWidth(), padding = 12.dp) {
                MiniStatStrip(Fixtures.deepScanMetrics)
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
            GlassPanel(Modifier.fillMaxWidth(), padding = 16.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressDonut(
                        progress = 0.72f,
                        centerText = "72%",
                        caption = "Overall\nScan",
                        arcColors = listOf(KnouxMagenta, KnouxCyan),
                        size = 92.dp,
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        SummaryLine(Icons.Rounded.Groups, "Modules scanned:", "5 of 5", KnouxCyan)
                        SummaryLine(Icons.Rounded.WarningAmber, "Findings so far:", "1,656 items", KnouxAmber)
                        SummaryLine(Icons.Rounded.Schedule, "Estimated time:", "~ 1 min 24 sec", KnouxViolet)
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
            Text(
                "Scan Modules (5)",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = KnouxText),
                modifier = Modifier.fillMaxWidth()
            )
        }

        items(Fixtures.scanModules.size) { index ->
            val module = Fixtures.scanModules[index]
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
                    .padding(horizontal = 12.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        module.name,
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KnouxText)
                    )
                    Text(
                        module.description,
                        style = TextStyle(fontSize = 10.sp, color = KnouxSoftText),
                        maxLines = 1
                    )
                    Spacer(Modifier.height(7.dp))
                    GradientProgressBar(module.progress / 100f, height = 6.dp)
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    "${module.progress}%",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp, color = KnouxCyan)
                )
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
            InsightBanner(
                title = "Scan Insight",
                message = "12 modules are checked in this pass. Junk files, duplicates and privacy signals stay separate so nothing is mixed up.",
                tint = KnouxCyan,
                badge = "12 MODULES",
            )
        }

        item {
            Spacer(Modifier.height(12.dp))
            ReferenceSectionLabel("Scan signals", "preview values")
            Spacer(Modifier.height(6.dp))
            SignalTileGrid(
                tiles = listOf(
                    SignalTile(Icons.Rounded.Storage, "2.4 GB", "Reclaimable", KnouxMagenta),
                    SignalTile(Icons.Rounded.Shield, "7", "Privacy alerts", KnouxCyan),
                    SignalTile(Icons.Rounded.Memory, "5", "Apps to review", KnouxViolet),
                    SignalTile(Icons.Rounded.Bolt, "5", "Battery sources", KnouxAmber),
                ),
            )
        }

    }
}
