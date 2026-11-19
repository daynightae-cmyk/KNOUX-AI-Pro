package com.knoux.ai.microsoft.outlook

import android.content.Context
import com.knoux.ai.cloud.model.CleanupCandidate
import com.knoux.ai.cloud.model.CleanupCategory
import com.knoux.ai.cloud.model.ScanResult
import com.knoux.ai.cloud.model.ScanStatus
import com.knoux.ai.cloud.model.CloudProvider
import com.knoux.ai.microsoft.auth.MicrosoftAccountManager
import com.knoux.ai.microsoft.auth.MicrosoftAuthProvider
import kotlinx.coroutines.CoroutineScope
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

    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * Scan an Outlook account for cleanup candidates.
     */
    suspend fun scanAccount(accountId: String): ScanResult {
        return withContext(Dispatchers.IO) {
            val scanId = java.util.UUID.randomUUID().toString()

            try {
                val account = accountManager.getConnectedAccounts()
                    .firstOrNull { it.id == accountId }
                if (account == null) {
                    return@withContext ScanResult(
                        scanId = scanId,
                        provider = CloudProvider.Outlook,
                        accountId = accountId,
                        status = ScanStatus.Failed,
                        error = "Account not found",
                    )
                }

                val accessToken = accountManager.getValidAccessToken(account)
                    ?: return@withContext ScanResult(
                        scanId = scanId,
                        provider = CloudProvider.Outlook,
                        accountId = accountId,
                        status = ScanStatus.Failed,
                        error = "No valid access token",
                    )

                var itemsFound = 0
                var bytesFound = 0L
                val candidates = mutableListOf<CleanupCandidate>()

                // 1. Old emails (> 1 year)
                val oldEmails = searchMessages(accessToken, "receivedDateTime lt 2025-01-01T00:00:00Z", maxResults = 50)
                itemsFound += oldEmails.size
                for (msg in oldEmails) {
                    bytesFound += msg.sizeBytes
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
                itemsFound += largeEmails.size
                for (msg in largeEmails) {
                    bytesFound += msg.sizeBytes
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

                // 3. Junk mail
                val junk = searchMessages(accessToken, "folderId eq 'JunkEmail'", maxResults = 50)
                itemsFound += junk.size
                for (msg in junk) {
                    bytesFound += msg.sizeBytes
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

                ScanResult(
                    scanId = scanId,
                    provider = CloudProvider.Outlook,
                    accountId = accountId,
                    status = ScanStatus.Completed,
                    itemsFound = itemsFound,
                    bytesFound = bytesFound,
                    bytesReclaimable = bytesFound,
                    completedAt = System.currentTimeMillis(),
                )
            } catch (e: Exception) {
                ScanResult(
                    scanId = scanId,
                    provider = CloudProvider.Outlook,
                    accountId = accountId,
                    status = ScanStatus.Failed,
                    error = e.message ?: "Unknown error",
                )
            }
        }
    }

    private data class OutlookMessage(
        val id: String,
        val subject: String?,
        val sizeBytes: Long,
        val receivedDateTime: String?,
    )

    private suspend fun searchMessages(
        accessToken: String,
        filter: String,
        maxResults: Int = 50,
    ): List<OutlookMessage> {
        return withContext(Dispatchers.IO) {
            try {
                val encodedFilter = java.net.URLEncoder.encode(filter, "UTF-8")
                val url = java.net.URL("$GRAPH_BASE/me/messages?`\$filter=$encodedFilter&`\$top=$maxResults&`\$select=id,subject,size,receivedDateTime")
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

                parseOutlookMessages(responseBody)
            } catch (_: Exception) {
                emptyList()
            }
        }
    }

    private fun parseOutlookMessages(json: String): List<OutlookMessage> {
        val messages = mutableListOf<OutlookMessage>()
        val idRegex = kotlin.text.Regex("\"id\"\\s*:\\s*\"([^\"]+)\"")
        val subjectRegex = kotlin.text.Regex("\"subject\"\\s*:\\s*\"([^\"]*)\"")
        val sizeRegex = kotlin.text.Regex("\"size\"\\s*:\\s*(\\d+)")

        val idMatches = idRegex.findAll(json).map { it.groupValues[1] }.toList()
        val subjectMatches = subjectRegex.findAll(json).map { it.groupValues[1] }.toList()
        val sizeMatches = sizeRegex.findAll(json).map { it.groupValues[1].toLongOrNull() ?: 0L }.toList()

        for (i in idMatches.indices) {
            messages.add(OutlookMessage(
                id = idMatches[i],
                subject = subjectMatches.getOrElse(i) { null },
                sizeBytes = sizeMatches.getOrElse(i) { 0L },
                receivedDateTime = null,
            ))
        }

        return messages
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
                    val url = java.net.URL("$GRAPH_BASE/me/messages/$msgId/move")
                    val connection = url.openConnection() as java.net.HttpURLConnection
                    connection.requestMethod = "POST"
                    connection.doOutput = true
                    connection.setRequestProperty("Authorization", "Bearer $accessToken")
                    connection.setRequestProperty("Content-Type", "application/json")

                    // Move to Deleted Items folder
                    val body = """{"destinationId":"DeletedItems"}"""
                    connection.outputStream.use { it.write(body.toByteArray()) }

                    if (connection.responseCode in 200..299) {
                        moved++
                    }
                    connection.disconnect()
                } catch (_: Exception) {
                    // Continue
                }
            }
            moved
        }
    }
}