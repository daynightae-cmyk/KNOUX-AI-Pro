# KNOUX AI — FINAL OPENCODE MOBILE END-TO-END EXECUTION CONTRACT

**Edition:** 2026-11-12 — owner-authorized real-functional development, Pro integration and persistent workflow.  
**Deliverable:** SINGLE self-contained Markdown file. The complete 1,202-line Deep Search / 16-phase / 13-table master follows this final addendum intact.  
**TARGET PROJECT:** `/root/workspace/KNOUX-AI` — Android-phone Linux OpenCode workspace **ONLY**.  
**NOT THE SAME PROJECT AS:** `D:\KNOUX-AI` on Windows. No code, `.git`, APK, database, assets, branches, histories or data may be copied/merged/synchronized between them. Common engineering patterns are conceptual only.

## FINAL P0 OWNER AUTHORIZATION — START REAL ENGINEERING NOW

The owner has decided to proceed with **actual End-to-End functionality**. Do not stop because an older instruction said to wait for the PC visual gate, a previous screen preview, or approval at every phase. However, the owner's PC visual approval is **not automatic visual acceptance of the independent phone UI**: preserve phone-approved designs and report any phone-specific visual defects truthfully. Do not reopen a finished UI merely to redesign it; focus on real features and fix only verified regressions.

**You are an executing engineer, not a plan writer.** In this current OpenCode session:
1. Inspect the REAL phone repo `pwd`, `git rev-parse HEAD`, tracking remote (`git remote -v` and `git ls-remote` only when authorized/possible), `git status --porcelain`, worktrees, stash, reflog, active writers, modified/untracked files, runtime API and available storage. The historic SHA inside the original research is NOT the current checkout target.
2. Read the **complete preserved master below**, its 16-phase section list, local SQL research, and any newer source/skills/instructions within this SAME phone repo. Detect implementation that already exists; do NOT redo or replace valuable work.
3. Reuse/initialize `docs/workflows/KNOUX_MOBILE_AUTONOMOUS_E2E.md`, `docs/workflows/MOBILE_TASK_MANIFEST.json`, `qa-evidence/e2e/EXECUTION_LEDGER.md`, `ACCEPTANCE_MATRIX.md`, and `ACTIVE_TASK.json`. Single writer for overlapping source files; model-agnostic checkpoints.
4. Begin **M00 foundation with actual code**, then **M01 Storage Analyzer first real vertical slice**; after verified M01 execute M02 Duplicate Finder, M03 App Residue, M04 Privacy, M05 App Manager, M06 Battery, M07 Deep Scan, M08 Smart Clean, M09 Reports, M10 AI, M11 Home, M12 Settings, M13 Auth, M14 Optional Cloud + Pro and M15 Release QA. Preserve the original dependency-safe ordering. Where shared groundwork is necessary, implement the minimum real adapters for the first service, not a parallel rewrite of the whole application.
5. For every service: **user-selected scope → real Android data → full customer controls → authorized real action → OS user confirmation → operation/re-query → verified result → local Room history → cancel/retry/restore where possible → tests → screenshots/runtime → safe Git checkpoint → automatically next**.
6. NO fake GB reclaimed, fake app cleanup, fake battery boosts, silent file deletion, prefilled “success,” server authentication based on animation, or Pro-locked nonfunctional placeholders. Do not artificially restrict an available user-authorized action to a read-only preview.
7. Create a useful code/test/evidence checkpoint in this session, not merely a roadmap. If truly blocked by external console/product keys, leave the functional offline slice working, set `BLOCKED_EXTERNAL`, and advance to the next independent implementable item. Do not claim work completed after session termination.

### Android customer controls are product features, not optional UI decoration

For Storage/Smart Clean/Duplicates/Residue, implement supported **Open / Share / Rename / Move / Move to Trash / Permanently Delete / Filter / Sort / Inspect / Select / Exclude / Batch Review / Re-query / History / Restore** in the correct provider and permission scopes. Do not pretend that all file providers support all actions: detect operations and offer a real eligible path. Android OS/system consent remains mandatory when required, and permanent removal needs clear customer confirmation. Read permissions do not imply write/delete rights.

Use true storage/provider APIs: `StatFs`, `ContentResolver`, `MediaStore`, Storage Access Framework, `DocumentsContract` and OS-backed `MediaStore.createTrashRequest` / `createDeleteRequest` where supported. For Android versions below the API threshold, choose correct fallback behavior instead of invoking unavailable APIs. Manage partial media grant, dynamic revocation, scope re-selection, cancellation and durable progress. If legitimate special all-files access fits the product and distribution rules, validate its actual eligibility and user consent; don't assume Play has approved it or that it grants other apps' private storage. Optional authorized Shizuku/ADB or enterprise managed-device mode is a separately consented, transparently labelled mode, never silent privilege elevation.

For App Manager: use real PackageManager-scoped visibility, system-managed uninstall/launch/settings and opt-in UsageStats; advanced freeze/backup/clone only where a separately verified authorized platform mode genuinely implements them. For Privacy: real permission assessment, system actions, optional properly tested Keystore vault; tracker *blocking* only when an actual consented tested filtering engine exists, otherwise do not market that capability. For Battery: use true Android battery/thermal observations and system settings rather than impossible CPU tuning claims. For AI: evidence-backed local rules and genuine approved actions, not invented gains.

### ONE Mobile Pro center — premium features must work

Implement a single integrated **KNOUX AI Pro** experience inside the current mobile UI and routing. The Windows implementation had a premium crown and glass cards; this is only a conceptual design description, not permission to copy Windows sources or trust its billing. Respect the *existing mobile* KNOUX identity; clean, restrained crown, compact responsive premium screens, correct insets, Arabic RTL and English LTR.

Pro is NOT an excuse to ship 50 empty cards. Treat the following **50 feature IDs as implementation/acceptance requirements**, with a working mechanism and truthful capability state per feature. Keep FREE core functionality actually usable. Do not sell or mark `VERIFIED` any unimplemented, externally blocked or platform-inaccessible feature. Stable ID strings are product requirements; they are **not** evidence that these IDs already exist inside the mobile source.

| # | Mobile Pro section | Five feature IDs | Customer acceptance focus |
|---|---|---|---|
| 01 | Smart Clean | `smart_clean_unlimited`, `smart_clean_scheduled`, `smart_clean_deep_analysis`, `smart_clean_priority`, `smart_clean_rollback` | Actual file review and authorized deletion, read-only scheduled discovery, local analysis, supported recovery journal; priority only for app-owned jobs |
| 02 | Duplicate Finder | `dup_verified_hashing`, `dup_keeper_protection`, `dup_smart_similarity`, `dup_batch_review`, `dup_recovery_journal` | Streamed exact hash, protected keeper, opt-in similar photo scoring, real Trash/Delete and verified recovery state |
| 03 | App Residue | `residue_own_cache`, `residue_accessible_candidates`, `residue_saf_profiles`, `residue_safe_trash`, `residue_history` | Real own-cache/user-granted accessible residue operations, selections, exclusions, consent, outcomes |
| 04 | Privacy Sweep | `prv_realtime`, `prv_vault`, `prv_tracker`, `prv_report`, `prv_history` | Permission-aware observations, real user data encryption/decryption vault when offered, accurate tracker awareness or tested blocker, evidence export |
| 05 | Deep Scan | `scan_malware`, `scan_hidden`, `scan_schedule`, `scan_custom`, `scan_forensic` | User-configured real scan and evidence; malware claims only if a verified detector/signature database exists |
| 06 | Reports | `rep_export`, `rep_history`, `rep_ai_insights`, `rep_compare`, `rep_schedule` | Actual Room records, genuine file exports, transparent insights, observed trends and opted-in reminders |
| 07 | App Manager | `app_batch`, `app_freeze`, `app_backup`, `app_usage`, `app_clone` | Real system app controls; advanced privileges honest and separately supported, no fake freeze/clone/backup |
| 08 | Storage Analyzer | `sto_detailed_breakdown`, `sto_large_files`, `sto_growth_trends`, `sto_capacity_planner`, `sto_export` | Source-backed filesystem/media metrics, real file tools, growth history and exports |
| 09 | Battery Saver | `bat_thermal_timeline`, `bat_charge_profiles`, `bat_usage_context`, `bat_smart_alerts`, `bat_recommendations` | Actual battery/thermal readings, optional usage stats, real alerts/settings actions |
| 10 | AI Optimization | `opt_gaming`, `opt_ai_learn`, `opt_thermal`, `opt_network`, `opt_scene` | Explanation-backed diagnostics and verified customer-applied OS/app changes |

For each ID store **feature status** separately from **subscription state** and **device capability**; examples: `CATALOGED`, `IMPLEMENTED_UNVERIFIED`, `VERIFIED_ON_SUPPORTED_SCOPE`, `BLOCKED_EXTERNAL`, `BLOCKED_PLATFORM_SCOPE`, `UNSUPPORTED_AS_CLAIMED`. A subscription unlock does not create missing OS capabilities. Where functionality has a real platform limitation, represent the exact state with a meaningful customer-controlled alternative instead of a dummy CTA.

#### Real membership, not client-generated keys

- The Windows visual prices of `29 AED/month` and `249 AED/year` were unverified display text; **do not reuse them as live product prices**. Actual Google Play purchase offers and localized prices come from real `ProductDetails` when products are configured and the required billing setup is approved.
- Trial codes must be backend-generated, cryptographically strong, expiring, single-use/account-bound and server-verified; NEVER use a random client-side `KX-...` string as entitlement authority.
- Feature gates must be backed by verified server-purchase or authorized store/backend states: pending, active, grace, hold, revoked, expired, restore, offline-unverified; include honest `CONFIGURATION_REQUIRED` until credentials and products exist. A `DataStore.isPro=true` is not authorization.
- Debug-only owner testing can simulate access inside explicit test builds, labelled TEST MODE; do not ship universal hardcoded bypass/password in release.
- Implement Google Play Billing and Google Play Developer API verification of subscriptions, acknowledgements, RTDN reconciliation and recovery, ONLY when authorized Play Console/backend is configured. Avoid placing Play/service-role secrets inside APK. Store only minimal purchase event/entitlement metadata and use actual user account authority. WhatsApp support is not generally a replacement for required digital purchase billing.
- The **13-table research SQL further below is an un-deployed reference design**, NOT a Pro backend already running. The existing SQL needs staging reconciliation, security corrections and genuine two-user JWT RLS tests. Add Pro entitlement/trial/billing tables *only after inspecting actual mobile project's schema and authorized target*. No remote production DDL by default.
- `cloud_history_enabled=false` by default. Local-first scans don't require Supabase login or subscription. Never upload filenames, paths, file hashes, images, installed app inventory or biometric data without a separately defined legitimate purpose and explicit informed consent.

### Quality gates: never replace proof with declarations

Before declaring **ANY** service finished, provide: exact current SHA and tree status; source API/permissions used; real in-app control path; operation performed on synthetic test targets; result re-query; errors/denial/cancellation/partial handling; persisted Room evidence; unit/instrumentation commands with actual exit statuses; Android API/device/screen captures; RTL/LTR/layout regression results; updated ledger and scope limitations. If instrumentation cannot run, report `NOT RUN`, not PASS. Use only an **explicitly authorized device/emulator**; don't silently install to another physical device. Never touch owner personal files in automated deletion tests.

Git integrity: preserve dirty/untracked/stash/parallel-agent work; no `git reset --hard`, destructive `clean`, force-push, branch pruning, duplicate projects or arbitrary asset deletion. Commit coherent verified changes on the live mobile project's established integration branch only after inspecting whether main/master is actually canonical; do not assume the Windows branch topology applies. If push is configured and authorized, use safe fast-forward then verify remote SHA; otherwise report committed-local/not-pushed. Disk on phone is constrained—delete only proven disposable reproducible caches once safely verified and authorized, never unique code/assets/evidence.

## DIRECT OPEN CODE START COMMAND

> READ **THIS ENTIRE FILE**, including the preserved original 16-phase Deep Search Master below. Work exclusively in `/root/workspace/KNOUX-AI`. First audit real source, current HEAD, status, available Android SDK/ADB/permissions, existing Room/Gradle, active agents and completed work. Resume rather than restart. Immediately implement M00 real data/domain/Room/permissions and M01 **Storage Analyzer real inventory + customer-authorized file action**; test with synthetic files and show actual evidence. Progress sequentially through all 16 phases, integrating the functional Pro mapping in each section without changing the established task graph. Implement actual user-controlled Trash and Permanent Delete where provider/user approval permits, with result verification. Preserve the approved mobile UI and every unique asset. Do not touch Windows PC. Run actual tests, record PASS/FAIL/NOT RUN and SHA. After each verified independently executable section, commit safely and continue the next; don't stop after planning, emit fake completion, fake paid entitlements, or claim release without live proof.

---

# ORIGINAL RESEARCH + WORKFLOW MASTER (PRESERVED IN FULL BELOW)

# KNOUX AI — MOBILE FULL-STACK END-TO-END MASTER EXECUTION & DATA PACK

