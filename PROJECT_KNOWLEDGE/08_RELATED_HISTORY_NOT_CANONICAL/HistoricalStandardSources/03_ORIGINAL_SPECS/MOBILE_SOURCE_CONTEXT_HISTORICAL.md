# KNOUX AI MOBILE — المصدر المرجعي الشامل لاستمرارية المشروع

**Project Knowledge / Continuity / Engineering Handoff — إصدار 13 نوفمبر 2026**  
**اللغة:** العربية (مع أسماء تقنية English)  
**الهدف:** ملف واحد يمكن رفعه إلى **مصادر مشروع ChatGPT / Project Sources** لكي يفهم أي مساعد أو وكيل تاريخ المنتج، نطاق التنفيذ، المراجع البصرية، القيود، الحالة الواقعية، وما يتوجب إثباته قبل إغلاق المهام.

> **مهم جدًا — مستوى الثقة:** هذا **ملخص موحد للقرارات والوثائق وتقارير سابقة**، وليس فحصًا مباشرًا بتاريخ اليوم للـGit أو الكمبيوتر أو Supabase أو التطبيق على هاتف حقيقي. قراءة ملفات المواصفات والمراسلات هنا **مثبتة**؛ أما تنفيذ الوكلاء، حالة HEAD الحالية، CI، وتفعيل مزود المصادقة وتشغيل APK فـ**غير مثبتة** ما لم يصاحبها سجل فعلي جديد. لا توجد في هذا الملف كلمات مرور أو Access Tokens أو Client Secrets.

## 1. الخلاصة التنفيذية والحالة الحالية

**KNOUX AI Mobile** منتج Android فعلي طموحه إدارة التخزين والملفات والخصوصية والتطبيقات والبطارية والمسح والتنظيف والتقارير وتوصيات الذكاء الاصطناعي في واجهة KNOUX أصلية سينمائية متماسكة. المطلوب ليس معرض صور ولا عدّادات وهمية؛ بل تطبيق **End-to-End** ينفذ العمليات المسموح بها من Android بعد موافقة المستخدم، يراجع النتائج بالفعل، ويسجل تاريخها.

**آخر توجيه للمالك:** مرحلة التصميم البصري على نسخة هاتف OpenCode **مُعلَن من المالك أنها انتهت**، والمطلوب الانتقال إلى التنفيذ الوظيفي الكامل. يجب **الحفاظ على كل شاشة وحركة وشعار وأصل وتصميم موافَق عليه**؛ عدم إعادة إنشاء المشروع أو تعديل مشروع Windows المستقل. وجود تصميم مقبول لا يعني أن جميع وظائفه تعمل.

**قرار الحالة التشغيلية:** لا يوجد دليل حالي كافٍ لاعتماد «كل المراحل COMPLETE»، ولا تحقق حالي من Google Sign-In أو Passkey أو Biometric أو مزامنة Supabase أو تسليم Release. انطلاق التنفيذ الصحيح: `REALITY → RISKS → UNIQUE WORK → GAPS → SAFE NEXT ACTION`.

## 2. تعريف المنتج وحدود المستودعات

| البند | القيمة/القاعدة | مستوى الإثبات |
|---|---|---|
| اسم المنتج | `KNOUX AI` — تطبيق Android | قرار صاحب المشروع |
| مستودع GitHub | `https://github.com/daynightae-cmyk/KNOUX-AI` | مرجع مسمّى؛ **SHA الحالي غير مفحوص** |
| **مساحة Mobile OpenCode المعتمدة** | `/root/workspace/KNOUX-AI` | أمر تنفيذي محدد، وليس إثبات وجود الملفات الآن |
| **نسخة الكمبيوتر المختلفة** | `D:\KNOUX-AI` | Checkout/مسار مستقل؛ **ممنوع** لمسُه أو نسخه أو مزامنته أو دمج عمله مع نسخة الهاتف بلا توجيه مستقل |
| Android application ID | `com.knoux.ai` | مذكور في فحص سابق لملف Gradle |
| Android SDK | `minSdk=26`, `targetSdk=36` | فحص سابق للمستودع، يجب إعادة التحقق من HEAD الحالي |
| مالك المنتج وهوية المؤسس | `Eng. Sadek Elgazar` — `Founder & Software Engineer` / `م. صادق الجزار` | هوية بصرية معتمدة |
| نطاق العلامة | `https://knoux.store/` | رابط مذكور في عقد الواجهة |

**القاعدة الحرجة:** لا تخلط بين مؤشرات PC00–PC13 الخاصة بالكمبيوتر وبين مراحل Android `M00–M15`. مشروع الهاتف له شجرة عمل وتاريخ Git وأصول وعمليات مختلفة؛ لا يجوز نقل الملفات أو `.git` أو SQL أو APK أو assets بين البيئتين. أي SHA أو Test PASS من نسخة Windows لا يثبت حالة Mobile.

## 3. الهوية البصرية والشاشات المقفولة ضد Regression

