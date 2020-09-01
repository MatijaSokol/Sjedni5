package hr.ferit.matijasokol.sjedni5.ui.fragments.ranking

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.models.Categories
import hr.ferit.matijasokol.sjedni5.other.Constants.DIALOG_KEY
import hr.ferit.matijasokol.sjedni5.other.Constants.SCORE_KEY
import hr.ferit.matijasokol.sjedni5.other.gone
import hr.ferit.matijasokol.sjedni5.other.visible
import kotlinx.android.synthetic.main.fragment_rank.*

class Category3Fragment : BaseCategoryFragment(R.layout.fragment_rank) {

    private val rangListRecyclerAdapter by lazy { getRangListAdapter(Categories.CATEGORY_3.type) { onDataChanged(it) } }
    private var firstRun = true
    private var score: Float? = null

    override fun setUpUi() {
        setRecycler()

        val shouldOpenDialog = arguments?.getBoolean(DIALOG_KEY) ?: false
        score = arguments?.getFloat(SCORE_KEY)

        if (shouldOpenDialog && firstRun) {
            firstRun = false
            openDialog(score, Categories.CATEGORY_3)
        }
    }

    private fun setRecycler() {
        recycler.apply {
            adapter = rangListRecyclerAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    override fun onStart() {
        super.onStart()
        rangListRecyclerAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        rangListRecyclerAdapter.stopListening()
    }

    private fun onDataChanged(itemCount: Int) {
        if (itemCount == 0) {
            tvEmptyList.visible()
        } else {
            tvEmptyList.gone()
        }
    }

    override fun onPlayerAdded() {
        rangListRecyclerAdapter.notifyForMedalImages()
        recycler.smoothScrollToPosition(0)
    }

    companion object {
        fun newInstance(shouldOpenDialog: Boolean, score: Float? = null) = Category3Fragment().apply {
            arguments = Bundle().apply {
                putBoolean(DIALOG_KEY, shouldOpenDialog)
                score?.let { putFloat(SCORE_KEY, it) }
            }
        }
    }
}