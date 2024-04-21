package io.github.warleysr.ankipadroid.viewmodels

import androidx.lifecycle.ViewModel
import io.github.warleysr.ankipadroid.AnkiDroidHelper
import io.github.warleysr.ankipadroid.CardInfo

class AnkiDroidViewModel : ViewModel() {

    var currentDeck: String = "tests"
    var currentQuestion: String? = null
    var currentAnswer: String? = null

    fun queryNextCard(onResult: (String, String) -> Unit) {
        val deckId = AnkiDroidHelper.getInstance().findDeckIdByName(currentDeck)
        val cardInfo = AnkiDroidHelper.getInstance().queryCurrentScheduledCard(deckId)
        onResult(cardInfo.question, cardInfo.answer)
    }

}