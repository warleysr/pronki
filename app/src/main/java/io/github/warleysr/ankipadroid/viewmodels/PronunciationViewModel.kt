package io.github.warleysr.ankipadroid.viewmodels

import androidx.compose.runtime.mutableStateOf
import com.microsoft.cognitiveservices.speech.PronunciationAssessmentResult
import io.github.warleysr.ankipadroid.api.AzureAPI

class PronunciationViewModel {

    private var pronunciationResult = mutableStateOf<PronunciationAssessmentResult?>(null)

    fun newAssessment(
        referenceText: String,
        language: String,
        speechApiKey: String,
        speechRegion: String,
        onResult: (Boolean) -> Unit
    ) {
        AzureAPI.performPronunciationAssessment(
            referenceText, language, speechApiKey, speechRegion,
            onSuccess = {
                pronunciationResult.value = it
                onResult(true)
            },
            onFailure = {
                pronunciationResult.value = null
                onResult(false)
            }
        )

    }

    fun getPronunciationResult(): PronunciationAssessmentResult? {
        return pronunciationResult.value
    }
}
