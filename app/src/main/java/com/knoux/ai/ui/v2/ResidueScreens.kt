package com.knoux.ai.ui.v2

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CheckBox
import androidx.compose.material.icons.rounded.CheckBoxOutlineBlank
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Inventory
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.database.AppDatabase
import com.knoux.ai.database.KnouxRepository
import com.knoux.ai.database.OperationItemOutcomeEntity
import com.knoux.ai.database.OperationRequestEntity
import com.knoux.ai.database.ScanSessionEntity
import com.knoux.ai.ui.theme.ProTextPrimary
import com.knoux.ai.ui.theme.ProTextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.roundToInt

// ============================================================================
// M03 App Residue Cleanup - Functional Screens (using corrected ResidueSharedViewModel)
// ============================================================================

/** Provides or retrieves the shared ResidueSharedViewModel for the navigation graph scope. */
@Composable
internal fun rememberResidueSharedViewModel(): ResidueSharedViewModel {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val database = remember(application) { AppDatabase.getDatabase(application) }
    val repository = remember(database) { KnouxRepository(database) }
    return viewModel(
        key = "residue-shared-vm",
        initializer = { ResidueSharedViewModel(application, repository) }
    )
}

/** Residue Sources Screen (res-sources) - Shows actual residue sources with real data */
@Composable
fun V2ResidueSourcesScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val sharedVm = rememberResidueSharedViewModel()
    val state = sharedVm.state
    val loaded = state.loaded

    FixedServiceScreen(
        spec = ServiceSpecs.residue,
        status = if (loaded) "مصادر مقروءة" else "جاهز للفحص",
        statusColor = if (loaded) V2Green else V2Violet,
        metric = if (loaded) "${formatBytes(state.facts.cleanFiles.residueBytes)} في ${formatCount(state.facts.cleanFiles.residueFiles)} ملف" else "—",
        scene = { sceneModifier -> ServiceArtScene(ServiceSpecs.residue, sceneModifier) },
        action = {
            KnouxRunOrbBlock(
                progress = if (loaded) 1f else 0f,
                running = state.isScanning,
                enabled = !state.isScanning,
                label = when {
                    state.isScanning -> state.stageLabel
                    loaded -> "اضغط لإعادة الفحص"
                    else -> "اضغط لبدء فحص المصادر"
                },
                accent = V2Violet,
                accent2 = V2Cyan,
                onTap = { if (!state.isScanning) sharedVm.scanResidue() },
            )
        },
        content = {
            if (loaded) {
                ResidueSourcesContent(state.facts, state.includeMediaStore, { sharedVm.setIncludeMediaStore(it) })
            } else {
                ResidueEmptyState(onScan = { sharedVm.scanResidue() })
            }
        },
    )
}

@Composable
internal fun ResidueSourcesContent(
    facts: DeviceFacts,
    includeMediaStore: Boolean,
    onMediaStoreToggle: (Boolean) -> Unit,
) {
    InteriorScaffold(onBack = { /* handled by FixedServiceScreen */ }, onPro = { /* handled by FixedServiceScreen */ }) {
        item { PageHeading("مصادر الملفات المسموحة", "أين يقرأ KNOUX البقايا", Icons.Rounded.Folder, V2Pink) }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 16.dp, radius = 20.dp, glow = V2Pink, borderAlpha = .3f) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ResidueSourceRow(
                        icon = Icons.Rounded.CleaningServices,
                        title = "كاش KNOUX",
                        subtitle = "${formatBytes(facts.ownCacheBytes)} · ${formatCount(facts.ownCacheFiles)} ملف",
                        detail = "cache · code_cache · external_cache",
                        sourceType = "مؤكد",
                        accent = V2Pink,
                    )
                    ResidueSourceRow(
                        icon = Icons.Rounded.Storage,
                        title = "التخزين الكلي",
                        subtitle = formatBytes(facts.storage.usedBytes) + " مستخدم",
                        detail = "قراءة StatFs",
                        sourceType = "نظامي",
                        accent = V2Cyan,
                    )
                    ResidueSourceRow(
                        icon = Icons.Rounded.Image,
                        title = "وسائط مقروءة",
                        subtitle = mediaSummary(facts),
                        detail = "MediaStore بصور/فيديو",
                        sourceType = if (facts.media.permissionGranted) "ممنوح" else "يحتاج إذن",
                        accent = V2Violet,
                    )
                    ResidueSourceRow(
                        icon = Icons.Rounded.Lock,
                        title = "بيانات تطبيقات أخرى",
                        subtitle = "محمية بواسطة Android",
                        detail = "Android/data · Android/obb",
                        sourceType = "مقيد",
                        accent = V2Amber,
                    )
                }
            }
        }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 16.dp, radius = 16.dp, glow = V2Violet, borderAlpha = .25f) {
                Row {
                    androidx.compose.material3.Switch(
                        checked = includeMediaStore,
                        onCheckedChange = onMediaStoreToggle,
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        KText("تضمين MediaStore", size = 13.sp, weight = FontWeight.Bold)
                        KText("فحص بقايا الوسائط القديمة/المعطلة", size = 11.sp, color = ProTextSecondary)
                    }
                }
            }
        }
        item { PreviewNote("لا يتجاوز KNOUX قيود Android. المصادر أعلاه هي كل ما يمكن قراءته دون صلاحيات جذر.") }
    }
}

@Composable
internal fun ResidueSourceRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    detail: String,
    sourceType: String,
    accent: Color,
) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 14.dp, radius = 16.dp, glow = accent, borderAlpha = .25f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(icon, accent, size = 44.dp, iconSize = 22.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row {
                    KText(title, size = 14.sp, weight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    StatusPill(sourceType, accent)
                }
                KText(subtitle, size = 11.sp, color = ProTextSecondary)
            }
            Column(horizontalAlignment = Alignment.End) {
                KText(detail, size = 10.sp, color = ProTextSecondary, maxLines = 2)
            }
        }
    }
}

