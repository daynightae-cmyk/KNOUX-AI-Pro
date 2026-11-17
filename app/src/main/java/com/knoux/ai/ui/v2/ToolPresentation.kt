package com.knoux.ai.ui.v2

/** Presentation only: a missing observation must never look like a successful empty scan. */
internal enum class ToolContentState { LOADING, PERMISSION_REQUIRED, EMPTY, FILTERED_EMPTY, CONTENT }

internal fun toolContentState(
    loaded: Boolean,
    permissionGranted: Boolean = true,
    sourceCount: Int,
    visibleCount: Int = sourceCount,
): ToolContentState = when {
    !loaded -> ToolContentState.LOADING
    !permissionGranted -> ToolContentState.PERMISSION_REQUIRED
    sourceCount == 0 -> ToolContentState.EMPTY
    visibleCount == 0 -> ToolContentState.FILTERED_EMPTY
    else -> ToolContentState.CONTENT
}
