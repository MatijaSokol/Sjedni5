package hr.ferit.matijasokol.sjedni5.ui.fragments.instructions

import android.os.Bundle
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.other.navigate
import hr.ferit.matijasokol.sjedni5.ui.activities.QuizActivity
import hr.ferit.matijasokol.sjedni5.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_instructions.*

class InstructionsFragment : BaseFragment(R.layout.fragment_instructions) {

    private val pagerAdapter by lazy { InstructionsPagerAdapter(this) }
    private val args: InstructionsFragmentArgs by navArgs()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (requireActivity() as QuizActivity).viewModel.setTitle(getString(R.string.instructions))
    }

    override fun setUpUi() {
        setPagerAndTabLayout()
        setListeners()
    }

    private fun setListeners() {
        btnSkip.setOnClickListener {
            navigate(InstructionsFragmentDirections.actionManualsFragmentToQuestionFragment(args.category))
        }
    }

    private fun setPagerAndTabLayout() {
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { _, _ ->
            /* NO-OP */
        }.attach()
    }

    companion object {
        fun newInstance() = InstructionsFragment()
    }
}