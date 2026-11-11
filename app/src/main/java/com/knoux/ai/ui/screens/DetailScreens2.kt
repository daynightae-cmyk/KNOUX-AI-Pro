package com.knoux.ai.ui.screens

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
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
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.ui.components.DetailScaffold
import com.knoux.ai.ui.components.GhostButton
import com.knoux.ai.ui.components.GlassPanel
import com.knoux.ai.ui.components.GradientActionButton
import com.knoux.ai.ui.components.HeroKind
import com.knoux.ai.ui.components.MiniStatStrip
import com.knoux.ai.ui.components.NeonIconTile
import com.knoux.ai.ui.components.PreviewDisclosure
import com.knoux.ai.ui.components.ProgressDonut
import com.knoux.ai.ui.components.SelectableFindingRow
import com.knoux.ai.ui.components.SummaryLine
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

/* 16 — Residue Item Review */
@Composable
fun ResidueItemReviewScreen(onBack: () -> Unit) {
    var selected by remember { mutableStateOf(setOf<Int>()) }
    var dialog by remember { mutableStateOf(false) }
    val items = listOf(
        PreviewRow("WhatsApp leftover media", "content://media/external/images/media/… · 24 days old · candidate", "268 MB · review", Key.Image),
        PreviewRow("Old game cache", "Shared storage cache folder · attributable to a removed game", "184 MB · review", Key.Chip),
        PreviewRow("Empty media folders", "Zero-byte directories under the media tree", "0 MB · safe to review", Key.Folder),
        PreviewRow("Broken temporary files", "Incomplete cache entries with no owning app", "172 MB · excluded", Key.Document),
        PreviewRow("Legacy app data", "Android/data/… is not readable on Android 11+", "Not accessible", Key.Shield),
    )
    DetailScaffold(
        hero = HeroKind.Layers,
        title = "Residue Item ",
        accent = "Review",
        subtitle = "Real URI evidence, confidence, and an explicit exclusion gate before any cleanup.",
        onBack = onBack,
        actions = {
            Box(Modifier.weight(1f)) { GhostButton("Exclusions", { dialog = true }, Modifier.fillMaxWidth()) }
            Box(Modifier.weight(1f)) { GradientActionButton("Review Cleanup", { dialog = true }, Modifier.fillMaxWidth()) }
        },
    ) {
        item { PreviewDisclosure() }
        item { MiniStatStrip(Fixtures.residueMetrics) }
        item { NoticePanel(Icons.Rounded.Shield, "Unknown or inaccessible items are excluded by default. Another app's private /Android/data is never offered for deletion on modern Android.", KnouxAmber) }
        item { DetailSectionTitle("Candidate Inventory", "Sample paths · confidence must come from a real scan") }
        items(items.size) { index ->
            Box(Modifier.padding(top = 8.dp)) {
                SelectableFindingRow(items[index], selected.contains(index)) {
                    selected = if (selected.contains(index)) selected - index else selected + index
                }
            }
        }
        item { Spacer(Modifier.height(8.dp)) }
        item { NoticePanel(Icons.Rounded.Info, "Deletion stays off in this visual milestone. A real flow previews exact URIs and sizes, asks for confirmation, deletes only media the app can write, and records the measured result.", KnouxCyan) }
    }
    if (dialog) {
        PreviewActionDialog(
            title = "Cleanup review",
            message = "No file operation is wired yet. When implemented, only user-consented, accessible URIs will be deleted, with a keep-safe gate, an exact preview and a persisted audit result.",
            confirm = "Close",
            onDismiss = { dialog = false },
        )
    }
}

