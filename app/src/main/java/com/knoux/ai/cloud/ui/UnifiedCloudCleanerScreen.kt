package com.knoux.ai.cloud.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.CloudQueue
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.BuildConfig
import com.knoux.ai.cloud.model.CleanupCandidate
import com.knoux.ai.cloud.model.CleanupCategory
import com.knoux.ai.cloud.model.CleanupOperation
import com.knoux.ai.cloud.model.CloudAccount
import com.knoux.ai.cloud.model.CloudProvider
import com.knoux.ai.cloud.model.OperationStatus
import com.knoux.ai.cloud.model.ScanResult
import com.knoux.ai.cloud.model.ScanStatus
import com.knoux.ai.cloud.model.StorageAnalytics
import com.knoux.ai.developer.vault.CredentialVault
import com.knoux.ai.google.auth.GoogleAccountManager
import com.knoux.ai.google.auth.GoogleAuthProvider
import com.knoux.ai.google.drive.GoogleDriveConnector
import com.knoux.ai.google.gmail.GmailConnector
import com.knoux.ai.microsoft.auth.MicrosoftAccountManager
import com.knoux.ai.microsoft.auth.MicrosoftAuthProvider
import com.knoux.ai.microsoft.outlook.OutlookConnector
import com.knoux.ai.ui.v2.KText
import com.knoux.ai.ui.v2.V2Amber
import com.knoux.ai.ui.v2.V2Blue
import com.knoux.ai.ui.v2.V2Cyan
import com.knoux.ai.ui.v2.V2GlassCard
import com.knoux.ai.ui.v2.V2GoldText
import com.knoux.ai.ui.v2.V2Green
import com.knoux.ai.ui.v2.V2LuxuryIdentity
import com.knoux.ai.ui.v2.V2Pink
import com.knoux.ai.ui.v2.V2Red
import com.knoux.ai.ui.v2.V2Soft
import com.knoux.ai.ui.v2.V2Violet
import kotlinx.coroutines.launch
import java.util.UUID

private enum class CleanerStage { Idle, Scanning, Review, Confirm, Executing, Verifying, Done }

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
    CleanupCategory.Sent -> "المرسل"
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

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

/**
 * Unified Mail & Cloud Cleaner (EXT-02).
 *
 * Every row comes from a provider scan performed in this session. There are no
 * demo accounts, no invented storage numbers and no silent "success". The flow
 * is: SCAN -> REVIEW -> SELECT -> CONFIRM -> provider move-to-trash -> REQUEUE
 * unverified items -> VERIFY by re-querying each provider item id. Only the
 * provider's own response counts as proof; an unreachable provider is reported
 * as unknown, never as deleted.
 */
