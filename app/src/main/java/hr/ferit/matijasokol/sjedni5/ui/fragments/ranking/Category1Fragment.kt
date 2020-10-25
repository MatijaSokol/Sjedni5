package hr.ferit.matijasokol.sjedni5.ui.fragments.ranking

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.models.Categories
import hr.ferit.matijasokol.sjedni5.other.Constants.DIALOG_KEY
import hr.ferit.matijasokol.sjedni5.other.Constants.SCORE_KEY
import hr.ferit.matijasokol.sjedni5.other.gone
import hr.ferit.matijasokol.sjedni5.other.visible
import hr.ferit.matijasokol.sjedni5.ui.fragments.ranking.adapters.RangListRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_rang.*

class Category1Fragment : BaseCategoryFragment(R.layout.fragment_rang) {

    private var rangListRecyclerAdapter: RangListRecyclerAdapter? = null
    private var updatedRangListRecyclerAdapter: RangListRecyclerAdapter? = null
    private var firstRun = true
    private var score: Int? = null

    override fun setUpUi() {
        rangListRecyclerAdapter = getRangListAdapter(Categories.CATEGORY_1.type) { onDataChanged(it) }
        setRecycler(rangListRecyclerAdapter)

        val shouldOpenDialog = arguments?.getBoolean(DIALOG_KEY) ?: false
        score = arguments?.getInt(SCORE_KEY)

        if (shouldOpenDialog && firstRun) {
            firstRun = false
            openDialog(score, Categories.CATEGORY_1)
        }
    }

    override fun setRecycler(adapter: RangListRecyclerAdapter?) {
        recycler.apply {
            this.adapter = adapter
            itemAnimator = null
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    override fun onStart() {
        super.onStart()
        rangListRecyclerAdapter?.startListening() ?: updatedRangListRecyclerAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        rangListRecyclerAdapter?.stopListening() ?: updatedRangListRecyclerAdapter?.stopListening()
    }

    private fun onDataChanged(itemCount: Int, filtered: Boolean = false, date: String = "") {
        val values = date.split("-")
        if (itemCount == 0) {
            tvEmptyList?.visible()
            tvEmptyList?.text = if (filtered) {
                getString(R.string.empty_list_with_date, values[2].toInt(), values[1].toInt(), values[0].toInt())
            } else {
                getString(R.string.empty_list)
            }
        } else {
            tvEmptyList?.gone()
        }
    }

    override fun onPlayerAdded() {
        rangListRecyclerAdapter?.notifyForMedalImages()
        recycler.smoothScrollToPosition(0)
    }

    override fun applyDateFilter(date: String) {
        rangListRecyclerAdapter?.stopListening()
        rangListRecyclerAdapter = null
        updatedRangListRecyclerAdapter = null
        updatedRangListRecyclerAdapter = getUpdatedRangListAdapter(date, Categories.CATEGORY_1) { onDataChanged(it) }
        updatedRangListRecyclerAdapter?.startListening()
        setRecycler(updatedRangListRecyclerAdapter)
        onDataChanged(updatedRangListRecyclerAdapter!!.itemCount, true, date)
    }

    override fun setInitialList() {
        if (rangListRecyclerAdapter == null) {
            rangListRecyclerAdapter = getRangListAdapter(Categories.CATEGORY_1.type) { onDataChanged(it) }
            updatedRangListRecyclerAdapter?.stopListening()
            updatedRangListRecyclerAdapter = null
            rangListRecyclerAdapter?.startListening()
            setRecycler(rangListRecyclerAdapter)
        }
    }

    companion object {
        fun newInstance(shouldOpenDialog: Boolean, score: Int? = null) = Category1Fragment().apply {
            arguments = Bundle().apply {
                putBoolean(DIALOG_KEY, shouldOpenDialog)
                score?.let { putInt(SCORE_KEY, it) }
            }
        }
    }
}