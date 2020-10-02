package hr.ferit.matijasokol.sjedni5.ui.fragments.createDeleteQuestions

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentSnapshot
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.app.QuizApp
import hr.ferit.matijasokol.sjedni5.models.Resource
import hr.ferit.matijasokol.sjedni5.models.Term
import hr.ferit.matijasokol.sjedni5.repositories.QuizRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class CreateDeleteQuestionsViewModel @ViewModelInject constructor(
    app: Application,
    private val repository: QuizRepository
) : AndroidViewModel(app) {

    private val _questionDeleteResponse = MutableLiveData<Resource<Unit>>()

    val questionDeleteResponse: LiveData<Resource<Unit>>
        get() = _questionDeleteResponse

    private val _termDeleteResponse = MutableLiveData<Resource<Unit>>()

    val termDeleteResponse: LiveData<Resource<Unit>>
        get() = _termDeleteResponse

    fun deleteQuestion(documentSnapshot: DocumentSnapshot) = viewModelScope.launch(IO) {
        try {
            repository.deleteQuestion(documentSnapshot)
            _questionDeleteResponse.postValue(Resource.Success(Unit))
        } catch (t: Throwable) {
            _questionDeleteResponse.postValue(
                Resource.Error(getApplication<QuizApp>().getString(
                    R.string.network_failure
                )))
        }
    }

    fun deleteTerm(term: Term, documentSnapshot: DocumentSnapshot) = viewModelScope.launch(IO) {
        try {
            repository.deleteTerm(term, documentSnapshot)
            _termDeleteResponse.postValue(Resource.Success(Unit))
        } catch (t: Throwable) {
            _termDeleteResponse.postValue(
                Resource.Error(getApplication<QuizApp>().getString(
                    R.string.network_failure
                )))
        }
    }
}