- الطابع المطلوب: **Deep Navy / Violet / Electric Blue / Cyan** مع Glassmorphism احترافي، لمسات Magenta/Gold محسوبة، إضاءة حقيقية وحركة سلسة؛ هوية KNOUX موحدة في كل الأقسام.
- شعار **K** الأصلي و`knoux ai` والـSplash والـSmile stroke والـorb وglow/orbits والـheader والـbottom navigation محمية. لا تستبدل مكونات حقيقية بصورة PNG ثابتة ولا تعيد رسم شعار وافق المالك عليه.
- **Login واحد فقط**، بلا صفحة تسجيل دخول مكررة أو Scroll غير ضروري. واجهة Full-screen متزنة وآمنة من تداخل الـheader؛ التمرير يكون فقط لمحتوى يحتاجه فعلًا.
- **ممنوع البصمة الضخمة وسط الشاشة.** استخدم Biometric كأيقونة صغيرة راقية بأسفل/بجوار طرق الدخول الأخرى، مع `Google` و`Passkey` بترتيب احترافي. لا تغيّر شاشة Login/OTP المعتمدة عند تنفيذ الخدمات.
- حواف وتباعد العناصر واضحة، 48dp touch targets، RTL/LTR، شاشات مختلفة، Safe Insets، Android 15/16 edge-to-edge، Font scaling، Accessibility، Back/Keyboard، وDark/Day فقط إذا كانا مدعومين أصلًا.
- شاشة المؤسس: English signature-style الاسم `Eng. Sadek Elgazar` مع الوصف المعتمد، لا تبديل ملامح/شعار/ألوان المراجع الأصلية.
- الـPro crown رمز صغير راقٍ وليس عنصرًا يطغى على المنتج؛ لا تدّعِ اشتراكًا مدفوعًا ما لم يتم التحقق من Entitlement حقيقي.

### 3.1 مسار الدخول والتعريف الأولي المعتمد

1. **Splash:** بناء حروف K/الاسم بحركة الأصل، انتهاء خط الابتسامة ثم تشغيل **الصوت المرخّص الذي قدمه المالك مرة واحدة فقط** عند **حدث انتهاء الحركة الحقيقي**، لا Timer تخميني؛ مع مراعاة Audio Focus وSilent Mode وعدم التكرار بسبب Recomposition.
2. **Awakening** (الموجودة أصلًا) ثم **Ecosystem**: عبارة `منظومة ذكية واحدة.` وعشر بطاقات خدمات تتحرك حول K، ثم **Founder**، ثم Identity/Login، وبعد مصادقة حقيقية ينتقل المستخدم لـHome. لا Onboarding إضافي مكرر؛ حفظ حالة first-run وskip/back/progress.
3. مرجعان أصليان إضافيان: `01_KNOUX_AI_MOBILE_ECOSYSTEM_APPROVED.png` و`02_KNOUX_AI_MOBILE_FOUNDER_APPROVED.png` بحجم 841×1870 لكل منهما. **وجودهما فعليًا داخل مشروع الهاتف يجب إثباته**؛ هذا الملخص لا يحوي الصورة نفسها.
4. رابط الصوت الذي اعتمده المالك: `https://mp3tourl.com/audio/1794520051232-3d804d0a-2f23-4c00-9879-af7542c6616f.mp3`. حق استخدامه **بحسب تصريح المالك**. صلاحية تحميل الـMP3/صيغته/Hash/سلامة التشغيل محليًا **غير مثبتة هنا**؛ لا تختلق مدة أو شهادة ترخيص، ولا تحذف صوتًا موجودًا ويعمل.

### 3.2 كتالوج الشاشات المرجعية الـ20 (مطابقة Route + Function)

كل PNG **مرجع تصميم مستقل** وليس بديلًا عن شاشة تفاعلية أو إثباتًا لوجودها في APK.

| ID | اسم ملف المرجع | الشاشة | السلوك الضروري |
|---|---|---|---|
| 01 | `01_Splash_Logo_Smile.png` | Cinematic branded reveal | Exact K stroke build → `knoux ai` progressive wordmark → smile stroke → one end click → Home handoff |
| 02 | `02_Home_Dashboard.png` | Home | Powerful hero orb, REAL scan CTA, device summary, six service cards, bottom navigation |
| 03 | `03_Privacy_Sweep.png` | Privacy overview | Privacy analysis, permission categories, risk review; opens `13` |
| 04 | `04_Duplicate_Files_Finder.png` | Duplicate overview | Groups, types, recoverable estimates, review CTA; opens `12` |
| 05 | `05_Deep_Scan.png` | Scan in progress | Real lifecycle, five module status panels, real progress/cancel/pause semantics; opens `14` after completion |
| 06 | `06_Reports_Insights.png` | Reports overview | Summary/history/charts sourced from actual data; opens `15` |
| 07 | `07_App_Residue_Cleanup.png` | Residue overview | Candidate inventory, individually reviewed cleanable data; opens `16` |
| 08 | `08_Storage_Analyzer.png` | Storage overview | Accurate accessible storage data + category drill-down; opens `18` |
| 09 | `09_Battery_Saver.png` | Battery overview | Measurable signals, recommendations, OS limitations; opens `19` |
| 10 | `10_AI_Optimization.png` | AI optimization overview | Measurable recommendations + supported actions; opens `20` |
| 11 | `11_Smart_Clean_Center.png` | Clean detailed view | Category selection, preview, confirmation, measurable before/after; links to applicable screens |
| 12 | `12_Duplicate_Group_Review.png` | Duplicate detail | Real thumbnails, keep-one original safety invariant, selection, preview, confirm |
| 13 | `13_Privacy_Risk_Inspector.png` | Privacy detail | Detailed per-app permissions/possible risks, OS-setting handoff; NO fake universal tracker blocking |
| 14 | `14_Deep_Scan_Summary.png` | Scan summary | Actual findings, timeline, supported next actions, no fabricated completion numbers |
| 15 | `15_Performance_Report_Detail.png` | Report detail | Charts/history/exports from real records, empty-state before records exist |
| 16 | `16_Residue_Item_Review.png` | Residue detail | Actual accessible URIs, preview + exclusion + scoped deletion + audit result |
| 17 | `17_Installed_Apps_Review.png` | App Manager detail | Installed app list/group, supported usage/permissions, OS handoffs; preserve user-installed apps |
| 18 | `18_Storage_Breakdown.png` | Storage detail | Category sizes, selectable large files, properly derived totals, drill-down |
| 19 | `19_Battery_Diagnostics.png` | Battery detail | Battery state, usage signals where permitted, estimated/unknown labeled honestly |
| 20 | `20_AI_Recommendations.png` | Recommendation detail | Evidence-backed plan, customizable approved actions, estimated vs verified outcomes |

