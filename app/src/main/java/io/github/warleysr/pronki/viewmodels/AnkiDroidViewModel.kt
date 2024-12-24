package io.github.warleysr.pronki.viewmodels

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import io.github.warleysr.pronki.api.ankidroid.AnkiDroidAPI
import io.github.warleysr.pronki.api.ankidroid.CardInfo
import io.github.warleysr.pronki.api.ankidroid.DeckInfo

class AnkiDroidViewModel : ViewModel() {

    var permissionGranted = mutableStateOf(false)
        private set

    var useFront: Boolean = true
        private set

    var selectedDeck: MutableState<DeckInfo?> = mutableStateOf(null)
        private set

    var selectedCard: MutableState<CardInfo?> = mutableStateOf(null)
        private set

    var currentIndex: MutableIntState = mutableIntStateOf(1)
        private set

    fun isDeckSelected() = selectedDeck.value != null

    init {
        println("AnkiDroidViewModel initialized")
        permissionGranted.value = AnkiDroidAPI.isPermissionGranted()
    }

    fun queryNextCard(onDeckFinished: () -> Unit = {}) {
        selectedCard.value = AnkiDroidAPI.queryNextCard(currentIndex.value)

        if (selectedCard.value == null) {
            exitFlashcardPreview()
            onDeckFinished()
        }
    }

    fun reviewCard(ease: Int, onNextResult: () -> Unit) {
        AnkiDroidAPI.reviewCard(selectedCard.value!!, ease)
        onNextResult()
    }

    fun selectDeck(deck: DeckInfo) {
        AnkiDroidAPI.selectDeck(deck)
        selectedDeck.value = deck

        queryNextCard()
    }

    fun exitFlashcardPreview() {
        selectedDeck.value = null
        currentIndex.intValue = 1
    }

    fun toggleCardField(onToggle: (Boolean) -> Unit) {
        useFront = !useFront
        onToggle(useFront)
    }

    fun previousCard() {
        currentIndex.value--
    }

    fun nextCard() {
        currentIndex.value++
    }

    fun ankiPermissionGranted() {
        permissionGranted.value = true
    }

}