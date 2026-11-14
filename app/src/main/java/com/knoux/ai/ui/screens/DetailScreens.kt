package com.knoux.ai.ui.screens

import com.knoux.ai.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.knoux.ai.ui.components.DetailScaffold
import com.knoux.ai.ui.components.GhostButton
import com.knoux.ai.ui.components.GlassPanel
import com.knoux.ai.ui.components.GradientActionButton
import com.knoux.ai.ui.components.HeroKind
import com.knoux.ai.ui.components.InsightBanner
import com.knoux.ai.ui.components.ReferenceSectionLabel
import com.knoux.ai.ui.components.SignalTile
import com.knoux.ai.ui.components.SignalTileGrid
import com.knoux.ai.ui.components.MiniStatStrip
import com.knoux.ai.ui.components.NeonIconTile
import com.knoux.ai.ui.components.PreviewDisclosure
import com.knoux.ai.ui.components.ProgressDonut
import com.knoux.ai.ui.components.SelectableFindingRow
import com.knoux.ai.ui.components.SelectAllPill
import com.knoux.ai.ui.components.SummaryLine
import com.knoux.ai.ui.components.iconFor
import com.knoux.ai.ui.data.Fixtures
import com.knoux.ai.ui.data.Key
import com.knoux.ai.ui.data.MiniStat
import com.knoux.ai.ui.data.PreviewRow
import com.knoux.ai.ui.theme.KnouxAmber
import com.knoux.ai.ui.theme.KnouxCyan
import com.knoux.ai.ui.theme.KnouxMagenta
import com.knoux.ai.ui.theme.KnouxPanelAlt
import com.knoux.ai.ui.theme.KnouxPanelDeep
import com.knoux.ai.ui.theme.KnouxSoftText
import com.knoux.ai.ui.theme.KnouxText
import com.knoux.ai.ui.theme.KnouxViolet
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape

/* ------------------------------------------------------------------ *
 * Shared detail building blocks (screens 11-20)
 * ------------------------------------------------------------------ */

@Composable
fun DetailSectionTitle(text: String, caption: String? = null) {
    Column(Modifier.fillMaxWidth().padding(top = 4.dp)) {
        Text(text, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = KnouxText))
        if (caption != null) {
            Text(caption, style = TextStyle(fontSize = 10.5.sp, color = KnouxSoftText))
        }
    }
}

@Composable
fun NoticePanel(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, tint: Color = KnouxCyan) {
    GlassPanel(Modifier.fillMaxWidth().padding(top = 8.dp), padding = 12.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            NeonIconTile(icon, size = 32.dp, iconSize = 16.dp, tint = tint)
            Spacer(Modifier.width(10.dp))
            Text(text, style = TextStyle(fontSize = 10.5.sp, lineHeight = 15.sp, color = KnouxSoftText))
        }
    }
}

@Composable
fun DetailRowCard(
    row: PreviewRow,
    checked: Boolean? = null,
    trailing: String? = null,
    onClick: (() -> Unit)? = null,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.horizontalGradient(listOf(KnouxPanelAlt.copy(alpha = 0.92f), KnouxPanelDeep.copy(alpha = 0.88f))))
            .border(1.dp, KnouxViolet.copy(alpha = 0.26f), RoundedCornerShape(16.dp))
            .let { if (onClick != null) it.clickable { onClick() } else it }
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NeonIconTile(iconFor(row.icon), size = 34.dp, iconSize = 17.dp)
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(row.title, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KnouxText))
            Text(row.subtitle, style = TextStyle(fontSize = 10.sp, lineHeight = 14.sp, color = KnouxSoftText), maxLines = 2)
        }
        if (trailing != null) {
            Text(trailing, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = KnouxCyan))
        }
        if (checked != null) {
            Icon(
                if (checked) Icons.Rounded.Check else Icons.Rounded.ChevronRight,
                null,
                tint = if (checked) KnouxCyan else KnouxSoftText,
                modifier = Modifier.size(17.dp),
            )
        }
    }
}

@Composable
fun PreviewActionDialog(title: String, message: String, confirm: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, color = KnouxText, fontWeight = FontWeight.Bold) },
        text = { Text(message, color = KnouxSoftText, fontSize = 13.sp, lineHeight = 19.sp) },
        confirmButton = { TextButton(onClick = onDismiss) { Text(confirm, color = KnouxCyan) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Close", color = KnouxSoftText) } },
        containerColor = KnouxPanelAlt,
        titleContentColor = KnouxText,
        textContentColor = KnouxSoftText,
    )
}

