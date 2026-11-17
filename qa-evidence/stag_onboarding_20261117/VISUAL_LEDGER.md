# Stag + ordered onboarding — 2026-11-17

Owner direction: keep app/card/window dimensions unchanged, use the original stag at a generous consistent header size, allow it behind the upper block, and add subtle motion around upper icons. Review entry screens in order.

## Implementation / references

- Original supplied art: `app/src/main/res/drawable-nodpi/knoux_stag_official.png`, unchanged.
- Shared Home/hub background footprint: `app/src/main/java/com/knoux/ai/ui/v2/HeaderMascotV2.kt` (232 dp width, capped for narrow screens). It is a Box background layer, not a new content row.
- Stag uses `ContentScale.Fit`, never stretched. Zoom/breathing scale removed from `V2DeerHeader`; only gentle translation remains.
- Upper-icon glow + orbital highlight: `app/src/main/java/com/knoux/ai/ui/components/HeaderHalo.kt`.
- Entry mascot float: `app/src/main/java/com/knoux/ai/ui/screens/AuthStag.kt`.
- Ordered onboarding: `app/src/main/java/com/knoux/ai/ui/v2/OnboardingV2.kt`: Welcome → Company → Ecosystem → Founder → existing Login.
- Existing ecosystem art/layout preserved in `OnboardingEcosystem.kt`; small icon halos added within existing footprints.
- Motion respects the existing `rememberMotionEnabled()` policy.
- Baseline display: physical size 720×1600, density 280; no size/density override commands issued.
- Pre-existing login/welcome and Pro palette edits are preserved. No authentication engine or service-engine work is part of this batch.
- New owner reference pack: `C:\Users\day night\Downloads\KNOUX_AI_PRO_VISUAL_EXECUTION_COMPLETE_20261117 (1)\KNOUX_AI_PRO_VISUAL_EXECUTION_HANDOFF_20261117`. Onboarding polish follows `references/02_ONBOARDING_AND_FOUNDER/084_Welcome_Intro.png`, `095_Welcome_Stag_Intro.png`, `046_Ecosystem_Tools.png` and `030_Founder_Signature_Cinematic.png` as style references only; their illustrated numbers and portrait treatment are not runtime claims.
- Preservation snapshot taken before the polish batch: `D:\Temp\opencode\knoux-visual-snapshot-20261117-110031`, 49 files, manifest SHA-256 `B20BAA119EC9001B7CFB0486B98969FF0816D0AE3356D53ECD23287950DEC1E7`, restore spot-check MATCH. No reset, clean, worktree deletion, agent stop, force-push or remote write.
- Owner clarified the immediate scope: finish the few entry/onboarding pages between Login and Home, not the internal service pages. An unintegrated service-continuity draft was removed; no service UI was changed by the polish batch.
- Final onboarding pass: the Welcome/Founder hero scene and the Company art use the same 232 dp stag footprint as the Home/hub header (Fit, never stretched). The lower part of Welcome, Company and Founder now carries real product structure instead of empty space: a capability card, the catalog counts (10 sections / 100 navigable service cards), the review-before-execute path, the vision card, and three product values. These are catalog/UI facts, not device measurements or scan results.
- Welcome feature tiles were given one equal width; the subtitle was tightened; the Company page lost a duplicated footer line; the Founder name is English-only (`Eng. Sadek Elgazar`) and the sign-off uses a native cursive face. The PRO chip in the header is a brand label, not an entitlement.
- Intermediate captures `11_*`–`19_*` are iteration frames and are superseded by the final set below. `16_welcome_fit.png` and `17_company_fit.png` documented the clipping found during review.
- Final APK SHA-256: `410DE54AB46B2399B02ABCF92FAF0340019E62FD3DD8DDD58ABB75B1FE60E4BC` (supersedes `7A81F379…`; the only screen difference is the new Ecosystem PRO chip); package `com.knoux.ai` versionName `1.0.0-preview` (versionCode 1); build `:app:assemblePreview :app:testPreviewUnitTest` succeeded, 8/8 unit tests passed.
- Local commits for this screen-reviewed entry batch: `f39a979` (`Polish onboarding entry flow and unify stag header`, 9 code files) and `2fed349` (`Add PRO chip to ecosystem onboarding header`). Service-hub modifications, `ToolPresentation.kt`, its test and all QA evidence remain outside those commits; nothing was pushed. The installed APK also contains the unstaged service-hub polish, so its SHA identifies the tested build, not the committed entry-only tree.

## Capture review

`01_welcome_before.png` is the actual installed baseline. New-version screenshots are accepted only after opening and examining each one; build success is not a visual verdict.

| Screen | Evidence / review | Status |
|---|---|---|
| Welcome | `24_welcome_final.png`: 1/4, full stag at the fixed footprint, gold laurels, small PRO chip, three equal feature cards, capability card, catalog band fully visible above the navigation dots. | VISUAL_REVIEWED |
| Company | `21_company_approved.png`: 2/4, 232 dp ghost stag right / brand orb left, laurels, existing company copy, catalog band, review-before-execute path fully visible. | VISUAL_REVIEWED |
| Ecosystem | `29_ecosystem_pro_chip.png`: existing ten-node map preserved, gold laurels added, small PRO header chip, icon halos, links and labels intact, lower rails fill the page. | VISUAL_REVIEWED |
| Founder | `25_founder_final.png`: 4/4, English-only founder name, laurels, product band, vision card with cursive sign-off, values and catalog band. | VISUAL_REVIEWED |
| Login | `26_login_from_onboarding.png`, `27_login_scrolled.png`: reached from Founder; upper scene and scrollable lower actions examined; guest entry remains reachable. | VISUAL_REVIEWED |
| Home / shared section mascot | `28_home_after_onboarding.png`: real post-onboarding Home, fixed 232 dp stag behind the upper block, title and subtitle clear of the art, lower stats filled with live readings. | VISUAL_REVIEWED |

- Actual Next navigation was verified Welcome → Company → Ecosystem → Founder; Start opened Login; guest entry opened Home. No real sign-in was attempted.
- `03_company.png` is an intermediate frame that actually shows Login, not Company. It is not used as company evidence; `04_company_verified.png` was the earlier verified second step and `21_company_approved.png` is the final one.
- First build needed a missing `BrandSymbol` import; corrected. Later builds and all 8 unit tests succeeded and installed on the handset.
- The catalog counts 10/100 come from the shipped 10-section × 10-card navigation structure. They are not a claim that every destination has completed the 100-destination visual gate.

This is a partial visual batch, not owner approval or completion of the 100-destination visual gate.
