# KNOUX AI — CANONICAL VISUAL IA / 10 SECTIONS × 10 SERVICE CARDS

## Scope and interpretation

**100 named cards means 100 navigable design entries, not 100 proven Android engines.** Each of the ten top-level service screens has ten visible cards, and each card must lead to its own appropriate **internal contextual screen or section state**. Reuse a shared component or actual backend later where sensible; do not duplicate code simply for the count. Every service has unique art, interaction affordances, visual state diagrams and back/return navigation. Ten separate section portraits are mandatory; internal screens are additional, each separately previewed.

This document provides the 100-card service catalog only; for the independent KNOUX-AI-Pro product use its OWN golden theme and repository, not the standard product source.

### Navigation

`Splash -> first-launch Welcome -> Ecosystem -> Founder -> Login -> applicable Auth branch -> Identity Success -> Home -> selected Service Section -> one of 10 subservice cards -> Detail / Review / Result / History`

Google / OTP / Passkey / Biometric are conditional/alternative, not a fixed sequence. Preserve user-visible back navigation and bottom four tabs.

## Per-section screens, 100 service destinations and 50 Pro placements

### 01 — HOME & SMART CLEAN / الرئيسية ومركز التنظيف الذكي

**Hero:** حجرة تحكم KNOUX مضيئة؛ حلقة ذكاء بنفسجي-سيان + ملخص تخزين أنيق؛ بدون رسومات ذهبية قبل التفعيل. **Primary CTA:** ابدأ الفحص الذكي.

| # | Card label (Arabic) | Design destination key | Internal visual experience |
|---:|---|---|---|
| 01 | الفحص الذكي | `smart_clean/smart-scan` | حدد نطاق الفحص ثم ابدأ مسحًا استكشافيًا |
| 02 | التنظيف الآمن | `smart_clean/safe-clean` | حدد العناصر والمستثنيات ثم شاشة المراجعة |
| 03 | الفحص العميق | `smart_clean/deep-scan` | افتح مركز أدوات الفحص العميق |
| 04 | الملفات المكررة | `smart_clean/duplicate-finder` | اعرض مجموعات التطابق والمقارنة |
| 05 | بقايا التطبيقات | `smart_clean/app-residue` | اعرض مصادر البقايا المتاحة |
| 06 | تحليل التخزين | `smart_clean/storage-analyzer` | مخطط فئات التخزين وتفاصيله |
| 07 | إدارة التطبيقات | `smart_clean/app-manager` | قائمة التطبيقات والفرز |
| 08 | فحص الخصوصية | `smart_clean/privacy-sweep` | خريطة الأذونات |
| 09 | مراقبة البطارية | `smart_clean/battery-insights` | أداة البطارية والحرارة |
| 10 | تحسين الذكاء الاصطناعي | `smart_clean/ai-optimization` | اقتراحات مبنية على الفحص |

**Five additional Pro features for this existing section (from exact source catalog):** `smart_clean_unlimited`, `smart_clean_scheduled`, `smart_clean_deep_analysis`, `smart_clean_priority`, `smart_clean_rollback`.

**Visual states:** Main category, selected tool detail, Loading/Not Scanned, Permission/Unsupported, Data list/Empty, Select & Review, Confirmation concept, Result, Error/Retry, History, Pro Locked/Active. Produce separate snapshots of distinct important states, not collages.

### 02 — DUPLICATE FINDER / الملفات المكررة

**Hero:** عدسة ثلاثية الأبعاد تعرض صورًا متناظرة وأيقونات أنواع الملفات، Keeper مضيء. **Primary CTA:** اكتشف الملفات المكررة.

| # | Card label (Arabic) | Design destination key | Internal visual experience |
|---:|---|---|---|
| 01 | الصور المكررة | `duplicate_finder/duplicate-photos` | مجموعات صور ومعاينات |
| 02 | الفيديوهات المكررة | `duplicate_finder/duplicate-videos` | مجموعات فيديو ومساراتها |
| 03 | الملفات الصوتية | `duplicate_finder/duplicate-audio` | أزواج صوت بمدة وحجم |
| 04 | المستندات المتكررة | `duplicate_finder/duplicate-documents` | أزواج مستندات قابلة للفحص |
| 05 | ملفات التنزيلات | `duplicate_finder/duplicate-downloads` | نطاق التنزيلات |
| 06 | المطابقة الدقيقة | `duplicate_finder/exact-match` | نتائج بصمة متطابقة |
| 07 | الصور المتشابهة | `duplicate_finder/similar-images` | تشابه بصري بدرجة ثقة |
| 08 | معاينة المجموعات | `duplicate_finder/group-preview` | مقارنة مفصلة جنبًا لجنب |
| 09 | حماية النسخة الأصلية | `duplicate_finder/keeper-protection` | اختيار Keeper وإظهار حمايته |
| 10 | المراجعة والاستعادة | `duplicate_finder/review-restore` | مراجعة وحالة سلة/استعادة عند الدعم |

