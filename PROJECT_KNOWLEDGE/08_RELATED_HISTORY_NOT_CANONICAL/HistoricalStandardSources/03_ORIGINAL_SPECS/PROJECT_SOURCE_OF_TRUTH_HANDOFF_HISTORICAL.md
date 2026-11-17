# KNOUX AI — Comprehensive Project Knowledge & Engineering Handoff

**Document type:** Project source / decision ledger / implementation handoff  
**Snapshot date:** 2026-11-13 (Asia/Dubai)  
**Product:** KNOUX AI — Android mobile application under KNOUX parent brand  
**Founder:** Eng. Sadek Elgazar  
**Brand domain:** knoux.store  
**Document state:** HISTORICAL / [REDACTED_DEVICE_SERIAL] [REDACTED_DEVICE_SERIAL] — **NOT** a certified live-state audit  
**Classification:** Internal engineering reference; **no credentials included**.

> **Read this first.** This document consolidates owner decisions, retrieved master specifications, and historical reports. It does **not** prove the current repository state, current deployment, credentials, passing tests, or feature completion. At the beginning of any implementation session, inspect the active checkout, actual code, Git, tests, screenshots, APIs, and runtime. A reported PASS without reproducible evidence is a report, not verified proof.

## 1. Executive summary and the non-negotiable scope boundary

KNOUX AI aims to become a real, production-grade Android device intelligence and management product. The owner has declared the **approved mobile visual design phase complete** and authorized real end-to-end implementation of every service, customer control, persistence layer, permissions workflow, Cloud/Pro feature, and verification gate. Development must add functionality **inside** the approved product rather than redesigning established screens or substituting glossy mockups for real Android operations.

Two project trees have been referenced, and they are **independent products/workspaces**:

| Environment | Canonical path | Rule |
|---|---|---|
| Android-phone Linux/OpenCode mobile workspace | `/root/workspace/KNOUX-AI` | **Primary target of the M00–M15 mobile execution contract.** Do not merge or synchronize its code, `.git`, assets, APKs, databases or history with Windows. |
| Windows project / PC development environment | `D:\KNOUX-AI` | Independent workstream with separate PC00–PC13 reports. Preserve it; do not treat PC progress as proof of mobile M-stage completion. |
| GitHub URL previously given for KNOUX AI | `https://github.com/daynightae-cmyk/KNOUX-AI.git` | Repository URL provided by owner; verify each local tree's `git remote -v` and ancestry before assuming it tracks this remote or shares identical history. |

**Decision precedence:** newest explicit owner instruction for the **specific project** > currently approved project assets and actual code > project master documents > previous assistant/agent claims. Never silently overwrite independent work.

## 2. Owner intent / product quality definition

1. All customer-facing services must **actually operate**, with genuine Android data, explicit scope and permissions, safe user-authorized actions, and a re-query proving results.
2. Preserve all approved design elements, spacing, palette, animation, logos, headers, UI text, onboarding, splash, Login, OTP, Google Sign-In, Passkeys, biometrics, Home, Pro and functional services.
3. Never replace a real workflow with `Preview only`, animation-only feedback, fake GB reclaimed, fake device speedups, fabricated scanning, or silent cleaning.
4. All relevant controls must exist and work: select/filter/sort/open/review/confirm/cancel/retry/restore/export/history as applicable. If Android or a provider prohibits an action, give an exact and truthful capability reason rather than fake success.
5. `main` is the integration target, but unique work on branches, worktrees, stashes and untracked files must be preserved and inspected before any merge or cleanup.
6. Avoid destructive Git operations and file deletion without clear evidence and express owner authorization. Never interfere with another active agent writing overlapping files.
7. Completion requires code **and** test evidence **and** runtime validation **and** Git/CI/deployment verification where applicable.

## 3. Approved visual identity and design decisions

### 3.1 Product styling

