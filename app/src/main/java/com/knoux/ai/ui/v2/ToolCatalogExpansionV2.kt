package com.knoux.ai.ui.v2

import android.os.BatteryManager
import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AccountTree
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.BatteryFull
import androidx.compose.material.icons.rounded.BatterySaver
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Cached
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Compare
import androidx.compose.material.icons.rounded.CompareArrows
import androidx.compose.material.icons.rounded.ContactPhone
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.DeveloperMode
import androidx.compose.material.icons.rounded.DeviceThermostat
import androidx.compose.material.icons.rounded.ElectricBolt
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderCopy
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.FolderShared
import androidx.compose.material.icons.rounded.FolderZip
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Inventory
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockPerson
import androidx.compose.material.icons.rounded.ManageAccounts
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.PermMedia
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.PhotoSizeSelectLarge
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material.icons.rounded.Power
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Recycling
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.Rule
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Summarize
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material.icons.rounded.TipsAndUpdates
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material.icons.rounded.TrendingDown
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.VideoFile
import androidx.compose.material.icons.rounded.VideoLibrary
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.knoux.ai.BuildConfig
import java.util.Locale
import kotlin.math.abs

/* ------------------------------------------------------------------ *
 * The 150-service expansion.
 *
 * The original catalog ships ten services per section. These forty-five add the
 * missing lenses that the reference service screens rely on: photo/video
 * duplicate views, cache and residue audits, permission spotlights, scan
 * readiness, trends, app scopes, media shares, battery telemetry and the
 * final AI plans. Every value is still read from DeviceFacts.
 * ------------------------------------------------------------------ */

private fun x(
    section: String,
    route: String,
    order: String,
    title: String,
    english: String,
    tagline: String,
    icon: ImageVector,
    accent: Color,
    pro: Boolean = false,
    metric: (DeviceFacts, List<ScanHistoryEntry>) -> String,
    rows: (DeviceFacts, List<ScanHistoryEntry>) -> List<ToolRow>,
    note: String = "",
    list: ToolList = ToolList.NONE,
    behavior: ToolBehavior = ToolBehavior.NONE,
    handoff: ToolHandoff = ToolHandoff.NONE,
    actionRoute: String? = null,
    actionLabel: String? = null,
    initialAppFilter: Int = 0,
): SectionToolDef = SectionToolDef(
    section = section,
    route = route,
    order = order,
    title = title,
    english = english,
    tagline = tagline,
    icon = icon,
    accent = accent,
    proOnly = pro,
    metric = metric,
    rows = rows,
    note = note,
    list = list,
    behavior = behavior,
    handoff = handoff,
    actionRoute = actionRoute,
    actionLabel = actionLabel,
    initialAppFilter = initialAppFilter,
)

private val DeviceFacts.mediaReady: Boolean get() = media.permissionGranted

private fun DeviceFacts.photoGroups() = media.duplicateGroups.filter { !it.isVideo }
private fun DeviceFacts.videoGroups() = media.duplicateGroups.filter { it.isVideo }

private fun DeviceFacts.photoDuplicateCount(): Int =
    photoGroups().sumOf { (it.count - 1).coerceAtLeast(0) }

private fun DeviceFacts.videoDuplicateCount(): Int =
    videoGroups().sumOf { (it.count - 1).coerceAtLeast(0) }

private fun DeviceFacts.allDuplicateCount(): Int =
    media.duplicateGroups.sumOf { (it.count - 1).coerceAtLeast(0) }

private fun DeviceFacts.reclaimableBytes(): Long =
    media.duplicateGroups.sumOf { it.reclaimableBytes }

private fun DeviceFacts.mediaBytes(): Long = media.imagesBytes + media.videosBytes

private fun DeviceFacts.largestGroupCount(): Int =
    media.duplicateGroups.maxOfOrNull { it.count } ?: 0

private fun DeviceFacts.averageGroupBytes(): Long =
    media.duplicateGroups.takeIf { it.isNotEmpty() }?.sumOf { it.sizeBytes }?.div(media.duplicateGroups.size) ?: 0L

private fun DeviceFacts.topBucket(): Pair<String, Long>? = media.largestFiles
    .groupBy { it.bucket?.takeIf(String::isNotBlank) ?: "مجلد غير مسمى" }
    .mapValues { (_, files) -> files.sumOf { it.sizeBytes } }
    .maxByOrNull { it.value }
    ?.toPair()

private fun DeviceFacts.bucketCount(): Int = media.largestFiles
    .mapNotNull { it.bucket?.takeIf(String::isNotBlank) }
    .distinct()
    .size

private fun permitValue(ready: Boolean, value: () -> String, fallback: String = "بعد منح الصلاحية"): String =
    if (ready) value() else fallback

private fun shareOf(part: Long, whole: Long): String =
    if (whole > 0L) "${(part * 100 / whole).toInt()}%" else "—"

private fun signedBytes(value: Long): String =
    (if (value >= 0L) "+" else "−") + formatBytes(abs(value))

private fun List<Long>.averageOrZero(): Long =
    if (isEmpty()) 0L else sum() / size

private fun temperature(facts: DeviceFacts): String =
    facts.battery.temperatureC?.let { String.format(Locale.US, "%.1f°C", it) } ?: "غير متاح"

private fun voltage(facts: DeviceFacts): String =
    facts.battery.voltageMv?.let { "$it mV" } ?: "غير متاح"

private fun powerSource(facts: DeviceFacts): String = when (facts.battery.plugged) {
    BatteryManager.BATTERY_PLUGGED_AC -> "شاحن AC"
    BatteryManager.BATTERY_PLUGGED_USB -> "USB"
    BatteryManager.BATTERY_PLUGGED_WIRELESS -> "شحن لاسلكي"
    else -> "غير موصول"
}

private fun permissionGroup(facts: DeviceFacts, key: String): AppPermissionGroup? =
    facts.privacyGroups.firstOrNull { it.key == key }

private fun protectionScore(facts: DeviceFacts): Int =
    if (facts.installedCount == 0) 0
    else (100 - facts.sensitiveAppCount * 100 / facts.installedCount).coerceIn(0, 100)

private fun declaredPermissionCount(facts: DeviceFacts): Int =
    facts.installedApps.sumOf { it.declaredPermissions.size }

private fun firstHistory(history: List<ScanHistoryEntry>): ScanHistoryEntry? =
    history.minByOrNull { it.timestamp }

private fun lastHistory(history: List<ScanHistoryEntry>): ScanHistoryEntry? =
    history.maxByOrNull { it.timestamp }

private fun captureRows(history: List<ScanHistoryEntry>): List<ToolRow> {
    val first = firstHistory(history)
    val last = lastHistory(history)
    return listOf(
        ToolRow(
            Icons.Rounded.History,
            "عدد القراءات المحفوظة",
            "${formatCount(history.size)} قراءة",
            V2Violet,
        ),
        ToolRow(
            Icons.Rounded.Storage,
            "أول قراءة",
            first?.let { formatBytes(it.storageUsedBytes) } ?: "لا سجل",
            V2Cyan,
        ),
        ToolRow(
            Icons.Rounded.Storage,
            "آخر قراءة",
            last?.let { formatBytes(it.storageUsedBytes) } ?: "لا سجل",
            V2Green,
        ),
        ToolRow(
            Icons.Rounded.Cached,
            "آخر كاش مقاس",
            last?.let { formatBytes(it.cacheBytes) } ?: "لا سجل",
            V2Pink,
        ),
    )
}

internal object SectionToolsExpansion {

