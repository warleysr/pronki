package io.github.warleysr.ankipadroid.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.warleysr.ankipadroid.AnkiDroidHelper
import io.github.warleysr.ankipadroid.R
import io.github.warleysr.ankipadroid.viewmodels.AnkiDroidViewModel
import io.github.warleysr.ankipadroid.viewmodels.PronunciationViewModel
import io.github.warleysr.ankipadroid.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardPreview(
    settingsViewModel: SettingsViewModel,
    pronunciationViewModel: PronunciationViewModel,
    ankiDroidViewModel: AnkiDroidViewModel
) {
    var currentQuestion by remember { mutableStateOf("Waiting...") }
    var currentAnswer by remember { mutableStateOf("Waiting...") }
    var deckSelected by remember { mutableStateOf(false) }
    var showAnswer by remember { mutableStateOf(false) }

    val onResult: (String, String) -> Unit = {question, answer ->
        currentQuestion = question
        currentAnswer = answer
    }

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
            if (ankiDroidViewModel.isDeckSelected.value) {
                ankiDroidViewModel.queryNextCard(onResult = onResult)
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 128.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    onClick = { showAnswer = !showAnswer }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .weight(1f, false)
                    ) {
                        Text(currentQuestion, style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                Divider()
                Spacer(Modifier.height(16.dp))

                AnimatedVisibility(visible = showAnswer) {
                    Column {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 128.dp)
                            ,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .verticalScroll(rememberScrollState())
                                    .weight(1f, false)
                            ) {
                                Text(
                                    currentAnswer, style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(16.dp),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        Divider()
                        Spacer(Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    ankiDroidViewModel.reviewCard(
                                        AnkiDroidHelper.EASE_1,
                                        onNextResult = onResult
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFD32F2F),
                                    contentColor = Color.White
                                )
                            ) {
                                Text(stringResource(id = R.string.again))
                            }
                            Button(
                                onClick = {
                                    ankiDroidViewModel.reviewCard(
                                        AnkiDroidHelper.EASE_2,
                                        onNextResult = onResult
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF455A64),
                                    contentColor = Color.White
                                )
                            ) {
                                Text(stringResource(id = R.string.hard))
                            }
                            Button(
                                onClick = {
                                    ankiDroidViewModel.reviewCard(
                                        AnkiDroidHelper.EASE_3,
                                        onNextResult = onResult
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAF50),
                                    contentColor = Color.White
                                )
                            ) {
                                Text(stringResource(id = R.string.good))
                            }
                            Button(
                                onClick = {
                                    ankiDroidViewModel.reviewCard(
                                        AnkiDroidHelper.EASE_4,
                                        onNextResult = onResult
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF03A9F4),
                                    contentColor = Color.White
                                )
                            ) {
                                Column {
                                    Text(stringResource(id = R.string.easy))
                                }
                            }
                        }
                    }
                }

                RecordFAB(
                    settingsViewModel, pronunciationViewModel, ankiDroidViewModel,
                    onBackUse = { showAnswer = true },
                    onExit = { deckSelected = false }
                )
            } else {
                Text(stringResource(id = R.string.select_deck), style = MaterialTheme.typography.headlineMedium)

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