package io.github.warleysr.pronki.screens.flashcards

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.text.parseAsHtml
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.Balloon
import com.skydoves.balloon.compose.rememberBalloonBuilder
import io.github.warleysr.pronki.R
import io.github.warleysr.pronki.api.ankidroid.AnkiDroidAPI
import io.github.warleysr.pronki.api.ankidroid.DeckInfo
import io.github.warleysr.pronki.viewmodels.AnkiDroidViewModel
import io.github.warleysr.pronki.viewmodels.PronunciationViewModel
import io.github.warleysr.pronki.viewmodels.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FlashcardPreview(
    settingsViewModel: SettingsViewModel,
    pronunciationViewModel: PronunciationViewModel,
    ankiDroidViewModel: AnkiDroidViewModel
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val deckSelected = remember { mutableStateOf(false) }
    val showAnswer = remember { mutableStateOf(false) }
    val deckFinished = stringResource(R.string.deck_finished)
    val withoutPermission = stringResource(R.string.without_permission)

    Scaffold(
        topBar = {
            if (!ankiDroidViewModel.isDeckSelected()) {
                TopAppBar(
                    title = { },
                    actions = {
                        Text(stringResource(R.string.review_mode))
                        Spacer(Modifier.width(8.dp))
                        Switch(
                            checked = settingsViewModel.isReviewModeEnabled(),
                            onCheckedChange = { settingsViewModel.setReviewModeEnabled(it) }
                        )
                        Spacer(Modifier.width(8.dp))
                        val builder = rememberBalloonBuilder {
                            setArrowSize(10)
                            setArrowPosition(0.5f)
                            setWidth(BalloonSizeSpec.WRAP)
                            setHeight(BalloonSizeSpec.WRAP)
                            setPadding(12)
                            setMarginHorizontal(12)
                            setCornerRadius(8f)
                            setBalloonAnimation(BalloonAnimation.ELASTIC)
                        }
                        Balloon(
                            builder = builder,
                            balloonContent = {
                                Text(
                                    stringResource(R.string.review_mode_description),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                            }
                        ) { balloonWindow ->
                            Icon(
                                Icons.Filled.Info, "",
                                modifier = Modifier.clickable {
                                    balloonWindow.showAlignBottom()
                                }
                            )
                        }
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = {
            FlashcardPreviewContent(
                paddingValues = it,
                ankiDroidViewModel = ankiDroidViewModel,
                settingsViewModel = settingsViewModel,
                showAnswer = showAnswer,
                deckSelected = deckSelected,
                onDeckFinished = {
                    scope.launch {
                        snackbarHostState.showSnackbar(deckFinished)
                    }
                }
            )
        },
        floatingActionButton = {
            if (!ankiDroidViewModel.isDeckSelected()) return@Scaffold
            RecordFAB(
                settingsViewModel, pronunciationViewModel, ankiDroidViewModel,
                onBackUse = { showAnswer.value = true },
                onExit = { deckSelected.value = false },
                onFailure = { reason ->
                    scope.launch {
                        snackbarHostState.showSnackbar(reason)
                    }
                },
                onPermissionDenied = {
                    scope.launch {
                        snackbarHostState.showSnackbar(withoutPermission)
                    }
                }
            )
        }
    )

}

@Composable
fun FlashcardPreviewContent(
    paddingValues: PaddingValues,
    ankiDroidViewModel: AnkiDroidViewModel,
    settingsViewModel: SettingsViewModel,
    showAnswer: MutableState<Boolean>,
    deckSelected: MutableState<Boolean>,
    onDeckFinished: () -> Unit
) {

    val onNextResult: () -> Unit = { ankiDroidViewModel.queryNextCard(onDeckFinished) }

    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (ankiDroidViewModel.isDeckSelected()) {
                val card = ankiDroidViewModel.selectedCard.value!!

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 128.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    onClick = { showAnswer.value = !showAnswer.value }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .weight(1f, false)
                    ) {
                        Text(
                            card.question.parseAsHtml().toAnnotatedString(),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                Divider()
                Spacer(Modifier.height(16.dp))

                AnimatedVisibility(visible = showAnswer.value) {
                    Column {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 128.dp)
                            ,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .verticalScroll(rememberScrollState())
                                    .weight(1f, false)
                            ) {
                                Text(
                                    card.answer.parseAsHtml().toAnnotatedString(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(16.dp),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        Divider()
                        Spacer(Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (settingsViewModel.isReviewModeEnabled()) {
                                ReviewButtons(ankiDroidViewModel, onNextResult)
                            }
                            else {
                                NavigationButtons(ankiDroidViewModel, onDeckFinished)
                            }
                        }
                    }
                }
            } else {
                Text(stringResource(id = R.string.select_deck), style = MaterialTheme.typography.headlineSmall)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnkiDroidAPI.getDeckList()?.forEach { deck ->
                        DeckDetails(
                            deck = deck,
                            onClick = {
                                ankiDroidViewModel.selectDeck(deck)
                                deckSelected.value = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeckDetails(deck: DeckInfo, onClick: () -> Unit) {
    Spacer(Modifier.height(8.dp))
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            deck.deckName,
            textDecoration = TextDecoration.Underline
        )
        val zeroColor = Color.LightGray

        Spacer(Modifier.width(4.dp))
        val newColor = if (deck.new > 0) Color(147, 196, 252) else zeroColor
        Text(deck.new.toString(), color = newColor)

        Spacer(Modifier.width(4.dp))
        val learnColor = if (deck.learn > 0) Color( 249, 113, 112) else zeroColor
        Text(deck.learn.toString(), color = learnColor)

        Spacer(Modifier.width(4.dp))
        val dueColor = if (deck.due > 0) Color(35, 197, 95)else zeroColor
        Text(deck.due.toString(), color = dueColor)
    }

}

@Composable
fun ReviewButtons(ankiDroidViewModel: AnkiDroidViewModel, onNextResult: () -> Unit) {
    Button(
        onClick = {
            ankiDroidViewModel.reviewCard(
                AnkiDroidAPI.AGAIN,
                onNextResult = onNextResult
            )
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFD32F2F),
            contentColor = Color.White
        )
    ) {
        Text(stringResource(id = R.string.again))
    }
    Button(
        onClick = {
            ankiDroidViewModel.reviewCard(
                AnkiDroidAPI.HARD,
                onNextResult = onNextResult
            )
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF455A64),
            contentColor = Color.White
        )
    ) {
        Text(stringResource(id = R.string.hard))
    }
    Button(
        onClick = {
            ankiDroidViewModel.reviewCard(
                AnkiDroidAPI.GOOD,
                onNextResult = onNextResult
            )
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4CAF50),
            contentColor = Color.White
        )
    ) {
        Text(stringResource(id = R.string.good))
    }
    Button(
        onClick = {
            ankiDroidViewModel.reviewCard(
                AnkiDroidAPI.EASY,
                onNextResult = onNextResult
            )
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF03A9F4),
            contentColor = Color.White
        )
    ) {
        Text(stringResource(id = R.string.easy))
    }
}

@Composable
fun NavigationButtons(ankiDroidViewModel: AnkiDroidViewModel, onDeckFinished: () -> Unit) {
    Button(
        onClick = {
            ankiDroidViewModel.previousCard()
            ankiDroidViewModel.queryNextCard(onDeckFinished)
        },
        enabled = ankiDroidViewModel.currentIndex.value > 1
    ) {
        Icon(Icons.Filled.KeyboardDoubleArrowLeft, "")
        Text(stringResource(R.string.previous))
    }
    Button(
        onClick = {
            ankiDroidViewModel.nextCard()
            ankiDroidViewModel.queryNextCard(onDeckFinished)
        }
    ) {
        Text(stringResource(R.string.next))
        Icon(Icons.Filled.KeyboardDoubleArrowRight, "")
    }
}

fun Spanned.toAnnotatedString(): AnnotatedString = buildAnnotatedString {
    val spanned = this@toAnnotatedString
    append(spanned.toString())
    spanned.getSpans(0, spanned.length, Any::class.java).forEach { span ->
        val start = spanned.getSpanStart(span)
        val end = spanned.getSpanEnd(span)
        when (span) {
            is StyleSpan -> when (span.style) {
                Typeface.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                Typeface.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                Typeface.BOLD_ITALIC -> addStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    ), start, end
                )
            }
            is UnderlineSpan -> addStyle(
                SpanStyle(textDecoration = TextDecoration.Underline),
                start,
                end
            )
            is ForegroundColorSpan -> addStyle(
                SpanStyle(color = Color(span.foregroundColor)),
                start,
                end
            )
        }
    }
}