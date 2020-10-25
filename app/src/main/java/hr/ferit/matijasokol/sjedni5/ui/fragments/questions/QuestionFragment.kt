package hr.ferit.matijasokol.sjedni5.ui.fragments.questions

import android.animation.Animator
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.models.AnswerButton
import hr.ferit.matijasokol.sjedni5.models.Question
import hr.ferit.matijasokol.sjedni5.models.Resource
import hr.ferit.matijasokol.sjedni5.other.*
import hr.ferit.matijasokol.sjedni5.other.Constants.LOTTIE_CORRECT_FILE_NAME
import hr.ferit.matijasokol.sjedni5.other.Constants.LOTTIE_INCORRECT_FILE_NAME
import hr.ferit.matijasokol.sjedni5.ui.activities.QuizActivity
import hr.ferit.matijasokol.sjedni5.ui.base.BaseFragment
import hr.ferit.matijasokol.sjedni5.ui.fragments.info.InfoBottomSheet
import hr.ferit.matijasokol.sjedni5.ui.fragments.jokers.JokerBottomSheet
import kotlinx.android.synthetic.main.fragment_question.*
import kotlin.math.sqrt

@AndroidEntryPoint
class QuestionFragment : BaseFragment(R.layout.fragment_question) {

    private val viewModel: QuestionsViewModel by viewModels()
    private val args: QuestionFragmentArgs by navArgs()

    private val answers by lazy {
        listOf(
            AnswerButton(btnAnswer1, false),
            AnswerButton(btnAnswer2, false),
            AnswerButton(btnAnswer3, false),
            AnswerButton(btnAnswer4, false)
        )
    }

    private lateinit var questions: List<Question>
    private lateinit var jokerQuestion: Question

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerationSensor: Sensor

    private var lastAcc = 0f
    private var currAcc = 0f

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showAlertDialog()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun setUpUi() {
        setActionBarText(getString(R.string.question))
        setHasOptionsMenu(true)
        setListeners()

        setObservers()
        viewModel.getQuestions(args.category)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(sensorListener, accelerationSensor, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorListener)
    }

    private fun setListeners() {
        btnInfo.setOnClickListener {
            if (hasInternetConnection(requireContext())) {
                val bottomSheetWebView = InfoBottomSheet(requireContext())
                val url = if (viewModel.thirdJokerClicked) jokerQuestion.url else getCurrentQuestionUrl()
                bottomSheetWebView.showWithUrl(url)
            } else {
                root.showSnackbar(getString(R.string.int_conn_need_show_info), Snackbar.LENGTH_LONG)
            }
        }

        lottie.setOnClickListener { onLottieClicked() }

        btnAnswer1.setOnClickListener { onAnswerClick(btnAnswer1) }
        btnAnswer2.setOnClickListener { onAnswerClick(btnAnswer2) }
        btnAnswer3.setOnClickListener { onAnswerClick(btnAnswer3) }
        btnAnswer4.setOnClickListener { onAnswerClick(btnAnswer4) }
    }

    private fun setObservers() {
        viewModel.questions.observe(viewLifecycleOwner, { response ->
            when(response) {
                is Resource.Loading -> {
                    progress.visible()
                }
                is Resource.Success -> {
                    progress.gone()
                    response.data?.let {
                        questions = it.shuffled()
                        jokerQuestion = questions.last()
                        questions = questions.subList(0, questions.size - 1)
                        questions.sortedBy { question -> question.level}
                        viewModel.incrementCurrentQuestion()
                    }
                }
                is Resource.Error -> {
                    progress.gone()
                    response.message?.let {
                        root.showSnackbar(it, Snackbar.LENGTH_LONG)
                    }
                    navigate(QuestionFragmentDirections.actionQuestionFragmentToMenuFragment())
                }
            }
        })

        viewModel.questionCounter.observe(viewLifecycleOwner, { questionIndex ->
            if (questionIndex < questions.size) {
                setQuestion(questions[questionIndex])
                setActionBarText("${getString(R.string.question)} ${viewModel.currentQuestion + 1}/${questions.size}")
                resetAllAnswerButtons()
                setAnswersClickability(true)
                tvQuestion.visible()
            } else {
                navigate(QuestionFragmentDirections.actionQuestionFragmentToScoreFragment(viewModel.score, args.category))
            }
        })
    }

