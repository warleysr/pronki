package io.github.warleysr.pronki.screens.vocabulary

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.warleysr.pronki.ConfigUtils
import io.github.warleysr.pronki.R
import io.github.warleysr.pronki.api.ImportedVocabulary
import io.github.warleysr.pronki.api.OpenCV
import io.github.warleysr.pronki.viewmodels.SettingsViewModel
import io.github.warleysr.pronki.viewmodels.VocabularyViewModel

@Composable
fun VocabularyImportList(
    settingsViewModel: SettingsViewModel,
    vocabularyViewModel: VocabularyViewModel
) {
    var currentText by remember { mutableStateOf(vocabularyViewModel.importList.value) }
    val defaultLanguage by remember {
        mutableStateOf(
            ConfigUtils.getAvailableLanguages().getOrDefault(
                settingsViewModel.getSetting("language"),
                "English"
            ).split("(")[0]
        )
    }
    val langRegex = Regex(".+\\((.+)\\)")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(stringResource(R.string.insert_vocabulary), fontWeight = FontWeight.Bold)
        TextField(
            value = currentText,
            onValueChange = {
                currentText = it
                vocabularyViewModel.updateImportList(it)
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .clip(RoundedCornerShape(12.dp))
        )
        Row {
            Button(
                onClick = {
                    vocabularyViewModel.updateImportList("")
                    vocabularyViewModel.hideImportList()
                }
            ) {
                Text(stringResource(R.string.cancel))
            }
            Spacer(Modifier.width(16.dp))
            Button(
                onClick = {
                    val importedWords = vocabularyViewModel.importList.value.split("\n")
                    importedWords.forEachIndexed{ idx, word ->
                        val langGroup = langRegex.find(word.trim())?.groups?.get(1)
                        val wordDefaultLanguage = langGroup?.value ?: defaultLanguage
                        val forceLanguage = langGroup != null
                        val wordFinal = if (forceLanguage)
                            word.split("(")[0].trim()
                        else
                            word.trim()

                        OpenCV.recognizeLanguage(
                            text = wordFinal,
                            defaultLanguage = wordDefaultLanguage,
                            onFinish = { lang ->
                                val vocab = ImportedVocabulary(data = wordFinal, language = lang)
                                vocabularyViewModel.insertVocabulary(vocab)

                                if (idx == importedWords.size - 1)
                                    vocabularyViewModel.hideImportList()
                            },
                            forceLanguage = forceLanguage
                        )
                    }
               }
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}