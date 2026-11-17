# KNOUX AI — الملف المرجعي الشامل للمشروع | Project Source of Truth & Evidence Handoff

> **التاريخ المرجعي:** الجمعة 13 نوفمبر 2026، الساعة 11:51 تقريبًا بتوقيت الإمارات (UTC+04:00).  
> **نوع الوثيقة:** ملخص شامل للقرارات والتصميمات والمعمارية والتنفيذ والربط والخدمات والأدلة والفجوات، صالح للإضافة إلى «مصادر المشروع» في ChatGPT أو التسليم إلى وكيل تطوير جديد.  
> **نطاق الملخص:** منظومة **KNOUX / KNOUX AI**، مع فصل صارم بين نسخة **Windows PC** ونسخة **Android Mobile OpenCode** المستقلة.  
> **قاعدة القراءة:** الحالة المذكورة لقطة زمنية؛ كل حالة Git/CI/runtime/سيرفر متغيرة يجب إعادة فحصها قبل أي تعديل.  
> **الخصوصية:** لا يحتوي هذا الملف على رقم تعريف الهاتف الحقيقي، عنوان Wireless ADB، كلمات مرور، API keys، توكنات أو Client Secrets.  
> **التنفيذ أثناء إعداد هذا الملخص:** فحص قراءة فقط للكمبيوتر؛ لم تُعدل ملفات المشروع العامل، ولم تُطلق اختبارات أو حذف ملفات في هاتف المستخدم.

---

## 1. الملخص التنفيذي — ماذا بنينا وأين وصلنا؟

**KNOUX** هي العلامة/الشركة الأم، و**KNOUX AI** هو المنتج. الفكرة ليست شاشة Cleaning استعراضية، بل تطبيق Android هندسي متميز بصريًا يقدم مسارات متكاملة لإدارة التخزين، التكرارات، بقايا التطبيقات، الخصوصية، مدير التطبيقات، البطارية، الفحص العميق، التنظيف الذكي، التقارير، وتوصيات الذكاء الاصطناعي. واجهاته المعتمدة سينمائية، لكن **أي زر أو قيمة أو خدمة موجّهة للعميل يجب أن ترتبط ببيانات/إجراءات حقيقية حسب حدود Android وموافقات المستخدم**. لا يُسمح بخدمات صورية، أو أرقام محاكاة تُعرض على أنها حقيقية، أو ضمان نجاح عملية حذف اعتمادًا على انتهاء Animation فقط.

### أحدث الواقع المثبت للكمبيوتر — فحص مباشر 13 نوفمبر 2026، 11:48–11:51 UTC+04

| البند | الحالة المسجلة والتحقق |
|---|---|
| المستودع الرسمي في بيئة Windows | `D:\KNOUX-AI` — الفرع `main` |
| HEAD المحلي | `b8577cbc7e798cfaf23a0f44650941be7ae332d0` |
| GitHub `origin/main` | **نفس SHA من استعلام `git ls-remote` مباشر**؛ التحقق كان ناجحًا |
| PC00 — Foundation | `VERIFIED` في الـManifest، مرجعه `722af86...`؛ يوجد شغل واختبارات مثبتة ضمن نطاقها، وليس إثبات Release عام |
| PC01 — Smart Clean | `VERIFIED` في الـManifest، مرجعه `722af86...`؛ اختبارات جهاز فعلي وJVM موثقة مع قيود مذكورة لاحقًا |
| PC02 — Duplicate Finder | `VERIFIED` في الـManifest، كود مدمج في `59806cad...` وتوثيق/انتقال إلى PC03 في `b8577cb...`؛ تحفظ مستقل على بعض سيناريوهات السلامة لم يُغلق بالأدلة المحددة |
| PC03 — App Residue | **في التنفيذ والاختبار محليًا، غير مدمج في آخر SHA المثبت**، رغم أن Manifest كان لا يزال `QUEUED`؛ الحالة الدفترية متأخرة عن واقع ملفات العمل |
| PC04–PC13 | `QUEUED` في الـManifest؛ لا تعتبر الشاشات الموجودة إثباتًا لاستكمال المنظومة الخدمية |
| آخر تشغيل كامل لـPC03 معروف | `44` اختبار Android؛ **4 إخفاقات** في سجل `49-full-suite.txt` (11:40)؛ لا يجوز إعلان PASS كامل. بدء إعادة محاولة `50-...` بدون تقرير نهائي متاح وقت المراجعة |
| الهاتف التجريبي | جهاز itel / Android 15 API 35 ظهر متصل عبر ADB Wi-Fi، في وقت الفحص تم العثور على جهاز مصرح واحد؛ لا تُنشر هويته الشبكية أو الرقم التسلسلي |
| مساحة قسم `D:` | حوالي **2.44 GB** وقت الفحص؛ متغير ومتأثر بالبناء الجاري |
| الوكيل | OpenCode و`opencode-cli` ظهرا يعملان؛ لا يُتدخل في ملفات الوكيل أو يُنفّذ Build موازي أثناء شغله |
| النشر النهائي / Play Store | **غير مثبت**؛ لا Release/Store acceptance بمجرد نجاح Debug APK |

**الحكم العام:** هناك تنفيذ فعلي متراكم، واختبارات وجلسات حقيقية، وCommits مرفوعة. المنتج **غير مكتمل Production E2E**. الاعتماد يجب أن يكون **لكل خدمة على حدة** وليس نسبة «100%» عامة.

### الحالة المرجعية للموبايل المستقل (Linux OpenCode)

- **المسار المستقل:** `/root/workspace/KNOUX-AI`؛ ممنوع خلطه مع `D:\KNOUX-AI` أو رفع تغييرات Windows إليه دون قرار صريح ومسار دمج مدروس.
- المستخدم أعلن أن **مرحلة التصميم البصري اكتملت** لهذه النسخة، ثم أصدر عقد تنفيذ شامل للوظائف الحقيقية.
- العقد الرئيسي الذي تم تجهيزه: `KNOUX_AI_MOBILE_OPENCODE_SUPREME_FULL_FUNCTIONAL_E2E_MASTER_20261113.md`، ملف أصلي موجود ضمن مخرجات المحادثة، بحوالي 2,571 سطرًا، ويشمل 16 مرحلة `M00–M15` و10 خدمات و50 ميزة Pro وملحقات الهوية/الصوت/المراجع البصرية.
- **لم يتم فحص HEAD/CI/Runtime/مخرجات OpenCode للمسار Linux مباشرة أثناء إعداد هذا الملخص**؛ لذلك «انتهى التصميم» **إقرار مالك/تكليف** لا ادعاء إثبات آلي أن كل شاشة تعمل، كما أن خدمات موبايل Linux ليست مساوية لحالة PC00–PC13 على Windows.

---

## 2. قاموس الحالات المُلزم — منع Fake Closure

| التسمية | الدليل اللازم | لا يعني |
|---|---|---|
| `PLANNED` | تصميم/Prompt/قائمة Features | لا يعني وجود كود |
| `IMPLEMENTED_UNVERIFIED` | ملفات كود حقيقية تقابل الوظيفة | لا يعني نجاح Build أو Runtime |
| `UNIT_VERIFIED` | JUnit XML حديث لنفس الشجرة: نجاح/فشل/Skip | لا يعني تشغيل Android UI أو OS dialogs |
| `DEVICE_SUBSET_VERIFIED` | Instrumentation ناجحة على الجهاز لمسارات محددة | لا يعني باقي المسارات أو اختلاف OEM/Android |
| `INDEPENDENT_REVIEW_PENDING` | مراجعة غير مكتملة أو Findings مفتوحة | لا يجوز إعلان السلامة النهائية |
| `LOCAL_COMMITTED` | SHA محلي واضح | لا يعني Push ولا تكامل المستودع الخارجي |
| `REMOTE_MAIN_VERIFIED` | الاستعلام المباشر `git ls-remote origin refs/heads/main` يساوي SHA المحلي | لا يعني CI PASS أو Release |
| `CI_VERIFIED` | حالة CI حقيقية على نفس الـSHA | لا يعني سلامة UX أو الجهاز أو إذن Android |
| `PRODUCTION_RELEASED` | توقيع ونشر وتدفق استخدام فعلي مطابق وشواهد تشغيل | لا يُستنتج من `BUILD SUCCESSFUL` |
| `BLOCKED_EXTERNAL` | نقص تصاريح، OAuth، حساب تاجر أو اشتراك أو منصة | لا يجوز اختلاق خدمة بديلة |

**قواعد جوهرية:** «اختبار skipped» ≠ PASS؛ «موافقة Android ظهرت» ≠ ملف حُذف؛ «مساحة مستردة محاسبيًا» ≠ مجموع أحجام الـURI إلا بقياس مستقل؛ «تقرير الوكيل قال DONE» ≠ حقيقة قبل معاينة الأدلة.

---

## 3. حدود المشروعين والمصادر المعتمدة

### 3.1 Windows PC — المشروع الجاري

