package io.github.warleysr.ankipadroid.api.ankidroid

import android.net.Uri
import com.ichi2.anki.FlashCardsContract
import com.ichi2.anki.api.AddContentApi
import io.github.warleysr.ankipadroid.AnkiPADroid

class AnkiDroidAPI {

    companion object {
    fun queryNextCard(deck: DeckInfo): CardInfo? {
        println("Selected deck: ${deck.deckName} - ${deck.deckId}")

        val builder = FlashCardsContract.ReviewInfo.CONTENT_URI.buildUpon()
            .appendQueryParameter(
                FlashCardsContract.Note.DECK_ID_QUERY_PARAM, deck.deckId.toString()
            )
            .appendQueryParameter("limit", "5")
        val deckUri = builder.build()
        val deckCursor = AnkiPADroid.instance.applicationContext.contentResolver.query(
            deckUri, null, null, null, null
        )
        if (deckCursor == null || !deckCursor.moveToFirst())
            return null

        println("Deck Uri: $deckUri")

        val noteIdCol = deckCursor.getColumnIndex(FlashCardsContract.ReviewInfo.NOTE_ID)
        val cardOrdCol = deckCursor.getColumnIndex(FlashCardsContract.ReviewInfo.CARD_ORD)
        val noteId = deckCursor.getLong(noteIdCol)
        val cardOrd = deckCursor.getInt(cardOrdCol)

        deckCursor.close()

        val noteUri = Uri.withAppendedPath(
            FlashCardsContract.Note.CONTENT_URI, noteId.toString()
        )
        val cardsUri = Uri.withAppendedPath(noteUri, "cards")
        val specificCardUri = Uri.withAppendedPath(cardsUri, cardOrd.toString())

        val cardCursor = AnkiPADroid.instance.applicationContext.contentResolver.query(
            specificCardUri, null, null, null, null
        )
        if (cardCursor == null || !cardCursor.moveToFirst())
            return null


        println("Specific card Uri: $specificCardUri")
        val questionCol = cardCursor.getColumnIndex(FlashCardsContract.Card.QUESTION)
        val answerCol = cardCursor.getColumnIndex(FlashCardsContract.Card.ANSWER)

        val question = cardCursor.getString(questionCol)
        val answer = cardCursor.getString(answerCol)

        cardCursor.close()

        return CardInfo(noteId, cardOrd, question, answer)
    }

        fun getDeckList(): List<DeckInfo>? {
            return AddContentApi(AnkiPADroid.instance.applicationContext)
                .deckList?.map { deck -> DeckInfo(deck.key, deck.value) }
        }

    }
}