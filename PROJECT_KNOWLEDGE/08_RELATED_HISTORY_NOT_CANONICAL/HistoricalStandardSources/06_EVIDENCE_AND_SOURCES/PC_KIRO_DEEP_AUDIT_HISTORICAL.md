# KNOUX AI PC — Deep Kiro Audit + OpenCode Workflow Takeover

**Prepared:** 2026-11-13 (Asia/Dubai)  
**Canonical repository:** `D:\KNOUX-AI` — do not touch independent `/root/workspace/KNOUX-AI`.

## 1. Independent evidence from uploaded Kiro session

The supplied `kiro-session-sess_76ae4392-8a79-4a73-85b5-658d38915723.zip` passed ZIP validation and contained session metadata, 960 session events, and two sub-execution logs. The session had 210 tool calls, 210 corresponding tool results, and two sub-executions. Its metadata explicitly said `agentMode=vibe`, `workflowsEnabled=false` and `status=failed`: it did **not** execute a verified native Kiro workflow.

Kiro performed significant **real work**: updated UiAutomator polling and Compose semantics, fixed test DataStore initialization, modified action rules and idempotency, and repaired scoped-cache/symlink behavior. These changes are present in the existing working tree as unique, uncommitted work.

| Checkpoint | Actual evidence | Accurate status |
|---|---|---|
| JVM rules | Host JUnit XML dated 2026-11-13 02:05:36: PreviewFixturesTest 3/3, RealDataRulesTest 13/13 | **16/16 pass — UNIT only** |
| Debug/test APKs | Gradle build success in session; Android test APK dated 02:06:10 | **Built, not release-ready** |
| Device storage tests, non-media | Two instrumented runs on authorized itel API35 report `OK (8 tests)` | **8/8 subset pass** |
| Synthetic MediaStore Trash → Restore → Delete | `provider must verify the exact requested state`; then intermittent ADB disconnection | **FAIL / unverified** |
| Subsequent media-test `assumeTrue` | OEM/provider timeout now potentially skips test | **SKIP is not PASS** |
| Five `SmartCleanRealTest` UI cases | Launched; no terminal result before provider/session stopped | **Unverified** |
| Full PC01 customer journey | No complete verified provider action + UI + Room report | **NOT VERIFIED** |
| Real Play Billing / Supabase | Only designs, draft SQL and catalog are known | **NOT DEPLOYED/VERIFIED** |

Kiro stopped after `UsageLimitReachedError` at 02:17:43 Dubai, with a JSON deserialization error in a later retry, followed by another usage-limit error at 02:44:48. This is an **agent/provider failure**, not proof that Android work completed.

## 2. Local and remote Git, and status after this audit

Before audit documentation integration, `main` and LIVE GitHub `main` both equaled `c0530b61872841dacb52b7d8602ff6a479b1bde1`. Only a **public-safe workflow policy** was then committed and pushed:

**Current verified local and remote main:** `41eb16259c5a383d3578b491a6a8640c82ac36ec`  
**Commit:** `docs(workflow): establish evidence-gated per-service E2E policy`  
**Public file:** `docs/workflows/KNOUX_E2E_PUBLIC_WORKFLOW_POLICY.md`

The repository is **public**. Raw on-device details, serials, private paths and unredacted evidence must remain local, not pushed. The additional detailed audit and agent handoff files are saved on the Windows machine but have **not** been pushed. All actual Smart Clean/Room/Pro tracked/untracked work remains intact and **uncommitted**. No cleanup or deletion was performed.

**Disk free at inspection:** C ~9.40 GB, D ~2.38 GB, K ~5.52 GB. Physical ADB device authorized by the owner is connected (itel S686LN, API35); recheck the precise serial locally before any test.

## 3. Actual task changes on Windows PC

- **PC00: UNIT_TESTING** — Data/Room foundation exists, 16/16 JVM unit tests pass; verify migrations, permission changes, DataStore, WorkManager and current runtime before full acceptance.
- **PC01: RUNTIME_TESTING** — 8 non-media storage instrumentation tests pass; genuine MediaStore Trash/Restore/Delete and five UI tests remain unverified. Do not mark COMPLETE.
- **PC02–PC13: QUEUED**, subject to task dependencies.
- Dynamic workflow validator corrected: it checks that the current task is one of the 14 IDs instead of forcing PC00 forever. It ran successfully with 14 tasks, 10 Pro sections and 50 catalog feature IDs. The validator itself does **not** verify services.

Files written in the PC repository:

