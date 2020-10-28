package hr.ferit.matijasokol.sjedni5.data.firebase

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.storage.StorageReference
import hr.ferit.matijasokol.sjedni5.other.Constants.FIREBASE_STORAGE_IMAGE_QUALITY
import hr.ferit.matijasokol.sjedni5.other.Constants.IMAGES
import hr.ferit.matijasokol.sjedni5.other.Constants.ONE_MEGABYTE
import hr.ferit.matijasokol.sjedni5.other.getThumbnail
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class FirebaseStorageService @Inject constructor(
    private val storageReference: StorageReference
) {

    suspend fun uploadTerm(id: String, extension: String, uri: Uri, contentResolver: ContentResolver) = getThumbnail(uri, contentResolver)?.let {
        val outputStream = ByteArrayOutputStream()
        it.compress(Bitmap.CompressFormat.JPEG, FIREBASE_STORAGE_IMAGE_QUALITY, outputStream)
        val data = outputStream.toByteArray()
        storageReference.child("${IMAGES}$id.$extension").putBytes(data).await()
    }

    suspend fun deleteImage(nameWithExtension: String) = storageReference.child("${IMAGES}$nameWithExtension").delete().await()

    suspend fun getImageFromStorage(nameWithExtension: String): ByteArray =
        storageReference.child("${IMAGES}$nameWithExtension").getBytes(ONE_MEGABYTE).await()
}