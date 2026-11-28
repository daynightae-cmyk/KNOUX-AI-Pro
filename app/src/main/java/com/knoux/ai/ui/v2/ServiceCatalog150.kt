package com.knoux.ai.ui.v2

/**
 * Single source of truth for the 150-service catalog (100 CORE + 50 PRO).
 *
 * Every entry maps to a route that really exists in the navigation graph, or
 * to `null` when Android does not allow the capability (or it is not built
 * yet). Nothing here is marked as "verified": runtime verification lives in
 * the Room journal and is read back from there.
 */
enum class CatalogKind { CORE, PRO }

data class CatalogItem(
    val id: String,
    val family: String,
    val kind: CatalogKind,
    val ordinal: Int,
    val titleAr: String,
    val route: String?,
    /** Why a capability cannot be bound to a screen, when [route] is null. */
    val blockReason: String? = null,
) {
    val familyLabelAr: String get() = serviceLabel(family)
    val isRouted: Boolean get() = route != null
}

object ServiceCatalog150 {

    /** 10 families in catalog order. */
    val families: List<String> = listOf(
        "storage_analyzer",
        "duplicate_finder",
        "app_residue",
        "privacy_sweep",
        "app_manager",
        "battery_saver",
        "deep_scan",
        "smart_clean",
        "reports",
        "ai_optimization",
    )

    val total: Int get() = items.size
    val coreCount: Int get() = items.count { it.kind == CatalogKind.CORE }
    val proCount: Int get() = items.count { it.kind == CatalogKind.PRO }
    val routedCount: Int get() = items.count { it.isRouted }
    val blockedCount: Int get() = items.count { !it.isRouted }

    fun byFamily(family: String): List<CatalogItem> = items.filter { it.family == family }

    fun byId(id: String): CatalogItem? = items.firstOrNull { it.id == id }

    fun proItems(family: String): List<CatalogItem> =
        items.filter { it.family == family && it.kind == CatalogKind.PRO }

    fun coverageFor(family: String): Pair<Int, Int> {
        val rows = byFamily(family)
        return rows.count { it.isRouted } to rows.size
    }

    // ---------------------------------------------------------------- M01
    private val storageAnalyzer = listOf(
        core("STORAGE_ANALYZER_C01", "storage_analyzer", 1, "اختيار وحدة التخزين أو مجلد مصرح به", "sto-volumes"),
        core("STORAGE_ANALYZER_C02", "storage_analyzer", 2, "قراءة المساحة الكلية والمتاحة من النظام", "sto-used-free"),
        core("STORAGE_ANALYZER_C03", "storage_analyzer", 3, "حصر الملفات المرئية فقط ضمن الإذن", "sto-categories"),
        core("STORAGE_ANALYZER_C04", "storage_analyzer", 4, "تصنيف وسائط الملفات حسب النوع ومصدرها", "sto-categories"),
        core("STORAGE_ANALYZER_C05", "storage_analyzer", 5, "استعراض الملفات الكبيرة وفرزها", "sto-sorting"),
        core("STORAGE_ANALYZER_C06", "storage_analyzer", 6, "البحث والتصفية بالحجم والتاريخ والنوع", "sto-search"),
        core("STORAGE_ANALYZER_C07", "storage_analyzer", 7, "فتح ومشاركة ملف مرئي عبر provider", "sto-open-share"),
        core("STORAGE_ANALYZER_C08", "storage_analyzer", 8, "اختيار ونسخ/نقل/إعادة تسمية عند دعم provider", "sto-move-delete"),
        core("STORAGE_ANALYZER_C09", "storage_analyzer", 9, "مراجعة وحذف إلى السلة أو دائمًا بموافقة صريحة", "sto-move-delete"),
        core("STORAGE_ANALYZER_C10", "storage_analyzer", 10, "إعادة القياس وإنشاء سجل تاريخ قابل للتصدير", "sto-details"),
        pro("sto_detailed_breakdown", "storage_analyzer", 1, "تفصيل التخزين الكامل", "storage-breakdown"),
        pro("sto_large_files", "storage_analyzer", 2, "الملفات الكبيرة للهاتف", "large-files"),
        pro("sto_growth_trends", "storage_analyzer", 3, "اتجاه نمو التخزين", "rpt-trends"),
        pro("sto_capacity_planner", "storage_analyzer", 4, "مخطط السعة", "storage-breakdown"),
        pro("sto_export", "storage_analyzer", 5, "تصدير تقرير التخزين", null, "التصدير كملف لسه غير مبني"),
    )

