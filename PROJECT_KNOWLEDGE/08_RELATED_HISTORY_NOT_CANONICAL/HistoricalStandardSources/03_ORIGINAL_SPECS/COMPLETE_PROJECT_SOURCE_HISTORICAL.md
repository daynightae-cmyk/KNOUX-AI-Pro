---
title: "KNOUX AI — Project Source of Truth / Decisions, Designs, Execution, Integrations & Evidence"
project: "KNOUX AI"
scope: "Android Mobile primary; Windows project informational only (strictly isolated)"
as_of: "2026-11-13 11:40 Asia/Dubai"
artifact_type: "Project knowledge source / evidence-aware handoff"
version: "1.0"
status: "LIVING BASELINE — not a production release certificate"
confidentiality: "Sanitized: no credentials, access tokens or private keys"
canonical_mobile_workspace: "/root/workspace/KNOUX-AI"
separate_windows_workspace: "D:\\KNOUX-AI"
repository: "https://github.com/daynightae-cmyk/KNOUX-AI"
verified_remote_main: "b8577cbc7e798cfaf23a0f44650941be7ae332d0"
---

# KNOUX AI — المرجع الموحد الكامل للمشروع

**تاريخ التوثيق:** الجمعة 13 نوفمبر 2026، بتوقيت الإمارات.  
**الغرض:** ملف قابل للإضافة مباشرة إلى مصادر مشروع ChatGPT أو `docs/`، يجمع القرارات والتصميمات، نطاق التنفيذ، التكاملات، المشاكل، المتطلبات المفتوحة، وحالة الإثبات.  
**مهم جدًا:** هذا **ملف معرفة وتسليم ومراجعة** وليس تقرير PASS/COMPLETE/RELEASED. جزء من المعلومات مسترد من توجيهات المالك ومستندات التنفيذ وتقارير عمل سابقة؛ وتم إجراء قراءة مباشرة محدودة لـGitHub خلال إعداد الملف. لم يتم في هذه الجلسة فتح بيئة Android المحلية، تشغيل Gradle، اختبار جهاز، أو قراءة إعدادات Supabase الحية.

## 0. طريقة قراءة الحالة — مفتاح إلزامي

| الرمز | المعنى الدقيق | ما يجوز قوله |
|---|---|---|
| `OWNER_APPROVED_DESIGN` | تصميم/نص/سلوك وافق عليه مالك المنتج | **مُعتمد بصريًا** فقط، لا يعني أنه موجود داخل APK |
| `REPORTED_IMPLEMENTED` | تنفيذ ورد في تقرير وكيل أو محادثة سابقة | **مبلّغ عن تنفيذه**، يلزمه فحص repo/runtime |
| `DOCUMENTED_REQUIREMENT` | التزام هندسي أو مخطط مكتوب | **مطلوب**، وليس منفذًا |
| `DIRECT_REMOTE_VERIFIED` | تمت قراءة حالة GitHub مباشرة أثناء إعداد الملف | مثبت **في حدود البيانات المقروءة فقط** |
| `LOCAL_NOT_INSPECTED` | لا وصول/فحص حي لمجلد المشروع في الجلسة الحالية | لا يصح تعيين PASS/FAIL من التخمين |
| `EXTERNAL_NOT_VERIFIED` | لم تثبت حالة Google/Supabase/Play أو أي موفر خارجي من اختبار حي | Pending أو غير مثبت |
| `RUNTIME_NOT_VERIFIED` | لا تشغيل فعلي موثق على الجهاز من نفس source SHA | لا يثبت عمل المستخدم النهائي |
| `BLOCKED_BY_PLATFORM` | قدرة يقيدها Android/Google Play فعليًا بعد تحقيق الحدود المدعومة | توضيح scope والبديل المشروع بدل تسويق ميزة وهمية |

**القاعدة الذهبية:** `موافقة على التصميم ≠ بناء ناجح ≠ اختبارات ناجحة ≠ وظائف تعمل على الجهاز ≠ push إلى main ≠ CI green ≠ RELEASED`.

## 1. الحقيقة التنفيذية الحالية — REALITY FIRST

### 1.1 حدود المنتج والمستودعات

- **المنتج:** `KNOUX AI`؛ أحد منتجات مظلة `KNOUX` وليس الشركة الأم نفسها.
- **المؤسس:** `Eng. Sadek Elgazar` / `م. صادق الجزار`، بالصفة `Founder & Software Engineer` و`مؤسس KNOUX`.
- **الموقع الرسمي:** https://knoux.store/ . **GitHub:** https://github.com/daynightae-cmyk/KNOUX-AI .
- **Android/Phone canonical workspace:** `/root/workspace/KNOUX-AI` (بيئة OpenCode على الهاتف/لينكس). هذا هو **الهدف التنفيذي لمسار Mobile M00–M15**.
- **مسار كمبيوتر منفصل:** `D:\KNOUX-AI`. تعليمات المالك تمنع القراءة/النسخ/الدمج التلقائي أو اعتبار المشروعين شجرة عمل واحدة؛ حتى لو اتفق الاسم أو ربطا بمستودع بعيد واحد. لا تفترض أن مشروع الكمبيوتر Electron أو موبايل قبل فحصه.
- **Git remote:** `daynightae-cmyk/KNOUX-AI` عام وقت الفحص؛ `main` هو default branch. لا تعتبر وجود الملفات في GitHub دليلًا على أن كل عمل OpenCode المحلي أصبح مرفوعًا.

### 1.2 فحص GitHub مباشر جرى لهذه الوثيقة

| عنصر | النتيجة المقروءة | حدود الدلالة |
|---|---|---|
| Remote repository | `daynightae-cmyk/KNOUX-AI`، default branch `main` | **DIRECT_REMOTE_VERIFIED** |
| Remote `main` SHA | `b8577cbc7e798cfaf23a0f44650941be7ae332d0` | **DIRECT_REMOTE_VERIFIED** وقت الفحص فقط |
| رسالة آخر commit | `docs(pc02): verify duplicate service evidence and advance to PC03` | إثبات **وجود commit/رسالة**، لا إثبات نتائج اختبار Android |
| PRs المفتوحة | لم تُظهر GitHub API أي PR مفتوح وقت الفحص | لا ينفي وجود فروع محلية أو PRs مغلقة |
| GitHub Actions | استعلام GitHub API أعاد `total_count: 0` للـworkflow runs | لا يعني وجود CI PASS؛ قد توجد اختبارات أو CI خارج Actions |
| حماية `main` | branch API أظهر `protected=false` | لا تعوّض مراجعة الدمج والتحقق من SHA |
| الجذر البعيد | `app/`, `android/`, `database/`, `design/`, `docs/`, `qa-evidence/`, `gradlew`, `build.gradle.kts` وغيرها | إثبات أسماء مسارات فقط؛ لا فحص محتوى كل ملف |

**الرابط القابل لإعادة التحقق:** https://github.com/daynightae-cmyk/KNOUX-AI/commit/b8577cbc7e798cfaf23a0f44650941be7ae332d0 .  
**غير مقروء مباشرة:** `HEAD` و`git status` في هاتف OpenCode، الشغل غير الملتزم، worktrees، stashes، APK الأخير، نتائج device/runtime، قيمة local main، حالة deployment أو حالة Supabase الحالية. لا تجعل commit برسالة `verify` شهادة قبول مستقلة.

### 1.3 آخر معالم تاريخية مبلّغ عنها — وليست Snapshot حي

- **10–11 نوفمبر:** ورد في تقرير فحص بيئة Android PRoot/OpenCode وجود شجرة `/root/workspace/KNOUX-AI` وحوالي 457 ملفًا، وأن Git/بناء APK لم يكونا مؤكدين بتلك المحاولة. هذا وصف **قد يكون تجاوزه شغل لاحق**؛ لا يُعاد استخدامه بوصفه حالة اليوم.
- **12 نوفمبر:** ذُكر baseline سابق `d561f82164a7e79288ffd04172b264098846d96b` في مرحلة تنسيق التصميم، مع تعديلات محلية مُبلّغ عنها على `MasterScreens.kt`, `NeonArt.kt`, `PreviewData.kt`, `styles.xml` وملف غير متتبع `ProSystem.kt` ومواد QA؛ لا نعرف وضعها الحالي أو ما إذا دُمجت.
- **13 نوفمبر:** أعلن المالك انتهاء **مرحلة التصميم البصري للموبايل** واعتماد الهوية والشاشات، وفتح صلاحية مرحلة التشغيل الفعلي لكل الخدمات، مع حفظ الأنظمة القائمة بدل إعادة بنائها.
- ورد سابقًا عن **نسخة الكمبيوتر** أن PC00/PC01 مدمجان، وPC02 Duplicate Finder قيد العمل، ثم صارت رسالة أحدث `main` البعيد تقول advance to PC03؛ تلك الرسالة وحدها لا تسد فجوة مراجعة اختبارات PC02 أو تثبت اكتمال Mobile M02.
- ورد سابقًا عن الهاتف `itel S686LN` أن APK كان مطابقًا في SHA-256 في سياق اختبار، لكن لا توجد هنا قيمة SHA-256 وسجل تثبيت/إقلاع/اختبارات مصادقة مقروءة؛ لذلك حالة الـruntime **RUNTIME_NOT_VERIFIED**.

