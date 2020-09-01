package hr.ferit.matijasokol.sjedni5.ui.dialogs.questionInput

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.models.Question
import hr.ferit.matijasokol.sjedni5.models.Resource
import hr.ferit.matijasokol.sjedni5.other.Constants.CATEGORY_1
import hr.ferit.matijasokol.sjedni5.other.Constants.CATEGORY_2
import hr.ferit.matijasokol.sjedni5.other.Constants.LEVEL_KEY
import hr.ferit.matijasokol.sjedni5.other.displayMessage
import hr.ferit.matijasokol.sjedni5.other.visible
import kotlinx.android.synthetic.main.dialog_add_question.*

@AndroidEntryPoint
class QuestionInputDialog : DialogFragment() {

    private val viewModel: QuestionInputViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_add_question, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val level = arguments?.getInt(LEVEL_KEY) ?: 1
        spCategory.setSelection(level - 1)

        setListeners()
        setObservers()
    }

    private fun setObservers() {
        viewModel.uploadStatus.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Loading -> {
                    progress.visible()
                }
                is Resource.Success -> {
                    dismiss()
                }
                is Resource.Error -> {
                    dismiss()
                    response.message?.let {
                        requireContext().displayMessage(it)
                    }
                }
            }
        })
    }

    private fun setListeners() {
        btnSave.setOnClickListener {
            addQuestion()
        }
    }

    private fun addQuestion() {
        val category = when(spCategory.selectedItem.toString()) {
            getString(R.string.category1) -> CATEGORY_1
            else -> CATEGORY_2
        }

        val question = Question(
            etQuestionText.text.toString(),
            etAnswer1.text.toString(),
            etAnswer2.text.toString(),
            etAnswer3.text.toString(),
            etAnswer4.text.toString(),
            etCorrectAnswer.text.toString(),
            etUrl.text.toString(),
            category,
            spLevel.selectedItem.toString().toInt()
        )

        val valid = isQuestionValid(question)

        if (valid) {
            viewModel.uploadQuestion(question)
        }
    }

    private fun isQuestionValid(question: Question): Boolean {
        if (question.isSomePropertyEmpty()) {
            requireContext().displayMessage(getString(R.string.inputs_empty))
            return false
        }

        if (!question.isCorrectAnswerInAnswers()) {
            requireContext().displayMessage(getString(R.string.correct_answer_not_valid))
            return false
        }

        if (!question.isUrlValid()) {
            requireContext().displayMessage(getString(R.string.url_not_valid))
            return false
        }

        return true
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    companion object {
        fun newInstance(level: Int) = QuestionInputDialog().apply {
            arguments = Bundle().apply {
                putInt(LEVEL_KEY, level)
            }
        }
    }

}