@Composable
fun UnifiedCloudCleanerScreen(
    onBack: () -> Unit,
    onPro: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val vault = remember { CredentialVault(context) }
    val googleClientId = BuildConfig.GOOGLE_ANDROID_CLIENT_ID
    val googleScheme = if (googleClientId.isNotBlank()) {
        "com.googleusercontent.apps.${googleClientId.removeSuffix(".apps.googleusercontent.com")}"
    } else {
        "knoux.oauth.unconfigured"
    }
    val googleAccounts = remember { GoogleAccountManager(context, vault, googleClientId) }
    val microsoftAccounts = remember { MicrosoftAccountManager(context, vault, BuildConfig.MICROSOFT_CLIENT_ID) }
    val googleAuth = remember {
        GoogleAuthProvider(context, googleClientId, "$googleScheme:/oauth2redirect")
    }
    val microsoftAuth = remember {
        MicrosoftAuthProvider(
            context,
            BuildConfig.MICROSOFT_CLIENT_ID,
            redirectUri = "com.knoux.ai.pro:/oauth2redirect/ms",
        )
    }
    val gmail = remember { GmailConnector(context, googleAccounts, googleAuth) }
    val drive = remember { GoogleDriveConnector(context, googleAccounts, googleAuth) }
    val outlook = remember { OutlookConnector(context, microsoftAccounts, microsoftAuth) }

    var accounts by remember { mutableStateOf(emptyList<CloudAccount>()) }
    var candidates by remember { mutableStateOf(emptyList<CleanupCandidate>()) }
    var selected by remember { mutableStateOf(emptySet<String>()) }
    var scans by remember { mutableStateOf(emptyList<ScanResult>()) }
    var operations by remember { mutableStateOf(emptyList<CleanupOperation>()) }
    var stage by remember { mutableStateOf(CleanerStage.Idle) }
    var message by remember { mutableStateOf<String?>(null) }
    var showAllHistory by remember { mutableStateOf(false) }

    fun reloadAccounts() {
        accounts = googleAccounts.getConnectedAccounts() + microsoftAccounts.getConnectedAccounts()
    }

    LaunchedEffect(Unit) { reloadAccounts() }

    fun startScan() {
        if (stage == CleanerStage.Scanning || stage == CleanerStage.Executing || stage == CleanerStage.Verifying) return
        scope.launch {
            stage = CleanerStage.Scanning
            message = null
            if (accounts.isEmpty()) {
                stage = CleanerStage.Idle
                message = "لا توجد حسابات متصلة. اربط حساب Google أو Microsoft أولًا."
                return@launch
            }
            val results = mutableListOf<ScanResult>()
            val found = mutableListOf<CleanupCandidate>()
            accounts.forEach { account ->
                when (account.provider) {
                    CloudProvider.Gmail -> {
                        listOf(
                            gmail.scanWithCandidates(account.id),
                            drive.scanWithCandidates(account.id),
                        ).forEach { outcome ->
                            results += outcome.result
                            found += outcome.candidates
                        }
                    }
                    CloudProvider.Outlook -> {
                        val outcome = outlook.scanWithCandidates(account.id)
                        results += outcome.result
                        found += outcome.candidates
                    }
                    else -> Unit
                }
            }
            scans = results
            candidates = found.distinctBy { it.id }
            selected = emptySet()
            stage = if (candidates.isEmpty()) CleanerStage.Idle else CleanerStage.Review
            val failures = results.filter { it.status == ScanStatus.Failed }
            message = when {
                failures.isNotEmpty() -> "فشل ${failures.size} فحصًا: " +
                    failures.joinToString(" · ") { it.error ?: "خطأ غير معروف" }
                candidates.isEmpty() -> "اكتمل الفحص ولم يعثر على عناصر قابلة للتنظيف."
                else -> "عُثر على ${candidates.size} عنصرًا. راجع العناصر ثم أكّد النقل إلى سلة المهملات."
            }
        }
    }

    fun executeSelected() {
        val chosen = candidates.filter { it.id in selected }
        if (chosen.isEmpty()) {
            message = "اختر عنصرًا واحدًا على الأقل."
            return
        }
        scope.launch {
            stage = CleanerStage.Executing
            message = null
            val operationId = UUID.randomUUID().toString()
            val operation = CleanupOperation(
                id = operationId,
                operationType = "نقل إلى سلة المهملات",
                itemsSelected = chosen.size,
                status = OperationStatus.Executing,
            )
            operations = listOf(operation) + operations

            var moved = 0
            val failures = mutableListOf<String>()
            chosen.groupBy { it.provider to it.accountId }.forEach { (key, items) ->
                val ids = items.map { it.providerItemId }.distinct()
                val count = when (key.first) {
                    CloudProvider.Gmail -> gmail.trashMessages(key.second, ids)
                    CloudProvider.GoogleDrive -> drive.trashFiles(key.second, ids)
                    CloudProvider.Outlook -> outlook.moveToDeletedItems(key.second, ids)
                    else -> 0
                }
                moved += count
                if (count < ids.size) {
                    failures += "${providerDisplayName(key.first)}: تعذر نقل ${ids.size - count}"
                }
            }

            stage = CleanerStage.Verifying
            val verified = mutableListOf<CleanupCandidate>()
            val unverified = mutableListOf<CleanupCandidate>()
            chosen.forEach { candidate ->
                val state = when (candidate.provider) {
                    CloudProvider.Gmail -> gmail.isTrashed(candidate.accountId, candidate.providerItemId)
                    CloudProvider.GoogleDrive -> drive.isTrashed(candidate.accountId, candidate.providerItemId)
                    CloudProvider.Outlook -> outlook.isInDeletedItems(candidate.accountId, candidate.providerItemId)
                    else -> null
                }
                if (state == true) verified += candidate else unverified += candidate
            }

            operations = operations.map { existing ->
                if (existing.id != operationId) existing else existing.copy(
                    itemsExecuted = verified.size,
                    bytesReclaimed = verified.sumOf { it.sizeBytes },
                    status = if (unverified.isEmpty() && failures.isEmpty()) {
                        OperationStatus.Completed
                    } else {
                        OperationStatus.Failed
                    },
                    completedAt = System.currentTimeMillis(),
                    error = when {
                        unverified.isNotEmpty() -> "لم يتم التحقق من ${unverified.size} عنصرًا"
                        failures.isNotEmpty() -> failures.joinToString(" · ")
                        else -> null
                    },
                )
            }

            candidates = unverified
            selected = emptySet()
            stage = if (unverified.isEmpty()) CleanerStage.Done else CleanerStage.Review
            message = when {
                unverified.isEmpty() && failures.isEmpty() ->
                    "تم التحقق من نقل ${verified.size} عنصرًا إلى سلة المهملات (${formatBytes(verified.sumOf { it.sizeBytes })})."
                else ->
                    "تم التحقق من ${verified.size} عنصرًا؛ بقي ${unverified.size} في قائمة المراجعة لإعادة المحاولة."
            }
        }
    }

    fun addGoogleAccount() {
        val activity = context.findActivity()
        if (activity == null) {
            message = "لا يمكن بدء التفويض من هذه الواجهة."
            return
        }
        if (!googleAccounts.isConfigured) {
            message = "Google OAuth غير مهيأ. أضف معرّف عميل Android عبر " +
                "-Pknoux.googleAndroidClientId=… ثم أعد البناء. لم يُرسل أي طلب وهمي."
            return
        }
        googleAuth.authorize(
            activity,
            googleAuth.gmailScopes + googleAuth.driveScopes,
        ) { result ->
            val token = result.tokenResponse
            if (!result.success || token == null) {
                message = result.error ?: "فشل تفويض Google"
                return@authorize
            }
            scope.launch {
                val info = googleAuth.fetchUserInfo(token.accessToken)
                if (info == null) {
                    message = "تم التفويض لكن تعذر قراءة هوية الحساب من Google."
                    return@launch
                }
                googleAccounts.saveAccount(
                    googleAuth.accountFromResult(info.email, info.displayName, token),
                    token,
                )
                reloadAccounts()
                message = "تم ربط ${info.email}"
            }
        }
    }

    fun addMicrosoftAccount() {
        val activity = context.findActivity()
        if (activity == null) {
            message = "لا يمكن بدء التفويض من هذه الواجهة."
            return
        }
        if (!microsoftAccounts.isConfigured) {
            message = "Microsoft OAuth غير مهيأ. أضف معرّف تطبيق Entra عبر " +
                "-Pknoux.microsoftClientId=… ثم أعد البناء. لم يُرسل أي طلب وهمي."
            return
        }
        microsoftAuth.authorize(
            activity,
            microsoftAuth.mailScopes + microsoftAuth.driveScopes,
        ) { result ->
            val token = result.tokenResponse
            if (!result.success || token == null) {
                message = result.error ?: "فشل تفويض Microsoft"
                return@authorize
            }
            scope.launch {
                val info = microsoftAuth.fetchUserInfo(token.accessToken)
                if (info == null) {
                    message = "تم التفويض لكن تعذر قراءة هوية الحساب من Microsoft."
                    return@launch
                }
                microsoftAccounts.saveAccount(
                    microsoftAuth.accountFromResult(info.email, info.displayName, token),
                    token,
                )
                reloadAccounts()
                message = "تم ربط ${info.email}"
            }
        }
    }

    val selectedItems = candidates.filter { it.id in selected }
    val selectedBytes = selectedItems.sumOf { it.sizeBytes }

    Column(Modifier.fillMaxSize()) {
        V2LuxuryIdentity(
            title = "المنظف الموحد للبريد والسحابة",
            english = "UNIFIED MAIL & CLOUD CLEANER",
            subtitle = "فحص حقيقي · مراجعة · تأكيد · تحقق من المزوّد",
            accent = V2Cyan,
        )

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            item {
                CloudAccountsStrip(
                    accounts = accounts,
                    googleConfigured = googleAccounts.isConfigured,
                    microsoftConfigured = microsoftAccounts.isConfigured,
                    onAddGoogle = ::addGoogleAccount,
                    onAddMicrosoft = ::addMicrosoftAccount,
                )
            }
            message?.let { text ->
                item { MessageCard(text) }
            }
            item { StorageAnalyticsCard(analytics = emptyList<StorageAnalytics>()) }
            item {
                CleanerControls(
                    stage = stage,
                    selectedCount = selectedItems.size,
                    selectedBytes = selectedBytes,
                    onScan = ::startScan,
                    onReview = { stage = CleanerStage.Review },
                    onConfirm = { stage = CleanerStage.Confirm },
                    onExecute = ::executeSelected,
                    onCancel = {
                        stage = if (candidates.isEmpty()) CleanerStage.Idle else CleanerStage.Review
                        message = "أُلغي التنفيذ. لم يُنقل أي عنصر."
                    },
                    onRescan = ::startScan,
                )
            }
            if (scans.isNotEmpty()) {
                item { ScanSummary(scans) }
            }
            item {
                CleanupCandidatesList(
                    candidates = candidates,
                    selected = selected,
                    onToggle = { id ->
                        selected = if (id in selected) selected - id else selected + id
                    },
                    onSelectAll = {
                        selected = if (selected.size == candidates.size) {
                            emptySet()
                        } else {
                            candidates.map { it.id }.toSet()
                        }
                    },
                )
            }
            item {
                OperationHistory(
                    operations = if (showAllHistory) operations else operations.take(5),
                    total = operations.size,
                    onToggleAll = { showAllHistory = !showAllHistory },
                )
            }
        }
    }
}

