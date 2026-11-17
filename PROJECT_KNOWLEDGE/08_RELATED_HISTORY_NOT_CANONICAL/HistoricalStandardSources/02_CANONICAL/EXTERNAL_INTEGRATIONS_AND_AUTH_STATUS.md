# KNOUX AI — External Integrations / مصادقة وخدمات مدفوعة

**Evidence:** historical uploaded OAuth/Supabase conversation+summary; NOT an authorized live Supabase management read in this ZIP generation.

## Google & Supabase
- Google Cloud project: `fiery-script-514604-n6` (historically reported).
- App package: `com.knoux.ai`.
- Supabase project ref: `tduofkizwkjtqcbklfah`.
- Public OAuth callback: `https://tduofkizwkjtqcbklfah.supabase.co/auth/v1/callback`.
- Historically identified three OAuth client roles: Android debug client (no secret), Web client for Supabase (secret stored securely outside repo), Web Server client for native credential flow if appropriate. Their local JSON files were reportedly inspected and moved to protected local folder; this history is **not** fresh verification.
- Google Provider was visible/toggled in UI; later instruction to add client IDs and matching Web secret. **No live proof of saved provider config, no Android Google login/Supabase session verification in this package.** Never claim success from creation of clients, enabled switch or local script unit test.
- Implement Credential Manager/Google ID token flow against the correct Web client configuration, nonce checks enforced, redirect/Android signing fingerprint checked, session and logout/revocation proved. Test debug and eventual release signing separately. Don't paste tokens, client secret or user's local credential file bytes into ChatGPT sources.

## Biometrics, passkeys, guest
- Biometric is a local gate, not a replacement for authenticated backend identity. Preserve approved small icon + auth flow. Require real BiometricPrompt/session boundary tests.
- Passkeys require actual supported Credential Manager/FIDO2 + relying-party backend; visible icon ≠ working passkey. Guest must have accurate limited capability boundaries.

## Supabase data SQL research
- Original Mobile master preserves a 13-table SQL proposal; it is **research**, not evidence database is deployed. Before implementation inspect actual Supabase schema, RLS, ownership and rollback; migrations under authorized credentials only, ensure no service role key in APK or public docs. Prefer local Room-first when cloud provider blocked.

## Play Billing / KNOUX AI Pro
- 50 IDs exist in catalog, not verified entitlements. One coherent Pro center, actual Play Billing purchase/recovery/status/expiry/cancel flow, secure backend entitlement verification, offline truthful state and actual feature behavior. Dev-only hidden Pro gesture must not leak production entitlements. Never hardcode premium activation as real billing.

## Audio provenance
- Owner-provided audio source: `https://mp3tourl.com/audio/1794520051232-3d804d0a-2f23-4c00-9879-af7542c6616f.mp3`.
- Owner reports site-owner permission. Must verify actual recording content, format, offline resource bundled, audio-focus and once-after-smile timing; record permissions as an owner statement and investigate any additional recording rights before commercial release. No MP3 binary bundled in this knowledge package.

## Security and external source rules
Never place Google Client Secret, Supabase Management Token, AWS API secret, GitHub token, private ADB serial, Wireless debugging IP/port, or unredacted logcat in Project Sources or public repository. Use connected tools with owner permission; otherwise mark BLOCKED_EXTERNAL. HTTP URLs here are reference targets, not evidence of active reachability now.
