package io.github.warleysr.pronki.screens.vocabulary

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import io.github.warleysr.pronki.R
import io.github.warleysr.pronki.api.ImportedVocabulary
import io.github.warleysr.pronki.viewmodels.AnkiDroidViewModel
import io.github.warleysr.pronki.viewmodels.SettingsViewModel
import io.github.warleysr.pronki.viewmodels.VocabularyViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VocabularyScreen(
    settingsViewModel: SettingsViewModel,
    vocabularyViewModel: VocabularyViewModel,
    ankiDroidViewModel: AnkiDroidViewModel
) {
    if (vocabularyViewModel.showingRecognized.value) {
        VocabularyRecognitionScreen(vocabularyViewModel)
    }
    else if (vocabularyViewModel.showingImportList.value) {
        VocabularyImportList(settingsViewModel, vocabularyViewModel)
    }
    else {
        VocabularyScreenScaffold(settingsViewModel, vocabularyViewModel, ankiDroidViewModel)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun VocabularyScreenScaffold(
    settingsViewModel: SettingsViewModel,
    vocabularyViewModel: VocabularyViewModel,
    ankiDroidViewModel: AnkiDroidViewModel
) {

    val vocabList = remember { mutableStateListOf<VocabularyState>() }
    LaunchedEffect(key1 = true) {
        vocabularyViewModel.getVocabularyList(
            onFinish = { it.forEach { vocab -> vocabList.add(VocabularyState(vocab)) } }
        )
    }

    val selectedVocabs = remember { mutableIntStateOf(0) }
    val topBarVisible = selectedVocabs.intValue > 0
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            VocabularyOptionsBar(
                settingsViewModel = settingsViewModel,
                vocabularyViewModel = vocabularyViewModel,
                ankiDroidViewModel = ankiDroidViewModel,
                visible = topBarVisible,
                selectedVocabs = selectedVocabs,
                vocabList = vocabList,
                onFailure = {
                    scope.launch {
                        snackbarHostState.showSnackbar(it)
                    }
                }
            )
        },
        floatingActionButton = {
            VocabularyFAB(settingsViewModel, vocabularyViewModel, topBarVisible)
        }
    ) {
        Surface(Modifier.padding(top = it.calculateTopPadding())) {
            VocabularyScreenList(vocabList, selectedVocabs)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyScreenList(
    vocabList: List<VocabularyState>?,
    selectedVocabs: MutableState<Int>
) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US)
    val updateVocabularySelected: (VocabularyState, Boolean) -> Unit = { vocab, selected ->
        vocab.selected.value = selected
        selectedVocabs.value += if (selected) 1 else -1
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                vocabList?.forEach { vocab ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        onClick = { updateVocabularySelected(vocab, !vocab.selected.value) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = vocab.selected.value,
                                onCheckedChange = { newState ->
                                    updateVocabularySelected(vocab, newState)
                                }
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                Text(vocab.vocabulary.data, fontWeight = FontWeight.Bold)
                                Text(vocab.vocabulary.language)
                                Text(dateFormatter.format(vocab.vocabulary.importedAt ?: Date(0)))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VocabularyFAB(
    settingsViewModel: SettingsViewModel,
    vocabularyViewModel: VocabularyViewModel,
    topBarVisible: Boolean
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
    var defaultLanguage by remember { mutableStateOf(settingsViewModel.getSetting("language")) }
    defaultLanguage =  if (defaultLanguage.isEmpty()) "English" else defaultLanguage.split("(")[0]

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
                    vocabularyViewModel.processBitmap(
                        bitmap!!,
                        result.rotation,
                        colors.first.toScalar(),
                        colors.second.toScalar(),
                        defaultLanguage = defaultLanguage
                    )
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
    Column( horizontalAlignment = Alignment.End ) {
        AnimatedVisibility(
            visible = !topBarVisible && fabState,
            enter = scaleIn(),
            exit = scaleOut(),
        ) {
            Column {
                SmallFloatingActionButton(
                    onClick = { vocabularyViewModel.showImportList() },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(Icons.Filled.List, null)
                }

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

        AnimatedVisibility(
            visible = !topBarVisible,
            enter = scaleIn(),
            exit = scaleOut(),
        ) {
            ExtendedFloatingActionButton(
                onClick = { fabState = !fabState },
                icon = {
                    Icon(
                        if (fabState) Icons.Filled.Close else Icons.Filled.Add,
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
}

data class VocabularyState(val vocabulary: ImportedVocabulary, val initialState: Boolean = false) {
    val selected = mutableStateOf(initialState)
}