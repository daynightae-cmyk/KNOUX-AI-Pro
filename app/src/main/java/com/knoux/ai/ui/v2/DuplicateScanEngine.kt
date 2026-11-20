package com.knoux.ai.ui.v2

import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.CancellationSignal
import android.provider.MediaStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FilterInputStream
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest
import java.util.TreeMap
import kotlin.math.floor
import kotlin.math.ln

internal enum class DuplicateScanMode { EXACT, VISUAL, NAME }
internal enum class DuplicateMatchKind { EXACT, VISUAL, NAME }

internal data class DuplicateScanFile(
    val uriString: String,
    val name: String,
    val sizeBytes: Long,
    val isVideo: Boolean,
    val bucket: String?,
    val modifiedAt: Long = 0,
    val width: Int = 0,
    val height: Int = 0,
    val mimeType: String? = null,
)

/** First file is the deterministic reference (oldest modifiedAt, then URI).
 * VISUAL is a manual-review hint, never proof of equality or an auto-selection.
 */
internal data class DuplicateScanGroup(
    val id: String,
    val files: List<DuplicateScanFile>,
    val kind: DuplicateMatchKind,
    val visualDistance: Int? = null,
)

/** During discovery totalFiles is 0 (unknown), not a fabricated estimate.
 * Later totals are the actual number of candidates in the named stage.
 * bytesRead counts real stream reads, including failed reads and thumbnail/EXIF passes.
 */
internal data class DuplicateScanProgress(
    val phase: String,
    val completedFiles: Int,
    val totalFiles: Int,
    val bytesRead: Long,
)

/** filesCompared counts successfully hashed/decoded files, not attempted reads.
 * partialAccess also covers incomplete comparison/query coverage; an empty groups
 * list with partialAccess/warnings must NOT be presented as "no duplicates".
 */
internal data class DuplicateScanResult(
    val groups: List<DuplicateScanGroup>,
    val filesDiscovered: Int,
    val filesCompared: Int,
    val unreadableFiles: Int,
    val bytesRead: Long,
    val partialAccess: Boolean,
    val completedAt: Long,
    val warnings: List<String>,
)

private const val DUPLICATE_BUFFER_BYTES = 64 * 1024
private const val DUPLICATE_THUMBNAIL_SIDE = 256
private const val DUPLICATE_VISUAL_DISTANCE = 3
private const val DUPLICATE_ASPECT_RATIO = 1.02
private val duplicateKeeperOrder = compareBy<DuplicateScanFile> { it.modifiedAt }
    .thenBy { it.uriString }

/** No size/file/group cap. Caller owns and closes input. IO/security/provider
 * exceptions propagate so deletion revalidation can distinguish failure from
 * equality. Null means an unknown length, truncation, or trailing content.
 */
internal suspend fun duplicateContentDigest(
    input: InputStream,
    expectedBytes: Long,
    onBytes: (Int) -> Unit = {},
): String? {
    val active = currentCoroutineContext()
    active.ensureActive()
    if (expectedBytes < 0) return null
    val digest = MessageDigest.getInstance("SHA-256")
    val buffer = ByteArray(DUPLICATE_BUFFER_BYTES)
    var remaining = expectedBytes
    while (remaining > 0) {
        active.ensureActive()
        val read = try {
            input.read(buffer, 0, minOf(remaining, buffer.size.toLong()).toInt())
        } finally {
            active.ensureActive()
        }
        if (read < 0) return null
        // Some provider streams return zero; consume one byte rather than spin.
        if (read == 0) {
            val byte = try { input.read() } finally { active.ensureActive() }
            if (byte < 0) return null
            onBytes(1)
            active.ensureActive()
            digest.update(byte.toByte())
            remaining--
        } else {
            onBytes(read)
            active.ensureActive()
            digest.update(buffer, 0, read)
            remaining -= read
        }
    }
    active.ensureActive()
    val trailing = try { input.read() } finally { active.ensureActive() }
    if (trailing != -1) {
        onBytes(1)
        active.ensureActive()
        return null
    }
    return duplicateHex(digest.digest())
}

private fun duplicateHex(bytes: ByteArray): String {
    val alphabet = "0123456789abcdef"
    return buildString(bytes.size * 2) {
        for (byte in bytes) {
            val value = byte.toInt() and 255
            append(alphabet[value ushr 4])
            append(alphabet[value and 15])
        }
    }
}

