package io.github.warleysr.ankipadroid.screens

import TextEditDialog
import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.warleysr.ankipadroid.R
import io.github.warleysr.ankipadroid.viewmodels.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {

    val coroutineScope = rememberCoroutineScope()

    val azureKey = remember { mutableStateOf("azure") }
    val geminiKey = remember { mutableStateOf("gemini") }

    val languageOptions = mapOf(Pair("pt", "Português"), Pair("en", "English"))
    val selectedLanguage = remember { mutableStateOf("pt") }

    coroutineScope.launch {
        azureKey.value = viewModel.getSetting("azure_key")
        geminiKey.value = viewModel.getSetting("gemini_key")
        selectedLanguage.value = viewModel.getSetting("language")
    }

    val saveKey: (String, String) -> Unit = { key: String, value: String ->
        coroutineScope.launch {
            viewModel.saveSetting(key, value)
        }
    }

    var isAzureDialogShown by remember { mutableStateOf(false) }
    var isGeminiDialogShown by remember { mutableStateOf(false) }
    var isLanguagesDialogShown by remember { mutableStateOf(false) }


    if (isAzureDialogShown) {
        AlertDialog(
            onDismissRequest = {
                isAzureDialogShown = false
            },
            modifier = Modifier.clip(RoundedCornerShape(12.dp)),
        ) {
            TextEditDialog(
                name = R.string.configure_azure,
                storedValue = azureKey.value,
                extraOptions = { AzureExtraOptions() },
                onSave = { finalValue -> saveKey("azure_key", finalValue)},
                ) {
                    isAzureDialogShown = false
            }
        }
    }

    if (isGeminiDialogShown) {
        AlertDialog(
            onDismissRequest = {
                isGeminiDialogShown = false
            },
            modifier = Modifier.clip(RoundedCornerShape(12.dp)),
        ) {
            TextEditDialog(
                name = R.string.configure_gemini,
                storedValue = geminiKey.value,
                extraOptions = {},
                onSave = { finalValue -> saveKey("gemini_key", finalValue)},
            ) {
                isGeminiDialogShown = false
            }
        }
    }

    if (isLanguagesDialogShown) {
        AlertDialog(
            onDismissRequest = {
                isLanguagesDialogShown = false
            },
            modifier = Modifier.clip(RoundedCornerShape(12.dp)),
        ) {
            Surface {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        stringResource(id = R.string.select_language),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    languageOptions.forEach { language ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = (language.key == selectedLanguage.value),
                                onClick = {
                                    selectedLanguage.value = language.key
                                    saveKey("language", language.key)
                                    viewModel.changeLanguage(language.key)
                                }
                            )
                            Text(
                                text = language.value,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {

        Text(
            text = stringResource(id = R.string.settings),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.padding(16.dp))

        Divider()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = { isAzureDialogShown = true },
                )
                .padding(all = 16.dp),
        ) {
            Text(
                text = stringResource(id = R.string.configure_azure),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.surfaceTint,
            )

            Text(
                text = stringResource(id = R.string.configure_azure_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Divider()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = { isGeminiDialogShown = true },
                )
                .padding(all = 16.dp),
        ) {
            Text(
                text = stringResource(id = R.string.configure_gemini),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.surfaceTint,
            )

            Text(
                text = stringResource(id = R.string.configure_gemini_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Divider()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = { isLanguagesDialogShown = true },
                )
                .padding(all = 16.dp),
        ) {
            Text(
                text = stringResource(id = R.string.select_language),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.surfaceTint,
            )

            Text(
                text = stringResource(id = R.string.select_language_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Divider()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = { viewModel.openAboutInfo() },
                )
                .padding(all = 16.dp),
        ) {
            Text(
                text = stringResource(id = R.string.about),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.surfaceTint,
            )

            Text(
                text = stringResource(id = R.string.about_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}