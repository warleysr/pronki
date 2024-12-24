package io.github.warleysr.pronki.screens.flashcards

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.Balloon
import com.skydoves.balloon.compose.rememberBalloonBuilder
import io.github.warleysr.pronki.R

@Composable
fun ScoreGauge(
    title: String,
    metricName: String,
    score: Float,
    floatAnimation: State<Float>
) {

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
                    color = colorByPercentage(score),
                    -90f,
                    360 * floatAnimation.value,
                    useCenter = false,
                    style = Stroke(4.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Text(
                text = (score * 100).toInt().toString() + "%",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Balloon(
            builder = builder,
            balloonContent = {
                Text(
                    stringResource(getDescriptionResource(metricName)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                )
            }
        ) { balloonWindow ->
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    balloonWindow.showAlignBottom()
                }
            )
        }
    }
    Spacer(modifier = Modifier.width(16.dp))
}

fun getDescriptionResource(metricName: String): Int {
    return when (metricName) {
        "pronunciation" -> R.string.description_pronunciation
        "prosody" -> R.string.description_prosody
        "accuracy" -> R.string.description_accuracy
        "fluency" -> R.string.description_fluency
        "completeness" -> R.string.description_completeness
        else -> R.string.description_accuracy
    }
}