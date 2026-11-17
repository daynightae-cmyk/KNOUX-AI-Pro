# KNOUX AI MOBILE — COMPLETE FORENSIC RECOVERY & SINGLE DOWNLOADS WORKSPACE
OWNER EXECUTION CONTRACT | 2026-11-13 | ONE-SHOT | NO DESTRUCTIVE COMMANDS

## OWNER GOAL
EXECUTE, not planning only. Connect from the authorized Windows computer to the actual Android phone using ADB. Investigate ALL existing KNOUX AI MOBILE workspaces, archives, Git trees, untracked assets, images, screens, original designs, APKs and QA logs. Identify the newest authentic Android mobile source by Git ancestry, individual file hashes, timestamps + provenance, and actual build/test evidence. Safely reconcile unique and compatible mobile files; preserve every conflict as a named candidate and do not casually overwrite. Present ONE canonical, organized parent folder under Android Downloads for the actual latest recovered mobile project. Separate it completely from the independently developed Windows app. Do not invent missing work or claim 100% without physical proof.

OUTPUT (REAL ANDROID SHARED STORAGE):
/storage/emulated/0/Download/KNOUX_AI_MOBILE_CANONICAL_RECOVERY_20261113/

Required internal structure:
00_AUDIT/
01_ORIGINALS_INDEX/
02_CANDIDATES/
03_CANONICAL_SOURCE/KNOUX-AI/
04_UNIQUE_UNMERGED/
05_VISUAL_ASSETS/
06_QA_EVIDENCE/
07_APK/
08_REPORTS_HANDOFF/

One parent folder, not multiple scattered "final-final" folders. Sources and huge archives already present elsewhere must remain in their ORIGINAL locations and may be referenced by full path + SHA manifest rather than copied twice. Keep recovery staging isolated; do not create a canonical source unless it has been carefully selected and validated.

## STRICT PROJECT IDENTITY — NEVER CONFUSE THE TWO LANES

MOBILE PRODUCT TO RECOVER:
Original Linux/OpenCode mobile development workspace: /root/workspace/KNOUX-AI
Phone app: com.knoux.ai.debug
Previous mobile agent REPORTED Git HEAD 64aeb002b1ab5d1da42ac7f0787bbd2318192a4b on master, at M09.DATA Reports. This is an UNVERIFIED REPORT, NOT a recovered commit. Prove by actual Git object and source evidence.
Separate PRIVATE repository intended for this lane: https://github.com/daynightae-cmyk/KNOUX-AI-Pro.git. Last verified empty (size 0). Inspect again; do not treat an empty repo as backup.
Old phone app app.getopencode was no longer installed in Android user 0 at last check; its private /data/user/0/app.getopencode is NOT readable via unprivileged adb. Never claim it is accessible. Do not root, bypass Android isolation or reinstall app to overwrite data.

INDEPENDENT WINDOWS PROJECT — READ ONLY FOR CLASSIFICATION:
D:\KNOUX-AI, public GitHub daynightae-cmyk/KNOUX-AI, entirely different active PC00–PC13 work. Its Git HEAD can change and it has unique dirty files. NEVER reset, copy, merge, amend, push, checkout, modify, clean, delete or use it to recreate mobile source. The phone ZIP Download/KNOUX-AI-main.zip is classified as PC/Windows repository content until proven otherwise and must NOT become mobile baseline.
Other out-of-scope products: KNOUX X, Crypt, VPN, GrabFlow, Basma and all personal user media.

## DEVICE / CONNECTION FACTS — REVALIDATE
Authorized Windows host: DESKTOP-VQKU9P8.
Last verified USB serial: 1334825549012211.
Phone: itel S686LN, Android 15 API 35.
Phone installed apps: com.yugahashimoto.andcode, ai.sol.opencode, com.termux, com.knoux.ai.debug.
Last observed Wireless ADB mDNS _adb-tls-connect._tcp endpoints: [REDACTED_NETWORK_ENDPOINT] and [REDACTED_NETWORK_ENDPOINT]. These ports are DYNAMIC. Use discovery, never assume either is live.
Phone last shared-storage free space ~131GB. Windows D: and K: have VERY LITTLE free space; check now before any copying. Never place multi-GB extraction on D:\KNOUX-AI.

