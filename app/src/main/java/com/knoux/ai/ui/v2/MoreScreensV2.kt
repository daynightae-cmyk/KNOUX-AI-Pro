package com.knoux.ai.ui.v2

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.knoux.ai.database.AppDatabase
import com.knoux.ai.database.KnouxRepository
import com.knoux.ai.database.OperationItemOutcomeEntity
import com.knoux.ai.ui.v2.markKnouxOnboardingSeen
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.Article
import androidx.compose.material.icons.rounded.Audiotrack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.BatteryFull
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Report
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.R
import com.knoux.ai.ui.v2.V2LuxuryIdentity
import com.knoux.ai.ui.v2.V2LuxuryStage
import com.knoux.ai.ui.v2.V2LuxuryServiceGrid

/* ------------------------------------------------------------------ *
 * Shared interior scaffold — replaced by V2LuxuryStage
 * ------------------------------------------------------------------ */

@Composable
private fun V2PageHeading(
    title: String,
    subtitle: String,
    accent: Color,
) {
    V2LuxuryIdentity(
        title = title,
        english = title.uppercase().replaceFirstChar { it.lowercase() },
        subtitle = subtitle,
        accent = accent,
    )
}

/** Legacy wrapper - uses InteriorScaffold for backward compatibility with existing screens. */
@Composable
private fun InteriorScaffold(
    onBack: () -> Unit,
    onPro: () -> Unit,
    bottomBar: (@Composable () -> Unit)? = null,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit,
) {
    Box(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(Modifier.fillMaxSize()) {
            V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = onPro)
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                content = content,
            )
            if (bottomBar != null) {
                Box(Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                    bottomBar()
                }
            }
        }
    }
}

/** Legacy wrapper - uses PageHeading for backward compatibility. */
@Composable
private fun PageHeading(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            KText(
                title,
                size = 20.sp,
                weight = FontWeight.Black,
                color = Color.White,
            )
            KText(subtitle, size = 10.sp, color = V2Soft)
        }
        if (trailing != null) {
            trailing()
        } else {
            val day = LocalDayMode.current
            val tint = if (day) dayAccent(accent) else accent
            Box(
                Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(tint.withAlpha( if (day) .16f else .34f), (if (day) DayViolet else V2Violet).withAlpha( if (day) .12f else .22f))))
                    .border(1.dp, tint.withAlpha( if (day) .45f else .52f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = if (day) tint else Color.White, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
private fun InfoLine(label: String, value: String, accent: Color = V2Soft, onClick: (() -> Unit)? = null) {
    val modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp).then(
        if (onClick != null) Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp)
            .clickable(onClick = onClick)
            .background(Color.White.withAlpha( .02f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp)
        else Modifier.fillMaxWidth().padding(vertical = 7.dp)
    )
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        KText(label, size = 9.5.sp, color = V2Soft)
        Spacer(Modifier.weight(1f))
        KText(value, size = 9.5.sp, weight = FontWeight.SemiBold, color = accent)
        if (onClick != null) {
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Rounded.Build, null, tint = V2Amber.withAlpha( .6f), modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
private fun ScopedNotice(text: String, accent: Color = V2Cyan) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 18.dp, glow = accent, borderAlpha = .28f) {
        KText(text, size = 12.sp, lineHeight = 18.sp, color = V2Soft)
    }
}

private data class ScopedDocument(
    val uri: Uri,
    val name: String,
    val bytes: Long?,
    val mime: String,
    val isDirectory: Boolean = false,
    val lastModified: Long? = null,
)

private fun readSelectedDocument(context: Context, uri: Uri): ScopedDocument {
    val cursor = context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null)
        ?: error("موفر الملف لم يُعد بيانات")
    return cursor.use {
        check(it.moveToFirst()) { "الملف المختار لم يعد متاحاً" }
        val nameColumn = it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
        val sizeColumn = it.getColumnIndexOrThrow(OpenableColumns.SIZE)
        ScopedDocument(uri, it.getString(nameColumn) ?: uri.lastPathSegment.orEmpty(),
            if (it.isNull(sizeColumn)) null else it.getLong(sizeColumn).takeIf { bytes -> bytes >= 0L },
            context.contentResolver.getType(uri) ?: "نوع غير معلن")
    }
}

private fun readScopedFolder(context: Context, tree: Uri, documentId: String): List<ScopedDocument> {
    val children = DocumentsContract.buildChildDocumentsUriUsingTree(tree, documentId)
    val projection = arrayOf(DocumentsContract.Document.COLUMN_DOCUMENT_ID, DocumentsContract.Document.COLUMN_DISPLAY_NAME,
        DocumentsContract.Document.COLUMN_SIZE, DocumentsContract.Document.COLUMN_MIME_TYPE,
        DocumentsContract.Document.COLUMN_LAST_MODIFIED)
    val cursor = context.contentResolver.query(children, projection, null, null, null)
        ?: error("موفر المجلد لم يُعد بيانات")
    return cursor.use {
        val result = mutableListOf<ScopedDocument>()
        while (it.moveToNext() && result.size < 500) {
            val mime = it.getString(3) ?: "نوع غير معلن"
            result += ScopedDocument(DocumentsContract.buildDocumentUriUsingTree(tree, it.getString(0)),
                it.getString(1) ?: "اسم غير معلن", if (it.isNull(2)) null else it.getLong(2).takeIf { bytes -> bytes >= 0L },
                mime, mime == DocumentsContract.Document.MIME_TYPE_DIR,
                if (it.isNull(4)) null else it.getLong(4).takeIf { millis -> millis > 0L })
        }
        result.sortedWith(compareByDescending<ScopedDocument> { entry -> entry.isDirectory }.thenBy { entry -> entry.name })
    }
}

@Composable
private fun ScopedDocumentRow(name: String, size: String, subtitle: String, directory: Boolean, onOpen: () -> Unit) {
    V2GlassCard(Modifier.fillMaxWidth().heightIn(min = 48.dp).clickable(onClick = onOpen),
        padding = 12.dp, radius = 16.dp, glow = if (directory) V2Violet else V2Cyan) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(if (directory) Icons.Rounded.Folder else Icons.Rounded.OpenInNew, null, tint = V2Cyan, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                KText(name, size = 14.sp, weight = FontWeight.Bold)
                KText(subtitle, size = 12.sp, color = V2Soft)
                if (!directory) KText(size, size = 12.sp, color = V2Cyan)
            }
        }
    }
}

@Composable
private fun HairLine() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(if (LocalDayMode.current) DayCardSeam.withAlpha(.55f) else Color.White.withAlpha( .08f))
    )
}

@Composable
internal fun PreviewNote(text: String) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(Icons.Rounded.Info, null, tint = V2Muted, modifier = Modifier.size(13.dp))
        Spacer(Modifier.width(4.dp))
        KText(text, size = 9.sp, lineHeight = 11.5.sp, color = V2Muted, align = TextAlign.Center)
    }
}

@Composable
internal fun V2Toggle(checked: Boolean, onChange: (Boolean) -> Unit, accent: Color = V2Cyan) {
    val day = LocalDayMode.current
    Box(
        Modifier
            .size(width = 42.dp, height = 24.dp)
            .clip(RoundedCornerShape(50))
            .background(
                if (checked) Brush.horizontalGradient(listOf((if (day) dayAccent(accent) else accent).withAlpha( .90f), if (day) DayViolet else V2Violet))
                else if (day) Brush.horizontalGradient(listOf(Color(0xFFE6DEF5), Color(0xFFDDD2F0)))
                else Brush.horizontalGradient(listOf(Color.White.withAlpha( .10f), Color.White.withAlpha( .06f)))
            )
            .border(1.dp, if (checked) Color.White.withAlpha( .28f) else if (day) DayBorderSoft else Color.White.withAlpha( .18f), RoundedCornerShape(50))
            .clickable { onChange(!checked) }
            .padding(3.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Box(
            Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(Color.White)
                .then(if (day && !checked) Modifier.border(1.dp, DayBorderSoft, CircleShape) else Modifier)
        )
    }
}

@Composable
internal fun SegmentedBar(segments: List<Pair<Float, Color>>, height: Int = 9) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape(50)),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        segments.forEach { (fraction, color) ->
            Box(
                Modifier
                    .weight(fraction.coerceAtLeast(.01f))
                    .fillMaxSize()
                    .background(color)
            )
        }
    }
}

/* ------------------------------------------------------------------ *
 * App manager
 * ------------------------------------------------------------------ */

private data class ManagedAppV2(
    val name: String,
    val packageName: String,
    val mark: AppMark,
    val size: String,
    val data: String,
    val cache: String,
    val lastUsed: String,
    val permissions: Int,
    val accent: Color,
)

private val managedAppsV2 = listOf(
    ManagedAppV2("TikTok", "com.zhiliaoapp.musically", AppMark.TikTok, "842 MB", "486 MB", "128 MB", "منذ 12 دقيقة", 12, V2Cyan),
    ManagedAppV2("WhatsApp", "com.whatsapp", AppMark.WhatsApp, "412 MB", "1.8 GB", "64 MB", "منذ 3 ساعات", 9, V2Green),
    ManagedAppV2("YouTube", "com.google.android.youtube", AppMark.YouTube, "356 MB", "920 MB", "212 MB", "منذ ساعة", 8, V2Pink),
    ManagedAppV2("Telegram", "org.telegram.messenger", AppMark.Telegram, "228 MB", "2.4 GB", "310 MB", "منذ 5 دقائق", 7, V2Blue),
    ManagedAppV2("الصور", "com.google.android.apps.photos", AppMark.Photos, "186 MB", "1.2 GB", "96 MB", "منذ يوم", 6, V2Violet),
)

private val appFilters = listOf("الكل (54)", "ثقيلة (8)", "نادرًا (12)", "حديثة (6)")

@Composable
fun V2AppManagerScreen(
    onBack: () -> Unit,
    onOpenDetail: () -> Unit,
    onPro: () -> Unit,
) {
    val context = LocalContext.current
    val (refresh, bump) = rememberRefreshCounter()
    val facts = rememberDeviceFacts(refresh)
    var filter by remember { mutableStateOf(1) }
    // The orb drives the read; the flag keeps its ring animating until the
    // PackageManager pass publishes fresh facts.
    var refreshing by remember { mutableStateOf(false) }
    LaunchedEffect(facts.computedAt) { if (refreshing) refreshing = false }

    val apps = remember(facts, filter) {
        val base = when (filter) {
            1 -> facts.installedApps.filter { !it.isSystem }
            2 -> facts.installedApps.filter { it.isSystem }
            else -> facts.installedApps
        }
        base.sortedWith(
            compareByDescending<InstalledAppFacts> { it.appBytes ?: 0L }
                .thenBy { it.label.lowercase() }
        )
    }

    val database = remember(context) { AppDatabase.getDatabase(context.applicationContext) }
    val repository = remember(database) { KnouxRepository(database) }
    val scope = rememberCoroutineScope()
    var appToUninstall by remember { mutableStateOf<String?>(null) }
    var uninstallMessage by remember { mutableStateOf<String?>(null) }

    val uninstallLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val pkg = appToUninstall ?: return@rememberLauncherForActivityResult
        val code = result.resultCode
        appToUninstall = null
        scope.launch {
            try {
                val opId = withContext(Dispatchers.IO) {
                    repository.createOperationRequest(
                        scanSessionId = "",
                        serviceId = "app_manager",
                        requestedAction = "uninstall_app",
                        selectionCount = 1,
                        keeperIds = null,
                        exclusionIds = null,
                        predictedEligibleBytes = null,
                    )
                }
                val accepted = code == android.app.Activity.RESULT_OK
                val presence = withContext(Dispatchers.IO) { queryPackagePresence(context, pkg) }
                val status = uninstallStatus(accepted, presence.exists)
                withContext(Dispatchers.IO) {
                    repository.recordConsent(opId, granted = accepted)
                    repository.recordItemOutcomes(opId, listOf(
                        OperationItemOutcomeEntity(
                            operationId = opId,
                            itemId = pkg,
                            result = if (status == "VERIFIED") "SUCCESS" else status,
                            // Unknown cannot prove absence; providerError retains that distinction.
                            postQueryExists = presence.exists != false,
                            measuredBytes = null,
                            providerError = presence.error,
                            completedAt = System.currentTimeMillis(),
                        )
                    ), finalStatus = status)
                }
                uninstallMessage = when (status) {
                    "VERIFIED" -> "تمت إزالة الحزمة والتحقق من غيابها: $pkg"
                    "CANCELLED" -> "أُلغي طلب الإزالة دون تأكيد نجاح"
                    "ERROR" -> "تعذّر التحقق من الحزمة: ${presence.error}"
                    else -> "لا تزال الحزمة مثبتة؛ لم يتم تأكيد الإزالة"
                }
                bump()
            } catch (error: Exception) {
                uninstallMessage = "تعذّر تسجيل نتيجة الإزالة: ${error.message}"
            }
        }
    }

    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
        bottomBar = {
            V2GradientButton(
                "فتح إعدادات التطبيقات",
                { openAllAppSettings(context) },
                icon = Icons.Rounded.Apps,
                trailingChevron = true,
            )
        },
    ) {
        item {
            ServiceBanner(
                spec = ServiceSpecs.appManager,
                status = if (facts.computedAt > 0L) "PackageManager" else "جاري القراءة",
                statusColor = if (facts.computedAt > 0L) V2Green else V2Amber,
                metric = "${formatCount(facts.installedCount)} تطبيق",
            )
        }
        item {
            // The universal K orb: the only way this service starts.
            KnouxRunOrbBlock(
                progress = when {
                    refreshing -> null
                    facts.computedAt > 0L -> 1f
                    else -> 0f
                },
                running = refreshing,
                enabled = !refreshing,
                label = when {
                    refreshing -> "جارٍ قراءة التطبيقات…"
                    facts.computedAt > 0L -> "اضغط لإعادة قراءة التطبيقات"
                    else -> "اضغط لتشغيل الخدمة"
                },
                accent = V2Pink,
                accent2 = V2Violet,
                onTap = {
                    if (!refreshing) {
                        refreshing = true
                        bump()
                    }
                },
            )
        }
        item { AppManagerRealSummary(facts) }
        uninstallMessage?.let { message -> item { ScopedNotice(message, V2Amber) } }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                val labels = listOf(
                    "الكل (${formatCount(facts.installedCount)})",
                    "مستخدم (${formatCount(facts.userAppCount)})",
                    "نظام (${formatCount(facts.installedCount - facts.userAppCount)})",
                )
                labels.forEachIndexed { index, label ->
                    AppFilterChip(
                        text = label,
                        active = filter == index,
                        modifier = Modifier.weight(1f),
                        onClick = { filter = index },
                    )
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                KText("التطبيقات الظاهرة للتطبيق", size = 12.5.sp, weight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                KText("مرتبة حسب الحجم", size = 9.sp, color = V2Muted)
            }
        }
        if (apps.isEmpty()) {
            item {
                ServiceCapabilityCard(
                    title = if (facts.computedAt == 0L) "جاري قراءة الحزم" else "لا تطبيقات في هذا التصنيف",
                    body = "تُقرأ القائمة من PackageManager على هذا الهاتف فقط، بدون أي بيانات تجريبية.",
                    state = ServiceState.LIVE,
                    actionLabel = "تحديث",
                    onAction = bump,
                )
            }
        }
        items(apps.size) { index ->
            val app = apps[index]
            RealInstalledAppRow(
                app = app,
                onClick = { openAppInfo(context, app.packageName) },
                onUninstall = {
                    if (!app.isSystem) {
                        if (appToUninstall == null) {
                            appToUninstall = app.packageName
                            try {
                                uninstallLauncher.launch(
                                    Intent(Intent.ACTION_DELETE)
                                        .setData(Uri.parse("package:${app.packageName}"))
                                        .putExtra(Intent.EXTRA_RETURN_RESULT, true)
                                )
                            } catch (error: Exception) {
                                appToUninstall = null
                                uninstallMessage = "تعذّر فتح إزالة التطبيق: ${error.message}"
                            }
                        }
                    }
                },
            )
        }
        item { ServiceSubGrid(ServiceSpecs.appManager) }
        item {
            PreviewNote(
                "المصدر: PackageManager. حجم الحزمة يظهر فقط عندما يوفره النظام؛ " +
                    "الغياب يعني «غير متاح» وليس صفرًا. تفاصيل الأذونات تُفتح في إعدادات Android الرسمية."
            )
        }
    }
}