    private val duplicates = listOf(
        x(
            "duplicates", "dup-photo-lens", "02.11", "عدسة تكرار الصور", "PHOTO DUPLICATE LENS",
            "كل مجموعات الصور المتطابقة في قراءة واحدة", Icons.Rounded.PhotoLibrary, V2Pink,
            metric = { f, _ -> permitValue(f.mediaReady, { "${formatCount(f.photoGroups().size)} مجموعة صور" }, "صلاحية مطلوبة") },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.Image, "صور مكررة", permitValue(f.mediaReady, { formatCount(f.photoDuplicateCount()) }), V2Pink),
                    ToolRow(Icons.Rounded.Layers, "مجموعات الصور", permitValue(f.mediaReady, { formatCount(f.photoGroups().size) }), V2Violet),
                    ToolRow(Icons.Rounded.Savings, "قابلة للاسترجاع", permitValue(f.mediaReady, { formatBytes(f.photoGroups().sumOf { it.reclaimableBytes }) }), V2Green),
                    ToolRow(Icons.Rounded.Compare, "أكبر مجموعة", permitValue(f.mediaReady, { "${f.photoGroups().maxOfOrNull { it.count } ?: 0} صور" }), V2Amber),
                )
            },
            note = "قراءة الصور تعتمد على MediaStore وبصمة SHA-256 ضمن نطاق الفحص المحدود؛ لا تُعد المكتبة كاملة إن لم تُمنح الصلاحية.",
            list = ToolList.PHOTO_GROUPS,
            actionRoute = "dup-review", actionLabel = "فتح مراجعة الحذف",
        ),
        x(
            "duplicates", "dup-video-lens", "02.12", "عدسة تكرار الفيديو", "VIDEO DUPLICATE LENS",
            "مجموعات الفيديو المتطابقة ومساحتها القابلة للاسترجاع", Icons.Rounded.VideoFile, V2Violet,
            metric = { f, _ -> permitValue(f.mediaReady, { "${formatCount(f.videoGroups().size)} مجموعة فيديو" }, "صلاحية مطلوبة") },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.VideoLibrary, "فيديو مكرر", permitValue(f.mediaReady, { formatCount(f.videoDuplicateCount()) }), V2Pink),
                    ToolRow(Icons.Rounded.Layers, "مجموعات الفيديو", permitValue(f.mediaReady, { formatCount(f.videoGroups().size) }), V2Violet),
                    ToolRow(Icons.Rounded.Savings, "قابلة للاسترجاع", permitValue(f.mediaReady, { formatBytes(f.videoGroups().sumOf { it.reclaimableBytes }) }), V2Green),
                    ToolRow(Icons.Rounded.Compare, "أكبر مجموعة", permitValue(f.mediaReady, { "${f.videoGroups().maxOfOrNull { it.count } ?: 0} ملفات" }), V2Amber),
                )
            },
            note = "الفيديو يُقرأ من المصدر نفسه؛ المجموعات لا تظهر إلا إذا منحت صلاحية قراءة الفيديو.",
            list = ToolList.VIDEO_GROUPS,
            actionRoute = "dup-groups", actionLabel = "فتح مجموعات التكرار",
        ),
        x(
            "duplicates", "dup-library-map", "02.13", "خريطة مكتبة التكرار", "DUPLICATE LIBRARY MAP",
            "توزيع النسخ المكررة بين الصور والفيديو", Icons.Rounded.FolderCopy, V2Cyan,
            metric = { f, _ -> permitValue(f.mediaReady, { "${formatCount(f.media.duplicateGroups.size)} مجموعة" }, "صلاحية مطلوبة") },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.Image, "مجموعات الصور", permitValue(f.mediaReady, { formatCount(f.photoGroups().size) }), V2Pink),
                    ToolRow(Icons.Rounded.VideoLibrary, "مجموعات الفيديو", permitValue(f.mediaReady, { formatCount(f.videoGroups().size) }), V2Violet),
                    ToolRow(Icons.Rounded.Inventory, "النسخ المرشحة", permitValue(f.mediaReady, { formatCount(f.media.duplicateCandidates) }), V2Cyan),
                    ToolRow(Icons.Rounded.Savings, "مساحة قابلة للاسترجاع", permitValue(f.mediaReady, { formatBytes(f.reclaimableBytes()) }), V2Green),
                )
            },
            note = "خريطة القراءة الحالية فقط؛ لا تُضاف أنواع ملفات لا يستعلم عنها MediaStore.",
            actionRoute = "dup-groups", actionLabel = "فتح المجموعات",
        ),
        x(
            "duplicates", "dup-hash-audit", "02.14", "تدقيق بصمة المحتوى", "CONTENT FINGERPRINT AUDIT",
            "ماذا فحص SHA-256 فعليًا في آخر قراءة؟", Icons.Rounded.Fingerprint, V2Amber,
            pro = true,
            metric = { f, _ -> permitValue(f.mediaReady, { formatCount(f.media.duplicateCandidates) + " ملفًا" }, "بدون صلاحية") },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.Layers, "مجموعات مؤكدة", permitValue(f.mediaReady, { formatCount(f.media.duplicateGroups.size) }), V2Violet),
                    ToolRow(Icons.Rounded.Inventory, "ملفات مرشحة", permitValue(f.mediaReady, { formatCount(f.media.duplicateCandidates) }), V2Pink),
                    ToolRow(Icons.Rounded.FilterAlt, "حد الملفات", "256 ملفًا كحد أقصى", V2Cyan),
                    ToolRow(Icons.Rounded.Storage, "حد الحجم", "128 MB إجمالًا · 32 MB للملف", V2Soft),
                )
            },
            note = "البصمة تُحسب على أول 32 MB من كل ملف؛ الملفات الأكبر من ذلك تُستثنى من المطابقة بدل ادّعاء فحصها.",
            actionRoute = "dup-scan", actionLabel = "فتح فحص المطابقة",
        ),
        x(
            "duplicates", "dup-reclaim-lens", "02.15", "عدسة المساحة القابلة للاسترجاع", "RECLAIM LENS",
            "أثر الحذف المتوقع على مساحة الهاتف", Icons.Rounded.Recycling, V2Green,
            metric = { f, _ -> permitValue(f.mediaReady, { formatBytes(f.reclaimableBytes()) }, "صلاحية مطلوبة") },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.Savings, "إجمالي الاسترجاع", permitValue(f.mediaReady, { formatBytes(f.reclaimableBytes()) }), V2Green),
                    ToolRow(Icons.Rounded.PieChart, "من التخزين المستخدم", permitValue(f.mediaReady, { shareOf(f.reclaimableBytes(), f.storage.usedBytes) }), V2Cyan),
                    ToolRow(Icons.Rounded.Compare, "أكبر مجموعة", permitValue(f.mediaReady, { f.media.duplicateGroups.maxByOrNull { it.reclaimableBytes }?.let { formatBytes(it.reclaimableBytes) } ?: "—" }), V2Pink),
                    ToolRow(Icons.Rounded.Calculate, "متوسط المجموعة", permitValue(f.mediaReady, { formatBytes(f.media.duplicateGroups.map { it.reclaimableBytes }.averageOrZero()) }), V2Violet),
                )
            },
            note = "الاسترجاع تقديري قبل التأكيد؛ لا يُحذف أي ملف من هذه الشاشة.",
            actionRoute = "dup-confirm", actionLabel = "فتح تأكيد العملية",
        ),
    )

    private val residue = listOf(
        x(
            "residue", "res-cache-lens", "03.11", "عدسة كاش KNOUX", "KNOUX CACHE LENS",
            "الحجم الفعلي للملفات المؤقتة داخل مساحة التطبيق", Icons.Rounded.Cached, V2Cyan,
            metric = { f, _ -> formatBytes(f.ownCacheBytes) },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.CleaningServices, "كاش KNOUX", formatBytes(f.ownCacheBytes), V2Cyan),
                    ToolRow(Icons.Rounded.Description, "ملفات مؤقتة", "${formatCount(f.ownCacheFiles)} ملفًا", V2Violet),
                    ToolRow(Icons.Rounded.PieChart, "من التخزين المستخدم", shareOf(f.ownCacheBytes, f.storage.usedBytes), V2Amber),
                    ToolRow(Icons.Rounded.Storage, "المساحة الحرة", formatBytes(f.storage.freeBytes), V2Green),
                )
            },
            note = "هذا الكاش يخص KNOUX وحده؛ لا يشمل كاش التطبيقات الأخرى التي لا يقرأها النظام دون صلاحية.",
            actionRoute = "clean-cache-audit", actionLabel = "فتح تدقيق الكاش",
        ),
        x(
            "residue", "res-duplicate-residue", "03.12", "بقايا وسائط مكررة", "DUPLICATE MEDIA RESIDUE",
            "النسخ المكررة كبقايا قابلة للمراجعة قبل التنظيف", Icons.Rounded.ContentCopy, V2Pink,
            metric = { f, _ -> permitValue(f.mediaReady, { formatCount(f.allDuplicateCount()) + " نسخة" }, "صلاحية مطلوبة") },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.Layers, "مجموعات مكررة", permitValue(f.mediaReady, { formatCount(f.media.duplicateGroups.size) }), V2Violet),
                    ToolRow(Icons.Rounded.ContentCopy, "نسخ زائدة", permitValue(f.mediaReady, { formatCount(f.allDuplicateCount()) }), V2Pink),
                    ToolRow(Icons.Rounded.Savings, "مساحة قابلة للاسترجاع", permitValue(f.mediaReady, { formatBytes(f.reclaimableBytes()) }), V2Green),
                    ToolRow(Icons.Rounded.Category, "النطاق", "صور وفيديو فقط", V2Cyan),
                )
            },
            note = "النسخ المكررة بقايا محتملة وليست حذفًا تلقائيًا؛ المراجعة تبقى بيدك.",
            list = ToolList.GROUPS,
            actionRoute = "dup-review", actionLabel = "فتح مراجعة الحذف",
        ),
        x(
            "residue", "res-source-audit", "03.13", "تدقيق مصادر البقايا", "RESIDUE SOURCE AUDIT",
            "ما المصادر التي قرأها KNOUX فعلًا؟", Icons.Rounded.AccountTree, V2Violet,
            metric = { f, _ -> "${formatCount(f.installedCount)} تطبيقًا" },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.Apps, "تطبيقات مثبتة", formatCount(f.installedCount), V2Violet),
                    ToolRow(Icons.Rounded.Person, "تطبيقات المستخدم", formatCount(f.userAppCount), V2Cyan),
                    ToolRow(Icons.Rounded.PermMedia, "ملفات وسائط", permitValue(f.mediaReady, { formatCount(f.media.images + f.media.videos) }, "بعد المنح"), V2Pink),
                    ToolRow(Icons.Rounded.Cached, "ملفات كاش KNOUX", formatCount(f.ownCacheFiles), V2Amber),
                )
            },
            note = "لا يقرأ KNOUX مجلدات التطبيقات الأخرى؛ المصادر المذكورة هي المتاحة رسميًا لهذا التطبيق.",
        ),
        x(
            "residue", "res-storage-echo", "03.14", "أثر البقايا على التخزين", "STORAGE ECHO",
            "نسبة الكاش والنسخ المكررة من المساحة المشغولة", Icons.Rounded.Storage, V2Amber,
            metric = { f, _ -> shareOf(f.ownCacheBytes + f.reclaimableBytes(), f.storage.usedBytes) },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.Storage, "المستخدم", formatBytes(f.storage.usedBytes), V2Cyan),
                    ToolRow(Icons.Rounded.Cached, "كاش KNOUX", formatBytes(f.ownCacheBytes), V2Amber),
                    ToolRow(Icons.Rounded.Layers, "نسخ مكررة", permitValue(f.mediaReady, { formatBytes(f.reclaimableBytes()) }, "بعد المنح"), V2Pink),
                    ToolRow(Icons.Rounded.Savings, "المتاح", formatBytes(f.storage.freeBytes), V2Green),
                )
            },
            note = "النسب تُحسب من قراءة StatFs الحالية، لا من أرقام تصميم ثابتة.",
            actionRoute = "storage", actionLabel = "فتح تحليل التخزين",
        ),
        x(
            "residue", "res-safe-plan", "03.15", "خطة التنظيف الآمنة", "SAFE CLEANUP PLAN",
            "ما يمكن مراجعته الآن دون لمس ملفاتك الشخصية", Icons.Rounded.CleaningServices, V2Green,
            metric = { f, _ -> formatBytes(f.ownCacheBytes + f.reclaimableBytes()) },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.Cached, "كاش KNOUX", formatBytes(f.ownCacheBytes), V2Green),
                    ToolRow(Icons.Rounded.Layers, "نسخ مكررة", permitValue(f.mediaReady, { formatBytes(f.reclaimableBytes()) }, "بعد المنح"), V2Pink),
                    ToolRow(Icons.Rounded.Shield, "محمي", "ملفات النظام والتطبيقات", V2Cyan),
                    ToolRow(Icons.Rounded.Verified, "التنفيذ", "بتأكيد صريح فقط", V2Violet),
                )
            },
            note = "الخطة لا تنفذ حذفًا تلقائيًا؛ تفتح مركز التنظيف لمراجعة العناصر وتأكيدها.",
            actionRoute = "smartclean", actionLabel = "فتح مركز التنظيف",
        ),
    )

    private val privacy = listOf(
        x(
            "privacy", "prv-contacts-lens", "04.11", "عدسة جهات الاتصال", "CONTACTS LENS",
            "التطبيقات التي تعلن وصولها لجهات الاتصال", Icons.Rounded.ContactPhone, V2Cyan,
            metric = { f, _ -> permissionGroup(f, "contacts")?.let { "${formatCount(it.grantedCount)} ممنوح" } ?: "غير متاح" },
            rows = { f, _ ->
                val group = permissionGroup(f, "contacts")
                listOf(
                    ToolRow(Icons.Rounded.ContactPhone, "تطبيقات تعلن الوصول", group?.let { formatCount(it.appCount) } ?: "—", V2Cyan),
                    ToolRow(Icons.Rounded.Lock, "ممنوح فعليًا", group?.let { formatCount(it.grantedCount) } ?: "—", V2Pink),
                    ToolRow(Icons.Rounded.Apps, "إجمالي التطبيقات", formatCount(f.installedCount), V2Violet),
                    ToolRow(Icons.Rounded.AdminPanelSettings, "درجة الحماية", "${protectionScore(f)}%", V2Green),
                )
            },
            note = "العدّ من PackageManager وGrantedPermissions، وليس من أرقام تصميم أو إحصاءات سحابية.",
            handoff = ToolHandoff.PRIVACY_SETTINGS,
        ),
        x(
            "privacy", "prv-notification-lens", "04.12", "عدسة الإشعارات", "NOTIFICATION LENS",
            "من يملك إذن إرسال الإشعارات على هذا الهاتف؟", Icons.Rounded.NotificationsActive, V2Pink,
            metric = { f, _ -> permissionGroup(f, "notifications")?.let { "${formatCount(it.grantedCount)} ممنوح" } ?: "غير متاح" },
            rows = { f, _ ->
                val group = permissionGroup(f, "notifications")
                listOf(
                    ToolRow(Icons.Rounded.NotificationsActive, "تطبيقات تعلن الإشعارات", group?.let { formatCount(it.appCount) } ?: "—", V2Pink),
                    ToolRow(Icons.Rounded.Lock, "ممنوح فعليًا", group?.let { formatCount(it.grantedCount) } ?: "—", V2Violet),
                    ToolRow(Icons.Rounded.Apps, "التطبيقات المثبتة", formatCount(f.installedCount), V2Cyan),
                    ToolRow(Icons.Rounded.Policy, "المراجعة", "من إعدادات Android", V2Amber),
                )
            },
            note = "إذن الإشعارات يُمنح من النظام؛ KNOUX يعرض الحالة فقط.",
            handoff = ToolHandoff.PRIVACY_SETTINGS,
        ),
        x(
            "privacy", "prv-file-access", "04.13", "وصول التطبيقات للملفات", "FILE ACCESS",
            "أذونات قراءة التخزين والوسائط على الجهاز", Icons.Rounded.FolderShared, V2Violet,
            metric = { f, _ -> permissionGroup(f, "files")?.let { "${formatCount(it.grantedCount)} ممنوح" } ?: "غير متاح" },
            rows = { f, _ ->
                val group = permissionGroup(f, "files")
                listOf(
                    ToolRow(Icons.Rounded.FolderShared, "تطبيقات تعلن الوصول", group?.let { formatCount(it.appCount) } ?: "—", V2Violet),
                    ToolRow(Icons.Rounded.Lock, "ممنوح فعليًا", group?.let { formatCount(it.grantedCount) } ?: "—", V2Pink),
                    ToolRow(Icons.Rounded.PermMedia, "صلاحية KNOUX للوسائط", if (f.mediaReady) "ممنوحة" else "غير ممنوحة", if (f.mediaReady) V2Green else V2Amber),
                    ToolRow(Icons.Rounded.Storage, "نطاق القراءة", "MediaStore", V2Cyan),
                )
            },
            note = "KNOUX لا يقرأ ملفات التطبيقات الأخرى؛ النطاق هو MediaStore المصرح به فقط.",
            handoff = ToolHandoff.PRIVACY_SETTINGS,
        ),
        x(
            "privacy", "prv-protection-score", "04.14", "درجة الحماية", "PROTECTION SCORE",
            "مؤشر محلي مبني على نسبة التطبيقات ذات الأذونات الحساسة", Icons.Rounded.AdminPanelSettings, V2Green,
            metric = { f, _ -> "${protectionScore(f)}%" },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.Shield, "الدرجة الحالية", "${protectionScore(f)}%", V2Green),
                    ToolRow(Icons.Rounded.WarningAmber, "تطبيقات حساسة", formatCount(f.sensitiveAppCount), V2Amber),
                    ToolRow(Icons.Rounded.PieChart, "من التطبيقات", shareOf(f.sensitiveAppCount.toLong(), f.installedCount.toLong()), V2Pink),
                    ToolRow(Icons.Rounded.Rule, "أذونات معلنة", formatCount(declaredPermissionCount(f)), V2Violet),
                )
            },
            note = "الدرجة إرشادية محلية وليست تقييمًا أمنيًا رسميًا من Google أو مصنّع الجهاز.",
            actionRoute = "privacy", actionLabel = "فتح مركز الخصوصية",
        ),
        x(
            "privacy", "prv-sensitive-index", "04.15", "فهرس التطبيقات الحساسة", "SENSITIVE APP INDEX",
            "قائمة قابلة للبحث بالتطبيقات ذات الأذونات الحساسة", Icons.Rounded.Policy, V2Amber,
            metric = { f, _ -> "${formatCount(f.sensitiveAppCount)} تطبيقًا" },
            rows = { f, _ ->
                val multi = f.installedApps.count { it.sensitivePermissions.size >= 2 }
                listOf(
                    ToolRow(Icons.Rounded.Policy, "تطبيقات حساسة", formatCount(f.sensitiveAppCount), V2Amber),
                    ToolRow(Icons.Rounded.Rule, "أذونات معلنة", formatCount(declaredPermissionCount(f)), V2Violet),
                    ToolRow(Icons.Rounded.WarningAmber, "أذونات متعددة", formatCount(multi), V2Pink),
                    ToolRow(Icons.Rounded.DeveloperMode, "من النظام", formatCount(f.installedApps.count { it.isSystem && it.sensitivePermissions.isNotEmpty() }), V2Cyan),
                )
            },
            note = "استخدم شرائح الفلترة لعرض الحساسة أو النظام أو المستخدم؛ لا يتم إخفاء أي تطبيق.",
            list = ToolList.APPS,
            behavior = ToolBehavior.FILTER_APPS_SIZE,
            initialAppFilter = 3,
            handoff = ToolHandoff.APP_SETTINGS,
        ),
    )

    private val deepScan = listOf(
        x(
            "deepscan", "dsn-preflight", "05.11", "فحص الجاهزية", "PREFLIGHT CHECK",
            "هل الظروف مناسبة لبدء فحص عميق الآن؟", Icons.Rounded.Checklist, V2Cyan,
            metric = { f, _ -> if (f.computedAt > 0L) "جاهز للمراجعة" else "بانتظار القراءة" },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.Storage, "مساحة حرة", formatBytes(f.storage.freeBytes), V2Cyan),
                    ToolRow(Icons.Rounded.BatteryChargingFull, "مستوى البطارية", f.battery.levelPercent?.let { "$it%" } ?: "غير متاح", V2Green),
                    ToolRow(Icons.Rounded.PermMedia, "صلاحية الوسائط", if (f.mediaReady) "ممنوحة" else "مطلوبة", if (f.mediaReady) V2Green else V2Amber),
                    ToolRow(Icons.Rounded.Apps, "حزم مقروءة", formatCount(f.installedCount), V2Violet),
                )
            },
            note = "الفحص لا يبدأ تلقائيًا من هذه الشاشة؛ يعرض الجاهزية ثم تنتقل إلى شاشة التشغيل.",
            actionRoute = "dsn-start", actionLabel = "فتح شاشة بدء الفحص",
        ),
        x(
            "deepscan", "dsn-hash-budget", "05.12", "ميزانية فحص البصمات", "HASH BUDGET",
            "حدود القراءة التي يلتزم بها محرك المطابقة", Icons.Rounded.Fingerprint, V2Amber,
            pro = true,
            metric = { f, _ -> permitValue(f.mediaReady, { "${formatCount(f.media.duplicateCandidates)} مرشحًا" }, "بدون صلاحية") },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.FilterAlt, "حد الملفات", "256 ملفًا", V2Amber),
                    ToolRow(Icons.Rounded.Storage, "حد الحجم الكلي", "128 MB", V2Cyan),
                    ToolRow(Icons.Rounded.Description, "حد الملف الواحد", "32 MB", V2Violet),
                    ToolRow(Icons.Rounded.Layers, "مجموعات مؤكدة", permitValue(f.mediaReady, { formatCount(f.media.duplicateGroups.size) }), V2Pink),
                )
            },
            note = "هذه حدود حقيقية في محرك القراءة، وليست أرقام عرض؛ الملفات الأكبر تُستثنى.",
            actionRoute = "dup-scan", actionLabel = "فتح فحص البصمات",
        ),
        x(
            "deepscan", "dsn-thermal-guard", "05.13", "حارس الحرارة", "THERMAL GUARD",
            "قراءة حرارة البطارية قبل وأثناء الفحص", Icons.Rounded.Thermostat, V2ProRed,
            metric = { f, _ -> temperature(f) },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.Thermostat, "الحرارة الحالية", temperature(f), if ((f.battery.temperatureC ?: 0f) >= 43f) V2ProRed else V2Green),
                    ToolRow(Icons.Rounded.Tune, "حد التنبيه", "43.0°C", V2Amber),
                    ToolRow(Icons.Rounded.HealthAndSafety, "صحة البطارية", f.battery.healthLabel, V2Cyan),
                    ToolRow(Icons.Rounded.Power, "حالة الطاقة", if (f.battery.charging) "موصول بالشاحن" else "على البطارية", V2Violet),
                )
            },
            note = "القراءة من BatteryManager فقط؛ لا يوجد تبريد أو تحكم تلقائي في حرارة الجهاز.",
        ),
        x(
            "deepscan", "dsn-battery-guard", "05.14", "حارس الطاقة", "POWER GUARD",
            "مستوى البطارية ومصدر الطاقة قبل الفحص الطويل", Icons.Rounded.BatterySaver, V2Green,
            metric = { f, _ -> f.battery.levelPercent?.let { "$it%" } ?: "غير متاح" },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.BatteryFull, "المستوى", f.battery.levelPercent?.let { "$it%" } ?: "غير متاح", V2Green),
                    ToolRow(Icons.Rounded.Power, "المصدر", powerSource(f), V2Cyan),
                    ToolRow(Icons.Rounded.Bolt, "الجهد", voltage(f), V2Amber),
                    ToolRow(Icons.Rounded.Thermostat, "الحرارة", temperature(f), V2Violet),
                )
            },
            note = "لا يوقف KNOUX الفحص أو يغيّر إعدادات الطاقة من تلقاء نفسه.",
            handoff = ToolHandoff.BATTERY_SETTINGS,
        ),
        x(
            "deepscan", "dsn-sessions", "05.15", "سجل جلسات الفحص", "SCAN SESSIONS",
            "كل قراءة محفوظة مع نطاق الفترة", Icons.Rounded.History, V2Violet,
            metric = { _, h -> "${formatCount(h.size)} جلسة" },
            rows = { _, h -> captureRows(h) },
            note = "السجل محلي بالكامل داخل التطبيق، ويمكن تصفيره بالفترة من الشرائح.",
            list = ToolList.HISTORY,
            behavior = ToolBehavior.RANGE_HISTORY,
        ),
    )

    private val reports = listOf(
        x(
            "reports", "rpt-health-lens", "06.11", "عدسة صحة الجهاز", "DEVICE HEALTH LENS",
            "كل مؤشرات درجة الصحة في لوحة واحدة", Icons.Rounded.HealthAndSafety, V2Green,
            metric = { f, _ -> if (f.healthEstimate > 0) "${f.healthEstimate}%" else "بانتظار القراءة" },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.HealthAndSafety, "درجة الصحة", "${f.healthEstimate}%", V2Green),
                    ToolRow(Icons.Rounded.Storage, "التخزين المستخدم", shareOf(f.storage.usedBytes, f.storage.totalBytes), V2Cyan),
                    ToolRow(Icons.Rounded.BatteryChargingFull, "البطارية", f.battery.levelPercent?.let { "$it%" } ?: "غير متاح", V2Amber),
                    ToolRow(Icons.Rounded.WarningAmber, "تطبيقات حساسة", formatCount(f.sensitiveAppCount), V2Pink),
                )
            },
            note = "الدرجة تقدير محلي من القراءات المتاحة، وليست تقريرًا طبيًا أو حكمًا على العتاد.",
            actionRoute = "performance-report", actionLabel = "فتح تقرير الأداء",
        ),
        x(
            "reports", "rpt-storage-trend", "06.12", "اتجاه التخزين", "STORAGE TREND",
            "مقارنة المساحة المستخدمة بين أول وآخر قراءة", Icons.Rounded.TrendingDown, V2Cyan,
            metric = { _, h ->
                val a = firstHistory(h); val b = lastHistory(h)
                if (a != null && b != null) signedBytes(b.storageUsedBytes - a.storageUsedBytes) else "لا سجل"
            },
            rows = { _, h ->
                val sorted = h.sortedBy { it.timestamp }
                val first = sorted.firstOrNull()
                val last = sorted.lastOrNull()
                val peak = sorted.maxByOrNull { it.storageUsedBytes }
                listOf(
                    ToolRow(Icons.Rounded.History, "أول قراءة", first?.let { formatBytes(it.storageUsedBytes) } ?: "لا سجل", V2Violet),
                    ToolRow(Icons.Rounded.Storage, "آخر قراءة", last?.let { formatBytes(it.storageUsedBytes) } ?: "لا سجل", V2Cyan),
                    ToolRow(Icons.Rounded.TrendingUp, "الفرق", if (first != null && last != null) signedBytes(last.storageUsedBytes - first.storageUsedBytes) else "—", V2Amber),
                    ToolRow(Icons.Rounded.BarChart, "أعلى قراءة", peak?.let { formatBytes(it.storageUsedBytes) } ?: "—", V2Green),
                )
            },
            note = "الفرق سالب يعني مساحة مستخدمة أكبر؛ هذه قراءات محفوظة وليست توقعًا.",
        ),
        x(
            "reports", "rpt-battery-trend", "06.13", "اتجاه البطارية", "BATTERY TREND",
            "مستوى البطارية عبر الجلسات المحفوظة", Icons.Rounded.BatteryChargingFull, V2Amber,
            metric = { _, h ->
                val levels = h.mapNotNull { it.batteryLevel }
                if (levels.isEmpty()) "لا قياس" else "متوسط ${levels.average().toInt()}%"
            },
            rows = { _, h ->
                val sorted = h.sortedBy { it.timestamp }
                val levels = sorted.mapNotNull { it.batteryLevel }
                listOf(
                    ToolRow(Icons.Rounded.History, "أول مستوى", sorted.firstOrNull { it.batteryLevel != null }?.batteryLevel?.let { "$it%" } ?: "—", V2Violet),
                    ToolRow(Icons.Rounded.BatteryFull, "آخر مستوى", sorted.lastOrNull { it.batteryLevel != null }?.batteryLevel?.let { "$it%" } ?: "—", V2Green),
                    ToolRow(Icons.Rounded.TrendingUp, "المتوسط", if (levels.isEmpty()) "—" else "${levels.average().toInt()}%", V2Amber),
                    ToolRow(Icons.Rounded.WarningAmber, "أدنى مستوى", levels.minOrNull()?.let { "$it%" } ?: "—", V2Pink),
                )
            },
            note = "المستويات تأتي من كل قراءة محفوظة؛ لا تُضاف قياسات وهمية لتكبير الرسم.",
        ),
        x(
            "reports", "rpt-session-compare", "06.14", "مقارنة الجلسات", "SESSION COMPARE",
            "ماذا تغير بين القراءة الأولى والأخيرة؟", Icons.Rounded.CompareArrows, V2Violet,
            metric = { _, h -> "${formatCount(h.size)} قراءة" },
            rows = { _, h ->
                val sorted = h.sortedBy { it.timestamp }
                val first = sorted.firstOrNull()
                val last = sorted.lastOrNull()
                listOf(
                    ToolRow(Icons.Rounded.History, "عدد الجلسات", formatCount(sorted.size), V2Violet),
                    ToolRow(Icons.Rounded.Timeline, "المدة", if (first != null && last != null) formatDuration(last.timestamp - first.timestamp) else "—", V2Cyan),
                    ToolRow(Icons.Rounded.Cached, "فرق الكاش", if (first != null && last != null) signedBytes(last.cacheBytes - first.cacheBytes) else "—", V2Pink),
                    ToolRow(Icons.Rounded.Tune, "فرق الحساسة", if (first != null && last != null) signedCount(last.privacyFindings - first.privacyFindings) else "—", V2Amber),
                )
            },
            note = "المقارنة تعتمد على السجل المحلي فقط، وقد تكون هناك فجوات بين الجلسات.",
            actionRoute = "reports", actionLabel = "فتح التقارير",
        ),
        x(
            "reports", "rpt-export-kit", "06.15", "حزمة مشاركة الملخص", "SUMMARY EXPORT KIT",
            "ما الذي يمكن أن يحتويه ملخص المشاركة؟", Icons.Rounded.Share, V2Pink,
            metric = { _, h -> "${formatCount(h.size)} قراءة" },
            rows = { f, h ->
                listOf(
                    ToolRow(Icons.Rounded.Summarize, "أقسام الملخص", "الجهاز · التخزين · البطارية · الخصوصية", V2Pink),
                    ToolRow(Icons.Rounded.History, "آخر جلسة", lastHistory(h)?.let { formatScanTime(it.timestamp) } ?: "لا سجل", V2Violet),
                    ToolRow(Icons.Rounded.Verified, "مصدر الأرقام", "قراءات محلية من الهاتف", V2Green),
                    ToolRow(Icons.Rounded.Lock, "بيانات شخصية", "لا أسماء ملفات أو مسارات في الملخص", V2Cyan),
                )
            },
            note = "الحزمة وصف لما سيُشارك؛ لا يتم رفع أي تقرير إلى خادم من هذه الشاشة.",
            actionRoute = "rpt-device", actionLabel = "فتح لوحة ملخص الجهاز",
        ),
    )

    private val appManager = listOf(
        x(
            "appmanager", "apm-sensitive-index", "07.11", "مستكشف الأذونات", "PERMISSION EXPLORER",
            "تصفية التطبيقات حسب الأذونات الحساسة المعلنة", Icons.Rounded.Shield, V2Pink,
            metric = { f, _ -> "${formatCount(f.sensitiveAppCount)} تطبيقًا" },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.Shield, "تطبيقات حساسة", formatCount(f.sensitiveAppCount), V2Pink),
                    ToolRow(Icons.Rounded.Rule, "أذونات حساسة معلنة", formatCount(f.installedApps.sumOf { it.sensitivePermissions.size }), V2Violet),
                    ToolRow(Icons.Rounded.WarningAmber, "أكثر من إذن", formatCount(f.installedApps.count { it.sensitivePermissions.size >= 2 }), V2Amber),
                    ToolRow(Icons.Rounded.DeveloperMode, "من تطبيقات النظام", formatCount(f.installedApps.count { it.isSystem && it.sensitivePermissions.isNotEmpty() }), V2Cyan),
                )
            },
            note = "القائمة قابلة للبحث والفلترة؛ الإذن المعلن لا يعني أنه ممنوح فعليًا.",
            list = ToolList.APPS,
            behavior = ToolBehavior.FILTER_APPS_SIZE,
            initialAppFilter = 3,
            handoff = ToolHandoff.APP_SETTINGS,
        ),
        x(
            "appmanager", "apm-disabled", "07.12", "التطبيقات المعطلة", "DISABLED APPS",
            "التطبيقات الموجودة لكنها غير مفعّلة حاليًا", Icons.Rounded.Block, V2Amber,
            metric = { f, _ -> "${formatCount(f.installedApps.count { !it.isEnabled })} معطلًا" },
            rows = { f, _ ->
                val disabled = f.installedApps.filter { !it.isEnabled }
                listOf(
                    ToolRow(Icons.Rounded.Block, "معطلة", formatCount(disabled.size), V2Amber),
                    ToolRow(Icons.Rounded.DeveloperMode, "منها نظام", formatCount(disabled.count { it.isSystem }), V2Cyan),
                    ToolRow(Icons.Rounded.Person, "منها مستخدم", formatCount(disabled.count { !it.isSystem }), V2Violet),
                    ToolRow(Icons.Rounded.Tune, "الإجراء", "من إعدادات التطبيق", V2Soft),
                )
            },
            note = "إعادة التفعيل تتم من شاشة النظام؛ KNOUX لا يغيّر حالة الحزم من تلقاء نفسه.",
            list = ToolList.APPS,
            behavior = ToolBehavior.FILTER_APPS_SIZE,
            initialAppFilter = 4,
            handoff = ToolHandoff.APP_SETTINGS,
        ),
        x(
            "appmanager", "apm-system", "07.13", "تطبيقات النظام", "SYSTEM APPS",
            "حزم النظام المثبتة على هذا الهاتف", Icons.Rounded.DeveloperMode, V2Cyan,
            metric = { f, _ -> "${formatCount(f.installedCount - f.userAppCount)} حزمة" },
            rows = { f, _ ->
                val system = f.installedApps.filter { it.isSystem }
                listOf(
                    ToolRow(Icons.Rounded.DeveloperMode, "حزم النظام", formatCount(system.size), V2Cyan),
                    ToolRow(Icons.Rounded.Shield, "منها حساسة", formatCount(system.count { it.sensitivePermissions.isNotEmpty() }), V2Pink),
                    ToolRow(Icons.Rounded.Block, "منها معطلة", formatCount(system.count { !it.isEnabled }), V2Amber),
                    ToolRow(Icons.Rounded.Apps, "الإجمالي", formatCount(f.installedCount), V2Violet),
                )
            },
            note = "تُعرض المعلومات فقط؛ إلغاء تثبيت حزم النظام ليس متاحًا من تطبيق عادي.",
            list = ToolList.APPS,
            behavior = ToolBehavior.FILTER_APPS_SIZE,
            initialAppFilter = 2,
            handoff = ToolHandoff.APP_SETTINGS,
        ),
        x(
            "appmanager", "apm-user", "07.14", "تطبيقات المستخدم", "USER APPS",
            "التطبيقات التي ثبتها المستخدم على الجهاز", Icons.Rounded.ManageAccounts, V2Green,
            metric = { f, _ -> "${formatCount(f.userAppCount)} تطبيقًا" },
            rows = { f, _ ->
                val user = f.installedApps.filter { !it.isSystem }
                listOf(
                    ToolRow(Icons.Rounded.ManageAccounts, "تطبيقات المستخدم", formatCount(user.size), V2Green),
                    ToolRow(Icons.Rounded.PlayArrow, "قابلة للتشغيل", formatCount(user.count { it.isEnabled }), V2Cyan),
                    ToolRow(Icons.Rounded.Shield, "ذات أذونات حساسة", formatCount(user.count { it.sensitivePermissions.isNotEmpty() }), V2Pink),
                    ToolRow(Icons.Rounded.Tune, "الإجمالي", formatCount(f.installedCount), V2Violet),
                )
            },
            note = "القائمة نفسها قابلة للفتح وإدارة الإعدادات عبر أيقونة كل تطبيق.",
            list = ToolList.APPS,
            behavior = ToolBehavior.FILTER_APPS_SIZE,
            initialAppFilter = 1,
            handoff = ToolHandoff.APP_SETTINGS,
        ),
        x(
            "appmanager", "apm-package-index", "07.15", "فهرس الحزم والإصدارات", "PACKAGE INDEX",
            "فهرس تقني لأسماء الحزم وإصداراتها", Icons.Rounded.ReceiptLong, V2Violet,
            metric = { f, _ -> "${formatCount(f.installedCount)} حزمة" },
            rows = { f, _ ->
                val versions = f.installedApps.count { !it.versionName.isNullOrBlank() }
                listOf(
                    ToolRow(Icons.Rounded.ReceiptLong, "حزم مفهرسة", formatCount(f.installedCount), V2Violet),
                    ToolRow(Icons.Rounded.Verified, "إصدار معروف", formatCount(versions), V2Green),
                    ToolRow(Icons.Rounded.DeveloperMode, "نظام", formatCount(f.installedCount - f.userAppCount), V2Cyan),
                    ToolRow(Icons.Rounded.Person, "مستخدم", formatCount(f.userAppCount), V2Pink),
                )
            },
            note = "الفهرس يعرض بيانات PackageManager المتاحة للتطبيق، ولا يقرأ ملفات APK.",
            list = ToolList.APPS,
            behavior = ToolBehavior.SORT_APPS,
        ),
    )

    private val storage = listOf(
        x(
            "storage", "sto-media-mix", "08.11", "مزيج الوسائط", "MEDIA MIX",
            "عدد وحجم الصور والفيديو في القراءة الحالية", Icons.Rounded.PermMedia, V2Pink,
            metric = { f, _ -> permitValue(f.mediaReady, { formatBytes(f.mediaBytes()) }, "صلاحية مطلوبة") },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.Image, "صور", permitValue(f.mediaReady, { "${formatCount(f.media.images)} · ${formatBytes(f.media.imagesBytes)}" }), V2Pink),
                    ToolRow(Icons.Rounded.VideoLibrary, "فيديو", permitValue(f.mediaReady, { "${formatCount(f.media.videos)} · ${formatBytes(f.media.videosBytes)}" }), V2Violet),
                    ToolRow(Icons.Rounded.Compare, "متوسط الصورة", permitValue(f.mediaReady, { if (f.media.images > 0) formatBytes(f.media.imagesBytes / f.media.images) else "—" }), V2Cyan),
                    ToolRow(Icons.Rounded.Movie, "متوسط الفيديو", permitValue(f.mediaReady, { if (f.media.videos > 0) formatBytes(f.media.videosBytes / f.media.videos) else "—" }), V2Amber),
                )
            },
            note = "النطاق هو ملفات MediaStore التي مُنحت صلاحيتها فقط.",
        ),
        x(
            "storage", "sto-largest-lens", "08.12", "عدسة أكبر الملفات", "LARGEST FILE LENS",
            "أكبر الملفات المقروءة وفرزها بالحجم", Icons.Rounded.PhotoSizeSelectLarge, V2Amber,
            metric = { f, _ -> permitValue(f.mediaReady, { f.media.largestFiles.maxOfOrNull { it.sizeBytes }?.let(::formatBytes) ?: "—" }, "صلاحية مطلوبة") },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.PhotoSizeSelectLarge, "أكبر ملف", permitValue(f.mediaReady, { f.media.largestFiles.maxOfOrNull { it.sizeBytes }?.let(::formatBytes) ?: "—" }), V2Amber),
                    ToolRow(Icons.Rounded.Inventory, "ملفات مقروءة", permitValue(f.mediaReady, { formatCount(f.media.largestFiles.size) }), V2Cyan),
                    ToolRow(Icons.Rounded.Image, "صور", permitValue(f.mediaReady, { formatCount(f.media.largestFiles.count { !it.isVideo }) }), V2Pink),
                    ToolRow(Icons.Rounded.VideoLibrary, "فيديو", permitValue(f.mediaReady, { formatCount(f.media.largestFiles.count { it.isVideo }) }), V2Violet),
                )
            },
            note = "القائمة تعرض أعلى الملفات حجمًا في القراءة الحالية، وليست كل ملفات الجهاز.",
            list = ToolList.FILES,
            behavior = ToolBehavior.SORT_FILES,
        ),
        x(
            "storage", "sto-buckets", "08.13", "المجلدات الأثقل", "HEAVY BUCKETS",
            "تجميع الملفات المقروءة حسب المجلد", Icons.Rounded.FolderZip, V2Violet,
            metric = { f, _ -> f.topBucket()?.first ?: "لا مجلدات" },
            rows = { f, _ ->
                val top = f.topBucket()
                listOf(
                    ToolRow(Icons.Rounded.FolderZip, "أثقل مجلد", permitValue(f.mediaReady, { top?.first ?: "—" }), V2Violet),
                    ToolRow(Icons.Rounded.Storage, "حجمه", permitValue(f.mediaReady, { top?.second?.let(::formatBytes) ?: "—" }), V2Cyan),
                    ToolRow(Icons.Rounded.Folder, "مجلدات ظاهرة", permitValue(f.mediaReady, { formatCount(f.bucketCount()) }), V2Pink),
                    ToolRow(Icons.Rounded.PieChart, "من قراءة الوسائط", permitValue(f.mediaReady, { top?.second?.let { shareOf(it, f.mediaBytes()) } ?: "—" }), V2Amber),
                )
            },
            note = "اسم المجلد من MediaStore؛ لا يتم فتح مسارات غير مصرح بها.",
        ),
        x(
            "storage", "sto-video-share", "08.14", "حصة الفيديو", "VIDEO SHARE",
            "كم يشغل الفيديو من مساحة الوسائط؟", Icons.Rounded.Movie, V2Cyan,
            metric = { f, _ -> permitValue(f.mediaReady, { shareOf(f.media.videosBytes, f.mediaBytes()) }, "صلاحية مطلوبة") },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.VideoLibrary, "عدد الفيديو", permitValue(f.mediaReady, { formatCount(f.media.videos) }), V2Cyan),
                    ToolRow(Icons.Rounded.Storage, "حجم الفيديو", permitValue(f.mediaReady, { formatBytes(f.media.videosBytes) }), V2Violet),
                    ToolRow(Icons.Rounded.PieChart, "من الوسائط", permitValue(f.mediaReady, { shareOf(f.media.videosBytes, f.mediaBytes()) }), V2Amber),
                    ToolRow(Icons.Rounded.Movie, "أكبر فيديو", permitValue(f.mediaReady, { f.media.largestFiles.filter { it.isVideo }.maxOfOrNull { it.sizeBytes }?.let(::formatBytes) ?: "—" }), V2Pink),
                )
            },
            note = "الحصة من مساحة الصور والفيديو المقروءة، لا من إجمالي التخزين.",
            list = ToolList.VIDEO_FILES,
            behavior = ToolBehavior.SORT_FILES,
        ),
        x(
            "storage", "sto-image-share", "08.15", "حصة الصور", "PHOTO SHARE",
            "كم تشغل الصور من مساحة الوسائط؟", Icons.Rounded.PhotoLibrary, V2Green,
            metric = { f, _ -> permitValue(f.mediaReady, { shareOf(f.media.imagesBytes, f.mediaBytes()) }, "صلاحية مطلوبة") },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.Image, "عدد الصور", permitValue(f.mediaReady, { formatCount(f.media.images) }), V2Green),
                    ToolRow(Icons.Rounded.Storage, "حجم الصور", permitValue(f.mediaReady, { formatBytes(f.media.imagesBytes) }), V2Cyan),
                    ToolRow(Icons.Rounded.PieChart, "من الوسائط", permitValue(f.mediaReady, { shareOf(f.media.imagesBytes, f.mediaBytes()) }), V2Amber),
                    ToolRow(Icons.Rounded.PhotoLibrary, "أكبر صورة", permitValue(f.mediaReady, { f.media.largestFiles.filter { !it.isVideo }.maxOfOrNull { it.sizeBytes }?.let(::formatBytes) ?: "—" }), V2Pink),
                )
            },
            note = "الحصة من مساحة الصور والفيديو المقروءة فقط.",
            list = ToolList.PHOTO_FILES,
            behavior = ToolBehavior.SORT_FILES,
        ),
    )

    private val battery = listOf(
        x(
            "battery", "bat-voltage-lens", "09.11", "عدسة الجهد", "VOLTAGE LENS",
            "قراءة الجهد الحالي للبطارية من النظام", Icons.Rounded.ElectricBolt, V2Amber,
            metric = { f, _ -> voltage(f) },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.ElectricBolt, "الجهد", voltage(f), V2Amber),
                    ToolRow(Icons.Rounded.BatteryFull, "المستوى", f.battery.levelPercent?.let { "$it%" } ?: "غير متاح", V2Green),
                    ToolRow(Icons.Rounded.Power, "الحالة", f.battery.statusLabel, V2Cyan),
                    ToolRow(Icons.Rounded.Thermostat, "الحرارة", temperature(f), V2Violet),
                )
            },
            note = "قيمة الجهد تُقرأ من BatteryManager وقت القراءة، وقد تتغير أثناء الشحن.",
        ),
        x(
            "battery", "bat-health-lens", "09.12", "عدسة صحة البطارية", "BATTERY HEALTH LENS",
            "حالة الصحة والتقنية التي يعلنها النظام", Icons.Rounded.HealthAndSafety, V2Green,
            metric = { f, _ -> f.battery.healthLabel },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.HealthAndSafety, "الصحة", f.battery.healthLabel, if (f.battery.healthCode == BatteryManager.BATTERY_HEALTH_GOOD) V2Green else V2Amber),
                    ToolRow(Icons.Rounded.Memory, "التقنية", f.battery.technology ?: "غير متاحة", V2Cyan),
                    ToolRow(Icons.Rounded.Thermostat, "الحرارة", temperature(f), V2Violet),
                    ToolRow(Icons.Rounded.Power, "الحالة", f.battery.statusLabel, V2Pink),
                )
            },
            note = "الصحة تصنيف من نظام Android وليست تشخيصًا مخبريًا للبطارية.",
        ),
        x(
            "battery", "bat-power-source", "09.13", "مصدر الطاقة", "POWER SOURCE",
            "هل الجهاز على الشاحن؟ وما نوعه؟", Icons.Rounded.Power, V2Cyan,
            metric = { f, _ -> powerSource(f) },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.Power, "المصدر", powerSource(f), V2Cyan),
                    ToolRow(Icons.Rounded.BatteryChargingFull, "الشحن", f.battery.statusLabel, if (f.battery.charging) V2Green else V2Amber),
                    ToolRow(Icons.Rounded.BatteryFull, "المستوى", f.battery.levelPercent?.let { "$it%" } ?: "غير متاح", V2Violet),
                    ToolRow(Icons.Rounded.ElectricBolt, "الجهد", voltage(f), V2Pink),
                )
            },
            note = "نوع الشاحن يعلنه النظام؛ الشحن اللاسلكي قد يظهر بسرعة منخفضة.",
            handoff = ToolHandoff.BATTERY_SETTINGS,
        ),
        x(
            "battery", "bat-thermal-lens", "09.14", "عدسة الحرارة", "THERMAL LENS",
            "الحرارة مقابل حد التنبيه المحلي", Icons.Rounded.DeviceThermostat, V2ProRed,
            metric = { f, _ -> temperature(f) },
            rows = { f, _ ->
                val hot = (f.battery.temperatureC ?: 0f) >= 43f
                listOf(
                    ToolRow(Icons.Rounded.DeviceThermostat, "الحرارة", temperature(f), if (hot) V2ProRed else V2Green),
                    ToolRow(Icons.Rounded.Tune, "حد التنبيه", "43.0°C", V2Amber),
                    ToolRow(Icons.Rounded.HealthAndSafety, "الصحة", f.battery.healthLabel, V2Cyan),
                    ToolRow(Icons.Rounded.Power, "الشحن", if (f.battery.charging) "نشط" else "غير نشط", V2Violet),
                )
            },
            note = "حد 43°C للتنبيه المحلي فقط، وليس حدًا رسميًا من مصنّع الجهاز.",
        ),
        x(
            "battery", "bat-energy-tips", "09.15", "توصيات الطاقة", "ENERGY TIPS",
            "توصيات محلية حسب المستوى والحرارة", Icons.Rounded.TipsAndUpdates, V2Violet,
            metric = { f, _ -> f.battery.levelPercent?.let { "$it%" } ?: "غير متاح" },
            rows = { f, _ ->
                val level = f.battery.levelPercent
                val tip = when {
                    level == null -> "انتظر قراءة البطارية"
                    level <= 20 -> "وصّل الشاحن قبل الفحص الطويل"
                    (f.battery.temperatureC ?: 0f) >= 43f -> "أوقف المهام الثقيلة حتى تنخفض الحرارة"
                    level >= 80 -> "المستوى مناسب للفحص الآن"
                    else -> "راقب المستوى أثناء الفحص"
                }
                listOf(
                    ToolRow(Icons.Rounded.BatterySaver, "التوصية", tip, V2Violet),
                    ToolRow(Icons.Rounded.BatteryFull, "المستوى", level?.let { "$it%" } ?: "غير متاح", V2Green),
                    ToolRow(Icons.Rounded.Thermostat, "الحرارة", temperature(f), V2Amber),
                    ToolRow(Icons.Rounded.Power, "المصدر", powerSource(f), V2Cyan),
                )
            },
            note = "توصيات إرشادية من القراءات، ولا يغيّر KNOUX إعدادات الطاقة تلقائيًا.",
            handoff = ToolHandoff.BATTERY_SAVER,
        ),
    )

    private val ai = listOf(
        x(
            "ai", "ai-health-plan", "10.11", "خطة صحة الجهاز", "DEVICE HEALTH PLAN",
            "أولوية التحسين مبنية على درجة الجهاز الحالية", Icons.Rounded.HealthAndSafety, V2Green,
            metric = { f, _ -> if (f.healthEstimate > 0) "${f.healthEstimate}%" else "بانتظار القراءة" },
            rows = { f, _ ->
                val rec = aiRecommendations(f).firstOrNull()
                listOf(
                    ToolRow(Icons.Rounded.HealthAndSafety, "الدرجة", "${f.healthEstimate}%", V2Green),
                    ToolRow(Icons.Rounded.TipsAndUpdates, "أول توصية", rec?.title ?: "لا توصية حالية", V2Pink),
                    ToolRow(Icons.Rounded.Storage, "التخزين", shareOf(f.storage.usedBytes, f.storage.totalBytes), V2Cyan),
                    ToolRow(Icons.Rounded.WarningAmber, "تطبيقات حساسة", formatCount(f.sensitiveAppCount), V2Amber),
                )
            },
            note = "التوصيات تُحسب محليًا من القراءات، ولا تُرسل إلى خدمة سحابية.",
            actionRoute = "ai-recommendations", actionLabel = "فتح مركز التوصيات",
        ),
        x(
            "ai", "ai-storage-plan", "10.12", "خطة المساحة", "STORAGE PLAN",
            "ما الذي يمكن أن يحرر مساحة على هذا الهاتف؟", Icons.Rounded.PieChart, V2Cyan,
            metric = { f, _ -> shareOf(f.reclaimableBytes() + f.ownCacheBytes, f.storage.usedBytes) },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.PieChart, "المستخدم", shareOf(f.storage.usedBytes, f.storage.totalBytes), V2Cyan),
                    ToolRow(Icons.Rounded.Layers, "نسخ مكررة", permitValue(f.mediaReady, { formatBytes(f.reclaimableBytes()) }, "بعد المنح"), V2Pink),
                    ToolRow(Icons.Rounded.Cached, "كاش KNOUX", formatBytes(f.ownCacheBytes), V2Violet),
                    ToolRow(Icons.Rounded.Savings, "مساحة حرة", formatBytes(f.storage.freeBytes), V2Green),
                )
            },
            note = "الفرص المذكورة تحتاج تأكيدك؛ لا يوجد تنفيذ تلقائي من هذه الشاشة.",
            actionRoute = "ai-recommendations", actionLabel = "فتح التوصيات",
        ),
        x(
            "ai", "ai-privacy-plan", "10.13", "خطة الخصوصية", "PRIVACY PLAN",
            "أولويات مراجعة الأذونات الحساسة", Icons.Rounded.LockPerson, V2Pink,
            metric = { f, _ -> "${protectionScore(f)}%" },
            rows = { f, _ ->
                val camera = permissionGroup(f, "camera")
                val mic = permissionGroup(f, "microphone")
                listOf(
                    ToolRow(Icons.Rounded.LockPerson, "تطبيقات حساسة", formatCount(f.sensitiveAppCount), V2Pink),
                    ToolRow(Icons.Rounded.PhotoCamera, "كاميرا ممنوحة", camera?.let { formatCount(it.grantedCount) } ?: "—", V2Violet),
                    ToolRow(Icons.Rounded.Mic, "ميكروفون ممنوح", mic?.let { formatCount(it.grantedCount) } ?: "—", V2Amber),
                    ToolRow(Icons.Rounded.AdminPanelSettings, "درجة الحماية", "${protectionScore(f)}%", V2Green),
                )
            },
            note = "المراجعة تقودك إلى إعدادات Android الرسمية؛ لا يغيّر KNOUX الأذونات بنفسه.",
            actionRoute = "ai-recommendations", actionLabel = "فتح التوصيات",
        ),
        x(
            "ai", "ai-scan-plan", "10.14", "خطة الفحص", "SCAN PLAN",
            "ما الذي سيقرأه الفحص وما حدوده؟", Icons.Rounded.Checklist, V2Cyan,
            metric = { f, _ -> if (f.mediaReady) "وسائط متاحة" else "صلاحية مطلوبة" },
            rows = { f, _ ->
                val last = DeviceFactsCache.latest
                listOf(
                    ToolRow(Icons.Rounded.Checklist, "المرحلة", "قراءة ← بصمات ← مراجعة", V2Cyan),
                    ToolRow(Icons.Rounded.Storage, "حد الملفات", "256 ملفًا · 128 MB", V2Violet),
                    ToolRow(Icons.Rounded.PermMedia, "صلاحية الوسائط", if (f.mediaReady) "ممنوحة" else "مطلوبة", if (f.mediaReady) V2Green else V2Amber),
                    ToolRow(Icons.Rounded.History, "آخر قراءة", last?.let { formatScanTime(it.computedAt) } ?: "—", V2Pink),
                )
            },
            note = "الخطة تعرض حدود المحرك الحقيقية، وليس زمنًا تقديريًا غير مقاس.",
            actionRoute = "deepscan", actionLabel = "فتح الفحص العميق",
        ),
        x(
            "ai", "ai-daily-brief", "10.15", "الملخص الذكي اليومي", "DAILY BRIEF",
            "أهم توصية وأسبابها في بطاقة واحدة", Icons.Rounded.Today, V2Amber,
            metric = { f, _ -> if (f.computedAt > 0L) "جاهز" else "بانتظار القراءة" },
            rows = { f, _ ->
                val rec = aiRecommendations(f).firstOrNull()
                listOf(
                    ToolRow(Icons.Rounded.Today, "العنوان", rec?.title ?: "لا توصية حالية", V2Amber),
                    ToolRow(Icons.Rounded.Bolt, "القيمة", rec?.value ?: "—", V2Pink),
                    ToolRow(Icons.Rounded.Info, "السبب", rec?.reason ?: "لا توجد قراءة كافية بعد", V2Cyan),
                    ToolRow(Icons.Rounded.Verified, "المصدر", "قراءة محلية · بدون سحابة", V2Green),
                )
            },
            note = "الملخص يتغير مع كل قراءة جديدة؛ لا يحفظ أسماء ملفات أو محتوى شخصي.",
            actionRoute = "ai-recommendations", actionLabel = "فتح مركز التوصيات",
        ),
    )

    /* -------------------------------------------------------------- *
     * Owner-requested extra sections: developer tools, Google services,
     * advanced security and backup/restore. Every value below is read
     * from this device through the same DeviceFacts providers.
     * -------------------------------------------------------------- */

    private val developerTools = listOf(
        x(
            "developer", "dev-build", "11.1", "تفاصيل البناء", "BUILD DETAILS",
            "إصدار النظام والمستوى والمعمارية والطراز", Icons.Rounded.Build, V2Cyan,
            metric = { f, _ -> if (f.computedAt > 0L) "Build ${Build.VERSION.SDK_INT}" else "جاري القراءة" },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.Android, "إصدار النظام", Build.VERSION.RELEASE, V2Cyan),
                    ToolRow(Icons.Rounded.Build, "مستوى API", Build.VERSION.SDK_INT.toString(), V2Violet),
                    ToolRow(Icons.Rounded.DeveloperMode, "المعمارية", Build.SUPPORTED_ABIS.firstOrNull() ?: "—", V2Pink),
                    ToolRow(Icons.Rounded.Apps, "تطبيقات مرئية", formatCount(f.installedCount), V2Green),
                )
            },
            note = "كل القيم من android.os.Build على هذا الجهاز. لا يوجد أي رقم مُصطنع.",
        ),
        x(
            "developer", "dev-log", "11.2", "سجل العمليات", "OPERATION JOURNAL",
            "آخر الفحوص المسجلة محليًا في Room", Icons.Rounded.History, V2Violet,
            metric = { _, h -> "${formatCount(h.size)} سجل" },
            rows = { _, h ->
                val recent = h.take(3)
                if (recent.isEmpty()) {
                    listOf(ToolRow(Icons.Rounded.Info, "السجل", "لا عمليات مسجلة بعد", V2Soft))
                } else {
                    recent.map { entry ->
                        ToolRow(Icons.Rounded.Timeline, "فحص مسجل", formatScanTime(entry.timestamp), V2Cyan)
                    }
                }
            },
            note = "السجل محلي على الجهاز فقط ولا يُرفع لأي خادم.",
            list = ToolList.HISTORY,
        ),
        x(
            "developer", "dev-packages", "11.3", "حزم النظام", "SYSTEM PACKAGES",
            "تطبيقات النظام المرئية عبر PackageManager", Icons.Rounded.Apps, V2Cyan,
            metric = { f, _ -> "${formatCount(f.installedCount - f.userAppCount)} نظامي" },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.Apps, "كل الحزم", formatCount(f.installedCount), V2Cyan),
                    ToolRow(Icons.Rounded.Person, "تطبيقات المستخدم", formatCount(f.userAppCount), V2Pink),
                    ToolRow(Icons.Rounded.Android, "تطبيقات النظام", formatCount(f.installedCount - f.userAppCount), V2Violet),
                    ToolRow(Icons.Rounded.Shield, "تطبيقات حساسة", formatCount(f.sensitiveAppCount), V2Amber),
                )
            },
            note = "ن PackageManager يعرض الحزم الظاهرة لهذا الهاتف فقط، بلا أي صلاحية إضافية.",
            list = ToolList.APPS,
        ),
        x(
            "developer", "dev-memory", "11.4", "قراءة الذاكرة", "MEMORY READINGS",
            "حجم الذاكرة المتاحة كما تراه النظام", Icons.Rounded.Memory, V2Violet,
            metric = { f, _ -> formatBytes(f.storage.freeBytes) },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.Storage, "سعة التخزين", formatBytes(f.storage.totalBytes), V2Cyan),
                    ToolRow(Icons.Rounded.Memory, "متاح", formatBytes(f.storage.freeBytes), V2Green),
                    ToolRow(Icons.Rounded.PieChart, "مستخدم", formatBytes(f.storage.usedBytes), V2Pink),
                    ToolRow(Icons.Rounded.Cached, "كاش KNOUX", formatBytes(f.ownCacheBytes), V2Amber),
                )
            },
            note = "StatFs يقرأ مساحة الملفات. الذاكرة الفعلية للعمليات تحتاج UsageStats.",
        ),
        x(
            "developer", "developer-info", "11.5", "معلومات المطور", "ABOUT THE BUILD",
            "هوية التطبيق والإصدار والحقوق", Icons.Rounded.Info, V2Cyan,
            metric = { f, _ -> if (f.computedAt > 0L) "إصدار مثبت" else "جاري القراءة" },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.Verified, "الحزمة", "com.knoux.ai", V2Cyan),
                    ToolRow(Icons.Rounded.Build, "إصدار التطبيق", BuildConfig.VERSION_NAME, V2Violet),
                    ToolRow(Icons.Rounded.Build, "كود البناء", BuildConfig.VERSION_CODE.toString(), V2Pink),
                    ToolRow(Icons.Rounded.Android, "النظام", "Android ${Build.VERSION.RELEASE}", V2Green),
                )
            },
            note = "معلومات الحزمة من BuildConfig المثبت في هذا الـ APK.",
        ),
    )

    private val googleServices = listOf(
        x(
            "googleservices", "gsc-apps", "12.1", "تطبيقات Google", "GOOGLE APPS",
            "حزم Google المرئية على هذا الجهاز", Icons.Rounded.AccountCircle, V2Cyan,
            metric = { f, _ -> "${formatCount(googleAppCount(f))} تطبيق" },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.AccountCircle, "تطبيقات Google", formatCount(googleAppCount(f)), V2Cyan),
                    ToolRow(Icons.Rounded.Apps, "كل التطبيقات", formatCount(f.installedCount), V2Violet),
                    ToolRow(Icons.Rounded.Memory, "حجم الكاش", formatBytes(f.ownCacheBytes), V2Pink),
                    ToolRow(Icons.Rounded.Storage, "مساحة متاحة", formatBytes(f.storage.freeBytes), V2Green),
                )
            },
            note = "العد يتم على أسماء الحزم التي تبدأ بـ com.google. أو com.android.vending فقط.",
            list = ToolList.APPS,
        ),
        x(
            "googleservices", "gsc-settings", "12.2", "إعدادات Play", "PLAY SETTINGS",
            "فتح إعدادات Google Play في النظام", Icons.Rounded.Settings, V2Violet,
            metric = { f, _ -> if (f.computedAt > 0L) "إجراء النظام" else "جاري القراءة" },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.Apps, "تطبيقات مثبتة", formatCount(f.installedCount), V2Cyan),
                    ToolRow(Icons.Rounded.AccountCircle, "حزم Google", formatCount(googleAppCount(f)), V2Violet),
                    ToolRow(Icons.Rounded.Notifications, "الإشعارات", "من إعدادات النظام", V2Pink),
                    ToolRow(Icons.Rounded.Security, "Play Protect", "من إعدادات النظام", V2Green),
                )
            },
            note = "الزر يفتح إعدادات Google Play الرسمية في النظام. KNOUX لا يغيّر إعدادات Protection.",
            handoff = ToolHandoff.NONE,
        ),
        x(
            "googleservices", "gsc-notifications", "12.3", "قنوات الإشعارات", "NOTIFICATION CHANNELS",
            "ما يعرضه النظام عن قنوات الإشعارات", Icons.Rounded.NotificationsActive, V2Cyan,
            metric = { f, _ -> if (f.computedAt > 0L) "قراءة النظام" else "جاري القراءة" },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.NotificationsActive, "إشعارات KNOUX", "من إعدادات التطبيق", V2Cyan),
                    ToolRow(Icons.Rounded.Apps, "تطبيقات مرئية", formatCount(f.installedCount), V2Violet),
                    ToolRow(Icons.Rounded.Shield, "تطبيقات حساسة", formatCount(f.sensitiveAppCount), V2Pink),
                    ToolRow(Icons.Rounded.Storage, "مساحة متاحة", formatBytes(f.storage.freeBytes), V2Green),
                )
            },
            note = "إدارة القنوات تتم من إعدادات نظام Android. KNOUX لا تتلاعب بإشعارات تطبيقات أخرى.",
        ),
        x(
            "googleservices", "gsc-account", "12.4", "حالة الحساب", "ACCOUNT STATE",
            "تسجيل الدخول ومزامنة الحساب كما تراها KNOUX", Icons.Rounded.ManageAccounts, V2Violet,
            metric = { f, _ -> if (f.computedAt > 0L) "محلي" else "جاري القراءة" },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.AccountCircle, "تسجيل الدخول", "من بوابة KNOUX", V2Cyan),
                    ToolRow(Icons.Rounded.Lock, "الجلسات", "محلية على الجهاز", V2Violet),
                    ToolRow(Icons.Rounded.Apps, "تطبيقات مثبتة", formatCount(f.installedCount), V2Pink),
                    ToolRow(Icons.Rounded.Storage, "مساحة متاحة", formatBytes(f.storage.freeBytes), V2Green),
                )
            },
            note = "لا تُخزّن كلمات مرور. الجلسة في Keystore عبر طبقة المصادقة المعتمدة.",
        ),
        x(
            "googleservices", "google-services", "12.5", "دعم Google", "GOOGLE SUPPORT",
            "فتح روابط الدعم الرسمية داخل الجهاز", Icons.Rounded.HelpOutline, V2Cyan,
            metric = { f, _ -> if (f.computedAt > 0L) "روابط النظام" else "جاري القراءة" },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.HelpOutline, "مركز مساعدة Google", "فتح في المتصفح", V2Cyan),
                    ToolRow(Icons.Rounded.AccountCircle, "حزم Google", formatCount(googleAppCount(f)), V2Violet),
                    ToolRow(Icons.Rounded.Apps, "تطبيقات مثبتة", formatCount(f.installedCount), V2Pink),
                    ToolRow(Icons.Rounded.Shield, "تطبيقات حساسة", formatCount(f.sensitiveAppCount), V2Green),
                )
            },
            note = "روابط الدعم تفتح في المتصفح النظامي. KNOUX لا يعترض أي اتصال.",
        ),
    )

    private val advancedSecurity = listOf(
        x(
            "security", "sec-lock", "13.1", "قفل الشاشة", "SCREEN LOCK",
            "إعدادات قفل الجهاز والمصادقة", Icons.Rounded.Lock, V2Green,
            metric = { f, _ -> if (f.computedAt > 0L) "إعداد النظام" else "جاري القراءة" },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.Lock, "قفل الشاشة", "من إعدادات النظام", V2Cyan),
                    ToolRow(Icons.Rounded.Fingerprint, "المصادقة البيومترية", "من إعدادات النظام", V2Violet),
                    ToolRow(Icons.Rounded.Shield, "تطبيقات حساسة", formatCount(f.sensitiveAppCount), V2Pink),
                    ToolRow(Icons.Rounded.Apps, "تطبيقات مثبتة", formatCount(f.installedCount), V2Green),
                )
            },
            note = "KNOUX لا يقرأ بصمة ولا يتجاوز القفل. الزر يفتح إعدادات الأمان الرسمية.",
        ),
        x(
            "security", "sec-accounts", "13.2", "حسابات الجهاز", "DEVICE ACCOUNTS",
            "الحسابات التي يراها Android على هذا الهاتف", Icons.Rounded.ManageAccounts, V2Cyan,
            metric = { f, _ -> if (f.computedAt > 0L) "من النظام" else "جاري القراءة" },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.AccountCircle, "حسابات النظام", "من إعدادات الحسابات", V2Cyan),
                    ToolRow(Icons.Rounded.Person, "تطبيقات المستخدم", formatCount(f.userAppCount), V2Violet),
                    ToolRow(Icons.Rounded.Shield, "تطبيقات حساسة", formatCount(f.sensitiveAppCount), V2Pink),
                    ToolRow(Icons.Rounded.Storage, "مساحة متاحة", formatBytes(f.storage.freeBytes), V2Green),
                )
            },
            note = "لا نطلب بيانات حساب. كل ما يظهر هو ما يسمح به Android بعرضه.",
        ),
        x(
            "security", "sec-permissions", "13.3", "مراجعة الأذونات", "PERMISSION REVIEW",
            "كل الأذونات الحساسة والتطبيقات التي طلبتها", Icons.Rounded.Security, V2Violet,
            metric = { f, _ -> "${formatCount(f.sensitiveAppCount)} تطبيق حساس" },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.Shield, "تطبيقات حساسة", formatCount(f.sensitiveAppCount), V2Cyan),
                    ToolRow(Icons.Rounded.Lock, "مجموعات الأذونات", "${f.privacyGroups.size} مجموعة", V2Violet),
                    ToolRow(Icons.Rounded.Visibility, "وصول الاستخدام", if (f.usageAccessGranted) "ممنوح" else "غير ممنوح", V2Pink),
                    ToolRow(Icons.Rounded.Apps, "تطبيقات مثبتة", formatCount(f.installedCount), V2Green),
                )
            },
            note = "المراجعة من PackageManager مباشرة. لا نطلب صلاحية إضافية لعرض القائمة.",
            list = ToolList.PERMISSIONS,
        ),
        x(
            "security", "sec-privacy-links", "13.4", "روابط الخصوصية", "PRIVACY SHORTCUTS",
            "إعدادات الخصوصية السريعة في النظام", Icons.Rounded.LockPerson, V2Cyan,
            metric = { f, _ -> if (f.computedAt > 0L) "إعداد النظام" else "جاري القراءة" },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.LockPerson, "قفل الجهاز", "من إعدادات النظام", V2Cyan),
                    ToolRow(Icons.Rounded.LocationOn, "الموقع", "من إعدادات الموقع", V2Violet),
                    ToolRow(Icons.Rounded.Shield, "تطبيقات حساسة", formatCount(f.sensitiveAppCount), V2Pink),
                    ToolRow(Icons.Rounded.Storage, "مساحة متاحة", formatBytes(f.storage.freeBytes), V2Green),
                )
            },
            note = "كل الاختصارات تفتح إعدادات Android الرسمية فقط.",
        ),
        x(
            "security", "advanced-security", "13.5", "ملخص الأمان", "SECURITY SUMMARY",
            "ملخص واحد للحالة الأمنية من القراءات الحقيقية", Icons.Rounded.HealthAndSafety, V2Green,
            metric = { f, _ -> if (f.computedAt > 0L) "${f.healthEstimate}% صحة" else "جاري القراءة" },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.HealthAndSafety, "تقدير الجهاز", if (f.healthEstimate > 0) "${f.healthEstimate}%" else "—", V2Cyan),
                    ToolRow(Icons.Rounded.Shield, "تطبيقات حساسة", formatCount(f.sensitiveAppCount), V2Violet),
                    ToolRow(Icons.Rounded.Visibility, "وصول الاستخدام", if (f.usageAccessGranted) "ممنوح" else "غير ممنوح", V2Pink),
                    ToolRow(Icons.Rounded.Storage, "مساحة متاحة", formatBytes(f.storage.freeBytes), V2Green),
                )
            },
            note = "تقدير مبني على قراءات الجهاز فقط. ليس تقييم اختراق ولا كشف برمجيات خبيثة.",
        ),
    )

    private val backupTools = listOf(
        x(
            "backup", "bkp-pick", "14.1", "اختيار مجلد الوجهة", "PICK DESTINATION",
            "تختار مجلدًا عبر منتقي SAF بنفسك", Icons.Rounded.FolderOpen, V2Amber,
            metric = { f, _ -> if (f.computedAt > 0L) formatBytes(f.storage.freeBytes) + " متاحة" else "جاري القراءة" },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.FolderOpen, "متاح", formatBytes(f.storage.freeBytes), V2Cyan),
                    ToolRow(Icons.Rounded.Storage, "سعة", formatBytes(f.storage.totalBytes), V2Violet),
                    ToolRow(Icons.Rounded.Cached, "كاش KNOUX", formatBytes(f.ownCacheBytes), V2Pink),
                    ToolRow(Icons.Rounded.Description, "ملفات KNOUX", "${formatCount(f.ownCacheFiles)} ملف", V2Green),
                )
            },
            note = "الاختيار عبر ACTION_OPEN_DOCUMENT_TREE. لا يُكتب أي ملف قبل موافقتك.",
        ),
        x(
            "backup", "bkp-inventory", "14.2", "جرد ما سيُنسخ", "BACKUP INVENTORY",
            "قائمة صريحة بالملفات المرشحة قبل النسخ", Icons.Rounded.Inventory, V2Cyan,
            metric = { f, _ -> "${formatCount(f.ownCacheFiles)} ملف" },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.Cached, "كاش KNOUX", formatBytes(f.ownCacheBytes), V2Cyan),
                    ToolRow(Icons.Rounded.Description, "ملفات", formatCount(f.ownCacheFiles), V2Violet),
                    ToolRow(Icons.Rounded.PermMedia, "وسائط", if (f.media.permissionGranted) formatCount(f.media.images + f.media.videos) else "بعد المنح", V2Pink),
                    ToolRow(Icons.Rounded.Storage, "مساحة متاحة", formatBytes(f.storage.freeBytes), V2Green),
                )
            },
            note = "الجرد فقط. لا يتم نسخ أو حذف أي ملف من هذه الشاشة.",
        ),
        x(
            "backup", "bkp-log", "14.3", "سجل النسخ", "BACKUP JOURNAL",
            "آخر عمليات النسخ والاستعادة المسجلة", Icons.Rounded.History, V2Violet,
            metric = { _, h -> "${formatCount(h.size)} سجل" },
            rows = { _, h ->
                val recent = h.take(3)
                if (recent.isEmpty()) {
                    listOf(ToolRow(Icons.Rounded.Info, "السجل", "لا عمليات بعد", V2Soft))
                } else {
                    recent.map { entry ->
                        ToolRow(Icons.Rounded.Timeline, "عملية مسجلة", formatScanTime(entry.timestamp), V2Cyan)
                    }
                }
            },
            note = "السجل محلي في Room ويمكن تصديره كـ CSV عند طلبك.",
            list = ToolList.HISTORY,
        ),
        x(
            "backup", "bkp-restore", "14.4", "الاستعادة", "RESTORE FROM SAF",
            "استرجاع نسخة من مجلد تختاره", Icons.Rounded.Restore, V2Cyan,
            metric = { f, _ -> if (f.computedAt > 0L) "اختيار صريح" else "جاري القراءة" },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.Restore, "المصدر", "من مجلد تختاره", V2Cyan),
                    ToolRow(Icons.Rounded.Storage, "سعة الجهاز", formatBytes(f.storage.totalBytes), V2Violet),
                    ToolRow(Icons.Rounded.FolderOpen, "متاح", formatBytes(f.storage.freeBytes), V2Pink),
                    ToolRow(Icons.Rounded.Shield, "الحماية", "كاش KNOUX فقط", V2Green),
                )
            },
            note = "الاستعادة تلمس ملفات KNOUX فقط، وتطلب تأكيدًا قبل الكتابة.",
        ),
        x(
            "backup", "backup-restore", "14.5", "إدارة النسخ", "BACKUP MANAGEMENT",
            "كل مسارات النسخ والاستعادة في مكان واحد", Icons.Rounded.Backup, V2Amber,
            metric = { f, _ -> if (f.computedAt > 0L) "محلي بالكامل" else "جاري القراءة" },
            rows = { f, _ ->
                listOf(
                    ToolRow(Icons.Rounded.Backup, "الوجهة", "SAF · تختارها أنت", V2Cyan),
                    ToolRow(Icons.Rounded.CloudOff, "السحابة", "غير مستخدمة", V2Violet),
                    ToolRow(Icons.Rounded.Cached, "كاش KNOUX", formatBytes(f.ownCacheBytes), V2Pink),
                    ToolRow(Icons.Rounded.Storage, "مساحة متاحة", formatBytes(f.storage.freeBytes), V2Green),
                )
            },
            note = "لا يوجد رفع سحابي تلقائي. كل شيء يبقى بين تطبيقك والمجلد الذي تختاره.",
        ),
    )

    val defs: List<SectionToolDef> =
        duplicates + residue + privacy + deepScan + reports + appManager + storage + battery + ai +
            developerTools + googleServices + advancedSecurity + backupTools
}

/* ------------------------------------------------------------------ *
 * Small formatting helpers used only by the expansion entries.
 * ------------------------------------------------------------------ */

private fun formatDuration(millis: Long): String {
    if (millis <= 0L) return "—"
    val hours = millis / 3_600_000L
    val days = hours / 24L
    return when {
        days > 0 -> "$days يومًا"
        hours > 0 -> "$hours ساعة"
        else -> "${(millis / 60_000L).coerceAtLeast(1L)} دقيقة"
    }
}

private fun signedCount(value: Int): String = (if (value >= 0) "+" else "−") + abs(value)
