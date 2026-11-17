# KNOUX AI PRO — CANONICAL WINDOWS EXECUTION & VISUAL CONTRACT
**Authoritative owner correction • 2026-11-15 • Standalone Android application • EXECUTE IN PLACE**

> **THIS REPLACES THE PREVIOUS PHONE/ANDCODE RECOVERY HANDOFF.** Do not follow older prompts instructing you to work in `/root/workspace/KNOUX-AI`, scan Android Downloads, recover TAR/ZIP archives from the itel, or transfer the development APK to Windows. The owner has designated the two existing Windows checkouts below as the only canonical local source repositories. Their previous phone/PC development history is historical provenance, not a current workspace instruction. This is a new conversation, **not a new project**.

## 0. CANONICAL PATHS AND STRICT REPOSITORY BOUNDARIES

| Field | KNOUX AI — Standard | KNOUX AI Pro — TARGET |
|---|---|---|
| Windows source checkout | `D:\KNOUX-AI` | **`K:\KNOUX-AI-Pro`** |
| GitHub | `https://github.com/daynightae-cmyk/KNOUX-AI.git` | **`https://github.com/daynightae-cmyk/KNOUX-AI-Pro.git`** |
| Base branch | `main` | **`master`** |
| Independence | Completely separate Android product | Completely separate Android product and Git history |
| Modification authority for this mission | **NONE** | **ONLY this repository**, after preflight |

- This mission's absolute source path is **`K:\KNOUX-AI-Pro`** and nowhere else. Do not fall back to `D:\KNOUX-AI`, `/root/workspace/KNOUX-AI`, `K:\KNOUX-AI` or a newly cloned duplicate. Do not create extra copies or backup folders by default.
- **Never copy, sync, import, merge, replace, or cherry-pick Standard source into Pro.** No actions in D: beyond passive validation necessary to avoid conflict; even there, prefer not to touch it.
- `K:\KNOUX-AI-Pro` is a Windows checkout of the Android **Pro** app. Its earlier development on the itel does not make the itel its current source host. Keep independent signing, Room data, history, auth, QA and deployment.
- Never perform `reset --hard`, `git clean`, force push, history rewrite, deletion of unique files/assets/stashes/worktrees, or automatic cleanup of old phone archives. If duplicate artifacts are later considered for cleanup, inventory and require explicit approval for destructive steps.
- Important identity collision: if Pro and Standard currently use the same Android `applicationId` (historically `com.knoux.ai`), the phone may treat a Pro installation as an upgrade or reject a different signing certificate. Inspect actual application IDs, signatures and installed packages; **do not uninstall, overwrite, rename packages, change OAuth config or signing without approval**.

## 1. CURRENT VERIFICATION SNAPSHOT — RECHECK LIVE BEFORE ACTIONS

The following was directly read on 2026-11-15 from authorized PC `DESKTOP-VQKU9P8` and GitHub; it is NOT a perpetual guarantee:

- `D:\KNOUX-AI`: `main`, HEAD `78391aa927b3629fcf856de04b10dae23fc2881c`; matches GitHub `main`; **20 status entries at inspection**, so there is concurrent/unique work to protect; NOT a Pro source.
- **`K:\KNOUX-AI-Pro`: `master`, HEAD `f04f634b03f8c79d606fd2973b24dc793d8d7661`; GitHub `master` matched this SHA.** `git status --porcelain=v1 -uall` returned **0 entries**, one worktree and 0 stashes at inspection.
- Pro repository contains `.git`, `android`, `app`, `database`, `design`, `docs`, `gradle`, `prompts`, `provenance`, `qa-evidence`, `scripts` and Gradle wrapper files. The Git history includes previous `b271d8c...` recovery/auth commit; **do not restart or recover source from itel**.
- **Security discrepancy:** the owner's desired classification for Pro is private, but the linked GitHub repository metadata actually returned **`visibility=public`** on 2026-11-15. Do not claim privacy or assume secrets can be published. Report as `REPO_VISIBILITY_MISMATCH`; do not change access settings without explicit approval. Review for exposed credentials only using safe authorized methods; never print secret values.
- Previous 2026-11-14 agent reports of Kotlin/Compose compile errors or M09 recovery gaps are HISTORICAL, not current build failures. Inspect current source and build logs before repeating or "fixing" them.

