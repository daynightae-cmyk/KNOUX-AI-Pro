package com.knoux.ai.ui.screens

import com.knoux.ai.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.knoux.ai.ui.components.GlassPanel
import com.knoux.ai.ui.components.GradientActionButton
import com.knoux.ai.ui.components.GhostButton
import com.knoux.ai.ui.components.ProgressDonut
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProBlue
import com.knoux.ai.ui.theme.ProSafe
import com.knoux.ai.ui.theme.ProTextPrimary
import com.knoux.ai.ui.theme.ProTextSecondary
import com.knoux.ai.ui.theme.ProWarning

private enum class DupScreen { SCAN, REVIEW, CONFIRM, RESULTS }

data class DupFile(
    val id: String,
    val name: String,
    val path: String,
    val sizeBytes: Long,
    val type: String,
    val isProtectedOriginal: Boolean = false,
)

data class DupGroup(
    val id: String,
    val name: String,
    val files: List<DupFile>,
    val totalSizeBytes: Long,
)

private val dummyGroups = listOf(
    DupGroup(
        id = "g1",
        name = "Similar Selfies",
        files = listOf(
            DupFile("f1", "selfie_001.jpg", "/DCIM/Camera/selfie_001.jpg", 2_457_600, "image", isProtectedOriginal = true),
            DupFile("f2", "selfie_002.jpg", "/DCIM/Camera/selfie_002.jpg", 2_457_600, "image"),
            DupFile("f3", "selfie_copy.jpg", "/Downloads/selfie_copy.jpg", 2_457_600, "image"),
        ),
        totalSizeBytes = 7_372_800,
    ),
    DupGroup(
        id = "g2",
        name = "Burst Photos",
        files = listOf(
            DupFile("f4", "burst_01.jpg", "/DCIM/Camera/burst_01.jpg", 3_145_728, "image", isProtectedOriginal = true),
            DupFile("f5", "burst_02.jpg", "/DCIM/Camera/burst_02.jpg", 3_145_728, "image"),
            DupFile("f6", "burst_03.jpg", "/DCIM/Camera/burst_03.jpg", 3_145_728, "image"),
            DupFile("f7", "burst_04.jpg", "/DCIM/Camera/burst_04.jpg", 3_145_728, "image"),
        ),
        totalSizeBytes = 12_582_912,
    ),
    DupGroup(
        id = "g3",
        name = "Duplicate Videos",
        files = listOf(
            DupFile("f8", "vacation.mp4", "/DCIM/Camera/vacation.mp4", 25_165_824, "video", isProtectedOriginal = true),
            DupFile("f9", "vacation_copy.mp4", "/Downloads/vacation_copy.mp4", 25_165_824, "video"),
        ),
        totalSizeBytes = 50_331_648,
    ),
    DupGroup(
        id = "g4",
        name = "Repeated Documents",
        files = listOf(
            DupFile("f10", "report.pdf", "/Documents/report.pdf", 1_048_576, "document", isProtectedOriginal = true),
            DupFile("f11", "report_copy.pdf", "/Documents/report_copy.pdf", 1_048_576, "document"),
            DupFile("f12", "report_final.pdf", "/Downloads/report_final.pdf", 1_048_576, "document"),
        ),
        totalSizeBytes = 3_145_728,
    ),
)

