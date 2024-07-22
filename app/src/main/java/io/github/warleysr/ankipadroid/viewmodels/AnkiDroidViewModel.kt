package io.github.warleysr.ankipadroid.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ichi2.anki.api.AddContentApi
import io.github.warleysr.ankipadroid.AnkiDroidHelper
import io.github.warleysr.ankipadroid.AnkiPADroid
import io.github.warleysr.ankipadroid.api.ankidroid.CardInfo
import io.github.warleysr.ankipadroid.api.ankidroid.DeckInfo

class AnkiDroidViewModel : ViewModel() {

    private var currentDeckId: Long? = null
    var permissionGranted = mutableStateOf(false)
        private set

    var isDeckSelected: MutableState<Boolean> = mutableStateOf(false)
        private set

    var useFront: Boolean = true
        private set

    var selectedDeck: DeckInfo? = null
        private set

    var selectedCard: CardInfo? = null
        private set

    init {
        println("AnkiDroidViewModel initialized")
        permissionGranted.value = AnkiDroidHelper.getInstance().isPermissionGranted
    }

    fun queryNextCard(onResult: (String, String) -> Unit) {
//        val cardInfo = AnkiDroidHelper(AnkiPADroid.instance.applicationContext).queryCurrentScheduledCard(currentDeckId!!)
//        this.cardInfo = cardInfo
//        if (cardInfo != null)
//            onResult(cardInfo.question, cardInfo.answer)
//        else
//            onResult("NO CARD TO REVIEW.", "NONE.")
    }

    fun reviewCard(ease: Int, onNextResult: (String, String) -> Unit) {
//        AnkiDroidHelper(AnkiPADroid.instance.applicationContext).reviewCard(
//            cardInfo!!.noteID, cardInfo!!.cardOrd, cardInfo!!.cardStartTime, ease
//        )
//        queryNextCard(onResult = onNextResult)
    }


    fun selectDeck(deck: DeckInfo) {
        selectedDeck = deck
        isDeckSelected.value = true
    }

    fun exitFlashcardPreview() {
        currentDeckId = null
        isDeckSelected.value = false
    }

    fun toggleCardField(onToggle: (Boolean) -> Unit) {
        useFront = !useFront
        onToggle(useFront)
    }

    fun ankiPermissionGranted() {
        permissionGranted.value = true
    }

}