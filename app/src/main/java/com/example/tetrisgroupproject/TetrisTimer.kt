package com.example.tetrisgroupproject

import java.util.Timer

class TetrisTimer(private val activity: GameActivity) {
    companion object {
        const val DELTA_TIME: Long = 500
    }

    private var timer: Timer? = null
    private var task: TetrisTimerTask? = null
    private var isRunning = false

    fun start() {
        if (!isRunning) {
            isRunning = true
            scheduleTimer()
        }
    }

    fun stop() {
        isRunning = false
        timer?.cancel()
        timer = null
        task = null
    }

    private fun scheduleTimer() {
        timer = Timer()
        task = TetrisTimerTask(activity)
        timer?.schedule(task, 0L, DELTA_TIME)
    }

    fun isRunning(): Boolean {
        return isRunning
    }
}
