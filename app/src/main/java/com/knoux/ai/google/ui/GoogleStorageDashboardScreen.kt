package com.knoux.ai.google.ui

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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.CloudQueue
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.AlertDialog
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
import com.knoux.ai.cloud.model.CloudAccount
import com.knoux.ai.cloud.model.CloudProvider
import com.knoux.ai.cloud.model.ScanResult
import com.knoux.ai.cloud.model.ScanStatus
import com.knoux.ai.developer.vault.CredentialVault
import com.knoux.ai.google.auth.GoogleAccountManager
import com.knoux.ai.google.auth.GoogleAuthProvider
import com.knoux.ai.google.drive.GoogleDriveConnector
import com.knoux.ai.google.gmail.GmailConnector
import com.knoux.ai.ui.v2.KText
import com.knoux.ai.ui.v2.V2Amber
import com.knoux.ai.ui.v2.V2Blue
import com.knoux.ai.ui.v2.V2Cyan
import com.knoux.ai.ui.v2.V2GlassCard
import com.knoux.ai.ui.v2.V2GoldText
import com.knoux.ai.ui.v2.V2Green
import com.knoux.ai.ui.v2.V2LuxuryIdentity
import com.knoux.ai.ui.v2.V2TopBar
import com.knoux.ai.ui.v2.V2TopMode
import com.knoux.ai.ui.v2.V2Pink
import com.knoux.ai.ui.v2.V2Red
import com.knoux.ai.ui.v2.V2Soft
import com.knoux.ai.ui.v2.V2Violet
import kotlinx.coroutines.launch

private tailrec fun Context.findHostActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findHostActivity()
    else -> null
}

private fun providerLabel(provider: CloudProvider?): String = when (provider) {
    CloudProvider.Gmail -> "Gmail"
    CloudProvider.GoogleDrive -> "Google Drive"
    CloudProvider.GooglePhotos -> "Google Photos"
    CloudProvider.Outlook -> "Outlook"
    CloudProvider.OneDrive -> "OneDrive"
    CloudProvider.iCloudMail -> "iCloud Mail"
    null -> "جوجل"
}

/**
 * Google Storage Dashboard (GOOGLE-05).
 *
 * Every account comes from the encrypted account manager, every candidate comes
 * from a live Gmail/Drive scan, and deletion is a real provider trash operation
 * followed by a re-query of each item id. Storage quota is not invented: Gmail
 * exposes no per-app quota and the Drive quota endpoint is not queried here, so
 * the card says so instead of showing fake numbers.
 */
