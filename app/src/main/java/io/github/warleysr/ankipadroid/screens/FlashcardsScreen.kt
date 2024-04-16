package io.github.warleysr.ankipadroid.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.warleysr.ankipadroid.R
import io.github.warleysr.ankipadroid.viewmodels.PronunciationViewModel
import io.github.warleysr.ankipadroid.viewmodels.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun FlashcardsScreen(settingsViewModel: SettingsViewModel, pronunciationViewModel: PronunciationViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var success by remember { mutableStateOf(false) }
    var currentStatus by remember { mutableStateOf("Click here") }
    var currentInput by remember { mutableStateOf("How are you?") }
    var performing by remember { mutableStateOf(false) }
    var recording by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        if (!success) {
            Column {

                OutlinedTextField(
                    currentInput,
                    label = { Text("Text to pronounce") },
                    onValueChange = {
                        currentInput = it
                    },
                    singleLine = true,
                    enabled = !performing
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row (
                    modifier = Modifier.padding(16.dp)
                ) {
                    Button(
                        enabled = !performing,
                        onClick = {
                            coroutineScope.launch {
                                performing = true
                                val azureKey = settingsViewModel.getSetting("azure_key")

                                pronunciationViewModel.newAssessment(
                                    currentInput,
                                    language = "en-US",
                                    speechApiKey = azureKey,
                                    speechRegion = "brazilsouth",
                                    onResult = { result ->
                                        success = result
                                        currentStatus = if (result) "OK" else "Canceled"
                                        performing = false
                                    }
                                )
                            }
                        }
                    ) {
                        Text(text = currentStatus)
                    }
                    Button (
                        enabled = !performing,
                        onClick = {
                            recording = !recording
                            if (recording) {
                                pronunciationViewModel.startRecording()
                            }
                            else {
                                pronunciationViewModel.stopRecording()
                            }
                        }
                    ) {
                        Text(if (recording) "Stop" else "Record")
                    }
                }
            }
        } else {
            PronunciationAssessmentResults(pronunciationViewModel)
        }
    }
}