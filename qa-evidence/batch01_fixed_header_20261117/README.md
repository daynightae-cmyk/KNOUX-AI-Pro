# Batch 01 — service header (board 00) · 2026-11-17

Owner board: `_references/annotated_20261117/annotations/00_fixed_service_layout.png`
(ZIP opened from `KNOUX_AI_Annotated_Visual_References.zip`).

## What the board asks for

1. One fixed service header: the top bar never moves, and the service hero never
   scrolls entirely away while the operator reviews results underneath it.
2. Inside that header, the official stag **above** the content, the wordmark and
   the service instrument (radar for Deep Scan) on the same stage.
3. The radar moves **only** while the scan is really reading a stage; it stops on
   cancel and when the result appears.
4. Reduce Motion must leave the same screen without movement.

## What changed (code)

| File | Change |
| --- | --- |
| `ServiceShellV2.kt` | Header is no longer a list item. `FixedServiceScreen` puts the hero above the list and folds it from its full height to a compact bar as the list scrolls (`serviceHeroFold`, `foldedHeroHeight`, `heroStageScale`, `serviceStageHeight`). The wordmark row fades while folding, the title and the live status line stay, the stage lane keeps 96–112dp so the stag and the radar are never squeezed away, and the lane is clipped so nothing paints over the title. |
| `ServiceShellV2.kt` | `HeroStatusStrip` has a `collapsed` mode that enlarges the live status ("جاري الفحص · 2 من 6") when the header is folded. |
| `DeepScanScreenV2.kt` | Starting a scan animates the list back to the top so the open header and the moving radar are on screen while the stages are read. |
| `MainActivity.kt` | Scaffold comment/inset note: the bottom bar owns the navigation-bar inset, so the last card of a page is not cut behind the bar. |

The radar sweep itself (`DeepScanSonar`) was already driven by the Compose frame
clock, only while `DeepScanUiState.Running`, and it is cancelled by the
`LaunchedEffect(running, motion)` key change on cancel/complete. Reduce Motion
(`rememberMotionEnabled`, backed by `ANIMATOR_DURATION_SCALE`) keeps the static
frame and snaps every fold.

## Evidence (device: SM-A025F, Android 12, 720x1600 @280dpi)

Screenshots and videos in this folder are real device frames captured over ADB.
`adb shell am start -n com.knoux.ai.debug/com.knoux.ai.MainActivity --es route deepscan`
opens the Deep Scan route directly in the debug build.
