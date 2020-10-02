package hr.ferit.matijasokol.sjedni5.data.db

import androidx.room.*
import hr.ferit.matijasokol.sjedni5.models.Question
import hr.ferit.matijasokol.sjedni5.other.Constants.QUESTIONS_TABLE

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllQuestions(question: List<Question>)

    @Query("SELECT * FROM $QUESTIONS_TABLE WHERE category = :selectedCategory")
    suspend fun getAllQuestions(selectedCategory: String): List<Question>

    @Query("DELETE FROM $QUESTIONS_TABLE")
    suspend fun deleteAllQuestions()

    @Transaction
    suspend fun replaceAllQuestions(questions: List<Question>) {
        deleteAllQuestions()
        insertAllQuestions(questions)
    }
}