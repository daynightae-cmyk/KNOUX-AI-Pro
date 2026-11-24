package com.knoux.ai.ui.v2

import android.app.ActivityManager
import android.app.AppOpsManager
import android.app.KeyguardManager
import android.app.usage.StorageStatsManager
import android.app.usage.UsageStatsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.BatteryManager
import android.os.Environment
import android.os.Process
import android.os.StatFs
import android.os.storage.StorageManager
import android.provider.MediaStore
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/* ------------------------------------------------------------------ *
 * KNOUX AI Pro — live device observations
 *
 * Everything in this file is read from the running Android device.
 * Capabilities the platform hides stay null / unavailable instead of
 * being replaced with a design-mockup number.
 * ------------------------------------------------------------------ */

data class BatteryFacts(
    val levelPercent: Int? = null,
    val temperatureC: Float? = null,
    val healthCode: Int = BatteryManager.BATTERY_HEALTH_UNKNOWN,
    val charging: Boolean = false,
    val plugged: Int = 0,
    val voltageMv: Int? = null,
    val technology: String? = null,
    val statusCode: Int = BatteryManager.BATTERY_STATUS_UNKNOWN,
) {
    val healthLabel: String
        get() = when (healthCode) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "جيدة"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "حرارة مرتفعة"
            BatteryManager.BATTERY_HEALTH_COLD -> "باردة"
            BatteryManager.BATTERY_HEALTH_DEAD -> "تالفة"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "جهد مرتفع"
            else -> "غير معروفة"
        }

    val statusLabel: String
        get() = when (statusCode) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "تشحن"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "تستهلك"
            BatteryManager.BATTERY_STATUS_FULL -> "ممتلئة"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "لا تشحن"
            else -> "غير معروفة"
        }
}

data class StorageFacts(
    val totalBytes: Long = 0L,
    val freeBytes: Long = 0L,
) {
    /**
     * True only when StatFs actually returned a volume. A failed read yields
     * 0/0, which is NOT "0% used": callers must show "غير متاح"/error instead of
     * a healthy gauge or a false "+6" health bonus.
     */
    val isMeasured: Boolean get() = totalBytes > 0L

    val usedBytes: Long get() = (totalBytes - freeBytes).coerceAtLeast(0L)
    val usedFraction: Float
        get() = if (isMeasured) usedBytes.toFloat() / totalBytes.toFloat() else 0f
}

/** Live RAM observation from ActivityManager; null fields when hidden. */
data class RamFacts(
    val totalBytes: Long = 0L,
    val availBytes: Long = 0L,
    val lowMemory: Boolean = false,
) {
    val usedBytes: Long get() = (totalBytes - availBytes).coerceAtLeast(0L)
    val usedFraction: Float
        get() = if (totalBytes > 0L) usedBytes.toFloat() / totalBytes.toFloat() else 0f
}

data class AppPermissionGroup(
    val key: String,
    val label: String,
    val appCount: Int,
    val packages: List<String>,
    val grantedCount: Int = 0,
    val grantedPackages: List<String> = emptyList(),
)

data class InstalledAppFacts(
    val label: String,
    val packageName: String,
    val versionName: String?,
    val isSystem: Boolean,
    val isEnabled: Boolean,
    val declaredPermissions: List<String>,
    val sensitivePermissions: List<String>,
    val icon: Drawable?,
    val appBytes: Long? = null,
    /** Foreground time over the last 7 days; null without Usage Access. */
    val usage7dMs: Long? = null,
)

data class MediaFileFacts(
    val name: String,
    val sizeBytes: Long,
    val bucket: String?,
    val isVideo: Boolean,
    val uriString: String,
    val isAudio: Boolean = false,
)

data class MediaDuplicateGroup(
    val name: String,
    val sizeBytes: Long,
    val count: Int,
    val uris: List<String>,
    val isVideo: Boolean,
) {
    val sampleUri: String get() = uris.firstOrNull().orEmpty()
    val reclaimableBytes: Long get() = sizeBytes * (count - 1).coerceAtLeast(0)
}

data class MediaFacts(
    val permissionGranted: Boolean = false,
    /**
     * The MediaStore walk finished. When permission is granted but this stays
     * false, the query itself failed and every zero below is "unknown", not
     * "the phone has no photos".
     */
    val readSucceeded: Boolean = false,
    val images: Int = 0,
    val videos: Int = 0,
    val imagesBytes: Long = 0L,
    val videosBytes: Long = 0L,
    val duplicateGroups: List<MediaDuplicateGroup> = emptyList(),
    val duplicateCandidates: Int = 0,
    val largestFiles: List<MediaFileFacts> = emptyList(),
    /** Images sharing a byte size with another image (similar candidates, not confirmed copies). */
    val similarImageCandidates: Int = 0,
    /** Audio files that are byte-identical copies of another audio file. */
    val duplicateAudioFiles: Int = 0,
    /** The duplicate group with the most copies (null when none found). */
    val largestGroup: MediaDuplicateGroup? = null,
)

/**
 * Cleanable-file observations for the Smart Clean hub. Downloads come from
 * MediaStore's Download bucket; APKs and empty folders are walked on shared
 * storage within a strict directory budget. Hidden or unreadable trees stay
 * at zero instead of being guessed.
 */
data class CleanFileFacts(
    val downloadsBytes: Long = 0L,
    val downloadsCount: Int = 0,
    val apkBytes: Long = 0L,
    val apkCount: Int = 0,
    val emptyFolders: Int = 0,
    /** Dot-prefixed files visible through MediaStore (deep-scan hub). */
    val hiddenFiles: Int = 0,
    /** Sum of every row the bounded MediaStore walk actually read. */
    val scannedBytes: Long = 0L,
    /** Residue hub: files inside known app-leftover buckets (cache/webview/logs). */
    val residueFiles: Int = 0,
    val residueBytes: Long = 0L,
    /** Cache-bucket files older than the residue age threshold. */
    val oldCacheFiles: Int = 0,
    val oldCacheBytes: Long = 0L,
    /** Download-bucket files that look truncated (.part/.crdownload/.tmp). */
    val brokenDownloads: Int = 0,
    /** .obb files outside the Android/obb sandbox, visible to MediaStore. */
    val obbFiles: Int = 0,
    val obbBytes: Long = 0L,
    /** Files inside browser-named buckets (Chrome/WebView/Firefox/…). */
    val browserFiles: Int = 0,
    val browserBytes: Long = 0L,
    /** Log-named files (.log / logs bucket). */
    val logFiles: Int = 0,
    /** Folders named after packages that are no longer installed. */
    val orphanAppFolders: Int = 0,
    /** Storage hub: largest MediaStore bucket by summed bytes (null when none). */
    val largestBucket: Pair<String, Long>? = null,
    /** Storage hub: summed bytes of image+video buckets (DCIM/Camera/Pictures/…). */
    val mediaBucketBytes: Long = 0L,
)

data class ScanHistoryEntry(
    val timestamp: Long,
    val appsScanned: Int,
    val privacyFindings: Int,
    val cacheBytes: Long,
    val storageUsedBytes: Long,
    val batteryLevel: Int?,
)

