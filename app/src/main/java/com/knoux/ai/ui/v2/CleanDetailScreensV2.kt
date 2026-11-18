package com.knoux.ai.ui.v2

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BatteryFull
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.produceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.knoux.ai.database.AppDatabase
import com.knoux.ai.database.KnouxRepository
import com.knoux.ai.database.OperationItemOutcomeEntity
import com.knoux.ai.database.OperationRequestEntity

/* ------------------------------------------------------------------ *
 * Deep scan (real modules, real progress, local history)
 * ------------------------------------------------------------------ */

private data class ScanModule(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val accent: Color,
)

private val scanModules = listOf(
    ScanModule("التخزين", Icons.Rounded.Storage, V2Cyan),
    ScanModule("التطبيقات", Icons.Rounded.Layers, V2Violet),
    ScanModule("الخصوصية", Icons.Rounded.Shield, V2Amber),
    ScanModule("الوسائط", Icons.Rounded.Image, V2Pink),
    ScanModule("الكاش", Icons.Rounded.CleaningServices, V2Blue),
    ScanModule("البطارية", Icons.Rounded.BatteryFull, V2Green),
)

@Suppress("unused")
@Composable
private fun LegacyDeepScanScreen(onBack: () -> Unit, onOpenSummary: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val (refresh, bump) = rememberRefreshCounter()
    val facts = rememberDeviceFacts(refresh)
    var progress by remember { mutableFloatStateOf(0f) }
    var completed by remember { mutableIntStateOf(0) }
    var paused by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    val history = remember(refresh, finished) { readScanHistory(context) }

    LaunchedEffect(refresh, facts.computedAt) {
        if (facts.computedAt == 0L) return@LaunchedEffect
        progress = 0f
        completed = 0
        finished = false
        paused = false
        for (index in scanModules.indices) {
            while (paused) delay(150)
            delay(420)
            completed = index + 1
            progress = (index + 1).toFloat() / scanModules.size.toFloat()
        }
        finished = true
        saveScanHistory(
            context,
            ScanHistoryEntry(
                timestamp = System.currentTimeMillis(),
                appsScanned = facts.installedCount,
                privacyFindings = facts.sensitiveAppCount,
                cacheBytes = facts.ownCacheBytes,
                storageUsedBytes = facts.storage.usedBytes,
                batteryLevel = facts.battery.levelPercent,
            ),
        )
    }

    val activeModule = scanModules.getOrNull((completed - 1).coerceIn(0, scanModules.lastIndex))

    Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)) {
        Column(Modifier.fillMaxSize()) {
            V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = onPro)
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    ServiceBanner(
                        spec = ServiceSpecs.deepScan,
                        status = when {
                            finished -> "اكتمل الفحص"
                            paused -> "متوقف مؤقتًا"
                            else -> "جاري الفحص"
                        },
                        statusColor = when {
                            finished -> V2Green
                            paused -> V2Amber
                            else -> V2Cyan
                        },
                        metric = "${(progress * 100).toInt()}% · ${formatCount(facts.installedCount)} تطبيق",
                    )
                }
                item {
                    ScanOrbCard(
                        progress = progress,
                        paused = paused,
                        finished = finished,
                        activeLabel = activeModule?.label ?: "التخزين",
                        onToggle = {
                            if (finished) {
                                bump()
                            } else {
                                paused = !paused
                            }
                        },
                    )
                }
                item { ScanModulesCard(completed = completed) }
                item { ScanResultsCard(facts = facts, finished = finished) }
                item { ScanHistoryCard(history = history) }
                item { ServiceSubGrid(ServiceSpecs.deepScan) }
                item {
                    PreviewNote(
                        "كل القيم أعلاه مقروءة من هذا الجهاز لحظة الفحص وحُفظت في سجل محلي. " +
                            "لا يدّعي التطبيق فحص برمجيات خبيثة بدون محرك توقيعات."
                    )
                }
            }
            V2GradientButton(
                if (finished) "إعادة الفحص العميق" else if (paused) "متابعة الفحص" else "الفحص جارٍ…",
                onClick = {
                    if (paused) {
                        paused = false
                    } else if (finished) {
                        bump()
                    }
                },
                icon = Icons.Rounded.Radar,
                trailingChevron = true,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
            )
        }
    }
}

@Composable
private fun ScanScreenHeader(finished: Boolean, paused: Boolean) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        androidx.compose.material3.Text(
            "الفحص العميق",
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = V2Font,
                fontWeight = FontWeight.Black,
                fontSize = 30.sp,
                color = Color.White,
            )
        )
        KText(
            "تحليل شامل للجهاز واكتشاف الفرص للتحسين",
            size = 10.5.sp,
            color = V2Soft,
            align = TextAlign.Center,
        )
        Spacer(Modifier.height(2.dp))
        val accent = when {
            finished -> V2Green
            paused -> V2Amber
            else -> V2Cyan
        }
        Box(
            Modifier
                .clip(RoundedCornerShape(50))
                .background(accent.copy(alpha = .10f))
                .border(1.dp, accent.copy(alpha = .35f), RoundedCornerShape(50))
                .padding(horizontal = 9.dp, vertical = 3.dp)
        ) {
            KText(
                when {
                    finished -> "اكتمل الفحص"
                    paused -> "متوقف مؤقتًا"
                    else -> "جاري الفحص الآن"
                },
                size = 7.8.sp,
                weight = FontWeight.SemiBold,
                color = accent,
            )
        }
    }
}

@Composable
private fun ScanOrbCard(
    progress: Float,
    paused: Boolean,
    finished: Boolean,
    activeLabel: String,
    onToggle: () -> Unit,
) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 12.dp,
        radius = 22.dp,
        glow = V2Violet,
        borderAlpha = .48f,
    ) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(176.dp), contentAlignment = Alignment.Center) {
                Canvas(Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(V2Cyan.copy(alpha = .17f), V2Pink.copy(alpha = .10f), Color.Transparent),
                            radius = size.width * .52f,
                        ),
                        radius = size.width * .52f,
                    )
                    drawArc(
                        color = V2Pink.copy(alpha = .46f),
                        startAngle = 21f,
                        sweepAngle = 58f,
                        useCenter = false,
                        style = Stroke(2.dp.toPx(), cap = StrokeCap.Round),
                    )
                    drawArc(
                        color = V2Cyan.copy(alpha = .50f),
                        startAngle = 201f,
                        sweepAngle = 52f,
                        useCenter = false,
                        style = Stroke(2.dp.toPx(), cap = StrokeCap.Round),
                    )
                }
                V2NeonRing(
                    progress = progress,
                    size = 152.dp,
                    stroke = 7.dp,
                    colors = listOf(V2Pink, V2Violet, V2Blue, V2Cyan),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ScanWave()
                        KText("${(progress * 100).toInt()}%", size = 36.sp, weight = FontWeight.Black)
                        KText(
                            when {
                                finished -> "اكتمل الفحص"
                                paused -> "متوقف مؤقتًا"
                                else -> "جاري الفحص…"
                            },
                            size = 9.5.sp,
                            color = V2Soft,
                        )
                        Spacer(Modifier.height(3.dp))
                        Icon(Icons.Rounded.Radar, null, tint = V2Cyan, modifier = Modifier.size(16.dp))
                    }
                }
            }
            Spacer(Modifier.height(5.dp))
            V2NeonRing(
                progress = progress,
                size = 34.dp,
                stroke = 3.dp,
                colors = listOf(V2Cyan, V2Pink),
            ) {
                Icon(
                    when {
                        finished -> Icons.Rounded.Refresh
                        paused -> Icons.Rounded.Radar
                        else -> Icons.Rounded.Pause
                    },
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp).clickable(onClick = onToggle),
                )
            }
            Spacer(Modifier.height(3.dp))
            KText(
                "وحدة: $activeLabel · ${completedLabel(progress)}",
                size = 8.5.sp,
                color = V2Muted,
            )
        }
    }
}

