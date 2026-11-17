# KNOUX AI — Dual-Repository & Native UI Audit
**Read-only engineering audit • 15 November 2026 (Asia/Dubai)**

## Executive decision
**Continue production integration and visual correction on `D:\KNOUX-AI` (`KNOUX-AI/main`) now.** Preserve `K:\KNOUX-AI-Pro` as an entirely independent Android product and a later, separate workstream. Inside the PC repository only, reconcile the native-UI branch (`K:\KNOUX-AI-NATIVE-UI-20261114`) with the 18 tracked + 4 untracked in-progress Kotlin/data source files in `D:\KNOUX-AI`. Select the best behavior, geometry and graphics per component. Do not wholesale overwrite either implementation; do not cross-merge repositories. **NO merge/commit/push was executed in this audit.**

## 1. Verified live facts
| Item | Windows-developed KNOUX AI | Mobile-origin KNOUX AI Pro |
|---|---|---|
| Canonical Windows checkout | `D:\KNOUX-AI` | `K:\KNOUX-AI-Pro` |
| GitHub | `daynightae-cmyk/KNOUX-AI` | `daynightae-cmyk/KNOUX-AI-Pro` |
| Base branch | `main` | `master` |
| Local HEAD | `78391aa927b3629fcf856de04b10dae23fc2881c` | `f04f634b03f8c79d606fd2973b24dc793d8d7661` |
| Observed live remote | `78391aa927b3629fcf856de04b10dae23fc2881c` | `b32c2786938940a9601e239fbb1399607f36414b` |
| Local changes | 18 modified tracked + 4 untracked Kotlin/data files; additionally untracked `.work/continuation-review-20261115` reference/QA artifacts | clean working tree locally, **but remote 2 commits ahead** |
| Kotlin under app/src/main | 55 | 39 |
| Build/test evidence | debug APK exists; original JVM XML 74/74 PASS, 6 suites; `compileDebugKotlin`, `compileDebugAndroidTestKotlin`, `testDebugUnitTest` succeeded on prior checkout; Samsung installed debug APK | no local APK and no local unit or instrumentation result XML found in inspected paths |
| CI on GitHub | Actions API returned 0 workflow runs | Actions API returned 0 workflow runs |
| Repository visibility reported by GitHub | public | **public**, whereas owner intended private |
| Gradle `applicationId` | `com.knoux.ai` | **also `com.knoux.ai`** |

GitHub Pro has two additional commits beyond the local Windows Pro checkout: `42dde3957806c09b85adb354c71e39a21f2fd55a` (2026-11-15 04:35 Dubai: identity overhaul) and `b32c2786938940a9601e239fbb1399607f36414b` (2026-11-15 05:40 Dubai: duplicate-screen compilation corrections). The latter commit *claims* a successful APK build and hash `8ddf5d3ff212feb64ed7472b3a9c0a377aba339240e06c48460b19eb5aef6023`; independent build and runtime were **not** reproduced during this audit.

**Package collision:** Equal `applicationId`s mean the two independent Android source trees are not installable alongside each other as distinct applications in the same Android user profile. Compatible signatures can update/replace; incompatible signatures can reject installation. Do not change IDs casually: release signing, OAuth redirects, Google services configuration, Android App Links, existing data and store identity must be assessed first. Do not install Pro over the standard app on Samsung during this visual-QA milestone.

## 2. UNIQUE WORK NOT YET INTEGRATED — KNOUX AI repository
### Canonical D: dirty source
18 existing modified files: `AndroidManifest.xml`; `DeepScanReal.kt`, `DuplicateReal.kt`, `HomeScreen.kt`, `KnouxComponents.kt`, `KnouxIdentity.kt`, `KnouxScaffold.kt`, `KnouxTheme.kt`, `KnouxUniverse.kt`, `MainActivity.kt`, `MasterScreens.kt`, `PreviewData.kt`, `PrivacyReal.kt`, `ResidueReal.kt`, `ServicePlaceholder.kt`, `SmartCleanReal.kt`, EN/AR `strings.xml`.

4 source files currently untracked: `IdentityKit.kt`, `NeonKit.kt`, `StorageScreens.kt`, `data/DeviceInsights.kt`. Treat all as high-value product source, not disposable.