/** Residue Scan Screen (res-scan) - Actual scanning with progress and results */
@Composable
fun V2ResidueScanScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val sharedVm = rememberResidueSharedViewModel()
    val state = sharedVm.state
    val loaded = state.loaded

    FixedServiceScreen(
        spec = ServiceSpecs.residue,
        status = when {
            state.isScanning -> "جارٍ الفحص… ${(state.progress * 100).roundToInt()}%"
            state.scanError != null -> "خطأ: ${state.scanError}"
            loaded -> "الفحص مكتمل"
            else -> "جاهز للفحص"
        },
        statusColor = when {
            state.isScanning -> V2Amber
            state.scanError != null -> V2Red
            loaded -> V2Green
            else -> V2Violet
        },
        metric = when {
            loaded -> "${formatCount(state.facts.cleanFiles.residueFiles)} ملف · ${formatBytes(state.facts.cleanFiles.residueBytes)}"
            else -> "اضغط الزر الدائري لبدء الفحص"
        },
        scene = { sceneModifier -> ServiceArtScene(ServiceSpecs.residue, sceneModifier) },
        action = {
            KnouxRunOrbBlock(
                progress = when {
                    state.isScanning -> state.progress
                    loaded -> 1f
                    else -> 0f
                },
                running = state.isScanning,
                enabled = !state.isScanning,
                label = when {
                    state.isScanning -> state.stageLabel
                    loaded -> "اضغط لإعادة الفحص"
                    state.scanError != null -> "خطأ - اضغط لإعادة المحاولة"
                    else -> "اضغط لبدء فحص البقايا"
                },
                accent = V2Violet,
                accent2 = V2Cyan,
                onTap = { if (!state.isScanning) sharedVm.scanResidue() },
            )
        },
        content = {
            if (state.isScanning) {
                ScanProgressIndicator(state.progress, state.stageLabel)
            } else if (state.scanError != null) {
                ServiceErrorCard(state.scanError!!) { sharedVm.scanResidue() }
            } else if (loaded) {
                ScanCompleteSummary(state.facts)
            } else {
                V2GlassCard(Modifier.fillMaxWidth().padding(16.dp), padding = 16.dp, radius = 16.dp, glow = V2Violet, borderAlpha = .25f) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row {
                            androidx.compose.material3.Switch(
                                checked = state.includeMediaStore,
                                onCheckedChange = { sharedVm.setIncludeMediaStore(it) },
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                KText("تضمين MediaStore", size = 13.sp, weight = FontWeight.Bold)
                                KText("فحص بقايا الوسائط القديمة/المعطلة", size = 11.sp, color = ProTextSecondary)
                            }
                        }
                    }
                }

                V2GlassCard(Modifier.fillMaxWidth(), padding = 20.dp, radius = 20.dp, glow = V2Violet, borderAlpha = .3f) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Rounded.Radar, "فحص", tint = V2Violet, modifier = Modifier.size(48.dp))
                        KText("اضغط الزر الدائري لبدء فحص البقايا", size = 14.sp, weight = FontWeight.Bold, color = ProTextPrimary)
                        KText("سيفحص: كاش KNOUX + وسائط MediaStore + مجلدات SAF", size = 12.sp, color = ProTextSecondary, align = TextAlign.Center)
                    }
                }
            }
        },
    )
}

@Composable
internal fun ScanProgressIndicator(progress: Float, phase: String) {
    V2GlassCard(Modifier.fillMaxWidth().padding(16.dp), padding = 20.dp, radius = 20.dp, glow = V2Violet, borderAlpha = .4f) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = V2Violet,
                trackColor = V2Violet.copy(alpha = .1f),
                strokeWidth = 6.dp,
                progress = progress,
            )
            KText("جاري الفحص", size = 16.sp, weight = FontWeight.Bold)
            KText("$phase · ${(progress * 100).roundToInt()}%", size = 12.sp, color = ProTextSecondary)
        }
    }
}

@Composable
internal fun ScanCompleteSummary(facts: DeviceFacts) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 16.dp, radius = 20.dp, glow = V2Green, borderAlpha = .3f) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row {
                Icon(Icons.Rounded.CheckCircle, null, tint = V2Green, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(8.dp))
                Column {
                    KText("اكتمل الفحص", size = 16.sp, weight = FontWeight.Bold, color = V2Green)
                    KText("${formatCount(facts.cleanFiles.residueFiles)} ملف · ${formatBytes(facts.cleanFiles.residueBytes)}", size = 12.sp, color = ProTextSecondary)
                }
            }
            // Show breakdown by residue type
            V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 12.dp, glow = V2Cyan, borderAlpha = .2f) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    KText("تفاصيل البقايا", size = 12.sp, weight = FontWeight.Bold, color = V2Cyan)
                    ResidueBreakdownRow("كاش قديمة", facts.cleanFiles.oldCacheBytes, facts.cleanFiles.oldCacheFiles, V2Violet)
                    ResidueBreakdownRow("تنزيلات معطلة", 0L, facts.cleanFiles.brokenDownloads, V2Pink)
                    ResidueBreakdownRow("ملفات OBB", facts.cleanFiles.obbBytes, facts.cleanFiles.obbFiles, V2Blue)
                    ResidueBreakdownRow("كاش المتصفحات", facts.cleanFiles.browserBytes, facts.cleanFiles.browserFiles, V2Cyan)
                    ResidueBreakdownRow("ملفات السجل", 0L, facts.cleanFiles.logFiles, V2Amber)
                }
            }
        }
    }
}