    private fun findCorrectAnswerIndex() = answers.indexOfFirst { it.button.text == questions[viewModel.currentQuestion].correctAnswer }

    private fun onFirstJokerClicked() {
        requireContext().displayMessage(getString(R.string.half_half_joker_used))

        viewModel.firstJokerClicked = true
        viewModel.firstJokerUsed = true

        val allButtons = answers.map { it.button }

        var firstIncorrectIndex = 0
        var secondIncorrectIndex = 0

        if (viewModel.thirdJokerClicked) {
            val answers = listOf(jokerQuestion.answer1, jokerQuestion.answer2, jokerQuestion.answer3, jokerQuestion.answer4)
            val correctAnswerIndex = answers.indexOf(jokerQuestion.correctAnswer)

            firstIncorrectIndex = getRandomNumberExcept(0, allButtons.size, correctAnswerIndex)
            secondIncorrectIndex = getRandomNumberExcept(0, allButtons.size, correctAnswerIndex, firstIncorrectIndex)
        } else {
            val correctAnswerIndex = findCorrectAnswerIndex()

            firstIncorrectIndex = getRandomNumberExcept(0, allButtons.size, correctAnswerIndex)
            secondIncorrectIndex = getRandomNumberExcept(0, allButtons.size, correctAnswerIndex, firstIncorrectIndex)
        }

        allButtons.apply {
            get(firstIncorrectIndex).apply {
                isEnabled = false
                background = ContextCompat.getDrawable(requireContext(), R.drawable.question_shape_disabled)
            }
            get(secondIncorrectIndex).apply {
                isEnabled = false
                background = ContextCompat.getDrawable(requireContext(), R.drawable.question_shape_disabled)
            }
        }
    }

    private fun onSecondJokerClicked() {
        requireContext().displayMessage(getString(R.string.skip_joker_used))
        viewModel.secondJokerUsed = true
        viewModel.incrementScore()
        skipQuestion()
    }

    private fun onThirdJokerClicked() {
        requireContext().displayMessage(getString(R.string.swap_joker_used))
        setQuestion(jokerQuestion)
        resetAllAnswerButtons()
        viewModel.thirdJokerUsed = true
        viewModel.thirdJokerClicked = true
    }

    private fun onLottieClicked() {
        if (viewModel.end) {
            navigate(QuestionFragmentDirections.actionQuestionFragmentToScoreFragment(viewModel.score, args.category))
            return
        }

        lottie.gone()
        btnInfo.gone()
        viewModel.thirdJokerClicked = false
        viewModel.incrementCurrentQuestion()
    }

    private fun onAnswerClick(button: Button) {
        val answer = answers.first { it.button.id == button.id }
        if (!answer.clicked) {
            if (viewModel.firstJokerClicked) {
                resetOnlyEnabledAnswerButtons()
            } else {
                resetAllAnswerButtons()
            }
            answer.apply {
                button.setBackgroundResource(R.drawable.question_shape_selected)
                clicked = true
            }
        } else {
            checkForCorrectAnswer(button.text.toString())
            viewModel.firstJokerClicked = false
        }
    }

    private fun resetOnlyEnabledAnswerButtons() = answers.filter { it.button.isEnabled }.forEach {
        it.button.setBackgroundResource(R.drawable.question_shape_unselected)
        it.clicked = false
    }

    private fun resetAllAnswerButtons() = answers.forEach {
        it.button.setBackgroundResource(R.drawable.question_shape_unselected)
        it.button.isEnabled = true
        it.clicked = false
    }

