# KNOUX AI PRO — UI BATCH FINISHER / EXECUTE NOW
Owner instruction — 2026-11-16. This replaces repetitive screen-by-screen planning and endless ADB loops; it does NOT replace or reset existing code.

YOU ARE THE CONTINUING OPENCODE AGENT. IMPLEMENT, DON'T PLAN. Communicate progress in short, natural Egyptian Arabic; technical identifiers/code remain English.

TARGET / KNOWLEDGE / REFERENCES
- Only Windows: K:\KNOUX-AI-Pro, GitHub https://github.com/daynightae-cmyk/KNOUX-AI-Pro.git, branch master.
- Do not touch independent D:\KNOUX-AI or /root/workspace/KNOUX-AI.
- Read only relevant entries of PROJECT_KNOWLEDGE/README.md and PROJECT_KNOWLEDGE/04_DESIGN_AND_IMAGES/IMAGE_INDEX.md.
- Primary owner archive: design/owner_references_20261115/KNOUX_AI_PRO_VISUAL_REFERENCES_DEDUP_20261115.zip (SHA256 previously verified, 40 images; TEN intact blueprint images at 01_TEN_SERVICE_BLUEPRINTS/).
- Secondary visuals: design/refs20_complete, design/original_hi_res, current approved local assets. Current app/runtime code takes precedence over historical design docs.
- Before any write, refresh live HEAD, origin/master, git status -uall and check active authors. Latest observed SHA a70ffa5322f61a8c0f0dc2d9e5d0dc5c2b24e3a5 was committed AND pushed to origin/master while this prompt was being prepared; AndroidManifest.xml remained locally modified and untracked files including DeviceReality.kt, OnboardingV2.kt and ProState.kt may hold unique work. Re-check before touching anything. Preserve ALL unique work. Do not change files under parallel-agent ownership.

CRITICAL REVISED GOAL
Many screens ALREADY exist: V2DeepScanScreen, V2DuplicatesScreen, V2ResidueScreen, V2PrivacyScreen, V2CleanHubScreen, V2ToolsHubScreen, V2SmartCleanScreen, V2ReportsScreen, V2AppManagerScreen, V2StorageScreen, V2BatteryScreen, V2AiScreen, plus numerous subservice screens in MoreScreensV2.kt. DO NOT generate duplicate screens, create a second navigation graph, or rewrite approved work.

CREATE A REUSABLE VISUAL FINISHING LAYER, NOT A NEW APP:
1) Inventory existing Composables/routes and map the ten owner blueprints to actual screens; mark present / needs polish / absent / device-verified.
2) Extract the archive once ONLY if needed, into an isolated folder, without overwriting source assets. The ten ALL_SERVICES composites are visual blueprints; preserve them intact.
3) Reuse existing Compose primitives. If actually missing, implement a small shared KnouxServiceShell, ServiceVisualSpec, HeroVisual, ServiceMetric, SubServiceTile, ReviewActionBar, CapabilityState in NEW focused Kotlin files; don't cause a giant refactor.
4) Apply one consistent KNOUX look across current screens: official K mark, stag/landscape where approved, dark obsidian purple, violet-magenta-cyan glow, restrained champagne gold for Pro, Arabic-first RTL, glass cards, distinct service-specific hero art, service grids and real navigation; maintain safe insets, screen scaling, and touch targets.
5) Use a per-service visual spec for header, hero, subservices, metrics, CTA, empty/loading/review states. Wire into EXISTING Compose functions and routes; retain original service behavior and existing data providers. Don't convert interactive screens into a PNG or fake static UI.
6) In each batch polish 3-4 related screens and the missing subroutes, using the existing implementation. Prioritize visibly incomplete screens and the ten blueprints: Smart Clean, Duplicate Finder, App Residue, Privacy, Deep Scan, Reports, App Manager, Storage Analyzer, Battery Saver, AI Optimization. Preserve work already completed and avoid revisiting a screen whose visual behavior is verified unless there's a specific regression.
7) NEVER fabricate percentages, reclaimed GB, threats, power-saving time, permission changes, or operation histories. Use real Android provider observations; show not-scanned/unavailable/needs permission when no valid data is available. Keep OS-confirmed user actions, Room history, review/selection, retry and restore where supported. No impossible Android permissions or unauthorized cleanup.
8) Don't alter Splash Smile, approved Onboarding x3, Founder, Login/OTP, Google, Passkeys, small biometric, Identity Success, logos, or audio behavior unless a proven regression directly blocks this task.

THROUGHPUT RULE — 80% IMPLEMENT / 20% VERIFY
- No lengthy design writeups, architectural redesign, exhaustive test suites, repeated APK rebuilds, or endless diagnosis of the same screenshot.
- For EACH batch: edit 3-4 existing screens => 1 scoped Gradle assembleDebug => 1 APK install on authorized Samsung => capture key route screenshots and compare to owner blueprints => fix only new blockers => immediately move on.
- If compile fails from your new changes, fix it before running. If unrelated tests fail, record FAIL/NOT_RUN and continue independent screens. Do not call these PASS.
- Avoid adb uninstall / pm clear / deleting app data / modifying phone settings. No forced stop of another agent and no parallel workspace mutations.
- If a screen displays blank content, spend at most one focused reproduction/triage round, then isolate the blocker and continue on independent screens; record its status honestly. Never hide a compile failure.
- Maintain a VERY SMALL progress ledger with screen / source path / visual status / build status / Samsung screenshot path. Don't waste cycles on enormous reports.
- Minimize turns, don't keep asking for permission for non-destructive in-scope edits, and do not stop after a plan. Start the first batch NOW.

GIT SAFETY / HANDOFF
- Stage explicit safe paths only, never secrets, serials, ADB endpoints, personal screenshots, or the knowledge vault wholesale.
- Commit/push to master only after owner-independent changes are compiled, concurrent modifications are protected and merge is safe; verify remote SHA after any push. If uncertain leave LOCAL_ONLY and explain.
- Never reset --hard, clean, force-push, delete a branch/worktree or unique untracked content, or claim production release from a UI build.
- Short final update in Arabic must separate IMPLEMENTED / BUILD PROVEN / SAMSUNG VERIFIED / LOCAL ONLY / NOT VERIFIED and state exact files and next batch.

BEGIN WITH CURRENT APP COMPOSABLES. DO NOT DUPLICATE EXISTING SERVICES. IMPLEMENT THE FIRST BATCH NOW.
