package io.github.warleysr.pronki.api

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import java.util.Date

@Database(entities = [ImportedVocabulary::class], version = 1)
@TypeConverters(Converters::class)
abstract class VocabularyDatabase : RoomDatabase() {
    abstract fun vocabularyDAO(): VocabularyDAO
}

@Dao
interface VocabularyDAO {

    @Insert
    fun insertAll(vararg vocabs: ImportedVocabulary)

    @Delete
    fun delete(vocab: ImportedVocabulary)

    @Query("SELECT * FROM ImportedVocabulary")
    fun getAll(): List<ImportedVocabulary>

    @Query("SELECT * FROM ImportedVocabulary WHERE flashcard is NULL ORDER BY imported_at DESC")
    fun getAllNew(): List<ImportedVocabulary>

    @Update
    fun updateAll(vararg vocabs: ImportedVocabulary)

}

@Entity
data class ImportedVocabulary(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "data") val data: String,
    @ColumnInfo(name = "language") val language: String,
    @ColumnInfo(name = "imported_at") val importedAt: Date? = Date(),
    @ColumnInfo(name = "flashcard") var flashcard: Long? = null,
)

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}