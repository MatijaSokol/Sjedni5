package hr.ferit.matijasokol.sjedni5.other

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import java.util.*

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.showSnackbar(message: String, length: Int = Snackbar.LENGTH_SHORT) = Snackbar.make(this, message, length).show()

fun Context.displayMessage(message: String, length: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, message, length).show()

fun Fragment.navigate(destination: NavDirections) =
    with(this.findNavController()) {
        currentDestination?.getAction(destination.actionId)?.let {
                navigate(destination)
            }
    }

fun ImageView.setImageWithAnimation(context: Context, bitmap: Bitmap) {
    val animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out).apply {
        interpolator = DecelerateInterpolator()
        duration = 500
    }
    val animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in).apply {
        interpolator = AccelerateInterpolator()
        duration = 500
    }

    animOut.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(p0: Animation?) { /* NO-OP */ }

        override fun onAnimationStart(p0: Animation?) { /* NO-OP */ }

        override fun onAnimationEnd(p0: Animation?) {
            this@setImageWithAnimation.setImageBitmap(bitmap)
            animIn.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(p0: Animation?) { /* NO-OP */ }

                override fun onAnimationEnd(p0: Animation?) { /* NO-OP */ }

                override fun onAnimationStart(p0: Animation?) { /* NO-OP */ }
            })

            this@setImageWithAnimation.startAnimation(animIn)
        }
    })

    this.startAnimation(animOut)
}

fun Context.getUriExtension(uri: Uri) = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))

fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    if (index1 < index2) {
        for (i in index1 until index2) {
            Collections.swap(this, i, i + 1)
        }
    } else {
        for (i in index1 downTo index2 + 1) {
            Collections.swap(this, i, i - 1)
        }
    }
}

fun Context.showAlertDialog(
    @StringRes titleResId: Int,
    @StringRes messageResId: Int,
    @StringRes positiveButtonTextResId: Int,
    @StringRes negativeButtonTextResId: Int,
    onPositiveButtonClicked: () -> Unit
) {
    AlertDialog.Builder(this)
        .setTitle(titleResId)
        .setMessage(messageResId)
        .setPositiveButton(positiveButtonTextResId) { _, _ -> onPositiveButtonClicked() }
        .setNegativeButton(negativeButtonTextResId) { dialog, _ -> dialog.dismiss() }
        .show()
}