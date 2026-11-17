# KNOUX AI PRO — OWNER'S SINGLE-FILE FIGMA-FAITHFUL ANDROID EXECUTION CONTRACT
**Owner-authorized agent handoff | 2026-11-14 | Mobile-developed application | Independent repository**

> **AGENT: READ THIS ENTIRE SINGLE DOCUMENT, THEN IMPLEMENT THE ACTUAL ANDROID CODE.** This is not a prompt to make ten pictures, generate a Web UI, rewrite history, or redesign approved onboarding/auth screens. It is the native Android implementation contract for **KNOUX AI Pro**, with Figma layout geometry preserved and a DISTINCT Pro color system. No additional prompt/config/spec file is required to understand the requested design work. Consult existing project source, owner assets, manifests and real Figma only as evidence, not as prerequisites to ignore execution.

## 0. THE TWO NAMED MODELS / [REDACTED_DEVICE_SERIAL] — NEVER CONFUSE THEM

| Name the owner will use | Exact GitHub repository | Canonical working lane | Your authority |
|---|---|---|---|
| **KNOUX AI** | `https://github.com/daynightae-cmyk/KNOUX-AI.git` | **Windows-developed Android** project, canonical checkout `D:\KNOUX-AI` | **NOT the target** of this task. Do not clone or push its code into Pro. |
| **KNOUX AI Pro** | `https://github.com/daynightae-cmyk/KNOUX-AI-Pro.git` | **Mobile-developed Android** code originally worked on in OpenCode/AndCode on the real phone | **ONLY target** of this task. No permanent source checkout path has been owner-designated; discover the actual existing source. |

- Both are Android products; **"computer model" means the Android project developed from the PC**, not a Windows desktop executable.
- One company (`KNOUX`) and product family, different Git histories, unique implementations, QA evidence, APK signing and design variants. They may use the same approved Supabase backend but do not automatically share local databases or source.
- **As verified via GitHub on 2026-11-14, `KNOUX-AI-Pro` exists, is private, and GitHub says its Git repository is EMPTY (size 0; commit API HTTP 409).** A metadata `default_branch=main` does NOT prove any commit exists. Recheck live remote before work; a later agent may have pushed. Do not make up a SHA or clone success from the historical snapshot.
- Historical mobile source was in `/root/workspace/KNOUX-AI` under a prior Android OpenCode container; after app reinstall this path may be empty or inaccessible. The owner has extracted large TAR/ZIP recovery material into Android Downloads. A file with a similar name, or an APK, does not prove source history. **Recover and inventory source before implementation; do not silently substitute the PC GitHub repository.**
- The old project handoff reported one mobile source commit `64aeb002b1ab5d1da42ac7f0787bbd2318192a4b` on `master` and M09 implementation in progress, with M01–M08 device verification gaps. This is a **historical claim, not the current verified source**. Keep all unique work including untracked/uncommitted artifacts, Room migrations, media assets, approved motion/audio, auth code and QA.
- Work may happen in AndCode, Windows terminal, or another authorized runtime, but **the requested target remains `KNOUX-AI-Pro`**, not the PC repository. Work environment is not repository identity.

### Why we're implementing this
The owner dislikes excessive purple neon, generic glowing web cards, interlocking arrows, giant ornamental hero art, and an app that feels like a website. He supplied five smart-home examples showing **tactile native mobile proportions, organized service cards, a usable gauge, elegant charts, genuine device context, clean spacing and functional bottom navigation**. Reproduce their visual **anatomy and rhythm** with KNOUX maintenance/privacy/AI functions. Do not copy smart-home devices or their imaginary figures. Pro must be visually distinct from the standard KNOUX AI line.

## 1. ONLY THE PRO DESIGN IDENTITY — “ARCTIC DEPTH / FROSTLINE”

**Choose the following committed creative direction:** a highly finished **deep midnight/navy Android app with icy blue control accents**, plus a carefully designed **bright pearl-white Day Mode**. Not monochrome black, not a loud neon cyan app, not saturated purple. The original KNOUX logo is preserved in its exact proportions and original purple/cyan artwork; it is a brand mark, NOT a mandate to paint every screen purple.

