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
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {

    private var settingsViewModel: SettingsViewModel? = null
    private var pronunciationViewModel: PronunciationViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var configJson: String? = null
        val config = File(filesDir, "config.json")
        if (!config.exists()) {
            val inputStream = resources.openRawResource(R.raw.config)
            val bytes = inputStream.readBytes()
            configJson = String(bytes, Charsets.UTF_8)

            val outputStream = FileOutputStream(config)
            outputStream.write(bytes)

            outputStream.close()
            inputStream.close()
        } else {
            val inputStream = FileInputStream(config)
            val bytes = inputStream.readBytes()
            configJson = String(bytes, Charsets.UTF_8)

            inputStream.close()
        }

        ConfigUtils.initialize(configJson)

        settingsViewModel = SettingsViewModel(this)
        pronunciationViewModel = PronunciationViewModel(filesDir.absolutePath)

        CoroutineScope(Dispatchers.IO).launch {
            val language = settingsViewModel!!.getSetting("language_app")
            settingsViewModel!!.changeLanguage(language.ifEmpty { "en" })
        }

        setContent {
            AppNavigation(settingsViewModel!!, pronunciationViewModel!!)
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