- Brand hierarchy: **KNOUX** is the parent brand; **KNOUX AI** is the product name.
- Visual language: refined **Deep Navy / Violet / Cyan glassmorphism**, luminous professional highlights, dark background, tasteful gradients and microanimations. Some approved surfaces include rose accents; inherit exact existing project design tokens rather than inventing numeric hex values.
- Use the **actual approved KNOUX AI logo**, not a generic fingerprint/logo redesign. Preserve its shape, safe-area, visual weight and intended motion.
- English product name/brand signature is required on the founder/onboarding treatment where explicitly approved; the owner rejected inserting an Arabic transliteration of the brand in that position.
- Maintain responsive spacing, screen insets, RTL/LTR behavior, readable typography and accessible tap targets. No overlapped logo/header or cropped text.
- Login is one composed, fixed full-screen surface; **no vertical scrolling** on Login. Content-rich Home and services may scroll as needed.

### 3.2 Login and identity flow

- The owner explicitly rejected the **giant central fingerprint** repeatedly. Never restore it.
- Authentication choices are presented **professionally and compactly** as coordinated options including **Google, Passkey, biometric/fingerprint and Guest** (and the existing password/OTP flows as required).
- Fingerprint/biometric appears as a **small affordance alongside** the other authentication methods, typically lower in the layout, not as a hero illustration.
- Login, OTP input and post-login **Identity Success** are separate logical screens; Identity Success must not be turned into a second OTP input form.
- Preserve an uncluttered header with logo and correct spacing. The final visual intent was inspired by the calm composition of owner-supplied later references, recolored and rebranded to KNOUX AI rather than copied wholesale.
- Original referenced images and approved screenshots must be reviewed locally before any pixel-level UI modification; text descriptions are **not** a substitute for the actual original PNGs.

### 3.3 Splash and cinematic onboarding

- Preserve the existing Splash **smile** animation and its exact completion event.
- Product onboarding was described as **three cinematic chapters/slides** leading into existing Login. The latest targeted implementation request concerned the final two chapters: **Ecosystem** and **Founder**.
- An owner-approved Salawat audio recording was supplied: `https://mp3tourl.com/audio/1794520051232-3d804d0a-2f23-4c00-9879-af7542c6616f.mp3`.
- Requirement: download, inspect/validate audio encoding, package the actual recording as an **offline Android resource**, and trigger playback **exactly once after** the existing Splash smile animation completes. Do not replay on every recomposition, navigation or lifecycle resume. Honor device audio state and do not hide failures.
- Targeted instruction file previously cited: `KNOUX_AI_MOBILE_OPENCODE_TWO_SCREENS_SALAWAT_DIRECT_MP3_FINAL_20261113.txt`.
- Confirm audio usage rights and actual packaged file/instrumented playback; having a URL or directive alone is not proof of implementation.

### 3.4 Visual QA rules

- Compare approved reference screenshot(s) vs actual runtime screenshot(s) of the same route, viewport, orientation, theme and locale.
- Inspect logo/header alignment, safe areas, overflow, auth-control proportions, gradients, translucent layering, animation timing and transitions.
- A green Gradle build or CI result does **not** prove visual fidelity; broken approved UI is a regression.

## 4. Architecture, functional expectations and data path

The mobile engineering contract specifies **real Android APIs**, scoped storage, MediaStore, SAF, system intents, Room persistence and migrations, DataStore for preferences, WorkManager for eligible background jobs, explicit permission/capability routing, typed models, auditable local history, optional authenticated cloud sync and clear feature entitlement gates.

**Reference workflow for every user action:**

`User-selected scope → permitted native data source → accurate results → inspect/select controls → user action → Android confirmation if required → actual operation → fresh re-query → measurable outcome → Room history → retry/restore where possible → automated & device tests → evidence checkpoint`.

Security requirements:

- Minimal permissions, privacy-preserving defaults, scoped storage, correct Android version/target/API behavior, no unsupported root-like claims.
- Do not imply broad access to protected app data, caches, private data, or silently grant OS rights unavailable to the app.
- Never delete user data for QA. Use safe fixtures or explicitly approved targets; protect a keeper copy in duplicate tests.
- Do not advertise unsupported security/encryption assurances unless they have been implemented and independently validated.
- Offline Free operations remain functional even when optional Supabase, billing or model APIs are absent; mark only their specific external features `BLOCKED_EXTERNAL`.

## 5. The fixed 16-stage mobile delivery contract (M00–M15)

The stages below are **requirements**, not completed work. The sequence is dependency-safe and should be reconciled against the actual source before any new implementation.

