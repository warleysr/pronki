package io.github.warleysr.ankipadroid

import android.Manifest
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import io.github.warleysr.ankipadroid.navigation.AppNavigation
import io.github.warleysr.ankipadroid.viewmodels.PronunciationViewModel
import io.github.warleysr.ankipadroid.viewmodels.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val settingsViewModel = SettingsViewModel(this)
    private var pronunciationViewModel: PronunciationViewModel? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pronunciationViewModel = PronunciationViewModel(filesDir.absolutePath)

        CoroutineScope(Dispatchers.IO).launch {
            val language = settingsViewModel.getSetting("language")
            settingsViewModel.changeLanguage(language.ifEmpty { "en" })
        }

        setContent {
            AppNavigation(settingsViewModel, pronunciationViewModel!!)
        }

        this.requestPermission()

//        val mAnkiDroid = AnkiDroidHelper(this)
//        if (mAnkiDroid.shouldRequestPermission()) {
//            mAnkiDroid.requestPermission(this, 0)
//        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
            ),
            1 )
    }

}