**Distinctive Pro signature:** a precise, very thin **Frostline** blue edge or single highlight on the selected instrument/card, 1–2dp, and a delicate *swept waterline* reflection inside ONE purposeful hero instrument. The highlight follows data state or touch, not an autonomous light show. Use two-dimensional Canvas + subtle elevation to convey surface, rather than fake HUD sci-fi. No 3D renderer dependency.

**Native material palette — Dark (default):**

| Role | Hex | Usage |
|---|---|---|
| Background / Deep Ink | `#071321` | Main screen background |
| Main Surface / Navy | `#10243A` | Standard tactile cards |
| Elevated Surface | `#18334E` | Selected controls and primary summary |
| Fine Divider | `#2B4963` | 1dp separators; sparingly |
| Primary Text | `#F0F6FC` | Titles and values |
| Secondary Text | `#A9BCD0` | Descriptions, timestamps |
| Pro Blue | `#398BEA` | Primary action, selected tab |
| Arctic Cyan | `#83D4F1` | Measurement/selected gauge tick (small area) |
| Frozen White | `#DDEBF6` | Tiny highlights, outlined figures |
| Confirmed / Safe | `#49B5A9` | Real safe state, not decoration |
| Warning | `#E5AD5D` | Real user warning |
| Error | `#E77E87` | Real failure only |

**Native material palette — Light / Day (not an inverted screenshot):**

| Role | Hex | Usage |
|---|---|---|
| Background | `#F3F7FB` | Page canvas |
| Main Surface | `#FFFFFF` | Cards and sheets |
| Raised Surface | `#E8F1F8` | Secondary summary and selected tint |
| Fine Divider | `#D7E3EF` | Subtle separation |
| Primary Text | `#152D43` | Strong high-contrast copy |
| Secondary Text | `#607A90` | Details |
| Primary Action | `#205EAC` | Solid native CTAs |
| Instrument Accent | `#158DC5` | Small data-only highlights |
| Confirmed / Safe | `#087F73` | Accessible positive state |
| Warning | `#A16C1E` | Accessible warning |
| Error | `#B54552` | Accessible error |

- Main dark mode uses **blue only where something can be acted upon or actually measured**. Do not put blue behind every tile. No hardcoded infinite lens flares, exploding particles, glowing full-screen gradients, aura rings, dancing logo, crossing rays or fake sci-fi notation.
- Dark backgrounds mostly matte, cards soft and subtly elevated; one strong primary value per screen. A gentle 1dp rim is acceptable; a full-blown luminous border is not. Light surfaces should look premium and tactile, not washed-out/flat.
- **KNOUX AI Pro identity is distinctive without inventing or changing the official logo.** Use compact official logo in a native toolbar; add a small `PRO` wordmark only when it does not imply paid subscription for a user without entitlement. If Pro is not active for the account, label the product family/feature availability truthfully.
- Typography: high-quality Android sans using available fonts; Arabic script should be legible, not thin gray; respect system font scale. Tab labels and titles should never clip. Use consistent digits and human-readable data units.
- **Dark and Light Modes share exact screen geometry and component API.** Day mode is equally polished, not a separate redesign. Verify contrast (WCAG AA where feasible) for all control/label states, including pressed, disabled and error.
- Motion: 120–220ms state transitions when needed, a restrained scan sweep only during a real scan, no perpetual flashy movement. Honor animator duration scale/reduced motion, battery/thermal state and Android performance.
- The approved **Splash smile, onboarding chapters, approved founder signature, offline founder audio once after actual smile completion, Login, OTP, Google, Passkeys, Biometric and Identity Success** are protected from visual redesign unless a concrete defect is proven and the owner authorizes that specific change. No huge fingerprint. Preserve exact approved art and logo transparency.

## 2. THE FIGMA REFERENCE IS THE SHELL GEOMETRY AUTHORITY

**Owner's Figma source**: `https://www.figma.com/design/MLUbH2QkR1tOlj0qzk2O4e/Design-a-futuristic-smart-home-app-UI-with-Figma--Community-?node-id=2010-6`

