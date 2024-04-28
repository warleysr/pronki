package io.github.warleysr.ankipadroid.screens

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FlipToBack
import androidx.compose.material.icons.filled.FlipToFront
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.warleysr.ankipadroid.viewmodels.AnkiDroidViewModel
import io.github.warleysr.ankipadroid.viewmodels.PronunciationViewModel
import io.github.warleysr.ankipadroid.viewmodels.SettingsViewModel

@Composable
fun RecordFAB(
    settingsViewModel: SettingsViewModel,
    pronunciationViewModel: PronunciationViewModel,
    ankiDroidViewModel: AnkiDroidViewModel,
    onBackUse: () -> Unit,
    onExit: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    var performing by remember { mutableStateOf(false) }
    var useFront by remember { mutableStateOf(true) }

    if (isPressed) {
        if (performing) return
        println("Pressed! Recording voice...")
        pronunciationViewModel.startRecording()

        DisposableEffect(Unit) {
            onDispose {
                println("Released! Starting assessment...")
                pronunciationViewModel.stopRecording()

                val referenceText = if (useFront) ankiDroidViewModel.cardInfo!!.question else ankiDroidViewModel.cardInfo!!.answer
                val azureKey = settingsViewModel.getSetting("azure_key")
                val language = settingsViewModel.getSetting("language")
                val region = settingsViewModel.getSetting("region")

                pronunciationViewModel.newAssessment(
                    referenceText = referenceText!!,
                    language = language,
                    speechApiKey = azureKey,
                    speechRegion = region,
                    onResult = { result ->
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
        Row {
            SmallFloatingActionButton(
                onClick = {
                    ankiDroidViewModel.exitFlashcardPreview()
                    onExit()
                }
            ) {
                Icon(Icons.Filled.ArrowBack, null)
            }
            Spacer(Modifier.width(8.dp))

            SmallFloatingActionButton(
                onClick = {
                    ankiDroidViewModel.toggleCardField(
                        onToggle = {
                            newValue -> useFront = newValue
                            if (!useFront)
                                onBackUse()
                        }
                    )
                }
            ) {
                Icon(if (useFront) Icons.Filled.FlipToFront else Icons.Filled.FlipToBack, null)
            }
            Spacer(Modifier.width(8.dp))

            FloatingActionButton(
                shape = CircleShape,
                onClick = { },
                interactionSource = interactionSource,
            ) {
                Icon(if (isPressed) Icons.Filled.Mic else Icons.Outlined.Mic, null)
            }
        }
    }
}