@Composable
internal fun ResidueBreakdownRow(label: String, bytes: Long, count: Int, accent: Color) {
    if (count == 0 && bytes == 0L) return
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        V2IconTile(icon = Icons.Rounded.Layers, accent = accent, size = 28.dp, iconSize = 14.dp)
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            KText(label, size = 11.sp, weight = FontWeight.Bold)
            KText("${formatCount(count)} ملف", size = 9.sp, color = ProTextSecondary)
        }
        KText(formatBytes(bytes), size = 11.sp, weight = FontWeight.Bold, color = accent)
    }
}

/** Residue Candidates Screen (res-candidates) - Shows actual discovered candidates with selection */
@Composable
fun V2ResidueCandidatesScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val sharedVm = rememberResidueSharedViewModel()
    val state = sharedVm.state
    val loaded = state.loaded
    val facts = state.facts

    InteriorScaffold(onBack, onPro) {
        item { PageHeading("الملفات المرشحة", "نتائج الفحص القابلة للتنظيف", Icons.Rounded.Inventory, V2Violet) }
        item {
            if (loaded) {
                V2GlassCard(Modifier.fillMaxWidth(), padding = 16.dp, radius = 20.dp, glow = V2Violet, borderAlpha = .3f) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // KNOUX cache files
                        ResidueCandidateSection(
                            title = "كاش KNOUX",
                            count = facts.ownCacheFiles,
                            bytes = facts.ownCacheBytes,
                            accent = V2Pink,
                            icon = Icons.Rounded.Memory,
                            detail = "ملفات مؤقتة في cache · code_cache · external_cache",
                            itemId = "knoux_own_cache",
                            isSelected = state.selectedItems.contains("knoux_own_cache"),
                            isProtected = state.protectedItems.contains("knoux_own_cache"),
                            onToggle = { sharedVm.toggleCandidateSelection("knoux_own_cache", facts.ownCacheBytes) },
                            onProtect = { sharedVm.addProtectedItem("knoux_own_cache") },
                            onUnprotect = { sharedVm.removeProtectedItem("knoux_own_cache") },
                        )
                        // Media duplicates
                        if (facts.media.duplicateGroups.isNotEmpty()) {
                            facts.media.duplicateGroups.forEachIndexed { groupIndex, group ->
                                group.uris.forEachIndexed { uriIndex, uri ->
                                    val isKeeper = uriIndex == 0
                                    if (!isKeeper) {
                                        ResidueCandidateSection(
                                            title = "مكرر: ${group.uris.first()}",
                                            count = 1,
                                            bytes = group.sizeBytes,
                                            accent = V2Violet,
                                            icon = Icons.Rounded.ContentCopy,
                                            detail = "مجموعة مكررة ${groupIndex + 1} - ${group.count} نسخ",
                                            itemId = uri,
                                            isSelected = state.selectedItems.contains(uri),
                                            isProtected = state.protectedItems.contains(uri),
                                            onToggle = { sharedVm.toggleCandidateSelection(uri, group.sizeBytes) },
                                            onProtect = { sharedVm.addProtectedItem(uri) },
                                            onUnprotect = { sharedVm.removeProtectedItem(uri) },
                                            isKeeper = isKeeper,
                                        )
                                    }
                                }
                            }
                        }
                        // Large media files
                        if (facts.media.largestFiles.isNotEmpty()) {
                            facts.media.largestFiles.forEach { file ->
                                ResidueCandidateSection(
                                    title = file.name,
                                    count = 1,
                                    bytes = file.sizeBytes,
                                    accent = V2Cyan,
                                    icon = if (file.isVideo) Icons.Rounded.Videocam else Icons.Rounded.Image,
                                    detail = "ملف وسائط كبير · ${file.bucket}",
                                    itemId = file.uriString,
                                    isSelected = state.selectedItems.contains(file.uriString),
                                    isProtected = state.protectedItems.contains(file.uriString),
                                    onToggle = { sharedVm.toggleCandidateSelection(file.uriString, file.sizeBytes) },
                                    onProtect = { sharedVm.addProtectedItem(file.uriString) },
                                    onUnprotect = { sharedVm.removeProtectedItem(file.uriString) },
                                )
                            }
                        }
                    }
                }
            } else {
                V2GlassCard(Modifier.fillMaxWidth(), padding = 16.dp, radius = 20.dp, glow = V2Violet, borderAlpha = .3f) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        KText("لا توجد بيانات بعد", size = 14.sp, color = ProTextSecondary)
                        KText("افتح شاشة 'فحص البقايا' (res-scan) لقراءة الملفات المرشحة", size = 12.sp, color = ProTextSecondary)
                    }
                }
            }
        }
        item { PreviewNote("القائمة تظهر الملفات التي حددها الفحص كبقايا محتملة. راجع كل ملف قبل التحديد.") }
    }
}