@Composable
private fun AppManagerRealSummary(facts: DeviceFacts) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Violet, borderAlpha = .40f) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            RealSummaryCell("${formatCount(facts.installedCount)}", "مثبت", V2Violet, Modifier.weight(1f))
            Box(Modifier.width(1.dp).height(30.dp).background(Color.White.withAlpha(.09f)))
            RealSummaryCell("${formatCount(facts.userAppCount)}", "مستخدم", V2Cyan, Modifier.weight(1f))
            Box(Modifier.width(1.dp).height(30.dp).background(Color.White.withAlpha(.09f)))
            RealSummaryCell("${formatCount(facts.sensitiveAppCount)}", "حساس", V2Amber, Modifier.weight(1f))
        }
    }
}

@Composable
private fun RealSummaryCell(value: String, label: String, accent: Color, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        KText(value, size = 16.sp, weight = FontWeight.Black, color = accent)
        KText(label, size = 9.sp, color = V2Soft)
    }
}

@Composable
private fun RealInstalledAppRow(
    app: InstalledAppFacts,
    onClick: () -> Unit,
    onUninstall: (() -> Unit)? = null,
) {
    val icon = rememberAppIcon(app.packageName)
    val canUninstall = onUninstall != null && !app.isSystem
    V2GlassCard(
        Modifier.fillMaxWidth().clickable(onClick = onClick),
        padding = 9.dp,
        radius = 15.dp,
        glow = if (app.isSystem) V2Violet else V2Cyan,
        borderAlpha = .22f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(V2Deep2)
                    .border(1.dp, (if (app.isSystem) V2Violet else V2Cyan).withAlpha(.28f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                if (icon != null) {
                    androidx.compose.foundation.Image(
                        bitmap = icon,
                        contentDescription = null,
                        modifier = Modifier.size(34.dp),
                    )
                } else {
                    Icon(Icons.Rounded.Android, null, tint = V2Soft, modifier = Modifier.size(19.dp))
                }
            }
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                KText(app.label, size = 10.sp, weight = FontWeight.Bold, maxLines = 1)
                KText(
                    buildString {
                        append(app.packageName)
                        if (app.isSystem) append(" · نظام")
                        if (!app.isEnabled) append(" · معطّل")
                    },
                    size = 9.sp,
                    color = V2Muted,
                    maxLines = 1,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                KText(app.appBytes?.let { formatBytes(it) } ?: "غير متاح", size = 9.sp, weight = FontWeight.Bold, color = V2Cyan)
                KText(app.versionName ?: "—", size = 9.sp, color = V2Muted, maxLines = 1)
                if (canUninstall) {
                    Spacer(Modifier.height(4.dp))
                    // 40dp touch target around the small delete glyph.
                    Box(
                        Modifier
                            .size(40.dp)
                            .clickable(onClick = onUninstall),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(V2Red.withAlpha(.10f))
                                .border(1.dp, V2Red.withAlpha(.45f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Rounded.Delete, null, tint = V2Red, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppFilterChip(
    text: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier
            .height(34.dp)
            .clip(shape)
            .background(
                if (active) Brush.horizontalGradient(listOf(V2Cyan.withAlpha( .88f), V2Blue.withAlpha( .92f)))
                else Brush.horizontalGradient(listOf(Color.White.withAlpha( .055f), Color.White.withAlpha( .02f)))
            )
            .border(1.dp, if (active) Color.Transparent else V2Border.withAlpha( .26f), shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        KText(
            text,
            size = 9.sp,
            weight = if (active) FontWeight.Bold else FontWeight.Medium,
            color = if (active) V2Deep else V2Soft,
            maxLines = 1,
        )
    }
}

@Composable
private fun AppManagerSummary() {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 12.dp,
        radius = 22.dp,
        glow = V2Violet,
        borderAlpha = .52f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2NeonRing(
                progress = .67f,
                size = 92.dp,
                stroke = 7.dp,
                colors = listOf(V2Cyan, V2Blue, V2Violet),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    KText("46.8", size = 17.sp, weight = FontWeight.Black)
                    KText("GB مستخدمة", size = 9.sp, color = V2Soft)
                }
            }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                KText("54 تطبيقًا مثبتًا", size = 11.5.sp, weight = FontWeight.Bold)
                KText("من أصل 72.4 GB مساحة متاحة", size = 9.sp, color = V2Soft)
                Spacer(Modifier.height(6.dp))
                V2ProgressBar(.67f, height = 5.dp)
                Spacer(Modifier.height(7.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SummaryPill("8 ثقيلة", V2Pink)
                    SummaryPill("12 نادرًا", V2Amber)
                }
            }
        }
    }
}

@Composable
internal fun SummaryPill(text: String, accent: Color) {
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(accent.withAlpha( .12f))
            .border(1.dp, accent.withAlpha( .28f), RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        KText(text, size = 9.sp, weight = FontWeight.SemiBold, color = accent)
    }
}

@Composable
private fun ManagedAppRow(
    app: ManagedAppV2,
    checked: Boolean,
    onToggle: () -> Unit,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    if (checked) listOf(app.accent.withAlpha( .13f), V2Panel2)
                    else listOf(Color.White.withAlpha( .04f), V2Panel2)
                )
            )
            .border(1.dp, if (checked) app.accent.withAlpha( .45f) else Color.White.withAlpha( .10f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 9.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppLogo(app.mark, size = 42.dp)
        Spacer(Modifier.width(9.dp))
        Column(Modifier.weight(1f)) {
            KText(app.name, size = 11.5.sp, weight = FontWeight.Bold, maxLines = 1)
            KText(app.packageName, size = 9.sp, color = V2Muted, maxLines = 1)
            Spacer(Modifier.height(2.dp))
            KText("آخر استخدام ${app.lastUsed}", size = 9.sp, color = V2Muted, maxLines = 1)
        }
        Column(horizontalAlignment = Alignment.End) {
            KText(app.size, size = 10.5.sp, weight = FontWeight.Bold, color = app.accent)
            KText("${app.permissions} أذونات", size = 9.sp, color = V2Muted)
        }
        Spacer(Modifier.width(8.dp))
        V2Check(checked = checked, accent = app.accent, onToggle = onToggle)
    }
}

/* ------------------------------------------------------------------ *
 * App detail
 * ------------------------------------------------------------------ */

@Composable
fun V2AppDetailScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val (refresh, bump) = rememberRefreshCounter()
    val facts = rememberDeviceFacts(refresh)
    var selectedPackage by remember { mutableStateOf<String?>(null) }
    var actionError by remember { mutableStateOf<String?>(null) }
    val app = facts.installedApps.firstOrNull { it.packageName == selectedPackage }

    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
        bottomBar = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (app != null) {
                    V2GhostButton("معلومات التطبيق في Android", {
                        actionError = showInstalledAppInfo(context, app.packageName)
                    }, icon = Icons.Rounded.Settings, height = 48.dp)
                    V2GradientButton("فتح التطبيق", {
                        actionError = launchVisiblePackage(context, app.packageName)
                    }, icon = Icons.Rounded.PlayArrow, height = 48.dp)
                } else {
                    V2GradientButton("تحديث قائمة التطبيقات", bump, icon = Icons.Rounded.Refresh, height = 48.dp)
                }
            }
        },
    ) {
        item { PageHeading("تفاصيل تطبيق مثبت", "اختر حزمة ظاهرة من هذا الجهاز", Icons.Rounded.Apps, V2Cyan) }
        actionError?.let { message -> item { ScopedNotice(message, V2Amber) } }
        if (app != null) {
            item {
                V2GlassCard(Modifier.fillMaxWidth(), padding = 14.dp, radius = 20.dp, glow = V2Violet) {
                    Column {
                        KText(app.label, size = 22.sp, weight = FontWeight.Bold)
                        KText(app.packageName, size = 12.sp, color = V2Soft)
                        InfoLine("الإصدار", app.versionName ?: "غير معلن", V2Cyan)
                        InfoLine("النوع", if (app.isSystem) "نظام" else "مستخدم", V2Violet)
                        InfoLine("التمكين", if (app.isEnabled) "مفعّل" else "معطّل", V2Cyan)
                        InfoLine("حجم الحزمة", app.appBytes?.let(::formatBytes) ?: "غير متاح", V2Soft)
                        InfoLine("أذونات معلنة", formatCount(app.declaredPermissions.size), V2Amber)
                    }
                }
            }
            item { ScopedNotice("الكاش والبيانات الخاصة وإيقاف التطبيق وإدارة الخلفية تُدار في معلومات التطبيق الرسمية. فتح الإعدادات لا يعني تنفيذ مسح أو إيقاف.") }
            app.sensitivePermissions.forEach { permission ->
                item {
                    ScopedNotice("إذن معلن: $permission\nالإعلان لا يثبت المنح أو الاستخدام؛ راجع الحالة في إعدادات Android.", V2Violet)
                }
            }
            item { V2GhostButton("اختيار تطبيق آخر", { selectedPackage = null; actionError = null }, height = 48.dp) }
        } else {
            item {
                ScopedNotice(when {
                    facts.computedAt == 0L -> "جاري قراءة PackageManager…"
                    selectedPackage != null -> "لم تعد الحزمة المختارة ظاهرة. اختر تطبيقاً آخر أو حدّث القائمة."
                    facts.installedApps.isEmpty() -> "لم تُعد قراءة PackageManager حزمًا ظاهرة. افتح إعدادات التطبيقات أو أعد القراءة."
                    else -> "اضغط على تطبيق لعرض إصداره وأذوناته وخيارات فتحه."
                })
            }
            items(facts.installedApps.size) { index ->
                val candidate = facts.installedApps[index]
                RealInstalledAppRow(candidate, onClick = { selectedPackage = candidate.packageName; actionError = null })
            }
            item { V2GhostButton("إعدادات جميع التطبيقات", { openAllAppSettings(context) }, height = 48.dp) }
        }
    }
}

@Composable
private fun DetailLegend(label: String, value: String, accent: Color) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(7.dp).clip(CircleShape).background(accent))
        Spacer(Modifier.width(5.dp))
        KText(label, size = 9.sp, color = V2Soft)
        Spacer(Modifier.weight(1f))
        KText(value, size = 9.sp, weight = FontWeight.SemiBold, color = Color.White)
    }
}

@Composable
private fun AppActionTile(label: String, icon: ImageVector, accent: Color, modifier: Modifier = Modifier) {
    V2GlassCard(modifier, padding = 8.dp, radius = 15.dp, glow = accent, borderAlpha = .32f) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(19.dp))
            Spacer(Modifier.height(4.dp))
            KText(label, size = 9.sp, weight = FontWeight.SemiBold, color = Color.White, maxLines = 1)
        }
    }
}

@Composable
private fun PermissionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    status: String,
    accent: Color,
) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(accent.withAlpha( .10f))
                .border(1.dp, accent.withAlpha( .26f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(15.dp))
        }
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            KText(title, size = 9.8.sp, weight = FontWeight.SemiBold)
            KText(subtitle, size = 9.sp, color = V2Muted, maxLines = 1)
        }
        Box(
            Modifier
                .clip(RoundedCornerShape(50))
                .background(accent.withAlpha( .11f))
                .padding(horizontal = 7.dp, vertical = 2.dp)
        ) {
            KText(status, size = 9.sp, weight = FontWeight.Bold, color = accent)
        }
    }
}

@Composable
private fun SettingToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    accent: Color,
    onChange: (Boolean) -> Unit,
) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        V2IconTile(icon, accent, size = 32.dp, iconSize = 16.dp)
        Spacer(Modifier.width(9.dp))
        Column(Modifier.weight(1f)) {
            KText(title, size = 9.8.sp, weight = FontWeight.SemiBold)
            KText(subtitle, size = 9.sp, color = V2Muted, maxLines = 1)
        }
        V2Toggle(checked, onChange, accent)
    }
}

/* ------------------------------------------------------------------ *
 * Storage analyzer
 * ------------------------------------------------------------------ */

private data class StorageCategoryV2(
    val name: String,
    val size: String,
    val percent: String,
    val fraction: Float,
    val icon: ImageVector,
    val accent: Color,
)

private val storageCategories = listOf(
    StorageCategoryV2("التطبيقات", "46.8 GB", "65%", .65f, Icons.Rounded.Apps, V2Violet),
    StorageCategoryV2("الصور والفيديو", "18.2 GB", "25%", .25f, Icons.Rounded.PhotoLibrary, V2Pink),
    StorageCategoryV2("المستندات", "4.6 GB", "6%", .06f, Icons.Rounded.Article, V2Cyan),
    StorageCategoryV2("الصوتيات", "1.8 GB", "3%", .03f, Icons.Rounded.Audiotrack, V2Amber),
    StorageCategoryV2("أخرى", "1.0 GB", "1%", .01f, Icons.Rounded.Folder, V2Green),
)

