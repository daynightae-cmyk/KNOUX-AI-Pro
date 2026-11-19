package com.knoux.ai.microsoft.ui

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
import androidx.compose.material.icons.rounded.History
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
import com.knoux.ai.cloud.model.ScanResult
import com.knoux.ai.cloud.model.ScanStatus
import com.knoux.ai.cloud.model.StorageAnalytics
import com.knoux.ai.cloud.model.CloudProvider
import com.knoux.ai.microsoft.auth.MicrosoftAccountManager
import com.knoux.ai.microsoft.auth.MicrosoftAuthProvider
import com.knoux.ai.microsoft.outlook.OutlookConnector
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
 * Microsoft Storage Dashboard (MS-05).
 * Unified view of Outlook + OneDrive storage with cleanup candidates.
 */
@Composable
fun MicrosoftStorageDashboardScreen(
    onBack: () -> Unit,
    onPro: () -> Unit,
) {
    val context = LocalContext.current
    val vault = com.knoux.ai.developer.vault.CredentialVault(context)
    val accountManager = MicrosoftAccountManager(context, vault)
    val authProvider = MicrosoftAuthProvider(
        context = context,
        clientId = com.knoux.ai.BuildConfig.GOOGLE_WEB_CLIENT_ID, // Placeholder — MS client ID
        redirectUri = "com.knoux.ai.pro:/oauth2redirect",
    )

    val outlookConnector = OutlookConnector(context, accountManager, authProvider)
    val coroutineScope = rememberCoroutineScope()

    var accounts by remember { mutableStateOf(listOf<com.knoux.ai.cloud.model.CloudAccount>()) }
    var analytics by remember { mutableStateOf(listOf<StorageAnalytics>()) }
    var candidates by remember { mutableStateOf(listOf<CleanupCandidate>()) }
    var scanResults by remember { mutableStateOf(listOf<ScanResult>()) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        accounts = accountManager.getConnectedAccounts()
        analytics = listOf(
            StorageAnalytics(
                provider = CloudProvider.Outlook,
                accountEmail = accounts.firstOrNull { it.provider == CloudProvider.Outlook }?.email ?: "—",
                totalBytes = 50_000_000_000L,
                usedBytes = 12_500_000_000L,
                freeBytes = 37_500_000_000L,
                emailBytes = 12_500_000_000L,
            ),
        )
    }

    Column(Modifier.fillMaxSize()) {
        V2LuxuryIdentity(
            title = "لوحة تخزين مايكروسوفت",
            english = "MICROSOFT STORAGE DASHBOARD",
            subtitle = "Outlook · OneDrive — تحليل وفحص موحد",
            accent = V2Blue,
        )

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp, end = 16.dp, bottom = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            item {
                MicrosoftAccountsStrip(accounts = accounts)
            }
            item {
                MicrosoftStorageAnalytics(analytics = analytics)
            }
            item {
                MicrosoftActionButtons(
                    onScanOutlook = { coroutineScope.launch {
                        val result = outlookConnector.scanAccount(accounts.firstOrNull { it.provider == CloudProvider.Outlook }?.id ?: "")
                        scanResults = scanResults + result
                    } },
                )
            }
            item {
                MicrosoftScanResults(results = scanResults)
            }
            item {
                MicrosoftCandidatesList(candidates = candidates)
            }
        }
    }
}

@Composable
private fun MicrosoftAccountsStrip(accounts: List<com.knoux.ai.cloud.model.CloudAccount>) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 18.dp,
        glow = V2Blue,
        borderAlpha = .38f,
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(36.dp).clip(RoundedCornerShape(12.dp))
                        .background(V2Blue.copy(alpha = .16f))
                        .border(1.dp, V2Blue.copy(alpha = .42f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Rounded.Email,
                        contentDescription = null,
                        tint = V2Blue,
                        modifier = androidx.compose.ui.Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    KText("حسابات مايكروسوفت المتصلة", size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                    KText("${accounts.size} حساب", size = 8.6.sp, color = V2Soft, maxLines = 1)
                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { /* connect microsoft */ }) {
                    KText("إضافة حساب", size = 9.4.sp, color = V2Cyan)
                }
            }
        }
    }
}

@Composable
private fun MicrosoftStorageAnalytics(analytics: List<StorageAnalytics>) {
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
                    KText("تخزين مايكروسوفت الموحد", size = 13.sp, weight = FontWeight.Bold, color = Color.White)
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
                    MicrosoftStorageRow(stat = stat)
                }
            }
        }
    }
}

@Composable
private fun MicrosoftStorageRow(stat: StorageAnalytics) {
    val pct = if (stat.totalBytes > 0) stat.usedBytes.toFloat() / stat.totalBytes else 0f
    val color = when (stat.provider) {
        CloudProvider.Outlook -> V2Blue
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
                imageVector = Icons.Rounded.Email,
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
private fun MicrosoftActionButtons(
    onScanOutlook: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(V2Blue.copy(alpha = .10f))
                .border(1.dp, V2Blue.copy(alpha = .42f), RoundedCornerShape(16.dp))
                .clickable(onClick = onScanOutlook),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Rounded.CloudQueue,
                    contentDescription = null,
                    tint = V2Blue,
                    modifier = androidx.compose.ui.Modifier.size(24.dp),
                )
                KText("فحص Outlook", size = 11.sp, weight = FontWeight.Bold, color = V2Blue)
                KText("الرسائل القديمة والمرفقات", size = 8.4.sp, color = V2Soft, maxLines = 2)
            }
        }
    }
}

@Composable
private fun MicrosoftScanResults(results: List<ScanResult>) {
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
                    MicrosoftScanRow(result = result)
                }
            }
        }
    }
}

@Composable
private fun MicrosoftScanRow(result: ScanResult) {
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
            KText("فحص ${result.provider?.toString() ?: "مايكروسوفت"}", size = 11.sp, weight = FontWeight.SemiBold, color = Color.White)
            KText("${result.itemsFound} عنصر · ${formatBytes(result.bytesFound)}", size = 9.sp, color = V2Soft, maxLines = 1)
        }
        Spacer(Modifier.weight(1f))
        KText(result.status.toString(), size = 10.sp, weight = FontWeight.SemiBold, color = statusColor, maxLines = 1)
    }
}

@Composable
private fun MicrosoftCandidatesList(candidates: List<CleanupCandidate>) {
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
                    MicrosoftCandidateRow(candidate = candidate)
                }
            }
        }
    }
}

@Composable
private fun MicrosoftCandidateRow(candidate: CleanupCandidate) {
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