    // ---------------------------------------------------------------- M02
    private val duplicateFinder = listOf(
        core("DUPLICATE_FINDER_C01", "duplicate_finder", 1, "اختيار مجموعات المصادر والمجلدات", "dup-scope"),
        core("DUPLICATE_FINDER_C02", "duplicate_finder", 2, "مسح حجم الملفات قبل التجزئة", "dup-types"),
        core("DUPLICATE_FINDER_C03", "duplicate_finder", 3, "حساب SHA-256 بشكل streaming للمتاح", "dup-scan"),
        core("DUPLICATE_FINDER_C04", "duplicate_finder", 4, "تجميع النسخ المطابقة بالبايتات", "dup-groups"),
        core("DUPLICATE_FINDER_C05", "duplicate_finder", 5, "تمييز التشابه البصري عن التطابق التام", "dup-compare"),
        core("DUPLICATE_FINDER_C06", "duplicate_finder", 6, "عرض معاينة وصورة/ملف لكل نسخة", "dup-compare"),
        core("DUPLICATE_FINDER_C07", "duplicate_finder", 7, "اختيار Keeper محمي لكل مجموعة", "dup-keeper"),
        core("DUPLICATE_FINDER_C08", "duplicate_finder", 8, "اختيار جماعي باستثناء Keeper والـaliases", "dup-exclusions"),
        core("DUPLICATE_FINDER_C09", "duplicate_finder", 9, "مراجعة Trash/Delete وموافقة Android لكل مسار", "dup-confirm"),
        core("DUPLICATE_FINDER_C10", "duplicate_finder", 10, "التحقق من بقاء Keeper وسجل الاستعادة والنتائج", "dup-review"),
        pro("dup_verified_hashing", "duplicate_finder", 1, "تجزئة موثقة", "dup-scan"),
        pro("dup_keeper_protection", "duplicate_finder", 2, "حماية Keeper المتقدمة", "dup-keeper"),
        pro("dup_smart_similarity", "duplicate_finder", 3, "تشابه ذكي", "dup-compare"),
        pro("dup_batch_review", "duplicate_finder", 4, "مراجعة دفعية", "dup-review"),
        pro("dup_recovery_journal", "duplicate_finder", 5, "سجل الاستعادة", "dup-confirm"),
    )

    // ---------------------------------------------------------------- M03
    private val appResidue = listOf(
        core("APP_RESIDUE_C01", "app_residue", 1, "اختيار نطاق كاش التطبيق نفسه أو مجلدات مصرح بها", "res-sources"),
        core("APP_RESIDUE_C02", "app_residue", 2, "فحص شجرة المجلدات المسموح بها", "res-scan"),
        core("APP_RESIDUE_C03", "app_residue", 3, "إظهار المصدر ونسبة الثقة في نسبة البقايا", "res-candidates"),
        core("APP_RESIDUE_C04", "app_residue", 4, "فصل الملفات المؤكدة عن غير المؤكدة", "res-groups"),
        core("APP_RESIDUE_C05", "app_residue", 5, "تصفية حسب العمر والنوع والحجم", "res-detail"),
        core("APP_RESIDUE_C06", "app_residue", 6, "معاينة المجلدات والملفات والبصمات", "res-detail"),
        core("APP_RESIDUE_C07", "app_residue", 7, "حماية العناصر المستثناة واختيار جزئي", "res-exclusions"),
        core("APP_RESIDUE_C08", "app_residue", 8, "مراجعة التقدير والمخاطر قبل إزالة المحدد", "res-risk"),
        core("APP_RESIDUE_C09", "app_residue", 9, "تنفيذ حذف/Trash في النطاق المصرح به فقط", "res-confirm"),
        core("APP_RESIDUE_C10", "app_residue", 10, "إعادة فحص الحالة وحفظ سجل المحاولات والنتيجة", "res-history"),
        pro("residue_own_cache", "app_residue", 1, "كاش KNOUX نفسه", "res-sources"),
        pro("residue_accessible_candidates", "app_residue", 2, "مرشحات الوصول المصرح به", "res-candidates", "يعرض الملفات المرشحة من كاش KNOUX ووسائط MediaStore"),
        pro("residue_saf_profiles", "app_residue", 3, "ملفات SAF محفوظة", null, "إدارة ملفات SAF غير منفذة — يتطلب SAF picker و ScopedGrantEntity"),
        pro("residue_safe_trash", "app_residue", 4, "سلة آمنة", "res-confirm", "تحذيرات السلة معروضة؛ كاش KNOUX حذف دائم، MediaStore يستخدم سلة النظام"),
        pro("residue_history", "app_residue", 5, "تاريخ البقايا", "res-history", "سجل العمليات من Room مع إعادة فحص"),
    )