data class DeviceFacts(
    val battery: BatteryFacts = BatteryFacts(),
    val storage: StorageFacts = StorageFacts(),
    val ram: RamFacts = RamFacts(),
    val installedApps: List<InstalledAppFacts> = emptyList(),
    val ownCacheBytes: Long = 0L,
    val ownCacheFiles: Int = 0,
    val media: MediaFacts = MediaFacts(),
    val cleanFiles: CleanFileFacts = CleanFileFacts(),
    val usageAccessGranted: Boolean = false,
    val privacyGroups: List<AppPermissionGroup> = emptyList(),
    val sensitiveAppCount: Int = 0,
    /** A VPN transport is active right now (ConnectivityManager, real). */
    val vpnActive: Boolean = false,
    /** Device-admin apps declared through PackageManager (real). */
    val deviceAdminCount: Int = 0,
    /** Apps declaring SYSTEM_ALERT_WINDOW — «الظهور فوق التطبيقات». */
    val overlayAppCount: Int = 0,
    /** Screen lock is set and secure (KeyguardManager, real). */
    val lockSecure: Boolean = false,
    /** Biometric hardware class: 2 strong, 1 weak, 0 none, -1 unknown. */
    val biometricStrength: Int = -1,
    /**
     * True only when the live readers actually returned measurable state.
     * [computedAt] alone is a wall-clock stamp, not proof of a successful read.
     */
    val measured: Boolean = false,
    val computedAt: Long = 0L,
) {
    val installedCount: Int get() = installedApps.size
    val userAppCount: Int get() = installedApps.count { !it.isSystem }
    /**
     * True only when the snapshot rests on real measurements; drive-by
     * defaults and a bare wall-clock stamp are not evidence.
     */
    val isLive: Boolean get() = computedAt > 0L && measured

    val healthEstimate: Int
        get() {
            if (!isLive) return 0
            var score = 70
            val level = battery.levelPercent
            if (level != null) score += when {
                level >= 80 -> 12
                level >= 40 -> 8
                level >= 20 -> 2
                else -> -10
            }
            // A missing storage reading must not earn the "low usage" bonus.
            if (storage.isMeasured) score += when {
                storage.usedFraction >= .93f -> -20
                storage.usedFraction >= .82f -> -8
                storage.usedFraction <= .60f -> 6
                else -> 0
            }
            if (battery.charging) score += 2
            if (battery.healthCode == BatteryManager.BATTERY_HEALTH_GOOD) score += 4
            if (sensitiveAppCount > 40) score -= 4
            return score.coerceIn(20, 98)
        }
}

private val sensitivePermissions = listOf(
    "android.permission.CAMERA",
    "android.permission.RECORD_AUDIO",
    "android.permission.ACCESS_FINE_LOCATION",
    "android.permission.ACCESS_COARSE_LOCATION",
    "android.permission.READ_CONTACTS",
    "android.permission.READ_SMS",
    "android.permission.READ_CALL_LOG",
    "android.permission.READ_PHONE_STATE",
    "android.permission.READ_EXTERNAL_STORAGE",
    "android.permission.READ_MEDIA_IMAGES",
    "android.permission.READ_MEDIA_VIDEO",
    "android.permission.POST_NOTIFICATIONS",
)

private const val SCAN_HISTORY_PREFS = "knoux_scan_history_v1"
private const val SCAN_HISTORY_KEY = "entries"

fun formatBytes(bytes: Long): String {
    if (bytes <= 0L) return "0 B"
    val kb = 1024.0
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    var value = bytes.toDouble()
    var index = 0
    while (value >= kb && index < units.lastIndex) {
        value /= kb
        index++
    }
    val decimals = when {
        index == 0 -> 0
        value >= 100 -> 0
        value >= 10 -> 1
        else -> 2
    }
    return String.format(Locale.US, "%.${decimals}f %s", value, units[index])
}

fun formatCount(value: Int): String = String.format(Locale.US, "%,d", value)

fun formatScanTime(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd MMM · HH:mm", Locale("ar"))
    return formatter.format(Date(timestamp))
}

/* ------------------------------------------------------------------ *
 * Readers
 * ------------------------------------------------------------------ */

private fun readBattery(context: Context): BatteryFacts {
    val intent = runCatching {
        context.registerReceiver(null, android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }.getOrNull() ?: return BatteryFacts()
    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
    val percent = if (level >= 0 && scale > 0) (level * 100 / scale) else null
    val tempRaw = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE)
    val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
    val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)
    return BatteryFacts(
        levelPercent = percent,
        temperatureC = if (tempRaw != Int.MIN_VALUE) tempRaw / 10f else null,
        healthCode = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN),
        charging = plugged != 0 || status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL,
        plugged = plugged,
        voltageMv = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1).takeIf { it > 0 },
        technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY),
        statusCode = status,
    )
}

private fun readStorage(): StorageFacts = runCatching {
    val stat = StatFs(Environment.getDataDirectory().absolutePath)
    val total = stat.blockCountLong * stat.blockSizeLong
    val free = stat.availableBlocksLong * stat.blockSizeLong
    StorageFacts(totalBytes = total, freeBytes = free)
}.getOrElse { StorageFacts() }

private fun readRam(context: Context): RamFacts = runCatching {
    val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val info = ActivityManager.MemoryInfo()
    am.getMemoryInfo(info)
    RamFacts(totalBytes = info.totalMem, availBytes = info.availMem, lowMemory = info.lowMemory)
}.getOrElse { RamFacts() }

private fun directoryStats(root: File?, limit: Int = 40_000): Pair<Long, Int> {
    if (root == null || !root.exists()) return 0L to 0
    var bytes = 0L
    var files = 0
    val stack = ArrayDeque<File>()
    stack.add(root)
    while (stack.isNotEmpty() && files < limit) {
        val current = stack.removeLast()
        val children = runCatching { current.listFiles() }.getOrNull() ?: continue
        for (child in children) {
            if (files >= limit) break
            if (runCatching { child.isSymbolicLink() }.getOrDefault(false)) continue
            if (child.isDirectory) {
                stack.add(child)
            } else {
                bytes += runCatching { child.length() }.getOrDefault(0L)
                files++
            }
        }
    }
    return bytes to files
}

private fun File.isSymbolicLink(): Boolean = runCatching {
    java.nio.file.Files.isSymbolicLink(toPath())
}.getOrDefault(false)

fun ownCacheBytes(context: Context): Pair<Long, Int> {
    val roots = listOfNotNull(
        context.cacheDir,
        context.codeCacheDir,
        context.externalCacheDir,
    )
    var bytes = 0L
    var files = 0
    roots.distinctBy { it.absolutePath }.forEach { root ->
        val (b, f) = directoryStats(root)
        bytes += b
        files += f
    }
    return bytes to files
}

fun clearOwnCache(context: Context): Long {
    val roots = listOfNotNull(context.cacheDir, context.codeCacheDir, context.externalCacheDir)
    return roots.distinctBy { it.absolutePath }.sumOf { root ->
        root.listFiles()?.sumOf(::deleteCachedEntry) ?: 0L
    }
}

internal fun deleteCachedEntry(entry: File): Long {
    if (entry.isSymbolicLink()) {
        entry.delete()
        return 0L
    }
    if (entry.isDirectory) {
        val removedBytes = entry.listFiles()?.sumOf(::deleteCachedEntry) ?: return 0L
        entry.delete()
        return removedBytes
    }
    val bytes = entry.length()
    return if (entry.delete()) bytes else 0L
}

private const val MAX_HASH_FILE_BYTES = 32L * 1024 * 1024
private const val MAX_HASH_SCAN_BYTES = 128L * 1024 * 1024
private const val MAX_HASH_FILES = 256

