package com.knoux.ai.ui.v2

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.ManageSearch
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.InsertDriveFile
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.R
import com.knoux.ai.ui.components.rememberMotionEnabled

/* ------------------------------------------------------------------ *
 * Reference-faithful section hubs.
 *
 * The approved section art (sec1…sec10) re-cuts the Home world for one
 * section: same night sky, same header capsule, a hero emblem on an orbit
 * base with the stag at the right, a glass readings panel (gauge LEFT,
 * three live rows RIGHT), a 3×3 + 4 service grid, one big action pill, the
 * stag PRO banner and the shared V2 bottom bar. Every number is read from
 * this device through DeviceFacts — nothing is a fixture.
 * ------------------------------------------------------------------ */

internal data class HubRefCard(
    val title: String,
    val route: String,
    val art: Int,
    val accent: Color,
    val value: String,
    val gold: Boolean = false,
    val compact: Boolean = false,
    /** Compact card with a value pill + chevron (sec5 row2 reads this way). */
    val pill: Boolean = false,
    /** sec9/10 compact cards carry long two-to-three-line titles. */
    val titleLines: Int = 2,
)

internal data class HubRefWideCard(
    val title: String,
    val route: String,
    val art: Int,
    val gold: Boolean = false,
)

/** One cell of the sec5 stat-trio strip: icon, label, live value, footnote. */
internal data class HubRefStatTrio(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String,
    val value: String,
    val sub: String,
    val route: String,
    val accent: Color,
    /** sec9 strip: cropped neon glyph instead of a Material icon. */
    val art: Int? = null,
)

internal data class HubRefConfig(
    val heroArt: Int,
    val heroSubtitle: String,
    val cards: List<HubRefCard>,
    val ctaLabel: String,
    val ctaRoute: String,
    val bannerTitle: String,
    val bannerSubtitle: String,
    /** Reference hero title when it differs from the service spec title. */
    val heroTitle: String? = null,
    val panelLines: Int = 3,
    val gaugeSizeDp: Int = 104,
    val wideCard: HubRefWideCard? = null,
    /** Two half-width action cards rendered side by side (sec3). */
    val widePair: List<HubRefWideCard> = emptyList(),
    /** Three live stat cells between the readings panel and the grid (sec5). */
    val statTrio: List<HubRefStatTrio> = emptyList(),
    /** Drawable CTA glyph (crosshair …); null = drawn broom. */
    val ctaArt: Int? = null,
    /** Per-section stag cutout (hero + PRO banner). */
    val stagArt: Int = R.drawable.knoux_sec1_stag,
    /** Denser vertical rhythm for sections with 3 full rows + wide card, so
     *  the CTA and PRO banner stay above the fold on a 720×1600 device. */
    val dense: Boolean = false,
    /** Show the identity's stat strip inside the readings panel (sec3). */
    val showStats: Boolean = false,
    /** Render every grid card as a compact icon+title card (sec3 4×2). */
    val compactGrid: Boolean = false,
    /** Centered wide card (sec7 lock card) instead of the full-width row. */
    val wideCentered: Boolean = false,
    /** sec7 readings-panel footer strip: live VPN state + action-plan count. */
    val panelStrip: Pair<String, String>? = null,
)

/** Smart Clean — the first approved section reference (sec1). */
@Composable
internal fun rememberSmartCleanRefConfig(facts: DeviceFacts): HubRefConfig {
    val live = facts.computedAt > 0L
    val mediaOk = facts.media.permissionGranted
    fun bytes(v: Long): String = if (!live) "…" else formatBytes(v)
    fun count(v: Int): String = if (!live) "…" else formatCount(v)
    fun mediaCount(v: Int): String = when {
        !live -> "…"
        !mediaOk -> "يحتاج إذن"
        else -> formatCount(v)
    }
    val tempBytes = facts.ownCacheBytes + facts.media.imagesBytes / 10
    val residueBytes = facts.ownCacheBytes + facts.cleanFiles.apkBytes
    // Recomposed on every facts change — no remember() here, or the loading
    // placeholders would be frozen forever once the live read lands.
    return HubRefConfig(
            heroArt = R.drawable.knoux_sec1_hero,
            heroSubtitle = "تنظيف شامل وآمن لملفات الجهاز القابلة للمراجعة",
            cards = listOf(
                // Row 1 — right → left, exactly as the approved art.
                HubRefCard("الملفات المؤقتة", "clean-cache-audit", R.drawable.knoux_sec1g_temp, V2Violet, bytes(tempBytes)),
                HubRefCard("بقايا التطبيقات", "residue", R.drawable.knoux_sec1g_residue, V2Blue, bytes(residueBytes)),
                HubRefCard("التنزيلات القديمة", "clean-large", R.drawable.knoux_sec1g_downloads, V2Cyan, bytes(facts.cleanFiles.downloadsBytes)),
                // Row 2
                HubRefCard("الكاش القابل للتنظيف", "clean-scan", R.drawable.knoux_sec1g_cache, V2Violet, bytes(facts.ownCacheBytes)),
                HubRefCard("ملفات APK غير المطلوبة", "clean-classify", R.drawable.knoux_sec1g_apk, V2Pink, count(facts.cleanFiles.apkCount)),
                HubRefCard("المجلدات الفارغة", "clean-buckets", R.drawable.knoux_sec1g_empty, V2Violet, count(facts.cleanFiles.emptyFolders)),
                // Row 3 — four compact cards, gold edge on the starter.
                HubRefCard("الصور المصغرة والملفات الخفية", "clean-media-mix", R.drawable.knoux_sec1g_images, V2Blue, mediaCount(facts.media.images + facts.media.videos), compact = true, titleLines = 3),
                HubRefCard("تنظيف آمن بعد المراجعة", "clean-confirm", R.drawable.knoux_sec1g_shield, V2Violet, "تأكيد", compact = true, titleLines = 3),
                HubRefCard("خطة تنظيف ذكية", "clean-free-plan", R.drawable.knoux_sec1g_plan, V2Violet, "خطة", compact = true, titleLines = 3),
                HubRefCard("بدء التنظيف الذكي", "clean-scope", R.drawable.knoux_sec1g_start, RefGold, "بدء", gold = true, compact = true, titleLines = 3),
            ),
            ctaLabel = "بدء التنظيف الذكي الآن",
            ctaRoute = "clean-scan",
            bannerTitle = "KNOUX AI Pro",
            bannerSubtitle = "تنظيف أعمق · مساحة أكبر · أداء أسرع",
    )
}

/** Deep Scan — the second approved section reference (sec2). */
@Composable
internal fun rememberDeepScanRefConfig(facts: DeviceFacts): HubRefConfig {
    val live = facts.computedAt > 0L
    val mediaOk = facts.media.permissionGranted
    fun bytes(v: Long): String = if (!live) "…" else formatBytes(v)
    fun count(v: Int): String = if (!live) "…" else formatCount(v)
    fun mediaCount(v: Int): String = when {
        !live -> "…"
        !mediaOk -> "يحتاج إذن"
        else -> formatCount(v)
    }
    fun cleanCount(v: Int): String = when {
        !live -> "…"
        !mediaOk -> "يحتاج إذن"
        else -> formatCount(v)
    }
    return HubRefConfig(
        heroArt = R.drawable.knoux_sec2_hero,
        heroSubtitle = "تحليل عميق لمساحة جهازك والملفات الحساسة",
        stagArt = R.drawable.knoux_sec2_stag,
        cards = listOf(
            // Row 1 — right → left, exactly as the approved art.
            HubRefCard("فحص شامل للتخزين", "storage", R.drawable.knoux_sec2g_r1c3, V2Violet, bytes(facts.storage.totalBytes)),
            HubRefCard("فحص الملفات الكبيرة", "clean-large", R.drawable.knoux_sec2g_r1c2, V2Violet, mediaCount(facts.media.largestFiles.size)),
            HubRefCard("فحص الملفات المخفية", "dsn-findings", R.drawable.knoux_sec2g_r1c1, V2Violet, cleanCount(facts.cleanFiles.hiddenFiles)),
            // Row 2
            HubRefCard("فحص بقايا التطبيقات", "residue", R.drawable.knoux_sec2g_r2c3, V2Blue, bytes(facts.ownCacheBytes + facts.cleanFiles.apkBytes)),
            HubRefCard("فحص المجلدات اليتيمة", "clean-buckets", R.drawable.knoux_sec2g_r2c2, V2Blue, count(facts.cleanFiles.emptyFolders)),
            HubRefCard("تحليل الوسائط", "clean-media-mix", R.drawable.knoux_sec2g_r2c1, V2Cyan, mediaCount(facts.media.images + facts.media.videos)),
            // Row 3 — full value cards; gold edge on the report card at the LEFT.
            HubRefCard("اكتشاف المخاطر والملفات غير المعتادة", "prv-app-risk", R.drawable.knoux_sec2g_r3c3, V2Violet, count(facts.sensitiveAppCount)),
            HubRefCard("مراجعة النتائج بالتفصيل", "dsn-findings", R.drawable.knoux_sec2g_r3c2, V2Blue, if (live) "مراجعة" else "…"),
            HubRefCard("تقرير فحص شامل", "deep-scan-summary", R.drawable.knoux_sec2g_r3c1, RefGold, if (live) "تقرير" else "…", gold = true),
        ),
        ctaLabel = "بدء الفحص العميق",
        ctaRoute = "deepscan",
        ctaArt = R.drawable.knoux_sec2_cta,
        bannerTitle = "KNOUX AI Pro",
        bannerSubtitle = "فحص أعمق · كشف أدق · حماية أقوى",
        panelLines = 5,
        gaugeSizeDp = 96,
        dense = true,
        wideCard = HubRefWideCard("تحليل مناطق الاستهلاك الأعلى", "sto-categories", R.drawable.knoux_sec2g_w1),
    )
}

