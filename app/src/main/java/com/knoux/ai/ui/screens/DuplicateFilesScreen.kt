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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.ui.components.FeatureRow
import com.knoux.ai.ui.components.GlassPanel
import com.knoux.ai.ui.components.GradientActionButton
import com.knoux.ai.ui.components.GhostButton
import com.knoux.ai.ui.components.HeroKind
import com.knoux.ai.ui.components.InsightBanner
import com.knoux.ai.ui.components.ReferenceSectionLabel
import com.knoux.ai.ui.components.SignalTile
import com.knoux.ai.ui.components.SignalTileGrid
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
fun DuplicateFilesScreen(onBack: () -> Unit, onOpenDetail: () -> Unit) {
    ToolScreenScaffold(
        hero = HeroKind.Images,
        title = "Duplicate Files ",
        accent = "Finder",
        subtitle = "Detect and review duplicate photos, videos, audio, and documents.",
        rightIcon = Icons.Rounded.Groups,
        onBack = onBack,
        heroImage = R.drawable.knoux_hero_duplicates,
        bottomActions = {
            Box(Modifier.weight(1f)) { GhostButton("Preview Groups", onOpenDetail) }
            Box(Modifier.weight(1f)) { GradientActionButton("Review & Remove", onOpenDetail, Modifier.fillMaxWidth()) }
        },
    ) {
        item {
            GlassPanel(Modifier.fillMaxWidth(), padding = 16.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NeonIconTile(Icons.Rounded.ChevronRight, size = 42.dp, iconSize = 21.dp, tint = KnouxMagenta)
                    Spacer(Modifier.width(11.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "1,248",
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp, color = KnouxText)
                        )
                        Text(
                            "duplicates found",
                            style = TextStyle(fontSize = 11.sp, color = KnouxSoftText)
                        )
                    }
                    Box(
                        Modifier
                            .width(1.dp)
                            .height(38.dp)
                            .background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.15f))
                    )
                    Spacer(Modifier.width(14.dp))
                    NeonIconTile(Icons.Rounded.Layers, size = 42.dp, iconSize = 21.dp, tint = KnouxViolet)
                    Spacer(Modifier.width(11.dp))
                    Column {
                        Text(
                            "8.7 GB",
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp, color = KnouxText)
                        )
                        Text(
                            "recoverable",
                            style = TextStyle(fontSize = 11.sp, color = KnouxSoftText)
                        )
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
            GlassPanel(Modifier.fillMaxWidth(), padding = 12.dp) {
                MiniStatStrip(Fixtures.duplicateStats)
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
            GlassPanel(Modifier.fillMaxWidth(), padding = 16.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressDonut(
                        progress = 0.96f,
                        centerText = "96%",
                        caption = "Match\nConfidence",
                        arcColors = listOf(KnouxCyan, KnouxMagenta),
                        size = 92.dp,
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        SummaryLine(Icons.Rounded.Groups, "Auto-grouped items:", "218 groups", KnouxCyan)
                        SummaryLine(Icons.Rounded.WarningAmber, "Needs review:", "37 items", KnouxAmber)
                        SummaryLine(Icons.Rounded.Event, "Last scan:", Fixtures.LAST_RUN, KnouxViolet)
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
            Text(
                "Duplicate Groups (5)",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = KnouxText),
                modifier = Modifier.fillMaxWidth()
            )
        }

        items(Fixtures.duplicateGroups.size) { index ->
            Spacer(Modifier.height(8.dp))
            FeatureRow(
                row = Fixtures.duplicateGroups[index],
                onClick = onOpenDetail,
            )
        }

        item {
            Spacer(Modifier.height(12.dp))
            InsightBanner(
                title = "Duplicate Insight",
                message = "1,248 copies were hashed across photos, video, audio and documents. Every group keeps one protected original.",
                tint = KnouxCyan,
                badge = "1,248 FOUND",
            )
        }

        item {
            Spacer(Modifier.height(12.dp))
            ReferenceSectionLabel("Duplicates by type", "preview values")
            Spacer(Modifier.height(6.dp))
            SignalTileGrid(
                tiles = listOf(
                    SignalTile(Icons.Rounded.Image, "1,248", "Photos", KnouxCyan),
                    SignalTile(Icons.Rounded.Movie, "86", "Videos", KnouxMagenta),
                    SignalTile(Icons.Rounded.MusicNote, "12", "Audio files", KnouxViolet),
                    SignalTile(Icons.Rounded.Description, "34", "Documents", KnouxAmber),
                ),
            )
        }

    }
}