| Stage | Scope and minimum acceptance | Status at document creation |
|---|---|---|
| **M00 Foundation** | Permission/capability router, real API adapters, typed models, Room + migration tests, DataStore, WorkManager, action/operation repository, real source proof | **Needs live verification** |
| **M01 Storage Analyzer** | Actual accessible volume/files inventory, honest totals, inspect/filter/search/sort, open/share/rename/move/copy/trash/delete in supported scopes, history/re-query | **Needs live verification** |
| **M02 Duplicate Finder** | Streaming cryptographic exact hash, stable identity, safe keeper protection, optional similar-media mode, batch selection and post-action verification | **Needs live verification** |
| **M03 App Residue** | Only accessible/user-selected or app-owned residual candidates, accurate attribution, exclusions, review, authorized removal and audit log | **Needs live verification** |
| **M04 Privacy Sweep** | Real permissions/privacy inspection, actionable system controls, consented vault if implemented, security claims validated | **Needs live verification** |
| **M05 App Manager** | OS-scoped installed apps list, granted usage data, real app detail and supported settings/uninstall/install intents, constraints explained | **Needs live verification** |
| **M06 Battery** | Battery/thermal/charging readings, defensible diagnostics/alerts and system actions; **no fake boost** | **Needs live verification** |
| **M07 Deep Scan** | Real scan modules and scope, progress/cancel/results, traceable findings/remediation, evidence and independent tests | **Needs live verification** |
| **M08 Smart Clean** | Real selected cleanup scope, confirmation, Trash/permanent distinctions, restore where available, scheduling/history and proof | **Needs live verification** |
| **M09 Reports** | Data reconciled with actual operations, history/trends/analytics/export/schedules and consistency tests | **Needs live verification** |
| **M10 AI Optimization** | Evidence-backed local or approved remote suggestions, explicit user execution and measured outcomes, no false device claims | **Needs live verification** |
| **M11 Home** | Every navigation route works; live aggregate KPIs, honest empty/loading/error states and approved visuals preserved | **Needs live verification** |
| **M12 Settings** | Real permission/privacy center, language/locale, scheduling, user controls, export/erase and device limitations | **Needs live verification** |
| **M13 Authentication** | Existing Guest/Login/OTP/Google/Passkeys/biometrics properly integrated with sessions/backend; preserve screens | **Historical implementation claims; live E2E unverified** |
| **M14 Optional Cloud + Pro** | Explicit opt-in Supabase staging/RLS/sync, billing/entitlement/trials and **50** customer-usable Pro gates | **External/provider work partly prepared; live verification needed** |
| **M15 Release** | QA matrix, debug/release signing, privacy/Play compliance, performance/security, distribution artifacts and actual release evidence | **Not established** |

### 5.1 Ten full service contracts / 50 Pro capabilities

The retained master specifies **10 services** under M01–M10 with **five Pro capabilities per service** = 50 total. The authoritative feature-level contract is the original master, not this abbreviated inventory. Each feature must be located by exact ID, inspected against current code and supported by tests; **50 documented ≠ 50 implemented**. Do not invent feature IDs in telemetry, billing or database schema.

Core customer-service catalog: Storage Analyzer; Duplicate Finder; App Residue; Privacy Sweep; App Manager; Battery; Deep Scan; Smart Clean; Reports; AI Optimization.

Examples of detailed required controls from the master for Storage Analyzer: volume/source picker; accessible scope and totals; category drill-down; large-file threshold slider; search name/type/date/size; sort and folder filters; open, locate, rename, copy, move, share, Trash/Delete where provider supports; protect/exclude; batch actions; CSV export; growth tracking; no double counting across overlapping URIs. The storage-ring/treemap must be genuinely interactive, not a frozen decorative preview.

### 5.2 Workflow/ledger artifacts that must persist

Target files (re-use if already present, never destroy current content):

- `docs/workflows/KNOUX_MOBILE_AUTONOMOUS_E2E.md`
- `docs/workflows/MOBILE_TASK_MANIFEST.json`
- `qa-evidence/e2e/EXECUTION_LEDGER.md` (append-only evidence)
- `qa-evidence/e2e/ACCEPTANCE_MATRIX.md`
- `qa-evidence/e2e/ACTIVE_TASK.json`

