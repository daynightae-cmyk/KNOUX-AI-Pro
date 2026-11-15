package com.knoux.ai.ui.data

import androidx.compose.ui.graphics.Color
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProArcticCyan
import com.knoux.ai.ui.theme.ProBlue

/**
 * Phase 1 UI-only fixture layer.
 *
 * Every number below is a *design reference* value copied from the approved
 * mockups. It is NOT device telemetry and must never be presented as a real
 * scan result (see docs/testing/FAKE_CLOSURE_PRODUCTION_GATE.md).
 */
enum class UiDataProvenance { DESIGN_PREVIEW, DEVICE_OBSERVED, COMPUTED, ESTIMATED, UNAVAILABLE }

data class PreviewRow(
    val title: String,
    val subtitle: String,
    val badge: String,
    val icon: Key,
)

enum class Key { Image, Video, Document, Audio, Share, Folder, Storage, Chip, Shield, Bolt, Clock }

data class MiniStat(val icon: Key, val value: String, val label: String)

data class HomeService(
    val title: String,
    val description: String,
    val metric: String,
    val metricCaption: String,
    val action: String?,
    val icon: Key,
    val accent: Color,
)

data class ProgressModule(val name: String, val description: String, val progress: Int)

object Fixtures {

    const val LAST_RUN = "Today, 10:21 AM"

    val homeServices = listOf(
        HomeService("Clean", "Remove junk & temporary files", "2.4 GB", "removable", "Clean", Key.Folder, ProArcticCyan),
        HomeService("Duplicate Files", "Find and remove duplicate items", "1,248", "duplicates", "Review", Key.Image, ProArcticCyan),
        HomeService("Storage", "Analyze usage & free up space", "128 GB / 256 GB", "50% used", null, Key.Storage, ProBlue),
        HomeService("Privacy", "Detect trackers & protect your data", "7", "trackers found", "Review", Key.Shield, ProArcticCyan),
        HomeService("Battery", "Reduce drain & extend battery life", "6h 42m", "estimated gain · preview", null, Key.Bolt, ProArcticCyan),
        HomeService("AI Optimization", "Boost performance & keep your device fast", "Score 94", "design preview", "Optimize", Key.Chip, ProArcticCyan),
    )

    val benefits = listOf("Clean", "Optimize", "Protect", "Manage")

    // 02 Privacy Sweep
    val privacyIndicators = listOf(
        MiniStat(Key.Shield, "2 apps", "Camera Access"),
        MiniStat(Key.Audio, "1 app", "Microphone Access"),
        MiniStat(Key.Bolt, "7 trackers", "Background Trackers"),
        MiniStat(Key.Document, "2 apps", "Clipboard Watchers"),
    )
    val privacyItems = listOf(
        PreviewRow("Ad SDK Trackers", "Third-party ad SDKs that track your activity across apps.", "7 found", Key.Bolt),
        PreviewRow("Background Location", "Apps that can access your location in the background.", "1 app", Key.Shield),
        PreviewRow("Clipboard Access", "Apps that can read your clipboard content (e.g., links, text, codes).", "2 apps", Key.Document),
        PreviewRow("Overlay Permission", "Apps that can appear over other apps and monitor your activity.", "1 app", Key.Folder),
        PreviewRow("Notification Listeners", "Apps that can read and process your notifications.", "2 apps", Key.Bolt),
    )