**MASTER FILE / TEXT-ONLY / SINGLE HANDOFF / 2026-11-12**  
**Canonical application:** `/root/workspace/KNOUX-AI` (OpenCode on the Android phone's isolated Linux workspace)  
**Intended agent:** Beem or any code-capable text-only OpenCode agent  
**Mission:** Complete every existing Android service as a fully customer-controlled workflow (real data, user-selected operations including authorized Trash/Permanent Delete, permission grants, execution and verified results), persistence, tests and runtime evidence; automatically proceed to the next independently implementable section.  
**NON-NEGOTIABLE:** Every advertised customer feature needs actual end-to-end execution, not preview-only functionality. Never touch, read from, merge into or synchronize with the independent PC project `D:\KNOUX-AI`.

> **EXECUTE, do not merely summarize this document.** Read it as the authoritative functional and quality contract. Do not expect the model to receive screenshots, videos, ZIP images, or other visual input. The local repo's current UI/assets and preserved visual identity are the source of truth. Do not reconstruct the app, create another shell or replace approved designs. Do not claim any feature is real merely because the screen displays a value. Do not ask for confirmation between ordinary implementation sections. Pause only for a true authorization/secret/unsupported-platform blocker; record it and continue independent work.


## P0 — PERSISTENT AUTONOMOUS WORKFLOW CONTROLLER (2026-11-12)

**Owner's most recent workflow decision:** Execute all services under a Kiro-style, **model-independent** workflow with persistent checkpoints, not an isolated one-shot prompt. This operational amendment takes precedence over older requests to stop for A/B/C after each screen. It does **NOT** modify service requirements, the 13-table research SQL or the precise 16-phase sequence below. It does not imply that a background worker can run after the agent session ends.

### W0.1 — STRICT PROJECT IDENTITY AND INDEPENDENCE

- THIS WORKFLOW IS FOR THE PHONE LINUX APPLICATION ONLY: `/root/workspace/KNOUX-AI`.
- The independent WINDOWS PC project `D:\KNOUX-AI` is entirely OUT OF SCOPE. Never copy, import, merge, cherry-pick, sync, reset or delete its code, images, assets, local Git metadata, database or test artifacts. Workflow *principles* may be the same; source repositories remain completely isolated.
- The current mobile source and existing owner-approved designs are authoritative. Historic mobile archive SHA references in this master are evidence of history only, not the current checkout target. No source replacement from any old snapshot.
- The mobile agent must **not assert** it has Kiro's native workflow UI. Use whatever task/agent APIs OpenCode + Beem actually expose; if no native workflow is supported, implement the equivalent as explicit file-backed tasks and a single active coding worker with independent verification passes.

### W0.2 — INITIALIZE OR RESUME, NEVER RESET

First inspect, *without modifying code*: current directory, Git HEAD and `origin/main` if configured, current branch, tracked/untracked status, all worktrees, stashes/reflog, available filesystem, active agents/processes, Android environment and battery/disk constraints. Find any existing `AGENTS.md`, `.agents`, `docs/workflows`, and `qa-evidence/e2e` files before creating/altering them. Preserve existing instructions; append a clearly dated owner override for the current mobile End-to-End phase if conflicting old UI-only rules exist. Never overwrite another agent's active task state or QA evidence.

Within this **mobile repo only**, reuse or create these durable local files:

- `docs/workflows/KNOUX_MOBILE_AUTONOMOUS_E2E.md` — workflow operational rules and project identity.
- `docs/workflows/MOBILE_TASK_MANIFEST.json` — machine-readable 16-phase work graph and per-feature subtask IDs, dependency edges, ownership, statuses, acceptance tests and checkpoint refs.
- `qa-evidence/e2e/EXECUTION_LEDGER.md` — append-only audit of actual results, SHA, commands, tests, screenshots, blockers and next task.
- `qa-evidence/e2e/ACCEPTANCE_MATRIX.md` — every visible Free/Pro service control and its tested actual user outcome.
- `qa-evidence/e2e/ACTIVE_TASK.json` — exactly one file-writing service owner with observed baseline SHA, relevant source paths, status, current checkpoint and expected next task. Prefer explicit file lock / compare-and-swap when available; don't claim OS-process locks that don't exist.

Write an **immutable snapshot of pre-edit Git status and SHA** in the ledger before changes. Do not create new duplicate repos or proliferation of branches.

### W0.3 — TASK GRAPH: EXISTING SIXTEEN PHASES, UNCHANGED

Use the precise 16 phases **already specified below** as the task source of truth, with IDs and dependency edges:

`M00 Foundation → M01 Storage Analyzer → M02 Duplicate Finder → M03 App Residue → M04 Privacy Sweep → M05 App Manager → M06 Battery Saver → M07 Deep Scan → M08 Smart Clean → M09 Reports → M10 AI Optimization → M11 Home & Navigation → M12 Settings & Permissions → M13 Identity & Auth → M14 Optional Supabase Sync → M15 Release/Security/Play Gate`.

Create independent feature-level sub-tasks such as `M01.DATA`, `M01.PERMISSIONS`, `M01.ACTIONS`, `M01.HISTORY`, `M01.TESTS`, `M01.RUNTIME` under the correct module, using actual existing code ownership and dependencies. Never mark the whole phase VERIFIED because one subtask compiles.

### W0.4 — WORKFLOW ENGINE AND STATUS STATES

One controller, **one active writer for an overlapping file set**, one independent verifier pass. The controller may orchestrate tools/roles through Beem/OpenCode if available, but no simultaneous edits to shared source files. A lightweight review role is allowed after implementation. Prefer a measured task graph over agents performing redundant code-generation.

State machine:

`QUEUED → PREFLIGHT → IMPLEMENTING → UNIT_TESTING → RUNTIME_TESTING → INDEPENDENT_REVIEW → INTEGRATION_REVIEW → VERIFIED`.

On actual failure use `FAILED_VERIFICATION`, on a real external dependency use `BLOCKED_EXTERNAL`, on an unavailable platform permission/use case use `BLOCKED_PLATFORM_SCOPE`, and for work kept but not integrated use `IMPLEMENTED_NOT_MERGED`. A failed test may be repaired and reverified; no automatic fake green states. A service blocked for one optional external capability does **not** prevent independently implementable local features from proceeding.

Use evidence for **every status transition**. Task manifest must record:

`task_id, parent_phase, goal, dependencies, owner, current_SHA, impacted_paths, status, started_at, last_checkpoint, evidence_paths, test_commands, test_results, runtime_device, known_risks, blockers, safe_next_action`.

Timestamps/SHAs must come from actual tools, not a guessed example. Prefer append-only transition history; don't erase prior failures.

### W0.5 — COMPLETE FUNCTIONALITY GATE — ZERO PREVIEW END-STATES

For each service implement the full customer path from REAL discovery and proper permissions through inspection, selection/exclusion, configuration, genuine authorized ACTION, re-query/verification, durable history, cancellation/error/retry and restore where a provider actually supports it. For file cleanup give user-controlled **Trash and Permanent Delete** when legitimately supported; neither silently delete their private data nor artificially restrict a supported customer action to read-only. If a required OS prompt cannot be bypassed, launch it properly and let the CUSTOMER decide. Never write production code that uses a timer or canned fixture number as the underlying operation.

A feature's acceptance requires all of: real Android API/provider; customer-facing control; permitted operation; accurate post-operation evidence; durable Room state; permissions/denial/grant revocation handling; relevant tests; actual runtime screenshot and navigation; no UI regression/RTL damage. `Build successful` alone is never FULLSTACK/VERIFIED. Preserve functional FREE services and implement honest extra Pro capabilities, not a pricing card pretending to work.

### W0.6 — SAFE AUTOMATIC PROGRESSION

After each phase: verify changed files/test exit codes; capture runtime evidence on an **explicitly authorized device/emulator**; inspect Git changes and other agent work; preserve unique untracked assets; record ledger; safely commit only isolated verified modifications where appropriate; advance automatically to the next independent ready task. Do **not** hard reset, git clean, force-push, delete worktrees or replace main indiscriminately. If phone permissions or external credentials require owner input, record the exact blocker and continue independent tasks; don't invent grants. No constant A/B/C prompts.

**Resource and interruption tolerance:** Keep a useful completed checkpoint after each batch and a deterministic next-task pointer. On a later session or model change, read ledger/status and resume, not replay all work. No promise of continuous background operation after current runtime exits. Avoid unbounded loops, excessive phone CPU/battery drain and endless retries. Enforce max verification retry count per failed assertion (e.g., 2), then mark failed with error evidence and continue another safe task.

### W0.7 — ROLE HANDOFF WITHOUT DEPENDENCE ON ONE MODEL

- `controller`: inventory state, task graph, ownership, dependencies and safe next task.
- `implementer`: actual changes and tests in assigned paths only.
- `verifier`: review test outputs, API permissions, scope, screenshots and self-serving PASS claims; verify by reproduction where available.
- `integrator`: inspect unique work/other agents, record SHA and commit only consistent completed slices; do not create merges between independent phone and Windows repos.

All roles read the same manifest/ledger; no hidden context or 
claim that another agent's verbal assertion is test evidence. When the coding model lacks visual input, it may capture screenshots and geometry/semantics output, but MUST NOT claim human visual approval without actual human review.

### W0.8 — MANDATORY EACH-CHECKPOINT REPORT

At the end of EVERY checkpoint update the manifest and ledger with actual repository HEAD, branch/remote sync state, changed paths, untracked preservation, implementation scope, actual tests/exit codes/counts, runtime target, screenshots, APK path/SHA when produced, failed cases and block reasons. Distinguish: `implemented_verified`, `implemented_unverified`, `committed_not_pushed`, `merged_not_released`, `external_blocked`, `not_started`. Report exactly the next executable task, then proceed if runtime and authorization remain available.

**Start mobile workflow by inspecting the live mobile workspace and updating its existing task tracking; only then implement `M00` and continue in order. The rest of this Master contains the FULL ORIGINAL FUNCTIONAL SPEC and SQL — follow all of it.**

---

## OWNER P0 OVERRIDE — COMPLETE CUSTOMER CONTROL, NO PREVIEW PRODUCTS

**This section overrides ALL earlier lines in this handoff wherever they could be misunderstood as requiring read-only-only, preview-only, inert paywalled, or nonfunctional outcomes.** The previous Deep Search data, exact SQL reference and 16-phase program remain preserved. The goal is an actual Android tool in the customer's hands, not a decorative app that promises cleaning, optimizing, managing, securing or finding files without enabling a real operation.

### A. Every section must ship a complete, working customer journey

All service overview cards and detail screens must give the customer the fullest feasible self-service controls, depending on actual platform access:

`Choose scope / permissions → Collect REAL data → Inspect details → Search / sort / filter → Select / exclude / protect → Configure action → Review impact → Confirm (and Android system approve where needed) → EXECUTE → Show true progress → Cancel/retry where technically possible → Re-query reality → Display verified results / failures → History → Restore/undo where actually supported.`

Do not stop at read-only scans. Do not mark a section done after preview, recommendations, graphs, generic settings advice, or a beautiful authentication gate. Each advertised action must perform a real operation or be honestly marked unavailable with a tested actionable alternative. Do not replace functional controls with permanent preview banners. For selected file operations support **both Move to Trash and Permanent Delete**, where Android provider/API and explicit user confirmation support them. Show differences in recoverability clearly. User has final informed control within lawful Android permissions.

Do not introduce arbitrary restrictions to reduce implementation complexity. Implement all supported Android SDK access paths, test permissions, and work through platform-specific user approval dialogs. Where broad access is essential to core file-management/antivirus functionality, evaluate `MANAGE_EXTERNAL_STORAGE` as a genuine product option with device support, clear user grant and Google Play declaration/approval. Do not assume it is approved, available to all users, or that it allows other apps' private data. Handle partial grants and restrictions accurately.

Optional **advanced-user mode** can research and implement an authorized Shizuku/ADB companion, explicit enterprise Device Owner/Profile Owner edition or privileged manufacturer-specific integration where permitted. Such modes require separate opt-in, accurate capability labels, owner approval and device/Play policy testing. These capabilities are not a silent escalation, normal consumer feature or excuse to skip standard APIs. Differentiate STANDARD / BROAD_FILE_ACCESS / ADVANCED_AUTHORIZED / ENTERPRISE_MANAGED / UNAVAILABLE in a runtime capability registry.

**Safety is about consent and truth, not about disabling customer functionality.** For supported destructive operations, provide both reversible and irreversible choices with explicit confirmation. Never silently delete the customer's personal files, never claim permanent deletion if the data merely entered Trash, and never fake OS-level access. Synthetic emulator files are required for destructive automated tests; only the customer intentionally initiates real device operations.

### B. Ten service-by-service real customer outcome gates

1. **Storage Analyzer:** Actual disk/volume and accessible inventory, category breakdown, folders and files, large files, search/filter/sort, preview/open/share, supported rename/move/copy, user-authorized Trash/Permanent Delete, rescan verified difference, history and report. A dashboard pie chart is not sufficient.
2. **Duplicate Finder:** Scan accessible real bytes; exact SHA-256 plus separately labelled similarity; visual/file preview, group selection, one keeper protected by default, bulk review, user-approved Trash/Permanent Delete, provider result requery, recovery where supported and durable journal. A list of dummy duplicates is not sufficient.
3. **App Residue:** Scan app-owned and user-authorized shared folders, attribution confidence, exclude/protect, choose candidates, perform supported user-approved cleanup, verify actual results, retry/restore where possible. Cannot assert that other apps' inaccessible private caches were cleared.
4. **Privacy Sweep:** Real available permission/app visibility checks, risk evidence, per-app controls that open legitimate OS permission/notification/security actions, post-action recheck and audit/report; any advertised Vault must implement AES-GCM/Keystore import/export/unlock/restore; any advertised tracker **blocker** must have actual consented VpnService + filtering engine, tested rules, bypass/allowlist and stop/start, otherwise advertise awareness rather than blocking.
5. **App Manager:** Real scoped app list, search/filter/details, open/launch, uninstall via Android user confirmation, app info/permissions/storage settings, permitted sequential user-controlled batch actions, usage statistics with special access. Investigate dedicated authorized managed-device/advanced paths for freeze/backup/clone; do not fake such operations in a normal consumer APK.
6. **Battery:** Live BatteryManager/PowerManager data, charging/thermal timeline, threshold notifications, explainable recommendations, actual OS Battery Saver/optimization control via available settings/actions, post-return state recheck, history. Do not advertise fabricated hardware cycle counts, forced thermal control or earned extra hours.
7. **Deep Scan:** Full real scoped orchestrator; scan module options, progress from actual work, pause/cancel/retry when supported, finding details and evidence, select remediation and perform allowed action after consent, false-positive controls, export, scheduling and history. Malware claims require validated engine and dataset.
8. **Smart Clean:** End-to-end usable cleaner with category and candidate scanning, search/filter/select/exclusions, size estimates, preview for CONFIRMATION (not the end state), actual OS-mediated Trash or Permanent Delete and eligible own-cache actions, requery, verified result/history, cancel/partial/retry, supported restore and scheduled review notifications. Do not count a simulated GB as saved storage.
9. **Reports:** Actual Room history, date and category filters, source drilldown, verified trends, PDF/CSV export through document picker, opt-in scheduled summaries and history management. Static charts do not count.
10. **AI Optimization:** Real diagnostics using measured data and explainable decisions; configure and apply eligible **actual** user-approved device/app settings or existing KNOUX jobs, verify outcomes, log/cancel/undo where supported and learn from explicit feedback. Optional cloud LLM only if real credentials and consent are available; not a prerequisite for local optimization.

**Cross-cutting Home, Settings, Login, Sync and Pro:** Home reflects actual verified service status; Settings provides full permission/consent/history/notifications controls; biometric unlock must authenticate for real; cloud accounts and Passkeys require server verification; Pro must unlock genuinely implemented premium functionality through authoritative store/backend verification, not client-generated trial codes or a visual badge. Guest/local offline core services remain usable. Missing externally supplied credentials must be recorded precisely, but cannot excuse incomplete local functions.

### C. End-to-end acceptance gates — no convenient shortcuts

For EACH visible feature record in the execution ledger:

| Evidence key | Proof required |
|---|---|
| Customer outcome | What the user can do, not what the screen claims |
| Actual source | API/provider, permission and accessible scope with timestamp |
| Controls | Search, filters, selection, exclusions, action and confirmation UX as relevant |
| Execution | Real OS/app operation and true progress (not a timer) |
| Verification | Post-operation re-query, measured bytes/items/state and journal persisted |
| Failure/recovery | Denial, revoke, cancellation, partial success, restart, retries and supported Trash restore |
| Tests | Commands, exit codes, accurate counts and instrumentation/emulator evidence |
| UI | Real screenshot, header and bottom insets, Arabic/English, no clipping or disabled core CTA |
| Verdict | PASS only for actual successful end-to-end flow; otherwise PARTIAL / BLOCKED with precise scope |

A section is not complete while its main purpose remains a Preview or settings hint instead of a real supported workflow. Where a platform genuinely blocks a sub-feature, implement and verify all independent real operations, research an authorized user-controlled mode and **do not claim that sub-feature passes**. Automatically continue to the next independent section without telling the owner to choose A/B/C.

### D. Specific Android methods to implement where appropriate

- API 30+: `MediaStore.createTrashRequest(resolver, uris, true)` for recoverable media Trash; `MediaStore.createDeleteRequest(resolver, uris)` for immediate permanent deletion with the OS confirmation dialog, plus `createWriteRequest` for eligible editing.
- SAF: `DocumentsContract.deleteDocument` if the document provider exposes `FLAG_SUPPORTS_DELETE`; move/rename through provider-supported operations and flags. Check permissions per exact URI; never assume `content://` is a filesystem path.
- Broad shared storage where legitimate: investigate `MANAGE_EXTERNAL_STORAGE` via `Environment.isExternalStorageManager()` and `ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION`, document Play declaration and fallback, no grant spoofing and no access promise for `/Android/data` or other apps' private storage.
- Package management: real app launch/details/uninstall intents with Activity Result, package visibility and optional `UsageStatsManager`; no silent system app manipulation.
- Real scheduling: WorkManager for read-only scans, user review/approval for destructive actions. Do not claim exact timer precision.
- Separate free-tier and premium **actually working functionality**, not purely commercial screens. Backend-only entitlement and audited billing when externally authorized.

Official anchors (confirm current platform behavior in live implementation):
- https://developer.android.com/training/data-storage/shared/media
- https://developer.android.com/reference/android/provider/MediaStore
- https://developer.android.com/training/data-storage/manage-all-files
- https://support.google.com/googleplay/android-developer/answer/10467955

### E. Execution order stays intact — implement, verify, next service

Keep original SECTION 00–15 execution sequence, data schema and all 13 Supabase tables. Add this override as a top-level acceptance contract for **each** section. Do not silently ship a reduced no-action scope because the original text emphasized privacy. Privacy and permission limits require customer consent and accurate operating modes; they are not a prohibition on legitimate execution. Preserve every unique file and every approved screen, no new disconnected project, no destructive Git operation, no touching `D:\KNOUX-AI`.

---

## 0. DATA PROVENANCE — WHAT WAS ACTUALLY FOUND

This handoff consolidates **prior research** and **known code snapshots**; it does not claim every researched API, database, migration or service is deployed.

| Source / date | What it established | Evidence quality |
|---|---|---|
| Deep Search report, **2026-11-10**, title `بحث تنفيذي لبناء التطبيق الظاهر في الصور بالكامل باستخدام Coding Agents` | Device-cleaner architecture, 10 services, Android/Play limits, Local-First/Room, an actual proposed 13-table Supabase SQL schema with RLS, end-to-end execution prompt | **Research / proposed design; no deployment proof** |
| `KNOUX-AI_FULL_SOURCE.txt` recovered from prior project handoff | Code file map, 20 UI reference names, `docs/architecture/REAL_DATA_AND_BOUNDARIES.md`, `docs/policies/ANDROID_PLAY_CONSTRAINTS_2026_11_10.md`, `prompts/PHASE_2_MASTER_SERVICES.md`, ten `prompts/phase2/*.md`, `docs/testing/FAKE_CLOSURE_PRODUCTION_GATE.md` | **Older source/reference snapshot**, not proof of today's HEAD |
| Phone snapshot captured **2026-11-11 21:04** | `D:\KNOUX-AI-Phone-Exports\KNOUX-AI-phone-snapshot-210018.zip` had 3,188 entries and passed SHA-256/ZIP CRC; branch `master` ref `cffe7b4f7cc1de4beb5539202dbcf067de19741e`, 32 main Kotlin sources, UI fixtures and no archived test source | **Verified older archive, not live state** |
| Shared Downloads folder `KNOUX_AI_COMPLETE_CODEX_DATABASE_ANDROID_PLAY_PRODUCTION_KIT/KNOUX_AI_COMPLETE_HANDOFF/` | Folder hierarchy (`android/integration`, `database/supabase`, `docs`, `prompts` etc.) visible, but inspected leaf folders were **empty** | **Do not claim migration/test/snippet files are present just because folders exist** |
| Official Android/Play/Supabase documentation, rechecked **2026-11-12** | Android 16/API 36 target required for standard new/update Play phone apps since 2026-08-31; OS-mediated MediaStore trash; server challenge required for Passkeys; RLS by `auth.uid()` | **Current documentation, not evidence of implementation** |

**Known older UI source state:** Compose splash, Welcome/auth (`AuthGate.kt`, `AuthVisual.kt`, `FingerprintButton.kt`), Home, Smart Clean, Deep Scan, Duplicate Files, App Residue, Storage Analyzer, App Manager, Privacy Sweep, Battery Saver, AI Optimization, Reports and detail screens. `Fixtures.kt` explicitly said Phase 1 UI-only. Some battery diagnostics read `BatteryManager` for real; other cards remained preview. Presence of old `AuthGate.kt` is **not** proof of secure authenticated sessions. This baseline MUST be refreshed against the live phone workspace before changes.

**Older identity fixes needed before real cloud auth:** Google flow considered `getCredential()` success an authenticated login without backend ID token verification; Passkey called `GetPublicKeyCredentialOption()` with an RP ID string rather than server-generated `requestJson` challenge. Fix later in Identity gate and never treat these as a trusted logged-in session.

**Current owner intent supersedes the older one-service STOP instruction:** Execute the ordered services autonomously **one at a time**, with explicit per-service quality gates and persisted ledger; do not stop to ask for the next service. An external credential/Play permission is NOT permission to fake it.

## 1. HARD REPOSITORY, SECURITY AND DESIGN BOUNDARIES

1. Work ONLY in live `/root/workspace/KNOUX-AI` after verifying `pwd`, `.git`, current `git status --short --branch`, `git rev-parse HEAD`, refs, stashes, reflog, untracked and existing workers. The 2026-11-11 SHA above is **historical, not a reset target**. Preserve all unique work. Never `reset --hard`, `clean`, rewrite history, force push, delete dirty worktrees or replace the project with this handoff.
2. Phone-hosted OpenCode runs in an isolated app/Linux context. **ADB/Shizuku shell access does not magically grant unrestricted private Linux workspace access.** Default consumer implementation uses normal Android APIs and actual granted permissions. Evaluate optional advanced-user, Shizuku/ADB companion, or enterprise Device Owner/Profile Owner distribution only with explicit setup/consent and correct legal/Play policy; do not impersonate root/system, auto-escalate privileges or market those powers in a normal APK. Debugging access is not proof of production privileges.
3. Keep **canonical UI/assets, KNOUX official logo, neon violet/cyan/magenta glass design, animations, splash, approved ten visual references (if present), compact single header and RTL/LTR**. Visual phase should be owner accepted; if not, isolate core data work and report visual gate as pending rather than regress UI.
4. - Never **silently** delete real user files or modify device state. Implement REAL customer-authorized file Trash and Permanent Delete, and user-controlled OS app/settings operations wherever supported. All destructive actions require review → explicit selection → user consent → OS-mediated authorization where applicable → execute → re-query actual result → journal. Never force-stop or alter third-party apps without legitimate supported user authorization. Automated tests use synthetic files only.
5. No credentials, service-role, signing keys, OAuth client secrets, private report contents, personal file names, filenames, media URIs or hashes in public logs, screenshots, Git commits or cloud sync. Never prompt users to provide secrets through chat; use controlled runtime configuration and secret manager. Do not use real personal files for QA.
6. A Google Play-compatible **ordinary Android app** is the product. Do not promise hidden access to `/Android/data`, other apps' caches, clipboard history, all package usage or OEM system optimizations. Never replace an unsupported feature with a false success animation.
7. Make every operation rerunnable and crash-safe (idempotency keys, DB migrations, job state journals, duplicate scan prevention). Preserve offline operation; network services must be optional and fail gracefully.
8. Distinguish **IMPLEMENTED+TESTED**, **IMPLEMENTED+UNVERIFIED**, **PREVIEW**, **BLOCKED**, **UNSUPPORTED**, **CONFIGURATION_REQUIRED** and **DEPLOYED**. Build PASS alone never equals a verified feature or release.

## 2. ONE CONTINUOUS EXECUTION ALGORITHM (BEEM MUST FOLLOW)

**Start immediately, no phase-selection questions.** Use a file-based run ledger such as `qa-evidence/end-to-end/EXECUTION_LEDGER.md` (not a second project). Add a durable checkpoint for each service; preserve after context compaction/rate-limit interruption. The state machine is:

```
REALITY_AUDIT → DEPENDENCY_AND_CAPABILITY_CHECK → IMPLEMENT_ALL_LAYERS
→ BUILD → UNIT_TESTS → DEVICE/INSTRUMENTATION_WHEN_AVAILABLE
→ REAL_DATA_AND_UI_QA → SECURITY_AND_PERMISSION_REVIEW
→ WRITE_EVIDENCE → SAFE_INTEGRATION/CHECKPOINT → AUTOMATIC_NEXT_SERVICE
```

For each ordered service S:

- Read current implementation, available APIs, test coverage, and approved UI reference. Inventory all functions before replacing any code. Prefer incremental changes; don't reimplement a completed capability.
- Produce **complete vertical slice**: Android Platform Adapter + typed Domain/Repository + Room DAO/migration + ViewModel/state machine + actual Compose UI wiring + permissions/consent/errors/cancel/retry + test cases + evidence.
- Use REAL live values only from permitted APIs. Every displayed metric has `source`, `scope`, `capturedAt`, `status`, `method`, `confidence` and `limitations` as appropriate; fixtures cannot enter real reporting.
- Run actual Gradle commands and inspect exit codes/logs. Emulator not available? Record `ANDROID_TEST_NOT_RUN`; never substitute JUnit or screenshots from old builds. Do not claim device runtime validation when ADB connectivity is not proven.
- If gate passes with evidence, mark **VERIFIED for that specific scope only** and immediately proceed to next service. A commit may be made when safe on the canonical branch after verifying other workers; no new branch merely as a holding area.
- If a required permission/user interaction/secret/Play declaration cannot be granted by the agent, mark **BLOCKED or CONFIGURATION_REQUIRED** with exactly which input is missing, implement independent safe parts, and **continue next independent section automatically**. Never silently bypass it or claim equivalent capability.
- If a foundational dependency prevents safe progress for *all* subsequent tasks, stop with a specific reproducible blocker; don't spin, create fake tasks, or reset Git.
- Avoid lengthy status speeches and A/B/C questions. At the end of all possible sections, deliver one honest summary, status ledger, artifact paths, remaining owner-only steps and next release gate. Do not assume hidden time/background execution.

### Section completion gate (strict AND)

A section can advance only if: (a) capability and permission map, (b) real adapter or explicit UNSUPPORTED, (c) persistent typed local model, (d) UI real-data/error integration, (e) safe consent path if applicable, (f) passing relevant tests with actual logs, (g) no visual regression, (h) no unexplained data falsification, (i) evidence paths, (j) unique Git work preservation. For partially supported Android/OEM capabilities, the **supported subset** may be verified while unsupported subfeatures remain clearly labelled; don't hold the whole app hostage for impossible global access.

## 3. TARGET ANDROID ARCHITECTURE (LOCAL-FIRST)

```
Compose UI + existing branded design / Navigation
           ↓ typed UI states, provenance and a11y
ViewModels / StateFlow / Cancellation / Permission UX
           ↓ interfaces
Domain Use Cases and Repositories (pure business logic)
  ├─ MediaInventoryAdapter → ContentResolver/MediaStore/Photo Picker/SAF
  ├─ StorageAdapter → StatFs + scoped category aggregation
  ├─ InstalledAppsAdapter → PackageManager + authorized UsageStats
  ├─ PrivacySignalAdapter → declared/visible permissions, settings status
  ├─ BatteryAdapter → ACTION_BATTERY_CHANGED, BatteryManager, PowerManager
  ├─ DuplicateEngine → streaming hash, keeper invariant, grouped candidates
  ├─ ActionExecutor → selection + OS approval + re-query + operation journal
  ├─ ScanOrchestrator → coroutine/WorkManager, cancellation/retry/progress
  ├─ RecommendationEngine → deterministic rules; optional model afterward
  └─ ReportAggregator → Room truth, date windows, no duplicate counting
           ↓
Room/SQLite local source of truth (with migrations, transactionality)
DataStore (theme, locale, consent, feature preferences)
Secure session/token storage where justified
           ↓ ONLY IF explicit per-user opt-in, authenticated, RLS verified
Supabase Auth / Postgres RLS / Edge Function (aggregate-only sync)
```

**Keep local by default:** media `content://` URIs, original display names, full paths, hashes, similarity embeddings, selected duplicate members, exact app/package inventory, usage events, file contents, raw privacy observations, permissions and local action selection. Do not upload these via general telemetry or AI prompts. Use randomized app installation UUID for opt-in device sync; never IMEI, Android ID or hardware serial.

**Design strict model contracts (proposal; reconcile against existing DB first):**

```
enum DataStatus { PREVIEW, OBSERVED, COMPUTED, ESTIMATED, STALE, UNAVAILABLE, DENIED, UNSUPPORTED, ERROR }
enum JobStatus  { QUEUED, RUNNING, WAITING_CONSENT, CANCELLED, FAILED, PARTIAL, COMPLETED }
enum ActionStatus { REQUESTED, SELECTED, AWAITING_OS_APPROVAL, REJECTED, RUNNING, VERIFIED, PARTIAL, FAILED, CANCELLED }

Evidence<T> {
  value: T?, status: DataStatus, capturedAt: Instant?, source: String,
  accessScope: String, method: String, confidence: Float?, limitations: List<String>
}
ScanProgress { attemptedModules, completedModules, skippedModules, measuredItems,
  measuredBytes, stage, cancelled, failed, updatedAt }
PermissionState { capability, sdkLevel, grantedScope, denied, permanentlyDenied,
  restrictedByOS, requiredNextAction, checkedAt }
```

Model null/unknown values honestly; never initialize a real measurement from `Fixtures.kt`. Separate Preview build/runtime data namespace from real repository; server sync must reject demo provenance.

### Room/SQLite: required local database proposal (not yet verified as existing)

| Table / entity | Required fields or relationships | Invariant |
|---|---|---|
| `scan_sessions` | id, scanType, status, startedAt, endedAt, permissionScope, attempted/completed/skipped modules, examined count, errors | no fabricated completion |
| `scan_items` | id, sessionId, ephemeral contentUri/documentUri, mime, size, lastModified, category, source, access/grant scope, observedAt | no raw path in cloud |
| `duplicate_groups` | id, sessionId, groupType, contentHash, size, keeperItemId, totalCandidateBytes | minimum one keeper, candidate != reclaimed |
| `duplicate_members` | groupId, itemId, keeperFlag, userSelected, readVerifiedAt, accessibility | no file deletion without keeper review |
| `cleanup_actions` | id, sessionId, request id, action type, selectedUriIds, userConsentAt, OS approval, result status, actual verified bytes, errors | idempotent journal and no pretend savings |
| `storage_snapshots` | volumeId, total/used/free bytes, scope, category map, timestamp | no double count across media classes |
| `app_observations` | package id locally, version, source, visibility status, optional usage last observed | no full app listing claim if filtered |
| `privacy_findings` | signal type, target class, observed evidence, inferred risk, confidence, limitation | observed vs inferred separated |
| `battery_snapshots` | percentage, plugged, temp, voltage, android health enum, thermal status, source/time | health enum != health percentage |
| `scan_modules` | sessionId, module, attempted, completed, skippedReason, errors, item counts | progress derived from work |
| `recommendations` | source snapshot ids, rule id/version, explanation, confidence, suggested action, status | no autonomous destructive action |
| `report_history` | period, category, successful operation ids, measured bytes, chart input rows | actual verified totals only |
| `capability_grants` | permission/scope, grant state, checkedAt, expiry/revocation status | degrade gracefully |
| `sync_outbox` | idempotency UUID, consent version, aggregate payload, status, retry count | no raw sensitive data |

Use `@Entity`, `@Dao`, `Flow`, indexes, foreign keys, stable migrations and Room migration tests. Use `DataStore` for preferences/consent. Introduce WorkManager for deferrable resumable work within Android background-execution limits; foreground service only with justified type, notification and policy-compliant user-facing task.

### Explicit permission/capability decision table

| Capability | Supported Android mechanism | Mandatory limitation |
|---|---|---|
| Storage free/used | `StatFs` scoped mounted volume | system bytes != accessible category ownership |
| User-selected images/video | Android Photo Picker, SAF, scoped MediaStore | partial selection must remain partial |
| Broader authorized media collection | granular Android media permissions with per-API version gates; Android 14+ selected photos | Play photo/video permission policy and request rationale |
| Documents/folders | `ACTION_OPEN_DOCUMENT`, `ACTION_OPEN_DOCUMENT_TREE`, persistable grants where possible | Android 11+ denies roots/Download root/tree `Android/data`/`obb` |
| Trash media | API 30+ `MediaStore.createTrashRequest` and OS confirmation | never count trash as permanently deleted; verify outcome |
| Delete authorized SAF document | `DocumentsContract.deleteDocument` if provider supports | user-selected items, no silent bulk delete |
| Visible apps | `PackageManager` + targeted `<queries>` | Android 11+ package visibility filtered |
| Usage signals | `UsageStatsManager` after user grants Usage Access in Settings | no per-app usage claims before grant |
| Battery | `ACTION_BATTERY_CHANGED`, `BatteryManager` | no guaranteed hours gained, charge-cycle data may be unavailable |
| Thermal | `PowerManager.getCurrentThermalStatus` (API 29+) | observe only, cannot cool OS on command |
| Privacy | permission declarations, app-specific signals/settings and user-selected APK metadata | not system-wide tracking detection; no all-app clipboard history |
| Own cache | `context.cacheDir` for app-owned files | cannot wipe third-party app private caches |
| Sensitive broad access | `MANAGE_EXTERNAL_STORAGE`, `QUERY_ALL_PACKAGES` | restricted Play declaration; no default use or Shizuku workaround |

**Google Play policy snapshot:** standard new/updated phone apps require target API **36+ as of 2026-08-31**. Recheck at release; minSdk 26 is a business decision, not a Play mandate. Official links appear near the end.

## 4. SERVICE EXECUTION ORDER — COMPLETE EACH VERTICAL SLICE AND AUTO-ADVANCE

**No menu questions.** Each service below specifies actual sources, operations, data, permissions, tests, honest non-capabilities and exit evidence. Integrate existing approved Compose pages and their detail routes rather than replacing them with generic screens. The owner requested end-to-end logic, not another visual redesign. If the same capability already exists and passes real tests, preserve and reuse it.

### SECTION 00 — Foundation, actual platform adapter, schema, permission coordinator

- Inspect the existing Gradle namespace, AGP/Kotlin/Compose compatibility, current compile/target/min SDK, main manifest, source tree, current Room/DataStore if any, active test tooling, and release/debug variants. Do not pull a new Gradle stack without a compatibility check.
- Create only the missing domain/repository interfaces, `Evidence<T>` provenance wrapper, capability registry, structured error model, and shared operations journal. Wire `StateFlow` with cancellation. Ensure Arabic/English friendly errors and no preview leakage.
- Room and DAO migration tests; add minimal local database. Do not overwrite data from a previously installed build. Test schema upgrade from actual pre-existing version or initialize safely if absent; never fake a migration by destructive fallback.
- PermissionCoordinator: explain rationale, request only on action, denied/partial/permanently denied states, OS Settings deep links, runtime version guards; no forced all-files policy.
- Tests: repository fake vs live provenance; process death/restore; permission revoke mid-scan; Room migration and foreign keys; sanitized logs; no cloud by default.
- **Gate:** app boots offline; previously approved screen and navigation remain; a measured value can't be created by fixture copy; build and targeted tests recorded.

### SECTION 01 — STORAGE ANALYZER + STORAGE BREAKDOWN

**Pages:** Storage Analyzer overview, Storage Breakdown detail, Home/Tools summaries.

- Platform read: `StatFs` for device/volume free/total, `StorageManager` only when publicly authorized, `MediaStore` for accessible Photos/Video/Audio/Downloads when permitted, SAF for individually granted documents/trees. Deduplicate URIs and account for filtered/private/inaccessible files.
- `totalBytes`, `freeBytes`, `usedBytes`, `volumeId`, `categoryBytes`, `categoryItemCount`, `scope`, `capturedAt`, `remainderUnclassified`, `coverageEstimate`. Bytes use `Long` and checked aggregation; if category totals differ from volume, label **scoped subset**. Do not fabricate app cache allocations.
- Wire ViewModel with FULL real customer control: permission scope, file/folder list and category drill-down, search/sort/filter, open/share, provider-supported copy/move/rename, explicit OS-authorized Trash/Permanent Delete, cancel/retry, verified post-action counts and persistent history. Show unknown/unclassified/permission-restricted states rather than fake category breakdowns. No silent actions.
- Persist snapshots in Room and make actual differences available to reports.
- Tests: tiny/zero/huge sizes, overflow, exact byte conversion, multiple volumes, denied grants, empty media, partial photo grants, duplicate URI counting, device volume change, offline case.
- **Gate:** observed storage totals have documented API/timestamps; scoped inventory, file-management controls, actual user-approved operation and re-query are verified; restart/history survives; screenshots/tests evidence. A static chart alone FAILS.

### SECTION 02 — DUPLICATE FILES FINDER + DUPLICATE GROUP REVIEW

- Build access-scoped inventory from MediaStore/SAF. Stream content through ContentResolver; use fast candidate buckets `(size, mime/format where reliable)`, then streaming SHA-256 **of actual bytes**. Avoid storing raw file data; handle URI grants, cancellation, unavailable provider, zero-byte, renamed/changed files. Recheck identity/size/modified value before acting.
- Separate **byte-identical** from **visually similar** photos. Similarity is its own opt-in algorithm with separate confidence and explicit human review. Do not call size-matched files duplicates.
- Group by full verified hash with at least 2 members; apply `KEEP_ONE` invariant, immutable keeper until user changes it explicitly; never select every member in a group for removal by default. Compute candidate reclaimable bytes excluding keeper and unselected/inaccessible objects. Show group preview, user choices and accurate counts.
- REQUIRED real customer action, not optional Preview: implement `MediaStore.createTrashRequest()` API 30+ for recoverable Trash AND `MediaStore.createDeleteRequest()` API 30+ for user-approved permanent deletion where eligible; provider-supported SAF deletion/rename/move with grants and explicit confirmation. Re-query each result, preserve original on OS cancel; journal pending/approved/partial/failed; never claim delete if provider refused. For Android API 26–29 use the supported user-mediated alternative; never silently override OS consent.
- Tests: same-size different bytes; binary hash exact; mid-read failure; URI revoked; changed item after scan; protected keeper; user cancels OS prompt; one successful selected trash; partial OS result; no double-counted reclaimed bytes; group detection without media permission.
- **Gate:** full flow (two synthetic duplicates → detect → review → choose keeper → user selects Trash or Permanent Delete → OS consent → real operation → re-query → persist operation → reports), on test-only media. No fake successful deletion.

### SECTION 03 — APP RESIDUE CLEANUP + RESIDUE ITEM REVIEW

- Explain actual narrow scope: **app-owned caches** and user-granted **accessible** folders/files, NOT other apps' private cache, Android/data, Android/obb, or the system global cache.
- Enumerate grants, media and designated user-owned directory candidates; produce per-item evidence: source URI, permission scope, age (if metadata), candidate reason, confidence and risk class. Never infer that a folder is safe to remove merely because a package was uninstalled or because its name resembles an app.
- Initial state is **review/unselected**, but the workflow MUST continue to real user-controlled cleanup: select eligible items, show detailed evidence, choose supported Trash or Permanent Delete where available, confirm, execute, re-query and journal. High-risk/unknown items remain protected until explicit valid approval. Show attribution uncertainty.
- Use OS-mediated delete/trash depending on URI capability and API. App's own `cacheDir` cleanup may be implemented with per-action confirmation and actual post-check; never confuse it with third-party cleanup.
- Tests: WhatsApp-like media/false positives protected; inaccessible residual directory; no grant; revoked grant; user cancels; partial operation; URI with nested descendant; symlink/alias handling where provider permits; app-owned cache safe path.
- **Gate:** real scoped inventory, interactive review/selection, REAL approved Trash/Delete or own-cache action on synthetic scoped items, verified effect and truthful journal. Dry-run-only is NOT PASS.

### SECTION 04 — PRIVACY SWEEP + PRIVACY RISK INSPECTOR

- Build a source-backed permission/exposure review only from Android-accessible signals: visible `PackageManager` metadata and declared permissions (subject to package visibility), current app's own runtime grants, user-enabled special access pages, overlay capability where visible, notifications/usage access only with legitimate consent, and user-supplied APK static metadata if feature exists.
- Findings must include `OBSERVED`, `INFERRED_RISK`, `USER_CONFIGURABLE`, `UNAVAILABLE`, `UNSUPPORTED`; source/time/method/confidence. Don't present arbitrary tracker counts as detected network interceptions. No all-app AppOps history, notification spying or clipboard reader enumeration without an actually authorized/public mechanism.
- Compose risk cards reflect real observed signals and clear limitations. Show direct, functioning user-controlled OS permission/security settings handoffs and RECHECK changed state on return. Where the product advertises tracker blocking, build an actual user-consented VpnService/filtering stack with tested on/off/allowlist/blocklist or remove the claim; a generic link to settings is not a completed blocker. A marketed encrypted vault must use actual authenticated encryption, import/unlock/export/delete and security tests.
- Privacy score, if any, must have publicly documented deterministic weighting and data completeness; never assert a risk score when inputs are unavailable.
- Tests: visible package granted/denied; installed app filtered out; unsupported OS permission; inferred vs observed invariant; score absent if incomplete; settings intent fallback.
- **Gate:** at least real, supported user-configurable findings and evidence; all unavailable categories honest; no mock tracker counts appear as live.

### SECTION 05 — APP MANAGER + INSTALLED APPS REVIEW

- Query packages visible under Android 11+ package visibility rules (`PackageManager` with targeted `<queries>`). Classify user/system when allowed, SDK-safe app label/icon, version, installation state. `UsageStatsManager` usage/last used requires manual special Usage Access approval and may be incomplete. Do not request broad `QUERY_ALL_PACKAGES` by default.
- Determine package size only from supported public APIs; show Unavailable when not exposed. Build full user controls: launch app, app-details/permissions/storage settings, user-initiated uninstall, review selected packages in sequence, and re-query actual state. Offer authorized managed-device/advanced-mode integrations for privileged operations ONLY if explicitly configured; do not claim arbitrary silent force stop/clear data/freeze/clone/backup in ordinary Android APK. App update indicator requires a real source.
- ViewModel supports search, grouping, sorting, limited visibility banner, permission settings fallback, empty/error/retry. Avoid uploading package lists to cloud or AI.
- Tests: query filtering, renamed app, uninstalled between scan and detail, denied Usage Access, inaccessible package-size data, no installed-app changes by query, Android version guards.
- **Gate:** actual visible app count and details are locally sourced, not fabricated; unsupported privileged actions are explicitly not offered; UI filter/search works.

### SECTION 06 — BATTERY SAVER + BATTERY DIAGNOSTICS + THERMAL

- Read `ACTION_BATTERY_CHANGED` intent and `BatteryManager` for charge %, plugged/charging, temperature when supplied, voltage, Android health enum, technology, measurement timestamp. Use `PowerManager.getCurrentThermalStatus()` on API 29+ if supported. Show device/OEM unavailable values as unavailable.
- Distinguish Android health enum (`GOOD`, etc.) from numeric battery **health percentage**, charge cycles, rated battery capacity, individual-app battery use and projected hours; latter cannot be invented without supported source and validated method.
- Implement actual user-configured battery/thermal threshold notifications, charge history, real OS battery saver/optimization settings navigation with state re-check, and supported user-triggered adjustments. Do not claim to kill/hibernate apps or improve performance merely by animation. Recommendations require measured evidence and explicit estimate limitations.
- Persist snapshots, battery trend history when actual measurements exist, and re-read when resumed.
- Tests: status charging/unplugged/unknown, temperatures negative/extreme, API 26 thermal not supported, revoked/absent data, device rotation, no false "hours gained".
- **Gate:** live battery values compared against device OS within measurement tolerance; missing signals clearly marked; safe settings handoffs work.

### SECTION 07 — DEEP SCAN ORCHESTRATOR + DEEP SCAN SUMMARY

- Build orchestrator for all **verified adapters** only: storage/media, duplicate candidates, app residue, privacy, installed apps, battery. Each module reports `queued/running/completed/partial/skipped/failed`, source scope, item counts, error, timestamp. No fake animation timer or contradictory percentage.
- Progress denominator = attempted / eligible modules and actual measured work, not an arbitrary `delay()` or hardcoded 5/5. Support cancellation, skip on denied access, consistent resume checkpoint and process death recovery. Use coroutine structured concurrency, bounded hashing concurrency, WorkManager for eligible deferrable work and Android foreground restrictions.
- Deep Scan Summary must show completed, skipped and failed modules distinctly, then enable real customer remediation per finding: review/file select, launch approved cleanup, OS settings/permission handoff, mark false positive, export and re-scan/verify. A cancelled scan is not a successful scan.
- Tests: module fails mid-stream, permission changes, two scans raced, process death, battery/storage low, one module unsupported, all blocked, progress monotonic, no stale fixture contamination.
- **Gate:** traceable scan session; UI progress tied to work; deterministic report and Room state; cancel/retry proven.

### SECTION 08 — SMART CLEAN CENTER + SAFE CLEANUP EXECUTION

- This screen is the user-facing **action hub** for verified duplicate candidates, accessible cache/residue candidates and other safely eligible items. Use actual candidates from previous sections, not fixed example GB.
- Categorize actual selected vs total candidate bytes, risk and access limits. Present category drill-down and per-item consent. Show Preview vs Executed numbers separately.
- Build a COMPLETE executable action plan: user chooses eligible items → validates grants and freshness → selects recoverable Trash OR immediate Permanent Delete where API/provider permits, with clear irreversibility warning → request Android OS consent when required → execute selected operations → re-query each URI/status → journal results. Add own-cache operations where eligible, pause/cancel/retry where supported. No fabricated universal cross-app cache wipe or unattended deletion; partial results must be visible.
- For media trash, state "Moved to Trash" not "Permanently freed" unless actual storage measurement and semantics support claim. Reconcile disk delta separately and label concurrent changes.
- Tests: all selections empty, select all except keeper, mixed permission scopes, content changed, OS dialog rejected, process death mid-operation, partial trash, retries without duplicated deletion, accurate reclaimed vs candidate bytes.
- **Gate:** real synthetic file selected and ACTUALLY processed through OS-confirmed Trash or Permanent Delete (when supported), post-operation recheck, accurate bytes, journal, proper cancel/partial behavior and cross-screen state integrity. Dry-run alone FAILS. No full-device deletion test on personal files.

### SECTION 09 — REPORTS & INSIGHTS + PERFORMANCE REPORT DETAIL

- Query persisted Room scan sessions, successful operations, storage snapshots and privacy/battery findings. Aggregate by 7/30/90 days using actual timestamps and item identities, not fixed demo values.
- Separate candidate/approved/acted/reclaimed bytes and uncertainty; deduplicate overlapping scan results, avoid counting an already-trashed file twice. `totalCleanedBytes` is only from independently verified operations with declared limitations.
- Report charts source raw persisted time series. Mark gaps/permission changes. Include audit trace to operation IDs. - Provide fully functioning user-directed CSV/PDF export via `ACTION_CREATE_DOCUMENT`, useful filters, drilldowns and opt-in scheduled reports with actual persisted history; no background upload by default.
- Tests: aggregation boundary/time-zone/DST, account/user distinction, duplicate operation idempotency, unavailable data, export cancellation, consistency Home↔Reports↔journal.
- **Gate:** synthetic known expected report and real device data case reconcile; export works locally; no seeded fixture metrics labelled live.

### SECTION 10 — AI OPTIMIZATION + AI [REDACTED_DEVICE_SERIAL]

- **Local deterministic recommendation engine first**: rules over measured storage/reclaimable candidates, duplicate groups, rarely-used apps only with granted usage, privacy exposure and battery/thermal observations. Every recommendation includes evidence reference, ruleId/version, benefit assumptions, confidence/limitations, and a *safe user action*. Do not invent “AI cleaned 4.7 GB”, “+40% faster”, global memory boosts, boot-time gains or app hibernation.
- The customer can inspect, customize, accept, dismiss, apply and verify each eligible actual optimization through authorized app/OS actions. For potentially destructive recommendations, present the exact operation and require explicit user/OS authorization before using the same real ActionExecutor; AI must not silently delete files. Log actual outcomes and offer supported undo. Sync only with explicit cloud consent and privacy-minimized aggregates.
- Optional remote LLM enhancement ONLY after owner enables a funded model/provider and an authenticated Edge Function is deployed. Validate typed JSON output, injection-resistant prompts, provider errors and timeouts, consent and data minimization. API keys belong in server secrets, never Android. Have deterministic offline fallback. Do not force external API subscription as a dependency for device utility functionality.
- Tests: same measured inputs produce explainable recommendations, unsupported input produces none, hallucinated benefit blocked, LLM malformed JSON rejected, unauthorized provider call not made, offline returns local rules, user choices persist.
- **Gate:** recommendations are real, traceable and actionable within Android public capabilities; no fabricated performance claims.

### SECTION 11 — HOME + DEVICE TOOLS HUB + ALL EXISTING DETAIL ROUTES

- Convert every Home card/preview value to a typed repository summary from the relevant VERIFIED services. UI must distinguish `MEASURED`, `PARTIAL`, `NOT_SCANNED`, `DENIED`, `UNAVAILABLE` and `PREVIEW` on every metric. Zero is not equivalent to missing evidence.
- Navigation hierarchy: Hub → Service Overview → Service Detail. Preserve every existing service screen and route. Do not bypass service overview by redirecting its card to an interior details page.
- Current historic route inventory (verify live): `home`, `clean`, `tools`, `smartclean`, `deepscan`, `duplicates`, `residue`, `privacy`, `storage`, `appmanager`, `battery`, `ai`, `reports`, `duplicate-group`, `privacy-risk`, `deep-scan-summary`, `residue-item`, `storage-breakdown`, `installed-apps`, `battery-diagnostics`, `ai-recommendations`, `performance-report`.
- Maintain approved visual proportions, single inset-safe compact header, six Home cards and correct Bottom Navigation; preserve the premium 3D icon and artwork identity. No root-level clipping, overflow, duplicate header or navigation regression. Lists may scroll internally where appropriate.
- Full smoke: Home→Storage→real file action→verified result→back; Home→Duplicates→exact detection→select keeper→user-authorized Trash or Delete→result→home summary; Smart Clean→OS-confirmed synthetic action→Reports→Home totals; App Manager→real OS action→re-check; Privacy→real settings/vault/blocker when advertised; battery alerts; user-controlled AI apply/verify; permission denial, tab stacks, Arabic/English and accessibility.
- **Gate:** each customer-visible card has a truthful functioning route; verified real metrics reconcile between all screens. No demo values silently rendered as measured.

### SECTION 12 — SETTINGS + PERMISSION CENTER + LOCAL PRIVACY & UX

- Persist locale, RTL, theme, reduced motion, scan cadence preference, preview disclosures, privacy settings, permission/grant state, consent lifecycle. Support system font scales, Android 15/16 edge-to-edge insets, actual status/nav bar cutouts, one fixed header and accessible 48dp controls.
- List exactly what the app can and cannot read. Provide controls to revoke opt-in cloud sync, clear *app-owned cache* with confirmation, manage per-service grants and open Settings intents. Never show a global cleanup button that only clears this app's own cache.
- Background scans require WorkManager scheduling within quotas/OS policies, battery awareness and user-visible cancel. No silent perpetual scanning or notification access without clear separate product feature.
- Tests: partial permission, denial, permanent denial, grant revoked, locale switch, 1.0/1.3/1.5 font scale, low-height screens, cancellation, settings persist after restart, offline no degraded core.
- **Gate:** no misleading capabilities; no security/privacy leak; approved UI and navigation retained.

### SECTION 13 — IDENTITY GATE: LOCAL BIOMETRIC, GUEST, GOOGLE, PASSKEY, SESSIONS

- The app MUST remain fully usable **as Guest** without cloud credentials. Biometric local unlock uses actual Android `BiometricPrompt` and correctly distinguishes cancelled/failed/unavailable/success. Biometric success is a local unlock event, **not a server-proven identity**.
- For real Google sign-in, use supported Android Credential Manager/Google ID integration; obtain Google ID credential, send ID token to the **authorized backend**, verify token signature, audience/client ID, issuer, expiry, nonce where used, and user mapping. Only then establish trusted backend/account session. Do not return success just because `getCredential` returned a value.
- For real Passkey/WebAuthn, the server issues a challenge and complete request options JSON (`challenge`, `rpId`, `allowCredentials`, etc.). Android uses `GetPublicKeyCredentialOption(requestJson = ...)`, returns assertion, server validates challenge, origin/RP ID, authenticator signature/counter/policy and expiry before session issuance. `rpId` string alone is NOT valid `requestJson`.
- Google OAuth, Passkey RP domain, SHA fingerprints/Digital Asset Links, Supabase project credentials and server verification require the owner's actual authorized setup. **Do not invent accounts, project IDs, secrets or Google client IDs**. If absent, implement explicit interfaces, fail-closed disabled/config-pending UX and tests; mark the external integration `CONFIGURATION_REQUIRED`, then continue independent sections.
- Never keep refresh tokens in plaintext or log them. Use secure storage appropriate to Android; support token refresh/revocation, sign out, guest↔account upgrade, identity merging only after deliberate consent and no accidental privacy sync.
- Test failure/cancel/replay/expired token, account switching, guest isolation, biometric unavailable, server offline, race conditions and logout. Keep original UI identity and high-quality biometric artwork.
- **Gate:** local biometrics actually tested; production Google/Passkey cannot be VERIFIED without live server signature/challenge tests, so report blocked accurately.

### SECTION 14 — OPTIONAL ACCOUNT CLOUD SYNC + SUPABASE + EDGE FUNCTION

**Cloud is OPTIONAL and requires explicit owner authorization, approved credentials and privacy consent. The next section provides a historic reference SQL baseline, NOT a deployed DB.**

- Before any remote write, discover **actual** authorized Supabase project, its current migration history and environment. Do not assume an existing service or apply migration onto unrelated user project. Use staging, diff migrations, backup plan with owner control and reversible additive changes. Never paste service-role key into client.
- Room is the offline truth. DataStore consent `cloud_history_enabled=false` by default. Create sync DTO mapper that exports only consented **aggregated, privacy-minimized** summaries (scan counts/volume, verified cleanup totals, health trends with method, anonymized recommendation codes). Device ID should be random per installation and rotatable. No raw URIs, paths, filenames, hashes, package names or media uploaded.
- Use Supabase Auth user JWT, publishable key in Android (not secret), Postgres RLS on all public user data, owner-reference integrity, idempotent `sync_outbox`, offline queue and incremental backoff. On revocation, stop requests and purge queued sync. Provide remote data erasure path if cloud already enabled.
- AI Edge Function if needed must validate user JWT server-side, use constrained backend secret environment, restrict to allowed aggregate fields, schema-validate output, enforce quotas and costs, never let LLM directly trigger device deletion.
- Entitlements/Pro must be server-authoritative (or appropriately store-verified); do not let a mobile client modify its own entitlements. Honor account deletion, privacy/export policy and Google Play billing if applicable; no pretend Pro unlock.
- Required staging tests: authenticated user A can access A rows but not user B's; user B cannot inject cross-owner `device_id` or `scan_session_id`; unauthenticated denied; cloud disabled makes **zero** data requests; expired JWT denied; consent withdrawn prevents sync and clears queue; user erasure; replay/idempotency; Edge Function rejects untrusted claims.
- **Gate:** `DEPLOYED` only after real project, migration checks, RLS two-user real JWT test and production configuration confirmed. Otherwise `CONFIGURATION_REQUIRED` and proceed to final local-only quality gates.

### SECTION 15 — RELEASE / SECURITY / PLAY / END-TO-END QUALITY GATE

- Confirm actual package/applicationId, signing, versionName/versionCode, SDK 26–36 compatibility as chosen, target API 36+, Play permissions declaration, scoped storage and Photo/Video permissions policy. Google Play acceptance is NOT guaranteed by compile.
- Remove all prohibited debug bridges, QA pollers, exposed local servers, mock fixture data in release. Verify debug manifest overlays do not leak into release and application `android:allowBackup`/data extraction rules are safe for user data and sessions. Verify network security config, exported components, Intent validation, dependencies, signed build and privacy/data safety disclosure.
- Test release and debug independently; no signing keys in code, no credentials in APK/repository, no raw content in crash/analytics logs. Validate dependency versions against repo compatibility; no unnecessary major framework upgrade.
- Run `assembleDebug`, `testDebugUnitTest`, `lintDebug`, `connectedDebugAndroidTest` where environment supports, `assembleRelease` under proper credentials when authorized, plus component tests, migration tests, permission/denial/partial, UI layout and RTL, API 26/30/33/35/36 matrix, disk/memory/leak/performance, actual APK launch and screen capture. **Report all NOT RUN accurately**.
- Perform an end-to-end **synthetic user journey**: Guest → authorize limited Photos → scan → detect exact duplicate → review keeper → approve OS trash → re-query → Room event → reports update → Home summary update → optional opt-in cloud RLS sync → revoke cloud consent → logout. No test against private user's personal images without explicit user-operated consent.
- Compare approved cinematic visuals before/after, including fixed header, intro, real fingerprint, page geometry, no clipped CTA, no generic white icon regression, Arabic/English and dark mode; do not conflate CI PASS with visual signoff.
- **Final gate:** report `LOCAL_FUNCTIONAL_PASS` only for actually tested public-API functions; `CLOUD_DEPLOYED` only when real staging+production deployment verified; `PLAY_SUBMITTED/APPROVED` only with real console evidence; `OWNER_VISUAL_ACCEPTED` only after owner approval. Never conclude `100%` from mocks/tests alone.


## 5. RESEARCHED SUPABASE SQL — ORIGINAL DEEP SEARCH BASELINE (NOT DEPLOYED)

The following 13-table PostgreSQL/Supabase schema and RLS policies are **preserved from the 2026-11-10 Deep Search report** for the owner's exact data continuity. It is a **reference migration design**, not an existing deployed database, not necessarily compatible with the live user's project, and not a pass for a security audit. **Do not run it against production blindly.** Inspect actual project migrations first, reconcile schema names and existing tables, run in isolated staging, then validate RLS and ownership.

Tables: `profiles`, `devices`, `scan_sessions`, `scan_category_summaries`, `cleanup_actions`, `storage_snapshots`, `app_inventory_summaries`, `privacy_summaries`, `battery_snapshots`, `ai_recommendations`, `reports`, `user_settings`, `entitlements`.

```sql
-- ============================================================
-- KNOUX AI — SUPABASE / POSTGRES PRODUCTION BASELINE
-- ============================================================

create extension if not exists pgcrypto;

-- ------------------------------------------------------------
-- PROFILES
-- ------------------------------------------------------------

create table if not exists public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  display_name text,
  avatar_url text,
  locale text not null default 'en',
  theme text not null default 'dark'
    check (theme in ('dark', 'light', 'system')),
  onboarding_completed boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

-- ------------------------------------------------------------
-- DEVICES
-- ------------------------------------------------------------

create table if not exists public.devices (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  installation_id uuid not null,
  manufacturer text,
  model text,
  android_version text,
  sdk_int integer,
  app_version text,
  total_storage_bytes bigint,
  first_seen_at timestamptz not null default now(),
  last_seen_at timestamptz not null default now(),
  unique(user_id, installation_id)
);

create index if not exists devices_user_id_idx
on public.devices(user_id);

-- ------------------------------------------------------------
-- SCAN SESSIONS
-- ------------------------------------------------------------

create table if not exists public.scan_sessions (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  device_id uuid references public.devices(id) on delete cascade,

  scan_type text not null check (
    scan_type in (
      'deep_scan',
      'smart_clean',
      'duplicate_scan',
      'privacy_scan',
      'residue_scan',
      'storage_analysis',
      'battery_analysis',
      'app_analysis'
    )
  ),

  status text not null default 'running'
    check (status in ('queued','running','completed','failed','cancelled')),

  started_at timestamptz not null default now(),
  completed_at timestamptz,

  scanned_items bigint not null default 0,
  reclaimable_bytes bigint not null default 0,
  cleaned_bytes bigint not null default 0,

  health_score smallint
    check (health_score between 0 and 100),

  privacy_score smallint
    check (privacy_score between 0 and 100),

  optimization_score smallint
    check (optimization_score between 0 and 100),

  metadata jsonb not null default '{}'::jsonb
);

create index if not exists scan_sessions_user_created_idx
on public.scan_sessions(user_id, started_at desc);

-- ------------------------------------------------------------
-- SCAN SUMMARY CATEGORIES
-- Never store raw filesystem paths here.
-- ------------------------------------------------------------

create table if not exists public.scan_category_summaries (
  id uuid primary key default gen_random_uuid(),
  scan_session_id uuid not null
    references public.scan_sessions(id) on delete cascade,

  category text not null,
  item_count bigint not null default 0,
  bytes bigint not null default 0,
  selected_count bigint not null default 0,
  selected_bytes bigint not null default 0,

  unique(scan_session_id, category)
);

-- ------------------------------------------------------------
-- CLEANUP ACTIONS
-- ------------------------------------------------------------

create table if not exists public.cleanup_actions (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  device_id uuid references public.devices(id) on delete cascade,
  scan_session_id uuid references public.scan_sessions(id) on delete set null,

  action_type text not null,
  status text not null
    check (status in (
      'requested',
      'user_confirmation_required',
      'completed',
      'partially_completed',
      'failed',
      'cancelled'
    )),

  item_count bigint not null default 0,
  reclaimed_bytes bigint not null default 0,

  android_result_code text,
  failure_reason text,

  created_at timestamptz not null default now(),
  completed_at timestamptz
);

create index if not exists cleanup_actions_user_created_idx
on public.cleanup_actions(user_id, created_at desc);

-- ------------------------------------------------------------
-- STORAGE SNAPSHOTS
-- ------------------------------------------------------------

create table if not exists public.storage_snapshots (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  device_id uuid not null references public.devices(id) on delete cascade,

  total_bytes bigint not null,
  used_bytes bigint not null,
  free_bytes bigint not null,

  apps_bytes bigint not null default 0,
  photos_bytes bigint not null default 0,
  videos_bytes bigint not null default 0,
  audio_bytes bigint not null default 0,
  downloads_bytes bigint not null default 0,
  documents_bytes bigint not null default 0,
  other_bytes bigint not null default 0,

  captured_at timestamptz not null default now()
);

create index if not exists storage_snapshots_device_time_idx
on public.storage_snapshots(device_id, captured_at desc);

-- ------------------------------------------------------------
-- APP INVENTORY SUMMARY
-- No sensitive per-app data should sync unless user opted in.
-- ------------------------------------------------------------

create table if not exists public.app_inventory_summaries (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  device_id uuid not null references public.devices(id) on delete cascade,

  installed_apps_count integer not null default 0,
  rarely_used_count integer not null default 0,
  large_apps_count integer not null default 0,
  total_app_bytes bigint not null default 0,

  captured_at timestamptz not null default now()
);

-- ------------------------------------------------------------
-- PRIVACY SCAN SUMMARY
-- ------------------------------------------------------------

create table if not exists public.privacy_summaries (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  device_id uuid not null references public.devices(id) on delete cascade,
  scan_session_id uuid references public.scan_sessions(id) on delete cascade,

  tracker_findings integer not null default 0,
  location_findings integer not null default 0,
  overlay_findings integer not null default 0,
  clipboard_findings integer not null default 0,
  notification_access_findings integer not null default 0,

  risk_score smallint
    check (risk_score between 0 and 100),

  created_at timestamptz not null default now()
);

-- ------------------------------------------------------------
-- BATTERY SNAPSHOTS
-- ------------------------------------------------------------

create table if not exists public.battery_snapshots (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  device_id uuid not null references public.devices(id) on delete cascade,

  battery_level smallint
    check (battery_level between 0 and 100),

  is_charging boolean,
  temperature_c numeric(5,2),
  voltage_mv integer,
  health text,

  estimated_runtime_minutes integer,

  captured_at timestamptz not null default now()
);

create index if not exists battery_snapshots_device_time_idx
on public.battery_snapshots(device_id, captured_at desc);

-- ------------------------------------------------------------
-- AI [REDACTED_DEVICE_SERIAL]
-- ------------------------------------------------------------

create table if not exists public.ai_recommendations (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  device_id uuid references public.devices(id) on delete cascade,

  recommendation_type text not null,
  title text not null,
  description text not null,

  priority smallint not null default 50
    check (priority between 0 and 100),

  confidence numeric(4,3)
    check (confidence between 0 and 1),

  estimated_savings_bytes bigint,
  estimated_battery_gain_percent numeric(5,2),

  action_key text,
  action_payload jsonb not null default '{}'::jsonb,

  status text not null default 'active'
    check (status in ('active','accepted','dismissed','expired')),

  generated_at timestamptz not null default now(),
  expires_at timestamptz
);

create index if not exists ai_recommendations_user_status_idx
on public.ai_recommendations(user_id, status, generated_at desc);

-- ------------------------------------------------------------
-- REPORTS
-- ------------------------------------------------------------

create table if not exists public.reports (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  device_id uuid references public.devices(id) on delete cascade,

  report_type text not null,
  period_start timestamptz,
  period_end timestamptz,

  total_cleaned_bytes bigint not null default 0,
  scans_completed integer not null default 0,
  privacy_improvements integer not null default 0,
  battery_sessions integer not null default 0,

  summary jsonb not null default '{}'::jsonb,

  created_at timestamptz not null default now()
);

-- ------------------------------------------------------------
-- USER SETTINGS
-- ------------------------------------------------------------

create table if not exists public.user_settings (
  user_id uuid primary key references auth.users(id) on delete cascade,

  smart_scan_enabled boolean not null default true,
  notifications_enabled boolean not null default true,
  cloud_history_enabled boolean not null default true,
  analytics_enabled boolean not null default false,

  auto_scan_interval text not null default 'manual'
    check (auto_scan_interval in ('manual','daily','weekly')),

  last_modified_at timestamptz not null default now()
);

-- ------------------------------------------------------------
-- ENTITLEMENTS / PRO
-- ------------------------------------------------------------

create table if not exists public.entitlements (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,

  product_id text not null,
  provider text not null,
  status text not null
    check (status in ('active','grace','expired','cancelled')),

  valid_from timestamptz,
  valid_until timestamptz,

  provider_reference text,

  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),

  unique(user_id, product_id, provider)
);

-- ------------------------------------------------------------
-- UPDATED_AT TRIGGER
-- ------------------------------------------------------------

create or replace function public.set_updated_at()
returns trigger
language plpgsql
security invoker
set search_path = ''
as $$
begin
  new.updated_at = now();
  return new;
end;
$$;

drop trigger if exists profiles_set_updated_at on public.profiles;
create trigger profiles_set_updated_at
before update on public.profiles
for each row execute function public.set_updated_at();

drop trigger if exists entitlements_set_updated_at on public.entitlements;
create trigger entitlements_set_updated_at
before update on public.entitlements
for each row execute function public.set_updated_at();

-- ------------------------------------------------------------
-- PROFILE CREATION AFTER AUTH SIGNUP
-- ------------------------------------------------------------

create or replace function public.handle_new_user()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
begin
  insert into public.profiles(id, display_name)
  values (
    new.id,
    coalesce(new.raw_user_meta_data ->> 'display_name', '')
  )
  on conflict (id) do nothing;

  insert into public.user_settings(user_id)
  values (new.id)
  on conflict (user_id) do nothing;

  return new;
end;
$$;

drop trigger if exists on_auth_user_created on auth.users;

create trigger on_auth_user_created
after insert on auth.users
for each row execute procedure public.handle_new_user();

-- ============================================================
-- ROW LEVEL SECURITY
-- ============================================================

alter table public.profiles enable row level security;
alter table public.devices enable row level security;
alter table public.scan_sessions enable row level security;
alter table public.scan_category_summaries enable row level security;
alter table public.cleanup_actions enable row level security;
alter table public.storage_snapshots enable row level security;
alter table public.app_inventory_summaries enable row level security;
alter table public.privacy_summaries enable row level security;
alter table public.battery_snapshots enable row level security;
alter table public.ai_recommendations enable row level security;
alter table public.reports enable row level security;
alter table public.user_settings enable row level security;
alter table public.entitlements enable row level security;

-- Profiles
create policy "profile_select_own"
on public.profiles
for select to authenticated
using ((select auth.uid()) = id);

create policy "profile_update_own"
on public.profiles
for update to authenticated
using ((select auth.uid()) = id)
with check ((select auth.uid()) = id);

-- Generic user-owned tables
create policy "devices_own_all"
on public.devices
for all to authenticated
using ((select auth.uid()) = user_id)
with check ((select auth.uid()) = user_id);

create policy "scan_sessions_own_all"
on public.scan_sessions
for all to authenticated
using ((select auth.uid()) = user_id)
with check ((select auth.uid()) = user_id);

create policy "cleanup_actions_own_all"
on public.cleanup_actions
for all to authenticated
using ((select auth.uid()) = user_id)
with check ((select auth.uid()) = user_id);

create policy "storage_snapshots_own_all"
on public.storage_snapshots
for all to authenticated
using ((select auth.uid()) = user_id)
with check ((select auth.uid()) = user_id);

create policy "app_inventory_own_all"
on public.app_inventory_summaries
for all to authenticated
using ((select auth.uid()) = user_id)
with check ((select auth.uid()) = user_id);

create policy "privacy_summaries_own_all"
on public.privacy_summaries
for all to authenticated
using ((select auth.uid()) = user_id)
with check ((select auth.uid()) = user_id);

create policy "battery_snapshots_own_all"
on public.battery_snapshots
for all to authenticated
using ((select auth.uid()) = user_id)
with check ((select auth.uid()) = user_id);

create policy "ai_recommendations_own_all"
on public.ai_recommendations
for all to authenticated
using ((select auth.uid()) = user_id)
with check ((select auth.uid()) = user_id);

create policy "reports_own_all"
on public.reports
for all to authenticated
using ((select auth.uid()) = user_id)
with check ((select auth.uid()) = user_id);

create policy "user_settings_own_all"
on public.user_settings
for all to authenticated
using ((select auth.uid()) = user_id)
with check ((select auth.uid()) = user_id);

create policy "entitlements_read_own"
on public.entitlements
for select to authenticated
using ((select auth.uid()) = user_id);

-- Category summaries inherit ownership via scan_sessions.
create policy "scan_categories_select_own"
on public.scan_category_summaries
for select to authenticated
using (
  exists (
    select 1
    from public.scan_sessions s
    where s.id = scan_session_id
      and s.user_id = (select auth.uid())
  )
);

create policy "scan_categories_insert_own"
on public.scan_category_summaries
for insert to authenticated
with check (
  exists (
    select 1
    from public.scan_sessions s
    where s.id = scan_session_id
      and s.user_id = (select auth.uid())
  )
);

create policy "scan_categories_update_own"
on public.scan_category_summaries
for update to authenticated
using (
  exists (
    select 1
    from public.scan_sessions s
    where s.id = scan_session_id
      and s.user_id = (select auth.uid())
  )
)
with check (
  exists (
    select 1
    from public.scan_sessions s
    where s.id = scan_session_id
      and s.user_id = (select auth.uid())
  )
);

create policy "scan_categories_delete_own"
on public.scan_category_summaries
for delete to authenticated
using (
  exists (
    select 1
    from public.scan_sessions s
    where s.id = scan_session_id
      and s.user_id = (select auth.uid())
  )
);

-- ------------------------------------------------------------
-- Revoke anonymous access by default.
-- ------------------------------------------------------------

revoke all on public.profiles from anon;
revoke all on public.devices from anon;
revoke all on public.scan_sessions from anon;
revoke all on public.scan_category_summaries from anon;
revoke all on public.cleanup_actions from anon;
revoke all on public.storage_snapshots from anon;
revoke all on public.app_inventory_summaries from anon;
revoke all on public.privacy_summaries from anon;
revoke all on public.battery_snapshots from anon;
revoke all on public.ai_recommendations from anon;
revoke all on public.reports from anon;
revoke all on public.user_settings from anon;
revoke all on public.entitlements from anon;
```

### SECURITY REVIEW REQUIRED BEFORE TURNING THE RESEARCH SQL INTO A MIGRATION

1. **Consent default correction:** The original research SQL uses `public.user_settings.cloud_history_enabled boolean NOT NULL DEFAULT true`. This **conflicts with privacy-by-default**. In the final reviewed staging migration for **new users**, change the default to `false`. Do not silently overwrite an existing user's real, previously given consent; explicitly migrate existing state according to actual provenance. Disabled cloud sync must cause **zero** automatic data uploads.
2. **Foreign-owner FK protection:** A valid `user_id = auth.uid()` RLS policy alone does not prove that `device_id` or `scan_session_id` points to a record owned by that same user. Add owner-consistent composite foreign keys and/or verified server-side ownership enforcement; test foreign-record insertion, updates and cross-account relations. Do not leave cross-owner association possible.
3. **Entitlements are server-controlled:** keep the app's JWT able only to SELECT its entitlements; creation and changes require trusted backend/store verification, never client-updated Pro flags. Do not invent billing success.
4. **Permissions and privileges:** verify actual schema GRANT/REVOKE and RLS using real authenticated JWT A and JWT B; do not mistake a privileged SQL editor test for client RLS coverage. `auth.uid()` must be non-null and match the row owner. Test insert/select/update/delete, not select alone.
5. **Schema idempotency:** `CREATE TABLE IF NOT EXISTS` does not make `CREATE POLICY` idempotent. Rewrite into correctly numbered, one-time, ordered migrations with tested rollback strategy and version history. `DROP TRIGGER` may be valid only in appropriate staging. Do not execute scripts against unrelated existing Supabase projects.
6. **Profiles/metadata:** validate user-controlled metadata fields, data types, lengths, enum checks, privacy, access-level restrictions and secure trigger function permissions/search path. Retention/account-erasure policies and remote deletion must be real.
7. **No sensitive JSON:** `metadata`, `summary`, `action_payload` and cloud-synced recommendation descriptions must be schema-validated, size-limited and privacy-filtered. Do not let arbitrary raw URI/path/hash/package details leak via JSONB.
8. **RLS performance:** index real owner columns and relevant lookup fields (`scan_session_id`, device id, time); benchmark policies and queries on realistic rows. Maintain safe `auth.uid()` checks and JWT expiry behavior.
9. **False numbers:** any user or client with local control could forge a summary; reporting must distinguish device self-reported analytics from server-attested values. Do not use self-reported cleanup counters for rewards, billing or external audit without verification.
10. **Execution classification:** until schema tests and endpoint auth pass: `SQL_RESEARCH_REFERENCE`, NOT `MIGRATION_APPLIED`, NOT `RLS_VERIFIED`, NOT `CLOUD_DEPLOYED`.

## 6. PLATFORM API IMPLEMENTATION NOTES — PRECISE BOUNDARIES

### MediaStore + SAF

- Query only accessible `content://` URIs; never convert them into unrestricted absolute filesystem paths. On newer Android versions, consider selected photo access and the user's limited library selections; handle revoked/temporary grants.
- A `MediaStore.createTrashRequest(resolver, uris, true)` call returns a `PendingIntent`; it does **not** mean a file was moved. Launch consent UI with appropriate Activity Result handling; only after OS response and a subsequent query may action status be recorded. For a user-selected SAF document, inspect provider flags before calling `DocumentsContract.deleteDocument()`.
- An app cannot promise a device-wide duplicate scan if the OS only granted selected photos. The UI must say **“Scanned X authorized items”**, not “Full device scanned”.
- Implement cancellation while streaming digest from `openInputStream()`, count bytes precisely, cap concurrent open descriptors, and never hash on the main thread.

### Package and privacy scope

- Android 11+ package visibility filters what `PackageManager` can see; absence from the query is **not proof an app is not installed**. `<queries>` entries must match actual business need. `QUERY_ALL_PACKAGES` is restricted by Play policy, not a convenience switch.
- `UsageStatsManager` requires the user to grant Usage Access in Android Settings; an ordinary app cannot directly grant it. Accuracy and OEM support vary.
- An application cannot assume unrestricted access to other apps' historical permission use, all clipboard readers, hidden trackers, OS private APIs or all other apps' caches. `AccessibilityService` is not a legitimate workaround for unrelated automation.

### Android background work and battery

- Use lifecycle-aware coroutines and `WorkManager` when appropriate, with Android background limits, constraints and user cancel. Foreground service type/notification must be legitimate and declared correctly. No indefinite hidden scan.
- Battery `EXTRA_HEALTH` returns an enum describing reported status; not a percentage of battery capacity. Avoid promising battery lifetime or temperature reductions. A thermal state reading does not grant thermal control.

### Auth

- Android's local `BiometricPrompt` proves the device authenticator completed, not that the remote account belongs to the user. Separate trust/session states.
- Google ID must be verified by trusted backend and correct audience/issuer/expiry; Passkey must use a fresh WebAuthn challenge and real server verification of assertion/origin/RP ID.
- If the owner has not provided authorized project settings, leave external providers in a truthful `CONFIGURATION_REQUIRED` state; don't hardcode REPLACE placeholders into a supposedly working login.

### Backup/privacy

- On Android, `android:allowBackup="true"` in the historical manifest must be re-assessed for Room, media URIs, secret/session data, device restore and Android data extraction rules. Exclude secrets and sensitive local indexed data where appropriate. Validate on test builds.

## 7. QUALITY, SECURITY AND INTEGRATION PROOF MATRIX

At the completion of EACH service, produce a machine-readable evidence row AND a short human report. Use captured outputs, not statements.

| Gate | Required proof | Unverified status if absent |
|---|---|---|
| Source baseline | full `git rev-parse HEAD`, `git status`, worktree/unique work inventory, actual files | `BASELINE_UNKNOWN` |
| Feature code | Kotlin interfaces/use cases/adapter/DAO/ViewModel/UI diff paths | `CODE_NOT_REVIEWED` |
| Android permissions | manifest delta, API checks, user consent/denial/partial cases | `PERMISSION_UNVERIFIED` |
| Unit tests | actual test command, passed/failed counts, stdout/stderr, exit code | `UNIT_NOT_RUN` |
| Room migrations | versioned migration, migration test, old DB upgrade behavior | `DATA_MIGRATION_UNVERIFIED` |
| Emulator or phone run | exact authorized ADB device serial, OS API, install time, launch screenshot | `RUNTIME_NOT_VERIFIED` |
| Positive flow | synthetic fixture inputs, source-backed outputs, before/after evidence | `END_TO_END_NOT_PROVEN` |
| Negative flow | permission denied, OS cancelled, partial data, crash/restart | `ERROR_PATH_NOT_PROVEN` |
| Security | no secret in APK/log, local data isolation, no unauthorized delete | `SECURITY_NOT_VERIFIED` |
| RLS / cloud | staging deployment URL/project identity redacted, 2-user real JWT A/B denial tests | `CLOUD_NOT_VERIFIED` |
| Visual regression | approved reference comparison, insets, 48dp controls, RTL, no clipping | `VISUAL_NOT_ACCEPTED` |
| Release | signed artifact SHA/CI/Play status + owner approval | `NOT_RELEASED` |

### Integration test journey (non-destructive and repeatable)

1. Open the app offline as Guest with no photo grants → Home says **not yet scanned**; zero is not substituted.
2. Grant a controlled test media selection → Storage Analyzer counts the visible selection accurately.
3. Provide two byte-identical synthetic files and one same-size-different file → Duplicate Finder groups only the exact two.
4. Select one duplicate while preserving its keeper → display and obtain OS trash approval → if denied, original remains; if approved, re-query and journal.
5. Home and Reports read **the same** Room journal and accurately reflect completed/partial operations with caveats.
6. Revoke media permission and restart → stale results labelled with last verified timestamp; no fake completed scan.
7. Run privacy limited package case and App Manager filtered visibility case → inaccessible signals labelled unknown, not zero.
8. Battery readings reflect actual OS value, with unsupported health % omitted.
9. Complete Deep Scan with at least one skipped or denied module → no 100% claim for attempted incomplete modules.
10. Enable cloud sync only on staging with owner consent → perform user A/B RLS isolation; disable consent → zero further sync; test erasure.
11. Run English LTR and Arabic RTL layouts, image icons, compact status-bar-safe header, touch targets and no clipped fixed CTAs.
12. Record failures separately. Tests cannot be altered to always pass or skipped to get a green headline.

## 8. FINAL AUTOMATIC EXECUTION LEDGER TEMPLATE

Save/maintain within the LIVE MOBILE repository, e.g. `qa-evidence/end-to-end/EXECUTION_LEDGER.md`:

```text
RUN_ID:
PROJECT_ROOT=/root/workspace/KNOUX-AI
START_HEAD=<actual full SHA>
CURRENT_HEAD=<actual full SHA>
CURRENT_BRANCH=<actual branch>
ACTIVE_CONCURRENT_WRITERS=<observed>
LOCAL_DEVICE=<actual model / API / serial if authorized>
SDK/AGP/KOTLIN/ROOM=<actual versions>
OWNER_VISUAL_GATE=<ACCEPTED/PENDING/UNKNOWN; don't invent>

SECTION_00_FOUNDATION=<VERIFIED/PARTIAL/BLOCKED/UNSUPPORTED> evidence=...
SECTION_01_STORAGE_ANALYZER=... evidence=...
SECTION_02_DUPLICATE_FILES=... evidence=...
SECTION_03_APP_RESIDUE=... evidence=...
SECTION_04_PRIVACY_SWEEP=... evidence=...
SECTION_05_APP_MANAGER=... evidence=...
SECTION_06_BATTERY_SAVER=... evidence=...
SECTION_07_DEEP_SCAN=... evidence=...
SECTION_08_SMART_CLEAN=... evidence=...
SECTION_09_REPORTS=... evidence=...
SECTION_10_AI_OPTIMIZATION=... evidence=...
SECTION_11_HOME_AND_ROUTES=... evidence=...
SECTION_12_SETTINGS_PERMISSIONS_UX=... evidence=...
SECTION_13_AUTH_SESSIONS=... evidence=...
SECTION_14_SUPABASE_OPTIONAL=... evidence=...
SECTION_15_RELEASE_SECURITY=... evidence=...

UNIQUE_WORK_PRESERVED=<paths/commits>
ACTUAL_BUILD_COMMANDS_EXIT_CODES=<...>
UNIT_TEST_TOTALS=<...>
INSTRUMENTATION_TEST_TOTALS=<...>
APK_PATH_SIZE_SHA256=<...>
CLOUD_PROJECT_STATUS=<NOT_CONFIGURED/STAGING_VALIDATED/DEPLOYED>
RELEASE_STATUS=<NOT_RELEASED/IN_REVIEW/RELEASED; documentary proof>
OWNER_REQUIRED_ACTIONS=<minimum actionable list>
FIRST_REPRODUCIBLE_BLOCKER=<...>
AUTO_NEXT_INDEPENDENT_SECTION=<...>
```

**Do not create one report per success claim while leaving functionality empty.** Evidence must be inspectable: tests, screenshots, logs, SHA and actual data provenance. Commit or record checkpoints safely after each vertical slice; no reset/clean/branch pruning that risks unique unmerged work.

## 9. SOURCES — PRIOR PROJECT MATERIAL AND OFFICIAL UP-TO-DATE LINKS

### Recovered project/research materials

- Deep Search research report (10 Nov 2026): `بحث تنفيذي لبناء التطبيق الظاهر في الصور بالكامل باستخدام Coding Agents` — primary origin of the **13-table SQL** block above and the device/API architecture.
- `KNOUX-AI_FULL_SOURCE.txt` — consolidated historical source and files: `docs/architecture/REAL_DATA_AND_BOUNDARIES.md`, `docs/policies/ANDROID_PLAY_CONSTRAINTS_2026_11_10.md`, `docs/research/SOURCE_LEDGER.md`, `docs/testing/FAKE_CLOSURE_PRODUCTION_GATE.md`, `prompts/PHASE_2_MASTER_SERVICES.md`, `prompts/phase2/{01..10}_*.md`, `qa-evidence/knoux-ai-visual-finish/SCREEN_MAP.md`.
- Historic phone snapshot / **not live state**: `D:\KNOUX-AI-Phone-Exports\KNOUX-AI-phone-snapshot-210018.zip`; SHA-256 `cef861bc756c6aeda6a85b36c07d12f798280e9fd368d5d545b332298fbf2e88`.
- Shared phone directory with **empty leaf files when last inspected**: `/sdcard/Download/KNOUX_AI_COMPLETE_CODEX_DATABASE_ANDROID_PLAY_PRODUCTION_KIT/KNOUX_AI_COMPLETE_HANDOFF/`; do not claim its SQL migrations or Kotlin integration files actually exist on disk.
- This file is intended to supersede **old Windows `D:\KNOUX-AI` paths and obsolete STOP-after-every-service rules** in recovered historical handoff prompts; preserve technical facts but follow the owner's present mobile-only continuous-execution request.

### Official Android / Google Play docs (checked 2026-11-12 where noted)

- Google Play target API level: https://support.google.com/googleplay/android-developer/answer/11926878
- MediaStore and scoped shared-media rules: https://developer.android.com/training/data-storage/shared/media
- MediaStore trash (API 30+): https://developer.android.com/reference/android/provider/MediaStore#createTrashRequest(android.content.ContentResolver,java.util.Collection,boolean)
- Photo Picker: https://developer.android.com/training/data-storage/shared/photo-picker
- Storage Access Framework: https://developer.android.com/training/data-storage/shared/documents-files
- `MANAGE_EXTERNAL_STORAGE` Play policy: https://support.google.com/googleplay/android-developer/answer/10467955
- Package visibility: https://developer.android.com/training/package-visibility
- `QUERY_ALL_PACKAGES` Play policy: https://support.google.com/googleplay/android-developer/answer/10158779
- UsageStatsManager: https://developer.android.com/reference/android/app/usage/UsageStatsManager
- BatteryManager: https://developer.android.com/reference/android/os/BatteryManager
- PowerManager thermal: https://developer.android.com/reference/android/os/PowerManager
- Android Room: https://developer.android.com/training/data-storage/room
- Background work: https://developer.android.com/develop/background-work/background-tasks/persistent
- Credential Manager and Google sign-in: https://developer.android.com/identity/sign-in/credential-manager
- Passkey with **server-issued challenge**: https://developer.android.com/identity/passkeys/sign-in-with-passkeys
- Android 15/16 edge-to-edge/insets: https://developer.android.com/develop/ui/compose/system/insets

### Supabase / PostgreSQL docs

- Supabase Row Level Security: https://supabase.com/docs/guides/database/postgres/row-level-security
- Supabase Auth: https://supabase.com/docs/guides/auth
- Supabase Kotlin: https://supabase.com/docs/reference/kotlin/installing
- Supabase Edge Functions auth: https://supabase.com/docs/guides/functions/auth
- Supabase Functions secrets: https://supabase.com/docs/guides/functions/secrets

**All outside links are engineering references; they do not prove an integration has been installed, a DB deployed, policy approved, or a feature tested.**

---

# 10. FINAL BEEM EXECUTION COMMAND — FOLLOW THIS WITHOUT PAUSING FOR NORMAL WORK

**THIS IS AN IMPLEMENTATION ORDER, NOT A REQUEST FOR IDEAS OR A PLAN.**

Work ONLY in the current phone-hosted `/root/workspace/KNOUX-AI`. Read the complete master file and historic research, audit the actual HEAD/untracked/agent state, and protect all unique work. Preserve the owner-approved KNOUX visual identity and current functional screens. Implement Section 00 through Section 15 **in the exact dependency-safe order**, each as a complete real Android vertical slice: platform adapter → typed data/repository → Room persisted state → ViewModel/UI integration → permissions and safe action handling → unit/instrumentation/runtime tests → evidence and safe Git checkpoint.

Immediately auto-advance after each section has actually passed its own gate. Do not stop to ask permission for the next service, generate another feature plan instead of code, call a fixture “live”, or claim screenshots/QA/tests you didn't run. If owner-provided account keys, service configuration, special Android consent, proprietary private device access or Play approval is unavailable, implement the safe local slice, mark the dependent external integration **CONFIGURATION_REQUIRED/BLOCKED**, and continue every independent section. Do not invent credentials, use root/Shizuku production bypasses, erase user files, or touch the PC project. Never declare ALL COMPLETE/PRODUCTION/RELEASED until all corresponding independently verified gates are satisfied. Preserve the complete run ledger and deliver the actual results, APK checksum, tests, current Git SHA, pending owner-only dependencies, and actionable first release blocker.

**GO: AUDIT → IMPLEMENT → VERIFY → RECORD → NEXT, REPEATED UNTIL ALL INDEPENDENT SERVICES ARE COMPLETE.**