/** Duplicates — the third approved section reference (sec3). */
@Composable
internal fun rememberDuplicatesRefConfig(facts: DeviceFacts): HubRefConfig {
    val live = facts.computedAt > 0L
    val mediaOk = facts.media.permissionGranted
    fun count(v: Int): String = when {
        !live -> "…"
        !mediaOk -> "يحتاج إذن"
        else -> formatCount(v)
    }
    val media = facts.media
    val dupImages = media.duplicateGroups.filter { !it.isVideo }.sumOf { it.count }
    val dupVideos = media.duplicateGroups.filter { it.isVideo }.sumOf { it.count }
    return HubRefConfig(
        heroArt = R.drawable.knoux_sec3_hero,
        heroSubtitle = "اكتشاف الصور والفيديوهات والملفات المتطابقة",
        heroTitle = "البحث عن المكررات",
        stagArt = R.drawable.knoux_sec3_stag,
        compactGrid = true,
        showStats = true,
        dense = true,
        panelLines = 4,
        gaugeSizeDp = 96,
        cards = listOf(
            // Row 1 — right → left, exactly as the approved art.
            HubRefCard("صور مكررة", "dup-types", R.drawable.knoux_sec3g_r1c4, V2Violet, count(dupImages)),
            HubRefCard("صور متشابهة", "dup-compare", R.drawable.knoux_sec3g_r1c3, V2Violet, count(media.similarImageCandidates)),
            HubRefCard("فيديوهات مكررة", "dup-types", R.drawable.knoux_sec3g_r1c2, V2Blue, count(dupVideos)),
            HubRefCard("ملفات صوتية مكررة", "dup-scan", R.drawable.knoux_sec3g_r1c1, V2Cyan, count(media.duplicateAudioFiles)),
            // Row 2
            HubRefCard("مستندات مكررة", "dup-scan", R.drawable.knoux_sec3g_r2c4, V2Violet, if (live) "فحص" else "…"),
            HubRefCard("ملفات APK مكررة", "dup-scan", R.drawable.knoux_sec3g_r2c3, V2Blue, count(facts.cleanFiles.apkCount)),
            HubRefCard("أرشيفات مكررة", "dup-scan", R.drawable.knoux_sec3g_r2c2, V2Violet, if (live) "فحص" else "…"),
            HubRefCard("مكررات بين المجلدات", "dup-groups", R.drawable.knoux_sec3g_r2c1, V2Cyan, count(media.duplicateGroups.size)),
        ),
        widePair = listOf(
            // Authored right→left: groups RIGHT, gold review LEFT.
            HubRefWideCard("أكبر مجموعات التكرار", "dup-groups", R.drawable.knoux_sec3g_w2),
            HubRefWideCard("مراجعة النتائج قبل الحذف", "dup-review", R.drawable.knoux_sec3g_w1, gold = true),
        ),
        ctaLabel = "مراجعة المكررات الآن",
        ctaRoute = "duplicates",
        ctaArt = R.drawable.knoux_sec3_cta,
        bannerTitle = "KNOUX AI Pro",
        bannerSubtitle = "إدارة ذكية للمكررات · تحرير مساحة أكبر · أداء أسرع",
    )
}

/** App Residue — the fourth approved section reference (sec4). */
@Composable
internal fun rememberResidueRefConfig(facts: DeviceFacts): HubRefConfig {
    val live = facts.computedAt > 0L
    val mediaOk = facts.media.permissionGranted
    val cf = facts.cleanFiles
    fun bytes(v: Long): String = when {
        !live -> "…"
        !mediaOk -> "يحتاج إذن"
        else -> formatBytes(v)
    }
    fun count(v: Int): String = when {
        !live -> "…"
        !mediaOk -> "يحتاج إذن"
        else -> formatCount(v)
    }
    return HubRefConfig(
        heroArt = R.drawable.knoux_sec4_hero,
        heroSubtitle = "حذف المخلفات المتبقية بعد إزالة التطبيقات",
        stagArt = R.drawable.knoux_sec4_stag,
        dense = true,
        panelLines = 5,
        gaugeSizeDp = 96,
        cards = listOf(
            // Row 1 — right → left, exactly as the approved art.
            HubRefCard("مجلدات التطبيقات المحذوفة", "res-groups", R.drawable.knoux_sec4g_r1c3, V2Violet, count(cf.orphanAppFolders)),
            HubRefCard("بقايا الكاش القديمة", "res-candidates", R.drawable.knoux_sec4g_r1c2, V2Violet, bytes(cf.oldCacheBytes)),
            HubRefCard("مجلدات فارغة بعد الحذف", "res-scan", R.drawable.knoux_sec4g_r1c1, V2Cyan, count(cf.emptyFolders)),
            // Row 2
            HubRefCard("ملفات السجل القديمة", "res-detail", R.drawable.knoux_sec4g_r2c3, V2Violet, count(cf.logFiles)),
            HubRefCard("ملفات تحميل منكسرة", "res-risk", R.drawable.knoux_sec4g_r2c2, V2Pink, count(cf.brokenDownloads)),
            HubRefCard("بقايا WebView", "res-link", R.drawable.knoux_sec4g_r2c1, V2Blue, bytes(cf.browserBytes)),
            // Row 3 — four compact cards, gold history card at the LEFT.
            HubRefCard("بقايا تطبيقات المراسلة", "res-sources", R.drawable.knoux_sec4g_r3c4, V2Violet, "فحص", compact = true, titleLines = 3),
            HubRefCard("بقايا المتصفحات", "res-groups", R.drawable.knoux_sec4g_r3c3, V2Blue, bytes(cf.browserBytes), compact = true, titleLines = 3),
            HubRefCard("بقايا الألعاب وملفات OBB", "res-candidates", R.drawable.knoux_sec4g_r3c2, V2Violet, bytes(cf.obbBytes), compact = true, titleLines = 3),
            HubRefCard("سجل التنظيف والاستعادة", "res-history", R.drawable.knoux_sec4g_r3c1, RefGold, "سجل", gold = true, compact = true, titleLines = 3),
        ),
        ctaLabel = "تنظيف البقايا الان",
        ctaRoute = "residue",
        ctaArt = R.drawable.knoux_sec4_cta,
        bannerTitle = "KNOUX AI Pro",
        bannerSubtitle = "تنظيف أعمق · استعادة مساحة أكبر · أداء أسرع",
    )
}

/** Storage Analysis — the fifth approved section reference (sec5). */
@Composable
internal fun rememberStorageRefConfig(facts: DeviceFacts): HubRefConfig {
    val live = facts.computedAt > 0L
    val mediaOk = facts.media.permissionGranted
    val cf = facts.cleanFiles
    val context = androidx.compose.ui.platform.LocalContext.current
    val growth = remember(facts.computedAt) {
        storageGrowthBytes(readScanHistory(context), System.currentTimeMillis())
    }
    fun bytes(v: Long): String = when {
        !live -> "…"
        !mediaOk -> "يحتاج إذن"
        else -> formatBytes(v)
    }
    val largestFile = facts.media.largestFiles.firstOrNull()
    val reclaimable = facts.media.duplicateGroups.sumOf { it.reclaimableBytes } + cf.residueBytes
    return HubRefConfig(
        heroArt = R.drawable.knoux_sec5_hero,
        heroSubtitle = "رؤية تفصيلية لكيفية استخدام مساحة الجهاز",
        stagArt = R.drawable.knoux_sec5_stag,
        dense = true,
        panelLines = 4,
        gaugeSizeDp = 96,
        statTrio = listOf(
            // Right → left as the approved strip reads.
            HubRefStatTrio(Icons.Rounded.FolderOpen, "أكثر مجلد", bytes(cf.largestBucket?.second ?: 0L), cf.largestBucket?.first ?: "…", "sto-browser", V2Violet),
            HubRefStatTrio(Icons.Rounded.InsertDriveFile, "أكبر ملف", bytes(largestFile?.sizeBytes ?: 0L), largestFile?.name ?: "…", "sto-details", V2Cyan),
            HubRefStatTrio(
                Icons.Rounded.TrendingUp, "نمو التخزين",
                when {
                    !live -> "…"
                    growth == null -> "لا سجل بعد"
                    growth >= 0 -> "+${formatBytes(growth)}"
                    else -> "-${formatBytes(-growth)}"
                },
                "آخر 30 يوماً", "rpt-trends", V2Green,
            ),
        ),
        cards = listOf(
            // Row 1 — right → left.
            HubRefCard("توزيع المساحة حسب الفئة", "sto-categories", R.drawable.knoux_sec5g_r1c3, V2Violet, bytes(cf.scannedBytes)),
            HubRefCard("أكبر الملفات", "sto-sorting", R.drawable.knoux_sec5g_r1c2, V2Cyan, "${formatCount(facts.media.largestFiles.size)} ملف"),
            HubRefCard("تحليل المجلدات", "sto-browser", R.drawable.knoux_sec5g_r1c1, V2Violet, cf.largestBucket?.first ?: "…"),
            // Row 2 — four compact cards.
            HubRefCard("النمو الزمني", "rpt-trends", R.drawable.knoux_sec5g_r2c4, V2Pink, if (growth == null) "لا سجل" else if (growth >= 0) "+${formatBytes(growth)}" else "-${formatBytes(-growth)}", compact = true, pill = true, titleLines = 3),
            HubRefCard("التطبيقات حسب الحجم", "sto-categories", R.drawable.knoux_sec5g_r2c3, V2Cyan, "${formatCount(facts.installedCount)} تطبيق", compact = true, pill = true, titleLines = 3),
            HubRefCard("الصور والفيديوهات حسب النوع", "sto-categories", R.drawable.knoux_sec5g_r2c2, V2Violet, bytes(cf.mediaBucketBytes), compact = true, pill = true, titleLines = 3),
            HubRefCard("تحليل التنزيلات", "sto-used-free", R.drawable.knoux_sec5g_r2c1, V2Cyan, bytes(cf.downloadsBytes), compact = true, pill = true, titleLines = 3),
            // Row 3 — gold export card at the LEFT.
            HubRefCard("المساحة القابلة للاستعادة", "sto-used-free", R.drawable.knoux_sec5g_r3c3, V2Green, bytes(reclaimable)),
            HubRefCard("توصيات ذكية للتنظيف", "smartclean", R.drawable.knoux_sec5g_r3c2, V2Violet, "فتح"),
            HubRefCard("تصدير تقرير التخزين", "rpt-storage", R.drawable.knoux_sec5g_r3c1, RefGold, "تقرير", gold = true),
        ),
        ctaLabel = "عرض تقرير التخزين",
        ctaRoute = "rpt-storage",
        ctaArt = R.drawable.knoux_sec5_cta,
        bannerTitle = "KNOUX AI Pro",
        bannerSubtitle = "تحليل أعمق · تقارير متقدمة · إدارة أذكى للمساحة",
    )
}

