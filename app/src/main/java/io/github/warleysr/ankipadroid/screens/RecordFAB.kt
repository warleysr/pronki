package io.github.warleysr.ankipadroid.screens

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.warleysr.ankipadroid.viewmodels.AnkiDroidViewModel
import io.github.warleysr.ankipadroid.viewmodels.PronunciationViewModel
import io.github.warleysr.ankipadroid.viewmodels.SettingsViewModel

@Composable
fun RecordFAB(
    settingsViewModel: SettingsViewModel,
    pronunciationViewModel: PronunciationViewModel,
    ankiDroidViewModel: AnkiDroidViewModel,
    onResult: (Boolean) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    var performing by remember { mutableStateOf(false) }

    if (isPressed) {
        if (performing) return
        println("Pressed! Recording voice...")
        pronunciationViewModel.startRecording()

        DisposableEffect(Unit) {
            onDispose {
                println("Released! Starting assessment...")
                pronunciationViewModel.stopRecording()

                val referenceText = ankiDroidViewModel.currentQuestion
                val azureKey = settingsViewModel.getSetting("azure_key")
                val language = settingsViewModel.getSetting("language")
                val region = settingsViewModel.getSetting("region")

                pronunciationViewModel.newAssessment(
                    referenceText = referenceText!!,
                    language = language,
                    speechApiKey = azureKey,
                    speechRegion = region,
                    onResult = { result ->
                        onResult(result)
                        performing = false
                    }
                )
            }
        }
    }
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxWidth().fillMaxHeight()
    ) {
        FloatingActionButton(
            shape = CircleShape,
            onClick = { /*TODO*/ },
            interactionSource = interactionSource,

        ) {
            Icon(if (isPressed) Icons.Filled.Mic else Icons.Outlined.Mic, null)
        }
    }
}