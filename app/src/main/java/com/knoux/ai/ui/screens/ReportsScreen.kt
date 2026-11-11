package com.knoux.ai.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.TrendingUp
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
import com.knoux.ai.ui.components.FeatureRow
import com.knoux.ai.ui.components.GlassPanel
import com.knoux.ai.ui.components.GradientActionButton
import com.knoux.ai.ui.components.GhostButton
import com.knoux.ai.ui.components.HeroKind
import com.knoux.ai.ui.components.MiniStatStrip
import com.knoux.ai.ui.components.NeonIconTile
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
fun ReportsScreen(onBack: () -> Unit, onOpenDetail: () -> Unit) {
    ToolScreenScaffold(
        hero = HeroKind.Bars,
        title = "Reports & ",
        accent = "Insights",
        subtitle = "Track cleanups, storage gains, optimization history, and device health trends.",
        rightIcon = Icons.Rounded.BarChart,
        onBack = onBack,
        bottomActions = {
            Box(Modifier.weight(1f)) { GhostButton("View History", onOpenDetail) }
            Box(Modifier.weight(1f)) { GradientActionButton("Generate Report", onOpenDetail, Modifier.fillMaxWidth()) }
        },
    ) {
        item {
            GlassPanel(Modifier.fillMaxWidth(), padding = 16.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NeonIconTile(Icons.Rounded.BarChart, size = 42.dp, iconSize = 21.dp, tint = KnouxMagenta)
                    Spacer(Modifier.width(11.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "12.6 GB",
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 23.sp, color = KnouxText)
                        )
                        Text(
                            "total saved",
                            style = TextStyle(fontSize = 11.sp, color = KnouxSoftText)
                        )
                    }
                    Box(
                        Modifier
                            .width(1.dp)
                            .height(38.dp)
                            .background(Color.White.copy(alpha = 0.15f))
                    )
                    Spacer(Modifier.width(14.dp))
                    NeonIconTile(Icons.Rounded.Groups, size = 42.dp, iconSize = 21.dp, tint = KnouxViolet)
                    Spacer(Modifier.width(11.dp))
                    Column {
                        Text(
                            "31",
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 23.sp, color = KnouxText)
                        )
                        Text(
                            "cleanups completed",
                            style = TextStyle(fontSize = 11.sp, color = KnouxSoftText)
                        )
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
            GlassPanel(Modifier.fillMaxWidth(), padding = 12.dp) {
                MiniStatStrip(Fixtures.reportMetrics)
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
            GlassPanel(Modifier.fillMaxWidth(), padding = 16.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ProgressDonut(
                            progress = 0.92f,
                            centerText = "92%",
                            arcColors = listOf(KnouxCyan, KnouxMagenta),
                            size = 96.dp,
                        )
                        Spacer(Modifier.height(6.dp))
                        Box(
                            Modifier
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(50))
                                .background(Brush.horizontalGradient(listOf(KnouxCyan, Color(0xFF48A8FF))))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                "▲ +6%",
                                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(0xFF032033))
                            )
                        }
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        SummaryLine(Icons.Rounded.EmojiEvents, "Best improvement:", "Storage Cleanup", KnouxAmber)
                        SummaryLine(Icons.Rounded.TrendingUp, "Current device score:", "92%  ▲ +6%", KnouxCyan)
                        SummaryLine(Icons.Rounded.Event, "Last report:", Fixtures.LAST_RUN, KnouxViolet)
                    }
                }
            }
        }

        items(Fixtures.reportRows.size) { index ->
            Spacer(Modifier.height(8.dp))
            FeatureRow(row = Fixtures.reportRows[index], onClick = onOpenDetail)
        }
    }
}
