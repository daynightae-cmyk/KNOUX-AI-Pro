package com.knoux.ai.ui.v2

import android.content.Context
import android.content.IntentSender
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knoux.ai.database.AppDatabase
import com.knoux.ai.database.KnouxRepository
import com.knoux.ai.database.ObservedItemEntity
import com.knoux.ai.database.OperationItemOutcomeEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import kotlin.coroutines.coroutineContext

internal data class DuplicateWorkspaceState(
    val restoring: Boolean = true,
    val mode: DuplicateScanMode = DuplicateScanMode.EXACT,
    val scanning: Boolean = false,
    val scanCancelled: Boolean = false,
    val progress: DuplicateScanProgress? = null,
    val result: DuplicateScanResult? = null,
    val sessionId: String? = null,
    val operating: Boolean = false,
    val awaitingSystem: Boolean = false,
    val message: String? = null,
    val error: String? = null,
    val deletion: DuplicateDeleteVerification? = null,
) {
    val busy: Boolean get() = restoring || scanning || operating || awaitingSystem
}

private data class PendingDuplicateDeletion(
    val operationId: String,
    val plan: DuplicateDeletePlan,
    val consented: Boolean? = null,
)

/** Owns the real scan and the measure -> consent -> query -> journal lifecycle. */
internal class DuplicateWorkspaceViewModel : ViewModel() {
    private val mutableState = MutableStateFlow(DuplicateWorkspaceState())
    val state = mutableState.asStateFlow()
    private var scanJob: Job? = null
    private var operationJob: Job? = null
    private var pending: PendingDuplicateDeletion? = null
    private var attached = false

    fun attach(context: Context) {
        if (attached) return
        attached = true
        val app = context.applicationContext
        viewModelScope.launch {
            try {
                pending = withContext(Dispatchers.IO) { readPending(app) }
                mutableState.update { it.copy(restoring = false, awaitingSystem = pending != null) }
            } catch (_: Exception) {
                mutableState.update { it.copy(restoring = false, error = "تعذّر استعادة سجل الطلب السابق. لم تُنفّذ أي عملية جديدة.") }
            }
        }
    }

    fun start(context: Context, mode: DuplicateScanMode) {
        if (state.value.busy) return
        val app = context.applicationContext
        mutableState.update {
            it.copy(mode = mode, scanning = true, scanCancelled = false, result = null,
                progress = null, message = null, error = null, sessionId = null)
        }
        scanJob = viewModelScope.launch {
            val repository = repository(app)
            var session: String? = null
            try {
                val sessionId = withContext(Dispatchers.IO) {
                    repository.createScanSession(
                        serviceId = SERVICE_ID,
                        serviceName = when (mode) {
                            DuplicateScanMode.EXACT -> "Exact duplicate scan"
                            DuplicateScanMode.VISUAL -> "Visual similarity review"
                            DuplicateScanMode.NAME -> "File name duplicate scan"
                        },
                        deviceModel = android.os.Build.MODEL,
                        apiLevel = android.os.Build.VERSION.SDK_INT,
                    )
                }
                session = sessionId
                val result = scanDuplicates(app, mode) { progress ->
                    mutableState.update { it.copy(progress = progress) }
                }
                coroutineContext.ensureActive()
                withContext(Dispatchers.IO) {
                    val observed = result.groups.flatMap { group ->
                        group.files.mapIndexed { index, file ->
                            ObservedItemEntity(
                                scanSessionId = sessionId,
                                providerId = file.uriString,
                                uri = file.uriString,
                                name = file.name,
                                sizeBytes = file.sizeBytes,
                                modifiedAt = file.modifiedAt,
                                bucket = file.bucket,
                                isVideo = file.isVideo,
                                isDuplicate = group.kind == DuplicateMatchKind.EXACT,
                                duplicateGroupId = group.id,
                                isKeeper = index == 0,
                                scope = "authorized_media",
                                lastSeen = result.completedAt,
                            )
                        }
                    }.distinctBy { it.providerId }
                    repository.completeScanSession(
                        sessionId = sessionId,
                        status = if (result.warnings.isEmpty() && !result.partialAccess) "COMPLETED" else "PARTIAL",
                        permissionGranted = result.filesDiscovered > 0 || result.warnings.isEmpty(),
                        itemsFound = result.filesDiscovered,
                        bytesFound = observed.sumOf { it.sizeBytes ?: 0L },
                        bytesReclaimable = result.groups.filter { it.kind == DuplicateMatchKind.EXACT }
                            .sumOf { group -> group.files.drop(1).sumOf { it.sizeBytes } },
                        items = observed,
                    )
                }
                mutableState.update { it.copy(scanning = false, result = result, sessionId = session) }
            } catch (cancelled: CancellationException) {
                withContext(NonCancellable + Dispatchers.IO) {
                    session?.let { repository.completeScanSession(it, "CANCELLED", false, 0, 0L, 0L) }
                }
                mutableState.update { it.copy(scanning = false, scanCancelled = true, message = "اتوقف الفحص. لم يُحذف أي ملف، والنتيجة غير المكتملة ليست نتيجة نظافة.") }
                throw cancelled
            } catch (_: Exception) {
                withContext(Dispatchers.IO) {
                    session?.let { repository.completeScanSession(it, "FAILED", false, 0, 0L, 0L) }
                }
                mutableState.update { it.copy(scanning = false, error = "تعذّر إكمال الفحص أو حفظ نتيجته. راجع الصلاحيات ثم أعد المحاولة؛ لم يُحذف أي ملف.") }
            }
        }
    }

