package hr.ferit.matijasokol.sjedni5.ui.fragments.guessTerm

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
import hr.ferit.matijasokol.sjedni5.models.Resource
import hr.ferit.matijasokol.sjedni5.models.Term
import hr.ferit.matijasokol.sjedni5.other.hasInternetConnection
import hr.ferit.matijasokol.sjedni5.repositories.QuizRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.IOException

class GuessTermViewModel @ViewModelInject constructor(
    app: Application,
    private val repository: QuizRepository
) : AndroidViewModel(app) {

    private val _terms = MutableLiveData<Resource<List<Term>>>()

    val terms: LiveData<Resource<List<Term>>>
        get() = _terms

    var currentTermIndex = -1
        private set

    private val _currentTerm = MutableLiveData<Int>()

    val currentTerm: LiveData<Int>
        get() = _currentTerm

    private var availableMistakes = 4

    private val _mistakes = MutableLiveData<Int>()

    val mistakes: LiveData<Int>
        get() = _mistakes

    var score = 0f
        private set

    fun incrementScore() {
        score++
    }

    fun decrementMistakes() {
        availableMistakes--
        _mistakes.postValue(availableMistakes)
    }

    fun incrementTerm() {
        currentTermIndex++
        _currentTerm.postValue(currentTermIndex)
    }

    fun getTerms() = viewModelScope.launch(IO) {
        try {
            val termsFromDb = repository.getTermsFromDb()
            if (termsFromDb.isNotEmpty()) {
                _terms.postValue(Resource.Success(termsFromDb))
            } else {
                if (hasInternetConnection(getApplication())) {
                    _terms.postValue(Resource.Loading())
                    val querySnapshot = repository.getTerms()
                    val terms = querySnapshot.documents.mapNotNull { it.toObject<Term>() }
                    terms.forEach {  term ->
                        val array = repository.getImageFromStorage(term.imageName)
                        term.image = BitmapFactory.decodeByteArray(array, 0, array.size)
                    }
                    repository.insertTerms(terms)
                    val newTerms = repository.getTermsFromDb()
                    _terms.postValue(Resource.Success(newTerms))
                } else {
                    _terms.postValue(Resource.Error(getApplication<QuizApp>().getString(R.string.data_empty_int_conn_need_new_data)))
                }
            }
        } catch (t: Throwable) {
            when(t) {
                is IOException -> _terms.postValue(
                    Resource.Error(getApplication<QuizApp>().getString(
                        R.string.network_failure
                    )))
                else -> _terms.postValue(
                    Resource.Error(getApplication<QuizApp>().getString(
                        R.string.conversion_error
                    )))
            }
        }
    }
}