@Composable
fun GoogleStorageDashboardScreen(
    onBack: () -> Unit,
    onPro: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val vault = remember { CredentialVault(context) }
    val googleClientId = BuildConfig.GOOGLE_ANDROID_CLIENT_ID
    val redirectScheme = if (googleClientId.isNotBlank()) {
        "com.googleusercontent.apps.${googleClientId.removeSuffix(".apps.googleusercontent.com")}"
    } else {
        "knoux.oauth.unconfigured"
    }
    val accountManager = remember { GoogleAccountManager(context, vault, googleClientId) }
    val authProvider = remember {
        GoogleAuthProvider(
            context = context,
            clientId = googleClientId,
            redirectUri = "$redirectScheme:/oauth2redirect",
        )
    }
    val gmailConnector = remember { GmailConnector(context, accountManager, authProvider) }
    val driveConnector = remember { GoogleDriveConnector(context, accountManager, authProvider) }

    var accounts by remember { mutableStateOf(listOf<CloudAccount>()) }
    var candidates by remember { mutableStateOf(listOf<CleanupCandidate>()) }
    var selected by remember { mutableStateOf(emptySet<String>()) }
    var scanResults by remember { mutableStateOf(listOf<ScanResult>()) }
    var scanning by remember { mutableStateOf(false) }
    var executing by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var confirmOpen by remember { mutableStateOf(false) }

    fun reload() {
        accounts = accountManager.getConnectedAccounts()
    }

    LaunchedEffect(Unit) { reload() }

    fun scan(target: CloudProvider?) {
        if (scanning || executing) return
        val targets = when (target) {
            CloudProvider.Gmail -> accounts.filter { it.provider == CloudProvider.Gmail }
            CloudProvider.GoogleDrive -> accounts.filter { it.provider == CloudProvider.GoogleDrive }
            else -> accounts
        }
        if (targets.isEmpty()) {
            message = "لا يوجد حساب Google متصل. اربط حسابًا أولًا."
            return
        }
        scope.launch {
            scanning = true
            message = null
            val results = mutableListOf<ScanResult>()
            val found = mutableListOf<CleanupCandidate>()
            targets.forEach { account ->
                if (target == null || target == CloudProvider.Gmail) {
                    val outcome = gmailConnector.scanWithCandidates(account.id)
                    results += outcome.result
                    found += outcome.candidates
                }
                if (target == null || target == CloudProvider.GoogleDrive) {
                    val outcome = driveConnector.scanWithCandidates(account.id)
                    results += outcome.result
                    found += outcome.candidates
                }
            }
            scanResults = results
            candidates = found.distinctBy { it.id }
            selected = emptySet()
            scanning = false
            val failures = results.filter { it.status == ScanStatus.Failed }
            message = when {
                failures.isNotEmpty() -> "فشل ${failures.size} فحصًا: " +
                    failures.joinToString(" · ") { it.error ?: "خطأ غير معروف" }
                candidates.isEmpty() -> "اكتمل الفحص ولم يعثر على عناصر قابلة للتنظيف."
                else -> "عُثر على ${candidates.size} عنصرًا. راجعها ثم أكّد النقل إلى سلة المهملات."
            }
        }
    }

    fun execute() {
        val chosen = candidates.filter { it.id in selected }
        if (chosen.isEmpty()) {
            message = "اختر عنصرًا واحدًا على الأقل."
            return
        }
        scope.launch {
            executing = true
            confirmOpen = false
            message = null
            val failures = mutableListOf<String>()
            chosen.groupBy { it.provider }.forEach { (provider, items) ->
                val ids = items.map { it.providerItemId }.distinct()
                val accountId = items.first().accountId
                val moved = when (provider) {
                    CloudProvider.Gmail -> gmailConnector.trashMessages(accountId, ids)
                    CloudProvider.GoogleDrive -> driveConnector.trashFiles(accountId, ids)
                    else -> 0
                }
                if (moved < ids.size) {
                    failures += "${providerLabel(provider)}: تعذر نقل ${ids.size - moved}"
                }
            }
            val verified = mutableListOf<CleanupCandidate>()
            val unverified = mutableListOf<CleanupCandidate>()
            chosen.forEach { candidate ->
                val state = when (candidate.provider) {
                    CloudProvider.Gmail ->
                        gmailConnector.isTrashed(candidate.accountId, candidate.providerItemId)
                    CloudProvider.GoogleDrive ->
                        driveConnector.isTrashed(candidate.accountId, candidate.providerItemId)
                    else -> null
                }
                if (state == true) verified += candidate else unverified += candidate
            }
            candidates = unverified
            selected = emptySet()
            executing = false
            message = when {
                unverified.isEmpty() && failures.isEmpty() ->
                    "تم التحقق من نقل ${verified.size} عنصرًا إلى سلة المهملات (${formatBytes(verified.sumOf { it.sizeBytes })})."
                else -> "تم التحقق من ${verified.size} عنصرًا؛ بقي ${unverified.size} لإعادة المحاولة. " +
                    failures.joinToString(" · ")
            }
        }
    }

    fun addAccount() {
        val activity = context.findHostActivity()
        if (activity == null) {
            message = "لا يمكن بدء التفويض من هذه الواجهة."
            return
        }
        if (!accountManager.isConfigured) {
            message = "Google OAuth غير مهيأ. أضف معرّف عميل Android عبر " +
                "-Pknoux.googleAndroidClientId=… ثم أعد البناء."
            return
        }
        authProvider.authorize(activity, authProvider.gmailScopes + authProvider.driveScopes) { result ->
            val token = result.tokenResponse
            if (!result.success || token == null) {
                message = result.error ?: "فشل تفويض Google"
                return@authorize
            }
            scope.launch {
                val info = authProvider.fetchUserInfo(token.accessToken)
                if (info == null) {
                    message = "تم التفويض لكن تعذر قراءة هوية الحساب من Google."
                    return@launch
                }
                accountManager.saveAccount(
                    authProvider.accountFromResult(info.email, info.displayName, token),
                    token,
                )
                reload()
                message = "تم ربط ${info.email}"
            }
        }
    }

    if (confirmOpen) {
        val chosen = candidates.filter { it.id in selected }
        AlertDialog(
            onDismissRequest = { confirmOpen = false },
            title = { KText("تأكيد النقل إلى سلة المهملات", size = 14.sp, weight = FontWeight.Bold, color = Color.White) },
            text = {
                KText(
                    "سيتم نقل ${chosen.size} عنصرًا (${formatBytes(chosen.sumOf { it.sizeBytes })}) " +
                        "إلى سلة مهملات Google. هذه ليست حذفًا نهائيًا ويمكن الاستعادة من المزوّد. " +
                        "سيُتحقق من كل عنصر بإعادة استعلام Google.",
                    size = 10.sp,
                    color = V2GoldText,
                    maxLines = 5,
                )
            },
            confirmButton = {
                TextButton(onClick = ::execute) { KText("تأكيد", size = 10.sp, color = V2Amber) }
            },
            dismissButton = {
                TextButton(onClick = { confirmOpen = false }) { KText("إلغاء", size = 10.sp, color = V2Soft) }
            },
        )
    }

    Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)) {
        V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = onPro)
        V2LuxuryIdentity(
            title = "لوحة تخزين جوجل",
            english = "GOOGLE STORAGE DASHBOARD",
            subtitle = "Gmail · Google Drive — فحص حقيقي ومراجعة وتحقق",
            accent = V2Cyan,
        )

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            item {
                GoogleAccountsStrip(
                    accounts = accounts,
                    configured = accountManager.isConfigured,
                    onAdd = ::addAccount,
                )
            }
            message?.let { text ->
                item {
                    V2GlassCard(
                        Modifier.fillMaxWidth(),
                        padding = 11.dp,
                        radius = 14.dp,
                        glow = V2Amber,
                        borderAlpha = .30f,
                    ) {
                        KText(text, size = 9.6.sp, color = V2GoldText, maxLines = 5)
                    }
                }
            }
            item { GoogleQuotaNote() }
            item {
                GoogleActionButtons(
                    scanning = scanning || executing,
                    onScanGmail = { scan(CloudProvider.Gmail) },
                    onScanDrive = { scan(CloudProvider.GoogleDrive) },
                    onScanAll = { scan(null) },
                )
            }
            if (scanResults.isNotEmpty()) {
                item { GoogleScanResults(results = scanResults) }
            }
            item {
                GoogleCandidatesList(
                    candidates = candidates,
                    selected = selected,
                    busy = scanning || executing,
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
                    onConfirm = {
                        if (selected.isEmpty()) {
                            message = "اختر عنصرًا واحدًا على الأقل."
                        } else {
                            confirmOpen = true
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun GoogleAccountsStrip(
    accounts: List<CloudAccount>,
    configured: Boolean,
    onAdd: () -> Unit,
) {
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
                        modifier = Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    KText("حسابات جوجل المتصلة", size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                    KText(
                        if (accounts.isEmpty()) "لا حساب مرتبط" else "${accounts.size} حساب مرتبط",
                        size = 9.sp,
                        color = V2Soft,
                        maxLines = 1,
                    )
                }
                TextButton(onClick = onAdd) {
                    KText(
                        if (configured) "إضافة حساب" else "OAuth غير مهيأ",
                        size = 9.4.sp,
                        color = if (configured) V2Cyan else V2Amber,
                    )
                }
            }
            if (accounts.isEmpty()) {
                Spacer(Modifier.height(6.dp))
                KText(
                    "اربط حساب Google لعرض رسائل وملفات حقيقية. لا تُعرض بيانات تجريبية.",
                    size = 9.2.sp,
                    color = V2Soft,
                    maxLines = 2,
                )
            } else {
                Spacer(Modifier.height(6.dp))
                accounts.forEach { account ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Rounded.Email,
                            contentDescription = null,
                            tint = V2Cyan,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        KText(account.email, size = 10.2.sp, color = Color.White, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
private fun GoogleQuotaNote() {
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
                        modifier = Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    KText("تخزين جوجل", size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                    KText("لا أرقام حصة من المزوّد", size = 9.sp, color = V2Soft, maxLines = 1)
                }
            }
            Spacer(Modifier.height(6.dp))
            KText(
                "Gmail لا يكشف حصة تخزين لتطبيق الطرف الثالث، ولم يُستعلم endpoint حصة Drive في هذه النسخة. " +
                    "لذلك لا تُعرض أرقام تقديرية؛ النتائج أدناه هي عناصر الفحص الفعلية فقط.",
                size = 9.sp,
                color = V2Soft,
                maxLines = 4,
            )
        }
    }
}

@Composable
private fun GoogleActionButtons(
    scanning: Boolean,
    onScanGmail: () -> Unit,
    onScanDrive: () -> Unit,
    onScanAll: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ScanTile(
                modifier = Modifier.weight(1f),
                color = V2Cyan,
                icon = Icons.Rounded.CloudQueue,
                title = "فحص Gmail",
                subtitle = "الرسائل القديمة والمرفقات",
                enabled = !scanning,
                onClick = onScanGmail,
            )
            ScanTile(
                modifier = Modifier.weight(1f),
                color = V2Green,
                icon = Icons.Rounded.PlayArrow,
                title = "فحص Drive",
                subtitle = "الملفات الكبيرة والمكررة",
                enabled = !scanning,
                onClick = onScanDrive,
            )
        }
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(V2Violet.copy(alpha = .10f))
                .border(1.dp, V2Violet.copy(alpha = .42f), RoundedCornerShape(14.dp))
                .clickable(enabled = !scanning, onClick = onScanAll)
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            KText(
                if (scanning) "جارٍ الفحص…" else "فحص شامل (Gmail + Drive)",
                size = 10.sp,
                weight = FontWeight.Bold,
                color = V2Violet,
            )
        }
    }
}

@Composable
private fun ScanTile(
    modifier: Modifier,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = .10f))
            .border(1.dp, color.copy(alpha = .42f), RoundedCornerShape(16.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp),
            )
            KText(title, size = 11.sp, weight = FontWeight.Bold, color = color)
            KText(subtitle, size = 9.sp, color = V2Soft, maxLines = 2)
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
                        modifier = Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    KText("نتائج الفحص", size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                    KText("${results.size} جلسة فحص", size = 9.sp, color = V2Soft, maxLines = 1)
                }
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

    Column(Modifier.fillMaxWidth()) {
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
                    modifier = Modifier.size(15.dp),
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                KText("فحص ${providerLabel(result.provider)}", size = 11.sp, weight = FontWeight.SemiBold, color = Color.White)
                KText("${result.itemsFound} عنصر · ${formatBytes(result.bytesFound)}", size = 9.sp, color = V2Soft, maxLines = 1)
            }
            Spacer(Modifier.weight(1f))
            KText(
                when (result.status) {
                    ScanStatus.Completed -> "مكتمل"
                    ScanStatus.Scanning -> "جارٍ…"
                    ScanStatus.Failed -> "فشل"
                    ScanStatus.Cancelled -> "ملغي"
                },
                size = 10.sp,
                weight = FontWeight.SemiBold,
                color = statusColor,
                maxLines = 1,
            )
        }
        result.error?.let { error ->
            KText(error, size = 9.sp, color = V2Red, maxLines = 2)
        }
    }
}

