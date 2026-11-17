package com.knoux.ai.ui.v2

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
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DataUsage
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.ElectricalServices
import androidx.compose.material.icons.rounded.FactCheck
import androidx.compose.material.icons.rounded.FileCopy
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.ManageSearch
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

/* ------------------------------------------------------------------ *
 * Section tool catalog — shared model
 * ------------------------------------------------------------------ */

internal data class ToolRow(
    val icon: ImageVector,
    val label: String,
    val value: String,
    val accent: Color,
)

internal enum class ToolList { NONE, FILES, GROUPS, APPS, PERMISSIONS, HISTORY }

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
)

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

    fun items(section: String): List<ServiceSubItem> = defs
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
        else -> V2Violet
    }

    fun bannerSpec(def: SectionToolDef): KnouxServiceSpec = KnouxServiceSpec(
        key = def.route,
        order = def.order,
        title = def.title,
        english = def.english,
        tagline = def.tagline,
        accent = def.accent,
        accent2 = accent2For(def.section),
        glyph = def.icon,
        chips = listOf("قراءة مباشرة", "بدون أرقام تجريبية"),
        subItems = emptyList(),
        pro = def.proOnly,
    )
}

/* ------------------------------------------------------------------ *
 * Shared screen for the nine remaining sections
 * ------------------------------------------------------------------ */

@Composable
fun V2SectionToolScreen(route: String, onBack: () -> Unit, onPro: () -> Unit) {
    val def = SectionTools.byRoute(route) ?: return
    val context = LocalContext.current
    val navigate = LocalServiceNavigate.current
    val (refresh, bump) = rememberRefreshCounter()
    val facts = rememberDeviceFacts(refresh)
    val history = remember(refresh) { readScanHistory(context) }
    val rows = def.rows(facts, history)

    var query by remember { mutableStateOf("") }
    var appFilter by remember { mutableIntStateOf(0) }
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
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    ServiceBanner(
                        spec = SectionTools.bannerSpec(def),
                        status = if (facts.computedAt > 0L) "قراءة مباشرة" else "جاري القراءة",
                        statusColor = if (facts.computedAt > 0L) V2Green else V2Amber,
                        metric = def.metric(facts, history),
                    )
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
                if (rows.isNotEmpty()) {
                    item { V2SectionLabel("قراءة هذا الهاتف", trailing = if (facts.computedAt > 0L) "مباشر" else "جاري") }
                    item {
                        V2GlassCard(
                            Modifier.fillMaxWidth(),
                            padding = 12.dp,
                            radius = 20.dp,
                            glow = def.accent,
                            borderAlpha = .32f,
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                                rows.forEach { row -> ToolRowView(row) }
                            }
                        }
                    }
                }
                toolsList(
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
            }
            V2GradientButton(
                "تحديث القراءة",
                bump,
                icon = Icons.Rounded.Refresh,
                trailingChevron = true,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
            )
        }
    }
}

/* ----------------------------- rows ----------------------------- */