/* ------------------------------------------------------------------ *
 * 11 — Smart Clean Center
 * ------------------------------------------------------------------ */
@Composable
fun LegacySmartCleanCenterScreen(onBack: () -> Unit, onOpenDeepScan: () -> Unit) {
    val groups = listOf(
        PreviewRow("Temporary Files", "Cached and unnecessary files, review before removal.", "2.4 GB", Key.Folder),
        PreviewRow("Duplicate Media", "Similar photos, repeated videos and copy documents.", "1,248 items", Key.Image),
        PreviewRow("App Residue", "Leftovers from uninstalled or reset applications.", "836 MB", Key.Storage),
        PreviewRow("Privacy Risks", "Trackers and risky permission combinations to review.", "7 findings", Key.Shield),
        PreviewRow("Large & Old Files", "Files above 100 MB or untouched for a year.", "4.1 GB", Key.Document),
    )
    var selected by remember { mutableStateOf(setOf(0, 1, 2)) }
    var dialog by remember { mutableStateOf(false) }
    val all = selected.size == groups.size
    DetailScaffold(
        hero = HeroKind.Radar,
        title = "Smart Clean ",
        accent = "Center",
        subtitle = "One place to review every cleanable category before you confirm.",
        onBack = onBack,
        actions = {
            Box(Modifier.weight(1f)) { GhostButton("Deep Scan", onOpenDeepScan, Modifier.fillMaxWidth()) }
            Box(Modifier.weight(1f)) { GradientActionButton("Preview Cleanup", { dialog = true }, Modifier.fillMaxWidth()) }
        },
    ) {
        item { PreviewDisclosure() }
        item { Spacer(Modifier.height(6.dp)) }
        item {
            GlassPanel(Modifier.fillMaxWidth(), padding = 14.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressDonut(progress = 0.62f, centerText = "6.8 GB", caption = "selected\n(preview)", size = 92.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        SummaryLine(Icons.Rounded.Folder, "Selected categories:", "${selected.size} of ${groups.size}")
                        SummaryLine(Icons.Rounded.Shield, "Safety:", "Preview only", KnouxCyan)
                        SummaryLine(Icons.Rounded.Event, "Last scan:", "Not measured yet", KnouxViolet)
                    }
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth().padding(top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                DetailSectionTitle("Cleanable Categories", "Sample estimates from the design reference")
                Spacer(Modifier.weight(1f))
                SelectAllPill("All", all) {
                    selected = if (all) emptySet() else groups.indices.toSet()
                }
            }
        }
        items(groups.size) { index ->
            Box(Modifier.padding(top = 8.dp)) {
                SelectableFindingRow(groups[index], selected.contains(index)) {
                    selected = if (selected.contains(index)) selected - index else selected + index
                }
            }
        }
        item { NoticePanel(Icons.Rounded.Lock, "Nothing is deleted on this screen. A real cleanup will require Android storage consent plus an explicit confirmation listing exact files, sizes and preserved copies.") }
    }
    if (dialog) {
        PreviewActionDialog(
            title = "Cleanup preview",
            message = "This is a visual preview of the confirmation step. No files will be removed until a real scan measures them and Android storage access is granted. Categories selected: ${selected.size}.",
            confirm = "Understood",
            onDismiss = { dialog = false },
        )
    }
}

/* ------------------------------------------------------------------ *
 * 12 — Duplicate Group Review
 * ------------------------------------------------------------------ */
@Composable
fun DuplicateGroupReviewScreen(onBack: () -> Unit) {
    var selected by remember { mutableStateOf(setOf<Int>()) }
    var dialog by remember { mutableStateOf(false) }
    val groups = Fixtures.duplicateGroups
    DetailScaffold(
        hero = HeroKind.Images,
        title = "Duplicate ",
        accent = "Group Review",
        subtitle = "Every group keeps one protected original. Review before removing anything.",
        onBack = onBack,
        heroImage = R.drawable.knoux_hero_duplicate_group,
        actions = {
            Box(Modifier.weight(1f)) { GhostButton("Preview Selection", { dialog = true }, Modifier.fillMaxWidth()) }
            Box(Modifier.weight(1f)) { GradientActionButton("Review & Remove", { dialog = true }, Modifier.fillMaxWidth()) }
        },
    ) {
        item { PreviewDisclosure() }

        item {
            ReferenceSectionLabel("Group summary", "preview values")
            Spacer(Modifier.height(6.dp))
            SignalTileGrid(
                tiles = listOf(
                    SignalTile(Icons.Rounded.Folder, "24", "Duplicate groups", KnouxMagenta),
                    SignalTile(Icons.Rounded.Storage, "3.8 GB", "Reclaimable", KnouxCyan),
                    SignalTile(Icons.Rounded.Image, "1.2 GB", "Largest group", KnouxViolet),
                    SignalTile(Icons.Rounded.Shield, "92%", "Media coverage", KnouxAmber),
                ),
            )
        }
        item { NoticePanel(Icons.Rounded.Lock, "Keep-one invariant: the canonical original of each group is always preserved, even when every other copy is selected.", KnouxCyan) }
        item { MiniStatStrip(Fixtures.duplicateStats) }
        item { DetailSectionTitle("Duplicate Groups", "${groups.size} groups · sample values") }
        items(groups.size) { index ->
            Box(Modifier.padding(top = 8.dp)) {
                SelectableFindingRow(groups[index], selected.contains(index)) {
                    selected = if (selected.contains(index)) selected - index else selected + index
                }
            }
        }
        item { Spacer(Modifier.height(8.dp)) }
        item { NoticePanel(Icons.Rounded.Info, "Exact duplicates require hash + size verification. Similar photos are marked as similar, never as guaranteed duplicates. Thumbnails appear only from media the app is allowed to read.", KnouxViolet) }
    }
    if (dialog) {
        PreviewActionDialog(
            title = "Removal preview",
            message = "No deletion is wired in this visual milestone. A real flow will verify hashes, keep one original per group, show exact paths and sizes, then require confirmation and offer recoverable removal where the platform allows it.",
            confirm = "Close",
            onDismiss = { dialog = false },
        )
    }
}

/* ------------------------------------------------------------------ *
 * 13 — Privacy Risk Inspector
 * ------------------------------------------------------------------ */
@Composable
fun PrivacyRiskInspectorScreen(onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val rows = listOf(
        Triple("Photos & Camera app", "Camera · Photos · Microphone", "High visibility"),
        Triple("Shopping app", "Background location · Advertising ID", "Review suggested"),
        Triple("Messaging app", "Notifications · Contacts · Microphone", "Expected for feature"),
        Triple("Utility scanner", "All files access · Clipboard", "Review suggested"),
        Triple("Streaming app", "Storage · Notifications", "Low visibility"),
    )
    val stats = listOf(
        MiniStat(Key.Shield, "2 apps", "Camera access"),
        MiniStat(Key.Audio, "1 app", "Microphone"),
        MiniStat(Key.Bolt, "7", "Ad SDK flags"),
        MiniStat(Key.Document, "2 apps", "Clipboard"),
    )
    DetailScaffold(
        hero = HeroKind.Shield,
        title = "Privacy Risk ",
        accent = "Inspector",
        subtitle = "Per-app permission evidence with direct Android settings handoff.",
        onBack = onBack,
        heroImage = R.drawable.knoux_hero_privacy_risk,
        actions = {
            Box(Modifier.weight(1f)) {
                GhostButton("App Settings", {
                    runCatching { context.startActivity(android.content.Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)) }
                }, Modifier.fillMaxWidth())
            }
            Box(Modifier.weight(1f)) { GradientActionButton("Open Privacy Settings", {
                runCatching { context.startActivity(android.content.Intent(android.provider.Settings.ACTION_PRIVACY_SETTINGS)) }
            }, Modifier.fillMaxWidth()) }
        },
    ) {
        item { PreviewDisclosure() }

        item {
            ReferenceSectionLabel("Risk overview", "preview values")
            Spacer(Modifier.height(6.dp))
            SignalTileGrid(
                tiles = listOf(
                    SignalTile(Icons.Rounded.Shield, "5", "High-risk apps", KnouxMagenta),
                    SignalTile(Icons.Rounded.CameraAlt, "2", "Camera access", KnouxAmber),
                    SignalTile(Icons.Rounded.Mic, "1", "Microphone", KnouxAmber),
                    SignalTile(Icons.Rounded.LocationOn, "3", "Background location", KnouxCyan),
                ),
            )
        }
        item { MiniStatStrip(stats) }
        item { NoticePanel(Icons.Rounded.Shield, "Android does not let a normal Play app block another app's trackers or revoke its permissions silently. These findings are evidence-based permission signals, not proof of malicious behaviour.", KnouxAmber) }
        item { DetailSectionTitle("App Permission Evidence", "Sample inventory · tap a row for details") }
        items(rows.size) { index ->
            val (app, perms, risk) = rows[index]
            Box(Modifier.padding(top = 8.dp)) {
                GlassPanel(Modifier.fillMaxWidth(), padding = 12.dp) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        NeonIconTile(Icons.Rounded.Shield, size = 34.dp, iconSize = 17.dp, tint = if (index < 2) KnouxMagenta else KnouxCyan)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(app, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KnouxText))
                            Text(perms, style = TextStyle(fontSize = 10.sp, color = KnouxSoftText))
                            Text(risk, style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (index < 2) KnouxMagenta else KnouxCyan))
                        }
                        Icon(Icons.Rounded.ChevronRight, null, tint = KnouxSoftText, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
        item { NoticePanel(Icons.Rounded.Info, "Risk labels explain permission combinations only. A granted permission is not evidence of tracking; SDK detection requires actual on-device analysis.", KnouxViolet) }
    }
}

