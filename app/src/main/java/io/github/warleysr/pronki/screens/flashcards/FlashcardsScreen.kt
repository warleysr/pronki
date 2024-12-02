package io.github.warleysr.pronki.screens.flashcards

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ichi2.anki.api.AddContentApi
import io.github.warleysr.pronki.R
import io.github.warleysr.pronki.viewmodels.AnkiDroidViewModel
import io.github.warleysr.pronki.viewmodels.PronunciationViewModel
import io.github.warleysr.pronki.viewmodels.SettingsViewModel

@Composable
fun FlashcardsScreen(
    settingsViewModel: SettingsViewModel,
    pronunciationViewModel: PronunciationViewModel,
    ankiDroidViewModel: AnkiDroidViewModel
) {
    if (ankiDroidViewModel.permissionGranted.value) {
        if (pronunciationViewModel.hasAssessmentSucceeded.value) {
            AssessmentResults(settingsViewModel, pronunciationViewModel)
        } else {
            FlashcardPreview(settingsViewModel, pronunciationViewModel, ankiDroidViewModel)
        }
    } else {
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                if (isGranted)
                    ankiDroidViewModel.ankiPermissionGranted()
            }
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(stringResource(R.string.needs_permission))
            Button(onClick = {
                permissionLauncher.launch(AddContentApi.READ_WRITE_PERMISSION)
            }) {
                Text(stringResource(R.string.grant_permission))
            }
        }
    }
}