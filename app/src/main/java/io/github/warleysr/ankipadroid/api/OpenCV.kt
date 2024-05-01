package io.github.warleysr.ankipadroid.api

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

class OpenCV {

    companion object {

        fun processImage(bitmap: Bitmap, rotation: Int, onSuccess: (String) -> Unit) {

            val originalMat = Mat()
            Utils.bitmapToMat(bitmap, originalMat)

            val tempMat = Mat()
            var tempMat2 = Mat()
            Imgproc.cvtColor(originalMat, tempMat, Imgproc.COLOR_BGR2GRAY)
//            Imgproc.threshold(tempMat, tempMat2, 0.0, 255.0, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU)
            tempMat2 = tempMat

            val tempBitmap = Bitmap.createBitmap(bitmap)
            Utils.matToBitmap(tempMat2, tempBitmap)

            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val image = InputImage.fromBitmap(tempBitmap, rotation)

            val result = recognizer.process(image)

            result.addOnSuccessListener { visionText ->
                visionText.textBlocks.forEach { textBlock ->
                    textBlock.lines.forEach { line ->
                        line.elements.forEach { elem ->
                            val box = elem.boundingBox!!
                            Imgproc.rectangle(
                                originalMat,
                                Point(box.left.toDouble(), box.top.toDouble()),
                                Point(box.right.toDouble(), box.bottom.toDouble()),
                                Scalar(255.0, 0.0, 1.0, 255.0),
                                2
                            )
                        }
                    }
                }

                Utils.matToBitmap(originalMat, bitmap)

                onSuccess(visionText.text)
            }
            .addOnFailureListener { e ->
            }

        }
    }
}