/** App Manager — the sixth approved section reference (sec6). */
@Composable
internal fun rememberAppManagerRefConfig(facts: DeviceFacts): HubRefConfig {
    // The approved art draws EMPTY value pills (chevron only) on every grid
    // card; the live counts live in the five readings-panel lines instead.
    return HubRefConfig(
        heroArt = R.drawable.knoux_sec6_hero,
        heroTitle = "إدارة التطبيقات",
        heroSubtitle = "تحكم كامل في تطبيقات جهازك",
        stagArt = R.drawable.knoux_sec6_stag,
        dense = true,
        panelLines = 5,
        gaugeSizeDp = 96,
        cards = listOf(
            // Row 1 — right → left, exactly as the approved art.
            HubRefCard("قائمة التطبيقات المثبتة", "apm-installed", R.drawable.knoux_sec6g_r1c3, V2Violet, ""),
            HubRefCard("التطبيقات الأكبر حجما", "apm-size", R.drawable.knoux_sec6g_r1c2, V2Violet, ""),
            HubRefCard("التطبيقات قليلة الاستخدام", "apm-usage", R.drawable.knoux_sec6g_r1c1, V2Violet, ""),
            // Row 2
            HubRefCard("التطبيقات سريعة التشغيل", "apm-launch", R.drawable.knoux_sec6g_r2c3, V2Cyan, ""),
            HubRefCard("التطبيقات الأعلى استهلاكا للبطارية", "apm-battery", R.drawable.knoux_sec6g_r2c2, V2Green, ""),
            HubRefCard("التطبيقات كثيرة الإشعارات", "apm-notifications", R.drawable.knoux_sec6g_r2c1, V2Violet, ""),
            // Row 3 — four compact cards with empty pills, as the art reads.
            HubRefCard("مراجعة الأذونات", "apm-permissions", R.drawable.knoux_sec6g_r3c4, V2Violet, "", compact = true, pill = true, titleLines = 3),
            HubRefCard("إلغاء التثبيت بعد المراجعة", "apm-uninstall", R.drawable.knoux_sec6g_r3c3, V2Violet, "", compact = true, pill = true, titleLines = 3),
            HubRefCard("اقتراحات التعطيل أو الإيقاف", "apm-disabled", R.drawable.knoux_sec6g_r3c2, V2Violet, "", compact = true, pill = true, titleLines = 3),
            HubRefCard("معلومات الإصدار وملف APK", "apm-package-index", R.drawable.knoux_sec6g_r3c1, V2Violet, "", compact = true, pill = true, titleLines = 3),
        ),
        ctaLabel = "فتح إدارة التطبيقات",
        ctaRoute = "apm-installed",
        ctaArt = R.drawable.knoux_sec6_cta,
        bannerTitle = "KNOUX AI Pro",
        bannerSubtitle = "تحكم أذكى في تطبيقاتك · أداء أسرع · جهاز أكثر تنظيما",
    )
}

/** AI & Recommendations — the ninth approved section reference (sec9). */
@Composable
internal fun rememberAiRefConfig(facts: DeviceFacts): HubRefConfig {
    val live = facts.computedAt > 0L
    val context = androidx.compose.ui.platform.LocalContext.current
    val insights = remember(facts.computedAt) {
        weeklyInsightCount(readScanHistory(context), System.currentTimeMillis())
    }
    val reclaimable = facts.media.duplicateGroups.sumOf { it.reclaimableBytes } +
        facts.cleanFiles.residueBytes + facts.ownCacheBytes
    val suggestedDelete = facts.media.duplicateGroups.sumOf { it.reclaimableBytes } + facts.cleanFiles.residueBytes
    // The approved art draws EMPTY value pills (chevron only) on every grid
    // card; the live priorities live in the four readings-panel lines and
    // the insight strip instead.
    return HubRefConfig(
        heroArt = R.drawable.knoux_sec9_hero,
        heroTitle = "الذكاء الاصطناعي والتوصيات",
        heroSubtitle = "توصيات ذكية مبنية على تحليل الجهاز والاستخدام",
        stagArt = R.drawable.knoux_sec9_stag,
        dense = true,
        panelLines = 4,
        gaugeSizeDp = 96,
        statTrio = listOf(
            // Right → left as the approved strip reads.
            HubRefStatTrio(Icons.Rounded.CalendarMonth, "الرؤى الأسبوعية", if (live) "$insights رؤى" else "…", "", "ai-weekly", V2Violet, art = R.drawable.knoux_sec9s_cal),
            HubRefStatTrio(Icons.Rounded.Delete, "المقترح حذفه", if (live) formatBytes(suggestedDelete) else "…", "", "ai-delete-suggest", V2Pink, art = R.drawable.knoux_sec9s_trash),
            HubRefStatTrio(Icons.Rounded.Layers, "خطة استعادة المساحة", if (live) "حتى ${formatBytes(reclaimable)}" else "…", "", "ai-storage-plan", V2Cyan, art = R.drawable.knoux_sec9s_layers),
        ),
        cards = listOf(
            // Row 1 — right → left, exactly as the approved art (4 compact).
            HubRefCard("ملخص ذكي لحالة الجهاز", "ai-daily-brief", R.drawable.knoux_sec9g_r1c4, V2Cyan, "", compact = true, pill = true, titleLines = 3),
            HubRefCard("أولوية التنظيف", "ai-clean-priority", R.drawable.knoux_sec9g_r1c3, V2Violet, "", compact = true, pill = true, titleLines = 3),
            HubRefCard("أولوية الأداء", "ai-perf-priority", R.drawable.knoux_sec9g_r1c2, V2Cyan, "", compact = true, pill = true, titleLines = 3),
            HubRefCard("أولوية البطارية", "ai-battery", R.drawable.knoux_sec9g_r1c1, V2Violet, "", compact = true, pill = true, titleLines = 3),
            // Row 2
            HubRefCard("أولوية الخصوصية", "ai-privacy-plan", R.drawable.knoux_sec9g_r2c4, V2Violet, "", compact = true, pill = true, titleLines = 3),
            HubRefCard("اقتراحات حذف التطبيقات", "ai-app-delete", R.drawable.knoux_sec9g_r2c3, V2Pink, "", compact = true, pill = true, titleLines = 3),
            HubRefCard("اقتراحات المكررات", "ai-dup-suggest", R.drawable.knoux_sec9g_r2c2, V2Violet, "", compact = true, pill = true, titleLines = 3),
            HubRefCard("خطة استعادة المساحة", "ai-storage-plan", R.drawable.knoux_sec9g_r2c1, V2Cyan, "", compact = true, pill = true, titleLines = 3),
        ),
        widePair = listOf(
            HubRefWideCard("أتمتة ذكية للمهام", "ai-automation", R.drawable.knoux_sec9g_w1),
            HubRefWideCard("رؤى وتوصيات أسبوعية", "ai-weekly", R.drawable.knoux_sec9g_w2),
        ),
        ctaLabel = "عرض التوصيات الذكية",
        ctaRoute = "ai-recommendations",
        ctaArt = R.drawable.knoux_sec9_cta,
        bannerTitle = "KNOUX AI Pro",
        bannerSubtitle = "ذكاء أعمق · توصيات أدق · جهاز أفضل",
    )
}

/** Reports & History — the tenth approved section reference (sec10). */
@Composable
internal fun rememberReportsRefConfig(facts: DeviceFacts, journal: ReportsJournal): HubRefConfig {
    val live = facts.computedAt > 0L
    fun n(v: Int): String = if (live) formatCount(v) else "…"
    // The approved art draws EMPTY value pills (chevron only) on every grid
    // card; the real journal totals live in the panel lines + quartet strip.
    return HubRefConfig(
        heroArt = R.drawable.knoux_sec10_hero,
        heroTitle = "التقارير والسجل",
        heroSubtitle = "سجل العمليات والتقارير والتحليلات",
        stagArt = R.drawable.knoux_sec10_stag,
        dense = true,
        panelLines = 3,
        gaugeSizeDp = 96,
        statTrio = listOf(
            // Right → left as the approved quartet reads. No scheduler exists,
            // so scheduled tasks stay honestly unavailable.
            HubRefStatTrio(Icons.Rounded.CalendarMonth, "المهام المجدولة", "غير متاح", "", "rpt-trends", RefGold, art = R.drawable.knoux_sec10s_cal),
            HubRefStatTrio(Icons.Rounded.ManageSearch, "تقارير الفحص", n(journal.scanReports), "", "rpt-scans", V2Cyan, art = R.drawable.knoux_sec10s_mag),
            HubRefStatTrio(Icons.Rounded.VerifiedUser, "تقارير الخصوصية", n(journal.privacyReports), "", "rpt-privacy", V2Violet, art = R.drawable.knoux_sec10s_shield),
            HubRefStatTrio(Icons.Rounded.BatteryChargingFull, "تقارير البطارية", n(journal.batteryReports), "", "rpt-battery", V2Green, art = R.drawable.knoux_sec10s_bat),
        ),
        cards = listOf(
            // Row 1 — right → left, exactly as the approved art.
            HubRefCard("سجل عمليات التنظيف", "rpt-cleanup", R.drawable.knoux_sec10g_r1c3, V2Violet, ""),
            HubRefCard("المساحة التي تم استعادتها", "rpt-storage", R.drawable.knoux_sec10g_r1c2, V2Violet, ""),
            HubRefCard("سجل حذف المكررات", "rpt-cleanup", R.drawable.knoux_sec10g_r1c1, V2Violet, ""),
            // Row 2
            HubRefCard("سجل تنظيف التطبيقات", "rpt-cleanup", R.drawable.knoux_sec10g_r2c3, V2Violet, ""),
            HubRefCard("تقارير الفحص", "rpt-scans", R.drawable.knoux_sec10g_r2c2, V2Cyan, ""),
            HubRefCard("تقارير البطارية والأداء", "rpt-battery", R.drawable.knoux_sec10g_r2c1, V2Green, ""),
            // Row 3 — four compact cards, gold export card at the LEFT.
            HubRefCard("تقارير الخصوصية", "rpt-privacy", R.drawable.knoux_sec10g_r3c4, V2Violet, "", compact = true, pill = true, titleLines = 3),
            HubRefCard("سجل توصيات الذكاء الاصطناعي", "ai-weekly", R.drawable.knoux_sec10g_r3c3, V2Cyan, "", compact = true, pill = true, titleLines = 3),
            HubRefCard("سجل المهام المجدولة", "rpt-trends", R.drawable.knoux_sec10g_r3c2, V2Violet, "", compact = true, pill = true, titleLines = 3),
            HubRefCard("تصدير ومشاركة التقارير", "rpt-export", R.drawable.knoux_sec10g_r3c1, RefGold, "", compact = true, pill = true, gold = true, titleLines = 3),
        ),
        ctaLabel = "فتح التقارير والسجل",
        ctaRoute = "rpt-device",
        ctaArt = R.drawable.knoux_sec10_cta,
        bannerTitle = "KNOUX AI Pro",
        bannerSubtitle = "تقارير أعمق · تحليلات أدق · سجل غير محدود",
    )
}