Each task should declare owner/agent, checkout/SHA, paths, baseline, exact changed files, user-visible behavior, permissions, unit/instrumented/UI tests, screenshots, measured outputs, failures and blockers. Status should distinguish `PLANNED`, `IMPLEMENTING`, `TESTING`, `REVIEW`, `VERIFIED`, `BLOCKED_EXTERNAL` and `FAILED`; do not infer verification from labels alone.

## 6. Authentication, Supabase and external integrations

### 6.1 Known identifiers and historical preparation

| Item | Known value / history | Evidence class |
|---|---|---|
| Android package ID | `com.knoux.ai` | Previously reported from environment inspection; recheck current manifest/applicationId |
| Supabase project | `tduofkizwkjtqcbklfah` | Previously reported as `ACTIVE_HEALTHY`; **live status not rechecked here** |
| Supabase URL | `https://tduofkizwkjtqcbklfah.supabase.co` | Public project endpoint identifier, not secret |
| Debug signing SHA-1 | `C0:58:EA:53:09:9B:1D:7F:C5:08:F9:39:83:91:F9:D5:B9:22:24:2C` | Historical debug fingerprint, **not proof of release key** |
| Device | `itel S686LN` previously connected over ADB | Historical device report, current connection unverified |
| Google OAuth client JSONs | Three downloaded OAuth client files reported on Windows | **Sensitive files; do not embed or copy in this handoff** |
| Secret staging location | `C:\Users\[LOCAL_USER]\Documents\KNOUX_AI_AUTH_PRIVATE_20261113` | Historical reported private location; access not verified here |
| Supabase schema | 13-table research schema referenced by master | **Design proposal until migrations/RLS are independently inspected** |

Three Google OAuth clients were previously discussed, alongside debug vs production package/SHA mismatch concerns. A prior report distinguished **local credential preparation** from **unfinished Supabase Google Provider configuration and live Sign-In validation**. Do not announce Google authentication working merely because an OAuth JSON exists, a local secret was saved, or Google Console/Supabase appeared configured.

### 6.2 Auth verification matrix

- Verify Google Console Android client `package_name`/SHA-1 for each debug/release build variant and the expected Web OAuth client used for ID token exchange.
- Check authorized callback/redirect configuration and actual Supabase Auth provider configuration through the authorized connector/API; never print secrets.
- Verify `access_token`/refresh behavior, callback/deep-link, cancellation, invalid token, logout/re-login, expired session and provider error cases.
- Verify Guest session semantics, native Credential Manager/Passkeys support and fallback, biometric enrollment/lockout/fallback and actual backend authorization where relevant.
- Inspect RLS per table and role, user ownership, migration execution, upload policies and opt-in synchronization; never equate schema drafts with deployed database.
- Release cannot rely on a debug keystore. Check release signing, Play App Signing and configured fingerprints independently.

### 6.3 External integration boundaries

- Supabase is an **optional cloud layer**, not an excuse to block independently implementable local core features.
- Billing/subscription, trials and Pro gates require real product configuration, entitlements, refunds/cancellation treatment and Google Play Billing policy verification. A visual Pro card alone does not grant or prove an entitlement.
- Remote AI/model integration needs valid authorized credentials, privacy terms and observable server response; fallback must report lack of service truthfully.
- Never paste or commit access tokens, service keys, OAuth client secrets, private keystores or credential JSONs. Audit for accidentally tracked secrets before pushing.

## 7. Historical work reports — NOT equivalent to live verification

These entries are retained so future agents do not overwrite or duplicate work, but each must be revalidated in its own workspace:

| Historical report | What was stated | What must be checked again |
|---|---|---|
| Windows project baseline | A prior `HEAD` was reported as `d561f82164a7e79288ffd04172b264098846d96b`; a later PC report referred to `d1b8067` synced with origin | Current Windows `HEAD`, `origin/main`, commits, dirty files and PRs |
| Windows functional tasks | PC00/PC01 reportedly integrated; PC02 Duplicate Finder in progress | Exact commits, tests, runtime & unique unmerged files; **not** mobile M00/M02 evidence |
| Windows test excerpts | JVM 16/16; UI 5/5; storage 8/9, then 1 media test reportedly passed with temporary permission | Capture actual full test invocation, logs, device permission model and current regression run |
| Mobile/desktop Visual Phase | Owner stated mobile visuals approved, and instructed focus on E2E features | Actual current screenshot comparisons, reference files and existing asset inventory |
| Auth support | Existing Login, OTP, Google Sign-In, Passkeys, biometrics described as already implemented by a prior project instruction | Examine full native wiring and attempt real auth journeys; no assumed PASS |
| APK/device checks | Prior report said a debug APK on device matched PC artifact | Recompute APK SHA-256, package/signature, install and launch behavior on current build |
| Supabase setup | One report said project healthy and credentials staged locally | Recheck provider/RLS/schema/migrations and execute live Google login without revealing secrets |
| Comprehensive master | A 2026-11-13 master was reported ~2,571 lines, ~282 KB, with appendices and feature contracts | Read the actual original master **entirely** before editing; do not pretend this handoff replaces its detailed SQL/feature definitions |