**Five additional Pro features for this existing section (from exact source catalog):** `dup_verified_hashing`, `dup_keeper_protection`, `dup_smart_similarity`, `dup_batch_review`, `dup_recovery_journal`.

**Visual states:** Main category, selected tool detail, Loading/Not Scanned, Permission/Unsupported, Data list/Empty, Select & Review, Confirmation concept, Result, Error/Retry, History, Pro Locked/Active. Produce separate snapshots of distinct important states, not collages.

### 03 — APP RESIDUE / بقايا التطبيقات

**Hero:** حجرة تنقية زجاجية بحلقات نيون وأيقونات بقايا ملفات. **Primary CTA:** افحص البقايا المتاحة.

| # | Card label (Arabic) | Design destination key | Internal visual experience |
|---:|---|---|---|
| 01 | فحص البقايا | `app_residue/residue-scan` | اختيار نطاق البقايا |
| 02 | ذاكرة التطبيق المؤقتة | `app_residue/own-app-cache` | ذاكرة التطبيق نفسه فقط |
| 03 | مجلدات قابلة للوصول | `app_residue/accessible-folders` | عرض مجلدات مصرّح بها |
| 04 | ملفات التنزيل القديمة | `app_residue/old-downloads` | نطاق التنزيلات والعمر |
| 05 | ملفات APK المحفوظة | `app_residue/saved-apks` | APK محفوظ في نطاق مرئي |
| 06 | الملفات المؤقتة المحددة | `app_residue/scoped-temporaries` | عناصر مؤقتة مصرح بها |
| 07 | اختيار المجلدات | `app_residue/folder-picker` | SAF واختيار الملفات |
| 08 | العناصر المستثناة | `app_residue/excluded-items` | إدارة الاستثناءات |
| 09 | مراجعة التنظيف | `app_residue/residue-review` | سلة المراجعة والتأكيد |
| 10 | سجل العمليات | `app_residue/residue-history` | سجل العمليات والنتائج |

**Five additional Pro features for this existing section (from exact source catalog):** `residue_own_cache`, `residue_accessible_candidates`, `residue_saf_profiles`, `residue_safe_trash`, `residue_history`.

**Visual states:** Main category, selected tool detail, Loading/Not Scanned, Permission/Unsupported, Data list/Empty, Select & Review, Confirmation concept, Result, Error/Retry, History, Pro Locked/Active. Produce separate snapshots of distinct important states, not collages.

### 04 — PRIVACY SWEEP / الخصوصية

**Hero:** درع زجاجي نيون داخل مدار سيان ومؤشرات أذونات. **Primary CTA:** ابدأ مراجعة الخصوصية.

| # | Card label (Arabic) | Design destination key | Internal visual experience |
|---:|---|---|---|
| 01 | مراجعة الأذونات | `privacy_sweep/permission-review` | نظرة عامة للأذونات |
| 02 | أذونات الكاميرا | `privacy_sweep/camera-access` | تفاصيل إذن الكاميرا |
| 03 | أذونات الميكروفون | `privacy_sweep/microphone-access` | تفاصيل إذن الميكروفون |
| 04 | الوصول إلى الموقع | `privacy_sweep/location-access` | إذن الموقع |
| 05 | الصور والوسائط | `privacy_sweep/media-access` | نطاق الوسائط المحدد |
| 06 | أذونات الإشعارات | `privacy_sweep/notification-access` | أذونات الإشعار |
| 07 | مراجعة التطبيقات | `privacy_sweep/app-permission-list` | الأذونات حسب التطبيق |
| 08 | إعدادات الخصوصية | `privacy_sweep/android-privacy-settings` | إحالة لإعدادات النظام |
| 09 | سجل المراجعة | `privacy_sweep/privacy-history` | سجل مراجعات المستخدم |
| 10 | تقرير الخصوصية | `privacy_sweep/privacy-report` | تقرير قابل للتصدير لاحقًا |

**Five additional Pro features for this existing section (from exact source catalog):** `prv_realtime`, `prv_vault`, `prv_tracker`, `prv_report`, `prv_history`.

