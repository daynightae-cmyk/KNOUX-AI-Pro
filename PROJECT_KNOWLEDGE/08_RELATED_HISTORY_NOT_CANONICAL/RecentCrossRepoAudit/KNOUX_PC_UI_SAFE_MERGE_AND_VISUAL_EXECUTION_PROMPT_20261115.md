# KNOUX AI — SINGLE-WRITER, MERGE-FIRST UI RECOVERY & 10-SCREEN NATIVE IMPLEMENTATION
**OWNER EXECUTION CONTRACT • 2026-11-15 • EXECUTE REAL CODE WHEN SAFE, NOT JUST A PLAN**

You are the designated integration engineer for the **Windows-developed KNOUX AI** Android app. Read the companion `KNOUX_DUAL_REPO_UI_AUDIT_20261115.md`, the ten approved visual PNGs, historical runtime shots, and the latest overlap screenshot fully. The audit is a previous snapshot: RECHECK LIVE FACTS before any decision.

## 0. HARD PRODUCT BOUNDARIES
- Only canonical PC product to edit/integrate: **`D:\KNOUX-AI`**, repository `https://github.com/daynightae-cmyk/KNOUX-AI.git`, integration branch **`main`**.
- Linked worktree of the SAME repository to protect/reconcile: **`K:\KNOUX-AI-NATIVE-UI-20261114`**, branch `knoux-native-ui-20261114`.
- A DISTINCT product, NOT editable or mergeable for this task: **`K:\KNOUX-AI-Pro`**, repository `daynightae-cmyk/KNOUX-AI-Pro`, branch `master`. Also never touch the independent phone/Linux working lane.
- The crown `👑 Pro` in the owner's 10 refs is the **verified premium subscription entitlement indicator inside the standard D: app**. It is NOT authorization to turn the app into the separate Pro repository, alter entitlement data or fake activation.
- The separate Pro repo was observed local `f04f634`, remote `b32c278` (2 commits ahead) and both Gradle products used `applicationId=com.knoux.ai`. This is a cross-product identity collision to record and separately resolve later; **do not install Pro over the standard app**.

## 1. THE MANDATORY STOP-GATE: NO WRITES BEFORE FULL UNIQUE-WORK PRESERVATION
First run read-only:
```
git -C D:\KNOUX-AI rev-parse --show-toplevel
git -C D:\KNOUX-AI rev-parse HEAD
git -C D:\KNOUX-AI status --porcelain=v1 -uall
git -C D:\KNOUX-AI diff --stat
git -C D:\KNOUX-AI diff --check
git -C D:\KNOUX-AI ls-remote origin refs/heads/main
git -C D:\KNOUX-AI branch -avv
git -C D:\KNOUX-AI worktree list
git -C D:\KNOUX-AI stash list
git -C D:\KNOUX-AI reflog -n 12
git -C K:\KNOUX-AI-NATIVE-UI-20261114 status --porcelain=v1 -uall
git -C D:\KNOUX-AI log --oneline main..knoux-native-ui-20261114
git -C D:\KNOUX-AI rev-list --left-right --count main...knoux-native-ui-20261114
```
Inspect `AGENTS.md`, `00_START_HERE.md` if present, canonical source of truth, workflow manifest, QA ledger and owner-protected assets. Inspect process command lines and running sessions for Manus/OpenCode/Kiro/Codex, Gradle and ADB; identify exact file ownership, not merely process names. Check C/D/K disk free capacity.

**Known starting snapshot (not an assumption):** main/origin-main `78391aa927b3629fcf856de04b10dae23fc2881c`; 18 modified tracked paths + 4 unique untracked Kotlin/data files in D; the branch at `6f1d7392ec614c3a36b60d4c734142bc8a7818af` with five unique commits + six modified tracked paths + untracked `NativeTools.kt`. The `.work` folder contains approved refs/QA and accounts for many more untracked inventory entries.

Before touching a line: enumerate exact unique content and hashes from both worktrees; inspect branches/stashes/reflog/untracked/QA. Obtain verified exclusive write ownership of all overlapping files. **If any active agent owns shared files, stop before any merge or UI edit, report `OWNERSHIP_BLOCKED` with paths and actual evidence, and do not kill or interrupt it.** Read-only comparison is still allowed.

## 2. PROTECT AND RECONCILE — ACTUAL MERGE, NOT FAKE CLOSURE
Once ownership is released, preserve all unique source in versioned, path-scoped local Git checkpoints (or equally verifiable reversible preservation), including previously untracked source and the worktree dirty work, with hashes. Avoid extra repository copies or backup-folder proliferation. Never stage raw private logs, serial/endpoint, PAT, OAuth secrets, or generated build caches into the public repo. Do NOT delete `NativeTools.kt`, `IdentityKit.kt`, `NeonKit.kt`, `StorageScreens.kt`, `data/DeviceInsights.kt` or visual refs.

