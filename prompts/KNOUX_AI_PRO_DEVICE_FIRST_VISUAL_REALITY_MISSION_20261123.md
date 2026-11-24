# KNOUX AI PRO — DEVICE-FIRST, REFERENCE-LOCKED EXECUTION MISSION
Date: 2026-11-23
Instruction file: prompts/KNOUX_AI_PRO_DEVICE_FIRST_VISUAL_REALITY_MISSION_20261123.md

## ROLE AND NON-NEGOTIABLE OBJECTIVE
أنت Principal Android/Kotlin/Jetpack Compose Engineer + Visual Fidelity Director + Device QA Lead + Git Integration Auditor. هذه مهمة تنفيذ فعلي داخل المشروع الأصلي وليست تخطيطًا أو توليد صور أو إعداد حزم. راجع آخر حالة حية للكود والهاتف والمراجع، ثم حسّن أو استبدل الشاشة غير المطابقة فقط، شاشة واحدة في كل دورة، حتى آخر شاشة/قسم موجود. المرجع البصري الملائم لكل Route هو هدف المطابقة الدقيقة، لكن لا تعلن 100% إلا بأدلة مطابقة مرئية وتجربة مستخدم فعلية. جودة الشكل والتفاعل وتكامل الوظائف كلها شروط قبول مستقلة.

## KNOWN OBSERVED BASELINE — REFRESH, DO NOT ASSUME CURRENT
- المشروع الحقيقي الذي تم الوصول إليه: K:\KNOUX-AI-Pro (تطبيق مستقل تمامًا عن D:\KNOUX-AI).
- التقنية: Android native, Kotlin, Jetpack Compose. نقاط الكود الفعلية تشمل app/src/main/java/com/knoux/ai/MainActivity.kt، ui/v2/HomeV2.kt، ui/v2/HubScreensV2.kt، ui/v2/SectionHubRefV2.kt، ui/v2/ExtraHubsV2.kt، ui/v2/OnboardingV2.kt، ui/screens/WelcomeScreen.kt، ui/screens/ServiceHubScreen.kt، وdeveloper/ui.
- لحظة الفحص 23 نوفمبر 2026: HEAD = 230d30db9dbd83ddb4f857bea05ad0697d2fd660 على master ومتطابق مع origin/master؛ git status --short لم يظهر تغييرات قبل إنشاء ملف التعليمات. رسالة آخر Commit تقول إعادة بناء الرئيسية وعشرة hubs؛ هذه رسالة Commit وليست قبولًا بصريًا موثقًا على الهاتف.
- المستودع الحالي لا يعرض origin/main؛ فرع الدمج الفعلي هو origin/master. لا تخترع origin/main ولا تغير الفرع الافتراضي ولا تعمل force-push؛ ادمج Fast Forward في الفرع القانوني الذي تعيد التحقق منه.
- نتيجة adb devices -l لحظة الفحص: لا يوجد هاتف متصل ظاهر. التقرير السابق qa-evidence/refs_20261121/device_verify_final_20261122.md أثبت اختبارات على Samsung SM-A025F، Android 12، serial R9ZR20HWHQY، وذكر تعذر Google OAuth في debug بسبب package/SHA-1. التقرير قديم بالنسبة إلى HEAD الحالي ولا يثبت اختبار هذا الـSHA.
- يوجد بالفعل design/ و _references/ و qa-evidence/ و prompts/. استعمل الموجود ولا تنسخ المشروع إلى مجلد جديد.

