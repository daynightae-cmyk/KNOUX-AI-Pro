# KNOUX AI Pro — Visual Reality Mission 20261123 — STATUS

## Forensic reconciliation (2026-11-23, read-only, before any edit)

- Repo: `K:\KNOUX-AI-Pro` (canonical), branch `master`
- HEAD = `230d30db9dbd83ddb4f857bea05ad0697d2fd660`; `git ls-remote origin refs/heads/master` = same → local == remote, 0 ahead/behind. No `origin/main` exists; `master` is the only merge branch.
- `git status --porcelain=v1 -uall`: only untracked file `prompts/KNOUX_AI_PRO_DEVICE_FIRST_VISUAL_REALITY_MISSION_20261123.md` (read, honored, to be committed with first proper change).
- Branches: only `master` (+ origin). Worktrees: only the main one. Stash: empty. Reflog top: `230d30d commit: Home fidelity + all 10 section hubs rebuilt as real Compose against approved references`.
- No java/gradle processes running at intake; `.gradle` locks are idle file locks (last write 23/11 00:43) → no concurrent Batch was active at check time. Will re-check before each build.
- Device: `adb devices` → `R9ZR20HWHQY  device  model:SM-A025F  product:a02qnnxx` (Samsung SM-A025F, Android 12) — **CONNECTED**.
- Installed package: `com.knoux.ai.debug` versionName `1.0.0-debug` versionCode 1, minSdk 26 targetSdk 36, lastUpdateTime 2026-11-23 00:44:12 (Arabic locale timestamp). APK path: `/data/app/~~Cce2jEmhJNZ2VSTO9w0TFg==/com.knoux.ai.debug-PahtLqsmFQqwBqVvyveO9g==/base.apk`. `com.knoux.ai` (release) NOT installed.
- Reference intake: owner archive found locally at `D:\Temp\opencode\uploads\cb72d6d0-5032-4393-b6d0-e3e2846eba51\Knoux ai pro.zip`; SHA-256 `1ca30c93…526a05f` **matches owner-declared fingerprint**. 22 entries / 20 unique SHA-256. Organized into `design/owner_refs_20261123/` + `MANIFEST.md`. Index 00 (neon stag) is byte-identical to project-owned `_references/annotated_20261117/assets/STAG_MASCOT_TRANSPARENT.png` → not re-imported. 05≡02 and 15≡14 → not imported.
- Route map (from `MainActivity.kt`): entry Splash→Loading→Onboarding→Auth→Shell. Shell tabs: `home`, `clean`, `tools`, `reports`, `pro` (+`settings`). Dedicated sections: `privacy`, `duplicates`, `deepscan`, `residue`, `appmanager`, `installed-apps`, `storage`, `battery`, `ai`, `smartclean`. Details: `app-detail`, `privacy-risk`, `duplicate-group`, `deep-scan-summary`, `performance-report`, `residue-item`, `storage-breakdown`, `battery-diagnostics`, `bat-readings`, `ai-recommendations`, `large-files`, `old-backups`, `system-logs`, `download-cleanup`, `file-explorer`, `device-info`, `network-scan`, `security-scan`, `notification-manager`, `developer-info`, `google-services`, `advanced-security`, `backup-restore`, `developer-studio`, `dev-provider-marketplace`, `dev-api-key-vault`, `dev-model-explorer`, `dev-agent-engine`, `dev-workspace`, `dev-tool-system`, `cloud-cleaner`, `google-storage`, `microsoft-storage`. Plus 10 `SectionHub.routes` each with ~15 `SectionTools` routes and 15 `CleanTools.routes`. Debug route injection via `--es route <route>` (BuildConfig.DEBUG only).
- Prior QA: `qa-evidence/refs_20261121/device_verify_final_20261122.md` verified Samsung SM-A025F on an older SHA; Google OAuth in debug failed due to unregistered `com.knoux.ai.debug` + debug SHA-1 in Google Console (known, not re-tested as pass).

## Plan of execution (one screen per cycle, evidence per cycle)

1. Auth/Home entry states vs `owner_refs_20261123` (only real gaps).
2. `home` vs `home/pro_home_dashboard.png`.
3. The 10 service screens from the new set: `smartclean`, `deepscan`, `duplicates`, `storage`, `privacy`, `battery`, `appmanager`, `reports`, `ai`, developer hub → `developer-studio` (+ `dev-*` states).
4. Remaining inner routes by nav order; full regression at the end.

Ledger: `docs/qa/pro_visual_20261123/SCREEN_LEDGER.md` (created on first screen cycle).
