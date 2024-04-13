package io.github.warleysr.ankipadroid.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.warleysr.ankipadroid.viewmodels.PronunciationViewModel

@Composable
fun PronunciationAssessmentResults(pronunciationViewModel: PronunciationViewModel) {
    val result = pronunciationViewModel.getPronunciationResult() ?: return
    val scores = mapOf(
        Pair("Accuracy", result.accuracyScore / 100),
        Pair("Fluency", result.fluencyScore / 100),
        Pair("Completeness", result.completenessScore / 100),
    )
    var animationPlayed by remember { mutableStateOf(false) }
    val percentages = HashMap<String, State<Float>>()
    scores.forEach {score ->
        val currentPercentage = animateFloatAsState(
            targetValue = if (animationPlayed) score.value.toFloat() else 0f,
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

    Row (
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ){
        scores.forEach { score ->
            Column (
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
                            color = colorByPercentage(score.value.toFloat()),
                            -90f,
                            360 * (percentages[score.key]?.value ?: 0f),
                            useCenter = false,
                            style = Stroke(4.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Text (
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
}

fun colorByPercentage(percentage: Float): Color {
    return if (percentage < 0.3f)
        Color.Red
    else if (percentage < 0.5f)
        Color(red=237, green=97, blue=16)
    else if (percentage < 0.7f)
        Color(red=252, green=211, blue=3)
    else
        Color.Green
}
