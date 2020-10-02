package hr.ferit.matijasokol.sjedni5.ui.fragments.menu

import android.app.Application
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
import hr.ferit.matijasokol.sjedni5.other.hasInternetConnection
import hr.ferit.matijasokol.sjedni5.repositories.QuizRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.io.IOException

class MenuViewModel @ViewModelInject constructor(
    app: Application,
    private val repository: QuizRepository
) : AndroidViewModel(app) {

    private val _questionAndTermsResponse = MutableLiveData<Resource<Unit>>()

    val questionAndTermsResponse: LiveData<Resource<Unit>>
        get() = _questionAndTermsResponse

    fun updateQuestionsAndTerms() = viewModelScope.launch(IO) {
        if (hasInternetConnection(getApplication())) {
            _questionAndTermsResponse.postValue(Resource.Loading())
            try {
                awaitAll(
                    async { updateQuestions() },
                    async { updateTerms() }
                )
                _questionAndTermsResponse.postValue(Resource.Success(Unit))
            } catch (t: Throwable) {
                when(t) {
                    is IOException -> _questionAndTermsResponse.postValue(
                        Resource.Error(getApplication<QuizApp>().getString(
                            R.string.network_failure
                        )))
                    else -> _questionAndTermsResponse.postValue(
                        Resource.Error(getApplication<QuizApp>().getString(
                            R.string.conversion_error
                        )))
                }
            }
        } else {
            _questionAndTermsResponse.postValue(
                Resource.Error(getApplication<QuizApp>().getString(R.string.int_conn_need_new_data))
            )
        }

    }

    private suspend fun updateQuestions() {
        val newQuestions = repository.getQuestions().documents.mapNotNull { it.toObject<Question>() }
        repository.replaceAllQuestions(newQuestions)
    }

    private suspend fun updateTerms() {
        val newTerms = repository.getTerms().documents.mapNotNull { it.toObject<Term>() }
        newTerms.forEach {  term ->
            val array = repository.getImageFromStorage(term.imageName)
            term.image = BitmapFactory.decodeByteArray(array, 0, array.size)
        }
        repository.replaceAllTerms(newTerms)
    }
}