@Composable
fun DuplicateFilesScreen(onBack: () -> Unit) {
    var screen by remember { mutableStateOf(DupScreen.SCAN) }
    var scanProgress by remember { mutableFloatStateOf(0f) }
    var filesScanned by remember { mutableIntStateOf(0) }
    var groupsFound by remember { mutableIntStateOf(0) }
    var scanning by remember { mutableStateOf(false) }
    var groups by remember { mutableStateOf<List<DupGroup>>(dummyGroups) }
    var selectedFileIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var scanComplete by remember { mutableStateOf(false) }

    if (screen == DupScreen.SCAN) {
        ScanPhase(
            scanProgress = scanProgress,
            filesScanned = filesScanned,
            groupsFound = groupsFound,
            scanning = scanning,
            onToggleScan = {
                if (!scanning) {
                    scanning = true
                    scanProgress = 0f
                    filesScanned = 0
                    groupsFound = 0
                    androidx.compose.runtime.LaunchedEffect(Unit) {
                        for (i in 1..100) {
                            if (!scanning) break
                            delay(30)
                            scanProgress = i / 100f
                            filesScanned = i * 12
                            groupsFound = i / 4
                        }
                        if (scanning) {
                            scanComplete = true
                            scanning = false
                            scanProgress = 1f
                        }
                    }
                } else {
                    scanning = false
                }
            },
            onScanComplete = { screen = DupScreen.REVIEW },
        )
    } else if (screen == DupScreen.REVIEW) {
        ReviewPhase(
            groups = groups,
            selectedFileIds = selectedFileIds,
            onBack = { screen = DupScreen.SCAN },
            onToggleFile = { fileId ->
                selectedFileIds = if (selectedFileIds.contains(fileId)) selectedFileIds - fileId else selectedFileIds + fileId
            },
            onToggleAll = { group ->
                val fileIds = group.files.filter { !it.isProtectedOriginal }.map { it.id }.toSet()
                selectedFileIds = if (selectedFileIds.containsAll(fileIds)) selectedFileIds - fileIds else selectedFileIds + fileIds
            },
            onConfirm = { screen = DupScreen.CONFIRM },
        )
    } else if (screen == DupScreen.CONFIRM) {
        val filesToDelete = groups.flatMap { it.files }.filter { it.id in selectedFileIds }
        val totalSize = filesToDelete.sumOf { it.sizeBytes }
        ConfirmPhase(
            filesToDelete = filesToDelete,
            totalSize = totalSize,
            onBack = { screen = DupScreen.REVIEW },
            onConfirm = { screen = DupScreen.RESULTS },
        )
    } else {
        val filesDeleted = selectedFileIds.size
        val spaceSaved = groups.flatMap { it.files }.filter { it.id in selectedFileIds }.sumOf { it.sizeBytes }
        ResultsPhase(
            filesDeleted = filesDeleted,
            spaceSaved = spaceSaved,
            groupsCount = groups.size,
            onFinish = { },
        )
    }
}

@Composable
private fun ScanPhase(
    scanProgress: Float,
    filesScanned: Int,
    groupsFound: Int,
    scanning: Boolean,
    onToggleScan: () -> Unit,
    onScanComplete: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(24.dp))
        Icon(
            image = Icons.Default.Refresh,
            contentDescription = null,
            tint = ProArcticCyan,
            modifier = Modifier.size(64.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "مسح الملفات المكررة",
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ProTextPrimary),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "جميع الملفات على جهازك، لا شيء يغادر الجهاز",
            style = TextStyle(fontSize = 12.sp, color = ProTextSecondary),
        )
        Spacer(Modifier.height(32.dp))
        GlassPanel(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
            padding = 24.dp,
            radius = 20.dp,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ProgressDonut(
                    progress = scanProgress,
                    centerText = "${(scanProgress * 100).toInt()}%",
                    caption = "المسح",
                    arcColors = listOf(ProArcticCyan, ProBlue),
                    size = 160.dp,
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "ملفات مفحوصة: $filesScanned",
                    style = TextStyle(fontSize = 14.sp, color = ProTextSecondary),
                )
                Text(
                    text = "مجموعات مكررة مكتشفة: $groupsFound",
                    style = TextStyle(fontSize = 14.sp, color = ProTextSecondary),
                )
                Spacer(Modifier.height(24.dp))
                GradientActionButton(
                    text = if (scanning) "إيقاف المسح" else "بدء المسح",
                    onClick = onToggleScan,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        if (scanProgress >= 1f) {
            GradientActionButton(
                text = "عرض المجموعات المكررة",
                onClick = onScanComplete,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
            )
        }
    }
}

