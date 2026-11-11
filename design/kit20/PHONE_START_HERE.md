# KNOUX AI — OPENCODE PHONE-ONLY IN-PLACE VISUAL COMPLETION COMMAND

**ROLE:** Principal Android/Product Engineer + Motion Director + UI/UX Architect + Git Forensics Specialist + Mobile Build/QA Lead.

**MISSION (OWNER DIRECTIVE):** أنت تعمل من خلال OpenCode المفتوح عندي على **هاتف Android**. عدِّل التطبيق الموجود بالفعل داخل مسار المشروع الحالي أو مساحة عمله الحقيقية؛ لا تبدأ من الصفر، ولا تنشئ مشروعاً بديلاً، ولا تنقل العمل إلى كمبيوتر، ولا تفترض وجود أقراص C:/D:/K:. كل المخرجات يجب أن تكون تعديلات فعلية متراكمة على المشروع نفسه مع أدلة. التصميم المرجعي ليس بديلاً عن الوظائف الفعلية.

**REFERENCE PACK:** `KNOUX_AI_20_REFERENCE_VISUAL_FINISH_KIT.zip` (the user's existing 20-reference visual-finish archive). Read `MASTER_PROMPT.md`, `manifest.json`, `DESIGN_TOKENS.json`, `VISUAL_QA_CHECKLIST.md`, `README_AR.md`, `references/`, and `source-originals/`. This mobile-specific execution command OVERRIDES any instruction in the old prompt that assumes Windows, laptop, remote computer, moving the project, or starting from scratch. The existing main/master project and its working code are the source of truth. The 20 images are visual targets, not functioning Android screens, real telemetry, or verified services.

## 0. THE FIRST RESPONSE MUST BE EVIDENCE, NOT A PLAN

You already have an active OpenCode session and apparently existing Shell Command/Write File/build log activity. FIRST reconnect to the actual current workspace/session; inspect what that session has already done, especially modified files and build logs. DO NOT discard its work, restart a scaffold, create another clone, or create a redundant branch. If the active run is editing the same files, avoid racing/overwriting it. You are tasked with CONTINUATION and RECONCILIATION, not speculative replacement.

Check (use only commands supported by your current shell; no failing command should abort later independent checks):

```sh
printf '\n=== RUN ENVIRONMENT ===\n'; pwd; uname -a; id
printf '\n=== ANDROID DETECTION ===\n'; command -v getprop >/dev/null && getprop ro.build.version.release || true
command -v termux-info >/dev/null && termux-info || true
printf '\n=== TOOLCHAINS ===\n'
for t in git node npm java javac gradle adb unzip python3; do command -v "$t" || true; done
printf '\n=== GIT ROOT ===\n'; git rev-parse --show-toplevel 2>/dev/null || true
printf '\n=== GIT REALITY ===\n'; git status --short 2>/dev/null || true
git rev-parse HEAD 2>/dev/null || true
git branch -vv 2>/dev/null || true
git worktree list 2>/dev/null || true
git stash list 2>/dev/null || true
printf '\n=== EXISTING BUILD EVIDENCE ===\n'; ls -lh build2.log opencode/build2.log 2>/dev/null || true
```

- IMPORTANT: **OpenCode visible on a phone does NOT prove that its Shell Commands execute inside that phone.** Establish the real runtime. Native Termux/Android runtime can be evidenced by Android paths, `getprop`, Termux info, and filesystem access. A cloud/Linux runner is NOT the phone. If shell is remote, do not claim it built or installed an app on the phone. Continue with authorized in-place changes in the existing accessible repo if possible; identify the exact phone-specific build/install blocker, without secretly moving the project to a PC or greenfield reimplementation.
- Find project via current open workspace, `git rev-parse`, existing session/tool state, and recent commands. If cwd is not a repo, inspect *only* authorized likely workspace directories (`$HOME`, the active workspace directory, existing Termux project folder, or permitted Android shared-storage folders), with bounded-depth non-destructive reads. Do NOT recursively scan the whole device, private application sandboxes, or unrelated data. Do NOT guess a repo name/path from the images.
- Inspect the actual stack: Kotlin/Compose/Android Views, React/Vite/PWA, React Native, Flutter, hybrid, etc. Preserve the detected stack and working dependencies. No technology rewrite without necessity and proof.
- Report `runtime = on-phone Termux / remote cloud / other / unknown`, `repo root =`, `HEAD =`, `branch =`, `dirty files =`, `unique work =`, `build system =`, `available Android tools =`, `current UI runtime =`, `20 images accessible = yes/no`. Then **START EDITING**, rather than only writing a roadmap.

## 1. NEVER REBUILD THE EXISTING PRODUCT

REQUIRED WORKFLOW: **REALITY -> RISKS -> UNIQUE WORK -> GAPS -> SAFE NEXT ACTION -> IMPLEMENT -> VERIFY**.

- Preserve old screens, working routes, services, auth flows, resources, animations, navigation, persistence, accessibility, UI states, and already-approved assets. Before modifying any file inspect its ownership/history and any parallel agent activity.
- Inspect HEAD, origin/main if available, local commits, old PR integrations if accessible, working tree, untracked files, active worktrees, stashes, prior OpenCode session changes, and screenshots. A non-main branch can contain irreplaceable work. No cherry-pick, rebase, merge, branch switch, or cleanup until uniqueness is understood.
- Do not run `git reset --hard`, `git clean`, `rm -rf`, `git push --force`, destructive rebase, worktree removal, or deletion of files/branches. Do not erase backups/caches without evidence of safe disposability, and do not delete user media, Android app data, or source files.
- Never run `npm create`, `vite create`, `flutter create`, `npx create-expo-app`, `gradle init`, new `git init`, or a fresh clone as a substitute for the existing project.
- Build reusable **in-place** design components in the CURRENT app: tokens, header/nav, shared gradient borders, authentic logo renderer, animated hero surfaces, stat widgets, status pills, graphs and safe interaction dialogs. Keep UI state connected to existing viewmodels/store/controllers; do not replace service implementations with fake/demo data.
- If a feature is already implemented and works, enhance visuals/interaction only where the reference demands it. If it exists unmerged, protect/reconcile the unique work before creating another implementation.

## 2. PHONE-ONLY EXECUTION AND STORAGE DISCIPLINE

- The work is requested from OpenCode on Android PHONE. Prefer the existing on-device project if you have actual on-device filesystem access. Do NOT request or assume a laptop, Windows PowerShell, remote desktop, D:/K:/C:, or desktop emulator.
- Use the actual build tool already used by the repo. If a web app already runs in Termux, run its existing dev script and preview via the phone browser using a correctly bound localhost port. If a native Android app already builds on-device, use the existing local JDK/SDK/Gradle setup and build the existing module in its existing path. Do NOT install a multi-gigabyte Android SDK or switch toolchains without checking storage and feasibility first.
- Check available disk and memory before large dependency installations/builds (`df -h`, `free -h` if present), avoid extra project copies, avoid multiple Gradle daemons or uncontrolled parallel builds, reuse trusted caches, and clean only clearly ephemeral build outputs after confirmation they are reproducible and not in use. Never delete source, assets, unique work, or active logs for space.
- A web preview is NOT a verified native APK. A successful compiler exit is NOT a verified install, launch, or production-ready app. If the execution environment is remote, do not label remote build as an on-phone build.
- Do not claim `adb install`, device launch, UI tests, audio recording, or on-phone APK validation unless an actual output proves it. If installation is possible, install only with the owner-authorized Android flow and verify launch/runtime; never silently change default launcher, root permissions, or privileged settings.
- All shell commands should be bounded, idempotent where feasible, and log clear output and exit codes. Do not overwhelm the phone with multiple builders. Respect existing shared/session state.

## 3. BRAND AND DESIGN SYSTEM — PINNED TO REFERENCES

Authentic mark from `source-originals/11_Official_Knoux_AI_Logo.png`: preserve the specific **K** geometry, purple/magenta wedge, crossing ribbon, cyan leg, proportions, lowercase exact wordmark `knoux ai`, and precise positioning. The circular logo backdrop is not mandatory everywhere; use the real K symbol inside designated orb/hero areas. Do not invent an unrelated K or replace with a font glyph.

Design direction: ultra-polished vivid **midnight black / deep indigo / saturated royal purple / electric cyan / controlled magenta**, glossy refined glass, energetic holographic hero artwork, sharpened typography, precise gradients, realistic depth and light falloff, sophisticated contrast. Avoid muddy gray panels, flat cheap purple fills, blur everywhere, excessive bloom over text, clutter, fake phone frame inside the actual UI, and static screenshot backgrounds.

Suggested tokens (adapt existing system only): BG `#080512`, SURFACE `#120826`, VIOLET `#843BFF`, PURPLE `#B047FF`, MAGENTA `#E34CFF`, CYAN `#00DCFF`, SKY `#21B9FF`, TEXT `#F8F6FF`, MUTED `#B9B0D0`. Use semantically limited green/amber/red for actual statuses. Provide reusable transitions, responsive spacing, shape radii, and typography. Maintain Arabic/English RTL/LTR and system insets, keyboard, gesture/nav areas, 48dp touch targets, accessibility, high-contrast labels, reduced-motion alternatives, and existing Day/Light mode if present.

The visual target is a **functional app**, not a 9:16 poster. Each reference screenshot includes a mock phone bezel, fake clock and status icons: NEVER bake the bezel, time, cutout, or device frame into the app. Numeric metrics in images are example data and must not be hardcoded as real readings.

## 4. SPLASH / ENTRY ANIMATION — HIGHEST PRIORITY, MUST ACTUALLY MOVE

Reference: `references/01_Splash_Logo_Smile.png`; source logo `source-originals/11_Official_Knoux_AI_Logo.png`.

Implement frame-driven branded animation ON the existing app's startup route/screen. Do not just fade a PNG. Use actual path reveal/masking/trim/staggered keyframes preserving exact K silhouette; support current Android startup/splash contract in the detected stack. Target approximately 2.7–3.3 sec only for a cold user-visible branded reveal, with adjustments for slow devices and accessibility:

1. `0–180ms`: near-black indigo stage, subtle concentrated violet/cyan light ignition.
2. `180–600ms`: luminous writing tip traces the ORIGINAL K's magenta upper wedge; the geometry fills progressively behind the tip; brief tapering spark tail.
3. `520–1120ms`: the K's signature diagonal ribbon is *drawn* left-to-right following its actual geometry, with magenta→violet→electric cyan color transition, then lower cyan leg resolves. Use restrained intersection flare.
4. `1030–1680ms`: reveal `knoux ai` progressively along its authentic typography, **not** unrelated handwriting. `knoux` white, `ai` violet-pink, correct kerning; ensure readable final shape.
5. `1600–2420ms`: grow a graceful shallow curved **SMILE UNDER THE WORDMARK** from left→right as a genuine stroked path (purple→cyan). It must look like a genuine smile, not a full circle or random light trail. The stroke remains aligned under the exact text width.
6. `2370–2550ms`: precise spark at the far-right smile tip. Play **ONE** refined 35–100ms soft click/tick SFX **synchronized to the moment the pen touches the tip**, once per eligible reveal, no repetition and no jarring beep. Preload sound, observe user mute/silent/DND/audio preferences; opt-out and reduced-motion supported. No voice.
7. `2550–3100ms`: resolve the full logo in crisp final state, then smoothly transform/reveal the actual Home hero/orb, with particles/orbits settling; no black flash, second splash, visual jump, repeated audio, layout shift or dead screen.

Cold/warm/hot start, authentication routing and deep links must remain correct. On modern Android, handle mandatory system splash correctly, then run the branded in-app reveal as supported; do not abuse system splash duration. Avoid replay on every app resume or every back navigation. Capture **real on-device screen recording + audio evidence when available**. If audio capture is not possible, state exactly what was tested and not tested.

## 5. ALL 20 VISUAL REFERENCES — EXTERIOR AND INTERIOR IN THE SAME APP

Resolve each numbered file in `references/`, map it to the existing component/activity/route, and MODIFY it IN PLACE. Avoid writing duplicate versions of any existing screen. Screens remain connected, responsive, visually alive, and accessible.

| Ref | Existing surface to polish/complete | Mandatory detail/action |
|---|---|---|
| `01_Splash_Logo_Smile.png` | App entry | K write-on, wordmark, smile, synchronized single click, Home handoff |
| `02_Home_Dashboard.png` | Existing Home | Real hero orb, strongest gradients, CTA, meaningful scan summary, six service cards, live navigation |
| `03_Privacy_Sweep.png` | Privacy exterior | Score and available privacy categories, permission-aware reviews; route to 13 |
| `04_Duplicate_Files_Finder.png` | Duplicates exterior | Accessible file types/group counts and recoverability; route to 12 |
| `05_Deep_Scan.png` | Scan progress exterior | Actual staged scan progress, module states and cancel/pause semantics; route to 14 |
| `06_Reports_Insights.png` | Reports exterior | Actual event/history summary; route to 15 |
| `07_App_Residue_Cleanup.png` | Residue exterior | Honest safe candidates with preview/selection; route to 16 |
| `08_Storage_Analyzer.png` | Storage exterior | Real accessible sizes/categories; route to 18 |
| `09_Battery_Saver.png` | Battery exterior | Accessible readings, actions within Android permissions; route to 19 |
| `10_AI_Optimization.png` | AI optimization exterior | Verified/estimated recommendations, honest unsupported states; route to 20 |
| `11_Smart_Clean_Center.png` | Cleaning interior | Categories, size/count, per-item selection, preview, confirmation, actual result |
| `12_Duplicate_Group_Review.png` | Duplicate interior | Group gallery, file metadata, keep-best-copy invariant, preview, safe deletion flow |
| `13_Privacy_Risk_Inspector.png` | Privacy interior | Per-app permissions, permission/OS-settings handoffs, realistic limitations |
| `14_Deep_Scan_Summary.png` | Scan interior | Completed scan timeline/findings/actions using true recorded scan results |
| `15_Performance_Report_Detail.png` | Report interior | Inspectable charts/history, export/share when implemented, accurate empty states |
| `16_Residue_Item_Review.png` | Residue interior | Real paths/URIs, previews, exclusion, scope-limited confirmed deletion |
| `17_Installed_Apps_Review.png` | App Manager interior | Installed app details, supported app actions, no fake privileged control |
| `18_Storage_Breakdown.png` | Storage interior | Detailed categories/largest files, auditable totals and safe action prompts |
| `19_Battery_Diagnostics.png` | Battery interior | Real battery signals and clearly labeled estimates/recommendations |
| `20_AI_Recommendations.png` | AI interior | Transparent rules/AI signals, before-after estimates separated from measurements |

**Critical extra existing screen:** `source-originals/07_App_Manager.png` is the App Manager EXTERIOR baseline; it must remain available and be polished with the same component system, then link to interior ref `17`. Do NOT skip it because no separate exterior redesign PNG was generated. Preserve any additional existing services/routes not listed here.

Every clickable control must either execute its supported real action or accurately open an explanation, preview, permission request, or device Settings handoff. No misleading "fixed", "boosted", "tracker blocked", "98 score", "battery extended" without real verification. Never delete any file merely because two photos look similar. Require keep-original preservation, meaningful preview, review and explicit confirmation. SAF/MediaStore/Android scoped storage rules apply; do not bypass privacy/sandbox restrictions. Battery or app-hibernation changes requiring privileged access must not be represented as universal one-tap fixes.

## 6. ANIMATION SYSTEM — LIFE WITH DISCIPLINE

Implement an actual shared motion system in existing framework, not endless decorative loops:

- Orb heroes: quiet living mesh, tiny rotational/parallax depth, short ring sweeps on entering, reduced rate/paused when offscreen; no continuous GPU-heavy particle flood.
- Dashboard: score arcs and charts grow **from real data** once, cards stagger into place (e.g. 30–65ms separation), reveal shadows and highlights with composited transforms; no flashing or motion sickness.
- Card presses: crisp ~90–150ms micro-compression, cyan/violet response, tactile haptics only when enabled/supported. Controls respond to touch visibly.
- Navigation: coherent ~180–320ms forward/back shared fade/slide/depth transition; maintain stack and saved state. No full reload on every tap.
- Scan: separate indeterminate animation from measured per-module completion; progress must reflect real work, no fake fixed 72%. Handle pause/cancel/retry lifecycle reliably.
- Cleanup/confirm: selected items update animated totals from actual candidates; confirm before deletion; show genuine success, partial failure and undo/restore only where technically supported.
- Detail/insight/report: expand/collapse with smooth measured motion, charts render correctly, errors/skeletons are polished, all lists scroll independently and remain touchable.
- Performance: aim for smooth 60fps on a mid-range phone, avoid heavy shader/blur overdraw and uncontrolled `requestAnimationFrame` loops; use native/composited transforms when available; respect reduced motion, backgrounding, battery saver, and lifecycle cleanup.

## 7. ACTUAL IMPLEMENTATION LOOP — DO NOT STOP AT PLANNING

**Sequence:** (A) read actual repo & preserve current work → (B) launch existing app on phone if environment truly supports it → (C) lock shared design components → (D) implement the animated splash first → (E) Home + navigation → (F) exterior/interior pairs one by one → (G) App Manager exterior+17 → (H) all 20 QA, recording and handoff.

For EACH screen:
1. Locate its existing route/activity/component and its dependencies; state the existing version and whether visual/functional work already exists.
2. Make a minimal, targeted in-place patch. No wholesale directory replacement.
3. Build/lint/tests applicable to that module. If build fails, analyze exact output instead of relabeling it success; fix only causally related defects.
4. Launch the current app (if possible on-phone), capture a screenshot against the reference, evaluate layout/saturation/contrast/typography/spacing/glow/hero/navigation and animations; iterate.
5. Exercise every visible control and back navigation; capture real outcomes and unsupported-cases UX. No screenshot-based "buttons".
6. Continue into the next screen; do not ask me to approve trivial implementation decisions. If tooling/device limitations prevent a step, do all remaining safely actionable work, then report exactly what is blocked.

No irrelevant massive dependency installs. No surprise Play Store publishing, no telemetry-upload or cloud service provision, no privileged security bypass. The objective NOW is finishing visuals and correct UX wiring, **not** pretending every underlying cleaning/privacy/AI service is already Production complete. Leave a precise service-personalization backlog afterwards.

## 8. ACCEPTANCE AND HONEST EVIDENCE — ZERO FAKE CLOSURE

Maintain concise ongoing progress with evidence instead of a huge final claim. Final handoff MUST contain:

- Actual project path and actual execution location (verified phone/remote), current branch/HEAD and working tree change set; note unique work preserved and any unmerged work.
- Exact files altered, reused modules and any new assets, with no duplicate project structure.
- Screenshot mapping for 01–20 and current App Manager exterior, clearly `matched`, `partial`, `not implemented`, or `blocked` per screen. Original PNG is a target, NOT runtime evidence.
- Real splash animation recording plus check whether K was actually drawn, text revealed, smile stroked, click audible and frame-synchronized, safe lifecycle tested, no repeated sound.
- What was really run: commands, success/error exit codes, tests, on-device APK (if built), installed package (if installed), launch proof and actual screenshot(s). Do NOT claim native build merely from browser preview, or claim Android device test from remote shell.
- Functionality status per service: functional / partially integrated / UI only / platform-blocked, and real measurements vs illustrative estimates.
- Regression checks for existing screens/features, navigation, permissions, EN/AR RTL/LTR, theme, scroll and small devices.
- Storage usage before/after if significant build/dependencies used, plus remaining phone space.
- Next stage: exact unimplemented backend/service-personalization requirements once the visual phase is proven.

**PASS is never the default.** Build success alone is not a UI or runtime pass. A visual concept image is not an installed app. A cloud shell is not Termux. If a capability is unavailable, label it unverified/blocked instead of fabricating a completion. No percentages without evidence.

## START NOW

1) Detect runtime and reopen existing OpenCode workspace/session.
2) Verify files/repo, SHA, modified state and current build log without destroying anything.
3) Read the supplied 20-reference kit, inspect existing UI, and map to actual current files.
4) **Continue editing the SAME project ON THE PHONE where actual access exists**, starting with the real K → `knoux ai` → smile → single click splash animation, then exterior/interior visual convergence.
5) Keep working until all safely feasible implementation and validation are completed; no empty promises, no greenfield start, no unnecessary questions, no fake DONE.
