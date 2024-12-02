package io.github.warleysr.pronki.api.ankidroid

data class CardInfo(
    val noteId: Long,
    val cardOrd: Int,
    val questionRaw: String,
    val answerRaw: String,
    val startTime: Long = System.currentTimeMillis()
) {
    val question: String = questionRaw.replace("\\[anki:.*?]".toRegex(), "")
    val answer: String = answerRaw.replace("\\[anki:.*?]".toRegex(), "")
}