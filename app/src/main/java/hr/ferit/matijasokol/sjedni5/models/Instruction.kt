package hr.ferit.matijasokol.sjedni5.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Instruction(
    val imageResourceId: Int,
    val textResourceId: Int
) : Parcelable