@Composable
private fun CloudAccountsStrip(
    accounts: List<CloudAccount>,
    googleConfigured: Boolean,
    microsoftConfigured: Boolean,
    onAddGoogle: () -> Unit,
    onAddMicrosoft: () -> Unit,
) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 18.dp,
        glow = V2Cyan,
        borderAlpha = .38f,
    ) {
        Column {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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
                        modifier = Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    KText("الحسابات المتصلة", size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                    KText(
                        if (accounts.isEmpty()) "لا حساب مرتبط" else "${accounts.size} حساب مرتبط",
                        size = 8.6.sp,
                        color = V2Soft,
                        maxLines = 1,
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            if (accounts.isEmpty()) {
                KText(
                    "اربط حسابًا لعرض عناصر حقيقية. لا تُعرض بيانات تجريبية.",
                    size = 9.6.sp,
                    color = V2Soft,
                    maxLines = 2,
                )
                Spacer(Modifier.height(6.dp))
            } else {
                accounts.forEach { account ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = providerIcon(account.provider),
                            contentDescription = null,
                            tint = providerColor(account.provider),
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            KText(account.email, size = 10.5.sp, color = Color.White, maxLines = 1)
                            KText(
                                providerDisplayName(account.provider),
                                size = 8.6.sp,
                                color = V2Soft,
                                maxLines = 1,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onAddGoogle) {
                    KText(
                        if (googleConfigured) "إضافة Google" else "Google غير مهيأ",
                        size = 9.4.sp,
                        color = if (googleConfigured) V2Cyan else V2Amber,
                    )
                }
                TextButton(onClick = onAddMicrosoft) {
                    KText(
                        if (microsoftConfigured) "إضافة Microsoft" else "Microsoft غير مهيأ",
                        size = 9.4.sp,
                        color = if (microsoftConfigured) V2Blue else V2Amber,
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageCard(text: String) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 11.dp,
        radius = 14.dp,
        glow = V2Amber,
        borderAlpha = .30f,
    ) {
        KText(text, size = 10.sp, color = V2GoldText, maxLines = 5)
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
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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
                        modifier = Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    KText("تخزين سحابي", size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                    KText(
                        if (analytics.isEmpty()) "لا أرقام موثوقة من المزوّد بعد"
                        else "${formatBytes(totalUsed)} مستخدمة من ${formatBytes(totalUsed + totalFree)}",
                        size = 8.6.sp,
                        color = V2Soft,
                        maxLines = 1,
                    )
                }
            }
            if (analytics.isEmpty()) {
                Spacer(Modifier.height(6.dp))
                KText(
                    "لا يعرض Gmail/Outlook حصة تخزين لهذا التطبيق. تُعرض فقط نتائج الفحص الفعلية أدناه.",
                    size = 9.sp,
                    color = V2Soft,
                    maxLines = 3,
                )
            } else {
                Spacer(Modifier.height(8.dp))
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    analytics.forEach { stat -> StorageRow(stat = stat) }
                }
            }
        }
    }
}

@Composable
private fun StorageRow(stat: StorageAnalytics) {
    val pct = if (stat.totalBytes > 0) stat.usedBytes.toFloat() / stat.totalBytes else 0f
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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
                modifier = Modifier.size(15.dp),
            )
        }
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            KText(providerDisplayName(stat.provider), size = 11.sp, weight = FontWeight.SemiBold, color = Color.White)
            KText(stat.accountEmail, size = 9.sp, color = V2Soft, maxLines = 1)
        }
        Spacer(Modifier.weight(1f))
        Column {
            KText(formatBytes(stat.usedBytes), size = 10.sp, weight = FontWeight.SemiBold, color = V2GoldText, maxLines = 1)
            KText("${(pct * 100).toInt()}%", size = 9.sp, color = V2Soft, maxLines = 1)
        }
    }
}

