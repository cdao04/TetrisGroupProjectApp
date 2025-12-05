package com.example.tetrisgroupproject

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

/* Class runs when the app starts. It sets the dark/light mode globally
* across all activities. Still need to manually check if the switch is dark/light mode when modifying views*/
class TetrisApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("darkMode", false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}