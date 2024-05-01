package io.github.warleysr.ankipadroid.screens.flashcards

import androidx.compose.runtime.Composable
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