internal fun findExactMediaDuplicates(
    items: List<MediaFileFacts>,
    open: (MediaFileFacts) -> InputStream?,
): List<MediaDuplicateGroup> {
    val candidates = items.filter { it.sizeBytes in 1..MAX_HASH_FILE_BYTES }
        .groupBy { Triple(it.sizeBytes, it.isVideo, it.isAudio) }
        .values
        .filter { it.size > 1 }
        .sortedBy { it.first().sizeBytes }

    var bytesRemaining = MAX_HASH_SCAN_BYTES
    var filesRemaining = MAX_HASH_FILES
    val buffer = ByteArray(64 * 1024)
    val groups = mutableListOf<MediaDuplicateGroup>()

    for (candidateGroup in candidates) {
        val matches = mutableMapOf<List<Byte>, MutableList<MediaFileFacts>>()
        for (item in candidateGroup) {
            if (filesRemaining == 0 || item.sizeBytes > bytesRemaining) break
            filesRemaining--
            bytesRemaining -= item.sizeBytes
            val hash = try {
                open(item)?.use { sha256OfSize(it, item.sizeBytes, buffer) }
            } catch (_: java.io.IOException) {
                null
            } catch (_: SecurityException) {
                null
            }
            if (hash != null) matches.getOrPut(hash) { mutableListOf() }.add(item)
        }
        for (copies in matches.values.filter { it.size > 1 }) {
            val keeper = copies.first()
            groups.add(
                MediaDuplicateGroup(
                    name = keeper.name,
                    sizeBytes = keeper.sizeBytes,
                    count = copies.size,
                    uris = copies.map { it.uriString },
                    isVideo = keeper.isVideo,
                ),
            )
        }
        if (filesRemaining == 0 || bytesRemaining < candidateGroup.first().sizeBytes) break
    }
    return groups.sortedByDescending { it.reclaimableBytes }.take(12)
}

private fun sha256OfSize(input: InputStream, expectedBytes: Long, buffer: ByteArray): List<Byte>? {
    val digest = MessageDigest.getInstance("SHA-256")
    var remaining = expectedBytes
    while (remaining > 0) {
        val count = input.read(buffer, 0, minOf(remaining, buffer.size.toLong()).toInt())
        if (count <= 0) return null
        digest.update(buffer, 0, count)
        remaining -= count
    }
    return if (input.read() == -1) digest.digest().toList() else null
}

private fun readMedia(context: Context): MediaFacts {
    val permission = if (android.os.Build.VERSION.SDK_INT >= 33) {
        "android.permission.READ_MEDIA_IMAGES"
    } else {
        "android.permission.READ_EXTERNAL_STORAGE"
    }
    val videoPermission = if (android.os.Build.VERSION.SDK_INT >= 33) {
        "android.permission.READ_MEDIA_VIDEO"
    } else {
        "android.permission.READ_EXTERNAL_STORAGE"
    }
    val imagesGranted = context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    val videosGranted = context.checkSelfPermission(videoPermission) == PackageManager.PERMISSION_GRANTED
    if (!imagesGranted && !videosGranted) return MediaFacts(permissionGranted = false)

    val collection = MediaStore.Files.getContentUri("external")
    val projection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.SIZE,
        MediaStore.Files.FileColumns.MEDIA_TYPE,
        "bucket_display_name",
        MediaStore.Files.FileColumns.MIME_TYPE,
    )
    val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} IN (?,?,?)"
    val args = arrayOf(
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString(),
        MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO.toString(),
    )
    val items = mutableListOf<MediaFileFacts>()
    val querySucceeded = runCatching {
        val cursor = context.contentResolver.query(
            collection, projection, selection, args,
            "${MediaStore.Files.FileColumns.SIZE} DESC",
        ) ?: return@runCatching false
        cursor.use {
            val idCol = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val nameCol = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val sizeCol = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val typeCol = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
            val bucketCol = it.getColumnIndexOrThrow("bucket_display_name")
            var guard = 0
            while (it.moveToNext() && guard < 6000) {
                guard++
                val id = it.getLong(idCol)
                val size = it.getLong(sizeCol)
                val mediaType = it.getInt(typeCol)
                val isVideo = mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
                val isAudio = mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO
                items += MediaFileFacts(
                    name = it.getString(nameCol) ?: "ملف",
                    sizeBytes = size,
                    bucket = it.getString(bucketCol),
                    isVideo = isVideo,
                    uriString = Uri.withAppendedPath(collection, id.toString()).toString(),
                    isAudio = isAudio,
                )
            }
        }
        true
    }.getOrDefault(false)
    val images = items.filter { !it.isVideo && !it.isAudio }
    val videos = items.filter { it.isVideo }
    val audio = items.filter { it.isAudio }
    val grouped = findExactMediaDuplicates(items) { item ->
        context.contentResolver.openInputStream(Uri.parse(item.uriString))
    }
    // Similar-image candidates: images that share a byte size with another
    // image. Honest same-size signal, not a confirmed perceptual match.
    val similarImageCandidates = images.groupBy { it.sizeBytes }
        .values.filter { it.size > 1 }.sumOf { it.size }
    return MediaFacts(
        permissionGranted = true,
        readSucceeded = querySucceeded,
        images = images.size,
        videos = videos.size,
        imagesBytes = images.sumOf { it.sizeBytes },
        videosBytes = videos.sumOf { it.sizeBytes },
        duplicateGroups = grouped,
        duplicateCandidates = grouped.sumOf { it.count },
        largestFiles = items.filter { !it.isAudio }.take(8),
        similarImageCandidates = similarImageCandidates,
        duplicateAudioFiles = grouped.filter { g -> audio.any { it.uriString in g.uris } }.sumOf { it.count },
        largestGroup = grouped.maxByOrNull { it.count },
    )
}

/**
 * Downloads bucket totals from MediaStore (same permission gate as media).
 * Pure aggregation so it can be unit-tested without a ContentResolver.
 */
internal fun aggregateDownloads(rows: List<Pair<String?, Long>>): Pair<Long, Int> {
    val downloads = rows.filter { (bucket, _) ->
        bucket.equals("Download", true) || bucket.equals("Downloads", true)
    }
    return downloads.sumOf { it.second } to downloads.size
}

/** APK rows: name ends with .apk (case-insensitive) or the installer MIME. */
internal fun aggregateApks(rows: List<Triple<String?, String?, Long>>): Pair<Long, Int> {
    val apks = rows.filter { (name, mime, _) ->
        name?.endsWith(".apk", true) == true || mime == "application/vnd.android.package-archive"
    }
    return apks.sumOf { it.third } to apks.size
}

/** One MediaStore row as seen by the residue walk: name, mime, bucket, size, mtime(s). */
internal data class ResidueRow(
    val name: String?,
    val mime: String?,
    val bucket: String?,
    val size: Long,
    val modifiedSec: Long,
)

internal data class ResidueAggregate(
    val residueFiles: Int = 0,
    val residueBytes: Long = 0L,
    val oldCacheFiles: Int = 0,
    val oldCacheBytes: Long = 0L,
    val brokenDownloads: Int = 0,
    val obbFiles: Int = 0,
    val obbBytes: Long = 0L,
    val browserFiles: Int = 0,
    val browserBytes: Long = 0L,
    val logFiles: Int = 0,
)

