package io.github.warleysr.ankipadroid

import android.app.Application
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class AnkiPADroid : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

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
    }
    companion object {
        lateinit var instance: AnkiPADroid
            private set
    }
}