package com.knoux.ai.ui.v2

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel

/** A native, actionable review workspace; no design fixtures or fake progress. */
@Composable
internal fun V2DuplicatesWorkspace(
    onBack: () -> Unit,
    onPro: () -> Unit,
    initialMode: DuplicateScanMode = DuplicateScanMode.EXACT,
) {
    val context = LocalContext.current
    val model: DuplicateWorkspaceViewModel = viewModel()
    val state by model.state.collectAsState()
    var mode by remember { mutableStateOf(initialMode) }
    var filter by remember { mutableIntStateOf(0) }
    var permissionRevision by remember { mutableIntStateOf(0) }
    val selected = remember { mutableStateMapOf<String, Boolean>() }
    val keepers = remember { mutableStateMapOf<String, String>() }
    var reviewGroupId by remember { mutableStateOf<String?>(null) }
    var confirmDeletion by remember { mutableStateOf(false) }
    var viewerError by remember { mutableStateOf<String?>(null) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) permissionRevision++
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    // Real device facts for the royal stage ribbon
    val (refresh, bump) = rememberRefreshCounter()
    val facts = rememberDeviceFacts(refresh)

    val mediaAllowed = remember(permissionRevision) {
        duplicateReadPermissions().any { context.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
    }
    LaunchedEffect(model) { model.attach(context) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grants ->
        permissionRevision++
        // Granting media access only opens the gate; the K orb is still the
        // only control that starts a scan.
        if (grants.any { it.value }) {
            selected.clear(); keepers.clear()
        }
    }
    val deleteLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        model.finishDelete(context, result.resultCode == Activity.RESULT_OK)
    }
    LaunchedEffect(state.deletion) { state.deletion?.deletedUris?.forEach { selected.remove(it) } }
    LaunchedEffect(state.result) {
        // A new/cleared result invalidates every old selection and keeper, so
        // the bottom CTA can never act on files from a previous scan.
        val known = state.result?.groups?.flatMap { it.files }?.map { it.uriString }?.toSet().orEmpty()
        selected.keys.toList().filterNot { it in known }.forEach { selected.remove(it) }
        val groupIds = state.result?.groups?.map { it.id }?.toSet().orEmpty()
        keepers.keys.toList().filterNot { it in groupIds }.forEach { keepers.remove(it) }
    }
    val groups = state.result?.groups.orEmpty()
    val visible = groups.filter { group ->
        when (filter) { 1 -> !group.files.first().isVideo; 2 -> group.files.first().isVideo; else -> true }
    }
    val knownFiles = groups.flatMap { it.files }.distinctBy { it.uriString }
    val knownUris = knownFiles.map { it.uriString }.toSet()
    val selectedUris = selected.filterValues { it }.keys.filter { it in knownUris }.toSet()
    val selectedBytes = knownFiles.filter { it.uriString in selectedUris }.sumOf { it.sizeBytes }
    fun startScan() {
        if (!mediaAllowed) permissionLauncher.launch(duplicateReadPermissions())
        else { selected.clear(); keepers.clear(); model.start(context, mode) }
    }
    fun chooseKeeper(group: DuplicateScanGroup, uri: String) {
        keepers[group.id] = uri
        selected.remove(uri)
    }
    fun toggle(file: DuplicateScanFile) {
        if (selected[file.uriString] == true) selected.remove(file.uriString) else selected[file.uriString] = true
    }
    fun preview(file: DuplicateScanFile) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW).setDataAndType(
                Uri.parse(file.uriString), file.mimeType ?: if (file.isVideo) "video/*" else "image/*",
            ).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION))
        } catch (_: Exception) { viewerError = "لا يوجد عارض متاح لهذا الملف. يمكنك مراجعة صورته وبياناته هنا." }
    }

    // Day world lets the porcelain shell show through; night keeps its own sky.
    // Local copies for smart-cast of delegated state properties (used in metric, action, content)
    val progress = state.progress
    val result = state.result

    Box(
        Modifier
            .fillMaxSize()
            .background(if (LocalDayMode.current) Color.Transparent else V2Deep)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(Modifier.fillMaxSize()) {
            V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = onPro)
            FixedServiceScreen(
                spec = ServiceSpecs.duplicates,
                status = when {
                    state.scanning -> "جارٍ الفحص…"
                    state.result != null -> "نتيجة جاهزة"
                    state.scanCancelled -> "مُوقَّف"
                    state.error != null -> "خطأ"
                    else -> if (mediaAllowed) "جاهز للتشغيل" else "تحتاج صلاحية الوسائط"
                },
                statusColor = when {
                    state.scanning -> V2Amber
                    state.result != null -> V2Green
                    state.scanCancelled -> V2Amber
                    state.error != null -> V2Red
                    else -> if (mediaAllowed) V2Violet else V2Muted
                },
                metric = when {
                    state.scanning && progress != null -> "${progress.completedFiles} / ${progress.totalFiles}"
                    result != null -> "${formatCount(result.groups.sumOf { it.files.size })} ملف في ${formatCount(result.groups.size)} مجموعة"
                    state.scanCancelled -> state.message ?: "تم الإيقاف"
                    state.error != null -> state.error
                    else -> if (!mediaAllowed) "اضغط للسماح بالوسائط" else "اضغط الزر الدائري لبدء الفحص"
                },
                scene = { sceneModifier -> ServiceArtScene(ServiceSpecs.duplicates, sceneModifier) },
                ribbonSlots = listOf(
                    HeroRibbonSlot(
                        Icons.Rounded.FolderCopy,
                        if (facts.computedAt > 0L) formatCount(facts.installedCount) else "—",
                        "تطبيق مثبت",
                        V2Cyan,
                    ),
                    HeroRibbonSlot(
                        Icons.Rounded.Storage,
                        if (facts.computedAt > 0L) formatBytes(facts.storage.freeBytes) else "—",
                        "مساحة متاحة",
                        V2Green,
                    ),
                    HeroRibbonSlot(
                        Icons.Rounded.Memory,
                        if (facts.computedAt > 0L) formatBytes(facts.ownCacheBytes) else "—",
                        "كاش KNOUX",
                        V2Pink,
                    ),
                    HeroRibbonSlot(
                        Icons.Rounded.Shield,
                        if (facts.computedAt > 0L) formatCount(facts.sensitiveAppCount) else "—",
                        "تطبيق حساس",
                        V2Violet,
                    ),
                ),
                action = {
                    // The universal K orb: the only way this service starts.
                    KnouxRunOrbBlock(
                        progress = when {
                            state.scanning && progress != null && progress.totalFiles > 0 ->
                                (progress.completedFiles.toFloat() / progress.totalFiles).coerceIn(0f, 1f)
                            state.scanning -> null
                            result != null -> 1f
                            else -> 0f
                        },
                        running = state.scanning,
                        enabled = !state.busy && mediaAllowed,
                        label = when {
                            state.scanning -> progress?.phase ?: "جارٍ الفحص…"
                            result != null -> "اضغط لفحص جديد"
                            else -> if (!mediaAllowed) "اضغط للسماح بالوسائط" else "اضغط لبدء الفحص"
                        },
                        accent = V2Cyan,
                        accent2 = V2Violet,
                        size = 148.dp,
                        onTap = { if (!state.busy && mediaAllowed) startScan() },
                    )
                },
                showIdentityLine = false,
                content = {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        DuplicateModeButton("المحتوى", Icons.Rounded.ContentCopy, mode == DuplicateScanMode.EXACT,
                            enabled = !state.busy && mediaAllowed, modifier = Modifier.weight(1f)) {
                            mode = DuplicateScanMode.EXACT
                            model.selectMode(mode)
                        }
                        DuplicateModeButton("الصور", Icons.Rounded.ImageSearch, mode == DuplicateScanMode.VISUAL,
                            enabled = !state.busy && mediaAllowed, modifier = Modifier.weight(1f)) {
                            mode = DuplicateScanMode.VISUAL
                            model.selectMode(mode)
                        }
                        DuplicateModeButton("الأسماء", Icons.Rounded.TextFields, mode == DuplicateScanMode.NAME,
                            enabled = !state.busy && mediaAllowed, modifier = Modifier.weight(1f)) {
                            mode = DuplicateScanMode.NAME
                            model.selectMode(mode)
                        }
                    }
            if (mode == DuplicateScanMode.VISUAL) {
                DuplicateNotice("التشابه مش تطابق", "تحليل بصري محلي، وليس إثباتًا لتكرار المحتوى. راجع كل صورة بنفسك؛ لا يوجد تحديد تلقائي هنا.", V2Amber)
            }
            if (mode == DuplicateScanMode.NAME) {
                DuplicateNotice("نفس الاسم مش نفس المحتوى", "المجموعات هنا ملفات تشترك في الاسم فقط. افتح كل ملف وقارن قبل ما تحدد أي حذف.", V2Violet)
            }
            if (state.scanning || state.operating || state.restoring) {
                DuplicateProgressPanel(state)
            }
            if (state.awaitingSystem) {
                DuplicateNotice("طلب حذف محفوظ", "موافقة Android لم تُحسم هنا بعد. لو نافذة النظام اتقفلت، أعد التحقق؛ لن نكرر الحذف أو نخمن الموافقة.", V2Amber)
                TextButton(onClick = { model.recoverInterrupted(context) }, modifier = Modifier.heightIn(min = 48.dp)) {
                    KText("إعادة التحقق من الطلب", size = 14.sp, color = V2Cyan)
                }
            }
            state.deletion?.let { deletion -> DuplicateOutcomePanel(deletion) }
            (state.error ?: viewerError)?.let { message ->
                DuplicateNotice("محتاج مراجعة", message, V2Amber)
            }
            if (state.scanCancelled) {
                DuplicateNotice("الفحص اتوقف", state.message ?: "النتيجة غير مكتملة. أعد الفحص وقت ما تحب.", V2Amber)
            }
            if (result != null) {
                DuplicateResultHeader(result, state.mode)
                if (result.warnings.isNotEmpty() || result.partialAccess) {
                    DuplicateNotice("نطاق الفحص محدود بالصلاحيات", buildString {
                        if (result.partialAccess) append("تم فحص الملفات المسموح بها فقط؛ النتيجة ليست إثباتًا لفحص المكتبة كاملة. ")
                        append(result.warnings.distinct().joinToString("\n"))
                    }, V2Amber)
                }
                if (groups.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("الكل", "صور", "فيديو").forEachIndexed { index, text ->
                            FilterChip(selected = filter == index, onClick = { filter = index }, enabled = !state.operating,
                                label = { KText(text, size = 13.sp, color = if (filter == index) V2Cyan else V2Soft) },
                                modifier = Modifier.heightIn(min = 48.dp))
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        if (state.mode != DuplicateScanMode.VISUAL) TextButton(
                            enabled = !state.busy,
                            onClick = {
                                visible.forEach { group ->
                                    val keeper = duplicateKeeper(group, keepers)
                                    group.files.forEach { file ->
                                        if (file.uriString != keeper) selected[file.uriString] = true
                                    }
                                }
                            },
                            modifier = Modifier.heightIn(min = 48.dp),
                        ) { KText("تحديد الكل", size = 13.sp, weight = FontWeight.Bold, color = V2Cyan) }
                        if (selectedUris.isNotEmpty()) TextButton(onClick = { selected.clear() }, enabled = !state.busy,
                            modifier = Modifier.heightIn(min = 48.dp)) { KText("إلغاء التحديد", size = 13.sp, color = V2Soft) }
                    }
                    visible.forEach { group ->
                        DuplicateReviewCard(group, duplicateKeeper(group, keepers), selectedUris, !state.busy,
                            onReview = { reviewGroupId = group.id }, onToggle = ::toggle, onPreview = ::preview)
                    }
                } else {
                    DuplicateNotice(
                        if (result.warnings.isEmpty() && !result.partialAccess) "لا مجموعات في النطاق المفحوص" else "لم تظهر مجموعات في الملفات المقروءة",
                        when (state.mode) {
                            DuplicateScanMode.EXACT -> "لم نجد بصمات محتوى متطابقة بين الملفات التي أمكن مقارنتها. الملفات غير المقروءة لا تدخل في هذا الحكم."
                            DuplicateScanMode.NAME -> "لم نجد ملفات تشترك في نفس الاسم داخل النطاق المفحوص. ده مش حكم على وجود تكرار بمحتوى مختلف."
                            DuplicateScanMode.VISUAL -> "لم تظهر مجموعات تشابه بالمعيار المحافظ الحالي؛ ده مش حكم على جودة الصور."
                        }, V2Green,
                    )
                }
            } else if (!state.scanning && !state.scanCancelled && !state.operating) {
                Surface(color = V2Panel, shape = RoundedCornerShape(24.dp), border = BorderStroke(1.dp, V2Cyan.copy(alpha = .24f))) {
                    Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.FolderCopy, null, tint = V2Cyan, modifier = Modifier.size(44.dp))
                            KText("فحص حقيقي. قرار واضح.", size = 20.sp, weight = FontWeight.Bold)
                        }
                        KText("المطابقة تقرأ المحتوى ببصمة SHA-256، مش اسم الملف أو حجمه فقط. صور وفيديو بدون سقف 256 ملف أو 32 MB للملف.", size = 14.sp, color = V2Soft)
                        HorizontalDivider(color = V2Soft.copy(alpha = .16f))
                        KText("بنحتفظ بنسخة من كل مجموعة. تقدر تغيّر النسخة المحفوظة، وتعاين الملفات قبل أي حذف.", size = 13.sp, color = V2Soft)
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
        }
        ) // closes FixedServiceScreen
        // Pinned bottom CTA bar (outside FixedServiceScreen so it stays visible)
        Surface(color = V2Panel2, tonalElevation = 0.dp) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (selectedUris.isNotEmpty()) Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    KText("${formatCount(selectedUris.size)} نسخة محددة", size = 14.sp, weight = FontWeight.Bold)
                    KText(formatBytes(selectedBytes), size = 14.sp, color = V2Cyan, weight = FontWeight.Bold)
                }
                when {
                    state.scanning -> OutlinedButton(onClick = { model.cancelScan() }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
                        Icon(Icons.Rounded.StopCircle, null, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp))
                        KText("إيقاف الفحص", size = 15.sp, color = V2Soft)
                    }
                    selectedUris.isNotEmpty() -> Button(onClick = { confirmDeletion = true }, enabled = !state.busy,
                        colors = ButtonDefaults.buttonColors(containerColor = V2Cyan, contentColor = V2Deep),
                        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp), shape = RoundedCornerShape(16.dp)) {
                        Icon(Icons.Rounded.DeleteOutline, null, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp))
                        KText("مراجعة وحذف المحدد", size = 15.sp, weight = FontWeight.Bold, color = V2Deep)
                    }
                    else -> KText(
                        "ابدأ الخدمة من زر K الدائري بالأعلى",
                        size = 13.sp,
                        color = V2Soft,
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 14.dp),
                    )
                }
            }
        }
    }
    } // closes Box
    val review = groups.firstOrNull { it.id == reviewGroupId }
    if (review != null) Dialog(onDismissRequest = { reviewGroupId = null }) {
        Surface(color = V2Panel2, shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.fillMaxWidth().heightIn(max = 620.dp).padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    KText("مراجعة ${review.files.size} نسخ", size = 19.sp, weight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    IconButton(onClick = { reviewGroupId = null }) { Icon(Icons.Rounded.Close, "إغلاق المراجعة", tint = V2Soft) }
                }
                KText("اختار النسخة اللي هتحتفظ بيها، وحدّد الباقي بنفسك.", size = 13.sp, color = V2Soft)
                Spacer(Modifier.height(12.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(review.files, key = { it.uriString }) { file ->
                        val kept = duplicateKeeper(review, keepers) == file.uriString
                        DuplicateFileReviewRow(file, kept, file.uriString in selectedUris, !state.busy,
                            onToggle = { toggle(file) }, onKeep = { chooseKeeper(review, file.uriString) }, onPreview = { preview(file) })
                    }
                }
            }
        }
    }
    if (confirmDeletion) AlertDialog(
        onDismissRequest = { confirmDeletion = false }, containerColor = V2Panel2,
        title = { KText("راجع قرار الحذف", size = 21.sp, weight = FontWeight.Bold) },
        text = { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            KText("${formatCount(selectedUris.size)} نسخة · ${formatBytes(selectedBytes)}", size = 16.sp, color = V2Cyan, weight = FontWeight.Bold)
            KText("هنعيد التحقق من الملفات والنسخة المحفوظة، وبعدها Android هيطلب موافقتك. الحذف ده مش سلة استرجاع؛ احتفظ بنسخة احتياطية لو الملفات مهمة.", size = 14.sp, color = V2Soft)
            if (state.mode == DuplicateScanMode.VISUAL) KText("دي صور متشابهة، وممكن يكون بينها تفاصيل مختلفة. تأكيدك يعني إنك راجعتها واخترت حذفها.", size = 14.sp, color = V2Amber)
            if (state.mode == DuplicateScanMode.NAME) KText("دي ملفات بنفس الاسم، ومحتواها ممكن يختلف. تأكيدك يعني إنك راجعت كل ملف قبل الحذف.", size = 14.sp, color = V2Amber)
            if (selectedUris.size > 2000) KText("هنطلب موافقة النظام على أول 2000 نسخة في الدفعة. الباقي يفضل محدد للدفعة التالية.", size = 13.sp, color = V2Amber)
        } },
        dismissButton = { TextButton(onClick = { confirmDeletion = false }) { KText("رجوع للمراجعة", size = 14.sp, color = V2Soft) } },
        confirmButton = { TextButton(onClick = {
            confirmDeletion = false
            model.prepareDelete(context, selectedUris, keepers.toMap()) { sender -> deleteLauncher.launch(IntentSenderRequest.Builder(sender).build()) }
        }) { KText("المتابعة لموافقة Android", size = 14.sp, color = V2Cyan, weight = FontWeight.Bold) } },
    )
}