@Composable
internal fun ResidueCandidateSection(
    title: String,
    count: Int,
    bytes: Long,
    accent: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    detail: String,
    itemId: String,
    isSelected: Boolean,
    isProtected: Boolean,
    onToggle: () -> Unit,
    onProtect: () -> Unit,
    onUnprotect: () -> Unit,
    isKeeper: Boolean = false,
) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 14.dp, radius = 16.dp, glow = accent, borderAlpha = .25f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(icon, accent, size = 40.dp, iconSize = 20.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row {
                    KText(title, size = 14.sp, weight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    if (isKeeper) {
                        StatusPill("حارس", V2Green)
                    } else if (isProtected) {
                        StatusPill("محمي", V2Amber)
                    } else if (isSelected) {
                        StatusPill("محدد", accent)
                    } else {
                        StatusPill("${formatCount(count)} ملف", accent)
                    }
                }
                KText(detail, size = 11.sp, color = ProTextSecondary)
            }
            Column(horizontalAlignment = Alignment.End) {
                if (isProtected) {
                    Icon(Icons.Rounded.Lock, null, tint = V2Amber, modifier = Modifier.size(20.dp))
                } else if (isSelected) {
                    Icon(Icons.Rounded.CheckCircle, null, tint = accent, modifier = Modifier.size(20.dp))
                    KText(formatBytes(bytes), size = 13.sp, weight = FontWeight.Bold, color = accent)
                } else {
                    KText(formatBytes(bytes), size = 13.sp, weight = FontWeight.Bold, color = accent)
                }
                // Selection/protection actions
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (isProtected) {
                        androidx.compose.material3.IconButton(onClick = onUnprotect) {
                            Icon(Icons.Rounded.LockOpen, contentDescription = "إزالة الحماية", tint = V2Amber, modifier = Modifier.size(20.dp))
                        }
                    } else {
                        androidx.compose.material3.IconButton(onClick = onToggle) {
                            Icon(if (isSelected) Icons.Rounded.CheckBox else Icons.Rounded.CheckBoxOutlineBlank, contentDescription = if (isSelected) "إلغاء التحديد" else "تحديد", tint = accent, modifier = Modifier.size(20.dp))
                        }
                        androidx.compose.material3.IconButton(onClick = onProtect) {
                            Icon(Icons.Rounded.Shield, contentDescription = "حماية", tint = V2Amber, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

/** Residue Groups Screen (res-groups) - Groups candidates by legitimate source */
@Composable
fun V2ResidueGroupsScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val sharedVm = rememberResidueSharedViewModel()
    val state = sharedVm.state
    val loaded = state.loaded
    val facts = state.facts

    InteriorScaffold(onBack, onPro) {
        item { PageHeading("تجميع المرشحات حسب المصدر", "كاش أم وسائط أم محمي", Icons.Rounded.Layers, V2Cyan) }
        item {
            if (loaded) {
                V2GlassCard(Modifier.fillMaxWidth(), padding = 16.dp, radius = 20.dp, glow = V2Cyan, borderAlpha = .3f) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        GroupRow("كاش KNOUX مؤكد", facts.ownCacheBytes, facts.ownCacheFiles, V2Pink, "مجلدات cache الخاصة بالتطبيق", state.protectedItems.contains("knoux_own_cache"), state.selectedItems.contains("knoux_own_cache"))
                        GroupRow("صور MediaStore", facts.media.imagesBytes, facts.media.images, V2Violet, "مقروءة بإذن الوسائط", false, false)
                        GroupRow("فيديو MediaStore", facts.media.videosBytes, facts.media.videos, V2Violet, "مقروءة بإذن الوسائط", false, false)
                        GroupRow("مجموعات مكررة", facts.media.duplicateGroups.sumOf { it.reclaimableBytes }, facts.media.duplicateCandidates, V2Amber, "مطابقة محتوى SHA-256", false, false)
                        GroupRow("نظام وتطبيقات أخرى", (facts.storage.usedBytes - facts.ownCacheBytes - facts.media.imagesBytes - facts.media.videosBytes).coerceAtLeast(0L), 0, V2Amber, "مقيد بواسطة Scoped Storage", false, false)
                    }
                }
            } else {
                V2GlassCard(Modifier.fillMaxWidth(), padding = 16.dp, radius = 20.dp, glow = V2Cyan, borderAlpha = .3f) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        KText("لا توجد بيانات بعد", size = 14.sp, color = ProTextSecondary)
                        KText("قم بالفحص أولاً من شاشة 'فحص البقايا'", size = 12.sp, color = ProTextSecondary)
                    }
                }
            }
        }
        item { PreviewNote("التجميع يعتمد على مصدر القراءة لا على تخمين اسم المجلد.") }
    }
}

@Composable
internal fun GroupRow(title: String, bytes: Long, count: Int, accent: Color, sourceDetail: String, isProtected: Boolean, isSelected: Boolean) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 14.dp, radius = 16.dp, glow = accent, borderAlpha = .25f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(icon = Icons.Rounded.Folder, accent = accent, size = 40.dp, iconSize = 20.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row {
                    KText(title, size = 14.sp, weight = FontWeight.Bold)
                    if (isProtected) {
                        Spacer(Modifier.width(8.dp))
                        StatusPill("محمي", V2Amber)
                    } else if (isSelected) {
                        Spacer(Modifier.width(8.dp))
                        StatusPill("محدد", accent)
                    }
                }
                KText("${formatCount(count)} ملف · ${formatBytes(bytes)}", size = 11.sp, color = ProTextSecondary)
                KText(sourceDetail, size = 9.sp, color = ProTextSecondary)
            }
            KText(formatBytes(bytes), size = 13.sp, weight = FontWeight.Bold, color = accent)
        }
    }
}

/** Residue Link Analysis (res-link) - Meaningful association evidence */
@Composable
fun V2ResidueLinkScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val sharedVm = rememberResidueSharedViewModel()
    val state = sharedVm.state
    val loaded = state.loaded
    val facts = state.facts

    InteriorScaffold(onBack, onPro) {
        item { PageHeading("تحليل ارتباط الملف بالتطبيق", "ما نعرفه وما لا نستطيع إثباته", Icons.Rounded.TrackChanges, V2Violet) }
        item {
            if (loaded) {
                V2GlassCard(Modifier.fillMaxWidth(), padding = 16.dp, radius = 20.dp, glow = V2Violet, borderAlpha = .3f) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        LinkRow(Icons.Rounded.Apps, "حزمة KNOUX", "com.knoux.ai.debug", V2Violet, "تطبيقنا - وصول كامل للكاش")
                        LinkRow(Icons.Rounded.Folder, "مجلدات الكاش", "cache · code_cache · external_cache", V2Cyan, "مسارات مؤكدة داخل sandbox")
                        LinkRow(Icons.Rounded.CleaningServices, "ملفات KNOUX مرتبطة", "${formatCount(facts.ownCacheFiles)}", V2Pink, "مملوكة لهذا التطبيق")
                        LinkRow(Icons.Rounded.Search, "أكبر ملف وسائط", facts.media.largestFiles.firstOrNull()?.name ?: "لا ملفات", V2Soft, "من MediaStore بإذن المستخدم")
                        LinkRow(Icons.Rounded.Lock, "بيانات تطبيقات أخرى", "غير قابلة للنسب", V2Amber, "Android يمنع القراءة - لا يمكن إثبات الارتباط")
                    }
                }
            } else {
                V2GlassCard(Modifier.fillMaxWidth(), padding = 16.dp, radius = 20.dp, glow = V2Violet, borderAlpha = .3f) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        KText("لا توجد بيانات بعد", size = 14.sp, color = ProTextSecondary)
                        KText("قم بالفحص أولاً من شاشة 'فحص البقايا'", size = 12.sp, color = ProTextSecondary)
                    }
                }
            }
        }
        item { PreviewNote("لا نستنتج أن ملفًا يخص تطبيقًا آخر من اسمه. الارتباط هنا مؤكد فقط داخل مجلدات KNOUX.") }
    }
}

