# KNOUX AI — Full Agent Operating Contract / اتفاق التشغيل المستمر

## 1. Identity and precedence
KNOUX is the corporate parent; KNOUX AI is the Android app. Founder Eng. Sadek Elgazar / م. صادق الجزار, Founder & Software Engineer. The live owner instruction for the selected lane outranks historic commands; current code/approved assets outrank prior agent narratives. Preserve existing branding; don't assume historical paths or SHA are current.

## 2. Determine exactly which system you are acting on
1. **PC / Windows OpenCode** `D:\KNOUX-AI`; milestones PC00..PC13; native Windows PowerShell/Gradle; use verified live remote and local worktree.
2. **Phone-hosted Linux OpenCode** `/root/workspace/KNOUX-AI`; milestones M00..M15; inspect its own Git history, Gradle and filesystem. Never blindly clone/copy from PC or treat PC tests as mobile evidence.
3. **Physical Android phone** used to test builds; it is not a source project. Reverify ADB transport and API per run. Dynamic ADB serial in local environment; no hardcode/public copy. It can be connected by Wi-Fi; absence or disconnect means BLOCKED_DEVICE, not project failure.
4. **Supabase/Google/Play** external accounts; never infer saved configuration from instructions, public callback or a generated local script. Use actually connected authorized external tools; no token in Markdown/JSON/Git.

## 3. Perform a source-of-truth preflight for every technical request
Record command & output for cwd/repo, `git rev-parse --show-toplevel`, HEAD, remote tracking SHA and actual `git ls-remote`, branch, `git status --porcelain -uall`, `git worktree list`, `git stash list`, recent reflog/PRs, tracked/untracked/modified files, jobs and other agents, build outputs and disk. If the request is only to REVIEW and active agents exist, default to READ-ONLY with no Gradle concurrent builds. Stop or avoid overlapping writes. No mass restore, clean, hard reset, force push, history rewrite or deletion without examining impact and explicit consent.

## 4. Workflow for EACH service, not a plan pretending to execute
Create and use your own native agent workflows/subagents if actually supported. Otherwise use sequential controller checkpoints in existing manifest+ledger. Do not block progress to repair speculative vendor-specific workflow JSON.

Gates: DISCOVER & OWNERSHIP → ACCEPTANCE CONTRACT → REAL DATA IMPLEMENTATION → CURRENT BUILD + JVM → INSTRUMENTATION on synthetic fixtures → Actual UI/OS confirmation/provider state/Room → independent QA → bounded repair → verified coherent local commit → fast-forward push to remote main → live SHA and CI → proven safe generated-output cleanup → next dependency-ready service.

Each feature's UI must expose real action buttons and reasoned disabled states (permissions, errors, unsupported providers) rather than fake success or inert controls. Support cancellation, selection, visibility, RTL/LTR, user data privacy, and recovery where platform permits.

## 5. Critical Android deletion safety
- No personal images/documents or other apps' data in automated QA. Generate new UUID fixtures, validate their URIs, owner/provenance, size and membership directly before each deletion/trash/restore. User-controlled system consent is mandatory.
- MediaStore and SAF may reference THE SAME physical file with different URIs; hash match alone is insufficient evidence of two physical copies. Fail closed on uncertain identity. Re-prove protection after Room restore and process death. A keeper must never be deleted as an alias.
- Mixed local cache + shared media operations may act before OS dialog; UI must truthfully disclose partial effects even if later dialog is cancelled. Return status and Room journal per item, not fabricated atomic rollback.
- When any provider requery shows `UNKNOWN`, a timeout, a skipped test, or revoked grant, do NOT report file removed or recovered. Do not invent free bytes after deletion.

## 6. Requirements and visual baseline
Use the detailed original master in `03_ORIGINAL_SPECS/` and current actual assets. Existing 20-screen references; approved onboarding chapters; small biometric icon (no giant fingerprint); original KNOUX logo, Deep Navy/Violet/Cyan, elegant glassmorphism, readable Arabic RTL and English LTR. Functional extensions must maintain approved design fidelity, responsive insets and accessibility. A Gradle green build does not prove visual acceptance.

## 7. Pro, Auth, Cloud truth
The 50 Pro IDs describe expected capabilities, not paid entitlements. One Pro center; real authorized Play Billing purchases and independently verified server entitlements, no hardcoded unlock. Supabase Auth Google provider configuration + Android signed ID token/session must be actually tested; OAuth JSON file existence is not success. Biometric is local identity presence, not stand-alone backend login. Passkey requires real credential server flow. SQL draft/13 tables are research until authorized migration+RLS and live DB checks. Never place keys/privileged tokens in APK or Git.

## 8. Status and evidence taxonomy
PLANNED; IMPLEMENTED_UNVERIFIED; UNIT_VERIFIED; DEVICE_SUBSET_VERIFIED; DEVICE_E2E_VERIFIED; INDEPENDENT_REVIEW_PENDING / VERIFIED; LOCAL_COMMITTED; REMOTE_MAIN_VERIFIED; CI_VERIFIED; BLOCKED_DEVICE / PLATFORM / EXTERNAL; PRODUCTION_RELEASED. Use exact tests/exit codes and JUnit numbers and timestamps. Report mismatches between manifest labels and independently observed unresolved safety gaps. No claims of complete service from a test subset, Commit title or actor assertion.

## 9. Safe integration and space reclamation
Main is the integrated product, not a landfill. Preserve unique branch/worktree/stash/untracked work, approved visual assets and history; stage selected reviewed paths, commit, push without force, verify same remote SHA and actual CI, then inventory and reclaim ONLY obsolete regenerable generated files not in use. Capture measured before/after bytes; don't delete authored tests/Room schema/QA evidence/unique assets or other writers' outputs. No blanket cleanup, no forced wipe of personal storage, no automatic deletion of app data.

## 10. Reporting format in conversation and task docs
Use: REALITY / RISKS / UNIQUE WORK / GAPS / SAFE NEXT ACTION / EXECUTION & EVIDENCE. Include environment/path, observed time, SHA remote/local, status including untracked, file ownership, build/test commands and pass/fail/skip, device provider/UI outcome, screenshots, commit+push/CI, safe cleanup bytes, blockers and next task. Distinguish user claim, prior agent report, current read-only tool verification, and unverified inference.

## 11. Do not turn static ChatGPT Project knowledge into active commands
This ZIP is static context. When the user requests a change, use fresh connected tools on authorized target if available; do not claim previous state still current. Connector permissions are separate. New ChatGPT chats and linked shared conversations aren't automatically imported into every agent; references require actual accessible text.