/* 17 — Installed Apps Review */
@Composable
fun InstalledAppsReviewScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var dialog by remember { mutableStateOf(false) }
    val apps = listOf(
        PreviewRow("Streaming apps", "Video, music and live-streaming clients.", "5.86 GB · 5 apps", Key.Video),
        PreviewRow("Social apps", "Communities, feeds and shared media.", "3.12 GB · 4 apps", Key.Share),
        PreviewRow("Game collection", "Installed games and downloaded resources.", "6.27 GB · 3 apps", Key.Chip),
        PreviewRow("Utility tools", "Productivity, file and system helpers.", "2.43 GB · 8 apps", Key.Folder),
        PreviewRow("Rarely used", "Not opened in the last 30 days (usage access needed).", "27 apps · sample", Key.Bolt),
        PreviewRow("Large apps", "500 MB or more of app + data storage.", "16 apps · sample", Key.Storage),
    )
    DetailScaffold(
        hero = HeroKind.Grid,
        title = "Installed Apps ",
        accent = "Review",
        subtitle = "Grouped inventory with honest Android handoffs for app info and uninstall.",
        onBack = onBack,
        actions = {
            Box(Modifier.weight(1f)) { GhostButton("Unused Apps", { dialog = true }, Modifier.fillMaxWidth()) }
            Box(Modifier.weight(1f)) { GradientActionButton("App Settings", {
                runCatching { context.startActivity(Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)) }
            }, Modifier.fillMaxWidth()) }
        },
    ) {
        item { PreviewDisclosure() }
        item { MiniStatStrip(Fixtures.appManagerMetrics) }
        item { NoticePanel(Icons.Rounded.Shield, "Uninstall always uses Android's standard confirmation screen. Usage-based grouping needs Usage Access; package visibility limits what a normal app can enumerate.", KnouxCyan) }
        item { DetailSectionTitle("App Groups", "Sample counts · tap App Settings for the real OS list") }
        items(apps.size) { index ->
            Box(Modifier.padding(top = 8.dp)) {
                DetailRowCard(apps[index], trailing = apps[index].badge)
            }
        }
        item { Spacer(Modifier.height(8.dp)) }
        item { NoticePanel(Icons.Rounded.Info, "Per-app uninstall and details are launched through documented Android intents after confirming the package is visible to this app.", KnouxViolet) }
    }
    if (dialog) {
        PreviewActionDialog(
            title = "Unused apps",
            message = "Detecting unused apps requires Usage Access granted in system settings. Until then, no app is labelled unused from guesswork.",
            confirm = "Understood",
            onDismiss = { dialog = false },
        )
    }
}

/* 18 — Storage Breakdown */
@Composable
fun StorageBreakdownScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var dialog by remember { mutableStateOf(false) }
    val categories = Fixtures.storageBreakdown
    val largeFiles = listOf(
        PreviewRow("VID_20260814_204512.mp4", "Movies / KNOUX capture · sample path", "486 MB", Key.Video),
        PreviewRow("IMG_20260729_113201.jpg", "DCIM / Camera · sample path", "124 MB", Key.Image),
        PreviewRow("archive_backup.zip", "Download · sample path", "312 MB", Key.Document),
    )
    DetailScaffold(
        hero = HeroKind.Disks,
        title = "Storage ",
        accent = "Breakdown",
        subtitle = "Category totals reconciled against device storage, with unclassified space shown.",
        onBack = onBack,
        actions = {
            Box(Modifier.weight(1f)) { GhostButton("Large Files", { dialog = true }, Modifier.fillMaxWidth()) }
            Box(Modifier.weight(1f)) { GradientActionButton("System Storage", {
                runCatching { context.startActivity(Intent(android.provider.Settings.ACTION_INTERNAL_STORAGE_SETTINGS)) }
            }, Modifier.fillMaxWidth()) }
        },
    ) {
        item { PreviewDisclosure() }
        item {
            GlassPanel(Modifier.fillMaxWidth(), padding = 14.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressDonut(progress = 0.5f, centerText = "50%", caption = "used · sample", size = 92.dp,
                        arcColors = listOf(KnouxCyan, KnouxMagenta, KnouxAmber))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        SummaryLine(Icons.Rounded.Storage, "Total:", "256 GB (sample)", KnouxText)
                        SummaryLine(Icons.Rounded.Folder, "Used:", "128 GB (sample)", KnouxCyan)
                        SummaryLine(Icons.Rounded.Info, "Unclassified:", "Shown, never guessed", KnouxAmber)
                    }
                }
            }
        }
        item { DetailSectionTitle("Categories", "Share of the 128 GB sample total") }
        items(categories.size) { index ->
            val row = categories[index]
            val fraction = listOf(0.38f, 0.29f, 0.05f, 0.065f, 0.032f, 0.173f)[index]
            Box(Modifier.padding(top = 8.dp)) {
                GlassPanel(Modifier.fillMaxWidth(), padding = 12.dp) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        NeonIconTile(com.knoux.ai.ui.components.iconFor(row.icon), size = 32.dp, iconSize = 16.dp)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(row.title, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = KnouxText))
                            Spacer(Modifier.height(5.dp))
                            Box(
                                Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(50))
                                    .background(KnouxPanelAlt),
                            ) {
                                Box(
                                    Modifier.fillMaxWidth(fraction).height(4.dp).clip(RoundedCornerShape(50))
                                        .background(Brush.horizontalGradient(listOf(KnouxCyan, KnouxViolet))),
                                )
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(row.badge, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = KnouxCyan))
                    }
                }
            }
        }
        item { NoticePanel(Icons.Rounded.Info, "Totals differ from the system view when some partitions or app-private data are not readable. The difference is labelled unclassified instead of being forced into a category.", KnouxViolet) }
    }
    if (dialog) {
        PreviewActionDialog(
            title = "Large files",
            message = "The file names above are illustrative. A real breakdown lists accessible files with exact sizes and opens only URIs the app is permitted to read.",
            confirm = "Close",
            onDismiss = { dialog = false },
        )
    }
}

