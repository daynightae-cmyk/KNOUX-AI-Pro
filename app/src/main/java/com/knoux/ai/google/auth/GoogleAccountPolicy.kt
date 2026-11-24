package com.knoux.ai.google.auth

import com.knoux.ai.cloud.model.CloudProvider

/**
 * Pure Google-connector rules, JVM-testable (no Android types).
 *
 * One Google consent can grant Mail and Drive, but the cloud dashboards work
 * per provider. A connected account therefore needs one slot per provider, and
 * the encrypted token is stored once per e-mail. Hard-coding every slot to
 * Gmail made a successful Drive grant report "no account connected" on the
 * Drive screen.
 */
object GoogleAccountPolicy {

    /** Row id per provider slot; Mail keeps the legacy id shape. */
    fun accountId(email: String, provider: CloudProvider): String = when (provider) {
        CloudProvider.GoogleDrive -> "google_drive_$email"
        else -> "google_$email"
    }

    /**
     * Persistence slot key for a provider, derived from the provider itself so a
     * caller can never save a Drive row under the Mail key (that collapsed both
     * slots into one on reload and made the Drive screen report "not connected").
     */
    fun slotKey(provider: CloudProvider): String = when (provider) {
        CloudProvider.GoogleDrive -> "drive"
        else -> "gmail"
    }

    /** Providers unlocked by one Google consent in this app. */
    fun consentProviders(): List<CloudProvider> = listOf(
        CloudProvider.Gmail,
        CloudProvider.GoogleDrive,
    )

    /** True when [accounts] has a Drive-capable slot for [email]. */
    fun hasDriveSlot(accounts: List<Pair<String, CloudProvider>>, email: String): Boolean =
        accounts.any { (accountEmail, provider) ->
            accountEmail == email && provider == CloudProvider.GoogleDrive
        }

    /** True when [accounts] has a Mail-capable slot for [email]. */
    fun hasMailSlot(accounts: List<Pair<String, CloudProvider>>, email: String): Boolean =
        accounts.any { (accountEmail, provider) ->
            accountEmail == email && provider == CloudProvider.Gmail
        }
}
