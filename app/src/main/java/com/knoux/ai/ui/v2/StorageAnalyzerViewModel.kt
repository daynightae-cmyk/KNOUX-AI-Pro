package com.knoux.ai.ui.v2

import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.knoux.ai.database.KnouxRepository
import com.knoux.ai.database.OperationItemOutcomeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

/** Explicit gate state for an operation that touches storage. */
enum class OperationGate {
    IDLE,
    AWAITING_CONSENT,
    RUNNING,
    VERIFIED,
    PARTIAL,
    CANCELLED,
    ERROR,
}

data class StorageUiState(
    val facts: DeviceFacts = DeviceFacts(),
    val isScanning: Boolean = false,
    val scanError: String? = null,
    val lastScanId: String? = null,
    val gate: OperationGate = OperationGate.IDLE,
    val operationId: String? = null,
    val operationMessage: String? = null,
    val measuredBefore: Long = 0L,
    val measuredAfter: Long? = null,
) {
    val loaded: Boolean get() = facts.computedAt > 0L
}

/**
 * Storage Analyzer service.
 *
 * Clearing KNOUX's own cache is the one destructive action this service may
 * perform without an OS dialog, so it is still gated: measure -> ask the user
 * -> act -> re-measure -> journal. A result is only marked VERIFIED when the
 * post-measurement proves the bytes actually went down.
 */