- **Canonical:** `D:\KNOUX-AI`، مستقل تمامًا عن Linux؛ فرع `main` هو المنتج المدمج.
- **GitHub:** `https://github.com/daynightae-cmyk/KNOUX-AI`، المستودع **Public**. التزام حماية الأسرار والمعرّفات أساسي.
- مجلدات مرجعية مهمة:
  - `AGENTS.md` — توجيهات المالك والحماية وتحديثات التصريح بالاختبار وخصوصية الأجهزة.
  - `docs/workflows/PC_E2E_TASK_MANIFEST_20261112.json` — 14 مهمة PC00–PC13 ومُعتمديات وحالات. هو مرجع المهام، لكن يجب إصلاح أي تأخر فيه عن الواقع.
  - `docs/workflows/KNOUX_PC_PER_SERVICE_AUTONOMOUS_EXECUTION_20261113.md` — عقد إجراء Workflow لكل قسم.
  - `docs/workflows/KNOUX_E2E_PUBLIC_WORKFLOW_POLICY.md` — سياسة عامة منشورة.
  - `qa-evidence/e2e/EXECUTION_LEDGER.md` — سجل تقدم/أدلة append-only.
  - `qa-evidence/e2e/ACCEPTANCE_MATRIX.md` — المصفوفة المطلوبة للاعتماد.
  - `qa-evidence/e2e/pc01`, `pc02`, `pc03` — نتائج التنفيذ والاختبار (بعض الملفات محلية حساسة/غير متتبعة).
  - `docs/pro/PC_PRO_FEATURE_CATALOG_20261112.json` — كتالوج 50 ميزة/10 أقسام، كثير منها `CATALOGED` وليس صالحًا للبيع.
  - `docs/pro/PC_PRO_OWNER_FULL_FUNCTIONALITY_OVERRIDE_20261112.md` — توجيه تجاوز العرض فقط نحو الوظائف الحقيقية.
  - `prompts/e2e/PC_OPENCODE_POST_KIRO76AE_FULL_WORKFLOW_TAKEOVER_20261113.md` — تسليم لما بعد جلسة Kiro.
  - `app/src/main/java/com/knoux/ai/` و`data/` — كود Kotlin/Compose/Room.
  - `app/src/androidTest/` و`app/src/test/` و`app/schemas/com.knoux.ai.data.KnouxDatabase/` — اختبارات ومخططات Room.
- يجوز أن تكون فيه ملفات تنفيذ فريدة لم تُرفع بعد. لا تستخدم `git clean`, `reset --hard`, `force push`, إعادة كتابة Git history أو حذف Worktree دون فحص وحماية وموافقة صريحة لأي فعل مدمر.

### 3.2 Linux Mobile OpenCode — منتج/بيئة مختلفة

- **Canonical:** `/root/workspace/KNOUX-AI`؛ المستخدم شدّد على عدم لمس نسخة Windows من وكيل الموبايل.
- ملف التعليمات التنفيذي الشامل: `KNOUX_AI_MOBILE_OPENCODE_SUPREME_FULL_FUNCTIONAL_E2E_MASTER_20261113.md`، وملف تعليمات الشاشة/الصوت `KNOUX_AI_MOBILE_OPENCODE_TWO_SCREENS_SALAWAT_DIRECT_MP3_FINAL_20261113.txt`.
- **Workflow mobile:** 16 مرحلة `M00` إلى `M15`؛ لا تقارن هذه الأرقام آليًا بـPC00–PC13.
- **منع نقل حالة الاختبارات:** نتيجة `PC02` Windows لا تساوي نجاح `M02` Linux؛ كل Repo يحتاج Git/Build/Runtime خاصًا به.

### 3.3 مصادر الحالة الفعلية مقابل المصادر التاريخية

الأولوية: **(أ)** كود/manifest/سجلات حديثة مباشرة لنفس SHA، **(ب)** JUnit XML/اختبار جهاز ومخرجات OS، **(ج)** Commit وRemote SHA، **(د)** تقرير مراجعة مستقلة، **(هـ)** رسائل الوكلاء والمراجع القديمة. عند تعارض مصدر قديم مع أحدث كود، لا تنسخ حكمًا قديمًا باعتباره حالة حالية، ولا تتجاوز عيبًا جديدًا لم يُختبر.

---

## 4. هوية KNOUX المعتمدة والشخصية المؤسسية

- العلامة الأم: **KNOUX**؛ المنتج الموجه للمستخدم: **KNOUX AI**.
- المؤسس: **Eng. Sadek Elgazar** — **Founder & Software Engineer**؛ بالعربية **م. صادق الجزار**، المؤسس ومهندس البرمجيات.
- الموقع: `https://knoux.store/`.
- البريد المؤسسي المستخدم في سياق المهام: `admin@knoux.store`؛ لا يلزم نشره في كل شاشة ما لم يكن عنصر اتصال معتمدًا.
- صيغة النسبة المطلوبة في Founder: **`A Product of KNOUX`** + توقيع المؤسس، وليست KNOUX AI على أنها الشركة الأم.
- الهوية البصرية: شعار KNOUX الهندسي البنفسجي الأصلي، أبعاد ونِسب محفوظة، إزالة حواف سوداء عند وجودها بشفافية حقيقية بدون إعادة رسم أو تغيير لون الشعار؛ درجات Violet / Purple مع Cyan / Magenta وذهبي معدني محسوب لـPro/التوقيع؛ Glass / Neon / cinematic motion، لكن قابلية القراءة أولوية.
- الاسم الإنجليزي في توقيع المؤسس بأسلوب شبيه الإمضاء عند اعتماد الشاشة؛ لا تعيد إدخال اسم عربي كبير في تصميم سابق رفضه المستخدم. دعم العربية داخل التطبيق مطلوب بشكل عام.
- تحسين تجربة المستخدم يجب أن **يستخدم أصول التصميم المعتمدة**، لا يستبدلها بواجهات Material عامة أو Screenshots خام غير تفاعلية.

---

## 5. تصميم التطبيق المحمي — الأصول والرحلات التي لا يُعاد بناؤها

### 5.1 Splash / Smile / Audio

تسلسل Splash النهائي: رسم العناصر الهندسية/الرمز ثم إظهار `knoux ai` تدريجيًا، ثم **خط الابتسامة المضيء**. **بعد حدث اكتمال حركة الابتسامة الحقيقي** يبدأ الصوت مرة واحدة. رابط المصدر الذي قدمه المالك، مع إقراره بموافقة صاحب الموقع:

`https://mp3tourl.com/audio/1794520051232-3d804d0a-2f23-4c00-9879-af7542c6616f.mp3`

- الصوت المطلوب نغمة/تسجيل **«صَلِّ على محمد»**، وليس Beep أو صوت TTS يُقدّم باعتباره التسجيل الأصلي.
- إذا كان ملف MP3 موجودًا ضمن التطبيق ومُختبرًا، **لا تعِد تنزيله**؛ وإلا نزّله وتحقق من نوع MIME/المحتوى وأنه صوت صالح، وأدرجه كمورد Android offline مثل `res/raw/knoux_salawat_intro.mp3`.
- التشغيل بعد `SmileStrokeCompleted` أو Callback مؤكد، وليس تأخيرًا مفترضًا 2600 ms؛ دورة حياة سليمة، `MediaPlayer` أو المحرك الحالي، Audio Focus، الصامت وDo Not Disturb وتقليل الحركة، منع إعادة التشغيل أثناء Recompositions وتحرير الموارد.
- **الحالة:** الإرشادات جاهزة؛ إثبات وجود التسجيل داخل APK المعني وتشغيله على جهاز الموبايل Linux لم يُتحقق منه هنا.

### 5.2 Onboarding — 3 فصول فقط، مرة واحدة بعد التثبيت الأول

1. **The Awakening**: `KNOUX AI` — *Intelligence. Beautifully Engineered.* / «ذكاء صُمم بإتقان.»؛ CTA **Explore KNOUX** / «اكتشف KNOUX».
2. **The Intelligence Universe**: «منظومة ذكية واحدة» / *One Intelligent Ecosystem* — شبكية خدمات حقيقية حول نواة K. توجد صورة مرجعية عربية أصلية باسم `01_KNOUX_AI_MOBILE_ECOSYSTEM_APPROVED.png`.
3. **The Creator’s Signature**: Founder plate وتوقيع هندسي + *Engineered by Vision. Built for You.* / «رؤية هندسية. تجربة صُممت من أجلك» مع **`A Product of KNOUX`**؛ الصورة الأصلية `02_KNOUX_AI_MOBILE_FOUNDER_APPROVED.png`.

- يتبع ذلك تسجيل الدخول الحالي؛ Back/Next/Skip والمراحل/التقدم والعودة وإعادة العرض المقصودة + Persist باستخدام Preferences/DataStore؛ لا تكرر Onboarding عند كل Splash.
- **قرار التصميم الأخير:** شاشتا Ecosystem وFounder تُستكملان على المرجعيات المعتمدة، وتبقى Splash/Login/OTP/Google/Passkey/Biometric دون إعادة تصميم. المالك صرّح لاحقًا باكتمال مرحلة التصميم عمومًا على نسخة الموبايل.

### 5.3 Login/Identity/Auth (تصحيح معتمد لخطأ سابق)

- **ممنوع البصمة المركزية الضخمة نهائيًا**. الرمز البيومتري صغير واحترافي بأسفل شاشة الدخول، بجوار Google Sign-In وPasskeys في صف خدمات متوازن.
- Header وLogo لا يتداخلان مع المحتوى أو شريط الحالة؛ تصميم هادئ، قوي، متناسب مع KNOUX violet/cyan ومرجع الواجهة الرابعة التي اختارها المالك سابقًا.
- احتفظ بـLogin/Sign Up/OTP/Google/Passkeys/Biometric، والنصوص AR/EN، عدم تخمين نجاح التكامل من أزرار مرسومة.
- Biometric = مفتاح محلي لحماية session/credentials حسب السياسة، **ليس** بديلًا عن OAuth/identity backend؛ OTP الحقيقية تحتاج تحقق server-side؛ Sessions/Guest/Recover يجب أن تكون حالات صادقة.

