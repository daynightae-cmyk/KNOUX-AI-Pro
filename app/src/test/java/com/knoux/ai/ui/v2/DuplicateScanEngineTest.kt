package com.knoux.ai.ui.v2

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.util.Arrays

/** JVM-only tests: no Context, MediaStore, Bitmap or Android runtime invocation. */
class DuplicateScanEngineTest {
    private fun file(
        id: String,
        size: Long = 3,
        modified: Long = 0,
        video: Boolean = false,
    ) = DuplicateScanFile("content://test/$id", id, size, video, null, modified)

    @Test
    fun sha256UsesActualFullContentAndCountsOnlyRealReads() = runBlocking {
        var bytes = 0L
        val input = ByteArrayInputStream("abc".toByteArray())
        val digest = duplicateContentDigest(input, 3) { bytes += it }
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", digest)
        assertEquals(3L, bytes)
        assertEquals(-1, input.read())
    }

    @Test
    fun emptyContentIsValidatedAndUnknownSizeIsNotTrusted() = runBlocking {
        assertEquals(
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
            duplicateContentDigest(ByteArrayInputStream(byteArrayOf()), 0),
        )
        assertNull(duplicateContentDigest(ByteArrayInputStream(byteArrayOf(1)), 0))
        assertNull(duplicateContentDigest(ByteArrayInputStream(byteArrayOf()), -1))
    }

    @Test
    fun truncationAndTrailingByteNeverBecomeADuplicateDigest() = runBlocking {
        var shortBytes = 0
        var trailingBytes = 0
        assertNull(duplicateContentDigest("ab".byteInputStream(), 3) { shortBytes += it })
        assertNull(duplicateContentDigest("abcd".byteInputStream(), 3) { trailingBytes += it })
        assertEquals(2, shortBytes)
        assertEquals(4, trailingBytes)
    }

    @Test
    fun zeroLengthBulkReadDoesNotSpinOrTreatTheFileAsTruncated() = runBlocking {
        val input = object : ByteArrayInputStream("abc".toByteArray()) {
            override fun read(buffer: ByteArray, offset: Int, length: Int): Int = 0
        }
        assertEquals(
            duplicateContentDigest("abc".byteInputStream(), 3),
            duplicateContentDigest(input, 3),
        )
    }

    @Test
    fun digestPropagatesIoSecurityAndProviderErrorsToRevalidationCaller() = runBlocking {
        for (failure in listOf(IOException("read failed"), SecurityException("revoked"), IllegalStateException("provider"))) {
            val input = object : InputStream() {
                override fun read(): Int = throw failure
                override fun read(buffer: ByteArray, offset: Int, length: Int): Int = throw failure
            }
            try {
                duplicateContentDigest(input, 3)
                fail("The read failure must propagate")
            } catch (actual: Exception) {
                assertTrue(actual === failure)
            }
        }
    }

    @Test
    fun sameSizeNonmatchAndLengthFailuresAreNotGrouped() = runBlocking {
        val files = listOf("keeper", "copy", "different", "short", "long").map {
            file(it, modified = if (it == "keeper") 1 else 2)
        }
        val payloads = mapOf("keeper" to "abc", "copy" to "abc", "different" to "xyz", "short" to "ab", "long" to "abcd")
        val result = compareExactDuplicateFiles(files, { payloads.getValue(it.name).byteInputStream() })
        val group = result.groups.single()
        assertEquals(listOf("keeper", "copy"), group.files.map { it.name })
        assertEquals(DuplicateMatchKind.EXACT, group.kind)
        assertNull(group.visualDistance)
        assertEquals(5, result.filesDiscovered)
        assertEquals(3, result.filesCompared)
        assertEquals(2, result.unreadableFiles)
        assertEquals(15L, result.bytesRead)
        assertTrue(result.partialAccess)
        assertTrue(result.warnings.any { "length changed or truncated" in it })
    }

    @Test
    fun uniqueSizesAreNotOpenedAndUnknownSizesExposeSkippedCoverage() = runBlocking {
        val files = listOf(file("a", 1), file("b", 2), file("unknown", -1))
        val result = compareExactDuplicateFiles(files, { error("Only same-size candidates may be opened") })
        assertTrue(result.groups.isEmpty())
        assertEquals(3, result.filesDiscovered)
        assertEquals(0, result.filesCompared)
        assertEquals(0L, result.bytesRead)
        assertEquals(0, result.unreadableFiles)
        assertTrue(result.partialAccess)
        assertTrue(result.warnings.any { "invalid size" in it })
        assertTrue(result.warnings.any { "2 unique-size" in it })
    }

