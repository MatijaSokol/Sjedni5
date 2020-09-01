package hr.ferit.matijasokol.sjedni5.models

import com.google.firebase.Timestamp

class Player(
    val name: String = "",
    val score: Float = 0f,
    val category: String = "",
    val time: Timestamp = Timestamp.now()
)