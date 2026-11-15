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
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.knoux.ai.ui.theme.ProWarning
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProTextSecondary
import com.knoux.ai.ui.theme.ProTextPrimary
import com.knoux.ai.ui.theme.ProBlue

@Composable
fun AppManagerScreen(onBack: () -> Unit, onOpenDetail: () -> Unit) {
    ToolScreenScaffold(
        hero = HeroKind.Grid,
        title = "App ",
        accent = "Manager",
        subtitle = "Review installed apps, space use, startup impact, and update status.",
        rightIcon = Icons.Rounded.Apps,
        onBack = onBack,
        bottomActions = {
            Box(Modifier.weight(1f)) { GhostButton("Review Apps", onOpenDetail) }
            Box(Modifier.weight(1f)) { GradientActionButton("Manage Apps", onOpenDetail, Modifier.fillMaxWidth()) }
        },
    ) {
        item {
            GlassPanel(Modifier.fillMaxWidth(), padding = 16.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NeonIconTile(Icons.Rounded.Layers, size = 42.dp, iconSize = 21.dp, tint = ProArcticCyan)
                    Spacer(Modifier.width(11.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "143",
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 23.sp, color = ProTextPrimary)
                        )
                        Text(
                            "installed apps",
                            style = TextStyle(fontSize = 11.sp, color = ProTextSecondary)
                        )
                    }
                    Box(
                        Modifier
                            .width(1.dp)
                            .height(38.dp)
                            .background(Color.White.copy(alpha = 0.15f))
                    )
                    Spacer(Modifier.width(14.dp))
                    NeonIconTile(Icons.Rounded.Storage, size = 42.dp, iconSize = 21.dp, tint = ProArcticCyan)
                    Spacer(Modifier.width(11.dp))
                    Column {
                        Text(
                            "18.4 GB",
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 23.sp, color = ProTextPrimary)
                        )
                        Text(
                            "app storage",
                            style = TextStyle(fontSize = 11.sp, color = ProTextSecondary)
                        )
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
            GlassPanel(Modifier.fillMaxWidth(), padding = 12.dp) {
                MiniStatStrip(Fixtures.appManagerMetrics)
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
            GlassPanel(Modifier.fillMaxWidth(), padding = 16.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressDonut(
                        progress = 0.87f,
                        centerText = "87%",
                        caption = "App\nHealth",
                        arcColors = listOf(ProArcticCyan, ProArcticCyan),
                        size = 92.dp,
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        SummaryLine(Icons.Rounded.WarningAmber, "Apps to review:", "27", ProWarning)
                        SummaryLine(Icons.Rounded.CheckCircle, "Safe suggestions:", "18", ProArcticCyan)
                        SummaryLine(Icons.Rounded.Event, "Last checked:", Fixtures.LAST_RUN, ProBlue)
                    }
                }
            }
        }

        items(Fixtures.appCategories.size) { index ->
            Spacer(Modifier.height(8.dp))
            FeatureRow(
                row = Fixtures.appCategories[index],
                actionLabel = when (index) {
                    0 -> "Optimize"; 1 -> "Review"; 2 -> "Manage"; 3 -> "Clean"; else -> "Open"
                },
                onAction = onOpenDetail,
                onClick = onOpenDetail,
            )
        }
    }
}