@Composable
private fun DuplicateModeButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, active: Boolean,
    enabled: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(color = if (active) V2Cyan.copy(alpha = .10f) else V2Panel2,
        shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, if (active) V2Cyan else V2Soft.copy(alpha = .24f)),
        modifier = modifier.selectable(selected = active, enabled = enabled, role = Role.RadioButton, onClick = onClick)) {
        Row(Modifier.heightIn(min = 56.dp).padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, tint = if (active) V2Cyan else V2Soft, modifier = Modifier.size(20.dp))
            KText(text, size = 13.sp, weight = FontWeight.Bold, color = if (active) V2Cyan else V2Soft)
        }
    }
}

@Composable
private fun DuplicateNotice(title: String, body: String, accent: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(if (accent == V2Green) Icons.Rounded.Verified else Icons.Rounded.Info, null, tint = accent, modifier = Modifier.size(20.dp))
            KText(title, size = 15.sp, weight = FontWeight.Bold, color = accent)
        }
        KText(body, size = 13.sp, lineHeight = 21.sp, color = V2Soft)
    }
}

@Composable
private fun DuplicateProgressPanel(state: DuplicateWorkspaceState) {
    Surface(color = V2Panel, shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val progress = state.progress
            KText(if (state.scanning) progress?.phase ?: "تجهيز فهرس الوسائط…" else state.message ?: "تحميل سجل الطلب…",
                size = 16.sp, weight = FontWeight.Bold)
            if (state.scanning && progress != null && progress.totalFiles > 0) {
                LinearProgressIndicator(progress = { (progress.completedFiles.toFloat() / progress.totalFiles).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(5.dp), color = V2Cyan, trackColor = V2Soft.copy(alpha = .12f))
                KText("${formatCount(progress.completedFiles)} / ${formatCount(progress.totalFiles)} ملف · ${formatBytes(progress.bytesRead)} مقروء", size = 13.sp, color = V2Soft)
            } else LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(5.dp), color = V2Cyan, trackColor = V2Soft.copy(alpha = .12f))
            KText("العداد بيتحرك مع القراءة الفعلية، مش بمؤقّت.", size = 12.sp, color = V2Soft)
        }
    }
}

