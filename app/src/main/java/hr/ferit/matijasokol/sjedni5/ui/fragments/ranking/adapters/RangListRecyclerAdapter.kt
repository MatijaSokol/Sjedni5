package hr.ferit.matijasokol.sjedni5.ui.fragments.ranking.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.models.Player
import hr.ferit.matijasokol.sjedni5.other.invisible
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.player_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class RangListRecyclerAdapter(
    options: FirestoreRecyclerOptions<Player>,
    private val onDataChanged: (Int) -> Unit
) : FirestoreRecyclerAdapter<Player, RangListRecyclerAdapter.RangListViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RangListViewHolder = RangListViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.player_row, parent, false)
    )

    override fun onBindViewHolder(holder: RangListViewHolder, position: Int, model: Player) = holder.bind(model)

    fun notifyForMedalImages() {
        if (itemCount in 1..3) {
            for (i in 0..itemCount) notifyItemChanged(i)
        } else if (itemCount > 3) {
            for (i in 0..3) notifyItemChanged(i)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onDataChanged() {
        super.onDataChanged()
        onDataChanged(itemCount)
    }

    inner class RangListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

        override val containerView: View?
            get() = itemView

        fun bind(player: Player) {
            with(itemView) {
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(player.date)
                date?.let {
                    val cal = Calendar.getInstance()
                    cal.time = it
                    tvDate.text = itemView.context.getString(
                        R.string.date,
                        cal.get(Calendar.DAY_OF_MONTH),
                        cal.get(Calendar.MONTH) + 1,
                        cal.get(Calendar.YEAR)
                    )
                }


                tvName.text = player.name
                tvScore.text = itemView.context.getString(R.string.score_with_number, player.score)
                tvRank.text = itemView.context.getString(R.string.rank, adapterPosition + 1)

                when (adapterPosition) {
                    0 -> ivMedal.setImageResource(R.drawable.gold_medal)
                    1 -> ivMedal.setImageResource(R.drawable.silver_medal)
                    2 -> ivMedal.setImageResource(R.drawable.bronze_medal)
                    else -> ivMedal.invisible()
                }
            }
        }
    }
}
