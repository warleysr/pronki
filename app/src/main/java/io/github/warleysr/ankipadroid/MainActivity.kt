package io.github.warleysr.ankipadroid

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import io.github.warleysr.ankipadroid.navigation.AppNavigation
import io.github.warleysr.ankipadroid.viewmodels.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.Locale


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsViewModel = SettingsViewModel(this)

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