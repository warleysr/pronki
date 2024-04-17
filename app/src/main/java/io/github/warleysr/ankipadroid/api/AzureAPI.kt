package io.github.warleysr.ankipadroid.api

import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.MediaRecorder.AudioSource
import com.microsoft.cognitiveservices.speech.PronunciationAssessmentConfig
import com.microsoft.cognitiveservices.speech.PronunciationAssessmentGradingSystem
import com.microsoft.cognitiveservices.speech.PronunciationAssessmentGranularity
import com.microsoft.cognitiveservices.speech.PronunciationAssessmentResult
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import com.microsoft.cognitiveservices.speech.audio.AudioConfig


class AzureAPI {
    companion object {
        fun performPronunciationAssessment(
            referenceText: String,
            language: String,
            speechApiKey: String,
            speechRegion: String,
            wavFilePath: String,
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
            pronunciationConfig.setPhonemeAlphabet("IPA")
            pronunciationConfig.enableProsodyAssessment()

            val audioConfig = AudioConfig.fromWavFileInput(wavFilePath)

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