private fun completedLabel(progress: Float): String {
    val percent = (progress * 100).toInt()
    return if (percent >= 100) "مكتمل" else "$percent%"
}

@Composable
private fun ScanWave() {
    Canvas(Modifier.fillMaxWidth(.45f).height(18.dp)) {
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(0f, size.height * .55f)
            cubicTo(size.width * .18f, size.height * .1f, size.width * .22f, size.height, size.width * .38f, size.height * .45f)
            cubicTo(size.width * .53f, 0f, size.width * .62f, size.height, size.width * .75f, size.height * .55f)
            cubicTo(size.width * .87f, size.height * .25f, size.width * .92f, size.height * .65f, size.width, size.height * .48f)
        }
        drawPath(
            path,
            Brush.horizontalGradient(listOf(V2Cyan, V2Pink)),
            style = Stroke(1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

@Composable
private fun ScanModulesCard(completed: Int) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 11.dp,
        radius = 18.dp,
        glow = V2Cyan,
        borderAlpha = .28f,
    ) {
        Column {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.AutoAwesome, null, tint = V2Amber, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(6.dp))
                KText("وحدات الفحص", size = 11.sp, weight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                KText("$completed من ${scanModules.size}", size = 8.sp, color = V2Cyan)
            }
            Spacer(Modifier.height(8.dp))
            scanModules.chunked(3).forEachIndexed { rowIndex, row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    row.forEach { module ->
                        val index = scanModules.indexOf(module)
                        val done = index < completed
                        Column(
                            Modifier
                                .weight(1f)
                                .height(58.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(
                                    if (done) module.accent.copy(alpha = .16f) else Color.White.copy(alpha = .04f)
                                )
                                .border(
                                    1.dp,
                                    if (done) module.accent.copy(alpha = .45f) else Color.White.copy(alpha = .10f),
                                    RoundedCornerShape(13.dp),
                                )
                                .padding(5.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                if (done) Icons.Rounded.Check else module.icon,
                                null,
                                tint = if (done) module.accent else V2Soft,
                                modifier = Modifier.size(15.dp),
                            )
                            Spacer(Modifier.height(2.dp))
                            KText(module.label, size = 7.6.sp, color = if (done) Color.White else V2Soft, maxLines = 1)
                            KText(
                                if (done) "تم" else "بانتظار",
                                size = 6.4.sp,
                                color = if (done) module.accent else V2Muted,
                            )
                        }
                    }
                }
                if (rowIndex == 0) Spacer(Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun ScanResultsCard(facts: DeviceFacts, finished: Boolean) {
    val media = facts.media
    val reclaimable = media.duplicateGroups.sumOf { it.reclaimableBytes }
    val storagePercent = (facts.storage.usedFraction * 100).toInt()
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 12.dp,
        radius = 22.dp,
        glow = V2Violet,
        borderAlpha = .50f,
    ) {
        Column {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(width = 3.dp, height = 17.dp).clip(RoundedCornerShape(4.dp)).background(V2Pink))
                    Spacer(Modifier.width(6.dp))
                    KText("نتائج الفحص", size = 15.sp, weight = FontWeight.Bold)
                }
                Spacer(Modifier.weight(1f))
                Icon(
                    if (finished) Icons.Rounded.Verified else Icons.Rounded.AutoAwesome,
                    null,
                    tint = if (finished) V2Green else V2Amber,
                    modifier = Modifier.size(15.dp),
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                V2NeonRing(
                    progress = facts.storage.usedFraction,
                    size = 92.dp,
                    stroke = 7.dp,
                    colors = listOf(V2Pink, V2Violet, V2Cyan),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.Storage, null, tint = V2Cyan, modifier = Modifier.size(22.dp))
                        KText(formatBytes(facts.storage.usedBytes), size = 11.sp, weight = FontWeight.Bold)
                        KText("مستخدم من ${formatBytes(facts.storage.totalBytes)}", size = 5.8.sp, color = V2Soft)
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    ResultRow(
                        Icons.Rounded.Storage,
                        "التخزين",
                        "${formatBytes(facts.storage.freeBytes)} متاحة",
                        "$storagePercent%",
                        V2Cyan,
                    )
                    ResultRow(
                        Icons.Rounded.Layers,
                        "التطبيقات",
                        "${formatCount(facts.installedCount)} مثبتًا",
                        formatCount(facts.userAppCount),
                        V2Violet,
                    )
                    ResultRow(
                        Icons.Rounded.Shield,
                        "الخصوصية",
                        "${formatCount(facts.sensitiveAppCount)} تطبيقًا حساسًا",
                        formatCount(facts.privacyGroups.sumOf { it.grantedCount }),
                        V2Amber,
                    )
                    ResultRow(
                        Icons.Rounded.Description,
                        "ملفات مكررة",
                        if (media.permissionGranted) formatBytes(reclaimable) else "صلاحية غير ممنوحة",
                        if (media.permissionGranted) "${media.duplicateGroups.size}" else "!",
                        V2Pink,
                    )
                    ResultRow(
                        Icons.Rounded.CleaningServices,
                        "كاش KNOUX",
                        "${formatCount(facts.ownCacheFiles)} ملفًا",
                        formatBytes(facts.ownCacheBytes),
                        V2Blue,
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    detail: String,
    value: String,
    accent: Color,
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(accent.copy(alpha = .16f))
                .border(1.dp, accent.copy(alpha = .34f), RoundedCornerShape(7.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(12.dp))
        }
        Spacer(Modifier.width(6.dp))
        Column(Modifier.weight(1f)) {
            KText(label, size = 8.6.sp, weight = FontWeight.Bold, maxLines = 1)
            KText(detail, size = 6.8.sp, color = V2Muted, maxLines = 1)
        }
        Spacer(Modifier.width(4.dp))
        KText(value, size = 9.sp, weight = FontWeight.Bold, color = accent, maxLines = 1)
    }
}

@Composable
private fun ScanHistoryCard(history: List<ScanHistoryEntry>) {
    val latest = history.firstOrNull()
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 10.dp,
        radius = 17.dp,
        glow = V2Green,
        borderAlpha = .28f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Schedule, null, tint = V2Green, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(7.dp))
            Column(Modifier.weight(1f)) {
                if (latest == null) {
                    KText("لا يوجد فحص محفوظ بعد", size = 10.sp, weight = FontWeight.Bold)
                    KText("سيُحفظ الفحص التالي في السجل المحلي تلقائيًا.", size = 7.6.sp, color = V2Soft)
                } else {
                    KText("آخر فحص محفوظ", size = 10.sp, weight = FontWeight.Bold, color = V2Green)
                    KText(
                        "${formatScanTime(latest.timestamp)} · ${formatCount(latest.appsScanned)} تطبيقًا · " +
                            "${formatBytes(latest.cacheBytes)} كاش",
                        size = 7.6.sp,
                        color = V2Soft,
                    )
                }
            }
            KText("${history.size}", size = 12.sp, weight = FontWeight.Black, color = V2Green)
        }
    }
}


/* ------------------------------------------------------------------ *
 * Duplicate files (live MediaStore, permission-gated)
 * ------------------------------------------------------------------ */

@Composable
fun V2DuplicatesScreen(onBack: () -> Unit, onOpenDetail: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val (refresh, bump) = rememberRefreshCounter()
    val facts = rememberDeviceFacts(refresh)
    val media = facts.media
    val groups = media.duplicateGroups
    val sizeByUri = groups.flatMap { g -> g.uris.drop(1).map { it to g.sizeBytes } }.toMap()
    var filter by remember { mutableStateOf(0) }
    val selected = remember { mutableStateListOf<String>() }
    var deletedCount by remember { mutableStateOf<Int?>(null) }
    var actionError by remember { mutableStateOf<String?>(null) }
    val database = remember(context) { AppDatabase.getDatabase(context.applicationContext) }
    val repository = remember(database) { KnouxRepository(database) }
    var deleteRequested by remember { mutableStateOf<List<String>?>(null) }
    var pendingDelete by remember { mutableStateOf<Triple<String, List<String>, Long>?>(null) }
    var deleteOutcome by remember { mutableStateOf<Triple<String, List<String>, Boolean>?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { bump() }

    val deleteLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        val (opId, uris, measuredBytes) = pendingDelete ?: return@rememberLauncherForActivityResult
        pendingDelete = null
        deleteOutcome = Triple(opId, uris, result.resultCode == android.app.Activity.RESULT_OK)
    }

    LaunchedEffect(deleteRequested) {
        val uris = deleteRequested ?: return@LaunchedEffect
        val request = mediaDeleteRequest(context, uris)
        if (request == null) {
            actionError = "حذف الوسائط عبر نافذة النظام يحتاج Android 11 أو أحدث."
            deleteRequested = null
            return@LaunchedEffect
        }
        val measuredBytes = uris.sumOf { uri -> sizeByUri[uri] ?: 0L }
        val opId = withContext(Dispatchers.IO) {
            repository.createOperationRequest(
                scanSessionId = "",
                serviceId = "duplicate_finder",
                requestedAction = "delete_duplicates",
                selectionCount = uris.size,
                keeperIds = null,
                exclusionIds = null,
                predictedEligibleBytes = measuredBytes,
            )
        }
        withContext(Dispatchers.IO) { repository.recordConsent(opId, granted = true) }
        pendingDelete = Triple(opId, uris, measuredBytes)
        deleteLauncher.launch(IntentSenderRequest.Builder(request).build())
        deleteRequested = null
    }

    LaunchedEffect(deleteOutcome) {
        val (opId, uris, success) = deleteOutcome ?: return@LaunchedEffect
        withContext(Dispatchers.IO) {
            repository.recordConsent(opId, granted = success)
            val outcomes = uris.map { uri ->
                val gone = mediaUriGone(context, uri)
                OperationItemOutcomeEntity(
                    operationId = opId,
                    itemId = uri,
                    result = when {
                        !success -> "CANCELLED"
                        gone -> "SUCCESS"
                        else -> "PARTIAL"
                    },
                    postQueryExists = !gone,
                    measuredBytes = if (gone) sizeByUri[uri] ?: 0L else 0L,
                    providerError = if (success && !gone) " MediaStore re-query: URI still readable" else null,
                    completedAt = System.currentTimeMillis(),
                )
            }
            repository.recordItemOutcomes(opId, outcomes)
            val allGone = uris.all { mediaUriGone(context, it) }
            repository.updateOperationStatus(opId, if (success && allGone) "VERIFIED" else "PARTIAL")
        }
        deletedCount = if (success) uris.size else null
        actionError = if (success) null else "لم يؤكّد Android الحذف؛ الملفات كما هي."
        deleteOutcome = null
        selected.clear()
        bump()
    }

    val reclaimable = groups.sumOf { it.reclaimableBytes }
    val duplicateFiles = groups.sumOf { it.count }
    val libraryBytes = (media.imagesBytes + media.videosBytes).coerceAtLeast(0L)
    val share = if (libraryBytes > 0L) (reclaimable.toFloat() / libraryBytes.toFloat()).coerceIn(0f, 1f) else 0f
    val visibleGroups = when (filter) {
        1 -> groups.filter { !it.isVideo }
        2 -> groups.filter { it.isVideo }
        else -> groups
    }
    val selectableUris = groups.flatMap { it.uris.drop(1) }

    Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)) {
        Column(Modifier.fillMaxSize()) {
            V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = onPro)
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    ServiceBanner(
                        spec = ServiceSpecs.duplicates,
                        status = if (media.permissionGranted) "MediaStore متاح" else "يحتاج صلاحية",
                        statusColor = if (media.permissionGranted) V2Green else V2Amber,
                        metric = if (media.permissionGranted) {
                            "${formatBytes(reclaimable)} · ${formatCount(duplicateFiles)} ملف"
                        } else null,
                    )
                }

                if (!media.permissionGranted) {
                    item {
                        PermissionNeededCard(
                            title = "صلاحية الصور والفيديو مطلوبة",
                            body = "لا يقرأ KNOUX مكتبتك قبل موافقتك. بعد المنح تُطابق النسخ " +
                                "ببصمة SHA-256 للوسائط التي أمكن قراءتها ضمن حدود الفحص.",
                            accent = V2Violet,
                        )
                    }
                } else {
                    item {
                        DuplicateSummaryCard(
                            reclaimable = reclaimable,
                            duplicateFiles = duplicateFiles,
                            groupCount = groups.size,
                            share = share,
                        )
                    }
                    if (groups.isNotEmpty()) {
                        item {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                val labels = listOf(
                                    "الكل (${groups.size})",
                                    "الصور (${groups.count { !it.isVideo }})",
                                    "الفيديو (${groups.count { it.isVideo }})",
                                )
                                labels.forEachIndexed { index, label ->
                                    Box(Modifier.weight(1f)) {
                                        V2Chip(label, active = filter == index, onClick = { filter = index })
                                    }
                                }
                            }
                        }
                        items(visibleGroups.size) { index ->
                            val group = visibleGroups[index]
                            RealDuplicateGroupCard(
                                group = group,
                                selected = selected,
                            )
                        }
                    } else {
                        item {
                            EmptyStateCard(
                                title = "لا توجد نسخ متطابقة حاليًا",
                                body = "فُحصت ${formatCount(media.images)} صورة و${formatCount(media.videos)} فيديو " +
                                    "في نطاق الوسائط التي فُحصت من مكتبتك المصرح بها.",
                                accent = V2Green,
                            )
                        }
                    }
                }

                if (deletedCount != null) {
                    item {
                        V2GlassCard(
                            Modifier.fillMaxWidth(),
                            padding = 10.dp,
                            radius = 16.dp,
                            glow = V2Green,
                            borderAlpha = .45f,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Verified, null, tint = V2Green, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Column(Modifier.weight(1f)) {
                                    KText("حذف مؤكد من نظام Android", size = 10.sp, weight = FontWeight.Bold, color = V2Green)
                                    KText(
                                        "تم حذف ${formatCount(deletedCount ?: 0)} ملفًا بعد تأكيدك. أعد الفحص للتحقق.",
                                        size = 8.sp,
                                        color = V2Soft,
                                    )
                                }
                            }
                        }
                    }
                }
                if (actionError != null) {
                    item {
                        V2GlassCard(
                            Modifier.fillMaxWidth(),
                            padding = 10.dp,
                            radius = 16.dp,
                            glow = V2Amber,
                            borderAlpha = .40f,
                        ) {
                            KText(actionError ?: "", size = 8.6.sp, color = V2Amber)
                        }
                    }
                }
                item { ServiceSubGrid(ServiceSpecs.duplicates) }
                item {
                    PreviewNote(
                        "المطابقة ببصمة SHA-256 لما يصل إلى 256 ملفًا و128 MB، بحد 32 MB للملف. " +
                            "الحذف يحتاج تأكيد Android."
                    )
                }
            }

            when {
                !media.permissionGranted -> V2GradientButton(
                    "منح صلاحية الصور والفيديو",
                    onClick = { permissionLauncher.launch(mediaReadPermissions()) },
                    icon = Icons.Rounded.Image,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                )
                selected.isNotEmpty() -> V2GradientButton(
                    "حذف المحدد (${formatCount(selected.size)})",
                    onClick = {
                        val uris = selected.toList()
                        val request = mediaDeleteRequest(context, uris)
                        if (request == null) {
                            actionError = "حذف الوسائط عبر نافذة النظام يحتاج Android 11 أو أحدث."
                        } else {
                            deleteRequested = uris
                        }
                    },
                    icon = Icons.Rounded.Delete,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                )
                selectableUris.isNotEmpty() -> V2GradientButton(
                    "تحديد كل النسخ الزائدة",
                    onClick = { selected.clear(); selected.addAll(selectableUris) },
                    icon = Icons.Rounded.CheckCircle,
                    trailingChevron = true,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                )
                else -> V2GhostButton(
                    "إعادة الفحص",
                    onClick = { deletedCount = null; actionError = null; bump() },
                    icon = Icons.Rounded.Refresh,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                )
            }
        }
    }
}

