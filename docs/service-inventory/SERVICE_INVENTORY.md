# KNOUX AI PRO — Service Inventory

**Source of truth**: `app/src/main/java/com/knoux/ai/ui/v2/ServiceCatalog150.kt`, `ToolCatalogV2.kt`, `ToolCatalogExpansionV2.kt`, `SectionToolsV2.kt`, `MainActivity.kt`

**Canonical branch**: `master` (SHA: `1768882`)

**Historical target**: 153 baseline + 50 PRO = 203 services (NOT verified — actual counts below)

**Classification scale**:
- `REGISTERED` — Listed in a service catalog
- `REACHABLE` — Has a route registered in navigation graph
- `IMPLEMENTED` — Has UI composable / implementation code
- `SUPPORTED_BY_ANDROID` — Uses only Android-supported APIs
- `PERMISSION_LIMITED` — Requires dangerous/special permission
- `FUNCTIONALLY_TESTED` — Verified on physical device
- `DEVICE_VERIFIED` — Verified end-to-end on Samsung SM-A025F

---

## Service Family 1: storage_analyzer (M01)

| Service ID | Route | Type | Classification | Source File | Status |
|---|---|---|---|---|---|
| STORAGE_ANALYZER_C01 | sto-volumes | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:63 | UI only |
| STORAGE_ANALYZER_C02 | sto-used-free | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:64 | UI only |
| STORAGE_ANALYZER_C03 | sto-categories | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:65 | UI only |
| STORAGE_ANALYZER_C04 | sto-categories | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:66 | UI only |
| STORAGE_ANALYZER_C05 | sto-sorting | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:67 | UI only |
| STORAGE_ANALYZER_C06 | sto-search | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:68 | UI only |
| STORAGE_ANALYZER_C07 | sto-open-share | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:69 | UI only |
| STORAGE_ANALYZER_C08 | sto-move-delete | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:70 | UI only |
| STORAGE_ANALYZER_C09 | sto-move-delete | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:71 | UI only |
| STORAGE_ANALYZER_C10 | sto-details | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:72 | UI only |
| STO_DETAILED_BREAKDOWN | storage-breakdown | PRO | REGISTERED, REACHABLE | ServiceCatalog150.kt:73 | UI only |
| STO_LARGE_FILES | large-files | PRO | REGISTERED, REACHABLE | ServiceCatalog150.kt:74 | UI only |
| STO_GROWTH_TRENDS | rpt-trends | PRO | REGISTERED, REACHABLE | ServiceCatalog150.kt:75 | UI only |
| STO_CAPACITY_PLANNER | storage-breakdown | PRO | REGISTERED, REACHABLE | ServiceCatalog150.kt:76 | UI only |
| STO_EXPORT | null | PRO | REGISTERED (blocked: export not built) | ServiceCatalog150.kt:77 | PLACEHOLDER |

---

## Service Family 2: duplicate_finder (M02)

| Service ID | Route | Type | Classification | Source File | Status |
|---|---|---|---|---|---|
| DUPLICATE_FINDER_C01 | dup-scope | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:82 | UI only |
| DUPLICATE_FINDER_C02 | dup-types | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:83 | UI only |
| DUPLICATE_FINDER_C03 | dup-scan | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:84 | UI only |
| DUPLICATE_FINDER_C04 | dup-groups | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:85 | UI only |
| DUPLICATE_FINDER_C05 | dup-compare | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:86 | UI only |
| DUPLICATE_FINDER_C06 | dup-compare | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:87 | UI only |
| DUPLICATE_FINDER_C07 | dup-keeper | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:88 | UI only |
| DUPLICATE_FINDER_C08 | dup-exclusions | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:89 | UI only |
| DUPLICATE_FINDER_C09 | dup-confirm | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:90 | UI only |
| DUPLICATE_FINDER_C10 | dup-review | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:91 | UI only |
| DUP_VERIFIED_HASHING | dup-scan | PRO | REGISTERED, REACHABLE | ServiceCatalog150.kt:92 | UI only |
| DUP_KEEPER_PROTECTION | dup-keeper | PRO | REGISTERED, REACHABLE | ServiceCatalog150.kt:93 | UI only |
| DUP_SMART_SIMILARITY | dup-compare | PRO | REGISTERED, REACHABLE | ServiceCatalog150.kt:94 | UI only |
| DUP_BATCH_REVIEW | dup-review | PRO | REGISTERED, REACHABLE | ServiceCatalog150.kt:95 | UI only |
| DUP_RECOVERY_JOURNAL | dup-confirm | PRO | REGISTERED, REACHABLE | ServiceCatalog150.kt:96 | UI only |

