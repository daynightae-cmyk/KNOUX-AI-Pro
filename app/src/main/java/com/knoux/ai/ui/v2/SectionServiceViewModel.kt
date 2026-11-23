package com.knoux.ai.ui.v2

import android.os.Build
import com.knoux.ai.database.KnouxRepository
import com.knoux.ai.database.ObservedItemEntity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * State exposed to a section tool screen.
 *
 * [facts] is always the real measured snapshot - it starts empty and is only
 * replaced by values read from the device. [sessionsLogged] / [itemsLogged]
 * are the truthful Room audit counters for this service.
 */
data class SectionUiState(
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
) {
    val loaded: Boolean get() = facts.computedAt > 0L
}

class SectionServiceViewModel(
    application: android.app.Application,
    repository: KnouxRepository,
    private val sectionKey: String,
    private val serviceName: String,
    private val requiresMedia: Boolean = true,
) : BaseServiceViewModel(application, repository) {

    private var _state by mutableStateOf(SectionUiState())
    val state: SectionUiState get() = _state

    private var lastTrigger: Int = Int.MIN_VALUE

    /** Reads the device once per [trigger] value and journals the result in Room. */
    fun scanSection(trigger: Int = 0) {
        if (trigger == lastTrigger && (_state.lastScanId != null || _state.isScanning)) return
        lastTrigger = trigger
        _state = _state.copy(
            isScanning = true,
            scanError = null,
            progress = 0.04f,
            stageLabel = "بدء الخدمة",
        )
        launchLatest {
            try {
                val ctx = appContext
                _state = _state.copy(progress = 0.14f, stageLabel = "قراءة التخزين والوسائط")
                val facts = withContext(Dispatchers.IO) { readDeviceFacts(ctx) }
                DeviceFactsCache.publish(facts)
                _state = _state.copy(progress = 0.58f, stageLabel = "تحليل التطبيقات والأذونات")

                val mediaBytes = facts.media.imagesBytes + facts.media.videosBytes
                val cacheBytes = facts.ownCacheBytes
                val otherBytes = (facts.storage.usedBytes - mediaBytes - cacheBytes).coerceAtLeast(0L)
                val permissionGranted = facts.media.permissionGranted
                val capability = when {
                    !facts.media.permissionGranted && requiresMedia -> "PERMISSION_REQUIRED"
                    !requiresMedia -> "AVAILABLE"
                    else -> "AVAILABLE"
                }

                val sessionId = withContext(Dispatchers.IO) {
                    _state = _state.copy(progress = 0.66f, stageLabel = "حفظ جلسة الفحص")
                    val id = repository.createScanSession(
                        serviceId = sectionKey,
                        serviceName = serviceName,
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
                        scope = sectionKey,
                    )

                    val mediaItems = facts.media.largestFiles.map { file ->
                        ObservedItemEntity(
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
                        ObservedItemEntity(
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
                        featureId = sectionKey,
                        serviceId = sectionKey,
                        capabilityStatus = capability,
                        apiSource = "MediaStore+PackageManager",
                        permissionRequired = if (requiresMedia) mediaPermissionName() else null,
                        reason = if (capability == "PERMISSION_REQUIRED") "منح صلاحية الوسائط مطلوب" else null,
                    )

                    id
                }

                val sessions = withContext(Dispatchers.IO) {
                    _state = _state.copy(progress = 0.92f, stageLabel = "تحديث سجل الخدمة")
                    repository.countSessions(sectionKey)
                }
                val items = withContext(Dispatchers.IO) {
                    repository.countItems(sessionId)
                }
                val operations = withContext(Dispatchers.IO) {
                    repository.countOperations(sectionKey)
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

    fun rescan() {
        scanSection(lastTrigger + 1)
    }

    private fun mediaPermissionName(): String =
        if (Build.VERSION.SDK_INT >= 33) "READ_MEDIA_IMAGES" else "READ_EXTERNAL_STORAGE"
}
