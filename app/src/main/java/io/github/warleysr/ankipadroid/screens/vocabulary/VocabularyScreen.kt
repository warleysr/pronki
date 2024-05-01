package io.github.warleysr.ankipadroid.screens.vocabulary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.warleysr.ankipadroid.R

@Preview
@Composable
fun VocabularyScreen() {
    var fabState by remember { mutableStateOf(false) }

    val fabRotation = animateFloatAsState(
        targetValue = if (fabState) 180f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            delayMillis = 5
        ), label = "FloatAnimation"
    )

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        AnimatedVisibility(
            visible = fabState,
            enter = scaleIn(),
            exit = scaleOut(),
        ) {
            Column {
                SmallFloatingActionButton(
                    onClick = { },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(Icons.Filled.List, null)
                }
                Spacer(Modifier.padding(4.dp))
                SmallFloatingActionButton(
                    onClick = { },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(Icons.Filled.CameraAlt, null)
                }
                Spacer(Modifier.padding(4.dp))
            }
        }
        ExtendedFloatingActionButton(
            onClick = { fabState = !fabState },
            icon = {
                Icon(
                    if (fabState) Icons.Filled.Close else Icons.Filled.Add ,
                    null,
                    modifier = Modifier.rotate(fabRotation.value)
                )
            },
            text = { 
                Text(
                    text =
                    if (fabState)
                        stringResource(id = R.string.cancel)
                    else
                        stringResource(id = R.string.add_vocabulary)
                ) 
               },
        )
    }
}