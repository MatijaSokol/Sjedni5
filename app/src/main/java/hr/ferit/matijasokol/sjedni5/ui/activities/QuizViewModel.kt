package hr.ferit.matijasokol.sjedni5.ui.activities

import android.content.SharedPreferences
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.ferit.matijasokol.sjedni5.other.Constants
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class QuizViewModel @ViewModelInject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _title = MutableLiveData<String>()

    val title: LiveData<String>
        get() = _title

    fun setTitle(title: String) {
        _title.postValue(title)
    }

    fun saveInstructionsEnabled(enabled: Boolean) = viewModelScope.launch(IO) {
        sharedPreferences
            .edit()
            .putBoolean(Constants.INSTRUCTIONS_KEY, enabled)
            .apply()
    }
}