package io.github.warleysr.pronki.viewmodels

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ichi2.anki.api.AddContentApi
import io.github.warleysr.pronki.PronKi
import io.github.warleysr.pronki.api.GeminiAPI
import io.github.warleysr.pronki.api.ImportedVocabulary
import io.github.warleysr.pronki.api.OpenCV
import io.github.warleysr.pronki.screens.settings.HSVColor
import io.github.warleysr.pronki.screens.vocabulary.VocabularyState
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

    var showingImportList = mutableStateOf(false)
        private set

    var cardsCreated = mutableIntStateOf(0)
        private set

    var usedTokens = mutableIntStateOf(0)
        private set

    var allWords = mutableStateListOf<VocabularyState>()
        private set

    var importList = mutableStateOf("")
        private set

    init {
        println("VocabularyViewModel initialized!")
        permissionCameraGranted.value = PronKi.instance.checkSelfPermission(
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun processBitmap(
        bitmap: Bitmap, rotation: Int, lower: Scalar, upper: Scalar, defaultLanguage: String
    ) {
        this.bitmap.value = bitmap
        OpenCV.processImage(
            this.bitmap.value!!, rotation, lower, upper, defaultLanguage,
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

    fun showImportList() {
        showingImportList.value = true
    }

    fun hideImportList() {
        showingImportList.value = false
    }

    fun updateImportList(text: String) {
        importList.value = text
    }

    fun insertVocabulary(vararg vocabularies: ImportedVocabulary) {
        CoroutineScope(Dispatchers.IO).launch {
            PronKi.vocabularyDatabase.vocabularyDAO().insertAll(*vocabularies)
        }
    }

    fun getVocabularyList(onFinish: (List<ImportedVocabulary>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val vocabList = PronKi.vocabularyDatabase.vocabularyDAO().getAllNew()
            onFinish(vocabList)
        }
    }

    fun deleteVocabulary(vararg vocabularies: ImportedVocabulary) {
        CoroutineScope(Dispatchers.IO).launch {
            for (vocab in vocabularies)
                PronKi.vocabularyDatabase.vocabularyDAO().delete(vocab)
        }
    }

    fun createFlashcards(
        apiKey: String,
        modelName: String,
        prompt: String,
        language: String,
        deckName: String,
        onSuccess: () -> Unit,
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
                    val ankiApi = AddContentApi(PronKi.instance.applicationContext)
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

                    PronKi.vocabularyDatabase.vocabularyDAO().updateAll(*vocabularies)

                    creatingCards.value = false
                    successCreation.value = true
                    cardsCreated.intValue = successCards
                    usedTokens.intValue = tokens ?: 0
                    onSuccess()
                },
                onFailure = onFailure
            )
        }
    }
}

fun String.processForFlashcard(): String {
    return this.replace("\\*\\*(.*?)\\*\\*".toRegex(), "<b>\$1</b>")
}