# KNOUX AI — SUPREME VISUAL COMPLETION COMMAND
## Existing-product visual convergence · 20 independent visual references · cinematic animated splash · external + internal screens · zero regression · verified handoff to personalization

**MODE: EXECUTE IN THE EXISTING PROJECT — NO GREENFIELD REBUILD.**

**YOUR ROLE:** Principal Android/Product Engineer + Motion Design Director + Senior UI Architect + Design Systems Lead + Accessibility Engineer + QA/Release Auditor. You are responsible for shipping a visually cohesive, working implementation based on the existing codebase, the supplied brand asset, and the TWENTY independent screenshots in `references/`. You must implement code and verify the result, not respond with an abstract plan. The references are visual design targets, **not** evidence that their fictional device statistics have been measured or their actions already work.

**Owner intent (Arabic):** أريد التطبيق الموجود نفسه يتحول إلى مستوى صور المرجعيات في الصقل، قوة الألوان، الانسيابية، الحركة الحقيقية، والهوية. لا تبدأ من الصفر ولا ترمِ الشغل القديم. أهم شيء شاشة الدخول: حرف K الأصلي يتكوّن كأنه يُرسم، ثم اسم `knoux ai` يظهر تدريجيًا، وتُرسم ابتسامة مضيئة تحت الاسم، وتنتهي بلمعة ونقرة صوتية واحدة لطيفة متزامنة تمامًا. بعد ذلك انتقال سينمائي سلس للهوم. بعد إقفال الـ20 تجربة شاشة وحالاتها سننتقل إلى مرحلة تخصيص الخدمات؛ لا تدّع أن التخصيص/backend كامل إذا لم يُنفّذ.

---

## 0 — REQUIRED INPUTS AND PRIORITY

This package contains:
- `references/`: **20 independent, numbered PNGs**, one screen per file. Never combine them into one collage or treat the phone bezel as a UI asset.
- `source-originals/`: the **10 user-provided predecessor screens** and the user's **official circular `knoux ai` logo**; these establish existing identity, existing module inventory, and a regression baseline. These are NOT 11 extra design targets.
- `manifest.json`: precise screen map and related baselines.
- `DESIGN_TOKENS.json`: suggested colors and motion values; adjust carefully to actual accessible contrast and existing tokens.
- `VISUAL_QA_CHECKLIST.md`: acceptance checklist.

**Priority rules:** (1) protect the existing app and unique work; (2) preserve the authentic logo/wordmark; (3) implement the new refs and fluid motion; (4) preserve/restore current functionality and navigation; (5) report Android capability restrictions truthfully. If the first image and an existing working function conflict, preserve the function while upgrading its appearance. Do not distort the K, replace the brand with a generic logo, or copy/mock the surrounding phone frame into the actual app. The original logo source is a **raster reference**, not permission to invent a different K vector. If no approved vector exists, trace or reconstruct faithfully and show a side-by-side visual comparison.

### 20 visual targets — implementation map

