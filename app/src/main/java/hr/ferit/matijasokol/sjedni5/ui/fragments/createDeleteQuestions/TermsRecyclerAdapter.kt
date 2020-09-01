package hr.ferit.matijasokol.sjedni5.ui.fragments.createDeleteQuestions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.models.Term
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.term_row.view.*

class TermsRecyclerAdapter(
    options: FirestoreRecyclerOptions<Term>
) : FirestoreRecyclerAdapter<Term, TermsRecyclerAdapter.TermViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TermViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.term_row, parent, false)
    )

    override fun onBindViewHolder(holder: TermViewHolder, position: Int, model: Term) = holder.bind(model)

    fun getItemSnapshot(position: Int) = snapshots.getSnapshot(position)

    inner class TermViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        LayoutContainer {

        override val containerView: View?
            get() = itemView

        fun bind(term: Term) {
            with(itemView) {
                tvText.text = term.text
            }
        }
    }
}