@Composable
fun V2StorageScreen(onBack: () -> Unit, onOpenDetail: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current

    val application = context.applicationContext as Application
    val storageDatabase = remember(application) { AppDatabase.getDatabase(application) }
    val storageRepository = remember(storageDatabase) { KnouxRepository(storageDatabase) }
    val storageViewModel: StorageAnalyzerViewModel = viewModel(
        key = "storage-analyzer",
        initializer = { StorageAnalyzerViewModel(application, storageRepository) },
    )

    val storageState = storageViewModel.state
    val facts = storageState.facts
    val storage = facts.storage

    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
        bottomBar = {
            V2GradientButton("إعدادات التخزين في Android", { openStorageSettings(context) }, icon = Icons.Rounded.Storage, trailingChevron = true)
        },
    ) {
        item {
            ServiceBanner(
                spec = ServiceSpecs.storage,
                status = when {
                    storageState.isScanning -> "جاري القراءة"
                    storage.totalBytes > 0L -> "StatFs مقروء"
                    else -> "جاهز للتشغيل"
                },
                statusColor = when {
                    storageState.isScanning -> V2Amber
                    storage.totalBytes > 0L -> V2Green
                    else -> V2Violet
                },
                metric = if (storage.totalBytes > 0L) "${(storage.usedFraction * 100).toInt()}% مستخدم" else "اضغط الزر الدائري لبدء الخدمة",
            )
        }
        item {
            // The universal K orb: the only way this service starts.
            KnouxRunOrbBlock(
                progress = when {
                    storageState.isScanning -> null
                    storageState.loaded -> 1f
                    else -> 0f
                },
                running = storageState.isScanning,
                enabled = !storageState.isScanning && storageState.gate != OperationGate.RUNNING,
                label = when {
                    storageState.isScanning -> "جارٍ قراءة التخزين…"
                    storageState.loaded -> "اضغط لإعادة قراءة التخزين"
                    else -> "اضغط لتشغيل الخدمة"
                },
                accent = V2Cyan,
                accent2 = V2Violet,
                onTap = { storageViewModel.rescan() },
            )
        }
        item { StorageHeroCard(storage, facts.ownCacheBytes) }
        item {
            StorageCacheActionCard(
                state = storageState,
                onPrepare = { storageViewModel.prepareClearOwnCache() },
                onConfirm = { storageViewModel.confirmClearOwnCache() },
                onCancel = { storageViewModel.cancelClearOwnCache() },
                onRescan = { storageViewModel.rescan() },
            )
        }
        item { V2SectionLabel("القراءة الفعلية", trailing = "من الجهاز") }
        item { RealStorageBreakdown(facts) }
        item { V2SectionLabel("أكبر الملفات", trailing = if (facts.media.permissionGranted) "MediaStore" else "بعد المنح") }
        if (!facts.media.permissionGranted) {
            item {
                ServiceCapabilityCard(
                    title = "صلاحية الصور والفيديو مطلوبة",
                    body = "لا يقرأ KNOUX مكتبتك قبل موافقتك. تُطلب الصلاحية من شاشة الملفات المكررة أو من إعدادات Android.",
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
            facts.media.largestFiles.take(4).forEach { file ->
                item {
                    LoadingFileRow(
                        title = file.name,
                        subtitle = file.bucket ?: if (file.isVideo) "فيديو" else "صورة",
                        size = formatBytes(file.sizeBytes),
                        accent = if (file.isVideo) V2Pink else V2Violet,
                    )
                }
            }
        }
        item { ServiceSubGrid(ServiceSpecs.storage) }
        item {
            PreviewNote(
                "الأرقام أعلاه من StatFs وMediaStore على هذا الهاتف. أحجام التطبيقات المنفصلة تحتاج " +
                    "صلاحية Usage Access، وتظهر «مقيدة» بدل رقم تقديري."
            )
        }
    }
}

@Composable
private fun StorageHeroCard(storage: StorageFacts, cacheBytes: Long) {
    val progress = storage.usedFraction.coerceIn(0f, 1f)
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 22.dp,
        glow = V2Violet,
        borderAlpha = .52f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2NeonRing(
                progress = progress,
                size = 112.dp,
                stroke = 8.dp,
                colors = listOf(V2Pink, V2Violet, V2Blue, V2Cyan),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    KText("${(progress * 100).toInt()}%", size = 26.sp, weight = FontWeight.Black)
                    KText("مستخدم", size = 9.sp, color = V2Soft)
                }
            }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                KText(formatBytes(storage.usedBytes), size = 22.sp, weight = FontWeight.Black)
                KText("من إجمالي ${formatBytes(storage.totalBytes)}", size = 9.sp, color = V2Soft)
                Spacer(Modifier.height(7.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Check, null, tint = V2Green, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(4.dp))
                    KText("${formatBytes(storage.freeBytes)} متاحة", size = 9.sp, weight = FontWeight.SemiBold, color = V2Green)
                }
                Spacer(Modifier.height(5.dp))
                KText("كاش KNOUX المقروء: ${formatBytes(cacheBytes)} · يُحذف بتأكيدك فقط.", size = 9.sp, color = V2Muted)
            }
        }
    }
}

@Composable
private fun RealStorageBreakdown(facts: DeviceFacts) {
    val total = facts.storage.totalBytes.coerceAtLeast(1L)
    val mediaGranted = facts.media.permissionGranted
    val images = if (mediaGranted) facts.media.imagesBytes else 0L
    val videos = if (mediaGranted) facts.media.videosBytes else 0L
    val cache = facts.ownCacheBytes
    val others = (facts.storage.usedBytes - images - videos - cache).coerceAtLeast(0L)
    V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Cyan, borderAlpha = .28f) {
        Column {
            listOf(
                Triple("الصور", if (mediaGranted) formatBytes(images) else "بعد المنح", images),
                Triple("الفيديو", if (mediaGranted) formatBytes(videos) else "بعد المنح", videos),
                Triple("كاش KNOUX", formatBytes(cache), cache),
                Triple("النظام والتطبيقات", formatBytes(others), others),
            ).forEachIndexed { index, (name, value, bytes) ->
                RealStorageRow(
                    name = name,
                    value = value,
                    fraction = (bytes.toFloat() / total.toFloat()).coerceIn(0f, 1f),
                    accent = when (index) {
                        0 -> V2Pink
                        1 -> V2Violet
                        2 -> V2Cyan
                        else -> V2Amber
                    },
                )
                if (index != 3) Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun RealStorageRow(name: String, value: String, fraction: Float, accent: Color) {
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            KText(name, size = 9.4.sp, weight = FontWeight.SemiBold)
            Spacer(Modifier.weight(1f))
            KText(value, size = 9.sp, weight = FontWeight.Bold, color = accent)
        }
        Spacer(Modifier.height(3.dp))
        V2ProgressBar(fraction, height = 4.dp, colors = listOf(accent, accent.withAlpha(.55f)))
    }
}

@Composable
private fun LoadingFileRow(title: String, subtitle: String, size: String, accent: Color) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 9.dp, radius = 15.dp, glow = accent, borderAlpha = .24f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(Icons.Rounded.PhotoLibrary, accent, size = 32.dp, iconSize = 16.dp)
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                KText(title, size = 9.2.sp, weight = FontWeight.SemiBold, maxLines = 1)
                KText(subtitle, size = 9.sp, color = V2Muted, maxLines = 1)
            }
            KText(size, size = 9.2.sp, weight = FontWeight.Bold, color = accent)
        }
    }
}

@Composable
private fun StorageCategoryRow(category: StorageCategoryV2) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(category.accent.withAlpha( .12f))
                .border(1.dp, category.accent.withAlpha( .28f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(category.icon, null, tint = category.accent, modifier = Modifier.size(15.dp))
        }
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                KText(category.name, size = 9.6.sp, weight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                KText(category.size, size = 9.2.sp, weight = FontWeight.Bold, color = category.accent)
            }
            Spacer(Modifier.height(4.dp))
            V2ProgressBar(category.fraction, height = 4.dp, colors = listOf(category.accent, category.accent.withAlpha( .55f)))
        }
        Spacer(Modifier.width(8.dp))
        KText(category.percent, size = 9.sp, color = V2Muted, modifier = Modifier.width(26.dp), align = TextAlign.End)
    }
}

@Composable
private fun BigFileRow(title: String, size: String, icon: ImageVector, accent: Color) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 9.dp, radius = 15.dp, glow = accent, borderAlpha = .24f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.withAlpha( .12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = accent, modifier = Modifier.size(17.dp))
            }
            Spacer(Modifier.width(8.dp))
            KText(title, size = 9.4.sp, weight = FontWeight.SemiBold, modifier = Modifier.weight(1f), maxLines = 1)
            KText(size, size = 9.4.sp, weight = FontWeight.Bold, color = accent)
        }
    }
}

/* ------------------------------------------------------------------ *
 * Battery
 * ------------------------------------------------------------------ */

@Composable
fun V2BatteryScreen(onBack: () -> Unit, onOpenDetail: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val (refresh, bump) = rememberRefreshCounter()
    val facts = rememberDeviceFacts(refresh)
    val battery = facts.battery
    val database = remember(context) { AppDatabase.getDatabase(context.applicationContext) }
    val repository = remember(database) { KnouxRepository(database) }
    val scope = rememberCoroutineScope()
    val openUsagePermission: () -> Unit = { openUsageAccessSettings(context) }
    var refreshing by remember { mutableStateOf(false) }
    LaunchedEffect(facts.computedAt) { if (refreshing) refreshing = false }

    LaunchedEffect(
        refresh,
        battery.levelPercent,
        battery.statusLabel,
        battery.temperatureC,
        battery.voltageMv,
        battery.technology,
        battery.charging,
        battery.healthLabel,
    ) {
        scope.launch {
            withContext(Dispatchers.IO) {
                repository.recordBatterySample(
                    id = UUID.randomUUID().toString(),
                    levelPercent = battery.levelPercent,
                    status = battery.statusLabel,
                    temperatureC = battery.temperatureC,
                    voltageMv = battery.voltageMv,
                    technology = battery.technology,
                    charging = battery.charging,
                    health = battery.healthLabel,
                    recordedAt = System.currentTimeMillis(),
                )
            }
        }
    }

    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
        bottomBar = {
            V2GhostButton(
                "إعدادات البطارية",
                onClick = { openBatterySaverSettings(context) },
                icon = Icons.Rounded.Settings,
                height = 47.dp,
            )
        },
    ) {
        item {
            ServiceBanner(
                spec = ServiceSpecs.battery,
                status = if (battery.levelPercent != null) "BatteryManager" else "جاري القراءة",
                statusColor = if (battery.levelPercent != null) V2Green else V2Amber,
                metric = battery.levelPercent?.let { "$it% · ${battery.statusLabel}" } ?: "غير متاح",
            )
        }
        item {
            // The universal K orb: the only way this service starts.
            KnouxRunOrbBlock(
                progress = when {
                    refreshing -> null
                    battery.levelPercent != null -> 1f
                    else -> 0f
                },
                running = refreshing,
                enabled = !refreshing,
                label = when {
                    refreshing -> "جارٍ قراءة البطارية…"
                    battery.levelPercent != null -> "اضغط لإعادة قراءة البطارية"
                    else -> "اضغط لتشغيل الخدمة"
                },
                accent = V2Green,
                accent2 = V2Cyan,
                onTap = {
                    if (!refreshing) {
                        refreshing = true
                        bump()
                    }
                },
            )
        }
        item { BatteryHeroCard(battery) }
        item { V2SectionLabel("القراءة الفعلية", trailing = "من النظام") }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Green, borderAlpha = .30f) {
                Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    BatteryFactRow("الحالة", battery.statusLabel, V2Green)
                    BatteryFactRow("الصحة", battery.healthLabel, V2Cyan)
                    BatteryFactRow("الحرارة", battery.temperatureC?.let { String.format(java.util.Locale.US, "%.1f°C", it) } ?: "غير متاح", V2Amber)
                    BatteryFactRow("الجهد", battery.voltageMv?.let { "${formatCount(it)} mV" } ?: "غير متاح", V2Violet)
                    BatteryFactRow("التقنية", battery.technology ?: "غير متاح", V2Soft)
                    BatteryFactRow("الشاحن", if (battery.charging) "متصل" else "غير متصل", if (battery.charging) V2Green else V2Muted)
                }
            }
        }
        item { V2SectionLabel("إدارة الاستهلاك", trailing = "يدوي") }
        item {
            ServiceCapabilityCard(
                title = "استهلاك التطبيقات التفصيلي",
                body = if (facts.usageAccessGranted) {
                    "صلاحية الاستخدام ممنوحة، لكن قراءة الاستهلاك لكل تطبيق لم تُدمج بعد. لا تُعرض أرقام تقديرية."
                } else {
                    "Android يخفي استهلاك كل تطبيق حتى تمنح Usage Access. لن يعرض KNOUX نسبًا وهمية."
                },
                state = if (facts.usageAccessGranted) ServiceState.PREVIEW else ServiceState.PERMISSION,
                actionLabel = if (facts.usageAccessGranted) null else "فتح الصلاحية",
                onAction = if (facts.usageAccessGranted) null else openUsagePermission,
            )
        }
        item {
            ServiceCapabilityCard(
                title = "وضع توفير البطارية",
                body = "يُدار من إعدادات Android الرسمية. لا يغيّر KNOUX إعدادات النظام تلقائيًا.",
                state = ServiceState.RESTRICTED,
                actionLabel = "فتح",
                onAction = { openBatterySettings(context) },
            )
        }
        item { ServiceSubGrid(ServiceSpecs.battery) }
        item {
            PreviewNote(
                "كل القيم أعلاه من BatteryManager لحظة القراءة. الوقت المتبقي غير معروض " +
                    "لأن Android لا يوفره بشكل موثوق لكل الأجهزة."
            )
        }
    }
}

@Composable
fun V2BatteryReadingsScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val database = remember(context) { AppDatabase.getDatabase(context.applicationContext) }
    val repository = remember(database) { KnouxRepository(database) }
    val samples by repository.recentBatterySamples(50).collectAsState(initial = emptyList())

    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
    ) {
        item {
            ServiceBanner(
                spec = ServiceSpecs.battery,
                status = if (samples.isNotEmpty()) "${samples.size} عينة محفوظة" else "لا عينات بعد",
                statusColor = if (samples.isNotEmpty()) V2Green else V2Amber,
            )
        }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Green, borderAlpha = .30f) {
                Column {
                    KText(
                        "كل عينة تُسجل لحظة القراءة الفعلية من BatteryManager - لا تقديرات، لا أرقام وهمية.",
                        size = 9.sp,
                        color = V2Soft,
                        lineHeight = 10.6.sp,
                    )
                }
            }
        }
        if (samples.isEmpty()) {
            item {
                V2GlassCard(
                    Modifier.fillMaxWidth(),
                    padding = 12.dp,
                    radius = 16.dp,
                    glow = V2Green,
                    borderAlpha = .30f,
                ) {
                    Column {
                        KText(
                            "لا عينات بطارية محفوظة بعد",
                            size = 9.4.sp,
                            weight = FontWeight.Bold,
                            color = V2Cyan,
                        )
                        Spacer(Modifier.height(4.dp))
                        KText(
                            "افتح شاشة البطارية لتسجيل أول قراءة فعلية من النظام.",
                            size = 9.sp,
                            color = V2Soft,
                        )
                    }
                }
            }
        } else {
            items(samples.size) { index ->
                val s = samples[index]
                V2GlassCard(
                    Modifier.fillMaxWidth(),
                    padding = 10.dp,
                    radius = 14.dp,
                    glow = V2Cyan,
                    borderAlpha = .22f,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            KText(
                                "${s.levelPercent?.let { "$it%" } ?: "غير متاح"} · ${s.status ?: "—"}",
                                size = 9.4.sp,
                                weight = FontWeight.Bold,
                                color = V2Cyan,
                            )
                            KText(
                                "${s.health ?: "—"} · ${if (s.charging) "يشحن" else "لا يشحن"}",
                                size = 9.sp,
                                color = V2Muted,
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            KText(
                                s.temperatureC?.let { String.format(java.util.Locale.US, "%.1f°C", it) } ?: "—",
                                size = 9.sp,
                                weight = FontWeight.Bold,
                                color = V2Amber,
                            )
                            KText(
                                java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                                    .format(java.util.Date(s.recordedAt)),
                                size = 9.sp,
                                color = V2Muted,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BatteryHeroCard(battery: BatteryFacts) {
    val level = battery.levelPercent
    val progress = (level ?: 0) / 100f
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 22.dp,
        glow = V2Green,
        borderAlpha = .52f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2NeonRing(
                progress = progress.coerceIn(0f, 1f),
                size = 112.dp,
                stroke = 8.dp,
                colors = listOf(V2Green, V2Cyan, V2Blue),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    KText(level?.let { "$it%" } ?: "—", size = 28.sp, weight = FontWeight.Black)
                    KText("مستوى البطارية", size = 9.sp, color = V2Soft)
                }
            }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Bolt, null, tint = V2Green, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(4.dp))
                    KText(battery.statusLabel, size = 11.sp, weight = FontWeight.Bold, color = V2Green)
                }
                Spacer(Modifier.height(4.dp))
                KText(battery.healthLabel, size = 18.sp, weight = FontWeight.Black)
                KText("صحة البطارية من النظام", size = 9.sp, color = V2Muted)
                Spacer(Modifier.height(6.dp))
                battery.temperatureC?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Thermostat, null, tint = V2Amber, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        KText(String.format(java.util.Locale.US, "%.1f°C", it), size = 9.sp, weight = FontWeight.SemiBold, color = V2Amber)
                    }
                    Spacer(Modifier.height(4.dp))
                }
                KText(if (battery.charging) "الشاحن متصل" else "يعمل على البطارية", size = 9.sp, color = V2Soft)
            }
        }
    }
}