### 5.4 عشرون مرجعًا بصريًا/وظيفيًا محميًا

هذا **فهرس 20 سطحًا** كان معتمدًا في المرجع الرئيسي، بما في ذلك المسارات الداخلية؛ الصورة ليست بديلًا عن Composable فعلي:

| ID | الصفحة | المطلوب وظيفيًا |
|---|---|---|
| 01 | Splash Logo Smile | بناء الشعار/النص/الابتسامة ومزامنة الصوت |
| 02 | Home Dashboard | ملخص الجهاز الحقيقي، CTA فحص وخدمات وتنقل |
| 03 | Privacy Sweep | تحليل قدرات/أذونات فعلية ومراجعة خطر |
| 04 | Duplicate Files Finder | مجموعات مدعومة ببصمة محتوى كاملة |
| 05 | Deep Scan | وحدات عمل حقيقية وتقدم وإلغاء |
| 06 | Reports & Insights | إحصاءات/تاريخ ومصدر معلوم |
| 07 | App Residue Cleanup | قوائم بقايا قابلة للمراجعة واشتراط الإذن |
| 08 | Storage Analyzer | تفصيل استخدام التخزين وفق النطاق المتاح |
| 09 | Battery Saver | قياسات بطارية حقيقية وتوصيات مع حدود النظام |
| 10 | AI Optimization | اقتراحات ببيانات/سبب وإجراء مصرح |
| 11 | Smart Clean Center | Preview/Select/Confirm/Execute/Verify/Journal |
| 12 | Duplicate Group Review | مقارنة + Keeper محمي + خيارات آمنة |
| 13 | Privacy Risk Inspector | تفاصيل أذونات/مخاطر مع OS settings |
| 14 | Deep Scan Summary | Findings وسجل ونتائج غير ملفقة |
| 15 | Performance Report Detail | رسوم/تاريخ/تصدير حقيقي |
| 16 | Residue Item Review | URIs مصرح بها وسبب الترشيح/الاستبعاد |
| 17 | Installed Apps Review | قائمة التطبيقات وخيارات OS المسموحة |
| 18 | Storage Breakdown | Drilldown ومجموعات/ملفات كبيرة |
| 19 | Battery Diagnostics | مؤشرات بطارية وقيود تقدير واضحة |
| 20 | AI Recommendations | أسباب وقياسات قبل/بعد وإجراءات اختيارية |

**استثناء مهم:** App Manager overview أصله مرجع مستقل `07_App_Manager.png` أو Service Worlds وفق الحزمة، ويتصل بالتفصيل 17؛ لا تلغِه لأن قائمة الـ20 لم تخصص له رقم overview منفصل. لا تختزل 20 شاشة إلى عدد أقل عند استكمال وظائف حقيقية.

### 5.5 قواعد حركة/جودة شاشة عامة

- شاشة كل خدمة لها تفرد بصري وتفاعل يليق بهويتها، مع الحفاظ على التصميم الحالي؛ الخدمة ليست Generic List فقط.
- RTL/LTR، نصوص Arabic/English، Day/Dark إذا موجودان، Display Cutouts، نظام Edge-to-Edge Android 15، Safe Insets، خطوط 100%/130%/150%، أهداف لمس مناسبة (قرابة 48dp)، TalkBack/Accessibility، Back gesture، عدم وجود تداخل Header/Footer، حالات Empty/Loading/Error/Permission Denied، موافقة OS واضحة.
- الحفاظ على Splash والشعار والموسيقى وOnboarding والتوقيع والأصول والصور المتحركة دون Regression.
- الهدف 60fps حيث تسمح الموارد وليس ادعاء معدل إطارات غير مقاس؛ استخدام reduced motion عند الإعداد.

---

## 6. الوظائف الرئيسية — قواعد مشتركة قبل تقسيم الخدمات

### 6.1 مصدر البيانات الصادق

- `StatFs` يعرض سعة/مساحة متاحة وفق مقياس الجهاز، و`MediaStore` يعرض وسائط مكشوفة بالإذن، و`DocumentsProvider/SAF` ملفات المجلدات التي اختارها المستخدم، و`PackageManager` بيانات التطبيقات التي يحق للتطبيق رؤيتها، و`BatteryManager`/batterystats/thermal APIs حسب الصلاحية، و`UsageStats` إن وافق المستخدم.
- Room يحفظ سجلات تنفيذ ورصد وعناوين/نتائج/تاريخ مع التحقق من migrations؛ DataStore للتفضيلات/الموافقة لا لتضخيم بيانات القياس. WorkManager لجدولة أعمال مناسبة ضمن حدود Background Execution الحديثة.
- كل رقم على Home/Reports/تفاصيل الخدمة يحمل مصدرًا وزمنًا ونطاقًا ووحدة وثقة/تقدير؛ تحديث ملخص Home بعد نجاح عملية فعلية، لا بمحاكاة.
- Delete/Trash/Restore الحقيقي يمر عبر Confirmation مطابق لنطاق Android، وينتهي بـ**re-query** للحالة ثم كتابتها في Journal؛ لا يعدّ `IntentSender` ناجحًا قبل جواب النظام ونتيجة مزود البيانات.

### 6.2 حدود Android المُلزمة

- App sandbox لتطبيق آخر **غير متاح** بدون صلاحيات خاصة لا يملكها تطبيق عادي؛ لا تدّع تنظيف Cache التطبيقات الأخرى بلا صلاحية حقيقية أو إلغاء تثبيتها نيابة عن النظام.
- `MediaStore.createTrashRequest`/`createDeleteRequest` وSAF grants و`DocumentsContract` تعتمد على إصدار OS وملكية URI والإذن وموافقة المستخدم.
- على Android 13/14/15 صلاحيات Photos/Video granular/selected، والتعامل مع Folders granted فقط؛ لا تطلب `MANAGE_EXTERNAL_STORAGE` بلا حاجة واضحة وتبرير Play، ولا تعد بصلاحية شاملة.
- تعطيل التطبيقات، تغيير VPN، تغيير إعدادات توفير الطاقة، الاستنساخ، «مكافحة فيروس شاملة»، أو تحسين شبكة/CPU قسريًا غالبًا مقيد؛ استخدم capability flags، deep links لإعدادات النظام، أو `UNSUPPORTED` بدلاً من واجهة كاذبة.
- الإلغاء لا يمكن تسويقه باعتباره Rollback لعملية تمت بالفعل؛ يجب توضيح كل جزء اكتمل وكل جزء لم يكتمل.
- لا ترسل قائمة ملفات أو بيانات مستخدم لذكاء سحابي من دون Consent صريح، سياسة Privacy ومحددات retention.

### 6.3 معايير كل عملية تخزين

1. اكتشاف من مصدر قابل للقراءة الآن، وتحديد URI/Owner/Size/Category/Grant.
2. مراجعة وإظهار مؤشرات الأمان قبل أي اختيار.
3. استثناء Keeper/الملفات الحساسة والموارد غير المؤهلة.
4. عرض فرق Trash مقابل Permanent Delete وتأكيد نهائي، مع Android dialog مستقل للوسائط عندما يلزم.
5. تنفيذ عمليات محكومة، أخطاء جزئية، retry آمن لا يكرر حذفًا، وIdempotency.
6. إعادة الاستعلام عن exact URI/Provider State وتثبيت النتائج في Room Journal مع Timestamp وإثبات قبل/بعد.
7. تحديث واجهات الخدمة وHome/Reports؛ بقاء سجل قابل للتدقيق.

---

## 7. الخدمات العشر — عقود التشغيل الفعلي وتفرد UI

هذه عقود المنتج المتفق عليها، **وليست إعلانًا أن تنفيذ كل واحدة مكتمل**. لكل خدمة Workflow وتحقق مستقل؛ انظر القسم 12.

### 7.1 Smart Clean — التنظيف الذكي

**مدخلات:** ملفات Cache خاصة بالتطبيق + MediaStore + SAF Granted + Exclusions + Permission state.  
**وظائف العميل:** بدء/إيقاف Scan، عرض الحجم/العدد الحقيقي ونطاق البيانات، Tabs/categories، Filter/Search/Sort، فتح Preview، اختيار/إلغاء/اختيار جماعي آمن، قائمة ملفات قبل التنفيذ، تصنيف eligible/unselectable/protected، Undo/Restore فقط حيث يدعم OS، Media Trash/Perm Delete بموافقة نظامية، History ومتابعة Retry/errors وProgress.  
**UI:** Cyber-clean stage، ضوء وGlass cards، قوائم تقدم وتأكيد قبل/بعد؛ زر التنظيف ليس Demo.  
**إثبات:** APK/Device instrumentation، إدخال ملفات اختبار صناعية UUID، مراجعة URI قبل وبعد، Room Journal ومطابقة العداد.  
**Windows PC01:** مدمج مع بعض أدلة النجاح الحقيقية على API35؛ انظر القسم 9 للتفاصيل والقيود.

### 7.2 Duplicate Finder — اكتشاف المكررات