Run READ ONLY on Windows before doing anything:
adb devices -l
adb mdns services
adb -s 1334825549012211 shell getprop ro.product.model
adb -s 1334825549012211 shell getprop ro.build.version.sdk
adb -s 1334825549012211 shell pm list packages --user 0
adb -s 1334825549012211 shell df -h /storage/emulated/0
adb -s 1334825549012211 shell ls -lah /storage/emulated/0/Download/OpenCode

Prefer authorized USB. If it drops, discover the latest paired wireless mDNS endpoint, adb connect to its CURRENT IP:port and VERIFY getprop after reconnect. Retry boundedly; do not restart adb server if other agents use it. Never ask for pairing if an authorized existing transport works. Every shell command MUST target the explicitly verified serial. If the OpenCode agent is on a separate cloud container, detect that fact and use the authorized Windows host/ADB bridge: [REDACTED_NETWORK_ENDPOINT] is a LAN IP, not publicly accessible.

## SOURCE INVENTORY — REPORTED PHYSICAL FILES
1. Historical source: Download/OpenCode/_knoux_snapshots-20261111-210018/KNOUX-AI, with .git and previous HEAD cffe7b4f7cc1de4beb5539202dbcf067de19741e. Independently inspect git fsck, refs, stash, untracked and unique design files.
2. Historical export: Download/OpenCode/_knoux_snapshots-20261111-210018/_knoux_snapshots/KNOUX-AI-export-20261111-132741.tar.gz; exactly 480861731 bytes; verified SHA-256 df944bfa4a1ef14a92f8580354c3ba0dcd04a5595d7952590fe2a2c301c164c5, 2933 TAR entries including .git/HEAD and 33 Kotlin files. It has unique untracked files and may have secrets. It is NOT the latest Nov13 source.
3. Independent Windows SHA-VERIFIED safety copy: K:\KNOUX-Phone-Exports\RECOVERY_20261113\KNOUX-AI-export-20261111-132741.VERIFIED.tar.gz. NEVER delete this copy or overwrite it.
4. Older similarly named snapshots and incomplete .part inside Download/OpenCode/_knoux_snapshots-20261111-210018/_knoux_snapshots; archive ZIPs Download/OpenCode/_knoux_snapshots-20261111-210018.zip (~2.3GB) and KNOUX-AI-export-20261111-123056.manifest-20261111-210304.zip (~1.6GB). Do not assume they're byte-for-byte duplicates until hash comparison. Do not treat incomplete .part as a complete archive.
5. Download/KNOUX_AppGetOpenCode_Export_20261113_1325/OpenCode_shared.tar (~6.8GB): shared-storage backup, NOT private app data or live /root/workspace source. Its manifest explicitly says protected app sandbox was NOT accessed.
6. Download/OpenCode/app-debug.apk, 35386564 bytes, SHA-256 cf3e3c785b662c89663f9c862b5a13ffe70c21b16238880a1ed68f236f813e76; M08 runtime PNGs timestamped Nov13. APK is built binary; do not pretend it contains editable original Kotlin project.
7. Download/OpenCode/KNOUX_AndCode_Migration_20261113: staging instructions, old APK, screenshot and recovery report. A previous agent CLAIMED that newer KNOUX_AI_LATEST_FULL_SOURCE.tar.gz (~496MB) and KNOUX_AI_QA_EVIDENCE.tar.gz (~480MB) were delivered there, but repeated genuine ADB searches DID NOT FIND them. Search again inside all accessible sources, not just this directory; do not report FOUND until stat+hash prove it.
8. Download/KNOUX-AI-main.zip (~1.06GB, Nov13): probably independent Windows repo; classify separately.
9. Original KNOUX mobile artwork: Download/knoux ai reference, Download/KNOUX_AI_10_UI_References, Splash ZIPs, onboarding/Founder/brand reference folders; inventory and visually compare. Never replace approved logo proportions, colors, gradients or approved motion.
10. Android App private workspaces belonging to AndCode and ai.sol.opencode, as accessible through THEIR OWN AUTHORIZED UI/terminal or user-accessible exports. Ordinary ADB cannot read their private /data/data by default. Do NOT claim inaccessible state has been inspected.
11. All other relevant KNOUX-AI-specific ZIP/TAR/MD/JSON/PNG under Downloads/Documents/shared media and authorized local Windows handoff folder. Exclude non-mobile products and personal secrets.

Treat the original mobile source SHA and M09 state as UNRECOVERED until evidence exists. Never rank freshness solely from ZIP filename, modification time, agent PASS claim or APK build date.