## FIRST: FORENSIC RECONCILIATION, READ-ONLY
قبل أي تغيير نفذ تدقيقًا حقيقيًا وسجّل النتائج المختصرة في docs/qa/pro_visual_20261123/STATUS.md:
1. pwd، repo canonical path، git status --porcelain=v1 -uall، HEAD SHA، local/remote refs، git branch -avv، git remote -v، git ls-remote origin، git worktree list --porcelain، git stash list، git reflog، آخر commits، فرق ahead/behind، untracked، ignored المهمة، وعمليات Git الجارية. لا تعتبر العمل على master هو كل العمل الفريد.
2. افحص فقط الأفرع وworktrees وstashes وreflog وPRs وأعمال الوكلاء الموازيين ذات الصلة، وقارن المحتوى فعليًا قبل اعتبار أي نسخة زائدة. تعامل مع .git/gc.pid، عمليات البناء، تعديل الوكلاء، والملفات المفتوحة بحذر؛ لا تتدخل في عمل جار.
3. راجع آخر كود Home/Auth/Onboarding/Settings والـ10 hubs الفعلية وRoutes والتنقل وViewModels/Repositories/Room/Android APIs والاختبارات ووثائق QA. استخرج جدول: Route | ملفات التنفيذ | الحالة الحقيقية | المرجع المعتمد | الفجوة | الإجراء.
4. افحص APK المثبت على جهاز Samsung، package/version/lastUpdateTime، وSHA-256 للـAPK إن كان الوصول ممكنًا. لا تخلط آخر Build محلي أو تقرير قديم مع ما يعمل فعليًا على الهاتف.
5. لا تلمس D:\KNOUX-AI أو مشاريع أخرى ولا تدمج الكود بين النسخة القياسية وPro. لا تنشئ فرعًا أو worktree جديدًا إلا إذا كان التعارض يجعل ذلك ضروريًا وبموافقة المالك.

## REFERENCE INTAKE — REAL ZIP, NO FAKE IMAGES
المراجع في الملف المرفق بالمحادثة باسم Knoux ai pro.zip؛ تمت معاينة محتوياته: 22 مدخلًا بصريًا منها 20 SHA-256 فريدًا. ابحث عن المصدر المحلي المصرح به أو الصور الموجودة بالفعل بالمشروع وقارن SHA/المحتوى قبل الاستيراد. وجود المرفق في محادثة ChatGPT لا يعني تلقائيًا وجوده على قرص Windows. إن لم تستطع الوصول للصور الجديدة في البيئة المحلية، صرّح BLOCKED_REFERENCE_SOURCE ولا تزعم استيرادها، واستمر فقط في تدقيق الموجود دون تخمين.

خريطة ترتيب الصور داخل الأرشيف، الترقيم يبدأ من 0:
- 00: الغزال المضيء الشفاف؛ mascot/brand asset وليس شاشة.
- 01: OTP / Verify Your Identity.
- 02 و05: Welcome / Founder؛ تطابق بايت-لبايت، احتفظ بمرجع واحد.
- 03 و06: شكلان بديلان لحالة نجاح التحقق البيومتري؛ اختَر نسخة واحدة تناسب الفلو الحالي، لا تنشئ شاشتين لنفس النجاح.
- 04: Login / Google / Passkey / Biometric.
- 07: Home / KNOUX AI Pro.
- 08: Developers Hub.
- 09 و10 و11 و12: Developer Studio ومشاهد/حالات قريبة منه؛ لا تكرر نفس القسم خمس مرات. اربط كل نسخة بدلالة Route/State حقيقية موجودة، أو اختَر الأفضل كمرجع Canonical واحد.
- 13: لوحة مجمّعة لعدة شاشات؛ تستخدم للفهرسة والمقارنة فقط، ممنوع نسخها كواجهة فعلية.
- 14 و15: Smart Clean؛ تطابق بايت-لبايت، مرجع واحد.
- 16: AI Optimization / Recommendations.
- 17: Battery & Performance.
- 18: Privacy & Security.
- 19: App Management.
- 20: Storage Analysis.
- 21: Duplicates.

