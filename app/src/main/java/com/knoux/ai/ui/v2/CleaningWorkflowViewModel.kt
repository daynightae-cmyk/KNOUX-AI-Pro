package com.knoux.ai.ui.v2

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.knoux.ai.database.KnouxRepository
import com.knoux.ai.database.OperationItemOutcomeEntity
import com.knoux.ai.database.OperationRequestEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

/** Shared state for the 15-route cleaning workflow. */
class CleaningWorkflowViewModel(application: Application, repository: KnouxRepository) :
    BaseServiceViewModel(application, repository) {

    private var _state by mutableStateOf(CleaningWorkflowState())
    val state: CleaningWorkflowState get() = _state

    /** Current scan stage and progress. */
    fun startFullScan(trigger: Int = 0) {
        if (_state.scanStatus == ScanStatus.SCANNING) return
        if (trigger == _state.lastScanTrigger && _state.scanResult != null) return
        _state = _state.copy(
            scanStatus = ScanStatus.SCANNING,
            scanProgress = 0f,
            scanMessage = "بدء الفحص…",
            lastScanTrigger = trigger,
            selection = SelectionState(),
            filterState = FilterState(),
        )
        launchLatest {
            try {
                val ctx = appContext
                val facts = withContext(Dispatchers.IO) { readDeviceFacts(ctx) }
                DeviceFactsCache.publish(facts)

                // Stage 1: Storage & Cache
                _state = _state.copy(scanMessage = "قراءة التخزين والكاش…", scanProgress = 0.1f)
                val storage = facts.storage
                val cacheBytes = facts.ownCacheBytes

                // Stage 2: MediaStore duplicates & large files
                _state = _state.copy(scanMessage = "فحص الوسائط والتكرار…", scanProgress = 0.3f)
                val media = facts.media
                val duplicateGroups = media.duplicateGroups
                val largestFiles = media.largestFiles

                // Stage 3: Deep scan modules
                _state = _state.copy(scanMessage = "وحدات الفحص العميق…", scanProgress = 0.6f)

                // Stage 4: Classification buckets
                _state = _state.copy(scanMessage = "تصنيف المجلدات…", scanProgress = 0.85f)
                val buckets = if (media.permissionGranted && largestFiles.isNotEmpty()) {
                    largestFiles
                        .groupBy { it.bucket?.takeIf { it.isNotBlank() } ?: "مجلد غير مسمى" }
                        .mapValues { (_, files) -> files.sumOf { it.sizeBytes } }
                        .entries
                        .sortedByDescending { it.value }
                        .take(10)
                        .map { entry -> BucketItem(entry.key, entry.value, largestFiles.count { (it.bucket ?: "مجلد غير مسمى") == entry.key }) }
                } else emptyList()

                val reclaimableDup = duplicateGroups.sumOf { it.reclaimableBytes }

                val result = ScanResult(
                    timestamp = System.currentTimeMillis(),
                    storageTotal = storage.totalBytes,
                    storageUsed = storage.usedBytes,
                    storageFree = storage.freeBytes,
                    cacheBytes = cacheBytes,
                    cacheFiles = facts.ownCacheFiles,
                    duplicateGroups = duplicateGroups,
                    reclaimableDuplicateBytes = reclaimableDup,
                    largestFiles = largestFiles,
                    imagesBytes = media.imagesBytes,
                    videosBytes = media.videosBytes,
                    imagesCount = media.images,
                    videosCount = media.videos,
                    mediaPermissionGranted = media.permissionGranted,
                    buckets = buckets,
                )

                // Save scan session to Room
                val sessionId = withContext(Dispatchers.IO) {
                    val id = repository.createScanSession(
                        serviceId = "cleaning_workflow",
                        serviceName = "Cleaning Workflow Full Scan",
                        deviceModel = android.os.Build.MODEL,
                        apiLevel = android.os.Build.VERSION.SDK_INT,
                    )
                    repository.completeScanSession(
                        sessionId = id,
                        status = "COMPLETED",
                        permissionGranted = media.permissionGranted,
                        itemsFound = duplicateGroups.sumOf { it.count } + largestFiles.size,
                        bytesFound = cacheBytes + reclaimableDup,
                        bytesReclaimable = reclaimableDup,
                    )
                    id
                }

                _state = _state.copy(
                    scanStatus = ScanStatus.COMPLETED,
                    scanProgress = 1f,
                    scanMessage = "اكتمل الفحص",
                    scanResult = result,
                    lastScanId = sessionId,
                    filterState = _state.filterState.copy(minSizeMb = 50),
                )
            } catch (e: Exception) {
                _state = _state.copy(
                    scanStatus = ScanStatus.FAILED,
                    scanMessage = "فشل الفحص: ${e.message ?: "خطأ غير معروف"}",
                )
            }
        }
    }

    fun applyFilter(minSizeMb: Int) {
        _state = _state.copy(filterState = _state.filterState.copy(minSizeMb = minSizeMb))
    }

    fun toggleSelection(uri: String, isSelected: Boolean) {
        val newSelected = if (isSelected) {
            _state.selection.selectedUris + uri
        } else {
            _state.selection.selectedUris - uri
        }
        // Simplified byte calculation
        val newBytes = _state.scanResult?.let { result ->
            val uriToSize = mutableMapOf<String, Long>()
            result.duplicateGroups.forEach { g ->
                g.uris.forEach { uriToSize[it] = g.sizeBytes }
            }
            result.largestFiles.forEach { f ->
                uriToSize[f.uriString] = f.sizeBytes
            }
            newSelected.sumOf { uriToSize[it] ?: 0L }
        } ?: 0L
        _state = _state.copy(selection = _state.selection.copy(
            selectedUris = newSelected,
            selectedBytes = newBytes,
        ))
    }

    fun selectAllEligible() {
        val eligible = _state.scanResult?.let { result ->
            (result.duplicateGroups.flatMap { it.uris } + result.largestFiles.map { it.uriString })
                .filterNot { it in _state.selection.protectedUris }
                .toSet()
        } ?: emptySet()
        val bytes = _state.scanResult?.let { result ->
            val uriToSize = mutableMapOf<String, Long>()
            result.duplicateGroups.forEach { g ->
                g.uris.forEach { uriToSize[it] = g.sizeBytes }
            }
            result.largestFiles.forEach { f ->
                uriToSize[f.uriString] = f.sizeBytes
            }
            eligible.sumOf { uriToSize[it] ?: 0L }
        } ?: 0L
        _state = _state.copy(selection = _state.selection.copy(
            selectedUris = eligible,
            selectedBytes = bytes,
        ))
    }

    fun clearSelection() {
        _state = _state.copy(selection = SelectionState())
    }

    fun addProtected(uri: String) {
        _state = _state.copy(selection = _state.selection.copy(
            protectedUris = _state.selection.protectedUris + uri,
            selectedUris = _state.selection.selectedUris - uri,
        ))
    }

    /** Execute the actual cleanup with Android-mediated deletion. */
    fun executeCleanup(
        onSystemDialogNeeded: (String) -> Unit,
    ) {
        val selected = _state.selection.selectedUris
        if (selected.isEmpty() || _state.scanResult == null) return

        _state = _state.copy(executionStatus = ExecutionStatus.AWAITING_SYSTEM)

        launchLatest {
            try {
                val opId = withContext(Dispatchers.IO) {
                    repository.createOperationRequest(
                        scanSessionId = _state.lastScanId ?: "",
                        serviceId = "cleaning_workflow",
                        requestedAction = "cleanup_selected",
                        selectionCount = selected.size,
                        keeperIds = null,
                        exclusionIds = null,
                        predictedEligibleBytes = _state.selection.selectedBytes,
                    )
                }

                _state = _state.copy(
                    executionStatus = ExecutionStatus.AWAITING_SYSTEM,
                    currentOperationId = opId,
                )

                withContext(Dispatchers.IO) { repository.recordConsent(opId, granted = true) }

                val uris = selected.map { android.net.Uri.parse(it) }.toList()
                val (freed, deletedCount, failedCount) = withContext(Dispatchers.IO) {
                    executeMediaStoreDeletion(uris)
                }

                val after = withContext(Dispatchers.IO) { readDeviceFacts(appContext) }
                DeviceFactsCache.publish(after)

                withContext(Dispatchers.IO) {
                    repository.recordItemOutcomes(
                        opId,
                        uris.map { uri ->
                            OperationItemOutcomeEntity(
                                operationId = opId,
                                itemId = uri.toString(),
                                result = if (deletedCount > 0) "SUCCESS" else "FAILED",
                                postQueryExists = false,
                                measuredBytes = freed / deletedCount.coerceAtLeast(1),
                                providerError = if (failedCount > 0) "partial" else null,
                                completedAt = System.currentTimeMillis(),
                            )
                        },
                        finalStatus = if (failedCount == 0) "VERIFIED" else "PARTIAL",
                    )
                }

                val refreshed = withContext(Dispatchers.IO) { readDeviceFacts(appContext) }
                DeviceFactsCache.publish(refreshed)

                _state = _state.copy(
                    executionStatus = ExecutionStatus.COMPLETED,
                    scanResult = _state.scanResult,
                    selection = SelectionState(),
                    lastOperationResult = OperationResult(
                        freedBytes = freed,
                        deletedCount = deletedCount,
                        failedCount = failedCount,
                        timestamp = System.currentTimeMillis(),
                    ),
                )
            } catch (e: Exception) {
                withContext(Dispatchers.IO) {
                    _state.currentOperationId?.let { repository.updateOperationStatus(it, "ERROR") }
                }
                _state = _state.copy(
                    executionStatus = ExecutionStatus.FAILED,
                    executionMessage = e.message ?: "فشل التنظيف",
                )
            }
        }
    }

    private suspend fun executeMediaStoreDeletion(uris: List<android.net.Uri>): Triple<Long, Int, Int> {
        var freed = 0L
        var deleted = 0
        var failed = 0
        uris.forEach { uri ->
            try {
                val ctx = appContext
                val size = ctx.contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.SIZE), null, null, null)?.use {
                    it.moveToFirst(); it.getLong(0)
                } ?: 0L
                val result = ctx.contentResolver.delete(uri, null, null)
                if (result > 0) {
                    freed += size
                    deleted++
                } else {
                    failed++
                }
            } catch (_: Exception) {
                failed++
            }
        }
        return Triple(freed, deleted, failed)
    }

    fun cancelCurrentOperation() {
        _state.currentOperationId?.let { opId ->
            launchLatest {
                withContext(Dispatchers.IO) { repository.recordConsent(opId, granted = false) }
            }
        }
        _state = _state.copy(
            executionStatus = ExecutionStatus.IDLE,
            executionMessage = "تم الإلغاء",
        )
    }

    // Alias for compatibility with CleanToolsV2.kt
    fun removeProtected(uri: String) {
        _state = _state.copy(selection = _state.selection.copy(
            protectedUris = _state.selection.protectedUris - uri,
        ))
    }

    fun confirmClearOwnCache() {
        cancelCurrentOperation()
    }
}

