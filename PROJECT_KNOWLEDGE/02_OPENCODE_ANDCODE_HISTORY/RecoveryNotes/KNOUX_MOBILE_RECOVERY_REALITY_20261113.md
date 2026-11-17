# KNOUX AI Mobile — Emergency Recovery Reality Report
Date: 2026-11-13 (UAE)
Authorized device: Android 15 phone connected through ADB.
Scope: historical mobile OpenCode project, NOT the independent Windows D:\KNOUX-AI.

## VERIFIED AND PRESERVED TODAY
- Historical full source archive from 2026-11-11, 13:27:41 UTC.
- Verified original phone archive SHA-256: df944bfa4a1ef14a92f8580354c3ba0dcd04a5595d7952590fe2a2c301c164c5
- Verified byte-identical Windows COPY: K:\KNOUX-Phone-Exports\RECOVERY_20261113\KNOUX-AI-export-20261111-132741.VERIFIED.tar.gz
- Exact size: 480,861,731 bytes; 2,933 TAR entries, includes KNOUX-AI/.git/HEAD, Gradle files and 33 .kt files; archive table readable.
- Phone original remains at Download/OpenCode/_knoux_snapshots-20261111-210018/_knoux_snapshots/KNOUX-AI-export-20261111-132741.tar.gz.
- Earlier extracted historical repo: Download/OpenCode/_knoux_snapshots-20261111-210018/KNOUX-AI with HEAD cffe7b4f7cc1de4beb5539202dbcf067de19741e.
- Compilation output APK from 2026-11-13 13:45 survives at Download/OpenCode/app-debug.apk. APK is NOT editable Kotlin source.
- Earlier shared-storage tar OpenCode_shared.tar exists at Download/KNOUX_AppGetOpenCode_Export_20261113_1325; its prior manifest describes Nov 11 work and explicitly excludes app-private data.
- KNOUX-AI-main.zip (downloaded 2026-11-13 ~21:46) is a GitHub/Windows-source ZIP, NOT proof of current live mobile source.

## NOT FOUND / NOT VERIFIED
- Claimed Nov 13 full mobile source export KNOUX_AI_LATEST_FULL_SOURCE.tar.gz (~496MB) and KNOUX_AI_QA_EVIDENCE.tar.gz (~480MB) do NOT appear within accessible shared storage.
- Existing Download/OpenCode/KNOUX_AndCode_Migration_20261113 holds handoff text, screenshot and APK, not those newer source archives.
- Existing private GitHub repo daynightae-cmyk/KNOUX-AI-Pro: empty (repo size 0 at audit).
- App package app.getopencode (old Android OpenCode 1.0.146) not currently installed for user 0; ordinary ADB cannot read its previous /data/user/0 app sandbox.
- Missing M08/M09 Kotlin working tree and untracked files should NOT be marked recovered until actually found in another still-existing workspace/backup.
- Failure to find in accessible shared storage is NOT evidence that every inaccessible app-private snapshot is gone; access requires proper app-native export or backup.

## SAFE ACTIONS ONLY
1. Do NOT clear data/uninstall remaining AndCode, ai.sol.opencode, Termux or other tools.
2. Do NOT factory reset or run storage cleaners. Do NOT run git clean/reset --hard or overwrite any source.
3. Check inside the running AndCode/OpenCode runtime itself for existing projects/workspaces and /root/workspace/KNOUX-AI. If found, inspect Git HEAD, status, worktrees, stash, untracked and export full source to a NEW external directory, then calculate SHA-256.
4. If no live source exists, restore the VERIFIED Nov 11 archive into a NEW EMPTY workspace; preserve original phone archive and Windows copy; use Nov 13 APK, designs, project context and historical logs to reconstruct later phases without claiming identical source.
5. Do not mix D:\KNOUX-AI Windows repo or Android KNOUX-AI-Pro without an explicit, verified reconciliation.
6. Exposed LAN file-sharing credential in a screenshot: disable sharing when unused and change LAN password.

## TRANSFER SAFETY
- USB failed during first large ADB pull; that incomplete copy is NOT a backup.
- A chunked, resumable, READ-ONLY ADB transfer then copied all 480,861,731 bytes to K: and independently matched SHA-256.
- No deletions, reset, formatting, overwriting existing project code, Git pushes or package uninstalls were performed during this recovery.
