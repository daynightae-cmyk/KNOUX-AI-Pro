package com.knoux.ai.ui.debug

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Debug-only navigation bridge.
 *
 * The debug source set starts a tiny loopback poller; the main UI listens to
 * this channel. It lets the visual QA loop capture real screens without adb or
 * an accessibility service. Release builds never post to the channel and all
 * app behaviour remains user-driven.
 */
object QaCommandBus {
    private val queue = Channel<String>(Channel.BUFFERED)
    val commands = queue.receiveAsFlow()

    fun post(route: String) {
        queue.trySend(route)
    }
}