private fun duplicateGroupId(kind: DuplicateMatchKind, files: List<DuplicateScanFile>): String {
    val digest = MessageDigest.getInstance("SHA-256")
    digest.update(kind.name.toByteArray(Charsets.UTF_8))
    for (file in files) {
        val uri = file.uriString.toByteArray(Charsets.UTF_8)
        // Length framing prevents ambiguous URI concatenations.
        digest.update(byteArrayOf(
            (uri.size ushr 24).toByte(), (uri.size ushr 16).toByte(),
            (uri.size ushr 8).toByte(), uri.size.toByte(),
        ))
        digest.update(uri)
    }
    return "${kind.name.lowercase()}:${duplicateHex(digest.digest())}"
}

private class DuplicateProgressCallbackFailure(cause: RuntimeException) : RuntimeException(cause)

private class DuplicateProgressReporter(
    private val callback: (DuplicateScanProgress) -> Unit,
) {
    private var lastPhase = ""
    private var lastNanos = 0L

    fun emit(phase: String, completed: Int, total: Int, bytes: Long, force: Boolean = false) {
        val now = System.nanoTime()
        if (force || phase != lastPhase || now - lastNanos >= 150_000_000L) {
            lastPhase = phase
            lastNanos = now
            try {
                callback(DuplicateScanProgress(phase, completed, total, bytes))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (failure: RuntimeException) {
                // UI/caller failure is not an unreadable provider file.
                throw DuplicateProgressCallbackFailure(failure)
            }
        }
    }
}

/** Bounded warning cardinality, not bounded scanning: counts every skipped file. */
private class DuplicateScanIssues {
    var partial = false
    var unreadable = 0
    private val notes = linkedSetOf<String>()
    private val counts = linkedMapOf<String, Int>()

    fun note(message: String, incomplete: Boolean = false) {
        notes.add(message)
        partial = partial || incomplete
    }

    fun skip(category: String, unreadableFile: Boolean = true) {
        partial = true
        if (unreadableFile) unreadable++
        counts[category] = (counts[category] ?: 0) + 1
    }

    fun warnings(): List<String> = notes.toList() + counts.map { (label, count) ->
        "$label: $count file(s) skipped."
    }
}

internal data class DuplicateContentFingerprint(val file: DuplicateScanFile, val digest: String)

/** Pure grouping is separated from Android/provider IO for JVM tests. */
internal suspend fun groupExactDuplicateDigests(
    fingerprints: List<DuplicateContentFingerprint>,
    onFileGrouped: (Int) -> Unit = {},
): List<DuplicateScanGroup> {
    val active = currentCoroutineContext()
    active.ensureActive()
    val matches = linkedMapOf<Pair<Long, String>, MutableList<DuplicateScanFile>>()
    val seen = hashSetOf<String>()
    for ((index, fingerprint) in fingerprints.withIndex()) {
        active.ensureActive()
        if (seen.add(fingerprint.file.uriString)) {
            matches.getOrPut(fingerprint.file.sizeBytes to fingerprint.digest) { mutableListOf() }
                .add(fingerprint.file)
        }
        onFileGrouped(index + 1)
    }
    val groups = mutableListOf<DuplicateScanGroup>()
    for (files in matches.values) {
        active.ensureActive()
        if (files.size < 2) continue
        val ordered = files.sortedWith(duplicateKeeperOrder)
        active.ensureActive()
        groups.add(DuplicateScanGroup(
            duplicateGroupId(DuplicateMatchKind.EXACT, ordered), ordered, DuplicateMatchKind.EXACT,
        ))
    }
    active.ensureActive()
    return groups.sortedBy { it.id }.also { active.ensureActive() }
}

/** Platform-free full exact comparison. Only repeated-size candidates are opened.
 * Empty files are valid; unknown sizes (-1) are explicitly skipped, not hashed.
 */
internal suspend fun compareExactDuplicateFiles(
    files: List<DuplicateScanFile>,
    open: (DuplicateScanFile) -> InputStream?,
    onProgress: (DuplicateScanProgress) -> Unit = {},
): DuplicateScanResult {
    val active = currentCoroutineContext()
    active.ensureActive()
    val issues = DuplicateScanIssues()
    val progress = DuplicateProgressReporter(onProgress)
    val unique = linkedMapOf<String, DuplicateScanFile>()
    val bySize = linkedMapOf<Long, MutableList<DuplicateScanFile>>()
    for (file in files) {
        active.ensureActive()
        if (unique.putIfAbsent(file.uriString, file) != null) continue
        if (file.sizeBytes < 0) {
            issues.skip("Missing or invalid size metadata", unreadableFile = false)
        } else {
            bySize.getOrPut(file.sizeBytes) { mutableListOf() }.add(file)
        }
    }
    val candidates = mutableListOf<DuplicateScanFile>()
    var singletons = 0
    for (bucket in bySize.values) {
        active.ensureActive()
        if (bucket.size > 1) candidates.addAll(bucket) else singletons++
    }
    candidates.sortWith(compareBy<DuplicateScanFile> { it.sizeBytes }.then(duplicateKeeperOrder))
    active.ensureActive()
    if (singletons > 0) issues.note("$singletons unique-size file(s) excluded without reading content.")
    val fingerprints = mutableListOf<DuplicateContentFingerprint>()
    var bytes = 0L
    var attempted = 0
    progress.emit("hash_exact", 0, candidates.size, bytes)
    for (file in candidates) {
        active.ensureActive()
        val digest = try {
            val input = open(file) ?: throw IOException("Provider returned no stream")
            input.use {
                duplicateContentDigest(it, file.sizeBytes) { read ->
                    bytes += read
                    progress.emit("hash_exact", attempted, candidates.size, bytes)
                }
            }.also { active.ensureActive() }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: IOException) {
            active.ensureActive()
            issues.skip("Content IO failure")
            attempted++
            progress.emit("hash_exact", attempted, candidates.size, bytes)
            continue
        } catch (_: SecurityException) {
            active.ensureActive()
            issues.skip("Content permission denied or revoked")
            attempted++
            progress.emit("hash_exact", attempted, candidates.size, bytes)
            continue
        } catch (failure: RuntimeException) {
            if (failure is DuplicateProgressCallbackFailure) throw failure
            active.ensureActive()
            issues.skip("Content provider failure")
            attempted++
            progress.emit("hash_exact", attempted, candidates.size, bytes)
            continue
        }
        if (digest == null) issues.skip("Content length changed or truncated")
        else fingerprints.add(DuplicateContentFingerprint(file, digest))
        attempted++
        progress.emit("hash_exact", attempted, candidates.size, bytes)
    }
    progress.emit("hash_exact", attempted, candidates.size, bytes, force = true)
    progress.emit("group_exact", 0, fingerprints.size, bytes)
    val groups = groupExactDuplicateDigests(fingerprints) { completed ->
        progress.emit("group_exact", completed, fingerprints.size, bytes)
    }
    active.ensureActive()
    progress.emit("complete", attempted, candidates.size, bytes, force = true)
    active.ensureActive()
    return DuplicateScanResult(
        groups, unique.size, fingerprints.size, issues.unreadable, bytes,
        issues.partial, System.currentTimeMillis(), issues.warnings(),
    )
}

internal fun duplicateHammingDistance(first: Long, second: Long): Int =
    java.lang.Long.bitCount(first xor second)

/** 9x8 row-major luminance, 64 horizontal comparisons; no AI/cloud score. */
internal fun duplicateLuminanceDHash(luminance: IntArray): Long {
    require(luminance.size == 9 * 8)
    var hash = 0L
    for (y in 0 until 8) for (x in 0 until 8) {
        if (luminance[y * 9 + x] > luminance[y * 9 + x + 1]) {
            hash = hash or (1L shl (y * 8 + x))
        }
    }
    return hash
}

/** Handles all EXIF rotations AND mirror orientations on a tiny raster. */
internal fun duplicateOrientedDimensions(width: Int, height: Int, orientation: Int): Pair<Int, Int> =
    if (orientation in 5..8) height to width else width to height

internal fun duplicateOrientPixels(
    pixels: IntArray,
    width: Int,
    height: Int,
    orientation: Int,
): IntArray {
    require(width > 0 && height > 0 && width.toLong() * height == pixels.size.toLong())
    val (targetWidth, _) = duplicateOrientedDimensions(width, height, orientation)
    val result = IntArray(pixels.size)
    for (y in 0 until height) for (x in 0 until width) {
        val (dx, dy) = when (orientation) {
            2 -> width - 1 - x to y
            3 -> width - 1 - x to height - 1 - y
            4 -> x to height - 1 - y
            5 -> y to x
            6 -> height - 1 - y to x
            7 -> height - 1 - y to width - 1 - x
            8 -> y to width - 1 - x
            else -> x to y
        }
        result[dy * targetWidth + dx] = pixels[y * width + x]
    }
    return result
}

internal data class DuplicateVisualFingerprint(
    val file: DuplicateScanFile,
    val hash: Long,
    val width: Int,
    val height: Int,
)

internal fun duplicateAspectCompatible(first: DuplicateVisualFingerprint, second: DuplicateVisualFingerprint): Boolean {
    if (first.width <= 0 || first.height <= 0 || second.width <= 0 || second.height <= 0) return false
    val ratio = (first.width.toDouble() * second.height) / (second.width.toDouble() * first.height)
    return ratio in (1.0 / DUPLICATE_ASPECT_RATIO)..DUPLICATE_ASPECT_RATIO
}

private class DuplicateVisualReference(val fingerprint: DuplicateVisualFingerprint, val order: Int) {
    val files = mutableListOf(fingerprint.file)
    var distance = 0
}

/** BK-tree over references, partitioned by log-aspect buckets. Identical hashes
 * at different aspect ratios do not devolve into one unbounded collision list.
 * Members never enter the index: every match must remain near its one anchor.
 */
private class DuplicateVisualTree {
    private class Node(val reference: DuplicateVisualReference) {
        val children = TreeMap<Int, Node>()
    }
    private var root: Node? = null

    fun add(reference: DuplicateVisualReference, checkActive: () -> Unit) {
        val first = root
        if (first == null) {
            root = Node(reference)
            return
        }
        var node: Node = first
        while (true) {
            checkActive()
            val distance = duplicateHammingDistance(reference.fingerprint.hash, node.reference.fingerprint.hash)
            val child = node.children[distance]
            if (child == null) {
                node.children[distance] = Node(reference)
                return
            }
            node = child
        }
    }

    fun find(
        fingerprint: DuplicateVisualFingerprint,
        radius: Int,
        checkActive: () -> Unit,
        visit: (DuplicateVisualReference, Int) -> Unit,
    ) {
        val stack = ArrayDeque<Node>()
        root?.let(stack::addLast)
        while (stack.isNotEmpty()) {
            checkActive()
            val node = stack.removeLast()
            val distance = duplicateHammingDistance(fingerprint.hash, node.reference.fingerprint.hash)
            if (distance <= radius && duplicateAspectCompatible(fingerprint, node.reference.fingerprint)) {
                visit(node.reference, distance)
            }
            for (child in node.children.subMap(maxOf(0, distance - radius), true, minOf(64, distance + radius), true).values) {
                checkActive()
                stack.addLast(child)
            }
        }
    }
}

internal suspend fun groupVisualDuplicateFingerprints(
    fingerprints: List<DuplicateVisualFingerprint>,
    maxDistance: Int = DUPLICATE_VISUAL_DISTANCE,
    onFileGrouped: (Int) -> Unit = {},
): List<DuplicateScanGroup> {
    // Do not allow consumers to turn conservative review into broad clustering.
    require(maxDistance in 0..DUPLICATE_VISUAL_DISTANCE)
    val active = currentCoroutineContext()
    active.ensureActive()
    val ordered = fingerprints.sortedWith(compareBy<DuplicateVisualFingerprint> { it.file.modifiedAt }
        .thenBy { it.file.uriString })
    active.ensureActive()
    val seen = hashSetOf<String>()
    val trees = hashMapOf<Int, DuplicateVisualTree>()
    val references = mutableListOf<DuplicateVisualReference>()
    val bucketWidth = ln(DUPLICATE_ASPECT_RATIO)
    for ((index, fingerprint) in ordered.withIndex()) {
        active.ensureActive()
        if (!seen.add(fingerprint.file.uriString) || fingerprint.file.isVideo ||
            fingerprint.width <= 0 || fingerprint.height <= 0) {
            onFileGrouped(index + 1)
            continue
        }
        val bucket = floor(ln(fingerprint.width.toDouble() / fingerprint.height) / bucketWidth).toInt()
        var best: DuplicateVisualReference? = null
        var bestDistance = Int.MAX_VALUE
        for (key in bucket - 1..bucket + 1) {
            trees[key]?.find(fingerprint, maxDistance, { active.ensureActive() }) { reference, distance ->
                if (distance < bestDistance || (distance == bestDistance && reference.order < (best?.order ?: Int.MAX_VALUE))) {
                    best = reference
                    bestDistance = distance
                }
            }
        }
        val anchor = best
        if (anchor != null) {
            anchor.files.add(fingerprint.file)
            anchor.distance = maxOf(anchor.distance, bestDistance)
        } else {
            val reference = DuplicateVisualReference(fingerprint, index)
            references.add(reference)
            trees.getOrPut(bucket) { DuplicateVisualTree() }.add(reference) { active.ensureActive() }
        }
        onFileGrouped(index + 1)
    }
    val groups = mutableListOf<DuplicateScanGroup>()
    for (reference in references) {
        active.ensureActive()
        if (reference.files.size < 2) continue
        val files = reference.files.toList()
        groups.add(DuplicateScanGroup(
            duplicateGroupId(DuplicateMatchKind.VISUAL, files), files,
            DuplicateMatchKind.VISUAL, reference.distance,
        ))
    }
    active.ensureActive()
    return groups.sortedBy { it.id }.also { active.ensureActive() }
}

/** Counting/cancellation wraps every decoder/EXIF read, not invented source sizes. */
private class DuplicateCountingStream(
    input: InputStream,
    private val checkActive: () -> Unit,
    private val onBytes: (Int) -> Unit,
) : FilterInputStream(input) {
    override fun read(): Int {
        checkActive()
        val value = try { `in`.read() } finally { checkActive() }
        if (value >= 0) onBytes(1)
        return value
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        checkActive()
        val count = try { `in`.read(buffer, offset, length) } finally { checkActive() }
        if (count > 0) onBytes(count)
        return count
    }

    override fun read(buffer: ByteArray): Int = read(buffer, 0, buffer.size)

    override fun skip(count: Long): Long {
        val scratch = ByteArray(4096)
        var remaining = count.coerceAtLeast(0)
        while (remaining > 0) {
            checkActive()
            val read = read(scratch, 0, minOf(scratch.size.toLong(), remaining).toInt())
            if (read < 0) break
            if (read == 0) {
                if (read() < 0) break
                remaining--
            } else remaining -= read
        }
        return count.coerceAtLeast(0) - remaining
    }
}

private suspend fun readDuplicateVisualFingerprint(
    context: Context,
    file: DuplicateScanFile,
    onBytes: (Int) -> Unit,
): DuplicateVisualFingerprint? {
    val active = currentCoroutineContext()
    val uri = Uri.parse(file.uriString)
    fun open(): InputStream {
        active.ensureActive()
        val input = context.contentResolver.openInputStream(uri) ?: throw IOException("No image stream")
        return DuplicateCountingStream(input, { active.ensureActive() }, onBytes)
    }
    val orientation = open().use { input ->
        ExifInterface(input).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    }
    active.ensureActive()
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    open().use { BitmapFactory.decodeStream(it, null, bounds) }
    active.ensureActive()
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) throw IOException("Unsupported image bounds")
    var sample = 1
    while ((bounds.outWidth.toLong() + sample - 1) / sample > DUPLICATE_THUMBNAIL_SIDE ||
        (bounds.outHeight.toLong() + sample - 1) / sample > DUPLICATE_THUMBNAIL_SIDE) {
        active.ensureActive()
        sample *= 2
    }
    val options = BitmapFactory.Options().apply {
        inSampleSize = sample
        inPreferredConfig = Bitmap.Config.ARGB_8888
        inScaled = false
    }
    var decoded: Bitmap? = null
    var thumbnail: Bitmap? = null
    try {
        // Assign before use closes: a close failure must still recycle the bitmap.
        open().use { decoded = BitmapFactory.decodeStream(it, null, options) }
        active.ensureActive()
        val bitmap = decoded ?: throw IOException("Image decode failed")
        if (bitmap.width > DUPLICATE_THUMBNAIL_SIDE || bitmap.height > DUPLICATE_THUMBNAIL_SIDE) {
            throw IOException("Decoder ignored bounded sampling")
        }
        val swapped = orientation in 5..8
        val gridWidth = if (swapped) 8 else 9
        val gridHeight = if (swapped) 9 else 8
        thumbnail = Bitmap.createScaledBitmap(bitmap, gridWidth, gridHeight, true)
        active.ensureActive()
        val pixels = IntArray(72)
        thumbnail.getPixels(pixels, 0, gridWidth, 0, 0, gridWidth, gridHeight)
        val oriented = duplicateOrientPixels(pixels, gridWidth, gridHeight, orientation)
        val luminance = IntArray(72) { index ->
            val pixel = oriented[index]
            // Composite transparent pixels over white, consistently.
            val alpha = pixel ushr 24
            val value = (299 * ((pixel ushr 16) and 255) + 587 * ((pixel ushr 8) and 255) +
                114 * (pixel and 255)) / 1000
            (value * alpha + 255 * (255 - alpha)) / 255
        }
        val hash = duplicateLuminanceDHash(luminance)
        active.ensureActive()
        // Flat/near-flat hashes have too little information for conservative review.
        if (luminance.maxOrNull()!! - luminance.minOrNull()!! < 16 ||
            java.lang.Long.bitCount(hash) !in 6..58) return null
        val (width, height) = duplicateOrientedDimensions(bounds.outWidth, bounds.outHeight, orientation)
        return DuplicateVisualFingerprint(file, hash, width, height)
    } finally {
        val small = thumbnail
        if (small != null && small !== decoded) small.recycle()
        decoded?.recycle()
    }
}

