package com.knoux.ai.cloud.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.CloudQueue
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material3.Checkbox
import androidx.compose.material3.TextButton
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.cloud.model.CloudAccount
import com.knoux.ai.cloud.model.CloudProvider
import com.knoux.ai.cloud.model.CleanupCandidate
import com.knoux.ai.cloud.model.CleanupCategory
import com.knoux.ai.cloud.model.CleanupOperation
import com.knoux.ai.cloud.model.OperationStatus
import com.knoux.ai.cloud.model.ScanResult
import com.knoux.ai.cloud.model.ScanStatus
import com.knoux.ai.cloud.model.StorageAnalytics
import com.knoux.ai.ui.v2.KText
import com.knoux.ai.ui.v2.V2Amber
import com.knoux.ai.ui.v2.V2Background
import com.knoux.ai.ui.v2.V2Blue
import com.knoux.ai.ui.v2.V2Cyan
import com.knoux.ai.ui.v2.V2Deep
import com.knoux.ai.ui.v2.V2GoldText
import com.knoux.ai.ui.v2.V2Green
import com.knoux.ai.ui.v2.V2Pink
import com.knoux.ai.ui.v2.V2Red
import com.knoux.ai.ui.v2.V2Soft
import com.knoux.ai.ui.v2.V2Violet
import com.knoux.ai.ui.v2.V2LuxuryIdentity
import com.knoux.ai.ui.v2.V2GlassCard

private fun providerDisplayName(provider: CloudProvider): String = when (provider) {
    CloudProvider.Gmail -> "Gmail"
    CloudProvider.Outlook -> "Outlook"
    CloudProvider.OneDrive -> "OneDrive"
    CloudProvider.GoogleDrive -> "Google Drive"
    CloudProvider.GooglePhotos -> "Google Photos"
    CloudProvider.iCloudMail -> "iCloud Mail"
}

private fun categoryDisplayName(category: CleanupCategory): String = when (category) {
    CleanupCategory.OldEmail -> "رسائل قديمة"
    CleanupCategory.LargeAttachment -> "مرفقات كبيرة"
    CleanupCategory.Duplicate -> "عناصر مكررة"
    CleanupCategory.Promotion -> "عروض ترويجية"
    CleanupCategory.Social -> "رسائل اجتماعية"
    CleanupCategory.Spam -> "رسائل غير مرغوب فيها"
    CleanupCategory.Draft -> "مسودات"
    CleanupCategory.Sent -> "المرسلات"
    CleanupCategory.LargeFile -> "ملفات كبيرة"
    CleanupCategory.OldFile -> "ملفات قديمة"
    CleanupCategory.DuplicateDocument -> "مستندات مكررة"
    CleanupCategory.DuplicateImage -> "صور مكررة"
    CleanupCategory.DuplicateVideo -> "فيديوهات مكررة"
    CleanupCategory.TrashItem -> "عناصر في سلة المهملات"
    CleanupCategory.DeletedItem -> "عناصر محذوفة"
}

private fun scanStatusDisplayName(status: ScanStatus): String = when (status) {
    ScanStatus.Scanning -> "جاري الفحص…"
    ScanStatus.Completed -> "مكتمل"
    ScanStatus.Failed -> "فشل"
    ScanStatus.Cancelled -> "ملغي"
}

private fun operationStatusDisplayName(status: OperationStatus): String = when (status) {
    OperationStatus.Pending -> "معلق"
    OperationStatus.Executing -> "جارٍ التنفيذ…"
    OperationStatus.Completed -> "مكتمل"
    OperationStatus.Failed -> "فشل"
    OperationStatus.Cancelled -> "ملغي"
}

/**
 * Unified Mail & Cloud Cleaner dashboard (EXT-02).
 * Central hub showing all connected accounts, storage analytics,
 * cleanup candidates, and operation history.
 */
