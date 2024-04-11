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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AzureExtraOptions() {
    val languages = arrayOf("Portuguese", "English", "Japanese", "Chinese", "Spanish")
    val regions = arrayOf("brazilsouth", "centralus", "germany")
    var expandedLanguages by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf(languages[0]) }
    var expandedRegions by remember { mutableStateOf(false) }
    var selectedRegion by remember { mutableStateOf(regions[0]) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top=16.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expandedLanguages,
            onExpandedChange = {
                expandedLanguages = !expandedLanguages
            }
        ) {
            OutlinedTextField(
                value = selectedLanguage,
                label = { Text("Language") },
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLanguages) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expandedLanguages,
                onDismissRequest = { expandedLanguages = false }
            ) {
                languages.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            selectedLanguage = item
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
            .padding(top=16.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expandedRegions,
            onExpandedChange = {
                expandedRegions = !expandedRegions
            }
        ) {
            OutlinedTextField(
                value = selectedRegion,
                label = { Text("Region") },
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRegions) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expandedRegions,
                onDismissRequest = { expandedRegions = false }
            ) {
                regions.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            selectedRegion = item
                            expandedRegions = false
                        }
                    )
                }
            }
        }
    }
}