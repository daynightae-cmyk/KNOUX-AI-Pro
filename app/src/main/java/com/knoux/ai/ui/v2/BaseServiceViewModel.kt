package com.knoux.ai.ui.v2

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.knoux.ai.database.KnouxRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Shared base for every service screen that has to read the real device,
 * show the measured result and write an audit trail to Room.
 *
 * Lifecycle is owned by the ViewModel store, so the measurement survives
 * configuration changes (rotation) instead of restarting the whole scan.
 */
abstract class BaseServiceViewModel(
    application: Application,
    protected val repository: KnouxRepository,
) : AndroidViewModel(application) {

    protected val appContext: Context get() = getApplication()

    protected val scope: CoroutineScope get() = viewModelScope

    protected var job: Job? = null

    /** Runs [block] on the view-model scope, cancelling any previous run. */
    protected fun launchLatest(block: suspend CoroutineScope.() -> Unit) {
        job?.cancel()
        job = scope.launch(block = block)
    }

    override fun onCleared() {
        job?.cancel()
        super.onCleared()
    }
}