**مدخلات:** Exact byte lengths + Streaming full SHA-256 لكل مجموعة أحجام متشابهة، Scope/Permissions/Exclusions.  
**وظائف العميل:** قائمة مجموعات متطابقة تمامًا، مقارنة مصغرات/metadata مع سبب الإثبات، Keeper محمي لا يُحدد ولا يُحذف، Batch selection الآمن، Filter/Sort، Split dry-run، Consent، Trash/Restore/Delete لكل URI حسب القدرة، Reconciliation بعد كل عملية، مراجعة تاريخ/مساحة مؤكدة. Similarity تشابه الصور **ميزة مختلفة** يجب ألا تُقدَّم كـexact hash.  
**UI:** مقارنة جانبية، شارة Original Protected، تقدير Redundant Bytes حقيقي، Bottom action bar وإجراءات مراجعة.  
**الخطر الجوهري:** نفس الملف الفيزيائي قد يظهر عبر MediaStore وSAF؛ يجب عدم تحويل alias إلى Duplicate. `physicalKey` موجود في المصدر الجديد لمنع المجموعة الكاذبة، لكن اختبار تداخل OS حقيقي واستمرارية هوية الملف بعد Persistence يحتاجان إثباتًا مستقلًا.  
**Windows PC02:** دمج وإثبات تشغيل 36/36 Android و29/29 JVM في آخر فحص متعلق بالقسم؛ ليس معناه إغلاق جميع مخاطر الحذف على كل مزوّدات Android.

### 7.3 App Residue Cleanup — بقايا التطبيقات

**مدخلات:** Cache التطبيق نفسه + ملفات مرخصة وصولها عبر MediaStore/SAF + metadata/pattern/age/ownership، دون اختراق مساحة تطبيقات أخرى.  
**وظائف العميل:** البحث/الفحص، تجميع حسب المصدر والتطبيق المرجح والنوع والقدم، Reasons & Confidence (`OWNED`, `STRONG`, `WEAK`)، اختيار فردي واضح للأهداف المشتركة، Dry Run، استثناءات، فتح/تفاصيل، طلب OS Trash/Remove حيث يمكن، Provider re-query، Audit trail/History، ملاحظات اسم الملف لا تكفي لإثبات أنه بقايا آمنة.  
**UI:** Timeline أنيق، بصمة المصدر، Risk labels، Review drawer، تفصيل العمليات والأساليب الممنوعة.  
**Windows PC03 عند 11:49:** `ResidueController`, `ResidueRepository`, `ResidueRules`, `ResidueReal.kt`, Tests, Schema v3 ظهرت محليًا، **لم تُدمج بعد**. توجد نتائج جزئية ناجحة وFull Suite فاشلة؛ لا تصنفه VERIFIED.

### 7.4 Privacy Sweep — الخصوصية

**وظائف:** جرد بيانات التطبيقات/الصلاحيات الممكن إظهارها، Risk methodology واضحة لا عشوائية، مراجعة استعمال الكاميرا/الميكروفون/الموقع وفق ما يتيحه النظام، فتح إعدادات أذونات التطبيق، حالة الحماية/تاريخ التحليل، Privacy reporting، Vault مشفر محليًا فقط عندما يكون فعليًا.  
**UI:** Permission constellation / risk inspector وفلاتر وتفسير الأسباب.  
**ممنوع:** ادعاء اعتراض جميع Trackers أو مسح بيانات تطبيقات أخرى أو منع عالمي للصلاحيات دون Android APIs/Device Owner/VPN حقيقية وتوثيق.

### 7.5 Deep Scan — الفحص العميق

**وظائف:** وحدات فحص فعلية للنطاق المتاح، Progress مبني على العمل لا عداد مزخرف، Cancellation/Pause/Retry إذا تدعمها البنية، مخرجات وتواريخ وSeverity مدعومة بواقع، Review/Remediation بموافقة المستخدم، Summary قابلة للتصدير.  
**UI:** 5 Modules ديناميكية، Scan console متقدم، نتائج مفصلة وأسباب ثقة.  
**ممنوع:** Anti-malware 100% أو Threat Removed دون Scanner مؤهل/Proof؛ `UNSUPPORTED` أو `ADVISORY` للحالات خارج قدرة التطبيق.

### 7.6 Reports & Insights — التقارير

**وظائف:** قراءة History الحقيقة من Room، Charts/time-ranges، Compare periods، Export PDF/CSV/JSON حيث متاح، Scheduled summary عبر WorkManager والقيود، اختلاف Estimated vs Verified.  
**UI:** Timeline ورسومات مقروءة ومصدر كل قيمة، حالات No Data صريحة.  
**ممنوع:** مخططات بيانات Demo في مسارات Production أو نسبة فائدة لم يتم قياسها.

### 7.7 App Manager — مدير التطبيقات

**وظائف:** Installed packages المسموح عرضها، Source/system/user labels، بحث وفرز وتصنيف، معلومات حجم/استخدام حيث API يسمح، Open/Settings/Uninstall عبر Intent OS، الإحصاءات وحدودها، Batch review بدون تجاوز موافقة نظام، Lifecycle/Events.  
**UI:** Mosaic/List + Detailed drawer + تمييز الخدمات حسب نظام Android.  
**ممنوع:** Freeze/Clone/Backup للتطبيقات الأخرى كميزة تعمل بضغطة إذا لا توجد API/صلاحية/تنفيذ فعلي؛ إظهار إعدادات النظام كإجراء مختلف بوضوح.

### 7.8 Storage Analyzer — محلل التخزين

**وظائف:** Summary حقيقي بـStatFs، Breakdown حسب مناطق متاحة من MediaStore/SAF/App cache، Drill-down، Large Files، Filters/Date/type، Open/Share/Manage، Delete/Trash بموافقة صحيحة، تغيرات زمنية محفوظة، حسابات حجم/سعة واضحة وأخطاء صلاحيات.  
**UI:** Radial rings/segment tap وحركات ذات معلومات فعلية؛ لا تعد بأن حجم جميع App data متاح.

### 7.9 Battery Saver — البطارية

**وظائف:** مستوى شحن/حالة الشحن/حرارة ضمن ما يوفره النظام، تاريخ قياسات، Charging context، battery health إن أتاح hardware/OS ذلك، Recommendations وNotifications اختيارية، فتح Settings للطاقة، thresholds قابلة للتعديل، قياس قبل/بعد حيث يمكن.  
**UI:** Thermal timeline/charging rings/diagnostics، مع تمييز `UNKNOWN` عن رقم تخميني.  
**ممنوع:** إجبار CPU أو تطبيقات أخرى على الإغلاق أو التلاعب بالطاقة بغير صلاحية.

### 7.10 AI Optimization — التحسين الذكي

**وظائف:** جمع إشارات مصرح بها، Rules/Inference قابلة للتفسير، User-confirmed actions، ML محلي/Remote اختياري بإذن، Context Gaming/Thermal/Network/Storage، سجل كل Recommendation/result والتفريق بين «مقترح» و«نفذ» و«مثبت أثره».  
**UI:** Recommendation universe أو cards ذكية، Reasons+Confidence وCTA لكل إجراء وسجل للقرارات.  
**ممنوع:** بوستات تخمينية بلا telemetry أو وعود «تسريع الجهاز 300%» من دون Benchmarks.

---

## 8. نظام Pro — 10 مجموعات، 50 Feature IDs مذكورة بالاسم

**مهم:** الكتالوج `PC_PRO_FEATURE_CATALOG_20261112.json` على Windows يضم **50 معرّفًا**، لكن وجود العنصر في JSON مع `implementation_status: CATALOGED` و`can_sell_as_is: false` لا يثبت جاهزية بيعه. الوكيل يجب أن يطابق كل Feature مع تنفيذ حقيقي + Gate + Backend/Play إذا يلزم، ويقرر `SUPPORTED / ADVISORY / EXTERNAL_BLOCKED / NOT_IMPLEMENTED` بصدق.

| المجموعة | Feature IDs الخمسة | التحفظ المنصّاتي |
|---|---|---|
| Smart Clean | `smart_clean_unlimited`, `smart_clean_scheduled`, `smart_clean_deep_analysis`, `smart_clean_priority`, `smart_clean_rollback` | جدولة ضمن WorkManager، وRollback عندما يدعم Trash/Restore فقط |
| Duplicate Finder | `dup_verified_hashing`, `dup_keeper_protection`, `dup_smart_similarity`, `dup_batch_review`, `dup_recovery_journal` | Similarity تختلف عن Exact، والKeeper محمي فعليًا |
| App Residue | `residue_own_cache`, `residue_accessible_candidates`, `residue_saf_profiles`, `residue_safe_trash`, `residue_history` | لا وصول سري إلى مجلدات تطبيقات أخرى |
| Privacy Sweep | `prv_realtime`, `prv_vault`, `prv_tracker`, `prv_report`, `prv_history` | Real-time/tracker/Vault يتطلب كل منها تنفيذ وإذنًا مستقلاً |
| Deep Scan | `scan_malware`, `scan_hidden`, `scan_schedule`, `scan_custom`, `scan_forensic` | malware/forensic ادعاءات أمنية تتطلب Engine/حدود فعلية |
| Reports | `rep_export`, `rep_history`, `rep_ai_insights`, `rep_compare`, `rep_schedule` | بيانات أصلية لا رسوم Demo |
| App Manager | `app_batch`, `app_freeze`, `app_backup`, `app_usage`, `app_clone` | Freeze/Clone غالبًا غير ممكن عموميًا على Android |
| Storage Analyzer | `sto_detailed_breakdown`, `sto_large_files`, `sto_growth_trends`, `sto_capacity_planner`, `sto_export` | التفصيل مرتبط بنطاق الإذن الفعلي |
| Battery Saver | `bat_thermal_timeline`, `bat_charge_profiles`, `bat_usage_context`, `bat_smart_alerts`, `bat_recommendations` | لا ادعاء تعقب حرارة أو صحة بطارية خارج إشارات الجهاز |
| AI Optimization | `opt_gaming`, `opt_ai_learn`, `opt_thermal`, `opt_network`, `opt_scene` | لا تغييرات نظام خفية، ولا AI «عمل» دون إثبات |

