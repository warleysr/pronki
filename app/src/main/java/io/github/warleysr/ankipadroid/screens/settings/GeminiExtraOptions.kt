package io.github.warleysr.ankipadroid.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
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
import io.github.warleysr.ankipadroid.R
import io.github.warleysr.ankipadroid.api.GeminiAPI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiExtraOptions(
    selectedModel: MutableState<String>,
    prompt: MutableState<String>
) {

    var expandedModels by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expandedModels,
            onExpandedChange = {
                expandedModels = !expandedModels
            }
        ) {
            OutlinedTextField(
                value = GeminiAPI.getAvailableModels().getOrDefault(selectedModel.value, ""),
                label = { Text(stringResource(R.string.model)) },
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedModels) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expandedModels,
                onDismissRequest = { expandedModels = false }
            ) {
                GeminiAPI.getAvailableModels().forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item.value) },
                        onClick = {
                            selectedModel.value = item.key
                            expandedModels = false
                        }
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
    ) {
        OutlinedTextField(
            value = prompt.value,
            onValueChange = { prompt.value = it },
            label = { Text("Prompt") },
            modifier = Modifier.defaultMinSize(minHeight = 128.dp)
        )
    }

}