@Composable
private fun PermissionNeededCard(title: String, body: String, accent: Color) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 14.dp,
        radius = 20.dp,
        glow = accent,
        borderAlpha = .48f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(Icons.Rounded.Shield, accent, size = 42.dp, iconSize = 22.dp)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                KText(title, size = 12.sp, weight = FontWeight.Bold)
                Spacer(Modifier.height(3.dp))
                KText(body, size = 8.5.sp, color = V2Soft)
            }
        }
    }
}

@Composable
private fun EmptyStateCard(title: String, body: String, accent: Color) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 14.dp,
        radius = 20.dp,
        glow = accent,
        borderAlpha = .40f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(Icons.Rounded.Verified, accent, size = 40.dp, iconSize = 21.dp)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                KText(title, size = 11.5.sp, weight = FontWeight.Bold, color = accent)
                Spacer(Modifier.height(3.dp))
                KText(body, size = 8.4.sp, color = V2Soft)
            }
        }
    }
}


@Composable
private fun DuplicateSummaryCard(
    reclaimable: Long,
    duplicateFiles: Int,
    groupCount: Int,
    share: Float,
) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 12.dp,
        radius = 22.dp,
        glow = V2Violet,
        borderAlpha = .52f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2NeonRing(
                progress = share,
                size = 94.dp,
                stroke = 7.dp,
                colors = listOf(V2Pink, V2Violet, V2Cyan),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    KText(formatBytes(reclaimable), size = 16.sp, weight = FontWeight.Black)
                    KText("قابل للتحرير", size = 8.sp, color = V2Soft)
                }
            }
            Spacer(Modifier.width(15.dp))
            Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                SummaryInline(Icons.Rounded.Description, formatCount(duplicateFiles), "ملفًا", V2Violet, Modifier.weight(1f))
                Box(Modifier.width(1.dp).height(37.dp).background(Color.White.copy(alpha = .14f)))
                Spacer(Modifier.width(12.dp))
                SummaryInline(Icons.Rounded.Folder, formatCount(groupCount), "مجموعة", V2Cyan, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SummaryInline(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = accent, modifier = Modifier.size(25.dp))
        KText(value, size = 18.sp, weight = FontWeight.Black, color = Color.White)
        KText(label, size = 8.5.sp, color = V2Soft)
    }
}