/** Battery & Performance — the eighth approved section reference (sec8). */
@Composable
internal fun rememberBatteryRefConfig(facts: DeviceFacts): HubRefConfig {
    // The approved art draws EMPTY value pills (chevron only) on every grid
    // card; the live readings live in the four readings-panel lines instead.
    return HubRefConfig(
        heroArt = R.drawable.knoux_sec8_hero,
        heroTitle = "البطارية والأداء",
        heroSubtitle = "تحسين الطاقة والسرعة واستقرار الجهاز",
        stagArt = R.drawable.knoux_sec8_stag,
        dense = true,
        panelLines = 4,
        gaugeSizeDp = 96,
        cards = listOf(
            // Row 1 — right → left, exactly as the approved art.
            HubRefCard("صحة البطارية", "bat-health-lens", R.drawable.knoux_sec8g_r1c3, V2Pink, ""),
            HubRefCard("التطبيقات المستنزفة للطاقة", "bat-drain-apps", R.drawable.knoux_sec8g_r1c2, V2Violet, ""),
            HubRefCard("النشاط الخلفي", "bat-background", R.drawable.knoux_sec8g_r1c1, V2Cyan, ""),
            // Row 2
            HubRefCard("Wake locks والتنبيهات", "bat-wakelocks", R.drawable.knoux_sec8g_r2c3, V2Violet, ""),
            HubRefCard("درجة الحرارة", "bat-temperature", R.drawable.knoux_sec8g_r2c2, V2Pink, ""),
            HubRefCard("ضغط الذاكرة RAM", "bat-ram-pressure", R.drawable.knoux_sec8g_r2c1, V2Violet, ""),
            // Row 3 — four compact cards with empty pills, as the art reads.
            HubRefCard("استهلاك المعالج", "bat-cpu", R.drawable.knoux_sec8g_r3c4, V2Violet, "", compact = true, pill = true, titleLines = 3),
            HubRefCard("أثر التخزين على الأداء", "bat-storage-impact", R.drawable.knoux_sec8g_r3c3, V2Violet, "", compact = true, pill = true, titleLines = 3),
            HubRefCard("توصيات توفير الطاقة", "bat-energy-tips", R.drawable.knoux_sec8g_r3c2, V2Green, "", compact = true, pill = true, titleLines = 3),
            HubRefCard("تقرير الأداء العام", "bat-perf-report", R.drawable.knoux_sec8g_r3c1, V2Violet, "", compact = true, pill = true, titleLines = 3),
        ),
        ctaLabel = "تحسين الأداء الآن",
        ctaRoute = "battery",
        ctaArt = R.drawable.knoux_sec8_cta,
        bannerTitle = "KNOUX AI Pro",
        bannerSubtitle = "أداء أقوى · بطارية أطول · استقرار أعلى",
    )
}

/** Privacy & Security — the seventh approved section reference (sec7). */
@Composable
internal fun rememberPrivacyRefConfig(facts: DeviceFacts): HubRefConfig {
    val live = facts.computedAt > 0L
    val plan = privacyActionPlanCount(
        facts.sensitiveAppCount, facts.overlayAppCount,
        captureRiskCount(facts.installedApps), facts.lockSecure, facts.usageAccessGranted,
    )
    // The approved art draws EMPTY value pills (chevron only) on every grid
    // card; the live counts live in the readings panel + footer strip.
    return HubRefConfig(
        heroArt = R.drawable.knoux_sec7_hero,
        heroTitle = "الخصوصية والأمان",
        heroSubtitle = "مراجعة الأذونات والمخاطر الأمنية وخصوصية الاستخدام",
        stagArt = R.drawable.knoux_sec7_stag,
        dense = true,
        panelLines = 4,
        gaugeSizeDp = 96,
        panelStrip = Pair(
            if (!live) "…" else if (facts.vpnActive) "متصل" else "غير متصل",
            if (live) "${formatCount(plan)} عناصر" else "…",
        ),
        cards = listOf(
            // Row 1 — right → left, exactly as the approved art.
            HubRefCard("الأذونات الحساسة", "prv-permissions", R.drawable.knoux_sec7g_r1c3, V2Violet, ""),
            HubRefCard("خدمات إمكانية الوصول", "prv-access-services", R.drawable.knoux_sec7g_r1c2, V2Violet, ""),
            HubRefCard("تطبيقات الظهور فوق التطبيقات", "prv-overlay-apps", R.drawable.knoux_sec7g_r1c1, V2Violet, ""),
            // Row 2
            HubRefCard("تطبيقات مسؤول الجهاز", "prv-device-admin", R.drawable.knoux_sec7g_r2c3, V2Cyan, ""),
            HubRefCard("حالة VPN / Proxy", "prv-vpn-proxy", R.drawable.knoux_sec7g_r2c2, V2Violet, ""),
            HubRefCard("خصوصية الحافظة", "prv-clipboard", R.drawable.knoux_sec7g_r2c1, V2Violet, ""),
            // Row 3 — gold action-plan card at the LEFT.
            HubRefCard("المتعقبات والمخاطر المحتملة", "prv-ranking", R.drawable.knoux_sec7g_r3c3, V2Cyan, ""),
            HubRefCard("مخاطر الالتقاط والتسجيل", "prv-capture-risk", R.drawable.knoux_sec7g_r3c2, V2Pink, ""),
            HubRefCard("خطة إجراءات الخصوصية", "prv-action-plan", R.drawable.knoux_sec7g_r3c1, RefGold, "", gold = true),
        ),
        wideCard = HubRefWideCard("حالة القفل والبصمة", "prv-lock-status", R.drawable.knoux_sec7g_w1),
        wideCentered = true,
        ctaLabel = "مراجعة الخصوصية الآن",
        ctaRoute = "privacy",
        ctaArt = R.drawable.knoux_sec7_cta,
        bannerTitle = "KNOUX AI Pro",
        bannerSubtitle = "حماية أعمق · خصوصية أقوى · أمان متقدم",
    )
}

@Composable
fun V2SectionHubRefScreen(
    route: String,
    onBack: () -> Unit,
    onPro: () -> Unit,
    onProfile: (() -> Unit)? = null,
    onNotifications: (() -> Unit)? = null,
) {
    val entry = SectionHub.byRoute(route) ?: return
    val facts = rememberDeviceFacts()
    val context = androidx.compose.ui.platform.LocalContext.current
    val navigate = LocalServiceNavigate.current
    val journal by androidx.compose.runtime.produceState(ReportsJournal(), facts.computedAt) {
        value = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { readReportsJournal(context) }
    }
    val identity = remember(facts.computedAt, entry.key, journal) { sectionIdentity(entry.key, facts) }
    val config = when (entry.key) {
        "deepscan" -> rememberDeepScanRefConfig(facts)
        "duplicates" -> rememberDuplicatesRefConfig(facts)
        "residue" -> rememberResidueRefConfig(facts)
        "storage" -> rememberStorageRefConfig(facts)
        "appmanager" -> rememberAppManagerRefConfig(facts)
        "privacy" -> rememberPrivacyRefConfig(facts)
        "battery" -> rememberBatteryRefConfig(facts)
        "ai" -> rememberAiRefConfig(facts)
        "reports" -> rememberReportsRefConfig(facts, journal)
        else -> rememberSmartCleanRefConfig(facts)
    }
    HubRefScreenFrame(
        title = config.heroTitle ?: entry.spec.title,
        config = config,
        identity = identity,
        onBack = onBack,
        onPro = onPro,
        onMenu = { navigate("settings") },
        onProfile = onProfile,
        onNotifications = onNotifications,
    )
}

/**
 * Device Tools — the fifth root destination, rendered with the same approved
 * section language as the ten reference hubs: one hero stage, one readings
 * panel, one grid of live cards, one CTA and one PRO banner.
 */
@Composable
internal fun V2ToolsHubRefScreen(
    onBack: () -> Unit,
    onPro: () -> Unit,
    onMenu: () -> Unit,
    onProfile: (() -> Unit)? = null,
    onNotifications: (() -> Unit)? = null,
) {
    val facts = rememberDeviceFacts()
    val identity = remember(facts.computedAt) { sectionIdentity("appmanager", facts) }
    HubRefScreenFrame(
        title = "أدوات الجهاز",
        config = rememberToolsRefConfig(facts),
        identity = identity,
        onBack = onBack,
        onPro = onPro,
        onMenu = onMenu,
        onProfile = onProfile,
        onNotifications = onNotifications,
    )
}

