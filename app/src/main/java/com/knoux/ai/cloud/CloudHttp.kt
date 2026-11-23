package com.knoux.ai.cloud

import com.knoux.ai.security.ProductionSecurity
import com.knoux.ai.security.secureRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Shared HTTP + JSON plumbing for cloud provider connectors.
 *
 * Contract:
 *  - Every non-2xx response throws [CloudApiException] with the status and a
 *    REDACTED provider message. A failed provider call is never silently
 *    converted into an empty successful result.
 *  - GETs retry transient failures (408/425/429/5xx, IO) through
 *    [secureRequest]; mutating calls execute exactly once.
 *  - All parsing uses org.json, so field-to-record alignment is structural,
 *    not positional regex matching (which mis-pairs fields when any record
 *    omits an optional field such as md5Checksum).
 */
class CloudApiException(
    val status: Int,
    val providerCode: String?,
    message: String,
) : Exception(message)

/** One page of a provider list response. */
data class CloudPage(val body: JSONObject, val nextPageToken: String?)

object CloudHttp {

    /** Bounded pagination: never loop forever on a misbehaving provider. */
    const val MAX_PAGES = 10

    /**
     * GET with bearer-style credential. [headerName] defaults to
     * "Authorization" (sent as `Bearer <credential>`); pass e.g.
     * "x-goog-api-key" for providers that take a raw header value. An empty
     * [credential] sends no auth header (local providers such as Ollama).
     */
    suspend fun getJson(
        url: String,
        credential: String,
        headerName: String = "Authorization",
    ): JSONObject =
        secureRequest(maxRetries = 3, idempotent = true) {
            withContext(Dispatchers.IO) {
                val connection = (URL(url).openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 15_000
                    readTimeout = 20_000
                    // Never follow redirects with a credential attached.
                    instanceFollowRedirects = false
                    if (credential.isNotBlank()) {
                        setRequestProperty(
                            headerName,
                            if (headerName.equals("Authorization", ignoreCase = true)) "Bearer $credential" else credential,
                        )
                    }
                    setRequestProperty("Accept", "application/json")
                }
                try {
                    readResponse(connection)
                } finally {
                    connection.disconnect()
                }
            }
        }

    /**
     * Executes a mutating request exactly once. Returns the parsed response
     * body; throws [CloudApiException] on any non-2xx status.
     */
    suspend fun mutateJson(
        url: String,
        accessToken: String,
        method: String,
        jsonBody: String? = null,
    ): JSONObject = withContext(Dispatchers.IO) {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 15_000
            readTimeout = 20_000
            instanceFollowRedirects = false
            setRequestProperty("Authorization", "Bearer $accessToken")
            setRequestProperty("Accept", "application/json")
            if (jsonBody != null) {
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
            } else {
                doOutput = true
                setRequestProperty("Content-Length", "0")
            }
        }
        try {
            connection.outputStream.use { stream ->
                if (jsonBody != null) stream.write(jsonBody.toByteArray(Charsets.UTF_8))
            }
            readResponse(connection)
        } finally {
            connection.disconnect()
        }
    }

    private fun readResponse(connection: HttpURLConnection): JSONObject {
        val status = connection.responseCode
        val stream = if (status in 200..299) connection.inputStream else connection.errorStream
        val payload = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
        if (status !in 200..299) {
            val error = runCatching { JSONObject(payload).optJSONObject("error") }.getOrNull()
            val providerCode = error?.optString("status")?.takeIf { it.isNotBlank() }
                ?: error?.optString("code")?.takeIf { it.isNotBlank() }
            // Redact: provider error bodies can echo tokens/PII from the request.
            val safeMessage = ProductionSecurity.sanitizeForLog(
                error?.optString("message") ?: payload, maxLength = 200,
            )
            throw CloudApiException(status, providerCode, "Provider HTTP $status: $safeMessage")
        }
        return runCatching { JSONObject(payload) }.getOrElse { JSONObject() }
    }

    /**
     * Follows pageToken pagination up to [maxPages], invoking [onPage] with
     * each page body. Stops when the provider stops returning a token.
     */
    suspend fun paginate(
        firstUrl: String,
        credential: String,
        tokenParam: String,
        headerName: String = "Authorization",
        maxPages: Int = MAX_PAGES,
        onPage: (JSONObject) -> Unit,
    ) {
        var url = firstUrl
        var pages = 0
        while (pages < maxPages) {
            val body = getJson(url, credential, headerName)
            onPage(body)
            pages++
            val next = body.optString("nextPageToken").takeIf { it.isNotBlank() } ?: break
            url = firstUrl + "&" + tokenParam + "=" +
                java.net.URLEncoder.encode(next, "UTF-8")
        }
    }
}