**File key:** `MLUbH2QkR1tOlj0qzk2O4e`.

**Original three Smart Home frames (visual hierarchy, spacing, instruments and proportions):**

| Node | Contents | Original geometry and exact adaptation |
|---|---|---|
| `2:2` | First Frame / Dashboard | **390×844**, status + compact top bar, primary data near top, horizontally grouped controls, balanced **2-column native service tiles**, fixed bottom tab dock. Replace room/device labels with measured KNOUX service groups. |
| `12:45` | Second Frame / Physical Dial | **390×844**, centered tactile instrument about 300×325, principal circular gauge ~210–270 width; plus/minus/modes below. Translate directly into **real Deep Scan, Storage or Battery instrument**. Not a temperature thermostat. |
| `12:147` | Third Frame / Analytics | **390×844**, upper summary approx x12,y118,w360,h262; lower chart approx x15,y407,w360,h413; single meaningful CTA. Translate into **Reports**, measured cleanup history and genuine battery/privacy insights. |

**Existing KNOUX-specific screens within the same Figma file:**

| Section | Node | Role |
|---|---|---|
| Home | `2010:2` | Base dashboard geometry |
| Deep Scan | `2010:5` | Instrument and scoped result state |
| Smart Clean | `2010:7` | Category and review UI |
| Duplicate Files | `2010:8` | Media groups and actions |
| App Residue | `2010:9` | Risk-aware source list |
| Privacy Sweep | `2010:10` | Real permission inspection |
| Battery | `2010:11` | Measured battery and history |
| AI Optimization | `2010:3` | Evidence-backed suggestions |
| Reports | `2010:4` | Genuine historical analytics |
| App Manager | `2010:6` | Installed-app browser |

**Figma design is the geometry reference, NOT the Pro color authority.** Preserve component locations, scale and functional meaning of the original reference, then apply **Arctic Depth / Frostline** tokens above. This design is not a copy/paste of the PC KNOUX AI theme; Pro intentionally looks calmer and more technical while sharing brand identity and Android ergonomics.

Other reference examples supplied by the owner: dark charcoal/amber tactile lamp/AC screens, deep blue dashboard/gauge/charts, refined white device dashboard, clean smart home UI asset pack. Use their visual discipline but NOT their Smart Home data. Do not add orange or purple across Pro as default; warning amber is state-specific only.

### Non-negotiable Android screen anatomy
- **Android native Kotlin + Jetpack Compose Material 3**, not React, HTML, WebView, promotional poster, iframe or image-backed fake screen.
- Design in **dp** for viewport, not 1080px as UI coordinates: baseline ~360×800dp and Figma 390×844 reference, with Android edge-to-edge/window insets and different screen dimensions.
- Proper Android status bar/cutout, content-safe padding and gesture bar. Do not paint a fabricated Apple Dynamic Island or extra device frame into real layout.
- Top toolbar ~56dp (compact official logo, back/nav contextual action). Content horizontal gutter 14–18dp. Vertical increments 8/12/16dp. Surface corners 16–22dp. Interactive hit areas >=48dp. Stable native dock bottom navigation with exactly existing tabs (Home / Clean / Tools / Reports) where these are actual routes; detail screens can use a contextual action rail and Android Back.
- **Real content scrolling** above the fixed action rail; no card buried under bottom nav, no header overlap, no text glyphs clipped in RTL or at fontScale >=1.3. When 2-column cards would become too narrow, switch to 1-column responsively; preserve semantic ordering.
- Native Compose primitive for real dial (`Canvas`, `graphicsLayer`) and optimized list (`LazyColumn`/grid) for many rows. No extra animation library or performance-heavy 3D dependency without demonstrated need.
- Match Figma major geometry first (toolbar, hero/content bounding boxes, card alignment, dial and charts, dock), then polish. Don't build a whole new information architecture.

## 3. START WITH REALITY — THE GITHUB-PRO REPO WAS EMPTY

**Never pretend the Pro GitHub repository already contains mobile source.** Use this deterministic source selection:

1. `pwd`, current Android/AndCode workspace, read active AGENTS/task manifest if present. Check whether a running session is modifying its current files and avoid collisions. Inspect `git rev-parse --show-toplevel`, `git status -uall`, `git remote -v`, `git branch -avv`, `git worktree list`, `git stash list`, `git reflog`, `.git` and untracked assets.
2. Query **live `https://github.com/daynightae-cmyk/KNOUX-AI-Pro.git`**, permissions, HEAD/branches. On 2026-11-14 it was an empty **private** repository (no commits). If now populated, compare history/assets carefully before pushing new work. Don't infer its contents from its name.
3. Locate the **most complete and recent authentic mobile-developed source**, not just most recent timestamp, across accessible AndCode workspace and owner Downloads recovery artifacts. The owner has extracted archives locally, possibly in many directories; inspect candidates without destructive cleanup. Recovery candidates include: `Download/KNOUX_AndCode_Migration_20261113/KNOUX_AI_LATEST_FULL_SOURCE.tar.gz`, verified historical mobile source archives, extracted recovered source trees, QA evidence and APK inventories. An archive and `.sha256` file should be validated against actual bytes when present. Distinguish code vs. generated build/cache, preserve assets/QA/Git history.
4. Verify candidate provenance: Git objects/reflog, mobile stage M00–M15 manifest, `KnouxAuthController`, `WelcomeScreen`, known Android package, Room schemas, tests, on-device behavior and latest unique code. Never equate an APK-only file with editable source. If sources conflict, inventory unique edits and explain source selection before merging; do not discard a candidate by recency alone.
5. Only after verifying chosen source, create **a dedicated new local Pro working checkout** in an available writable folder, or use a verified existing Pro checkout. **No fixed Windows path is required.** Never overwrite `/root/workspace/KNOUX-AI` or another agent's live workspace. No backup folder explosion or needless copies; protect uniquely valuable source first.
6. If the Pro remote is empty, establish this verified mobile source as Pro history using an ordinary initial branch/commit or safe repo-appropriate ancestry setup. Never import KNOUX-AI PC Git history as Pro source. Do not force-push. If Pro remote already contains history, reconcile without rewriting it. Do not delete artifacts or clean caches until after evidence and owner approval where destruction would occur.
7. If authentication/permission/network prevents remote push, continue local implementation, commit with true SHA where possible, and report `LOCAL_COMMITTED_REMOTE_NOT_VERIFIED`, not `PUSHED`. If source itself cannot be recovered, report `SOURCE_RECOVERY_BLOCKED` with exact inspected candidate evidence — do not fabricate code parity.

Useful commands (adapt to available platform; do not blindly run destructive equivalents):

```bash
pwd
[ -d .git ] && git rev-parse --show-toplevel || true
[ -d .git ] && git status --porcelain=v1 -uall || true
[ -d .git ] && git remote -v || true
[ -d .git ] && git log -5 --oneline || true
[ -d .git ] && git worktree list || true
[ -d .git ] && git stash list || true
[ -d .git ] && git reflog -n 8 || true
# Query Pro remote (may require logged-in GitHub CLI because private):
git ls-remote https://github.com/daynightae-cmyk/KNOUX-AI-Pro.git HEAD refs/heads/main
# Only identify high-value recovered archives, without unpacking atop existing source:
find /sdcard/Download -maxdepth 4 -type f \( -iname '*KNOUX*tar*' -o -iname '*KNOUX*zip' \) 2>/dev/null | head -80
```

If AndCode Android has scoped-storage restrictions and `/sdcard/Download` is not mounted in its container, use the app's authorized “Import project / Import files” picker, or an established ADB-accessible recovery folder. Do not request a Windows SSH password as a prerequisite for a phone-local source import.

## 4. ACTUAL APPLICATION FUNCTIONALITY STAYS CONNECTED

**A UI is accepted only when the customer control triggers a supported end-to-end Android workflow.** Keep the existing controllers/data/providers; change visual composition without replacing real code with sample JSON, fake progress or placeholder buttons.

### Ten essential Pro screens — visual component and real operation