private data class DuplicateDiscovery(val files: List<DuplicateScanFile>, val issues: DuplicateScanIssues)

/** Cancels a blocking query as well as checking every cursor row. Unconfined
 * watcher has no IO-dispatcher starvation when the scanning thread is blocked.
 */
private suspend fun <T> duplicateProviderCall(block: (CancellationSignal) -> T): T = coroutineScope {
    val signal = CancellationSignal()
    val watcher = launch(Dispatchers.Unconfined, start = CoroutineStart.UNDISPATCHED) {
        try { awaitCancellation() } finally { signal.cancel() }
    }
    try {
        ensureActive()
        block(signal).also { ensureActive() }
    } finally {
        watcher.cancel()
    }
}

private suspend fun discoverDuplicateMedia(
    context: Context,
    onProgress: (DuplicateScanProgress) -> Unit,
): DuplicateDiscovery {
    val active = currentCoroutineContext()
    val issues = DuplicateScanIssues()
    fun granted(permission: String) = context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    val imagesGranted = granted(if (Build.VERSION.SDK_INT >= 33) "android.permission.READ_MEDIA_IMAGES" else "android.permission.READ_EXTERNAL_STORAGE")
    val videosGranted = granted(if (Build.VERSION.SDK_INT >= 33) "android.permission.READ_MEDIA_VIDEO" else "android.permission.READ_EXTERNAL_STORAGE")
    val selectedGranted = Build.VERSION.SDK_INT >= 34 && granted("android.permission.READ_MEDIA_VISUAL_USER_SELECTED")
    if (!imagesGranted || !videosGranted) {
        issues.note(
            if (selectedGranted) "Selected-media access only for at least one collection; unselected media is outside scan coverage."
            else "Full image/video read grants are missing; only provider-visible/owned media can be scanned.",
            incomplete = true,
        )
    }
    if (Build.VERSION.SDK_INT >= 34) {
        // Android automatically adds the selected-media manifest permission;
        // inspecting requestedPermissions cannot prove the app adopted that flow.
        // Compatibility "false grants" cannot prove full-library visibility, so
        // this read-only engine conservatively reports unverified coverage.
        issues.note(
            "Android selected-photo compatibility grants may expose only a subset, even while a media grant is reported. Full-library coverage is unverified; all currently provider-visible media is scanned.",
            incomplete = true,
        )
    }
    val collections = listOf(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI to false,
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI to true,
        MediaStore.Images.Media.INTERNAL_CONTENT_URI to false,
        MediaStore.Video.Media.INTERNAL_CONTENT_URI to true,
    )
    val projection = arrayOf(
        MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DISPLAY_NAME,
        MediaStore.MediaColumns.SIZE, MediaStore.MediaColumns.DATE_MODIFIED,
        MediaStore.MediaColumns.WIDTH, MediaStore.MediaColumns.HEIGHT,
        MediaStore.MediaColumns.MIME_TYPE, "bucket_display_name",
    )
    val files = linkedMapOf<String, DuplicateScanFile>()
    val progress = DuplicateProgressReporter(onProgress)
    var rows = 0
    for ((collection, isVideo) in collections) {
        active.ensureActive()
        val phase = if (isVideo) "discover_videos" else "discover_images"
        progress.emit(phase, rows, 0, 0)
        try {
            duplicateProviderCall { signal ->
                val cursor = context.contentResolver.query(collection, projection, null, null, null, signal)
                    ?: throw IOException("Provider returned no cursor")
                cursor.use {
                    val idColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                    val nameColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                    val sizeColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
                    val modifiedColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED)
                    val widthColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH)
                    val heightColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT)
                    val mimeColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
                    val bucketColumn = it.getColumnIndexOrThrow("bucket_display_name")
                    while (true) {
                        active.ensureActive()
                        if (!it.moveToNext()) break
                        active.ensureActive()
                        rows++
                        try {
                            if (it.isNull(idColumn) || it.getLong(idColumn) < 0) {
                                issues.skip("Invalid MediaStore ID metadata")
                                continue
                            }
                            val uri = ContentUris.withAppendedId(collection, it.getLong(idColumn)).toString()
                            val seconds = if (it.isNull(modifiedColumn)) 0 else it.getLong(modifiedColumn)
                            files[uri] = DuplicateScanFile(
                                uriString = uri,
                                name = it.getString(nameColumn) ?: uri,
                                sizeBytes = if (it.isNull(sizeColumn)) -1 else it.getLong(sizeColumn),
                                isVideo = isVideo,
                                bucket = it.getString(bucketColumn),
                                modifiedAt = seconds.coerceIn(0, Long.MAX_VALUE / 1000) * 1000,
                                width = if (it.isNull(widthColumn)) 0 else it.getInt(widthColumn).coerceAtLeast(0),
                                height = if (it.isNull(heightColumn)) 0 else it.getInt(heightColumn).coerceAtLeast(0),
                                mimeType = it.getString(mimeColumn),
                            )
                        } catch (cancelled: CancellationException) {
                            throw cancelled
                        } catch (_: SecurityException) {
                            active.ensureActive()
                            issues.skip("Metadata permission denied")
                        } catch (_: RuntimeException) {
                            active.ensureActive()
                            issues.skip("Unreadable provider metadata")
                        }
                        progress.emit(phase, rows, 0, 0)
                    }
                    active.ensureActive()
                }
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: IOException) {
            active.ensureActive()
            issues.note("MediaStore IO/query failure at $collection; that collection was not fully scanned.", incomplete = true)
        } catch (_: SecurityException) {
            active.ensureActive()
            issues.note("MediaStore permission/query denial at $collection; that collection was not fully scanned.", incomplete = true)
        } catch (failure: RuntimeException) {
            if (failure is DuplicateProgressCallbackFailure) throw failure
            active.ensureActive()
            issues.note("MediaStore provider/query failure at $collection; that collection was not fully scanned.", incomplete = true)
        }
        progress.emit(phase, rows, 0, 0, force = true)
    }
    active.ensureActive()
    return DuplicateDiscovery(files.values.toList(), issues)
}

