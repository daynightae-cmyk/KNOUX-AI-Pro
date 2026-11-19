package com.knoux.ai.ui.v2

import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.Audiotrack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.BatteryAlert
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.BatteryFull
import androidx.compose.material.icons.rounded.BatterySaver
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Compare
import androidx.compose.material.icons.rounded.ContactPhone
import androidx.compose.material.icons.rounded.CreateNewFolder
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material.icons.rounded.DonutLarge
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.FactCheck
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.FindInPage
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.GppGood
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Inventory
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.ManageSearch
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.QueryStats
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.Rule
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.SimCard
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Summarize
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Usb
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.VideoLibrary
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/* ------------------------------------------------------------------ *
 * Nine sections × fifteen services.
 *
 * Every value below is produced by a reader in DeviceReality at run time.
 * The catalog only describes which live facts belong to which service.
 * ------------------------------------------------------------------ */

private fun tr(icon: ImageVector, label: String, value: String, accent: Color) =
    ToolRow(icon, label, value, accent)

private fun percent(f: DeviceFacts): String = "${(f.storage.usedFraction * 100).toInt()}%"

private fun mediaSummary(f: DeviceFacts): String =
    if (f.media.permissionGranted) {
        "${formatCount(f.media.images + f.media.videos)} ملفًا · ${formatBytes(f.media.imagesBytes + f.media.videosBytes)}"
    } else {
        "بانتظار صلاحية الوسائط"
    }

private fun sensitiveApps(f: DeviceFacts): Int =
    f.installedApps.count { it.sensitivePermissions.isNotEmpty() }

private fun lastScan(h: List<ScanHistoryEntry>): ScanHistoryEntry? = newestScan(h)

private fun d(
    section: String,
    route: String,
    order: String,
    title: String,
    english: String,
    tagline: String,
    icon: ImageVector,
    accent: Color,
    pro: Boolean = false,
    metric: (DeviceFacts, List<ScanHistoryEntry>) -> String = { f, _ ->
        if (f.computedAt > 0L) "${formatBytes(f.storage.freeBytes)} متاحة" else "جاري القراءة"
    },
    rows: (DeviceFacts, List<ScanHistoryEntry>) -> List<ToolRow> = { _, _ -> emptyList() },
    note: String = "",
    list: ToolList = ToolList.NONE,
    behavior: ToolBehavior = ToolBehavior.NONE,
    handoff: ToolHandoff = ToolHandoff.NONE,
    action: String? = null,
    actionLabel: String? = null,
) = SectionToolDef(
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
    actionRoute = action,
    actionLabel = actionLabel,
)

internal object SectionToolsCatalog {