**01 HOME — Precision Device Hub** (Figma `2:2`, `2010:2`)
- Use original top status/header, genuine device summary, main report metric, 2–3 compact live supporting figures, segmented category controls and a 2-column grid with real services.
- One proper solid Pro Blue CTA `Start Deep Scan`. Actual route to the existing Deep Scan controller; do not set a screenshot animation in lieu of logic.
- Grid uses real Android glyphs/icons, concise service labels and status, not copy-pasted neon rings: Smart Clean / Duplicates / Residue / Storage / Privacy / App Manager / Battery / AI / Reports. Compact dock of Home, Clean, Tools, Reports.
- A data card showing no scan uses `Not scanned yet`, not invented 3.2 GB or 75% optimized. Last successful scan/status must be sourced from Room/provider when known.

**02 DEEP SCAN — Frostline Sonar Instrument** (Figma `12:45`, `2010:5`)
- Original large dial becomes a real **scope-aware scan** ring with data-backed steps and a central current state, on-screen categories/files measured and an explicit scan scope.
- Avoid faux thermometer degrees, fictitious progress percentages, spinning globe with no actual reading, or “free 70 GB now” unless truly eligible and confirmed.
- Real `Start/Pause/Resume` only if controller supports it; otherwise `Cancel/Review Results` with honest capability states. Show actual scan errors, Android permissions, consent and partial coverage.
- Distinctive Pro waterline accent animates **only while scanning** and remains still otherwise. No theatrical particle halo.

**03 SMART CLEAN — Safe Review** (`2010:7`)
- Top measured reclaimable estimate, categorized cleanable items, selection state, protected and excluded items, item preview, legal Android trash/SAF workflow, final review and OS confirmation.
- Actual bytes recovered only after OS action + re-query; history persisted in Room. Review-first CTA, never one-tap delete everything. Distinct empty/permission/partial states.

**04 DUPLICATE MEDIA — Glacier Gallery** (`2010:8`)
- **Actual image/video thumbs**, visible video poster and duration, grouped duplicates, bytes, user-protected keeper, file path hints, group preview and per-copy selection. This is NOT a text-only page.
- Thumbnail grid and selection control must have 48dp targets. Respect media permissions and user privacy; use real content URI and cache appropriately.
- `Keep Original`, `Select Copies`, `Review`, `Move to Trash` only with necessary Android user consent; re-query and record history. No invented gallery photos or deletion count.

**05 APP RESIDUE — Provenance Explorer** (`2010:9`)
- Show source app, folder URI/location hint, actually validated ownership/risk status and size. Provide review/skip/exclude/consent-aware actions, using SAF/user grant. Never claim all foreign folders belong to an uninstalled app.
- Preserve owner files not in owned scope; no unattended permanent deletion.

**06 PRIVACY SWEEP — Permission Atlas** (`2010:10`)
- Real camera/microphone/location/overlay/notification/accessibility indicators from supported OS APIs; review apps and navigate to Android settings. Do not claim hidden access to privileged system audit data.
- Clear `Verified`, `Needs review`, `Permission required`, `Unsupported` status; no invented privacy score or exaggerated risk. A small clear privacy shield is okay, not a giant 3D neon helmet.

**07 BATTERY — Polar Meter** (`2010:11` and original dial `12:45`)
- One clean, tactile charge ring with actual measured `%`, charging state, temperature and available OS readings; history only when truly sampled/persisted. Clearly label unknown health/runtime estimates.
- Safe controls route to OS-supported settings or application-owned optimizations. No fake CPU governor/charging boost/battery miracle claims.

**08 AI OPTIMIZATION — Evidence Cards** (`2010:3`)
- Recommendation cards: issue, source/evidence, permission or user choice, potential impact only if justified, and **real action**. Distinguish local heuristic vs remote model vs unavailable provider.
- No center “AI orb” replacing actual actionable recommendations. Never auto-send personal storage/media details to cloud without user approval.

**09 REPORTS — Blue-Print Analytics** (`2010:4` and original `12:147`)
- Original upper compact KPI + lower large line/area chart layout; only Room-verified scan results, trash actions, actual recovered bytes, timestamps and status.
- Filter period and drill-down only if connected to real records. Chart empty state is *empty*, not a synthetic plotted history. User-friendly export only when implemented, permissioned and tested.