نظّم النسخ المرجعية الفريدة التي تستعملها فقط داخل design/owner_refs_20261123/ بأسماء واضحة مستقرة مثل:
brand/stag_mascot.png
auth/welcome_founder.png
auth/login.png
auth/otp_verify.png
auth/biometric_success.png
home/home_pro.png
developer/developer_hub.png
developer/developer_studio.png
clean/smart_clean.png
tools/ai_optimization.png
tools/battery_performance.png
tools/privacy_security.png
tools/app_management.png
tools/storage_analysis.png
tools/duplicates.png
أسماء ومسارات الأمثلة تُعدَّل حسب route النهائي بعد فحص الكود، بدون نسخ متعددة أو امتدادات مضللة. سجّل original file/index، SHA-256، dimensions، target route، قرار KEEP/MERGE/DUPLICATE/REFERENCE-ONLY، وسبب القرار في design/owner_refs_20261123/MANIFEST.md. لا تضع نفس الصورة في design وapp/res وpreview وqa بلا داعٍ: الأصل المعتمد نسخة واحدة؛ فقط عناصر runtime المنفصلة والمستخدمة فعلًا تدخل Android resources. لا تترك ZIP مستخرجًا كاملًا مكررًا ولا تنشئ حزمة أخرى.

## DESIGN LOCK — MATCH IMAGE, NOT IMAGE-AS-SCREEN
- حافظ على هوية KNOUX AI Pro المعتمدة: cosmic purple/navy + neon violet/cyan/magenta + gold PRO accent، RTL عربي، شعار K الحقيقي، crown/PRO، الغزال المضيء، header وbottom navigation المتسقين. لا تغيّرها لهوية جديدة.
- لا ترسم شعار K بديلًا. استخدم assets الرسمية الموجودة فعليًا. الغزال ظاهر بلا قص للقرون، بلا صندوق محيط مزيف، وفي مكانه بالنسبة للـheader. تأكد من صلاحية motif السفلي إن كان في المرجع المعتمد.
- اطابق التكوين المرئي الحقيقي: hierarchy، proportions، spacing، grid، gradients، glow، outlines، radii، typography، iconography، footer، insets، scroll physics، alignment، animations، transitions، and RTL ordering؛ وليس مجرد ألوان متشابهة.
- ممنوع وضع صورة Screenshot كاملة في ImageView/Compose Image كبديل للشاشة. ممنوع خلفية شاشة كاملة تحتوي أزرارًا مرسومة غير قابلة للتفاعل. ابنِ الواجهة من Composables حقيقية، clickable hit targets، state، accessibility semantics، motion، Canvas/graphicsLayer حين يفيد. الأصول الرسومية يجوز استخدامها للـmascot أو hero أو أيقونة معتمدة، وليس لنسخ واجهة ثابتة.
- لو المرجع يطلب عنصرًا مجسمًا/Orb/3D: اجعله حيًا على الهاتف عبر native Compose graphics, parallax, layered glow, perspective, touch/drag/press feedback, animation lifecycle، وتعامل مع reduced motion والأجهزة الضعيفة. لا تجعل فتح الشاشة مجرد صورة ثابتة أو فيديو أو Poster أو WebView تمثيلي.
- كل زر أو tile أو card يجب أن يفتح وظيفة/Route/حالة حقيقية أو يعرض سبب عدم الإتاحة بوضوح؛ بدون روابط ميتة، وهمية، أو action يوهم بالنجاح. لا تعرض نسب تنظيف أو بطارية أو RAM أو AI scoring أو سعات على أنها بيانات حية إلا من مصدر حقيقي مع documented provenance؛ وإلا حدّدها Demo/Preview بوضوح في بيئة QA فقط.
- لا تنشئ نسختين من أي قسم أو خيارين يقدمان الوظيفة نفسها. كل خدمة لها Route Canonical واحد، وتحتها states فعلية فقط. ما هو قديم أو أدنى بصريًا: عدّله أو استبدله بعد تحليل تكافؤ الوظيفة وحماية أي كود فريد؛ لا تترك الشاشة القديمة في Navigation أو قوائم مخفية.
- لا تحوّل اختلاف صور لبدائل UI مفتوحة للمستخدم: لكل Route واحد أفضل مرجع. إذا صورتان تعرضان حالات حقيقية مختلفة مثل OTP typing/success، اعمل state transitions داخل Flow واحد لا قسمًا مكررًا.
- حافظ على Day/Night، العربية/الإنجليزية حسب الموجود، dynamic font scaling، status/navigation insets، اللمس، accessibility، عدم القطع عند 720×1600، ودقة الشاشات المختلفة.

