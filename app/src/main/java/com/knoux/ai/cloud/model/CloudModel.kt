package com.knoux.ai.cloud.model

/**
 * A connected cloud account (Gmail, Outlook, OneDrive, Drive, Photos, etc.)
 */
data class CloudAccount(
    val id: String,
    val provider: CloudProvider,
    val email: String,
    val displayName: String?,
    val isConnected: Boolean = false,
    val scopesGranted: List<String> = emptyList(),
    val tokenExpiresAt: Long = 0L,
    val connectedAt: Long = System.currentTimeMillis(),
    val lastScanAt: Long = 0L,
)

enum class CloudProvider {
    Gmail, Outlook, OneDrive, GoogleDrive, GooglePhotos, iCloudMail,
}

fun CloudProvider.displayName(): String = when (this) {
    CloudProvider.Gmail -> "Gmail"
    CloudProvider.Outlook -> "Outlook"
    CloudProvider.OneDrive -> "OneDrive"
    CloudProvider.GoogleDrive -> "Google Drive"
    CloudProvider.GooglePhotos -> "Google Photos"
    CloudProvider.iCloudMail -> "iCloud Mail"
}

/**
 * A cleanup candidate discovered during a scan.
 */
data class CleanupCandidate(
    val id: String,
    val accountId: String,
    val provider: CloudProvider,
    val category: CleanupCategory,
    val providerItemId: String,
    val itemName: String?,
    val sizeBytes: Long = 0L,
    val isSelected: Boolean = false,
    val isProtected: Boolean = false,
    val detectedAt: Long = System.currentTimeMillis(),
    val sourceAccount: String?,
    val canRestore: Boolean = true,
)

enum class CleanupCategory {
    OldEmail, LargeAttachment, Duplicate, Promotion, Social, Spam,
    Draft, Sent, LargeFile, OldFile, DuplicateDocument, DuplicateImage,
    DuplicateVideo, TrashItem, DeletedItem,
}

fun CleanupCategory.displayName(): String = when (this) {
    CleanupCategory.OldEmail -> "رسائل قديمة"
    CleanupCategory.LargeAttachment -> "مرفقات كبيرة"
    CleanupCategory.Duplicate -> "عناصر مكررة"
    CleanupCategory.Promotion -> "عروض ترويجية"
    CleanupCategory.Social -> "رسائل اجتماعية"
    CleanupCategory.Spam -> "رسائل غير مرغوب فيها"
    CleanupCategory.Draft -> "مسودات"
    CleanupCategory.Sent -> "المرسلات"
    CleanupCategory.LargeFile -> "ملفات كبيرة"
    CleanupCategory.OldFile -> "ملفات قديمة"
    CleanupCategory.DuplicateDocument -> "مستندات مكررة"
    CleanupCategory.DuplicateImage -> "صور مكررة"
    CleanupCategory.DuplicateVideo -> "فيديوهات مكررة"
    CleanupCategory.TrashItem -> "عناصر في سلة المهملات"
    CleanupCategory.DeletedItem -> "عناصر محذوفة"
}

/**
 * Result of a scan session.
 */
data class ScanResult(
    val scanId: String,
    val provider: CloudProvider?,
    val accountId: String?,
    val status: ScanStatus,
    val itemsFound: Int = 0,
    val bytesFound: Long = 0L,
    val bytesReclaimable: Long = 0L,
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val error: String? = null,
)

enum class ScanStatus {
    Scanning, Completed, Failed, Cancelled,
}

fun ScanStatus.displayName(): String = when (this) {
    ScanStatus.Scanning -> "جاري الفحص…"
    ScanStatus.Completed -> "مكتمل"
    ScanStatus.Failed -> "فشل"
    ScanStatus.Cancelled -> "ملغي"
}

/**
 * Result of a cleanup operation.
 */
data class CleanupOperation(
    val id: String,
    val operationType: String,
    val itemsSelected: Int = 0,
    val itemsExecuted: Int = 0,
    val bytesReclaimed: Long = 0L,
    val status: OperationStatus = OperationStatus.Pending,
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val error: String? = null,
)

enum class OperationStatus {
    Pending, Executing, Completed, Failed, Cancelled,
}

fun OperationStatus.displayName(): String = when (this) {
    OperationStatus.Pending -> "معلق"
    OperationStatus.Executing -> "جارٍ التنفيذ…"
    OperationStatus.Completed -> "مكتمل"
    OperationStatus.Failed -> "فشل"
    OperationStatus.Cancelled -> "ملغي"
}

/**
 * Unified storage analytics across all providers.
 */
data class StorageAnalytics(
    val provider: CloudProvider,
    val accountEmail: String,
    val totalBytes: Long = 0L,
    val usedBytes: Long = 0L,
    val freeBytes: Long = 0L,
    val emailBytes: Long = 0L,
    val driveBytes: Long = 0L,
    val photosBytes: Long = 0L,
    val lastUpdated: Long = System.currentTimeMillis(),
)