**إضافة واجبة:** App Manager له Overview أصلي باسم `source-originals/07_App_Manager.png` مرتبط بتفاصيل `17_Installed_Apps_Review.png`. لا تحذفه بحجة أنه ليس ملفًا ضمن قائمة العشرين؛ حافظ على أي شاشة أصيلة أخرى خارج القائمة. الروابط والرجوع وحالات empty/loading/permission/error يجب أن تعمل.

## 4. الخدمات العشر — ماذا يعني End-to-End حقيقي؟

لكل خدمة مسار موحد: **Discover → Permission/Grant → Inspect → Configure → Search/Filter/Select → Preview & Risk → OS Consent → Real Action → Re-query → Journal → Retry/Restore/Export** حسب الإمكان الفعلي. الحالات `NOT_SCANNED`, `PARTIAL_ACCESS`, `RUNNING`, `CANCELLED`, `EMPTY_VERIFIED`, `FAILED`, `ACTION_CONFIRMATION_REQUIRED`, `ACTION_VERIFIED`, `BLOCKED_PLATFORM` وغيرها يجب أن تظهر بصدق.

| الخدمة | الوظيفة المقبولة | حدود Android التي لا يجوز تزويرها |
|---|---|---|
| Storage Analyzer | تحليل أحجام فعلية، مصادر/أقسام، فتح ومشاركة/نقل/حذف أو Trash في النطاق المسموح، مع سجل | `StatFs`, `MediaStore`, `SAF`; البيانات المقروءة ليست كل بيانات الهاتف |
| Duplicate Finder | Hash فعلي للنسخ المتطابقة، المقارنة، Keep-one، اختيار جماعي، Trash/Delete ومراجعة | Similarity تقدير مستقل عن Exact؛ لا حذف لآخر نسخة محمية |
| App Residue | كشف بقايا داخل نطاق التطبيق والمجلدات المختارة وعمليات تنظيف مصرّح بها | لا وصول سحري إلى بيانات التطبيقات الأخرى أو `Android/data` المحظور |
| Privacy Sweep | فحص الأذونات/المخاطر، تسليم لصفحات النظام، خزنة مشفرة إذا نُفذت فعلًا | لا ادّعاء حظر متتبعات شامل أو «VPN حماية» بلا محرك مثبت |
| App Manager | قائمة التطبيقات المتاحة، UsageStats بإذن، فتح إعدادات/إزالة عبر النظام، backup ضمن النطاق | لا Force-stop أو Clone أو Freeze غير مدعوم دون وضع إدارة جهاز مصرح |
| Battery Saver | قراءات شحن/حرارة/طاقة وتنبيهات وتوصيات قابلة للقياس | لا «زيادة بطارية 200%» أو تغيير إعدادات محمية بدون النظام |
| Deep Scan | وحدات مسح حقيقية، progress/cancel من العمل الحقيقي، تقرير findings قابل للتحقق | لا Malware detection حقيقي إلا بمصادر وتوقيعات واختبارات معلومة |
| Smart Clean | عناصر قابلة للاختيار، معاينة/تأكيد ثم تنفيذ فعلي وتحقق واسترجاع من Trash إن أمكن | لا حذف تلقائي لبيانات المستخدم أو ادعاء مساحة موفرة من تقدير |
| Reports & Insights | Room history، رسوم من الأحداث الحقيقية، تصدير PDF/CSV وسجلات | لا Charts أو نسب أو Trends وهمية؛ المصادر وأوقات العينات واجبة |
| AI Optimization | توصية مفسرة من قياسات فعلية، تحكم وموافقة، نتائج بعد الإجراء | لا تحسين FPS/سرعة شبكة مزيف، ولا حذف صامت باسم AI |

### 4.1 البنية التقنية المستهدفة

