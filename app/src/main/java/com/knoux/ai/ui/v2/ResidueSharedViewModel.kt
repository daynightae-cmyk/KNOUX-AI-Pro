package com.knoux.ai.ui.v2

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.knoux.ai.database.KnouxRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Shared ViewModel for all residue screens to maintain session continuity.
 * Provides a single source of truth for residue scan state, selections, and operations.
 * Uses separate jobs for read-only scans vs destructive cleanup operations to prevent
 * interference between unrelated user actions.
 */
class ResidueSharedViewModel(
    application: Application,
    repository: KnouxRepository
) : BaseServiceViewModel(application, repository) {

    private var _state by mutableStateOf(ResidueSharedState())
    val state: ResidueSharedState get() = _state

    /** Separate job for read-only scans - can be cancelled by new scans. */
    private var scanJob: Job? = null

    /** Separate job for destructive cleanup - must NOT be cancelled by scan requests. */
    private var cleanupJob: Job? = null

    /** Triggers a fresh device scan for residue. */
    fun scanResidue() {
        // Cancel any existing scan job, but preserve cleanup job
        scanJob?.cancel()
        if (_state.isScanning) return

        _state = _state.copy(
            isScanning = true,
            scanError = null,
            progress = 0.04f,
            stageLabel = "بدء فحص البقايا",
        )

        scanJob = scope.launch {
            try {
                val ctx = appContext
                _state = _state.copy(progress = 0.14f, stageLabel = "قراءة التخزين والوسائط")
                val facts = withContext(Dispatchers.IO) { readDeviceFactsFiltered(ctx, _state.includeMediaStore) }
                DeviceFactsCache.publish(facts)

                _state = _state.copy(progress = 0.58f, stageLabel = "تحليل مصادر البقايا")

                val mediaBytes = facts.media.imagesBytes + facts.media.videosBytes
                val cacheBytes = facts.ownCacheBytes
                val otherBytes = (facts.storage.usedBytes - mediaBytes - cacheBytes).coerceAtLeast(0L)
                val permissionGranted = facts.media.permissionGranted
                val capability = when {
                    !facts.media.permissionGranted && _state.includeMediaStore -> "PERMISSION_REQUIRED"
                    else -> "AVAILABLE"
                }

                val sessionId = withContext(Dispatchers.IO) {
                    _state = _state.copy(progress = 0.66f, stageLabel = "حفظ جلسة الفحص")
                    val id = repository.createScanSession(
                        serviceId = "residue",
                        serviceName = "فحص بقايا التطبيقات",
                        deviceModel = Build.MODEL,
                        apiLevel = Build.VERSION.SDK_INT,
                    )

                    repository.recordStorageSnapshot(
                        id = java.util.UUID.randomUUID().toString(),
                        totalBytes = facts.storage.totalBytes,
                        usedBytes = facts.storage.usedBytes,
                        freeBytes = facts.storage.freeBytes,
                        mediaBytes = mediaBytes,
                        cacheBytes = cacheBytes,
                        otherBytes = otherBytes,
                        scope = "residue",
                    )

                    val mediaItems = facts.media.largestFiles.map { file ->
                        com.knoux.ai.database.ObservedItemEntity(
                            scanSessionId = id,
                            providerId = file.uriString,
                            uri = file.uriString,
                            name = file.name,
                            sizeBytes = file.sizeBytes,
                            bucket = file.bucket,
                            isVideo = file.isVideo,
                            lastSeen = System.currentTimeMillis(),
                        )
                    }
                    val appItems = facts.installedApps.take(80).map { app ->
                        com.knoux.ai.database.ObservedItemEntity(
                            scanSessionId = id,
                            providerId = app.packageName,
                            name = app.label,
                            scope = "app",
                            lastSeen = System.currentTimeMillis(),
                        )
                    }

                    repository.completeScanSession(
                        sessionId = id,
                        status = if (facts.computedAt > 0L) "COMPLETED" else "PARTIAL",
                        permissionGranted = permissionGranted,
                        itemsFound = facts.media.images + facts.media.videos + facts.installedCount,
                        bytesFound = mediaBytes,
                        bytesReclaimable = cacheBytes,
                        items = mediaItems + appItems,
                    )

                    repository.recordFeatureCapability(
                        featureId = "residue",
                        serviceId = "residue",
                        capabilityStatus = capability,
                        apiSource = "MediaStore+PackageManager",
                        permissionRequired = if (Build.VERSION.SDK_INT >= 33) "READ_MEDIA_IMAGES" else "READ_EXTERNAL_STORAGE",
                        reason = if (capability == "PERMISSION_REQUIRED") "منح صلاحية الوسائط مطلوب" else null,
                    )

                    id
                }

                val sessions = withContext(Dispatchers.IO) {
                    _state = _state.copy(progress = 0.92f, stageLabel = "تحديث سجل الخدمة")
                    repository.countSessions("residue")
                }
                val items = withContext(Dispatchers.IO) {
                    repository.countItems(sessionId)
                }
                val operations = withContext(Dispatchers.IO) {
                    repository.countOperations("residue")
                }

                _state = _state.copy(
                    facts = facts,
                    isScanning = false,
                    scanError = null,
                    lastScanId = sessionId,
                    sessionsLogged = sessions,
                    itemsLogged = items,
                    operationsLogged = operations,
                    capabilityStatus = capability,
                    progress = 1f,
                    stageLabel = "اكتملت القراءة",
                )
            } catch (e: CancellationException) {
                // Scan cancelled - do not update state, preserve previous facts
                throw e
            } catch (e: Exception) {
                _state = _state.copy(
                    isScanning = false,
                    scanError = e.message ?: "تعذّر قراءة بيانات الجهاز",
                )
            }
        }
    }

    /** Updates the MediaStore inclusion preference and triggers rescan if data exists. */
    fun setIncludeMediaStore(include: Boolean) {
        if (_state.includeMediaStore == include) return
        _state = _state.copy(includeMediaStore = include)
        if (_state.facts.computedAt > 0L) {
            scanResidue()
        }
    }

    /** Updates the SAF source selection. */
    fun setSafSourceUri(uri: String?) {
        _state = _state.copy(safSourceUri = uri)
    }

    /** Adds a protected item that should never be deleted. */
    fun addProtectedItem(itemId: String) {
        _state = _state.copy(protectedItems = _state.protectedItems + itemId)
        // If item was selected, deselect it
        if (itemId in _state.selectedItems) {
            toggleCandidateSelection(itemId, 0L)
        }
    }

    /** Removes a protected item. */
    fun removeProtectedItem(itemId: String) {
        _state = _state.copy(protectedItems = _state.protectedItems - itemId)
    }

    /** Toggles selection of a candidate item. */
    fun toggleCandidateSelection(itemId: String, sizeBytes: Long) {
        // Block selection changes while a cleanup operation is pending or executing
        if (_state.operationStatus == OperationStatus.AWAITING_CONSENT ||
            _state.operationStatus == OperationStatus.EXECUTING) {
            return
        }

        val currentSelection = _state.selectedItems
        if (itemId in currentSelection) {
            _state = _state.copy(
                selectedItems = currentSelection - itemId,
                selectedBytes = _state.selectedBytes - sizeBytes,
            )
        } else {
            if (itemId in _state.protectedItems) return
            _state = _state.copy(
                selectedItems = currentSelection + itemId,
                selectedBytes = _state.selectedBytes + sizeBytes,
            )
        }
    }

    /** Clears all selections. */
    fun clearSelection() {
        if (_state.operationStatus == OperationStatus.AWAITING_CONSENT ||
            _state.operationStatus == OperationStatus.EXECUTING) {
            return
        }
        _state = _state.copy(
            selectedItems = emptySet(),
            selectedBytes = 0L,
        )
    }

    /**
     * Prepares a cleanup operation with immutable operation identity.
     * Returns the operation ID on success, which must be presented to the user for confirmation.
     * Legal transitions: IDLE -> PREPARING -> AWAITING_CONSENT
     * Rejects: concurrent preparation, preparation while operation executing, invalid state.
     */
    suspend fun prepareCleanup(): CleanupPreparationResult {
        // Legal state check: only allow from IDLE or after final states
        val currentStatus = _state.operationStatus
        if (currentStatus == OperationStatus.PREPARING ||
            currentStatus == OperationStatus.AWAITING_CONSENT ||
            currentStatus == OperationStatus.EXECUTING) {
            return CleanupPreparationResult(success = false, errorMessage = "عملية أخرى قيد التنفيذ")
        }

        val facts = _state.facts
        if (facts.computedAt == 0L) {
            return CleanupPreparationResult(success = false, errorMessage = "لا توجد بيانات فحص")
        }

        // Determine cleanup scope based on current selections and scope settings
        val (targetItemIds, predictedBytes, actionType) = determineCleanupScope()
        if (targetItemIds.isEmpty() || predictedBytes <= 0) {
            return CleanupPreparationResult(success = false, errorMessage = "لا توجد ملفات قابلة للتنظيف في النطاق المحدد")
        }

        _state = _state.copy(operationStatus = OperationStatus.PREPARING)

        val opId = withContext(Dispatchers.IO) {
            repository.createOperationRequest(
                scanSessionId = _state.lastScanId ?: "",
                serviceId = "app_residue",
                requestedAction = actionType,
                selectionCount = targetItemIds.size,
                keeperIds = _state.protectedItems.toList(),
                exclusionIds = _state.selectedItems.toList(), // Note: exclusions are items user explicitly excluded
                predictedEligibleBytes = predictedBytes,
            )
        }

        // Capture pre-operation existence for verification
        val preOpExistence = withContext(Dispatchers.IO) {
            targetItemIds.associateWith { itemId ->
                checkItemExists(appContext, itemId)
            }
        }
        val preOpSizes = withContext(Dispatchers.IO) {
            targetItemIds.associateWith { itemId ->
                getItemSize(appContext, itemId)
            }
        }

        _state = _state.copy(
            pendingOperationId = opId,
            pendingOperationAction = actionType,
            pendingOperationPredictedBytes = predictedBytes,
            pendingOperationScope = CleanupScope(
                targetItemIds = targetItemIds,
                preOpExistence = preOpExistence,
                preOpSizes = preOpSizes,
                pendingOperationId = opId,
            ),
            operationStatus = OperationStatus.AWAITING_CONSENT,
        )

        return CleanupPreparationResult(success = true, operationId = opId)
    }

    /**
     * Executes the prepared cleanup with explicit consent bound to the operation ID.
     * Legal transitions: AWAITING_CONSENT -> EXECUTING -> VERIFIED|PARTIAL|NO_CHANGE|FAILED
     * Rejects: missing operation ID, stale operation ID, invalid state, consent mismatch.
     */
    fun executeCleanup(operationId: String, consentGranted: Boolean): CleanupExecutionResult {
        val pendingOpId = _state.pendingOperationId
        val pendingScope = _state.pendingOperationScope

        // Validate operation identity
        if (pendingOpId == null || operationId != pendingOpId) {
            return CleanupExecutionResult(success = false, errorMessage = "معرف عملية غير صالح أو منتهي الصلاحية", finalStatus = OperationStatus.FAILED)
        }
        if (pendingScope == null) {
            return CleanupExecutionResult(success = false, errorMessage = "نطاق العملية غير متاح", finalStatus = OperationStatus.FAILED)
        }
        if (_state.operationStatus != OperationStatus.AWAITING_CONSENT) {
            return CleanupExecutionResult(success = false, errorMessage = "العملية ليست في انتظار الموافقة", finalStatus = OperationStatus.FAILED)
        }

        if (!consentGranted) {
            // Explicit cancellation - record consent as false
            cleanupJob?.cancel()
            cleanupJob = scope.launch {
                withContext(Dispatchers.IO) {
                    repository.recordConsent(pendingOpId, granted = false)
                }
            }
            _state = _state.copy(
                operationStatus = OperationStatus.CANCELLED,
                pendingOperationId = null,
                pendingOperationAction = null,
                pendingOperationPredictedBytes = 0L,
                pendingOperationScope = null,
            )
            return CleanupExecutionResult(success = false, errorMessage = "تم إلغاء الإذن", finalStatus = OperationStatus.CANCELLED)
        }

        // Legal state transition: AWAITING_CONSENT -> EXECUTING
        _state = _state.copy(operationStatus = OperationStatus.EXECUTING)

        cleanupJob = scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    repository.recordConsent(pendingOpId, granted = true)
                }

                val actionType = _state.pendingOperationAction ?: "clear_own_cache"
                val (freedBytes, verified, itemResults) = when (actionType) {
                    "clear_own_cache" -> executeCacheCleanup(pendingScope)
                    "cleanup_selected" -> executeSelectiveCleanup(pendingScope)
                    else -> executeCacheCleanup(pendingScope) // fallback
                }

                val finalStatus = when {
                    verified -> OperationStatus.VERIFIED
                    freedBytes > 0 -> OperationStatus.PARTIAL
                    else -> OperationStatus.NO_CHANGE
                }

                withContext(Dispatchers.IO) {
                    repository.recordItemOutcomes(
                        pendingOpId,
                        itemResults,
                        finalStatus = finalStatus.name
                    )
                }

                // Refresh device facts after cleanup
                val refreshedFacts = withContext(Dispatchers.IO) { readDeviceFactsFiltered(appContext, _state.includeMediaStore) }
                DeviceFactsCache.publish(refreshedFacts)

                _state = _state.copy(
                    facts = refreshedFacts,
                    operationStatus = finalStatus,
                    lastFreedBytes = freedBytes,
                    lastOperationResult = when {
                        verified -> "SUCCESS"
                        freedBytes > 0 -> "PARTIAL"
                        else -> "NO_CHANGE"
                    },
                    lastOperationVerified = verified,
                    pendingOperationId = null,
                    pendingOperationAction = null,
                    pendingOperationPredictedBytes = 0L,
                    pendingOperationScope = null,
                )
                scanResidue() // Refresh after cleanup

            } catch (e: CancellationException) {
                // Cleanup was cancelled mid-execution - do not claim success
                withContext(Dispatchers.IO) {
                    repository.recordItemOutcomes(pendingOpId, listOf(
                        com.knoux.ai.database.OperationItemOutcomeEntity(
                            operationId = pendingOpId,
                            itemId = "cleanup_cancelled",
                            result = "CANCELLED",
                            postQueryExists = true,
                            measuredBytes = 0L,
                            providerError = "Operation cancelled",
                            completedAt = System.currentTimeMillis(),
                        )
                    ), finalStatus = OperationStatus.CANCELLED.name)
                }
                _state = _state.copy(
                    operationStatus = OperationStatus.CANCELLED,
                    lastOperationResult = "CANCELLED",
                    pendingOperationId = null,
                    pendingOperationAction = null,
                    pendingOperationPredictedBytes = 0L,
                    pendingOperationScope = null,
                )
                throw e
            } catch (e: Exception) {
                withContext(Dispatchers.IO) {
                    repository.recordItemOutcomes(pendingOpId, listOf(
                        com.knoux.ai.database.OperationItemOutcomeEntity(
                            operationId = pendingOpId,
                            itemId = "cleanup_error",
                            result = "ERROR",
                            postQueryExists = true,
                            measuredBytes = 0L,
                            providerError = e.message,
                            completedAt = System.currentTimeMillis(),
                        )
                    ), finalStatus = OperationStatus.FAILED.name)
                }
                _state = _state.copy(
                    operationStatus = OperationStatus.FAILED,
                    lastOperationResult = "ERROR",
                    pendingOperationId = null,
                    pendingOperationAction = null,
                    pendingOperationPredictedBytes = 0L,
                    pendingOperationScope = null,
                )
            }
        }

        return CleanupExecutionResult(success = true, finalStatus = OperationStatus.EXECUTING)
    }

    /** Cancels a pending operation. Only allowed from AWAITING_CONSENT state. */
    fun cancelPendingOperation() {
        val currentStatus = _state.operationStatus
        if (currentStatus != OperationStatus.AWAITING_CONSENT) return

        val opId = _state.pendingOperationId ?: return
        cleanupJob?.cancel()
        cleanupJob = scope.launch {
            withContext(Dispatchers.IO) {
                repository.recordConsent(opId, granted = false)
            }
        }
        _state = _state.copy(
            operationStatus = OperationStatus.CANCELLED,
            pendingOperationId = null,
            pendingOperationAction = null,
            pendingOperationPredictedBytes = 0L,
            pendingOperationScope = null,
        )
    }

    /** Determines what cleanup action to perform based on current state. */
    private fun determineCleanupScope(): Triple<List<String>, Long, String> {
        val facts = _state.facts
        val includeMediaStore = _state.includeMediaStore

        // If user has selected specific items, use those
        if (_state.selectedItems.isNotEmpty()) {
            val selectedList = _state.selectedItems.toList()
            val totalBytes = _state.selectedBytes
            return Triple(selectedList, totalBytes, "cleanup_selected")
        }

        // Default: KNOUX own cache cleanup
        val cacheBytes = facts.ownCacheBytes
        if (cacheBytes > 0) {
            return Triple(listOf("knoux_own_cache"), cacheBytes, "clear_own_cache")
        }

        // MediaStore duplicates if enabled and available
        if (includeMediaStore && facts.media.duplicateGroups.isNotEmpty()) {
            val reclaimable = facts.media.duplicateGroups.sumOf { it.reclaimableBytes }
            if (reclaimable > 0) {
                val itemIds = facts.media.duplicateGroups.flatMap { group ->
                    group.uris.drop(1) // Keep first as keeper
                }
                return Triple(itemIds, reclaimable, "cleanup_selected")
            }
        }

        return Triple(emptyList(), 0L, "clear_own_cache")
    }

    /** Executes KNOUX own cache cleanup with proper verification. */
    private suspend fun executeCacheCleanup(scope: CleanupScope): Triple<Long, Boolean, List<com.knoux.ai.database.OperationItemOutcomeEntity>> {
        val ctx = appContext
        val before = withContext(Dispatchers.IO) { ownCacheBytes(ctx).first }
        val freed = withContext(Dispatchers.IO) { clearOwnCache(ctx) }
        val after = withContext(Dispatchers.IO) { ownCacheBytes(ctx).first }
        val verified = after < before

        val itemResult = com.knoux.ai.database.OperationItemOutcomeEntity(
            operationId = scope.pendingOperationId ?: "",
            itemId = "knoux_own_cache",
            result = if (verified) "SUCCESS" else "NO_CHANGE",
            postQueryExists = after > 0L,
            measuredBytes = freed,
            providerError = null,
            completedAt = System.currentTimeMillis(),
        )

        return Triple(freed, verified, listOf(itemResult))
    }

    /** Executes selective cleanup of specific items with per-item verification. */
    private suspend fun executeSelectiveCleanup(scope: CleanupScope): Triple<Long, Boolean, List<com.knoux.ai.database.OperationItemOutcomeEntity>> {
        val ctx = appContext
        val itemResults = mutableListOf<com.knoux.ai.database.OperationItemOutcomeEntity>()
        var totalFreed = 0L
        var deletedCount = 0
        var failedCount = 0

        for (itemId in scope.targetItemIds) {
            val preExisted = scope.preOpExistence[itemId] ?: false
            val preSize = scope.preOpSizes[itemId] ?: 0L

            // Attempt deletion
            val deleted = withContext(Dispatchers.IO) {
                deleteItemById(ctx, itemId)
            }

            // Post-operation verification
            val existsAfter = withContext(Dispatchers.IO) {
                checkItemExists(ctx, itemId)
            }

            val verification = when {
                !preExisted && !existsAfter -> VerificationResult.CONFIRMED_ABSENT // Was absent, still absent
                preExisted && !existsAfter -> VerificationResult.CONFIRMED_ABSENT // Existed, now gone
                preExisted && existsAfter -> VerificationResult.EXISTS // Still exists
                else -> VerificationResult.UNKNOWN
            }

            when (verification) {
                VerificationResult.CONFIRMED_ABSENT -> {
                    if (preExisted) {
                        totalFreed += preSize
                        deletedCount++
                        itemResults.add(com.knoux.ai.database.OperationItemOutcomeEntity(
                            operationId = scope.pendingOperationId ?: "",
                            itemId = itemId,
                            result = "SUCCESS",
                            postQueryExists = false,
                            measuredBytes = preSize,
                            providerError = null,
                            completedAt = System.currentTimeMillis(),
                        ))
                    } else {
                        // Didn't exist before, still absent - NOT_FOUND
                        failedCount++
                        itemResults.add(com.knoux.ai.database.OperationItemOutcomeEntity(
                            operationId = scope.pendingOperationId ?: "",
                            itemId = itemId,
                            result = "NOT_FOUND",
                            postQueryExists = false,
                            measuredBytes = 0L,
                            providerError = "Item did not exist before operation",
                            completedAt = System.currentTimeMillis(),
                        ))
                    }
                }
                VerificationResult.EXISTS -> {
                    failedCount++
                    itemResults.add(com.knoux.ai.database.OperationItemOutcomeEntity(
                        operationId = scope.pendingOperationId ?: "",
                        itemId = itemId,
                        result = "FAILED",
                        postQueryExists = true,
                        measuredBytes = 0L,
                        providerError = "Deletion did not remove the item",
                        completedAt = System.currentTimeMillis(),
                    ))
                }
                VerificationResult.UNKNOWN -> {
                    failedCount++
                    itemResults.add(com.knoux.ai.database.OperationItemOutcomeEntity(
                        operationId = scope.pendingOperationId ?: "",
                        itemId = itemId,
                        result = "UNVERIFIED",
                        postQueryExists = false,
                        measuredBytes = 0L,
                        providerError = "Post-operation verification unavailable",
                        completedAt = System.currentTimeMillis(),
                    ))
                }
            }
        }

        val allVerified = failedCount == 0 && deletedCount > 0
        return Triple(totalFreed, allVerified, itemResults)
    }

    override fun onCleared() {
        scanJob?.cancel()
        cleanupJob?.cancel()
        super.onCleared()
    }
}