class StorageAnalyzerViewModel(
    application: android.app.Application,
    repository: KnouxRepository,
) : BaseServiceViewModel(application, repository) {

    private var _state by mutableStateOf(StorageUiState())
    val state: StorageUiState get() = _state

    private var lastTrigger: Int = Int.MIN_VALUE

    fun scanStorage(trigger: Int = 0) {
        if (_state.gate == OperationGate.RUNNING || _state.gate == OperationGate.AWAITING_CONSENT) return
        if (trigger == lastTrigger && (_state.lastScanId != null || _state.isScanning)) return
        lastTrigger = trigger
        _state = _state.copy(isScanning = true, scanError = null)
        launchLatest {
            var sessionId: String? = null
            try {
                val ctx = appContext
                val facts = withContext(Dispatchers.IO) { readDeviceFacts(ctx) }
                DeviceFactsCache.publish(facts)

                val mediaBytes = facts.media.imagesBytes + facts.media.videosBytes
                val cacheBytes = facts.ownCacheBytes
                val otherBytes = (facts.storage.usedBytes - mediaBytes - cacheBytes).coerceAtLeast(0L)

                // Only create scan session after successful fact reading
                sessionId = withContext(Dispatchers.IO) {
                    val id = repository.createScanSession(
                        serviceId = SERVICE_ID,
                        serviceName = "Storage Analyzer",
                        deviceModel = Build.MODEL,
                        apiLevel = Build.VERSION.SDK_INT,
                    )

                    repository.recordStorageSnapshot(
                        id = UUID.randomUUID().toString(),
                        totalBytes = facts.storage.totalBytes,
                        usedBytes = facts.storage.usedBytes,
                        freeBytes = facts.storage.freeBytes,
                        mediaBytes = mediaBytes,
                        cacheBytes = cacheBytes,
                        otherBytes = otherBytes,
                        scope = "device",
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
                    repository.completeScanSession(
                        sessionId = id,
                        status = if (facts.computedAt > 0L && facts.media.readSucceeded) "COMPLETED" else "PARTIAL",
                        permissionGranted = facts.media.permissionGranted,
                        itemsFound = facts.media.images + facts.media.videos,
                        bytesFound = mediaBytes,
                        bytesReclaimable = cacheBytes,
                        items = mediaItems,
                    )

                    repository.recordFeatureCapability(
                        featureId = SERVICE_ID,
                        serviceId = SERVICE_ID,
                        capabilityStatus = "AVAILABLE",
                        apiSource = "StorageManager+MediaStore",
                        permissionRequired = null,
                        reason = null,
                    )
                    id
                }

                _state = _state.copy(
                    facts = facts,
                    isScanning = false,
                    scanError = null,
                    lastScanId = sessionId,
                    measuredBefore = facts.ownCacheBytes,
                    gate = if (_state.gate == OperationGate.RUNNING) _state.gate else OperationGate.IDLE,
                )
            } catch (e: Exception) {
                // If session was created but completion failed, mark it as FAILED
                sessionId?.let { id ->
                    withContext(Dispatchers.IO) {
                        repository.completeScanSession(id, "FAILED", false, 0, 0L, 0L)
                    }
                }
                _state = _state.copy(
                    isScanning = false,
                    scanError = e.message ?: "تعذّر قراءة التخزين",
                )
            }
        }
    }

    /** Explicit rescan triggered by the shared K run orb. */
    fun rescan() = scanStorage(lastTrigger + 1)

    /** Step 1 - records the intent and waits for the user's confirmation. */
    fun prepareClearOwnCache() {
        if (_state.gate == OperationGate.RUNNING || _state.gate == OperationGate.AWAITING_CONSENT) return
        val sessionId = _state.lastScanId
        if (sessionId == null) {
            _state = _state.copy(
                scanError = "لازم قراءة التخزين الأول قبل أي عملية",
                operationMessage = "لا توجد جلسة قراءة محفوظة",
            )
            return
        }
        _state = _state.copy(gate = OperationGate.RUNNING, operationMessage = "جاري تسجيل الطلب")
        launchLatest {
            try {
                val before = withContext(Dispatchers.IO) { ownCacheBytes(appContext).first }
                val opId = withContext(Dispatchers.IO) {
                    repository.createOperationRequest(
                        scanSessionId = sessionId,
                        serviceId = SERVICE_ID,
                        requestedAction = "clear_own_cache",
                        selectionCount = 1,
                        keeperIds = null,
                        exclusionIds = null,
                        predictedEligibleBytes = before,
                    )
                }
                _state = _state.copy(
                    gate = OperationGate.AWAITING_CONSENT,
                    operationId = opId,
                    measuredBefore = before,
                    operationMessage = "محتاج تأكيدك قبل المسح",
                )
            } catch (e: Exception) {
                _state = _state.copy(
                    gate = OperationGate.ERROR,
                    operationMessage = e.message ?: "فشل تسجيل الطلب",
                )
            }
        }
    }

    /** Step 2 - the user confirmed; run the real action and re-measure. */
    fun confirmClearOwnCache() {
        val opId = _state.operationId ?: return
        if (_state.gate != OperationGate.AWAITING_CONSENT) return
        _state = _state.copy(gate = OperationGate.RUNNING, operationMessage = "جاري مسح كاش KNOUX")
        launchLatest {
            try {
                val ctx = appContext
                withContext(Dispatchers.IO) { repository.recordConsent(opId, granted = true) }

                // Re-measure immediately before execution to account for concurrent changes.
                // The stored measuredBefore is the preparation baseline; we measure again
                // right before clearing to show the user the actual current value.
                val before = withContext(Dispatchers.IO) { ownCacheBytes(ctx).first }
                val freed = withContext(Dispatchers.IO) { clearOwnCache(ctx) }
                val after = withContext(Dispatchers.IO) { ownCacheBytes(ctx).first }

                val verified = after < before
                val status = if (verified) "VERIFIED" else "PARTIAL"

                withContext(Dispatchers.IO) {
                    repository.recordItemOutcomes(
                        opId,
                        listOf(
                            OperationItemOutcomeEntity(
                                operationId = opId,
                                itemId = "knoux_own_cache",
                                result = if (verified) "SUCCESS" else "NO_CHANGE",
                                postQueryExists = after > 0L,
                                measuredBytes = freed,
                                providerError = null,
                                completedAt = System.currentTimeMillis(),
                            )
                        ),
                        finalStatus = status,
                    )
                }

                val refreshed = withContext(Dispatchers.IO) { readDeviceFacts(ctx) }
                DeviceFactsCache.publish(refreshed)

                _state = _state.copy(
                    facts = refreshed,
                    gate = if (verified) OperationGate.VERIFIED else OperationGate.PARTIAL,
                    operationMessage = when {
                        verified -> "تم المسح والتحقق: ${formatBytes(freed)}"
                        freed > 0L -> "تم المسح جزئياً: ${formatBytes(freed)}"
                        else -> "مفيش كاش قابل للمسح"
                    },
                    measuredAfter = after,
                    measuredBefore = before, // Update with actual pre-execution measurement
                )
            } catch (e: Exception) {
                withContext(Dispatchers.IO) {
                    repository.updateOperationStatus(opId, "ERROR")
                }
                _state = _state.copy(
                    gate = OperationGate.ERROR,
                    operationMessage = e.message ?: "فشل مسح الكاش",
                )
            }
        }
    }

    fun cancelClearOwnCache() {
        val opId = _state.operationId ?: return
        // Do not cancel if operation is already executing or has completed -
        // the destructive action has begun or finished and cannot be safely undone.
        // Only allow cancellation when awaiting user consent (AWAITING_CONSENT).
        if (_state.gate != OperationGate.AWAITING_CONSENT) return
        _state = _state.copy(gate = OperationGate.CANCELLED, operationMessage = "تم الإلغاء بدون أي تغيير")
        launchLatest {
            withContext(Dispatchers.IO) {
                repository.recordConsent(opId, granted = false)
            }
        }
    }

    companion object {
        const val SERVICE_ID = "storage_analyzer"
    }
}
