package io.github.warleysr.ankipadroid.screens.flashcards

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FlipToBack
import androidx.compose.material.icons.filled.FlipToFront
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.unit.dp
import io.github.warleysr.ankipadroid.viewmodels.AnkiDroidViewModel
import io.github.warleysr.ankipadroid.viewmodels.PronunciationViewModel
import io.github.warleysr.ankipadroid.viewmodels.SettingsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RecordFAB(
    settingsViewModel: SettingsViewModel,
    pronunciationViewModel: PronunciationViewModel,
    ankiDroidViewModel: AnkiDroidViewModel,
    onBackUse: () -> Unit,
    onExit: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    var recording by remember { mutableStateOf(false) }
    var cancelled by remember { mutableStateOf(false) }
    var performing by remember { mutableStateOf(false) }
    var useFront by remember { mutableStateOf(true) }


    val viewConfiguration = LocalViewConfiguration.current

    LaunchedEffect(interactionSource) {

        interactionSource.interactions.collectLatest { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    delay(viewConfiguration.longPressTimeoutMillis)
                    println("Recording...")
                    recording = true

                    pronunciationViewModel.startRecording()
                }

                is PressInteraction.Release -> {
                    println("Released! Starting assessment...")
                    pronunciationViewModel.stopRecording()

                    recording = false
                    performing = true

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

                is PressInteraction.Cancel -> {
                    println("Cancelled...")
                    pronunciationViewModel.stopRecording()
                    recording = false
                    cancelled = true
                }
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
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

            val sizeState = animateDpAsState(
                targetValue = if (recording) 84.dp else 56.dp,
                animationSpec = tween(durationMillis = 300, delayMillis = 5),
                label = ""
            )

            FloatingActionButton(
                shape = CircleShape,
                onClick = { },
                interactionSource = interactionSource,
                containerColor = if (recording) Color.LightGray else FloatingActionButtonDefaults.containerColor,
                modifier = Modifier
                    .defaultMinSize(
                        minWidth = sizeState.value,
                        minHeight = sizeState.value
                    )
            ) {
                if (performing) {
                    CircularProgressIndicator(strokeWidth = 2.dp)
                } else {
                    Icon(
                        Icons.Filled.Mic, null,
                        modifier = Modifier.defaultMinSize(
                            minWidth = sizeState.value.div(2),
                            minHeight = sizeState.value.div(2)
                        )
                    )
                }
            }
        }
    }
}