---

## Service Family 3: app_residue (M03)

| Service ID | Route | Type | Classification | Source File | Status |
|---|---|---|---|---|---|
| APP_RESIDUE_C01 | res-sources | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:101 | UI only |
| APP_RESIDUE_C02 | res-scan | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:102 | UI only |
| APP_RESIDUE_C03 | res-candidates | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:103 | UI only |
| APP_RESIDUE_C04 | res-groups | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:104 | UI only |
| APP_RESIDUE_C05 | res-detail | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:105 | UI only |
| APP_RESIDUE_C06 | res-detail | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:106 | UI only |
| APP_RESIDUE_C07 | res-exclusions | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:107 | UI only |
| APP_RESIDUE_C08 | res-risk | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:108 | UI only |
| APP_RESIDUE_C09 | res-confirm | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:109 | UI only |
| APP_RESIDUE_C10 | res-history | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:110 | UI only |
| RESIDUE_OWN_CACHE | res-sources | PRO | REGISTERED, REACHABLE | ServiceCatalog150.kt:111 | UI only |
| RESIDUE_ACCESSIBLE_CANDIDATES | null | PRO | REGISTERED (blocked: screen not built) | ServiceCatalog150.kt:112 | PLACEHOLDER |
| RESIDUE_SAF_PROFILES | null | PRO | REGISTERED (blocked: SAF management not built) | ServiceCatalog150.kt:113 | PLACEHOLDER |
| RESIDUE_SAFE_TRASH | null | PRO | REGISTERED (blocked: safe trash not built) | ServiceCatalog150.kt:114 | PLACEHOLDER |
| RESIDUE_HISTORY | null | PRO | REGISTERED (blocked: residue history not built) | ServiceCatalog150.kt:115 | PLACEHOLDER |

---

## Service Family 4: privacy_sweep (M04)

| Service ID | Route | Type | Classification | Source File | Status |
|---|---|---|---|---|---|
| PRIVACY_SWEEP_C01 | prv-overview | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:120 | UI only |
| PRIVACY_SWEEP_C02 | prv-permissions | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:121 | UI only |
| PRIVACY_SWEEP_C03 | prv-ranking | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:122 | UI only |
| PRIVACY_SWEEP_C04 | prv-overview | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:123 | UI only |
| PRIVACY_SWEEP_C05 | prv-app-risk | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:124 | UI only |
| PRIVACY_SWEEP_C06 | prv-modules | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:125 | UI only |
| PRIVACY_SWEEP_C07 | prv-settings | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:126 | UI only |
| PRIVACY_SWEEP_C08 | prv-results | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:127 | UI only |
| PRIVACY_SWEEP_C09 | prv-access | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:128 | UI only |
| PRIVACY_SWEEP_C10 | prv-results | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:129 | UI only |
| PRV_REALTIME | null | PRO | REGISTERED (blocked: VpnService not available) | ServiceCatalog150.kt:130 | BLOCKED |
| PRV_VAULT | null | PRO | REGISTERED (blocked: encrypted vault not built) | ServiceCatalog150.kt:131 | BLOCKED |
| PRV_TRACKER | null | PRO | REGISTERED (blocked: VpnService not available) | ServiceCatalog150.kt:132 | BLOCKED |
| PRV_REPORT | null | PRO | REGISTERED (blocked: privacy report screen not built) | ServiceCatalog150.kt:133 | BLOCKED |
| PRV_HISTORY | null | PRO | REGISTERED (blocked: privacy history not built) | ServiceCatalog150.kt:134 | BLOCKED |