    private fun checkForCorrectAnswer(answerText: String) {
        viewModel.answered = true
        tvQuestion.gone()
        btnInfo.visible()
        setAnswersClickability(false)
        if (viewModel.thirdJokerClicked) {
            if (jokerQuestion.correctAnswer == answerText) {
                viewModel.incrementScore()
                showLottieAnimation(LOTTIE_CORRECT_FILE_NAME)
            } else {
                viewModel.end = true
                showLottieAnimation(LOTTIE_INCORRECT_FILE_NAME)
            }
            answers.filter { it.button.text == jokerQuestion.correctAnswer }[0].button.setBackgroundResource(R.drawable.question_shape_correct)
        } else {
            if (questions[viewModel.currentQuestion].correctAnswer == answerText) {
                viewModel.incrementScore()
                showLottieAnimation(LOTTIE_CORRECT_FILE_NAME)
            } else {
                viewModel.end = true
                showLottieAnimation(LOTTIE_INCORRECT_FILE_NAME)
            }
            answers[findCorrectAnswerIndex()].button.setBackgroundResource(R.drawable.question_shape_correct)
        }
    }

    private fun skipQuestion() {
        onLottieClicked()
    }

    private fun showLottieAnimation(lottieFileName: String) {
        lottie.apply {
            setAnimation(lottieFileName)
            playAnimation()
            visible()
            isEnabled = false
            addAnimatorListener(lottieListener)
        }
    }

    private fun getCurrentQuestionUrl() = questions[viewModel.currentQuestion].url

    private fun setAnswersClickability(clickable: Boolean) = answers.forEach { it.button.isEnabled = clickable }

    private fun setQuestion(question: Question) {
        viewModel.answered = false
        question.apply {
            tvQuestion.text = text
            btnAnswer1.text = answer1
            btnAnswer2.text = answer2
            btnAnswer3.text = answer3
            btnAnswer4.text = answer4
        }
    }

    private val lottieListener = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(p0: Animator?) { /* NO-OP */ }

        override fun onAnimationEnd(p0: Animator?) { lottie.isEnabled = true }

        override fun onAnimationCancel(p0: Animator?) { /* NO-OP */ }

        override fun onAnimationStart(p0: Animator?) { /* NO-OP */ }
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.close_quiz))
            .setMessage(getString(R.string.progress_not_saved))
            .setIcon(R.drawable.ic_exclamation_mark)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                navigate(QuestionFragmentDirections.actionQuestionFragmentToMenuFragment())
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private val sensorListener = object : SensorEventListener {
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) { /* NO-OP */ }

        override fun onSensorChanged(event: SensorEvent?) {
            if (!viewModel.secondJokerUsed) {
                if (event != null && event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    calculateAcc(event.values)
                }
            }
        }
    }

    private fun calculateAcc(values: FloatArray) {
        val x = values[0]
        val y = values[1]
        val z = values[2]

        lastAcc = currAcc
        currAcc = sqrt(x*x + y*y + z*z)

        val delta = currAcc - lastAcc
        var acc = 0f
        acc = acc * 0.9f + delta

        if (acc > 25) {
            onSecondJokerClicked()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) = inflater.inflate(R.menu.questions_frag_options_menu, menu)

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        R.id.joker -> {
            if (!viewModel.answered) {
                showBottomSheet()
            }
            true
        } else -> false
    }

    private fun showBottomSheet() {
        val bottomSheet = JokerBottomSheet.newInstance()
        bottomSheet.setListeners(
            { onFirstJokerClicked() },
            { onSecondJokerClicked() },
            { onThirdJokerClicked() }
        )
        bottomSheet.setJokerUsage(viewModel.firstJokerUsed, viewModel.secondJokerUsed, viewModel.thirdJokerUsed)
        bottomSheet.show(
            childFragmentManager, ""
        )
    }

    private fun setActionBarText(text: String) = (requireActivity() as QuizActivity).viewModel.setTitle(text)
}