@Composable
private fun HubRefScreenFrame(
    title: String,
    config: HubRefConfig,
    identity: SectionIdentity,
    onBack: () -> Unit,
    onPro: () -> Unit,
    onMenu: () -> Unit,
    onProfile: (() -> Unit)? = null,
    onNotifications: (() -> Unit)? = null,
) {
    val navigate = LocalServiceNavigate.current
    // Approved art is night-lit and composed LTR (gauge left, stag right);
    // Arabic strings keep their own RTL shaping inside KText/TextDirection.
    androidx.compose.runtime.CompositionLocalProvider(
        LocalDayMode provides false,
        LocalKnouxOnDark provides true,
        LocalLayoutDirection provides LayoutDirection.Ltr,
    ) {
        Box(Modifier.fillMaxSize()) {
            RefHomeBackground()
            Column(Modifier.fillMaxSize()) {
                LazyColumn(
                    Modifier.weight(1f).windowInsetsPadding(WindowInsets.statusBars),
                    contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 4.dp, bottom = 10.dp),
                ) {
                    item { HubRefTopBar(onBack, onPro, onMenu, onProfile, onNotifications) }
                    item {
                        HubRefHeroStage(
                            title = title,
                            subtitle = config.heroSubtitle,
                            heroArt = config.heroArt,
                            stagArt = config.stagArt,
                            dense = config.dense,
                        )
                    }
                    item { Spacer(Modifier.height(if (config.dense) 2.dp else 4.dp)) }
                    item { HubRefReadingsPanel(identity, config.panelLines, config.gaugeSizeDp, config.showStats, config.panelStrip, navigate) }
                    if (config.statTrio.isNotEmpty()) {
                        item { Spacer(Modifier.height(if (config.dense) 5.dp else 8.dp)) }
                        item { HubRefStatTrioRow(config.statTrio, config.dense, navigate) }
                    }
                    item { Spacer(Modifier.height(if (config.dense) 6.dp else 10.dp)) }
                    item { HubRefGrid(config.cards, navigate, config.dense, config.compactGrid) }
                    if (config.wideCard != null) {
                        item { Spacer(Modifier.height(if (config.dense) 5.dp else 8.dp)) }
                        if (config.wideCentered) {
                            item { HubRefWideCentered(config.wideCard, config.dense) { navigate(config.wideCard.route) } }
                        } else {
                            item { HubRefWideRow(config.wideCard, config.dense) { navigate(config.wideCard.route) } }
                        }
                    }
                    if (config.widePair.isNotEmpty()) {
                        item { Spacer(Modifier.height(if (config.dense) 5.dp else 8.dp)) }
                        item { HubRefWidePairRow(config.widePair, config.dense, navigate) }
                    }
                    item { Spacer(Modifier.height(if (config.dense) 7.dp else 12.dp)) }
                    item { HubRefCta(config.ctaLabel, config.ctaArt, config.dense) { navigate(config.ctaRoute) } }
                    item { Spacer(Modifier.height(if (config.dense) 6.dp else 10.dp)) }
                    item { HubRefProBanner(config.bannerTitle, config.bannerSubtitle, config.stagArt, config.dense, onPro) }
                    item { Spacer(Modifier.height(6.dp)) }
                    if (!config.dense) item { LiveReadNotice() }
                }
            }
        }
    }
}

/** Device Tools hub — live cards for every tool route reachable from Home. */
@Composable
internal fun rememberToolsRefConfig(facts: DeviceFacts): HubRefConfig {
    val live = facts.computedAt > 0L
    fun count(v: Int): String = if (live) "${formatCount(v)} تطبيق" else "…"
    fun ready(label: String): String = if (live) label else "…"
    return HubRefConfig(
        heroArt = R.drawable.knoux_sec5_hero,
        heroTitle = "أدوات الجهاز",
        heroSubtitle = "تحكم كامل في الأداء والمساحة والخصوصية",
        stagArt = R.drawable.knoux_sec1_stag,
        dense = true,
        compactGrid = true,
        panelLines = 4,
        gaugeSizeDp = 96,
        cards = listOf(
            // Row 1 — the four owner-facing tools, right → left.
            HubRefCard("مدير التطبيقات", "appmanager", R.drawable.knoux_svc_apps, V2Cyan, count(facts.installedCount)),
            HubRefCard("تحليل التخزين", "storage", R.drawable.knoux_svc_storage, V2Cyan, ready(formatBytes(facts.storage.freeBytes))),
            HubRefCard("البطارية", "battery", R.drawable.knoux_svc_battery, V2Green, facts.battery.levelPercent?.let { "$it%" } ?: "—"),
            HubRefCard("الخصوصية", "privacy", R.drawable.knoux_svc_privacy, V2Violet, count(facts.sensitiveAppCount)),
            // Row 2 — analysis and inspection.
            HubRefCard("التحسين الذكي", "ai", R.drawable.knoux_svc_ai, V2Pink, ready("اقتراحات")),
            HubRefCard("مستكشف الملفات", "file-explorer", R.drawable.knoux_svc_residue, V2Blue, ready("فتح")),
            HubRefCard("معلومات الجهاز", "device-info", R.drawable.knoux_svc_deepscan, V2Violet, ready("قراءة")),
            HubRefCard("فحص الشبكة", "network-scan", R.drawable.knoux_sec1g_cache, V2Cyan, ready("فحص")),
            // Row 3 — protection and records.
            HubRefCard("فحص الأمان", "security-scan", R.drawable.knoux_sec1g_shield, V2Pink, ready("فحص")),
            HubRefCard("مدير الإشعارات", "notification-manager", R.drawable.knoux_svc_duplicates, V2Amber, ready("إدارة")),
            HubRefCard("مركز التقارير", "reports", R.drawable.knoux_sec10_cta, V2Violet, ready("تقرير")),
            HubRefCard("إعدادات KNOUX", "settings", R.drawable.knoux_sec1g_plan, RefGold, ready("فتح"), gold = true),
        ),
        ctaLabel = "فتح مدير التطبيقات الآن",
        ctaRoute = "appmanager",
        ctaArt = R.drawable.knoux_svc_apps,
        bannerTitle = "KNOUX AI Pro",
        bannerSubtitle = "أدوات أعمق · تحكم أوسع · قراءة مباشرة من جهازك",
    )
}

/* ------------------------------------------------------------------ *
 * Header — exactly the approved chrome: small circular back at the start,
 * the official lockup dead centre, the gold hamburger at the end. No
 * competing full-width capsule, so the hero stays the only stage.
 * ------------------------------------------------------------------ */

@Composable
private fun HubRefTopBar(
    onBack: () -> Unit,
    onPro: () -> Unit,
    onMenu: () -> Unit,
    onProfile: (() -> Unit)? = null,
    onNotifications: (() -> Unit)? = null,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 2.dp),
    ) {
        Row(Modifier.align(Alignment.CenterStart), verticalAlignment = Alignment.CenterVertically) {
            if (onProfile != null) {
                // Root destination: profile + notifications, exactly like Home.
                RefCircleButton(Icons.Rounded.Person, "الحساب", onProfile)
                if (onNotifications != null) {
                    RefCircleButton(Icons.Rounded.NotificationsNone, "التنبيهات", onNotifications)
                }
            } else {
                RefCircleButton(Icons.AutoMirrored.Rounded.ArrowBack, "رجوع", onBack)
            }
        }
        Row(
            Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Official stag — fixed size on every hub, standing on a lit
            // ground plane so the header stays balanced. No float: grounded.
            HubRefHeaderStag(Modifier.size(width = 30.dp, height = 48.dp))
            Spacer(Modifier.width(6.dp))
            Image(
                painter = painterResource(R.drawable.knoux_symbol_alpha),
                contentDescription = "KNOUX",
                modifier = Modifier.height(22.dp),
            )
            Spacer(Modifier.width(5.dp))
            Image(
                painter = painterResource(R.drawable.knoux_wordmark_alpha),
                contentDescription = "knoux ai",
                modifier = Modifier.height(13.dp),
            )
            Spacer(Modifier.width(7.dp))
            Row(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF3A2404), Color(0xFF241503))))
                    .border(1.2.dp, Brush.horizontalGradient(listOf(RefGold, RefGoldBright, RefGold)), RoundedCornerShape(50))
                    .clickable(onClickLabel = "Pro", onClick = onPro)
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                KText("PRO", size = 9.sp, weight = FontWeight.Black, color = RefGoldBright, letterSpacing = .7.sp)
            }
        }
        Box(Modifier.align(Alignment.CenterEnd)) {
            RefMenuButton("القائمة", onMenu)
        }
    }
}

/* ------------------------------------------------------------------ *
 * Header stag — official asset, FIXED size on every hub, grounded on a
 * lit night plane (owner: «بمقاس ثابت وتحته الأرض ليكون معتدل»).
 * ------------------------------------------------------------------ */

@Composable
private fun HubRefHeaderStag(modifier: Modifier = Modifier) {
    Box(modifier) {
        // Tight-cropped official stag (alpha bbox of knoux_stag_official.png,
        // art unchanged). 959×1203 → 30dp wide ≈ 38dp tall.
        Image(
            painter = painterResource(R.drawable.knoux_stag_home),
            contentDescription = null,
            modifier = Modifier.align(Alignment.TopCenter).width(30.dp).height(38.dp),
            contentScale = ContentScale.Fit,
        )
        // Ground drawn OVER the hoof tips so the stag stands ON terrain,
        // exactly like the grounded Home stag — but header-scale.
        Canvas(Modifier.fillMaxSize()) {
            val cx = size.width * .5f
            val gy = size.height - 7.dp.toPx()
            // Cyan ground glow pooling beneath the outcrop.
            drawOval(
                Brush.radialGradient(listOf(V2Cyan.withAlpha(.30f), Color(0xFF7A2DFF).withAlpha(.18f), Color.Transparent), center = Offset(cx, gy), radius = size.width * .75f),
                topLeft = Offset(cx - size.width * .75f, gy - 6.dp.toPx()),
                size = Size(size.width * 1.5f, 12.dp.toPx()),
            )
            // Rock body: dark indigo mass fading into the header below.
            drawOval(
                Brush.verticalGradient(listOf(Color(0xFF33176B), Color(0xFF150A38), Color.Transparent)),
                topLeft = Offset(cx - size.width * .52f, gy - 2.5.dp.toPx()),
                size = Size(size.width * 1.04f, 9.dp.toPx()),
            )
            // Lit top plane the hooves stand on.
            drawOval(
                Brush.verticalGradient(listOf(Color(0xFF4A2590), Color(0xFF2A1157))),
                topLeft = Offset(cx - size.width * .46f, gy - 3.dp.toPx()),
                size = Size(size.width * .92f, 5.dp.toPx()),
            )
            // Contact shadow under the hooves.
            drawOval(
                Color.Black.withAlpha(.5f),
                topLeft = Offset(cx - size.width * .30f, gy - 2.dp.toPx()),
                size = Size(size.width * .60f, 3.dp.toPx()),
            )
            // Cyan-lit crest edge.
            drawOval(
                Brush.horizontalGradient(listOf(Color.Transparent, V2Cyan.withAlpha(.8f), Color(0xFF4DD9FF).withAlpha(.9f), Color.Transparent)),
                topLeft = Offset(cx - size.width * .46f, gy - 3.dp.toPx()),
                size = Size(size.width * .92f, 5.dp.toPx()),
                style = Stroke(.9.dp.toPx()),
            )
        }
    }
}