private val residueBucketHints = listOf("cache", "webview", "temp", "tmp", ".trash", "logs", "log")
private val browserBucketHints = listOf("chrome", "webview", "browser", "firefox", "opera", "samsung", "ucbrowser", "duckduckgo", "brave", "edge")
private val brokenDownloadSuffixes = listOf(".part", ".crdownload", ".tmp", ".partial", ".download")

/**
 * Pure residue aggregation over the bounded MediaStore walk. Buckets are the
 * only honest signal available without MANAGE_EXTERNAL_STORAGE; nothing here
 * claims to see inside Android/data sandboxes.
 */
internal fun aggregateResidue(rows: List<ResidueRow>, nowSec: Long, oldCacheAgeSec: Long = 30L * 24 * 3600): ResidueAggregate {
    var residueFiles = 0; var residueBytes = 0L
    var oldCacheFiles = 0; var oldCacheBytes = 0L
    var broken = 0; var obbFiles = 0; var obbBytes = 0L
    var browserFiles = 0; var browserBytes = 0L; var logFiles = 0
    for (row in rows) {
        val bucket = row.bucket?.lowercase().orEmpty()
        val name = row.name?.lowercase().orEmpty()
        val isCacheBucket = residueBucketHints.any { bucket.contains(it) }
        val isBrowserBucket = browserBucketHints.any { bucket.contains(it) }
        val isDownloadBucket = bucket == "download" || bucket == "downloads"
        val isObb = name.endsWith(".obb")
        val isLog = name.endsWith(".log") || bucket == "logs" || bucket == "log"
        val isBroken = isDownloadBucket && brokenDownloadSuffixes.any { name.endsWith(it) }
        if (isCacheBucket || isBrowserBucket || isObb || isLog) {
            residueFiles++; residueBytes += row.size
        }
        if (isCacheBucket && row.modifiedSec > 0 && nowSec - row.modifiedSec > oldCacheAgeSec) {
            oldCacheFiles++; oldCacheBytes += row.size
        }
        if (isBroken) broken++
        if (isObb) { obbFiles++; obbBytes += row.size }
        if (isBrowserBucket) { browserFiles++; browserBytes += row.size }
        if (isLog) logFiles++
    }
    return ResidueAggregate(residueFiles, residueBytes, oldCacheFiles, oldCacheBytes, broken, obbFiles, obbBytes, browserFiles, browserBytes, logFiles)
}

/**
 * Bounded walk counting top-level folders named like package ids
 * (com.example.app) whose package is no longer installed. Skips the Android
 * sandbox and dot-folders; stops at a directory budget.
 */
internal fun countOrphanAppFolders(root: File?, installedPackages: Set<String>, maxDirs: Int = 4_000): Int {
    if (root == null || !root.exists()) return 0
    val packageLike = Regex("^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*){2,}$")
    var orphan = 0
    var visited = 0
    val stack = ArrayDeque<File>()
    stack.add(root)
    while (stack.isNotEmpty() && visited < maxDirs) {
        val dir = stack.removeLast()
        visited++
        val name = dir.name
        if (name == "Android" || name.startsWith(".")) continue
        if (runCatching { dir.isSymbolicLink() }.getOrDefault(false)) continue
        if (packageLike.matches(name) && name !in installedPackages) orphan++
        val children = runCatching { dir.listFiles() }.getOrNull() ?: continue
        for (child in children) {
            if (child.isDirectory) stack.add(child)
        }
    }
    return orphan
}

private fun readCleanFiles(context: Context, installedPackages: Set<String> = emptySet()): CleanFileFacts {
    val permission = if (android.os.Build.VERSION.SDK_INT >= 33) {
        "android.permission.READ_MEDIA_IMAGES"
    } else {
        "android.permission.READ_EXTERNAL_STORAGE"
    }
    if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
        return CleanFileFacts()
    }
    val collection = MediaStore.Files.getContentUri("external")
    val projection = arrayOf(
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.SIZE,
        MediaStore.Files.FileColumns.MIME_TYPE,
        MediaStore.Files.FileColumns.DATE_MODIFIED,
        "bucket_display_name",
    )
    val rows = mutableListOf<Triple<String?, String?, Long>>()
    val buckets = mutableListOf<Pair<String?, Long>>()
    val residueRows = mutableListOf<ResidueRow>()
    runCatching {
        context.contentResolver.query(collection, projection, null, null, null)?.use { cursor ->
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
            val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)
            val bucketCol = cursor.getColumnIndexOrThrow("bucket_display_name")
            var guard = 0
            while (cursor.moveToNext() && guard < 20_000) {
                guard++
                val name = cursor.getString(nameCol)
                val size = cursor.getLong(sizeCol)
                val mime = cursor.getString(mimeCol)
                val bucket = cursor.getString(bucketCol)
                rows += Triple(name, mime, size)
                buckets += bucket to size
                residueRows += ResidueRow(name, mime, bucket, size, cursor.getLong(dateCol))
            }
        }
    }
    val (downBytes, downCount) = aggregateDownloads(buckets)
    val (apkBytes, apkCount) = aggregateApks(rows)
    val residue = aggregateResidue(residueRows, System.currentTimeMillis() / 1000)
    return CleanFileFacts(
        downloadsBytes = downBytes,
        downloadsCount = downCount,
        apkBytes = apkBytes,
        apkCount = apkCount,
        emptyFolders = countEmptyFolders(Environment.getExternalStorageDirectory()),
        hiddenFiles = countHiddenFiles(rows),
        scannedBytes = rows.sumOf { it.third },
        residueFiles = residue.residueFiles,
        residueBytes = residue.residueBytes,
        oldCacheFiles = residue.oldCacheFiles,
        oldCacheBytes = residue.oldCacheBytes,
        brokenDownloads = residue.brokenDownloads,
        obbFiles = residue.obbFiles,
        obbBytes = residue.obbBytes,
        browserFiles = residue.browserFiles,
        browserBytes = residue.browserBytes,
        logFiles = residue.logFiles,
        orphanAppFolders = countOrphanAppFolders(Environment.getExternalStorageDirectory(), installedPackages),
        largestBucket = largestBucketOf(buckets),
        mediaBucketBytes = mediaBucketBytesOf(buckets),
    )
}

/** Dot-prefixed display names are the hidden files MediaStore exposes. */
internal fun countHiddenFiles(rows: List<Triple<String?, String?, Long>>): Int =
    rows.count { (name, _, _) -> name?.startsWith(".") == true }

/** Storage hub: the bucket with the most summed bytes, or null when empty. */
internal fun largestBucketOf(rows: List<Pair<String?, Long>>): Pair<String, Long>? =
    rows.filter { !it.first.isNullOrBlank() }
        .groupBy { it.first!! }
        .map { (name, list) -> name to list.sumOf { it.second } }
        .maxByOrNull { it.second }

private val mediaBucketHints = listOf("camera", "dcim", "pictures", "screenshots", "movies", "video")

/** Storage hub: bytes inside camera/gallery-style buckets (honest subset). */
internal fun mediaBucketBytesOf(rows: List<Pair<String?, Long>>): Long =
    rows.filter { (bucket, _) ->
        val b = bucket?.lowercase().orEmpty()
        mediaBucketHints.any { b.contains(it) }
    }.sumOf { it.second }

/**
 * Storage hub: used-byte delta between the newest scan and the oldest scan
 * inside the lookback window. Null when fewer than two scans exist — growth
 * is never guessed from a single reading.
 */
