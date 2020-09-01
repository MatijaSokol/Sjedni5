package hr.ferit.matijasokol.sjedni5.ui.activities

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class QuizViewModel @ViewModelInject constructor() : ViewModel() {

    private val _title = MutableLiveData<String>()

    val title: LiveData<String>
        get() = _title

    fun setTitle(title: String) {
        _title.postValue(title)
    }
}