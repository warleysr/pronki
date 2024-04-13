package io.github.warleysr.ankipadroid.api

import com.microsoft.cognitiveservices.speech.PronunciationAssessmentConfig
import com.microsoft.cognitiveservices.speech.PronunciationAssessmentGradingSystem
import com.microsoft.cognitiveservices.speech.PronunciationAssessmentGranularity
import com.microsoft.cognitiveservices.speech.PronunciationAssessmentResult
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import com.microsoft.cognitiveservices.speech.audio.AudioConfig
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit


class AzureAPI {
    companion object {
        fun performPronunciationAssessment(
            referenceText: String,
            language: String,
            speechApiKey: String,
            speechRegion: String,
            onSuccess: (PronunciationAssessmentResult) -> Unit,
            onFailure: (String) -> Unit
        ) {

            val speechConfig = SpeechConfig.fromSubscription(speechApiKey, speechRegion)
            val pronunciationConfig = PronunciationAssessmentConfig(
                referenceText,
                PronunciationAssessmentGradingSystem.HundredMark,
                PronunciationAssessmentGranularity.Phoneme,
                false
            )
            pronunciationConfig.enableProsodyAssessment()

            val audioConfig = AudioConfig.fromStreamInput(MicrophoneStream.create())

            val speechRecognizer = SpeechRecognizer(
                speechConfig,
                language,
                audioConfig
            )

            pronunciationConfig.applyTo(speechRecognizer)
            speechRecognizer.recognizeOnceAsync()

            speechRecognizer.recognized.addEventListener { _, e ->
                 onSuccess(PronunciationAssessmentResult.fromResult(e.result))
            }
            speechRecognizer.canceled.addEventListener { _, e ->
                onFailure(e.result.text)
            }
        }
    }
}