    fun cancelScan() { scanJob?.cancel() }

    /**
     * Mode selection is not a run command. Switching mode drops a result from
     * the previous mode so it can never be mistaken for a scan of the new one;
     * the user still has to press the K orb.
     */
    fun selectMode(mode: DuplicateScanMode) {
        if (state.value.busy || state.value.mode == mode) return
        mutableState.update {
            it.copy(mode = mode, result = null, sessionId = null, deletion = null,
                scanCancelled = false, progress = null, message = null, error = null)
        }
    }

    fun prepareDelete(
        context: Context,
        selected: Set<String>,
        keeperOverrides: Map<String, String>,
        onReady: (IntentSender) -> Unit,
    ) {
        if (state.value.busy) return
        val snapshot = state.value.result ?: return
        val sessionId = state.value.sessionId ?: return
        val result = try {
            buildDuplicateDeletePlan(snapshot.groups, selected, keeperOverrides)
        } catch (error: IllegalArgumentException) {
            mutableState.update { it.copy(error = error.message) }
            return
        }
        val plan = result.plan
        // Warn if selection was truncated due to batch limit
        if (result.wasTruncated) {
            mutableState.update { it.copy(message = "تم تحديد ${result.totalSelected} نسخة؛ ستُحذف أول ${result.batchSize} في هذه الدفعة. الباقي (${result.remainingUris.size}) يفضل للدفعة التالية.") }
        }
        val app = context.applicationContext
        mutableState.update { it.copy(operating = true, deletion = null, error = null) }
        operationJob = viewModelScope.launch {
            var operationId: String? = null
            try {
                withContext(Dispatchers.IO) { validateBeforeConsent(app, plan) }
                val request = mediaDeleteRequest(app, plan.files.map { it.uriString })
                    ?: throw IllegalStateException("حوار الحذف غير متاح لهذا النطاق. تحتاج Android 11 أو أحدث وصلاحية الوسائط.")
                val repository = repository(app)
                val opId = withContext(Dispatchers.IO) {
                    repository.createOperationRequest(
                        scanSessionId = sessionId,
                        serviceId = SERVICE_ID,
                        requestedAction = when {
                            plan.groups.any { it.kind == DuplicateMatchKind.VISUAL } -> "delete_reviewed_similar_media"
                            plan.groups.any { it.kind == DuplicateMatchKind.NAME } -> "delete_name_matched_media"
                            else -> "delete_exact_duplicate_media"
                        },
                        selectionCount = plan.files.size,
                        keeperIds = plan.keepers.map { it.uriString },
                        exclusionIds = null,
                        predictedEligibleBytes = plan.bytes,
                    )
                }
                operationId = opId
                val requestState = PendingDuplicateDeletion(opId, plan)
                withContext(Dispatchers.IO) { savePending(app, requestState) }
                pending = requestState
                // The Android callback is the only source of consent. Do not
                // record consent just because the user opened the dialog.
                mutableState.update { it.copy(operating = false, awaitingSystem = true, message = "القرار الآن في نافذة Android. لن نعلن النجاح قبل إعادة الاستعلام.") }
                onReady(request)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                withContext(Dispatchers.IO) {
                    operationId?.let { repository(app).updateOperationStatus(it, "ERROR") }
                    if (pending?.operationId == operationId) { clearPending(app); pending = null }
                }
                mutableState.update { it.copy(operating = false, awaitingSystem = false, error = error.message ?: "تعذّر تجهيز الحذف. لم يبدأ طلب النظام.", message = null) }
            }
        }
    }

