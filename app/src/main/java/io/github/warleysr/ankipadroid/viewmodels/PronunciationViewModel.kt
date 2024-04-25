package io.github.warleysr.ankipadroid.viewmodels

import android.media.MediaPlayer
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.github.squti.androidwaverecorder.WaveRecorder
import com.microsoft.cognitiveservices.speech.PhonemeLevelTimingResult
import com.microsoft.cognitiveservices.speech.PronunciationAssessmentResult
import com.microsoft.cognitiveservices.speech.WordLevelTimingResult
import io.github.warleysr.ankipadroid.api.AzureAPI

class PronunciationViewModel(audioDir: String) : ViewModel() {

    private var pronunciationResult: PronunciationResult? = null
    private val audioPath = "$audioDir/record.wav"
    private var waveRecorder: WaveRecorder? = null
    var hasAssessmentSucceeded: MutableState<Boolean> = mutableStateOf(false)

    init {
        println("PronunciationViewModel initialized")
    }

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
                pronunciationResult = PronunciationResult(it)
                hasAssessmentSucceeded.value = true
                onResult(true)
            },
            onFailure = {
                pronunciationResult = null
                onResult(false)
            }
        )

    }

    fun getPronunciationResult(): PronunciationResult? {
        return pronunciationResult
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

    fun replayVoice() {
        val player = MediaPlayer()
        player.setDataSource(audioPath)
        player.prepare()
        player.start()
        player.setOnCompletionListener {
            player.release()
        }
    }

    fun exitResults() {
        pronunciationResult = null
        hasAssessmentSucceeded.value = false
    }

}

data class PronunciationResult(val result: PronunciationAssessmentResult) {

    val pronunciation = result.pronunciationScore.toFloat() / 100f
    val accuracy: Float = result.accuracyScore.toFloat() / 100f
    val fluency = result.fluencyScore.toFloat() / 100f
    val completeness = result.completenessScore.toFloat() / 100f
    val prosody = result.prosodyScore.toFloat() / 100f
    val words = result.words.map { word -> WordResult(word) }
}

data class WordResult(val wordResult: WordLevelTimingResult) {

    val word = wordResult.word
    val accuracy = wordResult.accuracyScore.toFloat() / 100f
    val error = wordResult.errorType
    val phonemes = wordResult.phonemes.map {phoneme -> PhonemeResult(phoneme)}
}

data class PhonemeResult(val phonemeResult: PhonemeLevelTimingResult) {

    val phoneme = phonemeResult.phoneme
    val accuracy = phonemeResult.accuracyScore.toFloat() / 100f
}
