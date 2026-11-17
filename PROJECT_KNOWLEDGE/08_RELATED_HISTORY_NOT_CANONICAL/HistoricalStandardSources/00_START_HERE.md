# KNOUX AI — START HERE / ملف المصادر الموحد

**As-of:** 2026-11-13 11:51 Asia/Dubai (UTC+04:00)  
**Purpose:** Verified-and-qualified project context for ChatGPT Project Sources, plus owner-facing guidance and agent handoff.  
**Status:** Knowledge bundle, **not** a Git snapshot, deployment certificate, or permission to mutate a running agent's project.  
**Brand:** KNOUX (parent) → KNOUX AI (Android product); founder Eng. Sadek Elgazar / م. صادق الجزار; https://knoux.store/

## طريقة الاستخدام في ChatGPT Project

1. ارفع هذا ZIP لمصادر مشروع ChatGPT إذا كانت المنصة تدعمه؛ **الأفضل استخراج الملفات ورفع Markdown/JSON بصورة مستقلة عندما يحتاج النظام لفهرسة محتوى كل ملف**. لا تفترض أن محتويات ZIP تُفهرس تلقائيًا.  
2. انسخ النص من `01_AGENT_RULES/PROJECT_INSTRUCTIONS_PASTE.md` إلى تعليمات المشروع إن أمكن. هذا الملف مختصر، والنسخة المفصلة في `01_AGENT_RULES/FULL_AGENT_OPERATING_CONTRACT.md`.  
3. اجعل `02_CANONICAL/KNOUX_AI_LIVING_SOURCE_OF_TRUTH.md` المرجع التنفيذي الأحدث. تفاصيل القديم في `03_ORIGINAL_SPECS/`، ولا تعتبر الجداول أو تعليمات قديمة شهادات PASS.  
4. أي طلب تغيير حديث يجب أن يبدأ بفحص الأدلة، ثم اختيار المسار الصحيح (PC أو Linux Mobile)، مع مراعاة الوكلاء الجاري عملهم.  
5. `04_TASKS/` به قالب مهام قابل لتحديث الحالة بدل استنساخ خطة جديدة لكل وكيل؛ القالب **Snapshot توثيقي** ولا يُكتب تلقائيًا فوق Manifest الوكيل الحي.  
6. `05_REFERENCES/` به صور أصلية مختارة للخدمات ومرجعا Ecosystem وFounder؛ إنها مراجع بصرية وليست إثباتًا للشاشة الموجودة حاليًا في APK.  
7. `06_EVIDENCE_AND_SOURCES/` به سلسلة المصدر وملخصات تاريخية؛ الروابط الثلاثة من المستخدم موجودة في `SHARED_LINKS_ACCESS_STATUS.md` وتحتاج نسخ محتواها للتحقق لأن الوصول فشل.

## 3 أشياء مختلفة لا يجوز دمجها تلقائيًا

| Lane | Location | Mission | Verification boundary |
|---|---|---|---|
| Windows OpenCode / Kiro / DeepSeek | `D:\KNOUX-AI` | Android source & PC00–PC13 service delivery | Live PC Git, logs, phone-test outputs; do not overwrite active agents |
| Phone/Linux OpenCode | `/root/workspace/KNOUX-AI` | Separate independent workspace; Mobile M00–M15 | Cannot infer state from Windows SHA/tests; inspect this workspace itself |
| Physical Android device | Owner-authorized phone, Android API 35 per previous probe | Install/debug/test apps, optionally Wireless ADB | Not a repository; tests only on new synthetic data; dynamic device selection, never publish identifiers |

The GitHub URL used in owner materials is `https://github.com/daynightae-cmyk/KNOUX-AI`. **Even if both checkouts show the same remote, NEVER assume they are identical or safe to synchronize** before independent ancestry/status/worktree comparison and express owner instructions.

## Snapshot — verified and qualified

- On Windows, direct local HEAD and live `git ls-remote origin main`: `b8577cbc7e798cfaf23a0f44650941be7ae332d0` at 2026-11-13 11:51 Asia/Dubai (UTC+04:00). `main` branch; last commit documentation advancing to PC03.  
- Windows manifest labels PC00 (foundation), PC01 (Smart Clean), PC02 (Duplicates) `VERIFIED`; they are committed remotely with evidence, **but this is a recorded per-stage status, not Play release or blanket risk closure**. Independent reviewer flags PC02 overlapping MediaStore/SAF alias scenario and protection across Room restore as not independently shown on a physical overlapping-provider test.  
- Windows PC03 App Residue actively under implementation: uncommitted Kotlin repository/controller/rules/UI, Room v3 and Android tests. Run `49-full-suite` yielded **44 tests with 4 failures** at 11:40; later `50-full-suite-media-granted` started but no conclusive final result as of this snapshot. Do not say PC03 PASS.  
- Windows PC04–PC13 queued in manifest. Pro registry lists 10 sections, 50 features, most individually `CATALOGED` — this is a spec, not an active Play Billing subscription.  
- Linux Mobile visual design was described by the owner as finished; Mobile source HEAD/ADB-runtime/CI not independently inspected for this snapshot. No automatic M00–M15 PASS.  
- OAuth/Supabase: Google OAuth clients were created/locally inspected in a previous reported workflow, but **successfully saved Google provider configuration and real Android authenticated session are not proven here**.  
- Source content is sanitized for project knowledge. No Google Client Secrets, Supabase management tokens, private ADB serial or Wi-Fi debugging endpoint belong here.

## Evidence vocabulary

`OWNER_APPROVED_DESIGN` → `IMPLEMENTED_UNVERIFIED` → `UNIT_VERIFIED` → `DEVICE_SUBSET_VERIFIED` → `DEVICE_E2E_VERIFIED` → `INDEPENDENT_REVIEW_VERIFIED` → `LOCAL_COMMITTED` → `REMOTE_MAIN_VERIFIED` → `CI_VERIFIED` → `PRODUCTION_RELEASED`.
Each is independent; no skipping a gate to produce a cosmetic COMPLETE. If unavailable, use `BLOCKED_EXTERNAL`, `BLOCKED_PLATFORM`, `BLOCKED_DEVICE`, or `PENDING_VERIFICATION` honestly.

## Known source limits

This package combines user-uploaded historical files and a direct read-only Windows audit up to 2026-11-13 11:51 Asia/Dubai (UTC+04:00). **It does not include full chat transcripts for every separate chat** unless they are present in the user-provided handoff documents, and it does not have a live connection to the independent Linux Mobile workspace. Three provided ChatGPT share pages could not be fetched; their text has NOT been silently inferred. The exported documents retain source attribution and limitations.
