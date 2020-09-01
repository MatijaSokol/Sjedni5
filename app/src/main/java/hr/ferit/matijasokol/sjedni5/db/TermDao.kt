package hr.ferit.matijasokol.sjedni5.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hr.ferit.matijasokol.sjedni5.models.Term
import hr.ferit.matijasokol.sjedni5.other.Constants.TERMS_TABLE

@Dao
interface TermDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTerm(term: Term): Long

    @Query("SELECT * FROM $TERMS_TABLE")
    suspend fun getAllTerms(): List<Term>

    @Query("DELETE FROM $TERMS_TABLE")
    suspend fun deleteAllTerms()
}