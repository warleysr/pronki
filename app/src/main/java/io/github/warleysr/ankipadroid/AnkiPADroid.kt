package io.github.warleysr.ankipadroid

import android.app.Application
import androidx.room.Room
import io.github.warleysr.ankipadroid.api.VocabularyDatabase
import org.opencv.android.OpenCVLoader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class AnkiPADroid : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        vocabularyDatabase = Room.databaseBuilder(
            this, VocabularyDatabase::class.java, "vocabulary"
        ).build()

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

        if (OpenCVLoader.initLocal()) {
            println("OpenCV loaded successfully")
        } else {
            println("OpenCV initialization failed!");
        }

    }
    companion object {
        lateinit var instance: AnkiPADroid
            private set

        lateinit var vocabularyDatabase: VocabularyDatabase
            private set
    }
}