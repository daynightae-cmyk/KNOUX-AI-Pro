package com.knoux.ai.ui.v2

import org.junit.Assert.assertEquals
import org.junit.Test

class ToolPresentationTest {
    @Test fun loadingIsNotAnEmptyResultOrADeniedPermission() {
        assertEquals(ToolContentState.LOADING, toolContentState(false, false, 0))
    }

    @Test fun missingPermissionTakesPrecedenceOverAnEmptyLibrary() {
        assertEquals(ToolContentState.PERMISSION_REQUIRED, toolContentState(true, false, 0))
    }

    @Test fun readableEmptyLibraryIsAnEmptyResult() {
        assertEquals(ToolContentState.EMPTY, toolContentState(true, true, 0))
    }

    @Test fun filteredOutFilesDoNotBecomeAnEmptyLibrary() {
        assertEquals(ToolContentState.FILTERED_EMPTY, toolContentState(true, true, 12, 0))
    }

    @Test fun visibleObservationsShowContent() {
        assertEquals(ToolContentState.CONTENT, toolContentState(true, true, 12, 3))
    }
}