    // 03 Duplicate files
    val duplicateStats = listOf(
        MiniStat(Key.Image, "612", "Similar Photos"),
        MiniStat(Key.Video, "238", "Repeated Videos"),
        MiniStat(Key.Document, "316", "Copy Documents"),
        MiniStat(Key.Audio, "82", "Duplicate Audio"),
    )
    val duplicateGroups = listOf(
        PreviewRow("Similar Selfies", "Duplicates and near-identical selfies.", "189 items  ·  1.6 GB", Key.Shield),
        PreviewRow("Burst Photos", "Multiple shots of the same moment.", "423 items  ·  3.1 GB", Key.Image),
        PreviewRow("Duplicate Videos", "Repeated videos and saved copies.", "238 items  ·  2.4 GB", Key.Video),
        PreviewRow("Repeated Documents", "Duplicate files (PDF, DOC, XLS, etc.).", "316 items  ·  1.1 GB", Key.Document),
        PreviewRow("Shared Media Copies", "Duplicates from chats and shared apps.", "82 items  ·  521 MB", Key.Share),
    )

    // 04 Deep scan
    val deepScanMetrics = listOf(
        MiniStat(Key.Storage, "2.4 GB", "Temporary Files"),
        MiniStat(Key.Image, "1,248", "Duplicate Media"),
        MiniStat(Key.Shield, "7", "Privacy Alerts"),
        MiniStat(Key.Chip, "95%", "App Health"),
    )
    val scanModules = listOf(
        ProgressModule("Scan Storage", "Scanning junk and residual files...", 84),
        ProgressModule("Review Media", "Checking for duplicate photos, videos...", 67),
        ProgressModule("Inspect Privacy", "Scanning trackers, permissions, and risks...", 73),
        ProgressModule("Analyze Apps", "Evaluating app health and resource usage...", 91),
        ProgressModule("Battery Diagnosis", "Analyzing battery health and usage patterns...", 58),
    )

    // 05 Reports
    val reportMetrics = listOf(
        MiniStat(Key.Storage, "4.8 GB", "Weekly Savings"),
        MiniStat(Key.Shield, "7", "Privacy Fixes"),
        MiniStat(Key.Bolt, "+18%", "Battery Gains"),
        MiniStat(Key.Image, "1,248", "Duplicate Reduction"),
    )
    val reportRows = listOf(
        PreviewRow("Cleanup History", "Past cleanups, space saved, and items removed.", "31 sessions", Key.Bolt),
        PreviewRow("Storage Trend", "Track storage usage and savings over time.", "12.6 GB total saved", Key.Storage),
        PreviewRow("Privacy Improvements", "Removed trackers, cleared data, and reduced privacy risks.", "7 fixes applied", Key.Shield),
        PreviewRow("Battery Sessions", "Optimization sessions and battery performance gains.", "+18% battery gain", Key.Bolt),
        PreviewRow("Smart Recommendations", "Personalized actions to free space, boost performance, and stay secure.", "3 new", Key.Chip),
    )

    // 06 App residue
    val residueMetrics = listOf(
        MiniStat(Key.Folder, "8", "Uninstalled Apps"),
        MiniStat(Key.Document, "42", "Empty Directories"),
        MiniStat(Key.Storage, "516 MB", "Old Cache"),
        MiniStat(Key.Bolt, "17", "Broken Shortcuts"),
    )
    val residueItems = listOf(
        PreviewRow("WhatsApp Leftovers", "Residual folders, logs, and unused data from removed or reset WhatsApp.", "268 MB", Key.Bolt),
        PreviewRow("Old Game Cache", "Obsolete cache and data from uninstalled games.", "184 MB", Key.Chip),
        PreviewRow("Empty Media Folders", "Media directories with no files (photos, videos, audio).", "96 MB", Key.Image),
        PreviewRow("Broken Temp Data", "Incomplete or invalid temporary data and abandoned sessions.", "172 MB", Key.Document),
        PreviewRow("Archived Installer Remnants", "Leftover files from old or partially removed app installers (APK/OBB).", "116 MB", Key.Storage),
    )