/**
 * Pure parsing helpers, JVM-testable. Every record is read structurally from
 * its own JSON object; a missing optional field never shifts other records.
 */
object CloudJsonParsers {

    data class DriveFile(
        val id: String,
        val name: String,
        val sizeBytes: Long,
        val md5Checksum: String?,
        val createdTime: String?,
    )

    fun parseDriveFiles(body: JSONObject): List<DriveFile> {
        val array = body.optJSONArray("files") ?: return emptyList()
        return (0 until array.length()).mapNotNull { i ->
            val obj = array.optJSONObject(i) ?: return@mapNotNull null
            val id = obj.optString("id").takeIf { it.isNotBlank() } ?: return@mapNotNull null
            DriveFile(
                id = id,
                name = obj.optString("name", "Unknown"),
                // Drive returns size as a string; Google Docs editors have no size.
                sizeBytes = obj.optString("size").toLongOrNull() ?: 0L,
                md5Checksum = obj.optString("md5Checksum").takeIf { it.isNotBlank() },
                createdTime = obj.optString("createdTime").takeIf { it.isNotBlank() },
            )
        }
    }

    /**
     * Groups by verified MD5 identity and returns all-but-first per group.
     * Files without a checksum are never claimed as duplicates.
     */
    fun exactDuplicates(files: List<DriveFile>): List<DriveFile> =
        files.filter { it.md5Checksum != null }
            .groupBy { it.md5Checksum!! }
            .values
            .filter { it.size > 1 }
            .flatMap { group -> group.drop(1) }

    data class GmailMessageRef(val id: String, val threadId: String)

    /** messages.list returns only {id, threadId}; sizes require messages.get. */
    fun parseGmailMessageRefs(body: JSONObject): List<GmailMessageRef> {
        val array = body.optJSONArray("messages") ?: return emptyList()
        return (0 until array.length()).mapNotNull { i ->
            val obj = array.optJSONObject(i) ?: return@mapNotNull null
            val id = obj.optString("id").takeIf { it.isNotBlank() } ?: return@mapNotNull null
            GmailMessageRef(id, obj.optString("threadId"))
        }
    }

    data class GmailMessageDetail(
        val id: String,
        val threadId: String,
        val subject: String?,
        val sizeEstimate: Long,
    )

    /** messages.get?format=metadata — real sizeEstimate and Subject header. */
    fun parseGmailMessageDetail(body: JSONObject): GmailMessageDetail? {
        val id = body.optString("id").takeIf { it.isNotBlank() } ?: return null
        val headers = body.optJSONObject("payload")?.optJSONArray("headers")
        var subject: String? = null
        if (headers != null) {
            for (i in 0 until headers.length()) {
                val header = headers.optJSONObject(i) ?: continue
                if (header.optString("name").equals("Subject", ignoreCase = true)) {
                    subject = header.optString("value").takeIf { it.isNotBlank() }
                    break
                }
            }
        }
        return GmailMessageDetail(
            id = id,
            threadId = body.optString("threadId"),
            subject = subject,
            sizeEstimate = body.optLong("sizeEstimate", 0L),
        )
    }

    data class OutlookMessage(
        val id: String,
        val subject: String?,
        val sizeBytes: Long,
        val receivedDateTime: String?,
    )

    fun parseOutlookMessages(body: JSONObject): List<OutlookMessage> {
        val array = body.optJSONArray("value") ?: return emptyList()
        return (0 until array.length()).mapNotNull { i ->
            val obj = array.optJSONObject(i) ?: return@mapNotNull null
            val id = obj.optString("id").takeIf { it.isNotBlank() } ?: return@mapNotNull null
            OutlookMessage(
                id = id,
                subject = obj.optString("subject").takeIf { it.isNotBlank() },
                sizeBytes = obj.optLong("size", 0L),
                receivedDateTime = obj.optString("receivedDateTime").takeIf { it.isNotBlank() },
            )
        }
    }

    /**
     * Deduplicates candidates by provider item id, keeping the first category
     * seen. A message matched by two queries is ONE cleanup candidate: counting
     * it twice inflates itemsFound/bytesFound with the same real bytes.
     */
    fun <T> distinctByProviderItem(
        candidates: List<T>,
        providerItemId: (T) -> String,
    ): List<T> {
        val seen = HashSet<String>()
        return candidates.filter { seen.add(providerItemId(it)) }
    }
}