@Composable
private fun ToolRowView(row: ToolRow) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(row.icon, null, tint = row.accent, modifier = Modifier.size(17.dp))
        Spacer(Modifier.width(7.dp))
        KText(
            row.label,
            size = 8.6.sp,
            color = V2Soft,
            modifier = Modifier.weight(1f),
            textDirection = TextDirection.Rtl,
        )
        KText(
            row.value,
            size = 9.sp,
            weight = FontWeight.Bold,
            color = row.accent,
            align = TextAlign.End,
            maxLines = 2,
            textDirection = TextDirection.Rtl,
        )
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
        ToolList.FILES -> {
            val base = facts.media.largestFiles.filter { file ->
                (query.isBlank() || file.name.contains(query, true)) &&
                    (def.behavior != ToolBehavior.SIZE_FILTER_MEDIA ||
                        file.sizeBytes >= minMb.toLong() * 1024L * 1024L)
            }
            val files = when (sortMode) {
                1 -> base.sortedBy { it.name }
                2 -> base.sortedByDescending { it.isVideo }
                else -> base.sortedByDescending { it.sizeBytes }
            }
            if (files.isEmpty()) {
                item {
                    ServiceCapabilityCard(
                        title = if (facts.media.permissionGranted) "لا ملفات وسائط لعرضها" else "صلاحية الوسائط مطلوبة",
                        body = if (facts.media.permissionGranted) {
                            "فُحصت المكتبة المصرح بها ولم تُعد MediaStore أي ملفات قابلة للعرض."
                        } else {
                            "امنح READ_EXTERNAL_STORAGE ثم حدّث القراءة لعرض ملفاتك الحقيقية."
                        },
                        state = if (facts.media.permissionGranted) ServiceState.LIVE else ServiceState.PERMISSION,
                    )
                }
            } else {
                item { V2SectionLabel("الملفات", trailing = "${formatCount(files.size)}") }
                files.take(12).forEach { file ->
                    item {
                        ToolFileRow(
                            title = file.name,
                            subtitle = file.bucket ?: if (file.isVideo) "فيديو" else "صورة",
                            size = formatBytes(file.sizeBytes),
                            accent = if (file.isVideo) V2Pink else V2Violet,
                        )
                    }
                }
            }
        }
        ToolList.GROUPS -> {
            if (facts.media.duplicateGroups.isEmpty()) {
                item {
                    ServiceCapabilityCard(
                        title = "لا مجموعات تكرار حالية",
                        body = "لم تظهر نسخ متطابقة في نطاق الوسائط المقروءة.",
                        state = ServiceState.LIVE,
                    )
                }
            } else {
                item { V2SectionLabel("المجموعات", trailing = "${facts.media.duplicateGroups.size}") }
                facts.media.duplicateGroups.take(10).forEach { group ->
                    item {
                        ToolFileRow(
                            title = group.name,
                            subtitle = "${group.count} نسخة · ${formatBytes(group.sizeBytes)} لكل ملف",
                            size = formatBytes(group.reclaimableBytes),
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
                        ToolPermissionRow(
                            label = group.label,
                            apps = group.appCount,
                            granted = group.grantedCount,
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
                visible.take(14).forEach { entry ->
                    item {
                        V2GlassCard(
                            Modifier.fillMaxWidth(),
                            padding = 10.dp,
                            radius = 16.dp,
                            glow = V2Green,
                            borderAlpha = .24f,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                V2IconTile(Icons.Rounded.Refresh, V2Green, size = 33.dp, iconSize = 16.dp)
                                Spacer(Modifier.width(8.dp))
                                Column(Modifier.weight(1f)) {
                                    KText(formatScanTime(entry.timestamp), size = 9.2.sp, weight = FontWeight.Bold)
                                    KText(
                                        "${formatCount(entry.appsScanned)} تطبيق · ${formatBytes(entry.storageUsedBytes)} مستخدم",
                                        size = 7.2.sp,
                                        color = V2Soft,
                                    )
                                }
                                KText(formatBytes(entry.cacheBytes), size = 8.4.sp, weight = FontWeight.Bold, color = V2Pink)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolFileRow(title: String, subtitle: String, size: String, accent: Color) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 10.dp, radius = 16.dp, glow = accent, borderAlpha = .25f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(Icons.Rounded.Search, accent, size = 33.dp, iconSize = 16.dp)
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                KText(title, size = 8.8.sp, weight = FontWeight.SemiBold, maxLines = 1)
                KText(subtitle, size = 7.sp, color = V2Soft, maxLines = 1)
            }
            KText(size, size = 8.4.sp, weight = FontWeight.Bold, color = accent)
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

@Composable
private fun ToolPermissionRow(label: String, apps: Int, granted: Int, accent: Color) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 10.dp, radius = 16.dp, glow = accent, borderAlpha = .24f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(Icons.Rounded.Settings, accent, size = 33.dp, iconSize = 16.dp)
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                KText(label, size = 8.8.sp, weight = FontWeight.Bold, maxLines = 1)
                KText("$granted ممنوحة من $apps تطبيقًا", size = 7.sp, color = V2Soft)
            }
            KText("$apps", size = 9.sp, weight = FontWeight.Bold, color = accent)
        }
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
                    "هذه الشاشة تعرض قراءة حقيقية فقط. لا يوجد زر ينفّذ تغييرًا على النظام من دون موافقتك.",
                    size = 7.6.sp,
                    lineHeight = 11.sp,
                    color = V2Soft,
                )
            }
        }
        return
    }
    V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 18.dp, glow = def.accent, borderAlpha = .30f) {
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

private fun MediaFacts.duplicatesNote(): String =
    if (!permissionGranted) "صلاحية الوسائط غير ممنوحة بعد." else ""

/* ------------------------------------------------------------------ *
 * Section hubs — 10 sections × 10 independently routed services
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
    )

    val routes: List<String> get() = entries.map { "hub-${it.key}" }

    fun byRoute(route: String): Entry? = entries.firstOrNull { "hub-${it.key}" == route }

    fun byKey(key: String): Entry? = entries.firstOrNull { it.key == key }
}

/**
 * A section hub is the Home screen, re-cut for one section: the same stag
 * header, the same title lockup, the same health panel, the same centerpiece
 * orb with two flanking action cards, the same 5+5 service rhythm, the same
 * Pro banner and the same live stat strip — only the emblem, the services and
 * the readings change. Every number below is read from this device.
 */
@Composable
fun V2SectionHubScreen(route: String, onBack: () -> Unit, onPro: () -> Unit) {
    val entry = SectionHub.byRoute(route) ?: return
    val navigate = LocalServiceNavigate.current
    val facts = rememberDeviceFacts()
    val proActive = LocalProActive.current
    val spec = entry.spec
    val identity = remember(facts.computedAt, entry.key) { sectionIdentity(entry.key, facts) }

    Box(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        V2DeerHeader(
            Modifier
                .align(Alignment.TopStart)
                .absoluteOffset(x = 2.dp, y = 33.dp)
                .size(width = 118.dp, height = 124.dp)
                .graphicsLayer { alpha = .96f }
        )

        Column(Modifier.fillMaxSize()) {
            V2TopBar(
                mode = V2TopMode.INTERIOR,
                onBack = onBack,
                onPro = onPro,
                onMenu = { navigate("settings") },
            )

            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 18.dp),
            ) {
                item {
                    Spacer(Modifier.height(2.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        androidx.compose.material3.Text(
                            buildAnnotatedString {
                                withStyle(SpanStyle(color = Color.White)) { append(spec.title) }
                                if (proActive) {
                                    withStyle(
                                        SpanStyle(
                                            brush = Brush.horizontalGradient(
                                                listOf(Color(0xFFFFE7AA), V2Amber, Color(0xFFFFB043))
                                            )
                                        )
                                    ) { append(" Pro") }
                                }
                            },
                            style = TextStyle(
                                fontFamily = V2Font,
                                fontWeight = FontWeight.Black,
                                fontSize = 23.sp,
                                lineHeight = 29.sp,
                            ),
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                    KText(
                        spec.english,
                        size = 9.sp,
                        weight = FontWeight.Bold,
                        color = V2GoldText,
                        align = TextAlign.Center,
                        letterSpacing = 1.4.sp,
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    )
                    KText(
                        spec.tagline,
                        size = 11.sp,
                        color = V2Soft,
                        align = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    )
                    if (proActive && spec.hasProServices) {
                        Spacer(Modifier.height(5.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            ProBadge(label = "قسم PRO")
                        }
                    }
                    Spacer(Modifier.height(9.dp))
                }

                item { SectionHealthPanel(spec, identity) }
                item { Spacer(Modifier.height(10.dp)) }
                item {
                    SectionCenterpiece(
                        spec = spec,
                        status = identity.orbStatus,
                        left = identity.left,
                        right = identity.right,
                        onOpen = { navigate(entry.primaryRoute) },
                        onLeft = { navigate(identity.left.route) },
                        onRight = { navigate(identity.right.route) },
                    )
                }
                item { Spacer(Modifier.height(11.dp)) }
                item {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        KText("خدمات ${spec.title}", size = 14.sp, weight = FontWeight.Bold)
                        Spacer(Modifier.weight(1f))
                        if (proActive && spec.hasProServices) {
                            ProBadge(label = "PRO ${spec.subItems.count { it.proOnly }}")
                            Spacer(Modifier.width(5.dp))
                        }
                        KText("${spec.subItems.size} خدمة", size = 10.sp, color = V2Muted)
                    }
                    Spacer(Modifier.height(7.dp))
                }
                spec.subItems.chunked(5).forEach { row ->
                    item {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            row.forEach { service ->
                                SectionHomeTile(service, Modifier.weight(1f)) { navigate(service.route) }
                            }
                            repeat(5 - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                        Spacer(Modifier.height(7.dp))
                    }
                }
                item { Spacer(Modifier.height(4.dp)) }
                item { ProBanner(onClick = onPro) }
                item { Spacer(Modifier.height(9.dp)) }
                item {
                    V2GlassCard(
                        Modifier.fillMaxWidth(),
                        padding = 9.dp,
                        radius = 16.dp,
                        glow = spec.accent,
                        borderAlpha = .22f,
                    ) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            identity.stats.forEachIndexed { index, stat ->
                                if (index > 0) StatDivider()
                                HomeStat(stat.icon, stat.value, stat.label, stat.accent, Modifier.weight(1f))
                            }
                        }
                    }
                }
                item {
                    Spacer(Modifier.height(6.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Rounded.Verified, null, tint = V2Green, modifier = Modifier.size(11.dp))
                        Spacer(Modifier.width(4.dp))
                        KText(
                            "كل خدمة تفتح واجهتها المستقلة، وهذه الأرقام مقروءة من الجهاز الآن.",
                            size = 7.2.sp,
                            color = V2Muted,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHealthPanel(spec: KnouxServiceSpec, identity: SectionIdentity) {
    V2GlassCard(
        modifier = Modifier.fillMaxWidth(),
        padding = 11.dp,
        radius = 22.dp,
        glow = spec.accent,
        borderAlpha = .52f,
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                identity.lines.forEach { line ->
                    HealthLine(line.icon, line.label, line.value, line.progress, line.accent)
                }
            }
            Spacer(Modifier.width(10.dp))
            V2NeonRing(
                progress = identity.gaugePercent / 100f,
                size = 88.dp,
                stroke = 7.dp,
                colors = listOf(spec.accent, spec.accent2, V2Blue, V2Cyan),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    KText(
                        if (identity.gaugePercent > 0) "${identity.gaugePercent}%" else "—",
                        size = 26.sp,
                        weight = FontWeight.Black,
                    )
                    KText(identity.gaugeLabel, size = 8.sp, color = V2Soft, align = TextAlign.Center)
                    Spacer(Modifier.height(2.dp))
                    Icon(Icons.Rounded.Verified, null, tint = V2Amber, modifier = Modifier.size(11.dp))
                }
            }
        }
    }
}

@Composable
private fun SectionCenterpiece(
    spec: KnouxServiceSpec,
    status: String,
    left: SectionSide,
    right: SectionSide,
    onOpen: () -> Unit,
    onLeft: () -> Unit,
    onRight: () -> Unit,
) {
    Box(Modifier.fillMaxWidth().height(186.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(spec.accent.copy(alpha = .30f), spec.accent2.copy(alpha = .08f), Color.Transparent),
                    center = Offset(size.width * .5f, size.height * .52f),
                    radius = size.width * .44f,
                ),
                radius = size.width * .44f,
                center = Offset(size.width * .5f, size.height * .52f),
            )
            drawOval(
                brush = Brush.horizontalGradient(
                    listOf(Color.Transparent, spec.accent2.copy(alpha = .48f), spec.accent.copy(alpha = .52f), Color.Transparent)
                ),
                topLeft = Offset(size.width * .22f, size.height * .80f),
                size = Size(size.width * .56f, 1.5.dp.toPx()),
            )
        }

        SideActionCard(
            title = right.title,
            subtitle = right.subtitle,
            icon = right.icon,
            accent = right.accent,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 2.dp, y = 21.dp)
                .graphicsLayer { rotationZ = -3f },
            onClick = onRight,
        )
        SideActionCard(
            title = left.title,
            subtitle = left.subtitle,
            icon = left.icon,
            accent = left.accent,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (-2).dp, y = 21.dp)
                .graphicsLayer { rotationZ = 3f },
            onClick = onLeft,
        )

        SectionOrb(
            spec = spec,
            status = status,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 1.dp)
                .clickable(onClick = onOpen),
        )
    }
}

@Composable
private fun SectionOrb(spec: KnouxServiceSpec, status: String, modifier: Modifier = Modifier) {
    Box(modifier.size(162.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(spec.accent.copy(alpha = .20f), Color.Transparent),
                    radius = size.width * .52f,
                ),
                radius = size.width * .52f,
            )
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(V2Amber, spec.accent, spec.accent2, V2Pink, V2Amber),
                    center,
                ),
                startAngle = -70f,
                sweepAngle = 305f,
                useCenter = false,
                style = Stroke(4.dp.toPx(), cap = StrokeCap.Round),
            )
            drawArc(
                color = V2Amber.copy(alpha = .28f),
                startAngle = 8f,
                sweepAngle = 336f,
                useCenter = false,
                style = Stroke(9.dp.toPx(), cap = StrokeCap.Round),
            )
            drawArc(
                color = Color.White.copy(alpha = .20f),
                startAngle = 245f,
                sweepAngle = 70f,
                useCenter = false,
                style = Stroke(1.dp.toPx()),
            )
            drawCircle(color = V2Amber.copy(alpha = .9f), radius = 3.dp.toPx(), center = Offset(size.width * .81f, size.height * .20f))
            drawCircle(color = V2Amber.copy(alpha = .8f), radius = 2.3.dp.toPx(), center = Offset(size.width * .16f, size.height * .33f))
        }
        Box(
            Modifier
                .size(136.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFF25115C), Color(0xFF0F082B))))
                .border(1.dp, V2Amber.copy(alpha = .46f), CircleShape),
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