**Visual states:** Main category, selected tool detail, Loading/Not Scanned, Permission/Unsupported, Data list/Empty, Select & Review, Confirmation concept, Result, Error/Retry, History, Pro Locked/Active. Produce separate snapshots of distinct important states, not collages.

### 05 — DEEP SCAN / الفحص العميق

**Hero:** ماسح دائري ثلاثي الأبعاد يمر خلال طبقات ملفات ضوئية. **Primary CTA:** ابدأ الفحص العميق.

| # | Card label (Arabic) | Design destination key | Internal visual experience |
|---:|---|---|---|
| 01 | فحص الوسائط | `deep_scan/media-scan` | صور وفيديو وصوت ضمن الأذونات |
| 02 | فحص المستندات | `deep_scan/document-scan` | ملفات مصرّح بها |
| 03 | مجلد التنزيلات | `deep_scan/downloads-scan` | نطاق التنزيلات |
| 04 | الملفات المضغوطة | `deep_scan/archive-scan` | سرد الملفات المضغوطة |
| 05 | الملفات الكبيرة | `deep_scan/large-items-scan` | عرض العناصر الكبيرة |
| 06 | مجموعات التكرار | `deep_scan/duplicate-groups` | تحويل إلى مراجع المجموعات |
| 07 | مجلدات التطبيقات المتاحة | `deep_scan/accessible-app-folders` | نطاق التطبيقات المتاح فقط |
| 08 | نطاق فحص مخصص | `deep_scan/custom-scope` | اختيار مصدر المستخدم |
| 09 | نتائج الفحص | `deep_scan/scan-results` | نتائج مصنفة |
| 10 | سجل الفحوصات | `deep_scan/scan-history` | تاريخ الفحص |

**Five additional Pro features for this existing section (from exact source catalog):** `scan_malware`, `scan_hidden`, `scan_schedule`, `scan_custom`, `scan_forensic`.

**Visual states:** Main category, selected tool detail, Loading/Not Scanned, Permission/Unsupported, Data list/Empty, Select & Review, Confirmation concept, Result, Error/Retry, History, Pro Locked/Active. Produce separate snapshots of distinct important states, not collages.

### 06 — REPORTS & INSIGHTS / التقارير

**Hero:** مجسم إحصائيات زجاجي بحلقات ونقاط بيانات. **Primary CTA:** استعرض التقارير.

| # | Card label (Arabic) | Design destination key | Internal visual experience |
|---:|---|---|---|
| 01 | ملخص الجهاز | `reports/device-summary` | ملخص مدعوم بمصادر |
| 02 | استخدام التخزين | `reports/storage-usage` | رسم تخزين مع فترة |
| 03 | سجل التنظيف | `reports/clean-history` | أحداث تنظيف مسجلة |
| 04 | نتائج التكرارات | `reports/duplicate-reports` | مجموعات مراجعة |
| 05 | تقارير البقايا | `reports/residue-reports` | حوادث بقايا |
| 06 | مراجعات الخصوصية | `reports/privacy-reviews` | تاريخ مراجعات الأذونات |
| 07 | بيانات البطارية | `reports/battery-data` | ملاحظات البطارية |
| 08 | اتجاهات الاستخدام | `reports/usage-trends` | مقارنة فترات |
| 09 | مقارنة الفترات | `reports/period-comparison` | مقارنة رسمين |
| 10 | تصدير التقارير | `reports/report-export` | تنزيل/مشاركة بعد بناء فعلية |

**Five additional Pro features for this existing section (from exact source catalog):** `rep_export`, `rep_history`, `rep_ai_insights`, `rep_compare`, `rep_schedule`.

**Visual states:** Main category, selected tool detail, Loading/Not Scanned, Permission/Unsupported, Data list/Empty, Select & Review, Confirmation concept, Result, Error/Retry, History, Pro Locked/Active. Produce separate snapshots of distinct important states, not collages.

### 07 — APP MANAGER / إدارة التطبيقات

**Hero:** مكعبات تطبيقات ثلاثية الأبعاد تحيط بلوحة إدارة شفافة. **Primary CTA:** استعرض تطبيقاتك.

