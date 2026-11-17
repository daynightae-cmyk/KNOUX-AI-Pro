# KNOUX — Task Writing / Execution Workflow / QA Proof Contract

## A. All durable tasks must specify
- **ID / Lane:** PCnn for Windows, Mnn for Linux Mobile, INTEG-nnn for external systems, DESIGN-nnn for approved visual revisions; never reuse the same ID across branches.
- **Target absolute path:** choose only one workspace. Include package/module and file ownership (who may write).
- **Baseline:** exact local SHA, live origin/main SHA, branch, worktree/unstaged/staged/untracked/stash, concurrent agents, disk, device readiness; if unavailable say PENDING.
- **Preserve:** approved identity assets/screens, existing code/test/migrations/unique untracked files, authored QA and history.
- **Requirement/user journeys:** per control describe Input → Provider/API → Permission/Consent → Action → Requery → Room event → user-visible outcome → error/cancel/recovery.
- **Acceptance:** exact positive + negative cases, Android API/OEM matrix, unit/instrumented tests, screenshots, RTL/EN, CI, release conditions. Mark BLOCKED where Android/Play forbids a feature.
- **Safety:** synthetic-only destructive fixtures with UUID and explicitly authorized device; scope decision and protected keeper for duplicate files; no broad cache/user folder deletes.
- **Execution outcome:** filenames, commands, exits, JUnit counts including skipped, issue log, UI comparison, source fingerprints and final SHA(s).
- **Integration/cleanup:** selective stage + reviewed commit main + fast-forward push + remote SHA/CI + measured safe generated-only disk reclamation; don't claim cleanup otherwise.
- **Next:** dependency-ready section and persistent ledger/manifest updated; no promise of work in the background without real automation.

## B. Only working workflows count
Preferred real native orchestrator when verifiably available. Otherwise one owner/controller running a stateful sequential workflow and optional independent reviewer. Never stop at writing JSON or making a speculative native Recipe. One writer per overlapping code file. Do not interrupt active OpenCode/Kiro processes to run redundant Gradle.

## C. Gate order and transition rules
`DISCOVER → SAFETY_SCOPE → IMPLEMENT → BUILD_UNIT → DEVICE_E2E → VISUAL_QA → REVIEW → COMMIT → PUSH → CI_CHECK → SPACE_RECLAIM → NEXT`.
Transitions must record evidence and timestamps. A `PASS` on 6 storage cases is NOT a `PASS` on the full 44-case suite. A test with `assumeTrue`/Skip does NOT prove a required feature. A code commit containing an alias guard does NOT prove two real provider URIs refer to different physical files. A unit-test success does not prove OS confirmation or a Play Billing purchase.

## D. Reporting template (copy per stage)
```markdown
## [TASK ID] — [service] — YYYY-MM-DD HH:mm +04
- Lane/workspace/branch:
- Baseline HEAD/live origin/main/status/worktrees/agents:
- Evidence class: OWNER_APPROVED_DESIGN | IMPLEMENTED_UNVERIFIED | UNIT_VERIFIED | DEVICE... etc
- Current stage and blockers:
- Unique work preserved (paths):
- Intended change / actual diff paths:
- UI routes and screenshot baselines:
- Permissions and real APIs:
- Test fixture IDs (redacted; synthetic only) + provider before/after:
- Commands/exits/JUnit tests, fail and skipped:
- Code reviewer findings / fix validation:
- Room migrations/ledger and lifecycle/recovery:
- Commit SHA / GitHub live SHA / CI:
- Disk bytes before/after; specific generated cleanup:
- Confirmed safe next action:
```

## E. Initial Windows PC priorities
PC00–PC02 have source + remote commits and manifest `VERIFIED`, but PC02 independent alias/Room verification is still a review risk; do not assume all physical provider overlap paths are proven. PC03 is active, latest whole 44-case instrumented run had four failures; not safe to mark FULL PASS. PC04 onward queued. Protect author and untracked work first.

## F. Initial Linux/Phone priorities
The mobile master M00–M15 is a [REDACTED_DEVICE_SERIAL], not a proof. Owner declares design finished; first inspect phone workspace and do one genuinely executable service vertical slice after verifying identity, auth/onboarding route preservation and actual storage constraints. Route results and work back to its own manifest only, not PC manifest.