    fun finishDelete(context: Context, consented: Boolean) {
        reconcile(context, consented)
    }

    /** A restored dialog without a returned result must not invent consent. */
    fun recoverInterrupted(context: Context) {
        reconcile(context, pending?.consented)
    }

    private fun reconcile(context: Context, consent: Boolean?) {
        if (operationJob?.isActive == true) return
        val app = context.applicationContext
        mutableState.update { it.copy(operating = true, awaitingSystem = false, error = null, message = "بنعيد استعلام الملفات والنسخ المحفوظة ونحفظ النتيجة…") }
        operationJob = viewModelScope.launch {
            try {
                val request = pending ?: withContext(Dispatchers.IO) { readPending(app) }
                    ?: throw IllegalStateException("لا يوجد طلب حذف محفوظ لإعادة التحقق.")
                val decided = consent ?: request.consented
                val withDecision = request.copy(consented = decided)
                pending = withDecision
                val verification = withContext(Dispatchers.IO) {
                    savePending(app, withDecision)
                    val observations = (request.plan.files + request.plan.keepers)
                        .distinctBy { it.uriString }.associate { it.uriString to queryPresence(app, it.uriString) }
                    val verified = verifyDuplicateDelete(request.plan, decided == true, observations)
                    val repository = repository(app)
                    if (decided != null) repository.recordConsent(request.operationId, decided)
                    val status = if (decided == null) "INTERRUPTED" else verified.status
                    repository.recordItemOutcomes(request.operationId, request.plan.files.map { file ->
                        val presence = observations.getValue(file.uriString)
                        OperationItemOutcomeEntity(
                            operationId = request.operationId,
                            itemId = file.uriString,
                            result = when {
                                decided == null -> "UNVERIFIED"
                                !decided -> "CANCELLED"
                                presence == DuplicateMediaPresence.ABSENT -> "SUCCESS"
                                presence == DuplicateMediaPresence.PRESENT -> "NO_CHANGE"
                                else -> "UNVERIFIED"
                            },
                            postQueryExists = presence != DuplicateMediaPresence.ABSENT,
                            measuredBytes = if (decided == true && presence == DuplicateMediaPresence.ABSENT) file.sizeBytes else 0L,
                            providerError = if (presence == DuplicateMediaPresence.UNKNOWN) "Provider re-query unavailable; absence not proven" else null,
                            completedAt = System.currentTimeMillis(),
                        )
                    }, finalStatus = status)
                    clearPending(app)
                    verified.copy(status = status)
                }
                pending = null
                val newResult = state.value.result?.let { result ->
                    result.copy(groups = result.groups.mapNotNull { group ->
                        val remaining = group.files.filterNot { it.uriString in verification.deletedUris }
                        if (remaining.size < 2) null else group.copy(files = remaining)
                    })
                }
                mutableState.update {
                    it.copy(operating = false, awaitingSystem = false, result = newResult,
                        deletion = verification, message = null)
                }
            } catch (_: Exception) {
                mutableState.update { it.copy(operating = false, awaitingSystem = pending != null,
                    error = "تعذّر تأكيد النتيجة أو حفظ السجل. الطلب محفوظ؛ أعد التحقق بدل تكرار الحذف.", message = null) }
            }
        }
    }

