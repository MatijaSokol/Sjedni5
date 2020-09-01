package hr.ferit.matijasokol.sjedni5.other

import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.models.Instruction

object InstructionsRepository {

    val instructions by lazy {
        listOf(
            Instruction(
                R.drawable.instruction1,
                R.string.instruction1
            ),
            Instruction(
                R.drawable.instruction2,
                R.string.instruction2
            ),
            Instruction(
                R.drawable.instruction3,
                R.string.instruction3
            ),
            Instruction(
                R.drawable.instruction4,
                R.string.instruction4
            )
        )
    }
}