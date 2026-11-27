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

/** Result of building a delete plan, including batch limit transparency. */
internal data class DuplicateDeletePlanResult(
    /** The plan for the first batch (up to batchLimit items). */
    val plan: DuplicateDeletePlan,
    /** Total number of selected items. */
    val totalSelected: Int,
    /** Number of items in this batch. */
    val batchSize: Int,
    /** Items that exceed the batch limit and require a subsequent operation. */
    val remainingUris: Set<String>,
    /** Whether the selection was truncated due to batch limit. */
    val wasTruncated: Boolean,
)

internal fun buildDuplicateDeletePlan(
    groups: List<DuplicateScanGroup>,
    selected: Set<String>,
    overrides: Map<String, String>,
    batchLimit: Int = 2000,
): DuplicateDeletePlanResult {
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
    val sortedFiles = selected.map { known.getValue(it) }.sortedBy { it.uriString }
    val wasTruncated = sortedFiles.size > batchLimit
    val batchFiles = sortedFiles.take(batchLimit)
    // Zero-byte files are valid and deletable (they free 0 bytes). Only reject
    // files with unknown size (sizeBytes < 0) which means the scan couldn't
    // determine their size. Unknown size != zero size.
    require(batchFiles.all { it.sizeBytes >= 0L }) { "تعذّر قياس بعض الملفات (حجم غير معروف)؛ أعد الفحص." }
    val remainingUris = if (wasTruncated) sortedFiles.drop(batchLimit).map { it.uriString }.toSet() else emptySet()
    return DuplicateDeletePlanResult(
        plan = DuplicateDeletePlan(batchFiles, keepers, touched),
        totalSelected = sortedFiles.size,
        batchSize = batchFiles.size,
        remainingUris = remainingUris,
        wasTruncated = wasTruncated,
    )
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