private suspend fun compareDuplicateVisualMedia(
    context: Context,
    files: List<DuplicateScanFile>,
    onProgress: (DuplicateScanProgress) -> Unit,
): DuplicateScanResult {
    val active = currentCoroutineContext()
    val issues = DuplicateScanIssues()
    issues.note("VISUAL groups are on-device perceptual similarities for manual review only; nothing is auto-selected.")
    val images = mutableListOf<DuplicateScanFile>()
    var videos = 0
    for (file in files) {
        active.ensureActive()
        if (file.isVideo) videos++ else images.add(file)
    }
    if (videos > 0) issues.note("VISUAL is images-only; $videos video(s) excluded (use EXACT for videos).")
    val progress = DuplicateProgressReporter(onProgress)
    val fingerprints = mutableListOf<DuplicateVisualFingerprint>()
    var bytes = 0L
    var attempted = 0
    progress.emit("hash_visual", 0, images.size, bytes)
    for (file in images) {
        active.ensureActive()
        try {
            val fingerprint = readDuplicateVisualFingerprint(context, file) { read ->
                bytes += read
                progress.emit("hash_visual", attempted, images.size, bytes)
            }
            active.ensureActive()
            if (fingerprint == null) issues.skip("Low-information visual hash", unreadableFile = false)
            else fingerprints.add(fingerprint)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: IOException) {
            active.ensureActive()
            issues.skip("Image IO, EXIF, or decode failure")
        } catch (_: SecurityException) {
            active.ensureActive()
            issues.skip("Image permission denied or revoked")
        } catch (failure: RuntimeException) {
            if (failure is DuplicateProgressCallbackFailure) throw failure
            active.ensureActive()
            issues.skip("Image provider or decoder failure")
        }
        attempted++
        progress.emit("hash_visual", attempted, images.size, bytes)
    }
    progress.emit("hash_visual", attempted, images.size, bytes, force = true)
    progress.emit("group_visual", 0, fingerprints.size, bytes)
    val groups = groupVisualDuplicateFingerprints(fingerprints) { completed ->
        progress.emit("group_visual", completed, fingerprints.size, bytes)
    }
    active.ensureActive()
    progress.emit("complete", attempted, images.size, bytes, force = true)
    active.ensureActive()
    return DuplicateScanResult(
        groups, files.size, fingerprints.size, issues.unreadable, bytes,
        issues.partial, System.currentTimeMillis(), issues.warnings(),
    )
}