@Composable
private fun DuplicateResultHeader(result: DuplicateScanResult, mode: DuplicateScanMode) {
    val extras = result.groups.sumOf { (it.files.size - 1).coerceAtLeast(0) }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(
                when (mode) {
                    DuplicateScanMode.EXACT -> Icons.Rounded.Verified
                    DuplicateScanMode.VISUAL -> Icons.Rounded.Compare
                    DuplicateScanMode.NAME -> Icons.Rounded.TextFields
                },
                null,
                tint = when (mode) {
                    DuplicateScanMode.EXACT -> V2Green
                    DuplicateScanMode.VISUAL -> V2Amber
                    DuplicateScanMode.NAME -> V2Violet
                },
                modifier = Modifier.size(28.dp),
            )
            KText(
                "${formatCount(result.groups.size)} مجموعة " + when (mode) {
                    DuplicateScanMode.EXACT -> "متطابقة"
                    DuplicateScanMode.VISUAL -> "للمراجعة"
                    DuplicateScanMode.NAME -> "بأسماء مكررة"
                },
                size = 23.sp,
                weight = FontWeight.Bold,
            )
        }
        if (mode == DuplicateScanMode.EXACT) KText("$extras نسخة زائدة · ${formatBytes(result.groups.sumOf { it.files.drop(1).sumOf { file -> file.sizeBytes } })} حجم النسخ الممكن حذفها",
            size = 15.sp, color = V2Cyan, weight = FontWeight.Medium)
        KText("الفهرس: ${formatCount(result.filesDiscovered)} ملف · قورنت ${formatCount(result.filesCompared)} ملفات · تعذّرت قراءة ${formatCount(result.unreadableFiles)}", size = 12.sp, color = V2Soft)
        KText(
            "${formatScanTime(result.completedAt)} · " + when (mode) {
                DuplicateScanMode.EXACT -> "إثبات ببصمة SHA-256"
                DuplicateScanMode.VISUAL -> "تحليل تشابه بصري محلي"
                DuplicateScanMode.NAME -> "مطابقة أسماء محلية بدون قراءة المحتوى"
            },
            size = 12.sp,
            color = V2Soft,
        )
        HorizontalDivider(color = V2Soft.copy(alpha = .18f))
    }
}