### 1.4 ما لا ينبغي الخلط بينه

1. الـ20 مرجعًا بصريًا = **خريطة واجهات**، وليست 20 خدمة شغالة.
2. 10 خدمات × 5 Pro = **50 تعاقد قدرة**، وليست إثبات 50 محرك منفذ.
3. مستند SQL الذي يحتوي 13 جدولًا هو **بحث/تصميم Migration**، وليس قاعدة بيانات تم نشرها.
4. `PASS` لاختبار سكريبت OAuth المحلي لا يثبت تعديل Google Provider البعيد.
5. صفحة Login أو بصمة مرسومة لا تساوي هوية متحققة من خادم أو Passkey فعلية.
6. لا تعتمد على Agent يتكلم عن المشروعين في نفس التقرير دون تحديد workspace/SHA.

## 2. القرارات المثبتة من المالك — DECISION REGISTER

| ID | القرار النهائي | الحالة | التحقق المطلوب في التطبيق |
|---|---|---|---|
| D-01 | إكمال **KNOUX AI Mobile** داخل `/root/workspace/KNOUX-AI`؛ لا لمس `D:\KNOUX-AI` | إلزامي | `pwd`, Git top-level وأذونات الوكيل |
| D-02 | التصميم البصري الحالي معتمد/مجمد، مع السماح بإضافات خدمية **داخل** الهوية | `OWNER_APPROVED_DESIGN` | before/after screenshots لكل مسار |
| D-03 | عدم إعادة بناء Splash/Login/OTP/Google/Passkeys/Biometric ولا صنع شاشة دخول ثانية | إلزامي | Route inventory + UX regression |
| D-04 | بداية واحدة سلسة: Splash → Awakening → Ecosystem → Founder → genuine Auth → Home | إلزامي | first-run + returning-user instrumentation |
| D-05 | إزالة البصمة العملاقة؛ أيقونات Biometric/Passkey/Google صغيرة ومحترفة بجوار وسائل الدخول | `OWNER_APPROVED_DESIGN` | مقارنة Login المرجعية |
| D-06 | خلفية deep navy/violet مع cyan/electric blue/magenta وglassmorphism راقٍ؛ لا generic Material UI | `OWNER_APPROVED_DESIGN` | screenshot comparisons |
| D-07 | شاشات Login/OTP/Onboarding غالبًا one viewport بدون Scroll، بينما Home وصفحات قوائم النتائج قابلة للتمرير عند اللزوم | توجيه UX | 841×1870 وأحجام أصغر وتكبير خط |
| D-08 | الصلوات الصوتية المصرح بها محليًا في APK، بعد اكتمال خط الابتسامة الحقيقي **مرة واحدة** | قرار صريح | فحص MP3 + audio-focus lifecycle + على الجهاز |
| D-09 | جميع الخدمات العشر حقيقية end-to-end، لا Preview-only ولا بيانات شكلية | إلزامي | كل M-stage بمعايير action/result/re-query |
| D-10 | 50 Pro feature IDs بالضبط، ليس مجرد بطاقات بيع؛ شراء/Trial Server Authoritative | إلزامي | feature catalog + Billing/entitlement QA |
| D-11 | قاعدة Local-first وCloud اختياري opt-in، بحدود صلاحيات Android وخصوصية البيانات | إلزامي | Room/migrations/RLS/consent |
| D-12 | تنفيذ 16 مرحلة M00→M15 بترتيب متدرج وevidence ledger دائم | إلزامي | سجل workflow + artifacts |
| D-13 | حفظ كل كود/أصول/تصميم فريد، منع reset/clean/force-push/مسح غير محقق | إلزامي | inventory + SHA + staged diff |
| D-14 | تحرير المساحة فقط من generated disposable بعد حفظ العمل وعدم تعارض Agent | إلزامي | قياس bytes قبل وبعد |
| D-15 | لا إعلان PASS/100%/RELEASED قبل اختبارات المصدر الحالي والجهاز وCI/النشر عند اللزوم | إلزامي | acceptance evidence matrix |

## 3. الهوية البصرية المعتمدة — DESIGN SYSTEM LOCK

### 3.1 العلامة

- الأصل هو رمز **K** الأصلي ذو الشريط القطري المتقاطع، إسفين magenta أعلى اليسار، قسم cyan سفلي، والنسب الصحيحة؛ مع كلمة `knoux ai` بالأحرف الإنجليزية الصغيرة والـwordmark الأصلي. لا يُعاد اختراع الشعار أو يُحاصر بدائرة إجبارية؛ الكرة/الـorb خيار تركيب بصري بحسب الشاشة.
- هوية الشركة الأم `KNOUX` مختلفة عن اسم المنتج `KNOUX AI`. لا تُضاف مشاريع الـportfolio الستة عشر إلى onboarding الموبايل الخاص بهذا المنتج.
- ألوان أساسية كنقاط مرجعية مستمدة من الماستر: الخلفية `#080512`, `#0B0719`, `#120826`؛ violet `#843BFF` → `#B047FF`، magenta `#E34CFF`، cyan `#00DCFF` / `#21B9FF`؛ text `#F8F6FF` وsecondary `#B9B0D0`. **الصور المعتمدة تحسم الضبط النهائي**، وليست أرقام hex وحدها.
- Deep glass layers / spectral hairline borders / hero glow مضبوط / 3D-ish holographic cards / orbital particles. الـHero واضح، وأزرار الخدمة قابلة للمس، ولا تتغلب الزينة على القراءة.
- رسوم متحركة مدروسة، لا تكرار مزعج أو CPU load دائم. ضوابط A11y وscreenreader وreduced-motion و48dp؛ RTL عربي وLTR للإنجليزي/المعرفات التقنية. الـdark لازم يطابق المراجع؛ الاحتفاظ بالـDay mode لو كان موجودًا.
- يحظر تحويل screenshot مرجعي كامل إلى UI ثابت أو وضع invisible hit zones فوق صورة. الصور **مراجع** للمكونات الأصلية responsive.

### 3.2 Animation الـSplash المعتمد

1. `0–180ms` خلفية كحلية شبه سوداء وهالة ضوء صغيرة عند أصل K.
2. `180–600ms` يُرسم إسفين K الـmagenta، مع writing tip حقيقي وليس Fade عشوائي.
3. `520–1120ms` شريط K القطري الأصلي يتكون تدريجيًا حتى cyan lower leg.
4. `1030–1680ms` تُكتب `knoux ai` تدريجيًا، و`knoux` أبيض و`ai` بنفسجي وردي.
5. `1600–2420ms` يُرسم خط ابتسامة ضحل تحت الاسم من اليسار لليمين.
6. `2370–2550ms` نقطة نجمة صغيرة عند نهاية الابتسامة + **SFX click واحد** قصير، بلا beep متكرر.
7. `2550–3100ms` يستقر الشعار ويحدث انتقال ناعم إلى مسار الدخول؛ النص الأقدم يصف Home handoff لكن قرار first-run الأحدث يُبقي onboarding/auth قبل Home عند المستخدم غير المصادق.
8. الصوت العربي المعتمد **لا يُبدأ بتايمر تقديري**؛ `SmileStrokeCompleted`/حدث هندسي دال على اكتمال الخط هو الزناد. مرة واحدة بعد اكتمال النهاية وفق صوت المستخدم وحالة foreground/lifecycle؛ الصوت منفصل عن SFX النقر ولا يُكرر عند Compose recomposition.
9. الزمن الأصلي المرغوب للـSplash **2.7–3.3 ثانية** قابل للتعديل بعد قياس حقيقي. لا تقرير عن توقيت مقاس أو فيديو runtime في هذه الجلسة.

### 3.3 شاشة الدخول والهوية — Design Acceptance

