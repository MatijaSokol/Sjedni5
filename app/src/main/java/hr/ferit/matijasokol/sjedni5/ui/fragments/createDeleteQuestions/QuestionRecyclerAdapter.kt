package hr.ferit.matijasokol.sjedni5.ui.fragments.createDeleteQuestions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.models.Question
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.question_row.view.*

class QuestionRecyclerAdapter(
    options: FirestoreRecyclerOptions<Question>
) : FirestoreRecyclerAdapter<Question, QuestionRecyclerAdapter.QuestionViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder = QuestionViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.question_row, parent, false)
    )

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int, model: Question) = holder.bind(model)

    fun getItemSnapshot(position: Int): DocumentSnapshot {
        return snapshots.getSnapshot(position)
    }

    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

        override val containerView: View?
            get() = itemView

        fun bind(question: Question) {
            with(itemView) {
                tvQuestion.text = question.text
                tvCorrectAnswer.text = context.getString(R.string.correct_answer, question.correctAnswer)
            }
        }
    }
}