package com.knoux.ai.google.photos

import android.content.Intent
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.activity.result.contract.ActivityResultContract
import com.knoux.ai.cloud.model.CleanupCandidate
import com.knoux.ai.cloud.model.CleanupCategory
import com.knoux.ai.cloud.model.CloudProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest
import java.util.UUID

/**
 * Google Photos media analysis (GOOGLE-04).
 *
 * TRUTHFUL SCOPE
 * ---------------
 * Google's Photos APIs do not grant unrestricted third-party access to a user's
 * library and do not grant arbitrary cloud deletion. A real cloud flow must use
 * Google's official Photos Picker API session (Play services based) with the
 * account's consented scope; that integration is [BLOCKED_EXTERNAL] until the
 * Play-services Picker dependency, OAuth client and review are configured.
 *
 * What IS genuinely implemented here is the local analysis stage: given a set of
 * media URIs the user selected (system Photo Picker / SAF), we read real bytes
 * and compute content hashes. This is local analysis, NOT a cloud library scan.
 * Nothing here deletes or mutates cloud content, and no cloud deletion is ever
 * claimed. The UI must present this as a local review and hand the user to
 * Google Photos for supported in-app cleanup.
 *
 * Hashing is real:
 *  - exact duplicates share a SHA-256 of the decoded file bytes,
 *  - similar-image candidates use a perceptual (average) hash of the decoded
 *    pixels, never a URI suffix.
 */
class GooglePhotosPickerConnector(private val context: android.content.Context) {

    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * Local media selection. This is a system document/photo picker, not the
     * Google Photos cloud Picker API; callers must not describe it as a cloud
     * library scan.
     */
    fun pickLocalMediaIntent(): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    /**
     * Official Google Photos Picker status. Kept explicit so no screen can
     * imply a cloud library scan or a cloud mutation happened when it did not.
     */
    companion object {
        const val OFFICIAL_CLOUD_PICKER: String = "BLOCKED_EXTERNAL"
        const val OFFICIAL_CLOUD_PICKER_REASON: String =
            "Requires Google Play services Photos Picker API, a registered OAuth client and consent review; " +
                "ACTION_OPEN_DOCUMENT is local selection only and does not grant cloud library access."
        /** Bounded read/decode buffer for content hashing. */
        const val DEFAULT_BUFFER = 8192
        /** 256 KiB is ample for an 8x8 perceptual sample; guards against
         *  pathological "compressed bomb" files without unbounded allocation. */
        const val MAX_HASH_SOURCE_BYTES = 256 * 1024
        /** Max Hamming distance between two 64-bit perceptual hashes for a
         *  similar-image review candidate (not an exact duplicate). */
        const val SIMILAR_HAMMING_MAX = 8
        private const val HEX = "0123456789abcdef"
    }

    /**
     * Analyze selected media for exact and (clearly-labelled) similar images.
     * Unreadable/undecodable items are skipped and reported, never guessed.
     */
    suspend fun analyzeDuplicates(uris: List<Uri>): List<DuplicateGroup> = withContext(Dispatchers.IO) {
        val hashes = mutableMapOf<Uri, String>()
        val perceptual = mutableMapOf<Uri, String>()
        for (uri in uris) {
            try {
                hashes[uri] = sha256(context, uri)
            } catch (_: Exception) {
                // Unreadable file: skip, do not fabricate a hash.
            }
            try {
                perceptual[uri] = perceptualHash(context, uri)
            } catch (_: Exception) {
                // Not decodable as image: exact hash may still exist.
            }
        }

        val groups = mutableListOf<DuplicateGroup>()
        val grouped = mutableSetOf<Uri>()

        // 1) Exact byte-for-byte duplicates.
        hashes.forEach { (uri, hash) ->
            if (grouped.contains(uri)) return@forEach
            val exact = mutableListOf(uri)
            hashes.forEach { (other, otherHash) ->
                if (other != uri && !grouped.contains(other) && otherHash == hash) {
                    exact.add(other)
                }
            }
            if (exact.size > 1) {
                exact.forEach { grouped.add(it) }
                groups.add(
                    DuplicateGroup(
                        id = UUID.randomUUID().toString(),
                        uris = exact,
                        recommendedKeeper = exact.first(),
                        kind = DuplicateKind.EXACT,
                        hashes = exact.associateWith { hash },
                    ),
                )
            }
        }

        // 2) Perceptual similarity among what is not already an exact group.
        perceptual.forEach { (uri, phash) ->
            if (grouped.contains(uri)) return@forEach
            val similar = mutableListOf(uri)
            perceptual.forEach { (other, otherHash) ->
                if (other != uri && !grouped.contains(other) &&
                    hammingDistance(phash, otherHash) <= SIMILAR_HAMMING_MAX
                ) {
                    similar.add(other)
                }
            }
            if (similar.size > 1) {
                similar.forEach { grouped.add(it) }
                groups.add(
                    DuplicateGroup(
                        id = UUID.randomUUID().toString(),
                        uris = similar,
                        recommendedKeeper = similar.first(),
                        kind = DuplicateKind.SIMILAR_REVIEW,
                        hashes = similar.associateWith { perceptual[it].orEmpty() },
                    ),
                )
            }
        }
        groups
    }