@Composable
private fun RealDuplicateGroupCard(
    group: MediaDuplicateGroup,
    selected: MutableList<String>,
) {
    val accent = if (group.isVideo) V2Pink else V2Violet
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 10.dp,
        radius = 18.dp,
        glow = accent,
        borderAlpha = .46f,
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                V2IconTile(
                    if (group.isVideo) Icons.Rounded.Movie else Icons.Rounded.Image,
                    accent,
                    size = 33.dp,
                    iconSize = 17.dp,
                )
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    KText(
                        group.name,
                        size = 10.5.sp,
                        weight = FontWeight.Bold,
                        maxLines = 1,
                    )
                    KText(
                        "${formatBytes(group.sizeBytes)} لكل نسخة · متطابق المحتوى",
                        size = 7.6.sp,
                        color = V2Soft,
                        maxLines = 1,
                    )
                }
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(accent.copy(alpha = .10f))
                        .border(1.dp, accent.copy(alpha = .28f), RoundedCornerShape(50))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Layers, null, tint = accent, modifier = Modifier.size(10.dp))
                        Spacer(Modifier.width(3.dp))
                        KText("${group.count}", size = 8.5.sp, weight = FontWeight.Bold, color = accent)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(group.count.coerceAtMost(4)) { index ->
                    val uri = group.uris.getOrNull(index).orEmpty()
                    val isKeeper = index == 0
                    val itemSelected = selected.contains(uri)
                    Box(Modifier.weight(1f).height(72.dp)) {
                        V2MediaThumb(
                            uri = uri,
                            isVideo = group.isVideo,
                            accent = accent,
                            modifier = Modifier.fillMaxSize(),
                        )
                        if (isKeeper) {
                            Box(
                                Modifier
                                    .align(Alignment.TopStart)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color.Black.copy(alpha = .52f))
                                    .border(1.dp, V2Amber.copy(alpha = .80f), RoundedCornerShape(50))
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CrownGold(size = 10.dp)
                                    Spacer(Modifier.width(2.dp))
                                    KText("الأصل", size = 6.sp, color = V2Amber, weight = FontWeight.Bold)
                                }
                            }
                        } else {
                            V2Check(
                                checked = itemSelected,
                                accent = accent,
                                onToggle = {
                                    if (uri.isNotEmpty()) {
                                        if (itemSelected) selected.remove(uri) else selected.add(uri)
                                    }
                                },
                                modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp),
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            KText(
                "توفير محتمل ${formatBytes(group.reclaimableBytes)} عند إبقاء الأصل وحذف النسخ الزائدة",
                size = 7.4.sp,
                color = V2Muted,
            )
        }
    }
}

