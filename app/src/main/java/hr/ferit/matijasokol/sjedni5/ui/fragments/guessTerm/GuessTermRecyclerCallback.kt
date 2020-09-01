package hr.ferit.matijasokol.sjedni5.ui.fragments.guessTerm

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class GuessTermRecyclerCallback(
    private val onLetterMoved: (Int, Int) -> Unit
) : ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT), 0) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val positionDragged = viewHolder.adapterPosition
        val positionTarget = target.adapterPosition

        onLetterMoved(positionDragged, positionTarget)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) { /* NO-OP */ }
}