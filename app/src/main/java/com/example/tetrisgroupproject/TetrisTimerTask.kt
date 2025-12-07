package com.example.tetrisgroupproject

import java.util.TimerTask

class TetrisTimerTask(private val activity: GameActivity) : TimerTask() {
    override fun run() {
        activity.updateModel()
        activity.updateView()
    }
}
