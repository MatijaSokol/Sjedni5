package hr.ferit.matijasokol.sjedni5.models

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import hr.ferit.matijasokol.sjedni5.other.Constants.TERMS_TABLE

@Entity(tableName = TERMS_TABLE)
data class Term(
    val text: String = "",
    val imageName: String = "",
    @set:Exclude @get:Exclude var image: Bitmap? = null
) {
    @set:Exclude @get:Exclude
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}