package com.knoux.ai.google

import com.knoux.ai.cloud.model.CloudProvider
import com.knoux.ai.google.auth.GoogleAccountPolicy
import com.knoux.ai.google.drive.GoogleDriveApiPolicy
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * P8 - Google connector correctness, pinned by JVM tests.
 *
 * Regressions covered:
 *  1. Drive v3 has no files/{id}/trash sub-resource. Trash and untrash are
 *     files.update on the file resource with a `trashed` flag; the old URL
 *     404d on every call and silently reported zero moved items.
 *  2. One Google consent unlocks Mail and Drive, but the cloud dashboards are
 *     per provider. The persisted slot key must be derived from the provider,
 *     never from a loose caller string, or a Drive grant is stored in the Mail
 *     slot and the Drive screen reports "no account connected" after reload.
 *  3. Account metadata is read back from its JSON payload, not by stripping a
 *     prefix off the preference key (e-mails may start with gmail_/drive_).
 */
class GoogleSlotContractTest {

    private fun contractFile(relativePath: String): java.io.File {
        val workingDirectory = java.io.File(System.getProperty("user.dir")).absoluteFile
        return listOf(workingDirectory, workingDirectory.parentFile)
            .map { java.io.File(it, "app/$relativePath").absoluteFile }
            .firstOrNull { it.isFile }
            ?: error("Cannot locate app contract: $relativePath from $workingDirectory")
    }

    // ------------------------------------------------- Drive v3 trash contract

    @Test
    fun driveTrashUrlsAreFilesUpdateOnV3Resource() {
        val url = GoogleDriveApiPolicy.trashedFlagUpdateUrl("abc123")
        assertEquals("https://www.googleapis.com/drive/v3/files/abc123?fields=trashed", url)
        assertFalse("v3 has no trash sub-resource", url.contains("/trash"))
        assertFalse("v3 has no untrash sub-resource", url.contains("/untrash"))
        // The flag itself is the mutation body, not a query parameter.
        assertEquals("{\"trashed\":true}", GoogleDriveApiPolicy.trashRequestBody())
        assertEquals("{\"trashed\":false}", GoogleDriveApiPolicy.untrashRequestBody())
        assertTrue(JSONObject(GoogleDriveApiPolicy.trashRequestBody()).getBoolean("trashed"))
        assertFalse(JSONObject(GoogleDriveApiPolicy.untrashRequestBody()).getBoolean("trashed"))
    }

    @Test
    fun driveConnectorMutatesThroughTheFlagPolicy() {
        val text = contractFile("src/main/java/com/knoux/ai/google/drive/GoogleDriveConnector.kt").readText()
        assertTrue(
            "trashFiles must PATCH the v3 file resource with the trashed flag",
            text.contains("trashedFlagUpdateUrl(fileId)") && text.contains("trashRequestBody()"),
        )
        assertTrue(
            "untrashFiles must PATCH the same resource with trashed false",
            text.contains("untrashRequestBody()"),
        )
        assertFalse("no Drive v2 trash sub-resource", text.contains("/files/\$fileId/trash"))
        assertFalse("no Drive v2 untrash sub-resource", text.contains("/files/\$fileId/untrash"))
        // Cutoff windows come from the pure policy so they can be pinned.
        assertTrue(text.contains("GoogleDriveApiPolicy.OLD_FILE_DAYS"))
        assertTrue(text.contains("GoogleDriveApiPolicy.LARGE_FILE_MIN_BYTES"))
    }

    // ---------------------------------------------------- per-provider slots

    @Test
    fun oneConsentUnlocksBothProviders() {
        assertEquals(
            listOf(CloudProvider.Gmail, CloudProvider.GoogleDrive),
            GoogleAccountPolicy.consentProviders(),
        )
    }

    @Test
    fun slotKeysAndRowIdsArePerProviderAndDistinct() {
        assertEquals("gmail", GoogleAccountPolicy.slotKey(CloudProvider.Gmail))
        assertEquals("drive", GoogleAccountPolicy.slotKey(CloudProvider.GoogleDrive))
        assertEquals("google_a@b.com", GoogleAccountPolicy.accountId("a@b.com", CloudProvider.Gmail))
        assertEquals(
            "google_drive_a@b.com",
            GoogleAccountPolicy.accountId("a@b.com", CloudProvider.GoogleDrive),
        )
        assertTrue(GoogleAccountPolicy.hasMailSlot(listOf("a@b.com" to CloudProvider.Gmail), "a@b.com"))
        assertFalse(GoogleAccountPolicy.hasDriveSlot(listOf("a@b.com" to CloudProvider.Gmail), "a@b.com"))
        assertTrue(
            GoogleAccountPolicy.hasDriveSlot(listOf("a@b.com" to CloudProvider.GoogleDrive), "a@b.com"),
        )
    }

    @Test
    fun managerNeverHardCodesASingleGmailSlot() {
        val text = contractFile("src/main/java/com/knoux/ai/google/auth/GoogleAccountManager.kt").readText()
        // Slot persistence is provider-derived, not a caller-supplied string.
        assertTrue(text.contains("GoogleAccountPolicy.slotKey(account.provider)"))
        assertFalse("a default slot re-collapses both providers", text.contains("accountKey: String"))
        // Reload must trust the stored payload, not a key-prefix guess.
        assertTrue(text.contains("JSONObject(value).optString(\"email\")"))
        assertFalse(text.contains("suffix.startsWith(\"gmail_\")"))
    }

    @Test
    fun everyGoogleLinkUiPersistsBothProviderSlotsWithIdentityScopes() {
        listOf(
            "src/main/java/com/knoux/ai/cloud/ui/UnifiedCloudCleanerScreen.kt",
            "src/main/java/com/knoux/ai/google/ui/GoogleStorageDashboardScreen.kt",
        ).forEach { path ->
            val text = contractFile(path).readText()
            assertTrue(
                "$path must request identity scopes or userinfo cannot return the e-mail",
                text.contains("connectScopes"),
            )
            assertTrue("$path must persist a Drive slot", text.contains("CloudProvider.GoogleDrive"))
            assertTrue("$path must persist a Mail slot", text.contains("CloudProvider.Gmail"))
        }
    }
}