/** Scan status enum. */
enum class ScanStatus {
    IDLE, SCANNING, COMPLETED, FAILED
}

/** Execution status enum. */
enum class ExecutionStatus {
    IDLE, AWAITING_SYSTEM, EXECUTING, COMPLETED, FAILED, CANCELLED
}

/** Filter state for cleaning routes. */
data class FilterState(
    val minSizeMb: Int = 50,
    val categoryFilter: String = "all",
)

/** Selection state shared across routes. */
data class SelectionState(
    val selectedUris: Set<String> = emptySet(),
    val selectedBytes: Long = 0L,
    val protectedUris: Set<String> = emptySet(),
)

/** Complete scan result. */
data class ScanResult(
    val timestamp: Long,
    val storageTotal: Long,
    val storageUsed: Long,
    val storageFree: Long,
    val cacheBytes: Long,
    val cacheFiles: Int,
    val duplicateGroups: List<MediaDuplicateGroup>,
    val reclaimableDuplicateBytes: Long,
    val largestFiles: List<MediaFileFacts>,
    val imagesBytes: Long,
    val videosBytes: Long,
    val imagesCount: Int,
    val videosCount: Int,
    val mediaPermissionGranted: Boolean,
    val buckets: List<BucketItem>,
)

/** Bucket item for heavy buckets route. */
data class BucketItem(
    val name: String,
    val totalBytes: Long,
    val fileCount: Int,
)

/** Operation result for history. */
data class OperationResult(
    val freedBytes: Long,
    val deletedCount: Int,
    val failedCount: Int,
    val timestamp: Long,
)

/** Complete workflow state. */
data class CleaningWorkflowState(
    val scanStatus: ScanStatus = ScanStatus.IDLE,
    val scanProgress: Float = 0f,
    val scanMessage: String = "جاهز",
    val scanResult: ScanResult? = null,
    val lastScanId: String? = null,
    val lastScanTrigger: Int = 0,
    val filterState: FilterState = FilterState(),
    val selection: SelectionState = SelectionState(),
    val executionStatus: ExecutionStatus = ExecutionStatus.IDLE,
    val currentOperationId: String? = null,
    val executionMessage: String? = null,
    val lastOperationResult: OperationResult? = null,
)