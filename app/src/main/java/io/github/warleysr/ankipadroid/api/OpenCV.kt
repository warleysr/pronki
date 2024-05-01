package io.github.warleysr.ankipadroid.api

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

class OpenCV {

    companion object {

        fun processImage(bitmap: Bitmap, rotation: Int, onSuccess: (String) -> Unit) {

            val originalMat = Mat()
            Utils.bitmapToMat(bitmap, originalMat)
            val tempMat = Mat()
            Imgproc.cvtColor(originalMat, tempMat, Imgproc.COLOR_BGR2GRAY)
            Utils.matToBitmap(tempMat, bitmap)

            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val image = InputImage.fromBitmap(bitmap, rotation)

            val result = recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    onSuccess(visionText.text)
                }
                .addOnFailureListener { e ->
                }

        }
    }
}