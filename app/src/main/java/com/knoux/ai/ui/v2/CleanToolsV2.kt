package com.knoux.ai.ui.v2

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.IntentSender
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Cached
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material.icons.rounded.Verified
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

/* ------------------------------------------------------------------ *
 * Smart Clean - fifteen separately routed services
 *
 * Each route below is a real screen. Numbers come from DeviceFacts /
 * MediaStore / PackageManager or read as unavailable; no fixture lists.
 * ------------------------------------------------------------------ */

internal data class CleanToolDef(
    val route: String,
    val order: String,
    val title: String,
    val english: String,
    val tagline: String,
    val icon: ImageVector,
    val accent: Color,
    val accent2: Color,
    val proOnly: Boolean = false,
)

internal object CleanTools {
    val defs = listOf(
        CleanToolDef(
            "clean-scope", "01.1", "نطاق التنظيف", "SCOPE SELECTION",
            "التخزين والذاكرة وملفات التطبيقات على هذا الهاتف",
            Icons.Rounded.Settings, V2Cyan, V2Blue,
        ),
        CleanToolDef(
            "clean-scan", "01.2", "فحص الملفات", "CLEANABLE SCAN",
            "كاش KNOUX ومجموعات الوسائط المطابقة",
            Icons.Rounded.Radar, V2Cyan, V2Pink,
        ),
        CleanToolDef(
            "clean-classify", "01.3", "تصنيف الملفات", "FILE CLASSIFICATION",
            "الصور والفيديو والكاش وحجم النظام من قراءة واحدة",
            Icons.Rounded.Layers, V2Violet, V2Cyan,
            proOnly = true,
        ),
        CleanToolDef(
            "clean-filters", "01.4", "حدود الحجم والعمر", "SIZE & AGE FILTERS",
            "حدّد حدًا أدنى للحجم واعرض ما يطابقه فعليًا",
            Icons.Rounded.Speed, V2Amber, V2Pink,
            proOnly = true,
        ),
        CleanToolDef(
            "clean-preview", "01.5", "معاينة المرشحات", "CANDIDATE PREVIEW",
            "أكبر ملفات الوسائط كما يعيدها MediaStore",
            Icons.Rounded.Image, V2Pink, V2Violet,
        ),
        CleanToolDef(
            "clean-protected", "01.6", "الملفات المحمية", "PROTECTED ITEMS",
            "ما يمنعه Android وحدود Scoped Storage بوضوح",
            Icons.Rounded.Shield, V2Green, V2Cyan,
            proOnly = true,
        ),
        CleanToolDef(
            "clean-selection", "01.7", "التحديد الفردي والجماعي", "BATCH SELECTION",
            "مجموعات التكرار الحقيقية قبل أي إجراء",
            Icons.Rounded.CheckCircle, V2Violet, V2Pink,
        ),
        CleanToolDef(
            "clean-impact", "01.8", "مراجعة المساحة", "SPACE IMPACT",
            "المساحة المستخدمة والمتاحة والأثر المحتمل",
            Icons.Rounded.Storage, V2Cyan, V2Green,
            proOnly = true,
        ),
        CleanToolDef(
            "clean-confirm", "01.9", "تأكيد النقل أو الحذف", "CONFIRM DELETE",
            "الحذف يمر دائمًا عبر حوار Android الرسمي",
            Icons.Rounded.Delete, V2Pink, V2Amber,
        ),
        CleanToolDef(
            "clean-history", "01.10", "التحقق والسجل", "POST-CLEAN HISTORY",
            "سجل الفحوص المحفوظة على هذا الهاتف",
            Icons.Rounded.Timeline, V2Green, V2Cyan,
            proOnly = true,
        ),
        CleanToolDef(
            "clean-large", "01.11", "عدسة الملفات الكبيرة", "LARGE FILE LENS",
            "أكبر الملفات المقروءة وفرزها قبل أي قرار",
            Icons.Rounded.FolderOpen, V2Amber, V2Pink,
        ),
        CleanToolDef(
            "clean-media-mix", "01.12", "مزيج الصور والفيديو", "MEDIA MIX",
            "عدد وحجم الوسائط المقروءة من MediaStore",
            Icons.Rounded.PhotoLibrary, V2Pink, V2Violet,
        ),
        CleanToolDef(
            "clean-buckets", "01.13", "المجلدات الأثقل", "HEAVY BUCKETS",
            "تجميع الملفات المقروءة حسب مجلد المصدر",
            Icons.Rounded.Folder, V2Violet, V2Cyan,
        ),
        CleanToolDef(
            "clean-free-plan", "01.14", "خطة المساحة الحرة", "FREE SPACE PLAN",
            "ما يمكن استرجاعه وما يبقى محميًا",
            Icons.Rounded.PieChart, V2Cyan, V2Green,
        ),
        CleanToolDef(
            "clean-cache-audit", "01.15", "تدقيق كاش KNOUX", "KNOUX CACHE AUDIT",
            "الحجم والملفات المؤقتة داخل مساحة التطبيق",
            Icons.Rounded.Cached, V2Green, V2Amber,
            proOnly = true,
        ),
    )

    val items: List<ServiceSubItem>
        get() = defs.mapIndexed { index, def ->
            ServiceSubItem(
                title = def.title,
                subtitle = def.tagline,
                route = def.route,
                icon = def.icon,
                accent = def.accent,
                state = ServiceState.LIVE,
                proOnly = def.proOnly,
                art = SectionArt.forIndex("smartclean", index),
            )
        }

    val routes: List<String> get() = defs.map { it.route }

    fun byRoute(route: String): CleanToolDef? = defs.firstOrNull { it.route == route }

    fun bannerSpec(tool: CleanToolDef): KnouxServiceSpec = KnouxServiceSpec(
        key = tool.route,
        order = tool.order,
        title = tool.title,
        english = tool.english,
        tagline = tool.tagline,
        accent = tool.accent,
        accent2 = tool.accent2,
        glyph = tool.icon,
        chips = listOf("قراءة مباشرة", "بدون أرقام تجريبية"),
        subItems = emptyList(),
        pro = tool.proOnly,
    )
}

