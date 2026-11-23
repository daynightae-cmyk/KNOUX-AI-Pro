package com.knoux.ai.cloud

import com.knoux.ai.google.auth.GoogleTokenParser
import com.knoux.ai.microsoft.auth.MicrosoftTokenParser
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * JVM contract tests for the cloud connector data layer: structural JSON
 * parsing (no positional regex mis-pairing), verified-identity duplicates,
 * candidate dedup, and OAuth token-response parsing for both providers.
 * All fixtures are synthetic; no live credentials or provider calls.
 */
class CloudConnectorContractTest {

    // ---------------------------------------------------------- Drive parsing

    @Test
    fun driveFilesKeepFieldAlignmentWhenOptionalFieldsAreMissing() {
        // Google Docs editors have no size/md5; the old positional regex
        // shifted every later record's checksum onto the wrong file.
        val body = JSONObject().put("files", JSONArray()
            .put(JSONObject().put("id", "doc1").put("name", "Doc without size"))
            .put(JSONObject().put("id", "bin1").put("name", "real.bin")
                .put("size", "10485760").put("md5Checksum", "aaa"))
            .put(JSONObject().put("id", "bin2").put("name", "copy.bin")
                .put("size", "10485760").put("md5Checksum", "aaa")))
        val files = CloudJsonParsers.parseDriveFiles(body)
        assertEquals(3, files.size)
        assertEquals("doc1", files[0].id)
        assertEquals(0L, files[0].sizeBytes)
        assertNull(files[0].md5Checksum)
        assertEquals("aaa", files[1].md5Checksum)
        assertEquals("aaa", files[2].md5Checksum)
        assertEquals(10485760L, files[2].sizeBytes)
    }

    @Test
    fun driveDuplicatesRequireVerifiedMd5Identity() {
        val body = JSONObject().put("files", JSONArray()
            .put(JSONObject().put("id", "a").put("size", "5").put("md5Checksum", "x"))
            .put(JSONObject().put("id", "b").put("size", "5").put("md5Checksum", "x"))
            .put(JSONObject().put("id", "c").put("size", "5").put("md5Checksum", "x"))
            .put(JSONObject().put("id", "d").put("size", "5")) // no checksum
            .put(JSONObject().put("id", "e").put("size", "5")) // no checksum
            .put(JSONObject().put("id", "f").put("size", "9").put("md5Checksum", "y")))
        val duplicates = CloudJsonParsers.exactDuplicates(CloudJsonParsers.parseDriveFiles(body))
        // Keeper = first of group; checksum-less files are NEVER duplicates.
        assertEquals(listOf("b", "c"), duplicates.map { it.id })
    }

    @Test
    fun driveParsingHandlesEmptyAndMalformedPages() {
        assertTrue(CloudJsonParsers.parseDriveFiles(JSONObject()).isEmpty())
        assertTrue(CloudJsonParsers.parseDriveFiles(JSONObject().put("files", JSONArray())).isEmpty())
        // A record without id is dropped, not invented.
        val body = JSONObject().put("files", JSONArray().put(JSONObject().put("name", "orphan")))
        assertTrue(CloudJsonParsers.parseDriveFiles(body).isEmpty())
    }

    // ---------------------------------------------------------- Gmail parsing

    @Test
    fun gmailListRefsAreIdAndThreadOnly() {
        val body = JSONObject().put("messages", JSONArray()
            .put(JSONObject().put("id", "m1").put("threadId", "t1"))
            .put(JSONObject().put("id", "m2").put("threadId", "t2")))
            .put("nextPageToken", "PAGE2")
        val refs = CloudJsonParsers.parseGmailMessageRefs(body)
        assertEquals(listOf("m1", "m2"), refs.map { it.id })
        assertEquals(listOf("t1", "t2"), refs.map { it.threadId })
        assertEquals("PAGE2", body.optString("nextPageToken"))
    }

    @Test
    fun gmailDetailExtractsRealSizeAndSubjectHeader() {
        val body = JSONObject()
            .put("id", "m1").put("threadId", "t1").put("sizeEstimate", 7_300_000)
            .put("payload", JSONObject().put("headers", JSONArray()
                .put(JSONObject().put("name", "From").put("value", "fixture@example.com"))
                .put(JSONObject().put("name", "Subject").put("value", "عقد إيجار قديم"))))
        val detail = CloudJsonParsers.parseGmailMessageDetail(body)!!
        assertEquals(7_300_000L, detail.sizeEstimate)
        assertEquals("عقد إيجار قديم", detail.subject)
        // Missing size is 0, never invented.
        val noSize = CloudJsonParsers.parseGmailMessageDetail(JSONObject().put("id", "m2"))!!
        assertEquals(0L, noSize.sizeEstimate)
        assertNull(noSize.subject)
        assertNull(CloudJsonParsers.parseGmailMessageDetail(JSONObject()))
    }

    // -------------------------------------------------------- Outlook parsing

