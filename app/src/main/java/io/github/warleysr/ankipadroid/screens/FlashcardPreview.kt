package io.github.warleysr.ankipadroid.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun FlashcardPreview(
    settingsViewModel: SettingsViewModel,
    pronunciationViewModel: PronunciationViewModel,
    ankiDroidViewModel: AnkiDroidViewModel,
    onResult: (Boolean) -> Unit
) {
    var currentQuestion by remember { mutableStateOf("Waiting...") }
    var currentAnswer by remember { mutableStateOf("Waiting...") }
    var deckSelected by remember { mutableStateOf(false) }
    var showAnswer by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            if (deckSelected || ankiDroidViewModel.isDeckSelected) {
                ankiDroidViewModel.queryNextCard(
                    onResult = { question, answer ->
                        currentQuestion = question
                        currentAnswer = answer
                    }
                )
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp),
                    onClick = { showAnswer = !showAnswer }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .weight(1f, false)
                    ) {
                        Text(currentQuestion, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Divider()
                Spacer(Modifier.height(16.dp))
                if (showAnswer) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(128.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .weight(1f, false)
                        ) {
                            Text(currentAnswer, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
                RecordFAB(
                    settingsViewModel, pronunciationViewModel, ankiDroidViewModel, onResult,
                    onExit = { deckSelected = false }
                )
            } else {
                Text("Select a deck", style = MaterialTheme.typography.headlineMedium)

                ankiDroidViewModel.getDeckList()?.forEach { deck ->
                    Button(onClick = {
                        ankiDroidViewModel.selectDeck(deck)
                        deckSelected = true
                    }) {
                        Text(deck)
                    }
                }
            }
        }
    }
}