**Mandatory live initial commands, executed from `K:\KNOUX-AI-Pro` only (adapt to current authorized Windows shell):**

```powershell
$repo = 'K:\KNOUX-AI-Pro'
if (-not (Test-Path -LiteralPath $repo)) { throw "Canonical Pro checkout not found: $repo" }
$root = (git -C $repo rev-parse --show-toplevel).Trim()
if (($root -replace '/','\').TrimEnd('\') -ine $repo.TrimEnd('\')) { throw "Repository identity mismatch: $root" }
git -C $repo rev-parse HEAD
git -C $repo branch --show-current
git -C $repo status --porcelain=v1 -uall
git -C $repo worktree list
git -C $repo stash list
git -C $repo reflog -n 8
git -C $repo branch -avv
git -C $repo ls-remote origin refs/heads/master
# IMPORTANT: redact credentials before ever printing remote URLs in logs.
```

Then inspect any running agents/IDE/Gradle/ADB jobs and worktree ownership before edits. If HEAD or dirty state changed from snapshot, **follow current reality**, inventory changed/unique work, and avoid overlapping active edits. Do not create a new branch merely to avoid inspecting the current master; preserve repository policy and only commit reviewed paths after verification.

## 2. DEVICES: ITEL IS PERSONAL, SAMSUNG IS QA ONLY

- **itel S686LN**, Android 15/API 35: owner's personal/previous development handset. **NOT a current source checkout or build lane.** Never scan its Downloads for source, install experimental builds on it, or use it to replace the authorized QA target.
- **Samsung Galaxy A02s**, model `SM-A025F`, Android 12/API 31, `armeabi-v7a`, 720×1600 @280dpi: dedicated physical Android QA target. Previously recognized from Windows with hardware serial `[REDACTED_DEVICE_SERIAL]` via USB and Wireless ADB; previous free internal storage about 2.8GB. **All are historical observations until rechecked live.**
- Windows ADB was `D:\Android\sdk\platform-tools\adb.exe` on `DESKTOP-VQKU9P8`; discover current actual executable. Android Emulator may also be attached — it is NEVER an automatic substitute for Samsung.
- The Pro source and Samsung test host are **both on the Windows PC**. Build directly from `K:\KNOUX-AI-Pro`; **NO APK transport from AndCode/itel is needed.** You may run Gradle via CLI without opening Android Studio.

**ADB gate:** Enumerate devices and wireless services. Choose an explicit transport and run read-only `getprop ro.serialno`, `ro.product.model`, `ro.build.version.sdk`, `ro.product.cpu.abilist`, `wm size`, and free-space checks. Require the Samsung hardware identity to match before install/test. For Wi-Fi, dynamically discover/revalidate endpoint and map it to the same hardware serial; never hardcode stale port/IP or use a Shizuku startup port as the PC ADB port. Use `adb -s <VERIFIED_SAMSUNG_TRANSPORT>` for EVERY mutating/test command. If USB is unplugged, verify Wi-Fi actually continues before claiming wireless independence. If inaccessible, report `SAMSUNG_QA_BLOCKED` and continue safe offline Gradle tests. No automatic switching to itel/emulator.

## 3. FINAL PRO VISUAL IDENTITY (INDEPENDENT FROM STANDARD)

**Obsidian Navy / Royal Violet / Electric Cyan / Champagne Gold.** The earlier Arctic Depth / Frostline is historical and superseded **in color direction**. Figma geometry, accepted official assets, approved motion, and functional shell remain protected.

