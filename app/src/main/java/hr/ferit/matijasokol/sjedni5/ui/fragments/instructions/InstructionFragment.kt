package hr.ferit.matijasokol.sjedni5.ui.fragments.instructions

import android.os.Bundle
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.models.Instruction
import hr.ferit.matijasokol.sjedni5.other.Constants.INSTRUCTION_KEY
import hr.ferit.matijasokol.sjedni5.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_instruction.*

class InstructionFragment : BaseFragment(R.layout.fragment_instruction) {

    override fun setUpUi() {
        setView()
    }

    private fun setView() {
        val instruction = arguments?.getParcelable(INSTRUCTION_KEY) ?: Instruction(R.drawable.instruction1, R.string.instruction1)

        tvInstruction.text = getString(instruction.textResourceId)
        ivInstruction.setImageResource(instruction.imageResourceId)
    }

    companion object {
        fun newInstance(instruction: Instruction) = InstructionFragment().apply {
            arguments = Bundle().apply {
                putParcelable(INSTRUCTION_KEY, instruction)
            }
        }
    }
}