| # | Card label (Arabic) | Design destination key | Internal visual experience |
|---:|---|---|---|
| 01 | التطبيقات المثبتة | `app_manager/installed-apps` | قائمة التطبيقات المصرح بها |
| 02 | البحث عن تطبيق | `app_manager/app-search` | بحث ديناميكي |
| 03 | تصنيف التطبيقات | `app_manager/app-categories` | تصنيف حسب السمات |
| 04 | فرز التطبيقات | `app_manager/app-sort` | فرز الاسم والحجم |
| 05 | تفاصيل التطبيق | `app_manager/app-detail` | بيانات التطبيق |
| 06 | استخدام المساحة | `app_manager/app-storage` | حجم التخزين المرئي |
| 07 | مراجعة الأذونات | `app_manager/app-permissions` | أذونات تطبيق بعينه |
| 08 | معلومات الاستخدام | `app_manager/app-usage` | معلومات عند توافر UsageStats |
| 09 | إلغاء التثبيت الآمن | `app_manager/os-uninstall` | تأكيد نظام Android |
| 10 | تحديثات المتجر | `app_manager/store-updates` | تحويل لمتجر التطبيقات |

**Five additional Pro features for this existing section (from exact source catalog):** `app_batch`, `app_freeze`, `app_backup`, `app_usage`, `app_clone`.

**Visual states:** Main category, selected tool detail, Loading/Not Scanned, Permission/Unsupported, Data list/Empty, Select & Review, Confirmation concept, Result, Error/Retry, History, Pro Locked/Active. Produce separate snapshots of distinct important states, not collages.

### 08 — STORAGE ANALYZER / تحليل التخزين

**Hero:** أسطوانة تخزين شفافة متعددة الطبقات وحلقات أرقام. **Primary CTA:** حلّل مساحة التخزين.

| # | Card label (Arabic) | Design destination key | Internal visual experience |
|---:|---|---|---|
| 01 | نظرة عامة للتخزين | `storage_analyzer/storage-overview` | ملخص مساحة فعلية |
| 02 | الصور | `storage_analyzer/storage-photos` | فئة الصور |
| 03 | الفيديوهات | `storage_analyzer/storage-videos` | فئة الفيديوهات |
| 04 | الملفات الصوتية | `storage_analyzer/storage-audio` | فئة الصوت |
| 05 | المستندات | `storage_analyzer/storage-documents` | فئة المستندات |
| 06 | التنزيلات | `storage_analyzer/storage-downloads` | فئة التنزيلات |
| 07 | الملفات الكبيرة | `storage_analyzer/storage-large` | مستعرض أكبر العناصر |
| 08 | المجلدات المحددة | `storage_analyzer/selected-folders` | SAF اختيار النطاق |
| 09 | المساحة المتاحة | `storage_analyzer/free-capacity` | المساحة التي يقيسها النظام |
| 10 | مراجعة فئات التخزين | `storage_analyzer/storage-category-review` | تفاصيل وفلاتر الفئات |

**Five additional Pro features for this existing section (from exact source catalog):** `sto_detailed_breakdown`, `sto_large_files`, `sto_growth_trends`, `sto_capacity_planner`, `sto_export`.

**Visual states:** Main category, selected tool detail, Loading/Not Scanned, Permission/Unsupported, Data list/Empty, Select & Review, Confirmation concept, Result, Error/Retry, History, Pro Locked/Active. Produce separate snapshots of distinct important states, not collages.

### 09 — BATTERY SAVER / البطارية والطاقة

**Hero:** بطارية مضيئة عائمة مع تيارات سيان بنفسجية دقيقة. **Primary CTA:** افحص حالة البطارية.

| # | Card label (Arabic) | Design destination key | Internal visual experience |
|---:|---|---|---|
| 01 | مستوى البطارية | `battery_saver/battery-level` | قراءة النسبة من النظام |
| 02 | حالة الشحن | `battery_saver/charging-state` | نوع ومصدر الشحن |
| 03 | درجة الحرارة المتاحة | `battery_saver/battery-temperature` | حساس متاح فقط |
| 04 | اتجاهات الشحن | `battery_saver/charging-trend` | مشاهدات زمنية |
| 05 | معلومات استخدام الطاقة | `battery_saver/power-usage` | سياق استخدام الطاقة |
| 06 | إعدادات توفير الطاقة | `battery_saver/power-settings` | اختصار لنظام Android |
| 07 | إعدادات السطوع | `battery_saver/brightness-settings` | إحالة للإعدادات |
| 08 | مهلة إيقاف الشاشة | `battery_saver/screen-timeout` | إعدادات الشاشة |
| 09 | تنبيهات البطارية | `battery_saver/battery-alerts` | حدود التنبيه |
| 10 | توصيات الاستهلاك | `battery_saver/energy-tips` | توصيات شفافة |

