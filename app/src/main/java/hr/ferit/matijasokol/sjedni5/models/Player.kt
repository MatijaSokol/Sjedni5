package hr.ferit.matijasokol.sjedni5.models

import java.text.SimpleDateFormat
import java.util.*

class Player(
    val name: String = "",
    val score: Int = 0,
    val category: String = "",
    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
)