package io.github.warleysr.pronki.api

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentifier
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import io.github.warleysr.pronki.screens.vocabulary.VocabularyState
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvException
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

            val binarizedBitmap = applyBinarization(bitmap) as Bitmap

            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val imageOrig = InputImage.fromBitmap(binarizedBitmap, rotation)
            val resultOrig = recognizer.process(imageOrig)

            val processedBitmap = applyMaskToImage(bitmap, lower, upper)

            val languageIdentifier = LanguageIdentification.getClient()

            var mainLanguage = defaultLanguage
            var mainConfidence = 1.0f

            val doRecognition: (Text) -> Unit  = { visionText ->
                recognizeWords(
                    visionText, mainLanguage, mainConfidence, languageIdentifier,
                    onFinish = { allWords ->
                        identifyHighlightedWords(
                            allWords, processedBitmap,
                            onFinish = { highlightedWords ->
                                val allWordsState = allWords.map {
                                    VocabularyState(
                                        vocabulary = ImportedVocabulary(data = it.word, language = it.language),
                                        initialState = it in highlightedWords
                                    )
                                }.toMutableStateList()
                                onSuccess(allWordsState)
                            }
                        )
                    }
                )

            }

            resultOrig.addOnSuccessListener { visionText ->
                languageIdentifier.identifyPossibleLanguages(visionText.text)
                    .addOnSuccessListener {
                        mainLanguage = Locale
                            .forLanguageTag(it[0].languageTag)
                            .getDisplayLanguage(Locale.ENGLISH)

                        mainConfidence = it[0].confidence

                        doRecognition(visionText)
                    }
                    .addOnFailureListener {
                        doRecognition(visionText)
                    }
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

        fun applyBinarization(bitmap: Bitmap, returnMat: Boolean = false): Any {
            val originalImage = Mat()
            val grayImage = Mat()
            val binaryImage = Mat()

            Utils.bitmapToMat(bitmap, originalImage)

            Imgproc.cvtColor(originalImage, grayImage, Imgproc.COLOR_BGR2GRAY)

            Imgproc.threshold(
                grayImage, binaryImage, 0.0, 255.0,
                Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU
            )

            if (returnMat)
                return binaryImage
            else {
                val finalBitmap = Bitmap.createBitmap(bitmap)
                Utils.matToBitmap(binaryImage, finalBitmap)

                return finalBitmap
                }
        }

        private fun recognizeWords(
            visionText: Text,
            mainLanguage: String,
            mainConfidence: Float,
            languageIdentifier: LanguageIdentifier,
            onFinish: (List<RecognizedWord>) -> Unit
        ) {
            val recognizedWords = ArrayList<RecognizedWord>()

            visionText.textBlocks.forEachIndexed { idxBlock, textBlock ->
                textBlock.lines.forEach { line ->
                    line.elements.forEachIndexed { idxElem, elem ->
                        val box = elem.boundingBox
                        val word = elem.text.lowercase().replace(Regex("[,.;!?:]+"), "")
                        val lastWord = idxBlock == visionText.textBlocks.lastIndex
                                && idxElem == line.elements.lastIndex

                        languageIdentifier.identifyPossibleLanguages(word)
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

                                recognizedWords.add(RecognizedWord(word, language, box))

                                if (lastWord)
                                    onFinish(recognizedWords)
                            }
                            .addOnFailureListener {
                                recognizedWords.add(RecognizedWord(word, mainLanguage, box))

                                if (lastWord)
                                    onFinish(recognizedWords)
                            }
                    }
                }
            }
        }

        fun identifyHighlightedWords(
            recognizedWords: List<RecognizedWord>,
            processedBitmap: Bitmap,
            onFinish: (List<RecognizedWord>) -> Unit
        ) {
            val binarizedBitmap = applyBinarization(processedBitmap, returnMat = true) as Mat

            val highlightedWords = ArrayList<RecognizedWord>()

            for (word in recognizedWords) {
                if (word.boundingBox == null) continue
                val x = word.boundingBox.left
                val y = word.boundingBox.top
                val w = word.boundingBox.width()
                val h = word.boundingBox.height()
                val rectThreshold = (w * h * 50) / 100
                val rect = org.opencv.core.Rect(x, y, w, h)
                try {
                    val imgRoi = binarizedBitmap.submat(rect)

                    val nonZero = Core.countNonZero(imgRoi)

                    if (nonZero >= rectThreshold) {
                        highlightedWords.add(word)
                        println("Highlighted word: ${word.word}")
                    }
                } catch (exc: CvException) {
                    println(exc.message)
                    continue
                }
            }

            onFinish(highlightedWords)
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

data class RecognizedWord(
    val word: String,
    val language: String,
    val boundingBox: Rect?
)