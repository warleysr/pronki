package io.github.warleysr.ankipadroid.screens.vocabulary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.warleysr.ankipadroid.R
import io.github.warleysr.ankipadroid.viewmodels.SettingsViewModel
import io.github.warleysr.ankipadroid.viewmodels.VocabularyViewModel
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.style.TextAlign
import io.github.warleysr.ankipadroid.api.ankidroid.AnkiDroidAPI
import io.github.warleysr.ankipadroid.viewmodels.AnkiDroidViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyOptionsBar(
    settingsViewModel: SettingsViewModel,
    vocabularyViewModel: VocabularyViewModel,
    ankiDroidViewModel: AnkiDroidViewModel,
    visible: Boolean,
    selectedVocabs: MutableIntState,
    vocabList: SnapshotStateList<VocabularyState>,
    onFailure: (String) -> Unit
) {
    var isDeleteDialogShown by remember { mutableStateOf(false) }
    var isCreateDialogShown by remember { mutableStateOf(false) }
    var expandedDecks by remember { mutableStateOf(false) }
    var selectedDeck by remember { mutableStateOf("") }

    if (isDeleteDialogShown) {
        AlertDialog(
            onDismissRequest = { isDeleteDialogShown = false },
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Surface {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Are you sure you want to delete ${selectedVocabs.intValue} vocabularies?")
                    Spacer(Modifier.height(16.dp))
                    Text(
                        vocabList.filter { it.selected.value }.joinToString { it.vocabulary.data },
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        modifier = Modifier.align(Alignment.End),
                        onClick = {
                            vocabularyViewModel.deleteVocabulary(
                                *vocabList
                                    .filter { it.selected.value }
                                    .map { it.vocabulary }
                                    .toTypedArray()
                            )
                            vocabList.removeIf { it.selected.value }
                            isDeleteDialogShown = false
                            selectedVocabs.intValue = 0
                        }
                    ) {
                        Text("Yes")
                    }
                }
            }
        }
    }

    if (isCreateDialogShown) {
        AlertDialog(
            onDismissRequest = {
                if (!vocabularyViewModel.creatingCards.value) {
                    isCreateDialogShown = false
                    vocabularyViewModel.hideSuccessDialog()
                }
            },
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Surface {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (vocabularyViewModel.creatingCards.value) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(16.dp))
                        Text("Creating cards, please wait...")
                    } else if (vocabularyViewModel.successCreation.value) {
                        Icon(
                            Icons.Filled.Done, null,
                            tint = MaterialTheme.colorScheme.surfaceTint,
                            modifier = Modifier.defaultMinSize(minHeight = 64.dp, minWidth = 64.dp)
                        )
                        Text("${vocabularyViewModel.cardsCreated.intValue} flashcards was successfully created!", textAlign = TextAlign.Center)
                        Text("${vocabularyViewModel.usedTokens.intValue} tokens were used.")
                        Spacer(Modifier.height(16.dp))
                        Button(
                            modifier = Modifier.align(Alignment.End),
                            onClick = {
                                isCreateDialogShown = false
                                vocabularyViewModel.hideSuccessDialog()
                            }
                        ) {
                            Text("Ok")
                        }
                    } else {
                        val language = vocabList[0].vocabulary.language

                        Text("Proceed to create ${selectedVocabs.intValue} flashcards?")
                        Spacer(Modifier.height(16.dp))

                        ExposedDropdownMenuBox(
                            expanded = expandedDecks,
                            onExpandedChange = { expandedDecks = !expandedDecks }
                        ) {
                            OutlinedTextField(
                                value = selectedDeck,
                                label = { Text("Deck") },
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDecks) },
                                modifier = Modifier.menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = expandedDecks,
                                onDismissRequest = { expandedDecks = false }
                            ) {
                                AnkiDroidAPI.getDeckList()?.forEach {
                                    DropdownMenuItem(
                                        text = { Text(text = it.deckName) },
                                        onClick = {
                                            selectedDeck = it.deckName
                                            expandedDecks = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))

                        Text(
                            vocabList
                                .filter { it.selected.value }
                                .joinToString { it.vocabulary.data },
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(16.dp))

                        Button(
                            modifier = Modifier.align(Alignment.End),
                            onClick = {
                                val apiKey = settingsViewModel.getSetting("gemini_key")
                                val model = settingsViewModel.getSetting("model")
                                val prompt = settingsViewModel.getSetting("prompt")

                                vocabularyViewModel.createFlashcards(
                                    apiKey = apiKey,
                                    modelName = model,
                                    prompt = prompt,
                                    language = language,
                                    deckName = selectedDeck,
                                    onFailure = {
                                        isCreateDialogShown = false
                                        onFailure(it ?: "Error")
                                    },
                                    onSuccess = {
                                        vocabList.removeIf { it.selected.value }
                                        selectedVocabs.intValue = 0
                                    },
                                    vocabularies = vocabList
                                        .filter { it.selected.value }
                                        .map { it.vocabulary }.toTypedArray()
                                )
                            }
                        ) {
                            Text("Create")
                        }
                    }
                }
            }
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()
    ) {
        TopAppBar(
            title = {
                Text("${selectedVocabs.intValue} ${stringResource(id = R.string.selected_text)}")
            },
            navigationIcon = {
                IconButton(onClick = {
                    vocabList.forEach {
                        it.selected.value = false
                        selectedVocabs.intValue = 0
                    }
                }) {
                    Icon(Icons.Filled.ArrowBack, null)
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        val selectedLanguages = vocabList
                            .filter { it.selected.value }
                            .map { it.vocabulary.language }
                            .toSet().size
                        if (selectedLanguages > 1) {
                            onFailure("It's possible to create flashcards of one language at a time.")
                            return@IconButton
                        }
                        val apiKey = settingsViewModel.getSetting("gemini_key")
                        val model = settingsViewModel.getSetting("model")
                        val prompt = settingsViewModel.getSetting("prompt")

                        if (apiKey.isEmpty() || model.isEmpty() || prompt.isEmpty()) {
                            onFailure("You need to configure Gemini properly in settings screen.")
                            return@IconButton
                        }
                        isCreateDialogShown = true
                    }
                ) {
                    Icon(Icons.Filled.LibraryAdd, null)
                }

                IconButton(onClick = { isDeleteDialogShown = true }) {
                    Icon(Icons.Filled.Delete, null)
                }

                IconButton(onClick = {
                    vocabList.forEach {
                        it.selected.value = true
                        selectedVocabs.intValue = vocabList.size
                    }
                }) {
                    Icon(Icons.Filled.SelectAll, null)
                }
            }
        )
    }
}