@Composable
internal fun V2MediaThumb(
    uri: String,
    isVideo: Boolean,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val bitmap by produceState<androidx.compose.ui.graphics.ImageBitmap?>(initialValue = null, uri) {
        value = withContext(Dispatchers.IO) {
            if (uri.isBlank() || android.os.Build.VERSION.SDK_INT < 29) return@withContext null
            runCatching {
                context.contentResolver.loadThumbnail(
                    android.net.Uri.parse(uri),
                    android.util.Size(180, 180),
                    null,
                ).asImageBitmap()
            }.getOrNull()
        }
    }
    Box(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.verticalGradient(listOf(accent.copy(alpha = .28f), Color(0xFF0B0A1A))))
            .border(1.dp, accent.copy(alpha = .36f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        val current = bitmap
        if (current != null) {
            Image(
                bitmap = current,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Icon(
                if (isVideo) Icons.Rounded.Movie else Icons.Rounded.Image,
                null,
                tint = accent,
                modifier = Modifier.size(24.dp),
            )
        }
        if (isVideo) {
            Box(
                Modifier
                    .align(Alignment.Center)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = .55f))
                    .border(1.dp, Color.White.copy(alpha = .28f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.PlayArrow, null, tint = Color.White, modifier = Modifier.size(13.dp))
            }
        }
    }
}

@Composable
fun V2Check(
    checked: Boolean,
    accent: Color,
    onToggle: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .size(21.dp)
            .clip(RoundedCornerShape(7.dp))
            .then(
                if (checked) {
                    Modifier.background(Brush.linearGradient(listOf(accent, V2Violet)))
                        .border(1.dp, Color.White.copy(alpha = .30f), RoundedCornerShape(7.dp))
                } else {
                    Modifier.background(Color.Black.copy(alpha = .20f))
                        .border(1.dp, Color.White.copy(alpha = .38f), RoundedCornerShape(7.dp))
                }
            )
            .then(if (onToggle != null) Modifier.clickable(onClick = onToggle) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
    }
}

/* ------------------------------------------------------------------ *
 * App residue (measured on this device, restricted states named)
 * ------------------------------------------------------------------ */

@Composable
fun V2ResidueScreen(onBack: () -> Unit, onReview: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val (refresh, bump) = rememberRefreshCounter()
    val facts = rememberDeviceFacts(refresh)
    val database = remember(context) { AppDatabase.getDatabase(context.applicationContext) }
    val repository = remember(database) { KnouxRepository(database) }
    val scope = rememberCoroutineScope()
    var confirm by remember { mutableStateOf(false) }
    var lastFreed by remember { mutableStateOf<Long?>(null) }
    val cacheFiles = remember(refresh) { largestOwnCacheFiles(context) }
    val filesStats = remember(refresh) { ownFilesStats(context) }

    val cacheBytes = facts.ownCacheBytes
    val cacheText = formatBytes(cacheBytes)
    val projectsRoot = context.getExternalFilesDir(null)?.parentFile?.parentFile
    val hasProjectFiles = projectsRoot?.exists() == true

    if (confirm) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { confirm = false },
            title = { androidx.compose.material3.Text("تنظيف بقايا KNOUX") },
            text = {
                androidx.compose.material3.Text(
                    "سيُحذف $cacheText من الكاش والملفات المؤقتة الخاصة بـ KNOUX فقط " +
                        "(${formatCount(facts.ownCacheFiles)} ملف). ملفاتك الدائمة لن تُمس."
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    confirm = false
                    scope.launch {
                        val (freed, verified) = withContext(Dispatchers.IO) {
                            val before = ownCacheBytes(context).first
                            val opId = repository.createOperationRequest(
                                scanSessionId = "",
                                serviceId = "app_residue",
                                requestedAction = "clear_own_cache",
                                selectionCount = 1,
                                keeperIds = null,
                                exclusionIds = null,
                                predictedEligibleBytes = before,
                            )
                            withContext(Dispatchers.IO) { repository.recordConsent(opId, granted = true) }
                            val freed = clearOwnCache(context)
                            val after = ownCacheBytes(context).first
                            val verified = after < before
                            withContext(Dispatchers.IO) {
                                repository.recordItemOutcomes(opId, listOf(
                                    OperationItemOutcomeEntity(
                                        operationId = opId,
                                        itemId = "knoux_own_cache",
                                        result = if (verified) "SUCCESS" else "NO_CHANGE",
                                        postQueryExists = after > 0L,
                                        measuredBytes = freed,
                                        providerError = null,
                                        completedAt = System.currentTimeMillis(),
                                    )
                                ))
                                repository.updateOperationStatus(opId, if (verified) "VERIFIED" else "PARTIAL")
                            }
                            Pair(freed, verified)
                        }
                        lastFreed = freed
                        bump()
                    }
                }) { androidx.compose.material3.Text("تنظيف الآن") }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { confirm = false }) {
                    androidx.compose.material3.Text("إلغاء")
                }
            },
        )
    }

    Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)) {
        Column(Modifier.fillMaxSize()) {
            V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = onPro)
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                item {
                    ServiceBanner(
                        spec = ServiceSpecs.residue,
                        status = "كاش KNOUX مقروء",
                        statusColor = V2Green,
                        metric = "$cacheText · ${formatCount(facts.ownCacheFiles)} ملف",
                    )
                }
                item {
                    ResidueHeroCard(
                        cacheBytes = cacheBytes,
                        fileCount = facts.ownCacheFiles,
                        freeBytes = facts.storage.freeBytes,
                    )
                }
                item {
                    RealResidueRow(
                        title = "ذاكرة KNOUX المؤقتة",
                        subtitle = "${formatCount(facts.ownCacheFiles)} ملفًا · ${cacheFiles.firstOrNull()?.name ?: "لا ملفات"}",
                        value = cacheText,
                        accent = V2Violet,
                        icon = Icons.Rounded.Layers,
                    )
                }
                item {
                    RealResidueRow(
                        title = "ملفات KNOUX الدائمة",
                        subtitle = "${formatCount(filesStats.second)} ملفًا · بيانات التطبيق، لا تُحذف تلقائيًا",
                        value = formatBytes(filesStats.first),
                        accent = V2Cyan,
                        icon = Icons.Rounded.Folder,
                    )
                }
                item {
                    RealResidueRow(
                        title = "بقايا التطبيقات الأخرى",
                        subtitle = "Android يحجب كاش التطبيقات الأخرى — افتح إعدادات التخزين",
                        value = "مقيد",
                        accent = V2Amber,
                        icon = Icons.Rounded.Storage,
                        onClick = { openStorageSettings(context) },
                    )
                }
                item {
                    RealResidueRow(
                        title = "حزم APK المتبقية",
                        subtitle = "غير مقروءة في Scoped Storage على Android 11",
                        value = "مقيد",
                        accent = V2Pink,
                        icon = Icons.Rounded.Description,
                    )
                }
                if (cacheFiles.isNotEmpty()) {
                    item { CacheFilesCard(cacheFiles) }
                }
                if (lastFreed != null) {
                    item {
                        V2GlassCard(
                            Modifier.fillMaxWidth(),
                            padding = 10.dp,
                            radius = 16.dp,
                            glow = V2Green,
                            borderAlpha = .45f,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Verified, null, tint = V2Green, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Column(Modifier.weight(1f)) {
                                    KText("تم التنظيف فعليًا", size = 10.sp, weight = FontWeight.Bold, color = V2Green)
                                    KText("حُرر ${formatBytes(lastFreed ?: 0L)} من كاش KNOUX.", size = 8.sp, color = V2Soft)
                                }
                            }
                        }
                    }
                }
                item { ServiceSubGrid(ServiceSpecs.residue) }
                item { ResidueSafetyCard(cacheBytes) }
                item {
                    PreviewNote(
                        "القيم المقروءة تخص كاش وملفات KNOUX على هذا الهاتف. كاش التطبيقات الأخرى " +
                            "يظل مقيدًا بنظام Android ويُدار من إعدادات النظام."
                    )
                }
            }
            V2GradientButton(
                if (cacheBytes > 0L) "تنظيف $cacheText" else "إعادة الفحص",
                onClick = {
                    if (cacheBytes > 0L) {
                        confirm = true
                    } else {
                        lastFreed = null
                        bump()
                    }
                },
                icon = if (cacheBytes > 0L) Icons.Rounded.Delete else Icons.Rounded.Radar,
                trailingChevron = true,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
            )
        }
    }
}