- تحقق أولًا من Stack **الموجود فعلًا** بدل فرض Compose أو React Native أو إعادة كتابة التطبيق. الفحص السابق لـGradle يشير إلى Android/Kotlin، لكن الطبقات الموجودة فعليًا يجب تدقيقها.
- `Room` + migrations/schema، `DataStore` للإعدادات، `WorkManager` للمهام المؤجلة وفق القيود، MediaStore/SAF والـSystem APIs المرخّصة، وtyped capability/permission gate لكل عملية.
- جدول قرارات الإمكانية: `SUPPORTED_NOW`, `NEEDS_USER_GRANT`, `NEEDS_OS_CONFIRMATION`, `SUPPORTED_WITH_USER_SELECTED_SCOPE`, `AUTHORIZED_ADVANCED_MODE_ONLY`, `UNSUPPORTED_THIS_ANDROID_API`, `RESTRICTED_DISTRIBUTION_POLICY`, `BLOCKED_EXTERNAL`.
- لا تستخدم أرقام Demo أو Timer باعتبارهما تقدم مسح حقيقي. كل رقم يعرض المصدر `StatFs/MediaStore/DocumentsProvider/PackageManager/BatteryManager/UsageStats/Room` ووقت القياس ونطاق الوصول ووحدات القياس وحالة الإذن.
- يحظر تشغيل عمليات تدميرية على ملفات المستخدم أثناء QA: استخدم fixtures صناعية معزولة فريدة، وراجع URI/المالك قبل التأكيد، واحذف فقط fixtures المصنوعة لذلك الاختبار.

## 5. خطة المراحل الـ16 — العقد التنفيذي الملزم

الجدول التالي **نطاق مطلوب وتنفيذ مرتب**، وليس تقريرًا بأن المراحل اكتملت.

| Stage | Expected Outcome | Acceptance Gate |
|---|---|---|
| M00 | Foundation: actual Android APIs, permission/capability router, typed models, Room/migrations, DataStore, WorkManager, source proof, safe operations | DB & permission tests and one real source/operation proven |
| M01 | Storage Analyzer | Full scoped storage inventory, inspect/filter/open/share/rename/move/trash/delete where supported, accurate totals/history/tests |
| M02 | Duplicate Finder | Exact SHA streaming, keeper protection, similar media opt-in, safe batch actions and true re-query |
| M03 | App Residue | App-owned+accessible user-selected residual candidates, attribution, review, exclusion, real remove and log |
| M04 | Privacy Sweep | Actual app/OS privacy capabilities and permissions, usable consented vault and validated security claims |
| M05 | App Manager | Real installed-app scope, usage when granted, OS install/uninstall/settings, authorized advanced modes only |
| M06 | Battery | Actual battery/thermal/charging signals, alerts, OS actions, no fake boost |
| M07 | Deep Scan | Real modules/scope/progress/cancel/result/evidence/remediation, validated findings |
| M08 | Smart Clean | Actual cleanup operations with all selection/confirmation/trash/permanent/history and schedules |
| M09 | Reports | Real history/analytics/export/schedule, data reconciled with actions |
| M10 | AI Optimization | Evidence-backed local/approved remote recommendations, user-run supported actions and measured result |
| M11 | Home | All screen routes, real live summaries, states, consistency, existing visuals |
| M12 | Settings | Live permission center, privacy, locale, work schedules, data export/erase and honest device constraints |
| M13 | Authentication | Existing Guest/Login/OTP/Google/Passkey/Biometrics wiring, sessions/security; never redesign the approved UI |
| M14 | Optional cloud + Pro | Actual owner-approved Supabase staging/RLS/opt-in sync, Billing/trials/50 real Pro feature gates; block unsupported external only |
| M15 | Release | Full audit/debug/release/signing/privacy/Play compliance, end-to-end test matrix and actual distribution evidence |

**ترتيب التنفيذ:** `M00 → M01 → M02 → M03 → M04 → M05 → M06 → M07 → M08 → M09 → M10 → M11 → M12 → M13 → M14 → M15`، مع إمكان متابعة عمل مستقل آمن إذا كان قسم منتظرًا مفتاحًا خارجيًا. يجب ألا يتحول انتظار Billing إلى تعطيل الخدمات المجانية.

## 6. ميزات Pro الخمسون — الأسماء الأصلية الدقيقة

**تنبيه:** هذه **التزامات Product Scope** وليست 50 ميزة مثبت تنفيذها ولا 50 بطاقة Upsell. لكل ID مسار استخدام حقيقي، Data Adapter، Capability Gate، Entitlement Gate، Consent، تنفيذ، اختبار وحالة مستقلة. إذا رفض نظام Android وظيفة معينة فلا تسوّقها كميزة شغالة.

