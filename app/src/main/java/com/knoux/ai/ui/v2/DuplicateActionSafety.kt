package com.knoux.ai.ui.v2

/** Provider failures are not proof that a file disappeared. */
internal enum class DuplicateMediaPresence { PRESENT, ABSENT, UNKNOWN }

internal data class DuplicateDeletePlan(
    val files: List<DuplicateScanFile>,
    val keepers: List<DuplicateScanFile>,
    val groups: List<DuplicateScanGroup>,
) {
    val bytes: Long get() = files.sumOf { it.sizeBytes }
}

internal fun duplicateKeeper(group: DuplicateScanGroup, overrides: Map<String, String>): String =
    overrides[group.id]?.takeIf { uri -> group.files.any { it.uriString == uri } }
        ?: group.files.first().uriString

/** A visual comparison can never participate in automatic bulk selection. */
internal fun exactDuplicateExtras(
    groups: List<DuplicateScanGroup>,
    overrides: Map<String, String>,
): Set<String> {
    val protected = groups.map { duplicateKeeper(it, overrides) }.toSet()
    return groups.filter { it.kind == DuplicateMatchKind.EXACT }
        .flatMap { group -> group.files.map { it.uriString } }
        .filterNot { it in protected }.toSet()
}

internal fun buildDuplicateDeletePlan(
    groups: List<DuplicateScanGroup>,
    selected: Set<String>,
    overrides: Map<String, String>,
    batchLimit: Int = 2000,
): DuplicateDeletePlan {
    require(selected.isNotEmpty()) { "حدّد النسخ التي تريد حذفها أولًا." }
    require(batchLimit > 0)
    val touched = groups.filter { group -> group.files.any { it.uriString in selected } }
    val keepers = touched.map { group ->
        val uri = duplicateKeeper(group, overrides)
        group.files.first { it.uriString == uri }
    }.distinctBy { it.uriString }
    require(keepers.none { it.uriString in selected }) { "لا يمكن حذف النسخة المحفوظة من المجموعة." }
    val known = touched.flatMap { it.files }.associateBy { it.uriString }
    require(selected.all { it in known }) { "التحديد قديم؛ أعد الفحص قبل الحذف." }
    val files = selected.map { known.getValue(it) }.sortedBy { it.uriString }.take(batchLimit)
    require(files.all { it.sizeBytes > 0L }) { "تعذّر قياس بعض الملفات؛ أعد الفحص." }
    return DuplicateDeletePlan(files, keepers, touched)
}

internal data class DuplicateDeleteVerification(
    val status: String,
    val deletedUris: Set<String>,
    val deletedBytes: Long,
    val unknownCount: Int,
    val keepersIntact: Boolean,
)

internal fun verifyDuplicateDelete(
    plan: DuplicateDeletePlan,
    consented: Boolean,
    observations: Map<String, DuplicateMediaPresence>,
): DuplicateDeleteVerification {
    val keepersIntact = plan.keepers.all {
        observations[it.uriString] == DuplicateMediaPresence.PRESENT
    }
    val unknown = (plan.files + plan.keepers).distinctBy { it.uriString }.count {
        observations[it.uriString] == null || observations[it.uriString] == DuplicateMediaPresence.UNKNOWN
    }
    // Cancellation is never upgraded to a successful deletion, even if an
    // unrelated process removed the URI while the system dialog was visible.
    val deleted = if (consented) plan.files.filter {
        observations[it.uriString] == DuplicateMediaPresence.ABSENT
    } else emptyList()
    val status = when {
        !consented -> "CANCELLED"
        deleted.size == plan.files.size && unknown == 0 && keepersIntact -> "VERIFIED"
        deleted.isEmpty() && unknown == 0 && keepersIntact -> "NO_CHANGE"
        else -> "PARTIAL"
    }
    return DuplicateDeleteVerification(
        status = status,
        deletedUris = deleted.map { it.uriString }.toSet(),
        deletedBytes = deleted.sumOf { it.sizeBytes },
        unknownCount = unknown,
        keepersIntact = keepersIntact,
    )
}