/** The Home tile rhythm, carried into every section: 91.dp, 15.dp radius, 5+5. */
@Composable
private fun SectionHomeTile(item: ServiceSubItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val proActive = LocalProActive.current
    Box(modifier) {
        V2GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(91.dp)
                .clickable(onClick = onClick),
            padding = 5.dp,
            radius = 15.dp,
            glow = item.accent,
            borderAlpha = .36f,
        ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                if (item.art != null) {
                    V2RasterTile(image = item.art, size = 52.dp, accent = item.accent)
                } else {
                    V2NeonGlyph(item.icon, item.accent, size = 50.dp)
                }
                Spacer(Modifier.height(2.dp))
                KText(
                    item.title,
                    size = 8.2.sp,
                    weight = FontWeight.SemiBold,
                    color = Color.White,
                    lineHeight = 10.5.sp,
                    align = TextAlign.Center,
                    maxLines = 2,
                )
            }
        }
        if (proActive && item.proOnly) {
            ProBadge(
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 3.dp, y = (-3).dp)
            )
        }
    }
}

/* ------------------------------------------------------------------ *
 * Per-section readings — every value comes from DeviceFacts
 * ------------------------------------------------------------------ */

private data class SectionLine(
    val icon: ImageVector,
    val label: String,
    val value: String,
    val progress: Float,
    val accent: Color,
)