---

## Service Family 5: app_manager (M05)

| Service ID | Route | Type | Classification | Source File | Status |
|---|---|---|---|---|---|
| APP_MANAGER_C01 | apm-installed | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:139 | UI only |
| APP_MANAGER_C02 | apm-info | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:140 | UI only |
| APP_MANAGER_C03 | apm-search | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:141 | UI only |
| APP_MANAGER_C04 | apm-system-user | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:142 | UI only |
| APP_MANAGER_C05 | apm-filters | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:143 | UI only |
| APP_MANAGER_C06 | apm-launch | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:144 | UI only |
| APP_MANAGER_C07 | apm-settings | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:145 | UI only |
| APP_MANAGER_C08 | apm-uninstall | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:146 | UI only |
| APP_MANAGER_C09 | apm-uninstall | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:147 | UI only |
| APP_MANAGER_C10 | apm-sorting | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:148 | UI only |
| APP_BATCH | null | PRO | REGISTERED (blocked: batch processing not built) | ServiceCatalog150.kt:149 | BLOCKED |
| APP_FREEZE | null | PRO | REGISTERED (blocked: requires Device Owner/Root) | ServiceCatalog150.kt:150 | BLOCKED |
| APP_BACKUP | null | PRO | REGISTERED (blocked: other app data backup not supported) | ServiceCatalog150.kt:151 | BLOCKED |
| APP_USAGE | null | PRO | REGISTERED (blocked: UsageStats requires special permission) | ServiceCatalog150.kt:152 | BLOCKED |
| APP_CLONE | null | PRO | REGISTERED (blocked: app cloning not officially supported) | ServiceCatalog150.kt:153 | BLOCKED |

---

## Service Family 6: battery_saver (M06)

| Service ID | Route | Type | Classification | Source File | Status |
|---|---|---|---|---|---|
| BATTERY_SAVER_C01 | bat-level | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:158 | UI only |
| BATTERY_SAVER_C02 | bat-charge | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:159 | UI only |
| BATTERY_SAVER_C03 | bat-temperature | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:160 | UI only |
| BATTERY_SAVER_C04 | bat-source | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:161 | UI only |
| BATTERY_SAVER_C05 | bat-info | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:162 | UI only |
| BATTERY_SAVER_C06 | bat-readings | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:163 | UI only |
| BATTERY_SAVER_C07 | bat-alerts | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:164 | UI only |
| BATTERY_SAVER_C08 | bat-saving | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:165 | UI only |
| BATTERY_SAVER_C09 | bat-summary | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:166 | UI only |
| BATTERY_SAVER_C10 | bat-summary | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:167 | UI only |
| BAT_THERMAL_TIMELINE | null | PRO | REGISTERED (blocked: thermal timeline not built) | ServiceCatalog150.kt:168 | BLOCKED |
| BAT_CHARGE_PROFILES | null | PRO | REGISTERED (blocked: charge profiles not built) | ServiceCatalog150.kt:169 | BLOCKED |
| BAT_USAGE_CONTEXT | null | PRO | REGISTERED (blocked: UsageStats permission) | ServiceCatalog150.kt:170 | BLOCKED |
| BAT_SMART_ALERTS | null | PRO | REGISTERED (blocked: smart alerts not built) | ServiceCatalog150.kt:171 | BLOCKED |
| BAT_RECOMMENDATIONS | null | PRO | REGISTERED (blocked: battery recommendations not built) | ServiceCatalog150.kt:172 | BLOCKED |

---

## Service Family 7: deep_scan (M07)