| ID | Reference file | Surface | Required behavior/relationship |
|---|---|---|---|
| 01 | `01_Splash_Logo_Smile.png` | Cinematic branded reveal | Exact K stroke build → `knoux ai` progressive wordmark → smile stroke → one end click → Home handoff |
| 02 | `02_Home_Dashboard.png` | Home | Powerful hero orb, REAL scan CTA, device summary, six service cards, bottom navigation |
| 03 | `03_Privacy_Sweep.png` | Privacy overview | Privacy analysis, permission categories, risk review; opens `13` |
| 04 | `04_Duplicate_Files_Finder.png` | Duplicate overview | Groups, types, recoverable estimates, review CTA; opens `12` |
| 05 | `05_Deep_Scan.png` | Scan in progress | Real lifecycle, five module status panels, real progress/cancel/pause semantics; opens `14` after completion |
| 06 | `06_Reports_Insights.png` | Reports overview | Summary/history/charts sourced from actual data; opens `15` |
| 07 | `07_App_Residue_Cleanup.png` | Residue overview | Candidate inventory, individually reviewed cleanable data; opens `16` |
| 08 | `08_Storage_Analyzer.png` | Storage overview | Accurate accessible storage data + category drill-down; opens `18` |
| 09 | `09_Battery_Saver.png` | Battery overview | Measurable signals, recommendations, OS limitations; opens `19` |
| 10 | `10_AI_Optimization.png` | AI optimization overview | Measurable recommendations + supported actions; opens `20` |
| 11 | `11_Smart_Clean_Center.png` | Clean detailed view | Category selection, preview, confirmation, measurable before/after; links to applicable screens |
| 12 | `12_Duplicate_Group_Review.png` | Duplicate detail | Real thumbnails, keep-one original safety invariant, selection, preview, confirm |
| 13 | `13_Privacy_Risk_Inspector.png` | Privacy detail | Detailed per-app permissions/possible risks, OS-setting handoff; NO fake universal tracker blocking |
| 14 | `14_Deep_Scan_Summary.png` | Scan summary | Actual findings, timeline, supported next actions, no fabricated completion numbers |
| 15 | `15_Performance_Report_Detail.png` | Report detail | Charts/history/exports from real records, empty-state before records exist |
| 16 | `16_Residue_Item_Review.png` | Residue detail | Actual accessible URIs, preview + exclusion + scoped deletion + audit result |
| 17 | `17_Installed_Apps_Review.png` | App Manager detail | Installed app list/group, supported usage/permissions, OS handoffs; preserve user-installed apps |
| 18 | `18_Storage_Breakdown.png` | Storage detail | Category sizes, selectable large files, properly derived totals, drill-down |
| 19 | `19_Battery_Diagnostics.png` | Battery detail | Battery state, usage signals where permitted, estimated/unknown labeled honestly |
| 20 | `20_AI_Recommendations.png` | Recommendation detail | Evidence-backed plan, customizable approved actions, estimated vs verified outcomes |

**Special case: App Manager external overview.** The user supplied `source-originals/07_App_Manager.png` as the overview baseline, and `references/17_Installed_Apps_Review.png` as the polished internal detail. Do **not** omit App Manager or invent a twenty-first required polished image. Polish the existing App Manager overview using the shared design system from refs 02–10, preserve its content, and connect it to ref 17.

**Traceable page routing:** all 20 referenced surfaces plus the existing App Manager overview must have discoverable entry points, correct back behavior, no dead cards, no broken nav, state continuity, scroll retention where appropriate, and honest loading/empty/error/permission-denied UI. If an existing user-approved module is outside this screenshot set, KEEP IT. This package is not permission to reduce the application's feature inventory.

---

## 1 — REALITY FIRST / UNIQUE WORK / ZERO-DESTRUCTIVE PREFLIGHT

Before editing, inspect and record facts, using actual tools available:

1. Determine repo root and native/web technology from the actual project; print canonical project path, framework, build system, app module, package/applicationId, min/target/compile SDK, current assets/fonts, navigation design, state management, storage APIs, and current splash implementation. **Do not assume Kotlin Compose, React, Expo, Flutter, or WebView: choose the existing stack.** Do not create a new app on `D:` or elsewhere as a substitute for the live repo.
2. Run `git status --short`, `git branch -vv`, `git rev-parse HEAD`, compare local `main` with `origin/main`, inspect `git worktree list`, `git stash list`, untracked assets, recent commits, PRs/CI where authorized. Check current agents and active worktrees before editing shared paths. Do not overwrite, reset, delete, clean, force-push, cherry-pick blindly, rebase destructively, or disrupt an active agent.
3. Inventory existing screens, routes, visuals, animations, icons, assets, UI tests, saved states, and functional services. Identify work already done, work not yet merged, and anything unique. Reuse what exists rather than recreating components or importing an entirely new template.
4. Take actual before screenshots/video for the existing splash, Home, all available overviews and detail screens. Save a baseline SHA and a file list; preserve approved visuals that the new reference does not replace.
5. Work in the current safe integrated branch/main according to repository state and owner's policy (no unnecessary new branch). If concurrent changes make an edit unsafe, isolate the affected files and continue with independently safe tasks; state the blocker accurately, rather than performing a destructive git operation.
6. Launch the existing app as soon as feasible. If native Android, use the installed emulator/device and record `adb` target + build variant; if the existing app is web-based, run its actual local dev server and browser preview. **Do not report a browser preview for a native app unless it truly exists.** Maintain a watchable runtime during implementation where feasible.

