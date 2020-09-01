package hr.ferit.matijasokol.sjedni5.other

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import hr.ferit.matijasokol.sjedni5.other.Constants.THUMBNAIL_SIZE
import kotlin.math.floor
import kotlin.random.Random


fun getRandomNumberExcept(from: Int, until: Int, vararg except: Int): Int {
    var number = Random.nextInt(from, until)
    while (except.contains(number)) {
        number = Random.nextInt(from, until)
    }
    return number
}

fun hasInternetConnection(context: Context): Boolean {
    val connectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.activeNetworkInfo?.run {
            return when(type) {
                ConnectivityManager.TYPE_WIFI -> true
                ConnectivityManager.TYPE_MOBILE -> true
                ConnectivityManager.TYPE_ETHERNET -> true
                else -> false
            }
        }
    }
    return false
}

fun getThumbnail(uri: Uri, contentResolver: ContentResolver): Bitmap? {
    var inputStream = contentResolver.openInputStream(uri)
    val onlyBoundsOptions = BitmapFactory.Options()
    onlyBoundsOptions.inJustDecodeBounds = true
    BitmapFactory.decodeStream(inputStream, null, onlyBoundsOptions)
    inputStream?.close()
    if (onlyBoundsOptions.outWidth == -1 || onlyBoundsOptions.outHeight == -1) {
        return null
    }
    val originalSize = if (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) {
        onlyBoundsOptions.outHeight
    } else {
        onlyBoundsOptions.outWidth
    }
    val ratio: Double = if (originalSize > THUMBNAIL_SIZE) {
        (originalSize / THUMBNAIL_SIZE).toDouble()
    } else {
        1.0
    }
    val bitmapOptions = BitmapFactory.Options()
    bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio)
    inputStream = contentResolver.openInputStream(uri)
    val bitmap = BitmapFactory.decodeStream(inputStream, null, bitmapOptions)
    inputStream?.close()
    return bitmap
}

private fun getPowerOfTwoForSampleRatio(ratio: Double): Int {
    val k = Integer.highestOneBit(floor(ratio).toInt())
    return if (k == 0) 1 else k
}