@Composable
private fun ReviewPhase(
    groups: List<DupGroup>,
    selectedFileIds: Set<String>,
    onBack: () -> Unit,
    onToggleFile: (String) -> Unit,
    onToggleAll: (DupGroup) -> Unit,
    onConfirm: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.CenterHorizontally,
        ) {
            GhostButton(
                text = "العودة للمسح",
                onClick = onBack,
                icon = Icons.Rounded.ChevronRight,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "المجموعات المكررة",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ProTextPrimary),
            )
            Spacer(Modifier.weight(1f))
            Icon(
                image = Icons.Default.Info,
                contentDescription = null,
                tint = ProArcticCyan,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.height(8.dp))
        GlassPanel(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            padding = 16.dp,
            radius = 16.dp,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MiniDupCard(
                    label = "المجموعات",
                    value = groups.size.toString(),
                    icon = Icons.Rounded.Groups,
                    accent = ProArcticCyan,
                )
                MiniDupCard(
                    label = "قابل للاستعادة",
                    value = formatBytes(groups.sumOf { it.totalSizeBytes }),
                    icon = Icons.Rounded.Storage,
                    accent = ProBlue,
                )
                MiniDupCard(
                    label = "النسخ المكررة",
                    value = groups.sumOf { it.files.size - 1 }.toString(),
                    icon = Icons.Rounded.Layers,
                    accent = ProWarning,
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = "اختر النسخ المكررة للحذف (يُحتفظ بنسخة أصلية واحدة لكل مجموعة)",
            style = TextStyle(fontSize = 12.sp, color = ProTextSecondary),
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, bottom = 100.dp),
        ) {
            items(groups.size) { index ->
                val group = groups[index]
                DupGroupRow(
                    group = group,
                    selectedFileIds = selectedFileIds,
                    onToggleFile = onToggleFile,
                    onToggleAll = { onToggleAll(group) },
                )
            }
        }
    }
}

@Composable
private fun ConfirmPhase(
    filesToDelete: List<DupFile>,
    totalSize: Long,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(40.dp))
        Icon(
            image = Icons.Rounded.Delete,
            contentDescription = null,
            tint = ProWarning,
            modifier = Modifier.size(48.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "تأكيد الحذف",
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ProTextPrimary),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "سيتم حذف ${filesToDelete.size} نسخة مكررة وتوفير ${formatBytes(totalSize)}",
            style = TextStyle(fontSize = 14.sp, color = ProTextSecondary),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))
        GlassPanel(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
            padding = 20.dp,
            radius = 20.dp,
        ) {
            Column {
                Text(
                    text = "الملفات المراد حذفها:",
                    style = TextStyle(fontSize = 12.sp, color = ProTextSecondary),
                )
                Spacer(Modifier.height(8.dp))
                filesToDelete.take(5).forEach { file ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            image = Icons.Rounded.Description,
                            contentDescription = null,
                            tint = ProTextSecondary,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = file.name,
                            style = TextStyle(fontSize = 13.sp, color = ProTextPrimary),
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = formatBytes(file.sizeBytes),
                            style = TextStyle(fontSize = 13.sp, color = ProTextSecondary),
                        )
                    }
                }
                if (filesToDelete.size > 5) {
                    Text(
                        text = "... و ${filesToDelete.size - 5} ملف آخر",
                        style = TextStyle(fontSize = 12.sp, color = ProTextSecondary),
                    )
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            GhostButton(
                text = "العودة",
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
            )
            GradientActionButton(
                text = "حذف المؤكد",
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ResultsPhase(
    filesDeleted: Int,
    spaceSaved: Long,
    groupsCount: Int,
    onFinish: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(40.dp))
        Icon(
            image = Icons.Rounded.Check,
            contentDescription = null,
            tint = ProSafe,
            modifier = Modifier.size(64.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "تم الانتهاء من المسح",
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ProTextPrimary),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "تم حذف $filesDeleted نسخة مكررة وتوفير ${formatBytes(spaceSaved)}",
            style = TextStyle(fontSize = 14.sp, color = ProTextSecondary),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))
        GlassPanel(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
            padding = 24.dp,
            radius = 20.dp,
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    ResultChip(
                        label = "المجموعات المعالجة",
                        value = groupsCount.toString(),
                        icon = Icons.Rounded.Groups,
                        accent = ProArcticCyan,
                    )
                    ResultChip(
                        label = "النسخ المحذوفة",
                        value = filesDeleted.toString(),
                        icon = Icons.Rounded.Delete,
                        accent = ProWarning,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    ResultChip(
                        label = "المساحة المستعادة",
                        value = formatBytes(spaceSaved),
                        icon = Icons.Rounded.Storage,
                        accent = ProBlue,
                    )
                    ResultChip(
                        label = "الكفاءة",
                        value = "100%",
                        icon = Icons.Rounded.Check,
                        accent = ProSafe,
                    )
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        GradientActionButton(
            text = "إنهاء",
            onClick = { },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
        )
    }
}

@Composable
private fun MiniDupCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            image = icon,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = value,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ProTextPrimary),
        )
        Text(
            text = label,
            style = TextStyle(fontSize = 10.sp, color = ProTextSecondary),
        )
    }
}