@Composable
fun UnifiedCloudCleanerScreen(
    onBack: () -> Unit,
    onPro: () -> Unit,
) {
    var accounts by remember { mutableStateOf(listOf<CloudAccount>()) }
    var analytics by remember { mutableStateOf(listOf<StorageAnalytics>()) }
    var candidates by remember { mutableStateOf(listOf<CleanupCandidate>()) }
    var operations by remember { mutableStateOf(listOf<CleanupOperation>()) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        accounts = listOf(
            CloudAccount("acc1", CloudProvider.Gmail, "user@gmail.com", "مستخدم Gmail", true, listOf("gmail.read", "gmail.modify"), System.currentTimeMillis() + 86400000, System.currentTimeMillis() - 172800000),
            CloudAccount("acc2", CloudProvider.Outlook, "user@outlook.com", "مستخدم Outlook", true, listOf("mail.read", "mail.write"), System.currentTimeMillis() + 86400000, System.currentTimeMillis() - 86400000),
            CloudAccount("acc3", CloudProvider.OneDrive, "user@onedrive.com", "مستخدم OneDrive", true, listOf("files.read", "files.write"), System.currentTimeMillis() + 86400000, System.currentTimeMillis() - 43200000),
        )
        analytics = listOf(
            StorageAnalytics(CloudProvider.Gmail, "user@gmail.com", 15_000_000_000L, 8_200_000_000L, 6_800_000_000L, 5_100_000_000L, 0L, 0L),
            StorageAnalytics(CloudProvider.Outlook, "user@outlook.com", 50_000_000_000L, 12_500_000_000L, 37_500_000_000L, 3_200_000_000L, 0L, 0L),
            StorageAnalytics(CloudProvider.OneDrive, "user@onedrive.com", 100_000_000_000L, 45_000_000_000L, 55_000_000_000L, 0L, 40_000_000_000L, 5_000_000_000L),
        )
        candidates = listOf(
            CleanupCandidate("c1", "acc1", CloudProvider.Gmail, CleanupCategory.OldEmail, "email_001", "نشرة إخبارية قديمة", 2_500_000L, false, false, System.currentTimeMillis(), "user@gmail.com", true),
            CleanupCandidate("c2", "acc1", CloudProvider.Gmail, CleanupCategory.LargeAttachment, "email_002", "مرفق كبير - تقرير سنوي", 15_000_000L, false, false, System.currentTimeMillis(), "user@gmail.com", true),
            CleanupCandidate("c3", "acc3", CloudProvider.OneDrive, CleanupCategory.DuplicateImage, "file_003", "صورة مكررة - إجازة صيفية", 8_000_000L, false, false, System.currentTimeMillis(), "user@onedrive.com", true),
            CleanupCandidate("c4", "acc2", CloudProvider.Outlook, CleanupCategory.Promotion, "email_004", "عرض ترويجي - متجر إلكتروني", 500_000L, false, false, System.currentTimeMillis(), "user@outlook.com", true),
        )
        operations = listOf(
            CleanupOperation("op1", "تنظيف البريد", 150, 150, 2_500_000_000L, OperationStatus.Completed, System.currentTimeMillis() - 3600000, System.currentTimeMillis() - 3500000),
            CleanupOperation("op2", "تنظيف السحابة", 45, 45, 12_000_000_000L, OperationStatus.Completed, System.currentTimeMillis() - 86400000, System.currentTimeMillis() - 85000000),
        )
    }

    Column(Modifier.fillMaxSize()) {
        V2LuxuryIdentity(
            title = "المنظف الموحد للبريد والسحابة",
            english = "UNIFIED MAIL & CLOUD CLEANER",
            subtitle = "إدارة موحدة لـ Gmail · Outlook · OneDrive · Drive · Photos",
            accent = V2Cyan,
        )

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp, end = 16.dp, bottom = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            item {
                CloudAccountsStrip(accounts = accounts)
            }
            item {
                StorageAnalyticsCard(analytics = analytics)
            }
            item {
                ActionButtons(
                    onScan = { /* scan all accounts */ },
                    onClean = { /* execute cleanup */ },
                )
            }
            item {
                CleanupCandidatesList(candidates = candidates)
            }
            item {
                OperationHistory(operations = operations)
            }
        }
    }
}

@Composable
private fun CloudAccountsStrip(accounts: List<CloudAccount>) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 18.dp,
        glow = V2Cyan,
        borderAlpha = .38f,
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(36.dp).clip(RoundedCornerShape(12.dp))
                        .background(V2Cyan.copy(alpha = .16f))
                        .border(1.dp, V2Cyan.copy(alpha = .42f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Rounded.CloudQueue,
                        contentDescription = null,
                        tint = V2Cyan,
                        modifier = androidx.compose.ui.Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    KText("الحسابات المتصلة", size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                    KText("${accounts.size} حساب مرتبط", size = 8.6.sp, color = V2Soft, maxLines = 1)
                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { /* connect account */ }) {
                    KText("إضافة حساب", size = 9.4.sp, color = V2Cyan)
                }
            }
        }
    }
}

