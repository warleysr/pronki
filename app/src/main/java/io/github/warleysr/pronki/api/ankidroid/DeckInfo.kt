package io.github.warleysr.pronki.api.ankidroid

data class DeckInfo(
    val deckId: Long,
    val deckName: String,
    val new: Int,
    val learn: Int,
    val due: Int
)
