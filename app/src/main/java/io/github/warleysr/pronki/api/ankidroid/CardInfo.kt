package io.github.warleysr.pronki.api.ankidroid

private val ankiTagRegex = "\\[anki:.*?]".toRegex()
private val scriptTagRegex = "<script\\b[^<]*(?:(?!</script>)<[^<]*)*</script\\s*>".toRegex()

data class CardInfo(
    val noteId: Long,
    val cardOrd: Int,
    val questionRaw: String,
    val answerRaw: String,
    val startTime: Long = System.currentTimeMillis()
) {
    val question: String = questionRaw
        .replace(ankiTagRegex, "")
        .replace(scriptTagRegex, "")

    val answer: String = answerRaw
        .replace(ankiTagRegex, "")
        .replace(scriptTagRegex, "")
}