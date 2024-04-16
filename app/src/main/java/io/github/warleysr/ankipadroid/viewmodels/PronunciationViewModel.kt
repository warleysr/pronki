package io.github.warleysr.ankipadroid.viewmodels

import android.media.MediaRecorder
import androidx.compose.runtime.mutableStateOf
import com.github.squti.androidwaverecorder.WaveRecorder
import com.microsoft.cognitiveservices.speech.PronunciationAssessmentResult
import io.github.warleysr.ankipadroid.api.AzureAPI

class PronunciationViewModel(audioDir: String) {

    private var pronunciationResult = mutableStateOf<PronunciationAssessmentResult?>(null)
    private val audioPath = "$audioDir/record.wav"
    private var waveRecorder: WaveRecorder? = null

    fun newAssessment(
        referenceText: String,
        language: String,
        speechApiKey: String,
        speechRegion: String,
        onResult: (Boolean) -> Unit
    ) {
        AzureAPI.performPronunciationAssessment(
            referenceText, language, speechApiKey, speechRegion,
            wavFilePath = audioPath,
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

    fun startRecording() {
        val waveRecorder = WaveRecorder(audioPath)
        waveRecorder.noiseSuppressorActive = true
        waveRecorder.startRecording()
        this.waveRecorder = waveRecorder
    }

    fun stopRecording() {
        this.waveRecorder!!.stopRecording()
        this.waveRecorder = null
    }
}
