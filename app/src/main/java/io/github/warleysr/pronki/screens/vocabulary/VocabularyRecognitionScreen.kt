package io.github.warleysr.pronki.screens.vocabulary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.warleysr.pronki.R
import io.github.warleysr.pronki.viewmodels.VocabularyViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VocabularyRecognitionScreen(
    viewModel: VocabularyViewModel
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.check_vocabulary), fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            viewModel.allWords.forEachIndexed { idx, vocab ->
                Box(
                    modifier = Modifier.background(
                        if (vocab.selected.value)
                            Color.Yellow
                        else
                            MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        vocab.vocabulary.data,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable(
                                onClick = {
                                    vocab.selected.value = !vocab.selected.value
                                }
                            )
                    )
                }
            }
        }
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Button(onClick = { viewModel.hideRecognized() }) {
                Text(stringResource(R.string.cancel))
            }
            Spacer(Modifier.width(16.dp))
            Button(
                onClick = {
                    val newVocab = viewModel.allWords
                        .filter { it.selected.value }
                        .map { it.vocabulary }
                        .toTypedArray()
                    viewModel.insertVocabulary(*newVocab)
                    viewModel.hideRecognized()
                }
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}