/* ------------------------------------------------------------------ *
 * 14 — Deep Scan Summary
 * ------------------------------------------------------------------ */
@Composable
fun DeepScanSummaryScreen(onBack: () -> Unit, onOpenSmartClean: () -> Unit) {
    val modules = listOf(
        PreviewRow("Storage", "Temporary files and reclaimable caches.", "2.4 GB", Key.Storage),
        PreviewRow("Media", "Similar photos, repeated videos and copies.", "1,248 items", Key.Image),
        PreviewRow("Privacy", "Trackers, permission signals and exposure.", "7 alerts", Key.Shield),
        PreviewRow("Apps", "App health and resource signals.", "95% score", Key.Chip),
        PreviewRow("Battery", "Supported battery and usage signals.", "5 sources", Key.Bolt),
    )
    DetailScaffold(
        hero = HeroKind.Radar,
        title = "Deep Scan ",
        accent = "Summary",
        subtitle = "What a completed scan reports, and which next actions are supported.",
        onBack = onBack,
        heroImage = R.drawable.knoux_hero_scan_summary,
        actions = {
            Box(Modifier.weight(1f)) { GhostButton("Back to Scan", onBack, Modifier.fillMaxWidth()) }
            Box(Modifier.weight(1f)) { GradientActionButton("Open Smart Clean", onOpenSmartClean, Modifier.fillMaxWidth()) }
        },
    ) {
        item { PreviewDisclosure() }

        item {
            ReferenceSectionLabel("Scan summary", "preview values")
            Spacer(Modifier.height(6.dp))
            SignalTileGrid(
                tiles = listOf(
                    SignalTile(Icons.Rounded.Storage, "2.4 GB", "Junk found", KnouxMagenta),
                    SignalTile(Icons.Rounded.Shield, "7", "Privacy alerts", KnouxCyan),
                    SignalTile(Icons.Rounded.Folder, "1,248", "Duplicates", KnouxViolet),
                    SignalTile(Icons.Rounded.Bolt, "5", "Battery sources", KnouxAmber),
                ),
            )
        }
        item { MiniStatStrip(Fixtures.deepScanMetrics) }
        item {
            GlassPanel(Modifier.fillMaxWidth().padding(top = 10.dp), padding = 14.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressDonut(progress = 1f, centerText = "Done", caption = "design preview", size = 88.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        SummaryLine(Icons.Rounded.Event, "Finished:", "Preview · no engine run", KnouxCyan)
                        SummaryLine(Icons.Rounded.Storage, "Modules:", "5 scanned (preview)", KnouxViolet)
                        SummaryLine(Icons.Rounded.Shield, "Result:", "Review required", KnouxMagenta)
                    }
                }
            }
        }
        item { DetailSectionTitle("Module Findings", "Sample values — not device telemetry") }
        items(modules.size) { index ->
            Box(Modifier.padding(top = 8.dp)) {
                DetailRowCard(modules[index], trailing = modules[index].badge)
            }
        }
        item { NoticePanel(Icons.Rounded.Info, "The real scan engine is not connected in this UI milestone. Progress, counts and completion shown anywhere in the app are labelled preview until a worker measures them.", KnouxCyan) }
    }
}

