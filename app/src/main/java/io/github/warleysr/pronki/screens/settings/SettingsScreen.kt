package io.github.warleysr.pronki.screens.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.warleysr.pronki.R
import io.github.warleysr.pronki.viewmodels.SettingsViewModel
import io.github.warleysr.pronki.viewmodels.VocabularyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {

    val azureKey = remember { mutableStateOf(viewModel.getSetting("azure_key")) }
    val geminiKey = remember { mutableStateOf(viewModel.getSetting("gemini_key")) }
    val selectedLanguage = remember { mutableStateOf(viewModel.getSetting("language")) }
    val selectedLanguageApp = remember { mutableStateOf(viewModel.getSetting("language_app")) }
    val selectedRegion = remember { mutableStateOf(viewModel.getSetting("region")) }
    val selectedModel = remember { mutableStateOf(viewModel.getSetting("model")) }
    val prompt = remember { mutableStateOf(viewModel.getSetting("prompt")) }
    val defaultPrompt = stringResource(id = R.string.default_prompt)

    LaunchedEffect(key1 = true) {
        if (prompt.value.isEmpty())
            prompt.value = defaultPrompt
    }

    var isAzureDialogShown by remember { mutableStateOf(false) }
    var isGeminiDialogShown by remember { mutableStateOf(false) }
    var isLanguagesDialogShown by remember { mutableStateOf(false) }
    var isThemeDialogShown by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) }

    val languageOptions = mapOf(Pair("pt", "Português"), Pair("en", "English"))

    viewModel.theme.value = viewModel.getSetting("theme")
    val materialYou = viewModel.getSetting("material_you")
    isChecked = materialYou.isNotEmpty() && materialYou.toBooleanStrict()

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
                extraOptions = {
                    AzureExtraOptions(
                        selectedLanguage = selectedLanguage,
                        selectedRegion = selectedRegion,
                        onLanguageChange = {newLanguage -> selectedLanguage.value = newLanguage},
                        onRegionChange = {newRegion -> selectedRegion.value = newRegion}
                    )
                },
                onSave = { apiKey ->
                    azureKey.value = apiKey
                    viewModel.saveSetting("azure_key", apiKey)
                    viewModel.saveSetting("language", selectedLanguage.value)
                    viewModel.saveSetting("region", selectedRegion.value)
                }
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
                extraOptions = {
                    GeminiExtraOptions(
                        selectedModel = selectedModel,
                        prompt = prompt
                    )
                },
                onSave = { apiKey ->
                    geminiKey.value = apiKey
                    viewModel.saveSetting("gemini_key", apiKey)
                    viewModel.saveSetting("model", selectedModel.value)
                    viewModel.saveSetting("prompt", prompt.value)
                 },
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
                                selected = (language.key == selectedLanguageApp.value),
                                onClick = {
                                    selectedLanguageApp.value = language.key
                                    viewModel.saveSetting("language_app", language.key)
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

    if (isThemeDialogShown) {
        AlertDialog(
            onDismissRequest = {
                isThemeDialogShown = false
            },
            modifier = Modifier.clip(RoundedCornerShape(12.dp)),
        ) {
            Surface {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(id = R.string.select_theme), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = viewModel.theme.value == "system",
                            onClick = {
                                viewModel.theme.value = "system"
                                viewModel.saveSetting("theme", "system")
                            }
                        )
                        Text(stringResource(id = R.string.system))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = viewModel.theme.value == "dark",
                            onClick = {
                                viewModel.theme.value = "dark"
                                viewModel.saveSetting("theme", "dark")
                            }
                        )
                        Text(stringResource(id = R.string.dark))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = viewModel.theme.value == "light",
                            onClick = {
                                viewModel.theme.value = "light"
                                viewModel.saveSetting("theme", "light")
                            }
                        )
                        Text(stringResource(id = R.string.light))
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
            .verticalScroll(rememberScrollState())
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
                    onClick = { viewModel.toggleAdjustingColors() },
                )
                .padding(all = 16.dp),
        ) {
            Text(
                text = stringResource(id = R.string.opencv),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.surfaceTint,
            )

            Text(
                text = stringResource(id = R.string.opencv_description),
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(id = R.string.material_you),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.surfaceTint,
                )
                Text(
                    text = stringResource(id = R.string.material_you_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Switch(
                checked = isChecked,
                onCheckedChange = {
                    isChecked = it
                    viewModel.setMaterialYou(it)
                }
            )
        }
        Divider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(id = R.string.app_theme),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.surfaceTint,
                )
                Text(
                    text = stringResource(id = R.string.app_theme_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Button(onClick = {isThemeDialogShown = true}) {
                Text(stringResource(id = themeString(viewModel.theme.value)))
            }
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

@Composable
fun SettingsScreenRoot(viewModel: SettingsViewModel, vocabularyViewModel: VocabularyViewModel) {
    if (viewModel.adjustingColors.value)
        HighlighterColorPicker(viewModel, vocabularyViewModel)
    else
        SettingsScreen(viewModel)
}

fun themeString(theme: String): Int {
    return when (theme) {
        "dark" -> R.string.dark
        "light" -> R.string.light
        "system" -> R.string.system
        else -> R.string.system
    }
}