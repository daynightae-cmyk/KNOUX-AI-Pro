# KNOUX-AI - AndCode EXECUTION CONTRACT (2026-11-13)
You are the coding agent running INSIDE AndCode's local OpenCode runtime on the owner's Android phone.
Do not confuse app.getopencode (old separate Android app), ai.sol.opencode, AndCode or the KNOUX-AI project.
DO NOT implement this mission until the latest real source has been delivered and verified.

OBJECTIVE
Carry the unique, current code from /root/workspace/KNOUX-AI into a new AndCode workspace, without loss of Git history, untracked files, visual assets, SHA evidence, local DB schemas, Android build artifacts, QA references, approved onboarding/login animations, or decisions. Improve KNOUX AI mobile services from their actual current status until each screen and service works end-to-end on a real phone. Do not touch D:\KNOUX-AI (independent Windows project).

0. OBSERVE BEFORE WRITING
- Read /sdcard/Download/KNOUX_AndCode_Migration_20261113/ANDCODE_HANDOFF_README_20261113.md first.
- Check existence of a newly delivered, checksum-verified export from the canonical /root/workspace/KNOUX-AI. A 2026-11-11 snapshot or a stand-alone debug APK is NOT the current source.
- If current export is absent, output BLOCKED_MISSING_LIVE_SOURCE and the exact import action needed; do not clone an older main branch as if current.
- Work on one confirmed folder only. Check git rev-parse --show-toplevel, HEAD SHA, status --porcelain=v1 -uall, branch -avv, reflog, stash list, worktree list, and remote -v. Check for duplicate or conflicting agent writes and secure them before making edits.
- If importing TAR, inspect entry names for path traversal and symlinks; extract into a brand-new versioned workspace under user-selected AndCode project storage. Verify all source paths, .git and SHA-256. Keep private archives protected.

1. RESTORE EXECUTION CONTEXT
- Read the original M00-M15 master contract and all appendices; the repair contract, AGENTS.md, task manifest, verified ledger, design/QA references, test XML and actual Git diffs.
- Restore in-progress phase from CURRENT evidence, not reported completion. Historically M08 had a 100-test BUILD SUCCESSFUL claim and a debug APK (35,386,564 bytes; SHA256 CF3E3C785B662C89663F9C862B5A13FFE70C21B16238880A1ED68F236F813E76). Independent Android check verified this APK installed and launched with the Deep Scan UI. It did NOT verify M08 operation or later source.
- Label every capability VERIFIED_ON_DEVICE / IMPLEMENTED_UNVERIFIED / PARTIAL / BLOCKED_PERMISSION / BLOCKED_EXTERNAL / NOT_IMPLEMENTED; no fabricated green checks.

2. ENGINEER SERVICES FULLY
- Each section and individual function gets a distinct, premium, accessible EN/AR RTL/LTR UI with complete customer controls, permissions, review and consent, real Android data, cancellation, accurate progress, result details, persistence and safe recovery.
- No preview-only or dummy actions; no fake AI metrics, duplicate detection, or battery-savings claims. Honor Scoped Storage/MediaStore/SAF rules.
- Preserve approved KNOUX visual system, logos, service icons, illustrations, motion, onboarding and identity flows. Compare device screenshots with accepted reference images; fix overlap/clipping, dark/light, small screens and reduced motion.
- Never perform deletion or uninstall on owner's real content during tests. Use synthetic fixture files in test-only folder and Android OS confirmation; inspect post-action state and journal.
- Existing screens with prior defects: Deep Scan progress text collision, Privacy Sweep unreadable overlapped cards, Battery Saver UI overlaps, Smart Clean permission dead end, Duplicate Finder scan and keeper protection, AI Recommendations ungrounded metrics.
- Complete M00-M15 and each of 50 Pro features only after individual current evidence check; do not overwrite work already completed by other agents.

3. PROVE EACH SERVICE ON THE REAL PHONE
- Read original build configuration; run actual Gradle compile, unit tests and relevant instrumented tests. Capture full logs and XML, not grep-only summaries.
- Install a signed compatible DEBUG test APK via ADB when authorized; NEVER uninstall/clear app data or bypass Android permission prompts.
- Exercise every visible button/flow, capture genuine before/after screenshots, and assert database/history and effect on synthetic fixtures.
- Do not claim VERIFIED because BUILD SUCCESSFUL, a screenshot, or an agent assertion alone.
- When ADB is unavailable, mark DEVICE_RUNTIME_NOT_RUN; preserve code and proceed on independent safe tasks but do not close the section.

4. DELIVER AFTER EACH TASK
- Record real HEAD, changed/untracked file paths, Git diff summary, test counts and failing traces, compile result, actual APK path and SHA256, Android install/runtime proof and screenshots.
- Preserve complete source snapshots (including .git/untracked assets) with manifests before cleanup, and never reset --hard, clean, force push, delete branches, rewrite history or discard unique work.
- Do not send credentials in reports or public archives. An API model's free quota is not unlimited; use another authorized provider or lower compatible output tokens, never loop on 429.
- End with COMPLETED_VERIFIED, PARTIAL_WITH_EVIDENCE, or BLOCKED_WITH_EXACT_REASON and list remaining functionality.
- If tasks cannot run because of missing current source or provider, deliver precise requirements for resuming and do not manufacture test results.

GO NOW: perform the read-only baseline audit, check for the live export and report the exact Git SHA you can prove before touching any source.
