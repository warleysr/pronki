package io.github.warleysr.ankipadroid.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.warleysr.ankipadroid.ConfigUtils
import io.github.warleysr.ankipadroid.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzureExtraOptions(
    selectedLanguage: MutableState<String>,
    selectedRegion: MutableState<String>,
    onLanguageChange: (String) -> Unit,
    onRegionChange: (String) -> Unit
) {
    var expandedLanguages by remember { mutableStateOf(false) }
    var expandedRegions by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expandedLanguages,
            onExpandedChange = {
                expandedLanguages = !expandedLanguages
            }
        ) {
            OutlinedTextField(
                value = ConfigUtils.getAvailableLanguages().getOrDefault(selectedLanguage.value, ""),
                label = { Text(stringResource(R.string.language)) },
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLanguages) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expandedLanguages,
                onDismissRequest = { expandedLanguages = false }
            ) {
                ConfigUtils.getAvailableLanguages().forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item.value) },
                        onClick = {
                            onLanguageChange(item.key)
                            expandedLanguages = false
                        }
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expandedRegions,
            onExpandedChange = {
                expandedRegions = !expandedRegions
            }
        ) {
            OutlinedTextField(
                value = selectedRegion.value,
                label = { Text(stringResource(id = R.string.region)) },
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRegions) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expandedRegions,
                onDismissRequest = { expandedRegions = false }
            ) {
                ConfigUtils.getAvailableRegions().forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            onRegionChange(item)
                            expandedRegions = false
                        }
                    )
                }
            }
        }
    }
}