/* ------------------------------------------------------------------ *
 * Hero stage — gold title + bar, subtitle, emblem on orbit base.
 * The stag now lives in the header (owner directive, fixed size).
 * ------------------------------------------------------------------ */

@Composable
private fun HubRefHeroStage(title: String, subtitle: String, heroArt: Int, stagArt: Int, dense: Boolean = false) {
    val motion = rememberMotionEnabled()
    val breath = rememberInfiniteTransition(label = "hubRefHero")
    val float by breath.animateFloat(
        initialValue = 0f,
        targetValue = if (motion) -3f else 0f,
        animationSpec = infiniteRepeatable(tween(4200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "heroFloat",
    )
    Box(Modifier.fillMaxWidth().height(if (dense) 150.dp else 184.dp)) {
        Column(Modifier.fillMaxWidth().padding(top = 2.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Approved art: cyan tick LEFT of the gold title.
                Box(
                    Modifier
                        .width(4.dp)
                        .height(22.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Brush.verticalGradient(listOf(V2Cyan, V2Blue))),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    title,
                    style = TextStyle(
                        brush = Brush.verticalGradient(listOf(RefGoldBright, RefGold, Color(0xFFE8B33C))),
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Black,
                        textDirection = TextDirection.Rtl,
                    ),
                    maxLines = 1,
                )
            }
            Spacer(Modifier.height(3.dp))
            Text(
                subtitle,
                style = TextStyle(color = Color.White.withAlpha(.88f), fontSize = 11.sp, fontWeight = FontWeight.Medium, textDirection = TextDirection.Rtl),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        // Emblem centered on its orbit base; base rings drawn beneath it.
        Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(if (dense) 104.dp else 130.dp), contentAlignment = Alignment.BottomCenter) {
            Canvas(Modifier.fillMaxSize()) {
                val cx = size.width * .5f
                val baseCy = size.height - 12.dp.toPx()
                drawOval(
                    Brush.radialGradient(listOf(V2Cyan.withAlpha(.30f), V2Violet.withAlpha(.22f), Color.Transparent), center = Offset(cx, baseCy), radius = size.width * .40f),
                    topLeft = Offset(cx - size.width * .40f, baseCy - 14.dp.toPx()),
                    size = Size(size.width * .80f, 28.dp.toPx()),
                )
                for (i in 0 until 4) {
                    drawOval(
                        brush = Brush.sweepGradient(listOf(Color.Transparent, V2Cyan, V2Violet, RefGold, V2Pink, Color.Transparent), center = Offset(cx, baseCy)),
                        topLeft = Offset(cx - size.width * (.24f + i * .05f), baseCy - (5 + i * 3.4f).dp.toPx()),
                        size = Size(size.width * (.48f + i * .10f), (10 + i * 6.8f).dp.toPx()),
                        style = Stroke((1.8f - i * .3f).dp.toPx()),
                        alpha = .85f - i * .15f,
                    )
                }
            }
            Image(
                painter = painterResource(heroArt),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-6).dp)
                    .height(if (dense) 100.dp else 128.dp)
                    .graphicsLayer { translationY = float.dp.toPx() * .6f },
                contentScale = ContentScale.Fit,
            )
        }
        // Approved art: the neon stag stands on the ridge at the RIGHT of the
        // hero stage, antlers level with the title, hooves above the orbit base.
        Image(
            painter = painterResource(stagArt),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = if (dense) 3.dp else 5.dp)
                .height(if (dense) 96.dp else 120.dp)
                .graphicsLayer { translationY = float.dp.toPx() * .35f },
            contentScale = ContentScale.Fit,
        )
    }
}

/* ------------------------------------------------------------------ *
 * Readings panel — rainbow gauge LEFT, three live rows RIGHT
 * ------------------------------------------------------------------ */

