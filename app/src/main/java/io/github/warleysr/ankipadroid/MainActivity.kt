package io.github.warleysr.ankipadroid

import android.Manifest
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.warleysr.ankipadroid.navigation.AppNavigation
import io.github.warleysr.ankipadroid.viewmodels.AnkiDroidViewModel
import io.github.warleysr.ankipadroid.viewmodels.PronunciationViewModel
import io.github.warleysr.ankipadroid.viewmodels.SettingsViewModel


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


//        CoroutineScope(Dispatchers.IO).launch {
//            val language = settingsViewModel!!.getSetting("language_app")
//            settingsViewModel!!.changeLanguage(language.ifEmpty { "en" })
//        }

        setContent {
            val settingsViewModel = viewModel<SettingsViewModel>(
                factory = object: ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return SettingsViewModel() as T
                    }
                }
            )
            val pronunciationViewModel = viewModel<PronunciationViewModel>(
                factory = object: ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return PronunciationViewModel(filesDir.absolutePath) as T
                    }
                }
            )
            val ankiDroidViewModel = viewModel<AnkiDroidViewModel>(
                factory = object: ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return AnkiDroidViewModel() as T
                    }
                }
            )
            AppNavigation(settingsViewModel, pronunciationViewModel, ankiDroidViewModel)
        }

        val ankiDroidHelper = AnkiDroidHelper(this)
        if (ankiDroidHelper.shouldRequestPermission()) {
            ankiDroidHelper.requestPermission(this, 0)
        }

        this.requestPermission()
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