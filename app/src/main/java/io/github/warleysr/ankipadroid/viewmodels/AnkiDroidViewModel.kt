package io.github.warleysr.ankipadroid.viewmodels

import androidx.lifecycle.ViewModel
import io.github.warleysr.ankipadroid.AnkiDroidHelper
import io.github.warleysr.ankipadroid.CardInfo

class AnkiDroidViewModel : ViewModel() {

    var currentDeckId: Long? = null
    var currentQuestion: String? = null
    var currentAnswer: String? = null

    init {
        println("AnkiDroidViewModel initialized")
    }

    fun queryNextCard(onResult: (String, String) -> Unit) {
        val cardInfo = AnkiDroidHelper.getInstance().queryCurrentScheduledCard(currentDeckId!!)
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