- `qa-evidence/e2e/KIRO_SESSION_76AE_DEEP_AUDIT_20261113.md`
- `docs/workflows/KNOUX_PC_PER_SERVICE_AUTONOMOUS_EXECUTION_20261113.md`
- `docs/workflows/PC_E2E_TASK_MANIFEST_20261112.json`
- `qa-evidence/e2e/EXECUTION_LEDGER.md`
- `qa-evidence/e2e/ACCEPTANCE_MATRIX.md`
- `prompts/e2e/PC_OPENCODE_POST_KIRO76AE_FULL_WORKFLOW_TAKEOVER_20261113.md`
- `scripts/validate-pc-e2e-workflow.ps1`

## 4. The exact prompt to send to OpenCode PC

**KNOUX AI — POST-KIRO END-TO-END AUTONOMOUS WORKFLOW TAKEOVER**

Work ONLY inside `D:\KNOUX-AI`. The previous Kiro session failed on provider usage limits, **not because the software was completed**. Preserve every unique modified/untracked source, Room schema, tests, Pro file, approved visual asset, and the existing Git history.

**First:** read completely:

1. `prompts/e2e/PC_OPENCODE_POST_KIRO76AE_FULL_WORKFLOW_TAKEOVER_20261113.md`
2. `qa-evidence/e2e/KIRO_SESSION_76AE_DEEP_AUDIT_20261113.md`
3. `docs/workflows/KNOUX_PC_PER_SERVICE_AUTONOMOUS_EXECUTION_20261113.md`
4. `docs/workflows/PC_E2E_TASK_MANIFEST_20261112.json`
5. `qa-evidence/e2e/EXECUTION_LEDGER.md`
6. `qa-evidence/e2e/ACCEPTANCE_MATRIX.md`
7. `AGENTS.md`

**Run your OWN genuinely supported workflow for EVERY section, not a guessed Kiro Recipe.** If native workflows/subagents exist, use real supported execution and show real run IDs/steps. Otherwise use a sequential autonomous controller that actually performs the work and persists checkpoints. Never stop at a plan or task definition.

**Strict per-section sequence:**

REALITY → RISKS → UNIQUE WORK → IMPLEMENTATION → CURRENT BUILD/TEST → DEVICE RUNTIME → INDEPENDENT REVIEW → REPAIR → ACCEPTANCE → SELECTIVE LOCAL MAIN COMMIT → SAFE FAST-FORWARD GITHUB PUSH → VERIFY REMOTE SHA/CI → SAFE GENERATED-DISK CLEANUP → NEXT DEPENDENCY-READY SECTION.

**Current mission:** finish PC00 foundation and PC01 Smart Clean from the existing Kotlin source. Do not start over. Current valid evidence is 16/16 JVM tests and 8/8 isolated non-media device tests. The media Trash/Restore/Delete case failed, and final UI instrumentation has no result. Diagnose the OEM/provider consent path and USB drops; do not change required assertions to `assumeTrue` and falsely call the feature passing. Execute all five Compose UI tests after a safety audit of test-owned fixtures. Confirm actual scan, inspect/filter/select/exclude, explicitly approved Trash/Permanent Delete, exact provider requery, Room history, retry/partial/recovery and Arabic/English layouts.

The owner explicitly permits using the connected physical phone. **All destructive tests must be limited to newly created and verified synthetic UUID fixtures.** Never touch personal media, apps, accounts, settings, or any unrelated user content; never clear app data or factory-reset the phone.

**After each independently accepted service:** inspect all other agents and untracked work; stage only coherent verified files, review the staged diff, commit to existing `main`, push safely without force, confirm the actual `git ls-remote origin refs/heads/main` SHA and CI. The repository is public: do not upload serial numbers, personal test paths, credentials or raw Logcat. Don't claim a service is released just because a commit is pushed.

**Space recovery only AFTER verified integration:** inspect storage usage and active processes; propose/perform only narrowly allowlisted deletion of genuinely disposable regenerated build intermediates, outdated duplicate APK copies and abandoned partial downloads after preserving compact QA evidence. NEVER delete Kotlin source, unique work, Git objects, schema, accepted images, authored tests, current APK or data to save space. Log actual bytes reclaimed. If an artifact's uniqueness is uncertain, retain it.

Update the SAME 14-task manifest and append-only execution ledger with exact exit codes, JUnit pass/fail/skip, test serial/API, fixture IDs, provider after-state, UI screenshots, source/remote SHA, CI, cleaned bytes and next task. Automatically continue PC02 (or another dependency-ready service) after verified PC01; if blocked, log exact blocker and continue other independent work only.

**START EXECUTION ON CURRENT REAL SOURCE. NO PREVIEW-ONLY SERVICES. NO FAKE PASS. NO UNIQUE WORK LOSS.**

## 5. Security and evidence boundaries

Do not copy code from the separate OpenCode mobile Linux project. Do not publish the raw Kiro session or raw personal device logs to the public repo. Do not erase any unique work or delete project source after committing it. Do not approve unknown Android system consent dialogs. If the real OS path remains unsupported, report the exact limitation; premium feature claims must reflect reality.