@Composable
internal fun LinkRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String, accent: Color, evidence: String) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 14.dp, radius = 16.dp, glow = accent, borderAlpha = .25f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(icon, accent, size = 40.dp, iconSize = 20.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                KText(title, size = 13.sp, weight = FontWeight.Bold)
                KText(value, size = 11.sp, color = ProTextSecondary)
                KText(evidence, size = 9.sp, color = ProTextSecondary)
            }
        }
    }
}

/** Residue Detail Preview (res-detail) - Actual item detail and preview */
@Composable
fun V2ResidueDetailScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val sharedVm = rememberResidueSharedViewModel()
    val state = sharedVm.state
    val loaded = state.loaded
    val facts = state.facts

    InteriorScaffold(onBack, onPro) {
        item { PageHeading("معاينة تفاصيل البقايا", "حجم وعدد ومسار ما يمكن مراجعته", Icons.Rounded.Description, V2Cyan) }
        item {
            if (loaded) {
                V2GlassCard(Modifier.fillMaxWidth(), padding = 16.dp, radius = 20.dp, glow = V2Cyan, borderAlpha = .3f) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        DetailRow(Icons.Rounded.Memory, "كاش KNOUX", formatBytes(facts.ownCacheBytes), "${formatCount(facts.ownCacheFiles)} ملف", V2Pink)
                        DetailRow(Icons.Rounded.Folder, "ملفات Dائمة", formatBytes(facts.storage.usedBytes - facts.ownCacheBytes), "بيانات التطبيق، لا تُحذف تلقائيًا", V2Cyan)
                        DetailRow(Icons.Rounded.ContentCopy, "وسائط مكررة", formatBytes(facts.media.duplicateGroups.sumOf { it.reclaimableBytes }), "${formatCount(facts.media.duplicateCandidates)} نسخة مرشحة", V2Violet)
                        DetailRow(Icons.Rounded.Image, "صور", formatBytes(facts.media.imagesBytes), "${formatCount(facts.media.images)} ملف", V2Violet)
                        DetailRow(Icons.Rounded.Videocam, "فيديو", formatBytes(facts.media.videosBytes), "${formatCount(facts.media.videos)} ملف", V2Cyan)
                        DetailRow(Icons.Rounded.Storage, "مساحة متاحة بعد التنظيف", formatBytes(facts.storage.freeBytes + facts.ownCacheBytes), "نظرية - تعتمد على نجاح الحذف", V2Green)
                    }
                }
            } else {
                V2GlassCard(Modifier.fillMaxWidth(), padding = 16.dp, radius = 20.dp, glow = V2Cyan, borderAlpha = .3f) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        KText("لا توجد بيانات بعد", size = 14.sp, color = ProTextSecondary)
                        KText("قم بالفحص أولاً من شاشة 'فحص البقايا'", size = 12.sp, color = ProTextSecondary)
                    }
                }
            }
        }
        item { PreviewNote("المتاح بعد التنظيف قيمة نظرية تعتمد على نجاح الحذف.") }
    }
}

@Composable
internal fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, detail: String, accent: Color) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 14.dp, radius = 16.dp, glow = accent, borderAlpha = .25f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(icon, accent, size = 40.dp, iconSize = 20.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                KText(label, size = 13.sp, weight = FontWeight.Bold)
                KText(detail, size = 11.sp, color = ProTextSecondary)
            }
            KText(value, size = 13.sp, weight = FontWeight.Bold, color = accent)
        }
    }
}

/** Residue Risk Classification (res-risk) - Actual selected operation risk */
@Composable
fun V2ResidueRiskScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val sharedVm = rememberResidueSharedViewModel()
    val state = sharedVm.state
    val loaded = state.loaded
    val facts = state.facts

    InteriorScaffold(onBack, onPro) {
        item { PageHeading("تصنيف المخاطر قبل التنظيف", "آمن أم متوسط أم محمي", Icons.Rounded.Security, V2Amber) }
        item {
            if (loaded) {
                V2GlassCard(Modifier.fillMaxWidth(), padding = 16.dp, radius = 20.dp, glow = V2Amber, borderAlpha = .3f) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        RiskRow(Icons.Rounded.CheckCircle, "آمن", "${formatCount(facts.ownCacheFiles)} ملف كاش KNOUX", V2Green, "ملفات مؤقتة يعيد التطبيق إنشاؤها")
                        RiskRow(Icons.Rounded.WarningAmber, "متوسط", "${formatCount(facts.media.duplicateGroups.size)} مجموعة وسائط مكررة", V2Amber, "تحتاج مراجعة يدوية قبل الحذف")
                        RiskRow(Icons.Rounded.Lock, "محمي", "بيانات التطبيقات الأخرى", V2Pink, "Android يمنع القراءة والكتابة")
                        RiskRow(Icons.Rounded.Info, "مجهول", "ملفات SAF غير مصنفة", V2Cyan, "تعتمد على اختيار المستخدم")
                    }
                }
            } else {
                V2GlassCard(Modifier.fillMaxWidth(), padding = 16.dp, radius = 20.dp, glow = V2Amber, borderAlpha = .3f) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        KText("لا توجد بيانات بعد", size = 14.sp, color = ProTextSecondary)
                        KText("قم بالفحص أولاً من شاشة 'فحص البقايا'", size = 12.sp, color = ProTextSecondary)
                    }
                }
            }
        }
        item { PreviewNote("التصنيف يشرح سبب كل فئة. الملفات المحمية لا تظهر كمرشح حذف أصلًا.") }
    }
}

