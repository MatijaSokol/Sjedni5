package hr.ferit.matijasokol.sjedni5.ui.fragments.ranking

import android.os.Bundle
import android.os.Handler
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.models.Categories
import hr.ferit.matijasokol.sjedni5.other.navigate
import hr.ferit.matijasokol.sjedni5.ui.activities.QuizActivity
import hr.ferit.matijasokol.sjedni5.ui.base.BaseFragment
import hr.ferit.matijasokol.sjedni5.ui.fragments.ranking.adapters.RangListPagerAdapter
import kotlinx.android.synthetic.main.fragment_rank_pager.*

class RangListFragment : BaseFragment(R.layout.fragment_rank_pager) {

    private val args: RangListFragmentArgs by navArgs()
    private val pagerAdapter by lazy {
        RangListPagerAdapter(
            this
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigate(RangListFragmentDirections.actionRangListFragmentToMenuFragment())
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        (requireActivity() as QuizActivity).viewModel.setTitle(getString(R.string.ranking))
    }

    override fun setUpUi() {
        setPagerAndTabLayout()
    }

    private fun setPagerAndTabLayout() {
        pagerAdapter.setFragments(
            when(args.category) {
                Categories.CATEGORY_1 -> listOf(
                    Category1Fragment.newInstance(true, args.score),
                    Category2Fragment.newInstance(false),
                    Category3Fragment.newInstance(false)
                )
                Categories.CATEGORY_2 -> listOf(
                    Category1Fragment.newInstance(false),
                    Category2Fragment.newInstance(true, args.score),
                    Category3Fragment.newInstance(false)
                )
                Categories.CATEGORY_3 -> listOf(
                    Category1Fragment.newInstance(false),
                    Category2Fragment.newInstance(false),
                    Category3Fragment.newInstance(true, args.score)
                )
                Categories.NO_CATEGORY -> listOf(
                    Category1Fragment.newInstance(false),
                    Category2Fragment.newInstance(false),
                    Category3Fragment.newInstance(false)
                )
            }
        )

        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> getString(R.string.category1)
                1 -> getString(R.string.category2)
                2 -> getString(R.string.category3)
                else -> ""
            }
        }.attach()

        when(args.category) {
            Categories.CATEGORY_1 -> Handler().postDelayed( { viewPager.setCurrentItem(0, true) }, 100)
            Categories.CATEGORY_2 -> Handler().postDelayed( { viewPager.setCurrentItem(1, true) }, 100)
            Categories.CATEGORY_3 -> Handler().postDelayed( { viewPager.setCurrentItem(2, true) }, 100)
            Categories.NO_CATEGORY -> Handler().postDelayed( { viewPager.setCurrentItem(0, true) }, 100)
        }
    }
}