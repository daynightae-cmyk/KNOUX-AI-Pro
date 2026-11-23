package com.knoux.ai.google.drive

import android.content.Context
import com.knoux.ai.cloud.CloudApiException
import com.knoux.ai.cloud.CloudHttp
import com.knoux.ai.cloud.CloudJsonParsers
import com.knoux.ai.cloud.model.CleanupCandidate
import com.knoux.ai.cloud.model.CleanupCategory
import com.knoux.ai.cloud.model.CloudScanOutcome
import com.knoux.ai.cloud.model.ScanResult
import com.knoux.ai.cloud.model.ScanStatus
import com.knoux.ai.cloud.model.CloudProvider
import com.knoux.ai.google.auth.GoogleAccountManager
import com.knoux.ai.google.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Google Drive Cleaner connector (GOOGLE-03).
 * Uses Google Drive API v3 to scan storage:
 * large files, exact duplicates, old files, filename similarity.
 * Uses Checksums (MD5) where available, never permanent delete — Trash only.
 */
class GoogleDriveConnector(
    private val context: Context,
    private val accountManager: GoogleAccountManager,
    private val authProvider: GoogleAuthProvider,
) {

    private companion object {
        const val DRIVE_API_BASE = "https://www.googleapis.com/drive/v3"
        const val FILE_FIELDS = "files(id,name,size,md5Checksum,createdTime),nextPageToken"
    }

    /**
     * Scan Google Drive for cleanup candidates.
     */
    suspend fun scanAccount(accountId: String): ScanResult = scanWithCandidates(accountId).result

    suspend fun scanWithCandidates(accountId: String): CloudScanOutcome {
        return withContext(Dispatchers.IO) {
            val scanId = java.util.UUID.randomUUID().toString()

            try {
                val account = accountManager.getConnectedAccounts()
                    .firstOrNull { it.id == accountId }
                if (account == null) {
                    return@withContext CloudScanOutcome(
                        result = ScanResult(
                            scanId = scanId,
                            provider = CloudProvider.GoogleDrive,
                            accountId = accountId,
                            status = ScanStatus.Failed,
                            error = "Account not found",
                        ),
                    )
                }

                val accessToken = accountManager.getValidAccessToken(account)
                    ?: return@withContext CloudScanOutcome(
                        result = ScanResult(
                            scanId = scanId,
                            provider = CloudProvider.GoogleDrive,
                            accountId = accountId,
                            status = ScanStatus.Failed,
                            error = "No valid access token",
                        ),
                    )

                var itemsFound = 0
                var bytesFound = 0L
                val candidates = mutableListOf<CleanupCandidate>()

                // 1. Large files (> 10MB), paginated.
                val largeFiles = listFiles(accessToken, "size > 10485760 and trashed = false", pageSize = 100)
                for (file in largeFiles) {
                    candidates.add(CleanupCandidate(
                        id = "drive_${file.id}",
                        accountId = accountId,
                        provider = CloudProvider.GoogleDrive,
                        category = CleanupCategory.LargeFile,
                        providerItemId = file.id,
                        itemName = file.name,
                        sizeBytes = file.sizeBytes,
                        sourceAccount = account.email,
                        canRestore = true,
                    ))
                }

                // 2. Old files (older than 1 year), paginated.
                val cutoff = java.time.Instant.now()
                    .minus(365, java.time.temporal.ChronoUnit.DAYS)
                    .toString()
                val oldFiles = listFiles(
                    accessToken,
                    "modifiedTime < '$cutoff' and trashed = false",
                    pageSize = 100,
                )
                for (file in oldFiles) {
                    candidates.add(CleanupCandidate(
                        id = "drive_${file.id}_old",
                        accountId = accountId,
                        provider = CloudProvider.GoogleDrive,
                        category = CleanupCategory.OldFile,
                        providerItemId = file.id,
                        itemName = file.name,
                        sizeBytes = file.sizeBytes,
                        sourceAccount = account.email,
                        canRestore = true,
                    ))
                }

                // 3. Exact duplicates (verified MD5 identity), paginated.
                val duplicates = findExactDuplicates(accessToken)
                for (file in duplicates) {
                    candidates.add(CleanupCandidate(
                        id = "drive_${file.id}_dup",
                        accountId = accountId,
                        provider = CloudProvider.GoogleDrive,
                        category = CleanupCategory.Duplicate,
                        providerItemId = file.id,
                        itemName = file.name,
                        sizeBytes = file.sizeBytes,
                        sourceAccount = account.email,
                        canRestore = true,
                    ))
                }

                // One real file matched by two queries is ONE candidate:
                // dedupe by provider id before counting items/bytes.
                val distinct = CloudJsonParsers.distinctByProviderItem(candidates) { it.providerItemId }
                itemsFound = distinct.size
                bytesFound = distinct.sumOf { it.sizeBytes }

                CloudScanOutcome(
                    result = ScanResult(
                        scanId = scanId,
                        provider = CloudProvider.GoogleDrive,
                        accountId = accountId,
                        status = ScanStatus.Completed,
                        itemsFound = itemsFound,
                        bytesFound = bytesFound,
                        bytesReclaimable = bytesFound,
                        completedAt = System.currentTimeMillis(),
                    ),
                    candidates = distinct,
                )
            } catch (e: CloudApiException) {
                CloudScanOutcome(
                    result = ScanResult(
                        scanId = scanId,
                        provider = CloudProvider.GoogleDrive,
                        accountId = accountId,
                        status = ScanStatus.Failed,
                        error = e.message ?: "Provider HTTP ${e.status}",
                    ),
                )
            } catch (e: Exception) {
                CloudScanOutcome(
                    result = ScanResult(
                        scanId = scanId,
                        provider = CloudProvider.GoogleDrive,
                        accountId = accountId,
                        status = ScanStatus.Failed,
                        error = e.message ?: "Unknown error",
                    ),
                )
            }
        }
    }

    /**
     * Paginated file listing. Provider errors throw [CloudApiException] and
     * fail the scan explicitly — never an empty "Completed" result.
     */
    private suspend fun listFiles(
        accessToken: String,
        query: String,
        pageSize: Int = 100,
    ): List<CloudJsonParsers.DriveFile> {
        val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
        val firstUrl = "$DRIVE_API_BASE/files?q=$encodedQuery&pageSize=$pageSize&fields=$FILE_FIELDS"
        val files = mutableListOf<CloudJsonParsers.DriveFile>()
        CloudHttp.paginate(firstUrl, accessToken, tokenParam = "pageToken") { body ->
            files += CloudJsonParsers.parseDriveFiles(body)
        }
        return files
    }

    /**
     * Find exact duplicate files by verified MD5 checksum across paginated
     * listing. Files without a checksum are never claimed as duplicates.
     */
    private suspend fun findExactDuplicates(accessToken: String): List<CloudJsonParsers.DriveFile> {
        val query = java.net.URLEncoder.encode("trashed = false", "UTF-8")
        val firstUrl = "$DRIVE_API_BASE/files?q=$query&pageSize=1000&fields=$FILE_FIELDS"
        val allFiles = mutableListOf<CloudJsonParsers.DriveFile>()
        CloudHttp.paginate(firstUrl, accessToken, tokenParam = "pageToken") { body ->
            allFiles += CloudJsonParsers.parseDriveFiles(body)
        }
        return CloudJsonParsers.exactDuplicates(allFiles)
    }

    /**
     * Move files to Trash (safe deletion).
     */
    suspend fun trashFiles(accountId: String, fileIds: List<String>): Int {
        return withContext(Dispatchers.IO) {
            val account = accountManager.getConnectedAccounts()
                .firstOrNull { it.id == accountId } ?: return@withContext 0
            val accessToken = accountManager.getValidAccessToken(account) ?: return@withContext 0

            var trashed = 0
            for (fileId in fileIds) {
                try {
                    val body = CloudHttp.mutateJson(
                        "$DRIVE_API_BASE/files/$fileId/trash", accessToken, method = "PATCH",
                    )
                    // Count only when the provider confirms the file is trashed.
                    if (body.optBoolean("trashed", false)) trashed++
                } catch (_: Exception) {
                    // Per-item failure: continue, but never count it as success.
                }
            }
            trashed
        }
    }

    /**
     * Re-query the provider after a trash request. true = trashed/gone,
     * false = still live, null = provider unreachable (never success).
     */
    suspend fun isTrashed(accountId: String, fileId: String): Boolean? {
        return withContext(Dispatchers.IO) {
            val account = accountManager.getConnectedAccounts()
                .firstOrNull { it.id == accountId } ?: return@withContext null
            val accessToken = accountManager.getValidAccessToken(account) ?: return@withContext null
            try {
                val url = java.net.URL("$DRIVE_API_BASE/files/$fileId?fields=trashed")
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 15_000
                connection.readTimeout = 20_000
                connection.setRequestProperty("Authorization", "Bearer $accessToken")
                val status = connection.responseCode
                val payload = (if (status in 200..299) connection.inputStream else connection.errorStream)
                    ?.bufferedReader(java.nio.charset.StandardCharsets.UTF_8)?.use { it.readText() }.orEmpty()
                connection.disconnect()
                if (status == 404) return@withContext true
                if (status !in 200..299) return@withContext null
                org.json.JSONObject(payload).optBoolean("trashed", false)
            } catch (_: Exception) {
                null
            }
        }
    }

    /**
     * Restore files from Trash.
     */
    suspend fun untrashFiles(accountId: String, fileIds: List<String>): Int {
        return withContext(Dispatchers.IO) {
            val account = accountManager.getConnectedAccounts()
                .firstOrNull { it.id == accountId } ?: return@withContext 0
            val accessToken = accountManager.getValidAccessToken(account) ?: return@withContext 0

            var restored = 0
            for (fileId in fileIds) {
                try {
                    val body = CloudHttp.mutateJson(
                        "$DRIVE_API_BASE/files/$fileId/untrash", accessToken, method = "PATCH",
                    )
                    // Count only when the provider confirms the file is live again.
                    if (!body.optBoolean("trashed", true)) restored++
                } catch (_: Exception) {
                    // Per-item failure: continue, but never count it as success.
                }
            }
            restored
        }
    }
}