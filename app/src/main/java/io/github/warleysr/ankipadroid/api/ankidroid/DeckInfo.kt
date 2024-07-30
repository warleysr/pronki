package io.github.warleysr.ankipadroid.api.ankidroid

data class DeckInfo(
    val deckId: Long,
    val deckName: String,
    val new: Int,
    val learn: Int,
    val due: Int
)