@Composable
private fun BatteryFactRow(label: String, value: String, accent: Color) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        KText(label, size = 9.sp, color = V2Soft)
        Spacer(Modifier.weight(1f))
        KText(value, size = 9.4.sp, weight = FontWeight.Bold, color = accent)
    }
}

@Composable
private fun DrainRow(name: String, percent: String, fraction: Float, accent: Color, mark: AppMark) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 9.dp, radius = 15.dp, glow = accent, borderAlpha = .22f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppLogo(mark, size = 34.dp)
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    KText(name, size = 9.6.sp, weight = FontWeight.SemiBold)
                    Spacer(Modifier.weight(1f))
                    KText(percent, size = 9.2.sp, weight = FontWeight.Bold, color = accent)
                }
                Spacer(Modifier.height(4.dp))
                V2ProgressBar(fraction, height = 4.dp, colors = listOf(accent, accent.withAlpha( .52f)))
            }
        }
    }
}

/* ------------------------------------------------------------------ *
 * AI optimization
 * ------------------------------------------------------------------ */

private data class AiAction(
    val title: String,
    val detail: String,
    val gain: String,
    val icon: ImageVector,
    val accent: Color,
)

private val aiActions = listOf(
    AiAction("إغلاق التطبيقات الخلفية", "11 تطبيقًا يستهلك الذاكرة بدون استخدام", "1.2 GB", Icons.Rounded.Memory, V2Cyan),
    AiAction("تحسين بدء التشغيل", "تعطيل 4 تطبيقات تبدأ تلقائيًا", "أسرع 18%", Icons.Rounded.Speed, V2Violet),
    AiAction("تنظيف ذاكرة التخزين", "ملفات مؤقتة قابلة للحذف بأمان", "684 MB", Icons.Rounded.CleaningServices, V2Pink),
    AiAction("تبريد الجهاز", "إيقاف النشاط الثقيل مؤقتًا", "-4°", Icons.Rounded.Thermostat, V2Amber),
)

private data class LiveRecommendation(
    val key: String,
    val title: String,
    val detail: String,
    val value: String,
    val icon: ImageVector,
    val accent: Color,
)

private fun buildLiveRecommendations(facts: DeviceFacts): List<LiveRecommendation> {
    val storage = facts.storage
    val battery = facts.battery
    return buildList {
        if (facts.ownCacheBytes > 0L) add(
            LiveRecommendation(
                "cache",
                "تنظيف كاش KNOUX",
                "ملفات مؤقتة داخل التطبيق فقط، بعد تأكيدك",
                formatBytes(facts.ownCacheBytes),
                Icons.Rounded.CleaningServices,
                V2Pink,
            )
        )
        if (storage.totalBytes > 0L && storage.usedFraction >= .80f) add(
            LiveRecommendation(
                "storage",
                "مساحة التخزين مشغولة",
                "راجع أكبر الملفات قبل أي حذف",
                "${(storage.usedFraction * 100).toInt()}%",
                Icons.Rounded.Storage,
                V2Cyan,
            )
        )
        if (facts.media.duplicateGroups.isNotEmpty()) add(
            LiveRecommendation(
                "duplicates",
                "نسخ وسائط مكررة",
                "مجموعات مطابقة ببصمة SHA-256 من MediaStore",
                "${facts.media.duplicateGroups.size} مجموعة",
                Icons.Rounded.Layers,
                V2Violet,
            )
        )
        if (facts.sensitiveAppCount > 0) add(
            LiveRecommendation(
                "privacy",
                "أذونات حساسة ممنوحة",
                "راجع الكاميرا والميكروفون والموقع",
                "${formatCount(facts.sensitiveAppCount)} تطبيق",
                Icons.Rounded.Shield,
                V2Amber,
            )
        )
        battery.levelPercent?.let { level ->
            if (level <= 35 && !battery.charging) add(
                LiveRecommendation(
                    "battery",
                    "بطارية منخفضة",
                    "افتح وضع توفير البطارية من إعدادات Android",
                    "$level%",
                    Icons.Rounded.BatteryFull,
                    V2Green,
                )
            )
        }
        if (!facts.usageAccessGranted) add(
            LiveRecommendation(
                "usage",
                "استهلاك التطبيقات غير مقروء",
                "صلاحية Usage Access غير ممنوحة",
                "مقيد",
                Icons.Rounded.Speed,
                V2Muted,
            )
        )
        if (facts.installedCount > 0) add(
            LiveRecommendation(
                "apps",
                "مراجعة التطبيقات الظاهرة للتطبيق",
                "الأحجام والأذونات من PackageManager",
                "${formatCount(facts.installedCount)} تطبيق",
                Icons.Rounded.Apps,
                V2Blue,
            )
        )
    }
}

@Composable
fun V2AiScreen(onBack: () -> Unit, onOpenDetail: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val navigate = LocalServiceNavigate.current
    val (refresh, bump) = rememberRefreshCounter()
    val facts = rememberDeviceFacts(refresh)
    val storage = facts.storage
    val battery = facts.battery
    var refreshing by remember { mutableStateOf(false) }
    LaunchedEffect(facts.computedAt) { if (refreshing) refreshing = false }

    val recommendations = remember(facts) { buildLiveRecommendations(facts) }

    fun run(key: String) {
        when (key) {
            "cache" -> navigate("smartclean")
            "storage" -> navigate("storage")
            "duplicates" -> navigate("duplicates")
            "privacy" -> navigate("privacy")
            "battery" -> openBatterySaverSettings(context)
            "usage" -> openUsageAccessSettings(context)
            "apps" -> navigate("appmanager")
        }
    }

    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
    ) {
        item {
            ServiceBanner(
                spec = ServiceSpecs.ai,
                status = if (facts.computedAt > 0L) "قراءة مباشرة" else "جاري القراءة",
                statusColor = if (facts.computedAt > 0L) V2Green else V2Amber,
                metric = "${recommendations.size} توصية",
            )
        }
        item {
            // The universal K orb: the only way this service starts.
            KnouxRunOrbBlock(
                progress = when {
                    refreshing -> null
                    facts.computedAt > 0L -> 1f
                    else -> 0f
                },
                running = refreshing,
                enabled = !refreshing,
                label = when {
                    refreshing -> "جارٍ تحديث التوصيات…"
                    facts.computedAt > 0L -> "اضغط لتحديث التوصيات"
                    else -> "اضغط لتشغيل الخدمة"
                },
                accent = V2Violet,
                accent2 = V2Cyan,
                onTap = {
                    if (!refreshing) {
                        refreshing = true
                        bump()
                    }
                },
            )
        }
        item {
            AiScoreCard(
                score = facts.healthEstimate,
                storageUsed = storage.usedFraction,
                batteryLevel = battery.levelPercent,
                apps = facts.installedCount,
            )
        }
        item { V2SectionLabel("التوصيات", trailing = "${recommendations.size} مرتبة بالأثر") }
        if (recommendations.isEmpty()) {
            item {
                ServiceCapabilityCard(
                    title = "لا توصيات حالية",
                    body = "كل القراءات المتاحة ضمن النطاق الطبيعي. تُحدَّث التوصيات مع كل قراءة جديدة.",
                    state = ServiceState.LIVE,
                )
            }
        }
        items(recommendations.size) { index ->
            val action = recommendations[index]
            LiveRecommendationRow(action) { run(action.key) }
        }
        item {
            V2GlassCard(
                Modifier.fillMaxWidth(),
                padding = 11.dp,
                radius = 17.dp,
                glow = V2GoldText,
                borderAlpha = .30f,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.AutoAwesome, null, tint = V2Amber, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        KText("تحسينات Pro الأعمق", size = 9.8.sp, weight = FontWeight.Bold, color = V2GoldText)
                        KText("غير متاح في هذا الإصدار — لا يوجد محرك تغيير تلقائي لإعدادات النظام.", size = 9.sp, color = V2Soft)
                    }
                }
            }
        }
        item { ServiceSubGrid(ServiceSpecs.ai) }
        item {
            PreviewNote(
                "كل توصية مبنية على قراءة فعلية من هذا الهاتف. التنفيذ يفتح الشاشة أو إعدادات Android " +
                    "المختصة؛ لا يوجد تطبيق تلقائي لأي تغيير."
            )
        }
    }
}

@Composable
private fun AiScoreCard(score: Int, storageUsed: Float, batteryLevel: Int?, apps: Int) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 13.dp,
        radius = 22.dp,
        glow = V2Pink,
        borderAlpha = .52f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2NeonRing(
                progress = score / 100f,
                size = 108.dp,
                stroke = 8.dp,
                colors = listOf(V2Cyan, V2Blue, V2Violet, V2Pink),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    KText(if (score > 0) "$score" else "—", size = 30.sp, weight = FontWeight.Black)
                    KText("تقدير الجهاز", size = 9.sp, color = V2Soft)
                }
            }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Check, null, tint = V2Green, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    KText("مؤشر تقديري", size = 11.sp, weight = FontWeight.Bold, color = V2Green)
                }
                Spacer(Modifier.height(4.dp))
                KText(
                    "يُحسب من التخزين والبطارية والأذونات، وليس قياسًا لسرعة المعالج.",
                    size = 9.sp,
                    lineHeight = 11.5.sp,
                    color = V2Soft,
                )
                Spacer(Modifier.height(6.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    KText("تخزين ${(storageUsed * 100).toInt()}%", size = 9.sp, color = V2Cyan, maxLines = 1, modifier = Modifier.weight(1f))
                    KText(batteryLevel?.let { "بطارية $it%" } ?: "بطارية —", size = 9.sp, color = V2Green, maxLines = 1, modifier = Modifier.weight(1f))
                    KText("$apps تطبيق", size = 9.sp, color = V2Violet, maxLines = 1, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(6.dp))
                V2ProgressBar(score / 100f, height = 5.dp, colors = listOf(V2Pink, V2Violet, V2Cyan))
            }
        }
    }
}

@Composable
private fun LiveRecommendationRow(action: LiveRecommendation, onClick: () -> Unit) {
    V2GlassCard(
        Modifier.fillMaxWidth().clickable(onClick = onClick),
        padding = 10.dp,
        radius = 17.dp,
        glow = action.accent,
        borderAlpha = .34f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(action.icon, action.accent, size = 36.dp, iconSize = 18.dp)
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                KText(action.title, size = 9.8.sp, weight = FontWeight.Bold)
                KText(action.detail, size = 9.sp, color = V2Muted, maxLines = 1)
            }
            KText(action.value, size = 9.sp, weight = FontWeight.Bold, color = action.accent)
            Spacer(Modifier.width(5.dp))
            Icon(Icons.Rounded.ChevronLeft, null, tint = V2Soft, modifier = Modifier.size(15.dp))
        }
    }
}

@Composable
private fun AiActionRow(action: AiAction, applied: Boolean, onToggle: () -> Unit) {
    V2GlassCard(
        Modifier.fillMaxWidth(),
        padding = 10.dp,
        radius = 17.dp,
        glow = if (applied) V2Green else action.accent,
        borderAlpha = .34f,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(action.icon, if (applied) V2Green else action.accent, size = 36.dp, iconSize = 18.dp)
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                KText(action.title, size = 9.8.sp, weight = FontWeight.Bold)
                KText(action.detail, size = 9.sp, color = V2Muted, maxLines = 1)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                KText(action.gain, size = 9.sp, weight = FontWeight.Bold, color = V2Cyan)
                Spacer(Modifier.height(3.dp))
                AppFilterChip(
                    text = if (applied) "مطبق" else "تطبيق",
                    active = applied,
                    onClick = onToggle,
                )
            }
        }
    }
}

@Composable
private fun AiImpactStat(
    value: String,
    label: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    V2GlassCard(modifier, padding = 9.dp, radius = 15.dp, glow = accent, borderAlpha = .28f) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(18.dp))
            Spacer(Modifier.height(3.dp))
            KText(value, size = 12.sp, weight = FontWeight.Bold)
            KText(label, size = 9.sp, color = V2Soft, maxLines = 1, align = TextAlign.Center)
        }
    }
}

/* ------------------------------------------------------------------ *
 * Settings
 * ------------------------------------------------------------------ */

