package hr.ferit.matijasokol.sjedni5.ui.activities

import android.content.SharedPreferences
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.other.gone
import hr.ferit.matijasokol.sjedni5.other.visible
import hr.ferit.matijasokol.sjedni5.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_quiz.*
import javax.inject.Inject

@AndroidEntryPoint
class QuizActivity : BaseActivity(R.layout.activity_quiz) {

    val viewModel: QuizViewModel by viewModels()

    @set:Inject
    var instructionsEnabled = true

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    override fun setUpUi() {
        bottomNavigationView.setupWithNavController(fragmentContainer.findNavController())

        bottomNavigationView.setOnNavigationItemReselectedListener { /* NO-OP */ }
        fragmentContainer.findNavController().addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.menuFragment, R.id.rangListFragment -> bottomNavigationView.visible()
                else -> bottomNavigationView.gone()
            }
        }

        viewModel.title.observe(this, Observer {  title ->
            supportActionBar?.title = title
        })
    }
}
