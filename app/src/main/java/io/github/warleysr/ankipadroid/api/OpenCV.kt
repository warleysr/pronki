package io.github.warleysr.ankipadroid.api

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

class OpenCV {

    companion object {

        fun processImage(bitmap: Bitmap, rotation: Int, onSuccess: (String) -> Unit) {

            val originalImage = Mat()
            Utils.bitmapToMat(bitmap, originalImage)

            val imageHSV = Mat()
            var tempMat2 = Mat()
            Imgproc.cvtColor(originalImage, imageHSV, Imgproc.COLOR_BGR2HSV)
//            Imgproc.threshold(tempMat, tempMat2, 0.0, 255.0, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU)

            val tempBitmap = Bitmap.createBitmap(bitmap)
            Utils.matToBitmap(tempMat2, tempBitmap)

//            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
//            val image = InputImage.fromBitmap(tempBitmap, rotation)
//
//            val result = recognizer.process(image)
//
//            result.addOnSuccessListener { visionText ->
//                visionText.textBlocks.forEach { textBlock ->
//                    textBlock.lines.forEach { line ->
//                        line.elements.forEach { elem ->
//                            val box = elem.boundingBox!!
//                            Imgproc.rectangle(
//                                originalMat,
//                                Point(box.left.toDouble(), box.top.toDouble()),
//                                Point(box.right.toDouble(), box.bottom.toDouble()),
//                                Scalar(255.0, 0.0, 1.0, 255.0),
//                                2
//                            )
//                        }
//                    }
//                }
//
//                Utils.matToBitmap(originalMat, bitmap)
//
//                onSuccess(visionText.text)
//            }
//            .addOnFailureListener { e ->
//            }

        }

        fun applyMaskToImage(bitmap: Bitmap, lower: Scalar, upper: Scalar): Bitmap {
            val originalImage = Mat()
            val hsvImage = Mat()
            val imageMask = Mat()
            val finalImage = Mat()

            Utils.bitmapToMat(bitmap, originalImage)
            Imgproc.cvtColor(originalImage, hsvImage, Imgproc.COLOR_RGB2HSV)
            Core.inRange(hsvImage, lower, upper, imageMask)
            Core.bitwise_and(originalImage, originalImage, finalImage, imageMask)

            val finalBitmap = Bitmap.createBitmap(bitmap)
            Utils.matToBitmap(finalImage, finalBitmap)

            return finalBitmap
        }
    }
}