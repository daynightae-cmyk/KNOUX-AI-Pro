# KNOUX AI PRO — CODEX IMMEDIATE UI-FIRST CONTINUATION
# Owner directive 2026-11-15 | Target: Windows K:\KNOUX-AI-Pro ONLY

YOU ARE CODEX, THE IMPLEMENTING ANDROID ENGINEER. START WRITING CODE IN THIS SESSION. Do not produce a plan instead of implementation. Explain status/progress to the owner in natural Egyptian Arabic; keep code and technical identifiers in English.

## Scope lock and current handoff
- Canonical working tree: K:\KNOUX-AI-Pro
- Git remote: https://github.com/daynightae-cmyk/KNOUX-AI-Pro.git
- Integration branch: master (NOT main). Recorded baseline before handoff: e0f4558; refresh actual HEAD and live origin/master before edits.
- Knowledge entry: K:\KNOUX-AI-Pro\PROJECT_KNOWLEDGE\README.md
- Knowledge map: PROJECT_KNOWLEDGE\90_INDEX\KNOWLEDGE_CATALOG.md
- Visual archive target: K:\KNOUX-AI-Pro\design\owner_references_20261115\KNOUX_AI_PRO_VISUAL_REFERENCES_DEDUP_20261115.zip
- If the new archive has not yet been physically copied to this PC, do NOT pretend it is present. Read already available design/refs20_complete/, design/original_hi_res/, design/original_jpeg/, design/reference/, and PROJECT_KNOWLEDGE/04_DESIGN_AND_IMAGES; implement immediately from the real assets on disk. Reconcile the owner's archive when it becomes available without resetting or restarting.
- The owner supplied 50 new images; 10 near-identical visually repeated images were filtered, leaving 40 distinct images. Of these, TEN independent large composite blueprints are the priority, one per service with all its subfeatures. Keep composites intact.
- The existing application is a real Kotlin + Jetpack Compose Android app; EXTEND its current code, navigation, identity, theme, assets, UI behavior and backend connections. DO NOT recreate the project, template, application ID, or shell.
- Separate projects D:\KNOUX-AI and /root/workspace/KNOUX-AI are STRICTLY OUT OF SCOPE. Do not copy/merge/sync source, artifacts, commits or status between them.

## P0: Rapid 30-60 SECOND safety/ownership probe (not a long audit)
1. Execute the minimal truthful probes: pwd, git status --porcelain -uall, git rev-parse HEAD, git ls-remote origin refs/heads/master if available, git worktree list, git stash list, process/agent ownership, free disk, adb devices -l (keep identifiers local/private). Observe modified/untracked files, do not discard them.
2. At writing of this handoff, DesignSystem.kt and MoreScreensV2.kt were modified and multiple OpenCode processes were running. These are possibly ACTIVE UNIQUE WORK. Never overwrite these files or parallel-agent sections; inspect ownership and avoid file collisions. Prefer NEW service-specific Compose files and isolated additions; integrate with existing navigation only when file ownership is clear.
3. No git reset --hard, git clean, force push, branch pruning, checkout over dirty files, deletion of unique art, or indiscriminate git add -A. Preserve worktrees, untracked art, Room migrations, and existing runtime.
4. Identify unique commits not in master and safely review them for selective non-destructive integration when no active writer conflict. Do NOT blindly merge every branch or call an agent log "verified".
5. The existing working app matters more than a historical design prompt. Existing MASTER_PROMPT_CODEX_PHASE_1_UI.md describes an OLD D-drive visual-only phase. It is NOT today's execution authority.

## P1: CODE NOW. Visual fidelity > lengthy regression-suite loops
Read PROJECT_KNOWLEDGE/README.md and only RELEVANT catalog entries, scan the existing routes and theme, then immediately implement/refine the ten live sections, retaining current visuals and behavior. Do not stop to build a big plan, reconstruct the architecture, regenerate image packs, or benchmark dozens of models.

The ten section reference priorities:
01 AI Smart Clean — selectable real review surfaces, progress, findings, safe action buttons.
02 Duplicate Finder — exact/similar groups, thumbnails, keeper protection, selection and review.
03 App Residue — accessible residual entries, type/source, selection, exclusions and confirmation.
04 Privacy Sweep — actual permissions/capability observations, app details, settings handoff.
05 Deep Scan — real scope/progress/results, phase panels, cancel and report navigation.
06 Reports & Insights — observed history, report cards/charts/exports, proper empty states.
07 App Manager — installed apps, search/filter/details, OS launch/settings/uninstall actions.
08 Storage Analyzer — storage charts, categories, sizes, large files, sort/search/drill-down.
09 Battery Saver — genuine BatteryManager/thermal observations and supported user actions.
10 AI Optimization — measured inputs, transparent recommendations and approved OS actions.

