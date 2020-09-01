package hr.ferit.matijasokol.sjedni5.ui.fragments.menu

import android.app.Application
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObject
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.app.QuizApp
import hr.ferit.matijasokol.sjedni5.models.Question
import hr.ferit.matijasokol.sjedni5.models.Resource
import hr.ferit.matijasokol.sjedni5.models.Term
import hr.ferit.matijasokol.sjedni5.other.Constants
import hr.ferit.matijasokol.sjedni5.other.hasInternetConnection
import hr.ferit.matijasokol.sjedni5.repositories.QuizRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException

class MenuViewModel @ViewModelInject constructor(
    app: Application,
    private val repository: QuizRepository
) : AndroidViewModel(app) {

    private val _questionResponse = MutableLiveData<Resource<Boolean>>()

    val questionResponse: LiveData<Resource<Boolean>>
        get() = _questionResponse

    private val _termResponse = MutableLiveData<Resource<Boolean>>()

    val termResponse: LiveData<Resource<Boolean>>
        get() = _termResponse

    fun updateQuestionsAndTerms() = viewModelScope.launch(IO) {
        async {
            updateQuestions()
            updateTerms()
        }
    }

    private suspend fun updateQuestions() {
        if (hasInternetConnection(getApplication())) {
            _questionResponse.postValue(Resource.Loading())
            try {
                val newQuestions = repository.getQuestions().documents.mapNotNull { it.toObject<Question>() }
                repository.deleteAllQuestions()
                repository.insertQuestions(newQuestions)
                _questionResponse.postValue(Resource.Success(true))
            } catch (t: Throwable) {
                when(t) {
                    is IOException -> _questionResponse.postValue(
                        Resource.Error(getApplication<QuizApp>().getString(
                            R.string.network_failure
                        )))
                    else -> _questionResponse.postValue(
                        Resource.Error(getApplication<QuizApp>().getString(
                            R.string.conversion_error
                        )))
                }
            }
        } else {
            _questionResponse.postValue(
                Resource.Error(getApplication<QuizApp>().getString(R.string.int_conn_need_new_data))
            )
        }
    }

    private suspend fun updateTerms() {
        if (hasInternetConnection(getApplication())) {
            _termResponse.postValue(Resource.Loading())
            try {
                val newTerms = repository.getTerms().documents.mapNotNull { it.toObject<Term>() }
                newTerms.forEach {  term ->
                    val array = repository.getImageFromStorage(term.imageName)
                    term.image = BitmapFactory.decodeByteArray(array, 0, array.size)
                }
                repository.deleteAllTerms()
                repository.insertTerms(newTerms)
                _termResponse.postValue(Resource.Success(true))
            } catch (t: Throwable) {
                when(t) {
                    is IOException -> _termResponse.postValue(
                        Resource.Error(getApplication<QuizApp>().getString(
                            R.string.network_failure
                        )))
                    else -> _termResponse.postValue(
                        Resource.Error(getApplication<QuizApp>().getString(
                            R.string.conversion_error
                        )))
                }
            }
        } else {
            _questionResponse.postValue(
                Resource.Error(getApplication<QuizApp>().getString(R.string.int_conn_need_new_data))
            )
        }
    }
}