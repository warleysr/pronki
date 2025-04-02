package io.github.warleysr.pronki.screens.vocabulary

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
import io.github.warleysr.pronki.R
import io.github.warleysr.pronki.viewmodels.SettingsViewModel
import io.github.warleysr.pronki.viewmodels.VocabularyViewModel
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.style.TextAlign
import io.github.warleysr.pronki.api.ankidroid.AnkiDroidAPI
import io.github.warleysr.pronki.viewmodels.AnkiDroidViewModel

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
                    Text(stringResource(R.string.sure_deletion, selectedVocabs.intValue))
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
                        Text(stringResource(R.string.yes))
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
                        Text(stringResource(R.string.creating_cards))
                    } else if (vocabularyViewModel.successCreation.value) {
                        Icon(
                            Icons.Filled.Done, null,
                            tint = MaterialTheme.colorScheme.surfaceTint,
                            modifier = Modifier.defaultMinSize(minHeight = 64.dp, minWidth = 64.dp)
                        )
                        Text(
                            stringResource(R.string.flashcards_success,vocabularyViewModel.cardsCreated.intValue),
                            textAlign = TextAlign.Center
                        )
                        Text(stringResource(R.string.used_tokens, vocabularyViewModel.usedTokens.intValue))
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
                        Text(stringResource(R.string.proceed_creation, selectedVocabs.intValue))
                        Spacer(Modifier.height(16.dp))

                        ExposedDropdownMenuBox(
                            expanded = expandedDecks,
                            onExpandedChange = { expandedDecks = !expandedDecks }
                        ) {
                            OutlinedTextField(
                                value = selectedDeck,
                                label = { Text(stringResource(R.string.deck)) },
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
                                val vocabularies = vocabList
                                    .filter { it.selected.value }
                                    .map { it.vocabulary }.toTypedArray()

                                vocabularyViewModel.createFlashcards(
                                    apiKey = apiKey,
                                    modelName = model,
                                    prompt = prompt,
                                    language = vocabularies[0].language,
                                    deckName = selectedDeck,
                                    onFailure = {
                                        isCreateDialogShown = false
                                        onFailure(it ?: "Error")
                                    },
                                    onSuccess = {
                                        vocabList.removeIf { it.vocabulary.flashcard != null }
                                        selectedVocabs.intValue = 0
                                    },
                                    vocabularies = vocabularies
                                )
                            }
                        ) {
                            Text(stringResource(R.string.create))
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
                Text(stringResource(id = R.string.selected_text, selectedVocabs.intValue))
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
                val onlyOneLanguage = stringResource(R.string.only_one_language)
                val configureGemini = stringResource(R.string.configure_gemini_properly)
                IconButton(
                    onClick = {
                        val selectedLanguages = vocabList
                            .filter { it.selected.value }
                            .map { it.vocabulary.language.lowercase().trim() }
                            .toSet().size
                        if (selectedLanguages > 1) {
                            onFailure(onlyOneLanguage)
                            return@IconButton
                        }
                        val apiKey = settingsViewModel.getSetting("gemini_key")
                        val model = settingsViewModel.getSetting("model")
                        val prompt = settingsViewModel.getSetting("prompt")

                        if (apiKey.isEmpty() || model.isEmpty() || prompt.isEmpty()) {
                            onFailure(configureGemini)
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