**10 APP MANAGER — Installed App Register** (`2010:6`)
- Native list with actual package label/icon, storage only if accessible, flags, filters, sort, app info, user-authorized uninstall intent, and permission review. No fake force-stop/uninstall commands requiring root/privileged capabilities.
- Preserve native app identity and item action hierarchy, not ecommerce-style app cards.

### Whole-app design propagation — DO NOT STOP AFTER TEN CONCEPTS
- Apply Frostline styling to every customer-visible routed part of the current mobile source: Home, Clean hub, Tools hub, Reports drill-down, scan detail, duplicates detail, residue item, storage analyzer, installed app details, privacy risk, battery diagnostics, AI suggestions, Pro Centre, the real 50-feature entitlement/capability catalog, settings, schedules, permissions, history, review sheets, selection modes, retry/restore, errors, empty/loading and user confirmation.
- **Protected nonfunctional-UI identity exception:** existing approved Splash/Smile/Onboarding/Login/OTP/Google/Passkey/Biometric/Identity Success components remain as approved, including founder logo and audio timing. Adapt only neutral surrounding app-shell areas when required and safe; do not repaint protected pages merely for uniformity.
- Product name display: `KNOUX AI Pro` where meaningful; parent brand `KNOUX`; do not change Android application ID/package name by default, as that can break Google OAuth, Play signing, existing user data, Supabase callback and migrations. Verify real owner-approved variant before renaming any package.
- Use 3–5 reusable Compose visual primitives with the existing app architecture rather than a parallel second design system. Pro palette is centralized; scales from dark to light. Reuse existing routes and controllers, do not replace navigation graph.

## 5. M00–M15 CONTINUITY / PRO FEATURE INTEGRITY

If the recovered Mobile Manifest is available, **preserve its actual state and history**, not a fabricated reset. Historical project milestones: M00 foundation, M01 Storage, M02 Duplicates, M03 Residue, M04 Privacy, M05 App Manager, M06 Battery, M07 Deep Scan, M08 Smart Clean, M09 Reports, M10 AI, M11 Home, M12 Settings, M13 Auth, M14 Pro/Cloud and M15 Release.

- Original M08 archival report asserted 100 JVM unit tests and build success in one historical environment, but also documented phone runtime gaps. **Rerun and report the tests in the chosen recovered workspace.** Do not claim these historical tests now pass.
- The 50 Pro capabilities are catalogued, not necessarily all implemented. Differentiate real implemented/permission-blocked/unsupported/queued; no fake subscription unlock or bogus Pro badge on guest users. Preserve the debug-only developer gate and keep production entitlement secure.
- OS restrictions are requirements, not bugs to bypass with root. User selections, confirmation, re-query, persisted Room history and retry/restore where supported determine real completion.
- Backend keys: A Supabase **publishable** key and OAuth client IDs may be placed in Android configuration; Google web client secrets, service_role, management PAT, refresh tokens and release signing private keys must remain secure server-side. Never add them to the repo, logs, screenshots, prompt or APK. No credentials are required in this design file.

## 6. DELIVERY PIPELINE — REAL IMPLEMENTATION, NOT A PROMPT-ONLY ANSWER

**Phase A — Source/agent reality.** Record actual `pwd`, file selection, target Pro remote state, HEAD, history, active writers, unique media, working tree, tests, Room schema and Android build environment. Do not alter the PC project. In an empty Pro repository, prioritize authentic recovery source before initializing Git.

**Phase B — Reference geometry capture.** Inspect Figma originals and available screenshots through authorized read-only tools. Map bounding rectangles: toolbar, main summary, native 2-column grid, fixed dock, dial zones, analytics graph. If live Figma inaccessible, use the exact node IDs/dimensions above plus owner images and existing UI baseline. Mark unknown measurements, do not invent a “verified pixel match.”

**Phase C — First native vertical slice.** Integrate the central Pro dark/light palette with existing Compose theme (do not duplicate theme classes), bottom navigation, shared service tile, gauge shell. Implement Home + one real destination without breaking its controller. Compile and capture actual device screenshot. Compare relative geometry with Figma. Correct layout overlap before rolling out.