| Service ID | Route | Type | Classification | Source File | Status |
|---|---|---|---|---|---|
| DEEP_SCAN_C01 | dsn-type | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:177 | UI only |
| DEEP_SCAN_C02 | dsn-scope | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:178 | UI only |
| DEEP_SCAN_C03 | dsn-start | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:179 | UI only |

*(Deep Scan family continues beyond line 179 — full catalog has 10 core + 5 PRO)*

---

## Service Family 8: smart_clean (M08)

| Service ID | Route | Type | Classification | Source File | Status |
|---|---|---|---|---|---|
| SMART_CLEAN_C01 | clean-scope | CORE | REGISTERED, REACHABLE, FUNCTIONALLY_TESTED | ServiceCatalog150.kt:196 | Device verified |
| SMART_CLEAN_C02 | clean-scan | CORE | REGISTERED, REACHABLE, FUNCTIONALLY_TESTED | ServiceCatalog150.kt:197 | Device verified |
| SMART_CLEAN_C03 | clean-classify | CORE | REGISTERED, REACHABLE, FUNCTIONALLY_TESTED | ServiceCatalog150.kt:198 | Device verified |
| SMART_CLEAN_C04 | clean-filters | CORE | REGISTERED, REACHABLE, FUNCTIONALLY_TESTED | ServiceCatalog150.kt:199 | Device verified |
| SMART_CLEAN_C05 | clean-preview | CORE | REGISTERED, REACHABLE, FUNCTIONALLY_TESTED | ServiceCatalog150.kt:200 | Device verified |
| SMART_CLEAN_C06 | clean-protected | CORE | REGISTERED, REACHABLE, FUNCTIONALLY_TESTED | ServiceCatalog150.kt:201 | Device verified |
| SMART_CLEAN_C07 | clean-selection | CORE | REGISTERED, REACHABLE, FUNCTIONALLY_TESTED | ServiceCatalog150.kt:202 | Device verified |
| SMART_CLEAN_C08 | clean-impact | CORE | REGISTERED, REACHABLE, FUNCTIONALLY_TESTED | ServiceCatalog150.kt:203 | Device verified |
| SMART_CLEAN_C09 | clean-confirm | CORE | REGISTERED, REACHABLE, FUNCTIONALLY_TESTED | ServiceCatalog150.kt:204 | Device verified |
| SMART_CLEAN_C10 | clean-history | CORE | REGISTERED, REACHABLE, FUNCTIONALLY_TESTED | ServiceCatalog150.kt:205 | Device verified |
| SMART_CLEAN_UNLIMITED | clean-large | PRO | REGISTERED, REACHABLE | ServiceCatalog150.kt:206 | UI only |
| SMART_CLEAN_SCHEDULED | null | PRO | REGISTERED (blocked: WorkManager not wired) | ServiceCatalog150.kt:207 | BLOCKED |
| SMART_CLEAN_DEEP_ANALYSIS | clean-classify | PRO | REGISTERED, REACHABLE | ServiceCatalog150.kt:208 | UI only |
| SMART_CLEAN_PRIORITY | clean-free-plan | PRO | REGISTERED, REACHABLE | ServiceCatalog150.kt:209 | UI only |
| SMART_CLEAN_ROLLBACK | clean-history | PRO | REGISTERED, REACHABLE | ServiceCatalog150.kt:210 | UI only |

---

## Service Family 9: reports (M09)

