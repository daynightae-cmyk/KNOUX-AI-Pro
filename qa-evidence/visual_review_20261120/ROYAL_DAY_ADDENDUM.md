# KNOUX AI Pro — Royal Stage Redesign + Professional Day Mode (addendum)

Date: 2026-11-20 (continues the visual review pass above)
Branch: master · Owner direction: "الجمال أولا" — match the annotated reference
boards `_references/annotated_20261117/annotations/00–06` as closely as possible;
the last four hubs get the same royal treatment; the run orb gets clearer and
more interactive; day mode must be professional, not an inverted night.

Owner granted full design authority for this pass: the previous compact-header
layout contracts were rewritten to pin the new royal-stage contract instead.
Service logic, Room data, routes, splash timeline (TOTAL_MS=3100f /
CLICK_AT_MS=2440f) and security invariants are untouched.

## 1. Royal service stage (boards 00–01)

- `serviceHeaderHeight`: compact lane 140dp; otherwise `(width * .80)` clamped
  264–312dp → 288dp on a 360dp phone (~45% of the window, the reference
  proportion). Results keep ≥480dp on an 800dp window.
- `serviceStagSize`: `min(header * .88, width * .62)` clamped 120–252dp — the
  stag floats large behind the instrument with its traveling halo.
- Title scale 28 / 25 / 22.5 / 20.5sp by name length (compact −3).
- `FixedServiceScreen`: royal stage fixed under the app bar → primary action
  pinned directly beneath it (`service_primary_action`) → results scroll below
  (`service_results_list`). The run control never sits at the screen bottom.
- `ServiceBanner` (legacy wrapper) renders the same `ServiceHero`; every caller
  places `KnouxRunOrbBlock` as the very next list item, i.e. directly under the
  header — verified at all 16 orb call sites.
- Contracts rewritten: `ServiceShellMathTest` + `ServiceHeaderLayoutTest` now
  assert the royal-stage sizes (old compact-header assertions replaced by
  owner direction).

## 2. Last four hubs — royal treatment

`ExtraHubHeader` (developer-info, google-services, advanced-security,
backup-restore) now takes spec/status/statusColor/metric and renders the same
royal `ServiceHero` stage as the ten service families. Their orbs became live
re-read controls (`enabled = facts.computedAt > 0L`, `onTap = bump()`, 118dp).

## 3. Run orb interaction (RunOrbV2)

Press settle (.93 scale) + glow bloom + haptic tick (LongPress, motion-gated)
+ tap ripple ring + clear pill caption. Semantics/labels unchanged; the orb
remains the only way a service starts.

## 4. Professional day mode (porcelain chrome, night-lit islands)

Architecture: `KnouxDayState` (prefs `knoux_day_v1`/`day_mode`, observable),
`LocalDayMode`, `LocalKnouxOnDark`. KText auto-maps White→DayInk and neon→
deepened `dayAccent()` unless inside an onDark island. Debug builds default to
the golden Pro preview, so Pro tokens deepen to warm gold/amber inks on
porcelain (ProCyan→DayGold, ProViolet/ProBlue→DayAmber, ProPink→DayPink).

- New `DayTheme.kt` palette: porcelain paper, white cards, deep violet ink,
  WCAG-minded deepened accents.
- Day world: V2Background (porcelain sky + day mountains/dust), V2GlassCard,
  V2LuxurySurface, V2BottomBar (+active tab island), HeaderIcon, V2IconTile,
  V2SearchField, V2GhostButton, V2Chip, V2Toggle (+mini toggle), HairLine,
  V2ProgressBar/V2LuxuryProgress tracks, V2Check, ChipsRow, stat/row tiles,
  LegendChip, V2MetricRow, section titles, health lines, glyphs, pro banner,
  BasicTextField ink, page headings, dialogs (Material `KnouxDayColorScheme`).
- Night-lit islands (LocalKnouxOnDark): cosmic ServiceHero stage, orb jewel
  inner column, gradient CTAs, PRO pill when active, media count badges,
  bottom-bar active tab. Gauges and V2NeonRing get porcelain faces in day so
  their center text stays legible without darkening.
- Brand wordmark (white-on-transparent night art) re-inked DayViolet on
  porcelain via ColorFilter.tint.
- Splash / onboarding / auth stay full night worlds (approved art): entry
  branches force `LocalDayMode=false, LocalKnouxOnDark=true` and repaint their
  own V2Background; status-bar glyphs follow the world
  (isAppearanceLightStatusBars = day && shell).
- Settings: live day toggle (`SettingToggleRow` → `KnouxDayState.setEnabled`),
  repaints the whole shell instantly. Debug sweep hook: `--es day_mode true|false`.

## 5. Verification

- Build: `:app:assembleDebug :app:testDebugUnitTest --no-daemon` —
  BUILD SUCCESSFUL in 7m 44s (build_royal_day2.log, this folder).
  First attempt failed on one joined-line syntax error (LuxuryV2.kt:395,
  introduced by an edit; fixed, full-file scan found no other damage).
