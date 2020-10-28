package hr.ferit.matijasokol.sjedni5.ui.fragments.score

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.navArgs
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.other.navigate
import hr.ferit.matijasokol.sjedni5.ui.activities.QuizActivity
import hr.ferit.matijasokol.sjedni5.ui.base.BaseFragment
import hr.ferit.matijasokol.sjedni5.ui.fragments.ranking.RangListFragmentArgs
import kotlinx.android.synthetic.main.fragment_score.*

class ScoreFragment : BaseFragment(R.layout.fragment_score) {

    private val args: RangListFragmentArgs by navArgs()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showAlertDialog()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        (requireActivity() as QuizActivity).viewModel.setTitle(getString(R.string.score))
    }

    override fun setUpUi() {
        setViews()
        setListeners()
    }

    private fun setViews() {
        tvScore.text = getString(R.string.score_with_number, args.score)

        when(args.score) {
            in 0 until 4 -> {
                ivResultImage.setImageResource(R.drawable.bad_result)
                tvMessage.text = getString(R.string.you_can_do_it_better)
            }
            in 4 until 8 -> {
                ivResultImage.setImageResource(R.drawable.average_result)
                tvMessage.text = getString(R.string.average_result)
            }
            in 8..10 -> {
                ivResultImage.setImageResource(R.drawable.excellent_result)
                tvMessage.text = getString(R.string.excellent_result)
            }
        }
    }

    private fun setListeners() {
        btnSubmit.setOnClickListener {
            navigate(ScoreFragmentDirections.actionScoreFragmentToRangListFragment(args.score, args.category))
        }

        btnCancel.setOnClickListener {
            navigate(ScoreFragmentDirections.actionScoreFragmentToRangListFragment(args.score))
        }
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.go_to_menu_page))
            .setMessage(getString(R.string.result_will_not_be_saved))
            .setIcon(R.drawable.ic_exclamation_mark)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                navigate(ScoreFragmentDirections.actionScoreFragmentToMenuFragment())
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}