## EXECUTION LOOP — EXACTLY ONE SCREEN PER CYCLE
1. اختر أول شاشة ذات فجوة مؤكدة بعد مقارنة المرجع والكود المثبت، وليس أول صورة اعتباطيًا. ابدأ بـEntry/Auth/Home إذا كانت بها فجوات حقيقية، ثم انتقل للأقسام تباعًا وفق خريطة التنقل الحية حتى آخر قسم.
2. اقرأ المرجع بالحجم الأصلي واقرأ جميع ملفات تنفيذ Route وحالاته ومصادر بياناته؛ التقط Before screenshot حقيقي من Samsung إن كان الجهاز متاحًا. اكتب discrepancy checklist قصيرة.
3. حافظ على الأجزاء المطابقة؛ نفّذ التغييرات اللازمة داخل Composables/State/ViewModel/Data/Navigation حتى تصبح الشاشة تفاعلية، لا صورة. لا تغيّر unrelated routes ولا تعِد بناء التطبيق كاملًا.
4. شغّل compilation وfocused tests وlint ذي الصلة. أصلح فقط الأخطاء التي أحدثها عملك أو تمنع قبول الشاشة ولا تُخفِ الأخطاء الأصلية. افحص runtime logcat وANR/crash ومشاكل القياسات.
5. ثبّت APK الجديد على Samsung المتصل بعد التحقق من serial/model، دون uninstall أو clear data أو العبث بحسابات المالك. افتح الشاشة عبر الدخول الحقيقي وباللمس، لا بمجرد deep link مخفي. اختبر Back وCTA والتمرير والتبديلات والروابط وحالات empty/loading/error/success والانتقالات. التقط After screenshot ومخرجات الدليل.
6. قارن مرجع الشاشة باللقطة الحقيقية على الهاتف عند نفس viewport: صورة مرجعية/بعد/overlay أو diff مع مراجعة بشرية بصرية؛ راجع الانحرافات، crop، typography، glow، footer، touch bounds. أصلح ثم أعد القياس. CI أو build pass وحده لا يساوي Visual PASS.
7. عندما تنجح شاشة واحدة: أثبت SHA المصدر، الاختبارات، package/APK hash، الجهاز، screenshots، route، وتفاعلات الهاتف. اعمل Commit محدد وواضح، ثم Fast-forward push إلى origin/master إذا ظل هو الفرع الرسمي وتحققت من remote SHA وموانع التزامن. انتظر CI لنفس commit إن كان متاحًا ولا تزعم PASS قبل النتيجة.
8. نظّف بعد نجاح الدمج فقط: مخرجات build المؤقتة التي يمكن إعادة إنشائها، النسخ المكررة الموثقة عبر hash، وstaging الخاصة بالشاشة بشرط عدم استخدامها من وكيل أو عملية أخرى؛ افحص أثر الحذف واطلب موافقة صريحة لأي حذف لملفات مستخدم/أفرع/worktrees/stash/تاريخ/بيانات غير قابلة للتعويض. لا تحفظ حزم نسخ احتياطية متراكمة ولا تنشئ previews جديدة. سجّل المساحة قبل/بعد بالدليل.
9. أعِد فحص HEAD/remote/status بعد كل دورة، ثم فقط انتقل للشاشة التالية. كرر حتى آخر شاشة موجودة في التطبيق؛ لا تكتفِ بتقرير تخطيط أو أول شاشتين.
10. لو Samsung غير متصل: حاول طرق ADB المعتمدة وغير المدمرة وأبلغ عن سبب العائق. يمكنك إجراء audit وbuild ووحدة الاختبارات، لكن لا تضع Device-Verified ولا تغلق الشاشة ولا تنتقل على أنها مكتملة؛ اجعل الحالة BLOCKED_DEVICE_QA مع الأدلة الناقصة. لا تعتمد على Emulator كبديل عن Samsung في بوابة القبول المطلوبة.

