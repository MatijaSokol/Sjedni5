package hr.ferit.matijasokol.sjedni5.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import hr.ferit.matijasokol.sjedni5.models.Question
import hr.ferit.matijasokol.sjedni5.models.Term

@Database(entities = [Question::class, Term::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class QuizDatabase : RoomDatabase() {

    abstract fun getQuestionDao(): QuestionDao

    abstract fun getTermDao(): TermDao
}