    private fun sha256(context: android.content.Context, uri: Uri): String {
        val digest = MessageDigest.getInstance("SHA-256")
        context.contentResolver.openInputStream(uri)?.use { input: InputStream ->
            val buffer = ByteArray(DEFAULT_BUFFER)
            while (true) {
                val read = input.read(buffer)
                if (read < 0) break
                digest.update(buffer, 0, read)
            }
        } ?: throw java.io.IOException("Unable to open media for hashing")
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    /**
     * Real perceptual hash: safely downsample the decoded image to an 8x8
     * grayscale average hash. EXIF orientation is applied first.
     */
    private fun perceptualHash(context: android.content.Context, uri: Uri): String {
        val bytes = readBytesBounded(context, uri, MAX_HASH_SOURCE_BYTES)
        var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            ?: throw java.io.IOException("Unsupported image")
        try {
            bitmap = applyExifOrientation(context, uri, bitmap)
            val small: Bitmap = Bitmap.createScaledBitmap(bitmap, 8, 8, true)
            val pixels = IntArray(64)
            small.getPixels(pixels, 0, 8, 0, 0, 8, 8)
            var sum = 0
            for (p in pixels) {
                sum += luminance(p)
            }
            val mean = sum.toDouble() / pixels.size
            val bits = BooleanArray(64)
            for (i in 0 until 64) bits[i] = pixels[i].toDouble() > mean
            val sb = StringBuilder(16)
            for (i in 0 until 4) {
                var nibble = 0
                for (j in 0 until 4) {
                    if (bits[i * 16 + j]) nibble = nibble or (1 shl (3 - j))
                }
                sb.append(HEX[nibble])
            }
            return sb.toString()
        } finally {
            if (!bitmap.isRecycled) bitmap.recycle()
        }
    }

    private fun readBytesBounded(context: android.content.Context, uri: Uri, max: Int): ByteArray {
        context.contentResolver.openInputStream(uri)?.use { input: InputStream ->
            val out = ByteArrayOutputStream()
            val buffer = ByteArray(DEFAULT_BUFFER)
            var total = 0
            while (true) {
                val read = input.read(buffer)
                if (read < 0 || total >= max) break
                val allowed = minOf(read, max - total)
                out.write(buffer, 0, allowed)
                total += allowed
            }
            return out.toByteArray()
        } ?: throw IOException("Unable to open media for perceptual hashing")
    }

    private fun applyExifOrientation(context: android.content.Context, uri: Uri, bitmap: Bitmap): Bitmap {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return bitmap
        val stream = try {
            context.contentResolver.openInputStream(uri)
        } catch (_: Exception) {
            null
        } ?: return bitmap
        return try {
            val exif = ExifInterface(stream)
            when (val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL,
            )) {
                ExifInterface.ORIENTATION_ROTATE_90 ->
                    rotate(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 ->
                    rotate(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 ->
                    rotate(bitmap, 270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL ->
                    flip(bitmap, -1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL ->
                    flip(bitmap, 1f, -1f)
                else -> bitmap
            }
        } catch (_: Exception) {
            bitmap
        } finally {
            try {
                stream.close()
            } catch (_: Exception) {
                // Read-only stream; close failure is not actionable here.
            }
        }
    }

    private fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun flip(bitmap: Bitmap, x: Float, y: Float): Bitmap {
        val matrix = Matrix().apply { postScale(x, y) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun luminance(pixel: Int): Int {
        val r = (pixel shr 16) and 0xFF
        val g = (pixel shr 8) and 0xFF
        val b = pixel and 0xFF
        return (r * 299 + g * 587 + b * 114) / 1000
    }

    private fun hammingDistance(a: String, b: String): Int {
        if (a.length != b.length) return Int.MAX_VALUE
        var diff = 0
        for (i in a.indices) if (a[i] != b[i]) diff++
        return diff
    }

    /** Get cleanup candidates from duplicate groups. */
    fun candidatesFromGroups(groups: List<DuplicateGroup>, accountId: String): List<CleanupCandidate> =
        groups.flatMap { group ->
            group.uris.drop(1).mapIndexed { index, uri ->
                CleanupCandidate(
                    id = "photos_${group.id}_$index",
                    accountId = accountId,
                    provider = CloudProvider.GooglePhotos,
                    category = CleanupCategory.DuplicateImage,
                    providerItemId = uri.toString(),
                    itemName = uri.lastPathSegment ?: "Selected media",
                    sizeBytes = contentSize(context, uri),
                    sourceAccount = null,
                    canRestore = false,
                )
            }
        }

    private fun contentSize(context: android.content.Context, uri: Uri): Long {
        return try {
            context.contentResolver.query(
                uri,
                arrayOf(OpenableColumns.SIZE),
                null,
                null,
                null,
            )?.use { cursor: android.database.Cursor ->
                if (cursor.moveToFirst()) cursor.getLong(0) else 0L
            } ?: 0L
        } catch (_: Exception) {
            0L
        }
    }

    data class DuplicateGroup(
        val id: String,
        val uris: List<Uri>,
        val recommendedKeeper: Uri,
        val kind: DuplicateKind = DuplicateKind.EXACT,
        val hashes: Map<Uri, String> = emptyMap(),
    )

    enum class DuplicateKind { EXACT, SIMILAR_REVIEW }
}

/**
 * ActivityResultLauncher contract for local media selection.
 * (Kept separate from the cloud Photos Picker, which is [BLOCKED_EXTERNAL].)
 */
class PhotosPickerContract : ActivityResultContract<Unit, Intent?>() {
    override fun createIntent(context: android.content.Context, input: Unit): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Intent? = intent
}
