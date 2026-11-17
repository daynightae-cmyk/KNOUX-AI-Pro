# KNOUX AI - AndCode phone migration handoff (2026-11-13)
STATUS: STAGING ONLY — NOT THE LIVE SOURCE WORKSPACE.
Phone: itel S686LN; AndCode 1.2.29 installed; OpenCode Mobile 1.0.146 still installed.
Goal: resume the same KNOUX AI mobile source from /root/workspace/KNOUX-AI in AndCode, preserving .git and unique work.
THE CURRENT SOURCE FROM /root/workspace/KNOUX-AI HAS NOT BEEN EXPORTED INTO THIS FOLDER.
Historical ZIP/TAR snapshots from 2026-11-11 under Download/OpenCode are not the current source.
The app-debug.apk in this folder is the verified 2026-11-13 13:45 debug APK, not proof that current later agent changes are included.

NEXT: In the ORIGINAL OpenCode agent session running against /root/workspace/KNOUX-AI:
1. Freeze only writes in that canonical workspace after finishing the current safe action; do not stop/kill agents blindly.
2. Record git root, HEAD, branch, status (including untracked), worktrees/stashes and any running Gradle.
3. Export complete current source, including .git and unique assets, to timestamped private TAR/GZIP; write SHA-256 and before/after source manifest.
4. Export actual latest built APK, test XML, build logs and real Android QA screenshots separately.
5. Transfer into THIS STAGING folder using authorized app file export / phone bridge, not as plaintext chat attachments if containing secrets.
6. Verify archive sha256 and expected files before importing, never overwrite an existing repo.
7. If source changed while archiving, label INCONSISTENT and repeat when writers are quiescent.
8. Do not disclose API keys, tokens, OAuth secrets or private .env files; migrate provider settings via secure account login rather than copying secret stores.
9. Import verified source in AndCode using Workspaces > device storage > Import folder (wording may vary).
10. Choose the resulting KNOUX-AI folder only after git rev-parse and git status pass, and avoid concurrent writes by the old agent.

AndCode reminder: 'OpenCode - local' in Workspaces is an AGENT RUNTIME, not evidence that the Git repository or chat history has migrated.
AndCode is NOT an independent coding model; it is a GUI for agent runtimes, subject to providers' rate limits.
Android Shizuku/RISH and ADB are separate; do not automatically grant shell-level privileges to AI agents.
Protect old app: DO NOT uninstall app.getopencode, clear data, or delete Downloads/OpenCode before independent verification.
Acceptance: HEAD, complete tracked+untracked tree, source asset inventory, screenshots, latest APK, tests and resumed agent tasks all verified.