private data class SectionSide(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accent: Color,
    val route: String,
)

private data class SectionStat(
    val icon: ImageVector,
    val value: String,
    val label: String,
    val accent: Color,
)

private data class SectionIdentity(
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

private fun sectionIdentity(key: String, facts: DeviceFacts): SectionIdentity {
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

    return when (key) {
        "duplicates" -> SectionIdentity(
            gaugeLabel = "قابل للاستعادة",
            gaugePercent = (ratio(reclaimable, mediaBytes) * 100).toInt(),
            lines = listOf(
                SectionLine(Icons.Rounded.ContentCopy, "مجموعات التكرار", "${media.duplicateGroups.size} مجموعة", ratioInt(media.duplicateGroups.size, 50), V2Cyan),
                SectionLine(Icons.Rounded.Image, "صور", formatCount(media.images), ratioInt(media.images, mediaTotal.coerceAtLeast(1)), V2Violet),
                SectionLine(Icons.Rounded.Videocam, "فيديو", formatCount(media.videos), ratioInt(media.videos, mediaTotal.coerceAtLeast(1)), V2Pink),
            ),
            left = SectionSide("تجميع النسخ", "Duplicate Groups", Icons.Rounded.ContentCopy, V2Amber, "dup-groups"),
            right = SectionSide("تأكيد العملية", "Safe Confirm", Icons.Rounded.Verified, V2Amber, "dup-confirm"),
            orbStatus = if (!media.permissionGranted) "صلاحية الوسائط غير ممنوحة" else "${media.duplicateCandidates} نسخة مرشحة",
            stats = listOf(
                SectionStat(Icons.Rounded.ContentCopy, "${media.duplicateGroups.size}", "مجموعة تكرار", V2Cyan),
                SectionStat(Icons.Rounded.FileCopy, "${media.duplicateCandidates}", "نسخة مرشحة", V2Pink),
                SectionStat(Icons.Rounded.DeleteSweep, formatBytes(reclaimable), "قابل للاستعادة", V2Green),
                SectionStat(Icons.Rounded.PermMedia, formatBytes(mediaBytes), "حجم الوسائط", V2Violet),
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
            left = SectionSide("فحص البقايا", "Residue Scan", Icons.Rounded.ManageSearch, V2Amber, "res-scan"),
            right = SectionSide("استثناء الملفات", "Exclusions", Icons.Rounded.Shield, V2Amber, "res-exclusions"),
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
                SectionLine(Icons.Rounded.Shield, "أذونات حساسة", "${formatCount(facts.sensitiveAppCount)} تطبيق", ratioInt(facts.sensitiveAppCount, facts.installedCount), V2Amber),
                SectionLine(Icons.Rounded.Lock, "مجموعات الأذونات", "${facts.privacyGroups.size} مجموعة", ratioInt(facts.privacyGroups.size, 12), V2Violet),
                SectionLine(Icons.Rounded.Visibility, "وصول الاستخدام", if (facts.usageAccessGranted) "ممنوح" else "غير ممنوح", if (facts.usageAccessGranted) 1f else 0f, V2Cyan),
            ),
            left = SectionSide("مجموعات الأذونات", "Permission Groups", Icons.Rounded.Lock, V2Amber, "prv-permissions"),
            right = SectionSide("تقييم التطبيقات", "App Risk", Icons.Rounded.Security, V2Amber, "prv-app-risk"),
            orbStatus = if (live) "${formatCount(facts.sensitiveAppCount)} تطبيق حساس" else "جاري القراءة",
            stats = listOf(
                SectionStat(Icons.Rounded.Shield, formatCount(facts.sensitiveAppCount), "تطبيق حساس", V2Amber),
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
                SectionLine(Icons.Rounded.Shield, "أذونات حساسة", "${formatCount(facts.sensitiveAppCount)} تطبيق", ratioInt(facts.sensitiveAppCount, facts.installedCount), V2Amber),
            ),
            left = SectionSide("نطاق الفحص", "Scan Scope", Icons.Rounded.Tune, V2Amber, "dsn-scope"),
            right = SectionSide("نتائج الفحص", "Findings", Icons.Rounded.FactCheck, V2Amber, "dsn-findings"),
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
            left = SectionSide("اتجاهات المساحة", "Storage Trends", Icons.Rounded.TrendingUp, V2Amber, "rpt-trends"),
            right = SectionSide("سجل الفحوص", "Scan Log", Icons.Rounded.Timeline, V2Amber, "rpt-scans"),
            orbStatus = if (live) "قراءة مباشرة · ${formatBytes(storage.freeBytes)} متاحة" else "جاري القراءة",
            stats = listOf(
                SectionStat(Icons.Rounded.Storage, formatBytes(storage.usedBytes), "مساحة مستخدمة", V2Cyan),
                SectionStat(Icons.Rounded.Apps, formatCount(facts.installedCount), "تطبيق مثبت", V2Pink),
                SectionStat(Icons.Rounded.Shield, formatCount(facts.sensitiveAppCount), "تطبيق حساس", V2Amber),
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
            left = SectionSide("قائمة التطبيقات", "Installed Apps", Icons.Rounded.Apps, V2Amber, "apm-installed"),
            right = SectionSide("إزالة التطبيقات", "Uninstall", Icons.Rounded.DeleteOutline, V2Amber, "apm-uninstall"),
            orbStatus = if (live) "${formatCount(facts.installedCount)} تطبيق" else "جاري القراءة",
            stats = listOf(
                SectionStat(Icons.Rounded.Apps, formatCount(facts.installedCount), "تطبيق مثبت", V2Cyan),
                SectionStat(Icons.Rounded.Person, formatCount(facts.userAppCount), "تطبيق مستخدم", V2Pink),
                SectionStat(Icons.Rounded.Android, formatCount(systemCount), "تطبيق نظام", V2Violet),
                SectionStat(Icons.Rounded.Shield, formatCount(facts.sensitiveAppCount), "تطبيق حساس", V2Amber),
            ),
        )
        "storage" -> SectionIdentity(
            gaugeLabel = "مساحة مستخدمة",
            gaugePercent = (storage.usedFraction * 100).toInt(),
            lines = listOf(
                SectionLine(Icons.Rounded.Storage, "مستخدم / كلي", "${formatBytes(storage.usedBytes)} / ${formatBytes(storage.totalBytes)}", storage.usedFraction, V2Cyan),
                SectionLine(Icons.Rounded.FolderOpen, "متاح", formatBytes(storage.freeBytes), 1f - storage.usedFraction, V2Green),
                SectionLine(Icons.Rounded.PermMedia, "وسائط", formatBytes(mediaBytes), ratio(mediaBytes, storage.usedBytes), V2Pink),
            ),
            left = SectionSide("توزيع الملفات", "Categories", Icons.Rounded.PieChart, V2Amber, "sto-categories"),
            right = SectionSide("فرز حسب الحجم", "Sort by Size", Icons.Rounded.Sort, V2Amber, "sto-sorting"),
            orbStatus = if (live) "${formatBytes(storage.freeBytes)} متاحة" else "جاري القراءة",
            stats = listOf(
                SectionStat(Icons.Rounded.Storage, formatBytes(storage.totalBytes), "سعة التخزين", V2Cyan),
                SectionStat(Icons.Rounded.DataUsage, formatBytes(storage.usedBytes), "مساحة مستخدمة", V2Pink),
                SectionStat(Icons.Rounded.FolderOpen, formatBytes(storage.freeBytes), "مساحة متاحة", V2Green),
                SectionStat(Icons.Rounded.PermMedia, formatBytes(mediaBytes), "حجم الوسائط", V2Violet),
            ),
        )
        "battery" -> SectionIdentity(
            gaugeLabel = "مستوى البطارية",
            gaugePercent = battery.levelPercent ?: 0,
            lines = listOf(
                SectionLine(Icons.Rounded.Bolt, "المستوى", batteryValue, (battery.levelPercent ?: 0) / 100f, V2Green),
                SectionLine(Icons.Rounded.Thermostat, "الحرارة", battery.temperatureC?.let { "$it°" } ?: "غير متاح", ((battery.temperatureC ?: 0f) / 60f).coerceIn(0f, 1f), V2Amber),
                SectionLine(Icons.Rounded.BatteryChargingFull, "الحالة", battery.statusLabel, if (battery.charging) 1f else .5f, V2Cyan),
            ),
            left = SectionSide("قراءات البطارية", "Live Readings", Icons.Rounded.Speed, V2Amber, "bat-readings"),
            right = SectionSide("مصادر الاستهلاك", "Power Sources", Icons.Rounded.ElectricalServices, V2Amber, "bat-source"),
            orbStatus = battery.levelPercent?.let { "$it% · ${battery.statusLabel}" } ?: "قراءة البطارية…",
            stats = listOf(
                SectionStat(Icons.Rounded.Bolt, batteryValue, "مستوى البطارية", V2Green),
                SectionStat(Icons.Rounded.Thermostat, battery.temperatureC?.let { "$it°" } ?: "—", "حرارة البطارية", V2Amber),
                SectionStat(Icons.Rounded.ElectricalServices, battery.voltageMv?.let { "${it}mV" } ?: "—", "الجهد", V2Cyan),
                SectionStat(Icons.Rounded.BatteryChargingFull, battery.healthLabel, "صحة البطارية", V2Violet),
            ),
        )
        "ai" -> SectionIdentity(
            gaugeLabel = "تقدير التحسين",
            gaugePercent = facts.healthEstimate,
            lines = listOf(
                SectionLine(Icons.Rounded.AutoAwesome, "توصيات مبنية على القراءة", if (live) "جاهزة" else "قيد القراءة", if (live) 1f else 0f, V2Amber),
                SectionLine(Icons.Rounded.Storage, "ضغط التخزين", "${formatBytes(storage.usedBytes)} مستخدم", storage.usedFraction, V2Cyan),
                SectionLine(Icons.Rounded.Bolt, "طاقة الجهاز", batteryValue, (battery.levelPercent ?: 0) / 100f, V2Green),
            ),
            left = SectionSide("أسباب التوصيات", "Why this advice", Icons.Rounded.Psychology, V2Amber, "ai-reasons"),
            right = SectionSide("تنفيذ بموافقة", "Approved Actions", Icons.Rounded.PlayCircle, V2Amber, "ai-execute"),
            orbStatus = if (facts.healthEstimate > 0) "${facts.healthEstimate}% جاهز" else "قراءة الجهاز…",
            stats = listOf(
                SectionStat(Icons.Rounded.AutoAwesome, if (facts.healthEstimate > 0) "${facts.healthEstimate}%" else "—", "تقدير التحسين", V2Amber),
                SectionStat(Icons.Rounded.Apps, formatCount(facts.installedCount), "تطبيق مثبت", V2Pink),
                SectionStat(Icons.Rounded.Storage, formatBytes(storage.freeBytes), "مساحة متاحة", V2Cyan),
                SectionStat(Icons.Rounded.Bolt, batteryValue, "مستوى البطارية", V2Green),
            ),
        )
        else -> SectionIdentity(
            gaugeLabel = "مساحة متاحة",
            gaugePercent = ((1f - storage.usedFraction) * 100).toInt(),
            lines = listOf(
                SectionLine(Icons.Rounded.CleaningServices, "كاش KNOUX", formatBytes(facts.ownCacheBytes), ratio(facts.ownCacheBytes, storage.usedBytes), V2Cyan),
                SectionLine(Icons.Rounded.Description, "ملفات KNOUX المؤقتة", "${formatCount(facts.ownCacheFiles)} ملف", ratioInt(facts.ownCacheFiles, 1000), V2Violet),
                SectionLine(Icons.Rounded.PermMedia, "وسائط للمراجعة", formatCount(mediaTotal), ratioInt(mediaTotal, 2000), V2Pink),
            ),
            left = SectionSide("معاينة المرشحات", "Candidate Preview", Icons.Rounded.FilterAlt, V2Amber, "clean-preview"),
            right = SectionSide("تأكيد الحذف", "Safe Delete", Icons.Rounded.Verified, V2Amber, "clean-confirm"),
            orbStatus = if (live) "${formatBytes(storage.freeBytes)} متاحة · ${formatCount(facts.installedCount)} تطبيق" else "جاري القراءة",
            stats = listOf(
                SectionStat(Icons.Rounded.Bolt, batteryValue, "مستوى البطارية", V2Green),
                SectionStat(Icons.Rounded.Apps, formatCount(facts.installedCount), "تطبيق مثبت", V2Pink),
                SectionStat(Icons.Rounded.CleaningServices, formatBytes(facts.ownCacheBytes), "كاش KNOUX", V2Cyan),
                SectionStat(Icons.Rounded.Storage, formatBytes(storage.freeBytes), "مساحة متاحة", V2Violet),
            ),
        )
    }
}
