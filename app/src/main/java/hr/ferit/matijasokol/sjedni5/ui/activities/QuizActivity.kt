package hr.ferit.matijasokol.sjedni5.ui.activities

import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.other.gone
import hr.ferit.matijasokol.sjedni5.other.visible
import hr.ferit.matijasokol.sjedni5.ui.base.BaseActivity
import hr.ferit.matijasokol.sjedni5.ui.fragments.ranking.RangListFragment
import kotlinx.android.synthetic.main.activity_quiz.*
import javax.inject.Inject

@AndroidEntryPoint
class QuizActivity : BaseActivity(R.layout.activity_quiz) {

    val viewModel: QuizViewModel by viewModels()

    @set:Inject
    var instructionsEnabled = true

    override fun setUpUi() {
        bottomNavigationView.setupWithNavController(fragmentContainer.findNavController())

        bottomNavigationView.setOnNavigationItemReselectedListener { menuItem ->
            if (menuItem.itemId == R.id.rangListFragment) {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as? NavHostFragment
                val rangListFragment = navHostFragment?.childFragmentManager?.fragments?.last() as? RangListFragment
                rangListFragment?.setInitialList()
            }
        }

        fragmentContainer.findNavController().addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.menuFragment, R.id.rangListFragment -> bottomNavigationView.visible()
                else -> bottomNavigationView.gone()
            }
        }

        viewModel.title.observe(this, {  title ->
            supportActionBar?.title = title
        })
    }
}