@Composable
fun V2SettingsScreenV2(
    onBack: () -> Unit,
    signedIn: Boolean = false,
    onSignOut: () -> Unit = {},
    signOutBusy: Boolean = false,
    accountNotice: String? = null,
) {
    val context = LocalContext.current
    val dayMode = KnouxDayState.enabled
    var notifications by remember { mutableStateOf(true) }
    var weekly by remember { mutableStateOf(true) }
    var haptics by remember { mutableStateOf(true) }
    var localAnalytics by remember { mutableStateOf(false) }
    var appLock by remember { mutableStateOf(false) }
    var highQuality by remember { mutableStateOf(true) }

    Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)) {
        Column(Modifier.fillMaxSize()) {
            V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = {})
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item { PageHeading("الإعدادات", "تخصيص التطبيق والخصوصية", Icons.Rounded.Settings, V2Violet) }
                item {
                    V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 20.dp, glow = V2Violet, borderAlpha = .42f) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(V2Violet, V2Cyan)))
                                    .border(1.dp, Color.White.withAlpha( .30f), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                KText("K", size = 19.sp, weight = FontWeight.Black)
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                KText("KNOUX ID", size = 11.sp, weight = FontWeight.Bold)
                                KText(
                                    if (signedIn) "حساب متصل · جلسة مشفّرة على الجهاز"
                                    else "غير مسجل · كل الأدوات تعمل محليًا",
                                    size = 9.sp,
                                    color = V2Soft,
                                )
                            }
                            SummaryPill(if (signedIn) "متصل" else "ضيف", if (signedIn) V2Green else V2Cyan)
                        }
                    }
                }
                item {
                    V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 18.dp, glow = V2Amber, borderAlpha = .30f) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable(enabled = !signOutBusy, onClick = onSignOut)
                                .padding(vertical = 2.dp),
                        ) {
                            SettingNavRow(
                                Icons.Rounded.Logout,
                                when {
                                    signOutBusy -> "جارٍ إنهاء الجلسة…"
                                    signedIn -> "تسجيل الخروج"
                                    else -> "فتح شاشة الدخول"
                                },
                                if (signedIn) "ينهي الجلسة ويعيدك لشاشة الدخول" else "Google · Passkeys · البريد · البصمة",
                                V2Amber,
                            )
                        }
                    }
                }
                if (accountNotice != null) {
                    item {
                        // Real outcome of the sign-out attempt: shown only when the
                        // session could not be confirmed as cleared.
                        V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 16.dp, glow = V2Amber, borderAlpha = .55f) {
                            KText(
                                accountNotice,
                                size = 10.sp,
                                color = V2Amber,
                                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                            )
                        }
                    }
                }
                item { V2SectionLabel("عام") }
                item {
                    V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 18.dp, glow = V2Cyan, borderAlpha = .26f) {
                        Column {
                            SettingNavRow(Icons.Rounded.Language, "اللغة", "العربية", V2Cyan)
                            HairLine()
                            SettingToggleRow(
                                if (dayMode) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                                "المظهر",
                                if (dayMode) "الوضع النهاري · بورسلين فاتح" else "الوضع الليلي · نيون كوني",
                                dayMode,
                                V2Violet,
                            ) { KnouxDayState.setEnabled(context, it) }
                            HairLine()
                            SettingNavRow(Icons.Rounded.Palette, "جودة العرض", if (highQuality) "عالية" else "موفرة", V2Pink)
                            HairLine()
                            SettingToggleRow(Icons.Rounded.Notifications, "الإشعارات", "تنبيهات الفحص والتقارير", notifications, V2Amber) { notifications = it }
                        }
                    }
                }
                item { V2SectionLabel("الخصوصية والأمان") }
                item {
                    V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 18.dp, glow = V2Green, borderAlpha = .26f) {
                        Column {
                            SettingToggleRow(Icons.Rounded.Report, "تحليلات محلية", "إحصاءات على الجهاز فقط", localAnalytics, V2Green) { localAnalytics = it }
                            HairLine()
                            SettingToggleRow(Icons.Rounded.Lock, "قفل التطبيق", "بصمة قبل فتح الأدوات الحساسة", appLock, V2Cyan) { appLock = it }
                            HairLine()
                            SettingToggleRow(Icons.Rounded.CloudOff, "منع الرفع السحابي", "لا تُرسل أي بيانات خارج الجهاز", true, V2Violet) { }
                        }
                    }
                }
                item { V2SectionLabel("التقارير") }
                item {
                    V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 18.dp, glow = V2Pink, borderAlpha = .26f) {
                        Column {
                            SettingToggleRow(Icons.Rounded.Timeline, "التقرير الأسبوعي", "ملخص كل يوم أحد", weekly, V2Pink) { weekly = it }
                            HairLine()
                            SettingToggleRow(Icons.Rounded.Settings, "الاهتزاز", "ردود فعل لمسية خفيفة", haptics, V2Amber) { haptics = it }
                            HairLine()
                            SettingNavRow(Icons.Rounded.Backup, "نسخة احتياطية للإعدادات", "غير مفعلة", V2Muted)
                        }
                    }
                }
                item { V2SectionLabel("حول") }
                item {
                    var versionTapCount by remember { mutableStateOf(0) }
                    var showDevPanel by remember { mutableStateOf(false) }
                    V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 18.dp, glow = V2Violet, borderAlpha = .26f) {
                        Column {
                            InfoLine(
                                label = "الإصدار",
                                value = "1.0.0 (build 2)",
                                accent = Color.White,
                                onClick = {
                                    versionTapCount++
                                    if (versionTapCount >= 5) {
                                        showDevPanel = true
                                        versionTapCount = 0
                                    }
                                }
                            )
                            HairLine()
                            InfoLine("البناء", "معاينة تصميم V2", Color.White)
                            HairLine()
                            SettingNavRow(Icons.Rounded.Description, "سياسة الخصوصية", "محلية بالكامل", V2Cyan)
                            HairLine()
                            SettingNavRow(Icons.Rounded.Info, "شروط الاستخدام", "عرض", V2Muted)
                        }
                    }
                    if (showDevPanel) {
                        DeveloperPanel(onDismiss = { showDevPanel = false })
                    }
                }
                item { PreviewNote("هذه الإعدادات واجهة معاينة؛ لا تغيّر سلوك النظام فعليًا بعد.") }
            }
        }
    }
}

@Composable
private fun SettingNavRow(icon: ImageVector, title: String, value: String, accent: Color) {
    Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
        V2IconTile(icon, accent, size = 30.dp, iconSize = 15.dp)
        Spacer(Modifier.width(9.dp))
        KText(title, size = 9.8.sp, weight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        KText(value, size = 9.sp, color = V2Muted, maxLines = 1)
        Spacer(Modifier.width(3.dp))
        Icon(Icons.Rounded.ChevronLeft, null, tint = V2Soft, modifier = Modifier.size(15.dp))
    }
}

/* ------------------------------------------------------------------ *
 * Deep scan summary
 * ------------------------------------------------------------------ */

@Composable
fun V2ScanSummaryScreen(onBack: () -> Unit, onOpenSmartClean: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val (refresh, bump) = rememberRefreshCounter()
    val facts = rememberDeviceFacts(refresh)
    val database = remember(context) { AppDatabase.getDatabase(context.applicationContext) }
    val repository = remember(database) { KnouxRepository(database) }
    val sessions by remember(database) { database.scanSessionDao().getByService("deep_scan") }
        .collectAsState(initial = emptyList())
    val operations by remember(repository) { repository.recentOperations(25) }.collectAsState(initial = emptyList())
    val outcomes by remember(repository) { repository.outcomeSummaries() }.collectAsState(initial = emptyList())
    var history by remember { mutableStateOf(emptyList<ScanHistoryEntry>()) }
    LaunchedEffect(refresh) { history = withContext(Dispatchers.IO) { readScanHistory(context) } }
    val last = newestScan(history)
    val session = sessions.maxByOrNull { it.startedAt }
    val lastMeasuredOperation = operations.sortedByDescending { it.createdAt }.firstOrNull { operation ->
        operation.status in listOf("VERIFIED", "PARTIAL") && outcomes.any { it.operationId == operation.id }
    }
    val measured = outcomes.firstOrNull { it.operationId == lastMeasuredOperation?.id }

    InteriorScaffold(onBack, onPro, bottomBar = {
        V2GradientButton("مراجعة تنظيف كاش KNOUX", onOpenSmartClean, icon = Icons.Rounded.CleaningServices, height = 48.dp)
    }) {
        item { PageHeading("ملخص الفحص العميق", "قراءات الجهاز وسجل محلي؛ لا نتائج تقديرية", Icons.Rounded.Radar, V2Cyan) }
        item {
            ScopedNotice(when {
                session != null -> "آخر جلسة Room: ${formatScanTime(session.startedAt)} · ${session.status}"
                last != null -> "آخر قياس محفوظ محلياً: ${formatScanTime(last.timestamp)}\nلا توجد جلسة فحص عميق في Room؛ هذا سجل القياسات القديم فقط."
                else -> "لا توجد جلسة فحص محفوظة. الأرقام أدناه قراءة الجهاز الحالية وليست نتائج فحص سابق."
            }, V2Violet)
        }
        if (last != null) {
            item { ScopedNotice("القياس المحفوظ: ${formatCount(last.appsScanned)} حزمة ظاهرة · كاش ${formatBytes(last.cacheBytes)} · بطارية ${last.batteryLevel?.let { "$it%" } ?: "غير مقروءة"}") }
        }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 14.dp, radius = 20.dp, glow = V2Cyan) {
                Column {
                    KText("قراءة الجهاز الحالية", size = 16.sp, weight = FontWeight.Bold)
                    if (facts.computedAt == 0L) KText("جاري القراءة…", size = 12.sp, color = V2Soft)
                    else {
                        KText(formatScanTime(facts.computedAt), size = 12.sp, color = V2Soft)
                        InfoLine("تخزين مستخدم", formatBytes(facts.storage.usedBytes), V2Cyan)
                        InfoLine("حزم ظاهرة", formatCount(facts.installedCount), V2Violet)
                        InfoLine("كاش KNOUX فقط", formatBytes(facts.ownCacheBytes), V2Cyan)
                        InfoLine("بطارية", facts.battery.levelPercent?.let { "$it%" } ?: "غير مقروءة", V2Green)
                        InfoLine("وسائط", if (facts.media.permissionGranted) "ضمن النطاق المصرح" else "تحتاج إذناً", V2Amber)
                    }
                }
            }
        }
        item {
            ScopedNotice(if (measured == null) "لا توجد نتيجة تنظيف مقيسة في سجل العمليات. حجم الكاش أعلاه ليس مساحة محررة."
            else "آخر عملية مسجلة (${lastMeasuredOperation?.serviceId}): ${lastMeasuredOperation?.status}\nبايتات الحذف المسجلة: ${formatBytes(measured.measuredBytes)} · ${formatScanTime(lastMeasuredOperation!!.createdAt)}\nهذه نتيجة العملية المسجلة، وليست نتيجة الفحص العميق.", V2Green)
        }
        item { V2GhostButton("تحديث القراءات", bump, icon = Icons.Rounded.Refresh, height = 48.dp) }
        item { V2GhostButton("إعدادات التخزين", { openStorageSettings(context) }, height = 48.dp) }
        item { ScopedNotice("الفحص تشخيصي ولا يحذف ملفات. التنظيف يحتاج مراجعتك وتأكيدك؛ لا نسبة اكتمال أو مساحة محررة دون قياس.") }
    }
}

@Composable
private fun SummaryModuleRow(icon: ImageVector, title: String, subtitle: String, value: String, accent: Color) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 10.dp, radius = 16.dp, glow = accent, borderAlpha = .26f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(icon, accent, size = 35.dp, iconSize = 17.dp)
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                KText(title, size = 9.8.sp, weight = FontWeight.Bold)
                KText(subtitle, size = 9.sp, color = V2Muted)
            }
            KText(value, size = 9.sp, weight = FontWeight.Bold, color = accent)
        }
    }
}

/* ------------------------------------------------------------------ *
 * Residue review
 * ------------------------------------------------------------------ */

@Composable
fun V2ResidueReviewScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val (refresh, bump) = rememberRefreshCounter()
    val facts = rememberDeviceFacts(refresh)
    InteriorScaffold(onBack, onPro, bottomBar = {
        V2GradientButton("إدارة التخزين في Android", { openStorageSettings(context) }, icon = Icons.Rounded.Storage, height = 48.dp)
    }) {
        item { PageHeading("مراجعة البقايا", "حدود Android ونطاق كاش KNOUX", Icons.Rounded.Folder, V2Violet) }
        item { ScopedNotice("لا يتيح Android قراءة Android/data للتطبيقات الأخرى أو إثبات أن مجلداتها بقايا آمنة للحذف. لا توجد قائمة بقايا مكتشفة ولا إجراء حذف لهذه المجلدات.", V2Amber) }
        item {
            ScopedNotice(if (facts.computedAt == 0L) "جاري قياس كاش KNOUX…"
            else "كاش KNOUX المقروء: ${formatBytes(facts.ownCacheBytes)} · ${formatCount(facts.ownCacheFiles)} ملفاً.\nهذا كاش التطبيق نفسه، وليس بقايا تطبيقات محذوفة.")
        }
        item { V2GhostButton("معلومات KNOUX وإدارة الكاش", { openAppInfo(context, context.packageName) }, icon = Icons.Rounded.Settings, height = 48.dp) }
        item { V2GhostButton("تحديث القياس", bump, icon = Icons.Rounded.Refresh, height = 48.dp) }
        item { ScopedNotice("إدارة الكاش من شاشة النظام تحتاج قرارك هناك. فتحها لا يسجل نجاح حذف أو مساحة مستعادة.") }
    }
}

/* ------------------------------------------------------------------ *
 * Privacy risk inspector
 * ------------------------------------------------------------------ */

@Composable
fun V2PrivacyRiskScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val (refresh, bump) = rememberRefreshCounter()
    val facts = rememberDeviceFacts(refresh)
    var actionError by remember { mutableStateOf<String?>(null) }
    InteriorScaffold(onBack, onPro, bottomBar = {
        V2GradientButton("إعدادات الخصوصية في Android", { openPrivacySettings(context) }, icon = Icons.Rounded.Security, height = 48.dp)
    }) {
        item { PageHeading("مراجعة الأذونات الحساسة", "PackageManager؛ لا كشف تسجيل أو تتبع", Icons.Rounded.Shield, V2Pink) }
        item { ScopedNotice("الإذن المعلن أو الممنوح ليس دليلاً على تشغيل الكاميرا أو التسجيل أو التتبع. لا يملك KNOUX سجلاً لاستخدام الحساسات في الخلفية؛ راجع لوحة خصوصية Android.", V2Amber) }
        actionError?.let { message -> item { ScopedNotice(message, V2Amber) } }
        if (facts.computedAt == 0L) item { ScopedNotice("جاري قراءة الأذونات…") }
        facts.privacyGroups.forEach { group ->
            item {
                ScopedNotice("${group.label}: ${formatCount(group.appCount)} حزمة تعلن الإذن؛ ${formatCount(group.grantedCount)} حزمة يظهر الإذن ممنوحاً لها ضمن الحزم الظاهرة.", V2Violet)
            }
            group.packages.forEach { pkg ->
                val app = facts.installedApps.firstOrNull { it.packageName == pkg }
                item {
                    V2GlassCard(Modifier.fillMaxWidth().heightIn(min = 48.dp).clickable {
                        actionError = showInstalledAppInfo(context, pkg)
                    }, padding = 12.dp, radius = 16.dp, glow = V2Cyan) {
                        Column {
                            KText(app?.label ?: pkg, size = 14.sp, weight = FontWeight.Bold)
                            KText(pkg, size = 12.sp, color = V2Soft)
                            KText(if (pkg in group.grantedPackages) "منح ظاهر للنظام · افتح لمراجعته" else "إذن معلن؛ لم يثبت المنح · افتح لمراجعته", size = 12.sp, color = V2Cyan)
                        }
                    }
                }
            }
        }
        if (facts.computedAt > 0L && facts.privacyGroups.all { it.appCount == 0 }) {
            item { ScopedNotice("لا أذونات حساسة في الحزم الظاهرة. هذا لا يثبت خلو الجهاز من مخاطر الخصوصية.") }
        }
        item { V2GhostButton("تحديث بعد تغيير الأذونات", bump, icon = Icons.Rounded.Refresh, height = 48.dp) }
    }
}

@Composable
private fun PrivacyRiskDetailRow(icon: ImageVector, title: String, subtitle: String, risk: String, accent: Color) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 10.dp, radius = 16.dp, glow = accent, borderAlpha = .28f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(icon, accent, size = 35.dp, iconSize = 17.dp)
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                KText(title, size = 9.8.sp, weight = FontWeight.Bold)
                KText(subtitle, size = 9.sp, color = V2Muted, maxLines = 1)
            }
            SummaryPill(risk, accent)
        }
    }
}

/* ------------------------------------------------------------------ *
 * Duplicate group review
 * ------------------------------------------------------------------ */

