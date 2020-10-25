package hr.ferit.matijasokol.sjedni5.ui.fragments.ranking

import android.widget.Toast
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.models.Categories
import hr.ferit.matijasokol.sjedni5.models.Player
import hr.ferit.matijasokol.sjedni5.other.Constants
import hr.ferit.matijasokol.sjedni5.other.displayMessage
import hr.ferit.matijasokol.sjedni5.other.hasInternetConnection
import hr.ferit.matijasokol.sjedni5.ui.base.BaseFragment
import hr.ferit.matijasokol.sjedni5.ui.dialogs.playerInput.PlayerInputDialog
import hr.ferit.matijasokol.sjedni5.ui.fragments.ranking.adapters.RangListRecyclerAdapter

abstract class BaseCategoryFragment(layoutResourceId: Int) : BaseFragment(layoutResourceId) {

    abstract fun onPlayerAdded()
    abstract fun setRecycler(adapter: RangListRecyclerAdapter?)
    abstract fun applyDateFilter(date: String)
    abstract fun setInitialList()

    protected fun getRangListAdapter(category: String, onDataChanged: (Int) -> Unit): RangListRecyclerAdapter {
        val query = Firebase.firestore
            .collection(Constants.PLAYERS_COLLECTION)
            .whereEqualTo(Constants.CATEGORY_FIELD, category)
            .orderBy(Constants.SCORE_FIELD, Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<Player>()
            .setQuery(query, Player::class.java)
            .build()

        return RangListRecyclerAdapter(
            options,
            onDataChanged
        )
    }

    protected fun openDialog(score: Int?, category: Categories) {
        if (hasInternetConnection(requireContext())) {
            val dialog = PlayerInputDialog.newInstance()
            score?.let {
                dialog.setScore(it)
            }
            dialog.setCategory(category)
            dialog.setOnPlayerAddedListener {
                onPlayerAdded()
            }
            dialog.show(childFragmentManager, "")
        } else {
            requireContext().displayMessage(getString(R.string.int_conn_need_submit_result), Toast.LENGTH_LONG)
        }
    }

    protected fun getUpdatedRangListAdapter(date: String, category: Categories, onDataChanged: (Int) -> Unit): RangListRecyclerAdapter {
        val query = Firebase.firestore
            .collection(Constants.PLAYERS_COLLECTION)
            .whereEqualTo(Constants.CATEGORY_FIELD, category.type)
            .whereEqualTo(Constants.DATE_FIELD, date)
            .orderBy(Constants.SCORE_FIELD, Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<Player>()
            .setQuery(query, Player::class.java)
            .build()

        return RangListRecyclerAdapter(options, onDataChanged)
    }
}