/** Reads device facts with optional MediaStore filtering. */
private fun readDeviceFactsFiltered(ctx: Context, includeMediaStore: Boolean): DeviceFacts {
    val facts = readDeviceFacts(ctx)
    if (!includeMediaStore) {
        return facts.copy(
            media = facts.media.copy(
                images = 0,
                videos = 0,
                imagesBytes = 0L,
                videosBytes = 0L,
                duplicateGroups = emptyList(),
                duplicateCandidates = 0,
                largestFiles = emptyList(),
                permissionGranted = false,
            )
        )
    }
    return facts
}

/** Checks if an item exists by ID. */
private fun checkItemExists(ctx: Context, itemId: String): Boolean {
    return when (itemId) {
        "knoux_own_cache" -> {
            val (bytes, _) = ownCacheBytes(ctx)
            bytes > 0
        }
        else -> {
            // For MediaStore items, would need URI-based check
            // Simplified for now
            true
        }
    }
}

/** Gets the size of an item by ID. */
private fun getItemSize(ctx: Context, itemId: String): Long {
    return when (itemId) {
        "knoux_own_cache" -> ownCacheBytes(ctx).first
        else -> 0L // Would need MediaStore query for actual items
    }
}

/** Deletes an item by ID. */
private fun deleteItemById(ctx: Context, itemId: String): Boolean {
    return when (itemId) {
        "knoux_own_cache" -> {
            val (before, _) = ownCacheBytes(ctx)
            val freed = clearOwnCache(ctx)
            val (after, _) = ownCacheBytes(ctx)
            after < before
        }
        else -> false // Would need MediaStore deletion for actual items
    }
}