/* 19 — Battery Diagnostics */
private data class BatterySnapshot(
    val percent: Int?,
    val charging: Boolean,
    val statusLabel: String,
    val temperatureC: Float?,
    val voltageMv: Int?,
    val healthLabel: String,
    val technology: String,
)

private fun readBattery(context: Context): BatterySnapshot {
    val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
    val percent = if (level >= 0 && scale > 0) level * 100 / scale else null
    val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)
    val plugged = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) ?: 0
    val charging = plugged != 0 || status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
    val statusLabel = when (status) {
        BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
        BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
        BatteryManager.BATTERY_STATUS_FULL -> "Full"
        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not charging"
        else -> "Unknown"
    }
    val tempRaw = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE) ?: Int.MIN_VALUE
    val voltage = intent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)?.takeIf { it > 0 }
    val health = intent?.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
    val healthLabel = when (health) {
        BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
        BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
        BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over voltage"
        BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
        else -> "Unavailable"
    }
    val tech = intent?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Unavailable"
    return BatterySnapshot(
        percent = percent,
        charging = charging,
        statusLabel = statusLabel,
        temperatureC = if (tempRaw > Int.MIN_VALUE) tempRaw / 10f else null,
        voltageMv = voltage,
        healthLabel = healthLabel,
        technology = tech,
    )
}

@Composable
fun BatteryDiagnosticsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var refresh by remember { mutableIntStateOf(0) }
    val battery = remember(refresh) { readBattery(context) }
    DetailScaffold(
        hero = HeroKind.Battery,
        title = "Battery ",
        accent = "Diagnostics",
        subtitle = "Device battery readings where Android exposes them; unavailable stays labelled.",
        onBack = onBack,
        actions = {
            Box(Modifier.weight(1f)) { GhostButton("Refresh", { refresh++ }, Modifier.fillMaxWidth()) }
            Box(Modifier.weight(1f)) { GradientActionButton("Battery Saver", {
                runCatching { context.startActivity(Intent(android.provider.Settings.ACTION_BATTERY_SAVER_SETTINGS)) }
            }, Modifier.fillMaxWidth()) }
        },
    ) {
        item { PreviewDisclosure() }
        item {
            GlassPanel(Modifier.fillMaxWidth(), padding = 14.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressDonut(
                        progress = (battery.percent ?: 0) / 100f,
                        centerText = battery.percent?.let { "$it%" } ?: "—",
                        caption = if (battery.charging) "charging" else battery.statusLabel.lowercase(),
                        size = 94.dp,
                        arcColors = listOf(KnouxCyan, KnouxMagenta),
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        SummaryLine(Icons.Rounded.BatteryChargingFull, "Status:", battery.statusLabel, KnouxCyan)
                        SummaryLine(Icons.Rounded.Thermostat, "Temperature:", battery.temperatureC?.let { "%.1f °C".format(it) } ?: "Unavailable", KnouxAmber)
                        SummaryLine(Icons.Rounded.Bolt, "Voltage:", battery.voltageMv?.let { "$it mV" } ?: "Unavailable", KnouxViolet)
                    }
                }
            }
        }
        item { DetailSectionTitle("Reported by the device", "Read live from BatteryManager — not simulated") }
        item {
            GlassPanel(Modifier.fillMaxWidth().padding(top = 8.dp), padding = 14.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    SummaryLine(Icons.Rounded.Shield, "Health:", battery.healthLabel, KnouxCyan)
                    SummaryLine(Icons.Rounded.Memory, "Technology:", battery.technology, KnouxViolet)
                    SummaryLine(Icons.Rounded.Event, "Charge cycles:", "Unavailable on this Android build", KnouxAmber)
                    SummaryLine(Icons.Rounded.Memory, "Per-app drain:", "Requires usage access · not measured", KnouxSoftText)
                }
            }
        }
        item { NoticePanel(Icons.Rounded.Warning, "Android does not expose charging-cycle count or per-app battery drain to every app. No battery gain, temperature reduction or CPU saving is claimed here.", KnouxAmber) }
    }
}