@Composable
fun V2DuplicateGroupScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val selected = remember { mutableStateOf(setOf(1, 2, 3)) }
    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
        bottomBar = {
            V2GradientButton(
                "حذف ${selected.value.size} نسخ مكررة (9.2 MB)",
                {},
                icon = Icons.Rounded.Delete,
                trailingChevron = true,
            )
        },
    ) {
        item { PageHeading("مراجعة مجموعة مكررة", "4 نسخ من الصورة نفسها", Icons.Rounded.PhotoLibrary, V2Violet) }
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = {
                        selected.value = if (selected.value.size >= 3) emptySet() else setOf(1, 2, 3)
                    },
                    modifier = Modifier.heightIn(min = 48.dp),
                ) {
                    KText(
                        if (selected.value.size >= 3) "إلغاء تحديد الكل" else "تحديد الكل",
                        size = 13.sp,
                        weight = FontWeight.Bold,
                        color = V2Cyan,
                    )
                }
            }
        }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 22.dp, glow = V2Violet, borderAlpha = .48f) {
                Column {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        repeat(4) { index ->
                            val checked = selected.value.contains(index)
                            Box(Modifier.weight(1f).height(104.dp)) {
                                MiniMountainThumb(
                                    Modifier.fillMaxSize(),
                                    if (index == 0) V2Amber else V2Violet,
                                    showCity = index % 2 == 0,
                                )
                                if (index == 0) {
                                    Box(
                                        Modifier
                                            .align(Alignment.TopStart)
                                            .padding(4.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(Color.Black.withAlpha( .55f))
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            CrownGold(size = 11.dp)
                                            Spacer(Modifier.width(2.dp))
                                            KText("الأفضل", size = 9.sp, weight = FontWeight.Bold, color = V2GoldText)
                                        }
                                    }
                                }
                                V2Check(
                                    checked = checked,
                                    accent = V2Pink,
                                    onToggle = {
                                        selected.value = if (checked) selected.value - index else selected.value + index
                                    },
                                    modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp),
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(9.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.PhotoLibrary, null, tint = V2Cyan, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(5.dp))
                        KText("IMG_2026_0451.jpg · 4.8 MB", size = 9.sp, color = V2Soft)
                        Spacer(Modifier.weight(1f))
                        KText("3 محددة", size = 9.sp, weight = FontWeight.Bold, color = V2Pink)
                    }
                }
            }
        }
        item { V2SectionLabel("تفاصيل المجموعة") }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 17.dp, glow = V2Cyan, borderAlpha = .24f) {
                Column {
                    InfoLine("نوع الملف", "صورة JPEG", Color.White)
                    HairLine()
                    InfoLine("الأبعاد", "4000 × 3000", Color.White)
                    HairLine()
                    InfoLine("تاريخ الالتقاط", "12 مايو 2026", Color.White)
                    HairLine()
                    InfoLine("المجلد", "DCIM/Camera", Color.White)
                }
            }
        }
        item { V2SectionLabel("النسخ في المجموعة", trailing = "أصل واحد · 3 نسخ") }
        items(4) { index ->
            val checked = selected.value.contains(index)
            V2GlassCard(
                Modifier.fillMaxWidth().clickable {
                    selected.value = if (checked) selected.value - index else selected.value + index
                },
                padding = 8.dp,
                radius = 14.dp,
                glow = if (index == 0) V2Amber else V2Violet,
                borderAlpha = if (checked) .46f else .18f,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    V2IconTile(
                        Icons.Rounded.PhotoLibrary,
                        if (index == 0) V2Amber else V2Violet,
                        size = 32.dp,
                        iconSize = 15.dp,
                    )
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        KText(
                            if (index == 0) "IMG_2026_0451.jpg" else "IMG_2026_0451 ($index).jpg",
                            size = 9.sp,
                            weight = FontWeight.SemiBold,
                            maxLines = 1,
                        )
                        KText(
                            if (index == 0) "النسخة الأصلية · 4.8 MB" else "نسخة مطابقة · 4.4 MB",
                            size = 9.sp,
                            color = V2Muted,
                        )
                    }
                    if (index == 0) {
                        SummaryPill("الأصل", V2Amber)
                        Spacer(Modifier.width(6.dp))
                    }
                    V2Check(checked = checked, accent = V2Pink)
                }
            }
        }
        item { PreviewNote("اختيار النسخة الأفضل إرشادي فقط؛ راجع الصور قبل الحذف.") }
    }
}

/* ------------------------------------------------------------------ *
 * Storage breakdown
 * ------------------------------------------------------------------ */

@Composable
fun V2StorageBreakdownScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val (refresh, bump) = rememberRefreshCounter()
    val facts = rememberDeviceFacts(refresh)
    val storage = facts.storage
    val mediaGranted = facts.media.permissionGranted
    val images = if (mediaGranted) facts.media.imagesBytes else 0L
    val videos = if (mediaGranted) facts.media.videosBytes else 0L
    val cache = facts.ownCacheBytes
    val total = storage.totalBytes.coerceAtLeast(1L)
    val others = (storage.usedBytes - images - videos - cache).coerceAtLeast(0L)

    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
        bottomBar = {
            V2GradientButton(
                "إعدادات التخزين في Android",
                { openStorageSettings(context) },
                icon = Icons.Rounded.Storage,
                trailingChevron = true,
            )
        },
    ) {
        item { PageHeading("تفصيل التخزين", "قراءة مباشرة من StatFs وMediaStore", Icons.Rounded.Storage, V2Cyan) }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Violet, borderAlpha = .40f) {
                Column {
                    SegmentedBar(
                        listOf(
                            (images.toFloat() / total).coerceIn(0f, 1f) to V2Pink,
                            (videos.toFloat() / total).coerceIn(0f, 1f) to V2Violet,
                            (cache.toFloat() / total).coerceIn(0f, 1f) to V2Cyan,
                            (others.toFloat() / total).coerceIn(0f, 1f) to V2Amber,
                        ),
                        height = 11,
                    )
                    Spacer(Modifier.height(9.dp))
                    KText(
                        if (storage.totalBytes > 0L) "${formatBytes(storage.usedBytes)} مستخدمة من ${formatBytes(storage.totalBytes)}"
                        else "جاري قراءة StatFs…",
                        size = 9.sp,
                        color = V2Soft,
                    )
                }
            }
        }
        item { V2SectionLabel("الفئات المقروءة", trailing = "من الجهاز") }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Cyan, borderAlpha = .28f) {
                Column {
                    RealStorageRow("الصور", if (mediaGranted) formatBytes(images) else "بعد المنح", (images.toFloat() / total).coerceIn(0f, 1f), V2Pink)
                    Spacer(Modifier.height(8.dp))
                    RealStorageRow("الفيديو", if (mediaGranted) formatBytes(videos) else "بعد المنح", (videos.toFloat() / total).coerceIn(0f, 1f), V2Violet)
                    Spacer(Modifier.height(8.dp))
                    RealStorageRow("كاش KNOUX", formatBytes(cache), (cache.toFloat() / total).coerceIn(0f, 1f), V2Cyan)
                    Spacer(Modifier.height(8.dp))
                    RealStorageRow("النظام والتطبيقات", formatBytes(others), (others.toFloat() / total).coerceIn(0f, 1f), V2Amber)
                }
            }
        }
        item { V2SectionLabel("أكبر الملفات", trailing = if (mediaGranted) "MediaStore" else "بعد المنح") }
        if (!mediaGranted) {
            item {
                ServiceCapabilityCard(
                    title = "صلاحية الصور والفيديو مطلوبة",
                    body = "لا يقرأ KNOUX مكتبتك قبل موافقتك. امنح الصلاحية من إعدادات التطبيق ثم حدّث القراءة.",
                    state = ServiceState.PERMISSION,
                    actionLabel = "فتح صلاحيات التطبيق",
                    onAction = { openPermissionSettings(context, context.packageName) },
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
            facts.media.largestFiles.take(6).forEach { file ->
                item {
                    LoadingFileRow(
                        title = file.name,
                        subtitle = file.bucket ?: if (file.isVideo) "فيديو" else "صورة",
                        size = formatBytes(file.sizeBytes),
                        accent = if (file.isVideo) V2Pink else V2Violet,
                    )
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = bump) { androidx.compose.material3.Text("تحديث القراءة", color = V2Cyan) }
            }
        }
        item {
            PreviewNote(
                "التفصيل يعرض الفئات التي يوفّرها Android فعليًا. تحليل كل مجلد على حدة يحتاج صلاحية " +
                    "Scoped Storage خاصة، ولا تُعرض أرقام تقديرية بدلًا منها."
            )
        }
    }
}

@Composable
private fun FolderBreakdownRow(title: String, size: String, fraction: Float, accent: Color) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 10.dp, radius = 15.dp, glow = accent, borderAlpha = .24f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Folder, null, tint = if (LocalDayMode.current) dayAccent(accent) else accent, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    KText(title, size = 9.6.sp, weight = FontWeight.SemiBold)
                    Spacer(Modifier.weight(1f))
                    KText(size, size = 9.2.sp, weight = FontWeight.Bold, color = Color.White)
                }
                Spacer(Modifier.height(4.dp))
                V2ProgressBar(fraction, height = 4.dp, colors = listOf(accent, accent.withAlpha( .50f)))
            }
        }
    }
}

/* ------------------------------------------------------------------ *
 * Battery diagnostics
 * ------------------------------------------------------------------ */

@Composable
fun V2BatteryDiagnosticsScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val (refresh, bump) = rememberRefreshCounter()
    val facts = rememberDeviceFacts(refresh)
    var saving by remember { mutableStateOf<Boolean?>(null) }
    val database = remember(context) { AppDatabase.getDatabase(context.applicationContext) }
    val samples by remember(database) { database.batterySampleDao().getRecent(12) }.collectAsState(initial = emptyList())
    LaunchedEffect(refresh) { saving = readPowerSaveMode(context) }
    // Re-read the state when returning from Android's settings, not just on first composition.
    val owner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(owner, context) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                saving = readPowerSaveMode(context)
                bump()
            }
        }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer) }
    }
    val battery = facts.battery
    InteriorScaffold(onBack, onPro, bottomBar = {
        V2GradientButton("إعدادات توفير الطاقة", { openBatterySaverSettings(context) }, icon = Icons.Rounded.BatteryFull, height = 48.dp)
    }) {
        item { PageHeading("تشخيص البطارية", "قراءات النظام الحالية وعينات Room", Icons.Rounded.Thermostat, V2Green) }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 14.dp, radius = 22.dp, glow = V2Green) {
                Column {
                    KText(battery.levelPercent?.let { "$it%" } ?: "المستوى غير مقروء", size = 24.sp, weight = FontWeight.Bold, color = V2Green)
                    InfoLine("حالة الشحن", battery.statusLabel, V2Cyan)
                    InfoLine("صحة النظام (وصف)", battery.healthLabel, V2Green)
                    InfoLine("الحرارة الحالية", battery.temperatureC?.let { String.format(java.util.Locale.US, "%.1f°C", it) } ?: "غير مقروءة", V2Amber)
                    InfoLine("الجهد", battery.voltageMv?.let { "$it mV" } ?: "غير مقروء", V2Cyan)
                    InfoLine("التقنية", battery.technology ?: "غير معلنة", V2Violet)
                    InfoLine("توفير الطاقة", powerSaveLabel(saving), V2Green)
                }
            }
        }
        item { ScopedNotice("نسبة صحة الخلايا والسعة التصميمية ودورات الشحن وزمن الشحن السريع غير مقاسة هنا. وصف صحة Android ليس نسبة سعة متبقية؛ لا نستنتج أحداث حرارة سابقة من القراءة الحالية.", V2Amber) }
        item { KText("عينات البطارية المحفوظة في Room", size = 16.sp, weight = FontWeight.Bold) }
        if (samples.isEmpty()) item { ScopedNotice("لا توجد عينات بطارية محفوظة. لا يوجد رسم زمني أو تشخيص آخر 24 ساعة دون قياسات.") }
        samples.sortedByDescending { it.recordedAt }.forEach { sample ->
            item {
                ScopedNotice("${formatScanTime(sample.recordedAt)} · ${sample.levelPercent?.let { "$it%" } ?: "مستوى غير مقروء"} · ${sample.temperatureC?.let { String.format(java.util.Locale.US, "%.1f°C", it) } ?: "حرارة غير مقروءة"}", V2Violet)
            }
        }
        item { V2GhostButton("تحديث القراءات", bump, icon = Icons.Rounded.Refresh, height = 48.dp) }
        item { V2GhostButton("استهلاك البطارية في Android", { openBatterySettings(context) }, height = 48.dp) }
    }
}

@Composable
private fun MiniDrainChart() {
    Canvas(Modifier.fillMaxWidth().height(72.dp)) {
        val points = listOf(.82f, .78f, .74f, .70f, .66f, .62f, .58f, .55f, .52f, .48f, .45f, .42f)
        val step = size.width / (points.size - 1)
        var previous = Offset(0f, size.height * (1f - points.first()))
        points.forEachIndexed { index, value ->
            val current = Offset(index * step, size.height * (1f - value))
            drawLine(
                brush = Brush.horizontalGradient(listOf(V2Green, V2Cyan, V2Pink)),
                start = previous,
                end = current,
                strokeWidth = 2.dp.toPx(),
            )
            drawCircle(V2Cyan, radius = 1.8.dp.toPx(), center = current)
            previous = current
        }
    }
}

@Composable
private fun DiagnosticNote(icon: ImageVector, title: String, subtitle: String, accent: Color) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 10.dp, radius = 16.dp, glow = accent, borderAlpha = .26f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                KText(title, size = 9.6.sp, weight = FontWeight.Bold)
                KText(subtitle, size = 9.sp, color = V2Soft)
            }
        }
    }
}

/* ------------------------------------------------------------------ *
 * AI recommendations detail
 * ------------------------------------------------------------------ */

@Composable
fun V2AiRecommendationsScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val navigate = LocalServiceNavigate.current
    val (refresh, bump) = rememberRefreshCounter()
    val facts = rememberDeviceFacts(refresh)
    val recommendations = remember(facts) { buildLiveRecommendations(facts) }

    fun run(key: String) {
        when (key) {
            "cache" -> navigate("smartclean")
            "storage" -> navigate("storage")
            "duplicates" -> navigate("duplicates")
            "privacy" -> navigate("privacy")
            "battery" -> openBatterySaverSettings(context)
            "usage" -> openUsageAccessSettings(context)
            "apps" -> navigate("appmanager")
        }
    }

    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
        bottomBar = {
            V2GradientButton(
                "تحديث التوصيات",
                bump,
                icon = Icons.Rounded.Refresh,
                trailingChevron = true,
            )
        },
    ) {
        item { PageHeading("كل التوصيات", "مبنية على قراءة هذا الهاتف فقط", Icons.Rounded.AutoAwesome, V2Pink) }
        if (recommendations.isEmpty()) {
            item {
                ServiceCapabilityCard(
                    title = "لا توصيات حالية",
                    body = "كل القراءات المتاحة ضمن النطاق الطبيعي. تُحدَّث القائمة مع كل قراءة جديدة.",
                    state = ServiceState.LIVE,
                )
            }
        }
        items(recommendations.size) { index ->
            val action = recommendations[index]
            LiveRecommendationRow(action) { run(action.key) }
        }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 11.dp, radius = 17.dp, glow = V2Violet, borderAlpha = .26f) {
                Column {
                    KText("كيف تُحسب التوصيات؟", size = 9.6.sp, weight = FontWeight.Bold)
                    Spacer(Modifier.height(3.dp))
                    KText(
                        "من التخزين الفعلي، وحجم كاش KNOUX، ومجموعات MediaStore، وأذونات الحزم، وحالة البطارية. " +
                            "لا يوجد تنفيذ تلقائي؛ كل إجراء يفتح الشاشة أو إعدادات Android المختصة.",
                        size = 9.sp,
                        lineHeight = 12.sp,
                        color = V2Soft,
                    )
                }
            }
        }
    }
}

/* ------------------------------------------------------------------ *
 * Performance report detail
 * ------------------------------------------------------------------ */

