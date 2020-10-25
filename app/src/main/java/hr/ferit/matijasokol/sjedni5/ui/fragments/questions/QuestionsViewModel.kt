package hr.ferit.matijasokol.sjedni5.ui.fragments.questions

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObject
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.app.QuizApp
import hr.ferit.matijasokol.sjedni5.models.Categories
import hr.ferit.matijasokol.sjedni5.models.Question
import hr.ferit.matijasokol.sjedni5.models.Resource
import hr.ferit.matijasokol.sjedni5.other.hasInternetConnection
import hr.ferit.matijasokol.sjedni5.repositories.QuizRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.IOException

class QuestionsViewModel @ViewModelInject constructor(
    app: Application,
    private val repository: QuizRepository
) : AndroidViewModel(app) {

    private val _questions = MutableLiveData<Resource<List<Question>>>()
    private val _questionCounter = MutableLiveData<Int>()

    var currentQuestion = -1
        private set

    val questions: LiveData<Resource<List<Question>>>
        get() = _questions

    val questionCounter: LiveData<Int>
        get() = _questionCounter

    var answered = false

    var firstJokerClicked = false
    var thirdJokerClicked = false

    var firstJokerUsed = false
    var secondJokerUsed = false
    var thirdJokerUsed = false

    var end = false

    var score = 0
        private set

    fun incrementScore() = score++

    fun incrementCurrentQuestion() {
        currentQuestion++
        _questionCounter.postValue(currentQuestion)
    }

    fun getQuestions(category: Categories) = viewModelScope.launch(IO) {
        try {
            val questionsFromDb = repository.getQuestionsFromDb(category)
            if (questionsFromDb.isNotEmpty()) {
                _questions.postValue(Resource.Success(questionsFromDb))
            } else {
                if (hasInternetConnection(getApplication())) {
                    _questions.postValue(Resource.Loading())
                    val querySnapshot = repository.getQuestions()
                    val questions = querySnapshot.documents.mapNotNull { it.toObject<Question>() }
                    repository.insertQuestions(questions)
                    val newQuestions = repository.getQuestionsFromDb(category)
                    _questions.postValue(Resource.Success(newQuestions))
                } else {
                    _questions.postValue(Resource.Error(getApplication<QuizApp>().getString(R.string.data_empty_int_conn_need_new_data)))
                }
            }
        } catch (t: Throwable) {
            when(t) {
                is IOException -> _questions.postValue(
                    Resource.Error(getApplication<QuizApp>().getString(
                        R.string.network_failure
                    )))
                else -> _questions.postValue(
                    Resource.Error(getApplication<QuizApp>().getString(
                        R.string.conversion_error
                    )))
            }
        }
    }
}