    // ---------------------------------------------------------------- M04
    private val privacySweep = listOf(
        core("PRIVACY_SWEEP_C01", "privacy_sweep", 1, "اختيار تطبيقات مرئية أو فئات صلاحيات للفحص", "prv-overview"),
        core("PRIVACY_SWEEP_C02", "privacy_sweep", 2, "عرض بيانات صلاحيات التطبيقات الفعلية", "prv-permissions"),
        core("PRIVACY_SWEEP_C03", "privacy_sweep", 3, "تقييم مخاطر مبني على أدلة قابلة للتفسير", "prv-ranking"),
        core("PRIVACY_SWEEP_C04", "privacy_sweep", 4, "إظهار حالات عدم الظهور أو نقص الإذن", "prv-overview"),
        core("PRIVACY_SWEEP_C05", "privacy_sweep", 5, "عرض تفاصيل كل خطر ومصدره", "prv-app-risk"),
        core("PRIVACY_SWEEP_C06", "privacy_sweep", 6, "فرز حسب فئة التطبيق أو نوع الصلاحية", "prv-modules"),
        core("PRIVACY_SWEEP_C07", "privacy_sweep", 7, "فتح إعدادات الخصوصية والتطبيق الأصلية", "prv-settings"),
        core("PRIVACY_SWEEP_C08", "privacy_sweep", 8, "التحقق من تغيّر الإذن بعد العودة من النظام", "prv-results"),
        core("PRIVACY_SWEEP_C09", "privacy_sweep", 9, "حفظ سجل ملاحظات الخصوصية فقط", "prv-access"),
        core("PRIVACY_SWEEP_C10", "privacy_sweep", 10, "تصدير تقرير منزوع البيانات الحساسة بموافقة المستخدم", "prv-results"),
        pro("prv_realtime", "privacy_sweep", 1, "مراقبة لحظية", null, "مراقبة لحظية للكاميرا/الميكروفون تحتاج أذونات نظام خاصة — غير متاحة لتطبيق عادي؛ البديل فتح إعدادات الأذونات"),
        pro("prv_vault", "privacy_sweep", 2, "خزنة الصلاحيات", null, "خزنة مشفرة غير منفذة بعد — await M04 build"),
        pro("prv_tracker", "privacy_sweep", 3, "تتبع التطبيقات", null, "تتبع التطبيقات يحتاج VpnService أو وصول نظام — غير مدعوم لتطبيق عادي؛ البديل عرض الصلاحيات المعلنة"),
        pro("prv_report", "privacy_sweep", 4, "تقرير خصوصية", null, "شاشة تقرير الخصوصية لسه مربوطة ببيانات حقيقية — await M04 build"),
        pro("prv_history", "privacy_sweep", 5, "تاريخ الخصوصية", null, "سجل الخصوصية لسه غير مبني — await M04 build"),
    )

    // ---------------------------------------------------------------- M05
    private val appManager = listOf(
        core("APP_MANAGER_C01", "app_manager", 1, "اكتشاف التطبيقات المرئية حسب سياسة Android", "apm-installed"),
        core("APP_MANAGER_C02", "app_manager", 2, "عرض اسم التطبيق والحزمة والإصدار", "apm-info"),
        core("APP_MANAGER_C03", "app_manager", 3, "البحث والفرز والمجموعات", "apm-search"),
        core("APP_MANAGER_C04", "app_manager", 4, "تصفية المستخدم والنظام ضمن نطاق الرؤية", "apm-system-user"),
        core("APP_MANAGER_C05", "app_manager", 5, "عرض البيانات المسموحة للتخزين والاستخدام", "apm-filters"),
        core("APP_MANAGER_C06", "app_manager", 6, "فتح التطبيق أو صفحة معلوماته", "apm-launch"),
        core("APP_MANAGER_C07", "app_manager", 7, "فتح إدارة الصلاحيات والإشعارات للتطبيق", "apm-settings"),
        core("APP_MANAGER_C08", "app_manager", 8, "طلب إلغاء التثبيت عبر واجهة النظام لا صامتًا", "apm-uninstall"),
        core("APP_MANAGER_C09", "app_manager", 9, "مراجعة نتائج عمليات المستخدم وإعادة استعلام الحزمة", "apm-uninstall"),
        core("APP_MANAGER_C10", "app_manager", 10, "تاريخ استخدام الإجراءات وتوضيح المجريات غير المدعومة", "apm-sorting"),
        pro("app_batch", "app_manager", 1, "إجراءات دفعة", null, "معالجة الدفعات لسه غير مبنية — await M05 build"),
        pro("app_freeze", "app_manager", 2, "تجميد التطبيقات", null, "التجميد يحتاج Device Owner أو Root — غير متاح لتطبيق عادي"),
        pro("app_backup", "app_manager", 3, "نسخ احتياطي للتطبيق", null, "نسخ احتياطي لبيانات التطبيقات الأخرى لسه غير مدعوم — await M05 build"),
        pro("app_usage", "app_manager", 4, "استخدام التطبيقات", null, "وصول UsageStats يحتاج إذن خاص — await M05 build"),
        pro("app_clone", "app_manager", 5, "استنساخ التطبيقات", null, "استنساخ التطبيقات مش مدعوم رسميًا في Android"),
    )