**Deliver a brief BEFORE evidence report and then IMPLEMENT.** Do not stop at audit/planning, do not ask repetitive questions, do not create empty project scaffolds, and do not start from scratch. Preserve all unique work and keep commit history intact.

---

## 2 — BRAND LOCK / EXTERIOR-INTERIOR DESIGN SYSTEM

Brand mark: the user's original stylized gradient **K**; lowercase wordmark spelled exactly **`knoux ai`**. Preserve the precise proportions, diagonal crossing ribbon, top-left magenta wedge, cyan lower segment, gradients, silhouette and baseline. The circle in the standalone logo image is a presentation/background shape, **not** a compulsory solid circle around the K when the K appears in the interface. Use K in hero orbs if reference calls for it, but do not change the mark.

Visual goal: **more powerful and sophisticated than the predecessor screenshots**, not merely brighter. Establish one reusable system:
- Background `#080512` / `#0B0719` / `#120826` with deep indigo atmosphere; avoid dirty gray, flat generic violet, color banding, washed-out haze, and muddy panels.
- Primary vivid violet `#843BFF` to luminous purple `#B047FF`; restrained fuchsia/magenta `#E34CFF`; energetic cyan `#00DCFF` / `#21B9FF`; soft white `#F8F6FF`; secondary gray-lavender `#B9B0D0`. These are **starting points**, tune with screenshot comparisons, not rigidly force every card into neon.
- Use luminous precision: strong hero and CTA glows, restrained body text and rows, hairline spectral borders, subtle glass reflections, layered cards (background + gradient stroke + top sheen + cast shadow). **Text must remain perfectly readable on dark surfaces.**
- Section-specific icon tint shifts, but one shared brand hue family, consistent radii/strokes/padding/icon sizes/typography.
- Keep authentic 3D-ish holographic heroes (orb, radar, shield, media stack, battery, storage cylinder, AI chip) with concentric arcs and particle waves. Hero is the visual anchor; information is the functional anchor. Do not let decorations obscure content or block touches.
- Distinguish hierarchy: Hero title → most important status/CTA → summary metrics → actionable list or grid → secondary action → navigation. Avoid excessive glow around every element, huge empty gaps, or illegibly dense 3-column text.
- Honor insets, system status/nav bars, foldable/tablet sizes, text scaling, landscape where supported, and small screens. The 9:16 phone border in references is a **mockup**, NOT app chrome. No fake camera cutout or static Android status/time/battery drawn into the app.
- Use real local icon assets or consistent vector icons; no emoji substituted for an approved icon. Reuse existing fonts/licenses. Build a centralized theme/tokens layer and reusable components, without needlessly rewriting the app.
- All screen labels and notifications should integrate into existing EN/AR localization, accurate RTL in Arabic and LTR for technical identifiers, with no clipped content. Keep the user’s current active language and settings. Support accessibility labels, screen readers, minimum touch target ~48dp, focus order, and color contrast, plus reduced-motion settings.
- Dark must match screenshots. If the app already supports Light/Day Mode, keep it functional and polished; do not remove existing themes in the name of fidelity.

**Use screenshot-grounded tokens, not random gradients.** Each surface MUST be recognizable side-by-side with its named reference: positions, silhouette, hierarchy, hero, visual weights, iconography, transitions, CTA style, list/grid organization, and bottom navigation. Do not replace a detailed screen with generic cards and claim equivalence.

---

## 3 — SPLASH SCREEN: EXACT MOVING STORYBOARD (HIGHEST PRIORITY)

Visual reference: `references/01_Splash_Logo_Smile.png` and the official logo in `source-originals/11_Official_Knoux_AI_Logo.png`.

**The still image expresses the final frame; the animation must be IMPLEMENTED as time-based renderable geometry with actual timing and sound. A static PNG with fade-in is not acceptable.** Build the identity reveal as a short cinematic animation, repeatable for testing, but not pointlessly replayed on every resume.

### Target storyboard: 2.7–3.3 seconds, measured, adjustable