### Pro/Entitlement/Play Billing

- لا يكفي التاج الذهبي أو قيمة boolean محلية لتفعيل اشتراك مدفوع بصدق. Play Billing + Backend purchase verification + entitlement expiry/revocation وتجديد/Refund وإدارة Errors. Server لا يثق بـClient وحده.
- Developer Gate تاريخي عبر **multi-tap على لوجو الهيدر** (ذكر سابقًا 5 taps) لتشخيص المطور، لكن يجب عزله في Debug/owner QA وعدم تضمين سر دائم أو صلاحية Pro إنتاجية يمكن تجاوزها.
- Guest/Free/Pro boundary: محتوى مجاني أصيل يظل قابلًا للعمل، وميزات Pro لا «تعمل وهميًا» إذا غير مرخصة أو غير مدعومة.

---

## 9. Windows — تسلسل العمل الحقيقي، الـCommits، الاختبارات، والعيوب المفتوحة

### 9.1 تاريخ مختصر ذي صلة (مؤكد بقدر الأدلة)

| وقت/Commit | معنى التقدم |
|---|---|
| `3adc636` — 12 نوفمبر | Phase 1 final visual gate للشاشات/الدخول/OTP في نسخة الكمبيوتر (لا يثبت E2E services) |
| `c0530b6` | توثيق Visual QA/المرجع ثم البدء في الوظائف |
| جلسة Kiro `sess_76ae4392...` | كود Data/Smart Clean واختبارات فعلية؛ الجلسة ليست native workflow، وانتهت بـUsage Limit وعدم إغلاق أعمال وقتها |
| `41eb162` | نشر سياسة workflow عامة خالية من معرّفات خاصة |
| `722af86` — 13 نوفمبر 06:11 | دمج PC00/PC01 الحقيقي (Data Foundation + Smart Clean) مع اختبارات وسجلات شواهد |
| `d1b8067` — 06:13 | تسجيل أدلة الانتقال من PC01 إلى PC02 |
| `e59eb844` — 06:49 | إزالة معرّف هاتف ADB من ملفات Public الحالية وتغيير سكريبتات الاختبار لتمريره محليًا |
| `59806cad` — 10:24 | دمج Duplicate Finder مع تحسين Physical-file alias protection واختبارات |
| `b8577cbc` — 10:36 | توثيق PC02 والانتقال إلى PC03؛ **LIVE GitHub main مطابق في فحص 11:48** |

**تحذير:** حذف الرقم من أحدث HEAD لا يمحو آثاره من Git history القديم. لم يُنفذ Force Push أو إعادة كتابة تاريخ Git، لأنهما قد يعطلان عمل الوكلاء والمستودع ويتطلبان موافقة مستقلة. الرقم وحده ليس كلمة مرور ولا يُعدّ طريق اختراق مباشر، لكنه لا يُنشر احترامًا للخصوصية.

### 9.2 Kiro — ما حدث فعلاً لا ما ادعاه الوكيل

- من الأرشيف `kiro-session-sess_76ae4392-8a79-4a73-85b5-658d38915723.zip`: `960` حدثًا رئيسيًا + سجلان فرعيان، نحو `210` استدعاءات Tools.
- `agentMode=vibe`, `workflowsEnabled=false`, `status=failed`؛ هذه **ليست Native Kiro Workflow** حتى لو رتبت خطوات يدوية.
- JVM 16/16 عند نقطة موثقة؛ 8/8 من اختبارات التخزين غير الوسائط على الهاتف؛ مسار MediaStore Trash/Restore/Delete تعثر سابقًا بسبب مزود حالة/انقطاع ADB. إدخال `assumeTrue` كان يمكن أن يحول القبول إلى Skip.
- لاحقًا OpenCode نفذ اختبارات مكمّلة، فالحكم القديم على Kiro لا يُستخدم لرفض كل عمل PC01 في الوقت الحالي.
- جلسة Kiro انتهت بـ`UsageLimitReachedError` وأخطاء deserialization، وهذا فشل استمرار وكيل وليس دليل فساد كود بالضرورة.

### 9.3 PC00/PC01 — أدلة الدمج والقيود

حسب `qa-evidence/e2e/EXECUTION_LEDGER.md`، Entry 0007:
- تشغيل على Android 15/API35 حقيقي عبر Wireless ADB، ملفات اختبار صناعية UUID فقط.
- 5/5 اختبار Compose UI لـSmartCleanRealTest، 16/16 JVM؛ اختبار Storage كامل 8/9 ثم اختبار الوسائط 1/1 بعد إذن `READ_MEDIA_IMAGES` مؤقت؛ النتيجة منفصلة ولا يجوز دمجها بعبارة «9/9 بدون شروط».
- اختبار Trash→Restore→Permanent Delete للصور الصناعية مع Provider re-query وRoom history في ظروف الاختبار المسجلة. الإذن المؤقت أُلغي بعده.
- إصلاح فشل UI كان بسبب Splash/Onboarding أول تشغيل يعيد بناء Shell أثناء Toggle في Test، وليس بالضرورة عيبًا في ViewModel. Test harness تجاوز Splash بـRuleChain؛ لا يُفسَّر هذا إثباتًا لكل الرحلات البصرية.
- جرى تنفيذ Selective Commit `722af86...` ورفع إلى GitHub وتسجيل SHA. APK Debug/AndroidTest **ليسا إصدار Play موقع Production**.
- **مراجعة مفتوحة مستقلة:** الضغط الفعلي على Checkbox عبر شاشة لمس/Overlay، لا الاكتفاء بـCompose Semantics؛ الصلاحيات من الواجهة الطبيعية بعد رفض/سحب وإعادة منح؛ Visual QA بعد التغييرات.

### 9.4 PC02 — Duplicate Finder: أدلة قوية مقابل safety gaps

**المثبت:**
- كود Production في `DuplicateReal.kt`, `DuplicateRules.kt`, `DuplicateRepository.kt`, `DuplicateController.kt`، وRoom schema v2 + migration 1→2، مع tests JVM/Android.
- مخطط Duplicate يعتمد Full SHA-256، لا مقارنة أسماء/أحجام فقط؛ Keeper مصنف `PROTECTED`، والمخطط يرفض إدخاله في خطة الحذف.
- تعديلات أحدث أضافت `physicalKey` إلى FileCandidate وتمييز MediaStore vs SAF aliases، وأظهرت Unit Test `mediaAndSafAliasesOfOneFileAreNeverTwoCopies` باستخدام مدخلات synthetic؛ رسالة mixed local/media consent تغيرت لتوضح أن Cache قد يحذف قبل OS consent.
- **JUnit Android XML** عند 10:20:32: **36 اختبارًا، 0 failures/errors/skips**، وسجل `103-full-suite-alias-fix-fixed.txt` يظهر `OK (36 tests)`، وJVM **29/29** في أرقام المراجعة. أحدث APK وHashes يجب إعادة إنتاجها من نفس الشجرة لو طُلب تسليم رسمي.
- Commit `59806cad...` متبوعًا بتوثيق `b8577cb...`، وGitHub محدث.

**التحفظات التي لا يغلقها مجرد 36/36:**
1. **Overlap فعلي MediaStore + SAF:** الاختبار الموجود يستعمل `physicalKey` مشتركًا synthetic في JVM؛ لم يُثبت في التدقيق المستقل نجاح اختباره على ملف واحد حقيقي باثنين URI ممنوحين لنفس الصورة. اطلب اختبار device fixture آمن بدون حذف الملف حتى يثبت عدم إنشاء Duplicate group كاذبة.
2. **Persistence:** `DuplicateMemberEntity` في Schema v2 لم يثبت أنه يحمل `physicalKey`؛ على Reload قد تضيع علاقة هوية نفس الملف. أعد بناء العلاقة من مصدر موثوق قبل السماح بالحذف، أو Forward Room migration مثبتة (لا تعدّل v2 في مكانها إذا استُخدمت على جهاز).
3. **Unknown/null identity:** غياب `physicalKey` لا يثبت اختلاف ملفين، خصوصًا Overlapping SAF trees/مزودات مختلفة؛ الإجراء المدمر يجب أن يفشل مغلقًا في الحالات الملتبسة، مع سبب واضح.
4. **Process-death/room reconciliation:** وُجد في مراجعة سابقة ترتيب Recovery غير صحيح؛ تعديل كود لاحق نقل `recoverInterrupted` قبل `reconcileRemoved`، لكن قبول السيناريو فعليًا يحتاج اختبار Process Restart وJournal.
5. **المزيد من UX/functional QA:** Cancel أثناء mixed cache+media، Media UI callback path، Actual touch overlay، Select All/Restore affordance، المقاييس الصغيرة `<1 KiB` وPARTIAL scope reasons، Budget handling، retention/DB growth.

