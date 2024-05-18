package io.github.warleysr.ankipadroid.screens.vocabulary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import io.github.warleysr.ankipadroid.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyOptionsBar(
    visible: Boolean,
    selectedVocabs: MutableState<Int>,
    selectAll: () -> Unit,
    unselectAll: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()
    ) {
        TopAppBar(
            title = {
                Text("${selectedVocabs.value} ${stringResource(id = R.string.selected_text)}")
            },
            navigationIcon = {
                IconButton(onClick = { unselectAll() }) {
                    Icon(Icons.Filled.ArrowBack, null)
                }
            },
            actions = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Filled.LibraryAdd, null)
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Filled.Delete, null)
                }
                IconButton(onClick = { selectAll() }) {
                    Icon(Icons.Filled.SelectAll, null)
                }
            }
        )
    }
}