package hr.ferit.matijasokol.sjedni5.repositories

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.StorageReference
import hr.ferit.matijasokol.sjedni5.db.QuestionDao
import hr.ferit.matijasokol.sjedni5.db.TermDao
import hr.ferit.matijasokol.sjedni5.models.Categories
import hr.ferit.matijasokol.sjedni5.models.Player
import hr.ferit.matijasokol.sjedni5.models.Question
import hr.ferit.matijasokol.sjedni5.models.Term
import hr.ferit.matijasokol.sjedni5.other.Constants.ADMINS_COLLECTION
import hr.ferit.matijasokol.sjedni5.other.Constants.FIREBASE_STORAGE_IMAGE_QUALITY
import hr.ferit.matijasokol.sjedni5.other.Constants.IMAGES
import hr.ferit.matijasokol.sjedni5.other.Constants.IMAGE_NAME_FIELD
import hr.ferit.matijasokol.sjedni5.other.Constants.ONE_MEGABYTE
import hr.ferit.matijasokol.sjedni5.other.Constants.PLAYERS_COLLECTION
import hr.ferit.matijasokol.sjedni5.other.Constants.QUESTION_COLLECTION
import hr.ferit.matijasokol.sjedni5.other.Constants.TERMS_COLLECTION
import hr.ferit.matijasokol.sjedni5.other.getThumbnail
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Named

class QuizRepository @Inject constructor(
    private val questionDao: QuestionDao,
    private val termDao: TermDao,
    @Named(QUESTION_COLLECTION) private val questionsCollectionReference: CollectionReference,
    @Named(PLAYERS_COLLECTION) private val playersCollectionReference: CollectionReference,
    @Named(TERMS_COLLECTION) private val termsCollectionReference: CollectionReference,
    @Named(ADMINS_COLLECTION) private val adminsCollectionReference: CollectionReference,
    private val storageReference: StorageReference
) {

    suspend fun getQuestions(): QuerySnapshot = questionsCollectionReference.get().await()

    suspend fun getTerms(): QuerySnapshot = termsCollectionReference.get().await()

    suspend fun getAdmins(): QuerySnapshot = adminsCollectionReference.get().await()

    suspend fun uploadPlayer(player: Player): DocumentReference = playersCollectionReference.add(player).await()

    suspend fun uploadQuestion(question: Question): DocumentReference = questionsCollectionReference.add(question).await()

    suspend fun uploadTerm(term: Term, extension: String, uri: Uri, contentResolver: ContentResolver) {
        val bitmap = getThumbnail(uri, contentResolver)
        bitmap?.let {  image ->
            val outputStream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, FIREBASE_STORAGE_IMAGE_QUALITY, outputStream)
            val data = outputStream.toByteArray()
            val id = termsCollectionReference.add(term).await().id
            termsCollectionReference.document(id).update(IMAGE_NAME_FIELD, "$id.$extension")
            storageReference.child("$IMAGES$id.$extension").putBytes(data).await()
        }
    }

    suspend fun getImageFromStorage(nameWithExtension: String): ByteArray =
        storageReference.child("$IMAGES$nameWithExtension").getBytes(ONE_MEGABYTE).await()

    suspend fun deleteQuestion(documentSnapshot: DocumentSnapshot) = documentSnapshot.reference.delete().await()

    suspend fun deleteTerm(documentSnapshot: DocumentSnapshot) = documentSnapshot.reference.delete().await()

    suspend fun deleteImage(nameWithExtension: String) = storageReference.child("$IMAGES$nameWithExtension").delete().await()

    suspend fun insertQuestions(questions: List<Question>) = questions.forEach { questionDao.insertQuestion(it) }

    suspend fun getQuestionsFromDb(category: Categories) = questionDao.getAllQuestions(category.type)

    suspend fun deleteAllQuestions() = questionDao.deleteAllQuestions()

    suspend fun insertTerms(terms: List<Term>) = terms.forEach { termDao.insertTerm(it) }

    suspend fun getTermsFromDb() = termDao.getAllTerms()

    suspend fun deleteAllTerms() = termDao.deleteAllTerms()
}