internal fun storageGrowthBytes(history: List<ScanHistoryEntry>, nowMs: Long, windowMs: Long = 30L * 24 * 3600 * 1000): Long? {
    val inWindow = history.filter { nowMs - it.timestamp <= windowMs && it.storageUsedBytes > 0 }
    if (inWindow.size < 2) return null
    val newest = inWindow.maxByOrNull { it.timestamp } ?: return null
    val oldest = inWindow.minByOrNull { it.timestamp } ?: return null
    return newest.storageUsedBytes - oldest.storageUsedBytes
}

/**
 * Bounded walk of shared storage counting directories with no entries.
 * Skips symlinks and the Android/data|obb sandboxes; stops at a directory
 * budget so a huge tree can never stall the facts pipeline.
 */
internal fun countEmptyFolders(root: File?, maxDirs: Int = 6_000): Int {
    if (root == null || !root.exists()) return 0
    var empty = 0
    var visited = 0
    val stack = ArrayDeque<File>()
    stack.add(root)
    while (stack.isNotEmpty() && visited < maxDirs) {
        val dir = stack.removeLast()
        visited++
        val name = dir.name
        if (name == "Android" || name.startsWith(".")) continue
        if (runCatching { dir.isSymbolicLink() }.getOrDefault(false)) continue
        val children = runCatching { dir.listFiles() }.getOrNull() ?: continue
        if (children.isEmpty()) {
            empty++
            continue
        }
        for (child in children) {
            if (child.isDirectory) stack.add(child)
        }
    }
    return empty
}

private fun readInstalledApps(context: Context, withSizes: Boolean = true): List<InstalledAppFacts> {
    val pm = context.packageManager
    val infos = runCatching {
        pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
    }.getOrElse { emptyList() }
    val usage = usageLast7Days(context)
    // StorageStats queries cost ~0.5-1s per package on low-end devices; the
    // first snapshot publishes without them and a backfill republishes later.
    val sizes = if (withSizes) appStorageSizes(context, infos.mapNotNull { it.applicationInfo?.packageName }) else emptyMap()
    return infos.mapNotNull { info ->
        val app = info.applicationInfo ?: return@mapNotNull null
        val permissions = info.requestedPermissions?.toList().orEmpty()
        val label = runCatching { pm.getApplicationLabel(app).toString() }.getOrDefault(app.packageName)
        InstalledAppFacts(
            label = label,
            packageName = app.packageName,
            versionName = info.versionName,
            isSystem = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
            isEnabled = runCatching { app.enabled }.getOrDefault(true),
            declaredPermissions = permissions,
            sensitivePermissions = permissions.filter { it in sensitivePermissions },
            icon = null,
            appBytes = sizes[app.packageName],
            usage7dMs = usage[app.packageName],
        )
    }.sortedWith(compareByDescending<InstalledAppFacts> { !it.isSystem }.thenBy { it.label })
}

/**
 * Per-package foreground time over the last 7 days. Empty map when Usage
 * Access is not granted — callers must show «يحتاج إذن», never a guess.
 */
/* ------------------------------------------------------------------ *
 * App-manager aggregates (sec6). Pure functions over the real package
 * list so the hub, the service screens and the unit tests all agree.
 * ------------------------------------------------------------------ */

/** Apps the platform actually measured, largest first. */
internal fun appsBySizeDesc(apps: List<InstalledAppFacts>): List<InstalledAppFacts> =
    apps.filter { it.appBytes != null }.sortedByDescending { it.appBytes ?: 0L }

/** Apps with recorded foreground time, most-used first. */
internal fun appsByUsageDesc(apps: List<InstalledAppFacts>): List<InstalledAppFacts> =
    apps.filter { it.usage7dMs != null }.sortedByDescending { it.usage7dMs ?: 0L }

/**
 * «قليلة الاستخدام»: recorded usage under 30 minutes in the last 7 days.
 * Apps the platform never reported are NOT counted — that would be a guess.
 */
internal fun lowUsageAppCount(apps: List<InstalledAppFacts>, thresholdMs: Long = 30L * 60 * 1000): Int =
    apps.count { it.usage7dMs != null && it.usage7dMs < thresholdMs }

/** Apps whose measured footprint is at least [minBytes] (default 100 MB). */
internal fun largeAppCount(apps: List<InstalledAppFacts>, minBytes: Long = 100L * 1024 * 1024): Int =
    apps.count { (it.appBytes ?: -1L) >= minBytes }

/**
 * Battery attribution needs BatteryStats, which is not a public API. The hub
 * shows «غير متاح» instead of inventing a per-app drain number.
 */
internal fun batteryDrainAppCount(@Suppress("UNUSED_PARAMETER") apps: List<InstalledAppFacts>): Int? = null

/**
 * Notification volume needs NotificationListener access. Without the grant
 * the hub shows «يحتاج إذن»; with it, callers pass the real observed count.
 */
internal fun notificationHeavyAppCount(observed: Int?): Int? = observed

/** Apps declaring at least one sensitive permission — real PackageManager data. */
internal fun permissionReviewAppCount(apps: List<InstalledAppFacts>): Int =
    apps.count { it.sensitivePermissions.isNotEmpty() }

/* ------------------------------------------------------------------ *
 * Privacy aggregates (sec7). Pure functions over the real package list
 * and platform state so hub, panel strip and unit tests all agree.
 * ------------------------------------------------------------------ */

/** Apps declaring SYSTEM_ALERT_WINDOW — can draw over other apps. */
internal fun overlayDeclaredCount(apps: List<InstalledAppFacts>): Int =
    apps.count { "android.permission.SYSTEM_ALERT_WINDOW" in it.declaredPermissions }

/** «مخاطر الالتقاط»: declared camera / mic / screen-capture style access. */
internal fun captureRiskCount(apps: List<InstalledAppFacts>): Int =
    apps.count { app ->
        app.declaredPermissions.any {
            it == "android.permission.CAMERA" ||
                it == "android.permission.RECORD_AUDIO" ||
                it == "android.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION"
        }
    }

/** «المتعقبات»: declared advertising-ID / location style tracking access. */
internal fun trackerDeclaredCount(apps: List<InstalledAppFacts>): Int =
    apps.count { app ->
        app.declaredPermissions.any {
            it == "com.google.android.gms.permission.AD_ID" ||
                it == "android.permission.ACCESS_FINE_LOCATION" ||
                it == "android.permission.ACCESS_COARSE_LOCATION"
        }
    }

/** «تنبيهات الحافظة»: clipboard read access (API 29+ declaration). */
internal fun clipboardAlertCount(apps: List<InstalledAppFacts>): Int =
    apps.count { "android.permission.READ_CLIPBOARD_IN_BACKGROUND" in it.declaredPermissions }

/** Real risk percentage: sensitive apps over installed apps. */
internal fun privacyRiskPercent(sensitive: Int, installed: Int): Int =
    if (installed <= 0) 0 else (sensitive * 100 / installed).coerceIn(0, 100)

/** Approved-art risk bands: منخفض ≤25%، متوسط ≤60%، مرتفع فوق ذلك. */
internal fun privacyRiskLabel(percent: Int): String = when {
    percent <= 25 -> "منخفض"
    percent <= 60 -> "متوسط"
    else -> "مرتفع"
}

/**
 * «خطة الإجراءات»: one actionable review item per real finding — sensitive
 * apps, overlay declarations, capture risks, missing screen lock, open usage
 * access. Never a fixture: an empty device yields an empty plan.
 */
