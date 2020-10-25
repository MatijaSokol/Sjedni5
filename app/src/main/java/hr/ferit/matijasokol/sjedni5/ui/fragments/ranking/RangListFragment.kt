package hr.ferit.matijasokol.sjedni5.ui.fragments.ranking

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
import java.text.SimpleDateFormat
import java.util.*

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
        setHasOptionsMenu(true)
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
        viewPager.offscreenPageLimit = 3

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.rang_list_frag_options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        R.id.calendar -> {
            showDatePickerDialog()
            true
        }
        else -> false
    }

    private fun showDatePickerDialog() {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), R.style.MyDatePickerDialogTheme, onDateChanged(), year, month, day).show()
    }

    private fun onDateChanged(): DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)

        applyDateFilters(date)
    }

    private fun applyDateFilters(date: String) {
        val firstFragment = childFragmentManager.findFragmentByTag("f0") as? Category1Fragment
        val secondFragment = childFragmentManager.findFragmentByTag("f1") as? Category2Fragment
        val thirdFragment = childFragmentManager.findFragmentByTag("f2") as? Category3Fragment

        firstFragment?.applyDateFilter(date)
        secondFragment?.applyDateFilter(date)
        thirdFragment?.applyDateFilter(date)

        val values = date.split("-")
        val day = values[2].toInt()
        val month = values[1].toInt()
        val year = values[0].toInt()
        (requireActivity() as QuizActivity).viewModel.setTitle(getString(R.string.ranking_with_date, day, month, year))
    }

    fun setInitialList() {
        val firstFragment = childFragmentManager.findFragmentByTag("f0") as? Category1Fragment
        val secondFragment = childFragmentManager.findFragmentByTag("f1") as? Category2Fragment
        val thirdFragment = childFragmentManager.findFragmentByTag("f2") as? Category3Fragment

        firstFragment?.setInitialList()
        secondFragment?.setInitialList()
        thirdFragment?.setInitialList()

        (requireActivity() as QuizActivity).viewModel.setTitle(getString(R.string.ranking))
    }
}