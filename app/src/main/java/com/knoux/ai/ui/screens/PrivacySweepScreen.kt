package com.knoux.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
import com.knoux.ai.ui.components.SelectAllPill
import com.knoux.ai.ui.components.SelectableFindingRow
import com.knoux.ai.ui.components.SummaryLine
import com.knoux.ai.ui.data.Fixtures
import com.knoux.ai.ui.theme.KnouxAmber
import com.knoux.ai.ui.theme.KnouxCyan
import com.knoux.ai.ui.theme.KnouxMagenta
import com.knoux.ai.ui.theme.KnouxSoftText
import com.knoux.ai.ui.theme.KnouxText
import com.knoux.ai.ui.theme.KnouxViolet

@Composable
fun PrivacySweepScreen(onBack: () -> Unit, onOpenDetail: () -> Unit) {
    val selected = remember { mutableStateListOf<Int>() }
    val allSelected = selected.size == Fixtures.privacyItems.size

    ToolScreenScaffold(
        hero = HeroKind.Shield,
        title = "Privacy ",
        accent = "Sweep",
        subtitle = "Review trackers, risky permissions, and privacy exposure indicators.",
        rightIcon = Icons.Rounded.Shield,
        onBack = onBack,
        bottomActions = {
            Box(Modifier.weight(1f)) { GhostButton("Review Risks", onOpenDetail) }
            Box(Modifier.weight(1f)) { GradientActionButton("Improve Privacy", onOpenDetail, Modifier.fillMaxWidth()) }
        },
    ) {
        item {
            GlassPanel(Modifier.fillMaxWidth(), padding = 16.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NeonIconTile(Icons.Rounded.Visibility, size = 44.dp, iconSize = 22.dp, tint = KnouxCyan)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "7",
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 26.sp, color = KnouxText)
                        )
                        Text(
                            "trackers found",
                            style = TextStyle(fontSize = 11.sp, color = KnouxSoftText)
                        )
                    }
                    Column(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Brush.horizontalGradient(listOf(Color(0x3303DAFF), Color(0x1A03DAFF))))
                            .border(1.dp, KnouxCyan.copy(alpha = 0.8f), RoundedCornerShape(50))
                            .padding(horizontal = 16.dp, vertical = 9.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(Icons.Rounded.Shield, null, tint = KnouxCyan, modifier = Modifier.size(17.dp))
                        Text(
                            "86 /100",
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp, color = KnouxText)
                        )
                        Text(
                            "Privacy Score",
                            style = TextStyle(fontSize = 9.sp, color = KnouxSoftText)
                        )
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
            GlassPanel(Modifier.fillMaxWidth(), padding = 12.dp) {
                MiniStatStrip(Fixtures.privacyIndicators)
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
            GlassPanel(Modifier.fillMaxWidth(), padding = 16.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressDonut(
                        progress = 0.86f,
                        centerText = "86%",
                        caption = "Privacy\nScore",
                        arcColors = listOf(KnouxCyan, KnouxMagenta),
                        size = 92.dp,
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        SummaryLine(Icons.Rounded.CheckCircle, "Protected apps:", "24", KnouxCyan)
                        SummaryLine(Icons.Rounded.ErrorOutline, "Needs review:", "5", KnouxAmber)
                        SummaryLine(Icons.Rounded.Event, "Last sweep:", Fixtures.LAST_RUN, KnouxViolet)
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Privacy Items (5)",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = KnouxText)
                )
                Spacer(Modifier.weight(1f))
                SelectAllPill("Select All (7 Trackers)", allSelected) {
                    if (allSelected) selected.clear() else selected.addAll(Fixtures.privacyItems.indices)
                }
            }
        }

        items(Fixtures.privacyItems.size) { index ->
            val row = Fixtures.privacyItems[index]
            Spacer(Modifier.height(8.dp))
            SelectableFindingRow(row, selected.contains(index)) {
                if (selected.contains(index)) selected.remove(index) else selected.add(index)
            }
        }
    }
}
