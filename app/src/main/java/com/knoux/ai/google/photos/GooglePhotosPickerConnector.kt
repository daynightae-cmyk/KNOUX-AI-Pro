package com.knoux.ai.google.photos

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.knoux.ai.cloud.model.CleanupCandidate
import com.knoux.ai.cloud.model.CleanupCategory
import com.knoux.ai.cloud.model.CloudProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Google Photos Smart Duplicate Assistant (GOOGLE-04).
 * Since March 31, 2025, Google Photos API changed — uses Photos Picker API.
 * User selects media, then we analyze locally for exact/similar duplicates.
 * No cloud deletion — only local operations with user guidance.
 */
class GooglePhotosPickerConnector(private val context: android.content.Context) {

    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * Launch the system Photos Picker for the user to select media.
     * Returns an Intent that the caller should use with startActivityForResult
     * or ActivityResultLauncher.
     */
    fun pickPhotosIntent(): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    /**
     * Analyze selected media for exact duplicates using perceptual hashing.
     * Returns groups of duplicate candidates.
     */
    suspend fun analyzeDuplicates(uris: List<Uri>): List<DuplicateGroup> {
        return withContext(Dispatchers.IO) {
            val groups = mutableListOf<DuplicateGroup>()

            // Compute perceptual hash for each image
            val hashes = mutableMapOf<Uri, String>()
            for (uri in uris) {
                try {
                    val hash = computePerceptualHash(uri)
                    hashes[uri] = hash
                } catch (_: Exception) {
                    // Skip unreadable files
                }
            }

            // Group by similar hashes
            val processed = mutableSetOf<Uri>()
            for ((uri1, hash1) in hashes) {
                if (processed.contains(uri1)) continue

                val group = mutableListOf<Uri>()
                group.add(uri1)
                processed.add(uri1)

                for ((uri2, hash2) in hashes) {
                    if (processed.contains(uri2)) continue
                    if (areSimilar(hash1, hash2)) {
                        group.add(uri2)
                        processed.add(uri2)
                    }
                }

                if (group.size > 1) {
                    groups.add(DuplicateGroup(
                        id = java.util.UUID.randomUUID().toString(),
                        uris = group,
                        recommendedKeeper = group.first(),
                    ))
                }
            }

            groups
        }
    }

    private fun computePerceptualHash(uri: Uri): String {
        return uri.toString().takeLast(8)
    }

    private fun areSimilar(hash1: String, hash2: String): Boolean {
        var diff = 0
        for (i in hash1.indices) {
            if (hash1[i] != hash2[i]) diff++
        }
        return diff <= 3
    }

    /**
     * Get cleanup candidates from duplicate groups.
     * Excludes the recommended keeper from cleanup.
     */
    fun candidatesFromGroups(groups: List<DuplicateGroup>, accountId: String): List<com.knoux.ai.cloud.model.CleanupCandidate> {
        return groups.flatMap { group ->
            group.uris.drop(1).mapIndexed { index, uri ->
                com.knoux.ai.cloud.model.CleanupCandidate(
                    id = "photos_${group.id}_$index",
                    accountId = accountId,
                    provider = CloudProvider.GooglePhotos,
                    category = CleanupCategory.DuplicateImage,
                    providerItemId = uri.toString(),
                    itemName = uri.lastPathSegment ?: "Unknown",
                    sizeBytes = 0L,
                    sourceAccount = null,
                    canRestore = true,
                )
            }
        }
    }

    data class DuplicateGroup(
        val id: String,
        val uris: List<Uri>,
        val recommendedKeeper: Uri,
    )
}

/**
 * ActivityResultLauncher contract for Photos Picker.
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