@Composable
internal fun RiskRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, detail: String, accent: Color, reasoning: String) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 14.dp, radius = 16.dp, glow = accent, borderAlpha = .25f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(icon, accent, size = 40.dp, iconSize = 20.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                KText(label, size = 14.sp, weight = FontWeight.Bold, color = accent)
                KText(detail, size = 11.sp, color = ProTextSecondary)
                KText(reasoning, size = 9.sp, color = ProTextSecondary)
            }
        }
    }
}

/** Residue Exclusions Screen (res-exclusions) - Actual exclusion controls with protection management */
@Composable
fun V2ResidueExclusionsScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val sharedVm = rememberResidueSharedViewModel()
    val state = sharedVm.state

    InteriorScaffold(onBack, onPro) {
        item { PageHeading("استثناء الملفات الآمنة", "ما لن يلمسه التنظيف", Icons.Rounded.Shield, V2Green) }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 16.dp, radius = 20.dp, glow = V2Green, borderAlpha = .3f) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ExclusionRow(Icons.Rounded.Folder, "مستثنى بواسطة النظام", "Android/data", V2Amber, "Scoped Storage يمنع الوصول", false, false, null, null)
                    ExclusionRow(Icons.Rounded.Folder, "مستثنى بواسطة النظام", "Android/obb", V2Amber, "Scoped Storage يمنع الوصول", false, false, null, null)
                    ExclusionRow(Icons.Rounded.Download, "التنزيلات", "تُراجع يدويًا في شاشتها", V2Cyan, "مجلد مشترك - مراجعة منفصلة", false, false, null, null)
                    ExclusionRow(Icons.Rounded.Lock, "بيانات KNOUX الدائمة", "خارج مجلد الكاش", V2Green, "قواعد بيانات، إعدادات، ملفات مستخدم", false, false, null, null)
                    ExclusionRow(Icons.Rounded.Shield, "نسخة محفوظة لكل مجموعة مكررة", "أول ملف في كل مجموعة", V2Pink, "قاعدة KNOUX: لا حذف للملف المحتفظ به", true, false,
                        onProtect = { /* system enforced */ },
                        onUnprotect = { /* system enforced */ })
                }
            }
        }
        item { PreviewNote("الاستثناءات هنا ليست قائمة تجميلية: هذه مسارات لا يمنح Android تطبيقًا عاديًا حق قراءتها.") }
    }
}

