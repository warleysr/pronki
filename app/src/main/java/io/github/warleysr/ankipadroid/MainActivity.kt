package io.github.warleysr.ankipadroid

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import io.github.warleysr.ankipadroid.navigation.AppNavigation
import io.github.warleysr.ankipadroid.viewmodels.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val settingsViewModel = SettingsViewModel(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            val language = settingsViewModel.getSetting("language")
            settingsViewModel.changeLanguage(language.ifEmpty { "en" })
        }

        setContent {
            AppNavigation(settingsViewModel)
        }

//        val mAnkiDroid = AnkiDroidHelper(this)
//        if (mAnkiDroid.shouldRequestPermission()) {
//            mAnkiDroid.requestPermission(this, 0)
//        }
    }
}