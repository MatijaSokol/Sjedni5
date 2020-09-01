package hr.ferit.matijasokol.sjedni5.ui.fragments.guessTerm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.other.swap
import kotlinx.android.synthetic.main.term_item.view.*

class GuessTermRecyclerAdapter : RecyclerView.Adapter<GuessTermRecyclerAdapter.GuessTermViewHolder>() {

    private val data = mutableListOf<Char>()
    private var originalData = data.toString()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuessTermViewHolder = GuessTermViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.term_item, parent, false)
    )

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: GuessTermViewHolder, position: Int) = holder.bind(data[position])

    fun setNewData(data: List<Char>) {
        originalData = data.toString()
        this.data.apply {
            clear()
            addAll(data)
        }
        this.data.shuffle()
        notifyDataSetChanged()
    }

    fun swapItems(draggedPosition: Int, targetPosition: Int) {
        data.swap(draggedPosition, targetPosition)
        notifyItemMoved(draggedPosition, targetPosition)
    }

    fun areItemsSame(): Boolean {
        return originalData == data.toString()
    }

    inner class GuessTermViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(letter: Char) {
            with(itemView) {
                tvLetter.text = letter.toLowerCase().toString()
            }
        }


    }
}