- `t=0–180ms`: dark black-indigo stage, a controlled spectral light bloom begins at the central logo origin. No flash of blank white, no placeholder logo or duplicated splash.
- `t=180–600ms`: a bright writing tip **constructs the top-left magenta wedge of the original K**. The wedge appears through a vector path mask / shape trim with luminous edge and a fading particle tail; no incorrect handwritten Latin `K` or arbitrary K shape.
- `t=520–1120ms`: the sweeping diagonal ribbon grows along the **authentic K geometry** from magenta/violet toward blue; the lower cyan right leg resolves. Add a subtle energetic intersection flare, not a large explosion.
- `t=1030–1680ms`: the lowercase wordmark **`knoux ai`** resolves progressively from left to right with a luminous ink/write-on or path-mask reveal. “knoux” is crisp white, “ai” is rich pink-purple. Exact spelling and kerning, no text morph or illegible retype effect.
- `t=1600–2420ms`: beneath the wordmark, the **SMILE IS DRAWN**, not simply faded: a clean elegant U-shaped shallow smile curve grows naturally left→right, magenta→violet→cyan, precisely placed UNDER the text with an arc gentle enough to look like a warm smile, not a giant circle/ring. The smile forms the brand signature.
- `t=2370–2550ms`: the moving tip reaches the far-right end of the smile and creates a tiny **one-time luminous star/pinpoint**; at that exact final-contact frame play **ONE delicate, premium click/tick SFX** (short 35–100ms transient), spatially/subtly mixed. No beep, no artificial voice, no repetitive loop. Visually a tiny micro-pulse echoes the click.
- `t=2550–3100ms`: settle the logo as a perfectly crisp branded lockup; subtle background particle drift, then a fluid **shared-element or mask/reveal transition** into the actual Home hero/orb. No abrupt screen cut, black flicker, opacity jump, extra logo bounce, or second click.

**Sound behavior:** Prefer the established app audio stack; otherwise a packaged tiny original audio asset or properly synthesized one-shot with predictable latency. Preload, release properly, honor device mute/silent mode, Do Not Disturb, sound settings and accessibility preference. Default must respect platform audio expectations. Sound plays only after real in-app cinematic reveal when enabled, only once per reveal; never on every background resume or a normal app redraw. Test audio-video frame synchronization with an actual screen recording and audio capture if available; disclose limitations of emulator audio recording.

**Platform-correct startup:** On Android 12+ respect the platform system SplashScreen requirement; use the supported splash icon/exit transition, then the branded immersive reveal IN the existing app UI (Compose/View/Flutter/React Native/etc. according to inspected stack). Avoid blocking real application startup or artificially holding a blank system splash. On lower API levels, use the compatibility library where applicable. Preserve deep-link behavior, cold/warm/hot startup, authentication state, and skip/reduced-motion handling. The optional full brand show should not play for every navigation back to Home. For accessibility reduced-motion mode, use a short clean logo reveal or static lockup with the intended destination accessible immediately.

**No fake implementation:** merely adding animation JSON, a static GIF or screenshot, changing one CSS `opacity`, or printing `SPLASH COMPLETE` does not satisfy. Must show the actual K visibly drawn, letters revealed, smile stroke written, click at tip, and genuine transition in a recorded runtime. If icon geometry cannot be faithfully vectorized from existing assets, first inspect approved logo vectors/artwork and implement a proportional mask instead of inventing one.

---

## 4 — MOTION LANGUAGE: EVERY SCREEN IS ALIVE, NOT CHAOTIC

Implement a shared motion system in the existing app stack, with corresponding duration/easing tokens:

- **Page entry:** staggered hero → score/card → tool tiles with subtle 12–24dp translations and fades; never make content inaccessible during load.
- **Hero motifs:** restrained 3D-like levitation, orbiting ring segments, low-amplitude pulsing radial illumination, drifting particles (if GPU budget supports). Different hero per module. NO constant rapid spin or sensory overload; use display-lifecycle aware animation and pause off-screen.
- **Touch feedback:** press depth/scale ~0.97–0.99, brighter rim highlight, tactile haptic only where appropriate, then return with spring. Every button/card/checkbox/toggle should have meaningful feedback and correct disabled/loading/processing states.
- **Navigation:** shared hero/CTA relationships, soft crossfade + slight axial translation, appropriate back animations, consistent bottom-nav transitions. Maintain information context; don’t reset unfinished review selections without reason.
- **Actual scan:** progress tied to real scan worker/module progress; radar sweep moves while running, submodule bars reflect actual state, pause only if backend supports it, cancellation aborts cooperatively, stage changes smoothly but percentages must NEVER be fabricated by a timer. If progress granularity unavailable, use honest indeterminate state.
- **Selections and results:** row/counter transitions, check glows, safe action confirmation, success animation only after API/OS/filesystem confirms operation; error and partial-success also animated and described truthfully.
- **Dashboard scores and reports:** animate numeric changes only between verifiable values; never count up to hardcoded 92%, 12.6 GB, +18% or 1,248. Use skeleton or `No scan yet`, `Not available on this Android version`, `Permission required`, `Estimated`, as appropriate.
- **Transitions & performance:** use compositor-friendly transforms/opacity, cache static 3D heroes where appropriate, avoid giant blurred surfaces over scrolling lists, prefer hardware accelerated vector/Canvas/Compose animation APIs according to stack. Respect battery saver, lifecycle pauses, low-power devices, reduced-motion. Aim for fluid 60fps and smooth higher-refresh experiences, measuring dropped frames or jank on test hardware.

**Motion QA evidence:** capture a playable splash video; at least one complete walkthrough video covering Home → Clean/Tools/Reports → detailed screen → action preview → back navigation; also capture frame comparisons for loading/active/completed/empty/error, including reduced motion. Do not pass because the first still frame resembles the reference.

---

## 5 — FUNCTIONAL FLOW IN EVERY EXTERNAL AND INTERNAL REFERENCE

### Home (02) → Core modules
Hero orb must breathe/rotate gently and respond to the user's deliberate scan action. All six visible Home service cards open their actual module overview (not just a modal saying coming soon). Card state reflects real records or a clearly labeled sample/demo mode. The App Manager and Report flows remain accessible through actual navigation, even though their placement differs. Menu, settings, Pro badge behavior, back state, search, and app identity must remain coherent with the existing app. Never make decorative controls inert.

### Clean / Scan (05, 11, 14)
- Visible scan states: never-run, preparing, scanning, paused-if-supported, interrupted, failed, completed, permissions needed, and no findings.
- Check actual scan coverage per Android API + permissions, with module-specific supported/unsupported labels.
- Category checkboxes re-total from underlying selected items; previews list content/file size/path/type correctly; remove only items with scoped user consent and safe rights; result includes exact reclaimed bytes measured after deletion.
- For Deep Scan, data acquisition stages and progress are real; completion report links to individual module results; no fake “safe to fix all” claims for system settings actions.

### Duplicate Finder (04, 12)
- Exact duplicates: robust hash plus file-identity and size checks as appropriate. Similar photos are **similar, not guaranteed duplicates**; label separately with manual review.
- A group must keep a protected canonical original; best-copy suggestion should consider quality/resolution/metadata, not silently delete the first or last file. Never remove the only surviving copy. Include confirm dialog with item paths/URIs, exact sizes, what is preserved, and restore/trash option if supported by MediaStore.
- Handle Android photo permissions, MediaStore `content://` URIs, user-selected photos, inaccessible folders, revoked permissions, and partial failures. Preview thumbnails use actual accessible media and performant caching.

### Privacy (03, 13)
- Show real permissions, permission groups, app visibility, permission manager links, sensor indicators where supported, and clearly sourced risk scoring methodology if a score is used.
- Do NOT invent systemwide blocking of third-party SDK trackers, clipboard reading histories, microphone/camera telemetry, or pretend a regular Play-distributed app can toggle other apps' permissions. A `Block` switch only exists if backed by an actual supported capability; otherwise present `Review`, `Open app settings`, or an explicitly advisory option. Explain user-consent boundaries.
- A permission grant is not automatically proof of malicious tracking. Differentiate detected SDK by actual analysis evidence vs inferred risk/permission flags.