- **Single primary Login surface** مريحة ونظيفة؛ هيدر اللوجو لا يتداخل مع المحتوى أو يكرر اللوجو مرتين؛ لا بصمة ضخمة في منتصف الشاشة، لا Google/Passkey متناثر أو mixed alignments.
- واجهة تحقق OTP واضحة ومطابقة لنفس Visual Language؛ Google Sign-In وPasskeys وBiometric كخيارات صغيرة راقية ومسماة بشكل صحيح. التحسين **للتوجيه والصيانة فقط**، وليس أمرًا لإعادة رسم معتمَد بالفعل.
- احتفظ بحقول الدخول، تسجيل حساب، OTP/Resend/Expiry، الاستعادة، Guest، المعالجة الحقيقية لرسائل الخطأ وBack. لا fake auth state بعد الضغط على CTA.
- عربي/إنجليزي، safe insets، لا تداخل Status bar، دعم keyboard IME، تركيز صحيح، حالات loading/disabled/failed، touch areas ≥48dp.

### 3.4 شاشة Ecosystem — الفصل الثاني (PNG رسمي 841×1870)

- الملف المرجعي: `01_KNOUX_AI_MOBILE_ECOSYSTEM_APPROVED.png`.
- أعلى الصفحة `تخطي` و`رجوع →` في مواضع تحترم safe insets، مع شعار K/wordmark الصغير.
- العنوان المعتمد حرفيًا: **«منظومة ذكية واحدة.»**، والسطر **«10 خدمات ذكية، منصة واحدة متكاملة.»**.
- K core مركزي عائم، حلقة violet/cyan، badge **«نواة الذكاء»**، جسور ضوئية رفيعة تربط عشر بطاقات خدمية متفاوتة التدرج ورموزها.
- أسماء البطاقات المعتمدة: `مراجعة الخصوصية`، `اكتشاف الملفات المكررة`، `مركز الجهاز الذكي`، `توفير البطارية`، `تنظيف بقايا التطبيقات`، `تحليل التخزين`، `التقارير والرؤى`، `إدارة التطبيقات`، `الفحص العميق`، `التحسين الذكي`.
- Dot التقدم **الثانية** مضيئة cyan، CTA **«التالي»** يقود فعليًا إلى Founder؛ التخطي يقود إلى auth القائم **دون تسجيل وهمي**؛ الرجوع يعمل.
- مهم: إظهار البطاقة في onboarding **لا يثبت عمل محركها**.

### 3.5 شاشة Founder — الفصل الثالث (PNG رسمي 841×1870)

- الملف المرجعي: `02_KNOUX_AI_MOBILE_FOUNDER_APPROVED.png`.
- أرضية سينمائية purple/navy مع ribbons cyan/magenta، لوجو أصلي، وكرة orbital pearl-white بشعار KNOUX الأم الأصلي، **ليست بصمة أو أيقونة Unlock**.
- عبارة **«أحد منتجات KNOUX»**، عنوان `KNOUX AI`، وجملة **«رؤية هندسية. تجربة صُممت من أجلك.»**، ووصف **«اكتشف الابتكار الذي يقف خلف KNOUX AI.»**.
- Founder glass card ثنائي اللغة: `Eng. Sadek Elgazar` / `Founder & Software Engineer` / `م. صادق الجزار` / `المؤسس ومهندس البرمجيات` / `مؤسس KNOUX` / `knoux.store`.
- Dot **الثالثة** مضيئة، وزر **«ابدأ رحلتك مع KNOUX AI»** يحدد completed-onboarding وينقل إلى auth الحقيقي، أو مسار الحساب المصادق حسب الحالة.
- تكوين نصوص وCTA عناصر Android أصلية؛ لا شاشات صور ثابتة، مع اختبار contrast وRTL وfont scaling.

### 3.6 Media/Audio provenance

- المصدر المحدد من المالك: `https://mp3tourl.com/audio/1794520051232-3d804d0a-2f23-4c00-9879-af7542c6616f.mp3`.
- أفاد المالك بأن لديه إذنًا باستخدام هذا التسجيل من صاحب الموقع؛ **إفادة صاحب الحق في المحادثة، وليست وثيقة ترخيص مستقلة تم التحقق منها**. مراجعة حقوق التسجيل/الأداء مطلوبة قبل التوزيع إن لم يشملها الإذن.
- مطلوب **تنزيل البايتات الفعلية** على بيئة البناء، التحقق من Content-Type/codec/duration وSHA-256 والسمع (العبارة المقصودة «صلّ على محمد»)، ثم وضعه في `app/src/main/res/raw/knoux_salawat_intro.mp3` أو reuse إن كان موجودًا.
- مطلوب `docs/media/INTRO_SALAWAT_PROVENANCE.md` بقيم **مقاسة** لا مصطنعة؛ Audio Focus، mute، DND، background/pause/skip، إلغاء الموارد. لا streaming runtime ولا Microphone permission.
- **حالة هذه الجلسة:** محتوى MP3 الفعلي وطوله وHash وتشغيله داخل APK **غير متحقق**. إذا فشل الرابط، نُكمل الواجهتين ونسجل `AUDIO_ASSET_DOWNLOAD_BLOCKED` فقط.

## 4. مرجع الواجهات العشرين — SCREEN MAP

هذه قائمة المرجع البصري من الماستر، وهي **inventory مطلوب الحفاظ عليه والتحقق منه**؛ ليست تقرير اكتمال 20 شاشة.

| ID | Reference file | Surface | Required behavior/relationship |
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

**استثناء مهم:** شاشة App Manager overview المرجعية الأصلية `source-originals/07_App_Manager.png` تُحفظ بجانب مرجع `17_Installed_Apps_Review.png` كتفاصيل داخلية. لا يُخلق رقم 21 ولا تُحذف شاشة موجودة لمجرد عدم ورودها في 20 صورة.

**نظام التنقل:** Home ست بطاقات أساسية، وجميع الخدمات العشر قابلة للوصول عبر Home/Tools والـnavigation القائم؛ كل Overview يفتح detail الصحيح ويحتفظ بحالة التصفح والـBack والـscroll وpermission/empty/error states. لا روابط dead/duplicated. واجهات Home/Services الثقيلة قد تحتاج scrolling بينما auth/onboarding تبقى مرتبة داخل viewport ما أمكن.

## 5. المحرك المطلوب: 10 خدمات حقيقية + 16 مرحلة M00–M15

### 5.1 العقد المشترك لكل خدمة

`اختيار Scope → التحقق من صلاحية الوصول → Scan حقيقي → عرض مصدر/عدد/حجم/حالة → تفاصيل وSearch/Filter/Sort → استبعاد/حماية → Select/Batch → Impact Preview → OS/User consent → تنفيذ مؤرخ قابل للإلغاء عند نقطة آمنة → نتيجة لكل عنصر → إعادة استعلام للمصدر → History/Recovery/Retry/Export عند الدعم`.

**بدون Fake Closure:** لا Progress وهمي؛ لا Cleaned Bytes مزيّفة؛ لا إغلاق يمنع الاسترجاع حيث يسمح نظام التشغيل؛ ولا claim أن كل ذاكرة الهاتف مقروءة. عند الحظر يظهر سبب `BLOCKED_PERMISSION`/`BLOCKED_PLATFORM`، وما يستطيع المستخدم عمله فعلاً.

**CapabilityDecision لكل عملية:** `SUPPORTED_NOW`, `NEEDS_USER_GRANT`, `NEEDS_OS_CONFIRMATION`, `SUPPORTED_WITH_USER_SELECTED_SCOPE`, `AUTHORIZED_ADVANCED_MODE_ONLY`, `UNSUPPORTED_THIS_ANDROID_API`, `RESTRICTED_DISTRIBUTION_POLICY`, `BLOCKED_EXTERNAL`.

### 5.2 خارطة المراحل ومخرج كل مرحلة

| مرحلة | نطاق الإنجاز الحقيقي | Gate نهائي |
|---|---|---|
| M00 | Foundation: adapters, Room/migrations, DataStore, WorkManager, permissions, source-provenance, safe-operation engine | unit + migration + actual first source/action |
| M01 | Storage Analyzer | scope، data، actions، verification/history |
| M02 | Duplicate Finder | SHA-256 حقيقي، Keeper invariant، user consent، re-query |
| M03 | App Residue | app-owned/accessible scoped residual candidates، history/confirm |
| M04 | Privacy Sweep | صلاحيات ومخاطر مرصودة + Vault حقيقي إذا معلن |
| M05 | App Manager | PackageManager/UsageStats ضمن visibility/permission وOS actions |
| M06 | Battery Saver | BatteryManager/Thermal observations، alerts، no fake boosts |
| M07 | Deep Scan | خمسة Modules بعمل حقيقي، progress/cancel/results |
| M08 | Smart Clean | اختيار/تنفيذ/OS approvals/Trash/Delete/after-state |
| M09 | Reports | history/events/charts/export/schedules من بيانات مقاسة |
| M10 | AI Optimization | توصيات قابلة للتفسير، action consent، قياس أثر |
| M11 | Home & Navigation | كل الرحلات والـwidgets من actual repository، AR/EN |
| M12 | Settings & Permissions | settings/consent/locale/export/erase/background scheduling |
| M13 | Auth | Google/OTP/Passkey/Biometric/Guest/session lifecycle بدون تصميم جديد |
| M14 | Cloud + Pro | opt-in Supabase/RLS، 50 features، verified billing/trial |
| M15 | Release | full audit/test/build/signing/privacy/Play and release evidence |

