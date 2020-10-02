package hr.ferit.matijasokol.sjedni5.data.db

import androidx.room.*
import hr.ferit.matijasokol.sjedni5.models.Term
import hr.ferit.matijasokol.sjedni5.other.Constants.TERMS_TABLE

@Dao
interface TermDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTerms(terms: List<Term>)

    @Query("SELECT * FROM $TERMS_TABLE")
    suspend fun getAllTerms(): List<Term>

    @Query("DELETE FROM $TERMS_TABLE")
    suspend fun deleteAllTerms()

    @Transaction
    suspend fun replaceAllTerms(terms: List<Term>) {
        deleteAllTerms()
        insertAllTerms(terms)
    }
}