    // ---------------------------------------------------------------- M06
    private val batterySaver = listOf(
        core("BATTERY_SAVER_C01", "battery_saver", 1, "قراءة مستوى البطارية الفعلي وطريقة القياس", "bat-level"),
        core("BATTERY_SAVER_C02", "battery_saver", 2, "قراءة الشحن والمصدر/الحالة", "bat-charge"),
        core("BATTERY_SAVER_C03", "battery_saver", 3, "قراءة حرارة البطارية عند إتاحتها", "bat-temperature"),
        core("BATTERY_SAVER_C04", "battery_saver", 4, "قراءة Thermal Status إن كان مدعومًا", "bat-source"),
        core("BATTERY_SAVER_C05", "battery_saver", 5, "إظهار نوع الإشارات الناقصة أو غير المتاحة", "bat-info"),
        core("BATTERY_SAVER_C06", "battery_saver", 6, "سجل عينات البطارية في Room بموافقة مناسبة", "bat-readings"),
        core("BATTERY_SAVER_C07", "battery_saver", 7, "تحديد حدود تنبيه يختارها المستخدم", "bat-alerts"),
        core("BATTERY_SAVER_C08", "battery_saver", 8, "فتح إعدادات توفير الطاقة الحقيقية", "bat-saving"),
        core("BATTERY_SAVER_C09", "battery_saver", 9, "إظهار توصيات مبنية على القراءات مع اختيار المستخدم", "bat-summary"),
        core("BATTERY_SAVER_C10", "battery_saver", 10, "مقارنة قياسات قبل/بعد الإعدادات دون ادعاء تسريع", "bat-summary"),
        pro("bat_thermal_timeline", "battery_saver", 1, "خط زمني للحرارة", null, "خط زمني حراري لسه غير مبني — await M06 build"),
        pro("bat_charge_profiles", "battery_saver", 2, "ملفات الشحن", null, "ملفات الشحن لسه غير مبنية — await M06 build"),
        pro("bat_usage_context", "battery_saver", 3, "سياق الاستخدام", null, "وصول UsageStats يحتاج إذن نظام خاص — await user grant + M06 build"),
        pro("bat_smart_alerts", "battery_saver", 4, "تنبيهات ذكية", null, "تنبيهات ذكية لسه غير مبنية — await M06 build"),
        pro("bat_recommendations", "battery_saver", 5, "توصيات البطارية", null, "توصيات البطارية لسه غير مبنية — await M06 build"),
    )

