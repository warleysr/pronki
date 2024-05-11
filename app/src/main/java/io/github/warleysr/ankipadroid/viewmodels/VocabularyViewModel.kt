package io.github.warleysr.ankipadroid.viewmodels

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import io.github.warleysr.ankipadroid.AnkiPADroid
import io.github.warleysr.ankipadroid.api.OpenCV
import io.github.warleysr.ankipadroid.screens.settings.HSVColor
import org.opencv.core.Scalar


class VocabularyViewModel: ViewModel() {

    var bitmap: MutableState<Bitmap?> = mutableStateOf(null)
        private set

    var text: MutableState<String?> = mutableStateOf(null)
        private set

    var permissionCameraGranted = mutableStateOf(false)
        private set

    init {
        println("VocabularyViewModel initialized!")
        permissionCameraGranted.value = AnkiPADroid.instance.checkSelfPermission(
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun processBitmap(bitmap: Bitmap, rotation: Int, lower: Scalar, upper: Scalar) {
        this.bitmap.value = bitmap
        OpenCV.processImage(this.bitmap.value!!, rotation, lower, upper, onSuccess = { text.value = it })
    }

    fun applyAdjustment(bitmap: Bitmap, lower: HSVColor, upper: HSVColor, onResult: (Bitmap) -> Unit) {
        val processedImage = OpenCV.applyMaskToImage(bitmap, lower.toScalar(), upper.toScalar())
        onResult(processedImage)
    }

    fun cameraPermissionGranted() {
        permissionCameraGranted.value = true
    }

}