package hr.ferit.matijasokol.sjedni5.ui.fragments.createDeleteQuestions

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentSnapshot
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.app.QuizApp
import hr.ferit.matijasokol.sjedni5.models.Resource
import hr.ferit.matijasokol.sjedni5.repositories.QuizRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class CreateDeleteQuestionsViewModel @ViewModelInject constructor(
    app: Application,
    private val repository: QuizRepository
) : AndroidViewModel(app) {

    private val _questionDeleteResponse = MutableLiveData<Resource<DocumentSnapshot>>()

    val questionDeleteResponse: LiveData<Resource<DocumentSnapshot>>
        get() = _questionDeleteResponse

    private val _termDeleteResponse = MutableLiveData<Resource<DocumentSnapshot>>()

    val termDeleteResponse: LiveData<Resource<DocumentSnapshot>>
        get() = _termDeleteResponse

    private val _playersDeleteResponse = MutableLiveData<Resource<String>>()

    val playersDeleteResponse: LiveData<Resource<String>>
        get() = _playersDeleteResponse

    fun deleteQuestion(documentSnapshot: DocumentSnapshot) = viewModelScope.launch(IO) {
        try {
            repository.deleteQuestion(documentSnapshot)
            _questionDeleteResponse.postValue(Resource.Success(documentSnapshot))
        } catch (t: Throwable) {
            _questionDeleteResponse.postValue(
                Resource.Error(getApplication<QuizApp>().getString(
                    R.string.network_failure
                )))
        }
    }

    fun undoDeleteQuestion(documentSnapshot: DocumentSnapshot) = viewModelScope.launch(IO) {
        try {
            repository.undoDeleteQuestion(documentSnapshot)
        } catch (t: Throwable) {
            /* NO-OP*/
        }
    }

    fun deleteTermFromFirestore(documentSnapshot: DocumentSnapshot) = viewModelScope.launch(IO) {
        try {
            repository.deleteTermFromFirestore(documentSnapshot)
            _termDeleteResponse.postValue(Resource.Success(documentSnapshot))
        } catch (t: Throwable) {
            _termDeleteResponse.postValue(
                Resource.Error(getApplication<QuizApp>().getString(
                    R.string.network_failure
                )))
        }
    }

    fun deleteTermFromStorage(imageName: String) = viewModelScope.launch(IO) {
        try {
            repository.deleteTermFromStorage(imageName)
        } catch (t: Throwable) {
            /* NO-OP*/
        }
    }

    fun undoDeleteTerm(documentSnapshot: DocumentSnapshot) = viewModelScope.launch(IO) {
        try {
            repository.undoDeleteTerm(documentSnapshot)
        } catch (t: Throwable) {
            /* NO-OP*/
        }
    }

    fun checkPlayersCollection() = viewModelScope.launch(IO) {
        _playersDeleteResponse.postValue(Resource.Loading())
        try {
            val players = repository.getPlayers()
            if (players.isEmpty()) {
                _playersDeleteResponse.postValue(Resource.Success(getApplication<QuizApp>().getString(R.string.players_collection_empty)))
            } else {
                deleteAllPlayers(players)
            }
        } catch (t: Throwable) {
            _playersDeleteResponse.postValue(Resource.Error(getApplication<QuizApp>().getString(
                R.string.network_failure
            )))
        }
    }

    private fun deleteAllPlayers(documents: List<DocumentSnapshot>) = viewModelScope.launch(IO) {
        try {
            repository.deleteAllPlayers(documents)
            _playersDeleteResponse.postValue(Resource.Success(getApplication<QuizApp>().getString(R.string.players_deleted)))
        } catch (t: Throwable) {
            _playersDeleteResponse.postValue(Resource.Error(getApplication<QuizApp>().getString(
                R.string.network_failure
            )))
        }
    }
}