    // ---------------------------------------------------------------- M07
    private val deepScan = listOf(
        core("DEEP_SCAN_C01", "deep_scan", 1, "اختيار نطاق الفحص وأنواعه", "dsn-type"),
        core("DEEP_SCAN_C02", "deep_scan", 2, "تقييم الإذن والنطاق قبل التشغيل", "dsn-scope"),
        core("DEEP_SCAN_C03", "deep_scan", 3, "بدء فحص حقيقي متعدد الوحدات المسموحة", "dsn-start"),
        core("DEEP_SCAN_C04", "deep_scan", 4, "عرض تقدم بالملفات/البايتات المفحوصة", "dsn-progress"),
        core("DEEP_SCAN_C05", "deep_scan", 5, "إيقاف وإلغاء العمل في نقاط آمنة", "dsn-control"),
        core("DEEP_SCAN_C06", "deep_scan", 6, "إظهار نتائج جزئية ووقت أخذ العينات", "dsn-progress"),
        core("DEEP_SCAN_C07", "deep_scan", 7, "تصنيف النتائج حسب النوع والأولوية والدليل", "dsn-findings"),
        core("DEEP_SCAN_C08", "deep_scan", 8, "فتح تفاصيل النتيجة وتحديد العلاج اليدوي", "dsn-evidence"),
        core("DEEP_SCAN_C09", "deep_scan", 9, "تنفيذ علاج آخر خدمة موثوقة بمراجعة/موافقة", "dsn-findings"),
        core("DEEP_SCAN_C10", "deep_scan", 10, "إعادة استعلام وعرض ملخص النتائج والسجل", "dsn-summary"),
        pro("scan_malware", "deep_scan", 1, "فحص برمجيات خبيثة", null, "لا محرك توقيعات خبيث معتمد — غير متاح لتطبيق عادي؛ البديل الإحالة إلى إعدادات الأمان"),
        pro("scan_hidden", "deep_scan", 2, "ملفات مخفية", null, "اكتشاف الملفات المخفية لسه غير مكتمل — await M07 build"),
        pro("scan_schedule", "deep_scan", 3, "جدولة الفحص", null, "جدولة الفحص عبر WorkManager لسه غير موصولة بواجهة — await M07 build"),
        pro("scan_custom", "deep_scan", 4, "فحص مخصص", "dsn-scope"),
        pro("scan_forensic", "deep_scan", 5, "فحص جنائي", null, "فحص جنائي يحتاج حقوق نظام — غير متاح لتطبيق عادي؛ البديل عرض بيانات تعريف الملفات المقروءة"),
    )

    // ---------------------------------------------------------------- M08
    private val smartClean = listOf(
        core("SMART_CLEAN_C01", "smart_clean", 1, "اختيار نطاق تنظيف محدد", "clean-scope"),
        core("SMART_CLEAN_C02", "smart_clean", 2, "فحص ملفات قابلة للوصول والتنظيف فعلًا", "clean-scan"),
        core("SMART_CLEAN_C03", "smart_clean", 3, "تصنيف المرشحات حسب النوع", "clean-classify"),
        core("SMART_CLEAN_C04", "smart_clean", 4, "اختيار حدود الحجم والعمر", "clean-filters"),
        core("SMART_CLEAN_C05", "smart_clean", 5, "معاينة الملفات المرشحة", "clean-preview"),
        core("SMART_CLEAN_C06", "smart_clean", 6, "حماية الملفات والاستثناءات", "clean-protected"),
        core("SMART_CLEAN_C07", "smart_clean", 7, "التحديد الفردي والجماعي الآمن", "clean-selection"),
        core("SMART_CLEAN_C08", "smart_clean", 8, "مراجعة المساحة المحتملة ومخاطر الإجراء", "clean-impact"),
        core("SMART_CLEAN_C09", "smart_clean", 9, "تأكيد نقل للسلة أو حذف بحسب دعم Android", "clean-confirm"),
        core("SMART_CLEAN_C10", "smart_clean", 10, "إعادة استعلام فعلية وتسجيل النتائج والاستعادة الممكنة", "clean-history"),
        pro("smart_clean_unlimited", "smart_clean", 1, "تنظيف كبير", "clean-large"),
        pro("smart_clean_scheduled", "smart_clean", 2, "تنظيف مجدول", null, "التنظيف المجدول عبر WorkManager لسه غير موصول بواجهة — await M08 build"),
        pro("smart_clean_deep_analysis", "smart_clean", 3, "تحليل أعمق", "clean-classify"),
        pro("smart_clean_priority", "smart_clean", 4, "أولويات التنظيف", "clean-free-plan"),
        pro("smart_clean_rollback", "smart_clean", 5, "تراجع عن التنظيف", "clean-history"),
    )

