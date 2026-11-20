package com.knoux.ai.ui.v2

import android.app.Application
import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DataUsage
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.DeveloperMode
import androidx.compose.material.icons.rounded.ElectricalServices
import androidx.compose.material.icons.rounded.FactCheck
import androidx.compose.material.icons.rounded.FileCopy
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.ManageSearch
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.PermMedia
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material.icons.rounded.ToggleOn
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.knoux.ai.database.AppDatabase
import com.knoux.ai.database.KnouxRepository
import com.knoux.ai.ui.v2.V2LuxuryIdentity
import com.knoux.ai.ui.v2.V2LuxuryStage
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/* ------------------------------------------------------------------ *
 * Section tool catalog — shared model
 * ------------------------------------------------------------------ */

internal data class ToolRow(
    val icon: ImageVector,
    val label: String,
    val value: String,
    val accent: Color,
)

internal enum class ToolList {
    NONE, FILES, PHOTO_FILES, VIDEO_FILES, GROUPS, PHOTO_GROUPS, VIDEO_GROUPS,
    APPS, PERMISSIONS, HISTORY,
}

internal enum class ToolBehavior {
    NONE, SEARCH_APPS, SEARCH_FILES, FILTER_APPS_SIZE, SIZE_FILTER_MEDIA,
    SORT_FILES, SORT_APPS, RANGE_HISTORY, GOAL_CHIPS, TOGGLES,
    LAUNCH_APPS, APP_SETTINGS_ACTION, UNINSTALL_APPS,
}

internal enum class ToolHandoff {
    NONE, BATTERY_SAVER, BATTERY_SETTINGS, PRIVACY_SETTINGS, STORAGE_SETTINGS,
    APP_SETTINGS, USAGE_ACCESS, OPEN_FIRST_MEDIA,
}

internal data class SectionToolDef(
    val section: String,
    val route: String,
    val order: String,
    val title: String,
    val english: String,
    val tagline: String,
    val icon: ImageVector,
    val accent: Color,
    val proOnly: Boolean = false,
    val metric: (DeviceFacts, List<ScanHistoryEntry>) -> String = { f, _ ->
        if (f.computedAt > 0L) formatBytes(f.storage.freeBytes) + " متاحة" else "جاري القراءة"
    },
    val rows: (DeviceFacts, List<ScanHistoryEntry>) -> List<ToolRow> = { _, _ -> emptyList() },
    val note: String = "",
    val list: ToolList = ToolList.NONE,
    val behavior: ToolBehavior = ToolBehavior.NONE,
    val handoff: ToolHandoff = ToolHandoff.NONE,
    val actionRoute: String? = null,
    val actionLabel: String? = null,
    /** App scope pre-selected on service screens that open an app list. */
    val initialAppFilter: Int = 0,
) {
    val requiresMedia: Boolean
        get() = list == ToolList.FILES || list == ToolList.PHOTO_FILES ||
            list == ToolList.VIDEO_FILES || list == ToolList.GROUPS ||
            list == ToolList.PHOTO_GROUPS || list == ToolList.VIDEO_GROUPS ||
            (section == "duplicates" && route != "dup-exclusions")
}

/* ------------------------------------------------------------------ *
 * Section card artwork — the same 3D raster language used by Home
 * ------------------------------------------------------------------ */

internal object SectionArt {

    private val palette: List<Int> = listOf(
        com.knoux.ai.R.drawable.knoux_svc_duplicates,
        com.knoux.ai.R.drawable.knoux_svc_residue,
        com.knoux.ai.R.drawable.knoux_svc_privacy,
        com.knoux.ai.R.drawable.knoux_svc_deepscan,
        com.knoux.ai.R.drawable.knoux_svc_apps,
        com.knoux.ai.R.drawable.knoux_svc_storage,
        com.knoux.ai.R.drawable.knoux_svc_battery,
        com.knoux.ai.R.drawable.knoux_svc_ai,
        com.knoux.ai.R.drawable.knoux_home_icon_clean,
        com.knoux.ai.R.drawable.knoux_home_icon_duplicate,
        com.knoux.ai.R.drawable.knoux_home_icon_privacy,
        com.knoux.ai.R.drawable.knoux_home_icon_storage,
        com.knoux.ai.R.drawable.knoux_home_icon_battery,
        com.knoux.ai.R.drawable.knoux_home_icon_ai,
        com.knoux.ai.R.drawable.knoux_clean_icon_cache,
        com.knoux.ai.R.drawable.knoux_clean_icon_apk,
        com.knoux.ai.R.drawable.knoux_clean_icon_temp,
        com.knoux.ai.R.drawable.knoux_clean_icon_empty,
        com.knoux.ai.R.drawable.knoux_clean_icon_gain,
        com.knoux.ai.R.drawable.knoux_clean_icon_recycle,
        com.knoux.ai.R.drawable.knoux_clean_icon_ai,
    )

    private val primary: Map<String, Int> = mapOf(
        "smartclean" to com.knoux.ai.R.drawable.knoux_home_icon_clean,
        "duplicates" to com.knoux.ai.R.drawable.knoux_svc_duplicates,
        "residue" to com.knoux.ai.R.drawable.knoux_svc_residue,
        "privacy" to com.knoux.ai.R.drawable.knoux_svc_privacy,
        "deepscan" to com.knoux.ai.R.drawable.knoux_svc_deepscan,
        "reports" to com.knoux.ai.R.drawable.knoux_clean_icon_gain,
        "appmanager" to com.knoux.ai.R.drawable.knoux_svc_apps,
        "storage" to com.knoux.ai.R.drawable.knoux_svc_storage,
        "battery" to com.knoux.ai.R.drawable.knoux_svc_battery,
        "ai" to com.knoux.ai.R.drawable.knoux_svc_ai,
        "developer" to com.knoux.ai.R.drawable.knoux_svc_deepscan,
        "googleservices" to com.knoux.ai.R.drawable.knoux_svc_apps,
        "security" to com.knoux.ai.R.drawable.knoux_svc_privacy,
        "backup" to com.knoux.ai.R.drawable.knoux_svc_residue,
    )

    /** The section emblem used by the Home-style centerpiece orb. */
    fun emblem(section: String): Int = primary[section] ?: palette.first()

    /** Every section gets ten distinct cards, starting with its own emblem. */
    fun forIndex(section: String, index: Int): Int {
        val head = primary[section] ?: palette.first()
        if (index == 0) return head
        val size = palette.size
        val start = palette.indexOf(head).takeIf { it >= 0 } ?: 0
        var step = index
        while (step < size + index) {
            val candidate = palette[(start + step) % size]
            if (candidate != head) return candidate
            step++
        }
        return head
    }
}

internal object SectionTools {
    val defs: List<SectionToolDef> get() = SectionToolsCatalog.all

    val allRoutes: List<String>
        get() = CleanTools.routes + defs.map { it.route }

    fun items(section: String): List<ServiceSubItem> {
        val core = defs
            .filter { it.section == section }
            .mapIndexed { index, def ->
                ServiceSubItem(
                    title = def.title,
                    subtitle = def.tagline,
                    route = def.route,
                    icon = def.icon,
                    accent = def.accent,
                    state = ServiceState.LIVE,
                    proOnly = def.proOnly,
                    art = SectionArt.forIndex(section, index),
                )
            }
        val pro = ServiceCatalog150.proItems(catalogFamily(section))
            .filter { it.isRouted }
            .mapIndexed { index, item ->
                ServiceSubItem(
                    title = item.titleAr,
                    subtitle = "PRO · ${item.id}",
                    route = item.route!!,
                    icon = Icons.Rounded.Lock,
                    accent = accent2For(section),
                    state = ServiceState.PREVIEW,
                    proOnly = true,
                    art = SectionArt.forIndex(section, 10 + index),
                )
            }
        return core + pro
    }

    /** Catalog family id for a UI section key. */
    fun catalogFamily(section: String): String = when (section) {
        "storage" -> "storage_analyzer"
        "duplicates" -> "duplicate_finder"
        "residue" -> "app_residue"
        "privacy" -> "privacy_sweep"
        "appmanager" -> "app_manager"
        "battery" -> "battery_saver"
        "deepscan" -> "deep_scan"
        "smartclean" -> "smart_clean"
        "reports" -> "reports"
        "ai" -> "ai_optimization"
        "developer" -> "developer_tools"
        "googleservices" -> "google_services"
        "security" -> "advanced_security"
        "backup" -> "backup_restore"
        else -> section
    }

    fun byRoute(route: String): SectionToolDef? = defs.firstOrNull { it.route == route }

    fun accent2For(section: String): Color = when (section) {
        "duplicates" -> V2Pink
        "residue" -> V2Cyan
        "privacy" -> V2Violet
        "deepscan" -> V2Cyan
        "reports" -> V2Violet
        "appmanager" -> V2Pink
        "storage" -> V2Green
        "battery" -> V2Cyan
        "ai" -> V2Cyan
        "developer" -> V2Blue
        "googleservices" -> V2Cyan
        "security" -> V2Violet
        "backup" -> V2Pink
        else -> V2Violet
    }

    fun bannerSpec(def: SectionToolDef, availability: ToolContentState): KnouxServiceSpec = KnouxServiceSpec(
        key = def.route,
        order = def.order,
        title = def.title,
        english = def.english,
        tagline = def.tagline,
        accent = def.accent,
        accent2 = accent2For(def.section),
        glyph = def.icon,
        chips = listOf(
            when (availability) {
                ToolContentState.LOADING -> "لم تكتمل القراءة"
                ToolContentState.PERMISSION_REQUIRED -> "لا نتائج قبل الإذن"
                else -> "ضمن النطاق المتاح"
            },
            "لا حذف تلقائي",
        ),
        subItems = emptyList(),
        pro = def.proOnly,
        artwork = SectionArt.forIndex(
            def.section,
            defs.filter { it.section == def.section }.indexOf(def).coerceAtLeast(0),
        ),
    )
}

/* ------------------------------------------------------------------ *
 * Shared screen for the nine remaining sections
 * ------------------------------------------------------------------ */