@Composable
private fun ResidueTitle() {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(43.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(listOf(V2Cyan.copy(alpha = .28f), V2Violet.copy(alpha = .30f))))
                .border(1.dp, V2Cyan.copy(alpha = .42f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.Layers, null, tint = Color.White, modifier = Modifier.size(23.dp))
        }
        Spacer(Modifier.width(9.dp))
        Column(Modifier.weight(1f)) {
            androidx.compose.material3.Text(
                "منظف بقايا التطبيقات",
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = V2Font, fontWeight = FontWeight.Black, fontSize = 24.sp, color = Color.White
                )
            )
            KText("البقايا والملفات المؤقتة غير الضرورية", size = 9.sp, color = V2Soft)
        }
    }
}

@Composable
private fun ResidueHeroCard(cacheBytes: Long, fileCount: Int, freeBytes: Long) {
    val progress = if (cacheBytes + freeBytes > 0L) {
        (cacheBytes.toFloat() / (cacheBytes + freeBytes).toFloat()).coerceIn(0f, 1f)
    } else 0f
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 12.dp,
        radius = 22.dp,
        glow = V2Violet,
        borderAlpha = .52f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2NeonRing(
                progress = progress,
                size = 113.dp,
                stroke = 8.dp,
                colors = listOf(V2Cyan, V2Blue, V2Violet, V2Pink),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    KText(formatBytes(cacheBytes), size = 17.sp, weight = FontWeight.Black)
                    KText("كاش KNOUX", size = 8.sp, color = V2Soft)
                    Icon(Icons.Rounded.Verified, null, tint = V2Green, modifier = Modifier.size(11.dp))
                }
            }
            Spacer(Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                NeonTrashCan(Modifier.size(72.dp))
                KText("${formatCount(fileCount)} ملفًا", size = 8.2.sp, color = V2Soft)
            }
            Spacer(Modifier.width(6.dp))
        }
    }
}

@Composable
private fun NeonTrashCan(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(listOf(V2Violet.copy(alpha = .25f), Color.Transparent)),
                radius = size.width * .56f,
            )
            drawOval(
                brush = Brush.horizontalGradient(
                    listOf(Color.Transparent, V2Pink.copy(alpha = .52f), V2Cyan.copy(alpha = .30f), Color.Transparent)
                ),
                topLeft = Offset(size.width * .10f, size.height * .78f),
                size = androidx.compose.ui.geometry.Size(size.width * .80f, 3.dp.toPx()),
            )
            val body = androidx.compose.ui.graphics.Path().apply {
                moveTo(size.width * .27f, size.height * .28f)
                lineTo(size.width * .73f, size.height * .28f)
                lineTo(size.width * .67f, size.height * .83f)
                lineTo(size.width * .33f, size.height * .83f)
                close()
            }
            drawPath(
                body,
                Brush.verticalGradient(listOf(V2Violet.copy(alpha = .85f), Color(0xFF150B33)))
            )
            drawPath(
                body,
                brush = Brush.verticalGradient(listOf(V2Cyan.copy(alpha = .85f), V2Pink.copy(alpha = .55f))),
                style = Stroke(1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
            drawRoundRect(
                color = V2Cyan.copy(alpha = .85f),
                topLeft = Offset(size.width * .22f, size.height * .20f),
                size = androidx.compose.ui.geometry.Size(size.width * .56f, size.height * .07f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()),
            )
            drawRoundRect(
                color = V2Pink.copy(alpha = .85f),
                topLeft = Offset(size.width * .41f, size.height * .14f),
                size = androidx.compose.ui.geometry.Size(size.width * .18f, size.height * .06f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()),
            )
            for (i in 0..2) {
                drawLine(
                    color = Color.White.copy(alpha = .32f),
                    start = Offset(size.width * (.40f + i * .10f), size.height * .38f),
                    end = Offset(size.width * (.38f + i * .10f), size.height * .72f),
                    strokeWidth = 1.4.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}

@Composable
private fun RealResidueRow(
    title: String,
    subtitle: String,
    value: String,
    accent: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: (() -> Unit)? = null,
) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 10.dp,
        radius = 17.dp,
        glow = accent,
        borderAlpha = .26f,
    ) {
        Row(
            Modifier.then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            V2IconTile(icon, accent, size = 36.dp, iconSize = 18.dp)
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                KText(title, size = 10.5.sp, weight = FontWeight.Bold)
                KText(subtitle, size = 7.6.sp, color = V2Soft, maxLines = 2)
            }
            Spacer(Modifier.width(6.dp))
            KText(value, size = 9.5.sp, weight = FontWeight.Bold, color = accent)
            if (onClick != null) {
                Icon(Icons.Rounded.ChevronLeft, null, tint = V2Soft, modifier = Modifier.size(15.dp))
            }
        }
    }
}