**Critical epistemic distinction:** A user instruction (“implement M00–M15”), an agent report (“PASS”), an artifact checked into Git, a green test job, and a release visibly running on a real phone are **five different evidence levels**. Record each separately.

## 8. Actual open issues / work requiring verification

### P0 — Blockers to trusted release claims

1. **Unknown current repository truth**: no live `pwd`, `git status`, HEAD, remotes, branches/worktrees, stash, reflog, active agents, CI or deployment were inspected while producing this file.
2. **Google OAuth end-to-end proof missing**: verify Supabase Provider and real device Sign-In/Sign-Out + refresh. Historical preparation must not be reported as completed integration.
3. **Debug vs release credentials/signing**: release keystore, Google/Play SHA registrations, API restrictions, and release build need actual validation.
4. **M00–M15 completeness unproven**: master describes requirements; no audited current source matrix for all 16 stages and 50 Pro features is attached here.
5. **Permissions and destructive operations**: need Android-version-specific capability tests and irreversible-action safeguards, especially scoped storage, cleanup and app-management operations.
6. **Real instrumentation/visual evidence absent from this snapshot**: test logs, screenshots, action traces, APK hash and device behavior must be gathered before VERIFIED.

### P1 — Important functional and design checks

1. Confirm the latest Ecosystem/Founder onboarding additions did not disturb Splash, Login, OTP, Google, Passkeys and biometrics.
2. Verify the Salawat asset downloaded correctly, is packaged offline, triggers **one time after** Splash smile completion, and handles lifecycle/audio focus correctly.
3. Confirm no giant fingerprint reappeared; login layout is composed, responsive and non-scrollable.
4. Validate local Room database schema/migrations, DataStore persistence, schedule jobs and history consistency.
5. Inventory missing operational controls across all ten services, empty/error/loading states, cancel/retry/resume and recovery/undo paths.
6. Confirm Cloud and Pro configuration does not lock the working Free/offline product.

### P2 — Delivery hygiene

1. Update task manifest, acceptance matrix and append-only ledger with exact SHA and dated evidence.
2. Centralize and retain approved reference images and screenshot comparisons in the correct **mobile workspace**.
3. Audit resource size, accessibility, Arabic/English RTL, theme contrasts, performance, crashes and Play policy.
4. Maintain one active writer per overlapping file and avoid duplicate heavy builds on constrained hardware.

## 9. Safe engineering continuation — REALITY → RISKS → UNIQUE WORK → GAPS → ACTION

### Step A. Identify the current checkout without modifying it

From **the selected workspace only**:

```bash
pwd
git rev-parse --show-toplevel
git rev-parse HEAD
git status --porcelain=v1 -uall
git branch -vv
git remote -v
git worktree list --porcelain
git stash list
git reflog -10 --date=iso
```

Check whether a writer, build or long-running agent currently owns files before editing. Check free disk/RAM and Android SDK/JDK/Gradle capabilities. Avoid destructive `git reset --hard`, `git clean`, branch deletion, forced pushes or cache cleanup.

### Step B. Preserve and reconcile unique work

Inventory tracked/modified/untracked files, active branches, worktrees, PRs, reflogs and local-only commits. Identify meaningful unique work, approved visuals and QA. Do **not** equate a behind-main branch with disposable work. Compare actual `HEAD` and `origin/main` by SHA. Do not copy assets or source between Windows and phone projects.

### Step C. Build an evidence-based gap matrix