### App Residue (07, 16)
- Only inventory files/directories actually accessible and attributable with evidence. Shared WhatsApp media is **not** automatically orphaned just because a path looks old. Never mark another app's private `/Android/data/` cleanable when Android forbids access.
- Show real URI, owner/source if known, last modified if available, size, candidate confidence, and exact safety reason. Exclude protected and unknown items by default. Do not make `Select All` override safety gates. Confirm every sensitive deletion and display unsupported OS access gracefully.

### Storage Analyzer (08, 18)
- Show correct device storage totals, used/free values and category definitions; reconcile subtotals with overall and label unclassified/unknown. Differentiate total device storage vs accessible user-selected files.
- Drill to real folders/files only where permission permits. Never pretend to scan inaccessible partitions; loading/empty/permission-denied states must be polished.

### App Manager (original overview + 17)
- Preserve installed apps/groups, filters, usage/sort/search, app details, installed version/update availability if actually sourced, clear navigation to OS App Info/Uninstall screens, per-app data where available.
- `Uninstall` must invoke Android's standard user-confirmed uninstall flow; no silent removal. Package visibility and UsageStats require legitimate APIs, user action and Play-compliant scopes. “Startup control,” hibernation and third-party updates cannot imply unsupported device-owner privileges. Respect existing capabilities without pretending to have elevated access.

### Battery (09, 19)
- Use Android-supported battery information, BatteryManager/usage APIs where appropriate, battery saver state and user-visible OS settings handoff. Battery *health*, charging-cycle count, and per-app battery drain may be unavailable on many devices; show `Unavailable` rather than invent it.
- No `+18% battery`, `-6°C`, `6h42m gain`, or `reduce CPU load by X%` unless calculated from a documented measurement/estimate and labeled accordingly. Do not claim control of charging circuitry, thermal policy, background apps, or OEM modes if not authorized.

### AI Optimization (10, 20)
- Clearly distinguish factual measurements, heuristic recommendations, estimated outcomes, and applied actions. Every recommendation needs source, impact explanation, capability requirements, risk class, selected state, and an exact supported action or settings handoff.
- Do not claim “applied successfully” merely because a button was clicked. Show only the supported effects. Do not blanket-kill applications or promote aggressive memory boosters that harm battery/user experience. Be careful with confidential device data, and do not silently upload it to AI services.

### Reports (06, 15)
- Reports derived from app-stored real events and before/after measurements; persist dates, module, action, result and bytes affected if appropriate. Graphs use consistent units, derived values and empty states. Real PDF export/sharing if already supported or implement using existing safe libraries and Android `FileProvider`/share intent. Ensure generated reports do not leak sensitive file paths without user choice.

### Every action
The minimum flow is **discover → detail → inspect/preview → select/configure → explain consequences → user confirmation where needed → execute permitted operation → verify actual effect → show result/report or honest failure → persist state**. Never take a destructive step with only aesthetic checkbox selection. Never label a designed mock as implemented function.

---

## 6 — ANDROID / GOOGLE PLAY REALITY (CHECK THE PROJECT'S TARGET API)

Keep behavior compatible with the actual minSdk and target SDK observed in the repo; do not lower the SDK to bypass restrictions. Use API-/permission-gated service adapters and display capability-aware UX.

- Use **MediaStore** for appropriate media operations, **Storage Access Framework** for user-selected documents/trees, app-specific storage for private caches. Never assume broad filesystem enumeration is available. `MANAGE_EXTERNAL_STORAGE` is sensitive and Play-restricted; do not add it unless the app genuinely qualifies, core use is documented, Play policy evaluated, and the product owner approves the declaration. It does **not** give access to other apps' private `Android/data` folders.
- Protect `QUERY_ALL_PACKAGES`, accessibility service, VPN service, device admin, usage stats, app ops, notification listeners and other special permissions behind real functionality, least-privilege, accurate declarations, and user-consented settings flows; do not add them to make a mock button appear functional.
- Android 12+ launches with a platform splash; use proper startup transition + branded in-app sequence, not a prohibited/slow double splash or a background full-screen takeover.
- Background work follows system lifecycle and energy restrictions, scoped background workers/foreground service types when truly needed, and user notification requirements.
- Treat updated official Android docs and Google Play policies as authoritative. Links for implementation review:
  - https://developer.android.com/develop/ui/views/launch/splash-screen
  - https://developer.android.com/training/data-storage
  - https://developer.android.com/training/data-storage/shared/documents-files
  - https://developer.android.com/training/data-storage/manage-all-files
  - https://support.google.com/googleplay/android-developer/answer/10467955?hl=en

