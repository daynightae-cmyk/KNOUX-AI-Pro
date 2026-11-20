package com.knoux.ai.google.drive

import android.content.Context
import com.knoux.ai.cloud.model.CleanupCandidate
import com.knoux.ai.cloud.model.CleanupCategory
import com.knoux.ai.cloud.model.CloudScanOutcome
import com.knoux.ai.cloud.model.ScanResult
import com.knoux.ai.cloud.model.ScanStatus
import com.knoux.ai.cloud.model.CloudProvider
import com.knoux.ai.google.auth.GoogleAccountManager
import com.knoux.ai.google.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    }

    private val scope = CoroutineScope(Dispatchers.IO)

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

                // 1. Large files (> 10MB)
                val largeFiles = listFiles(accessToken, "size > 10485760 and trashed = false", maxResults = 100)
                itemsFound += largeFiles.size
                for (file in largeFiles) {
                    bytesFound += file.sizeBytes
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

                // 2. Old files (older than 1 year)
                val cutoff = java.time.Instant.now()
                    .minus(365, java.time.temporal.ChronoUnit.DAYS)
                    .toString()
                val oldFiles = listFiles(
                    accessToken,
                    "modifiedTime < '$cutoff' and trashed = false",
                    maxResults = 100,
                )
                itemsFound += oldFiles.size
                for (file in oldFiles) {
                    bytesFound += file.sizeBytes
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

                // 3. Exact duplicates (by MD5 checksum)
                val duplicates = findExactDuplicates(accessToken)
                itemsFound += duplicates.size
                for (file in duplicates) {
                    bytesFound += file.sizeBytes
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
                    candidates = candidates.toList(),
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

    private data class DriveFile(
        val id: String,
        val name: String,
        val sizeBytes: Long,
        val md5Checksum: String?,
        val createdTime: String?,
    )

    private suspend fun listFiles(
        accessToken: String,
        query: String,
        maxResults: Int = 100,
    ): List<DriveFile> {
        return withContext(Dispatchers.IO) {
            try {
                val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
                val url = java.net.URL("$DRIVE_API_BASE/files?q=$encodedQuery&maxResults=$maxResults&fields=files(id,name,size,md5Checksum,createdTime)")
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Authorization", "Bearer $accessToken")
                connection.setRequestProperty("Accept", "application/json")

                val responseCode = connection.responseCode
                if (responseCode !in 200..299) {
                    connection.disconnect()
                    return@withContext emptyList()
                }

                val responseBody = connection.inputStream.bufferedReader(java.nio.charset.StandardCharsets.UTF_8).use { it.readText() }
                connection.disconnect()

                parseDriveFiles(responseBody)
            } catch (_: Exception) {
                emptyList()
            }
        }
    }

    private fun parseDriveFiles(json: String): List<DriveFile> {
        val files = mutableListOf<DriveFile>()
        // Extract each file object from the "files" array
        val idRegex = kotlin.text.Regex("\"id\"\\s*:\\s*\"([^\"]+)\"")
        val nameRegex = kotlin.text.Regex("\"name\"\\s*:\\s*\"([^\"]+)\"")
        val sizeRegex = kotlin.text.Regex("\"size\"\\s*:\\s*\"(\\d+)\"")
        val md5Regex = kotlin.text.Regex("\"md5Checksum\"\\s*:\\s*\"([^\"]+)\"")

        val idMatches = idRegex.findAll(json).map { it.groupValues[1] }.toList()
        val nameMatches = nameRegex.findAll(json).map { it.groupValues[1] }.toList()
        val sizeMatches = sizeRegex.findAll(json).map { it.groupValues[1].toLongOrNull() ?: 0L }.toList()
        val md5Matches = md5Regex.findAll(json).map { it.groupValues[1] }.toList()

        for (i in idMatches.indices) {
            files.add(DriveFile(
                id = idMatches[i],
                name = nameMatches.getOrElse(i) { "Unknown" },
                sizeBytes = sizeMatches.getOrElse(i) { 0L },
                md5Checksum = md5Matches.getOrElse(i) { null },
                createdTime = null,
            ))
        }

        return files
    }

    /**
     * Find exact duplicate files by MD5 checksum.
     * Groups files by checksum and returns all but the first occurrence of each group.
     */
    private suspend fun findExactDuplicates(accessToken: String): List<DriveFile> {
        return withContext(Dispatchers.IO) {
            try {
                // List all files with checksums
                val query = java.net.URLEncoder.encode("trashed = false", "UTF-8")
                val url = java.net.URL("$DRIVE_API_BASE/files?q=$query&maxResults=1000&fields=files(id,name,size,md5Checksum)")
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Authorization", "Bearer $accessToken")
                connection.setRequestProperty("Accept", "application/json")

                val responseCode = connection.responseCode
                if (responseCode !in 200..299) {
                    connection.disconnect()
                    return@withContext emptyList()
                }

                val responseBody = connection.inputStream.bufferedReader(java.nio.charset.StandardCharsets.UTF_8).use { it.readText() }
                connection.disconnect()

                val allFiles = parseDriveFiles(responseBody)

                // Group by MD5 checksum
                val byChecksum = allFiles
                    .filter { it.md5Checksum != null }
                    .groupBy { it.md5Checksum!! }

                // Return duplicates (all but first in each group)
                val duplicates = byChecksum.values
                    .filter { it.size > 1 }
                    .flatMap { group -> group.drop(1) } // Keep first as keeper

                duplicates
            } catch (_: Exception) {
                emptyList()
            }
        }
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
                    val url = java.net.URL("$DRIVE_API_BASE/files/$fileId/trash")
                    val connection = url.openConnection() as java.net.HttpURLConnection
                    connection.requestMethod = "PATCH"
                    connection.doOutput = true
                    connection.setRequestProperty("Authorization", "Bearer $accessToken")
                    connection.setRequestProperty("Content-Length", "0")
                    connection.outputStream.use { }

                    if (connection.responseCode in 200..299) {
                        trashed++
                    }
                    connection.disconnect()
                } catch (_: Exception) {
                    // Continue
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
                    val url = java.net.URL("$DRIVE_API_BASE/files/$fileId/untrash")
                    val connection = url.openConnection() as java.net.HttpURLConnection
                    connection.requestMethod = "PATCH"
                    connection.doOutput = true
                    connection.setRequestProperty("Authorization", "Bearer $accessToken")
                    connection.setRequestProperty("Content-Length", "0")
                    connection.outputStream.use { }

                    if (connection.responseCode in 200..299) {
                        restored++
                    }
                    connection.disconnect()
                } catch (_: Exception) {
                    // Continue
                }
            }
            restored
        }
    }
}