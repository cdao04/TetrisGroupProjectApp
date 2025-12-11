package com.example.tetrisgroupproject

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import android.widget.Spinner

class MainActivity : AppCompatActivity() {
    private lateinit var playButton: Button
    private lateinit var settingsButton: Button
    private lateinit var imageView: ImageView
    private lateinit var title: TextView
    private lateinit var layout: RelativeLayout
    private lateinit var popupLayout: LinearLayout
    private lateinit var settingsTitle: TextView
    private lateinit var spinnerTitle : TextView
    private lateinit var switchDark : Switch
    private lateinit var spinnerGUI : Spinner
    private lateinit var leaderboardBtn : Button

    private lateinit var currentLevel : Button


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

        currentLevel = findViewById(R.id.player_best_level)

        val savedLevel = prefs.getInt("level", 1)  // default to level 1
        currentLevel.text = "Level: $savedLevel"

        playButton = findViewById(R.id.play_button)
        settingsButton = findViewById(R.id.settings_button)
        imageView = findViewById(R.id.logo)
        layout = findViewById(R.id.root_layout)
        layout.setBackgroundColor(if (isDarkMode) Color.BLACK else Color.WHITE)
        imageView.setImageResource(if (isDarkMode) R.drawable.logo_dark else R.drawable.logo_white)
        leaderboardBtn = findViewById(R.id.leaderboard_button)

        leaderboardBtn.setOnClickListener {
            val intent = android.content.Intent(this, EndActivity::class.java)
            startActivity(intent)
        }

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

        val isDark = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
                Configuration.UI_MODE_NIGHT_YES

        imageView.setImageResource(if (isDark) R.drawable.logo_dark else R.drawable.logo_white)
        layout.setBackgroundColor(if (isDark) Color.BLACK else Color.WHITE)
        spinnerTitle.setTextColor(if (isDark) Color.WHITE else Color.BLACK)
    }

    private fun showSettingsPopup() {
        val view = layoutInflater.inflate(R.layout.popup_settings, null)
        switchDark = view.findViewById<Switch>(R.id.switchDarkMode)
        spinnerGUI = view.findViewById<Spinner>(R.id.spinnerSwitchGUI)
        popupLayout = view.findViewById(R.id.popup_layout)
        settingsTitle = view.findViewById(R.id.settings_title)
        spinnerTitle = view.findViewById(R.id.spinner_title)

        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("darkMode", false)
        val savedGUI = prefs.getString("guiSize", "Small")

        val adapter = object : ArrayAdapter<String>(
            this,
            R.layout.spinner_item,
            resources.getStringArray(R.array.spinnerSwitchGUI)
        ) {
            var darkMode = isDarkMode
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val inflater = LayoutInflater.from(context)
                val v = convertView ?: inflater.inflate(R.layout.spinner_item, parent, false)

                val tv = v.findViewById<TextView>(R.id.spinnerItemText)
                tv.text = getItem(position)
                tv.setTextColor(if (darkMode) Color.WHITE else Color.BLACK)

                return v
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val inflater = LayoutInflater.from(context)
                val v = convertView ?: inflater.inflate(R.layout.spinner_dropdown_item, parent, false)

                val tv = v.findViewById<TextView>(R.id.spinnerDropdownItemText)
                tv.text = getItem(position)
                tv.setTextColor(if (darkMode) Color.WHITE else Color.BLACK)

                return v
            }
        }

        spinnerGUI.adapter = adapter
        val index = if (savedGUI == "Large") 1 else 0
        spinnerGUI.setSelection(index)
        if (isDarkMode) {
            popupLayout.setBackgroundColor(Color.parseColor("#2C2C2C"))
            settingsTitle.setTextColor(Color.WHITE)
            switchDark.setTextColor(Color.WHITE)
            spinnerTitle.setTextColor(Color.WHITE)
        }
        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(true)
            .create()

        dialog.show()
        switchDark.isChecked = isDarkMode
        switchDark.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean("darkMode", checked).apply()
            adapter.darkMode = checked
            adapter.notifyDataSetChanged()
            // Immediately update popup colors before the Activity recreates
            if (checked) {
                popupLayout.setBackgroundColor(Color.parseColor("#2C2C2C"))
                settingsTitle.setTextColor(Color.WHITE)
                switchDark.setTextColor(Color.WHITE)
                spinnerGUI.setPopupBackgroundDrawable(
                    ColorDrawable(Color.parseColor("#3A3A3A"))
                )
            } else {
                popupLayout.setBackgroundColor(Color.WHITE)
                settingsTitle.setTextColor(Color.BLACK)
                switchDark.setTextColor(Color.BLACK)
                spinnerGUI.setPopupBackgroundDrawable(
                    ColorDrawable(Color.WHITE)
                )
            }
            AppCompatDelegate.setDefaultNightMode(
                if (checked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
        //storing choice in shared preferences.
        spinnerGUI.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val size = if (position == 0) "Small" else "Large"
                prefs.edit().putString("guiSize", size).apply()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}