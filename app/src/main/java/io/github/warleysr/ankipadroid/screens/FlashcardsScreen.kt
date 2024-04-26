package io.github.warleysr.ankipadroid.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.warleysr.ankipadroid.viewmodels.AnkiDroidViewModel
import io.github.warleysr.ankipadroid.viewmodels.PronunciationViewModel
import io.github.warleysr.ankipadroid.viewmodels.SettingsViewModel

@Composable
fun FlashcardsScreen(
    settingsViewModel: SettingsViewModel,
    pronunciationViewModel: PronunciationViewModel,
    ankiDroidViewModel: AnkiDroidViewModel
) {

    if (pronunciationViewModel.hasAssessmentSucceeded.value) {
        AssessmentResults(settingsViewModel, pronunciationViewModel)
    } else {
        FlashcardPreview(settingsViewModel, pronunciationViewModel, ankiDroidViewModel)
    }
}