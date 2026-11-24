package com.knoux.ai.google.drive

/**
 * Google Drive connector wire rules, kept pure so JVM tests can pin them.
 *
 * Drive v3 moves a file to Trash by PATCHing the file resource with a
 * `trashed` flag. There is no dedicated trash endpoint. A file that is gone
 * (404 on the re-query) counts as trashed; every other failure is unknown and
 * is never counted as success.
 */
object GoogleDriveApiPolicy {

    /** Days between the scan's old-file cutoff and now. */
    const val OLD_FILE_DAYS = 365L

    /** Minimum size for the large-file query, in bytes (10 MiB). */
    const val LARGE_FILE_MIN_BYTES = 10L * 1024L * 1024L

    /** Drive v3 files.update body that moves a file to Trash. */
    fun trashRequestBody(): String = "{\"trashed\":true}"

    /** Drive v3 files.update body that restores a file from Trash. */
    fun untrashRequestBody(): String = "{\"trashed\":false}"

    /** Drive v3 files.update URL (PATCH) used to write the trashed flag. */
    fun trashedFlagUpdateUrl(fileId: String): String =
        "https://www.googleapis.com/drive/v3/files/$fileId?fields=trashed"

    /** Re-query URL used to confirm a trash request; projects only the flag. */
    fun trashedConfirmationUrl(fileId: String): String =
        "https://www.googleapis.com/drive/v3/files/$fileId?fields=trashed"
}
