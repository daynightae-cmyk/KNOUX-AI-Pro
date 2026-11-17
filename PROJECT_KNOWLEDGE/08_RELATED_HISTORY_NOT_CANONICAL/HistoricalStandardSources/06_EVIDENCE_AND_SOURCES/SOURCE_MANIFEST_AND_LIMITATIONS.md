# Source provenance and archive limitations

## Evidence windows
- **Direct Windows PC read-only audit from this ongoing conversation:** at 2026-11-13 11:48–11:51+04; actual HEAD, live remote SHA, Git status, PC00–PC13 manifest, PC03 test logs, device authorized count and free disk. Source session is time-varying; no claims after snapshot.
- **Uploaded documents:** project canonical context, complete project source, source-of-truth handoff, historical conversation handoffs, original Mobile M00–M15 master, original onboarding/audio, PC Kiro audit, OAuth/Retaj summary. These are context artifacts, not new on-device functional tests.
- **User claims/decisions:** completed visual design for phone Linux/OpenCode app, desire for real E2E workflows and service-complete features, permission statement for audio source.
- **Three linked chats:** not retrievable; tracked separately.

## Stable classification
`owner_approved` = accepted requirements/design; `historical_agent_report` = previous claim; `direct_windows_git` = observed Git HEAD/remote/status at snapshot; `direct_test_log` = log files read from Windows; `inference_with_risk` = engineering risk proposed from code review; `unverified_external` = no live provider/Play/OAuth access; `mobile_linux_not_inspected` = no Linux repo read in this run.

## Archive is intentionally NOT
- A full private Git archive, private source code mirror, current Linux workspace snapshot, live DB export or raw customer data bundle.
- A dump of all conversations available in ChatGPT account: only the user-provided chat exports, transcripts and summaries that were available in this turn are used.
- A guarantee that ChatGPT indexes ZIP members; if project sources do not index the ZIP, extract and upload individual files.
- A user-approved destruction/force push of any historical commits containing private identifiers.

## Private-data hygiene
No raw OAuth JSON or secrets, device serials, pairing IP addresses or private unredacted Logcat are intentionally included. Source docs are copied from attached knowledge documents, with additional scrub checks. If an automatic scan flags tokens or personal details, exclude suspect file rather than publishing it.

## Source precedence
1. New owner directive for that lane
2. Live verified code, active agents and project Git/Tests/Runtime
3. Approved original visual assets
4. Authored project canonical/master requirements
5. Prior agent logs/reports, dated and scoped
6. Summaries, guesses, unlabeled screenshots or inaccessible share links

## Live updates
When a Project agent receives newer proof, write a dated addendum with exact source, local/remote SHA, tests/skips and material state changes. Do not silently edit older evidence; mark superseded. Keep this ZIP as an immutable snapshot and regenerate a new dated package when needed.