**ترتيب المالك:** مراحل M00→M15 بالتدرج؛ مسموح تجميع Pro features داخل مرحلة خدمتها، لكن المنتج لا يصبح Released قبل M15. ولا تُخلط أسماء PC00–PC13 الخاصة بالكمبيوتر مع M00–M15 الخاصة بالهاتف.

### 5.3 ملخص وظيفي تفصيلي لكل خدمة (مواصفات لا شهادات تنفيذ)

| المرحلة / الخدمة | مصادر بيانات ومخرجات | تفاعلات المستخدم والأمان | Visual functional adaptation |
|---|---|---|---|
| M01 Storage Analyzer | `StatFs`, `MediaStore`, `SAF`, Room snapshots، نسب مع scope | مصدر/volume، فئات، large files، فتح/rename/move/copy/share/trash/delete حيث يجيز provider، export؛ منع double count | storage ring/treemap مع drill-down |
| M02 Duplicate Finder | streams + size + `SHA-256` بعد grant، optional image similarity | Exact ≠ similar، حافظ على Keeper واحد على الأقل، user review + OS delete/trash + restore عند الدعم | compare cards، keeper badge، batch carousel |
| M03 App Residue | app-owned cache وSAF selected folders فقط | attribution confidence، ما ينفعش مسح بيانات apps الأخرى الخاصة؛ جرد وفحص وحذف scoped مؤكد | provenance timeline وcleanup drawer |
| M04 Privacy Sweep | platform permission/app signals، optional Keystore vault | evidence & confidence، OS settings handoff؛ لا Universal Tracker Blocker إلا بمحرك موثق | privacy permissions map + findings |
| M05 App Manager | PackageManager visibility + user-granted UsageStats | فلاتر ومقارنة وOS uninstall/settings، لا silent force-stop/clone/freeze | app mosaic/list + capability-specific actions |
| M06 Battery | `BatteryManager`, battery intents, thermal APIs | نسبة/حرارة/شحن حقيقية، توصيات وalerts، no fake hours gained or cooling | thermal rings/trend graphs |
| M07 Deep Scan | modules real scanner + evidence | job lifecycle، pause/cancel، فحوص فعّالة ضمن scope وملخص خطر موثق | modular scan console |
| M08 Smart Clean | actual eligible candidates، WorkManager + journal | preview → select → confirm → OS approval → action → re-query؛ avoid permanent silent action | Cyber clean stage + truthful before/after |
| M09 Reports | Room journal/observations/snapshots | 7/30/90/custom، gaps، CSV/PDF export، scheduled summaries | event timeline and provenance charts |
| M10 AI Optimization | provenance-backed rules/optional approved model | rationale + confidence، approve action، before/after measurement، no autonomous deletion | K reasoning core + recommendation cards |

### 5.4 Pro Feature Registry — **الأسماء الخمسون الأصلية دون تغيير**

هذه هي الـIDs المحددة حرفيًا من ماستر 13 نوفمبر (10 × 5). كل واحدة يجب أن تمتلك Route + Data Adapter + UI Controls + Permission Decision + Entitlement Gate + Test. **مذكورة كمواصفات، لا يوجد دليل 50 PASS بهذه الجلسة.**

#### M01 — STORAGE ANALYZER (5 Pro features)

| Stable feature ID | الوظيفة المطلوبة كما وردت بالماستر |
|---|---|
| `sto_detailed_breakdown` | Source-backed category/volume drilldown, accessible vs unavailable, non-overlapping counted size |
| `sto_large_files` | Large-file finder with threshold/config, user selection, real provider actions and accurate sort |
| `sto_growth_trends` | Room-backed repeated snapshots with real date range, retention and gaps |
| `sto_capacity_planner` | Observed storage trend projections with method/band; no imaginary guarantee |
| `sto_export` | Valid document export to user-chosen URI with accessible scoped data, correct totals |

#### M02 — DUPLICATE FINDER (5 Pro features)

| Stable feature ID | الوظيفة المطلوبة كما وردت بالماستر |
|---|---|
| `dup_verified_hashing` | Streamed cryptographic exact hash + collision-safe size recheck and provider source |
| `dup_keeper_protection` | Enforce at least one protected keeper per exact group, including batch execution |
| `dup_smart_similarity` | Distinct opt-in similarity scoring, confidence & confirm-before-action, no exact label |
| `dup_batch_review` | Real multi-group selection and consented batch operation with per-item results |
| `dup_recovery_journal` | History of OS trash/delete, restore when available, never universal undo |

#### M03 — APP RESIDUE CLEANUP (5 Pro features)

| Stable feature ID | الوظيفة المطلوبة كما وردت بالماستر |
|---|---|
| `residue_own_cache` | Genuine KNOUX-owned cache drilldown/cleanup without deleting app config |
| `residue_accessible_candidates` | Accessible residual candidate inventory with evidence and confidence labels |
| `residue_saf_profiles` | Persist/revalidate user-selected folder grants and favorite scopes |
| `residue_safe_trash` | Use OS/provider Trash where supported or explicit permanent delete otherwise |
| `residue_history` | Write per-target action journal and reconcile after restart |

#### M04 — PRIVACY SWEEP (5 Pro features)

| Stable feature ID | الوظيفة المطلوبة كما وردت بالماستر |
|---|---|
| `prv_realtime` | Permission-state observations at achievable cadence with timestamps/OS limits |
| `prv_vault` | Real authenticated encrypted vault, AEAD and safe lock/recovery/exports |
| `prv_tracker` | Real tracker awareness or proven explicit opt-in filtering engine before claiming blocked |
| `prv_report` | Full provenance-based export of privacy findings/limits |
| `prv_history` | Historical snapshots and changes, not copied static cards |

#### M05 — APP MANAGER (5 Pro features)

| Stable feature ID | الوظيفة المطلوبة كما وردت بالماستر |
|---|---|
| `app_batch` | Batch selection with individually supported OS actions and consent |
| `app_freeze` | Only verified managed-device/authorized mode implementation, no fake force-stop |
| `app_backup` | Real export/backup of accessible APK or user-granted data with accurate coverage |
| `app_usage` | Actual UsageStats-granted analytics, opt-in and provenance |
| `app_clone` | Only genuine supported multi-user/work-profile functionality with authorization; otherwise blocked truthfully |

#### M06 — BATTERY SAVER (5 Pro features)

| Stable feature ID | الوظيفة المطلوبة كما وردت بالماستر |
|---|---|
| `bat_thermal_timeline` | Real temperature/thermal timestamps, labels and graph |
| `bat_charge_profiles` | Charging observations and user-specified charging reminders |
| `bat_usage_context` | Only authorized usage correlation with limitations |
| `bat_smart_alerts` | Real configurable notification thresholds with mute/cancel |
| `bat_recommendations` | Supported settings shortcuts and proven measured post-action changes |

#### M07 — DEEP SCAN (5 Pro features)

| Stable feature ID | الوظيفة المطلوبة كما وردت بالماستر |
|---|---|
| `scan_malware` | Verified detection/signatures/known-answer corpus, provenance and honest unknown status |
| `scan_hidden` | Deeper scan inside currently authorized accessible scopes, not other apps private data |
| `scan_schedule` | WorkManager read-only scheduled scans and real notifications/cancel |
| `scan_custom` | Saved scope/module/exclusion settings, user selects what to scan |
| `scan_forensic` | Detailed file evidence, hashes, timestamps, source and export with consent |

#### M08 — SMART CLEAN (5 Pro features)

| Stable feature ID | الوظيفة المطلوبة كما وردت بالماستر |
|---|---|
| `smart_clean_unlimited` | Repeatable genuine scans and user-controlled actions without demo counts |
| `smart_clean_scheduled` | WorkManager scan/notification scheduling with no silent irreversible action |
| `smart_clean_deep_analysis` | Evidence-based in-scope candidate analysis, transparent confidence |
| `smart_clean_priority` | Scheduling priorities limited to KNOUX-owned tasks, real constraints/status |
| `smart_clean_rollback` | Durable operation history and actual provider Trash restore where possible, no impossible undo |