| Stage | Feature ID | Contractual Description |
|---|---|---|
| M01 | `sto_detailed_breakdown` | Source-backed category/volume drilldown, accessible vs unavailable, non-overlapping counted size. |
| M01 | `sto_large_files` | Large-file finder with threshold/config, user selection, real provider actions and accurate sort. |
| M01 | `sto_growth_trends` | Room-backed repeated snapshots with real date range, retention and gaps. |
| M01 | `sto_capacity_planner` | Observed storage trend projections with method/band; no imaginary guarantee. |
| M01 | `sto_export` | Valid document export to user-chosen URI with accessible scoped data, correct totals. |
| M02 | `dup_verified_hashing` | Streamed cryptographic exact hash + collision-safe size recheck and provider source. |
| M02 | `dup_keeper_protection` | Enforce at least one protected keeper per exact group, including batch execution. |
| M02 | `dup_smart_similarity` | Distinct opt-in similarity scoring, confidence & confirm-before-action, no exact label. |
| M02 | `dup_batch_review` | Real multi-group selection and consented batch operation with per-item results. |
| M02 | `dup_recovery_journal` | History of OS trash/delete, restore when available, never universal undo. |
| M03 | `residue_own_cache` | Genuine KNOUX-owned cache drilldown/cleanup without deleting app config. |
| M03 | `residue_accessible_candidates` | Accessible residual candidate inventory with evidence and confidence labels. |
| M03 | `residue_saf_profiles` | Persist/revalidate user-selected folder grants and favorite scopes. |
| M03 | `residue_safe_trash` | Use OS/provider Trash where supported or explicit permanent delete otherwise. |
| M03 | `residue_history` | Write per-target action journal and reconcile after restart. |
| M04 | `prv_realtime` | Permission-state observations at achievable cadence with timestamps/OS limits. |
| M04 | `prv_vault` | Real authenticated encrypted vault, AEAD and safe lock/recovery/exports. |
| M04 | `prv_tracker` | Real tracker awareness or proven explicit opt-in filtering engine before claiming blocked. |
| M04 | `prv_report` | Full provenance-based export of privacy findings/limits. |
| M04 | `prv_history` | Historical snapshots and changes, not copied static cards. |
| M05 | `app_batch` | Batch selection with individually supported OS actions and consent. |
| M05 | `app_freeze` | Only verified managed-device/authorized mode implementation, no fake force-stop. |
| M05 | `app_backup` | Real export/backup of accessible APK or user-granted data with accurate coverage. |
| M05 | `app_usage` | Actual UsageStats-granted analytics, opt-in and provenance. |
| M05 | `app_clone` | Only genuine supported multi-user/work-profile functionality with authorization; otherwise blocked truthfully. |
| M06 | `bat_thermal_timeline` | Real temperature/thermal timestamps, labels and graph. |
| M06 | `bat_charge_profiles` | Charging observations and user-specified charging reminders. |
| M06 | `bat_usage_context` | Only authorized usage correlation with limitations. |
| M06 | `bat_smart_alerts` | Real configurable notification thresholds with mute/cancel. |
| M06 | `bat_recommendations` | Supported settings shortcuts and proven measured post-action changes. |
| M07 | `scan_malware` | Verified detection/signatures/known-answer corpus, provenance and honest unknown status. |
| M07 | `scan_hidden` | Deeper scan inside currently authorized accessible scopes, not other apps private data. |
| M07 | `scan_schedule` | WorkManager read-only scheduled scans and real notifications/cancel. |
| M07 | `scan_custom` | Saved scope/module/exclusion settings, user selects what to scan. |
| M07 | `scan_forensic` | Detailed file evidence, hashes, timestamps, source and export with consent. |
| M08 | `smart_clean_unlimited` | Repeatable genuine scans and user-controlled actions without demo counts. |
| M08 | `smart_clean_scheduled` | WorkManager scan/notification scheduling with no silent irreversible action. |
| M08 | `smart_clean_deep_analysis` | Evidence-based in-scope candidate analysis, transparent confidence. |
| M08 | `smart_clean_priority` | Scheduling priorities limited to KNOUX-owned tasks, real constraints/status. |
| M08 | `smart_clean_rollback` | Durable operation history and actual provider Trash restore where possible, no impossible undo. |
| M09 | `rep_export` | Valid PDF/CSV reports from actual records with user-selected destination. |
| M09 | `rep_history` | 7/30/90/custom Room-backed history including no-data gaps. |
| M09 | `rep_ai_insights` | Explanatory deterministic insights or genuine model-backed results with methods. |
| M09 | `rep_compare` | Actual time-window comparisons, dataset provenance. |
| M09 | `rep_schedule` | Actual scheduled reports/notifications, cancellation & lifecycle. |
| M10 | `opt_gaming` | Genuine app-owned focus profile and supported OS actions, no fabricated FPS gains. |
| M10 | `opt_ai_learn` | Consent-based local observation preferences and explainable adaptation. |
| M10 | `opt_thermal` | Measured heat/thermal analysis and safe supported mitigation. |
| M10 | `opt_network` | Actual connectivity/DNS/latency diagnostics with scope and no fake speed boost. |
| M10 | `opt_scene` | User-configured context profiles and conditional recommendations without stealth actions. |

## 7. Identity — Login / OTP / Google / Passkey / Biometric

**المبدأ:** مستخدم واحد وهوية خادم واحدة، مع Guest مسموح بحدود واضحة؛ إثبات الخادم وجلسة صالحة أهم من نجاح حركة الواجهة.

- **Google:** Android Credential Manager يلتقط Google ID Token بـAudience الصحيح (`Web Client ID` الصحيح) ويرسله إلى Auth Backend للتحقق وإنشاء Session. لا Google WebView مدمج ولا Client Secret في APK.
- **OTP:** مسار التسجيل والتحقق وإعادة المحاولة وResend/Expiration واستعادة الحساب موجود تصميميًا حسب ما أعلنه المالك؛ **يجب إثبات Back-end عمليًا**. لا OTP ثابت أو bypass.
- **Passkey:** Android Credential Manager (`API >=28` مع دعم الجهاز) + WebAuthn server RP حقيقي بـHTTPS، challenge قصير العمر وتحقق signatures وorigin، وDigital Asset Links مع بصمة توقيع SHA-256 للشهادة ذات الصلة. Android API 26–27 يستعمل fallback حقيقي بدل محاكاة.
- **Biometric:** BiometricPrompt مع Android Keystore لتأمين **إعادة فتح جلسة خادم صالحة موجودة**؛ **ليس بديلًا مستقلًا لهوية Server**. راعِ القفل/الإلغاء/انتهاء الجلسة/إبطال التوكن/عدم تسجيل بصمة.
- يجب حفظ صلاحية الجلسات والـrevoke/sign-out بأمان وعدم طباعة tokens في Logcat.