@Composable
private fun HubRefReadingsPanel(
    identity: SectionIdentity,
    lines: Int = 3,
    gaugeDp: Int = 104,
    showStats: Boolean = false,
    strip: Pair<String, String>? = null,
    navigate: (String) -> Unit = {},
) {
    val shape = RoundedCornerShape(24.dp)
    val compact = lines > 3
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(Color(0xFF170C38), Color(0xFF1D1145), Color(0xFF170C38))))
            .border(1.3.dp, Brush.linearGradient(listOf(V2Violet.withAlpha(.9f), V2Cyan.withAlpha(.5f), V2Pink.withAlpha(.8f))), shape)
            .padding(horizontal = 12.dp, vertical = if (compact) 8.dp else 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            HubRefGauge(
                identity.gaugePercent, identity.gaugeLabel, identity.gaugeValue,
                identity.gaugeIcon ?: Icons.Rounded.Storage,
                identity.gaugeIconTint ?: RefGold,
                Modifier.size(gaugeDp.dp),
                identity.gaugeSub,
            )
            Spacer(Modifier.width(10.dp))
            Box(Modifier.width(1.dp).height(if (compact) 118.dp else 86.dp).background(Brush.verticalGradient(listOf(Color.Transparent, Color.White.withAlpha(.30f), Color.Transparent))))
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(if (compact) 5.dp else 8.dp)) {
                identity.lines.take(lines).forEach { line -> HubRefMetricLine(line, compact) }
            }
        }
        if (strip != null) {
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.withAlpha(.05f))
                    .border(1.dp, Brush.horizontalGradient(listOf(V2Violet.withAlpha(.7f), V2Cyan.withAlpha(.5f), V2Pink.withAlpha(.7f))), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Approved strip reads RTL: action plan RIGHT, VPN state LEFT.
                Row(
                    Modifier.weight(1f).clickable(role = Role.Button, onClickLabel = "خطة الإجراءات") { navigate("prv-action-plan") },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Rounded.CheckCircle, null, tint = V2Cyan, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(5.dp))
                    Text(
                        "خطة الإجراءات : ${strip.second}",
                        style = TextStyle(color = Color.White.withAlpha(.92f), fontSize = 9.sp, fontWeight = FontWeight.Bold, textDirection = TextDirection.Rtl),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Box(Modifier.width(1.dp).height(14.dp).background(Color.White.withAlpha(.25f)))
                Row(
                    Modifier.weight(1f).clickable(role = Role.Button, onClickLabel = "حالة VPN") { navigate("prv-vpn-proxy") },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(
                        "حالة VPN / Proxy : ",
                        style = TextStyle(color = Color.White.withAlpha(.92f), fontSize = 9.sp, fontWeight = FontWeight.Bold, textDirection = TextDirection.Rtl),
                        maxLines = 1,
                    )
                    Text(
                        strip.first,
                        style = TextStyle(color = if (strip.first == "متصل") V2Green else V2Pink, fontSize = 9.sp, fontWeight = FontWeight.Black, textDirection = TextDirection.Rtl),
                        maxLines = 1,
                    )
                    Spacer(Modifier.width(5.dp))
                    Icon(Icons.Rounded.Shield, null, tint = V2Violet, modifier = Modifier.size(15.dp))
                }
            }
        }
        if (showStats && identity.stats.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(Brush.horizontalGradient(listOf(Color.Transparent, Color.White.withAlpha(.25f), Color.Transparent))))
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                identity.stats.take(2).forEach { stat ->
                    Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Icon(stat.icon, null, tint = stat.accent, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(7.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                stat.label,
                                style = TextStyle(color = Color.White.withAlpha(.85f), fontSize = 8.5.sp, fontWeight = FontWeight.Bold, textDirection = TextDirection.Rtl),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                stat.value,
                                style = TextStyle(color = stat.accent, fontSize = 12.sp, fontWeight = FontWeight.Black, textDirection = TextDirection.Rtl),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HubRefGauge(percent: Int, label: String, value: String?, icon: ImageVector, iconTint: Color, modifier: Modifier = Modifier, sub: String? = null) {
    val motion = rememberMotionEnabled()
    val transition = rememberInfiniteTransition(label = "hubRefGauge")
    val glow by transition.animateFloat(
        initialValue = .75f,
        targetValue = if (motion) 1f else .85f,
        animationSpec = infiniteRepeatable(tween(2400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "gaugeGlow",
    )
    Box(modifier, contentAlignment = Alignment.Center) {
        // Approved art draws a FULL rainbow ring; the sweep encodes progress
        // only in the legacy hubs, never as an empty gray track here.
        val sweep = if (value != null) 360f else 360f * (percent / 100f).coerceIn(0f, 1f)
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 8.dp.toPx()
            val inset = stroke / 2 + 2.dp.toPx()
            val arcSize = Size(size.width - inset * 2, size.height - inset * 2)
            val topLeft = Offset(inset, inset)
            // Bloom underlay.
            drawArc(
                brush = Brush.sweepGradient(listOf(V2Pink, RefGold, V2Green, V2Cyan, V2Violet, V2Pink)),
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(stroke * 2.1f, cap = StrokeCap.Round),
                alpha = .22f * glow,
            )
            // Track.
            drawArc(Color.White.withAlpha(.08f), -90f, 360f, false, topLeft, arcSize, style = Stroke(stroke, cap = StrokeCap.Round))
            // Rainbow progress.
            drawArc(
                brush = Brush.sweepGradient(listOf(V2Pink, RefGold, V2Green, V2Cyan, V2Violet, V2Pink)),
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(stroke, cap = StrokeCap.Round),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val shown = value ?: "$percent%"
            Text(
                shown,
                style = TextStyle(color = Color.White, fontSize = if (shown.length > 8) 11.sp else 19.sp, fontWeight = FontWeight.Black),
                maxLines = 1,
            )
            Text(
                label,
                style = TextStyle(color = Color.White.withAlpha(.85f), fontSize = 8.5.sp, fontWeight = FontWeight.Bold, textDirection = TextDirection.Rtl),
                maxLines = 2,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
            )
            if (sub != null) {
                // Risk bands read green/amber/pink; other subs stay green like
                // the approved art's pill.
                val band = when (sub) {
                    "مرتفع", "يحتاج عناية" -> V2Pink
                    "متوسط", "جيد" -> RefGold
                    else -> V2Green
                }
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(band.withAlpha(.14f))
                        .border(1.dp, band.withAlpha(.8f), RoundedCornerShape(50))
                        .padding(horizontal = 8.dp, vertical = 1.dp),
                ) {
                    Text(
                        sub,
                        style = TextStyle(color = band, fontSize = 8.sp, fontWeight = FontWeight.Black, textDirection = TextDirection.Rtl),
                        maxLines = 1,
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
private fun HubRefMetricLine(line: SectionLine, compact: Boolean = false) {
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            // Approved row reads RTL: value at the RIGHT end, label beside the
            // icon at the LEFT end of the rows column.
            Icon(line.icon, null, tint = line.accent, modifier = Modifier.size(if (compact) 14.dp else 16.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                line.label,
                style = TextStyle(color = Color.White.withAlpha(.92f), fontSize = if (compact) 9.sp else 10.sp, fontWeight = FontWeight.Bold, textDirection = TextDirection.Rtl),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
            )
            Spacer(Modifier.width(6.dp))
            Text(
                line.value,
                style = TextStyle(color = Color.White, fontSize = if (compact) 9.sp else 10.sp, fontWeight = FontWeight.Black),
                maxLines = 1,
            )
        }
        Spacer(Modifier.height(if (compact) 3.dp else 4.dp))
        Box(Modifier.fillMaxWidth().padding(start = if (compact) 20.dp else 22.dp).height(if (compact) 5.dp else 6.dp).clip(RoundedCornerShape(50)).background(Color.White.withAlpha(.10f))) {
            Box(
                Modifier
                    .fillMaxWidth(line.progress.coerceIn(0f, 1f))
                    .height(if (compact) 5.dp else 6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Brush.horizontalGradient(listOf(line.accent.withAlpha(.65f), line.accent))),
            )
        }
    }
}

/* ------------------------------------------------------------------ *
 * Service grid — 3×3 value cards + a compact row of four
 * ------------------------------------------------------------------ */

/* ------------------------------------------------------------------ *
 * Stat trio strip — three live readings in one capsule (sec5)
 * ------------------------------------------------------------------ */

@Composable
private fun HubRefStatTrioRow(trio: List<HubRefStatTrio>, dense: Boolean, navigate: (String) -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(Color(0xFF170C38), Color(0xFF1D1145), Color(0xFF170C38))))
            .border(1.2.dp, Brush.linearGradient(listOf(V2Cyan.withAlpha(.7f), V2Violet.withAlpha(.6f), V2Pink.withAlpha(.7f))), shape)
            .padding(horizontal = 6.dp, vertical = if (dense) 6.dp else 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        trio.reversed().forEachIndexed { i, cell ->
            if (i > 0) {
                Box(Modifier.width(1.dp).height(44.dp).background(Brush.verticalGradient(listOf(Color.Transparent, Color.White.withAlpha(.25f), Color.Transparent))))
            }
            Column(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(role = Role.Button) { navigate(cell.route) }
                    .semantics { contentDescription = "${cell.label}، ${cell.value}" }
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (cell.art != null) {
                    Image(painter = painterResource(cell.art), contentDescription = null, modifier = Modifier.size(22.dp), contentScale = ContentScale.Fit)
                } else {
                    Icon(cell.icon, null, tint = cell.accent, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    cell.label,
                    style = TextStyle(color = Color.White.withAlpha(.85f), fontSize = 8.5.sp, fontWeight = FontWeight.Bold, textDirection = TextDirection.Rtl),
                    maxLines = if (cell.art != null) 2 else 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    lineHeight = 11.sp,
                )
                Text(
                    cell.value,
                    style = TextStyle(color = if (cell.art != null) RefGold else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black, textDirection = TextDirection.Content),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (cell.sub.isNotEmpty()) {
                    Text(
                        cell.sub,
                        style = TextStyle(color = Color.White.withAlpha(.6f), fontSize = 7.5.sp, fontWeight = FontWeight.Medium, textDirection = TextDirection.Rtl),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun HubRefGrid(cards: List<HubRefCard>, navigate: (String) -> Unit, dense: Boolean = false, compactGrid: Boolean = false) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(if (dense) 6.dp else 8.dp)) {
        if (compactGrid) {
            // sec3: every card is a compact icon+title card, four per row.
            cards.chunked(4).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    row.reversed().forEach { card -> HubRefCompactCard(card, Modifier.weight(1f)) { navigate(card.route) } }
                }
            }
            return@Column
        }
        // Consecutive runs keep the approved row structure (e.g. sec5 3+4+3):
        // full value cards chunk in threes, compact cards render as their own row.
        var i = 0
        while (i < cards.size) {
            if (cards[i].compact) {
                val run = cards.drop(i).takeWhile { it.compact }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    // Listed right→left as the art reads; the stage is LTR.
                    run.reversed().forEach { card -> HubRefCompactCard(card, Modifier.weight(1f)) { navigate(card.route) } }
                }
                i += run.size
            } else {
                val run = cards.drop(i).takeWhile { !it.compact }
                run.chunked(3).forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(if (dense) 6.dp else 8.dp)) {
                        row.reversed().forEach { card -> HubRefValueCard(card, Modifier.weight(1f), dense) { navigate(card.route) } }
                    }
                }
                i += run.size
            }
        }
    }
}

@Composable
private fun HubRefValueCard(card: HubRefCard, modifier: Modifier = Modifier, dense: Boolean = false, onClick: () -> Unit) {
    val shape = RoundedCornerShape(if (dense) 16.dp else 18.dp)
    Column(
        modifier
            .clip(shape)
            .background(Brush.verticalGradient(listOf(Color(0xFF22124C), Color(0xFF170B38))))
            .border(1.2.dp, Brush.linearGradient(listOf(card.accent.withAlpha(.85f), V2Violet.withAlpha(.55f), card.accent.withAlpha(.7f))), shape)
            .semantics { contentDescription = "${card.title}، ${card.value}" }
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 6.dp, vertical = if (dense) 6.dp else 9.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(painter = painterResource(card.art), contentDescription = null, modifier = Modifier.size(if (dense) 26.dp else 32.dp), contentScale = ContentScale.Fit)
        Spacer(Modifier.height(if (dense) 3.dp else 5.dp))
        Text(
            card.title,
            style = TextStyle(color = Color.White, fontSize = if (dense) 9.5.sp else 10.5.sp, fontWeight = FontWeight.Black, textDirection = TextDirection.Rtl),
            maxLines = 2,
            minLines = 2,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            lineHeight = if (dense) 12.sp else 13.sp,
        )
        Spacer(Modifier.height(if (dense) 4.dp else 6.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF0E0726))
                .border(1.dp, card.accent.withAlpha(.55f), RoundedCornerShape(50))
                .padding(horizontal = 8.dp, vertical = if (dense) 2.5.dp else 3.5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                card.value,
                style = TextStyle(color = Color.White, fontSize = if (dense) 9.sp else 9.5.sp, fontWeight = FontWeight.Bold),
                maxLines = 1,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Icon(Icons.Rounded.ChevronRight, null, tint = Color.White.withAlpha(.85f), modifier = Modifier.size(if (dense) 11.dp else 12.dp))
        }
    }
}

@Composable
private fun HubRefCompactCard(card: HubRefCard, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    val edge = if (card.gold) {
        Brush.linearGradient(listOf(RefGoldBright, RefGold, Color(0xFFB87A1E)))
    } else {
        Brush.linearGradient(listOf(card.accent.withAlpha(.85f), V2Violet.withAlpha(.5f), card.accent.withAlpha(.7f)))
    }
    Column(
        modifier
            .clip(shape)
            .background(Brush.verticalGradient(listOf(Color(0xFF22124C), Color(0xFF170B38))))
            .border(1.3.dp, edge, shape)
            .semantics { contentDescription = "${card.title}، ${card.value}" }
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(painter = painterResource(card.art), contentDescription = null, modifier = Modifier.size(28.dp), contentScale = ContentScale.Fit)
        Spacer(Modifier.height(4.dp))
        Text(
            card.title,
            style = TextStyle(
                color = Color.White,
                // 8.5sp lets «الخصوصية» fit one line inside the narrow card.
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                textDirection = TextDirection.Rtl,
                // Narrow compact cards: long single words («الخصوصية») compress
                // to fit their line instead of clipping mid-glyph.
                lineBreak = LineBreak.Simple,
            ),
            maxLines = card.titleLines,
            minLines = card.titleLines,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 11.5.sp,
            modifier = Modifier.fillMaxWidth(),
        )
        if (card.pill) {
            Spacer(Modifier.height(4.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF0E0726))
                    .border(1.dp, card.accent.withAlpha(.55f), RoundedCornerShape(50))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    card.value,
                    style = TextStyle(color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                )
                Icon(Icons.Rounded.ChevronRight, null, tint = Color.White.withAlpha(.85f), modifier = Modifier.size(10.dp))
            }
        }
    }
}

/* ------------------------------------------------------------------ *
 * Big action pill — cyan→violet, broom glyph at the right
 * ------------------------------------------------------------------ */

@Composable
private fun HubRefCta(label: String, art: Int? = null, dense: Boolean = false, onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    val motion = rememberMotionEnabled()
    val transition = rememberInfiniteTransition(label = "hubRefCta")
    val shine by transition.animateFloat(
        initialValue = -.3f,
        targetValue = if (motion) 1.3f else .5f,
        animationSpec = infiniteRepeatable(tween(3600, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "ctaShine",
    )
    Box(
        Modifier
            .fillMaxWidth()
            .height(if (dense) 46.dp else 50.dp)
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(Color(0xFF0FB6E8), Color(0xFF3F6CF0), Color(0xFF8A2BE2), Color(0xFFB01FD8))))
            .border(1.4.dp, Brush.horizontalGradient(listOf(V2Cyan.withAlpha(.9f), Color.White.withAlpha(.5f), V2Pink.withAlpha(.9f))), shape)
            .semantics { contentDescription = label }
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            // Travelling specular sweep, frozen under reduced motion.
            val x = size.width * shine
            drawRect(
                Brush.horizontalGradient(listOf(Color.Transparent, Color.White.withAlpha(.22f), Color.Transparent), startX = x - 60.dp.toPx(), endX = x + 60.dp.toPx()),
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                label,
                style = TextStyle(color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black, textDirection = TextDirection.Rtl),
                maxLines = 1,
            )
            Spacer(Modifier.width(10.dp))
            if (art != null) {
                Image(painter = painterResource(art), contentDescription = null, modifier = Modifier.size(26.dp), contentScale = ContentScale.Fit)
            } else {
                HubRefBroomGlyph(Modifier.size(24.dp))
            }
        }
    }
}

/** Small white broom + sparkles, echoing the approved CTA glyph. */
@Composable
private fun HubRefBroomGlyph(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val white = Color.White
        // Handle.
        val handle = Path().apply {
            moveTo(w * .78f, h * .06f)
            lineTo(w * .52f, h * .42f)
        }
        drawPath(handle, white, style = Stroke(2.4.dp.toPx(), cap = StrokeCap.Round))
        // Broom head (trapezoid bristles).
        val head = Path().apply {
            moveTo(w * .40f, h * .38f)
            lineTo(w * .64f, h * .50f)
            lineTo(w * .52f, h * .92f)
            lineTo(w * .16f, h * .74f)
            close()
        }
        drawPath(head, white.withAlpha(.95f))
        val bristle = Path().apply {
            moveTo(w * .30f, h * .62f); lineTo(w * .26f, h * .80f)
            moveTo(w * .40f, h * .67f); lineTo(w * .37f, h * .86f)
        }
        drawPath(bristle, Color(0xFF8A2BE2).withAlpha(.8f), style = Stroke(1.1.dp.toPx(), cap = StrokeCap.Round))
        // Sparkles.
        fun spark(cx: Float, cy: Float, r: Float) {
            val p = Path().apply {
                moveTo(cx, cy - r); lineTo(cx, cy + r)
                moveTo(cx - r, cy); lineTo(cx + r, cy)
            }
            drawPath(p, white, style = Stroke(1.2.dp.toPx(), cap = StrokeCap.Round))
        }
        spark(w * .88f, h * .30f, w * .07f)
        spark(w * .12f, h * .22f, w * .05f)
    }
}

/* ------------------------------------------------------------------ *
 * Wide action row — icon LEFT, two-line title RIGHT, gold chevron
 * ------------------------------------------------------------------ */

@Composable
private fun HubRefWideRow(card: HubRefWideCard, dense: Boolean = false, onClick: () -> Unit) {
    val shape = RoundedCornerShape(if (dense) 18.dp else 20.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .height(if (dense) 48.dp else 58.dp)
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(Color(0xFF1B0F45), Color(0xFF221258), Color(0xFF1B0F45))))
            .border(1.3.dp, Brush.linearGradient(listOf(V2Cyan.withAlpha(.8f), V2Violet.withAlpha(.7f), V2Pink.withAlpha(.8f))), shape)
            .semantics { contentDescription = card.title }
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(painter = painterResource(card.art), contentDescription = null, modifier = Modifier.size(if (dense) 28.dp else 34.dp), contentScale = ContentScale.Fit)
        Spacer(Modifier.width(12.dp))
        Text(
            card.title,
            style = TextStyle(color = Color.White, fontSize = if (dense) 11.sp else 11.5.sp, fontWeight = FontWeight.Black, textDirection = TextDirection.Rtl),
            maxLines = 2,
            lineHeight = if (dense) 14.sp else 15.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start,
        )
        Icon(Icons.Rounded.ChevronRight, null, tint = Color.White.withAlpha(.9f), modifier = Modifier.size(18.dp))
    }
}

/* ------------------------------------------------------------------ *
 * Centered wide card — sec7 lock & biometric card, ~60% width, icon top
 * ------------------------------------------------------------------ */

@Composable
private fun HubRefWideCentered(card: HubRefWideCard, dense: Boolean = false, onClick: () -> Unit) {
    val shape = RoundedCornerShape(if (dense) 18.dp else 20.dp)
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
    Column(
        Modifier
            .fillMaxWidth(.62f)
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(Color(0xFF1B0F45), Color(0xFF221258), Color(0xFF1B0F45))))
            .border(1.3.dp, Brush.linearGradient(listOf(V2Cyan.withAlpha(.8f), V2Violet.withAlpha(.7f), V2Pink.withAlpha(.8f))), shape)
            .semantics { contentDescription = card.title }
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(painter = painterResource(card.art), contentDescription = null, modifier = Modifier.size(if (dense) 26.dp else 32.dp), contentScale = ContentScale.Fit)
        Spacer(Modifier.height(3.dp))
        Text(
            card.title,
            style = TextStyle(color = Color.White, fontSize = if (dense) 10.5.sp else 11.5.sp, fontWeight = FontWeight.Black, textDirection = TextDirection.Rtl),
            maxLines = 1,
        )
        Spacer(Modifier.height(5.dp))
        Box(
            Modifier.fillMaxWidth().height(if (dense) 16.dp else 20.dp).clip(RoundedCornerShape(50))
                .background(Color.White.withAlpha(.06f))
                .border(1.dp, V2Violet.withAlpha(.5f), RoundedCornerShape(50)),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Icon(Icons.Rounded.ChevronRight, null, tint = Color.White.withAlpha(.9f), modifier = Modifier.padding(end = 4.dp).size(12.dp))
        }
    }
    }
}

