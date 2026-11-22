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

    @Test
    fun ramFactsDeriveUsedShareFromRealTotals() {
        val ram = RamFacts(totalBytes = 8L * 1024 * 1024 * 1024, availBytes = 2L * 1024 * 1024 * 1024)
        assertEquals(6L * 1024 * 1024 * 1024, ram.usedBytes)
        assertEquals(.75f, ram.usedFraction, 0.0001f)
    }

    @Test
    fun ramFactsStayZeroWhenPlatformHidesMemory() {
        val ram = RamFacts()
        assertEquals(0L, ram.usedBytes)
        assertEquals(0f, ram.usedFraction, 0.0001f)
    }

    @Test
    fun downloadsAggregateOnlyTheDownloadBucket() {
        val rows = listOf(
            "Download" to 5L,
            "downloads" to 7L,
            "Camera" to 100L,
            null to 9L,
        )
        val (bytes, count) = aggregateDownloads(rows)
        assertEquals(12L, bytes)
        assertEquals(2, count)
    }

    @Test
    fun apksMatchExtensionOrInstallerMime() {
        val rows = listOf(
            Triple("game.APK", null, 10L),
            Triple("notes.txt", "application/vnd.android.package-archive", 4L),
            Triple("photo.jpg", "image/jpeg", 99L),
        )
        val (bytes, count) = aggregateApks(rows)
        assertEquals(14L, bytes)
        assertEquals(2, count)
    }

    @Test
    fun emptyFolderWalkCountsOnlyTrulyEmptyDirs() {
        val root = temp.newFolder("storage")
        temp.newFolder("storage", "emptyA")
        temp.newFolder("storage", "emptyB")
        val filled = temp.newFolder("storage", "filled")
        java.io.File(filled, "file.bin").writeBytes(byteArrayOf(1))
        assertEquals(2, countEmptyFolders(root))
    }

    @Test
    fun emptyFolderWalkStopsAtTheDirectoryBudget() {
        val root = temp.newFolder("wide")
        repeat(12) { temp.newFolder("wide", "d$it") }
        // Budget 1 visits only the root (non-empty) → zero empties counted.
        assertEquals(0, countEmptyFolders(root, maxDirs = 1))
    }

    @Test
    fun cleanFileFactsDefaultToZeroWhenUnreadable() {
        val facts = CleanFileFacts()
        assertEquals(0L, facts.downloadsBytes)
        assertEquals(0, facts.apkCount)
        assertEquals(0, facts.emptyFolders)
        assertEquals(0, facts.hiddenFiles)
        assertEquals(0L, facts.scannedBytes)
    }

    @Test
    fun hiddenFilesAreDotPrefixedNamesOnly() {
        val rows = listOf(
            Triple(".nomedia", null, 0L),
            Triple(".trashed-123.jpg", "image/jpeg", 5L),
            Triple("photo.jpg", "image/jpeg", 9L),
            Triple(null, null, 3L),
            Triple("folder.hidden", null, 2L),
        )
        assertEquals(2, countHiddenFiles(rows))
    }

    @Test
    fun residueAggregatesOnlyHonestBucketSignals() {
        val now = 1_700_000_000L
        val old = now - 40L * 24 * 3600
        val fresh = now - 2L * 24 * 3600
        val rows = listOf(
            ResidueRow("thumb.db", "image/jpeg", "MyCache", 10L, old),      // old cache
            ResidueRow("page.bin", null, "WebView", 20L, fresh),            // browser, fresh cache bucket
            ResidueRow("movie.mkv.part", "video/mp4", "Download", 30L, fresh), // broken download, not residue
            ResidueRow("main.obb", null, "Documents", 40L, fresh),          // obb residue
            ResidueRow("trace.log", null, "Documents", 50L, fresh),         // log residue
            ResidueRow("photo.jpg", "image/jpeg", "Camera", 60L, fresh),    // clean, ignored
        )
        val agg = aggregateResidue(rows, now)
        assertEquals(4, agg.residueFiles)
        assertEquals(120L, agg.residueBytes)
        assertEquals(1, agg.oldCacheFiles)
        assertEquals(10L, agg.oldCacheBytes)
        // WebView is browser residue, not an app-cache bucket: fresh cache stays out.
        assertEquals(1, agg.brokenDownloads)
        assertEquals(1, agg.obbFiles)
        assertEquals(40L, agg.obbBytes)
        assertEquals(1, agg.browserFiles)
        assertEquals(20L, agg.browserBytes)
        assertEquals(1, agg.logFiles)
    }

    @Test
    fun freshCacheIsNotOldCache() {
        val now = 1_000_000L
        val rows = listOf(ResidueRow("a.bin", null, "cache", 5L, now - 3600))
        val agg = aggregateResidue(rows, now)
        assertEquals(1, agg.residueFiles)
        assertEquals(0, agg.oldCacheFiles)
    }

    @Test
    fun orphanFoldersCountOnlyUninstalledPackageNames() {
        val root = temp.newFolder("sd")
        temp.newFolder("sd", "com.removed.app")
        temp.newFolder("sd", "com.still.installed")
        temp.newFolder("sd", "Camera")
        temp.newFolder("sd", ".hidden")
        temp.newFolder("sd", "Android")
        temp.newFolder("sd", "Android", "com.removed.app")
        assertEquals(1, countOrphanAppFolders(root, setOf("com.still.installed")))
    }

    @Test
    fun orphanFolderWalkStopsAtTheDirectoryBudget() {
        val root = temp.newFolder("wide2")
        repeat(12) { temp.newFolder("wide2", "com.gone.app$it") }
        assertEquals(0, countOrphanAppFolders(root, emptySet(), maxDirs = 1))
    }

    @Test
    fun audioDuplicatesGroupSeparatelyFromImages() {
        val files = listOf(
            MediaFileFacts("songA", 3L, "Music", false, "a", isAudio = true),
            MediaFileFacts("songB", 3L, "Music", false, "b", isAudio = true),
            MediaFileFacts("img", 3L, "Camera", false, "c"),
        )
        val bytes = mapOf("a" to "abc", "b" to "abc", "c" to "abc")
        val groups = findExactMediaDuplicates(files) { bytes[it.uriString]?.byteInputStream() }
        assertEquals(1, groups.size)
        assertEquals(listOf("a", "b"), groups.single().uris)
    }

    @Test
    fun largestBucketSumsRowsPerNameAndIgnoresBlanks() {
        val rows = listOf(
            "Camera" to 500L,
            "Download" to 900L,
            "Camera" to 700L,
            null to 5_000L,
            " " to 4_000L,
        )
        assertEquals("Camera" to 1_200L, largestBucketOf(rows))
        assertEquals(null, largestBucketOf(emptyList()))
        assertEquals(null, largestBucketOf(listOf(null to 10L)))
    }

    @Test
    fun mediaBucketBytesOnlyCountsCameraStyleBuckets() {
        val rows = listOf(
            "DCIM/Camera" to 100L,
            "Screenshots" to 50L,
            "Pictures/WhatsApp" to 30L,
            "Download" to 999L,
            "Documents" to 777L,
            null to 555L,
        )
        assertEquals(180L, mediaBucketBytesOf(rows))
        assertEquals(0L, mediaBucketBytesOf(emptyList()))
    }

    @Test
    fun storageGrowthNeedsTwoScansInsideTheWindow() {
        val now = 1_700_000_000_000L
        val day = 24L * 3600 * 1000
        fun entry(ts: Long, used: Long) = ScanHistoryEntry(ts, 0, 0, 0L, used, null)
        // Fewer than two valid readings -> null, never guessed.
        assertEquals(null, storageGrowthBytes(emptyList(), now))
        assertEquals(null, storageGrowthBytes(listOf(entry(now, 100L)), now))
        assertEquals(null, storageGrowthBytes(listOf(entry(now, 100L), entry(now - day, 0L)), now))
        // Two readings -> honest delta, newest minus oldest.
        val two = listOf(entry(now - 2 * day, 1_000L), entry(now - day, 1_600L))
        assertEquals(600L, storageGrowthBytes(two, now))
        // Negative growth is real (user deleted files) and must survive.
        val shrink = listOf(entry(now - 2 * day, 2_000L), entry(now - day, 1_500L))
        assertEquals(-500L, storageGrowthBytes(shrink, now))
        // Scans older than the window are excluded.
        val stale = listOf(entry(now - 40 * day, 100L), entry(now - 35 * day, 900L))
        assertEquals(null, storageGrowthBytes(stale, now))
    }

    /* ---------------- sec6 app-manager aggregates ---------------- */

    private fun app(
        name: String,
        bytes: Long? = null,
        usage: Long? = null,
        sensitive: Int = 0,
    ) = InstalledAppFacts(
        label = name,
        packageName = "com.example.$name",
        versionName = "1.0",
        isSystem = false,
        isEnabled = true,
        declaredPermissions = emptyList(),
        sensitivePermissions = List(sensitive) { "android.permission.CAMERA" },
        icon = null,
        appBytes = bytes,
        usage7dMs = usage,
    )

    @Test
    fun lowUsageCountsOnlyRecordedAppsUnderTheThreshold() {
        val apps = listOf(
            app("idle", usage = 5L * 60 * 1000),      // 5 min -> low
            app("border", usage = 30L * 60 * 1000),    // exactly 30 min -> not low
            app("heavy", usage = 3L * 3600 * 1000),    // 3 h -> not low
            app("unknown", usage = null),              // no record -> never guessed
        )
        assertEquals(1, lowUsageAppCount(apps))
        assertEquals(0, lowUsageAppCount(emptyList()))
        // A zero-minute record is real data and counts as low usage.
        assertEquals(1, lowUsageAppCount(listOf(app("zero", usage = 0L))))
    }

    @Test
    fun largeAppCountUsesMeasuredBytesOnly() {
        val mb = 1024L * 1024
        val apps = listOf(
            app("big", bytes = 150 * mb),
            app("edge", bytes = 100 * mb),   // inclusive boundary
            app("small", bytes = 99 * mb),
            app("unmeasured", bytes = null), // absent measurement is not a zero
        )
        assertEquals(2, largeAppCount(apps))
        assertEquals(0, largeAppCount(emptyList()))
        assertEquals(1, largeAppCount(apps, minBytes = 150 * mb))
    }

    @Test
    fun sizeAndUsageRankingsSkipUnmeasuredApps() {
        val apps = listOf(
            app("mid", bytes = 200L, usage = 20L),
            app("top", bytes = 900L, usage = 90L),
            app("ghost", bytes = null, usage = null),
        )
        assertEquals(listOf("top", "mid"), appsBySizeDesc(apps).map { it.label })
        assertEquals(listOf("top", "mid"), appsByUsageDesc(apps).map { it.label })
        assertEquals(0, appsBySizeDesc(listOf(app("ghost"))).size)
    }

    @Test
    fun permissionReviewCountsAppsNotPermissions() {
        val apps = listOf(app("a", sensitive = 3), app("b", sensitive = 1), app("c"))
        assertEquals(2, permissionReviewAppCount(apps))
    }

    @Test
    fun unavailableStatsStayNullInsteadOfInvented() {
        assertEquals(null, batteryDrainAppCount(listOf(app("a"))))
        assertEquals(null, notificationHeavyAppCount(null))
        assertEquals(27, notificationHeavyAppCount(27))
    }

    @Test
    fun usageShortFormatsHonestDurations() {
        assertEquals("—", formatUsageShort(0L))
        assertEquals("1 د", formatUsageShort(30_000L))
        assertEquals("42 د", formatUsageShort(42L * 60_000))
        assertEquals("3 س", formatUsageShort(3L * 3_600_000))
        assertEquals("2 يومًا", formatUsageShort(50L * 3_600_000))
    }

    /* ---------------- async size backfill + reports journal ---------------- */

    @Test
    fun mergeSizesFillsMeasuredAppsAndLeavesUnmeasuredNull() {
        val before = DeviceFacts(installedApps = listOf(app("a"), app("b")), computedAt = 1L)
        DeviceFactsCache.publish(before)
        DeviceFactsCache.mergeSizes(mapOf("com.example.a" to 5_000L))
        val merged = DeviceFactsCache.latest!!
        assertEquals(5_000L, merged.installedApps[0].appBytes)
        assertEquals(null, merged.installedApps[1].appBytes) // never invented
        assertEquals(1L, merged.computedAt) // snapshot identity preserved
    }

    @Test
    fun mergeSizesIgnoresEmptyMapsAndMissingSnapshots() {
        val before = DeviceFacts(installedApps = listOf(app("a", bytes = 7L)), computedAt = 2L)
        DeviceFactsCache.publish(before)
        DeviceFactsCache.mergeSizes(emptyMap())
        assertEquals(7L, DeviceFactsCache.latest!!.installedApps[0].appBytes)
    }

    @Test
    fun reportsJournalDefaultsToHonestZeros() {
        val j = ReportsJournal()
        assertEquals(0, j.totalOperations)
        assertEquals(0L, j.reclaimedBytes)
        assertEquals(0L, j.reclaimedMonthBytes)
        assertEquals(0, j.scanReports)
        assertEquals(0, j.batteryReports)
        assertEquals(0, j.privacyReports)
        assertEquals(0, j.duplicateDeletes)
        assertEquals(0, j.appOperations)
    }
}