internal fun privacyActionPlanCount(
    sensitive: Int,
    overlay: Int,
    capture: Int,
    lockSecure: Boolean,
    usageGranted: Boolean,
): Int = listOf(sensitive > 0, overlay > 0, capture > 0, !lockSecure, !usageGranted).count { it }

/* ------------------------------------------------------------------ *
 * AI priority aggregates (sec9). Deterministic scores derived from the
 * real readings — the same inputs the recommendation engine uses, so
 * the hub panel and the service screens always agree.
 * ------------------------------------------------------------------ */

/** Cleaning priority: storage pressure + own cache + residue weight. */
internal fun aiCleanPriority(usedFraction: Float, ownCacheBytes: Long, residueBytes: Long): Int {
    var score = 40
    score += when {
        usedFraction >= .9f -> 35
        usedFraction >= .8f -> 25
        usedFraction >= .6f -> 12
        else -> 0
    }
    if (ownCacheBytes > 50L * 1024 * 1024) score += 10 else if (ownCacheBytes > 0) score += 5
    if (residueBytes > 0) score += 8
    return score.coerceIn(0, 99)
}

/** Performance priority: RAM pressure + storage pressure + battery level. */
internal fun aiPerfPriority(ramUsedFraction: Float, storageUsedFraction: Float, levelPercent: Int?): Int {
    var score = 45
    score += when {
        ramUsedFraction >= .85f -> 25
        ramUsedFraction >= .7f -> 15
        ramUsedFraction >= .5f -> 8
        else -> 0
    }
    if (storageUsedFraction >= .9f) score += 15 else if (storageUsedFraction >= .8f) score += 8
    if (levelPercent != null && levelPercent <= 20) score += 10
    return score.coerceIn(0, 99)
}

/** Battery priority: low level + heat + non-good health. */
internal fun aiBatteryPriority(levelPercent: Int?, temperatureC: Float?, healthGood: Boolean): Int {
    var score = 35
    if (levelPercent != null) score += when {
        levelPercent <= 15 -> 40
        levelPercent <= 30 -> 28
        levelPercent <= 50 -> 15
        else -> 5
    }
    if ((temperatureC ?: 0f) >= 43f) score += 15 else if ((temperatureC ?: 0f) >= 38f) score += 8
    if (!healthGood) score += 8
    return score.coerceIn(0, 99)
}

/** Privacy priority: sensitive-app share + missing lock. */
internal fun aiPrivacyPriority(sensitive: Int, installed: Int, lockSecure: Boolean): Int {
    var score = privacyRiskPercent(sensitive, installed)
    if (!lockSecure) score += 15
    return score.coerceIn(0, 99)
}

/** Approved-art band for the smart-device score. */
internal fun aiScoreLabel(score: Int): String = when {
    score >= 80 -> "ممتاز"
    score >= 60 -> "جيد"
    else -> "يحتاج عناية"
}

/** Real weekly insight count from the Room/SharedPreferences scan journal. */
internal fun weeklyInsightCount(history: List<ScanHistoryEntry>, nowMs: Long): Int =
    history.count { nowMs - it.timestamp <= 7L * 24 * 3600 * 1000 }

internal fun usageLast7Days(context: Context, nowMs: Long = System.currentTimeMillis()): Map<String, Long> {
    if (!hasUsageAccess(context)) return emptyMap()
    return runCatching {
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, nowMs - 7L * 24 * 3600 * 1000, nowMs)
        stats.orEmpty()
            .groupBy { it.packageName }
            .mapValues { (_, list) -> list.sumOf { it.totalTimeInForeground } }
            .filterValues { it >= 0L }
    }.getOrDefault(emptyMap())
}

/**
 * Real per-app storage (APK + data + cache) via StorageStatsManager.
 * Packages the platform refuses to measure are simply absent from the map.
 */
internal fun appStorageSizes(context: Context, packages: List<String>): Map<String, Long> {
    if (android.os.Build.VERSION.SDK_INT < 26) return emptyMap()
    return runCatching {
        val ssm = context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
        val user = Process.myUserHandle()
        val uuid = StorageManager.UUID_DEFAULT
        packages.mapNotNull { pkg ->
            runCatching {
                val st = ssm.queryStatsForPackage(uuid, pkg, user)
                pkg to (st.appBytes + st.dataBytes + st.cacheBytes)
            }.getOrNull()
        }.toMap()
    }.getOrDefault(emptyMap())
}

private fun grantedPackagesFor(context: Context, permissions: List<String>): Set<String> {
    val pm = context.packageManager
    return permissions.flatMap { permission ->
        runCatching { pm.getPackagesHoldingPermissions(arrayOf(permission), 0) }
            .getOrElse { emptyList() }
            .map { it.packageName }
    }.toSet()
}

private fun readPrivacyGroups(context: Context, apps: List<InstalledAppFacts>): List<AppPermissionGroup> {
    fun group(key: String, label: String, permissions: List<String>): AppPermissionGroup {
        val matches = apps.filter { app -> app.declaredPermissions.any { it in permissions } }
        val grantedSet = grantedPackagesFor(context, permissions)
        val grantedMatches = matches.filter { it.packageName in grantedSet }
        return AppPermissionGroup(
            key = key,
            label = label,
            appCount = matches.size,
            packages = matches.map { it.packageName },
            grantedCount = grantedMatches.size,
            grantedPackages = grantedMatches.map { it.packageName },
        )
    }
    return listOf(
        group("camera", "الكاميرا", listOf("android.permission.CAMERA")),
        group("microphone", "الميكروفون", listOf("android.permission.RECORD_AUDIO")),
        group(
            "location", "الموقع",
            listOf("android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"),
        ),
        group(
            "files", "الملفات",
            listOf(
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.READ_MEDIA_IMAGES",
                "android.permission.READ_MEDIA_VIDEO",
            ),
        ),
        group(
            "contacts", "جهات الاتصال",
            listOf("android.permission.READ_CONTACTS"),
        ),
        group(
            "notifications", "الإشعارات",
            listOf("android.permission.POST_NOTIFICATIONS"),
        ),
    )
}

private fun hasUsageAccess(context: Context): Boolean = runCatching {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = if (android.os.Build.VERSION.SDK_INT >= 29) {
        appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName,
        )
    } else {
        @Suppress("DEPRECATION")
        appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName,
        )
    }
    mode == AppOpsManager.MODE_ALLOWED
}.getOrDefault(false)

/**
 * Named entry points for the staged Deep Scan controller. Each one is the same
 * reader used by [readDeviceFacts]; the controller calls them one stage at a
 * time so the cover can show real progress and a cancel stops the pipeline.
 */
internal object DeviceReaders {
    fun battery(context: Context): BatteryFacts = readBattery(context)
    fun storage(): StorageFacts = readStorage()
    fun installedApps(context: Context): List<InstalledAppFacts> = readInstalledApps(context)
    fun privacyGroups(context: Context, apps: List<InstalledAppFacts>): List<AppPermissionGroup> =
        readPrivacyGroups(context, apps)
    fun ownCache(context: Context): Pair<Long, Int> = ownCacheBytes(context)
    fun media(context: Context): MediaFacts = readMedia(context)
    fun cleanFiles(context: Context): CleanFileFacts = readCleanFiles(context)
    fun usageAccess(context: Context): Boolean = hasUsageAccess(context)
}