## PHASE A — NON-DESTRUCTIVE REALITY AUDIT (DO THIS FIRST)
1. Check exact phone model, ADB serial, allowed user, installed packages, current shared-storage mount, free space, Windows drive space, concurrent writer processes, and all source/target paths. Capture commands and exit codes in 00_AUDIT/DEVICE_REALITY.txt (redact phone identifiers if publishing anywhere).
2. Inspect AndCode and ai.sol.opencode existing local runtime/Workspace via authorized app UI or own export interface FIRST. Search for /root/workspace/KNOUX-AI and related actual Android Linux roots; verify whether any current source survives INSIDE the existing current app. Do NOT uninstall, clear data, reinstall, migrate app data, or assume an app-private folder is readable. If native access is impossible, record APP_PRIVATE_NOT_ACCESSIBLE and do not fake completion.
3. Inventory every likely KNOUX AI MOBILE archive/source tree under accessible shared storage. Record absolute path, byte size, filename, modification time, package/purpose classification, top-level archive entries, nesting depth, expected SHA manifests, measured SHA-256, file-count, and any archive damage. Log each candidate in 00_AUDIT/CANDIDATES.json and INDEX.md. Only download/parse archives actually relevant to KNOUX AI mobile; skip unrelated apps and personal data.
4. Use streaming archive listing and integrity operations. For TAR: list/verify without extraction. For ZIP: validate central directory and check CRC where feasible. Be alert for ZIP bombs, path traversal, nested payloads, symlinks escaping staging, damaged .part files, fake .git folders, secrets, and overlapping files. Stage extraction only in uniquely named empty directories BELOW 02_CANDIDATES, after checking available space and validating all members.
5. Check any Git tree: .git exists AND has valid HEAD, refs and objects. Inspect git log --all, branch -avv, worktree list, reflog, stash list, status --porcelain=v1 -uall, fsck and git remote -v. The old snapshot may contain hundreds of untracked files, don't ignore them. If cannot trust a Git tree from shared storage due permissions, mirror its immutable files to safe staging and inspect there, without modifying original Git objects.
6. Compare the actual Git graphs for candidates. A SHA in agent text means nothing unless actual .git can resolve it. For every proposed "latest": verify Git HEAD, date, commit ancestry, tracked file hashes, untracked uniques and generated Kotlin build structure. A commit with earlier timestamp can contain later unique work; preserve all divergent content.
7. Build an evidence matrix with states FOUND_VERIFIED, FOUND_OLDER, SOURCE_MISSING, DUPLICATE_IDENTICAL_SHA, UNIQUE_UNMERGED, UNTRUSTED_ARCHIVE, SECRET_QUARANTINED, NOT_ACCESSIBLE, and BROKEN_ARCHIVE. File timestamps and hashes are not interchangeable. NEVER auto-delete identical files in originals.

## PHASE B — CREATE A FULL CONTENT PROVENANCE MAP
Before any merge, generate:
- 00_AUDIT/ARCHIVES_AND_GIT_SHA_MATRIX.csv: candidate, path, source-origin MOBILE/PC/UNKNOWN, archive SHA, root Git SHA, branch, Git object completeness, modified/untracked counts and content version.
- 00_AUDIT/ALL_UNIQUE_FILE_HASHES.csv: relative file path, size, SHA-256, candidate source, ownership (mobile/PC), Git tracked state, unique-or-duplicate reason.
- 00_AUDIT/FEATURE_COVERAGE.md: M00–M15 and 50 Pro capabilities against REAL Kotlin implementation, tests, screenshots, permissions, source file, Git SHA and status. Preserve unknowns as UNKNOWN.
- 00_AUDIT/VISUAL_REFERENCES_MAP.md: all current original logo, Splash smile, onboarding 3 scenes, approved audio/offline resource, Login, OTP, Google Sign-in, Passkeys, biometric, Home, Clean, Tools, Reports, Deep Scan, Duplicates, Residue, Privacy, Battery, Storage, Apps, AI and Pro, with exact original image SHA-256. No unauthorized design replacement.
- 00_AUDIT/CONFLICTS.md with BOTH versions' locations, what differs, which is selected and why, and tests impacted.

For a matching relative path with different bytes, keep both source variants until compared by source and behavioral compatibility. Do NOT blindly overlay by newer file timestamp. Do not apply three-way merges between unrelated histories without inspecting ancestry. Preserve Kotlin implementations, layout resources, assets and Gradle configurations as a coherent set; avoid Frankenstein copies that compile accidentally but break runtime.

