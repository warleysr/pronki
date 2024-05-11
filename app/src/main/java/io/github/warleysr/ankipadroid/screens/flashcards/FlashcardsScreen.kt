package io.github.warleysr.ankipadroid.screens.flashcards

import android.app.Activity
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ichi2.anki.api.AddContentApi
import io.github.warleysr.ankipadroid.AnkiDroidHelper
import io.github.warleysr.ankipadroid.viewmodels.AnkiDroidViewModel
import io.github.warleysr.ankipadroid.viewmodels.PronunciationViewModel
import io.github.warleysr.ankipadroid.viewmodels.SettingsViewModel

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
            Text("The app needs permission to access AnkiDroid database to use your flashcards.")
            Button(onClick = {
                permissionLauncher.launch(AddContentApi.READ_WRITE_PERMISSION)
            }) {
                Text("Grant permission")
            }
        }
    }
}