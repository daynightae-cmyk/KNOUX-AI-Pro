package com.knoux.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Trophy
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.knoux.ai.ui.components.MiniStatStrip
import com.knoux.ai.ui.components.NeonIconTile
import com.knoux.ai.ui.components.ProgressDonut
import com.knoux.ai.ui.components.SummaryLine
import com.knoux.ai.ui.components.iconFor
import com.knoux.ai.ui.data.Fixtures
import com.knoux.ai.ui.theme.KnouxAmber
import com.knoux.ai.ui.theme.KnouxCyan
import com.knoux.ai.ui.theme.KnouxMagenta
import com.knoux.ai.ui.theme.KnouxPanelAlt
import com.knoux.ai.ui.theme.KnouxPanelDeep
import com.knoux.ai.ui.theme.KnouxSoftText
import com.knoux.ai.ui.theme.KnouxText
import com.knoux.ai.ui.theme.KnouxViolet

@Composable
fun StorageAnalyzerScreen(onBack: () -> Unit) {
    ToolScreenScaffold(
        hero = HeroKind.Disks,
        title = "Storage ",
        accent = "Analyzer",
        subtitle = "Understand storage usage, large files, and space distribution.",
        rightIcon = Icons.Rounded.Storage,
        onBack = onBack,
        bottomActions = {
            Box(Modifier.weight(1f)) { GhostButton("View Breakdown", { }) }
            Box(Modifier.weight(1f)) { GradientActionButton("Manage Space", { }, Modifier.fillMaxWidth()) }
        },
    ) {
        item {
            GlassPanel(Modifier.fillMaxWidth(), padding = 16.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NeonIconTile(Icons.Rounded.Storage, size = 44.dp, iconSize = 22.dp, tint = KnouxMagenta)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                "128 GB",
                                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 21.sp, color = KnouxText)
                            )
                            Text(
                                " / 256 GB used",
                                style = TextStyle(fontSize = 12.sp, color = KnouxSoftText)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        GradientProgressBar(0.5f)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "50%",
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KnouxSoftText)
                    )
                }
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
            GlassPanel(Modifier.fillMaxWidth(), padding = 12.dp) {
                MiniStatStrip(Fixtures.storageTopStats)
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
            GlassPanel(Modifier.fillMaxWidth(), padding = 16.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressDonut(
                        progress = 0.5f,
                        centerText = "50%",
                        caption = "Used\n(128 GB)",
                        arcColors = listOf(KnouxCyan, KnouxMagenta, KnouxAmber),
                        size = 96.dp,
                    )
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        SummaryLine(Icons.Rounded.Trophy, "Largest category:", "Apps & Games", KnouxAmber)
                        SummaryLine(Icons.Rounded.TrendingUp, "Unused space trend:", "+12.6 GB", KnouxCyan)
                        SummaryLine(Icons.Rounded.Event, "Last updated:", Fixtures.LAST_RUN, KnouxViolet)
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
            Text(
                "Storage Categories",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = KnouxText),
                modifier = Modifier.fillMaxWidth()
            )
        }

        items(Fixtures.storageBreakdown.chunked(2)) { pair ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                pair.forEach { row ->
                    Column(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(KnouxPanelAlt.copy(alpha = 0.92f), KnouxPanelDeep.copy(alpha = 0.92f))
                                )
                            )
                            .border(1.dp, KnouxViolet.copy(alpha = 0.28f), RoundedCornerShape(20.dp))
                            .clickable { }
                            .padding(12.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            NeonIconTile(iconFor(row.icon), size = 34.dp, iconSize = 17.dp, tint = KnouxCyan)
                            Spacer(Modifier.weight(1f))
                            Icon(
                                Icons.Rounded.ChevronRight, null,
                                tint = KnouxSoftText.copy(alpha = 0.7f), modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            row.title,
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KnouxText)
                        )
                        Text(
                            row.badge,
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp, color = KnouxMagenta)
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            row.subtitle,
                            style = TextStyle(fontSize = 9.5.sp, lineHeight = 13.sp, color = KnouxSoftText),
                            maxLines = 3,
                        )
                    }
                }
                if (pair.size == 1) Box(Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}