    private val duplicates = listOf(
        d(
            "duplicates", "dup-scope", "02.1", "نطاق الاكتشاف", "SCAN SCOPE",
            "ما يقرأه فحص التكرار على هذا الهاتف", Icons.Rounded.Folder, V2Violet,
            metric = { f, _ -> percent(f) + " مستخدمة" },
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Storage, "التخزين المستخدم", formatBytes(f.storage.usedBytes), V2Cyan),
                    tr(Icons.Rounded.Image, "الصور", if (f.media.permissionGranted) "${formatCount(f.media.images)} · ${formatBytes(f.media.imagesBytes)}" else "بعد المنح", V2Pink),
                    tr(Icons.Rounded.VideoLibrary, "الفيديو", if (f.media.permissionGranted) "${formatCount(f.media.videos)} · ${formatBytes(f.media.videosBytes)}" else "بعد المنح", V2Violet),
                    tr(Icons.Rounded.Layers, "مجموعات التكرار", "${formatCount(f.media.duplicateGroups.size)} مجموعة", V2Amber),
                )
            },
            note = "النطاق الحالي هو مكتبة الوسائط التي منحت صلاحيتها. KNOUX لا يفتح مجلدات تطبيقات أخرى.",
            action = "duplicates", actionLabel = "فتح شاشة التكرار",
        ),
        d(
            "duplicates", "dup-types", "02.2", "فلترة أنواع الملفات", "FILE TYPE FILTERS",
            "الصور والفيديو فقط هما ما تقرأه MediaStore هنا", Icons.Rounded.Category, V2Pink,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Image, "صور", if (f.media.permissionGranted) formatCount(f.media.images) else "بعد المنح", V2Pink),
                    tr(Icons.Rounded.VideoLibrary, "فيديو", if (f.media.permissionGranted) formatCount(f.media.videos) else "بعد المنح", V2Violet),
                    tr(Icons.Rounded.Audiotrack, "صوتيات", "غير مفهرسة في هذه النسخة", V2Muted),
                    tr(Icons.Rounded.Description, "مستندات", "خارج نطاق MediaStore الحالي", V2Muted),
                )
            },
            note = "نُظهر الصور والفيديو فقط لأن استعلام MediaStore الحالي لا يطلب الصوتيات أو المستندات. لا ندّعي فحصها.",
            list = ToolList.FILES,
        ),
        d(
            "duplicates", "dup-min-size", "02.3", "الحد الأدنى للحجم", "MINIMUM SIZE FILTER",
            "اعرض فقط الملفات التي تتجاوز حدًا فعليًا", Icons.Rounded.FilterAlt, V2Amber,
            rows = { f, _ ->
                val files = f.media.largestFiles
                listOf(
                    tr(Icons.Rounded.Storage, "أكبر ملف", files.maxOfOrNull { it.sizeBytes }?.let(::formatBytes) ?: "لا ملفات", V2Amber),
                    tr(Icons.Rounded.Layers, "ملفات مفهرسة", "${formatCount(files.size)} في القائمة الحالية", V2Cyan),
                    tr(Icons.Rounded.Compare, "أصغر ملف", files.minOfOrNull { it.sizeBytes }?.let(::formatBytes) ?: "لا ملفات", V2Soft),
                )
            },
            note = "الحد يطبق على قائمة أكبر الملفات التي أعادها MediaStore فعلًا. الملفات الأصغر من الحد تختفي من القائمة.",
            list = ToolList.FILES, behavior = ToolBehavior.SIZE_FILTER_MEDIA,
        ),
        d(
            "duplicates", "dup-scan", "02.4", "فحص الملفات المتطابقة", "EXACT MATCHING SCAN",
            "مطابقة محتوى الوسائط ببصمة SHA-256", Icons.Rounded.Radar, V2Violet,
            pro = true,
            metric = { f, _ -> "${formatCount(f.media.duplicateGroups.size)} مجموعة" },
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Layers, "مجموعات مطابقة", formatCount(f.media.duplicateGroups.size), V2Violet),
                    tr(Icons.Rounded.Inventory, "نسخ مرشحة", formatCount(f.media.duplicateCandidates), V2Pink),
                    tr(Icons.Rounded.Savings, "مساحة قابلة للاسترجاع", formatBytes(f.media.duplicateGroups.sumOf { it.reclaimableBytes }), V2Green),
                    tr(Icons.Rounded.Info, "قاعدة المطابقة", "محتوى مطابق · SHA-256", V2Amber),
                )
            },
            note = "الفحص محدود إلى 256 ملفًا و128 MB إجمالًا، وبحد 32 MB للملف.",
            action = "duplicates", actionLabel = "فتح شاشة التكرار",
        ),
        d(
            "duplicates", "dup-groups", "02.5", "تجميع النسخ المكررة", "DUPLICATE GROUPS",
            "كل مجموعة كما أعادها MediaStore الآن", Icons.Rounded.Layers, V2Cyan,
            metric = { f, _ -> formatCount(f.media.duplicateGroups.size) + " مجموعة" },
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Layers, "المجموعات", formatCount(f.media.duplicateGroups.size), V2Cyan),
                    tr(Icons.Rounded.Inventory, "إجمالي النسخ", formatCount(f.media.duplicateCandidates), V2Pink),
                    tr(Icons.Rounded.Savings, "قابل للاسترجاع", formatBytes(f.media.duplicateGroups.sumOf { it.reclaimableBytes }), V2Green),
                )
            },
            list = ToolList.GROUPS,
            note = "المجموعات محسوبة من فهرس الوسائط الحالي. أي ملف جديد يظهر بعد تحديث القراءة.",
        ),
        d(
            "duplicates", "dup-compare", "02.6", "معاينة الملفات للمقارنة", "FILE PREVIEW COMPARE",
            "أكبر الملفات مع الحجم والنوع والمجلد", Icons.Rounded.Compare, V2Pink,
            pro = true,
            list = ToolList.FILES,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.PhotoLibrary, "ملفات قابلة للمعاينة", if (f.media.permissionGranted) formatCount(f.media.largestFiles.size) else "بعد المنح", V2Pink),
                    tr(Icons.Rounded.Image, "إجمالي الصور", if (f.media.permissionGranted) formatBytes(f.media.imagesBytes) else "بعد المنح", V2Violet),
                    tr(Icons.Rounded.VideoLibrary, "إجمالي الفيديو", if (f.media.permissionGranted) formatBytes(f.media.videosBytes) else "بعد المنح", V2Cyan),
                )
            },
            note = "القائمة هي أكبر الملفات التي أعادها MediaStore. المقارنة جنبًا إلى جنب تحتاج فتح شاشة التكرار.",
            action = "duplicates", actionLabel = "فتح المقارنة الكاملة",
        ),
        d(
            "duplicates", "dup-keeper", "02.7", "تحديد النسخة الأصلية", "KEEPER SELECTION",
            "اقتراح النسخة التي تبقى قبل أي حذف", Icons.Rounded.CheckCircle, V2Green,
            pro = true,
            list = ToolList.GROUPS,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Verified, "قاعدة الاقتراح", "النسخة الأولى في كل مجموعة", V2Green),
                    tr(Icons.Rounded.Layers, "مجموعات تحتاج قرارًا", formatCount(f.media.duplicateGroups.size), V2Violet),
                    tr(Icons.Rounded.Savings, "أقصى استرجاع", formatBytes(f.media.duplicateGroups.sumOf { it.reclaimableBytes }), V2Pink),
                )
            },
            note = "KNOUX لا يعدّل ملفاتك ليختار الأصل. الاقتراح للعرض فقط، والقرار النهائي لك في شاشة الحذف.",
            action = "duplicates", actionLabel = "مراجعة المجموعات",
        ),
        d(
            "duplicates", "dup-exclusions", "02.8", "حماية النسخ المستثناة", "PROTECTED EXCLUSIONS",
            "ما يمنعه Android وما لا يلمسه KNOUX", Icons.Rounded.Shield, V2Green,
            pro = true,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Folder, "محمي بواسطة Scoped Storage", "Android/data", V2Amber),
                    tr(Icons.Rounded.Folder, "محمي بواسطة Scoped Storage", "Android/obb", V2Amber),
                    tr(Icons.Rounded.Lock, "مجلد KNOUX الخاص", "${formatBytes(f.ownCacheBytes)} · ${formatCount(f.ownCacheFiles)} ملف", V2Cyan),
                    tr(Icons.Rounded.CheckCircle, "ملفات أخرى لتطبيقات", "غير مقروءة أصلًا", V2Green),
                )
            },
            note = "الاستثناءات هنا ليست تفضيلًا محليًا فقط: Android نفسه يمنع الوصول إلى بيانات التطبيقات الأخرى.",
        ),
        d(
            "duplicates", "dup-review", "02.9", "مراجعة النسخ للحذف", "REMOVAL REVIEW",
            "راجع المجموعات قبل فتح حوار الحذف الرسمي", Icons.Rounded.Rule, V2Violet,
            pro = true,
            list = ToolList.GROUPS,
            metric = { f, _ -> formatBytes(f.media.duplicateGroups.sumOf { it.reclaimableBytes }) + " قابلة" },
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Layers, "مجموعات للمراجعة", formatCount(f.media.duplicateGroups.size), V2Violet),
                    tr(Icons.Rounded.Inventory, "نسخ محتملة", formatCount(f.media.duplicateCandidates), V2Pink),
                    tr(Icons.Rounded.Delete, "المحدد حاليًا", "لا شيء قبل تأكيدك", V2Green),
                )
            },
            note = "لا يوجد حذف جماعي صامت. كل عملية تمر عبر شاشة التكرار ثم حوار MediaStore الرسمي.",
            action = "duplicates", actionLabel = "فتح المراجعة",
        ),
        d(
            "duplicates", "dup-confirm", "02.10", "تأكيد العملية والنتائج", "CONFIRM & VERIFY RESULTS",
            "الحذف يمر دائمًا عبر حوار Android الرسمي", Icons.Rounded.FactCheck, V2Pink,
            rows = { f, h ->
                listOf(
                    tr(Icons.Rounded.Savings, "المساحة المرشحة", formatBytes(f.media.duplicateGroups.sumOf { it.reclaimableBytes }), V2Pink),
                    tr(Icons.Rounded.Layers, "المجموعات", formatCount(f.media.duplicateGroups.size), V2Violet),
                    tr(Icons.Rounded.History, "آخر فحص", lastScan(h)?.let { formatScanTime(it.timestamp) } ?: "لا سجل بعد", V2Cyan),
                )
            },
            note = "بعد موافقتك يعيد Android نتيجة الحذف. KNOUX لا يخترع رقمًا للمساحة المحررة قبل أن يؤكدها النظام.",
            action = "duplicates", actionLabel = "فتح حوار الحذف",
        ),
    )

    private val residue = listOf(
        d(
            "residue", "res-sources", "03.1", "مصادر الملفات المسموحة", "ALLOWED SOURCES",
            "أين يقرأ KNOUX بقايا التطبيقات فعلًا", Icons.Rounded.Folder, V2Pink,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.CleaningServices, "كاش KNOUX", "${formatBytes(f.ownCacheBytes)} · ${formatCount(f.ownCacheFiles)} ملف", V2Pink),
                    tr(Icons.Rounded.Storage, "التخزين الكلي", formatBytes(f.storage.usedBytes) + " مستخدم", V2Cyan),
                    tr(Icons.Rounded.Image, "وسائط مقروءة", mediaSummary(f), V2Violet),
                    tr(Icons.Rounded.Lock, "بيانات تطبيقات أخرى", "محمية بواسطة Android", V2Amber),
                )
            },
            note = "المصادر المسموحة هي كاش KNOUX ومكتبة الوسائط المصرح بها. لا يوجد تجاوز لجدار Android.",
        ),
        d(
            "residue", "res-scan", "03.2", "فحص بقايا التطبيقات", "RESIDUE SCAN",
            "قراءة ما يمكن حذفه من كاش KNOUX الآن", Icons.Rounded.Radar, V2Cyan,
            metric = { f, _ -> formatBytes(f.ownCacheBytes) },
            rows = { f, h ->
                listOf(
                    tr(Icons.Rounded.CleaningServices, "كاش KNOUX", formatBytes(f.ownCacheBytes), V2Pink),
                    tr(Icons.Rounded.Description, "ملفات الكاش", formatCount(f.ownCacheFiles), V2Violet),
                    tr(Icons.Rounded.Layers, "وسائط مكررة", formatCount(f.media.duplicateGroups.size), V2Amber),
                    tr(Icons.Rounded.History, "آخر قياس", lastScan(h)?.let { formatScanTime(it.timestamp) } ?: "لا سجل بعد", V2Cyan),
                )
            },
            note = "الفحص لا يرى داخل تطبيقات أخرى. ما يظهر هنا هو كاش KNOUX ومؤشرات الوسائط فقط.",
            action = "residue", actionLabel = "فتح تنظيف البقايا",
        ),
        d(
            "residue", "res-candidates", "03.3", "عرض الملفات المرشحة", "CANDIDATE RESULTS",
            "المرشحون الحقيقيون قبل أي إزالة", Icons.Rounded.Inventory, V2Violet,
            pro = true,
            metric = { f, _ -> "${formatCount(f.ownCacheFiles)} ملفًا" },
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.CleaningServices, "كاش KNOUX", formatBytes(f.ownCacheBytes), V2Pink),
                    tr(Icons.Rounded.Storage, "نسبة من التخزين", if (f.storage.usedBytes > 0) "${(f.ownCacheBytes * 100 / f.storage.usedBytes)}%" else "—", V2Cyan),
                    tr(Icons.Rounded.Layers, "مجموعات وسائط", formatCount(f.media.duplicateGroups.size), V2Violet),
                    tr(Icons.Rounded.Savings, "وسائط قابلة للاسترجاع", formatBytes(f.media.duplicateGroups.sumOf { it.reclaimableBytes }), V2Green),
                )
            },
            note = "قائمة الملفات الفردية داخل كاش KNOUX تُعرض في شاشة تنظيف البقايا. هنا الملخص الحي فقط.",
            action = "residue", actionLabel = "عرض المرشحين",
        ),
        d(
            "residue", "res-groups", "03.4", "تجميع المرشحات حسب المصدر", "SOURCE GROUPING",
            "كاش أم وسائط أم محمي", Icons.Rounded.Layers, V2Cyan,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.CleaningServices, "كاش KNOUX", formatBytes(f.ownCacheBytes), V2Pink),
                    tr(Icons.Rounded.Image, "صور", if (f.media.permissionGranted) formatBytes(f.media.imagesBytes) else "بعد المنح", V2Violet),
                    tr(Icons.Rounded.VideoLibrary, "فيديو", if (f.media.permissionGranted) formatBytes(f.media.videosBytes) else "بعد المنح", V2Cyan),
                    tr(Icons.Rounded.Storage, "النظام والتطبيقات", formatBytes(f.storage.usedBytes - f.ownCacheBytes), V2Amber),
                )
            },
            note = "التجميع يعتمد على مصدر القراءة لا على تخمين اسم المجلد. لا تُنسب مساحة إلى تطبيق لا نقرأه.",
        ),
        d(
            "residue", "res-link", "03.5", "تحليل ارتباط الملف بالتطبيق", "APP LINK ANALYSIS",
            "ما نعرفه وما لا نستطيع إثباته", Icons.Rounded.TrackChanges, V2Violet,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Apps, "حزمة KNOUX", "com.knoux.ai.debug", V2Violet),
                    tr(Icons.Rounded.Folder, "مجلدات الكاش", "cache · code_cache · external_cache", V2Cyan),
                    tr(Icons.Rounded.CleaningServices, "ملفات مرتبطة", formatCount(f.ownCacheFiles), V2Pink),
                    tr(Icons.Rounded.Search, "ملف وسائط أكبر", f.media.largestFiles.firstOrNull()?.name ?: "لا ملفات", V2Soft),
                )
            },
            note = "لا نستنتج أن ملفًا يخص تطبيقًا آخر من اسمه. الارتباط هنا مؤكد فقط داخل مجلدات KNOUX.",
        ),
        d(
            "residue", "res-detail", "03.6", "معاينة تفاصيل البقايا", "RESIDUE DETAIL PREVIEW",
            "حجم وعدد ومسار ما يمكن مراجعته", Icons.Rounded.Description, V2Cyan,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.CleaningServices, "حجم الكاش", formatBytes(f.ownCacheBytes), V2Pink),
                    tr(Icons.Rounded.Description, "عدد الملفات", formatCount(f.ownCacheFiles), V2Violet),
                    tr(Icons.Rounded.Storage, "متاح بعد التنظيف النظري", formatBytes(f.storage.freeBytes + f.ownCacheBytes), V2Green),
                    tr(Icons.Rounded.Info, "نوع البيانات", "ملفات مؤقتة يعيد التطبيق إنشاءها", V2Amber),
                )
            },
            note = "المتاح بعد التنظيف قيمة نظرية تعتمد على نجاح الحذف. لا نعرضها كوعد.",
            action = "residue-item", actionLabel = "فتح مراجعة العنصر",
        ),
        d(
            "residue", "res-risk", "03.7", "تصنيف المخاطر قبل التنظيف", "RISK CLASSIFICATION",
            "آمن أم متوسط أم محمي", Icons.Rounded.Security, V2Amber,
            pro = true,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.CheckCircle, "آمن", "${formatCount(f.ownCacheFiles)} ملف كاش KNOUX", V2Green),
                    tr(Icons.Rounded.WarningAmber, "متوسط", "${formatCount(f.media.duplicateGroups.size)} مجموعة وسائط تحتاج مراجعة", V2Amber),
                    tr(Icons.Rounded.Lock, "محمي", "بيانات التطبيقات الأخرى", V2Pink),
                )
            },
            note = "التصنيف يشرح سبب كل فئة. الملفات المحمية لا تظهر كمرشح حذف أصلًا.",
        ),
        d(
            "residue", "res-exclusions", "03.8", "استثناء الملفات الآمنة", "SAFE EXCLUSIONS",
            "ما لن يلمسه التنظيف", Icons.Rounded.Shield, V2Green,
            pro = true,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Folder, "مستثنى بواسطة النظام", "Android/data", V2Amber),
                    tr(Icons.Rounded.Folder, "مستثنى بواسطة النظام", "Android/obb", V2Amber),
                    tr(Icons.Rounded.Download, "التنزيلات", "تُراجع يدويًا في شاشتها", V2Cyan),
                    tr(Icons.Rounded.Lock, "بيانات KNOUX الدائمة", "خارج مجلد الكاش", V2Green),
                )
            },
            note = "الاستثناءات هنا ليست قائمة تجميلية: هذه مسارات لا يمنح Android تطبيقًا عاديًا حق قراءتها.",
        ),
        d(
            "residue", "res-confirm", "03.9", "تحديد البقايا وتأكيد الإزالة", "CLEANUP CONFIRMATION",
            "قرارك قبل أي مسح فعلي", Icons.Rounded.DeleteSweep, V2Pink,
            pro = true,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.CleaningServices, "كاش KNOUX", formatBytes(f.ownCacheBytes), V2Pink),
                    tr(Icons.Rounded.Savings, "أثر فوري", "يتحرر بعد تأكيدك", V2Green),
                    tr(Icons.Rounded.Layers, "وسائط تحتاج حوارًا منفصلًا", formatCount(f.media.duplicateGroups.size), V2Violet),
                )
            },
            note = "مسح الكاش لا يمس صورك أو فيديوهاتك. حذف الوسائط له مسار منفصل وحوار MediaStore رسمي.",
            action = "residue", actionLabel = "فتح تأكيد التنظيف",
        ),
        d(
            "residue", "res-history", "03.10", "إعادة الفحص وسجل العمليات", "RE-SCAN & HISTORY",
            "كل قياس محفوظ محليًا على هذا الهاتف", Icons.Rounded.History, V2Green,
            pro = true,
            list = ToolList.HISTORY,
            rows = { _, h ->
                listOf(
                    tr(Icons.Rounded.History, "قياسات محفوظة", "${h.size}/24", V2Green),
                    tr(Icons.Rounded.CleaningServices, "آخر كاش", lastScan(h)?.let { formatBytes(it.cacheBytes) } ?: "لا سجل", V2Pink),
                    tr(Icons.Rounded.Storage, "آخر استخدام", lastScan(h)?.let { formatBytes(it.storageUsedBytes) } ?: "لا سجل", V2Cyan),
                )
            },
            note = "السجل محلي داخل التطبيق ولا يُرفع لأي خادم. إعادة الفحص تضيف قياسًا جديدًا.",
        ),
    )

    private val privacy = listOf(
        d(
            "privacy", "prv-overview", "04.1", "لوحة حالة الخصوصية", "PRIVACY OVERVIEW",
            "ملخص الأذونات الحساسة المقروءة من PackageManager", Icons.Rounded.Shield, V2Amber,
            metric = { f, _ -> "${formatCount(f.sensitiveAppCount)} تطبيقًا حساسًا" },
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Apps, "تطبيقات بأذونات حساسة", formatCount(f.sensitiveAppCount), V2Pink),
                    tr(Icons.Rounded.Layers, "مجموعات الأذونات", formatCount(f.privacyGroups.size), V2Amber),
                    tr(Icons.Rounded.QueryStats, "وصول الاستخدام", if (f.usageAccessGranted) "ممنوح" else "غير ممنوح", V2Green),
                    tr(Icons.Rounded.History, "آخر قراءة", if (f.computedAt > 0L) formatScanTime(f.computedAt) else "جاري", V2Cyan),
                )
            },
            note = "هذه قراءة تعريفات الأذونات وحالة المنح، وليست مراقبة استخدام فعلية في الخلفية.",
        ),
        d(
            "privacy", "prv-modules", "04.2", "اختيار وحدات الفحص", "SCAN MODULES",
            "الوحدات التي يقرأها KNOUX فعلًا", Icons.Rounded.Dashboard, V2Violet,
            list = ToolList.PERMISSIONS,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.CameraAlt, "الكاميرا والميكروفون", "وحدتان مقروءتان", V2Pink),
                    tr(Icons.Rounded.Place, "الموقع", "مجموعة قراءة واحدة", V2Amber),
                    tr(Icons.Rounded.Folder, "الملفات", "مجموعة قراءة واحدة", V2Cyan),
                    tr(Icons.Rounded.NotificationsActive, "الإشعارات", "حالة الإذن فقط", V2Violet),
                )
            },
            note = "الوحدات محدودة بما يسمح Android بقراءته دون صلاحيات جذر أو وصول استخدام إضافي.",
        ),
        d(
            "privacy", "prv-permissions", "04.3", "عرض صلاحيات التطبيقات", "APP PERMISSIONS",
            "الأذونات المعلنة والممنوحة لكل مجموعة", Icons.Rounded.Rule, V2Cyan,
            list = ToolList.PERMISSIONS,
            rows = { f, _ ->
                val total = f.privacyGroups.sumOf { it.appCount }
                val granted = f.privacyGroups.sumOf { it.grantedCount }
                listOf(
                    tr(Icons.Rounded.Apps, "إجمالي الإشارات", formatCount(total), V2Cyan),
                    tr(Icons.Rounded.Verified, "ممنوحة", formatCount(granted), V2Green),
                    tr(Icons.Rounded.WarningAmber, "معلنة وغير ممنوحة", formatCount((total - granted).coerceAtLeast(0)), V2Amber),
                )
            },
            note = "العدّ يكرر التطبيق إذا طلب أكثر من إذن. استخدم مجموعة الأذونات لمعرفة العدد لكل نوع.",
        ),
        d(
            "privacy", "prv-ranking", "04.4", "مراجعة التطبيقات حسب المخاطر", "RISK RANKING",
            "ترتيب حسب عدد الأذونات الحساسة المعلنة", Icons.Rounded.BarChart, V2Pink,
            pro = true,
            list = ToolList.PERMISSIONS,
            rows = { f, _ ->
                val ranked = f.installedApps
                    .filter { it.sensitivePermissions.isNotEmpty() }
                    .sortedByDescending { it.sensitivePermissions.size }
                listOf(
                    tr(Icons.Rounded.WarningAmber, "الأعلى", ranked.firstOrNull()?.let { "${it.label} · ${it.sensitivePermissions.size}" } ?: "لا تطبيقات", V2Pink),
                    tr(Icons.Rounded.Apps, "تطبيقات لديها أذونات", formatCount(ranked.size), V2Amber),
                    tr(Icons.Rounded.Layers, "متوسط الأذونات", if (ranked.isNotEmpty()) "${ranked.sumOf { it.sensitivePermissions.size } / ranked.size}" else "—", V2Violet),
                )
            },
            note = "الترتيب يعتمد على عدد الأذونات المعلنة، وليس على سلوك مرصود. لا نصنّف تطبيقًا بأنه خبيث.",
            action = "privacy-risk", actionLabel = "فتح تحليل المخاطر",
        ),
        d(
            "privacy", "prv-app-risk", "04.5", "تفاصيل مخاطر التطبيق", "APP RISK DETAIL",
            "أعلى التطبيقات حساسية على هذا الهاتف", Icons.Rounded.Visibility, V2Violet,
            pro = true,
            metric = { f, _ -> "${formatCount(sensitiveApps(f))} تطبيقًا" },
            rows = { f, _ ->
                f.installedApps
                    .filter { it.sensitivePermissions.isNotEmpty() }
                    .sortedByDescending { it.sensitivePermissions.size }
                    .take(5)
                    .map { tr(Icons.Rounded.Apps, it.label, "${it.sensitivePermissions.size} أذونات حساسة", V2Violet) }
            },
            note = "التفاصيل تعرض الأذونات المعلنة من الحزمة. الاستخدام الفعلي يحتاج صلاحية Usage Access ولم تُمنح بعد.",
            action = "privacy-risk", actionLabel = "فتح التفاصيل",
        ),
        d(
            "privacy", "prv-access", "04.6", "تحليل نطاق الوصول", "ACCESS SCOPE ANALYSIS",
            "كم تطبيقًا يصل إلى كل مجموعة", Icons.Rounded.ManageSearch, V2Amber,
            pro = true,
            list = ToolList.PERMISSIONS,
            rows = { f, _ ->
                f.privacyGroups.take(4).map { group ->
                    tr(Icons.Rounded.Lock, group.label, "${group.grantedCount} ممنوح من ${group.appCount}", V2Amber)
                }
            },
            note = "نطاق الوصول هنا هو حالة الإذن، وليس سجلًا زمنيًا لاستخدام الكاميرا أو الموقع.",
        ),
        d(
            "privacy", "prv-camera", "04.7", "الكاميرا والميكروفون", "CAMERA & MICROPHONE",
            "الأذونات الممنوحة لأخطر حساسين", Icons.Rounded.CameraAlt, V2Pink,
            rows = { f, _ ->
                val camera = f.privacyGroups.firstOrNull { it.key == "camera" }
                val mic = f.privacyGroups.firstOrNull { it.key == "microphone" }
                listOf(
                    tr(Icons.Rounded.CameraAlt, "الكاميرا", camera?.let { "${it.grantedCount}/${it.appCount} ممنوح" } ?: "غير مقروء", V2Pink),
                    tr(Icons.Rounded.Mic, "الميكروفون", mic?.let { "${it.grantedCount}/${it.appCount} ممنوح" } ?: "غير مقروء", V2Violet),
                    tr(Icons.Rounded.Verified, "إجمالي الممنوح", "${(camera?.grantedCount ?: 0) + (mic?.grantedCount ?: 0)} تطبيقًا", V2Amber),
                )
            },
            note = "لا يمكن معرفة متى استخدم تطبيق الكاميرا من دون صلاحية Usage Access أو سجل النظام.",
            handoff = ToolHandoff.PRIVACY_SETTINGS, actionLabel = "فتح إعدادات الخصوصية",
        ),
        d(
            "privacy", "prv-location", "04.8", "الموقع والملفات", "LOCATION & FILES",
            "أذونات الموقع والوصول إلى الملفات", Icons.Rounded.Place, V2Cyan,
            rows = { f, _ ->
                val location = f.privacyGroups.firstOrNull { it.key == "location" }
                val files = f.privacyGroups.firstOrNull { it.key == "files" }
                listOf(
                    tr(Icons.Rounded.Place, "الموقع", location?.let { "${it.grantedCount}/${it.appCount} ممنوح" } ?: "غير مقروء", V2Cyan),
                    tr(Icons.Rounded.Folder, "الملفات", files?.let { "${it.grantedCount}/${it.appCount} ممنوح" } ?: "غير مقروء", V2Amber),
                    tr(Icons.Rounded.Image, "مكتبة KNOUX", mediaSummary(f), V2Violet),
                )
            },
            note = "إذن الملفات هنا هو إذن القراءة. KNOUX لا يطلب كتابة أو حذفًا خارج حوارات النظام.",
            handoff = ToolHandoff.PRIVACY_SETTINGS, actionLabel = "فتح إعدادات الأذونات",
        ),
        d(
            "privacy", "prv-settings", "04.9", "معالجة المخاطر عبر إعدادات Android", "ANDROID SETTINGS HANDOFF",
            "التحويل إلى شاشات النظام الرسمية", Icons.Rounded.OpenInNew, V2Green,
            pro = true,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Settings, "إعدادات الخصوصية", "شاشة Android الرسمية", V2Green),
                    tr(Icons.Rounded.QueryStats, "وصول الاستخدام", if (f.usageAccessGranted) "ممنوح" else "يُفتح من النظام", V2Amber),
                    tr(Icons.Rounded.Apps, "كل التطبيقات", "إدارة أذونات لكل تطبيق", V2Cyan),
                )
            },
            note = "لا يغيّر KNOUX إذنًا نيابة عنك. التحويل يعرض شاشة Android حيث تمنح أو تسحب بنفسك.",
            handoff = ToolHandoff.PRIVACY_SETTINGS, actionLabel = "فتح إعدادات Android",
        ),
        d(
            "privacy", "prv-results", "04.10", "مراجعة نتائج فحص الخصوصية", "SCAN RESULTS",
            "ملخص ما قرأه الفحص في هذه الجلسة", Icons.Rounded.FactCheck, V2Cyan,
            pro = true,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Apps, "تطبيقات مفحوصة", formatCount(f.installedCount), V2Cyan),
                    tr(Icons.Rounded.WarningAmber, "تطبيقات حساسة", formatCount(f.sensitiveAppCount), V2Pink),
                    tr(Icons.Rounded.Layers, "مجموعات أذونات", formatCount(f.privacyGroups.size), V2Amber),
                    tr(Icons.Rounded.QueryStats, "وصول الاستخدام", if (f.usageAccessGranted) "ممنوح" else "غير ممنوح", V2Violet),
                )
            },
            note = "هذه نتيجة قراءة لحظية وليست تقريرًا تاريخيًا. السجل الزمني يظهر في قسم التقارير.",
            action = "privacy-risk", actionLabel = "فتح تحليل النتائج",
        ),
    )

    private val deepScan = listOf(
        d(
            "deepscan", "dsn-type", "05.1", "اختيار نوع الفحص", "SCAN TYPE",
            "فحص كامل أم سريع أم مخصص", Icons.Rounded.Tune, V2Cyan,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Shield, "فحص شامل", "التخزين والتطبيقات والخصوصية والبطارية", V2Cyan),
                    tr(Icons.Rounded.Bolt, "فحص سريع", "يحفظ قياسًا فوريًا في السجل", V2Amber),
                    tr(Icons.Rounded.Settings, "فحص مخصص", "وحدات مختارة داخل شاشة الفحص", V2Violet),
                    tr(Icons.Rounded.Apps, "تطبيقات متاحة", formatCount(f.installedCount), V2Pink),
                )
            },
            note = "الأنواع الثلاثة تستخدم نفس القراءات الحقيقية. الاختلاف في الوحدات التي تُعرض والوقت.",
            action = "deepscan", actionLabel = "فتح شاشة الفحص",
        ),
        d(
            "deepscan", "dsn-modules", "05.2", "اختيار وحدات التشخيص", "SCAN MODULES",
            "ما الذي يقرأه كل تشخيص", Icons.Rounded.Layers, V2Violet,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Apps, "التطبيقات", "${formatCount(f.installedCount)} تطبيقًا", V2Pink),
                    tr(Icons.Rounded.Image, "الوسائط", mediaSummary(f), V2Violet),
                    tr(Icons.Rounded.Shield, "الخصوصية", "${formatCount(f.sensitiveAppCount)} تطبيقًا حساسًا", V2Amber),
                    tr(Icons.Rounded.BatteryFull, "البطارية", f.battery.levelPercent?.let { "$it%" } ?: "غير مقروءة", V2Green),
                )
            },
            note = "كل وحدة تقرأ من واجهة Android الخاصة بها. لا توجد وحدات تعرض بيانات وهمية.",
        ),
        d(
            "deepscan", "dsn-scope", "05.3", "تحديد نطاق الملفات", "SCOPE SELECTION",
            "ما يدخل في الفحص وما يبقى خارجه", Icons.Rounded.FolderOpen, V2Cyan,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Storage, "إجمالي التخزين", formatBytes(f.storage.totalBytes), V2Cyan),
                    tr(Icons.Rounded.Storage, "المستخدم", "${formatBytes(f.storage.usedBytes)} · ${percent(f)}", V2Amber),
                    tr(Icons.Rounded.Image, "وسائط مقروءة", mediaSummary(f), V2Violet),
                    tr(Icons.Rounded.Lock, "بيانات تطبيقات أخرى", "خارج النطاق", V2Pink),
                )
            },
            note = "النطاق لا يتجاوز StatFs ومكتبة الوسائط المصرح بها. لا فحص لمجلدات تطبيقات أخرى.",
        ),
        d(
            "deepscan", "dsn-exclusions", "05.4", "إعداد الاستثناءات", "EXCLUSIONS",
            "لا استثناءات وهمية: هذه حدود Android", Icons.Rounded.Shield, V2Amber,
            pro = true,
            rows = { _,_ ->
                listOf(
                    tr(Icons.Rounded.Folder, "مستثنى تلقائيًا", "Android/data", V2Amber),
                    tr(Icons.Rounded.Folder, "مستثنى تلقائيًا", "Android/obb", V2Amber),
                    tr(Icons.Rounded.Lock, "بيانات تطبيقات أخرى", "غير مقروءة", V2Pink),
                    tr(Icons.Rounded.Verified, "استثناءات مخصصة", "غير مدعومة في هذا البناء", V2Soft),
                )
            },
            note = "هذه النسخة لا تحفظ استثناءات مسارات مخصصة. نعرض حدود النظام بدلًا من محاكاة إعدادات غير فعالة.",
        ),
        d(
            "deepscan", "dsn-start", "05.5", "تشغيل الفحص الفعلي", "START SCAN",
            "ابدأ القراءة واحفظ أول قياس محلي", Icons.Rounded.PlayArrow, V2Green,
            metric = { f, _ -> if (f.computedAt > 0L) "جاهز" else "جاري التحميل" },
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Apps, "تطبيقات ستُقرأ", formatCount(f.installedCount), V2Green),
                    tr(Icons.Rounded.CleaningServices, "كاش KNOUX", formatBytes(f.ownCacheBytes), V2Pink),
                    tr(Icons.Rounded.Storage, "مساحة حرة", formatBytes(f.storage.freeBytes), V2Cyan),
                )
            },
            note = "الفحص يعمل داخل شاشة الفحص العميق ويحفظ النتيجة في سجل محلي. لا شيء يُرسل خارج الهاتف.",
            action = "deepscan", actionLabel = "بدء الفحص",
        ),
        d(
            "deepscan", "dsn-progress", "05.6", "تقدم الفحص لكل وحدة", "MODULE PROGRESS",
            "حالة كل وحدة كما اكتملت فعلًا", Icons.Rounded.Timeline, V2Cyan,
            pro = true,
            rows = { f, _ ->
                val ready = f.computedAt > 0L
                listOf(
                    tr(Icons.Rounded.Apps, "التطبيقات", if (ready) "مكتملة · ${formatCount(f.installedCount)}" else "قيد القراءة", V2Green),
                    tr(Icons.Rounded.Image, "الوسائط", if (ready && f.media.permissionGranted) "مكتملة" else if (!f.media.permissionGranted) "بانتظار الصلاحية" else "قيد القراءة", V2Violet),
                    tr(Icons.Rounded.Shield, "الخصوصية", if (ready) "مكتملة · ${formatCount(f.privacyGroups.size)} مجموعة" else "قيد القراءة", V2Amber),
                    tr(Icons.Rounded.BatteryFull, "البطارية", if (ready) "مكتملة" else "قيد القراءة", V2Pink),
                )
            },
            note = "نسبة واحدة عامة ستكون ادعاءً. نعرض حالة كل وحدة على حدة.",
        ),
        d(
            "deepscan", "dsn-control", "05.7", "الإيقاف والإلغاء المدعوم", "PAUSE OR CANCEL",
            "تحكم حقيقي داخل شاشة الفحص", Icons.Rounded.Pause, V2Amber,
            pro = true,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.History, "آخر اكتمال", if (f.computedAt > 0L) formatScanTime(f.computedAt) else "لا اكتمال بعد", V2Cyan),
                    tr(Icons.Rounded.PlayArrow, "حالة الجلسة", if (f.computedAt > 0L) "جاهزة" else "قيد القراءة", V2Green),
                    tr(Icons.Rounded.Info, "الإلغاء", "من داخل شاشة الفحص العميق", V2Soft),
                )
            },
            note = "لا يوجد فحص في الخلفية يستمر بعد إغلاق التطبيق. لذلك الإيقاف متاح فقط داخل الشاشة.",
            action = "deepscan", actionLabel = "فتح التحكم",
        ),
        d(
            "deepscan", "dsn-findings", "05.8", "عرض النتائج المكتشفة", "FINDINGS",
            "ما وجده الفحص فعلًا في هذه الجلسة", Icons.Rounded.Search, V2Pink,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.WarningAmber, "تطبيقات حساسة", formatCount(f.sensitiveAppCount), V2Pink),
                    tr(Icons.Rounded.Layers, "تكرارات وسائط", formatCount(f.media.duplicateGroups.size), V2Violet),
                    tr(Icons.Rounded.CleaningServices, "كاش KNOUX", formatBytes(f.ownCacheBytes), V2Cyan),
                    tr(Icons.Rounded.Storage, "تخزين مستخدم", formatBytes(f.storage.usedBytes), V2Amber),
                )
            },
            note = "النتائج هي القراءات الحالية. لا توجد تهديدات مصطنعة لإثارة القلق.",
        ),
        d(
            "deepscan", "dsn-evidence", "05.9", "مراجعة تفاصيل الأدلة", "EVIDENCE DETAIL",
            "المصدر الخام لكل نتيجة", Icons.Rounded.DocumentScanner, V2Violet,
            pro = true,
            rows = { f, _ ->
                val top = f.installedApps
                    .filter { it.sensitivePermissions.isNotEmpty() }
                    .maxByOrNull { it.sensitivePermissions.size }
                listOf(
                    tr(Icons.Rounded.Apps, "أعلى تطبيق حساسية", top?.label ?: "لا تطبيقات", V2Violet),
                    tr(Icons.Rounded.Image, "أكبر ملف وسائط", f.media.largestFiles.firstOrNull()?.let { "${it.name} · ${formatBytes(it.sizeBytes)}" } ?: "لا ملفات", V2Pink),
                    tr(Icons.Rounded.Layers, "أكبر مجموعة تكرار", f.media.duplicateGroups.firstOrNull()?.let { "${it.count} نسخة · ${formatBytes(it.sizeBytes)}" } ?: "لا مجموعات", V2Amber),
                    tr(Icons.Rounded.Lock, "بيانات محمية", "لا تُعرض لأن Android يمنعها", V2Green),
                )
            },
            note = "كل دليل يعرض اسمه وحجمه ومصدره. لا نعرض مسارًا لا نستطيع قراءته.",
        ),
        d(
            "deepscan", "dsn-summary", "05.10", "ملخص الفحص والإجراءات", "SUMMARY & ACTIONS",
            "ملخص قابل للمشاركة والإجراء التالي", Icons.Rounded.Summarize, V2Cyan,
            pro = true,
            rows = { f, h ->
                val last = lastScan(h)
                listOf(
                    tr(Icons.Rounded.History, "آخر فحص محفوظ", last?.let { formatScanTime(it.timestamp) } ?: "لا سجل", V2Cyan),
                    tr(Icons.Rounded.Apps, "تطبيقات آخر فحص", last?.let { formatCount(it.appsScanned) } ?: "—", V2Green),
                    tr(Icons.Rounded.Shield, "ملاحظات خصوصية", last?.let { formatCount(it.privacyFindings) } ?: "—", V2Amber),
                    tr(Icons.Rounded.Storage, "تخزين آخر فحص", last?.let { formatBytes(it.storageUsedBytes) } ?: "—", V2Violet),
                )
            },
            note = "الملخص مبني على آخر قياس محفوظ. إن لم يوجد سجل فالأرقام تظهر كشرطة لا كصفر.",
            action = "deep-scan-summary", actionLabel = "فتح ملخص الفحص",
        ),
    )

    private val reports = listOf(
        d(
            "reports", "rpt-device", "06.1", "لوحة ملخص الجهاز", "DEVICE SUMMARY",
            "الطراز وإصدار Android والذاكرة المتاحة", Icons.Rounded.Dashboard, V2Cyan,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Memory, "الطراز", Build.MODEL, V2Cyan),
                    tr(Icons.Rounded.Info, "إصدار النظام", "Android ${Build.VERSION.RELEASE} · API ${Build.VERSION.SDK_INT}", V2Violet),
                    tr(Icons.Rounded.Storage, "التخزين", "${formatBytes(f.storage.usedBytes)} / ${formatBytes(f.storage.totalBytes)}", V2Amber),
                    tr(Icons.Rounded.Apps, "التطبيقات", formatCount(f.installedCount), V2Pink),
                )
            },
            note = "ملخص الجهاز يُقرأ من Build وStatFs وPackageManager مباشرة.",
        ),
        d(
            "reports", "rpt-scans", "06.2", "نتائج الفحص", "SCAN RESULTS",
            "آخر قياس محفوظ على الهاتف", Icons.Rounded.FactCheck, V2Green,
            list = ToolList.HISTORY,
            rows = { _, h ->
                val last = lastScan(h)
                listOf(
                    tr(Icons.Rounded.History, "قياسات", "${h.size}/24", V2Green),
                    tr(Icons.Rounded.Apps, "تطبيقات آخر فحص", last?.let { formatCount(it.appsScanned) } ?: "لا سجل", V2Cyan),
                    tr(Icons.Rounded.Shield, "ملاحظات", last?.let { formatCount(it.privacyFindings) } ?: "لا سجل", V2Amber),
                )
            },
            note = "لا توجد نتائج فحص قبل تشغيل الفحص العميق. نعرض السجل الفارغ كحالة صادقة.",
        ),
        d(
            "reports", "rpt-cleanup", "06.3", "سجل عمليات التنظيف", "CLEANUP HISTORY",
            "أثر التنظيف كما حُفظ في القياسات", Icons.Rounded.CleaningServices, V2Pink,
            list = ToolList.HISTORY,
            rows = { _, h ->
                listOf(
                    tr(Icons.Rounded.History, "عمليات محفوظة", "${h.size}", V2Pink),
                    tr(Icons.Rounded.Savings, "أعلى كاش مقيس", h.maxOfOrNull { it.cacheBytes }?.let(::formatBytes) ?: "لا سجل", V2Violet),
                    tr(Icons.Rounded.Storage, "أعلى استخدام", h.maxOfOrNull { it.storageUsedBytes }?.let(::formatBytes) ?: "لا سجل", V2Cyan),
                )
            },
            note = "هذا سجل قياسات، وليس سجل حذف. KNOUX لا يدّعي حذف ملفات لم يؤكدها Android.",
        ),
        d(
            "reports", "rpt-storage", "06.4", "مؤشرات استخدام التخزين", "STORAGE INSIGHTS",
            "المستخدم والمتاح والنسبة الحالية", Icons.Rounded.Storage, V2Amber,
            pro = true,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Storage, "الإجمالي", formatBytes(f.storage.totalBytes), V2Cyan),
                    tr(Icons.Rounded.DonutLarge, "المستخدم", "${formatBytes(f.storage.usedBytes)} · ${percent(f)}", V2Amber),
                    tr(Icons.Rounded.Verified, "المتاح", formatBytes(f.storage.freeBytes), V2Green),
                    tr(Icons.Rounded.CleaningServices, "كاش KNOUX", formatBytes(f.ownCacheBytes), V2Pink),
                )
            },
            note = "نسبة الاستخدام محسوبة من StatFs للقسم الداخلي، وليست تقديرًا من التطبيق.",
            action = "storage-breakdown", actionLabel = "فتح تفصيل التخزين",
        ),
        d(
            "reports", "rpt-battery", "06.5", "ملخص صحة البطارية", "BATTERY SUMMARY",
            "المستوى والحرارة والحالة الآن", Icons.Rounded.BatteryFull, V2Green,
            rows = { f, _ ->
                val battery = f.battery
                listOf(
                    tr(Icons.Rounded.BatteryFull, "المستوى", battery.levelPercent?.let { "$it%" } ?: "غير مقروء", V2Green),
                    tr(Icons.Rounded.Thermostat, "الحرارة", battery.temperatureC?.let { String.format(java.util.Locale.US, "%.1f°C", it) } ?: "غير مقروءة", V2Amber),
                    tr(Icons.Rounded.Verified, "الصحة", battery.healthLabel, V2Cyan),
                    tr(Icons.Rounded.Bolt, "الحالة", battery.statusLabel, V2Violet),
                )
            },
            note = "لا يوجد سجل حرارة زمني في هذه النسخة. نعرض القراءة الحالية فقط.",
        ),
        d(
            "reports", "rpt-privacy", "06.6", "ملخص مراجعة الخصوصية", "PRIVACY SUMMARY",
            "الأذونات الحساسة كما هي الآن", Icons.Rounded.Shield, V2Violet,
            pro = true,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Apps, "تطبيقات حساسة", formatCount(f.sensitiveAppCount), V2Pink),
                    tr(Icons.Rounded.Layers, "مجموعات", formatCount(f.privacyGroups.size), V2Violet),
                    tr(Icons.Rounded.QueryStats, "وصول الاستخدام", if (f.usageAccessGranted) "ممنوح" else "غير ممنوح", V2Amber),
                )
            },
            note = "الملخص قراءة لحظية. لا يحتوي على سجل استخدام فعلي ما لم تُمنح صلاحية Usage Access.",
            action = "privacy-risk", actionLabel = "فتح تحليل الخصوصية",
        ),
        d(
            "reports", "rpt-range", "06.7", "اختيار الفترة الزمنية", "TIME RANGE",
            "آخر 7 أو 30 أو 90 يومًا من القياسات", Icons.Rounded.Timeline, V2Cyan,
            pro = true,
            behavior = ToolBehavior.RANGE_HISTORY, list = ToolList.HISTORY,
            rows = { _, h ->
                listOf(
                    tr(Icons.Rounded.History, "أقدم قياس", h.minByOrNull { it.timestamp }?.let { formatScanTime(it.timestamp) } ?: "لا سجل", V2Cyan),
                    tr(Icons.Rounded.History, "أحدث قياس", h.maxByOrNull { it.timestamp }?.let { formatScanTime(it.timestamp) } ?: "لا سجل", V2Green),
                )
            },
            note = "الفترة تطبق على السجل المحلي فقط. لا توجد بيانات سحابية أصلًا.",
        ),
        d(
            "reports", "rpt-trends", "06.8", "الرسوم البيانية والاتجاهات", "CHARTS & TRENDS",
            "اتجاه التخزين والكاش من القياسات المحفوظة", Icons.Rounded.Insights, V2Violet,
            pro = true,
            list = ToolList.HISTORY,
            rows = { _, h ->
                listOf(
                    tr(Icons.Rounded.TrendingUp, "أول كاش", oldestScan(h)?.let { formatBytes(it.cacheBytes) } ?: "لا سجل", V2Cyan),
                    tr(Icons.Rounded.Insights, "آخر كاش", lastScan(h)?.let { formatBytes(it.cacheBytes) } ?: "لا سجل", V2Pink),
                    tr(Icons.Rounded.Storage, "تغير التخزين", if (h.map { it.timestamp }.distinct().size >= 2) formatSignedBytes(lastScan(h)!!.storageUsedBytes - oldestScan(h)!!.storageUsedBytes) else "يحتاج قياسين", V2Amber),
                )
            },
            note = "الرسم الحقيقي يحتاج قياسين على الأقل. حتى ذلك الحين نعرض الفروق لا رسمًا مخترعًا.",
        ),
        d(
            "reports", "rpt-operation", "06.9", "تفاصيل العملية السابقة", "OPERATION DETAIL",
            "كل ما حُفظ عن آخر قياس", Icons.Rounded.ReceiptLong, V2Cyan,
            pro = true,
            rows = { _, h ->
                val last = lastScan(h)
                if (last == null) {
                    listOf(tr(Icons.Rounded.Info, "لا عملية محفوظة", "شغّل الفحص العميق أولًا", V2Soft))
                } else {
                    listOf(
                        tr(Icons.Rounded.History, "الوقت", formatScanTime(last.timestamp), V2Cyan),
                        tr(Icons.Rounded.Apps, "تطبيقات", formatCount(last.appsScanned), V2Green),
                        tr(Icons.Rounded.Shield, "ملاحظات خصوصية", formatCount(last.privacyFindings), V2Amber),
                        tr(Icons.Rounded.CleaningServices, "كاش", formatBytes(last.cacheBytes), V2Pink),
                        tr(Icons.Rounded.BatteryFull, "بطارية", last.batteryLevel?.let { "$it%" } ?: "غير مقروءة", V2Violet),
                    )
                }
            },
            note = "هذه الحقول هي كل ما يحفظه السجل. لا نضيف حقولًا غير موجودة.",
        ),
        d(
            "reports", "rpt-empty", "06.10", "حالات غياب البيانات", "EMPTY & MISSING STATES",
            "ماذا تعني الشرطة في هذا التطبيق", Icons.Rounded.Info, V2Amber,
            rows = { _, h ->
                listOf(
                    tr(Icons.Rounded.History, "قياسات محفوظة", "${h.size}", V2Amber),
                    tr(Icons.Rounded.Shield, "وصول الاستخدام", "غير مطلوب لعرض هذه الصفحة", V2Cyan),
                    tr(Icons.Rounded.Info, "الشرطة (—)", "تعني غير متاح، وليست صفرًا", V2Soft),
                )
            },
            note = "عندما لا توجد بيانات لا نعرض صفرًا. الصفر قد يعني قياسًا ناجحًا، والشرطة تعني غياب القياس.",
            action = "deepscan", actionLabel = "تشغيل فحص لملء السجل",
        ),
    )

    private val appManager = listOf(
        d(
            "appmanager", "apm-installed", "07.1", "قائمة التطبيقات المثبتة", "INSTALLED APPS",
            "كل حزمة يراها PackageManager على هذا الهاتف", Icons.Rounded.Apps, V2Violet,
            metric = { f, _ -> "${formatCount(f.installedCount)} تطبيقًا" },
            list = ToolList.APPS,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Apps, "الإجمالي", formatCount(f.installedCount), V2Violet),
                    tr(Icons.Rounded.Inventory, "تطبيقات المستخدم", formatCount(f.userAppCount), V2Pink),
                    tr(Icons.Rounded.Settings, "تطبيقات النظام", formatCount(f.installedCount - f.userAppCount), V2Cyan),
                )
            },
            note = "الحجم لكل تطبيق غير متاح من PackageManager على Android 11 دون صلاحية إحصاءات الاستخدام. لا نعرض 0 كأنه قياس.",
        ),
        d(
            "appmanager", "apm-info", "07.2", "عرض معلومات كل تطبيق", "APP INFO",
            "الحزمة والإصدار وحالة التمكين", Icons.Rounded.Info, V2Cyan,
            list = ToolList.APPS,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Verified, "حزم مفعّلة", formatCount(f.installedApps.count { it.isEnabled }), V2Green),
                    tr(Icons.Rounded.Description, "إصدارات معروفة", formatCount(f.installedApps.count { !it.versionName.isNullOrBlank() }), V2Cyan),
                    tr(Icons.Rounded.WarningAmber, "حزم بأذونات حساسة", formatCount(sensitiveApps(f)), V2Pink),
                )
            },
            note = "اختر تطبيقًا لعرض معلوماته في إعدادات Android. لا نعرض بيانات خاصة بالتطبيق لا يسمح بها النظام.",
            behavior = ToolBehavior.APP_SETTINGS_ACTION,
        ),
        d(
            "appmanager", "apm-system-user", "07.3", "تصنيف النظام والمستخدم", "SYSTEM VS USER",
            "كم تطبيقًا جاء مع النظام وكم ثبّته المستخدم", Icons.Rounded.Category, V2Pink,
            list = ToolList.APPS,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Inventory, "مستخدم", formatCount(f.userAppCount), V2Pink),
                    tr(Icons.Rounded.Settings, "نظام", formatCount(f.installedCount - f.userAppCount), V2Cyan),
                    tr(Icons.Rounded.Apps, "الإجمالي", formatCount(f.installedCount), V2Violet),
                )
            },
            note = "التصنيف من ApplicationInfo.FLAG_SYSTEM. التطبيقات المحدثة قد تبقى مصنفة نظامًا.",
        ),
        d(
            "appmanager", "apm-search", "07.4", "البحث عن التطبيقات", "SEARCH",
            "ابحث بالاسم أو باسم الحزمة", Icons.Rounded.Search, V2Amber,
            behavior = ToolBehavior.SEARCH_APPS, list = ToolList.APPS,
            rows = { f, _ ->
                listOf(tr(Icons.Rounded.Apps, "قابل للبحث", "${formatCount(f.installedCount)} تطبيقًا", V2Amber))
            },
            note = "البحث محلي داخل قائمة الحزم المثبتة. لا يُرسل اسم أي تطبيق خارج الهاتف.",
        ),
        d(
            "appmanager", "apm-filters", "07.5", "فلترة التطبيقات", "FILTERS",
            "مستخدم أم نظام أم حساس أم معطّل", Icons.Rounded.FilterAlt, V2Violet,
            pro = true,
            behavior = ToolBehavior.FILTER_APPS_SIZE, list = ToolList.APPS,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.WarningAmber, "حساسة", formatCount(sensitiveApps(f)), V2Pink),
                    tr(Icons.Rounded.Settings, "معطّلة", formatCount(f.installedApps.count { !it.isEnabled }), V2Amber),
                )
            },
            note = "لا يوجد فلتر بالحجم لأن حجم الحزمة غير مقروء على هذا الإصدار من Android. الفلاتر المعروضة تعمل على بيانات حقيقية.",
        ),
        d(
            "appmanager", "apm-sorting", "07.6", "ترتيب التطبيقات", "SORTING",
            "بالاسم أو النظام أولًا أو الأكثر أذونات", Icons.Rounded.Sort, V2Cyan,
            pro = true,
            behavior = ToolBehavior.SORT_APPS, list = ToolList.APPS,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Sort, "الترتيب الحالي", "حسب الاسم افتراضيًا", V2Cyan),
                    tr(Icons.Rounded.WarningAmber, "الأكثر أذونات", f.installedApps.maxByOrNull { it.sensitivePermissions.size }?.label ?: "لا تطبيقات", V2Amber),
                )
            },
            note = "الترتيب بالحجم غير متاح لنفس سبب عدم توفر حجم الحزمة.",
        ),
        d(
            "appmanager", "apm-permissions", "07.7", "عرض الصلاحيات المتاحة", "PERMISSIONS",
            "الأذونات المعلنة والممنوحة لكل تطبيق", Icons.Rounded.Security, V2Pink,
            pro = true,
            list = ToolList.PERMISSIONS,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Apps, "تطبيقات حساسة", formatCount(sensitiveApps(f)), V2Pink),
                    tr(Icons.Rounded.Layers, "مجموعات مقروءة", formatCount(f.privacyGroups.size), V2Violet),
                )
            },
            note = "الحالة الممنوحة تأتي من PackageManager. لا نستنتج استخدامًا فعليًا من مجرد منح الإذن.",
        ),
        d(
            "appmanager", "apm-launch", "07.8", "تشغيل التطبيقات", "LAUNCH APP",
            "افتح التطبيق من شاشته الرسمية", Icons.Rounded.PlayArrow, V2Green,
            behavior = ToolBehavior.LAUNCH_APPS, list = ToolList.APPS,
            rows = { f, _ ->
                listOf(tr(Icons.Rounded.PlayArrow, "قابل للتشغيل", "${formatCount(f.installedApps.count { it.isEnabled })} تطبيقًا", V2Green))
            },
            note = "التشغيل يستخدم Launch Intent الرسمي. التطبيقات التي لا تعلن شاشة رئيسية لن تفتح.",
        ),
        d(
            "appmanager", "apm-settings", "07.9", "فتح إعدادات التطبيق", "OPEN SETTINGS",
            "إلى شاشة معلومات التطبيق في Android", Icons.Rounded.OpenInNew, V2Cyan,
            pro = true,
            behavior = ToolBehavior.APP_SETTINGS_ACTION, list = ToolList.APPS,
            rows = { f, _ ->
                listOf(tr(Icons.Rounded.Settings, "شاشة النظام", "معلومات التطبيق والأذونات", V2Cyan))
            },
            note = "لا يغيّر KNOUX إعدادات التطبيق. يفتح شاشة Android لتتخذ القرار بنفسك.",
        ),
        d(
            "appmanager", "apm-uninstall", "07.10", "طلب إلغاء التثبيت", "UNINSTALL FLOW",
            "حوار Android الرسمي فقط", Icons.Rounded.Delete, V2ProRed,
            pro = true,
            behavior = ToolBehavior.UNINSTALL_APPS, list = ToolList.APPS,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Delete, "حوار النظام", "تأكيد من المستخدم", V2ProRed),
                    tr(Icons.Rounded.Shield, "تطبيقات النظام", "لا يمكن إزالتها عادة", V2Amber),
                    tr(Icons.Rounded.Apps, "قابلة للمراجعة", formatCount(f.userAppCount), V2Cyan),
                )
            },
            note = "KNOUX لا يحذف حزمًا بنفسه. كل إزالة تمر عبر حوار Android وتأكيدك.",
        ),
    )

    private val storage = listOf(
        d(
            "storage", "sto-overview", "08.1", "نظرة عامة على التخزين", "STORAGE OVERVIEW",
            "القسم الداخلي كما يعيده StatFs", Icons.Rounded.Storage, V2Cyan,
            metric = { f, _ -> percent(f) + " مستخدمة" },
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Storage, "الإجمالي", formatBytes(f.storage.totalBytes), V2Cyan),
                    tr(Icons.Rounded.DonutLarge, "المستخدم", "${formatBytes(f.storage.usedBytes)} · ${percent(f)}", V2Amber),
                    tr(Icons.Rounded.Verified, "المتاح", formatBytes(f.storage.freeBytes), V2Green),
                    tr(Icons.Rounded.CleaningServices, "كاش KNOUX", formatBytes(f.ownCacheBytes), V2Pink),
                )
            },
            note = "القياس للقسم الداخلي الذي يستخدمه التطبيق. لا يشمل بطاقة SD أو USB إن وُجدت.",
        ),
        d(
            "storage", "sto-volumes", "08.2", "اختيار وحدة التخزين", "STORAGE VOLUME PICKER",
            "الداخلي متاح، والباقي لا نستطيع قياسه", Icons.Rounded.SimCard, V2Violet,
            pro = true,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Storage, "التخزين الداخلي", formatBytes(f.storage.totalBytes), V2Green),
                    tr(Icons.Rounded.SimCard, "بطاقة SD", "غير مقروءة في هذا البناء", V2Muted),
                    tr(Icons.Rounded.Usb, "منفذ USB OTG", "غير مقروء", V2Muted),
                )
            },
            note = "لا نعرض وحدات وهمية. StatFs الحالي يقرأ بيانات التطبيق الداخلية فقط.",
        ),
        d(
            "storage", "sto-used-free", "08.3", "المساحة المستخدمة والمتاحة", "USED VS FREE",
            "نسبة واحدة واضحة بلا تقديرات", Icons.Rounded.DonutLarge, V2Amber,
            metric = { f, _ -> formatBytes(f.storage.freeBytes) },
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Storage, "مستخدم", formatBytes(f.storage.usedBytes), V2Amber),
                    tr(Icons.Rounded.Verified, "متاح", formatBytes(f.storage.freeBytes), V2Green),
                    tr(Icons.Rounded.DonutLarge, "النسبة", percent(f), V2Cyan),
                )
            },
            note = "المساحة المتاحة قد تتغير بين القراءتين بسبب النظام والتطبيقات الأخرى.",
        ),
        d(
            "storage", "sto-categories", "08.4", "توزيع الملفات حسب الفئة", "CATEGORY DISTRIBUTION",
            "صور وفيديو وكاش والباقي", Icons.Rounded.Category, V2Violet,
            pro = true,
            metric = { f, _ -> mediaSummary(f) },
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Image, "صور", if (f.media.permissionGranted) formatBytes(f.media.imagesBytes) else "بعد المنح", V2Pink),
                    tr(Icons.Rounded.VideoLibrary, "فيديو", if (f.media.permissionGranted) formatBytes(f.media.videosBytes) else "بعد المنح", V2Violet),
                    tr(Icons.Rounded.CleaningServices, "كاش KNOUX", formatBytes(f.ownCacheBytes), V2Cyan),
                    tr(Icons.Rounded.Storage, "النظام والتطبيقات", formatBytes((f.storage.usedBytes - f.ownCacheBytes - f.media.imagesBytes - f.media.videosBytes).coerceAtLeast(0L)), V2Amber),
                )
            },
            note = "فئة النظام والتطبيقات هي الباقي بعد القراءات المتاحة، وليست قراءة مباشرة لكل تطبيق.",
        ),
        d(
            "storage", "sto-browser", "08.5", "استعراض الملفات المتاحة", "FILE BROWSER",
            "المجلدات التي نعرف محتواها فعلًا", Icons.Rounded.FolderOpen, V2Cyan,
            rows = { f, _ ->
                val buckets = f.media.largestFiles.mapNotNull { it.bucket }.distinct()
                listOf(
                    tr(Icons.Rounded.Folder, "مجلدات وسائط", formatCount(buckets.size), V2Cyan),
                    tr(Icons.Rounded.Image, "أكبر مجلد", buckets.firstOrNull() ?: "لا مجلدات", V2Violet),
                    tr(Icons.Rounded.Folder, "مجلد الكاش", "داخل بيانات KNOUX", V2Pink),
                )
            },
            note = "المستكشف الكامل يفتح مجلدات KNOUX ومكتبة الوسائط فقط، وليس جذر النظام.",
            action = "file-explorer", actionLabel = "فتح مستكشف الملفات",
        ),
        d(
            "storage", "sto-search", "08.6", "البحث عن الملفات", "FILE SEARCH",
            "ابحث بالاسم داخل أكبر ملفات MediaStore", Icons.Rounded.FindInPage, V2Amber,
            behavior = ToolBehavior.SEARCH_FILES, list = ToolList.FILES,
            rows = { f, _ ->
                listOf(tr(Icons.Rounded.Search, "ملفات قابلة للبحث", formatCount(f.media.largestFiles.size), V2Amber))
            },
            note = "البحث يطابق الأسماء في قائمة أكبر الملفات التي قرأها MediaStore، وليس كل ملفات الجهاز.",
        ),
        d(
            "storage", "sto-sorting", "08.7", "فرز الملفات حسب الحجم والتاريخ", "SORTING",
            "الأكبر أولًا أو بالاسم أو الفيديو أولًا", Icons.Rounded.Sort, V2Cyan,
            pro = true,
            behavior = ToolBehavior.SORT_FILES, list = ToolList.FILES,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Storage, "أكبر ملف", f.media.largestFiles.maxOfOrNull { it.sizeBytes }?.let(::formatBytes) ?: "لا ملفات", V2Cyan),
                    tr(Icons.Rounded.History, "تاريخ الملف", "غير مقروء في هذه النسخة", V2Muted),
                )
            },
            note = "لا يوجد فرز بالتاريخ لأن استعلام MediaStore لا يطلب DATE_ADDED. لا نعرض تاريخًا تقديريًا.",
        ),
        d(
            "storage", "sto-details", "08.8", "معاينة تفاصيل الملفات", "FILE DETAILS",
            "الاسم والحجم والنوع والمجلد", Icons.Rounded.Description, V2Violet,
            pro = true,
            list = ToolList.FILES,
            rows = { f, _ ->
                val first = f.media.largestFiles.firstOrNull()
                listOf(
                    tr(Icons.Rounded.Image, "أكبر ملف", first?.name ?: "لا ملفات", V2Violet),
                    tr(Icons.Rounded.Storage, "الحجم", first?.let { formatBytes(it.sizeBytes) } ?: "—", V2Cyan),
                    tr(Icons.Rounded.Folder, "المجلد", first?.bucket ?: "—", V2Amber),
                )
            },
            note = "التفاصيل من MediaStore. المسار المطلق قد لا يكون مقروءًا حتى لو عُرض اسم المجلد.",
        ),
        d(
            "storage", "sto-open-share", "08.9", "فتح ومشاركة الملفات", "OPEN & SHARE",
            "فتح أكبر ملف عبر تطبيق النظام المناسب", Icons.Rounded.Share, V2Green,
            handoff = ToolHandoff.OPEN_FIRST_MEDIA, actionLabel = "فتح أكبر ملف",
            rows = { f, _ ->
                val first = f.media.largestFiles.firstOrNull()
                listOf(
                    tr(Icons.Rounded.Image, "الملف", first?.name ?: "لا ملفات", V2Green),
                    tr(Icons.Rounded.Storage, "الحجم", first?.let { formatBytes(it.sizeBytes) } ?: "—", V2Cyan),
                    tr(Icons.Rounded.PlayArrow, "النوع", first?.let { if (it.isVideo) "فيديو" else "صورة" } ?: "—", V2Violet),
                )
            },
            note = "الفتح يستخدم Intent رسمي. المشاركة تُترك لتطبيقات النظام ولا ينسخ KNOUX الملف.",
        ),
        d(
            "storage", "sto-move-delete", "08.10", "مراجعة النقل والحذف", "MOVE & DELETE REVIEW",
            "ما يمكن حذفه وما يحتاج حوارًا رسميًا", Icons.Rounded.Delete, V2Pink,
            pro = true,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Layers, "مجموعات تكرار", formatCount(f.media.duplicateGroups.size), V2Violet),
                    tr(Icons.Rounded.Savings, "مساحة مرشحة", formatBytes(f.media.duplicateGroups.sumOf { it.reclaimableBytes }), V2Pink),
                    tr(Icons.Rounded.Lock, "ملفات محمية", "لا تظهر كمرشح", V2Green),
                    tr(Icons.Rounded.Info, "النقل", "غير منفذ في هذا البناء", V2Soft),
                )
            },
            note = "لا يوجد نقل صامت للملفات. الحذف يمر عبر شاشة التكرار وحوار MediaStore الرسمي.",
            action = "duplicates", actionLabel = "فتح مراجعة الحذف",
        ),
    )

    private val battery = listOf(
        d(
            "battery", "bat-level", "09.1", "مستوى البطارية الفعلي", "BATTERY LEVEL",
            "القراءة الحالية من BatteryManager", Icons.Rounded.BatteryFull, V2Green,
            metric = { f, _ -> f.battery.levelPercent?.let { "$it%" } ?: "غير مقروء" },
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.BatteryFull, "المستوى", f.battery.levelPercent?.let { "$it%" } ?: "غير مقروء", V2Green),
                    tr(Icons.Rounded.Bolt, "الحالة", f.battery.statusLabel, V2Cyan),
                    tr(Icons.Rounded.Verified, "الصحة", f.battery.healthLabel, V2Amber),
                )
            },
            note = "المستوى من Intent.ACTION_BATTERY_CHANGED. لا يوجد تقدير أو سجل زمني هنا.",
        ),
        d(
            "battery", "bat-charge", "09.2", "حالة الشحن والتفريغ", "CHARGE STATUS",
            "هل الهاتف يشحن الآن أم يستهلك", Icons.Rounded.BatteryChargingFull, V2Cyan,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.BatteryChargingFull, "الحالة", f.battery.statusLabel, if (f.battery.charging) V2Green else V2Amber),
                    tr(Icons.Rounded.Bolt, "يشحن", if (f.battery.charging) "نعم" else "لا", V2Green),
                    tr(Icons.Rounded.BatteryFull, "المستوى", f.battery.levelPercent?.let { "$it%" } ?: "غير مقروء", V2Cyan),
                )
            },
            note = "لا نعرض سرعة شحن أو وقتًا متبقيًا لأن BatteryManager لا يوفرهما بشكل موثوق هنا.",
        ),
        d(
            "battery", "bat-temperature", "09.3", "قياس درجة الحرارة", "TEMPERATURE",
            "حرارة البطارية الآن مقابل حدود التشغيل", Icons.Rounded.Thermostat, V2Amber,
            pro = true,
            rows = { f, _ ->
                val temp = f.battery.temperatureC
                listOf(
                    tr(Icons.Rounded.Thermostat, "الحرارة", temp?.let { String.format(java.util.Locale.US, "%.1f°C", it) } ?: "غير مقروءة", V2Amber),
                    tr(Icons.Rounded.CheckCircle, "النطاق المريح", "أقل من 43°C", V2Green),
                    tr(Icons.Rounded.WarningAmber, "تنبيه", "فوق 43°C", V2Pink),
                    tr(Icons.Rounded.Verified, "الصحة", f.battery.healthLabel, V2Cyan),
                )
            },
            note = "حدود الحرارة إرشادية لهذه الشاشة وليست إعداد تنبيه نظام. لا يوجد سجل حرارة زمني بعد.",
        ),
        d(
            "battery", "bat-source", "09.4", "حالة مصدر الطاقة", "POWER SOURCE",
            "الشاحن أم USB أم لاسلكي", Icons.Rounded.PowerSettingsNew, V2Violet,
            pro = true,
            rows = { f, _ ->
                val source = when (f.battery.plugged) {
                    android.os.BatteryManager.BATTERY_PLUGGED_AC -> "شاحن AC"
                    android.os.BatteryManager.BATTERY_PLUGGED_USB -> "USB"
                    android.os.BatteryManager.BATTERY_PLUGGED_WIRELESS -> "لاسلكي"
                    else -> "البطارية الداخلية"
                }
                listOf(
                    tr(Icons.Rounded.PowerSettingsNew, "المصدر", source, V2Violet),
                    tr(Icons.Rounded.Bolt, "الجهد", f.battery.voltageMv?.let { "${it} mV" } ?: "غير مقروء", V2Cyan),
                    tr(Icons.Rounded.BatteryFull, "الحالة", f.battery.statusLabel, V2Amber),
                )
            },
            note = "المصدر من EXTRA_PLUGGED. قد يعرض النظام USB حتى مع شاحن بطيء.",
        ),
        d(
            "battery", "bat-info", "09.5", "معلومات البطارية من النظام", "SYSTEM BATTERY INFO",
            "التقنية والجهد والحالة", Icons.Rounded.Info, V2Cyan,
            pro = true,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Memory, "التقنية", f.battery.technology ?: "غير معلنة", V2Cyan),
                    tr(Icons.Rounded.Bolt, "الجهد", f.battery.voltageMv?.let { "$it mV" } ?: "غير مقروء", V2Violet),
                    tr(Icons.Rounded.Verified, "الصحة", f.battery.healthLabel, V2Green),
                    tr(Icons.Rounded.BatteryFull, "المستوى", f.battery.levelPercent?.let { "$it%" } ?: "غير مقروء", V2Amber),
                )
            },
            note = "ليست كل الأجهزة تعلن السعة بالملي أمبير عبر واجهة Android العامة. لا نعرض رقمًا غير موجود.",
        ),
        d(
            "battery", "bat-saving", "09.6", "توفير الطاقة", "POWER SAVING STATE",
            "التحويل إلى شاشة توفير الطاقة الرسمية", Icons.Rounded.BatterySaver, V2Green,
            action = "battery-diagnostics", actionLabel = "قراءة وضع التوفير وإعداداته",
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.BatterySaver, "وضع التوفير", "قراءة مباشرة في شاشة التشخيص", V2Green),
                    tr(Icons.Rounded.BatteryFull, "المستوى", f.battery.levelPercent?.let { "$it%" } ?: "غير مقروء", V2Cyan),
                    tr(Icons.Rounded.Bolt, "الحالة", f.battery.statusLabel, V2Amber),
                )
            },
            note = "تعرض شاشة التشخيص حالة PowerManager.isPowerSaveMode الفعلية وتفتح إعدادات توفير الطاقة. لا يغيّر KNOUX الوضع تلقائيًا.",
        ),
        d(
            "battery", "bat-readings", "09.7", "عرض قراءات البطارية", "READINGS & METRICS",
            "كل ما تعرضه الواجهة الآن", Icons.Rounded.QueryStats, V2Cyan,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.BatteryFull, "المستوى", f.battery.levelPercent?.let { "$it%" } ?: "—", V2Cyan),
                    tr(Icons.Rounded.Thermostat, "الحرارة", f.battery.temperatureC?.let { String.format(java.util.Locale.US, "%.1f°C", it) } ?: "—", V2Amber),
                    tr(Icons.Rounded.Bolt, "الجهد", f.battery.voltageMv?.let { "$it mV" } ?: "—", V2Violet),
                    tr(Icons.Rounded.BatteryChargingFull, "الحالة", f.battery.statusLabel, V2Green),
                )
            },
            note = "القراءات لحظية. التحديث يعيد استعلام BatteryManager.",
        ),
        d(
            "battery", "bat-alerts", "09.8", "إعداد حدود التنبيه", "ALERT THRESHOLDS",
            "مقارنة القراءة بالحدود الإرشادية", Icons.Rounded.BatteryAlert, V2Pink,
            pro = true,
            rows = { f, _ ->
                val temp = f.battery.temperatureC
                val level = f.battery.levelPercent
                listOf(
                    tr(Icons.Rounded.Thermostat, "حرارة ≥ 43°C", if (temp != null) if (temp >= 43f) "تجاوزت" else "داخل الحد" else "—", if (temp != null && temp >= 43f) V2Pink else V2Green),
                    tr(Icons.Rounded.BatteryAlert, "مستوى ≤ 15%", if (level != null) if (level <= 15) "منخفض" else "داخل الحد" else "—", if (level != null && level <= 15) V2Pink else V2Green),
                    tr(Icons.Rounded.NotificationsActive, "إشعارات", "غير مفعّلة في هذا البناء", V2Muted),
                )
            },
            note = "هذه مقارنة عرض فقط. لا يوجد إشعار نظام مجدول من KNOUX حتى الآن.",
        ),
        d(
            "battery", "bat-settings", "09.9", "فتح إعدادات الطاقة", "POWER SETTINGS (ANDROID)",
            "إلى شاشة البطارية في إعدادات Android", Icons.Rounded.OpenInNew, V2Amber,
            handoff = ToolHandoff.BATTERY_SETTINGS, actionLabel = "فتح إعدادات البطارية",
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Settings, "الشاشة", "إعدادات البطارية", V2Amber),
                    tr(Icons.Rounded.BatteryFull, "المستوى الحالي", f.battery.levelPercent?.let { "$it%" } ?: "—", V2Cyan),
                )
            },
            note = "التحويل لشاشة النظام. لا يغيّر KNOUX إعدادات الطاقة تلقائيًا.",
        ),
        d(
            "battery", "bat-summary", "09.10", "ملخص تشخيص البطارية", "DIAGNOSTICS SUMMARY",
            "الخلاصة من القراءات المتاحة", Icons.Rounded.Summarize, V2Green,
            pro = true,
            rows = { f, h ->
                listOf(
                    tr(Icons.Rounded.Verified, "الصحة", f.battery.healthLabel, V2Green),
                    tr(Icons.Rounded.BatteryFull, "المستوى", f.battery.levelPercent?.let { "$it%" } ?: "—", V2Cyan),
                    tr(Icons.Rounded.Thermostat, "الحرارة", f.battery.temperatureC?.let { String.format(java.util.Locale.US, "%.1f°C", it) } ?: "—", V2Amber),
                    tr(Icons.Rounded.History, "آخر قياس", lastScan(h)?.let { formatScanTime(it.timestamp) } ?: "لا سجل", V2Violet),
                )
            },
            note = "الملخص لا يدعي عمرًا متبقيًا أو دورات شحن غير معلنة من النظام.",
            action = "battery-diagnostics", actionLabel = "فتح التشخيص الكامل",
        ),
    )

    private val ai = listOf(
        d(
            "ai", "ai-goal", "10.1", "اختيار هدف التحسين", "GOAL SELECTION",
            "الأداء أم البطارية أم الألعاب أم التوفير", Icons.Rounded.TrackChanges, V2Pink,
            behavior = ToolBehavior.GOAL_CHIPS,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Storage, "التخزين", "${formatBytes(f.storage.usedBytes)} مستخدم", V2Cyan),
                    tr(Icons.Rounded.BatteryFull, "البطارية", f.battery.levelPercent?.let { "$it%" } ?: "—", V2Green),
                    tr(Icons.Rounded.Thermostat, "الحرارة", f.battery.temperatureC?.let { String.format(java.util.Locale.US, "%.1f°C", it) } ?: "—", V2Amber),
                )
            },
            note = "اختيار الهدف يغيّر ترتيب التوصيات المعروضة، ولا ينفذ أي تغيير على النظام.",
        ),
        d(
            "ai", "ai-storage", "10.2", "تحليل مؤشرات التخزين", "STORAGE SIGNALS",
            "الضغط الحالي وأكبر مساحة قابلة للاسترجاع", Icons.Rounded.Storage, V2Cyan,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.DonutLarge, "نسبة الاستخدام", percent(f), V2Cyan),
                    tr(Icons.Rounded.Verified, "متاح", formatBytes(f.storage.freeBytes), V2Green),
                    tr(Icons.Rounded.CleaningServices, "كاش KNOUX", formatBytes(f.ownCacheBytes), V2Pink),
                    tr(Icons.Rounded.Layers, "تكرارات قابلة للاسترجاع", formatBytes(f.media.duplicateGroups.sumOf { it.reclaimableBytes }), V2Violet),
                )
            },
            note = "المؤشرات مباشرة. التوصية لا تُنفذ تلقائيًا.",
        ),
        d(
            "ai", "ai-battery", "10.3", "تحليل مؤشرات البطارية", "BATTERY SIGNALS",
            "المستوى والحرارة وحالة الشحن", Icons.Rounded.BatteryFull, V2Green,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.BatteryFull, "المستوى", f.battery.levelPercent?.let { "$it%" } ?: "—", V2Green),
                    tr(Icons.Rounded.Bolt, "الحالة", f.battery.statusLabel, V2Cyan),
                    tr(Icons.Rounded.Thermostat, "الحرارة", f.battery.temperatureC?.let { String.format(java.util.Locale.US, "%.1f°C", it) } ?: "—", V2Amber),
                )
            },
            note = "لا يوجد نموذج يتنبأ بالعمر المتبقي من قراءة واحدة. نعرض الإشارات فقط.",
        ),
        d(
            "ai", "ai-thermal", "10.4", "تحليل مؤشرات الحرارة", "THERMAL SIGNALS",
            "هل الحرارة داخل النطاق المريح", Icons.Rounded.Thermostat, V2Amber,
            pro = true,
            rows = { f, _ ->
                val temp = f.battery.temperatureC
                listOf(
                    tr(Icons.Rounded.Thermostat, "الحرارة الحالية", temp?.let { String.format(java.util.Locale.US, "%.1f°C", it) } ?: "—", V2Amber),
                    tr(Icons.Rounded.CheckCircle, "الحد المريح", "< 43°C", V2Green),
                    tr(Icons.Rounded.WarningAmber, "التقييم", if (temp != null) if (temp >= 43f) "مرتفعة" else "طبيعية" else "غير متاح", if (temp != null && temp >= 43f) V2Pink else V2Green),
                )
            },
            note = "لا يوجد سجل حراري زمني. التقييم لحظي من قراءة واحدة.",
        ),
        d(
            "ai", "ai-recommendations", "10.5", "مركز التوصيات", "AI RECOMMENDATIONS",
            "توصيات مرتبة من القراءات الحقيقية", Icons.Rounded.AutoAwesome, V2Pink,
            metric = { f, _ -> "${aiRecommendations(f).size} توصية" },
            rows = { f, _ -> aiRecommendations(f).map { tr(it.icon, it.title, it.value, it.accent) } },
            note = "التوصيات قواعد محلية صريحة مبنية على القراءات، وليست نموذجًا لغويًا يخمّن.",
            action = "ai-recommendations", actionLabel = "فتح مركز التوصيات",
        ),
        d(
            "ai", "ai-reasons", "10.6", "أسباب التوصيات", "RECOMMENDATION REASONS",
            "لماذا ظهرت كل توصية", Icons.Rounded.Lightbulb, V2Violet,
            rows = { f, _ -> aiRecommendations(f).map { tr(Icons.Rounded.Info, it.title, it.reason, it.accent) } },
            note = "كل سبب مرتبط بقراءة ظاهرة في شاشة المؤشرات. لا أسباب مخفية.",
        ),
        d(
            "ai", "ai-confidence", "10.7", "مستوى الثقة والحدود", "CONFIDENCE & LIMITS",
            "ما نعرفه وما ينقصنا", Icons.Rounded.GppGood, V2Cyan,
            pro = true,
            rows = { f, _ ->
                val fields = listOf(
                    f.storage.totalBytes > 0,
                    f.installedCount > 0,
                    f.battery.levelPercent != null,
                    f.media.permissionGranted,
                )
                listOf(
                    tr(Icons.Rounded.Verified, "حقول مقروءة", "${fields.count { it }}/4", V2Green),
                    tr(Icons.Rounded.WarningAmber, "نواقص", if (f.media.permissionGranted) "لا نواقص أساسية" else "صلاحية الوسائط", V2Amber),
                    tr(Icons.Rounded.Info, "الثقة", "قاعدة صريحة وليست احتمالًا", V2Cyan),
                )
            },
            note = "لا نعرض نسبة ثقة مصطنعة. نعرض الحقول المتاحة والحدود المعروفة.",
        ),
        d(
            "ai", "ai-customize", "10.8", "تخصيص إجراء التحسين", "ACTION CUSTOMIZATION",
            "تفضيلات محلية قبل أي إجراء", Icons.Rounded.Tune, V2Violet,
            pro = true,
            behavior = ToolBehavior.TOGGLES,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.Storage, "أثر التخزين المتاح", formatBytes(f.ownCacheBytes + f.media.duplicateGroups.sumOf { it.reclaimableBytes }), V2Cyan),
                    tr(Icons.Rounded.BatteryFull, "حالة البطارية", f.battery.levelPercent?.let { "$it%" } ?: "—", V2Green),
                )
            },
            note = "المفاتيح هنا تفضيلات عرض فقط. لا تغيّر إعدادات النظام ولا تحذف ملفًا.",
        ),
        d(
            "ai", "ai-execute", "10.9", "تنفيذ الإجراء بموافقة المستخدم", "EXECUTE ACTION",
            "كل إجراء يفتح شاشته الرسمية", Icons.Rounded.PlayArrow, V2Green,
            pro = true,
            rows = { f, _ ->
                listOf(
                    tr(Icons.Rounded.CleaningServices, "تنظيف الكاش", formatBytes(f.ownCacheBytes), V2Pink),
                    tr(Icons.Rounded.Layers, "مراجعة التكرار", formatBytes(f.media.duplicateGroups.sumOf { it.reclaimableBytes }), V2Violet),
                    tr(Icons.Rounded.BatterySaver, "توفير الطاقة", "من إعدادات Android", V2Green),
                )
            },
            note = "لا يوجد تنفيذ صامت. اختيار الإجراء ينقلك إلى شاشته حيث تراجع وتؤكد.",
            action = "ai-recommendations", actionLabel = "فتح الإجراءات",
        ),
        d(
            "ai", "ai-results", "10.10", "مقارنة النتائج", "BEFORE & AFTER RESULTS",
            "الفرق بين أول وآخر قياس محفوظ", Icons.Rounded.Compare, V2Cyan,
            pro = true,
            rows = { _, h ->
                val first = oldestScan(h)
                val last = newestScan(h)
                if (first == null || last == null || first.timestamp == last.timestamp) {
                    listOf(tr(Icons.Rounded.Info, "تحتاج قياسين", "شغّل الفحص مرتين لرؤية الفرق", V2Soft))
                } else {
                    listOf(
                        tr(Icons.Rounded.Storage, "قبل (الأقدم)", formatBytes(first.storageUsedBytes), V2Violet),
                        tr(Icons.Rounded.Storage, "بعد (الأحدث)", formatBytes(last.storageUsedBytes), V2Cyan),
                        tr(Icons.Rounded.Storage, "تغير التخزين", formatSignedBytes(last.storageUsedBytes - first.storageUsedBytes), V2Cyan),
                        tr(Icons.Rounded.CleaningServices, "تغير الكاش", formatSignedBytes(last.cacheBytes - first.cacheBytes), V2Pink),
                        tr(Icons.Rounded.BatteryFull, "تغير البطارية", formatBatteryDelta(first.batteryLevel, last.batteryLevel), V2Green),
                    )
                }
            },
            note = "الفرق حقيقي من السجل المحلي. لا نعرض تحسينًا لم يحدث.",
        ),
    )

    val all: List<SectionToolDef> =
        duplicates + residue + privacy + deepScan + reports + appManager + storage + battery + ai +
            SectionToolsExpansion.defs
}

