package hr.ferit.matijasokol.sjedni5.ui.fragments.jokers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import hr.ferit.matijasokol.sjedni5.R
import hr.ferit.matijasokol.sjedni5.other.gone
import hr.ferit.matijasokol.sjedni5.other.visible
import kotlinx.android.synthetic.main.bottom_sheet_joker.*

class JokerBottomSheet : BottomSheetDialogFragment() {

    private lateinit var onFirstJokerClicked: () -> Unit
    private lateinit var onSecondJokerClicked: () -> Unit
    private lateinit var onThirdJokerClicked: () -> Unit

    private var firstJokerUsed = false
    private var secondJokerUsed = false
    private var thirdJokerUsed = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_joker, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClickListeners()
        enableOrDisableJokers()
    }

    private fun setClickListeners() {
        iv1.setOnClickListener {
            onFirstJokerClicked()
            dismiss()
        }
        iv2.setOnClickListener {
            onSecondJokerClicked()
            dismiss()
        }
        iv3.setOnClickListener {
            onThirdJokerClicked()
            dismiss()
        }
    }

    fun setJokerUsage(firstJokerUsed: Boolean, secondJokerUsed: Boolean, thirdJokerUsed: Boolean) {
        this.firstJokerUsed = firstJokerUsed
        this.secondJokerUsed = secondJokerUsed
        this.thirdJokerUsed = thirdJokerUsed
    }

    private fun enableOrDisableJokers() {
        if (firstJokerUsed) {
            iv1JokerUsed.visible()
            iv1.isEnabled = false
        } else {
            iv1JokerUsed.gone()
            iv1.isEnabled = true
        }

        if (secondJokerUsed) {
            iv2JokerUsed.visible()
            iv2.isEnabled = false
        } else {
            iv2JokerUsed.gone()
            iv2.isEnabled = true
        }

        if (thirdJokerUsed) {
            iv3JokerUsed.visible()
            iv3.isEnabled = false
        } else {
            iv3JokerUsed.gone()
            iv3.isEnabled = true
        }
    }

    fun setListeners(
        onFirstJokerClicked: () -> Unit,
        onSecondJokerClicked: () -> Unit,
        onThirdJokerClicked: () -> Unit
    ) {
        this.onFirstJokerClicked = onFirstJokerClicked
        this.onSecondJokerClicked = onSecondJokerClicked
        this.onThirdJokerClicked = onThirdJokerClicked
    }

    companion object {
        fun newInstance() = JokerBottomSheet()
    }

}