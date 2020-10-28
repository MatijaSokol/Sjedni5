package hr.ferit.matijasokol.sjedni5.repositories

import android.content.ContentResolver
import android.net.Uri
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import hr.ferit.matijasokol.sjedni5.data.db.QuestionDao
import hr.ferit.matijasokol.sjedni5.data.db.TermDao
import hr.ferit.matijasokol.sjedni5.data.firebase.FirebaseStorageService
import hr.ferit.matijasokol.sjedni5.data.firebase.FirestoreService
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
    private val firestoreService: FirestoreService,
    private val firebaseStorageService: FirebaseStorageService
) {

    suspend fun getQuestions(): QuerySnapshot = firestoreService.getQuestions()

    suspend fun getTerms(): QuerySnapshot = firestoreService.getTerms()

    suspend fun getAdmins(): QuerySnapshot = firestoreService.getAdmins()

    suspend fun uploadPlayer(player: Player): DocumentReference = firestoreService.uploadPlayer(player)

    suspend fun getPlayers() = firestoreService.getPlayers()

    suspend fun deleteAllPlayers(documents: List<DocumentSnapshot>) = firestoreService.deleteAllPlayers(documents)

    suspend fun uploadQuestion(question: Question): DocumentReference = firestoreService.uploadQuestion(question)

    suspend fun deleteQuestion(documentSnapshot: DocumentSnapshot) = firestoreService.deleteQuestion(documentSnapshot)

    suspend fun undoDeleteQuestion(documentSnapshot: DocumentSnapshot) = firestoreService.undoDeleteQuestion(documentSnapshot)

    suspend fun deleteTermFromFirestore(documentSnapshot: DocumentSnapshot) = firestoreService.deleteTerm(documentSnapshot)

    suspend fun deleteTermFromStorage(imageName: String) = firebaseStorageService.deleteImage(imageName)

    suspend fun undoDeleteTerm(documentSnapshot: DocumentSnapshot) = firestoreService.undoDeleteTerm(documentSnapshot)

    suspend fun uploadTerm(term: Term, uri: Uri, extension: String, contentResolver: ContentResolver) = firestoreService.uploadTerm(term, extension).also {
        firebaseStorageService.uploadTerm(it, extension, uri, contentResolver)
    }

    suspend fun getImageFromStorage(nameWithExtension: String) = firebaseStorageService.getImageFromStorage(nameWithExtension)

    suspend fun insertQuestions(questions: List<Question>) = questionDao.insertAllQuestions(questions)

    suspend fun getQuestionsFromDb(category: Categories) = questionDao.getAllQuestions(category.type)

    suspend fun replaceAllQuestions(questions: List<Question>) = questionDao.replaceAllQuestions(questions)

    suspend fun insertTerms(terms: List<Term>) = termDao.insertAllTerms(terms)

    suspend fun getTermsFromDb() = termDao.getAllTerms()

    suspend fun replaceAllTerms(terms: List<Term>) = termDao.replaceAllTerms(terms)
}