package io.github.warleysr.ankipadroid.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat.startActivity
import androidx.core.os.LocaleListCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class SettingsViewModel(private val context: Context) : ViewModel() {

    suspend fun getSetting(key: String): String {
        val keyPref = stringPreferencesKey(key)
        val keyFlow: Flow<String> = context.dataStore.data
            .map { preferences ->
                preferences[keyPref] ?: ""
            }
        return keyFlow.first()
    }

    suspend fun saveSetting(key: String, value: String) {
        val keyPref = stringPreferencesKey(key)
        context.dataStore.edit { settings ->
            settings[keyPref] = value
        }
    }

    fun changeLanguage(language: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language))
    }

    fun openAboutInfo() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://github.com/warleysr/ankipadroid")
        context.startActivity(intent)
    }

}