    // ---------------------------------------------------------------- M09
    private val reports = listOf(
        core("REPORTS_C01", "reports", 1, "اختيار فترة أو نوع تقرير", "rpt-range"),
        core("REPORTS_C02", "reports", 2, "قراءة عمليات حقيقية من Room", "rpt-operation"),
        core("REPORTS_C03", "reports", 3, "عرض حالة الفراغ عند غياب السجلات", "rpt-empty"),
        core("REPORTS_C04", "reports", 4, "حساب إحصاءات حسب الإجراء والفئة", "rpt-cleanup"),
        core("REPORTS_C05", "reports", 5, "عرض مساحة مرشحة مقابل مساحة مؤكدة منفصلتين", "rpt-storage"),
        core("REPORTS_C06", "reports", 6, "إظهار فشل ورفض وجزئي وإلغاء العمليات", "rpt-operation"),
        core("REPORTS_C07", "reports", 7, "فلترة نتائج التقرير حسب المصدر", "rpt-device"),
        core("REPORTS_C08", "reports", 8, "مقارنة فترتين فعليتين من البيانات", "rpt-range"),
        core("REPORTS_C09", "reports", 9, "تصدير CSV/PDF محلي بإذن المستخدم", "rpt-operation"),
        core("REPORTS_C10", "reports", 10, "جدولة ملخصات مصرح بها وتاريخ التسليم", "rpt-trends"),
        pro("rep_export", "reports", 1, "تصدير التقارير", null, "تصدير الملفات لسه غير مبني"),
        pro("rep_history", "reports", 2, "تاريخ التقارير", null, "سجل التقارير لسه غير مبني — await M09 build"),
        pro("rep_ai_insights", "reports", 3, "رؤى الذكاء الاصطناعي", "ai-recommendations"),
        pro("rep_compare", "reports", 4, "مقارنة الفترات", "rpt-range"),
        pro("rep_schedule", "reports", 5, "جدولة التقارير", null, "الجدولة لسه غير موصولة بـ WorkManager"),
    )

    // ---------------------------------------------------------------- M10
    private val aiOptimization = listOf(
        core("AI_OPTIMIZATION_C01", "ai_optimization", 1, "جمع إشارات جهاز محددة ومصرح بها", "ai-storage"),
        core("AI_OPTIMIZATION_C02", "ai_optimization", 2, "عرض مصادر القياسات وتوقيتاتها", "ai-confidence"),
        core("AI_OPTIMIZATION_C03", "ai_optimization", 3, "توليد توصيات محلية قابلة للتفسير", "ai-recommendations"),
        core("AI_OPTIMIZATION_C04", "ai_optimization", 4, "إظهار حدود الدقة والثقة دون أرقام وهمية", "ai-confidence"),
        core("AI_OPTIMIZATION_C05", "ai_optimization", 5, "اختيار مجال تحسين البطارية أو التنبيه أو التخزين", "ai-goal"),
        core("AI_OPTIMIZATION_C06", "ai_optimization", 6, "ضبط أولوية توصيات المستخدم", "ai-customize"),
        core("AI_OPTIMIZATION_C07", "ai_optimization", 7, "معاينة أثر الإجراء والقيود قبل الموافقة", "ai-reasons"),
        core("AI_OPTIMIZATION_C08", "ai_optimization", 8, "فتح إعدادات Android أو عمليات KNOUX المسموحة", "ai-execute"),
        core("AI_OPTIMIZATION_C09", "ai_optimization", 9, "قياس النتيجة الواقعية بعد الإجراء إن أمكن", "ai-results"),
        core("AI_OPTIMIZATION_C10", "ai_optimization", 10, "تسجيل قرار المستخدم والنتيجة وملاحظات التراجع", "ai-battery"),
        pro("opt_gaming", "ai_optimization", 1, "وضع الألعاب", "ai-goal"),
        pro("opt_ai_learn", "ai_optimization", 2, "تعلّم محلي", "ai-recommendations"),
        pro("opt_thermal", "ai_optimization", 3, "إدارة حرارية", "ai-thermal"),
        pro("opt_network", "ai_optimization", 4, "تحسين الشبكة", "ai-confidence"),
        pro("opt_scene", "ai_optimization", 5, "مشاهد تلقائية", "ai-customize"),
    )

    // ------------------------------------------------------------ assembly
    val items: List<CatalogItem> = buildList {
        addAll(storageAnalyzer)
        addAll(duplicateFinder)
        addAll(appResidue)
        addAll(privacySweep)
        addAll(appManager)
        addAll(batterySaver)
        addAll(deepScan)
        addAll(smartClean)
        addAll(reports)
        addAll(aiOptimization)
    }

    // ------------------------------------------------------------ helpers
    private fun core(id: String, family: String, ordinal: Int, title: String, route: String?) =
        CatalogItem(id, family, CatalogKind.CORE, ordinal, title, route)

    private fun pro(
        id: String,
        family: String,
        ordinal: Int,
        title: String,
        route: String?,
        blockReason: String? = null,
    ) = CatalogItem(id, family, CatalogKind.PRO, ordinal, title, route, blockReason)
}