@Composable
fun V2CleanToolScreen(route: String, onBack: () -> Unit, onPro: () -> Unit) {
    val tool = CleanTools.byRoute(route) ?: return
    val context = LocalContext.current
    val navigate = LocalServiceNavigate.current
    val (refresh, bump) = rememberRefreshCounter()
    val facts = rememberDeviceFacts(refresh)
    val history = remember(refresh) { readScanHistory(context) }
    val storage = facts.storage
    val media = facts.media
    val cache = facts.ownCacheBytes
    val total = storage.totalBytes.coerceAtLeast(1L)
    val other = (storage.usedBytes - cache - (if (media.permissionGranted) media.imagesBytes + media.videosBytes else 0L))
        .coerceAtLeast(0L)
    val loading = facts.computedAt == 0L
    val failed = !loading && !facts.isLive
    val storageUnread = !loading && facts.isLive && !storage.isMeasured
    val mediaUnread = !loading && facts.isLive && media.permissionGranted && !media.readSucceeded
    val metric = when {
        loading -> "جاري القراءة"
        failed -> "غير متاح"
        !storage.isMeasured -> "مساحة التخزين غير مقروءة"
        else -> "${formatBytes(storage.freeBytes)} متاحة"
    }

    // Cleaning workflow ViewModel for shared state across 15 routes
    val workflowViewModel: CleaningWorkflowViewModel = viewModel()
    val workflowState = workflowViewModel.state

    // Launcher for MediaStore.createDeleteRequest system dialog
    val deleteRequestLauncher = rememberLauncherForActivityResult<IntentSenderRequest, ActivityResult>(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            workflowViewModel.onMediaStoreDeleteResult(true)
        } else {
            workflowViewModel.onMediaStoreDeleteResult(false)
        }
    }

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
                val scope = this
                item {
                    ServiceBanner(
                        spec = CleanTools.bannerSpec(tool),
                        status = when {
                            loading -> "جاري القراءة"
                            failed -> "تعذّرت القراءة"
                            else -> "قراءة مباشرة"
                        },
                        statusColor = when {
                            loading -> V2Amber
                            failed -> V2Red
                            else -> V2Green
                        },
                        metric = metric,
                        showIdentityLine = false,
                    )
                }
                item {
                    ServiceVisualDeck(
                        section = "smartclean",
                        facts = facts,
                        accent = V2Cyan,
                        accent2 = V2Pink,
                        history = history,
                    )
                }
                if (failed) {
                    item {
                        ServiceCapabilityCard(
                            title = "تعذّر إكمال قراءة الجهاز",
                            body = "لم تُعد قراءة التخزين أو التطبيقات أو الوسائط أي قيمة. لن نعرض أصفاراً كأنها نتيجة ناجحة.",
                            state = ServiceState.PERMISSION,
                            actionLabel = "إعادة القراءة",
                            onAction = bump,
                        )
                    }
                } else {
                    if (storageUnread) {
                        item {
                            ServiceCapabilityCard(
                                title = "مساحة التخزين غير مقروءة",
                                body = "StatFs لم يُعد حجماً لهذا الحجم. القيم المعتمدة على المساحة تبقى غير متاحة حتى تنجح إعادة القراءة.",
                                state = ServiceState.RESTRICTED,
                                actionLabel = "إعادة القراءة",
                                onAction = bump,
                            )
                        }
                    }
                    if (mediaUnread) {
                        item {
                            ServiceCapabilityCard(
                                title = "مكتبة الوسائط غير مقروءة",
                                body = "الإذن ممنوح لكن استعلام MediaStore لم يكتمل. الصفر هنا ليس «لا صور»؛ أعد القراءة قبل الاعتماد على أي رقم.",
                                state = ServiceState.RESTRICTED,
                                actionLabel = "إعادة القراءة",
                                onAction = bump,
                            )
                        }
                    }
                    when (tool.route) {
                        "clean-scope" -> scope.scopeItems(facts, workflowState, workflowViewModel, navigate)
                        "clean-scan" -> scope.scanItems(facts, workflowState, workflowViewModel, navigate)
                        "clean-classify" -> scope.classifyItems(facts, workflowState, workflowViewModel, total, other)
                        "clean-filters" -> scope.filterItems(facts, workflowState, workflowViewModel)
                        "clean-preview" -> scope.previewItems(facts, workflowState, workflowViewModel)
                        "clean-protected" -> scope.protectedItems(facts, workflowState, workflowViewModel)
                        "clean-selection" -> scope.selectionItems(facts, workflowState, workflowViewModel, navigate)
                        "clean-impact" -> scope.impactItems(facts, workflowState, workflowViewModel, navigate)
                        "clean-confirm" -> scope.confirmItems(facts, workflowState, workflowViewModel, navigate, deleteRequestLauncher)
                        "clean-history" -> scope.historyItems(history, workflowState, workflowViewModel, navigate)
                        "clean-large" -> scope.largeItems(facts, workflowState, workflowViewModel, navigate)
                        "clean-media-mix" -> scope.mediaMixItems(facts, workflowState, workflowViewModel, navigate)
                        "clean-buckets" -> scope.bucketItems(facts, workflowState, workflowViewModel, navigate)
                        "clean-free-plan" -> scope.freePlanItems(facts, workflowState, workflowViewModel, navigate)
                        "clean-cache-audit" -> scope.cacheAuditItems(facts, workflowState, workflowViewModel, navigate)
                    }
                }
                item {
                    PreviewNote(
                        when {
                            failed -> "لا توجد قيم معروضة بدلاً من قراءة فاشلة."
                            storageUnread || mediaUnread -> "بعض القراءات غير مكتملة وموضحة أعلاه؛ لا تُعرض كأصفار ناجحة."
                            else -> "كل الأرقام أعلاه مقروءة من هذا الهاتف الآن. لا تُعرض قيم تقديرية بدلًا من القراءة الفعلية."
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

/* ----------------------------- content ----------------------------- */

private fun androidx.compose.foundation.lazy.LazyListScope.scopeItems(
    facts: DeviceFacts,
    workflowState: CleaningWorkflowState,
    workflowViewModel: CleaningWorkflowViewModel,
    navigate: (String) -> Unit,
) {
    item { V2SectionLabel("نطاق القراءة", trailing = "هذا الهاتف") }
    item {
        V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Cyan, borderAlpha = .32f) {
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                CleanFactRow(Icons.Rounded.Storage, "التخزين", "${formatBytes(facts.storage.usedBytes)} / ${formatBytes(facts.storage.totalBytes)}", V2Cyan)
                CleanFactRow(Icons.Rounded.CleaningServices, "كاش KNOUX", formatBytes(facts.ownCacheBytes), V2Pink)
                CleanFactRow(Icons.Rounded.Apps, "التطبيقات", "${formatCount(facts.installedCount)} تطبيق", V2Violet)
                CleanFactRow(Icons.Rounded.Layers, "الوسائط", if (facts.media.permissionGranted) formatBytes(facts.media.imagesBytes + facts.media.videosBytes) else "بعد منح الصلاحية", V2Amber)
            }
        }
    }
    item {
        CleanActionCard(
            title = "نطاق التخزين الكامل",
            body = "تفاصيل الفئات وأكبر الملفات تُقرأ من StatFs وMediaStore.",
            action = "فتح تحليل التخزين",
            icon = Icons.Rounded.Storage,
            onClick = { navigate("storage-breakdown") },
        )
    }
    // Show scan status from workflow
    if (workflowState.scanStatus != ScanStatus.IDLE) {
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 16.dp, glow = V2Cyan, borderAlpha = .24f) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row {
                        V2IconTile(Icons.Rounded.Radar, V2Cyan, size = 28.dp, iconSize = 14.dp)
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            KText("حالة الفحص: ${workflowState.scanMessage}", size = 9.5.sp, weight = FontWeight.Bold, color = V2Cyan)
                            if (workflowState.scanProgress > 0f) {
                                androidx.compose.foundation.layout.Box(
                                    Modifier.fillMaxWidth().height(4.dp).background(V2Cyan.withAlpha(.15f)),
                                    contentAlignment = Alignment.CenterStart,
                                ) {
                                    androidx.compose.foundation.layout.Box(
                                        Modifier
                                            .width((workflowState.scanProgress * 100).toInt().dp)
                                            .height(4.dp)
                                            .background(V2Cyan)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    item {
        CleanActionCard(
            title = if (workflowState.scanStatus == ScanStatus.SCANNING) "جاري الفحص..." else "بدء فحص كامل",
            body = "فحص التخزين والتطبيقات والوسائط والكاش مع حفظ النتيجة في السجل المحلي.",
            action = if (workflowState.scanStatus == ScanStatus.SCANNING) "انتظر" else "بدء الفحص",
            icon = Icons.Rounded.Radar,
            onClick = {
                if (workflowState.scanStatus != ScanStatus.SCANNING) {
                    workflowViewModel.startFullScan()
                }
            },
            enabled = workflowState.scanStatus != ScanStatus.SCANNING,
        )
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.scanItems(
    facts: DeviceFacts,
    workflowState: CleaningWorkflowState,
    workflowViewModel: CleaningWorkflowViewModel,
    navigate: (String) -> Unit,
) {
    item { V2SectionLabel("نتيجة الفحص", trailing = if (facts.computedAt > 0L) "مباشر" else "بانتظار القراءة") }
    item {
        V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Cyan, borderAlpha = .34f) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                V2IconTile(Icons.Rounded.Radar, V2Cyan, size = 42.dp, iconSize = 21.dp)
                Spacer(Modifier.width(9.dp))
                Column(Modifier.weight(1f)) {
                    KText("${formatCount(facts.media.duplicateGroups.size)} مجموعة تكرار", size = 15.sp, weight = FontWeight.Black, color = V2Cyan)
                    KText("${formatCount(facts.media.duplicateGroups.sumOf { it.count - 1 })} ملف زائد · ${formatBytes(facts.media.duplicateGroups.sumOf { it.reclaimableBytes })}", size = 9.sp, color = V2Soft)
                    Spacer(Modifier.height(3.dp))
                    KText("كاش KNOUX: ${formatBytes(facts.ownCacheBytes)} · ${formatCount(facts.ownCacheFiles)} ملف", size = 9.sp, color = V2Muted)
                }
            }
        }
    }
    item {
        CleanActionCard(
            title = "تشغيل الفحص الكامل",
            body = "الفحص العميق يقرأ التخزين والتطبيقات والخصوصية والبطارية ويحفظ النتيجة محليًا.",
            action = "فتح الفحص العميق",
            icon = Icons.Rounded.Radar,
            onClick = { navigate("deepscan") },
        )
    }
    // Show workflow scan results if available
    workflowState.scanResult?.let { result ->
        item { V2SectionLabel("نتائج الفحص المشترك", trailing = "${result.duplicateGroups.size} مجموعة تكرار") }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 16.dp, glow = V2Cyan, borderAlpha = .24f) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    CleanFactRow(Icons.Rounded.Layers, "مجموعات التكرار", "${result.duplicateGroups.size}", V2Violet)
                    CleanFactRow(Icons.Rounded.Delete, "مساحة قابلة للاسترجاع", formatBytes(result.reclaimableDuplicateBytes), V2Pink)
                    CleanFactRow(Icons.Rounded.Cached, "كاش KNOUX", formatBytes(result.cacheBytes), V2Amber)
                    CleanFactRow(Icons.Rounded.Image, "الصور", formatBytes(result.imagesBytes), V2Pink)
                    CleanFactRow(Icons.Rounded.Layers, "الفيديو", formatBytes(result.videosBytes), V2Violet)
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.classifyItems(
    facts: DeviceFacts,
    workflowState: CleaningWorkflowState,
    workflowViewModel: CleaningWorkflowViewModel,
    total: Long,
    other: Long,
) {
    item { V2SectionLabel("التصنيف", trailing = "من MediaStore") }
    item {
        V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Violet, borderAlpha = .32f) {
            Column {
                SegmentedBar(
                    listOf(
                        (facts.media.imagesBytes.toFloat() / total).coerceIn(0f, 1f) to V2Pink,
                        (facts.media.videosBytes.toFloat() / total).coerceIn(0f, 1f) to V2Violet,
                        (facts.ownCacheBytes.toFloat() / total).coerceIn(0f, 1f) to V2Cyan,
                        (other.toFloat() / total).coerceIn(0f, 1f) to V2Amber,
                    ),
                    height = 11,
                )
                Spacer(Modifier.height(10.dp))
                CleanFactRow(Icons.Rounded.Image, "الصور", if (facts.media.permissionGranted) formatBytes(facts.media.imagesBytes) else "بعد المنح", V2Pink)
                Spacer(Modifier.height(7.dp))
                CleanFactRow(Icons.Rounded.Layers, "الفيديو", if (facts.media.permissionGranted) formatBytes(facts.media.videosBytes) else "بعد المنح", V2Violet)
                Spacer(Modifier.height(7.dp))
                CleanFactRow(Icons.Rounded.CleaningServices, "كاش KNOUX", formatBytes(facts.ownCacheBytes), V2Cyan)
                Spacer(Modifier.height(7.dp))
                CleanFactRow(Icons.Rounded.Storage, "النظام والتطبيقات", formatBytes(other), V2Amber)
            }
        }
    }
    if (!facts.media.permissionGranted) {
        item {
            ServiceCapabilityCard(
                title = "صلاحية الوسائط مطلوبة",
                body = "لن يقرأ KNOUX الصور والفيديو قبل موافقتك. امنح الصلاحية ثم حدّث القراءة.",
                state = ServiceState.PERMISSION,
            )
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.filterItems(
    facts: DeviceFacts,
    workflowState: CleaningWorkflowState,
    workflowViewModel: CleaningWorkflowViewModel,
) {
    item { FilterBody(facts, workflowViewModel) }
    item { PreviewNote("عمر الملف غير مقروء في هذه النسخة لأن MediaStore لم يُطلب منه DATE_ADDED بعد؛ لا نعرض عمرًا تقديريًا.") }
}

@Composable
private fun FilterBody(facts: DeviceFacts, workflowViewModel: CleaningWorkflowViewModel) {
    var minMb by remember { mutableIntStateOf(50) }
    val filtered = facts.media.largestFiles.filter { it.sizeBytes >= minMb.toLong() * 1024L * 1024L }
    V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Amber, borderAlpha = .32f) {
        Column {
            KText("الحد الأدنى للحجم", size = 9.5.sp, weight = FontWeight.Bold, color = V2Amber)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(10, 50, 100, 500).forEach { value ->
                    Box(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (minMb == value) V2Amber.withAlpha(.20f) else Color.White.withAlpha(.04f))
                            .border(1.dp, if (minMb == value) V2Amber.withAlpha(.55f) else Color.White.withAlpha(.10f), RoundedCornerShape(12.dp))
                            .clickable { minMb = value }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        KText("${value}MB", size = 9.sp, weight = FontWeight.Bold, color = if (minMb == value) V2Amber else V2Soft)
                    }
                }
            }
            Spacer(Modifier.height(7.dp))
            KText(
                "${formatCount(filtered.size)} ملف يطابق الحد · ${formatBytes(filtered.sumOf { it.sizeBytes })}",
                size = 9.sp,
                color = V2Muted,
            )
            // Sync with workflow filter state
            if (workflowViewModel.state.filterState.minSizeMb != minMb) {
                workflowViewModel.applyFilter(minMb)
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.previewItems(
    facts: DeviceFacts,
    workflowState: CleaningWorkflowState,
    workflowViewModel: CleaningWorkflowViewModel,
) {
    item { V2SectionLabel("أكبر الملفات", trailing = if (facts.media.permissionGranted) "MediaStore" else "بعد المنح") }
    if (!facts.media.permissionGranted) {
        item {
            ServiceCapabilityCard(
                title = "صلاحية الصور والفيديو مطلوبة",
                body = "لا يقرأ KNOUX مكتبتك قبل موافقتك. امنح READ_EXTERNAL_STORAGE ثم حدّث القراءة.",
                state = ServiceState.PERMISSION,
            )
        }
    } else if (facts.media.largestFiles.isEmpty()) {
        item {
            ServiceCapabilityCard(
                title = "لا ملفات وسائط لعرضها",
                body = "فُحصت المكتبة المصرح بها ولم تُعد MediaStore أي ملفات قابلة للعرض.",
                state = ServiceState.LIVE,
            )
        }
    } else {
        facts.media.largestFiles.take(10).forEach { file ->
            item {
                ServiceFileCard(
                    file = file,
                    accent = if (file.isVideo) V2Pink else V2Violet,
                )
            }
        }
        // Show workflow selection state
        if (workflowState.selection.selectedUris.isNotEmpty()) {
            item {
                V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 16.dp, glow = V2Cyan, borderAlpha = .24f) {
                    KText("${workflowState.selection.selectedUris.size} ملف محدد · ${formatBytes(workflowState.selection.selectedBytes)}", size = 9.sp, color = V2Cyan)
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.protectedItems(
    facts: DeviceFacts,
    workflowState: CleaningWorkflowState,
    workflowViewModel: CleaningWorkflowViewModel,
) {
    item {
        V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Green, borderAlpha = .34f) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                V2IconTile(Icons.Rounded.Shield, V2Green, size = 42.dp, iconSize = 21.dp)
                Spacer(Modifier.width(9.dp))
                Column(Modifier.weight(1f)) {
                    KText("الحماية التي يفرضها Android", size = 11.sp, weight = FontWeight.Black, color = V2Green)
                    Spacer(Modifier.height(3.dp))
                    KText(
                        "الملفات الخاصة بتطبيق آخر لا يمكن قراءتها أو حذفها من KNOUX. " +
                            "حتى مجلدات التطبيق نفسه تُمسح فقط بعد تأكيدك.",
                        size = 9.sp,
                        lineHeight = 12.sp,
                        color = V2Soft,
                    )
                }
            }
        }
    }
    item { V2SectionLabel("حالة القراءة", trailing = "بدون تجاوز") }
    item {
        V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Cyan, borderAlpha = .28f) {
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                CleanFactRow(Icons.Rounded.Folder, "مجلد KNOUX الخاص", "مقروء بعد موافقتك", V2Green)
                CleanFactRow(Icons.Rounded.Shield, "مجلدات التطبيقات الأخرى", "محمية بواسطة Android", V2Amber)
                CleanFactRow(Icons.Rounded.Image, "مكتبة الوسائط", if (facts.media.permissionGranted) "مقروءة" else "بانتظار الصلاحية", V2Cyan)
            }
        }
    }
    // Show protected items from workflow
    if (workflowState.selection.protectedUris.isNotEmpty()) {
        item { V2SectionLabel("ملفات محمية في الجلسة", trailing = "${workflowState.selection.protectedUris.size} ملف") }
        workflowState.selection.protectedUris.take(5).forEach { uri ->
            item {
                V2GlassCard(Modifier.fillMaxWidth(), padding = 10.dp, radius = 12.dp, glow = V2Green, borderAlpha = .2f) {
                    Row {
                        Icon(Icons.Rounded.Shield, null, tint = V2Green, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        KText(uri.take(60), size = 9.sp, color = V2Soft, modifier = Modifier.weight(1f))
                        IconButton(onClick = { workflowViewModel.removeProtected(uri) }) {
                            Icon(Icons.Rounded.Close, null, tint = V2Amber, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.selectionItems(
    facts: DeviceFacts,
    workflowState: CleaningWorkflowState,
    workflowViewModel: CleaningWorkflowViewModel,
    navigate: (String) -> Unit,
) {
    item { V2SectionLabel("مجموعات حقيقية", trailing = "${facts.media.duplicateGroups.size} مجموعة") }
    if (facts.media.duplicateGroups.isEmpty()) {
        item {
            ServiceCapabilityCard(
                title = "لا مجموعات تكرار حالية",
                body = "لم تظهر نسخ متطابقة في نطاق الوسائط المقروءة.",
                state = ServiceState.LIVE,
            )
        }
    } else {
        facts.media.duplicateGroups.take(6).forEach { group ->
            item {
                ServiceGroupCard(
                    group = group,
                    accent = V2Violet,
                )
            }
        }
    }
    item {
        CleanActionCard(
            title = "فتح التحديد والحذف",
            body = "شاشة الملفات المكررة تعرض كل مجموعة، وتستخدم حوار MediaStore الرسمي للحذف.",
            action = "فتح الملفات المكررة",
            icon = Icons.Rounded.Layers,
            onClick = { navigate("duplicates") },
        )
    }
    // Show workflow selection state
    if (workflowState.selection.selectedUris.isNotEmpty()) {
        item { V2SectionLabel("التحديد الحالي", trailing = "${workflowState.selection.selectedUris.size} ملف · ${formatBytes(workflowState.selection.selectedBytes)}") }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 16.dp, glow = V2Cyan, borderAlpha = .24f) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row {
                        KText("ملفات محددة: ${workflowState.selection.selectedUris.size}", size = 9.sp, weight = FontWeight.Bold, color = V2Cyan)
                        Spacer(Modifier.width(8.dp))
                        KText("إجمالي: ${formatBytes(workflowState.selection.selectedBytes)}", size = 9.sp, color = V2Soft)
                    }
                    Row {
                        CleanActionCard(
                            title = "تحديد الكل المؤهل",
                            body = "يحدد جميع الملفات القابلة للحذف مع استثناء المحمية",
                            action = "تحديد",
                            icon = Icons.Rounded.CheckCircle,
                            onClick = { workflowViewModel.selectAllEligible() },
                        )
                        Spacer(Modifier.width(8.dp))
                        CleanActionCard(
                            title = "إلغاء التحديد",
                            body = "يمسح كل التحديدات الحالية",
                            action = "مسح",
                            icon = Icons.Rounded.Delete,
                            onClick = { workflowViewModel.clearSelection() },
                        )
                    }
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.impactItems(
    facts: DeviceFacts,
    workflowState: CleaningWorkflowState,
    workflowViewModel: CleaningWorkflowViewModel,
    navigate: (String) -> Unit,
) {
    item { V2SectionLabel("المساحة الآن", trailing = "${(facts.storage.usedFraction * 100).toInt()}% مستخدمة") }
    item {
        V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Cyan, borderAlpha = .34f) {
            Column {
                SegmentedBar(
                    listOf(
                        facts.storage.usedFraction.coerceIn(0f, 1f) to V2Cyan,
                        (1f - facts.storage.usedFraction).coerceIn(0f, 1f) to V2Violet,
                    ),
                    height = 11,
                )
                Spacer(Modifier.height(9.dp))
                CleanFactRow(Icons.Rounded.Storage, "مستخدم", formatBytes(facts.storage.usedBytes), V2Cyan)
                Spacer(Modifier.height(7.dp))
                CleanFactRow(Icons.Rounded.Verified, "متاح", formatBytes(facts.storage.freeBytes), V2Green)
                Spacer(Modifier.height(7.dp))
                CleanFactRow(Icons.Rounded.Layers, "قابل للاسترجاع من التكرار", formatBytes(facts.media.duplicateGroups.sumOf { it.reclaimableBytes }), V2Pink)
            }
        }
    }
    item {
        CleanActionCard(
            title = "مراجعة الفئات",
            body = "الصور والفيديو والنظام والتطبيقات موزعة من قراءة StatFs وMediaStore.",
            action = "فتح تفصيل التخزين",
            icon = Icons.Rounded.Storage,
            onClick = { navigate("storage-breakdown") },
        )
    }
    // Show workflow impact if selection exists
    if (workflowState.selection.selectedUris.isNotEmpty()) {
        item { V2SectionLabel("أثر التحديد الحالي", trailing = "مباشر") }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 16.dp, glow = V2Pink, borderAlpha = .24f) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    CleanFactRow(Icons.Rounded.Delete, "سيتم تحرير", formatBytes(workflowState.selection.selectedBytes), V2Pink)
                    CleanFactRow(Icons.Rounded.CheckCircle, "ملفات محددة", "${workflowState.selection.selectedUris.size}", V2Cyan)
                    CleanFactRow(Icons.Rounded.Shield, "محمية", "${workflowState.selection.protectedUris.size}", V2Green)
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.confirmItems(
    facts: DeviceFacts,
    workflowState: CleaningWorkflowState,
    workflowViewModel: CleaningWorkflowViewModel,
    navigate: (String) -> Unit,
    deleteRequestLauncher: ActivityResultLauncher<IntentSenderRequest>,
) {
    item {
        V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Pink, borderAlpha = .34f) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    V2IconTile(Icons.Rounded.Delete, V2Pink, size = 40.dp, iconSize = 20.dp)
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        KText("الحذف ليس تلقائيًا", size = 11.sp, weight = FontWeight.Black, color = V2Pink)
                        KText("لا يوجد زر يحذف شيئًا بدون حوار Android الرسمي.", size = 9.sp, color = V2Soft)
                    }
                }
                Spacer(Modifier.height(9.dp))
                KText(
                    "عدد المجموعات المتاحة الآن: ${formatCount(facts.media.duplicateGroups.size)} · " +
                        "المساحة القابلة للاسترجاع: ${formatBytes(facts.media.duplicateGroups.sumOf { it.reclaimableBytes })}. " +
                        "كاش KNOUX الحالي: ${formatBytes(facts.ownCacheBytes)}.",
                    size = 9.sp,
                    lineHeight = 12.sp,
                    color = V2Muted,
                )
            }
        }
    }
    item {
        CleanActionCard(
            title = "فتح حوار الحذف الرسمي",
            body = "اختر المجموعة، راجع الملفات، ثم أكّد عبر MediaStore.createDeleteRequest.",
            action = "فتح الملفات المكررة",
            icon = Icons.Rounded.Delete,
            onClick = { navigate("duplicates") },
        )
    }
    // Show workflow confirmation state
    if (workflowState.executionStatus == ExecutionStatus.AWAITING_SYSTEM) {
        item { V2SectionLabel("في انتظار التأكيد", trailing = "اضغط للتأكيد") }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 16.dp, glow = V2Amber, borderAlpha = .3f) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    KText("هناك ${workflowState.selection.selectedUris.size} ملف جاهز للحذف", size = 9.sp, weight = FontWeight.Bold, color = V2Amber)
                    KText("المساحة المحررة: ${formatBytes(workflowState.selection.selectedBytes)}", size = 9.sp, color = V2Soft)
                    KText(workflowState.executionMessage ?: "في انتظار تأكيدك", size = 8.sp, color = V2Muted)
                    Row {
                        CleanActionCard(
                            title = "تأكيد الحذف",
                            body = "سيتم عرض حوار Android الرسمي للحذف",
                            action = "تأكيد",
                            icon = Icons.Rounded.CheckCircle,
                            onClick = {
                                workflowViewModel.confirmCleanupExecution { pendingIntent ->
                                    deleteRequestLauncher.launch(
                                        androidx.activity.result.IntentSenderRequest.Builder(pendingIntent.intentSender).build()
                                    )
                                }
                            },
                        )
                        Spacer(Modifier.width(8.dp))
                        CleanActionCard(
                            title = "إلغاء",
                            body = "إلغاء عملية الحذف",
                            action = "إلغاء",
                            icon = Icons.Rounded.Close,
                            onClick = { workflowViewModel.cancelCurrentOperation() },
                        )
                    }
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.historyItems(
    history: List<ScanHistoryEntry>,
    workflowState: CleaningWorkflowState,
    workflowViewModel: CleaningWorkflowViewModel,
    navigate: (String) -> Unit,
) {
    item { V2SectionLabel("آخر القراءات", trailing = "${history.size}/24") }
    if (history.isEmpty()) {
        item {
            ServiceCapabilityCard(
                title = "لا قراءات محفوظة بعد",
                body = "شغّل الفحص العميق ليُحفظ أول قياس محلي. السجل لا يُرفع لأي خادم.",
                state = ServiceState.LIVE,
                actionLabel = "فتح الفحص العميق",
                onAction = { navigate("deepscan") },
            )
        }
    } else {
        history.take(10).forEach { entry ->
            item {
                V2GlassCard(Modifier.fillMaxWidth(), padding = 10.dp, radius = 16.dp, glow = V2Green, borderAlpha = .24f) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        V2IconTile(Icons.Rounded.Timeline, V2Green, size = 34.dp, iconSize = 17.dp)
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            KText(formatScanTime(entry.timestamp), size = 9.2.sp, weight = FontWeight.Bold)
                            KText("${formatCount(entry.appsScanned)} تطبيق · ${formatBytes(entry.storageUsedBytes)} مستخدم", size = 9.sp, color = V2Soft)
                        }
                        KText(formatBytes(entry.cacheBytes), size = 9.sp, weight = FontWeight.Bold, color = V2Pink)
                    }
                }
            }
        }
    }
    item {
        CleanActionCard(
            title = "تقرير الأداء",
            body = "نفس السجل معروض كنقاط صحة الجهاز على هذا الهاتف.",
            action = "فتح تقرير الأداء",
            icon = Icons.Rounded.BarChart,
            onClick = { navigate("performance-report") },
        )
    }
    // Show workflow operation history if available
    workflowState.lastOperationResult?.let { opResult ->
        item { V2SectionLabel("آخر عملية تنظيف", trailing = "من الجلسة الحالية") }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 10.dp, radius = 16.dp, glow = V2Pink, borderAlpha = .24f) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    V2IconTile(Icons.Rounded.Delete, V2Pink, size = 34.dp, iconSize = 17.dp)
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        KText("تم في ${formatScanTime(opResult.timestamp)}", size = 9.2.sp, weight = FontWeight.Bold)
                        KText("${formatCount(opResult.deletedCount)} ملف محذوف · ${formatBytes(opResult.freedBytes)} محررة", size = 9.sp, color = V2Soft)
                    }
                    KText("${opResult.failedCount} فشل", size = 9.sp, weight = FontWeight.Bold, color = if (opResult.failedCount > 0) V2Red else V2Green)
                }
            }
        }
    }
}

/* ---------------------- expanded lenses ---------------------- */

private fun androidx.compose.foundation.lazy.LazyListScope.largeItems(
    facts: DeviceFacts,
    workflowState: CleaningWorkflowState,
    workflowViewModel: CleaningWorkflowViewModel,
    navigate: (String) -> Unit,
) {
    item { V2SectionLabel("أكبر الملفات", trailing = if (facts.media.permissionGranted) "MediaStore" else "بعد المنح") }
    when {
        !facts.media.permissionGranted -> item {
            ServiceCapabilityCard(
                title = "صلاحية الوسائط مطلوبة",
                body = "لن تُعرض قائمة الملفات قبل منح قراءة الصور والفيديو. لا تُستبدل القائمة بأرقام تجريبية.",
                state = ServiceState.PERMISSION,
            )
        }
        facts.media.largestFiles.isEmpty() -> item {
            ServiceCapabilityCard(
                title = "لا ملفات في النطاق",
                body = "فُحصت المكتبة المصرح بها ولم يُعد MediaStore ملفات قابلة للعرض.",
                state = ServiceState.LIVE,
            )
        }
        else -> facts.media.largestFiles.take(8).forEach { file ->
            item {
                ServiceFileCard(
                    file = file,
                    accent = if (file.isVideo) V2Pink else V2Violet,
                )
            }
        }
    }
    item {
        CleanActionCard(
            title = "المراجعة الكاملة",
            body = "انتقل إلى شاشة الملفات المكررة لمراجعة كل المجموعات قبل أي حذف.",
            action = "فتح الملفات المكررة",
            icon = Icons.Rounded.Layers,
            onClick = { navigate("duplicates") },
        )
    }
    // Show workflow selection state
    if (workflowState.selection.selectedUris.isNotEmpty()) {
        item { V2SectionLabel("التحديد الحالي", trailing = "${workflowState.selection.selectedUris.size} ملف · ${formatBytes(workflowState.selection.selectedBytes)}") }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.mediaMixItems(
    facts: DeviceFacts,
    workflowState: CleaningWorkflowState,
    workflowViewModel: CleaningWorkflowViewModel,
    navigate: (String) -> Unit,
) {
    item { V2SectionLabel("مزيج الوسائط", trailing = if (facts.media.permissionGranted) "مباشر" else "بعد المنح") }
    item {
        V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Pink, borderAlpha = .32f) {
            Column {
                val total = (facts.media.imagesBytes + facts.media.videosBytes).coerceAtLeast(1L)
                SegmentedBar(
                    listOf(
                        (facts.media.imagesBytes.toFloat() / total).coerceIn(0f, 1f) to V2Pink,
                        (facts.media.videosBytes.toFloat() / total).coerceIn(0f, 1f) to V2Violet,
                    ),
                    height = 11,
                )
                Spacer(Modifier.height(10.dp))
                CleanFactRow(Icons.Rounded.Image, "صور", if (facts.media.permissionGranted) "${formatCount(facts.media.images)} · ${formatBytes(facts.media.imagesBytes)}" else "بعد المنح", V2Pink)
                Spacer(Modifier.height(7.dp))
                CleanFactRow(Icons.Rounded.Layers, "فيديو", if (facts.media.permissionGranted) "${formatCount(facts.media.videos)} · ${formatBytes(facts.media.videosBytes)}" else "بعد المنح", V2Violet)
            }
        }
    }
    item {
        CleanActionCard(
            title = "أين تذهب المساحة؟",
            body = "شاشة التخزين توضح الحصص الفعلية وحدود القراءة.",
            action = "فتح تحليل التخزين",
            icon = Icons.Rounded.Storage,
            onClick = { navigate("storage") },
        )
    }
    // Show workflow selection state
    if (workflowState.selection.selectedUris.isNotEmpty()) {
        item { V2SectionLabel("التحديد الحالي", trailing = "${workflowState.selection.selectedUris.size} ملف · ${formatBytes(workflowState.selection.selectedBytes)}") }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.bucketItems(
    facts: DeviceFacts,
    workflowState: CleaningWorkflowState,
    workflowViewModel: CleaningWorkflowViewModel,
    navigate: (String) -> Unit,
) {
    item { V2SectionLabel("المجلدات الأثقل", trailing = if (facts.media.permissionGranted) "من الملفات المقروءة" else "بعد المنح") }
    if (!facts.media.permissionGranted || facts.media.largestFiles.isEmpty()) {
        item {
            ServiceCapabilityCard(
                title = if (facts.media.permissionGranted) "لا مجلدات لعرضها" else "صلاحية الوسائط مطلوبة",
                body = "لا تُبنى قائمة المجلدات إلا من الملفات التي أعادها MediaStore.",
                state = if (facts.media.permissionGranted) ServiceState.LIVE else ServiceState.PERMISSION,
            )
        }
    } else {
        val buckets = facts.media.largestFiles
            .groupBy { it.bucket?.takeIf(String::isNotBlank) ?: "مجلد غير مسمى" }
            .mapValues { (_, files) -> files.sumOf { it.sizeBytes } }
            .entries
            .sortedByDescending { it.value }
        buckets.take(6).forEach { (bucket, bytes) ->
            item {
                CleanFileRow(
                    title = bucket,
                    subtitle = "${formatCount(facts.media.largestFiles.count { (it.bucket ?: "مجلد غير مسمى") == bucket })} ملفًا مقروءًا",
                    size = formatBytes(bytes),
                    accent = V2Violet,
                )
            }
        }
    }
    item {
        CleanActionCard(
            title = "تفاصيل أعلى الملفات",
            body = "قائمة أكبر الملفات تكمل صورة المجلدات الأثقل.",
            action = "فتح الملفات الكبيرة",
            icon = Icons.Rounded.FolderOpen,
            onClick = { navigate("clean-large") },
        )
    }
    // Show workflow buckets if available
    workflowState.scanResult?.buckets?.take(5)?.forEach { bucket ->
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 10.dp, radius = 12.dp, glow = V2Violet, borderAlpha = .2f) {
                CleanFactRow(Icons.Rounded.Folder, bucket.name, "${formatCount(bucket.fileCount)} ملف · ${formatBytes(bucket.totalBytes)}", V2Violet)
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.freePlanItems(
    facts: DeviceFacts,
    workflowState: CleaningWorkflowState,
    workflowViewModel: CleaningWorkflowViewModel,
    navigate: (String) -> Unit,
) {
    item { V2SectionLabel("خطة المساحة", trailing = "قراءة مباشرة") }
    item {
        V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Cyan, borderAlpha = .32f) {
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                CleanFactRow(Icons.Rounded.Storage, "المساحة الحرة", formatBytes(facts.storage.freeBytes), V2Green)
                CleanFactRow(Icons.Rounded.PieChart, "المستخدم", "${(facts.storage.usedFraction * 100).toInt()}%", V2Cyan)
                CleanFactRow(Icons.Rounded.Cached, "كاش KNOUX", formatBytes(facts.ownCacheBytes), V2Amber)
                CleanFactRow(
                    Icons.Rounded.Layers,
                    "تكرار قابل للمراجعة",
                    if (facts.media.permissionGranted) formatBytes(facts.media.duplicateGroups.sumOf { it.reclaimableBytes }) else "بعد المنح",
                    V2Pink,
                )
            }
        }
    }
    item {
        CleanActionCard(
            title = "لا حذف تلقائي",
            body = "كل عنصر يُعرض هنا للمراجعة فقط؛ التنفيذ يحتاج تأكيدك في شاشة النظام.",
            action = "فتح مركز التنظيف",
            icon = Icons.Rounded.CleaningServices,
            onClick = { navigate("smartclean") },
        )
    }
    // Show workflow selection impact
    if (workflowState.selection.selectedUris.isNotEmpty()) {
        item { V2SectionLabel("أثر الخطة الحالية", trailing = "مباشر") }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 16.dp, glow = V2Cyan, borderAlpha = .24f) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    CleanFactRow(Icons.Rounded.Delete, "تحرير مخطط", formatBytes(workflowState.selection.selectedBytes), V2Pink)
                    CleanFactRow(Icons.Rounded.CheckCircle, "ملفات محددة", "${workflowState.selection.selectedUris.size}", V2Cyan)
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.cacheAuditItems(
    facts: DeviceFacts,
    workflowState: CleaningWorkflowState,
    workflowViewModel: CleaningWorkflowViewModel,
    navigate: (String) -> Unit,
) {
    item { V2SectionLabel("كاش KNOUX", trailing = "داخل التطبيق") }
    item {
        V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Green, borderAlpha = .34f) {
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                CleanFactRow(Icons.Rounded.Cached, "حجم الكاش", formatBytes(facts.ownCacheBytes), V2Green)
                CleanFactRow(Icons.Rounded.Description, "عدد الملفات", formatCount(facts.ownCacheFiles), V2Cyan)
                CleanFactRow(
                    Icons.Rounded.PieChart,
                    "من التخزين المستخدم",
                    if (facts.storage.usedBytes > 0L) "${(facts.ownCacheBytes * 100 / facts.storage.usedBytes)}%" else "—",
                    V2Amber,
                )
                CleanFactRow(Icons.Rounded.Storage, "المساحة الحرة", formatBytes(facts.storage.freeBytes), V2Violet)
            }
        }
    }
    item {
        CleanActionCard(
            title = "نطاق القراءة",
            body = "هذه الأرقام تخص KNOUX فقط، ولا تشمل كاش التطبيقات الأخرى.",
            action = "فتح التنظيف الذكي",
            icon = Icons.Rounded.CleaningServices,
            onClick = { navigate("smartclean") },
        )
    }
    // Show workflow cache audit
    if (workflowState.executionStatus == ExecutionStatus.AWAITING_SYSTEM && workflowState.scanResult != null) {
        item { V2SectionLabel("تنظيف الكاش", trailing = "جاهز للتنفيذ") }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 16.dp, glow = V2Green, borderAlpha = .3f) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    KText("كاش KNOUX الحالي: ${formatBytes(workflowState.scanResult!!.cacheBytes)}", size = 9.sp, weight = FontWeight.Bold, color = V2Green)
                    KText("عدد الملفات: ${workflowState.scanResult!!.cacheFiles}", size = 9.sp, color = V2Soft)
                    KText("سيتم تحرير المساحة بعد تأكيدك", size = 8.sp, color = V2Muted)
                    Row {
                        CleanActionCard(
                            title = "تأكيد تنظيف الكاش",
                            body = "سيتم حذف ملفات الكاش المؤقتة فقط",
                            action = "تأكيد",
                            icon = Icons.Rounded.Cached,
                            onClick = { workflowViewModel.confirmClearOwnCacheExecution() },
                        )
                        Spacer(Modifier.width(8.dp))
                        CleanActionCard(
                            title = "إلغاء",
                            body = "إلغاء تنظيف الكاش",
                            action = "إلغاء",
                            icon = Icons.Rounded.Close,
                            onClick = { workflowViewModel.cancelCurrentOperation() },
                        )
                    }
                }
            }
        }
    }
}

/* ----------------------------- rows ----------------------------- */

@Composable
private fun CleanFactRow(icon: ImageVector, title: String, value: String, accent: Color) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = accent, modifier = Modifier.size(17.dp))
        Spacer(Modifier.width(7.dp))
        KText(title, size = 9.sp, color = V2Soft, modifier = Modifier.weight(1f))
        KText(value, size = 9.sp, weight = FontWeight.Bold, color = accent)
    }
}

