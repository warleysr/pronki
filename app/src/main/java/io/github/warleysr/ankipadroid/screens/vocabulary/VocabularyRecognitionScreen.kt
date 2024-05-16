package io.github.warleysr.ankipadroid.screens.vocabulary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.warleysr.ankipadroid.ConfigUtils
import io.github.warleysr.ankipadroid.api.ImportedVocabulary
import io.github.warleysr.ankipadroid.viewmodels.SettingsViewModel
import io.github.warleysr.ankipadroid.viewmodels.VocabularyViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VocabularyRecognitionScreen(settingsViewModel: SettingsViewModel, viewModel: VocabularyViewModel) {
    val selected = remember {
        viewModel.allWords.map { mutableStateOf(it in viewModel.recognizedWords)}
    }
    // TODO: Implement auto language identification with ML Kit instead
    val language = ConfigUtils.getAvailableLanguages().getOrDefault(
        settingsViewModel.getSetting("language"), "English"
    ).split("(")[0]

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        FlowRow {
            viewModel.allWords.forEachIndexed { idx, vocab ->
                Text(
                    vocab,
                    color = if (selected[idx].value) Color.Red else Color.Black,
                    modifier = Modifier.clickable(
                        onClick = {
                            selected[idx].value = !selected[idx].value
                        }
                    ).padding(8.dp)
                )
            }
        }
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.weight(1f)
        ) {
            Button(
                onClick = {
                    val newVocab = ArrayList<ImportedVocabulary>()
                    for (idx in viewModel.allWords.indices) {
                        if (!selected[idx].value) continue
                        val vocab = ImportedVocabulary(
                            data = viewModel.allWords[idx], language = language
                        )
                        newVocab.add(vocab)
                    }
                    viewModel.insertVocabulary(newVocab)
                    viewModel.hideRecognized()
                }
            ) {
                Text("Save")
            }
        }
    }
}