### 7.1 إعدادات Google Cloud / Supabase كما وردت سابقًا

| العنصر | القيمة التاريخية (تحتاج إعادة تحقق قبل تعديل Production) |
|---|---|
| Google Cloud project | `fiery-script-514604-n6` |
| Supabase project | `knoux ai` — ref `tduofkizwkjtqcbklfah` |
| Supabase URL | `https://tduofkizwkjtqcbklfah.supabase.co` |
| Google Auth callback | `https://tduofkizwkjtqcbklfah.supabase.co/auth/v1/callback` |
| Android app package | `com.knoux.ai` |
| Debug SHA-1 المبلّغ عنه | `C0:58:EA:53:09:9B:1D:7F:C5:08:F9:39:83:91:F9:D5:B9:22:24:2C` |
| حالة Supabase التاريخية | `ACTIVE_HEALTHY`، ليست قياسًا مباشرًا في هذا التلخيص |

**تقسيم عملاء OAuth الثلاثة بحسب تقرير سابق:** Android Client + Web Client أساسي مربوط بالـSupabase callback + Web/Server Client إضافي. **لا تخلط** Client ID الأساسي مع الآخر أو Client Secret المختلف. ذُكر أن الملفات انتقلت إلى مجلد خاص محلي `C:\Users\[LOCAL_USER]\Documents\KNOUX_AI_AUTH_PRIVATE_20261113` مع سكريبتات فحص وتطبيق؛ **وجودها اليوم وصلاحيتها غير مفحوصين هنا**. لا تضع JSON secrets في مصادر مشروع ChatGPT أو داخل Git/Repository أو مستند علني.

**الحالة التاريخية لربط Google:** تقرير سابق أفاد بتصنيف ملفات OAuth وبنجاح الاختبار المحلي لسكريبتات PowerShell؛ **هذا ليس دليلًا على تغيير Supabase Auth Provider عن بعد**. فحص سابق أظهر أن Google Provider لم يُفعّل، و`Management Token/CLI` مناسبًا للتعديل لم يكن متاحًا آنذاك. لا تتبنّى القول إن Google Login جاهز ما لم يظهر read-back حقيقي لإعدادات Provider + جلسة مستخدم Android ناجحة. لا توقف `Skip nonce checks` بغرض نجاح شكلّي.

**أعمال أبلغ عنها سابقًا وليست مثبتة هنا:** تقييد ملفات OAuth في المجلد الخاص، Hash verification، تجهيز `RUN_APPLY_GOOGLE_AUTH.cmd` و`02_APPLY_SUPABASE_GOOGLE.ps1` و`05_FINAL_VERIFY.ps1` وhandoff JSON عام، وتطبيق Migration باسم `restrict_rls_auto_enable_execution` مع Advisors صفر تحذيرات. يجب إعادة فحص أثر الـMigration والحالة الحية للمشروع وعدم افتراضها صحيحة لمجرد التقرير.

### 7.2 SQL / Cloud / RLS / 13 Tables

عقد المشروع يتضمن مخططًا بحثيًا لقاعدة Supabase بـ13 جدولًا، تتضمن `profiles`, `devices`, `user_settings`, `scan_sessions`, `scan_category_summaries`, `storage_snapshots`, `app_inventory_summaries`, `battery_snapshots`, `privacy_summaries`, `cleanup_actions`, `ai_recommendations`, `reports`, `entitlements` (الأسماء مأخوذة من master). **وجود SQL كتصميم لا يثبت إنشاؤه أو تنفيذه في قاعدة البيانات**. يلزم فحص schema وmigrations/RLS Policies والـIndexes والـownership والبيانات الحقيقية ومطابقة Row-Level Security بين المستخدمين. لا تكتب SERVICE_ROLE أو Tokens داخل العميل.

- اختبر Cloud Sync على أساس opt-in فقط، وتجنب رفع البيانات الحساسة افتراضيًا.
- **Play Billing / Pro:** تحقّق من الشراء/اشتراك/الإلغاء/Refunds من مصدر موثوق على الخادم، ولا تجعل client boolean أو UI تاجًا ذهبيًا يعطي صلاحيات غير مصرح بها.
- إثبات `Auth configured` منفصل عن إثبات `Login works`، وبدوره منفصل عن `Sync works` و`Pro entitlement works`.

## 8. جودة البرمجيات — منع Fake Closure

**حالات المشروع المعتمدة:** `CATALOGED`, `EXISTS_UI_ONLY`, `IMPLEMENTED_NOT_TESTED`, `PARTIALLY_WORKING`, `TESTED_WITH_SYNTHETIC_FIXTURES`, `VERIFIED_ON_SUPPORTED_SCOPE`, `BLOCKED_PERMISSION`, `BLOCKED_PLATFORM`, `BLOCKED_EXTERNAL_CONFIGURATION`, `NOT_IMPLEMENTED`, `LOCALLY_COMMITTED_NOT_PUSHED`, `PUSHED_NOT_DEPLOYED`, `DEPLOYED_NOT_RELEASED`, `RELEASE_ACCEPTED`.