@Composable
private fun CacheFilesCard(files: List<OwnFileFact>) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 10.dp,
        radius = 17.dp,
        glow = V2Cyan,
        borderAlpha = .28f,
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Description, null, tint = V2Cyan, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(6.dp))
                KText("أكبر ملفات كاش KNOUX", size = 10.5.sp, weight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                KText("${files.size} ملف", size = 8.sp, color = V2Cyan)
            }
            Spacer(Modifier.height(6.dp))
            files.forEach { file ->
                Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Folder, null, tint = V2Muted, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(6.dp))
                    Column(Modifier.weight(1f)) {
                        KText(file.name, size = 8.2.sp, color = Color.White, maxLines = 1)
                        KText(file.folder, size = 6.8.sp, color = V2Muted, maxLines = 1)
                    }
                    KText(formatBytes(file.bytes), size = 8.sp, weight = FontWeight.Bold, color = V2Cyan)
                }
            }
        }
    }
}

@Composable
private fun ResidueSafetyCard(cacheBytes: Long) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 10.dp,
        radius = 17.dp,
        glow = V2Green,
        borderAlpha = .28f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Verified, null, tint = V2Green, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(7.dp))
            Column(Modifier.weight(1f)) {
                KText("قياس فعلي", size = 10.5.sp, weight = FontWeight.Bold, color = V2Green)
                KText("كاش KNOUX فقط — بياناتك الدائمة لا تُمس", size = 8.sp, color = V2Soft)
            }
            Spacer(Modifier.width(10.dp))
            Box(Modifier.width(1.dp).height(31.dp).background(Color.White.copy(alpha = .13f)))
            Spacer(Modifier.width(10.dp))
            Column(horizontalAlignment = Alignment.End) {
                KText(formatBytes(cacheBytes), size = 12.sp, weight = FontWeight.Bold, color = V2Cyan)
                KText("قابل للإزالة", size = 7.sp, color = V2Soft)
            }
        }
    }
}

/* ------------------------------------------------------------------ *
 * Privacy
 * ------------------------------------------------------------------ */

@Composable
fun V2PrivacyScreen(onBack: () -> Unit, onOpenDetail: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val (refresh, bump) = rememberRefreshCounter()
    val facts = rememberDeviceFacts(refresh)
    val groups = facts.privacyGroups
    val declared = groups.sumOf { it.appCount }
    val granted = groups.sumOf { it.grantedCount }
    val score = if (declared > 0) (100 - (granted * 100 / declared)).coerceIn(0, 100) else 100
    val sensitiveApps = remember(facts) {
        facts.installedApps
            .filter { it.sensitivePermissions.isNotEmpty() }
            .sortedByDescending { it.sensitivePermissions.size }
            .take(6)
    }

    Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)) {
        // The reference puts the stag on the opposite shoulder for privacy.
        V2DeerHeader(
            Modifier
                .align(Alignment.TopEnd)
                .absoluteOffset(x = (-6).dp, y = 56.dp)
                .size(width = 158.dp, height = 248.dp),
            mirrored = true,
        )
        Column(Modifier.fillMaxSize()) {
            V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = onPro)
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    ServiceBanner(
                        spec = ServiceSpecs.privacy,
                        status = "PackageManager",
                        statusColor = V2Green,
                        metric = "${formatCount(granted)}/${formatCount(declared)} إذن",
                    )
                }
                item {
                    PrivacyHeroCard(
                        score = score,
                        sensitiveApps = facts.sensitiveAppCount,
                        granted = granted,
                        declared = declared,
                    )
                }
                item { V2SectionLabel("مجالات الخصوصية", trailing = "قراءة مباشرة") }
                item { PrivacyDomainGrid(groups) { openAllAppSettings(context) } }
                item { SensitiveAppsCard(sensitiveApps) { packageName -> openAppInfo(context, packageName) } }
                item { ServiceSubGrid(ServiceSpecs.privacy) }
                item { PrivacyPreviewNote() }
            }
            V2BrightGoldButton(
                "مراجعة الأذونات في الإعدادات",
                onClick = { openAllAppSettings(context) },
                icon = Icons.Rounded.Shield,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
            )
        }
    }
}

@Composable
private fun PrivacyTitle() {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .width(4.dp)
                .height(46.dp)
                .clip(RoundedCornerShape(50))
                .background(Brush.verticalGradient(listOf(V2Violet, V2Pink)))
        )
        Spacer(Modifier.width(9.dp))
        Column(Modifier.weight(1f)) {
            androidx.compose.material3.Text(
                "الخصوصية الذكية",
                style = androidx.compose.ui.text.TextStyle(
                    fontFamily = V2Font, fontWeight = FontWeight.Black, fontSize = 27.sp, color = Color.White
                )
            )
            KText("تحكم كامل في أذوناتك وخصوصيتك بذكاء متقدم.", size = 10.sp, color = V2Soft)
        }
    }
}

