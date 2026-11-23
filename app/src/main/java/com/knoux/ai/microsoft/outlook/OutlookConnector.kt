package com.knoux.ai.microsoft.outlook

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
import com.knoux.ai.microsoft.auth.MicrosoftAccountManager
import com.knoux.ai.microsoft.auth.MicrosoftAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Outlook Email Cleaner connector (MS-02).
 * Uses Microsoft Graph v1.0 to scan mail:
 * old emails, large emails, attachments, duplicates, junk mail.
 * Uses Move to Deleted Items — never Permanent Delete.
 */
class OutlookConnector(
    private val context: Context,
    private val accountManager: MicrosoftAccountManager,
    private val authProvider: MicrosoftAuthProvider,
) {

    private companion object {
        const val GRAPH_BASE = "https://graph.microsoft.com/v1.0"
    }

    /**
     * Scan an Outlook account for cleanup candidates.
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
                            provider = CloudProvider.Outlook,
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
                            provider = CloudProvider.Outlook,
                            accountId = accountId,
                            status = ScanStatus.Failed,
                            error = "No valid access token",
                        ),
                    )
                val candidates = mutableListOf<CleanupCandidate>()

                // 1. Old emails (> 1 year) — cutoff measured at scan time,
                // never a hardcoded calendar date.
                val oldCutoff = java.time.Instant.now()
                    .minus(365, java.time.temporal.ChronoUnit.DAYS)
                    .toString()
                val oldEmails = searchMessages(accessToken, "receivedDateTime lt $oldCutoff", maxResults = 50)
                for (msg in oldEmails) {
                    candidates.add(CleanupCandidate(
                        id = "outlook_${msg.id}",
                        accountId = accountId,
                        provider = CloudProvider.Outlook,
                        category = CleanupCategory.OldEmail,
                        providerItemId = msg.id,
                        itemName = msg.subject,
                        sizeBytes = msg.sizeBytes,
                        sourceAccount = account.email,
                        canRestore = true,
                    ))
                }

                // 2. Large emails (> 5MB)
                val largeEmails = searchMessages(accessToken, "size gt 5242880", maxResults = 50)
                for (msg in largeEmails) {
                    candidates.add(CleanupCandidate(
                        id = "outlook_${msg.id}_large",
                        accountId = accountId,
                        provider = CloudProvider.Outlook,
                        category = CleanupCategory.LargeAttachment,
                        providerItemId = msg.id,
                        itemName = msg.subject,
                        sizeBytes = msg.sizeBytes,
                        sourceAccount = account.email,
                        canRestore = true,
                    ))
                }

                // 3. Junk mail — resolve the real junkemail folder id first;
                // Graph $filter needs the opaque id, not the well-known name.
                val junkFolder = CloudHttp.getJson(
                    "$GRAPH_BASE/me/mailFolders/junkemail?%24select=id", accessToken,
                )
                val junkFolderId = junkFolder.optString("id").takeIf { it.isNotBlank() }
                val junk = if (junkFolderId != null) {
                    searchMessages(accessToken, "parentFolderId eq '$junkFolderId'", maxResults = 50)
                } else emptyList()
                for (msg in junk) {
                    candidates.add(CleanupCandidate(
                        id = "outlook_${msg.id}_junk",
                        accountId = accountId,
                        provider = CloudProvider.Outlook,
                        category = CleanupCategory.Spam,
                        providerItemId = msg.id,
                        itemName = msg.subject,
                        sizeBytes = msg.sizeBytes,
                        sourceAccount = account.email,
                        canRestore = true,
                    ))
                }

                // One real message matched by two queries is ONE candidate.
                val distinct = CloudJsonParsers.distinctByProviderItem(candidates) { it.providerItemId }

                CloudScanOutcome(
                    result = ScanResult(
                        scanId = scanId,
                        provider = CloudProvider.Outlook,
                        accountId = accountId,
                        status = ScanStatus.Completed,
                        itemsFound = distinct.size,
                        bytesFound = distinct.sumOf { it.sizeBytes },
                        bytesReclaimable = distinct.sumOf { it.sizeBytes },
                        completedAt = System.currentTimeMillis(),
                    ),
                    candidates = distinct,
                )
            } catch (e: CloudApiException) {
                CloudScanOutcome(
                    result = ScanResult(
                        scanId = scanId,
                        provider = CloudProvider.Outlook,
                        accountId = accountId,
                        status = ScanStatus.Failed,
                        error = e.message ?: "Provider HTTP ${e.status}",
                    ),
                )
            } catch (e: Exception) {
                CloudScanOutcome(
                    result = ScanResult(
                        scanId = scanId,
                        provider = CloudProvider.Outlook,
                        accountId = accountId,
                        status = ScanStatus.Failed,
                        error = e.message ?: "Unknown error",
                    ),
                )
            }
        }
    }

    private suspend fun searchMessages(
        accessToken: String,
        filter: String,
        maxResults: Int = 50,
    ): List<CloudJsonParsers.OutlookMessage> {
        val encodedFilter = java.net.URLEncoder.encode(filter, "UTF-8")
        val url = "$GRAPH_BASE/me/messages?`\$filter=$encodedFilter&`\$top=$maxResults&`\$select=id,subject,size,receivedDateTime"
        val body = CloudHttp.getJson(url, accessToken)
        return CloudJsonParsers.parseOutlookMessages(body)
    }

    /**
     * Move emails to Deleted Items (safe deletion).
     */
    suspend fun moveToDeletedItems(accountId: String, messageIds: List<String>): Int {
        return withContext(Dispatchers.IO) {
            val account = accountManager.getConnectedAccounts()
                .firstOrNull { it.id == accountId } ?: return@withContext 0
            val accessToken = accountManager.getValidAccessToken(account) ?: return@withContext 0

            var moved = 0
            for (msgId in messageIds) {
                try {
                    // Graph move returns the moved message; count only when the
                    // response confirms a new parent folder (real state change).
                    val body = CloudHttp.mutateJson(
                        "$GRAPH_BASE/me/messages/$msgId/move", accessToken,
                        method = "POST",
                        jsonBody = """{"destinationId":"deleteditems"}""",
                    )
                    if (body.optString("parentFolderId").isNotBlank()) moved++
                } catch (_: Exception) {
                    // Per-item failure: continue, but never count it as success.
                }
            }
            moved
        }
    }

    /**
     * Re-query the provider after a move request. true = in Deleted Items or
     * gone, false = still live, null = provider unreachable (never success).
     */
    suspend fun isInDeletedItems(accountId: String, messageId: String): Boolean? {
        return withContext(Dispatchers.IO) {
            val account = accountManager.getConnectedAccounts()
                .firstOrNull { it.id == accountId } ?: return@withContext null
            val accessToken = accountManager.getValidAccessToken(account) ?: return@withContext null
            try {
                val (folderStatus, folderPayload) = graphGet(
                    "$GRAPH_BASE/me/mailFolders/deleteditems?%24select=id",
                    accessToken,
                )
                if (folderStatus !in 200..299) return@withContext null
                val deletedFolderId = org.json.JSONObject(folderPayload)
                    .optString("id").takeIf { it.isNotEmpty() } ?: return@withContext null

                val (status, payload) = graphGet(
                    "$GRAPH_BASE/me/messages/$messageId?%24select=parentFolderId",
                    accessToken,
                )
                when {
                    status == 404 -> true
                    status !in 200..299 -> null
                    else -> org.json.JSONObject(payload).optString("parentFolderId") == deletedFolderId
                }
            } catch (_: Exception) {
                null
            }
        }
    }

    private fun graphGet(url: String, accessToken: String): Pair<Int, String> {
        val connection = java.net.URL(url).openConnection() as java.net.HttpURLConnection
        return try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 15_000
            connection.readTimeout = 20_000
            connection.setRequestProperty("Authorization", "Bearer $accessToken")
            val status = connection.responseCode
            val payload = (if (status in 200..299) connection.inputStream else connection.errorStream)
                ?.bufferedReader(java.nio.charset.StandardCharsets.UTF_8)?.use { it.readText() }.orEmpty()
            status to payload
        } finally {
            connection.disconnect()
        }
    }
}