@Composable
internal fun ExclusionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    path: String,
    accent: Color,
    reason: String,
    isSystemEnforced: Boolean,
    isProtected: Boolean,
    onProtect: (() -> Unit)?,
    onUnprotect: (() -> Unit)?,
) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 14.dp, radius = 16.dp, glow = accent, borderAlpha = .25f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(icon, accent, size = 40.dp, iconSize = 20.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                KText(label, size = 13.sp, weight = FontWeight.Bold)
                KText(path, size = 11.sp, color = ProTextSecondary)
                KText(reason, size = 9.sp, color = ProTextSecondary)
            }
            if (isSystemEnforced) {
                Icon(Icons.Rounded.Lock, null, tint = accent, modifier = Modifier.size(20.dp))
            } else if (isProtected) {
                androidx.compose.material3.IconButton(onClick = onUnprotect!!) {
                    Icon(Icons.Rounded.LockOpen, contentDescription = "إزالة الحماية", tint = V2Amber, modifier = Modifier.size(20.dp))
                }
            } else {
                androidx.compose.material3.IconButton(onClick = onProtect!!) {
                    Icon(Icons.Rounded.Shield, contentDescription = "حماية", tint = V2Amber, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

/** Residue Confirmation Screen (res-confirm) - Uses shared operation controller with proper lifecycle */
@Composable
fun V2ResidueConfirmScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val sharedVm = rememberResidueSharedViewModel()
    val state = sharedVm.state
    val loaded = state.loaded
    val facts = state.facts
    val scope = rememberCoroutineScope()

    // Dialog state derived from authoritative operation state
    var confirmDialog by remember { mutableStateOf(false) }
    var lastError by remember { mutableStateOf<String?>(null) }

    // Sync dialog visibility with operation state
    LaunchedEffect(state.operationStatus) {
        when (state.operationStatus) {
            OperationStatus.AWAITING_CONSENT -> confirmDialog = true
            OperationStatus.EXECUTING, OperationStatus.VERIFIED, OperationStatus.PARTIAL,
            OperationStatus.NO_CHANGE, OperationStatus.FAILED, OperationStatus.CANCELLED -> {
                confirmDialog = false
            }
            else -> { /* IDLE, PREPARING - no dialog */ }
        }
    }

    // Handle dialog dismissal as explicit cancellation
    val onDialogDismiss = { sharedVm.cancelPendingOperation() }

    InteriorScaffold(onBack, onPro) {
        item { PageHeading("تحديد البقايا وتأكيد الإزالة", "قرارك قبل أي مسح فعلي", Icons.Rounded.DeleteSweep, V2Pink) }
        item {
            if (loaded) {
                V2GlassCard(Modifier.fillMaxWidth(), padding = 16.dp, radius = 20.dp, glow = V2Pink, borderAlpha = .3f) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ConfirmRow("كاش KNOUX", formatBytes(facts.ownCacheBytes), "${formatCount(facts.ownCacheFiles)} ملف", V2Pink)
                        if (facts.media.duplicateGroups.isNotEmpty()) {
                            ConfirmRow("وسائط مكررة", formatBytes(facts.media.duplicateGroups.sumOf { it.reclaimableBytes }), "${formatCount(facts.media.duplicateGroups.size)} مجموعات", V2Violet)
                        }
                        if (facts.cleanFiles.oldCacheBytes > 0) {
                            ConfirmRow("كاش قديمة", formatBytes(facts.cleanFiles.oldCacheBytes), "${formatCount(facts.cleanFiles.oldCacheFiles)} ملف", V2Amber)
                        }
                        // Show selected items count if any
                        if (state.selectedItems.isNotEmpty()) {
                            ConfirmRow("محدد يدويًا", formatBytes(state.selectedBytes), "${formatCount(state.selectedItems.size)} ملف", V2Cyan)
                        }
                    }
                }

                // Show operation result with proper status from authoritative state
                when (state.operationStatus) {
                    OperationStatus.IDLE -> { /* No result to show */ }
                    OperationStatus.PREPARING -> { /* Preparation in progress */ }
                    OperationStatus.AWAITING_CONSENT -> { /* Dialog handles this */ }
                    OperationStatus.VERIFIED -> {
                        OperationResultCard(
                            title = "تم التنظيف بنجاح - تم التحقق",
                            message = state.lastFreedBytes?.let { "حُرر ${formatBytes(it)} من كاش KNOUX." } ?: "",
                            result = state.lastOperationResult ?: "SUCCESS",
                            color = V2Green,
                            icon = Icons.Rounded.Verified,
                        )
                    }
                    OperationStatus.PARTIAL -> {
                        OperationResultCard(
                            title = "تنظيف جزئي - بعض الملفات لم تُحذف",
                            message = state.lastFreedBytes?.let { "حُرر ${formatBytes(it)} من كاش KNOUX." } ?: "",
                            result = state.lastOperationResult ?: "PARTIAL",
                            color = V2Amber,
                            icon = Icons.Rounded.WarningAmber,
                        )
                    }
                    OperationStatus.NO_CHANGE -> {
                        OperationResultCard(
                            title = "لا تغيير - الكاش كان فارغاً",
                            message = "",
                            result = state.lastOperationResult ?: "NO_CHANGE",
                            color = V2Cyan,
                            icon = Icons.Rounded.Info,
                        )
                    }
                    OperationStatus.FAILED -> {
                        OperationResultCard(
                            title = "فشل التنظيف",
                            message = "تعذر تنفيذ العملية. راجع السجل للتفاصيل.",
                            result = state.lastOperationResult ?: "ERROR",
                            color = V2Red,
                            icon = Icons.Rounded.Error,
                        )
                    }
                    OperationStatus.CANCELLED -> {
                        OperationResultCard(
                            title = "تم الإلغاء",
                            message = "لم يتم حذف أي ملف.",
                            result = state.lastOperationResult ?: "CANCELLED",
                            color = V2Amber,
                            icon = Icons.Rounded.Close,
                        )
                    }
                    OperationStatus.EXECUTING -> {
                        ExecutingCard()
                    }
                }

                V2GlassCard(Modifier.fillMaxWidth(), padding = 16.dp, radius = 16.dp, glow = V2Amber, borderAlpha = .3f) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        KText("⚠️ تحذيرات", size = 14.sp, weight = FontWeight.Bold, color = V2Amber)
                        KText("• مسح الكاش لا يمس صورك أو فيديوهاتك", size = 11.sp, color = ProTextSecondary)
                        KText("• حذف الوسائط له مسار منفصل وحوار MediaStore رسمي", size = 11.sp, color = ProTextSecondary)
                        KText("• الملفات المستثناة (Android/data, Android/obb) لا تُمس", size = 11.sp, color = ProTextSecondary)
                        KText("• لا يمكن استعادة الملفات المحذوفة من MediaStore إلا من السلة", size = 11.sp, color = ProTextSecondary)
                        KText("• الملفات المحمية والمحددة كحارس لا تُحذف", size = 11.sp, color = ProTextSecondary)
                    }
                }

                // Prepare button - only enabled in IDLE or final states
                V2GradientButton("تنظيف كاش KNOUX الآن", {
                    if (!state.canPrepareCleanup) return@V2GradientButton
                    scope.launch {
                        val prep = sharedVm.prepareCleanup()
                        if (prep.success) {
                            confirmDialog = true
                        } else {
                            lastError = prep.errorMessage
                        }
                    }
                }, icon = Icons.Rounded.DeleteSweep, modifier = Modifier.fillMaxWidth())
            } else {
                V2GlassCard(Modifier.fillMaxWidth(), padding = 16.dp, radius = 20.dp, glow = V2Pink, borderAlpha = .3f) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        KText("لا توجد بيانات بعد", size = 14.sp, color = ProTextSecondary)
                        KText("قم بالفحص أولاً من شاشة 'فحص البقايا'", size = 12.sp, color = ProTextSecondary)
                    }
                }
            }
        }
        item { PreviewNote("مسح الكاش لا يمس صورك أو فيديوهاتك. حذف الوسائط له مسار منفصل وحوار MediaStore رسمي.") }
    }

    // Error toast
    lastError?.let { error ->
        LaunchedEffect(error) {
            // In a real app, show a snackbar/toast
            lastError = null
        }
    }

    // Confirmation dialog - lifecycle bound to AWAITING_CONSENT state
    if (confirmDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = onDialogDismiss,
            title = { KText("تنظيف بقايا KNOUX", size = 16.sp, weight = FontWeight.Bold) },
            text = {
                KText(
                    "سيُحذف ${formatBytes(facts.ownCacheBytes)} من الكاش والملفات المؤقتة الخاصة بـ KNOUX فقط " +
                        "(${formatCount(facts.ownCacheFiles)} ملف). ملفاتك الدائمة لن تُمس.",
                    size = 12.sp,
                    color = V2Soft,
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    confirmDialog = false
                    scope.launch {
                        val result = sharedVm.executeCleanup(
                            operationId = state.pendingOperationId!!,
                            consentGranted = true
                        )
                        if (!result.success) {
                            lastError = result.errorMessage
                        }
                    }
                }) { KText("تنظيف الآن", size = 12.sp, weight = FontWeight.Bold, color = V2Cyan) }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = {
                    onDialogDismiss()
                }) { KText("إلغاء", size = 12.sp, color = V2Soft) }
            },
        )
    }
}

