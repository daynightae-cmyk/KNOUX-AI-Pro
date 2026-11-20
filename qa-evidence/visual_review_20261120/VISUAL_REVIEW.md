# KNOUX AI Pro — Full Visual Review & Polish (presentation-only)

Date: 2026-11-20 (session continuing M11 batch)
Branch: master · Scope: UI presentation only — no logic, data, navigation
destinations, splash timeline (TOTAL_MS=3100f / CLICK_AT_MS=2440f), security
invariants or approved neon/gold identity changes.

## Status vocabulary (honest)

- UNIT_VERIFIED: pending final `:app:testDebugUnitTest` run of this pass (see build log below).
- SOURCE_VERIFIED: static source sweeps listed below were executed against the working tree.
- DEVICE_SUBSET_VERIFIED: NOT claimed for this pass yet — authorized Samsung SM-A025F
  was disconnected (`adb devices` empty) during the pass. Device sweep will follow on reconnect.
- VISUAL_VERIFIED: NOT claimed — no image input available in this session; pixel-level
  review was not performed. Evidence is programmatic (source sweeps + unit tests +
  prior-device hierarchies), not screenshots judged by eye.

## What changed (grouped by impact)

### 1. Legibility floor (previous phase, included in this commit)
- 346 text sizes below 9sp raised to 9sp across 36 files (scripts:
  D:\Temp\opencode\type_scale_pass.ps1 / type_scale_pass2.ps1), 9 explicit
  lineHeights relaxed to >= 1.18x their font size.
- Final sweep: **0 occurrences of fontSize/size < 9sp remain** in app/src/main/java/com/knoux/ai.

### 2. Shared design system (DesignSystem.kt)
- KText: default overflow resolves to Ellipsis when maxLines == 1 (fixes ~101 call sites
  that hard-clipped mid-glyph); explicit Clip still honored.
- Removed invalid negative `Spacer(Modifier.height(-8.dp))` in V2DeerHeader Pro branch.
- HeaderIcon hit target 36 -> 40dp; CrownHexButton hit 34 -> 40dp (visual 34dp kept).
- V2Chip clickable min height 36dp; V2MetricRow label width 52 -> 58dp + maxLines=1.
- Bottom bar: standard-mode Pro orb brown fill (0xFF4A2A20/0xFF1D0F0B) replaced with
  theme panel tokens (violet world consistency); active tab literals -> V2Blue/panel2;
  tab radius 19 -> 20dp; bar fill literals -> ProTheme.barTop/panel2.
- Route -> tab highlight now catalog-driven (`bottomBarLaneForRoute`): SectionHub entries,
  CleanTools routes, SectionTools defs and dev-/cloud routes map to their real lane
  instead of silently falling through to "home".
- V2GradientButton trailing chevron ArrowBack -> ChevronLeft (RTL-correct forward glyph).
- Bottom-nav icons 20 -> 22dp, labels 9.5 -> 10.5sp; ProPill 9sp; ProBadge 8sp.

### 3. Pro theming restored on shared scenery (27 new ProTheme scenery tokens)
- New tokens: skyTop/skyMid/skyGlow/skyBase, heroTop/heroMid/heroBase, ridgeTop/ridgeBase,
  artCoreTop/Mid/Base, sonarGlass, orbCore/orbMid, surfaceTop/Mid/Base, surfaceSeam,
  mountainTint, gaugeCore/gaugeRim, bronzeEdge/bronzeMid, barTop.
- Standard values are byte-identical to the previously hardcoded art (no visual change
  in standard mode). Pro values warm the same layers toward the golden horizon
  (deep plum sky + gold accents — never a flat brown wash).
- Consumers converted: ServiceShellV2 (ServiceHero gradient, CompactHeroSky, ridge,
  ServiceArtScene core, DeepScanSonar glass + center), LuxuryV2 (header scenery,
  gauge core, orb core/fill, action pill, V2LuxurySurface fill + seam),
  ServiceVisualsV2 (gauge core, stat/row tile fills), CleanDetailScreensV2 (media thumb
  fallback), SectionToolsV2 (SectionOrb core).
- Gold unified: stray `0xFFFFD700` literals replaced by V2GoldText/ProTheme.gold;
  OnboardingV2 Pro gradient start 0xFFFFE7AA -> V2GoldText.

### 4. Back/crown affordances on 14 previously back-less routed screens
- ExtraHubsV2 x4 via ExtraHubHeader (developer-info, google-services,
  advanced-security, backup-restore).
- developer/ui x7 (DeveloperStudio, ProviderMarketplace, ApiKeyVault, ModelExplorer,
  AgentEngine, MobileDeveloperWorkspace, AgentToolSystem).
