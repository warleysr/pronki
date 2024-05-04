package io.github.warleysr.ankipadroid.screens.settings

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.warleysr.ankipadroid.viewmodels.SettingsViewModel

@Composable
fun HighlighterColorPicker(viewModel: SettingsViewModel) {

    val rangeColors = viewModel.getRangeColors()
    var lowerHSV by remember { mutableStateOf(rangeColors.first) }
    var upperHSV by remember { mutableStateOf(rangeColors.second) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { viewModel.saveRangeColors(lower = lowerHSV, upper = upperHSV) }
            ) {
                Text("Save")
            }
        }
        Box(
            modifier = Modifier
                .defaultMinSize(minHeight = 256.dp)
                .fillMaxWidth()
                .border(2.dp, Color.Black)
        )
        HSVLimitSelector(
            title = "Lower color",
            hsvColor = lowerHSV,
            onColorChange = { lowerHSV = it }
        )

        Spacer(Modifier.height(16.dp))

        HSVLimitSelector(
            title = "Upper color",
            hsvColor = upperHSV,
            onColorChange = { upperHSV = it },
            minimumValues = lowerHSV
        )
    }
}

@Composable
fun HSVLimitSelector(
    title: String,
    hsvColor: HSVColor,
    onColorChange: (HSVColor) -> Unit,
    minimumValues: HSVColor? = null
) {
    Text(title)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically)  {
                var newH = hsvColor.min(minimumValues).H
                if (newH != hsvColor.H)
                    onColorChange(HSVColor(newH, hsvColor.S, hsvColor.V))

                Text("H")
                Slider(
                    modifier = Modifier.weight(1f),
                    value = newH,
                    onValueChange = {
                        val targetColor = HSVColor(it, hsvColor.S, hsvColor.V)
                        newH = targetColor.min(minimumValues).H
                        onColorChange(HSVColor(newH, hsvColor.S, hsvColor.V))
                    },
                    valueRange = 0f..360f,
                    steps = 360
                )
                Text(newH.toInt().toString())
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                var newS = hsvColor.min(minimumValues).S
                if (newS != hsvColor.S)
                    onColorChange(HSVColor(hsvColor.H, newS, hsvColor.V))

                Text("S")
                Slider(
                    modifier = Modifier.weight(1f),
                    value = newS,
                    onValueChange = {
                        val targetColor = HSVColor(hsvColor.H, it, hsvColor.V)
                        newS = targetColor.min(minimumValues).S
                        onColorChange(HSVColor(hsvColor.H, newS, hsvColor.V))
                    },
                    steps = 100
                )
                Text(newS.times(100).toInt().toString())
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                var newV = hsvColor.min(minimumValues).V
                if (newV != hsvColor.V)
                    onColorChange(HSVColor(hsvColor.H, hsvColor.S, newV))

                Text("V")
                Slider(
                    modifier = Modifier.weight(1f),
                    value = newV,
                    onValueChange = {
                        val targetColor = HSVColor(hsvColor.H, hsvColor.S, it)
                        newV = targetColor.min(minimumValues).V
                        onColorChange(HSVColor(hsvColor.H, hsvColor.S, newV))
                    },
                    steps = 100
                )
                Text(newV.times(100).toInt().toString())
            }
        }
        Column {
            Canvas(
                Modifier
                    .defaultMinSize(minWidth = 64.dp, minHeight = 64.dp)
                    .clickable(onClick = {})
            ) {
                drawCircle(Color.hsv(hsvColor.H, hsvColor.S, hsvColor.V), radius = 64f)
            }
        }
    }
}

data class HSVColor(
    val H: Float,
    val S: Float,
    val V: Float
) {
    fun min(other: HSVColor?): HSVColor {
        if (other == null)
            return this
        return HSVColor(
            if (H < other.H) other.H else H,
            if (S < other.S) other.S else S,
            if (V < other.V) other.V else V
        )
    }

}