@Composable
internal fun ConfirmRow(label: String, bytes: String, count: String, accent: Color) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 14.dp, radius = 16.dp, glow = accent, borderAlpha = .25f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(icon = Icons.Rounded.Layers, accent = accent, size = 40.dp, iconSize = 20.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                KText(label, size = 13.sp, weight = FontWeight.Bold)
                KText(count, size = 11.sp, color = ProTextSecondary)
            }
            KText(bytes, size = 13.sp, weight = FontWeight.Bold, color = accent)
        }
    }
}

@Composable
internal fun OperationResultCard(
    title: String,
    message: String,
    result: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 10.dp, radius = 16.dp, glow = color, borderAlpha = .45f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = "حالة العملية",
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                KText(title, size = 10.sp, weight = FontWeight.Bold, color = color)
                if (message.isNotBlank()) {
                    KText(message, size = 9.sp, color = V2Soft)
                }
                KText("نتيجة العملية: $result", size = 9.sp, color = V2Soft)
            }
        }
    }
}

@Composable
internal fun ExecutingCard() {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 10.dp, radius = 16.dp, glow = V2Violet, borderAlpha = .45f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = V2Violet,
                strokeWidth = 3.dp,
            )
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                KText("جاري التنفيذ…", size = 10.sp, weight = FontWeight.Bold, color = V2Violet)
                KText("الرجاء عدم إغلاق التطبيق", size = 9.sp, color = V2Soft)
            }
        }
    }
}

/** Residue History Screen (res-history) - Uses genuine persisted operation history */
@Composable
fun V2ResidueHistoryScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val sharedVm = rememberResidueSharedViewModel()
    val application = context.applicationContext as Application
    val database = remember(application) { AppDatabase.getDatabase(application) }
    val repository = remember(database) { KnouxRepository(database) }
    val history = repository.recentOperations(50).collectAsState(initial = emptyList()).value
        .filter { it.serviceId == "app_residue" }
    val state = sharedVm.state

    InteriorScaffold(onBack, onPro) {
        item { PageHeading("إعادة الفحص وسجل العمليات", "كل قياس محفوظ محليًا", Icons.Rounded.History, V2Green) }
        item {
            if (history.isEmpty()) {
                ServiceEmptyState("لا توجد عمليات", "سجل تنظيف البقايا فارغ", Icons.Rounded.History, V2Green)
            } else {
                LazyColumn(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(history) { op ->
                        OperationHistoryCard(op)
                    }
                }
            }
        }
        item {
            V2GhostButton("إعادة الفحص الآن", { sharedVm.scanResidue() }, icon = Icons.Rounded.Refresh, height = 48.dp)
        }
    }
}

@Composable
internal fun OperationHistoryCard(op: OperationRequestEntity) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 14.dp, radius = 16.dp, glow = V2Cyan, borderAlpha = .25f) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row {
                KText(serviceLabel(op.serviceId), size = 13.sp, weight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                StatusPill(op.status, V2Cyan)
            }
            KText(op.requestedAction, size = 11.sp, color = ProTextSecondary)
            Row {
                KText(formatScanTime(op.createdAt), size = 10.sp, color = ProTextSecondary)
                Spacer(Modifier.weight(1f))
                KText(formatBytes(op.predictedEligibleBytes ?: 0L), size = 11.sp, weight = FontWeight.Bold, color = V2Cyan)
            }
        }
    }
}

// ============================================================================
// Helper Composables & Data Classes
// ============================================================================

@Composable
internal fun ResidueEmptyState(onScan: () -> Unit) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 32.dp, radius = 24.dp, glow = V2Violet, borderAlpha = .3f) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(Icons.Rounded.FolderOpen, "بلا بقايا", tint = V2Violet, modifier = Modifier.size(64.dp))
            KText("لا توجد بقايا معروفة", size = 18.sp, weight = FontWeight.Bold)
            KText("اضغط الزر الدائري لفحص البقايا", size = 13.sp, color = ProTextSecondary)
            V2GradientButton("بدء الفحص", onScan, icon = Icons.Rounded.Radar, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
internal fun ServiceErrorCard(message: String, onRetry: () -> Unit) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 16.dp, radius = 16.dp, glow = V2Red, borderAlpha = .3f) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row {
                Icon(Icons.Rounded.Error, null, tint = V2Red, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                KText("خطأ", size = 13.sp, weight = FontWeight.Bold, color = V2Red)
            }
            KText(message, size = 11.sp, color = ProTextSecondary)
            V2GradientButton("إعادة المحاولة", onRetry, icon = Icons.Rounded.Refresh, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
internal fun ServiceEmptyState(title: String, body: String, icon: androidx.compose.ui.graphics.vector.ImageVector, accent: Color) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 24.dp, radius = 20.dp, glow = accent, borderAlpha = .3f) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(48.dp))
            KText(title, size = 16.sp, weight = FontWeight.Bold)
            KText(body, size = 12.sp, color = ProTextSecondary, align = TextAlign.Center)
        }
    }
}

@Composable
internal fun StatusPill(text: String, color: Color) {
    Box(
        Modifier
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .background(color.copy(alpha = .15f))
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center,
    ) {
        KText(text, size = 9.sp, weight = FontWeight.Bold, color = color)
    }
}