@Composable
private fun StorageAnalyticsCard(analytics: List<StorageAnalytics>) {
    val totalUsed = analytics.sumOf { it.usedBytes }
    val totalFree = analytics.sumOf { it.freeBytes }

    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 18.dp,
        glow = V2GoldText,
        borderAlpha = .38f,
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(36.dp).clip(RoundedCornerShape(12.dp))
                        .background(V2Amber.copy(alpha = .16f))
                        .border(1.dp, V2Amber.copy(alpha = .42f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Rounded.Archive,
                        contentDescription = null,
                        tint = V2Amber,
                        modifier = androidx.compose.ui.Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    KText("تخزين سحابي موحد", size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                    KText("${formatBytes(totalUsed)} مستخدمة من ${formatBytes(totalUsed + totalFree)}", size = 8.6.sp, color = V2Soft, maxLines = 1)
                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { /* view details */ }) {
                    KText("التفاصيل", size = 9.4.sp, color = V2Cyan)
                }
            }
            Spacer(Modifier.height(8.dp))

            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                analytics.forEach { stat ->
                    StorageRow(stat = stat)
                }
            }
        }
    }
}

@Composable
private fun StorageRow(stat: StorageAnalytics) {
    val pct = if (stat.totalBytes > 0) stat.usedBytes.toFloat() / stat.totalBytes else 0f

    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(30.dp).clip(RoundedCornerShape(8.dp))
                .background(providerColor(stat.provider).copy(alpha = .16f))
                .border(1.dp, providerColor(stat.provider).copy(alpha = .42f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            androidx.compose.material3.Icon(
                imageVector = providerIcon(stat.provider),
                contentDescription = null,
                tint = providerColor(stat.provider),
                modifier = androidx.compose.ui.Modifier.size(15.dp),
            )
        }
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            KText(providerDisplayName(stat.provider), size = 11.sp, weight = FontWeight.SemiBold, color = Color.White)
            KText(stat.accountEmail, size = 9.sp, color = V2Soft, maxLines = 1)
        }
        Spacer(Modifier.weight(1f))
        Column {
            KText("${formatBytes(stat.usedBytes)}", size = 10.sp, weight = FontWeight.SemiBold, color = V2GoldText, maxLines = 1)
            KText("${(pct * 100).toInt()}%", size = 9.sp, color = V2Soft, maxLines = 1)
        }
    }
}

@Composable
private fun ActionButtons(onScan: () -> Unit, onClean: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(V2Cyan.copy(alpha = .10f))
                .border(1.dp, V2Cyan.copy(alpha = .42f), RoundedCornerShape(16.dp))
                .clickable(onClick = onScan),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Rounded.CloudQueue,
                    contentDescription = null,
                    tint = V2Cyan,
                    modifier = androidx.compose.ui.Modifier.size(24.dp),
                )
                KText("فحص شامل", size = 11.sp, weight = FontWeight.Bold, color = V2Cyan)
                KText("جميع الحسابات المرتبطة", size = 8.4.sp, color = V2Soft, maxLines = 2)
            }
        }
        Box(
            Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(V2Amber.copy(alpha = .10f))
                .border(1.dp, V2Amber.copy(alpha = .42f), RoundedCornerShape(16.dp))
                .clickable(onClick = onClean),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Rounded.DeleteOutline,
                    contentDescription = null,
                    tint = V2Amber,
                    modifier = androidx.compose.ui.Modifier.size(24.dp),
                )
                KText("تنظيف محدد", size = 11.sp, weight = FontWeight.Bold, color = V2Amber)
                KText("بعد مراجعة العناصر المحددة", size = 8.4.sp, color = V2Soft, maxLines = 2)
            }
        }
    }
}

@Composable
private fun CleanupCandidatesList(candidates: List<CleanupCandidate>) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 18.dp,
        glow = V2Violet,
        borderAlpha = .38f,
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(36.dp).clip(RoundedCornerShape(12.dp))
                        .background(V2Violet.copy(alpha = .16f))
                        .border(1.dp, V2Violet.copy(alpha = .42f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Rounded.History,
                        contentDescription = null,
                        tint = V2Violet,
                        modifier = androidx.compose.ui.Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    KText("عناصر مقترحة للتنظيف", size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                    KText("${candidates.size} عنصر مرشح", size = 8.6.sp, color = V2Soft, maxLines = 1)
                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { /* select all */ }) {
                    KText("تحديد الكل", size = 9.4.sp, color = V2Cyan)
                }
            }

            Spacer(Modifier.height(8.dp))

            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                candidates.forEach { candidate ->
                    CandidateRow(candidate = candidate)
                }
            }
        }
    }
}