#### M09 — REPORTS & INSIGHTS (5 Pro features)

| Stable feature ID | الوظيفة المطلوبة كما وردت بالماستر |
|---|---|
| `rep_export` | Valid PDF/CSV reports from actual records with user-selected destination |
| `rep_history` | 7/30/90/custom Room-backed history including no-data gaps |
| `rep_ai_insights` | Explanatory deterministic insights or genuine model-backed results with methods |
| `rep_compare` | Actual time-window comparisons, dataset provenance |
| `rep_schedule` | Actual scheduled reports/notifications, cancellation & lifecycle |

#### M10 — AI OPTIMIZATION (5 Pro features)

| Stable feature ID | الوظيفة المطلوبة كما وردت بالماستر |
|---|---|
| `opt_gaming` | Genuine app-owned focus profile and supported OS actions, no fabricated FPS gains |
| `opt_ai_learn` | Consent-based local observation preferences and explainable adaptation |
| `opt_thermal` | Measured heat/thermal analysis and safe supported mitigation |
| `opt_network` | Actual connectivity/DNS/latency diagnostics with scope and no fake speed boost |
| `opt_scene` | User-configured context profiles and conditional recommendations without stealth actions |


### 5.5 Pro & Billing — الحقيقة الأمنية

- **واحد فقط** `FeatureRegistry` يجمع 10 مجموعات/50 IDs متفردة؛ كل بطاقة Pro لها شاشة حقيقية ومحرك وسبب تعطّل مفهوم، وليس upsell بلا destination.
- Core Free يبقى مفيدًا وقابلًا للتنفيذ. Pro لا يعطي صلاحيات Android محظورة؛ `entitlement AND capability` كلاهما شرط.
- الـBilling الحقيقي يتطلب Google Play Billing ProductDetails الحاليين، localized pricing (لا أسعار hardcoded)، purchase lifecycle (PENDING/PURCHASED/REFUND/REVOKE/GRACE/ACCOUNT HOLD)، backend purchase-token verification وacknowledge وrestore.
- الـTrial codes تصدر من خادم موثوق، `single-use`/expiry/account-bound/audited؛ ممنوع كود محلي أو boolean من الـclient يفعّل اشتراك حقيقي.
- اختصار الضغط المتكرر على شعار Home (ذُكر تاريخيًا 5 مرات) لفتح أدوات المطور **Debug / Owner-verified test only**؛ لا password backdoor أو bypass دائم داخل production.
- **حالة التنفيذ الفعلي للـ50:** `LOCAL_NOT_INSPECTED`، وكل Feature يحتاج Test + device outcome؛ لا تسويق كـPro Complete قبل ذلك.

## 6. التصميم المعماري والبيانات — LOCAL FIRST

### 6.1 طبقات Android المستهدفة

1. **UI:** احتفظ بالـCompose/الـframework القائم بعد فحص الكود؛ ViewModel + StateFlow إذا بالفعل Android Kotlin/Compose؛ native original components + navigation.
2. **Domain/Capability:** typed operations، `SourceProof`, `CapabilityDecision`, `PermissionGrantSnapshot`, `OperationPlan`, `OperationConfirmation`, idempotency and provider-liveness re-query.
3. **Adapters:** Android `MediaStore`, `ContentResolver`, Photo Picker, `SAF`, `DocumentsContract`, `StatFs`, `PackageManager`, `UsageStatsManager` (بموافقة)، `BatteryManager`, thermal APIs.
4. **Persistence:** Room + DAO + migration tests + `DataStore` للتفضيلات، WorkManager للمهام المناسبة والقيود؛ Keystore للمفاتيح الحساسة.
5. **E2E operations:** `scanId`, `operationId`, timestamps, source URI scope, state, item results, actual before/after and journal، audit without leaking personal filenames in public Git.
6. **Cloud اختياري:** Sync metadata/aggregates بعد opt-in وتحقق RLS، لا raw media contents ولا raw filesystem paths أو tokens.

### 6.2 Room schema المقترح بالمستند الأصلي — **ليس دليل وجود جداول محلية حاليًا**

`scan_sessions`, `scan_items`, `duplicate_groups`, `duplicate_members`, `cleanup_actions`, `storage_snapshots`, `app_observations`, `privacy_findings`, `battery_snapshots`, `scan_modules`, `recommendations`, `report_history`, `capability_grants`, `sync_outbox`.

كل جدول يحتاج `@Entity`, DAO، indexes/relations، migration وdata-integrity tests. لا تصرّح بأنه deployed/working إلا بعد فحص المشروع وفتح Database inspector أو الاختبارات الحقيقية.

### 6.3 Supabase PostgreSQL baseline — 13 جدولاً محفوظة كبحث فقط

`profiles`, `devices`, `scan_sessions`, `scan_category_summaries`, `cleanup_actions`, `storage_snapshots`, `app_inventory_summaries`, `privacy_summaries`, `battery_snapshots`, `ai_recommendations`, `reports`, `user_settings`, `entitlements`.

- SQL الكامل/Policies محفوظ داخل Appendix A في مستند الـMaster، وليس من الآمن افتراض أن هذه الـ13 جداول موجودة بالفعل داخل مشروع Supabase الحي.
- **ثغرة تصميمية معلومة يجب إصلاحها قبل migration:** في SQL البحثي `user_settings.cloud_history_enabled DEFAULT true`، بينما القرار النهائي Cloud opt-in ويستلزم **DEFAULT false للمستخدمين الجدد** دون العبث بموافقات المستخدمين الحاليين.
- `user_id=auth.uid()` وحدها لا تمنع ربط FK بجهاز أو جلسة مستخدم آخر؛ يلزم composite FK أو server ownership checks واختبار JWT من حسابين A/B.
- `entitlements` للقراءة من المستخدم وللتعديل من backend الموثوق فقط؛ لا Client Pro flag. تأمين GRANT/REVOKE/RLS، policy idempotency، indexes، size-limited JSONB، retention/erasure، وعدم تخزين raw URI/files/hash/candidate lists.
- أوامر SQL المرجعية **لا تنفّذ على Production قبل مراجعة التوافق والمهاجرات ونسخة Staging**. الحالة `SQL_RESEARCH_REFERENCE` وليس `RLS_VERIFIED` أو `CLOUD_DEPLOYED`.

### 6.4 حدود Android وGoogle Play التي لا يجوز تجاوزها بوعود

| القدرة | الآلية الشرعية | المخاطر/حدود التشغيل |
|---|---|---|
| Media inventory | `MediaStore` + grants بالنسخة المناسبة | Android 14+ photo selection قد تكون جزئية |
| Document/folder tree | SAF + user-selected URI grants | لا افتراض صلاحية `/Android/data` أو root/Downloads المحظور |
| Trash/Restore Media | API 30+ `MediaStore.createTrashRequest` + OS prompt | Trash لا يساوي reclaimed bytes فورًا |
| Delete media | API 30+ `createDeleteRequest` | لا حذف صامت، لا نجاح بلا re-query |
| Other app cache | ممنوع الوصول إلى private cache لغير التطبيق في الوضع العادي | لا fake “clean all apps” |
| Installed apps | `PackageManager` filtered visibility | لا claim جرد كامل دون grant وسياسة `QUERY_ALL_PACKAGES` |
| Usage | `UsageStatsManager` بعد User Settings grant | history incomplete/denied تُعرض بصدق |
| Battery/Thermal | Android battery + thermal observation | لا ادعاء cool/boost/saved battery حقيقي دون قياس |
| App freeze/clone | managed-device/authorized advanced mode فقط لو متاح وموافق | لا force-stop/cloning وهمي على Consumer Android |
| External storage broad | `MANAGE_EXTERNAL_STORAGE` core-use/policy qualifying | ليس صلاحية لبيانات apps الخاصة ولا موافقة Play تلقائية |
| Background tasks | WorkManager / permitted foreground service | schedules ليست exact-time ما لم تجيزها المنصة |

**SDK baseline المذكور في وثيقة Auth (ليس قراءة مباشرة Gradle الآن):** package `com.knoux.ai`, `minSdk=26`, `targetSdk=36`. يلزم إعادة فتح ملفات Gradle وتأكيد build variants وسياسة Play الحالية قبل release.

## 7. المصادقة والربط الخارجي — Google / Supabase / Passkeys / Biometrics

### 7.1 المشروع والمتغيرات العامة المنقولة من سجلات سابقة

