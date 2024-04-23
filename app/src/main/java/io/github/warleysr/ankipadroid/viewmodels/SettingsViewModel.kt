package io.github.warleysr.ankipadroid.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import io.github.warleysr.ankipadroid.AnkiPADroid
class SettingsViewModel() : ViewModel() {

    private val sharedPrefs = AnkiPADroid.instance.getSharedPreferences(
        "settings", Context.MODE_PRIVATE
    )

    init {
        println("SettingsViewModel initialized")
    }

    fun getSetting(key: String): String {
        return sharedPrefs.getString(key, "") ?: ""
    }

    fun saveSetting(key: String, value: String) {
        with (sharedPrefs.edit()) {
            putString(key, value)
            apply()
        }
    }

    fun changeLanguage(language: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language))
    }

    fun openAboutInfo() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://github.com/warleysr/ankipadroid")
        AnkiPADroid.instance.startActivity(intent)
    }

}