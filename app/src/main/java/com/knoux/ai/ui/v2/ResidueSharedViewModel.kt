package com.knoux.ai.ui.v2

import android.app.Application
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.knoux.ai.database.KnouxRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Shared ViewModel for all residue screens to maintain session continuity.
 * Provides a single source of truth for residue scan state, selections, and operations.
 */
class ResidueSharedViewModel(
    application: Application,
    repository: KnouxRepository
) : BaseServiceViewModel(application, repository) {

    private var _state by mutableStateOf(ResidueSharedState())
    val state: ResidueSharedState get() = _state

    /** Triggers a fresh device scan for residue. */
    fun scanResidue() {
        if (_state.isScanning) return
        _state = _state.copy(
            isScanning = true,
            scanError = null,
            progress = 0.04f,
            stageLabel = "بدء فحص البقايا",
        )
        launchLatest {
            try {
                val ctx = appContext
                _state = _state.copy(progress = 0.14f, stageLabel = "قراءة التخزين والوسائط")
                val facts = withContext(Dispatchers.IO) { readDeviceFacts(ctx) }
                DeviceFactsCache.publish(facts)

                _state = _state.copy(progress = 0.58f, stageLabel = "تحليل مصادر البقايا")

                val mediaBytes = facts.media.imagesBytes + facts.media.videosBytes
                val cacheBytes = facts.ownCacheBytes
                val otherBytes = (facts.storage.usedBytes - mediaBytes - cacheBytes).coerceAtLeast(0L)
                val permissionGranted = facts.media.permissionGranted
                val capability = when {
                    !facts.media.permissionGranted -> "PERMISSION_REQUIRED"
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
            } catch (e: Exception) {
                _state = _state.copy(
                    isScanning = false,
                    scanError = e.message ?: "تعذّر قراءة بيانات الجهاز",
                )
            }
        }
    }

    /** Updates the MediaStore inclusion preference. */
    fun setIncludeMediaStore(include: Boolean) {
        _state = _state.copy(includeMediaStore = include)
    }

    /** Updates the SAF source selection. */
    fun setSafSourceUri(uri: String?) {
        _state = _state.copy(safSourceUri = uri)
    }

    /** Adds a protected item that should never be deleted. */
    fun addProtectedItem(itemId: String) {
        _state = _state.copy(protectedItems = _state.protectedItems + itemId)
    }

    /** Removes a protected item. */
    fun removeProtectedItem(itemId: String) {
        _state = _state.copy(protectedItems = _state.protectedItems - itemId)
    }

    /** Toggles selection of a candidate item. */
    fun toggleCandidateSelection(itemId: String, sizeBytes: Long) {
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
        _state = _state.copy(
            selectedItems = emptySet(),
            selectedBytes = 0L,
        )
    }

    /** Prepares a cleanup operation using the existing safe infrastructure. */
    suspend fun prepareCleanup(): CleanupPreparationResult {
        val facts = _state.facts
        if (facts.computedAt == 0L) {
            return CleanupPreparationResult(success = false, errorMessage = "لا توجد بيانات فحص")
        }

        val before = ownCacheBytes(appContext).first
        if (before <= 0) {
            return CleanupPreparationResult(success = false, errorMessage = "كاش KNOUX فارغ")
        }

        val opId = withContext(Dispatchers.IO) {
            repository.createOperationRequest(
                scanSessionId = _state.lastScanId ?: "",
                serviceId = "app_residue",
                requestedAction = "clear_own_cache",
                selectionCount = 1,
                keeperIds = null,
                exclusionIds = null,
                predictedEligibleBytes = before,
            )
        }

        _state = _state.copy(
            pendingOperationId = opId,
            pendingOperationAction = "clear_own_cache",
            pendingOperationPredictedBytes = before,
            operationStatus = OperationStatus.AWAITING_CONSENT,
        )

        return CleanupPreparationResult(success = true, operationId = opId)
    }

    /** Executes the prepared cleanup with consent. */
    fun executeCleanup(consentGranted: Boolean): CleanupExecutionResult {
        val pendingOpId = _state.pendingOperationId
        if (pendingOpId == null) {
            return CleanupExecutionResult(success = false, errorMessage = "لا توجد عملية معدة")
        }

        if (!consentGranted) {
            launchLatest {
                repository.recordConsent(pendingOpId, granted = false)
            }
            _state = _state.copy(
                operationStatus = OperationStatus.CANCELLED,
                pendingOperationId = null,
                pendingOperationAction = null,
                pendingOperationPredictedBytes = 0L,
            )
            return CleanupExecutionResult(success = false, errorMessage = "تم إلغاء الإذن", finalStatus = OperationStatus.CANCELLED)
        }

        _state = _state.copy(operationStatus = OperationStatus.EXECUTING)
        launchLatest {
            try {
                repository.recordConsent(pendingOpId, granted = true)
                val before = ownCacheBytes(appContext).first
                val freed = clearOwnCache(appContext)
                val after = ownCacheBytes(appContext).first
                val verified = after < before

                val result = if (verified) "SUCCESS" else "NO_CHANGE"
                val finalStatus = if (verified) OperationStatus.VERIFIED else OperationStatus.PARTIAL

                withContext(Dispatchers.IO) {
                    repository.recordItemOutcomes(pendingOpId, listOf(
                        com.knoux.ai.database.OperationItemOutcomeEntity(
                            operationId = pendingOpId,
                            itemId = "knoux_own_cache",
                            result = result,
                            postQueryExists = after > 0L,
                            measuredBytes = freed,
                            providerError = null,
                            completedAt = System.currentTimeMillis(),
                        )
                    ), finalStatus = finalStatus.name)
                }

                _state = _state.copy(
                    operationStatus = finalStatus,
                    lastFreedBytes = freed,
                    lastOperationResult = result,
                    lastOperationVerified = verified,
                    pendingOperationId = null,
                    pendingOperationAction = null,
                    pendingOperationPredictedBytes = 0L,
                )
                scanResidue() // Refresh after cleanup
            } catch (e: Exception) {
                withContext(Dispatchers.IO) {
                    repository.recordItemOutcomes(pendingOpId, listOf(
                        com.knoux.ai.database.OperationItemOutcomeEntity(
                            operationId = pendingOpId,
                            itemId = "knoux_own_cache",
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
                )
            }
        }

        return CleanupExecutionResult(success = true, finalStatus = OperationStatus.EXECUTING)
    }
}

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
    val operationStatus: OperationStatus = OperationStatus.IDLE,
    val lastFreedBytes: Long? = null,
    val lastOperationResult: String? = null,
    val lastOperationVerified: Boolean = false,
) {
    val loaded: Boolean get() = facts.computedAt > 0L
    val hasPendingOperation: Boolean get() = pendingOperationId != null
}

enum class OperationStatus {
    IDLE,
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