package com.example.tetrisgroupproject

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {
    private lateinit var playButton : Button
    private lateinit var settingsButton : Button
    private lateinit var imageView : ImageView
    private lateinit var title : TextView
    private lateinit var layout : RelativeLayout
    private lateinit var popupLayout : LinearLayout
    private lateinit var settingsTitle : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("darkMode", false)

        title = findViewById<TextView>(R.id.title)
        title.post {
            val width = title.paint.measureText("TETRIS")
            val shader = LinearGradient(
                0f, 0f, width, title.textSize,
                intArrayOf(
                    Color.RED,
                    Color.MAGENTA,
                    Color.BLUE,
                    Color.CYAN,
                    Color.GREEN,
                    Color.YELLOW
                ),
                null,
                Shader.TileMode.CLAMP
            )
            val spannable = SpannableString("ANTI-GRAVITY TETRIS")
            val start = spannable.indexOf("TETRIS")
            val end = start + "TETRIS".length
            val rainbowSpan = object : CharacterStyle() {
                override fun updateDrawState(tp: TextPaint) {
                    tp.shader = shader
                }
            }
            spannable.setSpan(rainbowSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            title.text = spannable
        }

        playButton = findViewById(R.id.play_button)
        settingsButton = findViewById(R.id.settings_button)
        imageView = findViewById(R.id.logo)
        layout = findViewById(R.id.root_layout)
        layout.setBackgroundColor(if (isDarkMode) Color.BLACK else Color.WHITE)
        imageView.setImageResource(if (isDarkMode) R.drawable.logo_dark else R.drawable.logo_white)
        
        playButton.setOnClickListener {
            val intent = android.content.Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
        
        settingsButton.setOnClickListener {
            showSettingsPopup()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
            Configuration.UI_MODE_NIGHT_YES) {
            imageView.setImageResource(R.drawable.logo_dark)
        } else {
            imageView.setImageResource(R.drawable.logo_white)
        }
    }

    private fun showSettingsPopup() {
        val view = layoutInflater.inflate(R.layout.popup_settings, null)

        val switchDark = view.findViewById<Switch>(R.id.switchDarkMode)
        val switchGUI = view.findViewById<Switch>(R.id.switchLargeGUI)

        popupLayout = view.findViewById<LinearLayout>(R.id.popup_layout)
        settingsTitle = view.findViewById<TextView>(R.id.settings_title)

        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("darkMode", false)

        switchDark.isChecked = isDarkMode
        switchGUI.isChecked = prefs.getBoolean("largeGUI", false)

        if (isDarkMode) {
            popupLayout.setBackgroundColor(Color.parseColor("#2C2C2C"))
            settingsTitle.setTextColor(Color.WHITE)
            switchDark.setTextColor(Color.WHITE)
            switchGUI.setTextColor(Color.WHITE)
        } else {
            popupLayout.setBackgroundColor(Color.WHITE)
            settingsTitle.setTextColor(Color.BLACK)
            switchDark.setTextColor(Color.BLACK)
            switchGUI.setTextColor(Color.BLACK)
        }

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(true)
            .create()

        dialog.show()

        switchDark.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("darkMode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                layout.setBackgroundColor(Color.BLACK)
                popupLayout.setBackgroundColor(Color.parseColor("#2C2C2C"))
                settingsTitle.setTextColor(Color.WHITE)
                switchDark.setTextColor(Color.WHITE)
                switchGUI.setTextColor(Color.WHITE)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                layout.setBackgroundColor(Color.WHITE)
                popupLayout.setBackgroundColor(Color.WHITE)
                settingsTitle.setTextColor(Color.BLACK)
                switchDark.setTextColor(Color.BLACK)
                switchGUI.setTextColor(Color.BLACK)
            }
        }

        switchGUI.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("largeGUI", isChecked).apply()
        }
    }
}