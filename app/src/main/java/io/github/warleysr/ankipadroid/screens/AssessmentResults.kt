package io.github.warleysr.ankipadroid.screens

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
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import io.github.warleysr.ankipadroid.ConfigUtils
import io.github.warleysr.ankipadroid.R
import io.github.warleysr.ankipadroid.viewmodels.PronunciationViewModel
import io.github.warleysr.ankipadroid.viewmodels.SettingsViewModel

@Composable
fun AssessmentResults(
    settingsViewModel: SettingsViewModel,
    pronunciationViewModel: PronunciationViewModel,
) {
    val result = pronunciationViewModel.getPronunciationResult()!!

    val scores1 = mapOf(
        Pair(stringResource(id = R.string.pronunciation), result.pronunciation),
        Pair(stringResource(id = R.string.prosody), result.prosody),
    )
    val scores2 = mapOf(
        Pair(stringResource(id = R.string.accuracy), result.accuracy),
        Pair(stringResource(id = R.string.fluency), result.fluency),
        Pair(stringResource(id = R.string.completeness), result.completeness),
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
        percentages[score.key] = currentPercentage
    }

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }
    Column(
        modifier = Modifier
            .fillMaxWidth().fillMaxHeight()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            scores1.forEach { score ->
                ScoreGauge(
                    title=score.key, score=score.value, floatAnimation=percentages[score.key]!!
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
                    title=score.key, score=score.value, floatAnimation=percentages[score.key]!!
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        FlowRow(
            horizontalGap = 4.dp,
            verticalGap = 4.dp,
            alignment = Alignment.CenterHorizontally,
        ) {
            result.words.forEach { word ->
                Text(
                    text = word.word,
                    color = colorByPercentage(word.accuracy),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    style = TextStyle(
                        textDecoration = TextDecoration.Underline,
                        lineBreak = LineBreak.Simple
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                        }
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Row() {
                SmallFloatingActionButton(onClick = {
                    pronunciationViewModel.exitResults()
                }) {
                    Icon(Icons.Filled.ArrowBack, null)
                }
                Spacer(modifier = Modifier.width(8.dp))
                FloatingActionButton(onClick = {
                    pronunciationViewModel.replayVoice()
                }) {
                    Icon(Icons.Filled.PlayCircle, null)
                }
                Spacer(modifier = Modifier.width(8.dp))
                FloatingActionButton(onClick = {

                    val azureKey = settingsViewModel.getSetting("azure_key")
                    val language = settingsViewModel.getSetting("language")
                    val region = settingsViewModel.getSetting("region")

                    pronunciationViewModel.playTTS(
                        referenceText = pronunciationViewModel.referenceText,
                        voiceName = ConfigUtils.getVoiceByLanguage(language),
                        speechApiKey = azureKey,
                        speechRegion = region
                    )
                }) {
                    Icon(Icons.Filled.RecordVoiceOver, null)
                }
            }
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
