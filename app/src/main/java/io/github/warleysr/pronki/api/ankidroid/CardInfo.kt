package io.github.warleysr.pronki.api.ankidroid

data class CardInfo(
    val noteId: Long,
    val cardOrd: Int,
    val question: String,
    val answer: String,
    val startTime: Long = System.currentTimeMillis()
)