| عنصر | القيمة **المبلّغ عنها** | مستوى الإثبات الحالي |
|---|---|---|
| Android `applicationId` | `com.knoux.ai` | مصدر سابق؛ لم يُعد فحص Gradle الحالي |
| Google Cloud Project | `fiery-script-514604-n6` | مذكور في تسليم سابق؛ لم يُفتح Google Cloud حاليًا |
| Supabase Project Ref | `tduofkizwkjtqcbklfah` | مذكور، وليس إثباتًا لحالة Auth config الآن |
| Supabase project URL | `https://tduofkizwkjtqcbklfah.supabase.co` | عنوان عام مبلّغ عنه؛ لم يُختبر حسابيًا الآن |
| Supabase Google callback | `https://tduofkizwkjtqcbklfah.supabase.co/auth/v1/callback` | قيمة التهيئة المستهدفة، تحتاج مقابلتها بالمزوّد |
| Debug signing SHA-1 reported | `C0:58:EA:53:09:9B:1D:7F:C5:08:F9:39:83:91:F9:D5:B9:22:24:2C` | Debug فقط، ليست Release/Play fingerprint |

**حماية أسرار:** لا يُدرج OAuth Client Secret أو Management Token أو `service_role` أو refresh token أو private key أو JWT أو keystore password في ملف مصادر المشروع أو Git. بعض `Downloads/*.json` تحتوي بيانات سرية؛ **لا ترفعها إلى Sources**. ملف `Supabase Access Tokens.txt` الخاص بالمستخدم يُعامل كسر محمي، لا مادة ملخص منشورة.

### 7.2 ثلاثة Google OAuth Clients — نتيجة فحص سابقة فقط

1. **Android client:** ملف JSON محلي باسمه الذي يبدأ `client_secret_966864797292-pbd1b3...json`، من الطبيعي ألا يحوي `client_secret` إذا كان Android client.
2. **Web principal / Supabase:** الملف `client_secret_966864797292-6gqjaebu...json`؛ سابقًا وُصف بأنه web client الأساسي الذي يحتوي Authorized Redirect URI للـcallback.
3. **Web additional / server:** الملف `client_secret_966864797292-tab00fjmp...json`؛ عميل ويب إضافي، لا يُخلط Secret الخاص به مع الأساسي.

**حالة الملفات:** ورد أنها نُقلت إلى `C:\Users\[LOCAL_USER]\Documents\KNOUX_AI_AUTH_PRIVATE_20261113` مع ACL وSHA-256 سليم، وتكوين:
`RUN_APPLY_GOOGLE_AUTH.cmd`, `02_APPLY_SUPABASE_GOOGLE.ps1`, `KNOUX_AI_PUBLIC_OAUTH_HANDOFF.json`, `README_SAFE_GOOGLE_AUTH.txt`, `05_FINAL_VERIFY.ps1`.
**كل هذا `REPORTED_IMPLEMENTED`**؛ لا يمكن إثبات استمرار وجودها أو سلامة ACL/Hash إلا عند فتح الجهاز. نجاح `RUN_APPLY_...` محليًا لا يثبت حفظ إعدادات Supabase عن بعد.

### 7.3 Google Sign-In — المطلوب والإشكال المفتوح

- Google Credential Manager/native ID Token يحدد **Web Client ID audience** الصحيح، ويُرسل الـID token إلى backend/auth exchange لإصدار جلسة Supabase/الخادم حقيقية؛ يجب التأكد من nonce/cancellation/expired token/errors.
- Supabase Auth → Providers → Google: تفعيل Google بالـWeb Client الأساسي الصحيح، وإضافة IDs أخرى فقط حسب إعدادات موفّر المشروع، عدم خلط secrets. `Skip nonce checks = OFF` (في التوجيه السابق)، `Allow users without an email = OFF`؛ يراجع إصدار الواجهة وSDK قبل التطبيق.
- حالة التطبيق الفعلي لـGoogle Provider على المشروع المذكور: **`EXTERNAL_NOT_VERIFIED / PENDING`**. ورد قبل ذلك أن قراءة بعض القيم أمكنَت لكن التعديل فشل/لم يتم بغياب access إداري ملائم.
- **Gate قبول:** provider config read-back من الخادم + اختبار Android physical sign-in باستخدام الحساب الحقيقي بإذن المستخدم، جلسة مصدق عليها، refresh/signout/cancel/retry، ولا تسريب رموز في logs.

### 7.4 Passkeys — لا تتساوى مع البصمة

- Android Credential Manager في الإصدارات المدعومة، وWebAuthn Relying Party/domain HTTPS، challenge من الخادم يُولد ويتحقق ويستهلك مرة واحدة، origin/RP ID ثابت، ربط user حقيقي، credential public key فقط يُخزَّن خادميًا.
- `/.well-known/assetlinks.json` يحتاج SHA-256 لشهادات Android الرسمية وHTTPS؛ Release SHA/Play SHA مستقلان عن Debug SHA-1.
- API 26–27: مسار fallback غير مضلل لطرق الدخول المدعومة. لا ادعاء Passkey متاحة لكل Android API.
- واجهات Supabase Passkeys التجريبية في بعض النسخ تحتاج فحص SDK/version pin وserver RP وقبول مطابق لبيئة Android؛ لا تفترض أن وجود button يعني توفر RP.
- **Gate:** enroll بعد login محقق، sign-out/login with passkey، revoked/expired challenge، lost device/recovery، API matrix، domain association verified.

### 7.5 BiometricPrompt — local unlock فقط

- بصمة الجهاز تثبت حضور صاحب الجهاز لتفتح **جلسة خادم قائمة وصالحة** أو تؤكد عملية حساسة؛ ليست تسجيل دخول خادمي مستقلًا ولا طريقة لكسر Pro.
- Android BiometricPrompt + Android Keystore، تعامل مع cancel/lockout/no enrollment/session expiry/revocation، وعدم تخزين tokens مكشوفة.
- لا تضخم أيقونة البصمة في التصميم ولا تنشئ Login موازية.

### 7.6 OTP / Guest / Sessions

- OTP من خادم حقيقي بمدة صلاحية وrate limit/expiry/retry/verify؛ لا OTP hardcoded.
- Guest يظل قادرًا على الخدمات المحلية الأساسية، ولا يرى بيانات حساب آخر أو يعمل cloud sync دون consent/login.
- Session refresh/expiry/revocation/logout/profile linking cross-device يجب أن تستند إلى هوية واحدة موثقة؛ منع auto-merge اعتمادًا على تشابه البريد وحده.

## 8. أنظمة تشغيل الاختبارات، الـQA، والـGit

### 8.1 Workflow حقيقي لكل مرحلة

- ملفات حالة مستقلة `docs/workflows/mobile/Mxx/`، مع حفظ `docs/workflows/MOBILE_TASK_MANIFEST.json` و`qa-evidence/e2e/EXECUTION_LEDGER.md`, `ACCEPTANCE_MATRIX.md`, `ACTIVE_TASK.json` **إذا كانت موجودة** مع merge واعٍ للحالة.
- التسلسل: `REALITY_GATE → ACCEPTANCE_DESIGN → IMPLEMENT_VERTICAL_SLICE → COMPILE_UNIT → DEVICE_RUNTIME → INDEPENDENT_REVIEW → BOUNDED_REPAIR_LOOP → SAFE_INTEGRATION → STORAGE_RECLAIM → AUTO_ADVANCE`.
- State machine المقصود: `QUEUED → AUDITING → IMPLEMENTING → UNIT_TESTING → DEVICE_TESTING → INDEPENDENT_REVIEW → INTEGRATION_REVIEW → VERIFIED_LOCAL → PUSHED_VERIFIED_REMOTE → RELEASE_GATE`، مع حالات توقف `BLOCKED_EXTERNAL`, `BLOCKED_PLATFORM_SCOPE`, `FAILED_VERIFICATION`, `NEEDS_USER_CONSENT`, `UNCOMMITTED_UNIQUE_WORK`.
- لا يكفي إنشاء JSON / YAML اسمها Workflow؛ يجب أن تسجل العمليات الفعلية وأوامرها ونتائجها، أو توضيح أن التنفيذ sequential self-reviewed وليس agent خارجي.

### 8.2 Evidence المطلوب لكل `Mxx`