**Five additional Pro features for this existing section (from exact source catalog):** `bat_thermal_timeline`, `bat_charge_profiles`, `bat_usage_context`, `bat_smart_alerts`, `bat_recommendations`.

**Visual states:** Main category, selected tool detail, Loading/Not Scanned, Permission/Unsupported, Data list/Empty, Select & Review, Confirmation concept, Result, Error/Retry, History, Pro Locked/Active. Produce separate snapshots of distinct important states, not collages.

### 10 — AI OPTIMIZATION / التحسين الذكي

**Hero:** دماغ شبكي K ثلاثي الأبعاد مع مسارات تفسير وأدلة. **Primary CTA:** حلّل حالة الجهاز.

| # | Card label (Arabic) | Design destination key | Internal visual experience |
|---:|---|---|---|
| 01 | تحليل نتائج الفحص | `ai_optimization/ai-scan-analysis` | قراءة أحدث نتائج فعلية |
| 02 | أولويات التنظيف | `ai_optimization/ai-clean-priority` | ترتيب أولويات بمبررات |
| 03 | توصيات الملفات المكررة | `ai_optimization/ai-duplicate-tips` | اقتراحات مراجعة |
| 04 | اقتراحات التخزين | `ai_optimization/ai-storage-tips` | مقترحات تخزين |
| 05 | توصيات الخصوصية | `ai_optimization/ai-privacy-tips` | شرح الأذونات |
| 06 | نصائح البطارية | `ai_optimization/ai-battery-tips` | مقترحات الطاقة |
| 07 | تحليل استخدام التطبيقات | `ai_optimization/ai-app-usage` | رؤى استخدام حسب المسموح |
| 08 | شرح التوصيات | `ai_optimization/ai-explanations` | مصدر كل توصية |
| 09 | مراجعة الإجراءات | `ai_optimization/ai-review` | تحديد ومراجعة المستخدم |
| 10 | سجل التوصيات | `ai_optimization/ai-history` | سجل ما ظهر للمستخدم |

**Five additional Pro features for this existing section (from exact source catalog):** `opt_gaming`, `opt_ai_learn`, `opt_thermal`, `opt_network`, `opt_scene`.

**Visual states:** Main category, selected tool detail, Loading/Not Scanned, Permission/Unsupported, Data list/Empty, Select & Review, Confirmation concept, Result, Error/Retry, History, Pro Locked/Active. Produce separate snapshots of distinct important states, not collages.

## Global design acceptance: every page and every nested card

1. **Reference mapping:** identify exact image(s) and owner-approved design for that screen. All 8 supplied entry/auth pictures are direct style authority; `design/owner-visual-sequence-20261116` is current standard gallery only as optional secondary ideas; Pro approved GOLDEN visuals take precedence. For Pro reference imagery use only look/feel when appropriate; never import Pro source code.
2. **Same visual family:** original KNOUX mark, obsidian–purple, flowing magenta/cyan neon, glass panels, unique luminous service instrument, clear short Arabic RTL. Pro-enabled theme adds gold coherently across the whole UI.
3. **Card-level navigation:** each card has distinct key, focus/selected/disabled state, correct back behavior and known destination. A card can use a shared stateful template but never land on a dead generic page.
4. **Real render gate:** show the actual device screenshot or rendered preview, compare to the corresponding reference, resolve any visual regression, and only then open the next screen. Do not claim from code inspection alone.
5. **No false claims:** static concept art or screenshot cannot prove feature engines, device data, entitlement, permissions, successful deletion, Google login, or Room history. Show examples/unknown states.
6. **Phase separation:** visual/UI-stage implementation only now. Build end-to-end Android engines and identity/cloud/Billing after the owner explicitly approves the entire visual library.

## Thematic blueprint

- **Standard:** coherent violet/pink/cyan throughout every nested service (tokenized).
- **Pro active:** same layout but champagne-gold premium transitions, crown, hero glow, borders/nav/CTA/pills/dialogs across **all** screens, activated only from a verified entitlement in the later real-functional stage.
- **Pro locked:** tasteful upgrade affordances without disabling Free tools or claiming active entitlement.
- **Platform restriction:** Visual concept must make real Android capability/permission boundaries apparent, not promise root-level freezing, arbitrary app data access, fake battery boosts or universal malware scanning.

## Quantity verification

- Top-level service screens: **10**.
- Distinct service cards: **100**.
- Pro catalog features: **50**.
- Every one of the 100 card keys appears once in the screen taxonomy.