/* ------------------------------------------------------------------ *
 * Wide action pair — two half-width cards side by side (sec3)
 * ------------------------------------------------------------------ */

@Composable
private fun HubRefWidePairRow(cards: List<HubRefWideCard>, dense: Boolean, navigate: (String) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        // Authored right→left; the LTR stage reverses so the gold card lands LEFT.
        cards.reversed().forEach { card ->
            val shape = RoundedCornerShape(if (dense) 18.dp else 20.dp)
            val edge = if (card.gold) {
                Brush.linearGradient(listOf(RefGoldBright, RefGold, Color(0xFFB87A1E)))
            } else {
                Brush.linearGradient(listOf(V2Cyan.withAlpha(.8f), V2Violet.withAlpha(.7f), V2Pink.withAlpha(.8f)))
            }
            Row(
                Modifier
                    .weight(1f)
                    .height(if (dense) 52.dp else 60.dp)
                    .clip(shape)
                    .background(Brush.horizontalGradient(listOf(Color(0xFF1B0F45), Color(0xFF221258), Color(0xFF1B0F45))))
                    .border(1.3.dp, edge, shape)
                    .semantics { contentDescription = card.title }
                    .clickable(role = Role.Button, onClick = { navigate(card.route) })
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(painter = painterResource(card.art), contentDescription = null, modifier = Modifier.size(if (dense) 26.dp else 30.dp), contentScale = ContentScale.Fit)
                Spacer(Modifier.width(8.dp))
                Text(
                    card.title,
                    style = TextStyle(color = Color.White, fontSize = if (dense) 10.sp else 11.sp, fontWeight = FontWeight.Black, textDirection = TextDirection.Rtl),
                    maxLines = 2,
                    lineHeight = if (dense) 13.sp else 14.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                )
                Icon(Icons.Rounded.ChevronRight, null, tint = if (card.gold) RefGoldBright else Color.White.withAlpha(.9f), modifier = Modifier.size(16.dp))
            }
        }
    }
}

/* ------------------------------------------------------------------ *
 * PRO banner — stag LEFT, gold italic title RIGHT, gold chevron
 * ------------------------------------------------------------------ */

@Composable
private fun HubRefProBanner(title: String, subtitle: String, stagArt: Int, dense: Boolean = false, onPro: () -> Unit) {
    val shape = RoundedCornerShape(if (dense) 22.dp else 24.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .height(if (dense) 64.dp else 74.dp)
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(Color(0xFF2A10AB), Color(0xFF171060), Color(0xFF1D1170))))
            .border(1.3.dp, Brush.linearGradient(listOf(V2Violet.withAlpha(.9f), RefGold.withAlpha(.7f), V2Cyan.withAlpha(.7f))), shape)
            .semantics { contentDescription = "مزايا KNOUX AI Pro" }
            .clickable(role = Role.Button, onClickLabel = "مزايا KNOUX AI Pro", onClick = onPro),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val ribbon = Path().apply {
                moveTo(-8f, size.height * .8f)
                cubicTo(size.width * .16f, size.height * .1f, size.width * .4f, size.height * .95f, size.width * .66f, size.height * .35f)
                cubicTo(size.width * .86f, size.height * .05f, size.width, size.height * .5f, size.width + 8f, size.height * .25f)
            }
            drawPath(ribbon, Brush.horizontalGradient(listOf(Color.Transparent, V2Pink.withAlpha(.5f), V2Cyan.withAlpha(.35f), Color.Transparent)), style = Stroke(1.4.dp.toPx(), cap = StrokeCap.Round))
            drawCircle(Brush.radialGradient(listOf(Color(0xFF3510EB).withAlpha(.5f), Color.Transparent), center = Offset(size.width * .10f, size.height * .5f), radius = size.width * .28f), radius = size.width * .28f, center = Offset(size.width * .10f, size.height * .5f))
        }
        Image(
            painter = painterResource(stagArt),
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 10.dp).size(width = if (dense) 54.dp else 62.dp, height = if (dense) 60.dp else 70.dp),
            contentScale = ContentScale.Fit,
        )
        Box(Modifier.align(Alignment.CenterStart).padding(start = 78.dp).width(1.dp).height(40.dp).background(Brush.verticalGradient(listOf(Color.Transparent, Color.White.withAlpha(.35f), Color.Transparent))))
        Column(
            Modifier.align(Alignment.Center).padding(start = 84.dp, end = 44.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                title,
                style = TextStyle(
                    brush = Brush.verticalGradient(listOf(Color.White, Color(0xFFE6E0F5), RefGold)),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                ),
                maxLines = 1,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                subtitle,
                style = TextStyle(color = Color.White.withAlpha(.85f), fontSize = 9.sp, fontWeight = FontWeight.Medium, textDirection = TextDirection.Rtl),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            Modifier.align(Alignment.CenterEnd).padding(end = 10.dp).size(28.dp).clip(CircleShape)
                .border(1.4.dp, RefGold, CircleShape)
                .background(Color(0xFF241503).withAlpha(.6f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.ChevronRight, null, tint = RefGoldBright, modifier = Modifier.size(16.dp))
        }
    }
}
