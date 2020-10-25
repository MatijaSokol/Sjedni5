package hr.ferit.matijasokol.sjedni5.repositories

import android.content.ContentResolver
import android.net.Uri
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import hr.ferit.matijasokol.sjedni5.data.db.QuestionDao
import hr.ferit.matijasokol.sjedni5.data.db.TermDao
import hr.ferit.matijasokol.sjedni5.data.firebase.FirebaseStorageSource
import hr.ferit.matijasokol.sjedni5.data.firebase.FirestoreSource
import hr.ferit.matijasokol.sjedni5.models.Categories
import hr.ferit.matijasokol.sjedni5.models.Player
import hr.ferit.matijasokol.sjedni5.models.Question
import hr.ferit.matijasokol.sjedni5.models.Term
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor(
    private val questionDao: QuestionDao,
    private val termDao: TermDao,
    private val firestoreSource: FirestoreSource,
    private val firebaseStorageSource: FirebaseStorageSource
) {

    suspend fun getQuestions(): QuerySnapshot = firestoreSource.getQuestions()

    suspend fun getTerms(): QuerySnapshot = firestoreSource.getTerms()

    suspend fun getAdmins(): QuerySnapshot = firestoreSource.getAdmins()

    suspend fun uploadPlayer(player: Player): DocumentReference = firestoreSource.uploadPlayer(player)

    suspend fun uploadQuestion(question: Question): DocumentReference = firestoreSource.uploadQuestion(question)

    suspend fun deleteQuestion(documentSnapshot: DocumentSnapshot) = firestoreSource.deleteQuestion(documentSnapshot)

    suspend fun undoDeleteQuestion(documentSnapshot: DocumentSnapshot) = firestoreSource.undoDeleteQuestion(documentSnapshot)

    suspend fun deleteTermFromFirestore(documentSnapshot: DocumentSnapshot) = firestoreSource.deleteTerm(documentSnapshot)

    suspend fun deleteTermFromStorage(imageName: String) = firebaseStorageSource.deleteImage(imageName)

    suspend fun undoDeleteTerm(documentSnapshot: DocumentSnapshot) = firestoreSource.undoDeleteTerm(documentSnapshot)

    suspend fun uploadTerm(term: Term, uri: Uri, extension: String, contentResolver: ContentResolver) {
        val id = firestoreSource.uploadTerm(term, extension)
        firebaseStorageSource.uploadTerm(id, extension, uri, contentResolver)
    }

    suspend fun getImageFromStorage(nameWithExtension: String) = firebaseStorageSource.getImageFromStorage(nameWithExtension)

    suspend fun insertQuestions(questions: List<Question>) = questionDao.insertAllQuestions(questions)

    suspend fun getQuestionsFromDb(category: Categories) = questionDao.getAllQuestions(category.type)

    suspend fun replaceAllQuestions(questions: List<Question>) = questionDao.replaceAllQuestions(questions)

    suspend fun insertTerms(terms: List<Term>) = termDao.insertAllTerms(terms)

    suspend fun getTermsFromDb() = termDao.getAllTerms()

    suspend fun replaceAllTerms(terms: List<Term>) = termDao.replaceAllTerms(terms)
}