@Composable
private fun GoogleCandidatesList(
    candidates: List<CleanupCandidate>,
    selected: Set<String>,
    busy: Boolean,
    onToggle: (String) -> Unit,
    onSelectAll: () -> Unit,
    onConfirm: () -> Unit,
) {
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
                        modifier = Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    KText("عناصر للمراجعة", size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                    KText(
                        if (candidates.isEmpty()) "لا عناصر" else "${candidates.size} عنصر · ${selected.size} محدد",
                        size = 9.sp,
                        color = V2Soft,
                        maxLines = 1,
                    )
                }
                if (candidates.isNotEmpty()) {
                    TextButton(onClick = onSelectAll, enabled = !busy) {
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
                    "شغّل فحصًا لعرض العناصر الفعلية من حسابك.",
                    size = 9.4.sp,
                    color = V2Soft,
                    maxLines = 2,
                )
            } else {
                Column(
                    Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    candidates.forEach { candidate ->
                        GoogleCandidateRow(
                            candidate = candidate,
                            checked = candidate.id in selected,
                            enabled = !busy,
                            onToggle = { onToggle(candidate.id) },
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(V2Amber.copy(alpha = .12f))
                        .border(1.dp, V2Amber.copy(alpha = .46f), RoundedCornerShape(12.dp))
                        .clickable(enabled = !busy && selected.isNotEmpty(), onClick = onConfirm)
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    KText(
                        if (busy) "جارٍ التنفيذ…"
                        else "نقل ${selected.size} عنصرًا إلى سلة المهملات (${formatBytes(candidates.filter { it.id in selected }.sumOf { it.sizeBytes })})",
                        size = 9.8.sp,
                        weight = FontWeight.Bold,
                        color = V2Amber,
                        maxLines = 2,
                    )
                }
            }
        }
    }
}