- cloud/google/microsoft dashboards x3 (UnifiedCloudCleaner, GoogleStorage, MicrosoftStorage).
- All now render `V2TopBar(V2TopMode.INTERIOR, onBack, onPro)` above V2LuxuryIdentity;
  the previously unused onBack/onPro parameters are wired, no route changes.

### 5. Touch targets
- Deep-scan pause/resume: 13dp glyph click -> whole 34dp V2NeonRing clickable.
- App uninstall (MoreScreensV2): ~22dp -> 40dp hit box around 28dp visual chip.
- Dev-panel close (MoreScreensV2): 24dp icon -> 40dp hit box, 20dp glyph.
- AppAction (SectionToolsV2): 30dp -> 40dp hit box, 30dp visual disc kept.
- Onboarding back pill: ~21dp -> heightIn(min=36dp), label 9.5 -> 10.5sp.

### 6. Typography consistency in dialogs and headers
- AlertDialogs converted to KText (Arabic font, bidi, lineHeight, theme colors):
  MainActivity account-info, HubScreensV2 own-cache consent, CleanDetailScreensV2
  residue consent (title/body/confirm/dismiss).
- Large raw-Text headers converted to KText: TabTitle (27sp), SmartCleanHeader (26sp),
  ScanScreenHeader (30sp), ResidueHeader (24sp), PrivacyTitle (27sp).
- Annotated-string Pro lockups (HubScreensV2 Pro screen, OnboardingV2 welcome) kept as
  raw Text intentionally (KText has no annotated overload) — they already use V2Font.

### 7. Layout robustness
- MoreScreensV2 estimator row: three dynamic KTexts now weight(1f) + maxLines=1 inside
  fillMaxWidth Row (no overflow at large counts / font scale).
- Fixed-height text containers -> heightIn(min=...) so accessibility font scale grows
  them instead of clipping: V2LuxuryServiceTile (102dp), V2LuxurySideCard (84 -> min 90dp,
  stage 199 -> 205dp to keep the orb/side-card composition), PrivacyDomainCard (93dp),
  SectionToolsV2 related-service card (72dp). Inner Columns switched fillMaxSize ->
  fillMaxWidth accordingly.

### 8. Corner-radius scale normalization
- 26 distinct single-value radii folded to 8/12/16/20/24dp (+ percent pills 50/100 kept):
  79 replacements across 17 ui/v2 files (script D:\Temp\opencode\radius_scale_pass.ps1).
- Post-pass distribution: 8dp x44, 12dp x46, 16dp x25, 20dp x6, 24dp x3.
- Asymmetric/dynamic shapes (hero 26/30 corners, size*.31f tiles) intentionally untouched.

## Deliberately NOT changed
- Approved art assets, splash timing, service logic, Room, navigation routes.
- OnboardingEcosystem illustration palette and caption tint accents (HubScreensV2
  0xFFD5ACFF/0xFFFFA4DF, Pro orb warm center 0xFF20100A, recycle-bin art 0xFF150B33):
  intentional illustration colors, not theme surfaces.
- Legacy dead UI files (ui/screens/* except Welcome/auth path, Neon/Rows/Premium/Reference):
  font floor applied only; deletion deferred as owner work.
- Encoding: files verified clean UTF-8 Arabic; `???` in console output is a PowerShell
  codepage artifact, not file damage. No mojibake "fixes" applied.

## Verification
- Build + unit tests: `:app:assembleDebug :app:testDebugUnitTest` —
  build_visual_final.log (this folder): BUILD SUCCESSFUL in 11m 48s, EXIT=0.
  Test XML parse: 12 suites, tests=105 failures=0 errors=0 skipped=0.
  First run of the pass (pre-token edits, build_visual_pass.log): BUILD SUCCESSFUL, 105/105.
  Intermediate run failed on 3 unresolved `theme` refs in DeepScanSonar (fixed before final run).
  Final debug APK SHA-256: 5084048141E38E8AB2B9B68D1FA3B8C92908FC9602873E18BC6A0F36EA445BA8.
- Static sweeps executed: sub-9sp count = 0; radius distribution above; ProTheme
  constructed only in DesignSystem companion (no other constructors to break);
  unit tests assert layout math (serviceHeaderHeight etc.), untouched by this pass.
- Device: SM-A025F disconnected during pass; DEVICE_SUBSET_VERIFIED for the visual
  pass pending reconnect + debug-variant sweep. Production com.knoux.ai untouched.

## Push state (unchanged by this pass)
Remote master still 2af28cf373f1ce5218ea55f799819f380aae9bc2. GH013 push protection
pinned to fdd7614 (ProductionSecurityTest.kt:78); owner unblock action required:
https://github.com/daynightae-cmyk/KNOUX-AI-Pro/security/secret-scanning/unblock-secret/3MMRZEaEyR1DON98dMUcT8cOJh7
No push attempted in this pass. No history rewrite (contract).