Observed `D:\KNOUX-AI\.work\continuation-review-20261115\` contains the ten approved 941×1672 visual references, twelve historic 720×1600 runtime images, reports, XML semantics and failed/valid screen captures. Many 0-byte captures have SHA-256 prefix `E3B0C442`; **never** treat these as screenshot evidence. Keep originals safe; sanitize before any public commit.

### Independent linked worktree — same PC repository, NOT Pro
`K:\KNOUX-AI-NATIVE-UI-20261114` on branch `knoux-native-ui-20261114`, HEAD `6f1d7392ec614c3a36b60d4c734142bc8a7818af`.

Git commit relation `main...knoux-native-ui-20261114` = `0 / 5`: the branch is 5 committed changes ahead of main and main has 0 distinct commits against their shared base, **but D: has significant uncommitted implementation**. Those five commits include: `efc8e03` Figma-measured shell and Home, `a907bcd` native Tools and Reports, `772c385` RTL byte-number isolation, `64cead0` native brand fingerprint, and `6f1d739` motion proof documentation.

Worktree additionally has 6 modified tracked paths (`PreviewNavigationTest.kt`, `AndroidManifest.xml`, `MainActivity.kt`, `ServicePlaceholder.kt`, EN/AR strings) and **one untracked** `NativeTools.kt`. The branch contains additional new `NativeBrand.kt` and `NeonArt.kt` committed files plus an `AbyssSonarBytesTest.kt` regression test. No source may be discarded because a branch is older than another.

Multiple Manus and OpenCode desktop processes were present. A process name is NOT proof of file ownership or active writing; inspect their task sessions and last source edits, coordinate, and do not modify owned files concurrently. Do not terminate agents or kill a shared Gradle/ADB server blindly.

## 3. Code-level UI assessment — which pieces to keep
| UI concern | Closest safe source | Verified code-level basis | Risk / prescribed fusion |
|---|---|---|---|
| Screen shell geometry & responsive behavior | Native UI worktree | Explicit Figma 390×844 mapping; `NativeSpace`, `NativeStorageOverview` 108dp, service grid/min 104dp, `stackForFontScale` >1.24, 48dp buttons, `NativeScreenTitle` | Merge semantic layout improvements, not entire `MainActivity.kt` / `HomeScreen.kt` unreviewed |
| Hero richness, violet/cyan, service-detail anatomy | D dirty working tree | `IdentityKit.kt` defines world-specific `IdentityHero`, `IdentityMetric`, ring, local gradients and selected color accents; `StorageScreens.kt` implements large media | Current screenshot has severe visible overlap; constrain each hero to its own route and available layout space |
| Brand identity / icon treatment / restrained motion | Native UI branch | `NativeBrand.kt`: stable `BrandTone`, `ServiceIconPlate`, `BrandHairline`, 120–300ms interactions and motion controls | Use palette direction from approved references; don't flatten cards excessively or duplicate accent layers |
| Actual provider/Room services | Canonical D dirty tree and integrated main | Real `DeepScanReal`, `DuplicateReal`, `PrivacyReal`, `ResidueReal`, `SmartCleanReal`; previously verified PC00–PC02 and later evidence | Never overwrite controllers/data/permissions with prettier stub pages or fake figures |
| Large Files & measured data | D dirty tree only | `MainActivity` maps `Page.LARGE_FILES` to `RealLargeFilesScreen`; `DeviceInsights.largeMedia` and `StorageScreens.kt` new | Native UI branch `MainActivity` instead maps battery/apps/storage and **lacks D's Large Files route**. Wholesale branch replacement would regress this route |
| Battery / Apps & native route behavior | Native worktree | `MainActivity` routes battery/app/storage to `NativeBatteryScreen`, `NativeAppsScreen`, `NativeStorageScreen` and there is new `NativeTools.kt` | Reconcile each route with D's active service screens instead of creating two alternate implementations |
| Subscription badge | Current entitlement implementation | Crown visible in owner references is an **active verified Pro subscription state inside D app**, not a separate Pro application theme | Never force ACTIVE Pro by design fixture, local bool, debug, or static art |

### Severe visual regression: precise observation, root cause NOT proven
The user-provided latest screen (`79718.png`) shows simultaneous, partially transparent Home title (`Smart Device Hub`), Large Files title and large-media hero, with overlapping tile/filter/service contents, plus repeated labels and action areas. That is **not a visual PASS** and cannot be fixed by changing hex colors alone.

Source inspection of D's `MainActivity.kt` shows a `NavHost` with animated enter/exit transitions inside a weighted `Column`, an app top bar and bottom navigation; debug routing uses `LaunchedEffect(visit)`. The `Large Files` UI itself is vertically scrollable with a fixed `IdentityActionBar`. These are plausible areas for a route-transition/capture-timing or layout-lifetime defect, but **no single root cause was established**. Diagnose idle-state versus transient transition frames before changing anything. Establish exactly one active route after a settled navigation and no modal/sheet left layered in front; check debug-visit and `NavHost` transitions, nested scroll, `safeDrawingPadding`, fixed toolbars/action rail and constraints at multiple font scales.

### Visual baseline
Ten owner-approved PNGs: 01 Home, 02 Smart Clean Review, 03 Privacy Risk Details, 04 AI Recommendation Details, 05 App Details, 06 Battery Plan, 07 Action History, 08 Large Files, 09 Final Review Confirmation, 10 Deep Scan Results. These are **design authority**, but example numbers (GB, %, apps, battery performance) are illustrative, not real measured results. 12 previous-agent screenshots are historical *before* states only.

The supplied `VISUAL_EVIDENCE_REPORT.md` records 74/74 JVM tests, APK `B2B4AAA0BABA2F3E4386B57FAEFF17D260D7C2A3DDBC24937D76E4AA65300483`, Samsung SM-A025F API31 successful installation and launches, but 9/10 invalid/empty screenshot captures and one valid Large Files screenshot **without visual approval**. There is no distinct Final Review route yet; debug route is reused as `clean-center`. Source/reports did not show successful 10-screen visual comparison. Android `FLAG_SECURE` is a plausible/recorded capture blocker: only adjust using supported in-app settings with owner consent, not direct DataStore binary writes or security bypass.

### Evidence comparison
- D APK at audit: `D:\KNOUX-AI\app\build\outputs\apk\debug\app-debug.apk`, 17,014,748 bytes, modified 2026-11-15 04:54 Dubai; SHA-256 matches evidence report `B2B4...0483`.
- D local JUnit XML: 6 suites, 74 total, 0 failures, 0 skipped. Android instrumentation compilation previously passed; **instrumentation runtime was not established for the current integrated/hybrid tree**.
- Worktree's internal documentation reports a separate 80/80 test pass with 7 suites, API26 emulator visual captures; this is historical evidence from its own branch, not current merged-product proof.
- No GitHub Actions runs appeared for either repo in live API lookup; no CI PASS or deployed release claimed.
- Disk-space audit: C: 5.94GB free, D: 2.29GB free, K: 9.12GB free. Avoid duplicate SDKs, emulators, backup explosions and deleting unique assets.

## 4. EXACT SAFE INTEGRATION PLAN (NOT EXECUTED)
**Gate A — Freeze & ownership.** Recheck live branch/remote SHA, `status -uall`, worktrees, stash, reflog, modified/untracked paths, agent sessions and active Gradle tasks. Obtain non-overlapping source ownership; if another writer is still editing shared files, remain read-only and report `OWNERSHIP_BLOCKED`.

**Gate B — Preserve.** Record a content-hash/path manifest for *every* unique source/asset (including untracked Kotlin and QA); preserve current changes through an explicitly scoped local source checkpoint **only after writer releases ownership**. Protect the worktree's six tracked and one untracked changes equally. Do not stage public device IDs, raw logcat, credentials, or generated build trees. Never use `git reset --hard`, `git clean`, force-push, branch deletion, blind `git checkout --theirs`, or unreviewed overwrite. Avoid making large duplicate repository copies; prefer Git object/commit checkpoints where possible.

**Gate C — Reconcile within the PC repo.** Account for the branch's five commits and all uncommitted changes. Perform a conflict-aware merge only after protecting both sides; review overlapping `MainActivity`, `HomeScreen`, `MasterScreens`, `KnouxComponents`, `KnouxScaffold`, strings and manifest line-by-line. Prefer semantic/manual fusion (measured shell geometry + branded service plates + D real service routes) rather than taking either side wholesale. Ensure all unique public behavior is represented in code or explicitly documented as intentionally superseded after evidence. **Do not touch/import any source from `K:\KNOUX-AI-Pro`.**

**Gate D — Verify merge BEFORE design expansion.** Compile Kotlin and androidTest source, run unit tests, inspect screen routes, permissions and persistence; test the known Large Files and App/Storage/Battery routes. Run Samsung-only instrumented targeted tests. Commit a clear merge/reconciliation with exact SHA; push fast-forward to `origin/main` only if verified compatible with live remote and release of file ownership. Verify `git ls-remote` after. If tests fail, do not claim integration verified or push an unverified integration as complete.

**Gate E — Repair regression & complete visuals.** Reproduce/resolve visible multiple-screen overlap, then implement ten screens sequentially against original PNGs. Preserve actual API states and absence-of-data UI. Dedicated Final Review may need route and consent-aware state. Do not represent an E2E flow by a fabricated animation. Screenshot through supported consented capture state; test EN/AR, RTL, font scale ≥1.3, insets, Samsung 720×1600, at least one alternate width and reduced motion. Create side-by-side pixel/layout reports. User acceptance remains pending until owner reviews actual screenshots.

## 5. What to do with the independent Pro repository
Keep `K:\KNOUX-AI-Pro` on hold for product work until the D visual stabilization milestone; do a separate *non-destructive* fast-forward reconciliation of its 2 missing commits only after checking local source and agents, then independently build/test. Its latest code changes touch the splash/auth and theme as well as Duplicate Files; protected approved flows need visual regression review. Plan separate Android package identity and signing/OAuth consequences before attempting simultaneous installation with D, and resolve the reported public visibility if private was intended. No cross-repo import, merge or sync.

## 6. Actual completion boundary
**DONE in this audit:** read-only source/worktree/GitHub comparison, changed-file/branch analysis, Gradle identity check, current APK/JUnit inspection, evidence-file analysis, architecture recommendation and execution handoff.

**NOT DONE:** source merge, agent coordination, new build, new Samsung E2E run, new screenshots, visual parity sign-off, local commit, remote push, release, CI pipeline. These cannot be represented as complete based on this report.

**Next safe action:** hand the companion execution prompt to the designated new single source writer; require `AUDIT + OWNERSHIP + PRESERVE` evidence before allowing any Git integration or visual edits.
