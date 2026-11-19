package com.knoux.ai.google.ui

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
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.cloud.model.CleanupCandidate
import com.knoux.ai.cloud.model.CleanupCategory
import com.knoux.ai.cloud.model.CleanupOperation
import com.knoux.ai.cloud.model.OperationStatus
import com.knoux.ai.cloud.model.ScanResult
import com.knoux.ai.cloud.model.ScanStatus
import com.knoux.ai.cloud.model.StorageAnalytics
import com.knoux.ai.cloud.model.CloudProvider
import com.knoux.ai.google.auth.GoogleAccountManager
import com.knoux.ai.google.auth.GoogleAuthProvider
import com.knoux.ai.google.drive.GoogleDriveConnector
import com.knoux.ai.google.gmail.GmailConnector
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

/**
 * Google Storage Dashboard (GOOGLE-05).
 * Unified view of Gmail + Drive + Photos storage with cleanup candidates.
 */
@Composable
fun GoogleStorageDashboardScreen(
    onBack: () -> Unit,
    onPro: () -> Unit,
) {
    val context = LocalContext.current
    val vault = com.knoux.ai.developer.vault.CredentialVault(context)
    val accountManager = com.knoux.ai.google.auth.GoogleAccountManager(context, vault)
    val authProvider = GoogleAuthProvider(
        context = context,
        clientId = com.knoux.ai.BuildConfig.GOOGLE_WEB_CLIENT_ID,
        redirectUri = "com.knoux.ai.pro:/oauth2redirect",
    )

    val gmailConnector = GmailConnector(context, accountManager, authProvider)
    val driveConnector = GoogleDriveConnector(context, accountManager, authProvider)
    val coroutineScope = rememberCoroutineScope()

    var accounts by remember { mutableStateOf(listOf<com.knoux.ai.cloud.model.CloudAccount>()) }
    var analytics by remember { mutableStateOf(listOf<StorageAnalytics>()) }
    var candidates by remember { mutableStateOf(listOf<CleanupCandidate>()) }
    var scanResults by remember { mutableStateOf(listOf<ScanResult>()) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        accounts = accountManager.getConnectedAccounts()
        analytics = listOf(
            StorageAnalytics(
                provider = CloudProvider.Gmail,
                accountEmail = accounts.firstOrNull { it.provider == CloudProvider.Gmail }?.email ?: "—",
                totalBytes = 15_000_000_000L,
                usedBytes = 8_200_000_000L,
                freeBytes = 6_800_000_000L,
                emailBytes = 8_200_000_000L,
            ),
            StorageAnalytics(
                provider = CloudProvider.GoogleDrive,
                accountEmail = accounts.firstOrNull { it.provider == CloudProvider.GoogleDrive }?.email ?: "—",
                totalBytes = 100_000_000_000L,
                usedBytes = 45_000_000_000L,
                freeBytes = 55_000_000_000L,
                driveBytes = 40_000_000_000L,
                photosBytes = 5_000_000_000L,
            ),
        )
    }

    Column(Modifier.fillMaxSize()) {
        V2LuxuryIdentity(
            title = "لوحة تخزين جوجل",
            english = "GOOGLE STORAGE DASHBOARD",
            subtitle = "Gmail · Google Drive · Google Photos — تحليل وفحص موحد",
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
                GoogleAccountsStrip(accounts = accounts)
            }
            item {
                GoogleStorageAnalytics(analytics = analytics)
            }
            item {
                GoogleActionButtons(
                    onScanGmail = { coroutineScope.launch {
                        val result = gmailConnector.scanAccount(accounts.firstOrNull { it.provider == CloudProvider.Gmail }?.id ?: "")
                        scanResults = scanResults + result
                    } },
                    onScanDrive = { coroutineScope.launch {
                        val result = driveConnector.scanAccount(accounts.firstOrNull { it.provider == CloudProvider.GoogleDrive }?.id ?: "")
                        scanResults = scanResults + result
                    } },
                )
            }
            item {
                GoogleScanResults(results = scanResults)
            }
            item {
                GoogleCandidatesList(candidates = candidates)
            }
        }
    }
}

@Composable
private fun GoogleAccountsStrip(accounts: List<com.knoux.ai.cloud.model.CloudAccount>) {
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
                        imageVector = Icons.Rounded.Email,
                        contentDescription = null,
                        tint = V2Cyan,
                        modifier = androidx.compose.ui.Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    KText("حسابات جوجل المتصلة", size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                    KText("${accounts.size} حساب", size = 8.6.sp, color = V2Soft, maxLines = 1)
                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { /* connect google */ }) {
                    KText("إضافة حساب", size = 9.4.sp, color = V2Cyan)
                }
            }
        }
    }
}

@Composable
private fun GoogleStorageAnalytics(analytics: List<StorageAnalytics>) {
    val totalUsed = analytics.sumOf { it.usedBytes }
    val totalTotal = analytics.sumOf { it.totalBytes }

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
                    KText("تخزين جوجل الموحد", size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                    KText("${formatBytes(totalUsed)} مستخدمة من ${formatBytes(totalTotal)}", size = 8.6.sp, color = V2Soft, maxLines = 1)
                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { /* details */ }) {
                    KText("التفاصيل", size = 9.4.sp, color = V2Cyan)
                }
            }
            Spacer(Modifier.height(8.dp))

            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                analytics.forEach { stat ->
                    GoogleStorageRow(stat = stat)
                }
            }
        }
    }
}

