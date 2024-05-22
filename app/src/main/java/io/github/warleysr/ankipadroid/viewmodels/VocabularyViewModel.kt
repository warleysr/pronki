package io.github.warleysr.ankipadroid.viewmodels

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.ichi2.anki.api.AddContentApi
import io.github.warleysr.ankipadroid.AnkiPADroid
import io.github.warleysr.ankipadroid.api.GeminiAPI
import io.github.warleysr.ankipadroid.api.ImportedVocabulary
import io.github.warleysr.ankipadroid.api.OpenCV
import io.github.warleysr.ankipadroid.screens.settings.HSVColor
import io.github.warleysr.ankipadroid.screens.vocabulary.VocabularyState
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

    var creatingCards = mutableStateOf(false)
        private set

    var successCreation = mutableStateOf(false)
        private set

    var cardsCreated = mutableIntStateOf(0)
        private set

    var usedTokens = mutableIntStateOf(0)
        private set

    var allWords = mutableStateListOf<VocabularyState>()
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
            onSuccess = { allWords ->
                this.allWords = allWords
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

    fun hideSuccessDialog() {
        successCreation.value = false
    }

    fun insertVocabulary(vararg vocabularies: ImportedVocabulary) {
        CoroutineScope(Dispatchers.IO).launch {
            AnkiPADroid.vocabularyDatabase.vocabularyDAO().insertAll(*vocabularies)
        }
    }

    fun getVocabularyList(onFinish: (List<ImportedVocabulary>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val vocabList = AnkiPADroid.vocabularyDatabase.vocabularyDAO().getAllNew()
            onFinish(vocabList)
        }
    }

    fun deleteVocabulary(vararg vocabularies: ImportedVocabulary) {
        CoroutineScope(Dispatchers.IO).launch {
            for (vocab in vocabularies)
                AnkiPADroid.vocabularyDatabase.vocabularyDAO().delete(vocab)
        }
    }

    fun createFlashcards(
        apiKey: String,
        modelName: String,
        prompt: String,
        language: String,
        deckName: String,
        onFailure: (String?) -> Unit,
        vararg vocabularies: ImportedVocabulary
    ) {
        creatingCards.value = true
        val wordList = vocabularies.map { it.data }.toString()
        val finalPrompt = prompt
            .replace("{LANGUAGE}", language)
            .replace("{WORD_LIST}", wordList)

        CoroutineScope(Dispatchers.IO).launch {
            GeminiAPI.generateContent(
                apiKey = apiKey, modelName = modelName, prompt = finalPrompt,
                onSuccess = { content, tokens ->
                    val ankiApi = AddContentApi(AnkiPADroid.instance.applicationContext)
                    val deckId = ankiApi.deckList?.filter { it.value == deckName }?.map { it.key }?.get(0)
                    if (deckId == null) {
                        onFailure("Deck not found")
                        return@generateContent
                    }
                    var successCards = 0
                    val cards = content?.split("\n")

                    cards?.forEach { card ->
                        val fields = card.split(";").toTypedArray()
                        if (fields.size >= 3) {
                            val word = fields[0]
                            val ankiFields = arrayOf(
                                fields[1].processForFlashcard(), fields[2].processForFlashcard()
                            )
                            val vocab = vocabularies.filter { it.data == word }.getOrNull(0)
                            if (vocab != null) {
                                val cardId = ankiApi.addNote(
                                    ankiApi.currentModelId,
                                    deckId,
                                    ankiFields,
                                    null
                                )
                                if (cardId != null) {
                                    vocab.flashcard = cardId
                                    println("Created a flashcard for word: $word")
                                    successCards++
                                }
                            }
                        }
                    }

                    AnkiPADroid.vocabularyDatabase.vocabularyDAO().updateAll(*vocabularies)

                    creatingCards.value = false
                    successCreation.value = true
                    cardsCreated.intValue = successCards
                    usedTokens.intValue = tokens ?: 0
                },
                onFailure = onFailure
            )
        }
    }
}

fun String.processForFlashcard(): String {
    return this.replace("\\*\\*(.*?)\\*\\*".toRegex(), "<b>\$1</b>")
}