@Composable
private fun PrivacyHeroCard(score: Int, sensitiveApps: Int, granted: Int, declared: Int) {
    val accent = when {
        score >= 75 -> V2Green
        score >= 50 -> V2Amber
        else -> V2Red
    }
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 12.dp,
        radius = 24.dp,
        glow = V2Violet,
        borderAlpha = .55f,
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                V2NeonRing(
                    progress = score / 100f,
                    size = 112.dp,
                    stroke = 8.dp,
                    colors = listOf(V2Amber, V2Pink, V2Violet, V2Cyan),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.Shield, null, tint = accent, modifier = Modifier.size(20.dp))
                        KText("$score%", size = 23.sp, weight = FontWeight.Black)
                        KText("مؤشر تقديري", size = 7.sp, color = V2Soft)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(accent.copy(alpha = .16f))
                            .border(1.dp, accent.copy(alpha = .50f), RoundedCornerShape(50))
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Verified, null, tint = accent, modifier = Modifier.size(13.dp))
                            Spacer(Modifier.width(4.dp))
                            KText("قراءة من PackageManager", size = 8.sp, weight = FontWeight.Bold, color = accent)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    KText(
                        "يُحسب المؤشر من نسبة الأذونات الحساسة الممنوحة فعليًا إلى المعلنة.",
                        size = 8.4.sp,
                        lineHeight = 12.sp,
                        color = V2Soft,
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PrivacyCount("${formatCount(sensitiveApps)}", "تطبيقًا حساسًا", Icons.Rounded.Shield, V2Amber)
                        Spacer(Modifier.width(12.dp))
                        Box(Modifier.width(1.dp).height(26.dp).background(Color.White.copy(alpha = .10f)))
                        Spacer(Modifier.width(12.dp))
                        PrivacyCount("${formatCount(granted)}/${formatCount(declared)}", "إذن ممنوح/معلن", Icons.Rounded.Radar, V2Violet)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            V2ProgressBar(score / 100f, height = 5.dp, colors = listOf(V2Amber, V2Pink, V2Violet, V2Cyan))
            Spacer(Modifier.height(4.dp))
            KText(
                "يمكنك سحب أي إذن من إعدادات التطبيق في Android.",
                size = 7.4.sp,
                color = V2Muted,
            )
        }
    }
}

@Composable
private fun PrivacyCount(value: String, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, accent: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = accent, modifier = Modifier.size(17.dp))
        Spacer(Modifier.width(5.dp))
        Column {
            KText(value, size = 14.sp, weight = FontWeight.Black, color = accent)
            KText(label, size = 7.2.sp, color = V2Soft, maxLines = 1)
        }
    }
}

private fun privacyDomainIcon(key: String): androidx.compose.ui.graphics.vector.ImageVector = when (key) {
    "camera" -> Icons.Rounded.CameraAlt
    "microphone" -> Icons.Rounded.Mic
    "location" -> Icons.Rounded.LocationOn
    "files" -> Icons.Rounded.Folder
    "contacts" -> Icons.Rounded.Person
    "notifications" -> Icons.Rounded.NotificationsActive
    else -> Icons.Rounded.Shield
}

private fun privacyDomainAccent(key: String): Color = when (key) {
    "camera" -> V2Pink
    "microphone" -> V2Amber
    "location" -> V2Cyan
    "files" -> V2Violet
    "contacts" -> V2Blue
    "notifications" -> V2Green
    else -> V2Soft
}

@Composable
private fun PrivacyDomainGrid(groups: List<AppPermissionGroup>, onOpen: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        groups.chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                row.forEach { group ->
                    PrivacyDomainCard(group, Modifier.weight(1f), onOpen)
                }
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun PrivacyDomainCard(group: AppPermissionGroup, modifier: Modifier, onClick: () -> Unit) {
    val accent = privacyDomainAccent(group.key)
    V2GlassCard(
        modifier.height(93.dp).clickable(onClick = onClick),
        padding = 8.dp,
        radius = 16.dp,
        glow = accent,
        borderAlpha = .35f,
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            V2IconTile(privacyDomainIcon(group.key), accent, size = 31.dp, iconSize = 16.dp)
            Spacer(Modifier.height(4.dp))
            KText(group.label, size = 8.4.sp, weight = FontWeight.Bold, maxLines = 1, align = TextAlign.Center)
            KText(
                "${formatCount(group.grantedCount)} ممنوح · ${formatCount(group.appCount)} معلن",
                size = 7.sp,
                color = V2Soft,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun SensitiveAppsCard(
    apps: List<InstalledAppFacts>,
    onOpenApp: (String) -> Unit,
) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Violet, borderAlpha = .42f) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Shield, null, tint = V2Amber, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(6.dp))
                KText("تطبيقات تطلب أذونات حساسة", size = 12.sp, weight = FontWeight.Black)
                Spacer(Modifier.weight(1f))
                KText("${formatCount(apps.size)}", size = 7.6.sp, color = V2Muted)
            }
            Spacer(Modifier.height(9.dp))
            if (apps.isEmpty()) {
                KText(
                    "لا توجد تطبيقات معلنة لأي من الأذونات الحساسة المرصودة.",
                    size = 8.4.sp,
                    color = V2Soft,
                )
            } else {
                apps.forEachIndexed { index, app ->
                    Row(
                        Modifier.fillMaxWidth().clickable { onOpenApp(app.packageName) },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AppIconImage(packageName = app.packageName, size = 34.dp)
                        Spacer(Modifier.width(9.dp))
                        Column(Modifier.weight(1f)) {
                            KText(app.label, size = 9.2.sp, weight = FontWeight.Bold, maxLines = 1)
                            KText(
                                "${formatCount(app.sensitivePermissions.size)} أذونات حساسة معلنة · ${app.packageName}",
                                size = 6.9.sp,
                                color = V2Muted,
                                maxLines = 1,
                            )
                        }
                        SummaryPill(if (app.isSystem) "نظام" else "مستخدم", if (app.isSystem) V2Violet else V2Cyan)
                    }
                    if (index < apps.lastIndex) {
                        Spacer(Modifier.height(6.dp))
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color.White.copy(alpha = .07f))
                        )
                        Spacer(Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AppIconImage(packageName: String, size: Dp) {
    val bitmap = rememberAppIcon(packageName)
    Box(
        Modifier
            .size(size)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = .06f))
            .border(1.dp, Color.White.copy(alpha = .12f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center,
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Icon(
                Icons.Rounded.Shield,
                null,
                tint = V2Cyan,
                modifier = Modifier.size(size * .55f),
            )
        }
    }
}

@Composable
private fun PrivacyPreviewNote() {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Rounded.Check, null, tint = V2Amber, modifier = Modifier.size(11.dp))
        Spacer(Modifier.width(5.dp))
        KText(
            "العدّ من PackageManager: الأذونات المعلنة في كل تطبيق وما مُنح منها فعليًا. " +
                "لا يدّعي التطبيق قراءة سجل تغييرات الأذونات.",
            size = 7.2.sp,
            color = V2Muted,
        )
    }
}

/** إعادة استعلام MediaStore بعد نية الحذف: URI غير مقروءة = تأكيد الحذف فعليًا. */
private fun mediaUriGone(context: android.content.Context, uriString: String): Boolean =
    runCatching {
        context.contentResolver.openInputStream(android.net.Uri.parse(uriString))?.use { }
    }.getOrDefault(false) == false
