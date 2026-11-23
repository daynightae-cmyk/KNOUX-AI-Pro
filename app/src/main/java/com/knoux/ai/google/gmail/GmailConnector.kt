package com.knoux.ai.google.gmail

import android.content.Context
import com.knoux.ai.cloud.CloudApiException
import com.knoux.ai.cloud.CloudHttp
import com.knoux.ai.cloud.CloudJsonParsers
import com.knoux.ai.cloud.model.CleanupCandidate
import com.knoux.ai.cloud.model.CleanupCategory
import com.knoux.ai.cloud.model.CloudScanOutcome
import com.knoux.ai.cloud.model.ScanResult
import com.knoux.ai.cloud.model.ScanStatus
import com.knoux.ai.google.auth.GoogleAccountManager
import com.knoux.ai.google.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Gmail Intelligence Cleaner connector (GOOGLE-02).
 * Uses Gmail API v1 to scan and classify emails:
 * old emails, large attachments, promotions, social, spam, duplicates.
 * All operations use Trash/Untrash — never Permanent Delete.
 */
class GmailConnector(
    private val context: Context,
    private val accountManager: GoogleAccountManager,
    private val authProvider: GoogleAuthProvider,
) {

    private companion object {
        const val GMAIL_API_BASE = "https://gmail.googleapis.com/gmail/v1"

        /** Bound on metadata fetches per query: each ref costs one API call. */
        const val MAX_DETAIL_FETCHES = 50
    }

    /**
     * Scan a Gmail account for cleanup candidates.
     * Returns a ScanResult with found items.
     */
    suspend fun scanAccount(accountId: String): ScanResult = scanWithCandidates(accountId).result

    suspend fun scanWithCandidates(accountId: String): CloudScanOutcome {
        return withContext(Dispatchers.IO) {
            val scanId = java.util.UUID.randomUUID().toString()
            val startTime = System.currentTimeMillis()

            try {
                val account = accountManager.getConnectedAccounts()
                    .firstOrNull { it.id == accountId }
                if (account == null) {
                    return@withContext CloudScanOutcome(
                        result = ScanResult(
                            scanId = scanId,
                            provider = com.knoux.ai.cloud.model.CloudProvider.Gmail,
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
                            provider = com.knoux.ai.cloud.model.CloudProvider.Gmail,
                            accountId = accountId,
                            status = ScanStatus.Failed,
                            error = "No valid access token",
                        ),
                    )

                val candidates = mutableListOf<CleanupCandidate>()

                // 1. Old emails (older than 1 year)
                val oldEmails = searchMessages(accessToken, account.email, "older_than:1y", maxResults = 50)
                for (msg in oldEmails) {
                    candidates.add(CleanupCandidate(
                        id = "gmail_${msg.id}",
                        accountId = accountId,
                        provider = com.knoux.ai.cloud.model.CloudProvider.Gmail,
                        category = CleanupCategory.OldEmail,
                        providerItemId = msg.id,
                        itemName = msg.subject,
                        sizeBytes = msg.sizeEstimate.toLong(),
                        sourceAccount = account.email,
                        canRestore = true,
                    ))
                }

                // 2. Large attachments (> 5MB)
                val largeAttachments = searchMessages(accessToken, account.email, "has:attachment larger:5M", maxResults = 50)
                for (msg in largeAttachments) {
                    candidates.add(CleanupCandidate(
                        id = "gmail_${msg.id}_attach",
                        accountId = accountId,
                        provider = com.knoux.ai.cloud.model.CloudProvider.Gmail,
                        category = CleanupCategory.LargeAttachment,
                        providerItemId = msg.id,
                        itemName = msg.subject,
                        sizeBytes = msg.sizeEstimate.toLong(),
                        sourceAccount = account.email,
                        canRestore = true,
                    ))
                }

                // 3. Promotions
                val promotions = searchMessages(accessToken, account.email, "category:promotions", maxResults = 50)
                for (msg in promotions) {
                    candidates.add(CleanupCandidate(
                        id = "gmail_${msg.id}_promo",
                        accountId = accountId,
                        provider = com.knoux.ai.cloud.model.CloudProvider.Gmail,
                        category = CleanupCategory.Promotion,
                        providerItemId = msg.id,
                        itemName = msg.subject,
                        sizeBytes = msg.sizeEstimate.toLong(),
                        sourceAccount = account.email,
                        canRestore = true,
                    ))
                }

                // 4. Social
                val social = searchMessages(accessToken, account.email, "category:social", maxResults = 50)
                for (msg in social) {
                    candidates.add(CleanupCandidate(
                        id = "gmail_${msg.id}_social",
                        accountId = accountId,
                        provider = com.knoux.ai.cloud.model.CloudProvider.Gmail,
                        category = CleanupCategory.Social,
                        providerItemId = msg.id,
                        itemName = msg.subject,
                        sizeBytes = msg.sizeEstimate.toLong(),
                        sourceAccount = account.email,
                        canRestore = true,
                    ))
                }

                // 5. Spam
                val spam = searchMessages(accessToken, account.email, "in:spam", maxResults = 50)
                for (msg in spam) {
                    candidates.add(CleanupCandidate(
                        id = "gmail_${msg.id}_spam",
                        accountId = accountId,
                        provider = com.knoux.ai.cloud.model.CloudProvider.Gmail,
                        category = CleanupCategory.Spam,
                        providerItemId = msg.id,
                        itemName = msg.subject,
                        sizeBytes = msg.sizeEstimate.toLong(),
                        sourceAccount = account.email,
                        canRestore = true,
                    ))
                }

                // One real message matched by two queries is ONE candidate.
                val distinct = CloudJsonParsers.distinctByProviderItem(candidates) { it.providerItemId }

                CloudScanOutcome(
                    result = ScanResult(
                        scanId = scanId,
                        provider = com.knoux.ai.cloud.model.CloudProvider.Gmail,
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
                        provider = com.knoux.ai.cloud.model.CloudProvider.Gmail,
                        accountId = accountId,
                        status = ScanStatus.Failed,
                        error = e.message ?: "Provider HTTP ${e.status}",
                    ),
                )
            } catch (e: Exception) {
                CloudScanOutcome(
                    result = ScanResult(
                        scanId = scanId,
                        provider = com.knoux.ai.cloud.model.CloudProvider.Gmail,
                        accountId = accountId,
                        status = ScanStatus.Failed,
                        error = e.message ?: "Unknown error",
                    ),
                )
            }
        }
    }

    /**
     * Search Gmail messages. messages.list returns only {id, threadId}; the
     * real sizeEstimate and Subject come from messages.get?format=metadata.
     * Provider errors throw [CloudApiException] — never an empty success.
     */
    private suspend fun searchMessages(
        accessToken: String,
        email: String,
        query: String,
        maxResults: Int = 50,
    ): List<GmailMessage> {
        val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
        val firstUrl = "$GMAIL_API_BASE/users/me/messages?q=$encodedQuery&maxResults=$maxResults"
        val refs = mutableListOf<CloudJsonParsers.GmailMessageRef>()
        CloudHttp.paginate(firstUrl, accessToken, tokenParam = "pageToken", maxPages = 2) { body ->
            refs += CloudJsonParsers.parseGmailMessageRefs(body)
        }
        // Bound the per-message metadata calls; a scan must stay affordable.
        return refs.take(MAX_DETAIL_FETCHES).mapNotNull { ref ->
            val detail = CloudHttp.getJson(
                "$GMAIL_API_BASE/users/me/messages/${ref.id}?format=metadata&metadataHeaders=Subject",
                accessToken,
            )
            CloudJsonParsers.parseGmailMessageDetail(detail)?.let {
                GmailMessage(it.id, it.threadId, it.subject, it.sizeEstimate)
            }
        }
    }

    private data class GmailMessage(
        val id: String,
        val threadId: String,
        val subject: String?,
        val sizeEstimate: Long,
    )

    /**
     * Move messages to Trash (safe deletion).
     * Returns the number of messages trashed.
     */
    suspend fun trashMessages(accountId: String, messageIds: List<String>): Int {
        return withContext(Dispatchers.IO) {
            val account = accountManager.getConnectedAccounts()
                .firstOrNull { it.id == accountId } ?: return@withContext 0
            val accessToken = accountManager.getValidAccessToken(account) ?: return@withContext 0

            var trashed = 0
            for (msgId in messageIds) {
                try {
                    val body = CloudHttp.mutateJson(
                        "$GMAIL_API_BASE/users/me/messages/$msgId/trash", accessToken, method = "POST",
                    )
                    // Count only when the provider response carries the TRASH label.
                    val labels = body.optJSONArray("labelIds")
                    if (labels != null && (0 until labels.length()).any { labels.optString(it) == "TRASH" }) {
                        trashed++
                    }
                } catch (_: Exception) {
                    // Per-item failure: continue, but never count it as success.
                }
            }
            trashed
        }
    }

    /**
     * Re-query the provider after a trash request. Returns true only when the
     * API confirms the message is in Trash (404 also means it no longer exists
     * in the mailbox), false when it is still live, and null when the provider
     * cannot be reached — null is never treated as success.
     */
    suspend fun isTrashed(accountId: String, messageId: String): Boolean? {
        return withContext(Dispatchers.IO) {
            val account = accountManager.getConnectedAccounts()
                .firstOrNull { it.id == accountId } ?: return@withContext null
            val accessToken = accountManager.getValidAccessToken(account) ?: return@withContext null
            try {
                val url = java.net.URL("$GMAIL_API_BASE/users/me/messages/$messageId?format=minimal")
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
                val labels = org.json.JSONObject(payload).optJSONArray("labelIds") ?: return@withContext false
                (0 until labels.length()).any { labels.optString(it) == "TRASH" }
            } catch (_: Exception) {
                null
            }
        }
    }

    /**
     * Restore messages from Trash.
     */
    suspend fun untrashMessages(accountId: String, messageIds: List<String>): Int {
        return withContext(Dispatchers.IO) {
            val account = accountManager.getConnectedAccounts()
                .firstOrNull { it.id == accountId } ?: return@withContext 0
            val accessToken = accountManager.getValidAccessToken(account) ?: return@withContext 0

            var restored = 0
            for (msgId in messageIds) {
                try {
                    val body = CloudHttp.mutateJson(
                        "$GMAIL_API_BASE/users/me/messages/$msgId/untrash", accessToken, method = "POST",
                    )
                    // Count only when the provider response no longer carries TRASH.
                    val labels = body.optJSONArray("labelIds")
                    val stillTrashed = labels != null &&
                        (0 until labels.length()).any { labels.optString(it) == "TRASH" }
                    if (labels != null && !stillTrashed) restored++
                } catch (_: Exception) {
                    // Per-item failure: continue, but never count it as success.
                }
            }
            restored
        }
    }
}