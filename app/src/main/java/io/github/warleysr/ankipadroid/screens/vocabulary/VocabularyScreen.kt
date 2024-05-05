package io.github.warleysr.ankipadroid.screens.vocabulary

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import io.github.warleysr.ankipadroid.R
import io.github.warleysr.ankipadroid.viewmodels.SettingsViewModel
import io.github.warleysr.ankipadroid.viewmodels.VocabularyViewModel

@Composable
fun VocabularyScreen(
    settingsViewModel: SettingsViewModel, vocabularyViewModel: VocabularyViewModel
) {
    var fabState by remember { mutableStateOf(false) }

    val fabRotation = animateFloatAsState(
        targetValue = if (fabState) 180f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            delayMillis = 5
        ), label = "FloatAnimation"
    )

    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    val context = LocalContext.current as Activity

    val imageCropLauncher =
        rememberLauncherForActivityResult(contract = CropImageContract()) { result ->
            if (result.isSuccessful) {
                result.uriContent?.let {
                    bitmap = if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                    } else {
                        val source = ImageDecoder.createSource(context.contentResolver, it)
                        ImageDecoder.decodeBitmap(source) {decoder, _, _ ->
                            decoder.isMutableRequired = true
                        }
                    }
                    val colors = settingsViewModel.getRangeColors()
                    vocabularyViewModel.processBitmap(bitmap!!, result.rotation, colors.first.toScalar(), colors.second.toScalar())
                }
            } else {
                println("ImageCropping error: ${result.error}")
            }
        }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            val text = vocabularyViewModel.text.value
            if (text != null)
                Text(text)
        }
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
                    onClick = {
                        val cropOptions = CropImageContractOptions(
                            null,
                            CropImageOptions(imageSourceIncludeCamera = false)
                        )
                        imageCropLauncher.launch(cropOptions)
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(Icons.Filled.Image, null)
                }
                Spacer(Modifier.padding(4.dp))

                SmallFloatingActionButton(
                    onClick = {
                        val cropOptions = CropImageContractOptions(
                            null,
                            CropImageOptions(imageSourceIncludeGallery = false)
                        )
                        imageCropLauncher.launch(cropOptions)
                    },
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