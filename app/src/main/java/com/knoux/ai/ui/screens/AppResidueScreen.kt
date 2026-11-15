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
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.knoux.ai.ui.components.SelectAllPill
import com.knoux.ai.ui.components.SelectableFindingRow
import com.knoux.ai.ui.components.SummaryLine
import com.knoux.ai.ui.data.Fixtures
import com.knoux.ai.ui.theme.ProWarning
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProTextSecondary
import com.knoux.ai.ui.theme.ProTextPrimary
import com.knoux.ai.ui.theme.ProBlue

@Composable
fun AppResidueScreen(onBack: () -> Unit, onOpenDetail: () -> Unit) {
    val selected = remember { mutableStateListOf<Int>() }
    val allSelected = selected.size == Fixtures.residueItems.size

    ToolScreenScaffold(
        hero = HeroKind.Layers,
        title = "App Residue ",
        accent = "Cleanup",
        subtitle = "Find leftover folders, empty data, and remnants from removed apps.",
        rightIcon = Icons.Rounded.Layers,
        onBack = onBack,
        heroImage = R.drawable.knoux_hero_residue,
        bottomActions = {
            Box(Modifier.weight(1f)) { GhostButton("Preview Residue", onOpenDetail) }
            Box(Modifier.weight(1f)) { GradientActionButton("Clean Remnants", onOpenDetail, Modifier.fillMaxWidth()) }
        },
    ) {
        item {
            GlassPanel(Modifier.fillMaxWidth(), padding = 16.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NeonIconTile(Icons.Rounded.Layers, size = 42.dp, iconSize = 21.dp, tint = ProArcticCyan)
                    Spacer(Modifier.width(11.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "836 MB",
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 23.sp, color = ProTextPrimary)
                        )
                        Text(
                            "residual data",
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
                    NeonIconTile(Icons.Rounded.Folder, size = 42.dp, iconSize = 21.dp, tint = ProArcticCyan)
                    Spacer(Modifier.width(11.dp))
                    Column {
                        Text(
                            "42",
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 23.sp, color = ProTextPrimary)
                        )
                        Text(
                            "orphan folders",
                            style = TextStyle(fontSize = 11.sp, color = ProTextSecondary)
                        )
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
            GlassPanel(Modifier.fillMaxWidth(), padding = 12.dp) {
                MiniStatStrip(Fixtures.residueMetrics)
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
            GlassPanel(Modifier.fillMaxWidth(), padding = 16.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressDonut(
                        progress = 0.91f,
                        centerText = "91%",
                        caption = "Cleanup\nSafe",
                        arcColors = listOf(ProArcticCyan, ProArcticCyan),
                        size = 92.dp,
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        SummaryLine(Icons.Rounded.CheckCircle, "Safe to clean:", "836 MB", ProArcticCyan)
                        SummaryLine(Icons.Rounded.WarningAmber, "Needs review:", "28 MB", ProWarning)
                        SummaryLine(Icons.Rounded.Event, "Last scan:", Fixtures.LAST_RUN, ProBlue)
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Items Found (5)",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ProTextPrimary)
                )
                Spacer(Modifier.weight(1f))
                SelectAllPill("Select All (836 MB)", allSelected) {
                    if (allSelected) selected.clear() else selected.addAll(Fixtures.residueItems.indices)
                }
            }
        }

        items(Fixtures.residueItems.size) { index ->
            Spacer(Modifier.height(8.dp))
            SelectableFindingRow(Fixtures.residueItems[index], selected.contains(index)) {
                if (selected.contains(index)) selected.remove(index) else selected.add(index)
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
            InsightBanner(
                title = "Residue Insight",
                message = "14 folders and 6 empty data stores were left behind by apps you already removed. Review them before anything is removed.",
                tint = ProArcticCyan,
                badge = "1.9 GB",
            )
        }

        item {
            Spacer(Modifier.height(12.dp))
            ReferenceSectionLabel("Residue by type", "preview values")
            Spacer(Modifier.height(6.dp))
            SignalTileGrid(
                tiles = listOf(
                    SignalTile(Icons.Rounded.Folder, "14", "Empty folders", ProBlue),
                    SignalTile(Icons.Rounded.Delete, "6", "Empty data stores", ProArcticCyan),
                    SignalTile(Icons.Rounded.Android, "3", "Old APKs", ProArcticCyan),
                    SignalTile(Icons.Rounded.Restore, "1.9 GB", "Reclaimable", ProWarning),
                ),
            )
        }

    }
}
