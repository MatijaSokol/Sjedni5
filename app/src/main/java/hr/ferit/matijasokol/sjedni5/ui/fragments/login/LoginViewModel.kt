package hr.ferit.matijasokol.sjedni5.ui.fragments.login

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObject
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.app.QuizApp
import hr.ferit.matijasokol.sjedni5.models.Admin
import hr.ferit.matijasokol.sjedni5.models.Resource
import hr.ferit.matijasokol.sjedni5.repositories.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class LoginViewModel @ViewModelInject constructor(
    app: Application,
    private val repository: QuizRepository
) : AndroidViewModel(app) {

    private val _uploadStatus = MutableLiveData<Resource<Boolean>>()

    val uploadStatus: LiveData<Resource<Boolean>>
        get() = _uploadStatus

    fun checkForAdmin(admin: Admin) = viewModelScope.launch(Dispatchers.IO) {
        _uploadStatus.postValue(Resource.Loading())
        try {
            val querySnapshot = repository.getAdmins()
            val admins = querySnapshot.documents.mapNotNull { it.toObject<Admin>() }
            if (admins.contains(admin)) {
                _uploadStatus.postValue(Resource.Success(true))
            } else {
                _uploadStatus.postValue(Resource.Success(false))
            }
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