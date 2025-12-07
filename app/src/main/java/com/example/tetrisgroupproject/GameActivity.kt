package com.example.tetrisgroupproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import java.util.Timer

class GameActivity : AppCompatActivity() {
    private lateinit var tetrisView: TetrisView
    private lateinit var game: TetrisGameManager
    private lateinit var timer: Timer
    private lateinit var task: TetrisTimerTask

    companion object {
        const val DELTA_TIME: Long = 500
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val windowInsetsController: WindowInsetsControllerCompat = 
            WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        // TODO, can make ratios for the GUI size
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels

        game = TetrisGameManager(this)
        
        tetrisView = TetrisView(this, width, height, game)
        setContentView(tetrisView)
        
        game.startNewGame()

        game.onGameOverCallback = {
            stopTimer()
        }
        
        game.onViewUpdate = {
            tetrisView.postInvalidate()
        }
        
        // TODO manipulate timer based on score or level
        task = TetrisTimerTask(this)
        timer = Timer()
        timer.schedule(task, 0L, DELTA_TIME)
    }

    private fun stopTimer() {
        timer.cancel()
        timer.purge()
    }

    fun updateModel() {
        game.tick()
    }

    fun updateView() {
        tetrisView.postInvalidate()
    }

    override fun onPause() {
        super.onPause()
        stopTimer()
    }

    override fun onResume() {
        super.onResume()
        // Resume always runs when programs starts for any case so we start timer here
        if (!game.isGameOver()) {
            task = TetrisTimerTask(this)
            timer = Timer()
            timer.schedule(task, 0L, DELTA_TIME)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }
}