fun readDeviceFacts(context: Context, withSizes: Boolean = true): DeviceFacts {
    val battery = readBattery(context)
    val storage = readStorage()
    val apps = readInstalledApps(context, withSizes)
    val cache = ownCacheBytes(context)
    val media = readMedia(context)
    val groups = readPrivacyGroups(context, apps)
    return DeviceFacts(
        battery = battery,
        storage = storage,
        ram = readRam(context),
        installedApps = apps,
        ownCacheBytes = cache.first,
        ownCacheFiles = cache.second,
        media = media,
        cleanFiles = readCleanFiles(context, apps.map { it.packageName }.toSet()),
        usageAccessGranted = hasUsageAccess(context),
        privacyGroups = groups,
        sensitiveAppCount = apps.count { it.sensitivePermissions.isNotEmpty() },
        vpnActive = readVpnActive(context),
        deviceAdminCount = readDeviceAdminCount(context),
        overlayAppCount = overlayDeclaredCount(apps),
        lockSecure = readLockSecure(context),
        biometricStrength = readBiometricStrength(context),
        measured = storage.isMeasured || media.readSucceeded || apps.isNotEmpty(),
        computedAt = System.currentTimeMillis(),
    )
}

/** A VPN transport is active right now — ConnectivityManager, not a guess. */
internal fun readVpnActive(context: Context): Boolean = runCatching {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    cm.allNetworks.any { n ->
        cm.getNetworkCapabilities(n)?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true
    }
}.getOrDefault(false)

/** Device-admin receivers declared by installed packages (PackageManager). */
internal fun readDeviceAdminCount(context: Context): Int = runCatching {
    val pm = context.packageManager
    val receivers = pm.queryBroadcastReceivers(Intent("android.app.action.DEVICE_ADMIN_ENABLED"), 0)
    receivers.mapNotNull { it.activityInfo?.packageName }.toSet().size
}.getOrDefault(0)

/** Screen lock set and secure — KeyguardManager. */
internal fun readLockSecure(context: Context): Boolean = runCatching {
    (context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager).isDeviceSecure
}.getOrDefault(false)

/** Biometric hardware class via androidx.biometric: 2 strong / 1 weak / 0 none / -1 unknown. */
internal fun readBiometricStrength(context: Context): Int = runCatching {
    when (androidx.biometric.BiometricManager.from(context).canAuthenticate(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
        androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS -> 2
        androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> 2
        else -> when (androidx.biometric.BiometricManager.from(context).canAuthenticate(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS -> 1
            androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> 1
            else -> 0
        }
    }
}.getOrDefault(-1)

/**
 * Process-wide snapshot of the last measured device facts.
 *
 * The 150 service screens all read the same phone. Re-scanning every package,
 * permission and MediaStore row on every navigation made each card sit on
 * zeros for several seconds - which reads as "the service is missing" on a
 * low-end device. One measured snapshot is shared; a manual refresh always
 * bypasses it, and a new snapshot is taken as soon as it is older than the
 * freshness window.
 */
object DeviceFactsCache {
    @Volatile
    var latest: DeviceFacts? = null
        private set

    @Volatile
    var loadedAt: Long = 0L
        private set

    fun publish(facts: DeviceFacts) {
        latest = facts
        loadedAt = System.currentTimeMillis()
    }

    /** Merge late per-app storage measurements into the current snapshot. */
    fun mergeSizes(sizes: Map<String, Long>) {
        val cur = latest ?: return
        if (sizes.isEmpty()) return
        publish(cur.copy(installedApps = cur.installedApps.map { a ->
            sizes[a.packageName]?.let { a.copy(appBytes = it) } ?: a
        }))
    }

    fun isFresh(now: Long = System.currentTimeMillis()): Boolean =
        latest != null && now - loadedAt < FRESH_WINDOW_MS

    private const val FRESH_WINDOW_MS = 90_000L
}

@Composable
fun rememberDeviceFacts(refreshKey: Int = 0): DeviceFacts {
    val context = LocalContext.current
    var facts by remember { mutableStateOf(DeviceFactsCache.latest ?: DeviceFacts()) }
    LaunchedEffect(refreshKey) {
        val cached = DeviceFactsCache.latest
        if (refreshKey == 0 && cached != null && DeviceFactsCache.isFresh()) {
            facts = cached
            return@LaunchedEffect
        }
        val loaded = withContext(Dispatchers.IO) { readDeviceFacts(context.applicationContext, withSizes = false) }
        DeviceFactsCache.publish(loaded)
        facts = loaded
        // Backfill real per-app sizes off the critical path (apm-size et al.).
        launch(Dispatchers.IO) {
            val sizes = appStorageSizes(context.applicationContext, loaded.installedApps.map { it.packageName })
            DeviceFactsCache.mergeSizes(sizes)
            withContext(Dispatchers.Main) { facts = DeviceFactsCache.latest ?: loaded }
        }
    }
    return facts
}

@Composable
fun rememberRefreshCounter(): Pair<Int, () -> Unit> {
    var value by remember { mutableIntStateOf(0) }
    return value to { value++ }
}

@Composable
fun rememberAppIcon(packageName: String): androidx.compose.ui.graphics.ImageBitmap? {
    val context = LocalContext.current
    val icon by produceState<androidx.compose.ui.graphics.ImageBitmap?>(null, packageName) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                context.packageManager.getApplicationIcon(packageName)
                    .toBitmap(96, 96)
                    .asImageBitmap()
            }.getOrNull()
        }
    }
    return icon
}

fun ownFilesStats(context: Context): Pair<Long, Int> = directoryStats(context.filesDir)

data class OwnFileFact(val name: String, val bytes: Long, val folder: String)

fun largestOwnCacheFiles(context: Context, limit: Int = 5): List<OwnFileFact> {
    val roots = listOfNotNull(context.cacheDir, context.codeCacheDir, context.externalCacheDir)
    val found = mutableListOf<OwnFileFact>()
    roots.distinctBy { it.absolutePath }.forEach { root ->
        if (!root.exists()) return@forEach
        val stack = ArrayDeque<File>()
        stack.add(root)
        var guard = 0
        while (stack.isNotEmpty() && guard < 4000) {
            guard++
            val current = stack.removeLast()
            val children = runCatching { current.listFiles() }.getOrNull() ?: continue
            for (child in children) {
                if (runCatching { child.isSymbolicLink() }.getOrDefault(false)) continue
                if (child.isDirectory) {
                    stack.add(child)
                } else {
                    found += OwnFileFact(
                        name = child.name,
                        bytes = runCatching { child.length() }.getOrDefault(0L),
                        folder = child.parentFile?.name ?: "cache",
                    )
                }
            }
        }
    }
    return found.sortedByDescending { it.bytes }.take(limit)
}

/* ------------------------------------------------------------------ *
 * Scan history (local, truthful)
 * ------------------------------------------------------------------ */

fun readScanHistory(context: Context): List<ScanHistoryEntry> {
    val prefs = context.getSharedPreferences(SCAN_HISTORY_PREFS, Context.MODE_PRIVATE)
    val raw = prefs.getStringSet(SCAN_HISTORY_KEY, emptySet()).orEmpty()
    return raw.mapNotNull { line ->
        val parts = line.split("|")
        if (parts.size < 6) return@mapNotNull null
        runCatching {
            ScanHistoryEntry(
                timestamp = parts[0].toLong(),
                appsScanned = parts[1].toInt(),
                privacyFindings = parts[2].toInt(),
                cacheBytes = parts[3].toLong(),
                storageUsedBytes = parts[4].toLong(),
                batteryLevel = parts[5].toIntOrNull(),
            )
        }.getOrNull()
    }.sortedByDescending { it.timestamp }
}