The user-provided composite visuals include sub-services and layouts. Match their header, branded K logo, illuminated violet/cyan/magenta glass cards, restrained gold PRO elements, hero orbit, glowing stag/landscape art where approved, RTL Arabic typography, icons, spacings, service layout, navigation bar, and interactions. Reuse existing approved images and fonts, do not draw giant fingerprints or generic Material replacement screens. Every component is actual native Compose, no full screenshot as a clickable fake UI.

Do not copy the sample values (92% health, fictitious reclaimed GB, boosts, threats, temperature drops) as real device results. Bind an EXISTING real provider when available; otherwise show truthful unknown/permission-required/not-scanned state. Do not refactor, disable or replace existing real Android service/data code just to match design.

## P2: Maximum screen throughput
- Implement one coherent screen or group per iteration in existing modules, minimizing unrelated edits. Favor reusable existing visual primitives; extend them narrowly if needed.
- Priority: complete missing visible category screens and service detail routes before broad UI polish of an already adequate Home screen.
- Ensure all actual visible controls navigate and interact. If a capability is restricted by Android, show reason and OS-supported handoff; never simulate cleanup/speedup/security success.
- Preserve Splash/Smile animation, three onboarding chapters, Founder, Login, OTP, Google Sign-In, Passkeys, small biometric option, Identity Success and all unique approved animations; fix only clear visual regressions in scope, do NOT build second login or central giant fingerprint.
- First-run navigation remains Splash → existing three onboarding screens → real Auth → Home, with subsequent launches skipping completed onboarding, no imaginary authenticated session.
- Android status/navigation bars belong to the OS. Do not embed screenshot bars or fake system clocks.
- Service content may scroll; Login/Auth should fit its screen appropriately, with insets/keyboard/accessibility and no header/CTA overlap.
- Use native visual effects efficiently; don't add expensive engines/dependencies for a mockup.

## P3: One quick phone preview per iteration — then NEXT
- Owner has connected an authorized Samsung over ADB to the PC; recheck adb devices (do not print serial in a public commit). Use the actual available device and build toolchain only.
- Once a coherent UI slice compiles, run the least-expensive scoped assembleDebug/smoke check, install the APK on the authorized Samsung, launch the target activity/route, capture an actual screenshot and compare it against the reference. Record only local redacted evidence.
- If a full instrumented suite is slow or old tests are failing, DON'T spend the whole quota debugging unrelated failures. Record exact NOT_RUN/FAIL, prioritize coding the next independent screen, and continue. Do not claim tests PASS by skipping them.
- If compile fails due to YOUR latest changes, repair only that compile break before installing; never tell the owner a screen is working when it cannot build.
- Do not delete personal data or change Samsung settings without explicit consent. Visual testing is in scope; simulated deletion is not.
- Keep status updates short in Egyptian Arabic: "Screen X edited / installed on Samsung yes-no / screenshot path / next screen".

## P4: Safe checkpoint and integration, not fake closure
- Before any commit check actual git status and source ownership again. Stage ONLY specific safe source/assets/docs for the completed slice; NEVER stage private credentials, ADB serial/IP, raw logcat, screenshots with personal data, or the knowledge library wholesale.
- Commit to existing working integration branch master only if independently buildable and there is no collision with active OpenCode edits. Push NON-FORCE and verify the remote SHA. If blocked/unverified, leave code safely local and report LOCAL_ONLY; do not bury/erase it.
- Failed tests or unverified real service operations must remain clearly labelled unverified. Never call COMPLETE/RELEASED from UI screenshots alone.
- NO mass cleanup, force merges, git history changes, deletes, or automatic backup purge. Do not waste the owner's limited Codex quota on exhaustive tests/reports unless a blocking safety/compile issue demands it.

## Delivery while coding
For each screen: exact changed file paths, the matched reference filename, whether app compiles, whether APK installed/route opened on Samsung, evidence screenshot path (local private), Git local/remote status, and ONE next section. Continue without owner prompt until the quota/session ends; no artificial phase STOP, no repeated planning, no discarding unique work.

BEGIN AT K:\KNOUX-AI-Pro. FIRST READ THE KNOWLEDGE ENTRYPOINT AND ACTIVE UI SOURCES, THEN WRITE CODE IMMEDIATELY.