**التقييم:** PC02 **مدمج ومثبت بالاختبارات المحددة**، لكن «سلامة جميع حالات مسح المكررات في الإنتاج» تحتاج مراجعة سيناريوهات إضافية. لا يُلغى التقدم الحقيقي بسبب هذه الفجوات ولا تُدفن الفجوات خلف `VERIFIED`.

### 9.5 PC03 — App Residue: أحدث مهمة ناشطة عند إعداد الوثيقة

**الموجود ماديًا محليًا ولم يكن مدمجًا:**
- `app/src/main/java/com/knoux/ai/data/ResidueRules.kt` (~14 KB)
- `ResidueRepository.kt` (~20 KB)
- `ResidueController.kt` (~14.5 KB)
- `app/src/main/java/com/knoux/ai/ResidueReal.kt`
- `app/src/androidTest/java/com/knoux/ai/ResidueMigrationTest.kt`, `ResidueStorageTest.kt`, `ResidueRealScreenTest.kt`
- `app/src/test/java/com/knoux/ai/ResidueRulesTest.kt`
- `app/schemas/com.knoux.ai.data.KnouxDatabase/3.json`
- تعديلات جارية في `MainActivity.kt`, `PreviewNavigationTest.kt`, `KnouxData.kt`, `KnouxDatabase.kt`, `SettingsStore.kt`, `app/build.gradle.kts`.

**معمارية الأمان الظاهرة من المصدر:** `ResidueConfidence` = `OWNED / STRONG / WEAK`، وOnly owned app cache يدخل Bulk Selection؛ ملفات مشتركة لا تُحدد إلا بعملية مراجعة فردية واضحة. تصنيف `.trashed-`, `.pending-`, `.tmp` ... ليس ضمانًا لحذف ملف شخصي لأن الاسم وحده لا يثبت أنه بقايا آمنة.

**أدلة اختبار PC03 المتاحة حتى 11:51:**
- `40-migration.txt` ⇒ `OK (1 test)`.
- `41-storage.txt` ⇒ 2 failures من 6، بسبب Lock files/توقع محتويات غير دقيقة؛ `42-storage-retry.txt` أعاد فشلين؛ `43-storage-final.txt` ⇒ `OK (6 tests)` بعد إصلاحات لاحقة.
- `44-ui.txt` و`47-ui-liveout.txt` ⇒ UI test فشل؛ `48-ui-liveout.txt` ⇒ `OK (1 test)`.
- **`49-full-suite.txt` الساعة 11:40:** **`44` اختبارًا، `4` إخفاقات**، منها `trash must be observable before restore` حالة `PRESENT` بدل `TRASHED`، وأخطاء Compose `performScrollTo()`، و`Provider must verify exact requested state GONE` بينما النتيجة `UNKNOWN` لوسائط.
- `50-full-suite-media-granted...` بدأت الساعة 11:49، ولم يتوفر تقرير نهائي موثوق أثناء إعداد هذا الملف. لا تعتبر فشل الجلسة السابقة نجاحًا ولا تعلن 44/44 PASS حتى يصل JUnit XML حديث لنفس الشجرة.
- الـManifest **ما زال `PC03 = QUEUED` مع current_task_id PC03** وقت الفحص، على الرغم من أن الشغل موجود؛ يلزم تحديث حقيقي إلى `RUNTIME_TESTING`/حالة مناسبة من قِبل الوكيل الكاتب بعد قراءة مصدره، وليس إعادة تهيئة `QUEUED` من الصفر.
- **القبول المتبقي:** إعادة اختبارات الجهاز، سبب اختلاف provider state، منع حذف ملفات المستخدم، حالة Permission denial، UI tap/scroll، Room v2→v3 migration على قاعدة موجودة مع الحفاظ على تاريخها، فحص Android API 35، التقرير والدمج الآمن.

### 9.6 Git وDisk والخصوصية — حالة تشغيلية

- عند 11:48، آخر SHA محلي/بعيد `b8577cbc...`، لكن يوجد **عمل فريد غير متتبع** داخل PC03 وتقارير PC02 ولقطات مرئية وأرشيفات وSession state، فلا يجوز `git clean -fd`, `git reset --hard`, أو حذف ملفات غير متتبعة تلقائيًا.
- المساحة الحرة حوالي 2.44 GB، يجب تجنّب System Images جديدة أو نسخ مشاريع/Gradle caches بدون حاجة. لا تنفّذ Cleanup أثناء أعمال Gradle/Agent جارية.
- ملفات QA القديمة قد تحتوي بيانات هاتف/أسماء/صور/قاعدة بيانات. مستودع GitHub عام؛ قبل stage افحص أسرار/Serial/IP/Logcat/اسم مالك/URI، وانشر **ملخصات منقحة** فقط، ولا تنشر نسخ DB الخاصة بالمستخدم.
- سكريبتات الاختبار تعتمد على `KNOUX_TEST_DEVICE_SERIAL` كمتغير بيئة محلي أو `-Serial`، لا قيمة Hardcoded في Git.
- قيمة `e59eb844...` هي Commit تنقيح ملفات HEAD؛ Git history القديمة لم تُنقح لأن إعادة كتابته إجراء مدمر ومؤثر يحتاج موافقة.

---

## 10. التكامل مع Supabase / Google / Play / AI — قرار، تنفيذ، فجوة

### 10.1 Supabase وGoogle Auth

- المستخدم طلب سابقًا مراجعة OAuth Clients وSupabase وربط Google Sign-In وOTP/Passkeys. ذُكرت ملفات `client_secret_...json` على جهاز Windows وملف Access Tokens، لكنها **أسرار لا تُنسخ ولا تُكتب في المصادر العامة**.
- لدى المستخدم توجيهات ربط Supabase واستعداد لاستخدام الصلاحيات الموصولة، لكن **لا يوجد في السجل الحالي إثبات نهائي** أن Google OAuth Callback/Redirect URLs وSHA-1/SHA-256 وSessions وRLS تم اختبارها End-to-End على نفس Release/Preview APK.
- ملف Android Mobile Master يحفظ **SQL بحثيًا أصليًا لعدد 13 جدولًا** مع متطلبات RLS؛ هذا ليس دليل `SQL EXECUTED`, `MIGRATED`, `DEPLOYED` أو `RLS VERIFIED`.
- كل تطبيق يطلب حقول OAuth الصحيحة (Android client, Web client, signing cert, redirect URI) وفق SDK الحالي والتكوين المصرح؛ لا تقلب أسرار client إلى BuildConfig public أو تشحن Service Role Key داخل APK.
- لا تعدّ Supabase «متصل Production» إلا بعد اختبار Session create/refresh/logout + failure + RLS negative tests + JWT/verifier + logs.

### 10.2 Play Billing / Pro

- الكتالوج 50 Features موجود، وليس دليلًا على نشأة منتجات فعلية داخل Play Console أو Purchase Verification.
- لا تبيع Feature غير مدعومة، ولا تفعّل اشتراك وهمي في الواجهة، ولا تستخدم Debug developer bypass لتوليد Entitlement إنتاجية.
- يكتمل فقط حين تتطابق Product IDs، الطلب/الإلغاء/الاسترجاع، Receipt/Purchase Token validation backend، Expiration/revocation and restore purchases مع بيئة Staging/Production الخاصة بالمالك.

### 10.3 نماذج AI وخدمات الجهات الأخرى

- جرت تجارب موفرين/نماذج مثل AWS Bedrock وOpenCode وKiro؛ نجاح API latency أو اتصال Model لا يثبت تكامل AI Optimization داخل APK.
- لا تعتمد استجابة AI وحدها لإصدار أحكام حذف أو تشخيص أمني خطير؛ احتفظ بمسار Rule-based، Confidence وConsent وعدم تسريب البيانات.
- اختبار نموذج في بيئة الكمبيوتر لا يعني نجاحه من تطبيق الهاتف، ولا يُخزّن Secret API في تطبيق عميل بصورة مكشوفة.

---

## 11. فهرس مراحل الـWorkflow — PC (Windows) مقابل M (Linux mobile)

### 11.1 Windows PC00–PC13 (14 مرحلة)

| المرحلة | القسم | حالة 11:48 تقريبًا |
|---|---|---|
| PC00 | Foundation and baseline | VERIFIED في Manifest؛ مدمج `722af86` |
| PC01 | Smart Clean Full-Stack | VERIFIED في Manifest؛ مدمج `722af86` مع اختبار API35 محدد |
| PC02 | Duplicates | VERIFIED في Manifest؛ مدمج `59806cad`، توثيق `b8577cb`، تحفظات سلامة غير محسومة بالكامل |
| PC03 | App Residue | **تنفيذ/اختبارات جارية محليًا؛ Manifest متأخر QUEUED؛ 44-test run فيه 4 failures** |
| PC04 | Privacy Sweep | QUEUED |
| PC05 | Deep Scan | QUEUED |
| PC06 | Reports | QUEUED |
| PC07 | App Manager | QUEUED |
| PC08 | Storage Analyzer | QUEUED |
| PC09 | Battery Saver | QUEUED |
| PC10 | AI Optimization | QUEUED |
| PC11 | Home/Auth/Settings/Navigation | QUEUED للتكامل E2E (مع وجود عمل بصري سابق محفوظ) |
| PC12 | Verified Billing and Supabase Staging | QUEUED / يتطلب تصاريح وربط فعلي |
| PC13 | Release Acceptance | QUEUED / ليس منشورًا |

