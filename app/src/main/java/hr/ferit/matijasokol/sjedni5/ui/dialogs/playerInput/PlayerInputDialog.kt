package hr.ferit.matijasokol.sjedni5.ui.dialogs.playerInput

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.models.Categories
import hr.ferit.matijasokol.sjedni5.models.Player
import hr.ferit.matijasokol.sjedni5.models.Resource
import hr.ferit.matijasokol.sjedni5.other.displayMessage
import hr.ferit.matijasokol.sjedni5.other.invisible
import hr.ferit.matijasokol.sjedni5.other.visible
import kotlinx.android.synthetic.main.dialog_player_input.*

@AndroidEntryPoint
class PlayerInputDialog : DialogFragment() {

    private val viewModel: PlayersViewModel by viewModels()

    private var score = 0f
    private lateinit var category: Categories
    private lateinit var onPlayerAdded: () -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_player_input, container, false)
        isCancelable = false
        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        setObservers()
    }

    fun setScore(score: Float) {
        this.score = score
    }

    fun setCategory(category: Categories) {
        this.category = category
    }

    fun setOnPlayerAddedListener(listener: () -> Unit) {
        this.onPlayerAdded = listener
    }

    private fun setObservers() {
        viewModel.uploadStatus.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Loading -> {
                    progressInput.visible()
                    btnSave.invisible()
                }
                is Resource.Success -> {
                    dismiss()
                    onPlayerAdded()
                }
                is Resource.Error -> {
                    dismiss()
                    response.message?.let {
                        requireContext().displayMessage(it)
                    }
                }
            }
        })
    }

    private fun setListeners() {
        btnSave.setOnClickListener { addPlayer() }
    }

    private fun addPlayer() {
        val name = etName.text.toString().trim()
        if (name.isNotBlank()) {
            Player(name, score, category.type).also {
                viewModel.uploadPlayer(it)
            }
        } else {
            requireContext().displayMessage(getString(R.string.enter_name))
        }
    }

    companion object {
        fun newInstance() =
            PlayerInputDialog()
    }
}