@Composable
private fun CandidateRow(candidate: CleanupCandidate) {
    val catColor = when (candidate.category) {
        CleanupCategory.OldEmail -> V2Cyan
        CleanupCategory.LargeAttachment -> V2Pink
        CleanupCategory.Duplicate -> V2Violet
        CleanupCategory.Promotion -> V2Amber
        CleanupCategory.Social -> V2Blue
        CleanupCategory.Spam -> V2Red
        else -> V2Soft
    }

    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 10.dp,
        radius = 12.dp,
        glow = catColor,
        borderAlpha = .28f,
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(30.dp).clip(RoundedCornerShape(8.dp))
                    .background(catColor.copy(alpha = .16f))
                    .border(1.dp, catColor.copy(alpha = .42f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center,
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Rounded.DeleteOutline,
                    contentDescription = null,
                    tint = catColor,
                    modifier = androidx.compose.ui.Modifier.size(15.dp),
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                KText(candidate.itemName ?: "عنصر غير مسمّى", size = 11.sp, weight = FontWeight.SemiBold, color = Color.White)
                KText(categoryDisplayName(candidate.category), size = 9.sp, color = V2Soft, maxLines = 1)
                KText(candidate.sourceAccount ?: "", size = 8.sp, color = V2Soft, maxLines = 1)
            }
            Spacer(Modifier.weight(1f))
            Column {
                KText(formatBytes(candidate.sizeBytes), size = 10.sp, weight = FontWeight.SemiBold, color = V2GoldText, maxLines = 1)
                Spacer(Modifier.height(4.dp))
                Checkbox(
                    checked = candidate.isSelected,
                    onCheckedChange = { /* toggle selection */ },
                    colors = CheckboxDefaults.colors(
                        checkedColor = catColor,
                        uncheckedColor = V2Soft,
                    ),
                )
            }
        }
    }
}

@Composable
private fun OperationHistory(operations: List<CleanupOperation>) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 18.dp,
        glow = V2Green,
        borderAlpha = .38f,
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(36.dp).clip(RoundedCornerShape(12.dp))
                        .background(V2Green.copy(alpha = .16f))
                        .border(1.dp, V2Green.copy(alpha = .42f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Rounded.Restore,
                        contentDescription = null,
                        tint = V2Green,
                        modifier = androidx.compose.ui.Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    KText("سجل العمليات", size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                    KText("${operations.size} عملية مسجلة", size = 8.6.sp, color = V2Soft, maxLines = 1)
                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { /* view full history */ }) {
                    KText("كل السجل", size = 9.4.sp, color = V2Cyan)
                }
            }

            Spacer(Modifier.height(8.dp))

            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                operations.forEach { operation ->
                    OperationRow(operation = operation)
                }
            }
        }
    }
}

@Composable
private fun OperationRow(operation: CleanupOperation) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(30.dp).clip(RoundedCornerShape(8.dp))
                .background(operationStatusColor(operation.status).copy(alpha = .16f))
                .border(1.dp, operationStatusColor(operation.status).copy(alpha = .42f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Rounded.Archive,
                contentDescription = null,
                tint = operationStatusColor(operation.status),
                modifier = androidx.compose.ui.Modifier.size(15.dp),
            )
        }
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            KText(operation.operationType, size = 11.sp, weight = FontWeight.SemiBold, color = Color.White)
            KText("${operation.itemsExecuted} عنصر · ${formatBytes(operation.bytesReclaimed)} تم استردادها", size = 9.sp, color = V2Soft, maxLines = 1)
        }
        Spacer(Modifier.weight(1f))
        KText(operationStatusDisplayName(operation.status), size = 10.sp, weight = FontWeight.SemiBold, color = operationStatusColor(operation.status), maxLines = 1)
    }
}

private fun providerColor(provider: CloudProvider): Color = when (provider) {
    CloudProvider.Gmail -> V2Cyan
    CloudProvider.Outlook -> V2Blue
    CloudProvider.OneDrive -> V2Green
    CloudProvider.GoogleDrive -> V2Amber
    CloudProvider.GooglePhotos -> V2Pink
    CloudProvider.iCloudMail -> V2Violet
}

private fun providerIcon(provider: CloudProvider): androidx.compose.ui.graphics.vector.ImageVector = when (provider) {
    CloudProvider.Gmail -> Icons.Rounded.Email
    CloudProvider.Outlook -> Icons.Rounded.Email
    CloudProvider.OneDrive -> Icons.Rounded.PlayArrow
    CloudProvider.GoogleDrive -> Icons.Rounded.PlayArrow
    CloudProvider.GooglePhotos -> Icons.Rounded.PlayArrow
    CloudProvider.iCloudMail -> Icons.Rounded.Email
}

private fun operationStatusColor(status: OperationStatus): Color = when (status) {
    OperationStatus.Completed -> V2Green
    OperationStatus.Executing -> V2Cyan
    OperationStatus.Failed -> V2Red
    OperationStatus.Cancelled -> V2Soft
    OperationStatus.Pending -> V2Amber
}

private fun formatBytes(bytes: Long): String {
    if (bytes < 1024) return "$bytes بايت"
    val kb = bytes / 1024
    if (kb < 1024) return "$kb كيلوبايت"
    val mb = kb / 1024
    if (mb < 1024) return "$mb ميجابايت"
    val gb = mb / 1024
    return "$gb جيجابايت"
}