@Composable
fun V2SectionToolScreen(route: String, onBack: () -> Unit, onPro: () -> Unit) {
    val def = SectionTools.byRoute(route) ?: return
    // Duplicate steps open the actual review/consent workflow rather than a
    // read-only rendering of the Home quick-scan cache.
    if (def.section == "duplicates") {
        V2DuplicatesWorkspace(
            onBack = onBack,
            onPro = onPro,
            initialMode = if (route == "dup-compare") DuplicateScanMode.VISUAL else DuplicateScanMode.EXACT,
        )
        return
    }
    val context = LocalContext.current
    val navigate = LocalServiceNavigate.current
    val (refresh, _) = rememberRefreshCounter()

    val application = context.applicationContext as Application
    val appDatabase = remember(application) { AppDatabase.getDatabase(application) }
    val repository = remember(appDatabase) { KnouxRepository(appDatabase) }
    val sectionViewModel: SectionServiceViewModel = viewModel(
        key = "section-${def.route}",
        initializer = {
            SectionServiceViewModel(
                application = application,
                repository = repository,
                sectionKey = def.section,
                serviceName = def.title,
                requiresMedia = def.requiresMedia,
            )
        },
    )
    // No automatic start: the K orb is the single entry point for every
    // service. A screen never scans until the user presses the orb.
    val sectionState = sectionViewModel.state
    val facts = sectionState.facts
    val history = remember(refresh) { readScanHistory(context) }
    val rows = def.rows(facts, history)
    val loaded = facts.computedAt > 0L
    val availability = toolContentState(
        loaded = loaded,
        permissionGranted = !def.requiresMedia || facts.media.permissionGranted,
        sourceCount = 1,
    )
    val missingMedia = availability == ToolContentState.PERMISSION_REQUIRED
    val relatedServices = SectionTools.defs
        .filter { it.section == def.section && it.route != def.route }
        .take(3)

    var query by remember { mutableStateOf("") }
    var appFilter by remember { mutableIntStateOf(def.initialAppFilter) }
    var sortMode by remember { mutableIntStateOf(0) }
    var rangeDays by remember { mutableIntStateOf(1) }
    var minMb by remember { mutableIntStateOf(50) }
    var goal by remember { mutableIntStateOf(0) }
    var smartAlerts by remember { mutableStateOf(true) }
    var backgroundTrim by remember { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(Modifier.fillMaxSize()) {
            V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = onPro)
            V2LuxuryIdentity(
                title = def.title,
                english = def.english,
                subtitle = def.tagline,
                accent = def.accent,
            )
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    // K orb as the centerpiece - universal service start control
                    KnouxRunOrbBlock(
                        progress = when {
                            sectionState.isScanning -> sectionState.progress
                            loaded -> 1f
                            else -> 0f
                        },
                        running = sectionState.isScanning,
                        enabled = !sectionState.isScanning,
                        label = when {
                            sectionState.isScanning -> sectionState.stageLabel.ifBlank { "جارٍ التنفيذ…" } +
                                " · ${(sectionState.progress * 100).roundToInt()}%"
                            loaded -> "اضغط لإعادة تشغيل الخدمة"
                            else -> "اضغط لتشغيل الخدمة"
                        },
                        accent = def.accent,
                        accent2 = SectionTools.accent2For(def.section),
                        size = 148.dp,
                        onTap = {
                            if (!sectionState.isScanning) sectionViewModel.rescan()
                        },
                    )
                }
                if (sectionState.sessionsLogged > 0 || sectionState.isScanning) {
                    item {
                        ServiceAuditStrip(
                            state = sectionState,
                            accent = def.accent,
                        )
                    }
                }
                if (sectionState.scanError != null) {
                    item {
                        ServiceCapabilityCard(
                            title = "تعذّر إكمال القراءة",
                            body = sectionState.scanError,
                            state = ServiceState.PERMISSION,
                            actionLabel = "إعادة القراءة",
                            onAction = { sectionViewModel.rescan() },
                        )
                    }
                }
                if (def.behavior != ToolBehavior.NONE) {
                    item {
                        ToolBehaviorCard(
                            behavior = def.behavior,
                            query = query,
                            onQuery = { query = it },
                            appFilter = appFilter,
                            onAppFilter = { appFilter = it },
                            sortMode = sortMode,
                            onSortMode = { sortMode = it },
                            rangeDays = rangeDays,
                            onRangeDays = { rangeDays = it },
                            minMb = minMb,
                            onMinMb = { minMb = it },
                            goal = goal,
                            onGoal = { goal = it },
                            smartAlerts = smartAlerts,
                            onSmartAlerts = { smartAlerts = it },
                            backgroundTrim = backgroundTrim,
                            onBackgroundTrim = { backgroundTrim = it },
                            accent = def.accent,
                        )
                    }
                }
                item {
                    ServiceVisualDeck(
                        section = def.section,
                        facts = facts,
                        accent = def.accent,
                        accent2 = SectionTools.accent2For(def.section),
                        history = history,
                    )
                }
                if (loaded && !missingMedia && rows.isNotEmpty()) {
                    item { V2SectionLabel("قراءة هذا الهاتف", trailing = if (facts.computedAt > 0L) "مباشر" else "جاري") }
                    item { ServiceRowTileGrid(rows, Modifier.fillMaxWidth()) }
                }
                if (!loaded) {
                    item {
                        V2GlassCard(Modifier.fillMaxWidth(), padding = 16.dp, glow = def.accent) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(Modifier.size(24.dp), color = def.accent, strokeWidth = 2.dp)
                                Spacer(Modifier.width(12.dp))
                                KText("جارٍ قراءة المصادر المتاحة…", size = 11.sp, color = V2Soft)
                            }
                        }
                    }
                } else if (missingMedia) {
                    item {
                        ServiceCapabilityCard(
                            title = "صلاحية الوسائط مطلوبة",
                            body = "لم تُقرأ مكتبة الصور والفيديو. افتح إعدادات التطبيق لمراجعة الأذونات، ثم ارجع وحدّث القراءة.",
                            state = ServiceState.PERMISSION,
                            actionLabel = "مراجعة أذونات التطبيق",
                            onAction = { openAppInfo(context, context.packageName) },
                        )
                    }
                } else toolsList(
                    def = def,
                    facts = facts,
                    history = history,
                    query = query,
                    appFilter = appFilter,
                    sortMode = sortMode,
                    rangeDays = rangeDays,
                    minMb = minMb,
                )
                if (def.note.isNotBlank()) {
                    item { PreviewNote(def.note) }
                }
                item {
                    ToolActionCard(
                        def = def,
                        facts = facts,
                        onNavigate = navigate,
                        onHandoff = {
                            when (def.handoff) {
                                ToolHandoff.BATTERY_SAVER -> openBatterySaverSettings(context)
                                ToolHandoff.BATTERY_SETTINGS -> openBatterySettings(context)
                                ToolHandoff.PRIVACY_SETTINGS -> openPrivacySettings(context)
                                ToolHandoff.STORAGE_SETTINGS -> openStorageSettings(context)
                                ToolHandoff.APP_SETTINGS -> openAllAppSettings(context)
                                ToolHandoff.USAGE_ACCESS -> openUsageAccessSettings(context)
                                ToolHandoff.OPEN_FIRST_MEDIA -> {
                                    facts.media.largestFiles.firstOrNull()?.let { openMedia(context, it.uriString) }
                                }
                                ToolHandoff.NONE -> Unit
                            }
                        },
                    )
                }
                item {
                    Row(
                        Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(V2Green.withAlpha(.06f))
                            .border(1.dp, V2Green.withAlpha(.22f), RoundedCornerShape(16.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Rounded.Verified, null, tint = V2Green, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(7.dp))
                        Column {
                            KText("مراجعة قبل التنفيذ", size = 9.6.sp, weight = FontWeight.Bold, color = V2Green)
                            KText(
                                "هذه الشاشة تقرأ وتوضح فقط. لا تغيير على النظام من دون تأكيد صريح منك.",
                                size = 8.4.sp,
                                lineHeight = 11.8.sp,
                                color = V2Soft,
                            )
                        }
                    }
                }
                item { V2SectionLabel("خدمات مرتبطة", trailing = "نفس القسم") }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        relatedServices.forEach { related ->
                            V2GlassCard(
                                Modifier
                                    .weight(1f)
                                    .height(72.dp)
                                    .clickable { navigate(related.route) },
                                padding = 7.dp,
                                radius = 15.dp,
                                glow = related.accent,
                                borderAlpha = .34f,
                            ) {
                                Column(
                                    Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Icon(related.icon, null, tint = related.accent, modifier = Modifier.size(22.dp))
                                    Spacer(Modifier.height(3.dp))
                                    KText(
                                        related.title,
                                        size = 8.6.sp,
                                        weight = FontWeight.SemiBold,
                                        lineHeight = 11.sp,
                                        align = TextAlign.Center,
                                        maxLines = 2,
                                    )
                                }
                            }
                        }
                        repeat(3 - relatedServices.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}

/* ----------------------------- behavior ----------------------------- */

@Composable
private fun ToolBehaviorCard(
    behavior: ToolBehavior,
    query: String,
    onQuery: (String) -> Unit,
    appFilter: Int,
    onAppFilter: (Int) -> Unit,
    sortMode: Int,
    onSortMode: (Int) -> Unit,
    rangeDays: Int,
    onRangeDays: (Int) -> Unit,
    minMb: Int,
    onMinMb: (Int) -> Unit,
    goal: Int,
    onGoal: (Int) -> Unit,
    smartAlerts: Boolean,
    onSmartAlerts: (Boolean) -> Unit,
    backgroundTrim: Boolean,
    onBackgroundTrim: (Boolean) -> Unit,
    accent: Color,
) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 18.dp, glow = accent, borderAlpha = .30f) {
        Column {
            when (behavior) {
                ToolBehavior.SEARCH_APPS -> {
                    KText("ابحث بالاسم أو اسم الحزمة", size = 9.sp, weight = FontWeight.Bold, color = accent)
                    Spacer(Modifier.height(7.dp))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.withAlpha(.05f))
                            .border(1.dp, Color.White.withAlpha(.10f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Rounded.Search, null, tint = V2Soft, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Box(Modifier.weight(1f)) {
                            if (query.isBlank()) {
                                KText("اسم التطبيق …", size = 8.6.sp, color = V2Muted)
                            }
                            BasicTextField(
                                value = query,
                                onValueChange = onQuery,
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                ),
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
                ToolBehavior.FILTER_APPS_SIZE -> {
                    KText("فلترة قائمة التطبيقات", size = 9.sp, weight = FontWeight.Bold, color = accent)
                    Spacer(Modifier.height(7.dp))
                    ChipsRow(
                        options = listOf("الكل", "المستخدم", "النظام", "حساسة", "معطّلة"),
                        selected = appFilter,
                        onSelect = onAppFilter,
                        accent = accent,
                    )
                }
                ToolBehavior.SEARCH_FILES -> {
                    KText("ابحث بالاسم داخل قائمة الملفات", size = 9.sp, weight = FontWeight.Bold, color = accent)
                    Spacer(Modifier.height(7.dp))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.withAlpha(.05f))
                            .border(1.dp, Color.White.withAlpha(.10f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Rounded.Search, null, tint = V2Soft, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Box(Modifier.weight(1f)) {
                            if (query.isBlank()) {
                                KText("اسم الملف …", size = 8.6.sp, color = V2Muted)
                            }
                            BasicTextField(
                                value = query,
                                onValueChange = onQuery,
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                ),
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
                ToolBehavior.SIZE_FILTER_MEDIA -> {
                    KText("الحد الأدنى للحجم", size = 9.sp, weight = FontWeight.Bold, color = accent)
                    Spacer(Modifier.height(7.dp))
                    ChipsRow(
                        options = listOf("10MB", "50MB", "100MB", "500MB"),
                        selected = listOf(10, 50, 100, 500).indexOf(minMb).coerceAtLeast(0),
                        onSelect = { index -> onMinMb(listOf(10, 50, 100, 500)[index]) },
                        accent = accent,
                    )
                }
                ToolBehavior.SORT_APPS -> {
                    KText("ترتيب التطبيقات", size = 9.sp, weight = FontWeight.Bold, color = accent)
                    Spacer(Modifier.height(7.dp))
                    ChipsRow(
                        options = listOf("الاسم", "النظام أولًا", "الأكثر أذونات"),
                        selected = sortMode,
                        onSelect = onSortMode,
                        accent = accent,
                    )
                }
                ToolBehavior.SORT_FILES -> {
                    KText("ترتيب الملفات", size = 9.sp, weight = FontWeight.Bold, color = accent)
                    Spacer(Modifier.height(7.dp))
                    ChipsRow(
                        options = listOf("الأكبر أولًا", "الاسم", "الفيديو أولًا"),
                        selected = sortMode,
                        onSelect = onSortMode,
                        accent = accent,
                    )
                }
                ToolBehavior.RANGE_HISTORY -> {
                    KText("الفترة الزمنية للسجل", size = 9.sp, weight = FontWeight.Bold, color = accent)
                    Spacer(Modifier.height(7.dp))
                    ChipsRow(
                        options = listOf("7 أيام", "30 يومًا", "90 يومًا", "الكل"),
                        selected = rangeDays,
                        onSelect = onRangeDays,
                        accent = accent,
                    )
                }
                ToolBehavior.GOAL_CHIPS -> {
                    KText("هدف التحسين", size = 9.sp, weight = FontWeight.Bold, color = accent)
                    Spacer(Modifier.height(7.dp))
                    ChipsRow(
                        options = listOf("أداء عام", "بطارية", "ألعاب", "توفير الطاقة"),
                        selected = goal,
                        onSelect = onGoal,
                        accent = accent,
                    )
                }
                ToolBehavior.TOGGLES -> {
                    KText("تخصيص الإجراء", size = 9.sp, weight = FontWeight.Bold, color = accent)
                    Spacer(Modifier.height(6.dp))
                    ToolToggleRow("تنبيهات ذكية", "تظهر عند تجاوز الحدود المقروءة", smartAlerts, onSmartAlerts, accent)
                    ToolToggleRow("تقليل عمل الخلفية", "تفضيل محلي لا يغيّر إعدادات النظام", backgroundTrim, onBackgroundTrim, accent)
                }
                ToolBehavior.NONE,
                ToolBehavior.LAUNCH_APPS,
                ToolBehavior.APP_SETTINGS_ACTION,
                ToolBehavior.UNINSTALL_APPS,
                -> Unit
            }
        }
    }
}

@Composable
private fun ChipsRow(
    options: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    accent: Color,
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        options.forEachIndexed { index, label ->
            Box(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected == index) accent.withAlpha(.20f) else Color.White.withAlpha(.04f))
                    .border(
                        1.dp,
                        if (selected == index) accent.withAlpha(.55f) else Color.White.withAlpha(.09f),
                        RoundedCornerShape(10.dp),
                    )
                    .clickable { onSelect(index) }
                    .padding(vertical = 6.dp, horizontal = 2.dp),
                contentAlignment = Alignment.Center,
            ) {
                KText(
                    label,
                    size = 6.8.sp,
                    weight = FontWeight.Bold,
                    color = if (selected == index) accent else V2Soft,
                    align = TextAlign.Center,
                    maxLines = 2,
                )
            }
        }
    }
}

@Composable
private fun ToolToggleRow(label: String, hint: String, checked: Boolean, onChange: (Boolean) -> Unit, accent: Color) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            KText(label, size = 8.8.sp, weight = FontWeight.Bold)
            KText(hint, size = 7.sp, color = V2Muted)
        }
        Box(
            Modifier
                .size(width = 34.dp, height = 19.dp)
                .clip(RoundedCornerShape(50))
                .background(if (checked) accent.withAlpha(.80f) else Color.White.withAlpha(.10f))
                .padding(2.dp),
            contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
        ) {
            Box(Modifier.size(15.dp).clip(CircleShape).background(Color.White))
        }
    }
}