@Composable
fun V2PerformanceReportScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val navigate = LocalServiceNavigate.current
    val (refresh, bump) = rememberRefreshCounter()
    val facts = rememberDeviceFacts(refresh)
    val history = remember(refresh) { readScanHistory(context) }
    val latest = history.firstOrNull()

    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
        bottomBar = {
            V2GradientButton("تحديث القراءة", bump, icon = Icons.Rounded.Refresh, trailingChevron = true)
        },
    ) {
        item { PageHeading("تقرير الأداء", "من سجل القياسات على هذا الهاتف", Icons.Rounded.Report, V2Cyan) }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 13.dp, radius = 22.dp, glow = V2Cyan, borderAlpha = .50f) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    V2NeonRing(
                        progress = facts.healthEstimate / 100f,
                        size = 100.dp,
                        stroke = 8.dp,
                        colors = listOf(V2Pink, V2Violet, V2Blue, V2Cyan),
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            KText(if (facts.healthEstimate > 0) "${facts.healthEstimate}" else "—", size = 26.sp, weight = FontWeight.Black)
                            KText("نقطة", size = 9.sp, color = V2Soft)
                        }
                    }
                    Spacer(Modifier.width(13.dp))
                    Column(Modifier.weight(1f)) {
                        KText("قراءة اللحظة", size = 15.sp, weight = FontWeight.Black, color = V2Cyan)
                        KText("محسوبة من التخزين والبطارية والأذونات", size = 9.sp, color = V2Soft)
                        Spacer(Modifier.height(6.dp))
                        KText(
                            "${formatCount(facts.installedCount)} تطبيق · ${formatBytes(facts.storage.freeBytes)} متاحة · " +
                                (facts.battery.levelPercent?.let { "بطارية $it%" } ?: "بطارية غير متاحة"),
                            size = 9.sp,
                            lineHeight = 11.sp,
                            color = V2Muted,
                        )
                    }
                }
            }
        }
        item {
            V2SectionLabel(
                "القياسات المحفوظة",
                trailing = if (latest != null) "${history.size}/24 · آخرها ${formatScanTime(latest.timestamp)}" else "0/24",
            )
        }
        if (history.isEmpty()) {
            item {
                ServiceCapabilityCard(
                    title = "لا قياسات محفوظة بعد",
                    body = "شغّل الفحص العميق ليُحفظ أول قياس محلي. لا تُعرض أرقام تقديرية بدلًا من السجل.",
                    state = ServiceState.LIVE,
                    actionLabel = "فتح الفحص العميق",
                    onAction = { navigate("deepscan") },
                )
            }
        } else {
            history.take(8).forEach { entry ->
                item { PerformanceHistoryRow(entry) }
            }
        }
        item {
            PreviewNote(
                "الاتجاه الأسبوعي غير معروض لأن الجهاز لا يملك بعد قراءات كافية على أيام مختلفة. " +
                    "السجل محلي بالكامل ويُقصر على 24 قراءة."
            )
        }
    }
}

@Composable
private fun PerformanceHistoryRow(entry: ScanHistoryEntry) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 10.dp, radius = 16.dp, glow = V2Cyan, borderAlpha = .24f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(Icons.Rounded.Timeline, V2Cyan, size = 34.dp, iconSize = 17.dp)
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                KText(formatScanTime(entry.timestamp), size = 9.4.sp, weight = FontWeight.SemiBold)
                KText(
                    "${formatCount(entry.appsScanned)} تطبيق · ${formatBytes(entry.storageUsedBytes)} مستخدم" +
                        (entry.batteryLevel?.let { " · بطارية $it%" } ?: ""),
                    size = 9.sp,
                    color = V2Soft,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                KText(formatBytes(entry.cacheBytes), size = 9.sp, weight = FontWeight.Bold, color = V2Pink)
                KText("كاش KNOUX", size = 9.sp, color = V2Muted)
            }
        }
    }
}

@Composable
private fun ReportDetailRow(title: String, value: String, icon: ImageVector, accent: Color) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 9.dp, radius = 15.dp, glow = accent, borderAlpha = .24f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            V2IconTile(icon, accent, size = 33.dp, iconSize = 16.dp)
            Spacer(Modifier.width(9.dp))
            KText(title, size = 9.4.sp, weight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            KText(value, size = 9.4.sp, weight = FontWeight.Bold, color = accent)
        }
    }
}

/* ------------------------------------------------------------------ *
 * Developer Panel (5-tap on version)
 * ------------------------------------------------------------------ */

@Composable
private fun DeveloperPanel(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val previewEnabled = KnouxProState.previewEnabled
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.withAlpha( .75f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        V2GlassCard(
            Modifier
                .width(340.dp)
                .padding(20.dp)
                .clickable(onClick = {}),
            padding = 20.dp,
            radius = 24.dp,
            glow = V2Amber,
            borderAlpha = .65f,
        ) {
            Column(Modifier.width(300.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    CrownGold(size = 28.dp, color = V2Amber)
                    Spacer(Modifier.width(8.dp))
                    KText("لوحة المطور", size = 18.sp, weight = FontWeight.Black, color = V2Amber)
                    Spacer(Modifier.weight(1f))
                    // 40dp hit area around the 20dp close glyph.
                    Box(
                        Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.Close, null, tint = V2Soft, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(Modifier.height(16.dp))
                Box(
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.withAlpha( .3f))
                        .border(1.dp, V2Amber.withAlpha( .3f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                ) {
                    val proStatusText = if (previewEnabled) "نشطة" else "متوقفة"
                    val proStatusColor = if (previewEnabled) V2Cyan else V2Soft
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        DevRow("معاينة Pro", proStatusText, proStatusColor)
                        DevRow("إصدار التطبيق", "1.0.0 (build 2)", V2Cyan)
                        DevRow("نموذج التصميم", "V2 (Arabic-first)", V2Violet)
                        DevRow("الخط", "KnouxArabic", V2Pink)
                        DividerLine()
                        if (com.knoux.ai.BuildConfig.DEBUG) {
                            DevRow("تبديل معاينة Pro", "", V2Amber, action = {
                                KnouxProState.setPreviewEnabled(context, !previewEnabled)
                                onDismiss()
                            })
                        }
                        DevRow("إعادة تعيين Onboarding", "", V2Pink, action = {
                            markKnouxOnboardingSeen(context, false)
                            onDismiss()
                        })
                        DevRow("إعادة تعيين جميع الإعدادات", "", V2Red, action = {
                            // Reset all SharedPreferences
                            context.applicationContext.getSharedPreferences("knoux_pro_v1", Context.MODE_PRIVATE).edit().clear().commit()
                            context.applicationContext.getSharedPreferences("knoux_prefs", Context.MODE_PRIVATE).edit().clear().commit()
                            onDismiss()
                        })
                    }
                }
                Spacer(Modifier.height(8.dp))
                KText(
                    "اضغط على الإصدار 5 مرات لفتح هذه اللوحة",
                    size = 9.sp,
                    color = V2Muted,
                    align = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun DevRow(label: String, value: String, accent: Color, action: (() -> Unit)? = null) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .then(if (action != null) Modifier
                .fillMaxWidth()
                .clickable(onClick = action)
                .background(Color.White.withAlpha( .03f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            else Modifier.fillMaxWidth().padding(vertical = 6.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        KText(label, size = 9.sp, color = V2Soft, weight = FontWeight.Medium)
        Spacer(Modifier.weight(1f))
        if (value.isNotBlank()) {
            KText(value, size = 9.sp, weight = FontWeight.Bold, color = accent)
        }
        if (action != null) {
            Icon(Icons.Rounded.ChevronLeft, null, tint = accent, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun DividerLine() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(Color.White.withAlpha( .1f)))
}

/* ------------------------------------------------------------------ *
 * Large Files
 * ------------------------------------------------------------------ */

@Composable
fun V2LargeFilesScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val (refresh, bump) = rememberRefreshCounter()
    val facts = rememberDeviceFacts(refresh)
    var message by remember { mutableStateOf<String?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        message = if (it.values.any { granted -> granted }) null else "لم يُمنح إذن الوسائط؛ تبقى قراءة التخزين متاحة ويمكن اختيار ملف من النظام."
        bump()
    }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        message = if (uri == null) "أُلغي اختيار الملف؛ لم يتغير شيء" else viewGrantedDocument(context, uri)
    }
    var refreshing by remember { mutableStateOf(false) }
    LaunchedEffect(facts.computedAt) { if (refreshing) refreshing = false }
    InteriorScaffold(onBack, onPro, bottomBar = {
        V2GradientButton("اختيار ملف وفتحه", { picker.launch(arrayOf("*/*")) }, icon = Icons.Rounded.OpenInNew, height = 48.dp)
    }) {
        item { PageHeading("الملفات الكبيرة", "أكبر وسائط في القراءة المتاحة فقط", Icons.Rounded.Storage, V2Amber) }
        item {
            // The universal K orb: the only way this service starts.
            KnouxRunOrbBlock(
                progress = when {
                    refreshing -> null
                    facts.computedAt > 0L -> 1f
                    else -> 0f
                },
                running = refreshing,
                enabled = !refreshing,
                label = when {
                    refreshing -> "جارٍ قراءة وسائط الجهاز…"
                    facts.computedAt > 0L -> "اضغط لإعادة قراءة الوسائط"
                    else -> "اضغط لبدء قراءة الوسائط"
                },
                accent = V2Amber,
                accent2 = V2Violet,
                onTap = {
                    if (!refreshing) {
                        refreshing = true
                        bump()
                    }
                },
            )
        }
        item { ScopedNotice("تعرض القائمة حتى 8 وسائط من فهرس الصور والفيديو المصرح به، وليست مسحاً لجميع الملفات. اضغط لفتح العارض الرسمي. الحذف العام غير مدعوم هنا؛ لا وعد باستعادة أو مساحة محررة.") }
        message?.let { item { ScopedNotice(it, V2Amber) } }
        if (facts.computedAt == 0L) item { ScopedNotice("جاري قراءة الوسائط…") }
        if (!mediaReadPermissions().all { context.checkSelfPermission(it) == android.content.pm.PackageManager.PERMISSION_GRANTED }) {
            item { V2GhostButton("طلب أذونات الصور والفيديو", { permissionLauncher.launch(mediaReadPermissions()) }, height = 48.dp) }
        }
        if (facts.computedAt > 0L && facts.media.largestFiles.isEmpty()) {
            item { ScopedNotice(if (facts.media.permissionGranted) "لم تُعد القراءة ملفات قابلة للعرض؛ قد تكون المكتبة فارغة أو الاستعلام غير متاح. اختر ملفاً من النظام أو أعد القراءة." else "لا توجد وسائط مقروءة دون إذنك؛ يمكنك استخدام منتقي ملفات النظام.", V2Amber) }
        }
        facts.media.largestFiles.sortedByDescending { it.sizeBytes }.forEach { file ->
            item {
                ScopedDocumentRow(file.name, formatBytes(file.sizeBytes), file.bucket ?: if (file.isVideo) "فيديو" else "صورة", false) {
                    message = viewGrantedDocument(context, Uri.parse(file.uriString))
                }
            }
        }
    }
}

/* ------------------------------------------------------------------ *
 * Old Backups
 * ------------------------------------------------------------------ */

private data class OldBackupV2(val name: String, val date: String, val size: String, val path: String, val accent: Color)

private val oldBackups = listOf(
    OldBackupV2("نسخة نظام 2025", "12 مايو 2025", "1.2 GB", "Android/backup/full_2025", V2Amber),
    OldBackupV2("نسخة تطبيقات واتساب", "3 مارس 2025", "850 MB", "Android/backup/whatsapp", V2Violet),
    OldBackupV2("نسخة صور Google", "1 يناير 2025", "620 MB", "Android/backup/photos", V2Cyan),
    OldBackupV2("نسخة إعدادات النظام", "15 يونيو 2025", "150 MB", "Android/backup/settings", V2Green),
)

@Composable
fun V2OldBackupsScreen(onBack: () -> Unit, onPro: () -> Unit) {
    var selected by remember { mutableStateOf(setOf<Int>()) }
    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
        bottomBar = {
            V2GradientButton(
                "حذف ${selected.size} نسخ احتياطية",
                {},
                icon = Icons.Rounded.Delete,
                trailingChevron = true,
            )
        },
    ) {
        item { PageHeading("النسخ الاحتياطية القديمة", "مراجعة وحذف النسخ غير المستخدمة", Icons.Rounded.Backup, V2Amber) }
        item { V2SectionLabel("النسخ المتاحة", trailing = "${oldBackups.size} نسخة") }
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = {
                        selected = if (selected.size == oldBackups.size) emptySet() else oldBackups.indices.toSet()
                    },
                    modifier = Modifier.heightIn(min = 48.dp),
                ) {
                    KText(
                        if (selected.size == oldBackups.size) "إلغاء تحديد الكل" else "تحديد الكل",
                        size = 13.sp,
                        weight = FontWeight.Bold,
                        color = V2Cyan,
                    )
                }
            }
        }
        items(oldBackups.size) { index ->
            val item = oldBackups[index]
            val checked = selected.contains(index)
            V2GlassCard(
                Modifier.fillMaxWidth().clickable {
                    selected = if (checked) selected - index else selected + index
                },
                padding = 10.dp,
                radius = 16.dp,
                glow = item.accent,
                borderAlpha = if (checked) .50f else .18f,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(38.dp).clip(RoundedCornerShape(12.dp))
                            .background(item.accent.withAlpha(.12f))
                            .border(1.dp, item.accent.withAlpha(.32f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.Backup, null, tint = item.accent, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(9.dp))
                    Column(Modifier.weight(1f)) {
                        KText(item.name, size = 9.4.sp, weight = FontWeight.SemiBold, maxLines = 1)
                        KText("${item.date} · ${item.path}", size = 9.sp, color = V2Muted, maxLines = 1)
                    }
                    KText(item.size, size = 9.4.sp, weight = FontWeight.Bold, color = item.accent)
                    Spacer(Modifier.width(8.dp))
                    V2Check(checked, item.accent, onToggle = { selected = if (checked) selected - index else selected + index })
                }
            }
        }
        item { PreviewNote("حذف النسخ الاحتياطية لا يسترجع المساحة فوراً حتى يتم فراغ سلة المهملات.") }
    }
}

/* ------------------------------------------------------------------ *
 * System Logs
 * ------------------------------------------------------------------ */

private data class SystemLogV2(val app: String, val level: String, val message: String, val time: String, val accent: Color)

private val systemLogs = listOf(
    SystemLogV2("النظام", "معلومات", "تم تشغيل الجهاز بنجاح", "منذ 4 ساعات", V2Cyan),
    SystemLogV2("واتساب", "تحذير", "فشل في مزامنة النسخة الاحتياطية", "منذ 3 ساعات", V2Amber),
    SystemLogV2("التيك توك", "خطأ", "توقف التطبيق بشكل غير متوقع", "منذ ساعتين", V2Red),
    SystemLogV2("المتصفح", "معلومات", "تم تحديث ملفات الكاش", "منذ ساعة", V2Green),
    SystemLogV2("النظام", "معلومات", "تم تثبيت تحديث أمني", "منذ 30 دقيقة", V2Cyan),
    SystemLogV2("اليوتيوب", "تحذير", "استهلاك بيانات مرتفع", "منذ 15 دقيقة", V2Amber),
    SystemLogV2("الصور", "معلومات", "تم نسخ الصور احتياطياً", "منذ 10 دقائق", V2Violet),
    SystemLogV2("النظام", "خطأ", "فشل في قراءة بطاقة الذاكرة", "منذ 5 دقائق", V2Red),
)

@Composable
fun V2SystemLogsScreen(onBack: () -> Unit, onPro: () -> Unit) {
    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
    ) {
        item { PageHeading("سجلات النظام", "عرض سجلات التطبيقات والنظام", Icons.Rounded.Description, V2Cyan) }
        item { V2SectionLabel("آخر السجلات", trailing = "${systemLogs.size} سجل") }
        items(systemLogs.size) { index ->
            val log = systemLogs[index]
            V2GlassCard(
                Modifier.fillMaxWidth(),
                padding = 10.dp,
                radius = 16.dp,
                glow = log.accent,
                borderAlpha = .24f,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                            .background(log.accent.withAlpha(.12f))
                            .border(1.dp, log.accent.withAlpha(.32f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            when (log.level) {
                                "خطأ" -> Icons.Rounded.Report
                                "تحذير" -> Icons.Rounded.WarningAmber
                                else -> Icons.Rounded.Info
                            },
                            null,
                            tint = log.accent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(Modifier.width(9.dp))
                    Column(Modifier.weight(1f)) {
                        KText(log.app, size = 9.4.sp, weight = FontWeight.SemiBold, maxLines = 1)
                        KText(log.message, size = 9.sp, color = V2Soft, maxLines = 1)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        KText(log.time, size = 9.sp, color = V2Muted)
                        Box(
                            Modifier.clip(RoundedCornerShape(50))
                                .background(log.accent.withAlpha(.12f))
                                .border(1.dp, log.accent.withAlpha(.32f), RoundedCornerShape(50))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        ) {
                            KText(log.level, size = 9.sp, weight = FontWeight.Bold, color = log.accent)
                        }
                    }
                }
            }
        }
        item { PreviewNote("السجلات هنا بيانات معاينة حتى تُربط بقراءة النظام الفعلية.") }
    }
}

/* ------------------------------------------------------------------ *
 * Download Cleanup
 * ------------------------------------------------------------------ */

@Composable
fun V2DownloadCleanupScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var files by remember { mutableStateOf(emptyList<ScopedDocument>()) }
    var message by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (uris.isEmpty()) message = "أُلغي الاختيار؛ لم تُحذف ملفات"
        else {
            loading = true
            scope.launch {
                try {
                    files = withContext(Dispatchers.IO) { uris.map { readSelectedDocument(context, it) } }
                    message = "هذه الملفات التي اخترتها فقط؛ ليست قائمة كل التنزيلات."
                } catch (error: Exception) {
                    files = emptyList()
                    message = "تعذّر قراءة الملفات المختارة: ${error.message}"
                } finally { loading = false }
            }
        }
    }
    InteriorScaffold(onBack, onPro, bottomBar = {
        V2GradientButton("اختيار ملفات من التنزيلات", {
            try { picker.launch(arrayOf("*/*")) }
            catch (error: Exception) { message = "منتقي الملفات غير متاح: ${error.message}" }
        }, icon = Icons.Rounded.Download, height = 48.dp)
    }) {
        item { PageHeading("مراجعة التنزيلات", "اختيار صريح عبر منتقي Android", Icons.Rounded.Download, V2Cyan) }
        item { ScopedNotice("اختر ملفات من قسم التنزيلات في منتقي النظام، ثم افتحها في العارض. الحذف العام من KNOUX غير مدعوم هنا؛ لا سلة مهملات أو ضمان استعادة خلال 30 يوماً.", V2Amber) }
        if (loading) item { ScopedNotice("جاري قراءة الملفات المختارة…") }
        message?.let { item { ScopedNotice(it, V2Amber) } }
        if (files.isEmpty() && !loading) item { ScopedNotice("لم تُقرأ ملفات بعد. اضغط اختيار ملفات لمنح وصول إلى الملفات التي تريد مراجعتها فقط.") }
        files.forEach { file ->
            item { ScopedDocumentRow(file.name, file.bytes?.let(::formatBytes) ?: "حجم غير معلن", file.mime, false) {
                message = viewGrantedDocument(context, file.uri)
            } }
        }
    }
}

/* ------------------------------------------------------------------ *
 * File Explorer
 * ------------------------------------------------------------------ */

@Composable
fun V2FileExplorerScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    var tree by remember { mutableStateOf<Uri?>(null) }
    var path by remember { mutableStateOf(emptyList<Pair<String, String>>()) }
    var entries by remember { mutableStateOf(emptyList<ScopedDocument>()) }
    var message by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    val (refresh, bump) = rememberRefreshCounter()
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri == null) message = "أُلغي اختيار المجلد؛ لم يتغير شيء"
        else try {
            tree = uri
            path = listOf(DocumentsContract.getTreeDocumentId(uri) to "المجلد المختار")
            message = null
        } catch (error: Exception) { message = "تعذّر فتح المجلد: ${error.message}" }
    }
    LaunchedEffect(tree, path, refresh) {
        val root = tree ?: return@LaunchedEffect
        val id = path.lastOrNull()?.first ?: return@LaunchedEffect
        loading = true
        entries = emptyList()
        try {
            entries = withContext(Dispatchers.IO) { readScopedFolder(context, root, id) }
            message = null
        } catch (error: Exception) {
            message = "تعذّر قراءة المجلد المصرح به: ${error.message}"
        } finally { loading = false }
    }
    InteriorScaffold(onBack, onPro, bottomBar = {
        V2GradientButton("اختيار مجلد من Android", {
            try { picker.launch(null) }
            catch (error: Exception) { message = "منتقي المجلدات غير متاح: ${error.message}" }
        }, icon = Icons.Rounded.Folder, height = 48.dp)
    }) {
        item { PageHeading("مستكشف الملفات", "تصفح نطاق اخترته بنفسك عبر SAF", Icons.Rounded.Folder, V2Cyan) }
        item { ScopedNotice("الوصول للمجلد المختار فقط خلال هذه الجلسة؛ لا صلاحية تخزين شاملة. قد يمنع Android اختيار جذر التخزين أو Download أو Android/data. لا حذف أو وعد استعادة هنا.", V2Amber) }
        message?.let { item { ScopedNotice(it, V2Amber) } }
        if (tree == null) item { ScopedNotice("اختر مجلداً لقراءة أسمائه الفعلية. لا توجد مجلدات أو أعداد مفترضة.") }
        else {
            item { ScopedNotice(path.joinToString(" / ") { it.second }) }
            if (path.size > 1) item { V2GhostButton("المجلد الأب", { path = path.dropLast(1) }, height = 48.dp) }
            if (loading) item { ScopedNotice("جاري القراءة…") }
            else if (entries.isEmpty() && message == null) item { ScopedNotice("لم يُعد الموفر عناصر في هذا المجلد.") }
            entries.forEach { entry ->
                item { ScopedDocumentRow(entry.name, entry.bytes?.let(::formatBytes) ?: "غير معلن", entry.mime, entry.isDirectory) {
                    if (entry.isDirectory) {
                        path = path + (DocumentsContract.getDocumentId(entry.uri) to entry.name)
                    } else message = viewGrantedDocument(context, entry.uri)
                } }
            }
            item { ScopedNotice("تُعرض حتى 500 نتيجة لكل مجلد؛ ليست أعداداً لكل الجهاز.") }
            item { V2GhostButton("إعادة قراءة المجلد", bump, icon = Icons.Rounded.Refresh, height = 48.dp) }
        }
    }
}

