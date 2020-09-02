package hr.ferit.matijasokol.sjedni5.ui.fragments.menu

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.models.Categories
import hr.ferit.matijasokol.sjedni5.models.Resource
import hr.ferit.matijasokol.sjedni5.other.*
import hr.ferit.matijasokol.sjedni5.other.Constants.INSTRUCTIONS_KEY
import hr.ferit.matijasokol.sjedni5.ui.activities.QuizActivity
import hr.ferit.matijasokol.sjedni5.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_menu.*

@AndroidEntryPoint
class MenuFragment : BaseFragment(R.layout.fragment_menu) {

    private val viewModel: MenuViewModel by viewModels()

    private var menu: Menu? = null

    override fun setUpUi() {
        (requireActivity() as QuizActivity).viewModel.setTitle(getString(R.string.menu))
        setHasOptionsMenu(true)
        setObservers()
        setListeners()
    }

    private fun setListeners() {
        category1.setOnClickListener { navigateToNextFragment(Categories.CATEGORY_1) }
        category2.setOnClickListener { navigateToNextFragment(Categories.CATEGORY_2) }
        category3.setOnClickListener { navigateToNextFragment(Categories.CATEGORY_3) }
    }

    private fun navigateToNextFragment(category: Categories) {
        if (category == Categories.CATEGORY_3) {
            navigate(MenuFragmentDirections.actionMenuFragmentToGuessTermFragment())
            return
        }

        if ((requireActivity() as QuizActivity).instructionsEnabled) {
            navigate(MenuFragmentDirections.actionMenuFragmentToManualsFragment(category))
        } else {
            navigate(MenuFragmentDirections.actionMenuFragmentToQuestionFragment(category))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_frag_options_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.instructions).isChecked = (requireActivity() as QuizActivity).instructionsEnabled
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        R.id.settings -> {
            viewModel.updateQuestionsAndTerms()
            true
        }
        R.id.instructions -> {
            (requireActivity() as QuizActivity).instructionsEnabled = !(requireActivity() as QuizActivity).instructionsEnabled
            item.isChecked = (requireActivity() as QuizActivity).instructionsEnabled
            saveToPrefs((requireActivity() as QuizActivity).instructionsEnabled)
            true
        }
        R.id.login -> {
            if (hasInternetConnection(requireContext())) {
                navigate(MenuFragmentDirections.actionMenuFragmentToLoginFragment())
            } else {
                rootElement.showSnackbar(getString(R.string.int_conn_need_login))
            }
            true
        }
        else -> false
    }

    private fun saveToPrefs(checked: Boolean) {
        (requireActivity() as QuizActivity).sharedPrefs
            .edit()
            .putBoolean(INSTRUCTIONS_KEY, checked)
            .apply()
    }

    private fun setObservers() {
        viewModel.questionResponse.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Loading -> {
                    progress.visible()
                }
            }
        })

        viewModel.termResponse.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Loading -> {
                    progress.visible()
                }
                is Resource.Success -> {
                    progress.gone()
                }
                is Resource.Error -> {
                    progress.gone()
                    response.message?.let {
                        rootElement.showSnackbar(it, Snackbar.LENGTH_LONG)
                    }
                }
            }
        })
    }

}