/* ----------------------------- lists ----------------------------- */

private fun androidx.compose.foundation.lazy.LazyListScope.toolsList(
    def: SectionToolDef,
    facts: DeviceFacts,
    history: List<ScanHistoryEntry>,
    query: String,
    appFilter: Int,
    sortMode: Int,
    rangeDays: Int,
    minMb: Int,
) {
    when (def.list) {
        ToolList.NONE -> Unit
        ToolList.FILES, ToolList.PHOTO_FILES, ToolList.VIDEO_FILES -> {
            val scope = when (def.list) {
                ToolList.PHOTO_FILES -> facts.media.largestFiles.filter { !it.isVideo }
                ToolList.VIDEO_FILES -> facts.media.largestFiles.filter { it.isVideo }
                else -> facts.media.largestFiles
            }
            val base = scope.filter { file ->
                (query.isBlank() || file.name.contains(query, true)) &&
                    (def.behavior != ToolBehavior.SIZE_FILTER_MEDIA ||
                        file.sizeBytes >= minMb.toLong() * 1024L * 1024L)
            }
            val files = when (sortMode) {
                1 -> base.sortedBy { it.name }
                2 -> base.sortedByDescending { it.isVideo }
                else -> base.sortedByDescending { it.sizeBytes }
            }
            val contentState = toolContentState(
                loaded = facts.computedAt > 0L,
                permissionGranted = facts.media.permissionGranted,
                sourceCount = scope.size,
                visibleCount = files.size,
            )
            if (contentState != ToolContentState.CONTENT) {
                item {
                    ServiceCapabilityCard(
                        title = if (contentState == ToolContentState.FILTERED_EMPTY) "لا ملفات تطابق الفلتر" else "لا ملفات وسائط لعرضها",
                        body = if (contentState == ToolContentState.FILTERED_EMPTY) {
                            "قائمة الملفات تحتوي عناصر، لكن لا شيء يطابق البحث أو حد الحجم الحالي. غيّر الفلتر لعرضها."
                        } else {
                            "لم يُعد المصدر ملفات في النطاق المقروء. هذا ليس تأكيدًا على خلو الجهاز كله."
                        },
                        state = ServiceState.LIVE,
                    )
                }
            } else {
                item { V2SectionLabel("الملفات", trailing = "${formatCount(files.size)}") }
                files.take(12).forEach { file ->
                    item {
                        ServiceFileCard(
                            file = file,
                            accent = if (file.isVideo) V2Pink else V2Violet,
                        )
                    }
                }
            }
        }
        ToolList.GROUPS, ToolList.PHOTO_GROUPS, ToolList.VIDEO_GROUPS -> {
            val groups = when (def.list) {
                ToolList.PHOTO_GROUPS -> facts.media.duplicateGroups.filter { !it.isVideo }
                ToolList.VIDEO_GROUPS -> facts.media.duplicateGroups.filter { it.isVideo }
                else -> facts.media.duplicateGroups
            }
            if (groups.isEmpty()) {
                item {
                    ServiceCapabilityCard(
                        title = if (facts.media.permissionGranted) "لا مجموعات تكرار في هذا النوع" else "صلاحية الوسائط مطلوبة",
                        body = "لم تظهر مجموعات ضمن نطاق المقارنة المحدود. لا يعني ذلك فحص كل الملفات أو خلو الجهاز كله من التكرار.",
                        state = if (facts.media.permissionGranted) ServiceState.LIVE else ServiceState.PERMISSION,
                    )
                }
            } else {
                item { V2SectionLabel("المجموعات", trailing = "${groups.size}") }
                groups.take(10).forEach { group ->
                    item {
                        ServiceGroupCard(
                            group = group,
                            accent = if (group.isVideo) V2Pink else V2Violet,
                        )
                    }
                }
            }
        }
        ToolList.APPS -> {
            val filtered = facts.installedApps
                .filter { app ->
                    (query.isBlank() || app.label.contains(query, true) || app.packageName.contains(query, true)) &&
                        when (appFilter) {
                            1 -> !app.isSystem
                            2 -> app.isSystem
                            3 -> app.sensitivePermissions.isNotEmpty()
                            4 -> !app.isEnabled
                            else -> true
                        }
                }
                .sortedWith(
                    when (sortMode) {
                        1 -> compareByDescending<InstalledAppFacts> { it.isSystem }.thenBy { it.label }
                        2 -> compareByDescending { it.sensitivePermissions.size }
                        else -> compareBy { it.label }
                    }
                )
            if (filtered.isEmpty()) {
                item {
                    ServiceCapabilityCard(
                        title = "لا تطبيقات تطابق الفلتر",
                        body = "غيّر كلمة البحث أو الفلتر لعرض تطبيقات أخرى مثبتة على هذا الهاتف.",
                        state = ServiceState.LIVE,
                    )
                }
            } else {
                item { V2SectionLabel("التطبيقات", trailing = "${formatCount(filtered.size)}") }
                filtered.take(14).forEach { app ->
                    item {
                        ToolAppRow(
                            app = app,
                            behavior = def.behavior,
                            accent = def.accent,
                        )
                    }
                }
            }
        }
        ToolList.PERMISSIONS -> {
            if (facts.privacyGroups.isEmpty()) {
                item {
                    ServiceCapabilityCard(
                        title = "لا مجموعات أذونات مقروءة",
                        body = "تعذّر على PackageManager قراءة أذونات التطبيقات في هذه الجلسة.",
                        state = ServiceState.PERMISSION,
                    )
                }
            } else {
                item { V2SectionLabel("مجموعات الأذونات", trailing = "${facts.privacyGroups.size}") }
                facts.privacyGroups.forEach { group ->
                    item {
                        ServicePermissionCard(
                            group = group,
                            accent = def.accent,
                        )
                    }
                }
            }
        }
        ToolList.HISTORY -> {
            val cutoff = when (rangeDays) {
                0 -> System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
                1 -> System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
                2 -> System.currentTimeMillis() - 90L * 24 * 60 * 60 * 1000
                else -> 0L
            }
            val visible = history.filter { it.timestamp >= cutoff }
            if (visible.isEmpty()) {
                item {
                    ServiceCapabilityCard(
                        title = "لا قياسات في هذه الفترة",
                        body = "شغّل الفحص العميق لتُحفظ قراءة محلية جديدة. السجل لا يُرفع لأي خادم.",
                        state = ServiceState.LIVE,
                    )
                }
            } else {
                item { V2SectionLabel("السجل", trailing = "${visible.size}/24") }
                val maxCache = visible.maxOfOrNull { it.cacheBytes }?.coerceAtLeast(1L) ?: 1L
                visible.take(14).forEach { entry ->
                    item {
                        ServiceHistoryEntryCard(
                            entry = entry,
                            maxCache = maxCache,
                            accent = V2Green,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolAppRow(app: InstalledAppFacts, behavior: ToolBehavior, accent: Color) {
    val context = LocalContext.current
    val icon = rememberAppIcon(app.packageName)
    V2GlassCard(Modifier.fillMaxWidth(), padding = 9.dp, radius = 16.dp, glow = accent, borderAlpha = .24f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(Color.White.withAlpha(.06f)),
                contentAlignment = Alignment.Center,
            ) {
                if (icon != null) {
                    Image(bitmap = icon, contentDescription = null, modifier = Modifier.size(28.dp))
                } else {
                    Icon(Icons.Rounded.Settings, null, tint = accent, modifier = Modifier.size(17.dp))
                }
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                KText(app.label, size = 8.8.sp, weight = FontWeight.Bold, maxLines = 1)
                KText(
                    "${app.packageName} · ${if (app.isSystem) "نظام" else "مستخدم"}",
                    size = 6.6.sp,
                    color = V2Soft,
                    maxLines = 1,
                )
            }
            when (behavior) {
                ToolBehavior.LAUNCH_APPS -> AppAction(Icons.Rounded.PlayArrow, accent) {
                    context.packageManager.getLaunchIntentForPackage(app.packageName)?.let { context.startActivity(it) }
                }
                ToolBehavior.APP_SETTINGS_ACTION -> AppAction(Icons.Rounded.Settings, accent) { openAppInfo(context, app.packageName) }
                ToolBehavior.UNINSTALL_APPS -> AppAction(Icons.Rounded.Delete, V2ProRed) { requestUninstall(context, app.packageName) }
                else -> {
                    KText(
                        app.versionName?.takeIf { it.isNotBlank() }?.let { "v$it" } ?: "—",
                        size = 7.4.sp,
                        weight = FontWeight.Bold,
                        color = accent,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
private fun AppAction(icon: ImageVector, tint: Color, onClick: () -> Unit) {
    Box(
        Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(tint.withAlpha(.14f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(16.dp))
    }
}

/* ----------------------------- action ----------------------------- */

@Composable
private fun ToolActionCard(
    def: SectionToolDef,
    facts: DeviceFacts,
    onNavigate: (String) -> Unit,
    onHandoff: () -> Unit,
) {
    val hasHandoff = def.handoff != ToolHandoff.NONE
    val hasRoute = def.actionRoute != null
    if (!hasHandoff && !hasRoute) {
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(def.accent.withAlpha(.07f))
                .border(1.dp, def.accent.withAlpha(.22f), RoundedCornerShape(16.dp))
                .padding(11.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Search, null, tint = def.accent, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(7.dp))
                KText(
                    "البيانات تعتمد على الأذونات المتاحة. لا تُجري هذه الشاشة تغييرًا على النظام من دون موافقتك.",
                    size = 7.6.sp,
                    lineHeight = 11.sp,
                    color = V2Soft,
                )
            }
        }
        return
    }
    V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 18.dp, glow = def.accent, borderAlpha = .30f) {
        Column(Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                V2IconTile(if (hasHandoff) Icons.Rounded.OpenInNew else Icons.Rounded.PlayArrow, def.accent, size = 36.dp, iconSize = 18.dp)
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    KText(
                        def.actionLabel ?: if (hasHandoff) "فتح إعدادات Android" else "متابعة في الشاشة المتخصصة",
                        size = 9.sp,
                        weight = FontWeight.Bold,
                    )
                    KText(
                        if (hasHandoff) "التحويل يتم إلى شاشة النظام الرسمية، وليس تنفيذًا داخليًا."
                        else "تفتح الشاشة التي تعرض هذه البيانات بتفصيل أوسع.",
                        size = 7.sp,
                        color = V2Soft,
                    )
                }
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(def.accent.withAlpha(.16f))
                        .border(1.dp, def.accent.withAlpha(.45f), RoundedCornerShape(50))
                        .clickable { if (hasRoute) onNavigate(def.actionRoute!!) else onHandoff() }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    KText("فتح", size = 8.sp, weight = FontWeight.Black, color = def.accent)
                }
            }
            if (hasHandoff && facts.media.duplicatesNote().isNotBlank()) {
                Spacer(Modifier.height(5.dp))
                KText(facts.media.duplicatesNote(), size = 7.sp, color = V2Muted)
            }
        }
    }
}

private fun MediaFacts.duplicatesNote(): String =
    if (!permissionGranted) "صلاحية الوسائط غير ممنوحة بعد." else ""

/* ------------------------------------------------------------------ *
 * Section hubs — 10 sections × 15 independently routed services
 * ------------------------------------------------------------------ */

internal object SectionHub {

    data class Entry(
        val key: String,
        val spec: KnouxServiceSpec,
        val primaryRoute: String,
        val primaryLabel: String,
    )

    val entries: List<Entry> = listOf(
        Entry("smartclean", ServiceSpecs.smartClean, "smartclean", "فتح مركز التنظيف الذكي"),
        Entry("duplicates", ServiceSpecs.duplicates, "duplicates", "فتح شاشة الملفات المكررة"),
        Entry("residue", ServiceSpecs.residue, "residue", "فتح شاشة بقايا التطبيقات"),
        Entry("privacy", ServiceSpecs.privacy, "privacy", "فتح شاشة الخصوصية"),
        Entry("deepscan", ServiceSpecs.deepScan, "deepscan", "تشغيل الفحص العميق"),
        Entry("reports", ServiceSpecs.reports, "reports", "فتح التقارير"),
        Entry("appmanager", ServiceSpecs.appManager, "appmanager", "فتح مدير التطبيقات"),
        Entry("storage", ServiceSpecs.storage, "storage", "فتح تحليل التخزين"),
        Entry("battery", ServiceSpecs.battery, "battery", "فتح شاشة البطارية"),
        Entry("ai", ServiceSpecs.ai, "ai", "فتح التحسين الذكي"),
        // Four owner-requested extra hubs, rendered with the same luxury language.
        Entry("developer", ServiceSpecs.developer, "developer-info", "فتح أدوات المطورين"),
        Entry("googleservices", ServiceSpecs.googleServices, "google-services", "فتح خدمات جوجل"),
        Entry("security", ServiceSpecs.advancedSecurity, "advanced-security", "فتح الأمان المتقدم"),
        Entry("backup", ServiceSpecs.backupRestore, "backup-restore", "فتح النسخ والاستعادة"),
    )

    val routes: List<String> get() = entries.map { "hub-${it.key}" }

    fun byRoute(route: String): Entry? = entries.firstOrNull { "hub-${it.key}" == route }

    fun byKey(key: String): Entry? = entries.firstOrNull { it.key == key }
}

/**
 * A section hub is the Home screen, re-cut for one section: the same stag
 * header, the same title lockup, the same health panel, the same centerpiece
 * orb with two flanking action cards, the same rows-of-five service rhythm, the same
 * Pro banner and the same live stat strip — only the emblem, the services and
 * the readings change. Every number below is read from this device.
 */
@Composable
fun V2SectionHubScreen(route: String, onBack: () -> Unit, onPro: () -> Unit) {
    val entry = SectionHub.byRoute(route) ?: return
    val navigate = LocalServiceNavigate.current
    val facts = rememberDeviceFacts()
    val spec = entry.spec
    val identity = remember(facts.computedAt, entry.key) { sectionIdentity(entry.key, facts) }

    Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)) {
        V2LuxuryHeaderScenery(spec.accent, Modifier.align(Alignment.TopStart))
        Column(Modifier.fillMaxSize()) {
            V2TopBar(
                mode = V2TopMode.INTERIOR,
                onBack = onBack,
                onPro = onPro,
                onMenu = { navigate("settings") },
            )
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                item {
                    V2LuxuryIdentity(
                        title = spec.title,
                        english = spec.english,
                        subtitle = spec.tagline,
                        accent = spec.accent,
                    )
                }
                item { SectionHealthPanel(spec, identity) }
                item {
                    SectionCenterpiece(
                        spec = spec,
                        status = identity.orbStatus,
                        progress = identity.gaugePercent / 100f,
                        left = identity.left,
                        right = identity.right,
                        onOpen = { navigate(entry.primaryRoute) },
                        onLeft = { navigate(identity.left.route) },
                        onRight = { navigate(identity.right.route) },
                    )
                }
                item {
                    V2LuxurySectionTitle("خدمات ${spec.title}", "${spec.subItems.size} خدمة", spec.accent)
                }
                item { V2LuxuryServiceGrid(spec.subItems, navigate) }
                item {
                    V2LuxuryProBanner(
                        onClick = onPro,
                        title = "مزايا ${spec.title}",
                        subtitle = "مزايا أوسع · كل تنفيذ يظل بموافقتك",
                    )
                }
                item { V2LuxuryStatStrip(identity.stats) }
                item { LiveReadNotice() }
            }
        }
    }
}

@Composable
private fun SectionHealthPanel(spec: KnouxServiceSpec, identity: SectionIdentity) {
    V2LuxuryHealthPanel(
        gaugeLabel = identity.gaugeLabel,
        gaugePercent = identity.gaugePercent,
        lines = identity.lines,
        accent = spec.accent,
        accent2 = spec.accent2,
    )
}

/**
 * A quiet section-tinted light wash. The shared KNOUX night sky stays, but every
 * hub gets its own colour temperature instead of a mechanically identical copy.
 */
@Composable
private fun SectionAura(accent: Color, accent2: Color, modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxWidth().height(300.dp)) {
        val warm = Offset(size.width * .84f, size.height * .16f)
        val warmRadius = size.width * .62f
        drawCircle(
            brush = Brush.radialGradient(
                listOf(accent.copy(alpha = .17f), accent2.copy(alpha = .05f), Color.Transparent),
                center = warm,
                radius = warmRadius,
            ),
            radius = warmRadius,
            center = warm,
        )
        val cool = Offset(size.width * .10f, size.height * .46f)
        val coolRadius = size.width * .48f
        drawCircle(
            brush = Brush.radialGradient(
                listOf(accent2.copy(alpha = .12f), Color.Transparent),
                center = cool,
                radius = coolRadius,
            ),
            radius = coolRadius,
            center = cool,
        )
    }
}

@Composable
private fun SectionCenterpiece(
    spec: KnouxServiceSpec,
    status: String,
    progress: Float,
    left: SectionSide,
    right: SectionSide,
    onOpen: () -> Unit,
    onLeft: () -> Unit,
    onRight: () -> Unit,
) {
    V2LuxuryStage(
        spec = spec,
        actionLabel = "دخول القسم",
        left = left,
        right = right,
        onPrimary = onOpen,
        onLeft = onLeft,
        onRight = onRight,
    )
}

@Composable
private fun SectionOrb(
    spec: KnouxServiceSpec,
    status: String,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    Box(modifier.size(162.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val centre = center
            val radius = size.minDimension / 2f
            val accent = spec.accent
            val accent2 = spec.accent2
            fun at(degrees: Float, r: Float): Offset {
                val angle = Math.toRadians(degrees.toDouble())
                return Offset(
                    centre.x + (r * cos(angle)).toFloat(),
                    centre.y + (r * sin(angle)).toFloat(),
                )
            }

            drawCircle(
                brush = Brush.radialGradient(
                    listOf(accent.copy(alpha = .22f), Color.Transparent),
                    center = centre,
                    radius = radius * 1.04f,
                ),
                radius = radius * 1.04f,
                center = centre,
            )

            // Shared premium ring, now in section colours: gold stays reserved for Pro.
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(accent, accent2, accent, accent2, accent),
                    centre,
                ),
                startAngle = -70f,
                sweepAngle = 305f,
                useCenter = false,
                style = Stroke(4.dp.toPx(), cap = StrokeCap.Round),
            )
            drawArc(
                color = accent.copy(alpha = .18f),
                startAngle = 8f,
                sweepAngle = 336f,
                useCenter = false,
                style = Stroke(9.dp.toPx(), cap = StrokeCap.Round),
            )

            when (spec.key) {
                "duplicates" -> {
                    drawArc(
                        color = accent2.copy(alpha = .34f),
                        startAngle = -46f,
                        sweepAngle = 268f,
                        useCenter = false,
                        style = Stroke(1.4.dp.toPx(), cap = StrokeCap.Round),
                    )
                    listOf(-152f, -28f).forEachIndexed { index, degrees ->
                        val p = at(degrees, radius * .90f)
                        val w = 11.dp.toPx()
                        val h = 14.dp.toPx()
                        drawRoundRect(
                            color = (if (index == 0) accent else accent2).copy(alpha = .26f),
                            topLeft = Offset(p.x - w / 2f + 3.dp.toPx(), p.y - h / 2f + 2.dp.toPx()),
                            size = Size(w, h),
                            cornerRadius = CornerRadius(3.dp.toPx()),
                        )
                        drawRoundRect(
                            color = (if (index == 0) accent else accent2).copy(alpha = .58f),
                            topLeft = Offset(p.x - w / 2f, p.y - h / 2f),
                            size = Size(w, h),
                            cornerRadius = CornerRadius(3.dp.toPx()),
                            style = Stroke(1.dp.toPx()),
                        )
                    }
                }
                "residue" -> {
                    for (index in 0 until 22) {
                        drawArc(
                            color = accent.copy(alpha = .34f),
                            startAngle = index * (360f / 22f),
                            sweepAngle = 7f,
                            useCenter = false,
                            style = Stroke(2.dp.toPx(), cap = StrokeCap.Round),
                        )
                    }
                    listOf(-150f, -115f, -80f, -45f, -10f).forEachIndexed { index, degrees ->
                        val p = at(degrees, radius * .91f)
                        val cell = 5.5.dp.toPx()
                        drawRoundRect(
                            color = (if (index % 2 == 0) accent2 else accent).copy(alpha = .72f),
                            topLeft = Offset(p.x - cell / 2f, p.y - cell / 2f),
                            size = Size(cell, cell),
                            cornerRadius = CornerRadius(1.5.dp.toPx()),
                        )
                    }
                }
                "privacy" -> {
                    for (index in 0 until 10) {
                        drawArc(
                            color = (if (index % 2 == 0) accent else accent2).copy(alpha = .46f),
                            startAngle = -88f + index * 36f,
                            sweepAngle = 23f,
                            useCenter = false,
                            style = Stroke(4.5.dp.toPx(), cap = StrokeCap.Round),
                        )
                    }
                    val tip = at(-90f, radius * .70f)
                    drawLine(
                        accent.copy(alpha = .55f),
                        Offset(tip.x - 5.dp.toPx(), tip.y),
                        Offset(tip.x, tip.y + 5.dp.toPx()),
                        1.6.dp.toPx(),
                        StrokeCap.Round,
                    )
                    drawLine(
                        accent.copy(alpha = .55f),
                        Offset(tip.x + 5.dp.toPx(), tip.y),
                        Offset(tip.x, tip.y + 5.dp.toPx()),
                        1.6.dp.toPx(),
                        StrokeCap.Round,
                    )
                }
                "deepscan" -> {
                    for (index in 0 until 3) {
                        drawCircle(
                            color = accent.copy(alpha = .14f),
                            radius = radius * (.58f + index * .11f),
                            center = centre,
                            style = Stroke(1.dp.toPx()),
                        )
                    }
                    drawLine(
                        accent.copy(alpha = .22f),
                        Offset(centre.x, centre.y - radius * .94f),
                        Offset(centre.x, centre.y - radius * .55f),
                        1.dp.toPx(),
                    )
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(Color.Transparent, accent.copy(alpha = .50f), Color.Transparent),
                            centre,
                        ),
                        startAngle = -118f,
                        sweepAngle = 52f,
                        useCenter = false,
                        style = Stroke(7.dp.toPx(), cap = StrokeCap.Round),
                    )
                    listOf(-72f, -34f, -140f).forEach { degrees ->
                        drawCircle(
                            color = accent2.copy(alpha = .85f),
                            radius = 2.4.dp.toPx(),
                            center = at(degrees, radius * .70f),
                        )
                    }
                }
                "reports" -> {
                    val heights = listOf(.42f, .62f, .48f, .78f, .55f, .88f, .60f, .72f, .50f, .82f, .46f, .66f)
                    heights.forEachIndexed { index, height ->
                        val degrees = -158f + index * (136f / (heights.size - 1))
                        drawLine(
                            color = (if (index % 2 == 0) accent else accent2).copy(alpha = .55f),
                            start = at(degrees, radius * .88f),
                            end = at(degrees, radius * (.88f + .13f * height)),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }
                }
                "appmanager" -> {
                    for (index in 0 until 14) {
                        val degrees = -168f + index * (156f / 13f)
                        drawCircle(
                            color = (if (index % 3 == 0) accent2 else accent).copy(alpha = .62f),
                            radius = (if (index % 3 == 0) 2.6.dp else 1.8.dp).toPx(),
                            center = at(degrees, radius * .92f),
                        )
                    }
                    listOf(-150f, -30f).forEachIndexed { index, degrees ->
                        val p = at(degrees, radius * .78f)
                        val cell = 9.dp.toPx()
                        drawRoundRect(
                            color = (if (index == 0) accent else accent2).copy(alpha = .30f),
                            topLeft = Offset(p.x - cell / 2f, p.y - cell / 2f),
                            size = Size(cell, cell),
                            cornerRadius = CornerRadius(2.dp.toPx()),
                            style = Stroke(1.2.dp.toPx()),
                        )
                    }
                }
                "storage" -> {
                    drawArc(
                        color = accent.copy(alpha = .22f),
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(6.dp.toPx(), cap = StrokeCap.Round),
                    )
                    drawArc(
                        brush = Brush.sweepGradient(listOf(accent, accent2, accent), centre),
                        startAngle = 135f,
                        sweepAngle = 270f * progress.coerceIn(0f, 1f),
                        useCenter = false,
                        style = Stroke(6.dp.toPx(), cap = StrokeCap.Round),
                    )
                    for (index in 0 until 5) {
                        drawCircle(
                            color = accent2.copy(alpha = .55f),
                            radius = 1.5.dp.toPx(),
                            center = at(135f + index * 67.5f, radius * .84f),
                        )
                    }
                }
                "battery" -> {
                    val filled = (progress.coerceIn(0f, 1f) * 12f).roundToInt()
                    for (index in 0 until 12) {
                        val degrees = -150f + index * (120f / 11f)
                        drawLine(
                            color = if (index < filled) accent else accent2.copy(alpha = .20f),
                            start = at(degrees, radius * .88f),
                            end = at(degrees, radius * .96f),
                            strokeWidth = 2.4.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }
                    val top = at(-90f, radius * .90f)
                    drawRoundRect(
                        color = accent2.copy(alpha = .55f),
                        topLeft = Offset(top.x - 4.dp.toPx(), top.y - 2.5.dp.toPx()),
                        size = Size(8.dp.toPx(), 5.dp.toPx()),
                        cornerRadius = CornerRadius(1.5.dp.toPx()),
                        style = Stroke(1.dp.toPx()),
                    )
                }
                "ai" -> {
                    val nodes = listOf(-156f, -120f, -84f, -48f, -12f).map { it to at(it, radius * .89f) }
                    nodes.zipWithNext().forEach { (a, b) ->
                        drawLine(accent.copy(alpha = .32f), a.second, b.second, 1.2.dp.toPx())
                    }
                    nodes.forEachIndexed { index, (_, p) ->
                        drawCircle(
                            color = (if (index % 2 == 0) accent else accent2).copy(alpha = .78f),
                            radius = (if (index == 2) 3.4.dp else 2.4.dp).toPx(),
                            center = p,
                        )
                    }
                    drawCircle(
                        color = accent.copy(alpha = .30f),
                        radius = radius * .80f,
                        center = centre,
                        style = Stroke(1.dp.toPx()),
                    )
                }
                else -> {
                    listOf(-138f, -108f, -78f, -48f, -18f).forEachIndexed { index, degrees ->
                        drawCircle(
                            color = (if (index % 2 == 0) accent2 else accent).copy(alpha = .72f),
                            radius = (if (index == 2) 2.8.dp else 1.8.dp).toPx(),
                            center = at(degrees, radius * .92f),
                        )
                    }
                    drawArc(
                        color = accent2.copy(alpha = .30f),
                        startAngle = -150f,
                        sweepAngle = 120f,
                        useCenter = false,
                        style = Stroke(1.dp.toPx(), cap = StrokeCap.Round),
                    )
                }
            }

            drawArc(
                color = Color.White.copy(alpha = .20f),
                startAngle = 245f,
                sweepAngle = 70f,
                useCenter = false,
                style = Stroke(1.dp.toPx()),
            )
        }
        Box(
            Modifier
                .size(136.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFF25115C), Color(0xFF0F082B))))
                .border(1.dp, spec.accent.copy(alpha = .44f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                V2RasterTile(image = SectionArt.emblem(spec.key), size = 46.dp, accent = spec.accent)
                Spacer(Modifier.height(3.dp))
                KText(spec.title, size = 12.sp, weight = FontWeight.Bold, maxLines = 1)
                KText(spec.english, size = 8.sp, color = V2Soft, maxLines = 1)
                Spacer(Modifier.height(3.dp))
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                listOf(spec.accent2.copy(alpha = .20f), spec.accent.copy(alpha = .20f))
                            )
                        )
                        .border(1.dp, spec.accent.copy(alpha = .28f), RoundedCornerShape(50))
                        .padding(horizontal = 9.dp, vertical = 2.dp)
                ) {
                    KText(status, size = 7.5.sp, color = V2Cyan, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun SectionHomeTile(item: ServiceSubItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    V2LuxuryServiceTile(item, modifier, onClick)
}

/* ------------------------------------------------------------------ *
 * Per-section readings — every value comes from DeviceFacts
 * ------------------------------------------------------------------ */

internal data class SectionLine(
    val icon: ImageVector,
    val label: String,
    val value: String,
    val progress: Float,
    val accent: Color,
)

internal data class SectionSide(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accent: Color,
    val route: String,
)

internal data class SectionStat(
    val icon: ImageVector,
    val value: String,
    val label: String,
    val accent: Color,
)

internal data class SectionIdentity(
    val gaugeLabel: String,
    val gaugePercent: Int,
    val lines: List<SectionLine>,
    val left: SectionSide,
    val right: SectionSide,
    val orbStatus: String,
    val stats: List<SectionStat>,
)

private fun ratio(part: Long, whole: Long): Float =
    if (whole <= 0L) 0f else (part.toFloat() / whole.toFloat()).coerceIn(0f, 1f)

private fun ratioInt(part: Int, whole: Int): Float =
    if (whole <= 0) 0f else (part.toFloat() / whole.toFloat()).coerceIn(0f, 1f)

/** Counts the Google-branded packages actually visible through PackageManager. */
internal fun googleAppCount(facts: DeviceFacts): Int =
    facts.installedApps.count { app ->
        app.packageName.startsWith("com.google.") || app.packageName.startsWith("com.android.vending")
    }

internal fun sectionIdentity(key: String, facts: DeviceFacts): SectionIdentity {
    val storage = facts.storage
    val battery = facts.battery
    val media = facts.media
    val mediaBytes = media.imagesBytes + media.videosBytes
    val reclaimable = media.duplicateGroups.sumOf { it.reclaimableBytes }
    val systemCount = facts.installedCount - facts.userAppCount
    val enabledCount = facts.installedApps.count { it.isEnabled }
    val live = facts.computedAt > 0L
    val batteryValue = battery.levelPercent?.let { "$it%" } ?: "غير متاح"
    val mediaTotal = media.images + media.videos
    fun mediaValue(value: String): String = when {
        !live -> "جاري القراءة"
        !media.permissionGranted -> "يحتاج إذن"
        else -> value
    }

    return when (key) {
        "duplicates" -> SectionIdentity(
            gaugeLabel = "نسخ زائدة",
            gaugePercent = (
                ratioInt(
                    (media.duplicateCandidates - media.duplicateGroups.size).coerceAtLeast(0),
                    media.duplicateCandidates,
                ) * 100
                ).toInt(),
            lines = listOf(
                SectionLine(Icons.Rounded.ContentCopy, "مجموعات التكرار", mediaValue("${media.duplicateGroups.size} مجموعة"), ratioInt(media.duplicateGroups.size, 50), V2Cyan),
                SectionLine(Icons.Rounded.Image, "صور", mediaValue(formatCount(media.images)), ratioInt(media.images, mediaTotal.coerceAtLeast(1)), V2Violet),
                SectionLine(Icons.Rounded.Videocam, "فيديو", mediaValue(formatCount(media.videos)), ratioInt(media.videos, mediaTotal.coerceAtLeast(1)), V2Pink),
            ),
            left = SectionSide("تجميع النسخ", "Duplicate Groups", Icons.Rounded.ContentCopy, V2Cyan, "dup-groups"),
            right = SectionSide("تأكيد العملية", "Safe Confirm", Icons.Rounded.Verified, V2Cyan, "dup-confirm"),
            orbStatus = if (!media.permissionGranted) "صلاحية الوسائط غير ممنوحة" else "${media.duplicateCandidates} نسخة مرشحة",
            stats = listOf(
                SectionStat(Icons.Rounded.ContentCopy, mediaValue("${media.duplicateGroups.size}"), "مجموعة تكرار", V2Cyan),
                SectionStat(Icons.Rounded.FileCopy, mediaValue("${media.duplicateCandidates}"), "نسخة مرشحة", V2Pink),
                SectionStat(Icons.Rounded.DeleteSweep, mediaValue(formatBytes(reclaimable)), "قابل للاستعادة", V2Green),
                SectionStat(Icons.Rounded.PermMedia, mediaValue(formatBytes(mediaBytes)), "حجم الوسائط", V2Violet),
            ),
        )
        "residue" -> SectionIdentity(
            gaugeLabel = "تطبيقات المستخدم",
            gaugePercent = (ratioInt(facts.userAppCount, facts.installedCount) * 100).toInt(),
            lines = listOf(
                SectionLine(Icons.Rounded.Apps, "تطبيقات مثبتة", formatCount(facts.installedCount), if (live) 1f else 0f, V2Cyan),
                SectionLine(Icons.Rounded.Android, "تطبيقات النظام", formatCount(systemCount), ratioInt(systemCount, facts.installedCount), V2Violet),
                SectionLine(Icons.Rounded.CleaningServices, "كاش KNOUX", formatBytes(facts.ownCacheBytes), ratio(facts.ownCacheBytes, storage.usedBytes), V2Pink),
            ),
            left = SectionSide("فحص البقايا", "Residue Scan", Icons.Rounded.ManageSearch, V2Cyan, "res-scan"),
            right = SectionSide("استثناء الملفات", "Exclusions", Icons.Rounded.Shield, V2Cyan, "res-exclusions"),
            orbStatus = if (live) "${formatCount(facts.installedCount)} تطبيق مقروء" else "جاري القراءة",
            stats = listOf(
                SectionStat(Icons.Rounded.Apps, formatCount(facts.installedCount), "تطبيق مثبت", V2Cyan),
                SectionStat(Icons.Rounded.Person, formatCount(facts.userAppCount), "تطبيق مستخدم", V2Pink),
                SectionStat(Icons.Rounded.CleaningServices, formatBytes(facts.ownCacheBytes), "كاش KNOUX", V2Green),
                SectionStat(Icons.Rounded.Storage, formatBytes(storage.freeBytes), "مساحة متاحة", V2Violet),
            ),
        )
        "privacy" -> SectionIdentity(
            gaugeLabel = "سلامة الأذونات",
            gaugePercent = 100 - (ratioInt(facts.sensitiveAppCount, facts.installedCount) * 100).toInt(),
            lines = listOf(
                SectionLine(Icons.Rounded.Shield, "أذونات حساسة", "${formatCount(facts.sensitiveAppCount)} تطبيق", ratioInt(facts.sensitiveAppCount, facts.installedCount), V2Pink),
                SectionLine(Icons.Rounded.Lock, "مجموعات الأذونات", "${facts.privacyGroups.size} مجموعة", ratioInt(facts.privacyGroups.size, 12), V2Violet),
                SectionLine(Icons.Rounded.Visibility, "وصول الاستخدام", if (facts.usageAccessGranted) "ممنوح" else "غير ممنوح", if (facts.usageAccessGranted) 1f else 0f, V2Cyan),
            ),
            left = SectionSide("مجموعات الأذونات", "Permission Groups", Icons.Rounded.Lock, V2Cyan, "prv-permissions"),
            right = SectionSide("تقييم التطبيقات", "App Risk", Icons.Rounded.Security, V2Cyan, "prv-app-risk"),
            orbStatus = if (live) "${formatCount(facts.sensitiveAppCount)} تطبيق حساس" else "جاري القراءة",
            stats = listOf(
                SectionStat(Icons.Rounded.Shield, formatCount(facts.sensitiveAppCount), "تطبيق حساس", V2Violet),
                SectionStat(Icons.Rounded.Lock, "${facts.privacyGroups.size}", "مجموعة أذونات", V2Violet),
                SectionStat(Icons.Rounded.Visibility, if (facts.usageAccessGranted) "ممنوح" else "لا", "وصول الاستخدام", V2Cyan),
                SectionStat(Icons.Rounded.Apps, formatCount(facts.installedCount), "تطبيق مثبت", V2Pink),
            ),
        )
        "deepscan" -> SectionIdentity(
            gaugeLabel = "تقدير الجهاز",
            gaugePercent = facts.healthEstimate,
            lines = listOf(
                SectionLine(Icons.Rounded.Storage, "التخزين", "${formatBytes(storage.usedBytes)} / ${formatBytes(storage.totalBytes)}", storage.usedFraction, V2Cyan),
                SectionLine(Icons.Rounded.Bolt, "البطارية", batteryValue, (battery.levelPercent ?: 0) / 100f, V2Green),
                SectionLine(Icons.Rounded.Shield, "أذونات حساسة", "${formatCount(facts.sensitiveAppCount)} تطبيق", ratioInt(facts.sensitiveAppCount, facts.installedCount), V2Pink),
            ),
            left = SectionSide("نطاق الفحص", "Scan Scope", Icons.Rounded.Tune, V2Cyan, "dsn-scope"),
            right = SectionSide("نتائج الفحص", "Findings", Icons.Rounded.FactCheck, V2Cyan, "dsn-findings"),
            orbStatus = if (facts.healthEstimate > 0) "${facts.healthEstimate}% جاهز" else "قراءة الجهاز…",
            stats = listOf(
                SectionStat(Icons.Rounded.Bolt, batteryValue, "مستوى البطارية", V2Green),
                SectionStat(Icons.Rounded.Apps, formatCount(facts.installedCount), "تطبيق مثبت", V2Pink),
                SectionStat(Icons.Rounded.CleaningServices, formatBytes(facts.ownCacheBytes), "كاش KNOUX", V2Cyan),
                SectionStat(Icons.Rounded.Storage, formatBytes(storage.freeBytes), "مساحة متاحة", V2Violet),
            ),
        )
        "reports" -> SectionIdentity(
            gaugeLabel = "تقدير الجهاز",
            gaugePercent = facts.healthEstimate,
            lines = listOf(
                SectionLine(Icons.Rounded.Storage, "مساحة مستخدمة", formatBytes(storage.usedBytes), storage.usedFraction, V2Cyan),
                SectionLine(Icons.Rounded.Apps, "تطبيقات مثبتة", formatCount(facts.installedCount), if (live) 1f else 0f, V2Violet),
                SectionLine(Icons.Rounded.Bolt, "البطارية", batteryValue, (battery.levelPercent ?: 0) / 100f, V2Green),
            ),
            left = SectionSide("اتجاهات المساحة", "Storage Trends", Icons.Rounded.TrendingUp, V2Cyan, "rpt-trends"),
            right = SectionSide("سجل الفحوص", "Scan Log", Icons.Rounded.Timeline, V2Cyan, "rpt-scans"),
            orbStatus = if (live) "قراءة مباشرة · ${formatBytes(storage.freeBytes)} متاحة" else "جاري القراءة",
            stats = listOf(
                SectionStat(Icons.Rounded.Storage, formatBytes(storage.usedBytes), "مساحة مستخدمة", V2Cyan),
                SectionStat(Icons.Rounded.Apps, formatCount(facts.installedCount), "تطبيق مثبت", V2Pink),
                SectionStat(Icons.Rounded.Shield, formatCount(facts.sensitiveAppCount), "تطبيق حساس", V2Violet),
                SectionStat(Icons.Rounded.Bolt, batteryValue, "مستوى البطارية", V2Green),
            ),
        )
        "appmanager" -> SectionIdentity(
            gaugeLabel = "تطبيقات مفعّلة",
            gaugePercent = (ratioInt(enabledCount, facts.installedCount) * 100).toInt(),
            lines = listOf(
                SectionLine(Icons.Rounded.Apps, "تطبيقات المستخدم", formatCount(facts.userAppCount), ratioInt(facts.userAppCount, facts.installedCount), V2Cyan),
                SectionLine(Icons.Rounded.Android, "تطبيقات النظام", formatCount(systemCount), ratioInt(systemCount, facts.installedCount), V2Violet),
                SectionLine(Icons.Rounded.ToggleOn, "مفعّلة", formatCount(enabledCount), ratioInt(enabledCount, facts.installedCount), V2Green),
            ),
            left = SectionSide("قائمة التطبيقات", "Installed Apps", Icons.Rounded.Apps, V2Cyan, "apm-installed"),
            right = SectionSide("إزالة التطبيقات", "Uninstall", Icons.Rounded.DeleteOutline, V2Cyan, "apm-uninstall"),
            orbStatus = if (live) "${formatCount(facts.installedCount)} تطبيق" else "جاري القراءة",
            stats = listOf(
                SectionStat(Icons.Rounded.Apps, formatCount(facts.installedCount), "تطبيق مثبت", V2Cyan),
                SectionStat(Icons.Rounded.Person, formatCount(facts.userAppCount), "تطبيق مستخدم", V2Pink),
                SectionStat(Icons.Rounded.Android, formatCount(systemCount), "تطبيق نظام", V2Violet),
                SectionStat(Icons.Rounded.Shield, formatCount(facts.sensitiveAppCount), "تطبيق حساس", V2Violet),
            ),
        )
        "storage" -> SectionIdentity(
            gaugeLabel = "مساحة مستخدمة",
            gaugePercent = (storage.usedFraction * 100).toInt(),
            lines = listOf(
                SectionLine(Icons.Rounded.Storage, "مستخدم / كلي", "${formatBytes(storage.usedBytes)} / ${formatBytes(storage.totalBytes)}", storage.usedFraction, V2Cyan),
                SectionLine(Icons.Rounded.FolderOpen, "متاح", formatBytes(storage.freeBytes), 1f - storage.usedFraction, V2Green),
                SectionLine(Icons.Rounded.PermMedia, "وسائط", mediaValue(formatBytes(mediaBytes)), ratio(mediaBytes, storage.usedBytes), V2Pink),
            ),
            left = SectionSide("توزيع الملفات", "Categories", Icons.Rounded.PieChart, V2Cyan, "sto-categories"),
            right = SectionSide("فرز حسب الحجم", "Sort by Size", Icons.Rounded.Sort, V2Cyan, "sto-sorting"),
            orbStatus = if (live) "${formatBytes(storage.freeBytes)} متاحة" else "جاري القراءة",
            stats = listOf(
                SectionStat(Icons.Rounded.Storage, formatBytes(storage.totalBytes), "سعة التخزين", V2Cyan),
                SectionStat(Icons.Rounded.DataUsage, formatBytes(storage.usedBytes), "مساحة مستخدمة", V2Pink),
                SectionStat(Icons.Rounded.FolderOpen, formatBytes(storage.freeBytes), "مساحة متاحة", V2Green),
                SectionStat(Icons.Rounded.PermMedia, mediaValue(formatBytes(mediaBytes)), "حجم الوسائط", V2Violet),
            ),
        )
        "battery" -> SectionIdentity(
            gaugeLabel = "مستوى البطارية",
            gaugePercent = battery.levelPercent ?: 0,
            lines = listOf(
                SectionLine(Icons.Rounded.Bolt, "المستوى", batteryValue, (battery.levelPercent ?: 0) / 100f, V2Green),
                SectionLine(Icons.Rounded.Thermostat, "الحرارة", battery.temperatureC?.let { "$it°" } ?: "غير متاح", ((battery.temperatureC ?: 0f) / 60f).coerceIn(0f, 1f), V2Pink),
                SectionLine(Icons.Rounded.BatteryChargingFull, "الحالة", battery.statusLabel, if (battery.charging) 1f else .5f, V2Cyan),
            ),
            left = SectionSide("قراءات البطارية", "Live Readings", Icons.Rounded.Speed, V2Cyan, "bat-readings"),
            right = SectionSide("مصادر الاستهلاك", "Power Sources", Icons.Rounded.ElectricalServices, V2Cyan, "bat-source"),
            orbStatus = battery.levelPercent?.let { "$it% · ${battery.statusLabel}" } ?: "قراءة البطارية…",
            stats = listOf(
                SectionStat(Icons.Rounded.Bolt, batteryValue, "مستوى البطارية", V2Green),
                SectionStat(Icons.Rounded.Thermostat, battery.temperatureC?.let { "$it°" } ?: "—", "حرارة البطارية", V2Pink),
                SectionStat(Icons.Rounded.ElectricalServices, battery.voltageMv?.let { "${it}mV" } ?: "—", "الجهد", V2Cyan),
                SectionStat(Icons.Rounded.BatteryChargingFull, battery.healthLabel, "صحة البطارية", V2Violet),
            ),
        )
        "ai" -> SectionIdentity(
            gaugeLabel = "تقدير التحسين",
            gaugePercent = facts.healthEstimate,
            lines = listOf(
                SectionLine(Icons.Rounded.AutoAwesome, "توصيات مبنية على القراءة", if (live) "جاهزة" else "قيد القراءة", if (live) 1f else 0f, V2Cyan),
                SectionLine(Icons.Rounded.Storage, "ضغط التخزين", "${formatBytes(storage.usedBytes)} مستخدم", storage.usedFraction, V2Cyan),
                SectionLine(Icons.Rounded.Bolt, "طاقة الجهاز", batteryValue, (battery.levelPercent ?: 0) / 100f, V2Green),
            ),
            left = SectionSide("أسباب التوصيات", "Why this advice", Icons.Rounded.Psychology, V2Cyan, "ai-reasons"),
            right = SectionSide("تنفيذ بموافقة", "Approved Actions", Icons.Rounded.PlayCircle, V2Cyan, "ai-execute"),
            orbStatus = if (facts.healthEstimate > 0) "${facts.healthEstimate}% جاهز" else "قراءة الجهاز…",
            stats = listOf(
                SectionStat(Icons.Rounded.AutoAwesome, if (facts.healthEstimate > 0) "${facts.healthEstimate}%" else "—", "تقدير التحسين", V2Cyan),
                SectionStat(Icons.Rounded.Apps, formatCount(facts.installedCount), "تطبيق مثبت", V2Pink),
                SectionStat(Icons.Rounded.Storage, formatBytes(storage.freeBytes), "مساحة متاحة", V2Cyan),
                SectionStat(Icons.Rounded.Bolt, batteryValue, "مستوى البطارية", V2Green),
            ),
        )
        "developer" -> SectionIdentity(
            gaugeLabel = "معلومات البناء",
            gaugePercent = 100,
            lines = listOf(
                SectionLine(Icons.Rounded.Build, "إصدار النظام", Build.VERSION.RELEASE, 1f, V2Cyan),
                SectionLine(Icons.Rounded.Android, "مستوى API", Build.VERSION.SDK_INT.toString(), ratioInt(Build.VERSION.SDK_INT, 36), V2Violet),
                SectionLine(Icons.Rounded.Memory, "تطبيقات مرئية", formatCount(facts.installedCount), if (live) 1f else 0f, V2Pink),
            ),
            left = SectionSide("تفاصيل الجهاز", "Build Details", Icons.Rounded.Build, V2Cyan, "dev-build"),
            right = SectionSide("سجل التشغيل", "Runtime Log", Icons.Rounded.Timeline, V2Cyan, "dev-log"),
            orbStatus = if (live) "Build ${Build.VERSION.SDK_INT} · ${formatCount(facts.installedCount)} تطبيق" else "جاري القراءة",
            stats = listOf(
                SectionStat(Icons.Rounded.Android, Build.VERSION.RELEASE, "إصدار النظام", V2Cyan),
                SectionStat(Icons.Rounded.Build, Build.VERSION.SDK_INT.toString(), "مستوى API", V2Violet),
                SectionStat(Icons.Rounded.Apps, formatCount(facts.installedCount), "تطبيق مثبت", V2Pink),
                SectionStat(Icons.Rounded.Memory, formatBytes(facts.ownCacheBytes), "كاش KNOUX", V2Green),
            ),
        )
        "googleservices" -> SectionIdentity(
            gaugeLabel = "خدمات جوجل",
            gaugePercent = (ratioInt(googleAppCount(facts), facts.installedCount.coerceAtLeast(1)) * 100).toInt(),
            lines = listOf(
                SectionLine(Icons.Rounded.AccountCircle, "تطبيقات Google", formatCount(googleAppCount(facts)), ratioInt(googleAppCount(facts), facts.installedCount), V2Cyan),
                SectionLine(Icons.Rounded.Notifications, "الإشعارات", if (live) "نشطة" else "قيد القراءة", if (live) 1f else 0f, V2Violet),
                SectionLine(Icons.Rounded.Shield, "Play Protect", if (live) "من.settings" else "—", if (live) .5f else 0f, V2Pink),
            ),
            left = SectionSide("تطبيقات Google", "Google Apps", Icons.Rounded.AccountCircle, V2Cyan, "gsc-apps"),
            right = SectionSide("إعدادات Play", "Play Settings", Icons.Rounded.Settings, V2Cyan, "gsc-settings"),
            orbStatus = if (live) "${formatCount(googleAppCount(facts))} تطبيق Google" else "جاري القراءة",
            stats = listOf(
                SectionStat(Icons.Rounded.AccountCircle, formatCount(googleAppCount(facts)), "تطبيق Google", V2Cyan),
                SectionStat(Icons.Rounded.Notifications, if (live) "مفعّل" else "—", "الإشعارات", V2Violet),
                SectionStat(Icons.Rounded.Apps, formatCount(facts.installedCount), "تطبيق مثبت", V2Pink),
                SectionStat(Icons.Rounded.Storage, formatBytes(storage.freeBytes), "مساحة متاحة", V2Green),
            ),
        )
        "security" -> SectionIdentity(
            gaugeLabel = "حالة القفل",
            gaugePercent = 100,
            lines = listOf(
                SectionLine(Icons.Rounded.Lock, "قفل الشاشة", if (live) "متاح" else "—", if (live) 1f else 0f, V2Cyan),
                SectionLine(Icons.Rounded.Fingerprint, "المصادقة", "إعدادات النظام", .5f, V2Violet),
                SectionLine(Icons.Rounded.Shield, "أذونات حساسة", formatCount(facts.sensitiveAppCount), ratioInt(facts.sensitiveAppCount, facts.installedCount), V2Pink),
            ),
            left = SectionSide("قفل الجهاز", "Screen Lock", Icons.Rounded.Lock, V2Cyan, "sec-lock"),
            right = SectionSide("حسابات الجهاز", "Device Accounts", Icons.Rounded.Person, V2Cyan, "sec-accounts"),
            orbStatus = if (live) "${formatCount(facts.sensitiveAppCount)} تطبيق حساس" else "جاري القراءة",
            stats = listOf(
                SectionStat(Icons.Rounded.Lock, if (live) "متاح" else "—", "قفل الشاشة", V2Cyan),
                SectionStat(Icons.Rounded.Shield, formatCount(facts.sensitiveAppCount), "تطبيق حساس", V2Violet),
                SectionStat(Icons.Rounded.Apps, formatCount(facts.installedCount), "تطبيق مثبت", V2Pink),
                SectionStat(Icons.Rounded.Storage, formatBytes(storage.freeBytes), "مساحة متاحة", V2Green),
            ),
        )
        "backup" -> SectionIdentity(
            gaugeLabel = "سعة التخزين",
            gaugePercent = (storage.usedFraction * 100).toInt(),
            lines = listOf(
                SectionLine(Icons.Rounded.FolderOpen, "متاح للنسخ", formatBytes(storage.freeBytes), 1f - storage.usedFraction, V2Cyan),
                SectionLine(Icons.Rounded.Storage, "سعة الجهاز", formatBytes(storage.totalBytes), 1f, V2Violet),
                SectionLine(Icons.Rounded.Description, "ملفات KNOUX", "${formatCount(facts.ownCacheFiles)} ملف", ratioInt(facts.ownCacheFiles, 1000), V2Pink),
            ),
            left = SectionSide("اختيار مجلد", "Pick Folder", Icons.Rounded.FolderOpen, V2Cyan, "bkp-pick"),
            right = SectionSide("سجل النسخ", "Backup Log", Icons.Rounded.History, V2Cyan, "bkp-log"),
            orbStatus = if (live) "${formatBytes(storage.freeBytes)} متاحة" else "جاري القراءة",
            stats = listOf(
                SectionStat(Icons.Rounded.FolderOpen, formatBytes(storage.freeBytes), "مساحة متاحة", V2Cyan),
                SectionStat(Icons.Rounded.Storage, formatBytes(storage.totalBytes), "سعة التخزين", V2Pink),
                SectionStat(Icons.Rounded.Description, formatCount(facts.ownCacheFiles), "ملف KNOUX", V2Violet),
                SectionStat(Icons.Rounded.Apps, formatCount(facts.installedCount), "تطبيق مثبت", V2Green),
            ),
        )
        else -> SectionIdentity(
            gaugeLabel = "مساحة متاحة",
            gaugePercent = ((1f - storage.usedFraction) * 100).toInt(),
            lines = listOf(
                SectionLine(Icons.Rounded.CleaningServices, "كاش KNOUX", formatBytes(facts.ownCacheBytes), ratio(facts.ownCacheBytes, storage.usedBytes), V2Cyan),
                SectionLine(Icons.Rounded.Description, "ملفات KNOUX المؤقتة", "${formatCount(facts.ownCacheFiles)} ملف", ratioInt(facts.ownCacheFiles, 1000), V2Violet),
                SectionLine(Icons.Rounded.PermMedia, "وسائط للمراجعة", mediaValue(formatCount(mediaTotal)), ratioInt(mediaTotal, 2000), V2Pink),
            ),
            left = SectionSide("معاينة المرشحات", "Candidate Preview", Icons.Rounded.FilterAlt, V2Cyan, "clean-preview"),
            right = SectionSide("تأكيد الحذف", "Safe Delete", Icons.Rounded.Verified, V2Cyan, "clean-confirm"),
            orbStatus = when {
                !live -> "جاري القراءة"
                !media.permissionGranted -> "جاهز · كاش ${formatBytes(facts.ownCacheBytes)}"
                else -> "${formatCount(mediaTotal)} وسيط · كاش ${formatBytes(facts.ownCacheBytes)}"
            },
            stats = listOf(
                SectionStat(Icons.Rounded.Bolt, batteryValue, "مستوى البطارية", V2Green),
                SectionStat(Icons.Rounded.Apps, formatCount(facts.installedCount), "تطبيق مثبت", V2Pink),
                SectionStat(Icons.Rounded.CleaningServices, formatBytes(facts.ownCacheBytes), "كاش KNOUX", V2Cyan),
                SectionStat(Icons.Rounded.Storage, formatBytes(storage.freeBytes), "مساحة متاحة", V2Violet),
            ),
        )
    }
}