@Composable
private fun CleanFileRow(title: String, subtitle: String, size: String, accent: Color) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 10.dp, radius = 16.dp, glow = accent, borderAlpha = .25f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(Icons.Rounded.Image, accent, size = 33.dp, iconSize = 16.dp)
            Spacer(Modifier.width(7.dp))
            Column(Modifier.weight(1f)) {
                KText(title, size = 9.sp, weight = FontWeight.SemiBold, maxLines = 1)
                KText(subtitle, size = 9.sp, color = V2Soft, maxLines = 1)
            }
            KText(size, size = 9.sp, weight = FontWeight.Bold, color = accent)
        }
    }
}

@Composable
private fun CleanActionCard(
    title: String,
    body: String,
    action: String,
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 10.dp, radius = 17.dp, glow = V2Cyan, borderAlpha = .26f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(icon, if (enabled) V2Cyan else V2Muted, size = 34.dp, iconSize = 17.dp)
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                KText(title, size = 9.4.sp, weight = FontWeight.Bold, color = if (enabled) Color.Unspecified else V2Muted)
                KText(body, size = 9.sp, lineHeight = 10.6.sp, color = if (enabled) V2Soft else V2Muted)
            }
            androidx.compose.material3.TextButton(onClick = onClick, enabled = enabled) {
                androidx.compose.material3.Text(action, color = if (enabled) V2Cyan else V2Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