- Obsidian Navy: sophisticated matte/deep base; Royal Violet: sparing royal-depth identity; Electric Cyan: limited true readings/active focus; Champagne Gold: subtle Pro signature where legitimate, not fake subscription entitlement.
- Preserve exact **original KNOUX logo** artwork and proportions. A cinematic luminous **KNOUX stag** is permitted **only when an owner-approved actual stag asset is present**; no invented replacement logo, giant glowing animal covering controls, perpetual particles, or ornamental clutter.
- Native tactile/glass cards with quiet elevation, fine controlled edges, one prominent primary action, more breathing room, shorter labels, legible Arabic typography; no overwhelming saturated purple-neon, no "web dashboard" mockups, no fake OS status bar.
- Compose Material 3 + Kotlin native widgets, tokens for Day/Dark, typography, shapes, spacing, elevation, surface roles, state/accent, and motion; consistent RTL/LTR and fontScale ≥1.3 with accessibility/contrast/insets; devices ~360×800 and reference geometry 390×844dp; interactive hit targets ≥48dp; bottom navigation is real.
- 120–220ms purposeful animations; scanning motion only for real ongoing scan, honor reduced motion, battery and thermal limitations. No generic infinite animated HUD/hero.
- Owner-attached **25 Pro reference images** are the Pro visual authority when actually available to the agent. **Inspect visually; never claim visual analysis of unseen images.** If inaccessible, implement only defensible written/Figma fundamentals and mark `VISUAL_REFERENCE_NOT_VERIFIED`. Older 20 neon screens of the independent Standard product are **not** Pro reference images.
- Figma geometry reference: `https://www.figma.com/design/MLUbH2QkR1tOlj0qzk2O4e/Design-a-futuristic-smart-home-app-UI-with-Figma--Community-?node-id=2010-6`; original nodes `2:2` Home, `12:45` instrument, `12:147` analytics; KNOUX-specific nodes `2010:2` Home, `2010:5` Scan, `2010:7` Smart Clean, `2010:8` Duplicates, `2010:9` Residue, `2010:10` Privacy, `2010:11` Battery, `2010:3` AI, `2010:4` Reports, `2010:6` Apps. Reuse existing real layouts/components; Figma is layout authority, not new Pro color authority.

## 4. PRESERVE ALL APPROVED AND UNIQUE WORK

Do not rework the approved Splash smile, actual completion-driven audio, onboarding/founder signature, Login, OTP, Google, Passkeys, Biometric, Identity Success, approved logo, or established navigational shell without explicit scoped authorization and reproducible defect. No giant fingerprint. Preserve `MainActivity`, current routes/back behavior, Android API controllers/providers, Room migrations, auth, licenses, history, animations, UI assets/QA and work of other agents. No static screenshot pretending to be an app. Implement Pro styling in-place through reusable components and screen refactoring rather than wiping screens to make Kotlin compile.

Entitlement handling must remain real and lawful: `FREE`, `PRO_ACTIVE`, `GRACE`, `PENDING`, `OFFLINE_UNVERIFIED`, `EXPIRED`, `REVOKED`, as supported by existing code. No hardcoded payment activation, fake licensing, or unverified Supabase provider claims. Supabase/Auth integration needs real external provider/domain/credential/redirect/device checks, not compile-only assurances.

## 5. TEN SCREENS + ACTUAL INTERIOR WORKFLOWS

1. **Home / Precision Pro Hub:** real last scan & device metrics when available; compact clean 2-column native service cards and proper Home/Clean/Tools/Reports navigation; single start-scan CTA. Unknown values show Not scanned rather than invented values.
2. **Deep Scan / Obsidian Sonar:** tactile state-aware native Canvas dial, real scope and examined items/bytes; progress from actual controller; truthful cancel/review/permission/partial/error states.
3. **Smart Clean / Review First:** scoped candidates, protected/excluded items, select, preview, OS consent, execute, re-query actual bytes, Room history; don't one-tap destroy content.
4. **Duplicate Media / Velvet Gallery:** real media thumbnails/metadata and duplicate groups; protected original keeper; select each copy, confirm Android trash; verify and persist.
5. **App Residue / Provenance Explorer:** validated folder/source context, ownership/risk limitations, authorized SAF scopes, review/skip/exclude; no assumptions that foreign files are safely disposable.
6. **Privacy / Permission Atlas:** supported installed-app permission inspection and OS settings intents with honest unsupported/unknown states. No imaginary privileged access.
7. **Battery / Royal Polar Meter:** actual OS percentage/charging/temperature and persisted real history; no fake health or time-to-empty calculations.
8. **AI Optimization / Evidence Cards:** provider-backed or explicitly local heuristics, explain source and capability; user selects/approves actions. No secret cloud upload.
9. **Reports / Obsidian Insights:** Room/OS-backed analytics, empty states, accurate verified space recovered and events; no invented chart points.
10. **App Manager / Control Registry:** Android-visibility-aware installed apps, actual package metadata, permissions, settings/uninstall intents with OS/user confirmation.

