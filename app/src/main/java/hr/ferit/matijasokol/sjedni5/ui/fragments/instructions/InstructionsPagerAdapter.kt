package hr.ferit.matijasokol.sjedni5.ui.fragments.instructions

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import hr.ferit.matijasokol.sjedni5.other.InstructionsRepository

class InstructionsPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = InstructionsRepository.instructions.size

    override fun createFragment(position: Int): Fragment = InstructionFragment.newInstance(
        InstructionsRepository.instructions[position])
}