**State machine للوكيل:** `QUEUED → PREFLIGHT → IMPLEMENTING → UNIT_TESTING → RUNTIME_TESTING → INDEPENDENT_REVIEW → INTEGRATION_REVIEW → VERIFIED`، مع `FAILED_VERIFICATION` و`BLOCKED_EXTERNAL` و`BLOCKED_PLATFORM_SCOPE` عند ثبوت أسبابها و`IMPLEMENTED_NOT_MERGED` لما لم يُدمج. لا تقفز لمرحلة VERIFIED بعد Build PASS.

**قبل أي تعديل في نسخة الهاتف افحص فعلًا:** `pwd`, Git HEAD, `origin/main`, `git status --porcelain=v1 -uall`, `git worktree list`, `git stash list`, `git reflog`, untracked، فروع/PR/CI، الـSDK، المساحة، الـAPK الحالي، الوكلاء المتوازيين وملكية الملفات. التاريخ السابق لا يصبح Current HEAD تلقائيًا.

**لكل مرحلة** احتفظ بسجل على `qa-evidence/e2e/Mxx/` يشمل شاشة قبل/بعد، مصادر البيانات، إذن النظام، logcat مُنقح، اختبارات الأوامر وexit/JUnit pass/fail/skip، API والجهاز، ملف APK وSHA-256، الأدلة قبل وبعد العملية وRoom journal، مراجعة RTL/LTR والوصولية، Commit SHA وRemote SHA وCI بعد الدمج. الحالة التي لا يمكن اختبارها تُكتب **NOT_RUN** لا PASS.

**منع التدمير:** لا `reset --hard` ولا `git clean` ولا force push ولا حذف branch/worktree/asset/source/SQL/QA أو ملفات untracked فريدة. نظّف فقط artifacts المؤقتة والمكررة القابلة للتوليد **بعد** قياسها والتأكد من عدم استخدامها وإثبات دمج/حفظ كل عمل فريد. لا تصنع نسخ Repo عملاقة إضافية أو تقوم بدمج بين Android وWindows.

### 8.1 معيار إثبات كل Feature

`CURRENT_SHA + EXACT_COMMAND + EXIT_CODE + JUNIT_RESULTS + DEVICE_API + REAL_PERMISSION_SCOPE + SYNTHETIC_INPUT + OS_CONSENT + BEFORE/AFTER_PROVIDER_REQUERY + ROOM_JOURNAL + SCREENSHOTS + INTEGRATION_SHA + CI_STATUS`.

**ليس دليلاً كافيًا منفردًا:** Screenshot، نجاح Gradle build، عدد اختبارات من تقرير قديم، كلام Agent، تشغيل Demo أو Mock، Readme يقول «DONE»، وجود ميزة Pro اسمها في الكتالوج.

## 9. سجل الحقائق والادعاءات والفجوات — 13 نوفمبر 2026

| الموضوع | مصدر المعلومة | التصنيف الواقعي الآن |
|---|---|---|
| وجود Contract شامل 16 مرحلة/10 خدمات/50 Pro/20 refs | ملف Master الأصلي المقروء | **موثق في المواصفة**؛ ليس اختبارًا للتطبيق |
| ملكية المالك للتصميم النهائي والانتهاء من Phase UI | توجيه المالك | **قرار/اعتماد تصميم**؛ التطابق الحالي على هاتف غير مفحوص |
| Splash/Onboarding/OTP/Passkey/Google/Biometric مُنفّذة | تصريح المالك وتقارير سابقة | **مُبلّغ عنه**؛ لا يوجد Evidence Runtime مستقل هنا |
| ملفي Ecosystem/Founder وصوت Salawat | أسماء PNG ورابط وصاحب المشروع | **مراجع محددة**؛ وجود الأصول في Android APK والتحقق من الصوت غير مثبت هنا |
| GitHub repo / package / Gradle versions | مصادر قديمة + فحص ملف سابق | **معلومات مرجعية تاريخية**؛ CURRENT HEAD غير مفحوص |
| Android release signed مع debug key | فحص سابق لملف Gradle | **تحذير تاريخي** يتطلب إعادة فحص signing قبل Release |
| Google Cloud/Supabase project refs/callback/3 OAuth clients | تقرير إعداد سابق | **معلومات إعداد مُبلّغ عنها**؛ إعادة تأكيد البيانات مطلوبة |
| ملف OAuth المحمي محليًا وسكريبتات PowerShell | تقرير سابق | **تم الإبلاغ عن تجهيزها**؛ لا فحص حالي للجهاز |
| Google Provider enabled في Supabase | لم يظهر Read-back أو Sign-In E2E | **NOT VERIFIED / likely pending** |
| Passkey server / Digital Asset Links / biometric re-unlock | عقد تقني | **NOT VERIFIED** |
| Supabase 13 tables/Policies/RLS | مقترح SQL + Migration أُبلغ عنها | **تصميم متوفر؛ نشر واختبار RLS غير مثبتين** |
| 16 M-stages / 50 Pro paid operations | أهداف تنفيذ | **غير موثق اكتمالها**؛ مراجعة كل ميزة ضرورية |
| Tests/CI/APK/Runtime/remote main الحالي | غير مفحوص مباشرة في هذه الجلسة | **UNKNOWN / NO CURRENT EVIDENCE** |
| Snapshot TAR وSHA-256 على هاتف OpenCode | طلب مالك سابق | **تعليمات**؛ لا أرشيف موثق هنا |

## 10. المخاطر ذات الأولوية وما يجب فعله عند الاستئناف

