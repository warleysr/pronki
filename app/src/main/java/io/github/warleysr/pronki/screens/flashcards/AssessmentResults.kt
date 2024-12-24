package io.github.warleysr.pronki.screens.flashcards

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.Balloon
import com.skydoves.balloon.compose.rememberBalloonBuilder
import io.github.warleysr.pronki.ConfigUtils
import io.github.warleysr.pronki.R
import io.github.warleysr.pronki.viewmodels.PronunciationViewModel
import io.github.warleysr.pronki.viewmodels.SettingsViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AssessmentResults(
    settingsViewModel: SettingsViewModel,
    pronunciationViewModel: PronunciationViewModel,
) {
    Scaffold(
        content = { AssessmentResultsContent(pronunciationViewModel) },
        floatingActionButton = { AssessmentResultsFAB(settingsViewModel, pronunciationViewModel) }
    )
}
@Composable
fun AssessmentResultsContent(
    pronunciationViewModel: PronunciationViewModel,
) {
    val result = pronunciationViewModel.getPronunciationResult()!!

    val scores1 = mapOf(
        Pair("pronunciation", stringResource(id = R.string.pronunciation)) to result.pronunciation,
        Pair("prosody", stringResource(id = R.string.prosody)) to result.prosody,
    )
    val scores2 = mapOf(
        Pair("accuracy", stringResource(id = R.string.accuracy)) to result.accuracy,
        Pair("fluency", stringResource(id = R.string.fluency)) to result.fluency,
        Pair("completeness", stringResource(id = R.string.completeness)) to result.completeness,
    )
    val scores = scores1 + scores2

    var animationPlayed by remember { mutableStateOf(false) }

    val percentages = HashMap<String, State<Float>>()
    scores.forEach {score ->
        val currentPercentage = animateFloatAsState(
            targetValue = if (animationPlayed) score.value else 0f,
            animationSpec = tween(
                durationMillis = 1500,
                delayMillis = 10
            ), label = "FloatAnimation"
        )
        percentages[score.key.first] = currentPercentage
    }

    val builder = rememberBalloonBuilder {
        setArrowSize(10)
        setArrowPosition(0.5f)
        setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
        setWidth(BalloonSizeSpec.WRAP)
        setHeight(BalloonSizeSpec.WRAP)
        setPadding(12)
        setMarginHorizontal(12)
        setCornerRadius(8f)
        setBalloonAnimation(BalloonAnimation.ELASTIC)
    }

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            scores1.forEach { score ->
                ScoreGauge(
                    title = score.key.second,
                    metricName = score.key.first,
                    score = score.value,
                    floatAnimation = percentages[score.key.first]!!
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            scores2.forEach { score ->
                ScoreGauge(
                    title = score.key.second,
                    metricName = score.key.first,
                    score = score.value,
                    floatAnimation = percentages[score.key.first]!!
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        FlowRow(
            alignment = Alignment.CenterHorizontally,
        ) {
            result.words.forEach { word ->
                Balloon(
                    builder = builder,
                    balloonContent = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(errorName(word.error), color = errorColor(word.error))
                            Row(modifier = Modifier.padding(8.dp)) {
                                word.phonemes.forEachIndexed { _, phoneme ->
                                    Text(
                                        phoneme.phoneme,
                                        color = colorByPercentage(phoneme.accuracy)
                                    )
                                }
                            }
                        }
                    }
                ) { balloonWindow ->
                    Text(
                        text = word.word,
                        color = if (word.error == "None") colorByPercentage(word.accuracy) else errorColor(word.error),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        style = TextStyle(
                            textDecoration = TextDecoration.Underline,
                            lineBreak = LineBreak.Simple
                        ),
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                balloonWindow.showAlignBottom()
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun AssessmentResultsFAB(
    settingsViewModel: SettingsViewModel,
    pronunciationViewModel: PronunciationViewModel,
) {
    var playingAudio by remember { mutableStateOf(false) }
    Row {
        SmallFloatingActionButton(onClick = {
            pronunciationViewModel.exitResults()
        }) {
            Icon(Icons.Filled.ArrowBack, null)
        }
        Spacer(modifier = Modifier.width(8.dp))
        FloatingActionButton(onClick = {
            if (playingAudio) return@FloatingActionButton
            playingAudio = true

            pronunciationViewModel.replayVoice(onFinish = { playingAudio = false })
        }) {
            Icon(Icons.Filled.PlayCircle, null)
        }
        Spacer(modifier = Modifier.width(8.dp))
        FloatingActionButton(onClick = {
            if (playingAudio) return@FloatingActionButton
            playingAudio = true

            val azureKey = settingsViewModel.getSetting("azure_key")
            val language = settingsViewModel.getSetting("language")
            val region = settingsViewModel.getSetting("region")

            pronunciationViewModel.playTTS(
                referenceText = pronunciationViewModel.referenceText,
                voiceName = ConfigUtils.getVoiceByLanguage(language),
                speechApiKey = azureKey,
                speechRegion = region,
                onFinish = { playingAudio = false }
            )
        }) {
            Icon(Icons.Filled.RecordVoiceOver, null)
        }
    }
}

fun colorByPercentage(percentage: Float): Color {
    return if (percentage < 0.3f)
        Color.Red
    else if (percentage < 0.5f)
        Color(red = 237, green = 97, blue = 16)
    else if (percentage < 0.7f)
        Color(red = 252, green = 211, blue = 3)
    else
        Color(red = 6, green = 162, blue = 6, alpha = 255)
}

fun errorName(error: String): String {
    return if (error == "None")
        "Correct"
    else
        error
}

fun errorColor(error: String): Color {
    return when (error) {
        "Omission" -> Color(red = 156, green = 39, blue = 176, alpha = 255)
        "Insertion" -> Color(red = 18, green = 75, blue = 226, alpha = 233)
        "Mispronunciation" -> Color.Red
        else -> Color(red = 6, green = 162, blue = 6, alpha = 255)
    }
}
