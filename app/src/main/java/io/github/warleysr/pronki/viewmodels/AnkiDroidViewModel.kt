package io.github.warleysr.pronki.viewmodels

import android.content.Intent
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import io.github.warleysr.pronki.PronKi
import io.github.warleysr.pronki.api.ankidroid.AnkiDroidAPI
import io.github.warleysr.pronki.api.ankidroid.CardInfo
import io.github.warleysr.pronki.api.ankidroid.DeckInfo

class AnkiDroidViewModel : ViewModel() {

    var permissionGranted = mutableStateOf(false)
        private set

    var useFront: Boolean = true
        private set

    private var selectedDeck: MutableState<DeckInfo?> = mutableStateOf(null)

    var selectedCard: MutableState<CardInfo?> = mutableStateOf(null)
        private set

    var currentIndex: MutableIntState = mutableIntStateOf(1)
        private set

    private var unsyncedChanges: MutableState<Boolean> = mutableStateOf(false)

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
        unsyncedChanges.value = true
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
        currentIndex.intValue--
    }

    fun nextCard() {
        currentIndex.intValue++
    }

    fun ankiPermissionGranted() {
        permissionGranted.value = true
    }

    fun hasUnsyncedChanges(): Boolean {
        return unsyncedChanges.value
    }

    fun triggerAnkiSync() {
        unsyncedChanges.value = false
        val intent = Intent().apply {
            action = "com.ichi2.anki.DO_SYNC"
            addCategory(Intent.CATEGORY_DEFAULT)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        PronKi.instance.applicationContext.startActivity(intent)
    }

}