@Composable
private fun DuplicateReviewCard(group: DuplicateScanGroup, keeper: String, selected: Set<String>, enabled: Boolean,
    onReview: () -> Unit, onToggle: (DuplicateScanFile) -> Unit, onPreview: (DuplicateScanFile) -> Unit) {
    val kind = group.kind
    val tint = when (kind) {
        DuplicateMatchKind.EXACT -> V2Cyan
        DuplicateMatchKind.VISUAL -> V2Amber
        DuplicateMatchKind.NAME -> V2Violet
    }
    val subtitle = when (kind) {
        DuplicateMatchKind.EXACT -> "محتوى متطابق"
        DuplicateMatchKind.VISUAL -> "تشابه يحتاج مراجعتك"
        DuplicateMatchKind.NAME -> "نفس الاسم · راجع المحتوى"
    }
    val icon = when (kind) {
        DuplicateMatchKind.EXACT -> Icons.Rounded.ContentCopy
        DuplicateMatchKind.VISUAL -> Icons.Rounded.Compare
        DuplicateMatchKind.NAME -> Icons.Rounded.TextFields
    }
    Surface(color = V2Panel2, shape = RoundedCornerShape(24.dp), border = BorderStroke(1.dp, tint.copy(alpha = .26f))) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    KText(group.files.first().name, size = 15.sp, weight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    KText("${group.files.size} نسخ · $subtitle", size = 12.sp, color = tint)
                }
                Icon(icon, null, tint = tint, modifier = Modifier.size(23.dp))
            }
            val displayed = listOfNotNull(group.files.firstOrNull { it.uriString == keeper }) + group.files.filterNot { it.uriString == keeper }.take(2)
            displayed.forEach { file ->
                DuplicateFileReviewRow(file, file.uriString == keeper, file.uriString in selected, enabled,
                    onToggle = { onToggle(file) }, onKeep = null, onPreview = { onPreview(file) })
            }
            OutlinedButton(onClick = onReview, enabled = enabled, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, V2Soft.copy(alpha = .25f))) {
                KText("مراجعة كل النسخ وتغيير المحفوظة", size = 13.sp, color = V2Soft)
            }
        }
    }
}