## AUTH, DATA AND PLATFORM SAFETY
- التحقق من Google OAuth في debug مهم: تقرير 22 نوفمبر أشار إلى فشل بسبب عدم تسجيل com.knoux.ai.debug مع debug SHA-1 في Google Console. تحقق من الواقع الجديد؛ لا تعلن نجاح Sign-In أو Passkey أو Biometric أو OTP دون تجربة End-to-End. لا تحاول تجاوز OAuth أو تضع مفاتيح في git أو logs.
- لا تطلب صلاحيات Android غير مدعومة ولا تدّعِ قدرة التطبيق على حذف ملفات أنظمة أو تطبيقات أخرى خلاف صلاحياته. لا تستخدم نسب تنظيف وهمية. الحذف داخل التطبيق يجب أن يكون scoped، مع معاينة/تأكيد/undo حيث ينطبق.
- لا تعمل destructive git commands: reset --hard, clean, force-push, filter-repo/rebase rewriting, حذف worktrees/stashes/branches، أو تعديل بيانات هاتف المالك دون موافقة صريحة.
- لا توقف وكلاء موازيين أو Gradle/git عمليات جارية بغير تحقق.

## RELEASE GATES / ANTI-FAKE-CLOSURE
كل شاشة تسلم بسجل موجز في docs/qa/pro_visual_20261123/SCREEN_LEDGER.md:
SCREEN_ID | ROUTE | SELECTED_REFERENCE_SHA | BEFORE_COMMIT | AFTER_COMMIT | APK_SHA256 | SAMSUNG_ID | BUILD | TESTS | LINT | CI | DEVICE_INTERACTIONS | BEFORE/AFTER/DIFF_PATH | VISUAL_DELTA | GIT_REMOTE_SHA | STATUS.
الحالات فقط: VERIFIED_ON_SAMSUNG / IMPLEMENTED_NOT_DEVICE_VERIFIED / PARTIAL / BLOCKED / NOT_STARTED. لا 100% ولا DONE ولا RELEASED بلا تحقق مطابق لكل بوابة. إذا اختلف المرجع مع وظيفة مهمة موثوقة، حافظ على الوظيفة ووثق الفرق واطلب قرارًا على التعارض المادي فقط، ولا تقدّم اختيارين بصريين للمستخدم.
بعد آخر شاشة: full regression على Samsung الفعلي عبر كل مسار + guest/auth/onboarding + tabs + permission denied + portrait/landscape handling + RTL/LTR + Day/Night + performance، مع مقارنة مرئية، APK SHA، Git/CI SHA مطابق، ومساحة تنظيف حقيقية.

## OUTPUT — SHORT USER REPORT, REAL ACTIONS
ابدأ التنفيذ الآن على K:\KNOUX-AI-Pro. لا تُنتج خطة بدل العمل. لا تعمل ZIP/حزم جديدة ولا تُخرج صورًا للتنزيل ولا تطلب من المالك إرسال ملفات كثيرة في المحادثة. البرومبت والـmanifest والـledger فقط داخل المشروع في مواقعها؛ عالج الصور الموجودة محليًا وسمِّها حسب وظيفتها. كل تقرير مرحلي بالعربية المصرية في بضعة أسطر: REALITY → RISKS → UNIQUE WORK → GAPS → SAFE NEXT ACTION، مع الشاشة المنفذة وSHA وأدلة الهاتف وفرق المساحة، وصرّح فورًا بما هو غير مثبت.