/**
 * Name-only grouping: files that share the same normalized display name,
 * regardless of folder, size or content. This is a review aid, not a proof of
 * equality, and the result carries that warning explicitly.
 */
private fun compareDuplicateNames(
    files: List<DuplicateScanFile>,
    onProgress: (DuplicateScanProgress) -> Unit,
): DuplicateScanResult {
    onProgress(
        DuplicateScanProgress(
            phase = "مقارنة أسماء الملفات",
            completedFiles = 0,
            totalFiles = files.size,
            bytesRead = 0,
        )
    )
    val groups = files
        .groupBy { it.name.trim().lowercase() }
        .values
        .filter { it.size > 1 }
        .map { matches ->
            val ordered = matches.sortedWith(duplicateKeeperOrder)
            DuplicateScanGroup(
                id = "name:" + ordered.first().uriString,
                files = ordered,
                kind = DuplicateMatchKind.NAME,
            )
        }
        .sortedWith(
            compareByDescending<DuplicateScanGroup> { group ->
                group.files.sumOf { it.sizeBytes }
            }.thenBy { it.id }
        )
    onProgress(
        DuplicateScanProgress(
            phase = "اكتملت مقارنة الأسماء",
            completedFiles = files.size,
            totalFiles = files.size,
            bytesRead = 0,
        )
    )
    return DuplicateScanResult(
        groups = groups,
        filesDiscovered = files.size,
        filesCompared = files.size,
        unreadableFiles = 0,
        bytesRead = 0,
        partialAccess = false,
        completedAt = System.currentTimeMillis(),
        warnings = listOf("تطابق الاسم لا يعني تطابق المحتوى. راجع كل ملف قبل أي حذف."),
    )
}

