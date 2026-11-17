package com.knoux.ai.ui.v2

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.BatteryManager
import android.os.Environment
import android.os.Process
import android.os.StatFs
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
    val usedBytes: Long get() = (totalBytes - freeBytes).coerceAtLeast(0L)
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
)

data class MediaFileFacts(
    val name: String,
    val sizeBytes: Long,
    val bucket: String?,
    val isVideo: Boolean,
    val uriString: String,
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
    val images: Int = 0,
    val videos: Int = 0,
    val imagesBytes: Long = 0L,
    val videosBytes: Long = 0L,
    val duplicateGroups: List<MediaDuplicateGroup> = emptyList(),
    val duplicateCandidates: Int = 0,
    val largestFiles: List<MediaFileFacts> = emptyList(),
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
    val installedApps: List<InstalledAppFacts> = emptyList(),
    val ownCacheBytes: Long = 0L,
    val ownCacheFiles: Int = 0,
    val media: MediaFacts = MediaFacts(),
    val usageAccessGranted: Boolean = false,
    val privacyGroups: List<AppPermissionGroup> = emptyList(),
    val sensitiveAppCount: Int = 0,
    val computedAt: Long = 0L,
) {
    val installedCount: Int get() = installedApps.size
    val userAppCount: Int get() = installedApps.count { !it.isSystem }
    val healthEstimate: Int
        get() {
            if (computedAt == 0L) return 0
            var score = 70
            val level = battery.levelPercent
            if (level != null) score += when {
                level >= 80 -> 12
                level >= 40 -> 8
                level >= 20 -> 2
                else -> -10
            }
            score += when {
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
        .groupBy { it.sizeBytes to it.isVideo }
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
    val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} IN (?,?)"
    val args = arrayOf(
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString(),
    )
    val items = mutableListOf<MediaFileFacts>()
    runCatching {
        context.contentResolver.query(
            collection, projection, selection, args,
            "${MediaStore.Files.FileColumns.SIZE} DESC",
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val typeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
            val bucketCol = cursor.getColumnIndexOrThrow("bucket_display_name")
            var guard = 0
            while (cursor.moveToNext() && guard < 6000) {
                guard++
                val id = cursor.getLong(idCol)
                val size = cursor.getLong(sizeCol)
                val mediaType = cursor.getInt(typeCol)
                val isVideo = mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
                items += MediaFileFacts(
                    name = cursor.getString(nameCol) ?: "ملف",
                    sizeBytes = size,
                    bucket = cursor.getString(bucketCol),
                    isVideo = isVideo,
                    uriString = Uri.withAppendedPath(collection, id.toString()).toString(),
                )
            }
        }
    }
    val images = items.filter { !it.isVideo }
    val videos = items.filter { it.isVideo }
    val grouped = findExactMediaDuplicates(items) { item ->
        context.contentResolver.openInputStream(Uri.parse(item.uriString))
    }
    return MediaFacts(
        permissionGranted = true,
        images = images.size,
        videos = videos.size,
        imagesBytes = images.sumOf { it.sizeBytes },
        videosBytes = videos.sumOf { it.sizeBytes },
        duplicateGroups = grouped,
        duplicateCandidates = grouped.sumOf { it.count },
        largestFiles = items.take(8),
    )
}

private fun readInstalledApps(context: Context): List<InstalledAppFacts> {
    val pm = context.packageManager
    val infos = runCatching {
        pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
    }.getOrElse { emptyList() }
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
        )
    }.sortedWith(compareByDescending<InstalledAppFacts> { !it.isSystem }.thenBy { it.label })
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

fun readDeviceFacts(context: Context): DeviceFacts {
    val battery = readBattery(context)
    val storage = readStorage()
    val apps = readInstalledApps(context)
    val cache = ownCacheBytes(context)
    val media = readMedia(context)
    val groups = readPrivacyGroups(context, apps)
    return DeviceFacts(
        battery = battery,
        storage = storage,
        installedApps = apps,
        ownCacheBytes = cache.first,
        ownCacheFiles = cache.second,
        media = media,
        usageAccessGranted = hasUsageAccess(context),
        privacyGroups = groups,
        sensitiveAppCount = apps.count { it.sensitivePermissions.isNotEmpty() },
        computedAt = System.currentTimeMillis(),
    )
}

/**
 * Process-wide snapshot of the last measured device facts.
 *
 * The 100 service screens all read the same phone. Re-scanning every package,
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
        val loaded = withContext(Dispatchers.IO) { readDeviceFacts(context.applicationContext) }
        DeviceFactsCache.publish(loaded)
        facts = loaded
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
