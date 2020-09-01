package hr.ferit.matijasokol.sjedni5.ui.dialogs.questionInput

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.app.QuizApp
import hr.ferit.matijasokol.sjedni5.models.Question
import hr.ferit.matijasokol.sjedni5.models.Resource
import hr.ferit.matijasokol.sjedni5.repositories.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class QuestionInputViewModel @ViewModelInject constructor(
    app: Application,
    private val repository: QuizRepository
) : AndroidViewModel(app) {

    private val _uploadStatus = MutableLiveData<Resource<Boolean>>()

    val uploadStatus: LiveData<Resource<Boolean>>
        get() = _uploadStatus

    fun uploadQuestion(question: Question) = viewModelScope.launch(Dispatchers.IO) {
        _uploadStatus.postValue(Resource.Loading())
        try {
            repository.uploadQuestion(question)
            _uploadStatus.postValue(Resource.Success(true))
        } catch (t: Throwable) {
            when(t) {
                is IOException -> _uploadStatus.postValue(
                    Resource.Error(getApplication<QuizApp>().getString(
                    R.string.network_failure
                )))
                else -> _uploadStatus.postValue(
                    Resource.Error(getApplication<QuizApp>().getString(
                    R.string.conversion_error
                )))
            }
        }
    }
}