/** Local, read-only full scan of mounted provider-visible image/video collections.
 * No network, no deletion, no AI confidence, no hard scan-budget truncation.
 * Callbacks run on Dispatchers.IO; callers must marshal UI updates themselves.
 */
internal suspend fun scanDuplicates(
    context: Context,
    mode: DuplicateScanMode,
    onProgress: (DuplicateScanProgress) -> Unit,
): DuplicateScanResult = withContext(Dispatchers.IO) {
    val discovery = discoverDuplicateMedia(context, onProgress)
    ensureActive()
    val compared = when (mode) {
        DuplicateScanMode.EXACT -> compareExactDuplicateFiles(discovery.files, { file ->
            context.contentResolver.openInputStream(Uri.parse(file.uriString))
        }, onProgress)
        DuplicateScanMode.VISUAL -> compareDuplicateVisualMedia(context, discovery.files, onProgress)
        DuplicateScanMode.NAME -> compareDuplicateNames(discovery.files, onProgress)
    }
    ensureActive()
    compared.copy(
        unreadableFiles = compared.unreadableFiles + discovery.issues.unreadable,
        partialAccess = compared.partialAccess || discovery.issues.partial,
        warnings = (discovery.issues.warnings() + compared.warnings).distinct(),
        completedAt = System.currentTimeMillis(),
    )
}
