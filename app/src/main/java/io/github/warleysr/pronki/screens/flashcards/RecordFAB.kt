package io.github.warleysr.pronki.screens.flashcards

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.text.parseAsHtml
import io.github.warleysr.pronki.R
import io.github.warleysr.pronki.viewmodels.AnkiDroidViewModel
import io.github.warleysr.pronki.viewmodels.PronunciationViewModel
import io.github.warleysr.pronki.viewmodels.SettingsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RecordFAB(
    settingsViewModel: SettingsViewModel,
    pronunciationViewModel: PronunciationViewModel,
    ankiDroidViewModel: AnkiDroidViewModel,
    onBackUse: () -> Unit,
    onExit: () -> Unit,
    onFailure: (String) -> Unit,
    onPermissionDenied: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    var recording by remember { mutableStateOf(false) }
    var cancelled by remember { mutableStateOf(false) }
    var performing by remember { mutableStateOf(false) }
    var useFront by remember { mutableStateOf(true) }

    val viewConfiguration = LocalViewConfiguration.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted)
                pronunciationViewModel.audioPermissionGranted()
            else
                onPermissionDenied()
        }
    )

    val configureAzure = stringResource(R.string.configure_azure_properly)
    val holdRecord = stringResource(R.string.hold_record)
    LaunchedEffect(interactionSource) {

        interactionSource.interactions.collectLatest { interaction ->
            val azureKey = settingsViewModel.getSetting("azure_key")
            val language = settingsViewModel.getSetting("language")
            val region = settingsViewModel.getSetting("region")
            val isAzureConfigured = (
                    azureKey.isNotEmpty() && language.isNotEmpty() && region.isNotEmpty()
            )
            when (interaction) {
                is PressInteraction.Press -> {
                    if (!pronunciationViewModel.permissionAudioGranted.value)
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)

                    else if (!isAzureConfigured)
                        onFailure(configureAzure)

                    else {
                        delay(viewConfiguration.longPressTimeoutMillis)
                        println("Recording...")
                        recording = true

                        pronunciationViewModel.startRecording()
                    }
                }

                is PressInteraction.Release -> {
                    if (
                        !pronunciationViewModel.permissionAudioGranted.value || !isAzureConfigured
                    )
                        return@collectLatest

                    if (!recording) {
                        onFailure(holdRecord)
                        return@collectLatest
                    }

                    println("Released! Starting assessment...")
                    pronunciationViewModel.stopRecording()
                    recording = false
                    performing = true

                    val referenceText =
                        if (useFront)
                            ankiDroidViewModel.selectedCard.value!!.question.parseAsHtml().toString()
                        else
                            ankiDroidViewModel.selectedCard.value!!.answer.parseAsHtml().toString()

                    pronunciationViewModel.newAssessment(
                        referenceText = referenceText,
                        language = language,
                        speechApiKey = azureKey,
                        speechRegion = region,
                        onSuccess = { performing = false },
                        onFailure = {
                            performing = false
                            onFailure(it)
                        }
                    )
                }

                is PressInteraction.Cancel -> {
                    if (
                        !pronunciationViewModel.permissionAudioGranted.value || !isAzureConfigured
                    )
                        return@collectLatest

                    println("Cancelled...")
                    pronunciationViewModel.stopRecording()
                    recording = false
                    cancelled = true
                }
            }
        }
    }

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
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
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