package io.github.warleysr.ankipadroid.viewmodels

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
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
    private var audioDataTTS: ByteArray? = null
    var referenceText: String = ""
        private set
    var hasAssessmentSucceeded: MutableState<Boolean> = mutableStateOf(false)
        private set
    var generatedTTS: MutableState<Boolean> = mutableStateOf(false)
        private set

    init {
        println("PronunciationViewModel initialized")
    }

    fun newAssessment(
        referenceText: String,
        language: String,
        speechApiKey: String,
        speechRegion: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        AzureAPI.performPronunciationAssessment(
            referenceText, language, speechApiKey, speechRegion,
            wavFilePath = audioPath,
            onSuccess = {
                pronunciationResult = PronunciationResult(it)
                hasAssessmentSucceeded.value = true
                this.referenceText = referenceText
                onSuccess()
            },
            onFailure = {
                pronunciationResult = null
                onFailure(it)
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

    fun replayVoice(onFinish: () -> Unit) {
        val player = MediaPlayer()
        player.setDataSource(audioPath)
        player.prepare()
        player.start()
        player.setOnCompletionListener {
            player.release()
            onFinish()
        }
    }

    fun playTTS(
        referenceText: String,
        voiceName: String,
        speechRegion: String,
        speechApiKey: String,
        onFinish: () -> Unit
    ) {
        if (generatedTTS.value)
            playTTSAudio(onFinish)
        else
            AzureAPI.generateTTS(
                referenceText, voiceName, speechApiKey, speechRegion,
                onResult = { audioData ->
                    audioDataTTS = audioData
                    generatedTTS.value = true
                    playTTSAudio(onFinish)
                }
            )
    }

    private fun playTTSAudio(onFinish: () -> Unit) {
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build())
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(24000)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build())
            .setBufferSizeInBytes(audioDataTTS!!.size)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(audioDataTTS!!, 0, audioDataTTS!!.size)

        audioTrack.setNotificationMarkerPosition(audioDataTTS!!.size / 2)
        audioTrack.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
            override fun onMarkerReached(track: AudioTrack?) { onFinish() }

            override fun onPeriodicNotification(track: AudioTrack?) {}
        })

        audioTrack.play()
}

    fun exitResults() {
        pronunciationResult = null
        audioDataTTS = null
        hasAssessmentSucceeded.value = false
        generatedTTS.value = false
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
