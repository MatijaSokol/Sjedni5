package hr.ferit.matijasokol.sjedni5.ui.fragments.login

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.models.Admin
import hr.ferit.matijasokol.sjedni5.models.Resource
import hr.ferit.matijasokol.sjedni5.other.*
import hr.ferit.matijasokol.sjedni5.ui.activities.QuizActivity
import hr.ferit.matijasokol.sjedni5.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_login.*

@AndroidEntryPoint
class LoginFragment : BaseFragment(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (requireActivity() as QuizActivity).viewModel.setTitle(getString(R.string.login))
    }

    override fun setUpUi() {
        setListeners()
        setObservers()
    }

    private fun setObservers() {
        viewModel.uploadStatus.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Loading -> {
                    progress.visible()
                }
                is Resource.Success -> {
                    progress.gone()
                    response.data?.let {
                        if (it) {
                            navigate(LoginFragmentDirections.actionLoginFragmentToCreateDeleteQuestionsFragment())
                        } else {
                            rootElement.showSnackbar(getString(R.string.username_password_incorrect), Snackbar.LENGTH_LONG)
                        }
                    }
                }
                is Resource.Error -> {
                    progress.gone()
                    response.message?.let {
                        rootElement.showSnackbar(it)
                    }
                }
            }
        })
    }

    private fun setListeners() {
        btnLogin.setOnClickListener {
            if (hasInternetConnection(requireContext())) {
                checkForAdmin()
            } else {
                rootElement.showSnackbar(getString(R.string.int_conn_need_login), Snackbar.LENGTH_LONG)
            }
        }
    }

    private fun checkForAdmin() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (username.isBlank() || password.isBlank()) {
            requireContext().displayMessage(getString(R.string.inputs_empty))
            return
        }

        val admin = Admin(username, password)

        viewModel.checkForAdmin(admin)
    }
}