/** Cleanup scope with pre-operation snapshot for verification. */
data class CleanupScope(
    val targetItemIds: List<String>,
    val preOpExistence: Map<String, Boolean>,
    val preOpSizes: Map<String, Long>,
    val pendingOperationId: String? = null,
)

/** Shared state for all residue screens. */
data class ResidueSharedState(
    val facts: DeviceFacts = DeviceFacts(),
    val isScanning: Boolean = false,
    val scanError: String? = null,
    val lastScanId: String? = null,
    val sessionsLogged: Int = 0,
    val itemsLogged: Int = 0,
    val operationsLogged: Int = 0,
    val capabilityStatus: String = "UNKNOWN",
    val progress: Float = 0f,
    val stageLabel: String = "",
    val includeMediaStore: Boolean = true,
    val safSourceUri: String? = null,
    val protectedItems: Set<String> = emptySet(),
    val selectedItems: Set<String> = emptySet(),
    val selectedBytes: Long = 0L,
    val pendingOperationId: String? = null,
    val pendingOperationAction: String? = null,
    val pendingOperationPredictedBytes: Long = 0L,
    val pendingOperationScope: CleanupScope? = null,
    val operationStatus: OperationStatus = OperationStatus.IDLE,
    val lastFreedBytes: Long? = null,
    val lastOperationResult: String? = null,
    val lastOperationVerified: Boolean = false,
) {
    val loaded: Boolean get() = facts.computedAt > 0L
    val hasPendingOperation: Boolean get() = pendingOperationId != null
    val canPrepareCleanup: Boolean get() = operationStatus == OperationStatus.IDLE ||
        operationStatus == OperationStatus.VERIFIED ||
        operationStatus == OperationStatus.PARTIAL ||
        operationStatus == OperationStatus.NO_CHANGE ||
        operationStatus == OperationStatus.FAILED ||
        operationStatus == OperationStatus.CANCELLED
}

/** Complete operation state machine with legal transitions. */
enum class OperationStatus {
    IDLE,
    PREPARING,
    AWAITING_CONSENT,
    EXECUTING,
    VERIFIED,
    PARTIAL,
    NO_CHANGE,
    FAILED,
    CANCELLED,
}

data class CleanupPreparationResult(
    val success: Boolean,
    val operationId: String? = null,
    val errorMessage: String? = null,
)

data class CleanupExecutionResult(
    val success: Boolean,
    val finalStatus: OperationStatus? = null,
    val errorMessage: String? = null,
)