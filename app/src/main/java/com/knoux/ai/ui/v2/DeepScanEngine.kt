package com.knoux.ai.ui.v2

import android.content.Context
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.knoux.ai.database.AppDatabase
import com.knoux.ai.database.KnouxRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/* ------------------------------------------------------------------ *
 * Deep Scan — staged, cancellable read of this device.
 *
 * Every stage below performs the real Android read it names. The progress
 * the cover shows is "completed stages", not a timer: the sonar turns only
 * while a stage is actually being read, freezes on cancel and on the result.
 * A cancelled run never writes a history entry and never claims the stages
 * that were not measured.
 * ------------------------------------------------------------------ */

internal enum class DeepScanStage(val label: String) {
    BATTERY("البطارية"),
    STORAGE("التخزين"),
    APPS("التطبيقات"),
    PRIVACY("الخصوصية"),
    CACHE("الكاش"),
    MEDIA("الوسائط"),
}

internal sealed interface DeepScanUiState {
    data class Idle(val facts: DeviceFacts = DeviceFacts()) : DeepScanUiState

    data class Running(
        val completed: Int,
        val stage: DeepScanStage,
        val facts: DeviceFacts,
    ) : DeepScanUiState

    data class Completed(
        val facts: DeviceFacts,
        val completedAt: Long,
    ) : DeepScanUiState

    data class Cancelled(
        val completed: Int,
        val facts: DeviceFacts,
    ) : DeepScanUiState

    data class Failed(
        val completed: Int,
        val facts: DeviceFacts,
        val reason: String,
    ) : DeepScanUiState
}

/** Stages actually completed in the current state. */
internal fun DeepScanUiState.completedStages(): Int = when (this) {
    is DeepScanUiState.Idle -> 0
    is DeepScanUiState.Running -> completed
    is DeepScanUiState.Completed -> DeepScanStage.values().size
    is DeepScanUiState.Cancelled -> completed
    is DeepScanUiState.Failed -> completed
}

internal fun DeepScanUiState.isRunning(): Boolean = this is DeepScanUiState.Running

internal class DeepScanViewModel : ViewModel() {
    var state: DeepScanUiState by mutableStateOf(DeepScanUiState.Idle())
        private set

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var job: Job? = null

    fun start(context: Context) {
        if (state.isRunning()) return
        job?.cancel()
        val appContext = context.applicationContext
        state = DeepScanUiState.Running(
            completed = 0,
            stage = DeepScanStage.BATTERY,
            facts = DeviceFacts(),
        )
        job = scope.launch {
            // A completed deep scan is a real Room session, so the summary and
            // activity log can show it instead of claiming no session exists.
            val repository = runCatching {
                KnouxRepository(AppDatabase.getDatabase(appContext))
            }.getOrNull()
            val sessionId = runCatching {
                repository?.createScanSession(
                    serviceId = "deep_scan",
                    serviceName = "الفحص العميق",
                    deviceModel = Build.MODEL,
                    apiLevel = Build.VERSION.SDK_INT,
                )
            }.getOrNull()
            var facts = DeviceFacts()
            var completed = 0
            try {
                for (stage in DeepScanStage.values()) {
                    val next = readStage(appContext, stage, facts)
                    ensureActive()
                    facts = next
                    completed++
                    if (completed < DeepScanStage.values().size) {
                        state = DeepScanUiState.Running(
                            completed = completed,
                            stage = DeepScanStage.values()[completed],
                            facts = facts,
                        )
                    }
                }
                val finished = facts.copy(computedAt = System.currentTimeMillis())
                DeviceFactsCache.publish(finished)
                runCatching {
                    saveScanHistory(
                        appContext,
                        ScanHistoryEntry(
                            timestamp = System.currentTimeMillis(),
                            appsScanned = finished.installedCount,
                            privacyFindings = finished.sensitiveAppCount,
                            cacheBytes = finished.ownCacheBytes,
                            storageUsedBytes = finished.storage.usedBytes,
                            batteryLevel = finished.battery.levelPercent,
                        ),
                    )
                }
                if (sessionId != null) {
                    runCatching {
                        repository?.completeScanSession(
                            sessionId = sessionId,
                            status = "COMPLETED",
                            permissionGranted = finished.media.permissionGranted,
                            itemsFound = finished.installedCount,
                            bytesFound = finished.storage.usedBytes,
                            bytesReclaimable = 0L,
                        )
                    }
                }
                state = DeepScanUiState.Completed(
                    facts = finished,
                    completedAt = System.currentTimeMillis(),
                )
            } catch (cancelled: CancellationException) {
                if (sessionId != null) {
                    runCatching {
                        repository?.completeScanSession(
                            sessionId = sessionId,
                            status = "CANCELLED",
                            permissionGranted = facts.media.permissionGranted,
                            itemsFound = 0,
                            bytesFound = 0L,
                            bytesReclaimable = 0L,
                        )
                    }
                }
                state = DeepScanUiState.Cancelled(completed = completed, facts = facts)
                throw cancelled
            } catch (error: Throwable) {
                if (sessionId != null) {
                    runCatching {
                        repository?.completeScanSession(
                            sessionId = sessionId,
                            status = "FAILED",
                            permissionGranted = facts.media.permissionGranted,
                            itemsFound = 0,
                            bytesFound = 0L,
                            bytesReclaimable = 0L,
                        )
                    }
                }
                state = DeepScanUiState.Failed(
                    completed = completed,
                    facts = facts,
                    reason = error.message?.takeIf { it.isNotBlank() } ?: "تعذّر إكمال قراءة الجهاز",
                )
            }
        }
    }

    fun cancel() {
        if (!state.isRunning()) return
        job?.cancel()
        job = null
    }

    private suspend fun readStage(
        context: Context,
        stage: DeepScanStage,
        facts: DeviceFacts,
    ): DeviceFacts = withContext(Dispatchers.IO) {
        when (stage) {
            DeepScanStage.BATTERY -> facts.copy(
                battery = DeviceReaders.battery(context),
            )
            DeepScanStage.STORAGE -> facts.copy(
                storage = DeviceReaders.storage(),
            )
            DeepScanStage.APPS -> {
                val apps = DeviceReaders.installedApps(context)
                facts.copy(
                    installedApps = apps,
                    sensitiveAppCount = apps.count { it.sensitivePermissions.isNotEmpty() },
                )
            }
            DeepScanStage.PRIVACY -> facts.copy(
                privacyGroups = DeviceReaders.privacyGroups(context, facts.installedApps),
            )
            DeepScanStage.CACHE -> {
                val (bytes, files) = DeviceReaders.ownCache(context)
                facts.copy(ownCacheBytes = bytes, ownCacheFiles = files)
            }
            DeepScanStage.MEDIA -> facts.copy(
                media = DeviceReaders.media(context),
                usageAccessGranted = DeviceReaders.usageAccess(context),
            )
        }
    }

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}