1. baseline `pwd`, `git rev-parse HEAD`, `remote main` SHA, branch/status/untracked/worktrees/stash/reflog، مالك ملف عند وجود Agent موازي.
2. خريطة routes، screenshot قبل وبعد عند نفس دقة الجهاز، تطابق مراجع approved، RTL/LTR وfont scale وmotion/accessibility.
3. `./gradlew` commands من المشروع الفعلي، exit codes، JUnit XML pass/fail/skip، compile وlint وDB migrations؛ عدم اخفاء skipped tests.
4. runtime: device model/API، install/cold start، end-to-end navigation، permission prompt وOS consent الحقيقي، screenshots/video/logcat sanitized، tests مع synthetic fixtures فقط للعمليات الحذف.
5. provider-before/after re-query، item count والـURI المحمي، journal وmeasured values مع source/timestamp.
6. Reviewer verdict مستقل عند توفره أو self-review صريح، findings+repairs، actual source SHA.
7. audited diff، local commit SHA، push، remote SHA matching، CI state، release signing evidence؛ `BUILD_PASS` وحده لا يكفي.
8. disk before/after measured bytes، paths cleaned بدقة إن حدث، ولا تلمس ملفات assets/QA/project originals أو .git.

### 8.3 Acceptance matrix

| نطاق الفحص | السيناريوهات الإجبارية | الحالة وقت إعداد هذا الملف |
|---|---|---|
| Permissions | denied/granted/partial/revoked/resume/API26-36 where feasible | `RUNTIME_NOT_VERIFIED` |
| Storage/Files | selected URI, MediaStore, SAF, Trash/Delete with confirmation, restored/re-query | `RUNTIME_NOT_VERIFIED` |
| Duplicates | streamed SHA, same-name false positive, Keeper, safe batching, restore | `RUNTIME_NOT_VERIFIED` |
| Privacy | findings evidence, revocation, vault AEAD/recovery if advertised | `RUNTIME_NOT_VERIFIED` |
| Apps | visibility/usage scopes, OS uninstall, no unsupported force-stop | `RUNTIME_NOT_VERIFIED` |
| Battery | measured battery/thermal, not fake boost, notification thresholds | `RUNTIME_NOT_VERIFIED` |
| Scan/Clean | real module states, cancel, process death, synthetic fixture deletion only | `RUNTIME_NOT_VERIFIED` |
| Reports/AI | analytics from journal, export data, explanation+before/after | `RUNTIME_NOT_VERIFIED` |
| Auth | Google/OTP/passkey/biometric/guest, no false signed-in | `EXTERNAL_NOT_VERIFIED` |
| Pro | 50 IDs + gating + Play verify/revoke/trial server | `EXTERNAL_NOT_VERIFIED` |
| UI | 20 refs + 2 onboarding + splash + responsive + RTL + audio | `OWNER_APPROVED_DESIGN`, **runtime not verified here** |
| Distribution | release keystore, signed APK/AAB, Play policy, privacy disclosures | `LOCAL_NOT_INSPECTED` |

**Physical-device destructive QA:** test only UUID-namespaced synthetic files created explicitly for the test; re-check source owner and exact target before `Delete`; no cleanup on user photos/files or other applications. Teardown removes generated fixture paths only.

### 8.4 سياسة الدمج والنظافة

- main هو الفرع المدمج للمنتج، **ليس مخزن كل الشغل الفريد غير المدمج**؛ احصر كل branch/worktree/stash/reflog/untracked/agent ownership قبل قرار discard.
- لا تستخدم `git reset --hard`, `git clean`, `force push`, تغيير تاريخ Git أو recursive deletion غير محقق. لا overwrite لعميل يعمل بالتوازي.
- staged diff انتقائي؛ اختبارات ودليل موجودان **قبل** merge؛ بعد push اعد قراءة remote SHA + CI. الإغلاق الإداري يفصل: `LOCAL_COMMITTED_NOT_PUSHED`, `PUSHED_NOT_TESTED`, `CI_FAIL`, `MERGED_NOT_RELEASED`, `RELEASE_ACCEPTED`.
- تحرير مساحة الهاتف/الكمبيوتر من generated/build intermediates المؤكدة القابلة للإعادة وبعد حماية الأصول، مع تقرير bytes reclaimed؛ صفر مسح أفضل من خسارة عمل فريد.

## 9. المشاكل المفتوحة والـRisks — P0/P1/P2

| ID | الأولوية | المشكلة / الفجوة | الدليل المتاح | الإجراء الآمن الذي يغلقها |
|---|---|---|---|---|
| GAP-01 | P0 | لا baseline حي للموبايل `HEAD/status` أو جرد العمل غير المدمج | لم يُفحص `/root/workspace/KNOUX-AI` | Read-only inventory + identify agents + preserve unique |
| GAP-02 | P0 | 10 service engines و50 Pro غير متحققين كرحلات مستخدم كاملة | التعاقد موجود؛ device/tests غير مقروءة | M00/M01 ثم كل M مع fixture/re-query/test/evidence |
| GAP-03 | P0 | Google Provider في Supabase لا توجد شهادة حفظ config + Android sign-in | تقارير OAuth local فقط | secure admin setup + read-back + genuine phone sign-in |
| GAP-04 | P0 | Passkeys تحتاج RP/assetlinks/signing + اختبار حقيقي | مطلوب معماريًا فقط | server challenge verify + domain/assetlinks + device QA |
| GAP-05 | P0 | بيومتريك/OTP/Guest sessions لا دليل runtime حالي | UI approved غير كاف | session invalidation/lockout/OTP E2E |
| GAP-06 | P0 | Cloud SQL Research غير منشور/غير مدقق RLS، privacy-default conflict | 13-table baseline في appendix | stage migration + default=false + JWT A/B RLS tests |
| GAP-07 | P0 | Pro billing/trials/entitlements قد تكون مجرد catalog | 50 IDs محددة، لا store backend proof | actual Billing library + backend verify + revocation tests |
| GAP-08 | P1 | لقطتا Ecosystem/Founder لم تُقارنَا بScreenshots تشغيل حي | مراجع 841×1870 معتمدة | capture real screenshots/route tests |
| GAP-09 | P1 | MP3 Salawat مذكور دون فحص bytes/licence/per-device playback | URL + owner statement فقط | verified download, duration/codec/hash, offline test |
| GAP-10 | P1 | الـSplash smile/click audio event ربما مفقود أو غير متزامن | storyboard requirements | capture real launch video + event trace |
| GAP-11 | P1 | قيود Android/Play لحذف files/cache/apps قد تجعل ميزات موهومة | policy-aware contract | capability router، honest UI、 consent، per-API matrix |
| GAP-12 | P1 | لا GitHub Actions runs مرصودة؛ لا CI green مثبت | GitHub API current query returned 0 | identify actual CI provider; build/test evidence by SHA |
| GAP-13 | P1 | مشروع PC/main البعيد قد يتقدم مستقلاً عن OpenCode mobile | remote latest commit `docs(pc02)` | compare branches/SHA safely, **no cross-workspace destructive sync** |
| GAP-14 | P2 | أصل ملفات الاعتماد والـACL والـSHA في Windows غير مفحوص | handoff reports | check private directory locally without printing secret |
| GAP-15 | P2 | مساحة التخزين وGradle/QA assets بحاجة لجرد لا مسح تلقائي | historical low-space concerns | identify safe generated dirs after preservation |
| GAP-16 | P2 | الاختبارات على Android 26–36 وDay/RTL/A11y غير مقروءة | plan only | source-SHA-specific matrix, log gaps honestly |

**المخاطر الحرجة:** فقدان untracked work؛ عرض نجاح مستند للـUI وحدها؛ ربط Google بمشروع خاطئ؛ تعطيل nonce/RLS؛ إعطاء Pro صلاحيات جهاز غير موجودة؛ حذف بيانات مستخدم؛ نشر secrets/production signing؛ والخلط بين main الكمبيوتر وOpenCode الموبايل.

## 10. أقصر خطة استئناف صحيحة — SAFE NEXT ACTION

**Reality → Risks → Unique Work → Gaps → Safe Next Action**:

1. افتح **موبايل فقط** `/root/workspace/KNOUX-AI`: اقرأ `pwd`, `HEAD`, `remote`, `git status -uall`, `worktrees`, `stash`, `reflog`, agents, source files, available disk/RAM and current build reports **read-only أولًا**.
2. أنشئ `docs/qa/REALITY_SNAPSHOT_20261113.md` أو حدّث ملفًا قائمًا، وميّز `reported` vs `confirmed`. لا تستبدل workflow state موجودة بخريطة فارغة.
3. طابق ملفات المشروع مع **20 references + 2 approved onboarding PNGs + Splash + auth routes**، وحدد ما هو منفذ بالفعل بدل خلق duplicates.
4. نفّذ **M00** foundation gaps فقط، مع Room/capability router/migrations وtests، ثم **M01 Storage Analyzer** على بيانات synthetic. حفظ الـproof بعد كل vertical slice.
5. افصل external-auth blocker: افحص Supabase project ref الحقيقي وProvider configuration بـread-back بعد auth admin action، ثم Google physical phone sign-in، ثم Passkey RP/DAL، ثم biometric session unlock وOTP.
6. استمر M02→M15 مع feature registry (50 IDs)، cloud opt-in staging RLS وBilling backend، ولا تسجل COMPLETE دون UI/OS action/DB/runtime/CI evidence.
7. افحص and reconcile remote `main` at its then-current SHA. Push/merge **fast-forward and only safe**؛ لا حذف لعمل موازٍ أو مسارات Windows. Recovery plan قبل cleanup.
8. عند آخر مرحلة قيّم release sign/Play policies+privacy+security، وادعم الادعاء بمستندات نشر حقيقية؛ خلاف ذلك `NOT_RELEASED`.

### 10.1 قالب حالة لأي وكيل يستلم هذا المرجع

```text
TASK ID / WORKSPACE / TIMESTAMP / AGENT OWNER
LOCAL HEAD / REMOTE MAIN SHA / BRANCH / DIRTY & UNIQUE WORK
DESIGN: approved ref IDs; before/after screenshot comparison; actual app route
IMPLEMENTATION: files changed; real adapter/DAO/provider; capability gates
TESTS: exact commands; exit; JUnit pass/fail/skip; SHA; Android API/device
USER ACTION: fixture ID; OS consent; source-before/after; Room journal
EXTERNAL: Supabase/Google/Play settings read-back; no secrets in logs
GIT: local commit vs push vs remote-verified vs CI; no merged-by-claim
RISKS/BLOCKERS: what failed, supported alternatives
NEXT SAFE ACTION: exact first executable action, no planning-only exit
```

## 11. تاريخ القرارات والوضع حسب اليوم

| التاريخ | الحدث | الحكم الصحيح |
|---|---|---|
| 2026-11-10 | مسودة بحث Supabase SQL 13 tables + Android full execution research | مرجع تصميم، ليست migration مثبتة |
| 2026-11-11 | نشاط OpenCode/PRoot/تصميمات/محاولات build | تقارير تاريخية؛ تحتاج إعادة فحص SHA |
| 2026-11-12 | Master أصلي 16 مرحلة + 20 صورة معتمدة + workflow/QA | اتفاق هندسي ملزم، ليس E2E PASS |
| 2026-11-13 | Master نهائي يحفظ Appendices A/B/C ويجيز كل الخدمات | نطاق تنفيذ كامل مع freeze للتصميم |
| 2026-11-13 | Ecosystem/Founder + Salawat user-approved | المتطلبات والأصول مسماة؛ bytes/runtime غير مثبتين هنا |
| 2026-11-13 | ثلاثة OAuth Clients وSupabase project ref بملف handoff | فحص سابق لملفات؛ تحديث Auth/live sign-in غير مثبت |
| 2026-11-13 | GitHub main قرئ مباشرة SHA `b8577cbc…` | مثبت commit بعيد فقط؛ ليس قبول Android |

## 12. مصادر الأدلة والمستندات المرجعية

**المراجع التي قرأتها وأدخلتها في هذا الملف:**

- `KNOUX_AI_MOBILE_OPENCODE_SUPREME_FULL_FUNCTIONAL_E2E_MASTER_20261113.md` — 282,313 bytes؛ SHA-256 `7b81664268fefdd28065ff7c2cd1e18236c65a9a8e9ef4cd4ef9e41d5aee7028`.
- `KNOUX_AI_MOBILE_OPENCODE_TWO_SCREENS_SALAWAT_DIRECT_MP3_FINAL_20261113.txt` — 22,573 bytes؛ SHA-256 `fa9cdb1eee6fd91958ce7bd02d8334a8916a07938ebf4afbbfbe6bdbbcfb8671`.
- `00_MASTER_AUTH_BOTH_AGENTS.md` — 11,871 bytes؛ SHA-256 `3d73155354403b366252230553d6f0b8e6ddfa39d8c742586a30ae788842907a`.
- `KNOUX_COMPLETE_CONVERSATION_HANDOFF_2026-11-13.md` — 25,690 bytes؛ SHA-256 `58998c1d583b3803c9e2af8165d7550cd81773e3343410c408b5c4c41f366c20`.

**قيمة SHA-256 أعلاه** تحقق سلامة **نسخ المراجع التي تمت قراءتها هنا** فقط؛ ليست إثبات تطابق مع نسخة هاتف أو GitHub، وليست SHA للـAPK أو MP3.

- Master (2026-11-13): الأقسام I–IX؛ Appendix A نسخة محفوظة من ماستر 2026-11-12 وفيه SQL كامل؛ Appendix B لقطة التوجيه المعتمد لواجهتي Onboarding والصوت؛ Appendix C عقد 20 مرجعًا والـSplash. المرجع أوسع بكثير من هذا الملخص وهو مصدر التفاصيل التنفيذية الدقيقة.
- Auth handoff: نموذج Google+Passkey+Biometric، فصل الهاتف/الكمبيوتر، صدق الجلسات والتوقيع، وعدم مزج الأسرار.
- Conversation handoff: تصنيف عمل OAuth المحلي مقابل Supabase البعيد، وفيه أيضًا مسار إعلانات WebPilot/GrabFlow/Retaj؛ **هذا المسار الإعلاني خارج scope تنفيذ تطبيق KNOUX AI Mobile** ولا ينبغي أن يلوث M00–M15.
- رابط GitHub main: https://github.com/daynightae-cmyk/KNOUX-AI/tree/main
- Android MediaStore: https://developer.android.com/reference/android/provider/MediaStore
- Android Passkeys: https://developer.android.com/identity/passkeys/sign-in-with-passkeys
- Billing: https://developer.android.com/google/play/billing/integrate
- Android storage policy: https://support.google.com/googleplay/android-developer/answer/10467955

**ما ليس موجودًا داخل هذا الملف:** صور الـPNG الأصلية، MP3 bytes، شهادات التوقيع، Login tokens، سورس Kotlin كامل، SQL ملف migration مطبق، APK، Git local runtime screenshots أو فيديوهات حقيقية، تقارير JUnit XML. عدم وجودها هنا **لا يثبت أنها غير موجودة في مشروع المالك**؛ يعني فقط أنها لم تُفحص أو تُرفق بشكل مثبت أثناء إعداد مرجع المصدر هذا.

## 13. الحكم التنفيذي النهائي

- **موثق ومُعتمد كقرارات:** هوية المنتج، مراجع التصميم، 20 خريطة واجهات، فصل المشروعين، Splash/Onboarding/Founder contract، 16 مراحل، 10 خدمات، 50 Pro IDs، قواعد أمن/خصوصية/QA.
- **مؤكد بفحص مباشر محدود:** مستودع GitHub موجود، `main` كان عند `b8577cbc7e798cfaf23a0f44650941be7ae332d0`، لا PRs مفتوحة في الاستعلام ولا GitHub Actions runs ظاهرة، وأسماء مسارات جذرية موجودة.
- **مبلّغ عن تنفيذه لكن لم يُعد تدقيقه:** إنهاء visual design للموبايل، وجود طرق المصادقة المرئية، نقل OAuth JSON للملف الخاص، اختبارات محلية لبعض سكريبتات الإعداد، أعمال سابقة داخل Phone/PC.
- **غير مثبت بعد في هذا الفحص:** تطبيق Supabase Google Provider والـlogin end-to-end، Passkeys / biometric session proof، تحقّق الصوت على الجهاز، كل خدمة متصلة بمصادر Android الفعلية، 50 Pro functions بحماية purchases، RLS staged/deployed، device test matrix، release sign/deploy.
- **قرار الاستئناف:** لا rewrite من الصفر؛ ابدأ بـGit/unique-work/reality audit، حدّد الفجوات فقط، ثم تنفيذ واختبار ودمج تدريجي مع حفظ الأدلة. **الحالة العامة: IN PROGRESS / RELEASE NOT VERIFIED.**

---

> **ملاحظة مهمة لأي نموذج AI يسترجع هذا المصدر:** لا تعتبر «ورد في التقرير أنه تم» مساوية لـ«تم واختُبر الآن». استخدم هذا الملف كذاكرة قرارات وعقد قبول، ثم اجلب دائمًا الأدلة الجديدة من الـworkspace والمزوّد الخارجي عند اتخاذ قرار كتابة/حذف/دمج/إطلاق. هذه هي الطريقة الوحيدة للحفاظ على المنتج الحقيقي بلا Fake Closure.