After protection, reconcile **only within KNOUX-AI**:
- Five committed branch changes, including Figma shell, Tools/Reports, byte-number RTL, brand plates and motion proof.
- The six branch-local tracked changes + new `NativeTools.kt`.
- D's eighteen modified tracked and four untracked Kotlin/data source paths.
- Current upstream `origin/main` and any additional live changes.

Use a reviewed merge with explicit conflict resolution and exact commit provenance; never blanket `ours` / `theirs` or cherry-pick without resolving overlapping semantics. Preserve real service contracts and all route wiring.

**Specific fusion contract:**
1. Native branch supplies responsive Figma layout geometry, `NativeStorageOverview`, `NativeScreenTitle`, `NativeBrand` (`BrandTone`, `ServiceIconPlate`, `BrandHairline`), stable nav controls and typography scale handling where demonstrably better.
2. D dirty source supplies richer owner-approved violet/cyan icon/hero language, `IdentityKit`, `StorageScreens` and `DeviceInsights`, and real service logic; enforce restrained local light rather than full-screen glow or stacking overlays.
3. Keep D's `Page.LARGE_FILES → RealLargeFilesScreen` route; the native branch's `MainActivity` lacks it. Also preserve `NativeAppsScreen`, `NativeBatteryScreen` and storage route improvements when reconciled; no dead service cards or duplicated screen registrations.
4. Preserve `DeepScanReal`, `DuplicateReal`, `SmartCleanReal`, `PrivacyReal`, `ResidueReal`, Room, OS permissions, splash/smile/audio, onboarding, login/OTP/Google/Passkey/biometric, official logo, entitlement lifecycle and original art. No static WebView/image screen substitution.
5. Compare `MainActivity.kt`, `HomeScreen.kt`, `MasterScreens.kt`, `KnouxComponents.kt`, `KnouxScaffold.kt`, `AndroidManifest.xml`, strings and tests line-by-line before resolving. No giant sed/token replace.
6. Record a merge matrix: `path | D unique behavior | branch unique behavior | chosen resolution | verification`. Preserve superseded versions through Git history/checkpoints without leaving duplicate production code.

**INTEGRATION EXIT CONDITIONS (BEFORE NEW DESIGNS):** code integrated safely into local `main`; no unique source was lost; clean compile + JVM tests + androidTest compilation performed on actual result; core routes and Room logic still compile; tests and status counted; stage only reviewed paths; commit exact SHA; push fast-forward after verifying remote still compatible; verify `git ls-remote origin refs/heads/main`; report any missing CI workflow honestly. If live remote or ownership changed, RECHECK and re-reconcile; never force push or rewrite history.

If real tests fail, fix confined regressions without dropping valuable visuals or services. If integration is blocked, state `INTEGRATION_BLOCKED`, preserve everything, and DO NOT START NEW UI WORK.

## 3. FIRST VISUAL DEFECT — NOT A PALETTE ISSUE
The owner screenshot `03_LATEST_OVERLAP_REGRESSION.png` shows **Home + Large Files content simultaneously layered**, with overlapping titles, cards, chips and buttons. Reproduce under actual runtime, then find root cause: navigation transition timing, debug `visit` state, multiple route composition, `NavHost` enter/exit, modal sheets, nested scroll/pinned action rail, `safeDrawingPadding`, root size constraints and font scale. Investigate; do not claim a cause without proof.

Create targeted Compose navigation tests: after settled navigation/idle each route has exactly one active screen root and correct title; no underlying route is visible/clickable; enter/pop/back/rapid-open settles to one screen. Test screenshots **after transitions settle** and compare to frames during navigation to isolate transient from persistent overlap. Do not mask the defect by setting everything opaque or disabling navigation indefinitely. No illegible captions, clipped hero, double status bar or obscured bottom action.

## 4. IMPLEMENT THE TEN VISUAL REFERENCES AS ACTUAL ANDROID COMPOSE
Look in `01_APPROVED_REFERENCE_IMAGES/` (10 PNGs), compare with `02_PREVIOUS_AGENT_RUNTIME_SHOTS/` (12 historical), and create fresh captures. The references are approval authority for layout, proportion, motif and component hierarchy, NOT permission to display fake GB, % or entitlement values. Use real measured/Room/provider readings or honest unknown/empty states.

| ID | Native screen to complete (retain working back/route/events) |
|---|---|
| 01 | Home / Device Hub: one premium measured instrument, two-column service cards, real scan CTA, quick actions, coherent dock |
| 02 | Smart Clean file review: scoped groups, legitimate select/review, permission and OS confirm |
| 03 | Privacy Risk detail: supported Android permission readings, per-app action to OS Settings, no fake private telemetry |
| 04 | AI recommendation detail: source/evidence, user-controlled real action, no invented gain/confidence |
| 05 | App detail: real package/metadata/permissions, supported settings and uninstall review |
| 06 | Battery plan: actual battery APIs and honest estimates, no fake system boost |
| 07 | Action history: real Room journal, honest empty state, no fabricated recovered space |
| 08 | Large Files: existing `RealLargeFilesScreen` + `DeviceInsights`, MediaStore/SAF, previews/filters, safe review |
| 09 | Final Review: add distinct route if absent, controlled selection, estimate vs actual, consent and OS confirmation |
| 10 | Deep Scan result: actual completion/partial coverage, measured results and honest unmeasured scope |