| Service ID | Route | Type | Classification | Source File | Status |
|---|---|---|---|---|---|
| REPORTS_C01 | rpt-range | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:215 | UI only |
| REPORTS_C02 | rpt-operation | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:216 | UI only |
| REPORTS_C03 | rpt-empty | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:217 | UI only |
| REPORTS_C04 | rpt-cleanup | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:218 | UI only |
| REPORTS_C05 | rpt-storage | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:219 | UI only |
| REPORTS_C06 | rpt-operation | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:220 | UI only |
| REPORTS_C07 | rpt-device | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:221 | UI only |
| REPORTS_C08 | rpt-range | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:222 | UI only |
| REPORTS_C09 | rpt-operation | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:223 | UI only |
| REPORTS_C10 | rpt-trends | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:224 | UI only |
| REP_EXPORT | null | PRO | REGISTERED (blocked: export not built) | ServiceCatalog150.kt:225 | BLOCKED |
| REP_HISTORY | null | PRO | REGISTERED (blocked: report history not built) | ServiceCatalog150.kt:226 | BLOCKED |
| REP_AI_INSIGHTS | ai-recommendations | PRO | REGISTERED, REACHABLE | ServiceCatalog150.kt:227 | UI only |
| REP_COMPARE | rpt-range | PRO | REGISTERED, REACHABLE | ServiceCatalog150.kt:228 | UI only |
| REP_SCHEDULE | null | PRO | REGISTERED (blocked: scheduling not wired to WorkManager) | ServiceCatalog150.kt:229 | BLOCKED |

---

## Service Family 10: ai_optimization (M10)

| Service ID | Route | Type | Classification | Source File | Status |
|---|---|---|---|---|---|
| AI_OPTIMIZATION_C01 | ai-storage | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:234 | UI only |
| AI_OPTIMIZATION_C02 | ai-confidence | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:235 | UI only |
| AI_OPTIMIZATION_C03 | ai-recommendations | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:236 | UI only |
| AI_OPTIMIZATION_C04 | ai-confidence | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:237 | UI only |
| AI_OPTIMIZATION_C05 | ai-goal | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:238 | UI only |
| AI_OPTIMIZATION_C06 | ai-customize | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:239 | UI only |
| AI_OPTIMIZATION_C07 | ai-reasons | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:240 | UI only |
| AI_OPTIMIZATION_C08 | ai-execute | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:241 | UI only |
| AI_OPTIMIZATION_C09 | ai-results | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:242 | UI only |
| AI_OPTIMIZATION_C10 | ai-battery | CORE | REGISTERED, REACHABLE | ServiceCatalog150.kt:243 | UI only |
| OPT_GAMING | ai-goal | PRO | REGISTERED, REACHABLE | ServiceCatalog150.kt:244 | UI only |
| OPT_AI_LEARN | ai-recommendations | PRO | REGISTERED, REACHABLE | ServiceCatalog150.kt:245 | UI only |
| OPT_THERMAL | ai-thermal | PRO | REGISTERED, REACHABLE | ServiceCatalog150.kt:246 | UI only |
| OPT_NETWORK | ai-confidence | PRO | REGISTERED, REACHABLE | ServiceCatalog150.kt:247 | UI only |
| OPT_SCENE | ai-customize | PRO | REGISTERED, REACHABLE | ServiceCatalog150.kt:248 | UI only |

---

## Summary Counts

| Family | Core | PRO | Total |
|---|---|---|---|
| storage_analyzer | 10 | 5 | 15 |
| duplicate_finder | 10 | 5 | 15 |
| app_residue | 10 | 5 | 15 |
| privacy_sweep | 10 | 5 | 15 |
| app_manager | 10 | 5 | 15 |
| battery_saver | 10 | 5 | 15 |
| deep_scan | 10 | 5 | 15 |
| smart_clean | 10 | 5 | 15 |
| reports | 10 | 5 | 15 |
| ai_optimization | 10 | 5 | 15 |
| **TOTAL** | **100** | **50** | **150** |

**Verification status**: All 100 core services are REGISTERED + REACHABLE (routes defined in ServiceCatalog150.kt). 10 smart_clean services are FUNCTIONALLY_TESTED on Samsung SM-A025F. Remaining 90 core services are UI-only (no production execution logic verified). All 50 PRO services are REGISTERED but most are BLOCKED or UI-only (no production execution).

**Additional service catalogs**: ToolCatalogV2.kt, ToolCatalogExpansionV2.kt, SectionToolsV2.kt contain additional service definitions requiring separate inventory.