## PHASE C — SAFE [REDACTED_DEVICE_SERIAL] INTO ONE ANDROID MOBILE SOURCE
Decision order:
(A) REAL living mobile workspace in AndCode/ai.sol.opencode if it exists, with full Git and untracked content.
(B) A later Nov13 complete source export IF physically discovered with correct hashes and .git/objects, resolving original expected 64aeb002...
(C) Closest valid historical mobile snapshot(s) from Nov11, honoring all unique untracked/modified files, snapshots and graphical references.
(D) Binary APK + QA + notes are evidence/reference ONLY; never turn decompiled APK into claimed original source.
(E) PC/Windows D:\KNOUX-AI or GitHub KNOUX-AI-main.zip are separate data, NOT source donors unless the OWNER explicitly approves a narrow reviewed feature port after lineage analysis.

If two MOBILE branches contain unique functionality, make a staged integration candidate and code-review every changed file; retain upstream files, add missing mobile work in coherent commits, note conflicts; do not erase or replace approved interfaces. Preserve original history and refs. If unique functionality is not mergeable safely, put it in 04_UNIQUE_UNMERGED with manifest and instructions, never throw it away.

The final mobile project should have all required original Compose source, assets, Gradle settings, Kotlin/Room data, feature navigation, screens, test fixtures, supported images, internal docs and actual necessary local files. Place safely only after positive identity/secret checks in:
03_CANONICAL_SOURCE/KNOUX-AI/

If exact original M08/M09 source missing, clearly call output RECOVERED_HISTORICAL_BASELINE + UNIQUE_SURVIVORS, not LATEST_LIVE_SOURCE. Create RECONSTRUCTION_GAPS.md detailing which progress after Nov11 requires restoration or implementation. Do not fabricate M09 Reports source or new commits. Existing separately protected verified PC lane continues untouched.

## PHASE D — VISUAL ASSETS AND SERVICE CONTENT (NO WEB-APP UI)
Gather original KNOUX identity separately by SHA and version, preserving names:
KNOUX parent brand vs KNOUX AI product; owner-approved purple/violet ink/cyan palette; original logo exact aspect ratios; smile Splash; 3 Onboarding chapters and original Founder signature; secure authentication screens; mobile bottom navigation; dynamic clean/deep scan services and 50 Pro catalog.
Only extract artwork if the archive is safely validated. Use deduplication manifest and approved references; NEVER replace originals with generated stock imagery, never modify identities or degrade to a plain web dashboard.

For each real Android service map its current capabilities to selectable native Compose customer UI: Smart Clean/Tide, Duplicates/Mirror with photo thumbnails and protected keeper, Residue/Strata, Privacy/Vault Radar, Deep Scan/Abyss Sonar, Reports/Prism, App Manager/Constellation, Storage Analyzer/Terrain, Battery/Pulse and AI/Synapse. A 70GB scan total is not deletable 70GB. Results must be shown as real selectable file cards/thumbnail grids with allowed Preview/Select/Exclude/Move/Copy/Trash only after user review, OS consent, provider verification and Room audit. If these features do NOT exist in recovered source, mark them MISSING, not magically IMPLEMENTED. This task recovers and reconciles; do not launch a large redesign until canonical source identity and safety are established.

## PHASE E — BUILD / RUNTIME / APK PROOF
First verify source dependencies/Gradle files and absent secrets. Then, only if safe and feasible in the actual environment, run existing Gradle Unit, compile and instrumentation. A cached old 100 PASS report does not prove the current code; collect new XML, exit codes and artifact hashes.
Do not touch, uninstall, clear data or destructively test com.knoux.ai.debug, nor owner photos/videos. Compile safely; use isolated synthetic test fixtures and authorized app install only if explicitly necessary. Real device UI checks should prove auth/navigation/permission handling, persistent Room and photo/video cards, not a mock screenshot. Report if Linux ARM toolchain cannot run native SQLite/Robolectric and Android instrumentation needs an authorized Windows host. Do not claim unit/JVM evidence establishes UI or M09 production readiness.
Classify every feature as VERIFIED_ON_DEVICE, CODE_ONLY_UNVERIFIED, PARTIAL, MISSING, BLOCKED_PERMISSION, BLOCKED_RUNTIME or FAILED. Provide exact paths and command exit evidence.
Keep all APK binaries distinguished by package ID, build variant, signing compatibility, timestamp, installed status and SHA. Never claim APK is a recovered source snapshot.

