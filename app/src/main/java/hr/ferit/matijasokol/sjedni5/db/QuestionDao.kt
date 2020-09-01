package hr.ferit.matijasokol.sjedni5.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hr.ferit.matijasokol.sjedni5.models.Categories
import hr.ferit.matijasokol.sjedni5.models.Question
import hr.ferit.matijasokol.sjedni5.other.Constants.QUESTIONS_TABLE

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: Question): Long

    @Query("SELECT * FROM $QUESTIONS_TABLE WHERE category = :selectedCategory")
    suspend fun getAllQuestions(selectedCategory: String): List<Question>

    @Query("DELETE FROM $QUESTIONS_TABLE")
    suspend fun deleteAllQuestions()
}