/* ------------------------------------------------------------------ *
 * Device Info
 * ------------------------------------------------------------------ */

@Composable
fun V2DeviceInfoScreen(onBack: () -> Unit, onPro: () -> Unit) {
    val context = LocalContext.current
    val packageInfo = remember { context.packageManager.getPackageInfo(context.packageName, 0) }
    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
    ) {
        item { PageHeading("معلومات الجهاز", "عرض مواصفات الجهاز وإصدار النظام", Icons.Rounded.Android, V2Violet) }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Violet, borderAlpha = .42f) {
                Column {
                    InfoLine("اسم التطبيق", "KNOUX AI Pro", Color.White)
                    HairLine()
                    InfoLine("الإصدار", "${packageInfo.versionName ?: "1.0.0"} (build ${packageInfo.versionCode})", Color.White)
                    HairLine()
                    InfoLine(label = "معرّف الحزمة", value = "com.knoux.ai.pro", accent = Color.White)
                    HairLine()
                    InfoLine("نظام التشغيل", "Android", Color.White)
                    HairLine()
                    InfoLine("واجهة المستخدم", "Compose Material 3", Color.White)
                    HairLine()
                    InfoLine("الاتجاه", "العربية (RTL)", Color.White)
                    HairLine()
                    InfoLine("النموذج الأولي", "V2 (Arabic-first)", Color.White)
                }
            }
        }
        item { PreviewNote("معلومات الجهاز 여기 بيانات معاينة حتى تُربط بقراءة النظام الفعلية.") }
    }
}

/* ------------------------------------------------------------------ *
 * Network Scan
 * ------------------------------------------------------------------ */

@Composable
fun V2NetworkScanScreen(onBack: () -> Unit, onPro: () -> Unit) {
    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
    ) {
        item { PageHeading("فحص الشبكة", "فحص الاتصالات واستهلاك البيانات", Icons.Rounded.Wifi, V2Cyan) }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Cyan, borderAlpha = .52f) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        KText("الشبكة المتصلة", size = 9.6.sp, weight = FontWeight.SemiBold, color = V2Soft)
                        Spacer(Modifier.weight(1f))
                        KText("Wi-Fi · آمن", size = 9.2.sp, weight = FontWeight.Bold, color = V2Green)
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        KText("استهلاك البيانات", size = 9.6.sp, weight = FontWeight.SemiBold, color = V2Soft)
                        Spacer(Modifier.weight(1f))
                        KText("2.4 GB هذا الشهر", size = 9.2.sp, weight = FontWeight.Bold, color = V2Cyan)
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        KText("أكثر التطبيقات استهلاكاً", size = 9.6.sp, weight = FontWeight.SemiBold, color = V2Soft)
                        Spacer(Modifier.weight(1f))
                        KText("اليوتيوب · 45%", size = 9.2.sp, weight = FontWeight.Bold, color = V2Pink)
                    }
                }
            }
        }
        item { V2SectionLabel("تطبيقات استهلاك البيانات", trailing = "آخر 7 أيام") }
        item {
            NetworkRow("اليوتيوب", "45%", V2Pink)
            NetworkRow("التيك توك", "28%", V2Violet)
            NetworkRow("الفيسبوك", "15%", V2Blue)
            NetworkRow("إنستغرام", "12%", V2Pink)
        }
        item { PreviewNote("بيانات الشبكة هنا معاينة تصميمية حتى تُربط بقراءة النظام الفعلية.") }
    }
}

@Composable
private fun NetworkRow(name: String, percent: String, accent: Color) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 8.dp, radius = 12.dp, glow = accent, borderAlpha = .20f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            KText(name, size = 9.sp, weight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            KText(percent, size = 9.sp, weight = FontWeight.Bold, color = accent)
        }
    }
}

/* ------------------------------------------------------------------ *
 * Security Scan
 * ------------------------------------------------------------------ */

@Composable
fun V2SecurityScanScreen(onBack: () -> Unit, onPro: () -> Unit) {
    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
    ) {
        item { PageHeading("فحص الأمان", "فحص الثغرات والمخاطر الأمنية", Icons.Rounded.Shield, V2Pink) }
        item {
            V2GlassCard(Modifier.fillMaxWidth(), padding = 12.dp, radius = 20.dp, glow = V2Pink, borderAlpha = .52f) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        KText("مستوى الأمان", size = 9.6.sp, weight = FontWeight.SemiBold, color = V2Soft)
                        Spacer(Modifier.weight(1f))
                        KText("82%", size = 20.sp, weight = FontWeight.Black, color = V2Green)
                    }
                    Spacer(Modifier.height(8.dp))
                    V2ProgressBar(.82f, height = 5.dp, colors = listOf(V2Pink, V2Violet, V2Cyan))
                }
            }
        }
        item { V2SectionLabel("نتائج الفحص", trailing = "4 فحوصات مكتملة") }
        item {
            SecurityRow("الثغرات المعروفة", "لا توجد ثغرات حرجة", V2Green, Icons.Rounded.Check)
            SecurityRow("التطبيقات الضارة", "لا تطبيقات مشبوهة", V2Green, Icons.Rounded.Check)
            SecurityRow("شبكة Wi-Fi", "شبكة آمنة ومشفرة", V2Green, Icons.Rounded.Check)
            SecurityRow("إعدادات القفل", "قفل الشاشة مفعل", V2Green, Icons.Rounded.Check)
        }
        item { PreviewNote("فحص الأمان هنا بيانات معاينة حتى تُربط بقراءة النظام الفعلية.") }
    }
}

@Composable
private fun SecurityRow(title: String, status: String, accent: Color, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 8.dp, radius = 12.dp, glow = accent, borderAlpha = .20f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            KText(title, size = 9.sp, weight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            KText(status, size = 9.sp, color = V2Soft)
        }
    }
}

/* ------------------------------------------------------------------ *
 * Notification Manager
 * ------------------------------------------------------------------ */

@Composable
fun V2NotificationManagerScreen(onBack: () -> Unit, onPro: () -> Unit) {
    InteriorScaffold(
        onBack = onBack,
        onPro = onPro,
    ) {
        item { PageHeading("إدارة الإشعارات", "التحكم في إشعارات التطبيقات", Icons.Rounded.Notifications, V2Amber) }
        item { V2SectionLabel("التطبيقات التي ترسل إشعارات", trailing = "12 تطبيق") }
        item {
            NotificationRow("واتساب", "4 إشعارات جديدة", V2Green, true)
            NotificationRow("التيك توك", "2 إشعارات جديدة", V2Violet, true)
            NotificationRow("اليوتيوب", "لا إشعارات", V2Pink, false)
            NotificationRow("الفيسبوك", "6 إشعارات جديدة", V2Blue, true)
            NotificationRow("إنستغرام", "لا إشعارات", V2Amber, false)
            NotificationRow("الرسائل", "لا إشعارات", V2Cyan, false)
        }
        item { PreviewNote("إدارة الإشعارات تفتح إعدادات النظام للتطبيقات المحددة.") }
    }
}

@Composable
private fun NotificationRow(name: String, status: String, accent: Color, enabled: Boolean) {
    V2GlassCard(Modifier.fillMaxWidth(), padding = 8.dp, radius = 12.dp, glow = accent, borderAlpha = .20f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                    .background(accent.withAlpha(.12f))
                    .border(1.dp, accent.withAlpha(.32f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.Notifications, null, tint = accent, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.width(9.dp))
            Column(Modifier.weight(1f)) {
                KText(name, size = 9.4.sp, weight = FontWeight.SemiBold, maxLines = 1)
                KText(status, size = 9.sp, color = V2Soft, maxLines = 1)
            }
            Box(
                Modifier.clip(RoundedCornerShape(50))
                    .background(if (enabled) V2Green.withAlpha(.20f) else V2Red.withAlpha(.20f))
                    .border(1.dp, if (enabled) V2Green.withAlpha(.40f) else V2Red.withAlpha(.40f), RoundedCornerShape(50))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                KText(if (enabled) "مفعل" else "معطل", size = 9.sp, weight = FontWeight.Bold, color = if (enabled) V2Green else V2Red)
            }
        }
    }
}
