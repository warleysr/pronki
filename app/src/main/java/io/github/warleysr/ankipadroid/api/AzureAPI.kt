package io.github.warleysr.ankipadroid.api


import com.microsoft.cognitiveservices.speech.PronunciationAssessmentConfig
import com.microsoft.cognitiveservices.speech.PronunciationAssessmentGradingSystem
import com.microsoft.cognitiveservices.speech.PronunciationAssessmentGranularity
import com.microsoft.cognitiveservices.speech.PronunciationAssessmentResult
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails
import com.microsoft.cognitiveservices.speech.SpeechSynthesisOutputFormat
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer
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

        fun generateTTS(
            referenceText: String,
            voiceName: String,
            speechApiKey: String,
            speechRegion: String,
            onResult: (ByteArray) -> Unit
        ) {
            val speechConfig = SpeechConfig.fromSubscription(speechApiKey, speechRegion)
            speechConfig.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Raw24Khz16BitMonoPcm)
            speechConfig.speechSynthesisVoiceName = voiceName
            val synthesizer = SpeechSynthesizer(speechConfig, null)
            synthesizer.SpeakTextAsync(referenceText)

            synthesizer.SynthesisStarted.addEventListener { _, e ->
                println("TTS started!")
            }
            synthesizer.SynthesisCanceled.addEventListener { _, e ->
                val details = SpeechSynthesisCancellationDetails.fromResult(e.result)
                println("TTS cancelled! Reason: ${details.errorCode.value} - ${details.errorDetails}")
            }

            synthesizer.SynthesisCompleted.addEventListener { _, e ->
                println("TTS finished...")
                onResult(e.result.audioData)
            }
        }
    }
}