## PHASE F — SAFE FINAL PACKAGE AND PORTABLE HANDOFF
Inside ONE parent Downloads folder create these precise manifests in 08_REPORTS_HANDOFF:
- README_AR.md — where the canonical source is and how it was proven.
- RECOVERED_VERSUS_MISSING.md — every recovered phase M00–M15 and exact lost/blocked items, no Fake Closure.
- FINAL_SOURCE_SHA256SUMS.txt — actual file hashes and archive hash, no invented numbers.
- GIT_EVIDENCE.md — exact source root, actual HEAD, status and unique unmerged refs, expected vs actual SHA, remote.
- VISUAL_INVENTORY.md — filename and visual role with asset hashes.
- ACCEPTANCE_RESULTS.md — build/Unit/androidTest/device screenshots and failures, not anecdotes.
- ANDCODE_RESUME.md — safe import instructions, existing live workspace preservation and next task. Do not overwrite current AndCode files.
- WINDOWS_RESUME.md — clone into a NEW independent Windows folder with enough actual free space, not D:\KNOUX-AI.
- OWNER_DECISIONS_REQUIRED.md — true unresolved conflicts, no guessed design or destructive choices.

If the canonical source is complete and verified, create ONE compressed export with a versioned name including REAL source HEAD, list its members, check archive integrity and SHA-256 and keep it in this same parent folder. Do NOT create 4 duplicate 1GB exports. If only historical source, mark archive HISTORICAL_RECOVERY and clearly state the latest lost work remains unresolved.
The separate GitHub repo KNOUX-AI-Pro can be pushed ONLY after a verified mobile Git lineage, branch and selected files, successful security/secret scan of staged source and tracked HISTORY, valid auth, and explicit non-force push to its own main. Never push the wrong Windows repository. If sensitive OAuth tokens appeared in old chat/logs, treat them as compromised and do not persist or push them. Verify remote main SHA physically. An empty private GitHub repo is NOT a backup.

## ABSOLUTE SAFETY RULES
NO git reset --hard, git clean, force-push, branch/worktree/stash deletion, Android uninstall, Clear Data, filesystem cleanup, factory reset, automatic photo/video/file deletion, replacement of a real workspace, rewriting old Git history, package data scraping, rooting, unauthorized Shizuku use or automated granting of destructive Android permissions.
NO destructive duplicate cleanup. Never execute a recovery command which modifies original archives/sources. READ originals, WRITE only to new canonical recovery staging, hash every result. If a source is active, avoid concurrent writes and record its current state. No changes to D:\KNOUX-AI.
Never expose private GitHub tokens/refresh tokens/keystores/user files in a report, ZIP, Git commit, public link or raw uploaded logs. Review all exports for secrets before sharing.
Do not exceed free storage; on space shortage, preserve references to original existing archives and reduce redundant copied evidence rather than deleting. Avoid keeping an extracted copy of a 6.8GiB nested archive if irrelevant.

## FINAL OWNER-FACING DELIVERABLE — MANDATORY
Return exact Android folder path, ADB target and transport proven, number of candidates and their categories, every valid/invalid archive, selected REAL mobile Git SHA and reasons, source file count and build status, all unique work preserved, asset inventory, last M-phase verified, outstanding missing M08/M09 content, final packaged SHA, exact records and GitHub remote status.
Show a comparison table:
CANDIDATE | MOBILE/PC | GIT_SHA | SOURCE COMPLETENESS | UNIQUE WORK | SECRET RISK | SELECTED/REJECTED REASON.
Close only with evidence states: FULLY_VERIFIED, HISTORICAL_RECOVERY_ONLY, PARTIAL, or BLOCKED. FULLY_VERIFIED requires actual current M08/M09 source and genuine tests, not a report saying "DONE".
If all recent mobile source is unavailable, provide the strongest verified Nov11 baseline and non-destructive reconstruction plan, and identify inaccessible private app backup as a blocker. NEVER claim the user's unique work was recovered if it was not.

## EXECUTE NOW
START IMMEDIATELY with reality preflight and mobile-source inventory. Produce a real findings report FIRST, then build the single Downloads canonical directory safely, reconcile only supported mobile work, verify hashes/archives/Gradle as available, document every lost vs recovered feature, and give owner the exact final folder and SHA. Do not stop at analysis only and do not ask unnecessary questions. Do NOT modify either original app's work or independent Windows source.
