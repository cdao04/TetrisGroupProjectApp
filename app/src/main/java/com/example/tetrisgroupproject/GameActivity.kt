package com.example.tetrisgroupproject

import android.content.Context
import android.content.Intent
import com.google.android.gms.ads.AdRequest
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.MotionEvent
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Timer

class GameActivity : AppCompatActivity() {
    private lateinit var tetrisView: TetrisView
    private lateinit var game: TetrisGameManager
    private lateinit var timer: Timer
    private lateinit var task: TetrisTimerTask
    private lateinit var vibrator : Vibrator
    private var lastDragX: Float? = null
    private var hasDragged = false

    //Database Additions:
    private lateinit var firebase : FirebaseDatabase
    private lateinit var leaderboard : DatabaseReference

    //For Ad
    private var ad : InterstitialAd? = null

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

            //Now for the popup after the game is done
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Game Over")
            builder.setMessage("Enter your name to save your score:")

            val input = EditText(this)
            builder.setView(input)

            val score = game.getScore()
            val level = game.getLevel()

            builder.setPositiveButton("OK") { dialog, which ->
                val name = input.text.toString()

                if (ad != null) {
                    ad?.show(this)
                }

                scoreToDatabase(name, score, level)

                val intent = Intent(this, EndActivity::class.java)
                intent.putExtra("name", name)
                intent.putExtra("score", score)
                intent.putExtra("level", level)

                startActivity(intent)
                finish()
            }
            builder.setNegativeButton("Cancel"){ dialog, which ->
                if(ad != null){
                    ad?.show(this)
                }

                val intent = Intent(this, EndActivity::class.java)
                intent.putExtra("name", "")
                intent.putExtra("score", score)
                intent.putExtra("level", level)
                startActivity(intent)
                finish()
            }

            builder.show()
        }

        game.onViewUpdate = {
            tetrisView.postInvalidate()
        }

        task = TetrisTimerTask(this)
        timer = Timer()
        var speed = game.getTickInterval()
        timer.schedule(task, speed, speed)

        // Initialize vibrator
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        // Vibrate when row cleared
        game.onRowCleared = {vibrateRowClear()}

        // Restart the timer when a new level starts
        game.onLevelUp = {
            restartTimer()
        }

        // TODO change view to end screen when game is done
        //Initialize Firebase
        firebase = FirebaseDatabase.getInstance()
        leaderboard = firebase.getReference("leaderboard")

        //Load the Ad
        loadInterstitialAd()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false

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

    private fun vibrateRowClear() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(effect)
        } else {
            vibrator.vibrate(80)
        }
        Log.w("MainActivity", "Vibrating")
    }

    private fun restartTimer() {
        timer.cancel()
        timer.purge()

        task = TetrisTimerTask(this)
        timer = Timer()

        val speed = game.getTickInterval()
        timer.schedule(task, speed, speed)
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

    /*
   When the user wants their score saved, we write it to the
   database, assuming no errors (might add)
    */

    fun scoreToDatabase(name: String, score: Int, level: Int){
        val player = leaderboard.child(name)

        player.child("score").setValue(score)
        player.child("level").setValue(level)
    }

    /*
    Show ad after the game is finished, then go to the end view
     */

    private fun loadInterstitialAd(){
        val builder = AdRequest.Builder()
        builder.addKeyword("gaming")
        var request: AdRequest = builder.build()

        var adUnitId = "ca-app-pub-3940256099942544/1033173712"

        InterstitialAd.load(this, adUnitId, request,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(adLoaded: InterstitialAd) {
                    ad = adLoaded
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    ad = null
                }
            })
    }
}
