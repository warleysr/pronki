package io.github.warleysr.ankipadroid.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import io.github.warleysr.ankipadroid.api.ankidroid.AnkiDroidAPI
import io.github.warleysr.ankipadroid.api.ankidroid.CardInfo
import io.github.warleysr.ankipadroid.api.ankidroid.DeckInfo

class AnkiDroidViewModel : ViewModel() {

    private var currentDeckId: Long? = null
    var permissionGranted = mutableStateOf(false)
        private set

    var useFront: Boolean = true
        private set

    var selectedDeck: MutableState<DeckInfo?> = mutableStateOf(null)
        private set

    var selectedCard: MutableState<CardInfo?> = mutableStateOf(null)
        private set

    fun isDeckSelected() = selectedDeck.value != null

    init {
        println("AnkiDroidViewModel initialized")
        permissionGranted.value = AnkiDroidAPI.isPermissionGranted()
    }

    fun queryNextCard() {
        selectedCard.value = AnkiDroidAPI.queryNextCard()

        if (selectedCard.value == null)
            exitFlashcardPreview()
    }

    fun reviewCard(ease: Int, onNextResult: () -> Unit) {
//        AnkiDroidHelper(AnkiPADroid.instance.applicationContext).reviewCard(
//            cardInfo!!.noteID, cardInfo!!.cardOrd, cardInfo!!.cardStartTime, ease
//        )
//        queryNextCard(onResult = onNextResult)
    }


    fun selectDeck(deck: DeckInfo) {
        AnkiDroidAPI.selectDeck(deck)
        selectedDeck.value = deck

        queryNextCard()
    }

    fun exitFlashcardPreview() {
        currentDeckId = null
        selectedDeck.value = null
    }

    fun toggleCardField(onToggle: (Boolean) -> Unit) {
        useFront = !useFront
        onToggle(useFront)
    }

    fun ankiPermissionGranted() {
        permissionGranted.value = true
    }

}