@Composable
private fun GoogleStorageRow(stat: StorageAnalytics) {
    val pct = if (stat.totalBytes > 0) stat.usedBytes.toFloat() / stat.totalBytes else 0f
    val color = when (stat.provider) {
        CloudProvider.Gmail -> V2Cyan
        CloudProvider.GoogleDrive -> V2Green
        CloudProvider.GooglePhotos -> V2Pink
        else -> V2Soft
    }

    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(30.dp).clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = .16f))
                .border(1.dp, color.copy(alpha = .42f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            androidx.compose.material3.Icon(
                imageVector = when (stat.provider) {
                    CloudProvider.Gmail -> Icons.Rounded.Email
                    CloudProvider.GoogleDrive -> Icons.Rounded.PlayArrow
                    CloudProvider.GooglePhotos -> Icons.Rounded.PhotoLibrary
                    else -> Icons.Rounded.Email
                },
                contentDescription = null,
                tint = color,
                modifier = androidx.compose.ui.Modifier.size(15.dp),
            )
        }
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            KText(stat.provider.toString(), size = 11.sp, weight = FontWeight.SemiBold, color = Color.White)
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
private fun GoogleActionButtons(
    onScanGmail: () -> Unit,
    onScanDrive: () -> Unit,
) {
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
                .clickable(onClick = onScanGmail),
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
                KText("فحص Gmail", size = 11.sp, weight = FontWeight.Bold, color = V2Cyan)
                KText("الرسائل القديمة والمرفقات", size = 8.4.sp, color = V2Soft, maxLines = 2)
            }
        }
        Box(
            Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(V2Green.copy(alpha = .10f))
                .border(1.dp, V2Green.copy(alpha = .42f), RoundedCornerShape(16.dp))
                .clickable(onClick = onScanDrive),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    tint = V2Green,
                    modifier = androidx.compose.ui.Modifier.size(24.dp),
                )
                KText("فحص Drive", size = 11.sp, weight = FontWeight.Bold, color = V2Green)
                KText("الملفات الكبيرة والمكررة", size = 8.4.sp, color = V2Soft, maxLines = 2)
            }
        }
    }
}

@Composable
private fun GoogleScanResults(results: List<ScanResult>) {
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
                    KText("نتائج الفحص", size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                    KText("${results.size} جلسة فحص", size = 8.6.sp, color = V2Soft, maxLines = 1)
                }
                Spacer(Modifier.weight(1f))
            }

            Spacer(Modifier.height(8.dp))

            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                results.forEach { result ->
                    GoogleScanRow(result = result)
                }
            }
        }
    }
}

@Composable
private fun GoogleScanRow(result: ScanResult) {
    val statusColor = when (result.status) {
        ScanStatus.Completed -> V2Green
        ScanStatus.Scanning -> V2Cyan
        ScanStatus.Failed -> V2Red
        ScanStatus.Cancelled -> V2Soft
    }

    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(30.dp).clip(RoundedCornerShape(8.dp))
                .background(statusColor.copy(alpha = .16f))
                .border(1.dp, statusColor.copy(alpha = .42f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Rounded.Archive,
                contentDescription = null,
                tint = statusColor,
                modifier = androidx.compose.ui.Modifier.size(15.dp),
            )
        }
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            KText("فحص ${result.provider?.toString() ?: "جوجل"}", size = 11.sp, weight = FontWeight.SemiBold, color = Color.White)
            KText("${result.itemsFound} عنصر · ${formatBytes(result.bytesFound)}", size = 9.sp, color = V2Soft, maxLines = 1)
        }
        Spacer(Modifier.weight(1f))
        KText(result.status.toString(), size = 10.sp, weight = FontWeight.SemiBold, color = statusColor, maxLines = 1)
    }
}

@Composable
private fun GoogleCandidatesList(candidates: List<CleanupCandidate>) {
    if (candidates.isEmpty()) return

    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 18.dp,
        glow = V2Pink,
        borderAlpha = .38f,
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(36.dp).clip(RoundedCornerShape(12.dp))
                        .background(V2Pink.copy(alpha = .16f))
                        .border(1.dp, V2Pink.copy(alpha = .42f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = null,
                        tint = V2Pink,
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
                    GoogleCandidateRow(candidate = candidate)
                }
            }
        }
    }
}

@Composable
private fun GoogleCandidateRow(candidate: CleanupCandidate) {
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
                KText(candidate.category.toString(), size = 9.sp, color = V2Soft, maxLines = 1)
                KText(candidate.sourceAccount ?: "", size = 8.sp, color = V2Soft, maxLines = 1)
            }
            Spacer(Modifier.weight(1f))
            Column {
                KText(formatBytes(candidate.sizeBytes), size = 10.sp, weight = FontWeight.SemiBold, color = V2GoldText, maxLines = 1)
                Spacer(Modifier.height(4.dp))
                androidx.compose.material3.Checkbox(
                    checked = candidate.isSelected,
                    onCheckedChange = { /* toggle */ },
                    colors = androidx.compose.material3.CheckboxDefaults.colors(
                        checkedColor = catColor,
                        uncheckedColor = V2Soft,
                    ),
                )
            }
        }
    }
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