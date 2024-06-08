package io.github.warleysr.ankipadroid.api

import android.graphics.Bitmap
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import io.github.warleysr.ankipadroid.screens.vocabulary.VocabularyState
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import java.util.Locale

class OpenCV {

    companion object {

        fun processImage(
            bitmap: Bitmap,
            rotation: Int,
            lower: Scalar,
            upper: Scalar,
            defaultLanguage: String,
            onSuccess: (SnapshotStateList<VocabularyState>) -> Unit
        ) {

            val originalImage = Mat()
            Utils.bitmapToMat(bitmap, originalImage)

            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val imageOrig = InputImage.fromBitmap(bitmap, rotation)
            val resultOrig = recognizer.process(imageOrig)

            val processedBitmap = applyMaskToImage(bitmap, lower, upper)
            val image = InputImage.fromBitmap(processedBitmap, rotation)
            val result = recognizer.process(image)
            val languageIdentifier = LanguageIdentification.getClient()

            val wordsMap = HashMap<String, String>()
            val recognizedWords = ArrayList<String>()
            val allWords = ArrayList<String>()

            var mainLanguage = defaultLanguage
            var mainConfidence = 1.0f

            val recognizeEachWord: (visionText: Text) -> Unit = {
                it.textBlocks.forEach { textBlock ->
                    textBlock.lines.forEach { line ->
                        line.elements.forEach { elem ->
                            languageIdentifier.identifyPossibleLanguages(elem.text)
                                .addOnSuccessListener { identifiedLanguages ->
                                    var language = mainLanguage

                                    // To identify words of different languages in the same text
                                    // check if the word confidence is higher than the main
                                    if (
                                        identifiedLanguages[0].languageTag != "und"
                                        && identifiedLanguages[0].confidence > mainConfidence
                                    )
                                        language = Locale
                                            .forLanguageTag(identifiedLanguages[0].languageTag)
                                            .getDisplayLanguage(Locale.ENGLISH)

                                    wordsMap[elem.text] = language
                                    allWords.add(elem.text)
                                }
                                .addOnFailureListener {
                                    wordsMap[elem.text] = mainLanguage
                                    allWords.add(elem.text)
                                }

                        }
                    }
                }
            }

            resultOrig.addOnSuccessListener { visionText ->
                languageIdentifier.identifyPossibleLanguages(visionText.text)
                    .addOnSuccessListener {
                        mainLanguage = Locale
                            .forLanguageTag(it[0].languageTag)
                            .getDisplayLanguage(Locale.ENGLISH)

                        mainConfidence = it[0].confidence

                        recognizeEachWord(visionText)
                    }
                    .addOnFailureListener {
                        recognizeEachWord(visionText)
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

                val allWordsState = allWords.map {
                    VocabularyState(
                        vocabulary = ImportedVocabulary(data = it, language = wordsMap[it]!!),
                        initialState = it in recognizedWords
                    )
                }.toMutableStateList()
                onSuccess(allWordsState)
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

        fun recognizeLanguage(
            text: String,
            defaultLanguage: String,
            onFinish: (String) -> Unit,
            forceLanguage: Boolean
        ) {
            if (forceLanguage) {
                onFinish(defaultLanguage)
                return
            }
            val languageIdentifier = LanguageIdentification.getClient()
            languageIdentifier.identifyLanguage(text)
                .addOnSuccessListener {
                    if (it != "und")
                        onFinish(
                            Locale.forLanguageTag(it).getDisplayLanguage(Locale.ENGLISH)
                        )
                    else
                        onFinish(defaultLanguage)
                }
                .addOnFailureListener {
                    onFinish(defaultLanguage)
                }
        }
    }
}