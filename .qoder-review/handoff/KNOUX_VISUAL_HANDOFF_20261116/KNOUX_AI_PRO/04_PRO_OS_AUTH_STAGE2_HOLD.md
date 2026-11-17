# KNOUX AI Pro — OS & AUTHENTICATION FULL CONTRACT (FUTURE FUNCTIONAL STAGE ONLY)

The Pro app is independent; its OAuth package name, Google clients, signing SHA, asset-link domain, Supabase project, Pro entitlement system and Android SDK must be inspected in its OWN repository and approved environment. Do NOT copy standard KNOUX AI auth credentials, source files, package identifiers or database automatically. Historic standard-app setup notes are not proof of a Pro configuration.

**Current mission: visually finish the existing golden Pro auth screens and their navigation without implementing new identity infrastructure.** Preserve the approved Splash Smile/audio, Founder, Login, OTP, Google, Passkey and small biometric designs.

Later, when owner authorizes functional E2E:

1. **Guest:** local-first isolated optional account; no cloud session implied. Migration explicit.
2. **Email / OTP:** server-backed request, expiry, replay rejection, resend throttling, backend response verification, logout and account recovery.
3. **Google Credential Manager:** use the Pro product's current legitimate OAuth clients/package/signing fingerprint; trusted backend verifies signature, issuer, audience, expiry, user identity and nonce where used before issuing account session. Never put web Client Secret in APK.
4. **Passkeys:** correct RP domain and Android Digital Asset Links for the Pro package, server-origin challenge and WebAuthn response verification. Passkey UI is not proof of backend readiness.
5. **BiometricPrompt:** local unlock/re-auth of an already verified or locally bound state; unavailable/fail/cancel/lockout correctly handled. Local biometric does not establish a new trusted cloud identity by itself.
6. **Credentials/security:** Android Keystore, encrypted persistence, short-lived tokens/refresh policy, secure redirect handling, no token logging, protected exported components, replay/logout and offline renewal states.
7. **Permissions/Android storage:** MediaStore with granular/selected-photos permissions; Photo Picker; scoped Storage Access Framework; persisted URI grants only as allowed; MediaStore.createTrashRequest/createDeleteRequest with OS confirmation; DocumentsContract operations when provider supports; no access to other apps' private cache, no universal app freeze/clone. Permissions can be revoked mid-session. Do not use root/ADB/Shizuku implicitly.
8. **Real engines:** user selection -> review -> OS confirmation -> requery -> Room audit history. Avoid deleting a duplicate keeper when MediaStore and SAF URIs alias same file. Synthetic-fixture-only destructive QA.
9. **Pro Billing:** authoritative entitlement source + supported Billing purchase/acknowledge/restore/refund/revocation states; one global theme driven by confirmed entitlement if the product offers gated Pro states. Never fake paid status.
10. **Cloud optional:** approved project's migration history, RLS/2-user isolation, opt-in sync of non-sensitive aggregates only by default, deletion/revocation; no file paths/personal filenames to server without explicit scoped user choice.

### Future acceptance cases
Device cold launch, restored session, first-launch skip, rejected OTP/replay, Google cancel/account change/expired token, Passkey origin/challenge rejection, biometric absent/cancel/lockout, account logout, limited/denied/partial media access, OS delete dialog denied, app process death, Room upgrade, permissions revoked mid-task, two-user cloud isolation, subscription transition and full Free/Pro palette update after a verified entitlement. Record real device/screenshots/CI/test count and SHA; unknown is `NOT_VERIFIED`.

**Use this only when the owner moves the independent Pro project from Visual Production into E2E implementation.**