Implementation must be composable and reusable, not screen-sized static images. Use the authentic KNOUX logo unchanged, dark navy, violet, selective magenta/cyan, refined single local glow, subtle native curves, round icon tiles and measured charts; match original hierarchy without turning every surface into a flashing neon card. Keep one visible scrollable screen area between system insets/top toolbar and fixed navigation/action rails, responsive layout (360dp baseline and Samsung 720×1600), Arabic RTL + English LTR, `fontScale=1.0` and ≥1.3, hit targets ≥48dp, Day/Dark where available and reduced motion. Pro badge active only from verified entitlement, not a second application identity.

**Visual approval loop for EACH screen:** reference open → exact discrepancy matrix (header, card bounds, tiles, CTA, hierarchy, type, colors, RTL, bottom dock) → code implementation → compile → real Samsung launch → valid runtime screenshot → side-by-side annotated comparison → fix deviations → tests → record evidence. Do not call the first successful build visual completion. Finish the ten; don't stop after Home.

## 5. SAMSUNG DEVICE & CAPTURE PRIVACY
Dedicated QA: owner's Samsung Galaxy A02s `SM-A025F`, Android 12/API31, local Windows ADB installation e.g. `D:\Android\sdk\platform-tools\adb.exe`. Verify exact authorized device live through a private local env (`KNOUX_TEST_DEVICE_SERIAL`) and `adb -s`; never target itel or emulator by fallback or hardcode device identifier in public files. Do not run an unscoped `connectedDebugAndroidTest` with multiple visible ADB transports. Use synthetic UUID-owned test fixtures for any deletion; don't touch personal data.

Last report: 74/74 JVM unit PASS, current debug APK hash `B2B4AAA0BABA2F3E4386B57FAEFF17D260D7C2A3DDBC24937D76E4AA65300483`, installation on Samsung reported success, only Large Files screenshot nonempty (no owner visual approval), 9 routes produced 0-byte capture. `FLAG_SECURE` may block screenshots: inspect supported in-app privacy toggle and obtain explicit owner consent before temporarily adjusting it; restore via legitimate UI/API and re-check the setting. **Never patch DataStore/Room bytes, disable security without consent, or fabricate captures.** If capture blocked, status is `BLOCKED_VISUAL_CAPTURE`, not visual PASS.

## 6. PRODUCT BEHAVIOR, TESTS & DELIVERY
Each real service must remain: OS/provider scope → permission → selection/review → explicit consent → actual allowed operation → re-query → Room history → retries/failures/restore where supported. No fake metrics, fake Pro, preview backend or unsupported Android authority. Treat a unit-test pass as a unit-test pass only; build alone proves no visual fidelity.

After integration, run actual project-appropriate commands (adapt modules if needed):
```
$env:ANDROID_HOME='D:\Android\sdk'
.\gradlew.bat :app:compileDebugKotlin --console=plain --no-daemon
.\gradlew.bat :app:compileDebugAndroidTestKotlin --console=plain --no-daemon
.\gradlew.bat :app:testDebugUnitTest --console=plain --no-daemon
.\gradlew.bat :app:assembleDebug --console=plain --no-daemon
```
Inspect XML pass/fail/skip counts and actual exit codes. Guard shared Gradle service against concurrent writers. Only run Samsung instrumented tests against the verified authorized target; inspect test harness because Gradle's connected task may affect all devices / clear app data. Logcat scrub and screenshots must not reveal private identifiers/tokens.

**Required final evidence:** exact source roots and local/remote SHAs; active agents/ownership; unique changes saved and merge ledger; source files/commits integrated; tests PASS/FAIL/SKIP and actual command outputs; APK SHA and installation target; ten per-screen visual states and screenshots; functional service/re-query/Room proofs; Pro badge truth; remote push/CI status; security/QA blockers; disk free before/after and anything actually reclaimed; next safe action. No `DONE`, `VERIFIED`, `RELEASED`, `100%`, or visual PASS without the required independent evidence.

## 7. DO NOT TOUCH THE SEPARATE PRO APP
`K:\KNOUX-AI-Pro` is distinct. Do not access/edit its source during PC integration, copy either application's assets/code, push its master, reset it, or change its package id. Its local clone lagged the remote 2 commits on audit; resolving that is a **separate follow-up** after PC milestone. GitHub visibility on Pro was PUBLIC, not the owner's intended PRIVATE; flag security for explicit follow-up. Both builds have `com.knoux.ai`; do not overwrite one with the other on Samsung.

**BEGIN NOW WITH READ-ONLY REALITY → RISKS → UNIQUE WORK → GAPS → OWNERSHIP → SAFE INTEGRATION.** Then execute and prove the merged UI, not another document-only plan. If an essential ownership/consent gate is blocked, produce the exact blocking evidence and STOP safely before writing; never work around it destructively.