    private suspend fun validateBeforeConsent(context: Context, plan: DuplicateDeletePlan) {
        for (file in (plan.files + plan.keepers).distinctBy { it.uriString }) {
            coroutineContext.ensureActive()
            require(queryPresence(context, file.uriString) == DuplicateMediaPresence.PRESENT) {
                "ملف أو نسخة محفوظة غير متاحة الآن. أعد الفحص؛ لم يُفتح طلب الحذف."
            }
            require(querySize(context, file.uriString) == file.sizeBytes) {
                "تغيّر حجم أحد الملفات بعد الفحص. أعد الفحص قبل الحذف."
            }
        }
        val selectedUris = plan.files.map { it.uriString }.toSet()
        for (group in plan.groups.filter { it.kind == DuplicateMatchKind.EXACT }) {
            val keeper = plan.keepers.firstOrNull { saved -> group.files.any { it.uriString == saved.uriString } }
                ?: throw IllegalStateException("لا توجد نسخة محفوظة صالحة للمجموعة.")
            val digest = digestOf(context, keeper) ?: throw IllegalStateException("تعذّر إعادة قراءة النسخة المحفوظة.")
            for (file in group.files.filter { it.uriString in selectedUris }) {
                require(digestOf(context, file) == digest) {
                    "تغيّر محتوى إحدى النسخ. اتلغى تجهيز الحذف للحفاظ على ملفاتك؛ أعد الفحص."
                }
            }
        }
    }

    private suspend fun digestOf(context: Context, file: DuplicateScanFile): String? =
        context.contentResolver.openInputStream(Uri.parse(file.uriString))?.use {
            duplicateContentDigest(it, file.sizeBytes)
        }

    private fun repository(context: Context) = KnouxRepository(AppDatabase.getDatabase(context))

    private fun queryPresence(context: Context, uri: String): DuplicateMediaPresence = try {
        context.contentResolver.query(Uri.parse(uri), arrayOf(MediaStore.MediaColumns._ID), null, null, null)
            ?.use { if (it.moveToFirst()) DuplicateMediaPresence.PRESENT else DuplicateMediaPresence.ABSENT }
            ?: DuplicateMediaPresence.UNKNOWN
    } catch (_: Exception) { DuplicateMediaPresence.UNKNOWN }

    private fun querySize(context: Context, uri: String): Long? = try {
        context.contentResolver.query(Uri.parse(uri), arrayOf(MediaStore.MediaColumns.SIZE), null, null, null)
            ?.use { if (it.moveToFirst() && !it.isNull(0)) it.getLong(0) else null }
    } catch (_: Exception) { null }

    // The small lifecycle token lives outside the instance-state Bundle. File
    // metadata is private app storage, never logs, screenshots, or cloud input.
    private fun savePending(context: Context, request: PendingDuplicateDeletion) {
        fun files(entries: List<DuplicateScanFile>) = JSONArray().apply {
            entries.forEach { file -> put(JSONObject().put("uri", file.uriString).put("bytes", file.sizeBytes).put("video", file.isVideo)) }
        }
        val json = JSONObject().put("id", request.operationId)
            .put("files", files(request.plan.files)).put("keepers", files(request.plan.keepers))
        request.consented?.let { json.put("consent", it) }
        check(context.getSharedPreferences(PENDING_PREFS, Context.MODE_PRIVATE).edit().putString("request", json.toString()).commit())
    }

    private fun readPending(context: Context): PendingDuplicateDeletion? {
        val text = context.getSharedPreferences(PENDING_PREFS, Context.MODE_PRIVATE).getString("request", null) ?: return null
        val json = JSONObject(text)
        fun files(key: String): List<DuplicateScanFile> {
            val array = json.getJSONArray(key)
            return (0 until array.length()).map { index ->
                val file = array.getJSONObject(index)
                DuplicateScanFile(file.getString("uri"), "ملف", file.getLong("bytes"), file.getBoolean("video"), null)
            }
        }
        return PendingDuplicateDeletion(
            json.getString("id"), DuplicateDeletePlan(files("files"), files("keepers"), emptyList()),
            if (json.has("consent")) json.getBoolean("consent") else null,
        )
    }

    private fun clearPending(context: Context) {
        check(context.getSharedPreferences(PENDING_PREFS, Context.MODE_PRIVATE).edit().remove("request").commit())
    }

    companion object {
        private const val SERVICE_ID = "duplicate_finder"
        private const val PENDING_PREFS = "knoux_duplicate_pending_v1"
    }
}
