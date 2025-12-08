package com.example.tetrisgroupproject

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.MotionEvent
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
    private lateinit var vibrator : Vibrator
    private var lastDragX: Float? = null
    private var hasDragged = false

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
        timer.schedule(task, DELTA_TIME, DELTA_TIME)

        // Initialize vibrator
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        // Vibrate when row cleared
        game.onRowCleared = {vibrateRowClear()}

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false
        if (game.isGameOver()) {
            // TODO change view to end screen

        }

        val screenWidth = resources.displayMetrics.widthPixels
        val cellWidth = screenWidth / TetrisGrid.GRID_WIDTH

        when (event.action) {
            // Handle block rotation (on tap)
            MotionEvent.ACTION_DOWN -> {
                lastDragX = event.x
                hasDragged = false
            }

            // Handle block shift (on tap and drag)
            MotionEvent.ACTION_MOVE -> {
                val prev = lastDragX ?: event.x
                val dx = event.x - prev

                if (dx < -cellWidth / 2f) {
                    game.moveLeft()
                    lastDragX = event.x
                    hasDragged = true
                } else if (dx > cellWidth / 2f) {
                    game.moveRight()
                    lastDragX = event.x
                    hasDragged = true
                }
            }

            MotionEvent.ACTION_UP -> {
                if (!hasDragged) {
                    game.rotate()
                }
                lastDragX = null
            }
        }
        return super.onTouchEvent(event)
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

    private fun vibrateRowClear() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(effect)
        } else {
            vibrator.vibrate(80)
        }
        Log.w("MainActivity", "Vibrating")
    }
}
