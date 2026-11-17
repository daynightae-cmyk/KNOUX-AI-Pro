# KNOUX AI PRO — CODEX VISUAL BATCH RELAY — IMMEDIATE EXECUTION
Owner instruction: 2026-11-16. Codex is the SUCCESSOR to DeepSeek's existing OpenCode work. Do NOT rebuild the application or repeat completed screens.

## 0 — TARGET & HANDOFF
Canonical Windows lane ONLY: K:\KNOUX-AI-Pro
GitHub: https://github.com/daynightae-cmyk/KNOUX-AI-Pro.git
Branch: master. Previously verified and remotely pushed baseline: a70ffa5322f61a8c0f0dc2d9e5d0dc5c2b24e3a5 (refresh HEAD/origin/master before touching code).
Current working tree may contain active uncommitted work in AndroidManifest.xml, MainActivity.kt, HomeV2.kt, HubScreensV2.kt, MoreScreensV2.kt, CleanDetailScreensV2.kt and unique untracked files. Do not overwrite or stage other agents' work.
Knowledge entry: PROJECT_KNOWLEDGE/README.md
Existing workflow: prompts/KNOUX_AI_PRO_UI_BATCH_FINISHER_EXECUTE_20261116.md
Owner reference ZIP: design/owner_references_20261115/KNOUX_AI_PRO_VISUAL_REFERENCES_DEDUP_20261115.zip
Independent D:\KNOUX-AI and Linux /root/workspace/KNOUX-AI are OUT OF SCOPE.

## 1 — OWNERSHIP: FOLLOW DEEPSEEK, NEVER RACE ITS WRITES
This is a handoff, not permission to edit concurrently on the same files. Check the current Git SHA, git status -uall, live OpenCode writes and most recently modified source. If DeepSeek is still actively editing the SAME files, DO NOT kill it, overwrite it, run git checkout, or merge/reset its work. Continue work in non-overlapping NEW service-specific Kotlin files while waiting for a safe integration point, or focus on references/untouched screens. Do not claim an integration is complete until the conflict is resolved. Once ownership is clear, integrate surgically into the current navigation.
All unique work remains protected, including untracked source/assets, prior APK/QA evidence and active sessions. No reset --hard, git clean, force push, stash apply onto dirty code, deleting user work, or mass stage.

## 2 — THE IMAGES ARE THE VISUAL SPEC
The owner will attach individual reference images and composites DIRECTLY to this Codex conversation. INSPECT THOSE IMAGES FIRST. Images attached to this turn are the top visual reference; follow their image-to-service mapping, not arbitrary marketing guesses. If the attachments haven't appeared, use the owner ZIP and in-project approved assets rather than blocking.
Match the supplied KNOUX AI PRO visual identity as closely as native Android Compose and real device dimensions allow:
- Correct logo, brand proportions, mascot/deer image when approved, hero styling and arrangement.
- Midnight obsidian/purple background, layered mountains, magenta/violet/cyan neon, warm gold PRO strokes, luminous rings, subtle glass cards, styled charts, clear Arabic hierarchy and precise iconography.
- Per-screen hero, actual subservice cards, banner, metrics, CTA, navigation tab and scroll position. Match layout density, spacing, relative geometry and graphic weights.
- Arabic RTL with safe status/gesture insets, keyboard and accessible sizing; do not paint fake Android OS bars, phone bezel or an entire screenshot as one clickable bitmap.
- Restore approved identity, do not replace with generic Material UI. No huge central fingerprint in login. Do not redesign approved Splash/Smile/Onboarding/Founder/Login/OTP/Google/Passkeys/Biometric.
- Existing real Android providers and data/state flows must remain. Reference numbers are DESIGN EXAMPLES ONLY: no fabricated storage reclaimed, health %, threats, speedup or thermal savings. Use truthful measured/unknown/permission-needed data.

## 3 — HIGH THROUGHPUT: 3–4 SCREEN BATCHES
Inventory route names / current composables QUICKLY; these V2 screens already exist: SmartClean, DeepScan, Duplicates, Residue, Privacy, Reports, AppManager, Storage, Battery, AI plus subservice screens. Never duplicate, rewrite, or re-create working screens.

Build/reuse a minimal shared visual finishing layer only if existing components cannot already express it: KnouxServiceShell, ServiceVisualSpec, ProHero, GlassMetricCard, SubServiceTile, GradientCTA, Review/Permission/Empty states. Apply existing style primitives and shared components; isolate service-specific differences in declarative specs. Reuse current navigation and actions; maintain state/review/selection and real OS permission/confirmation handoffs.

Choose the 3–4 screens with the largest visible gap first; implement their visuals AND subservice tiles. Then take the next batch without waiting for permission or writing a long report. Prioritize 10 owner blueprint groups: Smart Clean, Duplicate Finder, App Residue, Privacy, Deep Scan, Reports, App Manager, Storage Analyzer, Battery Saver, AI Optimization. Do not spend cycles polishing an already matching screen when an obviously missing one remains.

## 4 — SCREENSHOT-LED VERIFICATION, NOT ENDLESS TESTING
Do NOT execute lengthy full unit, instrumentation, E2E or emulator test suites for this visual handoff. No repeated root-cause research unless new code cannot compile. Build once per group of 3–4 screens with :app:assembleDebug, fix compile blockers caused by current changes, install/update the APK with adb install -r on the owner's authorized Samsung. Avoid adb uninstall, pm clear, and any data wipe.
Then open EACH updated route with existing debug navigation/ADB, capture ONE genuine PHONE SCREENSHOT for EACH screen, inspect its composition against its owner's reference, fix an obvious serious visual mismatch if necessary and move immediately to the next screen. Prefer batch build/install and per-screen screenshots over repeated builds per image.
If the same route remains blank/faulty after ONE focused triage pass, record BLOCKED with exact route, source and screenshot and move to an INDEPENDENT screen. Do not call it verified; don't trap the entire run in one route.
Screenshot proves only the visual appearance of that running screen, NOT all button behavior, data accuracy, security, permissions, performance or feature completeness. No invented PASS. Preserve Android/device logs locally, but never put device IDs, secrets, private personal screenshots or unredacted logcat in public GitHub.
Use short Arabic updates after batches, not after each tool call.

## 5 — SUCCESS CONDITIONS / GIT
Maximum visual throughput within available session. No planning-only turn. Implement real Kotlin Compose immediately after brief source inspection. Existing services and buttons still need correct route wiring; preserve original tested functionality.
Keep a compact progress matrix with: reference image name | existing Kotlin screen | visual changed | screenshot path | pass/fail visual | pending real-function verification. Build result, SHA and commit/push verification only when actually checked.
Stage only reviewed, specifically owned files; commit/push non-force only after compile and protection against parallel-writer conflicts; otherwise LOCAL_ONLY. No misleading "100% complete".
Speak in Egyptian Arabic; keep code, paths and commands in English.
BEGIN NOW IN K:\KNOUX-AI-Pro. READ OWNER ATTACHMENTS. FINISH THE EXISTING UI, SCREENSHOT EACH RESULT, THEN NEXT.