@Composable
private fun DuplicateFileReviewRow(file: DuplicateScanFile, kept: Boolean, selected: Boolean, enabled: Boolean,
    onToggle: () -> Unit, onKeep: (() -> Unit)?, onPreview: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.size(60.dp).clickable(role = Role.Button, onClick = onPreview)
                .semantics { contentDescription = "معاينة ${file.name}" }) {
                V2MediaThumb(file.uriString, file.isVideo, if (kept) V2Green else V2Cyan, Modifier.fillMaxSize())
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                KText(file.name, size = 13.sp, weight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                KText("${formatBytes(file.sizeBytes)} · ${file.bucket ?: "الوسائط"}", size = 12.sp, color = V2Soft, maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (kept) KText("نسخة محفوظة — لن تُحذف", size = 12.sp, color = V2Green)
            }
            if (kept) Icon(Icons.Rounded.Shield, "نسخة محمية من الحذف", tint = V2Green, modifier = Modifier.size(24.dp))
            else Checkbox(checked = selected, enabled = enabled, onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = V2Cyan, checkmarkColor = V2Deep, uncheckedColor = V2Soft),
                modifier = Modifier.semantics { contentDescription = "تحديد ${file.name} للحذف" })
        }
        if (!kept && onKeep != null) TextButton(onClick = onKeep, enabled = enabled, modifier = Modifier.heightIn(min = 48.dp)) {
            KText("احتفظ بهذه النسخة بدلًا من الحالية", size = 12.sp, color = V2Cyan)
        }
    }
}