### 11.2 Linux Mobile M00–M15 (16 مرحلة)

| المرحلة | المطلوب | حالة الإثبات المستقل هنا |
|---|---|---|
| M00 | Foundation / Permissions / Room / DataStore / WorkManager | لم يُفحص Runtime Linux |
| M01 | Storage Analyzer | لم يُفحص |
| M02 | Duplicate Finder | لم يُفحص |
| M03 | App Residue | لم يُفحص |
| M04 | Privacy Sweep | لم يُفحص |
| M05 | App Manager | لم يُفحص |
| M06 | Battery | لم يُفحص |
| M07 | Deep Scan | لم يُفحص |
| M08 | Smart Clean | لم يُفحص |
| M09 | Reports | لم يُفحص |
| M10 | AI Optimization | لم يُفحص |
| M11 | Home/Navigation | التصميم أُعلن مكتملًا من المالك، Runtime غير مفحوص هنا |
| M12 | Settings/Permissions | غير مفحوص |
| M13 | Identity/Login/OTP/Passkeys/Biometric | التصميم مرجعي محفوظ، الربط E2E غير مفحوص هنا |
| M14 | Pro/Cloud/Billing | كتالوج وعقود متاحة؛ نشر Backend/Billing غير مثبت |
| M15 | Release | غير مثبت |

**لا تدمج الـManifestين ولا تعيد تسمية المراحل لإعطاء انطباع بانتقال حالة من كمبيوتر إلى موبايل.**

---

## 12. العقد التنفيذي لكل خدمة — Workflow تصنعها الأداة بطريقتها

**قرار المالك:** كل قسم تُنشأ له **Workflow تشغيل فعلية مستقلة ومستمرة**، يختار OpenCode/Kiro/وكيل التطوير طريقة orchestration التي يدعمها فعلًا (Native flow إن وُجد، controller مع implementer/reviewer إن أمكن، وإلا خطوات sequential file-backed)، لا يضيع وقتًا في محاولة Validate وصفة JSON اخترعتها أداة مختلفة. **مجرد ملف Workflow لا يساوي تشغيل Workflow.**

المراحل الملزمة لكل قسم:

1. **REALITY:** تأكيد Repo path والفرع وHEAD + origin/main live SHA، `git status -uall`, worktrees, stash, reflog، وكل ملفات/عمل فريد ووكلاء يكتبون الآن، مساحة القرص والجهاز المتاح.
2. **RISKS & UNIQUE WORK:** منع الكتابة على نفس ملفات Kotlin بالتوازي، ومنع فقد الهوية البصرية وبيانات المستخدم، ومراجعة صلاحيات وخطورة CRUD.
3. **ACCEPTANCE CONTRACT:** كل زر/Dropdown/Filter/Preview/Export/Consent/Retry/Restore/History/Pro state، بياناته الحقيقية، Backend/Android API والمخرجات/الاستثناءات.
4. **IMPLEMENT:** Vertical Slice كاملة: Repository + Controller/Domain + Room/WorkManager + UI specialized + Error handling/Capability flags.
5. **TEST CURRENT TREE:** Gradle compile/jvm + Instrumentation على الهاتف المصرح به مع Tests حديثة لا مخفية، UI actual clicks/Back/rotation/RTL، مع pass/fail/skipped وExit/Unit/Build SHA.
6. **REAL DEVICE SAFETY:** Synthetic fixtures جديدة فريدة `UUID`, exact URI/Owner/Size، حذفها وحدها إن لزم، والتأكد من حالة OS dialog/Provider قبل وبعد؛ لا تلمس بيانات شخصية/صور/تطبيقات أخرى.
7. **INDEPENDENT REVIEW:** Reviewer منفصل Read-only إن متاح، وإلا Self-audit معلن بوضوح؛ يرفض fake stats/overlays/skip أو مخاطر Keeper/Consent.
8. **BOUNDED REPAIR:** إصلاح محدد بحد أقصى 3 دورات لكل عيب، ثم تسجيل Blocker وليس قلب الحالة إلى VERIFIED.
9. **SAFE INTEGRATION:** Stage الملفات المطلوبة فقط، فحص staged diff/secret scan، Commit على main القائم، Push fast-forward فقط، `git ls-remote` مطابق، CI status & runtime/release منفصلان.
10. **SAFE SPACE RECLAIM:** **بعد** Push/احتفاظ بالأدلة: قائمة files generated redundant مع bytes/LastWrite/process usage، Dry Run، حذف allowlisted فقط، وقياس المساحة فعلًا. ممنوع إزالة source/tests/migrations/untracked unique/logs necessary/current APK/.git.
11. **LEDGER + AUTO NEXT:** Update manifest + acceptance + ledger بالأدلة الفعلية، ثم بدء أول خدمة مستقلة جاهزة داخل نفس الجلسة. لا وعد بالعمل بعد توقف الجلسة دون Automation حقيقية؛ احفظ Exact Resume Point.

**عقد البيانات لكل Service:** `service_id`, baseline SHA, origin SHA, UI baseline, implementation paths, permission/provenance, acceptance cases, Gradle/JUnit summary, OS device/API (محلي فقط), synthetic fixture, requery, Room history, screenshots and redacted logs, reviewer verdict, local commit, confirmed remote SHA, disk reclaimed bytes, known limitations, next stage.

---

## 13. أولويات الإصلاح والمراجعة المفتوحة (Risk Register)

| الأولوية | القضية | الحالة | الإجراء الآمن التالي |
|---|---|---|---|
| P0 | PC03: Full-suite 44 tests / 4 failures | **مثبت فشل** في سجل 49، وإعادة اختبار لم تثبت بعد | شخّص OS provider `PRESENT/UNKNOWN`، UI scrolling، permissions، ثم سجل JUnit XML جديد |
| P0 | الحذف لا يجب أن يمس ملفات شخصية/بقايا ملتبسة | شرط منتج دائم، PC03 تحت الاختبار | Restrict automatic selection to owned cache؛ مراجعة فردية لملفات shared؛ exact requery + consent |
| P0 | PC02: alias overlap بين MediaStore وSAF الفعلي | وحدة الحماية موجودة؛ On-device overlap end-to-end **غير مثبت** | اختبار synthetic واحد باثنين URI ومراقبة no false duplicate، دون حذف حقيقي إلا بعد الإثبات |
| P0 | Physical identity بعد Room persistence/process death | فجوة مستقلة مفتوحة | إعادة إثبات Identity/Trust بعد Reload، Forward Migration إذا لزم |
| P0 | عدم تسريب هاتف/أسرار إلى GitHub Public | HEAD الحالي منقح من Serial في فحص سابق؛ تاريخ Git القديم لم يُعَد كتابته | Scan staged diffs وQA raw قبل كل Push، منع نشر DB/Logcat الخاص |
| P1 | PC03: Migration v2→v3 على قاعدة موجودة | Test migration فردي PASS؛ full acceptance غير مثبت | Re-run على نسخة QA صناعية واحفظ بيانات v2 الموجودة، لا تعديل schema تاريخية |
| P1 | PC01 UI checkbox overlay / real touch interaction | Tests Semantics PASS، لمس حقيقي يحتاج فحص | Test touch accessibility/overlay وحالات أول تشغيل |
| P1 | PC02 Mixed cache+media cancel/recovery | نص واجهة صُحح، مصادقة UI متكاملة لم تتثبت مستقلًا | Instrumentation click consent cancel + Room exact actions |
| P1 | PC02 smaller-than-1KiB/scope PARTIAL/budget | QA/UX observations معلومة | حدود/أسباب استبعاد صريحة، لا «No duplicates» بشكل مضلل |
| P1 | صحة البيانات عبر Home/Reports/Pro | عقد معماري، لم تُتحقق كل الخدمات | مصدر واحد وقياس قبل/بعد ونطاق/وحدة/زمن |
| P1 | Supabase Google OAuth/OTP/Passkey Backend/RLS | ليس لدينا E2E إثبات مكتمل | اختبارات حقيقية على Staging بالمفاتيح الآمنة والتوقيع الصحيح |
| P1 | Play Billing/Pro 50 ميزات | Features cataloged ≠ production | تمييز supported/blocked وPurchase Verification Server وإثبات التجديد والإلغاء |
| P1 | 20 screens/mobile aesthetics regression | مرجع محمي | screenshots فعلي قبل/بعد مع RTL/LTR/insets/accessibility |
| P2 | قاعدة Duplicate history retention/cleanup | نمو محتمل | Retention policy لا تمس سجلات تدقيق يحتاجها العميل |
| P2 | D disk free ~2.44 GB | ضغط تخزين | Reclaim proven generated artifacts بعد دمج/اختبار، لا حذف عشوائي |
| P2 | فصل المستودعين Windows vs Linux | التزام صارم | أسماء Paths واضحة وتغيير واحد في بيئته فقط |

---

## 14. أفعال ممنوعة دائمًا

