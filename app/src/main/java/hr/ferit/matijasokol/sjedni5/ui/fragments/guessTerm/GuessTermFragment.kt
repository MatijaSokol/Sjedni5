package hr.ferit.matijasokol.sjedni5.ui.fragments.guessTerm

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.models.Categories
import hr.ferit.matijasokol.sjedni5.models.Resource
import hr.ferit.matijasokol.sjedni5.models.Term
import hr.ferit.matijasokol.sjedni5.other.*
import hr.ferit.matijasokol.sjedni5.ui.activities.QuizActivity
import hr.ferit.matijasokol.sjedni5.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_guess_term.*

@AndroidEntryPoint
class GuessTermFragment : BaseFragment(R.layout.fragment_guess_term) {

    private val viewModel: GuessTermViewModel by viewModels()
    private lateinit var terms: List<Term>

    private val guessTermAdapter by lazy { GuessTermRecyclerAdapter() }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showAlertDialog()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun setUpUi() {
        setActionBarText(getString(R.string.term))
        setListeners()
        setRecycler()
        setObservers()
        viewModel.getTerms()
    }

    private fun setListeners() {
        btnSubmit.setOnClickListener {
            if (guessTermAdapter.areItemsSame()) {
                viewModel.incrementScore()
                viewModel.incrementTerm()
            } else {
                viewModel.decrementMistakes()
            }
        }
    }

    private fun setObservers() {
        viewModel.terms.observe(viewLifecycleOwner, { response ->
            when(response) {
                is Resource.Loading -> {
                    progress.visible()
                }
                is Resource.Success -> {
                    progress.gone()
                    response.data?.let {
                        terms = it.shuffled()
                        viewModel.incrementTerm()
                        viewModel.decrementMistakes()
                        tvMistakesText.visible()
                        btnSubmit.visible()
                    }
                }
                is Resource.Error -> {
                    progress.gone()
                    response.message?.let {
                        rootElement.showSnackbar(it, Snackbar.LENGTH_LONG)
                    }
                    navigate(GuessTermFragmentDirections.actionGuessTermFragmentToMenuFragment())
                }
            }
        })

        viewModel.currentTerm.observe(viewLifecycleOwner, { currentTermIndex ->
            if (currentTermIndex < terms.size) {
                setActionBarText("${getString(R.string.term)} ${viewModel.currentTermIndex + 1}/${terms.size}")
                setImageAndRecycler(terms[currentTermIndex])
            } else {
                navigate(GuessTermFragmentDirections.actionGuessTermFragmentToScoreFragment(viewModel.score, Categories.CATEGORY_3))
            }
        })

        viewModel.mistakes.observe(viewLifecycleOwner, { availableMistakes ->
            if (availableMistakes >= 0) {
                setMistakesTextView(availableMistakes)
            } else {
                navigate(GuessTermFragmentDirections.actionGuessTermFragmentToScoreFragment(viewModel.score, Categories.CATEGORY_3))
            }
        })
    }

    private fun setMistakesTextView(availableMistakes: Int?) {
        tvMistakesValue.text = getString(R.string.available_mistakes, availableMistakes)

        if (availableMistakes != 3) {
            val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce)
            tvMistakesValue.startAnimation(anim)
        }
    }

    private fun setImageAndRecycler(term: Term) {
        term.image?.let {
            ivPhoto.setImageWithAnimation(requireContext(), it)
        }
        guessTermAdapter.setNewData(term.text.toList())
    }

    private fun setRecycler() {
        recycler.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = guessTermAdapter
            setHasFixedSize(true)
        }

        ItemTouchHelper(GuessTermRecyclerCallback { draggedPosition, targetPosition ->
            onLetterMoved(draggedPosition, targetPosition)
        }).attachToRecyclerView(recycler)
    }

    private fun onLetterMoved(draggedPosition: Int, targetPosition: Int) {
        guessTermAdapter.swapItems(draggedPosition, targetPosition)
    }

    private fun setActionBarText(text: String) = (requireActivity() as QuizActivity).viewModel.setTitle(text)

    private fun showAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.close_quiz))
            .setMessage(getString(R.string.progress_not_saved))
            .setIcon(R.drawable.ic_exclamation_mark)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                navigate(GuessTermFragmentDirections.actionGuessTermFragmentToMenuFragment())
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}