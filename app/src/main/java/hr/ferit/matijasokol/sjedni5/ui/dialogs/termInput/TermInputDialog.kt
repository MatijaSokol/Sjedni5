package hr.ferit.matijasokol.sjedni5.ui.dialogs.termInput

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import hr.ferit.matijasokol.sjedni5.models.Resource
import hr.ferit.matijasokol.sjedni5.models.Term
import hr.ferit.matijasokol.sjedni5.other.*
import hr.ferit.matijasokol.sjedni5.other.Constants.IMAGE_TYPE
import hr.ferit.matijasokol.sjedni5.other.Constants.REQUEST_CODE_IMAGE_PICK
import kotlinx.android.synthetic.main.dialog_add_term.*

@AndroidEntryPoint
class TermInputDialog : DialogFragment() {

    private val viewModel: TermInputViewModel by viewModels()

    private lateinit var image: Uri

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_add_term, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        setObservers()
    }

    private fun setObservers() {
        viewModel.uploadStatus.observe(viewLifecycleOwner, Observer {  response ->
            when(response) {
                is Resource.Loading -> {
                    progress.visible()
                    btnSave.invisible()
                }
                is Resource.Success -> {
                    dismiss()
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
        ivPhoto.setOnClickListener { openGallery() }
        btnSave.setOnClickListener { uploadTerm() }
    }

    private fun uploadTerm() {
        val name = etText.text.toString().trim()
        if (this::image.isInitialized && name.isNotBlank()) {
            val extension = requireContext().getUriExtension(image) ?: ""
            val term = Term(name)
            viewModel.uploadTerm(term, extension, image, requireContext().contentResolver)
        } else {
            requireContext().displayMessage(getString(R.string.inputs_empty))
        }
    }

    private fun openGallery() {
        Intent(Intent.ACTION_GET_CONTENT).also {
            it.type = IMAGE_TYPE
            startActivityForResult(it, REQUEST_CODE_IMAGE_PICK)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE_PICK) {
            data?.data?.let {
                ivCheckMark.visible()
                image = it
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    companion object {
        fun newInstance() = TermInputDialog()
    }
}