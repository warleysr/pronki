package io.github.warleysr.ankipadroid.viewmodels

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import io.github.warleysr.ankipadroid.AnkiPADroid
import io.github.warleysr.ankipadroid.api.ImportedVocabulary
import io.github.warleysr.ankipadroid.api.OpenCV
import io.github.warleysr.ankipadroid.screens.settings.HSVColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.opencv.core.Scalar


class VocabularyViewModel: ViewModel() {

    var bitmap: MutableState<Bitmap?> = mutableStateOf(null)
        private set

    var text: MutableState<String?> = mutableStateOf(null)
        private set

    var permissionCameraGranted = mutableStateOf(false)
        private set

    var showingRecognized = mutableStateOf(false)
        private set

    var allWords = ArrayList<String>()
        private set

    var recognizedWords = ArrayList<String>()
        private set


    init {
        println("VocabularyViewModel initialized!")
        permissionCameraGranted.value = AnkiPADroid.instance.checkSelfPermission(
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun processBitmap(bitmap: Bitmap, rotation: Int, lower: Scalar, upper: Scalar) {
        this.bitmap.value = bitmap
        OpenCV.processImage(
            this.bitmap.value!!, rotation, lower, upper,
            onSuccess = { allWords, recognizedWords ->
                this.allWords = allWords
                this.recognizedWords = recognizedWords
                showRecognized()
            }
        )
    }

    fun applyAdjustment(bitmap: Bitmap, lower: HSVColor, upper: HSVColor, onResult: (Bitmap) -> Unit) {
        val processedImage = OpenCV.applyMaskToImage(bitmap, lower.toScalar(), upper.toScalar())
        onResult(processedImage)
    }

    fun cameraPermissionGranted() {
        permissionCameraGranted.value = true
    }

    fun showRecognized() {
        showingRecognized.value = true
    }

    fun hideRecognized() {
        showingRecognized.value = false
    }

    fun insertVocabulary(vocabulary: ArrayList<ImportedVocabulary>) {
        CoroutineScope(Dispatchers.IO).launch {
            for (vocab in vocabulary)
                AnkiPADroid.vocabularyDatabase.vocabularyDAO().insertAll(vocab)
        }
    }

    fun getVocabularyList(onFinish: (List<ImportedVocabulary>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val vocabList = AnkiPADroid.vocabularyDatabase.vocabularyDAO().getAllNew()
            onFinish(vocabList)
        }
    }

}