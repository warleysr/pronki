package io.github.warleysr.ankipadroid.viewmodels

import androidx.lifecycle.ViewModel
import io.github.warleysr.ankipadroid.AnkiDroidHelper
import io.github.warleysr.ankipadroid.CardInfo

class AnkiDroidViewModel : ViewModel() {

    private var currentDeckId: Long? = null
    var currentQuestion: String? = null
        private set
    var currentAnswer: String? = null
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
    }

    val isDeckSelected: Boolean
        get() = currentDeckId != null

}