/* ------------------------------------------------------------------ *
 * 15 — Performance Report Detail
 * ------------------------------------------------------------------ */
@Composable
fun PerformanceReportDetailScreen(onBack: () -> Unit) {
    var dialog by remember { mutableStateOf(false) }
    val days = listOf(0.55f, 0.68f, 0.62f, 0.74f, 0.71f, 0.86f, 0.92f)
    DetailScaffold(
        hero = HeroKind.Chip,
        title = "Performance ",
        accent = "Report Detail",
        subtitle = "History, chart units and export status from stored records only.",
        onBack = onBack,
        heroImage = R.drawable.knoux_hero_performance,
        actions = {
            Box(Modifier.weight(1f)) { GhostButton("View History", { dialog = true }, Modifier.fillMaxWidth()) }
            Box(Modifier.weight(1f)) { GradientActionButton("Export PDF", { dialog = true }, Modifier.fillMaxWidth()) }
        },
    ) {
        item { PreviewDisclosure() }

        item {
            ReferenceSectionLabel("Report summary", "preview values")
            Spacer(Modifier.height(6.dp))
            SignalTileGrid(
                tiles = listOf(
                    SignalTile(Icons.Rounded.TrendingUp, "92%", "Device score", KnouxCyan, trailing = "+6"),
                    SignalTile(Icons.Rounded.Storage, "12.6 GB", "Space saved", KnouxMagenta),
                    SignalTile(Icons.Rounded.BarChart, "7", "Reports stored", KnouxViolet),
                    SignalTile(Icons.Rounded.EmojiEvents, "Storage", "Best run", KnouxAmber),
                ),
            )
        }
        item {
            GlassPanel(Modifier.fillMaxWidth().padding(top = 8.dp), padding = 14.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressDonut(progress = 0.92f, centerText = "92%", caption = "device score", size = 94.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SummaryLine(Icons.Rounded.EmojiEvents, "Best improvement:", "Storage Cleanup", KnouxAmber)
                        SummaryLine(Icons.Rounded.TrendingUp, "Weekly change:", "▲ +6% (sample)", KnouxCyan)
                        SummaryLine(Icons.Rounded.Event, "Last report:", Fixtures.LAST_RUN, KnouxViolet)
                    }
                }
            }
        }
        item { DetailSectionTitle("Seven-Day Score", "Illustrative chart · no measurement records yet") }
        item {
            GlassPanel(Modifier.fillMaxWidth().padding(top = 8.dp), padding = 14.dp) {
                Row(
                    Modifier.fillMaxWidth().height(96.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    days.forEachIndexed { index, value ->
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height((72 * value).dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Brush.verticalGradient(listOf(KnouxCyan, KnouxViolet))),
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                listOf("M", "T", "W", "T", "F", "S", "S")[index],
                                style = TextStyle(fontSize = 8.5.sp, color = KnouxSoftText),
                            )
                        }
                    }
                }
            }
        }
        item { DetailSectionTitle("Report History", "Records appear here after real sessions") }
        items(Fixtures.reportRows.size) { index ->
            Box(Modifier.padding(top = 8.dp)) {
                DetailRowCard(Fixtures.reportRows[index], trailing = Fixtures.reportRows[index].badge)
            }
        }
        item { NoticePanel(Icons.Rounded.Info, "The chart is an illustrative preview, not stored telemetry. Export stays unavailable until report records exist; the button explains that instead of pretending to generate a PDF.", KnouxAmber) }
    }
    if (dialog) {
        PreviewActionDialog(
            title = "Report export",
            message = "No real report records exist yet in this build, so there is nothing truthful to export. The export flow will generate a PDF from stored events and share it through FileProvider once measurement is implemented.",
            confirm = "Understood",
            onDismiss = { dialog = false },
        )
    }
}