    @Test
    fun outlookMessagesKeepPerRecordAlignment() {
        val body = JSONObject().put("value", JSONArray()
            .put(JSONObject().put("id", "o1").put("size", 100).put("receivedDateTime", "2024-01-01T00:00:00Z"))
            .put(JSONObject().put("id", "o2").put("subject", "no size record"))
            .put(JSONObject().put("id", "o3").put("subject", "big").put("size", 9_000_000)))
        val messages = CloudJsonParsers.parseOutlookMessages(body)
        assertEquals(3, messages.size)
        assertEquals(100L, messages[0].sizeBytes)
        assertNull(messages[0].subject)
        assertEquals(0L, messages[1].sizeBytes)
        assertEquals("no size record", messages[1].subject)
        assertEquals(9_000_000L, messages[2].sizeBytes)
        assertEquals("2024-01-01T00:00:00Z", messages[0].receivedDateTime)
        assertNull(messages[2].receivedDateTime)
    }

    // ------------------------------------------------------- candidate dedup

    @Test
    fun sameProviderItemMatchedByTwoQueriesCountsOnce() {
        data class Candidate(val providerItemId: String, val category: String, val bytes: Long)
        val candidates = listOf(
            Candidate("m1", "OldEmail", 500),
            Candidate("m1", "LargeAttachment", 500), // same real message
            Candidate("m2", "Promotion", 300),
        )
        val distinct = CloudJsonParsers.distinctByProviderItem(candidates) { it.providerItemId }
        assertEquals(2, distinct.size)
        assertEquals(800L, distinct.sumOf { it.bytes })
        assertEquals("OldEmail", distinct.first { it.providerItemId == "m1" }.category)
    }

    // ----------------------------------------------------- OAuth token parsing

    @Test
    fun googleTokenResponseParsesNumericExpiryAndOptionalRefresh() {
        val json = JSONObject()
            .put("access_token", "ya29.fixture")
            .put("expires_in", 3599)
            .put("token_type", "Bearer")
            .put("scope", "https://www.googleapis.com/auth/drive.readonly")
            .toString()
        val token = GoogleTokenParser.parse(json, nowMs = 1_000L)
        assertEquals("ya29.fixture", token.accessToken)
        assertNull(token.refreshToken)
        assertEquals(3599L, token.expiresInSeconds)
        assertEquals(1_000L + 3_599_000L, token.expiresAtMs)

        val withRefresh = JSONObject(json).put("refresh_token", "1//fixture").toString()
        assertEquals("1//fixture", GoogleTokenParser.parse(withRefresh, 0L).refreshToken)
    }

    @Test
    fun googleTokenResponseWithoutAccessTokenIsRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            GoogleTokenParser.parse(JSONObject().put("error", "invalid_grant").toString(), 0L)
        }
    }

    @Test
    fun microsoftTokenResponseHandlesStringAndNumericExpiry() {
        // AAD frequently returns expires_in as a quoted string.
        val stringExpiry = JSONObject()
            .put("access_token", "EwBwFixture").put("expires_in", "3599")
            .put("refresh_token", "M.R3_FIXTURE").put("scope", "Mail.Read offline_access")
            .toString()
        val token = MicrosoftTokenParser.parse(stringExpiry, nowMs = 2_000L)
        assertEquals(3599L, token.expiresInSeconds)
        assertEquals("M.R3_FIXTURE", token.refreshToken)
        assertEquals(2_000L + 3_599_000L, token.expiresAtMs)

        val numericExpiry = JSONObject(stringExpiry).put("expires_in", 600).toString()
        assertEquals(600L, MicrosoftTokenParser.parse(numericExpiry, 0L).expiresInSeconds)

        assertThrows(IllegalArgumentException::class.java) {
            MicrosoftTokenParser.parse(JSONObject().put("error", "invalid_grant").toString(), 0L)
        }
    }

    // ---------------------------------------------------------- source contracts

    @Test
    fun connectorsNoLongerSwallowProviderErrorsIntoEmptySuccess() {
        listOf(
            "src/main/java/com/knoux/ai/google/drive/GoogleDriveConnector.kt",
            "src/main/java/com/knoux/ai/google/gmail/GmailConnector.kt",
            "src/main/java/com/knoux/ai/microsoft/outlook/OutlookConnector.kt",
        ).forEach { path ->
            val text = contractFile(path).readText()
            assertFalse("$path must not regex-parse provider JSON", text.contains("findAll(json)"))
            assertTrue("$path must surface CloudApiException", text.contains("catch (e: CloudApiException)"))
            assertTrue("$path must dedupe candidates", text.contains("distinctByProviderItem"))
        }
    }

    @Test
    fun outlookOldEmailCutoffIsMeasuredNotHardcoded() {
        val text = contractFile("src/main/java/com/knoux/ai/microsoft/outlook/OutlookConnector.kt").readText()
        assertFalse(text.contains("2025-01-01T00:00:00Z"))
        assertTrue(text.contains("java.time.Instant.now()"))
    }

    private fun contractFile(relativePath: String): java.io.File {
        val workingDirectory = java.io.File(System.getProperty("user.dir")).absoluteFile
        return listOf(workingDirectory, workingDirectory.parentFile)
            .map { java.io.File(it, "app/$relativePath").absoluteFile }
            .firstOrNull { it.isFile }
            ?: error("Cannot locate app contract: $relativePath from $workingDirectory")
    }
}
