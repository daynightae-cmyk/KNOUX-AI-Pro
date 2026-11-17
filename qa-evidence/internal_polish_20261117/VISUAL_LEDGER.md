# Internal service UI review — 2026-11-17

- Base HEAD: `6e81be41ac4fb18af64a086864d76882d263e486`; working tree contains pre-existing owner work and this batch. No commit/push or owner approval is asserted.
- Device: Samsung SM-A025F, API 31; real ADB screenshots, not generated images.
- Package: `com.knoux.ai`, preview build, installed with `adb install -r -t`; no app-data clear, permission grant, accessibility change or display-size/density change.
- Build + unit tests: `:app:assemblePreview :app:testPreviewUnitTest` succeeded, 8 tests, 0 failures/errors.
- Internal-detail capture build (03–10) APK SHA-256: `689FA6BDCBB8C76D97C4E974B44D0645FC9FA6CB31E35673D125E06DBE09A5B1`.
- Reference: current Home/hub artwork and the owner's visual-only handoff (`01_DEEPSEEK_PRO_GOLDEN_VISUAL_ONLY_EXECUTE.md` / `02_TEN_SECTIONS_100_SERVICE_CARDS_VISUAL_MAP.md`). Existing source artwork was reused; nothing was generated.

## Reviewed captures

Implementation for all detail rows: `app/src/main/java/com/knoux/ai/ui/v2/SectionToolsV2.kt`, shared hero/capability cards in `ServiceFinish.kt`, metadata in `ToolCatalogV2.kt`.

| Route | Capture | State / review | Status |
|---|---|---|---|
| `dup-min-size` | `03_dup_min_size_final.png` | Same art as its hub card; filter control; no fake media results before permission; all permission labels agree. | VISUAL_REVIEWED |
| `dup-groups` | `05_dup_groups_settled.png` | Permission state replaces an apparently successful empty duplicate result. | VISUAL_REVIEWED |
| `dup-scan` | `06_dup_scan_permission.png` | SHA-256 copy; bounds note retained; no reported group count before permission. | VISUAL_REVIEWED |
| `apm-search` | `07_app_search_data.png` | Live installed-app observations; search input and readable hero. | VISUAL_REVIEWED |
| `apm-search` | `08_app_search_no_match.png` | Entered a deliberately nonmatching QA query; correct filter-empty state. | VISUAL_REVIEWED |
| `rpt-range` | `09_report_range.png` | 30-day chip selected; local history empty, without invented entries. | VISUAL_REVIEWED |
| `dsn-scope` | `10_deep_scan_scope.png` | Real storage values, unavailable media clearly marked; Android data boundary retained. | VISUAL_REVIEWED |
| `hub-storage` | `12_hub_storage_final.png` | Mascot/text separation; media says “يحتاج إذن”, not “0 B”. Subsequently superseded by the owner's larger-mascot request; needs a fresh capture of that version. | VISUAL_REVIEWED |

`01` is the initial installed detail UI; `02` is an intermediate permission screen with a contradictory static chip, corrected in `03`. `04` is an earlier groups frame; use `05`. `11` is the pre-fix hub (media displayed as 0 B).

## Interaction verification

The permission card opened `com.android.settings/.applications.InstalledAppDetails` on the actual phone. Returned with Back; no permissions were granted. App search visibly produced the expected nonmatching state after input.

## Not verified / next work

- Loading / media filter-empty precedence has five presentation unit tests. No real-device screenshot of those media states is claimed: media permission remains ungranted.
- Shared hero changes affect the nine-section internal template. This ledger reviews six distinct internal routes, not all 90/100 destinations.
- The current 100-card legacy taxonomy is not yet fully reconciled with every named destination and all 50 Pro placements in the supplied handoff.
- No full-route acceptance, real entitlement, service-engine E2E, purchase, authentication success or Stage 1 completion is claimed.
- Owner's next direction: larger, consistently sized stag as a background overlay without changing app/card/window dimensions; review onboarding/company/ecosystem/founder/login in order. New evidence belongs in `../stag_onboarding_20261117/`.