/* ------------------------------------------------------------------ *
 * Local recommendation rules (shared by AI screens and reports)
 * ------------------------------------------------------------------ */

internal data class AiRecommendation(
    val title: String,
    val value: String,
    val reason: String,
    val icon: ImageVector,
    val accent: Color,
)

internal fun aiRecommendations(f: DeviceFacts): List<AiRecommendation> {
    val items = mutableListOf<AiRecommendation>()
    if (f.ownCacheBytes > 0L) {
        items += AiRecommendation(
            "تنظيف كاش KNOUX",
            formatBytes(f.ownCacheBytes),
            "كاش التطبيق الحالي ${formatBytes(f.ownCacheBytes)} على ${formatCount(f.ownCacheFiles)} ملفًا.",
            Icons.Rounded.CleaningServices, V2Pink,
        )
    }
    if (f.media.duplicateGroups.isNotEmpty()) {
        items += AiRecommendation(
            "مراجعة الملفات المكررة",
            formatBytes(f.media.duplicateGroups.sumOf { it.reclaimableBytes }),
            "${formatCount(f.media.duplicateGroups.size)} مجموعة متطابقة المحتوى، والحذف يحتاج تأكيدك.",
            Icons.Rounded.Layers, V2Violet,
        )
    }
    if (f.storage.usedFraction >= .82f) {
        items += AiRecommendation(
            "التخزين تحت ضغط",
            percent(f),
            "نسبة الاستخدام تجاوزت 82% من القسم الداخلي.",
            Icons.Rounded.Storage, V2Amber,
        )
    }
    if ((f.battery.temperatureC ?: 0f) >= 43f) {
        items += AiRecommendation(
            "خفض حرارة البطارية",
            f.battery.temperatureC?.let { String.format(java.util.Locale.US, "%.1f°C", it) } ?: "",
            "الحرارة تجاوزت النطاق المريح 43°C.",
            Icons.Rounded.Thermostat, V2ProRed,
        )
    }
    if (f.sensitiveAppCount > 0) {
        items += AiRecommendation(
            "مراجعة الأذونات الحساسة",
            "${formatCount(f.sensitiveAppCount)} تطبيقًا",
            "تطبيقات تعلن أذونات كاميرا أو ميكروفون أو موقع أو ملفات.",
            Icons.Rounded.Shield, V2Cyan,
        )
    }
    return items
}
