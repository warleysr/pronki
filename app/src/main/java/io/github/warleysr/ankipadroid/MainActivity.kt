package io.github.warleysr.ankipadroid

import android.Manifest
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.warleysr.ankipadroid.navigation.AppNavigation
import io.github.warleysr.ankipadroid.ui.theme.AppTheme
import io.github.warleysr.ankipadroid.viewmodels.AnkiDroidViewModel
import io.github.warleysr.ankipadroid.viewmodels.PronunciationViewModel
import io.github.warleysr.ankipadroid.viewmodels.SettingsViewModel
import io.github.warleysr.ankipadroid.viewmodels.VocabularyViewModel


class MainActivity : AppCompatActivity() {

    private var settingsViewModel: SettingsViewModel? = null
    private var ankiDroidViewModel: AnkiDroidViewModel? = null
    private var pronunciationViewModel: PronunciationViewModel? = null
    private var vocabularyViewModel: VocabularyViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            settingsViewModel = viewModel<SettingsViewModel>(
                factory = object: ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return SettingsViewModel() as T
                    }
                }
            )
            pronunciationViewModel = viewModel<PronunciationViewModel>(
                factory = object: ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return PronunciationViewModel(filesDir.absolutePath) as T
                    }
                }
            )
            ankiDroidViewModel = viewModel<AnkiDroidViewModel>(
                factory = object: ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return AnkiDroidViewModel() as T
                    }
                }
            )
            vocabularyViewModel = viewModel<VocabularyViewModel>(
                factory = object: ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return VocabularyViewModel() as T
                    }
                }
            )

            LaunchedEffect(key1 = true) {
                val language = settingsViewModel!!.getSetting("language_app")
                settingsViewModel!!.changeLanguage(language.ifEmpty { "en" })
            }

            val darkTheme = (
                settingsViewModel!!.theme.value == "dark"
                || (settingsViewModel!!.theme.value == "system" && isSystemInDarkTheme())
            )
            AppTheme(
                dynamicColor = settingsViewModel!!.materialYou.value,
                darkTheme = darkTheme
            ) {
                AppNavigation(
                    settingsViewModel!!,
                    pronunciationViewModel!!,
                    ankiDroidViewModel!!,
                    vocabularyViewModel!!
                )
            }
        }

        AnkiDroidHelper(this)
    }

}