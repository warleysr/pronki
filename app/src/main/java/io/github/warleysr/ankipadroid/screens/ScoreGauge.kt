package io.github.warleysr.ankipadroid.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ScoreGauge(
    title: String,
    score: Float,
    floatAnimation: State<Float>
) {
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
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
    Spacer(modifier = Modifier.width(16.dp))
}