**Visual work must not silently mutate existing Android manifest permissions, app signing, database migrations, release keys, backend endpoints, analytics consent or billing configuration.** Any such change needs documented justification, safety review, and tests.

---

## 7 — IMPLEMENTATION ORDER (KEEP APP RUNNING)

**PHASE A — Inspect and freeze design:** inspect actual sources and record SHA, inventory routes and assets, screenshot existing runtime, derive reusable tokens/gradients/typography, plan how 20 named refs map onto current screens. No new project.

**PHASE B — Splash identity scene first:** faithfully construct K, wordmark, smile stroke, single synchronized click, transition. Record video + audio behavior and verify cold/warm/deep-link/reduced-motion cases. Stop spending time on stock icon replacements; the approved original K is non-negotiable.

**PHASE C — Shared shell:** top app bar, card surfaces, badges, rings, CTA, bottom tabs, typography, responsive insets, shared motion primitives, localization, composable navigation. Keep old business logic and data connections.

**PHASE D — External overview passes:** 02 Home, 03 Privacy, 04 Duplicates, 05 Deep Scan, 06 Reports, 07 Residue, original App Manager overview, 08 Storage, 09 Battery, 10 AI Optimization. For each screen, implement reference layout AND meaningful navigation AND animation before marking visually ready.

**PHASE E — Internal details:** 11–20, linked from their corresponding overviews. Use real data adapters and safe placeholders for unavailable values. Complete each round trip and test before moving to next screen.

**PHASE F — Visual finesse:** screenshot-by-screenshot compare, fix exact alignment/spacing/hover/press/glow/type, animated state transitions, light/RTL/responsive/accessibility, reduced motion, loading/error/empty states, optimization for GPU/CPU/battery. Make it beautiful without losing readability.

**PHASE G — Verification / integrate:** run configured formatter/lint/typecheck/unit/UI tests/build and Android runtime checks. Preserve current main, selectively integrate only verified unique edits, no force operations. Create commits with actual code changes only when safe; distinguish committed vs pushed vs merged vs deployed.

You may run independent safe tasks in parallel if tools/agents support that; do not assign two writers to the same files without ownership. Do not overwrite current visual-agent work. Work in coherent reviewable vertical slices, not a massive blind rewrite.

---

## 8 — VISUAL & MOTION ACCEPTANCE GATES — NO FAKE CLOSURE

A screen is not COMPLETE merely because it compiles or a generated picture looks nice. Require evidence for every item:

1. **SCREEN REALITY:** exists in the existing actual app, navigable via expected entry point; original business state preserved; authentic logo.
2. **VISUAL FIDELITY:** after screenshot at fixed logical device size compared to named reference, same major hierarchy and distinct hero, with obvious stronger gradients, crisp text, controlled glow. Reference text/stat values are not hardcoded into runtime. Attach side-by-side or automated image diff plus manual findings.
3. **MOTION REALITY:** recorded entry, hero lifecycle, scroll, CTA press, state transition and back. No jerky animation, blank flash, offscreen bleed, or ghost screen. The splash clip visibly contains all K/stroke/smile/click phases and has frame timestamps.
4. **ACTION REALITY:** all visible actionable controls either do a supported thing, route to a legitimate detail/settings step, or are explicitly disabled with explanation. Real spinners/progress and user-visible failures; no fake success.
5. **DATA REALITY:** values come from actual API/storage/history or carry visible `Sample/Estimated/Unavailable` metadata. Counters reconcile; reports don't display made-up history.
6. **SAFETY:** file operations default to preview and confirm, duplicates preserve a canonical original, permission-denied never crashes, protected Android paths remain untouched, app state persists across lifecycle changes.
7. **RESPONSIVE & A11Y:** small and large phones, at least one tablet/landscape or responsive breakpoint where supported, Arabic RTL and English LTR, 100% and enlarged font sizes, screen reader labels and tap target minimums, reduced-motion/no-sound behavior, adequate contrast.
8. **PERFORMANCE:** real app launch and scroll on emulator/physical device, animations do not create excessive jank or hot CPU when idle, cancellation/lifecycle safe, no ANR or repeated log exceptions.
9. **BUILD / TEST / GIT:** record exact commands + pass/fail counts, app install/runtime recording, current SHA, `git status`, local/remote main relation, CI checks, and deployment status if applicable. Do not equate unit PASS with screenshot PASS or app release.
10. **ASSET INTEGRITY:** each of the 20 PNGs remains independent and traceable. Use images as references, not as a full-screen screenshot pasted behind invisible hit boxes. UI must be native/rendered components with real touch and adaptive layout.