@Composable
private fun DuplicateOutcomePanel(outcome: DuplicateDeleteVerification) {
    val verified = outcome.status == "VERIFIED"
    val cancelled = outcome.status == "CANCELLED"
    val title = when (outcome.status) {
        "VERIFIED" -> "الحذف اتأكد والنسخ المحفوظة موجودة"
        "CANCELLED" -> "اتلغى طلب الحذف"
        "NO_CHANGE" -> "لم يتأكد حذف أي نسخة"
        "INTERRUPTED" -> "الطلب اتقطع؛ الموافقة غير مثبتة"
        else -> "نتيجة جزئية — محتاجة مراجعة"
    }
    DuplicateNotice(title, when {
        verified -> "${formatCount(outcome.deletedUris.size)} نسخة لم تعد موجودة في استعلام MediaStore · حجمها ${formatBytes(outcome.deletedBytes)}. دي نتيجة محققة ومُسجلة، مش مجرد موافقة النظام."
        cancelled -> "لم نسجل نجاحًا أو مساحة محررة. الملفات التي لم يُثبت حذفها تظل في المراجعة."
        else -> "ثبت غياب ${formatCount(outcome.deletedUris.size)} نسخة بعد الموافقة. تعذّر التحقق من ${outcome.unknownCount} ملف. ${if (outcome.keepersIntact) "النسخ المحفوظة موجودة." else "لم نقدر نثبت بقاء كل النسخ المحفوظة؛ راجع المكتبة."}"
    }, if (verified) V2Green else V2Amber)
}

private fun duplicateReadPermissions(): Array<String> = when {
    Build.VERSION.SDK_INT >= 34 -> arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
    Build.VERSION.SDK_INT >= 33 -> arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
    else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
}