@Composable
private fun GoogleCandidateRow(
    candidate: CleanupCandidate,
    checked: Boolean,
    enabled: Boolean,
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
                    modifier = Modifier.size(15.dp),
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                KText(candidate.itemName ?: "عنصر غير مسمّى", size = 11.sp, weight = FontWeight.SemiBold, color = Color.White, maxLines = 1)
                KText(categoryLabel(candidate.category), size = 9.sp, color = V2Soft, maxLines = 1)
                KText(candidate.sourceAccount ?: "", size = 9.sp, color = V2Soft, maxLines = 1)
            }
            Spacer(Modifier.weight(1f))
            Column {
                KText(formatBytes(candidate.sizeBytes), size = 10.sp, weight = FontWeight.SemiBold, color = V2GoldText, maxLines = 1)
                Spacer(Modifier.height(4.dp))
                Checkbox(
                    checked = checked,
                    onCheckedChange = { onToggle() },
                    enabled = enabled,
                    colors = CheckboxDefaults.colors(checkedColor = catColor, uncheckedColor = V2Soft),
                )
            }
        }
    }
}

private fun categoryLabel(category: CleanupCategory): String = when (category) {
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

private fun formatBytes(bytes: Long): String {
    if (bytes < 1024) return "$bytes بايت"
    val kb = bytes / 1024
    if (kb < 1024) return "$kb كيلوبايت"
    val mb = kb / 1024
    if (mb < 1024) return "$mb ميجابايت"
    val gb = mb / 1024
    return "$gb جيجابايت"
}