@Composable
private fun DupGroupRow(
    group: DupGroup,
    selectedFileIds: Set<String>,
    onToggleFile: (String) -> Unit,
    onToggleAll: () -> Unit,
) {
    val nonOriginalFiles = group.files.filter { !it.isProtectedOriginal }
    val selectedInGroup = nonOriginalFiles.map { it.id }.toSet().intersect(selectedFileIds)
    val allSelected = selectedInGroup.size == nonOriginalFiles.size && nonOriginalFiles.isNotEmpty()

    GlassPanel(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        padding = 14.dp,
        radius = 14.dp,
    ) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onToggleAll),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = allSelected,
                    onCheckedChange = { onToggleAll() },
                    colors = androidx.compose.material3.CheckboxDefaults.colors(
                        checkedColor = ProArcticCyan,
                        uncheckedColor = ProTextSecondary,
                    ),
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    image = Icons.Rounded.Image,
                    contentDescription = null,
                    tint = ProArcticCyan,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = group.name,
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ProTextPrimary),
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = formatBytes(group.totalSizeBytes),
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ProTextSecondary),
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Top,
            ) {
                group.files.forEach { file ->
                    val isSelected = selectedFileIds.contains(file.id)
                    val isProtected = file.isProtectedOriginal
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp)
                                .background(
                                    if (isProtected) ProTextSecondary.copy(alpha = 0.1f) else ProArcticCyan.copy(alpha = 0.1f)
                                )
                                .clip(RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                image = when (file.type) {
                                    "image" -> Icons.Rounded.Image
                                    "video" -> Icons.Rounded.Movie
                                    "audio" -> Icons.Rounded.MusicNote
                                    else -> Icons.Rounded.Description
                                },
                                contentDescription = null,
                                tint = if (isProtected) ProTextSecondary else ProArcticCyan,
                                modifier = Modifier.size(20.dp),
                            )
                            if (isProtected) {
                                Icon(
                                    image = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = ProSafe,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 4.dp, y = -4.dp)
                                        .size(12.dp),
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = file.name,
                            style = TextStyle(fontSize = 10.sp, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, color = ProTextPrimary),
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = formatBytes(file.sizeBytes),
                            style = TextStyle(fontSize = 9.sp, color = ProTextSecondary),
                        )
                        if (!isProtected) {
                            androidx.compose.material3.Checkbox(
                                checked = isSelected,
                                onCheckedChange = { onToggleFile(file.id) },
                                colors = androidx.compose.material3.CheckboxDefaults.colors(
                                    checkedColor = ProArcticCyan,
                                    uncheckedColor = ProTextSecondary.copy(alpha = 0.5f),
                                ),
                            )
                        } else {
                            Text(
                                text = "الأصل",
                                style = TextStyle(fontSize = 8.sp, color = ProSafe),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultChip(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            image = icon,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = value,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ProTextPrimary),
        )
        Text(
            text = label,
            style = TextStyle(fontSize = 10.sp, color = ProTextSecondary),
        )
    }
}

private fun formatBytes(bytes: Long): String {
    val kb = bytes / 1024
    val mb = kb / 1024
    val gb = mb / 1024
    return when {
        gb > 0 -> "$gb GB"
        mb > 0 -> "$mb MB"
        kb > 0 -> "$kb KB"
        else -> "$bytes B"
    }
}