    // 07 App manager
    val appManagerMetrics = listOf(
        MiniStat(Key.Clock, "27", "Rarely Used"),
        MiniStat(Key.Storage, "16", "Large Apps (≥ 500 MB)"),
        MiniStat(Key.Bolt, "9", "Startup Apps"),
        MiniStat(Key.Share, "12", "Updates Available"),
    )
    val appCategories = listOf(
        PreviewRow("Streaming Apps", "Video, music, and live streaming apps.", "5.86 GB  ·  Today", Key.Video),
        PreviewRow("Social Apps", "Social networking and community apps.", "3.12 GB  ·  Today", Key.Share),
        PreviewRow("Game Collection", "Installed games and game resources.", "6.27 GB  ·  Yesterday", Key.Chip),
        PreviewRow("Utility Tools", "Productivity, file, and system tools.", "2.43 GB  ·  Today", Key.Folder),
        PreviewRow("Messaging Apps", "Chat, calls, and communication apps.", "744 MB  ·  Today", Key.Bolt),
    )

    // 08 Storage analyzer
    val storageBreakdown = listOf(
        PreviewRow("Apps & Games", "Installed apps, games, and app data.", "48.6 GB", Key.Chip),
        PreviewRow("Photos & Videos", "Photos, videos, and screen recordings.", "36.8 GB", Key.Image),
        PreviewRow("Downloads", "Downloaded files, installers, and updates.", "6.2 GB", Key.Folder),
        PreviewRow("Documents", "PDFs, Office files, and other documents.", "8.4 GB", Key.Document),
        PreviewRow("WhatsApp Media", "Photos, videos, audio, and shared files.", "4.1 GB", Key.Bolt),
        PreviewRow("System Files", "Android system, cache, and essential files.", "22.2 GB", Key.Storage),
    )
    val storageTopStats = listOf(
        MiniStat(Key.Chip, "48.6 GB", "Apps"),
        MiniStat(Key.Image, "36.8 GB", "Media"),
        MiniStat(Key.Document, "8.4 GB", "Documents"),
        MiniStat(Key.Storage, "22.2 GB", "System"),
        MiniStat(Key.Folder, "128 GB", "Free Space"),
    )

    // 09 Battery saver
    val batteryMetrics = listOf(
        MiniStat(Key.Bolt, "5", "Drain Sources"),
        MiniStat(Key.Chip, "18", "Background Apps"),
        MiniStat(Key.Shield, "−22%", "Brightness Impact"),
        MiniStat(Key.Clock, "287", "Charging Cycles"),
    )
    val batteryActions = listOf(
        PreviewRow("Screen Brightness", "High brightness increases battery drain.", "−22% impact", Key.Shield),
        PreviewRow("Background Sync", "Limit non-essential sync to save power.", "−14% impact", Key.Share),
        PreviewRow("App Wakeups", "Stop frequent background wakeups.", "−18% impact", Key.Chip),
        PreviewRow("GPS Usage", "High-accuracy GPS can drain battery.", "−11% impact", Key.Shield),
        PreviewRow("Charging Health", "Optimize charging to prolong battery life.", "+7% health", Key.Bolt),
    )

    // 10 AI optimization
    val aiMetrics = listOf(
        MiniStat(Key.Chip, "24%", "CPU Load"),
        MiniStat(Key.Storage, "1.6 GB", "Memory Freed"),
        MiniStat(Key.Folder, "12 apps", "App Hibernation"),
        MiniStat(Key.Bolt, "Normal", "Thermal Status"),
    )
    val aiTools = listOf(
        PreviewRow("Memory Boost", "Free up RAM and improve app responsiveness.", "+1.6 GB freed", Key.Storage),
        PreviewRow("Battery Balancing", "Intelligently manage power consumption.", "+18% battery life", Key.Bolt),
        PreviewRow("Smart Hibernation", "Put inactive apps to sleep and reduce background use.", "12 apps hibernated", Key.Folder),
        PreviewRow("Startup Control", "Manage auto-start apps to speed up boot time.", "−4.2 s boot time", Key.Bolt),
        PreviewRow("Thermal Guard", "Keep device cool and prevent performance throttling.", "−6°C peak temp", Key.Shield),
    )
}