fun saveScanHistory(context: Context, entry: ScanHistoryEntry) {
    val prefs = context.getSharedPreferences(SCAN_HISTORY_PREFS, Context.MODE_PRIVATE)
    val existing = prefs.getStringSet(SCAN_HISTORY_KEY, emptySet()).orEmpty()
    val encoded = "${entry.timestamp}|${entry.appsScanned}|${entry.privacyFindings}|" +
        "${entry.cacheBytes}|${entry.storageUsedBytes}|${entry.batteryLevel ?: ""}"
    val next = (existing + encoded)
        .sortedByDescending { it.substringBefore('|').toLongOrNull() ?: 0L }
        .take(24)
        .toSet()
    prefs.edit().putStringSet(SCAN_HISTORY_KEY, next).apply()
}

/* ------------------------------------------------------------------ *
 * Reports & History aggregates (sec10). Real counts from the Room
 * audit journal — never fixtures. No scheduled-task subsystem exists,
 * so that strip cell stays honestly unavailable.
 * ------------------------------------------------------------------ */

data class ReportsJournal(
    val totalOperations: Int = 0,
    val reclaimedBytes: Long = 0L,
    val duplicateDeletes: Int = 0,
    val appOperations: Int = 0,
    val scanReports: Int = 0,
    val batteryReports: Int = 0,
    val privacyReports: Int = 0,
    val reclaimedMonthBytes: Long = 0L,
)

/** Latest journal snapshot for the non-composable hub identity. */
object ReportsJournalCache {
    @Volatile var latest: ReportsJournal = ReportsJournal()
        private set
    fun publish(j: ReportsJournal) { latest = j }
}

suspend fun readReportsJournal(context: Context): ReportsJournal = runCatching {
    val db = com.knoux.ai.database.AppDatabase.getDatabase(context)
    val totals = db.operationItemOutcomeDao().totals()
    val cal = java.util.Calendar.getInstance()
    cal.set(java.util.Calendar.DAY_OF_MONTH, 1)
    cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
    cal.set(java.util.Calendar.MINUTE, 0)
    cal.set(java.util.Calendar.SECOND, 0)
    cal.set(java.util.Calendar.MILLISECOND, 0)
    val monthStart = cal.timeInMillis
    val monthBytes = db.operationRequestDao().getAll().first()
        .filter { it.createdAt >= monthStart }
        .sumOf { req ->
            db.operationItemOutcomeDao().getByOperation(req.id).first()
                .filter { it.result == "SUCCESS" }
                .sumOf { it.measuredBytes ?: 0L }
        }
    ReportsJournal(
        totalOperations = db.operationRequestDao().countAll(),
        reclaimedBytes = totals.measuredBytes,
        duplicateDeletes = totals.successCount,
        appOperations = db.operationRequestDao().countByService("app_manager"),
        scanReports = db.scanSessionDao().countAll(),
        batteryReports = db.batterySampleDao().countAll(),
        privacyReports = db.permissionEventDao().countAll(),
        reclaimedMonthBytes = monthBytes,
    )
}.getOrDefault(ReportsJournal()).also { ReportsJournalCache.publish(it) }

/* ------------------------------------------------------------------ *
 * OS handoffs
 * ------------------------------------------------------------------ */

fun mediaReadPermissions(): Array<String> = if (android.os.Build.VERSION.SDK_INT >= 33) {
    arrayOf(
        "android.permission.READ_MEDIA_IMAGES",
        "android.permission.READ_MEDIA_VIDEO",
    )
} else {
    arrayOf("android.permission.READ_EXTERNAL_STORAGE")
}

fun mediaDeleteRequest(context: Context, uris: List<String>): android.content.IntentSender? {
    if (android.os.Build.VERSION.SDK_INT < 30 || uris.isEmpty()) return null
    return runCatching {
        MediaStore.createDeleteRequest(context.contentResolver, uris.map { Uri.parse(it) }).intentSender
    }.getOrNull()
}

fun openAppInfo(context: Context, packageName: String) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        .setData(Uri.parse("package:$packageName"))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { context.startActivity(intent) }
}

fun launchApp(context: Context, packageName: String) {
    val intent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { context.startActivity(intent) }
}

fun requestUninstall(context: Context, packageName: String) {
    val intent = Intent(Intent.ACTION_DELETE, Uri.parse("package:$packageName"))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { context.startActivity(intent) }
}

fun openBatterySaverSettings(context: Context) {
    runCatching {
        context.startActivity(Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }.onFailure {
        runCatching { context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
    }
}

fun openBatterySettings(context: Context) {
    runCatching {
        context.startActivity(Intent(Intent.ACTION_POWER_USAGE_SUMMARY).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }.onFailure { openBatterySaverSettings(context) }
}

fun openUsageAccessSettings(context: Context) {
    runCatching {
        context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}

fun openAllAppSettings(context: Context) {
    runCatching {
        context.startActivity(Intent(Settings.ACTION_APPLICATION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }.onFailure { openAppInfo(context, context.packageName) }
}

fun openNotificationListenerSettings(context: Context) {
    runCatching {
        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }.onFailure { openAllAppSettings(context) }
}

/** «3 س» / «42 د» — compact Arabic duration for usage rows. */
internal fun formatUsageShort(millis: Long): String {
    if (millis <= 0L) return "—"
    val hours = millis / 3_600_000L
    return when {
        hours >= 24 -> "${hours / 24} يومًا"
        hours > 0 -> "$hours س"
        else -> "${(millis / 60_000L).coerceAtLeast(1L)} د"
    }
}

fun openPrivacySettings(context: Context) {
    val intent = if (android.os.Build.VERSION.SDK_INT >= 31) {
        Intent(Settings.ACTION_PRIVACY_SETTINGS)
    } else {
        Intent(Settings.ACTION_SECURITY_SETTINGS)
    }
    runCatching {
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }.onFailure { openAllAppSettings(context) }
}

fun openVpnSettings(context: Context) {
    runCatching {
        context.startActivity(Intent(Settings.ACTION_VPN_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }.onFailure { openAllAppSettings(context) }
}

fun openSecuritySettings(context: Context) {
    runCatching {
        context.startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }.onFailure { openPrivacySettings(context) }
}

fun openDeviceAdminSettings(context: Context) {
    runCatching {
        val intent = Intent().setComponent(
            ComponentName("com.android.settings", "com.android.settings.Settings\$DeviceAdminSettingsActivity"),
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }.onFailure { openSecuritySettings(context) }
}

fun openOverlaySettings(context: Context) {
    runCatching {
        context.startActivity(
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.packageName))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
    }.onFailure { openAllAppSettings(context) }
}

fun openStorageSettings(context: Context) {
    runCatching {
        context.startActivity(Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }.onFailure {
        runCatching { context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
    }
}

fun openAppNotificationSettings(context: Context, packageName: String) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { context.startActivity(intent) }.onFailure { openAppInfo(context, packageName) }
}

fun openPermissionSettings(context: Context, packageName: String) {
    try {
        context.startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.parse("package:$packageName"))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    } catch (_: Throwable) {
        openAppInfo(context, packageName)
    }
}

fun openMedia(context: Context, uriString: String) {
    runCatching {
        context.startActivity(
            Intent(Intent.ACTION_VIEW)
                .setDataAndType(Uri.parse(uriString), "image/*")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        )
    }
}