1. **P0 — حفظ العمل الفريد والتوازي:** قبل كل تعديل اعمل inventory فعلية لـHEAD/remote/status/untracked/agent writer. لا توقف وكيلًا ينفذ حاليًا أو تغيّر ملفاته في الخلفية. نسخة الهاتف غير نسخة Windows.
2. **P0 — حقيقة المصادقة:** افحص Authorized redirect URIs، web audience، Android package+SHA1 الحقيقي (Debug ثم Release وفق Build)، Supabase Google Provider read-back، تطبيق `Google Credential Manager → verified Supabase session` على جهاز. لا تعرض أسرارًا.
3. **P0 — Visual regression:** صور Runtime للشاشات المرجعية، Login صغير الخيارات بلا بصمة ضخمة أو duplicate، الـSplash لا يتداخل ولا يعيد الصوت، Founder/Ecosystem متماثلان مع PNG الأصلية.
4. **P0 — Start M00 ثم M01:** افحص الـSource والتبعيات أولًا، اختبر DB/permissions وابدأ عملية Storage على Scope حقيقي/Fixture صناعي، مع حالات deny/revoke/empty/cancel/real action/requery/history.
5. **P1 — Continue M02…M10:** خدمة كاملة كل مرة وبالترتيب، مع أدلة، ثم Home/Settings/Auth/Pro/Release (M11–M15). تقدّم الخدمات المجانية حتى لو Billing أو OAuth معطل جزئيًا.
6. **P1 — Cloud/Privacy/Store:** راجع 13-table SQL وأي migrations مطبقة فعلًا، تحقق RLS وdata minimization وPlay Billing وrestricted Android permissions والـRelease signing.
7. **P1 — Git convergence:** stage انتقائي وحفظ untracked، commit/push Fast-forward فقط بعد مراجعة وفحوص، ثم اقرأ Remote SHA/CI؛ لا تعلن `RELEASED` بدون دليل توزيع فعلي.
8. **P2 — Space:** قياس المساحة قبل وبعد وتنظيف Artifacts مؤقتة مثبت تكرارها فقط، بلا فقد مصادر فريدة.

## 11. مصادر هذه الوثيقة وترتيب الأولوية

1. **أعلى مرجع تنفيذي:** `KNOUX_AI_MOBILE_OPENCODE_SUPREME_FULL_FUNCTIONAL_E2E_MASTER_20261113.md` — المواصفة الكاملة (2,570 سطرًا)؛ بما فيها ملاحق المواصفة القديمة والـSQL والشاشتين والهوية.
2. **المراجع البصرية والصوت:** `KNOUX_AI_MOBILE_OPENCODE_TWO_SCREENS_SALAWAT_DIRECT_MP3_FINAL_20261113.md` وصور `01_KNOUX_AI_MOBILE_ECOSYSTEM_APPROVED.png` و`02_KNOUX_AI_MOBILE_FOUNDER_APPROVED.png`، و20 PNG قائمة §3.2.
3. **العقد الأمني المستقل:** `00_MASTER_AUTH_BOTH_AGENTS.md` — مع الفصل التام بين بيئة Android phone وPC.
4. **تقرير الحالة/المحادثة السابقة:** `KNOUX_COMPLETE_CONVERSATION_HANDOFF_2026-11-13.md` — مفيد لربط OAuth، ويضم أيضًا موضوع إنتاج إعلانات **WebPilot/GrabFlow** المنفصل عن تطبيق KNOUX AI؛ **لا تخلط المشروعين**.
5. **أحدث دليل حي عند الوكيل المنفذ** دائمًا أعلى من تقرير قديم: `git rev-parse`, Source/Gradle, DB provider read-back, CI, device QA, APK/runtime, screenshots, logs.

**قاعدة فضّ التعارض:** أحدث قرار مالك واضح يسبق تعليمات توقف قديمة؛ لكن لا يغيّر حقيقة Android أو Evidence. المرجع البصري الأصلي يتفوق على إعادة رسم أو استنتاج من نموذج. لا تجعل هذا الملخص يحل محل الـMaster الكبير عند بدء تنفيذ حسّاس؛ هو **سياق شامل ومؤشر للمصادر**.

## 12. فقرة نقل السياق لأي Agent جديد

> نحن نكمل **KNOUX AI Mobile** الموجود فعلًا في `/root/workspace/KNOUX-AI`، ولا نبدأ مشروعًا جديدًا. التصميم أُقرّ من المالك ويجب المحافظة عليه؛ الخدمات العشر وميزات Pro الخمسون والمراحل M00–M15 كلها التزام تنفيذي وليست شهادة اكتمال. ممنوع تعديل أو مزامنة `D:\KNOUX-AI` على Windows. افحص Current SHA وorigin/main وحالة Git وكل Untracked/الـAgents قبل التعديل. نفّذ ميزة حقيقية مكتملة End-to-End من Android APIs مع Permission/Consent/Room/Persistence/After-state/Tests/RTL/Screenshots. Google OAuth/Supabase/Passkey/Biometric **لا تعتبرها جاهزة** حتى تظهر أدلة Session وجهاز. لا Fake PASS، لا حذف أو Merge ضار، لا تضيع عملًا فريدًا. ابدأ من الفحص الحقيقي ثم M00 وM01 أو ثغراتهما الموجودة بدل التكرار، وواصل بقية المراحل حسب الواقع.

---

**وثيقة استمرارية مشروع — لا تمثل Production Certification أو شهادة أمن/إطلاق.**