Apply same visual system to interior pages: Duplicate Group, Storage Details, Large Files, Privacy Risk Detail, Battery Plan, Recommendation Detail, Action Log, Final Review. Preserve actual navigation routes. End-to-end contract per service: `Android Provider/API → actual discovery → Permission/Selection → Review → OS confirmation → Action → Re-query → Room history → Retry/Restore if supported`. For dangerous tests, use synthetic UUID-tagged fixtures and permission-aware cleanup; never delete real photos, docs, private app data or user-installed apps. Continue existing M00–M15 from verified manifest checkpoint, not from scratch.

## 6. EXECUTION ORDER (DO, NOT JUST PLAN)

1. **REALITY:** Inspect Windows K: checkout HEAD/local↔remote, status -uall, branches/worktrees/stash/reflog, docs/task manifest, active agents, disk space, build/toolchain and APKs. Redact secrets in outputs. Check `master` fast-forward relationship. Identify evidence-based current scope and all unique work.
2. **REPRODUCE:** Examine current build state first (historical `DuplicateFilesScreen.kt` issue is not proof of failure now). Discover Gradle modules and tasks, run current `compileDebugKotlin` or actual valid task safely. Do not start a conflicting Gradle session while another agent owns it.
3. **REPAIR SAFELY:** Fix reproduced Kotlin/Compose errors while preserving screen structure and service callbacks. Re-run relevant compilation, unit tests and `assembleDebug`; record exact exit codes, totals, APK hash, `applicationId`, API/ABI, signing and SHA. No build-only PASS for runtime.
4. **DESIGN:** Implement centralized final Pro tokens and shared native components, then improve Home and Deep Scan to establish a consistent approved reference, then remaining services/interiors. Compare before/after actual screenshots, restore any regressions. Maintain Day/Dark/RTL/LTR/insets/font scaling and approved image/asset fidelity.
5. **REAL DEVICE:** Verify Samsung live serial and transport, check installed package/signature collision and free space; install only a verified compatible APK on Samsung; run real screen navigation, permissions, synthetic cleanup workflows, screenshots/logcat/Room and instrumentation. Use explicit `adb -s` or single-target instrument setup. If blocked, report precise blocker, do not fake completion.
6. **HISTORY:** Document evidence and reconcile M00–M15 real status. Stage specific reviewed paths; make non-destructive local commit when safe; push fast-forward to Pro `origin/master` only; verify `git ls-remote` and CI status. No unrelated or automatic source synchronization to D:.

**Safety:** No forced history updates, `reset --hard`, `clean`, deletion of stashes or untracked assets, unauthorized GitHub visibility changes, suppression of failing tests, or claims of released/published based only on an APK compile.

## 7. REQUIRED EVIDENCE REPORT

- **Source lane** `K:\KNOUX-AI-Pro`; Pro `master` base/current SHA, remote SHA and dirty state; active agents and unique-work protection.
- **QA lane** `DESKTOP-VQKU9P8` Windows ADB → authorized Samsung `SM-A025F` with identity verified; whether USB/Wi-Fi works live and whether cable removal was actually tested.
- Code paths changed, approved Figma/images actually inspected and comparative screenshot locations.
- `compile`/unit tests/instrumentation/E2E command + exit code + PASS/FAIL/SKIP totals, duration, device/runtime screenshot/log evidence.
- APK filename, build time, SHA256, signing/package compatibility; installation and launch evidence.
- Service-specific Android provider and Room evidence, re-query results and precise reclaimed bytes; accurate unsupported capabilities.
- Auth/entitlement verification, CI and release status separated from build/test status.
- Git local commit SHA, remote `master` SHA, push verification and any CI result.
- `REPO_VISIBILITY_MISMATCH` if Pro remains public although owner expects private. Never publish credentials, private wireless device endpoints, hardware serial or personal test logs in public repo.
- REALITY → RISKS → UNIQUE WORK → GAPS → SAFE NEXT ACTION.

**FINAL INSTRUCTION:** Start now in **`K:\KNOUX-AI-Pro`**, **not on itel/AndCode**. Preserve the current independent Pro Android source and build directly on Windows, test only on the dedicated Samsung, apply the approved premium Pro design and continue real M00–M15. NO PHONE SOURCE RECOVERY. NO SECOND COPY. NO CROSS-REPO MERGE. NO FAKE PASS.