**Phase D — Apply to all services and states.** Proceed systematically through 10 screens and all routed details. For each screen, link click targets to real controller, measured state, permission, selection, review and Android system confirmation. Capture screenshot in Dark and Light where possible. Iterate source rather than recreating entire Android project.

**Phase E — Quality / identity.** Check 360dp and 390dp widths, RTL Arabic/English, font scale 1.3, tall/short screens, gesture bars, insets and reduced motion. Dark/Light polish. Check 48dp min touch areas, dark text contrast, animations, scrolling and device rotation when the existing navigation supports it. Preserve official logo and protected auth/onboarding design.

**Phase F — Tests and integration.** Run supported `./gradlew testDebugUnitTest`, `assembleDebug`, relevant instrumentation and real device QA if genuinely available; retain stdout, exit code, test counts and source SHA. Validate Room migrations, OS confirmation, selected actions and privacy safeguards. Runtime screenshot evidence is required for visual acceptance; Gradle success alone is not a visual pass. Work with existing open tasks; do not claim 100% from one build.

**Phase G — GitHub Pro only.** Commit only validated, owned changes to the **Pro** repository in a source-safe way. Remote push only with authorized credentials and fast-forward, after checking remote SHA and active writers. Verify remote commit via `git ls-remote` and CI if configured. If remote is empty, first legitimate source initialization must preserve authentic recovered mobile source/Git ancestry and any unique untracked items; never force an unrelated PC tree into Pro.

**Phase H — Handoff.** Report source lane/path, source archive/source SHA provenance, HEAD/remote, git status, unique-work preservation, exact Kotlin files changed, Figma frame mapping, actual screenshots, 10-screen status, tests pass/fail/skip, APK hash, real device/Room/provider evidence, latest remote SHA and next safe action. Missing test infrastructure → explicit `NOT_RUN`, missing source → `SOURCE_RECOVERY_BLOCKED`, unfinished UI → `PARTIAL`, remote not pushed → `LOCAL_ONLY`. **NO FAKE CLOSURE.**

### Acceptance rubric per screen (actual screenshot + user path)
- [ ] Looks like **one cohesive genuine native Android app**, not ten differently themed websites.
- [ ] Original Figma geometry preserved: appropriate header/margins/main instrument/cards/chart/bottom actions.
- [ ] Pro color language distinct from PC: Navy + Arctic blue, refined white Day Mode, no omnipresent purple.
- [ ] Official logo untouched; protected auth/onboarding/smile/audio untouched.
- [ ] Real data/permissions, no invented GB, sensor readings, risk scores, completed scans or fake charts.
- [ ] All controls work end-to-end or clearly disclose unavailability.
- [ ] No clipping/inset overlap, supports 360dp, 390dp, Arabic RTL, EN, font scaling, dark/light.
- [ ] Exact Git and test/runtime evidence attached; unverified remains unverified.

## 7. START NOW — MANDATORY AGENT RESPONSE

On receiving this file, do not ask the owner to restate requirements or produce ten planning artifacts. **Begin source and Pro remote verification immediately, recover the most current independent mobile source if needed, and implement the first safe Kotlin/Compose design slice.** Continue through the app with measured checkpoints; protect other agents' working files. You may choose a minimal implementation order to avoid regressions, but **must not change the approved original Figma shell geometry, invent additional product scope, or merge PC Git work.**

The owner's two distinct labels are permanent within this handoff:

**`KNOUX AI` → PC-managed project → `daynightae-cmyk/KNOUX-AI` → `D:\KNOUX-AI`.**

**`KNOUX AI Pro` → phone/mobile project → `daynightae-cmyk/KNOUX-AI-Pro` → discover local AndCode source.**

**FINAL DIRECTIVE: REBUILD THE *VISUAL LANGUAGE*, NOT THE PRODUCT'S SOURCE OR FUNCTIONS. DARK NAVY PRO + PEARL-WHITE DAY MODE. Figma skeleton stays intact. Safe real Android services stay connected.**