- عدم استبدال التصميم الموافق عليه أو Logo/Animation/Founder/Authentication بلوحة عامة.
- عدم لمس Repo Linux من مهمة Windows والعكس، أو مزامنة غير مصرح بها بين بيئات مختلفة.
- عدم حذف ملفات فريدة أو Worktrees/Branches/Stash/Untracked لمجرد أنها «قديمة» أو خلف main.
- عدم تنفيذ `git reset --hard`, `git clean`, `force push`, `filter-repo`, حذف `.git` أو إعادة كتابة History دون فحص أثر وموافقة المالك الصريحة.
- عدم تعديل schema v1/v2 التاريخية المستخدمة على الجهاز بطريقة تكسر Migration/Room.
- عدم نسخ Server Role / Client Secrets / API tokens إلى المستودع العام أو Build APK.
- عدم عمل Fake Pro / Fake antivirus / fake storage recovery / fake data/metrics / fake permission.
- عدم حذف ملفات المستخدم أثناء QA، حتى لو أذن المالك باستخدام الهاتف: الاختبار المدمر فقط بملفات صناعية جديدة مُعلمة ومثبتة الهوية.
- عدم العمل على نفس ملف Kotlin من أكثر من Agent في وقت واحد؛ المراجع Read-only.
- عدم تفسير Android result `UNKNOWN` أو Test `skipped` أو انقطاع ADB كنجاح.
- عدم افتراض أن تشغيل اختبارات PC02 اليوم يغطي صلاحيات Android مختلفة أو مزودي SAF مختلفين.
- عدم اعتبار نجاح Build أو CI ضمان عدم وجود Visual Regression أو صحة سلوك Runtime.

---

## 15. تعليمات الانطلاق لأي وكيل جديد يقرأ هذا الملف

1. **لا تنفذ تغييرًا اعتمادًا على هذه الوثيقة وحدها؛ هي Snapshot بتاريخها.**
2. حدّد صراحة هل المهمة في `D:\KNOUX-AI` (Windows PC00–PC13) أم `/root/workspace/KNOUX-AI` (Linux Mobile M00–M15).
3. افتح AGENTS.md والـManifest والـLedger والعقد E2E الحالي، ثم استعلم عن `HEAD`, `origin/main` LIVE, `status -uall`, worktrees/stash/reflog, active writers/Gradle, device availability/disk.
4. إذا PC Windows، أحدث نقطة معروفة في هذا التقرير: **PC03 App Residue**، والاختبار الكامل **فشل 4/44** قبل إعادة التشغيل الأخيرة؛ راجع نتائج `50-full-suite-media-granted` أو ما بعده، ولا تعِد كتابة ما أُنجز.
5. أعد فحص PC02 alias/Room-safety في أقرب مراجعة مستقلة، لكن لا تعرقل كتابة PC03 الجارية أو تعمل destructive test موازية على الهاتف.
6. اعمل Workflow **تستخدم أدواتك الحقيقية**، ونفذ Vertical Slice مع أدلة ومراجع البصريات، ثم Commit/Push/Remote verification وقيس Cleanup آمن، ثم المرحلة التالية.
7. تعامل مع أي خدمة غير قابلة للتنفيذ على جهاز Android عادي كـCapability محدودة وصادقة، وليس زرًا وهميًا.
8. تذكر أن **مسار الهدف هو منتج Production فعلي وليس عدد مراحل مغلقة في ملف JSON**.

---

## 16. مراجع الأصول والإصدارات التي استند إليها هذا التوثيق

### مرفقات سابقة متاحة في سياق المشروع

- `KNOUX_AI_MOBILE_OPENCODE_SUPREME_FULL_FUNCTIONAL_E2E_MASTER_20261113.md` — المرجع الموسع: 16 مرحلة/10 خدمات/50 ميزة وملحقات SQL/audio/visual.
- `KNOUX_AI_MOBILE_OPENCODE_TWO_SCREENS_SALAWAT_DIRECT_MP3_FINAL_20261113.md/.txt` — آخر عهد الشاشتين والصوت.
- `01_KNOUX_AI_MOBILE_ECOSYSTEM_APPROVED.png` و`02_KNOUX_AI_MOBILE_FOUNDER_APPROVED.png` — أصل تصميم Mobile Ecosystem/Founder.
- `KNOUX_AI_PHONE_FULL_FUNCTIONAL_AUTONOMOUS_E2E_WORKFLOW_20261112.md` — نسخة قديمة من عقد الموبايل.
- `KNOUX_AI_PC_KIRO76AE_AUDIT_OPENCODE_WORKFLOW_HANDOFF_20261113.md` — توثيق جلسة Kiro.
- ملف جلسة OpenCode المصدر: `duplicate-finder-over-wireless-adb(1).json` — سجل تاريخي، آخره `outcome: failed` خاص بالجلسة، **لا يعني** أن المستودع الحالي فشل (تم لاحقًا دمج PC02).
- `Pasted markdown.md` — مراجعة مستقلة Read-only لمخاطر PC02 قبل تصحيحات لاحقة؛ قراءته يجب أن تكون مقارنة مع أحدث SHA لا بديلاً عنه.
- مجموعة Visual References الرئيسية (الملفات 01–20 وأصول العرض/الهوية بحسب الحزمة المعتمدة)؛ لا تعتمد على أسماء الصور وحدها لإثبات UI.

### مراجع داخل Windows Repo

- `AGENTS.md`, `docs/workflows/PC_E2E_TASK_MANIFEST_20261112.json`, `docs/workflows/KNOUX_PC_PER_SERVICE_AUTONOMOUS_EXECUTION_20261113.md`, `docs/workflows/KNOUX_E2E_PUBLIC_WORKFLOW_POLICY.md`
- `qa-evidence/e2e/EXECUTION_LEDGER.md`, `qa-evidence/e2e/ACCEPTANCE_MATRIX.md`, `qa-evidence/e2e/KIRO_SESSION_76AE_DEEP_AUDIT_20261113.md`
- `qa-evidence/e2e/pc01/logs/37-real-ui-class-clean.txt`, `38-storage-class-final.txt`, `39-media-test-granted.txt`, `40-jvm-unit.txt`
- `qa-evidence/e2e/pc02/logs/103-full-suite-alias-fix-fixed.txt`, `104-connected-junit-xml.log`, `105-jvm-final-precommit.log`
- `qa-evidence/e2e/pc03/logs/40-migration.txt`, `43-storage-final.txt`, `48-ui-liveout.txt`, `49-full-suite.txt`, `50-full-suite-media-granted.txt` (نتيجة الأخيرة كانت غير نهائية وقت الفحص)
- `docs/pro/PC_PRO_FEATURE_CATALOG_20261112.json`, `docs/pro/PC_PRO_OWNER_FULL_FUNCTIONALITY_OVERRIDE_20261112.md`.

**سياسة المرجعية:** سجلات PC03 أُنشئت أثناء عمل الوكيل، ولا يجب نسخها إلى مصدر عام دون إزالة الجهاز/المحتويات الشخصية. لا تفترض أي `sandbox:/mnt/data/...` لنفس ملفات Windows؛ روابط الملفات المرفقة هنا مستقلة عن مسارات الجهاز.

---

## 17. خلاصة التسليم النهائية — Reality / Risks / Unique Work / Gaps / Safe Next Action

**REALITY:** KNOUX AI منتج Android مميز بصريًا وله عمل برمجي متراكم ومثبت. Windows GitHub main عند آخر فحص **`b8577cbc7e798cfaf23a0f44650941be7ae332d0`** محليًا وبعيدًا، PC00–PC02 مدمجة ومسجلة Verified بشواهد محددة. PC03 فيه شغل حقيقي غير مدمج واختبار Full Suite أخفق 4/44. Linux Mobile تصميمه أُعلن مكتملًا لكن لم يتم فحص Backend/Runtime له في هذه الجلسة.

**RISKS:** سلامة حذف المكررات عند physical aliases غير مؤكدة بكل الموردين، حالة Room بعد reload، App Residue provider/UI failures، مخاطر خصوصية مستودع Public، حجم قرص D، قيود Android وواقع Billing/OAuth/Pro.

**UNIQUE WORK:** جميع تصميمات KNOUX، Splash/Smile/Salawat، الهوية والتوقيع، Authentication UX، الشاشات العشرون، PC00–PC02 Committed SHA، PC03 Kotlin/Tests/Schema v3 غير المدمجة، والـQA والـManifest. لا تضعها في سلة «قديمة».

**GAPS:** قبول E2E لخدمات PC03–PC13، اختبارات SAF overlap/Room، Pro server entitlements، Supabase staging/RLS/OAuth end-to-end، اختبارات release signing/Google Play، ودليل Runtime مستقل على نسخة Linux Mobile.

**SAFE NEXT ACTION:** **اترك OpenCode الكاتب يواصل PC03**. عند الاستلام: راجع PC03 Logs بعد `49-full-suite`، احسم أخطاء Trash/Provider/Scrolling، تحقق من Migration 2→3 على بيانات QA صناعية، أعد Instrumentation كاملة على نفس الشجرة، استلم Review مستقلة، حدّث الحالة، ادمج فقط الجزء المثبت إلى main وGitHub بتحقق Remote SHA، ثم استرد مساحة مخرجات مولدة متكررة فقط بعد مراجعة، وابدأ PC04. لا تنسَ إعادة اختبار حماية PC02 physical aliases + persistence عندما تتوفر فرصة QA مستقلة آمنة.

**انتهى الملخص المرجعي — Snapshot 2026-11-13 11:51 UTC+04**.