- Unit tests: 12 suites, **105/105 PASS, 0 FAIL, 0 ERROR** (test-results XML parse).
- Debug APK SHA-256: FEAA49C036153A927E3F53C7EA68F9A52C7D8E7352C159B3B117253479D571C3
  installed on the authorized SM-A025F (isolated `com.knoux.ai.debug` only;
  production `com.knoux.ai` untouched).
- Device sweeps (26 routes each, screencap + uiautomator + logcat crash check):
  - Night: `device/` (script visual_sweep2.ps1, WaitSeconds 12) —
    **26/26 resumed=True, png>200KB, crash=False**.
  - Day: `device_day/` (script visual_sweep_day.ps1, `--es day_mode true`) —
    **26/26 resumed=True, png>440KB, crash=False**.
  - Logs: sweep_night_royal.log / sweep_day_royal.log (this folder).

## 6. Pixel review vs reference boards

Night (viewed: home, hub-smartclean, deepscan, residue, privacy, duplicates,
developer-info, pro): royal stage fixed under the app bar at reference
proportion; stag at stage scale behind the live instrument; status ribbon;
K orb + caption pinned directly beneath the header; results scroll below.
Boards 00/01/03/04/06 structure honored. Idle readings stay honest
("غير متاح", "0 B", "لم يبدأ بعد"); Pro preview keeps its
"المعاينة البصرية مفعّلة / استحقاق Pro غير متاح حاليًا" disclosure.

Day (viewed: home, hub-smartclean, hub-storage, hub-developer, deepscan,
residue, privacy, duplicates, appmanager, reports, pro, settings,
developer-info, google-services, advanced-security, backup-restore,
developer-studio, dev-api-key-vault, dev-model-explorer, cloud-cleaner,
google-storage, microsoft-storage): porcelain chrome with deep violet ink,
deepened gold/amber/pink/green accents, white cards; cosmic stage, orb jewel
and gradient CTAs stay night-lit islands with untouched white text; settings
shows the live day toggle ON; status-bar glyphs dark on porcelain. No
dark-on-dark or invisible-white text found on any viewed route. External
integrations keep honest "غير مهيأ / لا حساب مرتبط" states; Room operation
log shows real measured rows only.

### Defect found and fixed during review

The hero status ribbon was vertically sliced on BOTH worlds (uiautomator
bounds: 15px node for 8.8sp text, 7px for 7.6sp): the hero Column's fixed
content (identity + title + stage-scale instrument) consumed the stage, the
weighted spacers collapsed, and the ribbon was measured with leftover scraps
inside `clipToBounds()`. Fix in `ServiceShellV2.kt`: the ribbon is now pinned
to the stage floor as a `BottomCenter` overlay outside the column, the column
reserves a 40dp (compact 28dp) ribbon lane, and the instrument is capped by
`maxHeight - 165.dp` so the column always fits. Rebuilt, reinstalled and
re-swept the affected royal-stage routes in both worlds (see §7).

## 7. Ribbon-fix verification build

- `:app:assembleDebug :app:testDebugUnitTest --no-daemon` — BUILD SUCCESSFUL
  in 5m 19s (build_ribbon_fix.log, this folder). 12 suites, **105/105 PASS,
  0 FAIL, 0 ERROR**.
- Debug APK SHA-256: FAC5536720A58DB261084BDA707EADE265A888C5C242988BBC23E060BE283CFC
  installed (Success) on the authorized SM-A025F, isolated debug variant only.
- Targeted re-sweep: 8 royal-stage routes × 2 worlds
  (visual_resweep_ribbon.ps1 → ribbon_resweep_results.csv in each folder;
  sweep_ribbon_fix.log). RESULTS: **16/16 resumed=True, crash=False**,
  png 744KB–1.1MB.
- Objective ribbon check (uiautomator bounds of the ribbon zone, all 16 new
  XMLs): every capture has exactly 2 text nodes (status + metric), minimum
  text height **25px** (hub-smartclean: 30px), **0 squashed nodes**. Before
  the fix the same nodes measured 15px / 7px — glyphs sliced. Fixed in both
  worlds.
- Visual re-check after the fix: device/residue, device/hub-smartclean,
  device_day/residue, device_day/backup-restore — ribbon reads full-height
  with clean Arabic descenders; stage proportion and honest idle readings
  unchanged.

## Push state (unchanged)

Remote master still 2af28cf373f1ce5218ea55f799819f380aae9bc2. GH013 pinned to
fdd7614; owner unblock action required:
https://github.com/daynightae-cmyk/KNOUX-AI-Pro/security/secret-scanning/unblock-secret/3MMRZEaEyR1DON98dMUcT8cOJh7
No push attempted. No history rewrite (contract).
