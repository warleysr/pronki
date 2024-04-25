package io.github.warleysr.ankipadroid.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import io.github.warleysr.ankipadroid.AnkiDroidHelper
import io.github.warleysr.ankipadroid.CardInfo

class AnkiDroidViewModel : ViewModel() {

    private var currentDeckId: Long? = null
    var currentQuestion: String? = null
        private set
    var currentAnswer: String? = null
        private set

    var isDeckSelected: MutableState<Boolean> = mutableStateOf(false)
        private set

    var useFront: Boolean = true
        private set

    init {
        println("AnkiDroidViewModel initialized")
    }

    fun queryNextCard(onResult: (String, String) -> Unit) {
        val cardInfo = AnkiDroidHelper.getInstance().queryCurrentScheduledCard(currentDeckId!!)
        currentQuestion = cardInfo.question
        currentAnswer = cardInfo.answer
        onResult(cardInfo.question, cardInfo.answer)
    }

    fun getDeckList(): List<String>? {
        return AnkiDroidHelper.getAPI().deckList?.map { deck -> deck.value }
    }

    fun selectDeck(deckName: String) {
        currentDeckId = AnkiDroidHelper.getInstance().findDeckIdByName(deckName)
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

}