@Composable
private fun CleanerControls(
    stage: CleanerStage,
    selectedCount: Int,
    selectedBytes: Long,
    onScan: () -> Unit,
    onReview: () -> Unit,
    onConfirm: () -> Unit,
    onExecute: () -> Unit,
    onCancel: () -> Unit,
    onRescan: () -> Unit,
) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 12.dp,
        radius = 16.dp,
        glow = if (stage == CleanerStage.Confirm) V2Amber else V2Cyan,
        borderAlpha = .34f,
    ) {
        Column {
            when (stage) {
                CleanerStage.Idle, CleanerStage.Done -> {
                    KText(
                        if (stage == CleanerStage.Done) "اكتمل التنظيف والتحقق." else "ابدأ بفحص الحسابات المرتبطة.",
                        size = 11.sp,
                        color = Color.White,
                    )
                    Spacer(Modifier.height(8.dp))
                    PrimaryAction(if (stage == CleanerStage.Done) "فحص جديد" else "فحص شامل", V2Cyan, onScan)
                }
                CleanerStage.Scanning -> ProgressText("جارٍ فحص المزوّدين… لا تُعرض نتائج قبل وصولها.")
                CleanerStage.Executing -> ProgressText("جارٍ تنفيذ النقل إلى سلة المهملات عبر واجهة المزوّد…")
                CleanerStage.Verifying -> ProgressText("جارٍ التحقق من كل عنصر عبر إعادة استعلام المزوّد…")
                CleanerStage.Review -> {
                    KText(
                        "المحدد: $selectedCount عنصرًا · ${formatBytes(selectedBytes)}",
                        size = 11.sp,
                        color = Color.White,
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PrimaryAction("مراجعة وتأكيد", V2Amber, onConfirm)
                        TextButton(onClick = onScan) { KText("إعادة الفحص", size = 9.4.sp, color = V2Cyan) }
                    }
                }
                CleanerStage.Confirm -> {
                    KText(
                        "سيتم نقل $selectedCount عنصرًا (${formatBytes(selectedBytes)}) إلى سلة المهملات. " +
                            "هذه ليست حذفًا نهائيًا ويمكن الاستعادة من المزوّد. هل تؤكد؟",
                        size = 10.sp,
                        color = V2GoldText,
                        maxLines = 4,
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PrimaryAction("تأكيد النقل", V2Amber, onExecute)
                        TextButton(onClick = onCancel) { KText("إلغاء", size = 9.4.sp, color = V2Soft) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressText(text: String) {
    KText(text, size = 10.sp, color = V2Cyan, maxLines = 3)
}

@Composable
private fun PrimaryAction(label: String, color: Color, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = .14f))
            .border(1.dp, color.copy(alpha = .46f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center,
    ) {
        KText(label, size = 10.sp, weight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun ScanSummary(scans: List<ScanResult>) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 11.dp,
        radius = 14.dp,
        glow = V2Green,
        borderAlpha = .26f,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            KText("نتائج الفحص هذه الجلسة", size = 11.sp, weight = FontWeight.Bold, color = Color.White)
            scans.forEach { scan ->
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    KText(
                        "${scan.provider?.let { providerDisplayName(it) } ?: "مزوّد"} · ${scanStatusDisplayName(scan.status)}",
                        size = 9.4.sp,
                        color = if (scan.status == ScanStatus.Completed) V2Green else V2Red,
                        maxLines = 1,
                    )
                    Spacer(Modifier.weight(1f))
                    KText(
                        "${scan.itemsFound} عنصرًا · ${formatBytes(scan.bytesFound)}",
                        size = 9.sp,
                        color = V2Soft,
                        maxLines = 1,
                    )
                }
                scan.error?.let { error ->
                    KText(error, size = 8.6.sp, color = V2Red, maxLines = 2)
                }
            }
        }
    }
}

@Composable
private fun CleanupCandidatesList(
    candidates: List<CleanupCandidate>,
    selected: Set<String>,
    onToggle: (String) -> Unit,
    onSelectAll: () -> Unit,
) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 18.dp,
        glow = V2Violet,
        borderAlpha = .38f,
    ) {
        Column {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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
                        modifier = Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    KText("عناصر للمراجعة", size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                    KText(
                        if (candidates.isEmpty()) "لا عناصر" else "${candidates.size} عنصرًا · ${selected.size} محدد",
                        size = 8.6.sp,
                        color = V2Soft,
                        maxLines = 1,
                    )
                }
                if (candidates.isNotEmpty()) {
                    TextButton(onClick = onSelectAll) {
                        KText(
                            if (selected.size == candidates.size) "إلغاء الكل" else "تحديد الكل",
                            size = 9.4.sp,
                            color = V2Cyan,
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            if (candidates.isEmpty()) {
                KText(
                    "شغّل الفحص لعرض العناصر الفعلية من حساباتك.",
                    size = 9.4.sp,
                    color = V2Soft,
                    maxLines = 2,
                )
            } else {
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    candidates.forEach { candidate ->
                        CandidateRow(
                            candidate = candidate,
                            checked = candidate.id in selected,
                            onToggle = { onToggle(candidate.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CandidateRow(
    candidate: CleanupCandidate,
    checked: Boolean,
    onToggle: () -> Unit,
) {
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
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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
                    modifier = Modifier.size(15.dp),
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                KText(
                    candidate.itemName ?: "عنصر غير مسمّى",
                    size = 11.sp,
                    weight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 1,
                )
                KText(categoryDisplayName(candidate.category), size = 9.sp, color = V2Soft, maxLines = 1)
                KText(candidate.sourceAccount ?: "", size = 8.sp, color = V2Soft, maxLines = 1)
            }
            Spacer(Modifier.weight(1f))
            Column {
                KText(formatBytes(candidate.sizeBytes), size = 10.sp, weight = FontWeight.SemiBold, color = V2GoldText, maxLines = 1)
                Spacer(Modifier.height(4.dp))
                Checkbox(
                    checked = checked,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(checkedColor = catColor, uncheckedColor = V2Soft),
                )
            }
        }
    }
}

@Composable
private fun OperationHistory(
    operations: List<CleanupOperation>,
    total: Int,
    onToggleAll: () -> Unit,
) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 18.dp,
        glow = V2Green,
        borderAlpha = .38f,
    ) {
        Column {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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
                        modifier = Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    KText("سجل العمليات", size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                    KText("$total عملية في هذه الجلسة", size = 8.6.sp, color = V2Soft, maxLines = 1)
                }
                if (total > 5) {
                    TextButton(onClick = onToggleAll) {
                        KText("عرض الكل", size = 9.4.sp, color = V2Cyan)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            if (operations.isEmpty()) {
                KText(
                    "لا عمليات بعد. السجل يعرض فقط ما نُفّذ فعليًا.",
                    size = 9.4.sp,
                    color = V2Soft,
                    maxLines = 2,
                )
            } else {
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    operations.forEach { operation -> OperationRow(operation) }
                }
            }
        }
    }
}

@Composable
private fun OperationRow(operation: CleanupOperation) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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
                modifier = Modifier.size(15.dp),
            )
        }
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            KText(operation.operationType, size = 11.sp, weight = FontWeight.SemiBold, color = Color.White)
            KText(
                "${operation.itemsExecuted} عنصر · ${formatBytes(operation.bytesReclaimed)} تم التحقق منها",
                size = 9.sp,
                color = V2Soft,
                maxLines = 1,
            )
            operation.error?.let { error ->
                KText(error, size = 8.4.sp, color = V2Red, maxLines = 2)
            }
        }
        Spacer(Modifier.weight(1f))
        KText(
            operationStatusDisplayName(operation.status),
            size = 10.sp,
            weight = FontWeight.SemiBold,
            color = operationStatusColor(operation.status),
            maxLines = 1,
        )
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
