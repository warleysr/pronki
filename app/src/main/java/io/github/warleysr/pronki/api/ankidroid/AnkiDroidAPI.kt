package io.github.warleysr.pronki.api.ankidroid

import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat
import com.ichi2.anki.FlashCardsContract
import com.ichi2.anki.api.AddContentApi.Companion.READ_WRITE_PERMISSION
import io.github.warleysr.pronki.PronKi

class AnkiDroidAPI {

    companion object {

        private val CARD_PROJECTION = arrayOf(
            FlashCardsContract.Card.QUESTION_SIMPLE, FlashCardsContract.Card.ANSWER_PURE
        )

        const val AGAIN = 1
        const val HARD = 2
        const val GOOD = 3
        const val EASY = 4

        fun selectDeck(deck: DeckInfo) {
            val values = ContentValues()
            values.put(FlashCardsContract.Deck.DECK_ID, deck.deckId)
            PronKi.instance.applicationContext.contentResolver.update(
                FlashCardsContract.Deck.CONTENT_SELECTED_URI, values, null, null
            )
        }

        fun queryNextCard(amount: Int = 1): CardInfo? {
            val deckUri = FlashCardsContract.ReviewInfo.CONTENT_URI
            val deckCursor = PronKi.instance.applicationContext.contentResolver.query(
                deckUri, null, "limit=?", arrayOf(amount.toString()), null
            )
            if (deckCursor == null || !deckCursor.moveToLast())
                return null

            if (deckCursor.count < amount)
                return null

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

            val cardCursor = PronKi.instance.applicationContext.contentResolver.query(
                specificCardUri, CARD_PROJECTION, null, null, null
            )
            if (cardCursor == null || !cardCursor.moveToFirst())
                return null

            val questionCol = cardCursor.getColumnIndex(FlashCardsContract.Card.QUESTION_SIMPLE)
            val answerCol = cardCursor.getColumnIndex(FlashCardsContract.Card.ANSWER_PURE)

            val question = cardCursor.getString(questionCol)
            val answer = cardCursor.getString(answerCol)

            cardCursor.close()

            return CardInfo(noteId, cardOrd, question, answer)
        }

        fun getDeckList(): List<DeckInfo>? {
            val decksCursor = PronKi.instance.applicationContext.contentResolver.query(
                FlashCardsContract.Deck.CONTENT_ALL_URI, null, null, null, null
            )
            if (decksCursor == null || !decksCursor.moveToFirst())
                return null

            val decks = ArrayList<DeckInfo>()
            do {
                val deckIdCol = decksCursor.getColumnIndex(FlashCardsContract.Deck.DECK_ID)
                val deckNameCol = decksCursor.getColumnIndex(FlashCardsContract.Deck.DECK_NAME)
                val cardCountsCol = decksCursor.getColumnIndex(FlashCardsContract.Deck.DECK_COUNTS)

                val deckId = decksCursor.getLong(deckIdCol)
                val deckName = decksCursor.getString(deckNameCol)
                val cardCounts = decksCursor.getString(cardCountsCol)
                val counts = cardCounts.substring(1, cardCounts.length - 1).split(",")
                    .map { it.toInt() }.toTypedArray()
                val (learn, due, new) = counts

                decks.add(DeckInfo(deckId, deckName, new, learn, due))

            } while (decksCursor.moveToNext())

            decksCursor.close()

            return decks
        }

        fun reviewCard(card: CardInfo, ease: Int) {
            val values = ContentValues()

            values.put(FlashCardsContract.ReviewInfo.NOTE_ID, card.noteId)
            values.put(FlashCardsContract.ReviewInfo.CARD_ORD, card.cardOrd)
            values.put(FlashCardsContract.ReviewInfo.EASE, ease)

            val timeTaken = System.currentTimeMillis() - card.startTime
            values.put(FlashCardsContract.ReviewInfo.TIME_TAKEN, timeTaken)

            PronKi.instance.applicationContext.contentResolver.update(
                FlashCardsContract.ReviewInfo.CONTENT_URI, values, null, null
            )
        }

        fun isPermissionGranted(): Boolean {
            return ContextCompat.checkSelfPermission(
                PronKi.instance.applicationContext, READ_WRITE_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}