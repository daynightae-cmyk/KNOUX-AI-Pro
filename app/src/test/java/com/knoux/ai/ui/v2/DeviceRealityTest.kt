package com.knoux.ai.ui.v2

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DeviceRealityTest {
    @get:Rule val temp = TemporaryFolder()

    @Test
    fun sameSizeFilesRequireMatchingBytes() {
        val names = listOf("keeper", "different", "copy", "unreadable", "truncated")
        val files = names.map { MediaFileFacts(it, 3L, null, false, it) }
        val bytes = mapOf("keeper" to "abc", "different" to "xyz", "copy" to "abc", "truncated" to "ab")

        val group = findExactMediaDuplicates(files) { bytes[it.name]?.byteInputStream() }.single()

        assertEquals(listOf("keeper", "copy"), group.uris)
        assertEquals(3L, group.reclaimableBytes)
    }

    @Test
    fun oversizedFilesStayOutsideTheComparisonBudget() {
        val files = listOf("large1", "large2").map { MediaFileFacts(it, 33L * 1024 * 1024, null, true, it) }
        assertTrue(findExactMediaDuplicates(files) { error("Out-of-scope file was opened") }.isEmpty())
    }

    @Test
    fun cacheCleanupReportsOnlyBytesActuallyDeleted() {
        val entry = temp.newFile("entry")
        entry.writeBytes(byteArrayOf(1, 2, 3))
        assertEquals(3L, deleteCachedEntry(entry))
        assertEquals(0L, deleteCachedEntry(entry))
    }
}
