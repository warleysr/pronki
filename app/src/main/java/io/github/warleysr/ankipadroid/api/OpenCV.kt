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

        fun processImage(
            bitmap: Bitmap,
            rotation: Int,
            lower: Scalar,
            upper: Scalar,
            onSuccess: (ArrayList<String>, ArrayList<String>) -> Unit
        ) {

            val originalImage = Mat()
            Utils.bitmapToMat(bitmap, originalImage)



            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val imageOrig = InputImage.fromBitmap(bitmap, rotation)
            val resultOrig = recognizer.process(imageOrig)

            val processedBitmap = applyMaskToImage(bitmap, lower, upper)
            val image = InputImage.fromBitmap(processedBitmap, rotation)
            val result = recognizer.process(image)

            val allWords = ArrayList<String>()
            val recognizedWords = ArrayList<String>()

            resultOrig.addOnSuccessListener { visionText ->
                visionText.textBlocks.forEach { textBlock ->
                    textBlock.lines.forEach { line ->
                        line.elements.forEach { elem ->
                            allWords.add(elem.text)
                        }
                    }
                }
            }

            result.addOnSuccessListener { visionText ->
                visionText.textBlocks.forEach { textBlock ->
                    textBlock.lines.forEach { line ->
                        line.elements.forEach { elem ->
                            recognizedWords.add(elem.text)
//                            val box = elem.boundingBox!!
//                            Imgproc.rectangle(
//                                originalImage,
//                                Point(box.left.toDouble(), box.top.toDouble()),
//                                Point(box.right.toDouble(), box.bottom.toDouble()),
//                                Scalar(255.0, 0.0, 1.0, 255.0),
//                                2
//                            )
                        }
                    }
                }

                Utils.matToBitmap(originalImage, bitmap)

                onSuccess(allWords, recognizedWords)
            }

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