    @Test
    fun moreThan256FilesAndMoreThan12GroupsAreNotCapped() = runBlocking {
        val files = (0 until 300).map { index -> file("file-$index", modified = index.toLong()) }
        val result = compareExactDuplicateFiles(files, { item ->
            val pair = item.name.removePrefix("file-").toInt() / 2
            ByteArrayInputStream(byteArrayOf(pair.toByte(), (pair ushr 8).toByte(), 42))
        })
        assertEquals(300, result.filesDiscovered)
        assertEquals(300, result.filesCompared)
        assertEquals(150, result.groups.size)
        assertEquals(300, result.groups.sumOf { it.files.size })
        assertEquals(150, result.groups.map { it.id }.toSet().size)
        assertEquals(900L, result.bytesRead)
        assertFalse(result.partialAccess)
        assertTrue(result.groups.all { it.files.size == 2 })
    }

    /** Virtual large stream: never allocates content-sized arrays. */
    private class RepeatedStream(length: Long) : InputStream() {
        private var remaining = length
        var maxRequested = 0
        var closed = false
        override fun read(): Int {
            if (remaining == 0L) return -1
            remaining--
            return 7
        }
        override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
            maxRequested = maxOf(maxRequested, length)
            if (remaining == 0L) return -1
            val count = minOf(remaining, length.toLong()).toInt()
            Arrays.fill(buffer, offset, offset + count, 7.toByte())
            remaining -= count
            return count
        }
        override fun close() { closed = true }
    }

    @Test
    fun filesOver32MiBAndScanOver128MiBStreamWithoutHugeAllocations() = runBlocking {
        val size = 65L * 1024 * 1024
        val streams = mutableListOf<RepeatedStream>()
        val result = compareExactDuplicateFiles(listOf(file("large-a", size), file("large-b", size)), {
            RepeatedStream(size).also(streams::add)
        })
        assertEquals(1, result.groups.size)
        assertEquals(2, result.filesCompared)
        assertEquals(130L * 1024 * 1024, result.bytesRead)
        assertTrue(streams.all { it.maxRequested <= 64 * 1024 && it.closed })
        assertFalse(result.partialAccess)
    }

    @Test
    fun keeperOrderAndGroupIdsAreDeterministicAndUrisAreUnique() = runBlocking {
        val files = listOf(file("z", modified = 2), file("b", modified = 1), file("a", modified = 1))
        val first = compareExactDuplicateFiles(files + files.first(), { "abc".byteInputStream() })
        val shuffled = compareExactDuplicateFiles(files.reversed(), { "abc".byteInputStream() })
        assertEquals(first.groups, shuffled.groups)
        assertEquals(listOf("a", "b", "z"), first.groups.single().files.map { it.name })
        assertEquals(3, first.filesDiscovered)
        assertEquals(3, first.filesCompared)
        val differentMembership = compareExactDuplicateFiles(files.take(2), { "abc".byteInputStream() })
        assertNotEquals(first.groups.single().id, differentMembership.groups.single().id)
    }

    @Test
    fun errorsAreClassifiedSeparatelyAndAllRemainingFilesStillScan() = runBlocking {
        var closed = false
        val files = listOf("good", "copy", "io", "security", "provider", "null").map(::file)
        val result = compareExactDuplicateFiles(files, { item ->
            when (item.name) {
                "io" -> object : InputStream() {
                    override fun read(): Int = throw IOException("gone")
                    override fun close() { closed = true }
                }
                "security" -> throw SecurityException("revoked")
                "provider" -> throw IllegalStateException("provider failed")
                "null" -> null
                else -> "abc".byteInputStream()
            }
        })
        assertEquals(1, result.groups.size)
        assertEquals(2, result.filesCompared)
        assertEquals(4, result.unreadableFiles)
        assertEquals(6L, result.bytesRead)
        assertTrue(result.partialAccess)
        assertTrue(closed)
        assertTrue(result.warnings.any { "IO failure: 2" in it })
        assertTrue(result.warnings.any { "permission denied" in it })
        assertTrue(result.warnings.any { "provider failure" in it })
    }

    @Test
    fun allUnreadableIsIncompleteNotAClaimOfNoDuplicates() = runBlocking {
        val result = compareExactDuplicateFiles(listOf(file("a"), file("b")), { throw IOException("unavailable") })
        assertTrue(result.groups.isEmpty())
        assertEquals(0, result.filesCompared)
        assertEquals(2, result.unreadableFiles)
        assertTrue(result.partialAccess)
        assertTrue(result.warnings.isNotEmpty())
    }

    @Test
    fun closeFailureDoesNotPublishTheFilesDigest() = runBlocking {
        val result = compareExactDuplicateFiles(listOf(file("a"), file("b")), {
            object : ByteArrayInputStream("abc".toByteArray()) {
                override fun close(): Unit = throw IOException("close failed")
            }
        })
        assertTrue(result.groups.isEmpty())
        assertEquals(0, result.filesCompared)
        assertEquals(2, result.unreadableFiles)
        assertEquals(6L, result.bytesRead)
    }

    @Test
    fun progressUsesRealCandidateTotalsAndBytesAndIsThrottled() = runBlocking {
        val progress = mutableListOf<DuplicateScanProgress>()
        val files = (0 until 300).map { file("$it") }
        val result = compareExactDuplicateFiles(files, { "abc".byteInputStream() }, progress::add)
        assertEquals("hash_exact", progress.first().phase)
        assertEquals(300, progress.first().totalFiles)
        assertEquals(0L, progress.first().bytesRead)
        assertEquals("complete", progress.last().phase)
        assertEquals(300, progress.last().completedFiles)
        assertEquals(300, progress.last().totalFiles)
        assertEquals(result.bytesRead, progress.last().bytesRead)
        assertTrue(progress.zipWithNext().all { (a, b) -> a.bytesRead <= b.bytesRead })
        assertTrue("One callback per read/file would not be throttled", progress.size < 300)
    }

    @Test
    fun cancellationAfterChunkPropagatesAndClosesStream() = runBlocking {
        val stream = RepeatedStream(10L * 1024 * 1024)
        var cancelled = false
        val child = launch(start = CoroutineStart.UNDISPATCHED) {
            val job = currentCoroutineContext()[Job]!!
            try {
                stream.use { duplicateContentDigest(it, 10L * 1024 * 1024) { job.cancel() } }
                fail("Cancellation must not return a digest")
            } catch (expected: CancellationException) {
                cancelled = true
                throw expected
            }
        }
        child.join()
        assertTrue(cancelled)
        assertTrue(stream.closed)
        assertTrue(stream.maxRequested <= 64 * 1024)
    }

    @Test
    fun cancelledReadIoFailureIsStillCancellation() = runBlocking {
        var cancelled = false
        val child = launch(start = CoroutineStart.UNDISPATCHED) {
            val job = currentCoroutineContext()[Job]!!
            val input = object : InputStream() {
                override fun read(): Int {
                    job.cancel()
                    throw IOException("interrupted read")
                }
            }
            try {
                duplicateContentDigest(input, 3)
                fail("Cancellation must propagate")
            } catch (expected: CancellationException) {
                cancelled = true
                throw expected
            }
        }
        child.join()
        assertTrue(cancelled)
    }

    @Test
    fun providerCancellationIsNotRecordedAsUnreadableOrCompleted() = runBlocking {
        try {
            compareExactDuplicateFiles(listOf(file("a"), file("b")), { throw CancellationException("stop") })
            fail("Cancelled scan must not produce a result")
        } catch (expected: CancellationException) {
            assertEquals("stop", expected.message)
        }
    }

    @Test
    fun preCancelledDigestDoesNotReadAnything() = runBlocking {
        val job = Job().apply { cancel() }
        try {
            withContext(job) {
                duplicateContentDigest(object : InputStream() {
                    override fun read(): Int = error("No read after cancellation")
                }, 3)
            }
            fail("Cancellation must propagate")
        } catch (_: CancellationException) {
            // Expected; the enclosing test job remains active.
        }
    }

    @Test
    fun hammingAndLuminanceHashArePureAndUseAll64Bits() {
        assertEquals(64, duplicateHammingDistance(0, -1))
        assertEquals(1, duplicateHammingDistance(0, Long.MIN_VALUE))
        assertEquals(0L, duplicateLuminanceDHash(IntArray(72) { it % 9 }))
        assertEquals(-1L, duplicateLuminanceDHash(IntArray(72) { 8 - it % 9 }))
        val oneBit = IntArray(72)
        oneBit[0] = 255
        assertEquals(1L, duplicateLuminanceDHash(oneBit))
    }

    @Test
    fun allEightExifOrientationsIncludeMirroringAndSwappedDimensions() {
        val pixels = intArrayOf(1, 2, 3, 4, 5, 6) // 2x3
        val expected = listOf(
            intArrayOf(1, 2, 3, 4, 5, 6),
            intArrayOf(2, 1, 4, 3, 6, 5),
            intArrayOf(6, 5, 4, 3, 2, 1),
            intArrayOf(5, 6, 3, 4, 1, 2),
            intArrayOf(1, 3, 5, 2, 4, 6),
            intArrayOf(5, 3, 1, 6, 4, 2),
            intArrayOf(6, 4, 2, 5, 3, 1),
            intArrayOf(2, 4, 6, 1, 3, 5),
        )
        for (orientation in 1..8) {
            assertArrayEquals(expected[orientation - 1], duplicateOrientPixels(pixels, 2, 3, orientation))
            assertEquals(if (orientation >= 5) 3 to 2 else 2 to 3, duplicateOrientedDimensions(2, 3, orientation))
        }
        assertArrayEquals(intArrayOf(1, 2, 3, 4, 5, 6), pixels)
    }

    private fun visual(id: String, hash: Long, modified: Long, width: Int = 1000, height: Int = 1000) =
        DuplicateVisualFingerprint(file(id, modified = modified), hash, width, height)

    @Test
    fun similarityIsAnchoredSoABAndBCNeverDriftIntoAC() = runBlocking {
        val base = 0x3333333333333333L
        val a = visual("a", base, 1)
        val b = visual("b", base xor 0b111L, 2)
        val c = visual("c", base xor 0b111111L, 3)
        assertEquals(3, duplicateHammingDistance(a.hash, b.hash))
        assertEquals(3, duplicateHammingDistance(b.hash, c.hash))
        assertEquals(6, duplicateHammingDistance(a.hash, c.hash))
        val group = groupVisualDuplicateFingerprints(listOf(c, b, a)).single()
        assertEquals(listOf("a", "b"), group.files.map { it.name })
        assertEquals(DuplicateMatchKind.VISUAL, group.kind)
        assertEquals(3, group.visualDistance)
        assertEquals(listOf(group), groupVisualDuplicateFingerprints(listOf(a, b, c)))
    }

    @Test
    fun strictAspectGuardRejectsCropsAndInvertedAspectEvenWithEqualHash() = runBlocking {
        val hash = 0x3333333333333333L
        val square = visual("square", hash, 1)
        val near = visual("near", hash, 2, width = 1010)
        val crop = visual("crop", hash, 3, width = 1100)
        val wide = visual("wide", hash, 4, width = 2000)
        val tall = visual("tall", hash, 5, height = 2000)
        assertTrue(duplicateAspectCompatible(square, near))
        assertFalse(duplicateAspectCompatible(square, crop))
        assertFalse(duplicateAspectCompatible(wide, tall))
        val group = groupVisualDuplicateFingerprints(listOf(square, near, crop, wide, tall)).single()
        assertEquals(listOf("square", "near"), group.files.map { it.name })
        assertEquals(0, group.visualDistance)
    }

    @Test
    fun aspectBucketBoundaryDoesNotLoseAnOtherwiseValidMatch() = runBlocking {
        val hash = 0x3333333333333333L
        val first = visual("a", hash, 1, width = 1019)
        val second = visual("b", hash, 2, width = 1021)
        assertTrue(duplicateAspectCompatible(first, second))
        assertEquals(1, groupVisualDuplicateFingerprints(listOf(first, second)).size)
    }

    @Test
    fun visualGroupsHaveNoFileOrGroupCapAndIdsStayUnique() = runBlocking {
        // Aspect-separated pairs intentionally share a hash: exercise partitioned lookup.
        val hash = 0x3333333333333333L
        val fingerprints = (0 until 150).flatMap { index ->
            val width = (1000 * Math.pow(1.05, index.toDouble())).toInt()
            listOf(visual("a-$index", hash, index * 2L, width), visual("b-$index", hash, index * 2L + 1, width))
        }
        val groups = groupVisualDuplicateFingerprints(fingerprints)
        assertEquals(150, groups.size)
        assertEquals(300, groups.sumOf { it.files.size })
        assertEquals(150, groups.map { it.id }.toSet().size)
        assertTrue(groups.all { it.kind == DuplicateMatchKind.VISUAL && it.visualDistance == 0 })
    }

    @Test
    fun groupingChecksCancellationPerFileRatherThanReturningPartialGroups() = runBlocking {
        var cancelled = false
        val child = launch(start = CoroutineStart.UNDISPATCHED) {
            val job = currentCoroutineContext()[Job]!!
            try {
                groupVisualDuplicateFingerprints((0 until 300).map { visual("$it", 0x3333333333333333L, it.toLong()) }) {
                    job.cancel()
                }
                fail("Cancellation must not return partial groups")
            } catch (expected: CancellationException) {
                cancelled = true
                throw expected
            }
        }
        child.join()
        assertTrue(cancelled)
    }
}
