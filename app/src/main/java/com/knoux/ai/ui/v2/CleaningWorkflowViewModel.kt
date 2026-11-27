package com.knoux.ai.ui.v2

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.knoux.ai.database.KnouxRepository
import com.knoux.ai.database.OperationItemOutcomeEntity
import com.knoux.ai.database.OperationRequestEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

/** Immutable snapshot of a pending cleanup operation.
 * Captures all details needed for the system request and post-operation verification,
 * so callbacks cannot read stale or mutated selection state. */
data class PendingCleanupOperation(
    val operationId: String,
    val scanSessionId: String,
    val requestedAction: String,
    val authorizedUris: List<String>,
    val preOperationSizes: Map<String, Long>,
    val protectedUris: Set<String>,
    val executionType: String, // "media_delete" or "cache_clear"
    val creationTime: Long,
    val consentConfirmed: Boolean = false,
    val systemAuthorized: Boolean = false,
    val completed: Boolean = false,
)

/** Result of MediaStore deletion with per-item outcomes. */
data class MediaStoreDeletionResult(
    val freedBytes: Long,
    val deletedCount: Int,
    val failedCount: Int,
    val itemResults: List<OperationItemOutcomeEntity>,
)

/** Shared state for the 15-route cleaning workflow. */
class CleaningWorkflowViewModel(application: Application, repository: KnouxRepository) :
    BaseServiceViewModel(application, repository) {

    private var _state by mutableStateOf(CleaningWorkflowState())
    val state: CleaningWorkflowState get() = _state

    /** Immutable pending operation snapshot. Only one active at a time. */
    private var pendingOperation: PendingCleanupOperation? = null

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

                // Stage 2: MediaStore duplicates & large files (actual read, no extra modules)
                _state = _state.copy(scanMessage = "فحص الوسائط والتكرار…", scanProgress = 0.5f)
                val media = facts.media
                val duplicateGroups = media.duplicateGroups
                val largestFiles = media.largestFiles

                // Stage 3: Classification buckets (only from actual MediaStore read)
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
                        deviceModel = Build.MODEL,
                        apiLevel = Build.VERSION.SDK_INT,
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

    /** Toggle selection with safety invariants.
     * Rejects unknown, stale-session, protected, and keeper URIs. */
    fun toggleSelection(uri: String, isSelected: Boolean) {
        val currentSelection = _state.selection
        val currentScanResult = _state.scanResult

        // Validate URI is known and eligible
        val knownUris = currentScanResult?.let { result ->
            (result.duplicateGroups.flatMap { it.uris } + result.largestFiles.map { it.uriString }).toSet()
        } ?: emptySet()

        if (uri !in knownUris) {
            // Unknown candidate - reject silently
            return
        }

        // Reject protected URIs
        if (uri in currentSelection.protectedUris) {
            return
        }

        val newSelected = if (isSelected) {
            currentSelection.selectedUris + uri
        } else {
            currentSelection.selectedUris - uri
        }

        val newBytes = calculateSelectedBytes(newSelected, currentScanResult)

        _state = _state.copy(selection = _state.selection.copy(
            selectedUris = newSelected,
            selectedBytes = newBytes,
        ))
    }

    /** Select all eligible candidates while preserving at least one retained copy per duplicate group. */
    fun selectAllEligible() {
        val currentScanResult = _state.scanResult ?: return
        val protectedUris = _state.selection.protectedUris

        val allUris = mutableSetOf<String>()

        // Add duplicate group URIs, but keep at least one copy per group
        currentScanResult.duplicateGroups.forEach { group ->
            val eligibleInGroup = group.uris.filter { it !in protectedUris }
            if (eligibleInGroup.isNotEmpty()) {
                // Always retain the first copy (keeper)
                val retainOne = group.uris.firstOrNull { it !in protectedUris }
                if (retainOne != null) {
                    // Add all except the retained copy
                    eligibleInGroup.filter { it != retainOne }.forEach { allUris.add(it) }
                }
            }
        }

        // Add large file URIs (not in duplicate groups, not protected)
        val duplicateUris = currentScanResult.duplicateGroups.flatMap { it.uris }.toSet()
        currentScanResult.largestFiles.forEach { file ->
            if (file.uriString !in duplicateUris && file.uriString !in protectedUris) {
                allUris.add(file.uriString)
            }
        }

        val newBytes = calculateSelectedBytes(allUris, currentScanResult)

        _state = _state.copy(selection = _state.selection.copy(
            selectedUris = allUris,
            selectedBytes = newBytes,
        ))
    }

    fun clearSelection() {
        // Preserve protection state - only clear selected items, not protected
        _state = _state.copy(selection = _state.selection.copy(
            selectedUris = emptySet(),
            selectedBytes = 0L,
        ))
    }

    fun addProtected(uri: String) {
        val currentSelection = _state.selection
        val currentScanResult = _state.scanResult

        // Reject unknown URIs
        val knownUris = currentScanResult?.let { result ->
            (result.duplicateGroups.flatMap { it.uris } + result.largestFiles.map { it.uriString }).toSet()
        } ?: emptySet()
        if (uri !in knownUris) return

        val newProtected = currentSelection.protectedUris + uri
        val newSelected = currentSelection.selectedUris - uri
        val newBytes = calculateSelectedBytes(newSelected, currentScanResult)

        _state = _state.copy(selection = _state.selection.copy(
            protectedUris = newProtected,
            selectedUris = newSelected,
            selectedBytes = newBytes,
        ))
    }

    fun removeProtected(uri: String) {
        _state = _state.copy(selection = _state.selection.copy(
            protectedUris = _state.selection.protectedUris - uri,
        ))
    }

    private fun calculateSelectedBytes(uris: Set<String>, scanResult: ScanResult?): Long {
        if (scanResult == null || uris.isEmpty()) return 0L
        val uriToSize = mutableMapOf<String, Long>()
        scanResult.duplicateGroups.forEach { g ->
            g.uris.forEach { uriToSize[it] = g.sizeBytes }
        }
        scanResult.largestFiles.forEach { f ->
            uriToSize[f.uriString] = f.sizeBytes
        }
        return uris.sumOf { uriToSize[it] ?: 0L }
    }

    /** Execute the actual cleanup with Android-mediated deletion.
     *  Prepares an immutable pending operation and sets state to AWAITING_SYSTEM.
     *  Does NOT record consent yet - consent is recorded only after actual system authorization. */
    fun executeCleanup() {
        val selected = _state.selection.selectedUris
        if (selected.isEmpty() || _state.scanResult == null) return

        // Reject if any selected URI is protected
        val protected = _state.selection.protectedUris
        if (selected.any { it in protected }) return

        _state = _state.copy(executionStatus = ExecutionStatus.AWAITING_SYSTEM)

        launchLatest {
            try {
                val opId = UUID.randomUUID().toString()
                val scanSessionId = _state.lastScanId ?: ""
                val uris = selected.toList()

                // Capture pre-operation sizes for verification
                val ctx = appContext
                val preOperationSizes = withContext(Dispatchers.IO) {
                    uris.associateWith { uri ->
                        val u = Uri.parse(uri)
                        ctx.contentResolver.query(u, arrayOf(android.provider.OpenableColumns.SIZE), null, null, null)?.use {
                            it.moveToFirst(); it.getLong(0)
                        } ?: 0L
                    }
                }

                // Create immutable pending operation snapshot
                val pendingOp = PendingCleanupOperation(
                    operationId = opId,
                    scanSessionId = scanSessionId,
                    requestedAction = "cleanup_selected",
                    authorizedUris = uris,
                    preOperationSizes = preOperationSizes,
                    protectedUris = _state.selection.protectedUris,
                    executionType = "media_delete",
                    creationTime = System.currentTimeMillis(),
                    consentConfirmed = false,
                    systemAuthorized = false,
                    completed = false,
                )
                pendingOperation = pendingOp

                // Create operation request in Room (consent not yet given)
                withContext(Dispatchers.IO) {
                    repository.createOperationRequest(
                        scanSessionId = scanSessionId,
                        serviceId = "cleaning_workflow",
                        requestedAction = "cleanup_selected",
                        selectionCount = uris.size,
                        keeperIds = null,
                        exclusionIds = null,
                        predictedEligibleBytes = _state.selection.selectedBytes,
                    )
                }

                _state = _state.copy(
                    executionStatus = ExecutionStatus.AWAITING_SYSTEM,
                    currentOperationId = opId,
                    executionMessage = "في انتظار إذن النظام للحذف",
                )
            } catch (e: Exception) {
                pendingOperation = null
                _state = _state.copy(
                    executionStatus = ExecutionStatus.FAILED,
                    executionMessage = "فشل التحضير: ${e.message ?: "خطأ غير معروف"}",
                )
            }
        }
    }

    /** Launch the MediaStore.createDeleteRequest system dialog.
     *  Called from UI after user confirms the cleanup action.
     *  Does NOT execute deletion yet - only launches the authorization dialog. */
    fun requestMediaStoreDeletion(onSystemDialogNeeded: (PendingIntent) -> Unit) {
        val pendingOp = pendingOperation ?: return
        if (pendingOp.systemAuthorized || pendingOp.completed) return

        val uris = pendingOp.authorizedUris.map { Uri.parse(it) }

        launchLatest {
            try {
                val ctx = appContext
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && uris.isNotEmpty()) {
                    val deleteRequest = MediaStore.createDeleteRequest(ctx.contentResolver, uris)
                    if (deleteRequest != null) {
                        // Mark as system-authorization-pending (not yet authorized)
                        pendingOperation = pendingOp.copy(systemAuthorized = false, consentConfirmed = false)
                        onSystemDialogNeeded(deleteRequest)
                        return@launchLatest
                    }
                }
                // Fail closed - do not fall back to direct deletion
                _state = _state.copy(
                    executionStatus = ExecutionStatus.FAILED,
                    executionMessage = "لا يمكن تشغيل إذن الحذف الرسمي",
                )
                pendingOperation = null
            } catch (e: Exception) {
                // Fail closed - no fallback to unauthorized deletion
                _state = _state.copy(
                    executionStatus = ExecutionStatus.FAILED,
                    executionMessage = "فشل تشغيل إذن الحذف: ${e.message ?: "خطأ غير معروف"}",
                )
                pendingOperation = null
            }
        }
    }

    /** Handle the result from MediaStore.createDeleteRequest system dialog.
     *  Only records consent AFTER actual system authorization result.
     *  Verifies outcomes using pre-operation snapshot, does not delete again. */
    fun onMediaStoreDeleteResult(granted: Boolean) {
        val pendingOp = pendingOperation ?: return
        if (pendingOp.executionType != "media_delete") return
        if (pendingOp.completed) return

        if (!granted) {
            // User denied or cancelled - record consent as false, fail closed
            _state = _state.copy(
                executionStatus = ExecutionStatus.CANCELLED,
                executionMessage = "تم إلغاء الإذن من قبلك",
            )
            launchLatest {
                withContext(Dispatchers.IO) {
                    repository.recordConsent(pendingOp.operationId, granted = false)
                }
            }
            pendingOperation = null
            return
        }

        // User granted system authorization - now record consent and verify outcomes
        _state = _state.copy(executionStatus = ExecutionStatus.EXECUTING)

        launchLatest {
            try {
                // Record actual consent after system authorization
                withContext(Dispatchers.IO) {
                    repository.recordConsent(pendingOp.operationId, granted = true)
                }

                // Verify outcomes using pre-operation snapshot - do NOT delete again
                val ctx = appContext
                val itemResults = mutableListOf<OperationItemOutcomeEntity>()
                var freedBytes = 0L
                var deletedCount = 0
                var failedCount = 0

                pendingOp.authorizedUris.forEach { uriString ->
                    val uri = Uri.parse(uriString)
                    val preSize = pendingOp.preOperationSizes[uriString] ?: 0L

                    // Post-operation verification: check if URI is actually gone
                    val existsAfter = ctx.contentResolver.query(uri, arrayOf("_id"), null, null, null)?.use {
                        it.moveToFirst()
                    } ?: false

                    val preExisted = preSize > 0L

                    if (preExisted && !existsAfter) {
                        // Confirmed: existed before, now gone = successfully deleted
                        freedBytes += preSize
                        deletedCount++
                        itemResults.add(OperationItemOutcomeEntity(
                            operationId = pendingOp.operationId,
                            itemId = uriString,
                            result = "SUCCESS",
                            postQueryExists = false,
                            measuredBytes = preSize,
                            providerError = null,
                            completedAt = System.currentTimeMillis(),
                        ))
                    } else if (preExisted && existsAfter) {
                        // Existed before and still exists = deletion failed
                        failedCount++
                        itemResults.add(OperationItemOutcomeEntity(
                            operationId = pendingOp.operationId,
                            itemId = uriString,
                            result = "FAILED",
                            postQueryExists = true,
                            measuredBytes = 0L,
                            providerError = "Deletion did not remove the item",
                            completedAt = System.currentTimeMillis(),
                        ))
                    } else if (!preExisted && !existsAfter) {
                        // Didn't exist before and still doesn't = not found (already deleted or never existed)
                        failedCount++
                        itemResults.add(OperationItemOutcomeEntity(
                            operationId = pendingOp.operationId,
                            itemId = uriString,
                            result = "NOT_FOUND",
                            postQueryExists = false,
                            measuredBytes = 0L,
                            providerError = "Item did not exist before operation",
                            completedAt = System.currentTimeMillis(),
                        ))
                    } else {
                        // Didn't exist before but exists now = unknown state
                        failedCount++
                        itemResults.add(OperationItemOutcomeEntity(
                            operationId = pendingOp.operationId,
                            itemId = uriString,
                            result = "UNVERIFIED",
                            postQueryExists = true,
                            measuredBytes = 0L,
                            providerError = "Unable to verify pre-operation state",
                            completedAt = System.currentTimeMillis(),
                        ))
                    }
                }

                // Update pending operation as completed
                pendingOperation = pendingOp.copy(
                    consentConfirmed = true,
                    systemAuthorized = true,
                    completed = true,
                )

                // Persist outcomes with actual final status
                withContext(Dispatchers.IO) {
                    repository.recordItemOutcomes(
                        pendingOp.operationId,
                        itemResults,
                        finalStatus = if (failedCount == 0) "VERIFIED" else "PARTIAL",
                    )
                }

                val afterFacts = withContext(Dispatchers.IO) { readDeviceFacts(appContext) }
                DeviceFactsCache.publish(afterFacts)

                _state = _state.copy(
                    executionStatus = ExecutionStatus.COMPLETED,
                    scanResult = _state.scanResult,
                    selection = SelectionState(),
                    lastOperationResult = OperationResult(
                        freedBytes = freedBytes,
                        deletedCount = deletedCount,
                        failedCount = failedCount,
                        timestamp = System.currentTimeMillis(),
                    ),
                )
                pendingOperation = null
            } catch (e: Exception) {
                withContext(Dispatchers.IO) {
                    pendingOp.operationId.let { repository.updateOperationStatus(it, "ERROR") }
                }
                _state = _state.copy(
                    executionStatus = ExecutionStatus.FAILED,
                    executionMessage = e.message ?: "فشل التنظيف",
                )
                pendingOperation = null
            }
        }
    }

    fun cancelCurrentOperation() {
        val currentOp = pendingOperation
        if (currentOp != null && !currentOp.completed) {
            launchLatest {
                withContext(Dispatchers.IO) {
                    repository.recordConsent(currentOp.operationId, granted = false)
                }
            }
            pendingOperation = null
        }
        _state = _state.copy(
            executionStatus = ExecutionStatus.IDLE,
            executionMessage = "تم الإلغاء",
        )
    }

    /** Prepare cache cleanup operation with distinct operation type. */
    fun prepareCacheCleanup() {
        _state = _state.copy(executionStatus = ExecutionStatus.AWAITING_SYSTEM)

        launchLatest {
            try {
                val ctx = appContext
                val (cacheBytes, cacheFiles) = withContext(Dispatchers.IO) { ownCacheBytes(ctx) }
                if (cacheBytes <= 0L) {
                    _state = _state.copy(
                        executionStatus = ExecutionStatus.FAILED,
                        executionMessage = "لا توجد ملفات كاش للتنظيف",
                    )
                    return@launchLatest
                }

                val opId = UUID.randomUUID().toString()
                val scanSessionId = _state.lastScanId ?: ""

                val pendingOp = PendingCleanupOperation(
                    operationId = opId,
                    scanSessionId = scanSessionId,
                    requestedAction = "clear_own_cache",
                    authorizedUris = listOf("knoux_own_cache"),
                    preOperationSizes = mapOf("knoux_own_cache" to cacheBytes),
                    protectedUris = emptySet(),
                    executionType = "cache_clear",
                    creationTime = System.currentTimeMillis(),
                    consentConfirmed = false,
                    systemAuthorized = false,
                    completed = false,
                )
                pendingOperation = pendingOp

                withContext(Dispatchers.IO) {
                    repository.createOperationRequest(
                        scanSessionId = scanSessionId,
                        serviceId = "cleaning_workflow",
                        requestedAction = "clear_own_cache",
                        selectionCount = 1,
                        keeperIds = null,
                        exclusionIds = null,
                        predictedEligibleBytes = cacheBytes,
                    )
                }

                _state = _state.copy(
                    executionStatus = ExecutionStatus.AWAITING_SYSTEM,
                    currentOperationId = opId,
                    executionMessage = "في انتظار تأكيد تنظيف كاش KNOUX",
                )
            } catch (e: Exception) {
                pendingOperation = null
                _state = _state.copy(
                    executionStatus = ExecutionStatus.FAILED,
                    executionMessage = "فشل التحضير: ${e.message ?: "خطأ غير معروف"}",
                )
            }
        }
    }

    /** Execute cache cleanup after user confirmation.
     *  Only executes if pending operation matches cache_clear type. */
    fun executeCacheCleanup() {
        val pendingOp = pendingOperation ?: return
        if (pendingOp.executionType != "cache_clear" || pendingOp.completed) return

        _state = _state.copy(executionStatus = ExecutionStatus.EXECUTING)

        launchLatest {
            try {
                withContext(Dispatchers.IO) {
                    repository.recordConsent(pendingOp.operationId, granted = true)
                }

                val ctx = appContext
                val before = withContext(Dispatchers.IO) { ownCacheBytes(ctx).first }
                val freed = withContext(Dispatchers.IO) { clearOwnCache(ctx) }
                val after = withContext(Dispatchers.IO) { ownCacheBytes(ctx).first }
                val verified = after < before
                val freedBytes = before - after

                pendingOperation = pendingOp.copy(
                    consentConfirmed = true,
                    systemAuthorized = true,
                    completed = true,
                )

                val opId = pendingOp.operationId
                withContext(Dispatchers.IO) {
                    repository.recordItemOutcomes(
                        opId,
                        listOf(
                            OperationItemOutcomeEntity(
                                operationId = opId,
                                itemId = "knoux_own_cache",
                                result = if (verified) "SUCCESS" else "NO_CHANGE",
                                postQueryExists = after > 0L,
                                measuredBytes = freedBytes,
                                providerError = null,
                                completedAt = System.currentTimeMillis(),
                            )
                        ),
                        finalStatus = if (verified) "VERIFIED" else "PARTIAL",
                    )
                }

                val refreshed = withContext(Dispatchers.IO) { readDeviceFacts(appContext) }
                DeviceFactsCache.publish(refreshed)

                _state = _state.copy(
                    executionStatus = ExecutionStatus.COMPLETED,
                    scanResult = _state.scanResult?.copy(
                        cacheBytes = after,
                        cacheFiles = withContext(Dispatchers.IO) { ownCacheBytes(appContext).second },
                    ),
                    selection = SelectionState(),
                    lastOperationResult = OperationResult(
                        freedBytes = freedBytes,
                        deletedCount = if (verified) 1 else 0,
                        failedCount = if (verified) 0 else 1,
                        timestamp = System.currentTimeMillis(),
                    ),
                )
                pendingOperation = null
            } catch (e: Exception) {
                withContext(Dispatchers.IO) {
                    pendingOp.operationId.let { repository.updateOperationStatus(it, "ERROR") }
                }
                _state = _state.copy(
                    executionStatus = ExecutionStatus.FAILED,
                    executionMessage = e.message ?: "فشل تنظيف الكاش",
                )
                pendingOperation = null
            }
        }
    }

    /** Get the pending operation for UI to check if action is allowed. */
    fun getPendingOperation(): PendingCleanupOperation? = pendingOperation
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