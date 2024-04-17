package io.github.warleysr.ankipadroid.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltipBox
import androidx.compose.material3.RichTooltipState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.warleysr.ankipadroid.viewmodels.PronunciationViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PronunciationAssessmentResults(pronunciationViewModel: PronunciationViewModel) {
    val result = pronunciationViewModel.getPronunciationResult() ?: return
    val scores = mapOf(
        Pair("Accuracy", result.accuracy),
        Pair("Fluency", result.fluency),
        Pair("Completeness", result.completeness),
    )
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

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            scores.forEach { score ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(84.dp)
                    ) {
                        Canvas(
                            modifier = Modifier.size(84.dp)
                        ) {
                            drawArc(
                                color = Color.LightGray,
                                -90f,
                                360f,
                                useCenter = false,
                                style = Stroke(4.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = colorByPercentage(score.value),
                                -90f,
                                360 * (percentages[score.key]?.value ?: 0f),
                                useCenter = false,
                                style = Stroke(4.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        Text(
                            text = (score.value * 100).toInt().toString() + "%",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Text(
                        text = score.key,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            result.words.forEach { word ->
                val tooltipState = remember { RichTooltipState() }
                RichTooltipBox(
                    text = {
                       Row(
                           modifier = Modifier.padding(8.dp)
                       ){
                           word.phonemes.forEach {phoneme ->
                               Text(phoneme.phoneme, color = colorByPercentage(phoneme.accuracy), modifier = Modifier.padding(8.dp))
                           }
                       }
                    },
                    title = { Text(word.word, color = colorByPercentage(word.accuracy)) },
                    tooltipState = tooltipState,
                    action = {},
                ) {
                    Text(
                        text = word.word,
                        color = colorByPercentage(word.accuracy),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        style = TextStyle(textDecoration = TextDecoration.Underline),
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                scope.launch {
                                    tooltipState.show()
                                }
                            }
                    )
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