/* 20 — AI Recommendations */
@Composable
fun AiRecommendationsScreen(onBack: () -> Unit) {
    var selected by remember { mutableStateOf(setOf(0, 2)) }
    var dialog by remember { mutableStateOf(false) }
    val recs = listOf(
        Triple("Review temporary files", "Evidence: design preview · 2.4 GB reference", "Impact not measured"),
        Triple("Limit background sync", "Evidence: Android sync setting is user-visible", "Estimated only"),
        Triple("Review camera access", "Evidence: permission visible in Android settings", "Privacy review"),
        Triple("Clear duplicate media", "Evidence: requires real hash scan", "Not verified"),
        Triple("Keep thermal headroom", "Evidence: no direct thermal control allowed", "Advisory"),
    )
    DetailScaffold(
        hero = HeroKind.Chip,
        title = "AI ",
        accent = "Recommendations",
        subtitle = "Deterministic, evidence-labelled suggestions with supported OS handoffs.",
        onBack = onBack,
        actions = {
            Box(Modifier.weight(1f)) { GhostButton("Evidence", { dialog = true }, Modifier.fillMaxWidth()) }
            Box(Modifier.weight(1f)) { GradientActionButton("Apply Selected", { dialog = true }, Modifier.fillMaxWidth()) }
        },
    ) {
        item { PreviewDisclosure() }
        item { NoticePanel(Icons.Rounded.AutoAwesome, "No on-device model is connected in this UI milestone. These are deterministic rule-based previews, clearly separated from measured facts and estimated outcomes.", KnouxViolet) }
        item { MiniStatStrip(Fixtures.aiMetrics) }
        item { DetailSectionTitle("Recommendation Plan", "Select actions you want to review") }
        items(recs.size) { index ->
            val (title, evidence, impact) = recs[index]
            val checked = selected.contains(index)
            Box(Modifier.padding(top = 8.dp)) {
                GlassPanel(Modifier.fillMaxWidth(), padding = 12.dp) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        NeonIconTile(if (checked) Icons.Rounded.CleaningServices else Icons.Rounded.Shield, size = 34.dp, iconSize = 17.dp, tint = if (checked) KnouxCyan else KnouxSoftText)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(title, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KnouxText))
                            Text(evidence, style = TextStyle(fontSize = 10.sp, color = KnouxSoftText))
                            Text(impact, style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = KnouxAmber))
                        }
                        Box(
                            Modifier.size(20.dp).clip(RoundedCornerShape(6.dp))
                                .background(if (checked) KnouxCyan else KnouxPanelDeep)
                                .padding(3.dp),
                        ) {
                            if (checked) Icon(Icons.Rounded.ChevronRight, null, tint = KnouxPanelDeep, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
        item { NoticePanel(Icons.Rounded.Info, "Applying an action never reports success from a click alone. Supported effects come from Android settings or measured operations; everything else stays advisory.", KnouxCyan) }
    }
    if (dialog) {
        PreviewActionDialog(
            title = "Apply recommendations",
            message = "Nothing is applied from this preview. A real implementation performs only supported, non-destructive actions, verifies the platform result, and records verified vs estimated outcomes separately.",
            confirm = "Understood",
            onDismiss = { dialog = false },
        )
    }
}
