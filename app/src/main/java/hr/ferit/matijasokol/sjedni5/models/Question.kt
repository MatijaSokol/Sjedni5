package hr.ferit.matijasokol.sjedni5.models

import android.util.Patterns
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import hr.ferit.matijasokol.sjedni5.other.Constants.QUESTIONS_TABLE

@Entity(tableName = QUESTIONS_TABLE)
data class Question(
    val text: String = "",
    val answer1: String = "",
    val answer2: String = "",
    val answer3: String = "",
    val answer4: String = "",
    val correctAnswer: String = "",
    val url: String = "",
    val category: String = Categories.CATEGORY_1.type,
    val level: Int = 1
) {

    @set:Exclude @get:Exclude
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @Exclude
    fun isSomePropertyEmpty(): Boolean {
        if (text.isBlank() || answer1.isBlank() || answer2.isBlank() || answer3.isBlank() || answer4.isBlank() || correctAnswer.isBlank() || url.isBlank()) {
            return true
        }

        return false
    }

    @Exclude
    fun isCorrectAnswerInAnswers(): Boolean {
        if (answer1 == correctAnswer || answer2 == correctAnswer || answer3 == correctAnswer || answer4 == correctAnswer) {
            return true
        }

        return false
    }

    @Exclude
    fun isUrlValid() = Patterns.WEB_URL.matcher(url).matches()
}