### Mandatory evidence layout inside existing repo (non-destructive)

Use a suitable existing evidence/docs location or a scoped folder such as:

`qa-evidence/knoux-ai-visual-finish/`

Produce:
- `BASELINE.md`: actual project paths/framework/HEAD/branch/active worktrees, current state and preserved unique work.
- `SCREEN_MAP.md`: screen routes vs reference ID, changed components, implementation state, data source/action/state.
- `VISUAL_COMPARISONS/`: before/reference/after pairs, include Android device density/resolution/API.
- `SPLASH_STORYBOARD.md`: K paths, timing graph, click trigger frame, mute behavior, observed recording and device.
- `videos/`: actual runtime splash and end-to-end navigation captures (where tooling permits).
- `TEST_RESULTS.md`: commands, outputs, screenshot QA, emulator/device, build, lint, unit/UI test failures and fixes.
- `HANDOFF_TO_PERSONALIZATION.md`: remaining REAL customization gaps, system/OS-limited capabilities, data services, design decisions, owner inputs truly needed, and prioritized safe next actions.

### Required final per-screen report

For each reference `01`–`20`, return **Implemented & verified / Implemented but not verified / Partial / Not implemented / Blocked**; never claim 100% if a visible interaction is not wired. Include named changed files and objective evidence (actual screenshots/videos and, for code, SHA). Distinguish source/local commit/push/main merge/CI/runtime. If time runs short, deliver the actual finished slices and a precise remaining delta, not an invented finish.

---

## 9 — FINAL PRODUCT FEEL / IMPORTANT NEGATIVE INSTRUCTIONS

The goal is a **living, cinematic, dependable product**, not a stack of flashy posters. It must feel like one designed experience from startup to Home to a module to a deep detail to results to back navigation. The user must think: *the entrance writes the brand, smiles, clicks, enters the orb; the interface flows in the same material and light language, and every function responds truthfully.*

**FORBIDDEN:**
- Starting over, creating a new unrelated project/repo, wholesale app shell replacement, deleting unique files or branches/worktrees, unapproved force-push/reset/clean.
- Handing back a plan-only report instead of code; fake completion; fake backend success; dummy numbers copied from artwork; inert buttons; visually disguising unavailable Android privileges.
- Treating screenshot PNG as the final native screen; static splash substituted for animated stroke; smile fading in without being drawn; click playing at the wrong frame, repeatedly, or regardless of silent settings.
- “Performance boost” theater, invented tracker blocks, unsafe batch file deletion, or permissions added solely to create a magical UI claim.
- Ignoring the App Manager overview because only its detail has a polished new mockup; silently dropping old modules, approved sections or localization.
- Claiming release readiness based on build success alone, or deleting old work immediately upon a merge without inspecting unique content and obtaining explicit authorization.

**BEGIN NOW WITH EXISTING REPOSITORY FACTS, THEN IMPLEMENT. DO NOT ASK QUESTIONS THAT CAN BE RESOLVED FROM THE REPO OR THE PROVIDED IMAGES. KEEP THE RUNNING PRODUCT SAFE. FINISH THE COMPLETE VISUAL AND ANIMATION LAYER WITH EVIDENCE, THEN HAND OFF THE GENUINE REMAINING PERSONALIZATION/INTEGRATIONS.**
