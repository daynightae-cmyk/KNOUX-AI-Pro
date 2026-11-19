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
    val mediaAllowed = remember(permissionRevision) {
        duplicateReadPermissions().any { context.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
    }
    LaunchedEffect(model) { model.attach(context) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grants ->
        permissionRevision++
        if (grants.any { it.value }) {
            selected.clear(); keepers.clear()
            model.start(context, mode)
        }
    }
    val deleteLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        model.finishDelete(context, result.resultCode == Activity.RESULT_OK)
    }
    LaunchedEffect(state.deletion) { state.deletion?.deletedUris?.forEach { selected.remove(it) } }
    LaunchedEffect(state.result) {
        state.result?.let { result ->
            val known = result.groups.flatMap { it.files }.map { it.uriString }.toSet()
            selected.keys.toList().filterNot { it in known }.forEach { selected.remove(it) }
        }
    }
    val groups = state.result?.groups.orEmpty()
    val visible = groups.filter { group ->
        when (filter) { 1 -> !group.files.first().isVideo; 2 -> group.files.first().isVideo; else -> true }
    }
    val selectedUris = selected.filterValues { it }.keys.toSet()
    val selectedBytes = groups.flatMap { it.files }.distinctBy { it.uriString }
        .filter { it.uriString in selectedUris }.sumOf { it.sizeBytes }
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

    Column(Modifier.fillMaxSize().background(V2Deep).windowInsetsPadding(WindowInsets.statusBars)) {
        V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = onPro)
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    KText("الملفات المكررة", size = 28.sp, weight = FontWeight.Bold)
                    KText("احتفظ بالأفضل. راجع الباقي. واحذف بقرارك.", size = 14.sp, color = V2Soft)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Rounded.Shield, null, tint = V2Green, modifier = Modifier.size(18.dp))
                        KText("تحليل على جهازك · صورك لا تُرفع لأي خادم", size = 12.sp, color = V2Soft)
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DuplicateModeButton("تطابق المحتوى", Icons.Rounded.ContentCopy, mode == DuplicateScanMode.EXACT,
                        enabled = !state.busy, modifier = Modifier.weight(1f)) {
                        mode = DuplicateScanMode.EXACT
                        if (state.result != null && state.mode != mode) {
                            selected.clear(); keepers.clear(); model.start(context, mode)
                        }
                    }
                    DuplicateModeButton("تشابه الصور", Icons.Rounded.ImageSearch, mode == DuplicateScanMode.VISUAL,
                        enabled = !state.busy, modifier = Modifier.weight(1f)) {
                        mode = DuplicateScanMode.VISUAL
                        if (state.result != null && state.mode != mode) {
                            selected.clear(); keepers.clear(); model.start(context, mode)
                        }
                    }
                }
            }
            if (mode == DuplicateScanMode.VISUAL) item {
                DuplicateNotice("التشابه مش تطابق", "تحليل بصري محلي، وليس إثباتًا لتكرار المحتوى. راجع كل صورة بنفسك؛ لا يوجد تحديد تلقائي هنا.", V2Amber)
            }
            if (state.scanning || state.operating || state.restoring) item {
                DuplicateProgressPanel(state)
            }
            if (state.awaitingSystem) item {
                DuplicateNotice("طلب حذف محفوظ", "موافقة Android لم تُحسم هنا بعد. لو نافذة النظام اتقفلت، أعد التحقق؛ لن نكرر الحذف أو نخمن الموافقة.", V2Amber)
                TextButton(onClick = { model.recoverInterrupted(context) }, modifier = Modifier.heightIn(min = 48.dp)) {
                    KText("إعادة التحقق من الطلب", size = 14.sp, color = V2Cyan)
                }
            }
            state.deletion?.let { deletion -> item { DuplicateOutcomePanel(deletion) } }
            (state.error ?: viewerError)?.let { message -> item {
                DuplicateNotice("محتاج مراجعة", message, V2Amber)
            } }
            if (state.scanCancelled) item {
                DuplicateNotice("الفحص اتوقف", state.message ?: "النتيجة غير مكتملة. أعد الفحص وقت ما تحب.", V2Amber)
            }
            val result = state.result
            if (result != null) {
                item { DuplicateResultHeader(result, state.mode) }
                if (result.warnings.isNotEmpty() || result.partialAccess) item {
                    DuplicateNotice("نطاق الفحص محدود بالصلاحيات", buildString {
                        if (result.partialAccess) append("تم فحص الملفات المسموح بها فقط؛ النتيجة ليست إثباتًا لفحص المكتبة كاملة. ")
                        append(result.warnings.distinct().joinToString("\n"))
                    }, V2Amber)
                }
                if (groups.isNotEmpty()) {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("الكل", "صور", "فيديو").forEachIndexed { index, text ->
                                FilterChip(selected = filter == index, onClick = { filter = index }, enabled = !state.operating,
                                    label = { KText(text, size = 13.sp, color = if (filter == index) V2Cyan else V2Soft) },
                                    modifier = Modifier.heightIn(min = 48.dp))
                            }
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            if (state.mode == DuplicateScanMode.EXACT) TextButton(
                                enabled = !state.busy,
                                onClick = { exactDuplicateExtras(visible, keepers).forEach { selected[it] = true } },
                                modifier = Modifier.heightIn(min = 48.dp),
                            ) { KText("تحديد النسخ الزائدة", size = 13.sp, color = V2Cyan) }
                            if (selectedUris.isNotEmpty()) TextButton(onClick = { selected.clear() }, enabled = !state.busy,
                                modifier = Modifier.heightIn(min = 48.dp)) { KText("إلغاء التحديد", size = 13.sp, color = V2Soft) }
                        }
                    }
                    items(visible, key = { it.id }) { group ->
                        DuplicateReviewCard(group, duplicateKeeper(group, keepers), selectedUris, !state.busy,
                            onReview = { reviewGroupId = group.id }, onToggle = ::toggle, onPreview = ::preview)
                    }
                } else item {
                    DuplicateNotice(
                        if (result.warnings.isEmpty() && !result.partialAccess) "لا مجموعات في النطاق المفحوص" else "لم تظهر مجموعات في الملفات المقروءة",
                        if (state.mode == DuplicateScanMode.EXACT) "لم نجد بصمات محتوى متطابقة بين الملفات التي أمكن مقارنتها. الملفات غير المقروءة لا تدخل في هذا الحكم."
                        else "لم تظهر مجموعات تشابه بالمعيار المحافظ الحالي؛ ده مش حكم على جودة الصور.", V2Green,
                    )
                }
            } else if (!state.scanning && !state.scanCancelled && !state.operating) item {
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
            item { Spacer(Modifier.height(4.dp)) }
        }
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
                    else -> Button(onClick = ::startScan, enabled = !state.busy,
                        colors = ButtonDefaults.buttonColors(containerColor = V2Cyan, contentColor = V2Deep),
                        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp), shape = RoundedCornerShape(16.dp)) {
                        Icon(Icons.Rounded.Search, null, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp))
                        KText(if (!mediaAllowed) "السماح بالوسائط وبدء الفحص" else if (state.result != null) "فحص جديد" else "ابدأ الفحص", size = 15.sp,
                            weight = FontWeight.Bold, color = V2Deep)
                    }
                }
            }
        }
    }
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
        shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, if (active) V2Cyan else V2Soft.copy(alpha = .24f)),
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
            Icon(if (mode == DuplicateScanMode.EXACT) Icons.Rounded.Verified else Icons.Rounded.Compare,
                null, tint = if (mode == DuplicateScanMode.EXACT) V2Green else V2Amber, modifier = Modifier.size(28.dp))
            KText("${formatCount(result.groups.size)} مجموعة ${if (mode == DuplicateScanMode.EXACT) "متطابقة" else "للمراجعة"}", size = 23.sp, weight = FontWeight.Bold)
        }
        if (mode == DuplicateScanMode.EXACT) KText("$extras نسخة زائدة · ${formatBytes(result.groups.sumOf { it.files.drop(1).sumOf { file -> file.sizeBytes } })} حجم النسخ الممكن حذفها",
            size = 15.sp, color = V2Cyan, weight = FontWeight.Medium)
        KText("الفهرس: ${formatCount(result.filesDiscovered)} ملف · قورنت ${formatCount(result.filesCompared)} ملفات · تعذّرت قراءة ${formatCount(result.unreadableFiles)}", size = 12.sp, color = V2Soft)
        KText("${formatScanTime(result.completedAt)} · ${if (mode == DuplicateScanMode.EXACT) "إثبات ببصمة SHA-256" else "تحليل تشابه بصري محلي"}", size = 12.sp, color = V2Soft)
        HorizontalDivider(color = V2Soft.copy(alpha = .18f))
    }
}

@Composable
private fun DuplicateReviewCard(group: DuplicateScanGroup, keeper: String, selected: Set<String>, enabled: Boolean,
    onReview: () -> Unit, onToggle: (DuplicateScanFile) -> Unit, onPreview: (DuplicateScanFile) -> Unit) {
    val exact = group.kind == DuplicateMatchKind.EXACT
    val tint = if (exact) V2Cyan else V2Amber
    Surface(color = V2Panel2, shape = RoundedCornerShape(22.dp), border = BorderStroke(1.dp, tint.copy(alpha = .26f))) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    KText(group.files.first().name, size = 15.sp, weight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    KText("${group.files.size} نسخ · ${if (exact) "محتوى متطابق" else "تشابه يحتاج مراجعتك"}", size = 12.sp, color = tint)
                }
                Icon(if (exact) Icons.Rounded.ContentCopy else Icons.Rounded.Compare, null, tint = tint, modifier = Modifier.size(23.dp))
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
