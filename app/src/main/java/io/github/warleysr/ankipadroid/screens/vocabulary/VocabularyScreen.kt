package io.github.warleysr.ankipadroid.screens.vocabulary

import android.Manifest
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import io.github.warleysr.ankipadroid.R
import io.github.warleysr.ankipadroid.api.ImportedVocabulary
import io.github.warleysr.ankipadroid.viewmodels.SettingsViewModel
import io.github.warleysr.ankipadroid.viewmodels.VocabularyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VocabularyScreen(
    settingsViewModel: SettingsViewModel, vocabularyViewModel: VocabularyViewModel
) {
    if (vocabularyViewModel.showingRecognized.value) {
        VocabularyRecognitionScreen(settingsViewModel, vocabularyViewModel)
    } else {
        VocabularyScreenList(settingsViewModel, vocabularyViewModel)
    }
}

@Composable
fun VocabularyScreenList(
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

    val cropOptions = CropImageContractOptions(
        null,
        CropImageOptions(imageSourceIncludeGallery = false)
    )
    val launchCamera: () -> Unit = { imageCropLauncher.launch(cropOptions) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                vocabularyViewModel.cameraPermissionGranted()
                launchCamera()
            }
        }
    )

    var vocabList: List<ImportedVocabulary>? by remember { mutableStateOf(null) }

    LaunchedEffect(key1 = true) {
        vocabularyViewModel.getVocabularyList(onFinish = { vocabList = it })
    }

    val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US)

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .weight(1f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                vocabList?.let {
                    it.forEach { vocab ->
                        var selected by remember { mutableStateOf(false) }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = selected,
                                    onCheckedChange = { newState -> selected = newState }
                                )
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth().weight(1f)
                                ) {
                                    Text(vocab.data, fontWeight = FontWeight.Bold)
                                    Text(vocab.language)
                                    Text(dateFormatter.format(vocab.importedAt ?: Date(0)))
                                }
                            }
                        }
                    }
                }
            }
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
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(Icons.Filled.List, null)
                }

                Spacer(Modifier.padding(4.dp))

                SmallFloatingActionButton(
                    onClick = {
                        val cropOptionsImage = CropImageContractOptions(
                            null,
                            CropImageOptions(imageSourceIncludeCamera = false)
                        )
                        imageCropLauncher.launch(cropOptionsImage)
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(Icons.Filled.Image, null)
                }
                Spacer(Modifier.padding(4.dp))

                SmallFloatingActionButton(
                    onClick = {
                        if (vocabularyViewModel.permissionCameraGranted.value) {
                            launchCamera()
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
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