For each M-stage and each of the 50 Pro feature IDs from the **original master** record: source path, implemented UI control, native API and permission, persistence, action engine, happy/error/denial tests, device screenshot, test output, Git commit, CI, blocker and current status. Distinguish not present, partial, local-uncommitted, committed-not-pushed, pushed-not-merged, merged-not-shipped and VERIFIED.

### Step D. Deliver the next safe vertical slice

The master prioritizes **M00 Foundation → M01 Storage Analyzer → M02 Duplicate Finder**, then subsequent stages. If earlier slices are already valid, resume at the first genuinely missing acceptance point. Every slice must run one authorized real operation and prove it by re-query, persist a history record, pass applicable tests, and capture a Git checkpoint. If a cloud/billing secret is inaccessible, mark only that external feature blocked and continue offline work.

### Step E. Verification before any PASS or release announcement

Capture at least: exact checked-out SHA and remote tracking; dirty state; tests with command, count and exit code; Gradle assemble and instrumentation results; runtime launch/click path; screenshots/video; permission-denial behavior; APK SHA-256 and signature; cloud/auth transaction evidence where relevant; CI job status; release/deployment URL or distribution record if claiming shipped.

## 10. Source index and important original artifacts

The following were identified through previous work and Library search. Their complete bytes/content were **not** copied into this handoff, and this summary should **not** replace reading them for detailed implementation:

1. `KNOUX_AI_MOBILE_OPENCODE_SUPREME_FULL_FUNCTIONAL_E2E_MASTER_20261113.md` — comprehensive Android master, original research, 16 stages, ten service contracts, 50 Pro feature definitions, design/auth/cloud/SQL/release details and preserved appendices.
2. `KNOUX_AI_MOBILE_OPENCODE_SUPREME_FULL_FUNCTIONAL_E2E_MASTER_20261113.txt` — alternate plain-text copy of master.
3. `KNOUX_AI_MOBILE_OPENCODE_FINAL_END_TO_END_MASTER_20261112.md` — preceding full E2E contract and project boundaries.
4. `KNOUX_AI_PHONE_FULL_FUNCTIONAL_AUTONOMOUS_E2E_WORKFLOW_20261112.md` — provenance and task execution workflow.
5. `KNOUX_AI_MOBILE_OPENCODE_TWO_SCREENS_SALAWAT_DIRECT_MP3_FINAL_20261113.txt` — specific Ecosystem/Founder chapters and Salawat audio requirement.
6. Owner's original PNG/screenshots showing preferred compact login and identity layouts; **inspect the assets themselves** before visual work.
7. `docs/workflows/KNOUX_MOBILE_AUTONOMOUS_E2E.md`, `docs/workflows/MOBILE_TASK_MANIFEST.json`, `qa-evidence/e2e/EXECUTION_LEDGER.md`, `qa-evidence/e2e/ACCEPTANCE_MATRIX.md`, `qa-evidence/e2e/ACTIVE_TASK.json` — expected in-repo status evidence if present.
8. Project Git history, QA screenshots, real APKs, Supabase migrations/RLS/provider configuration, and Android device logs — authoritative only after direct inspection.

## 11. Rules for subsequent AI agents consuming this source

**Do not interpret this document as proof that the described changes are already in the current working tree.** Treat owner's design decisions, scope exclusions, no-data-loss rules and real-operation acceptance requirements as binding; treat historical SHA/status/test statements as leads requiring fresh inspection. Read the full master and its appendices before changing architecture or Pro schemas. Keep Windows and Android-phone projects separate. Preserve the approved mobile visual system; no giant biometric hero and no fake preview-only service completion. Deliver real end-to-end functionality with evidence, one safe checkpoint at a time. Do not present `DONE`, `PASS`, `100%`, `PRODUCTION`, `MERGED` or `RELEASED` without independently reproducible project-specific evidence.

---

### Provenance and limitations

This handoff was assembled from conversation history, saved context and **retrieved snippets of named master files** on 2026-11-13. The currently selected ChatGPT browser tab was **not readable** due to browser security policy. No repo filesystem, remote branch, cloud configuration or Android runtime was directly inspected in this session. This is a thorough knowledge/decisions handoff, **not** a snapshot, repository audit, recovered full chat transcript or a certificate of functionality. Private tokens, passwords, OAuth client secrets and key files are intentionally excluded.
