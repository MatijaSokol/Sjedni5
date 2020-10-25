package hr.ferit.matijasokol.sjedni5.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import hr.ferit.matijasokol.sjedni5.data.db.QuizDatabase
import hr.ferit.matijasokol.sjedni5.data.firebase.FirebaseStorageSource
import hr.ferit.matijasokol.sjedni5.data.firebase.FirestoreSource
import hr.ferit.matijasokol.sjedni5.other.Constants.ADMINS_COLLECTION
import hr.ferit.matijasokol.sjedni5.other.Constants.DB_NAME
import hr.ferit.matijasokol.sjedni5.other.Constants.INSTRUCTIONS_KEY
import hr.ferit.matijasokol.sjedni5.other.Constants.PLAYERS_COLLECTION
import hr.ferit.matijasokol.sjedni5.other.Constants.QUESTION_COLLECTION
import hr.ferit.matijasokol.sjedni5.other.Constants.SHARED_PREFS_NAME
import hr.ferit.matijasokol.sjedni5.other.Constants.TERMS_COLLECTION
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideQuestionDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, QuizDatabase::class.java, DB_NAME
    ).build()

    @Singleton
    @Provides
    fun provideQuestionDao(quizDatabase: QuizDatabase) = quizDatabase.getQuestionDao()

    @Singleton
    @Provides
    fun provideTermDao(quizDatabase: QuizDatabase) = quizDatabase.getTermDao()

    @Singleton
    @Provides
    @Named(QUESTION_COLLECTION)
    fun provideQuestionsCollectionReference() = Firebase.firestore.collection(QUESTION_COLLECTION)

    @Singleton
    @Provides
    @Named(PLAYERS_COLLECTION)
    fun providePlayersCollectionReference() = Firebase.firestore.collection(PLAYERS_COLLECTION)

    @Singleton
    @Provides
    @Named(TERMS_COLLECTION)
    fun provideTermsCollectionReference() = Firebase.firestore.collection(TERMS_COLLECTION)

    @Singleton
    @Provides
    @Named(ADMINS_COLLECTION)
    fun provideAdminCollectionReference() = Firebase.firestore.collection(ADMINS_COLLECTION)

    @Singleton
    @Provides
    fun provideStorageReference() = Firebase.storage.reference

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context): SharedPreferences =
        app.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideInstructionsEnabled(sharedPref: SharedPreferences) = sharedPref.getBoolean(INSTRUCTIONS_KEY, true)

    @Singleton
    @Provides
    fun provideFirestoreSource(
        @Named(QUESTION_COLLECTION) questionsCollectionReference: CollectionReference,
        @Named(PLAYERS_COLLECTION) playersCollectionReference: CollectionReference,
        @Named(TERMS_COLLECTION) termsCollectionReference: CollectionReference,
        @Named(ADMINS_COLLECTION) adminsCollectionReference: CollectionReference
    ) = FirestoreSource(
        questionsCollectionReference,
        playersCollectionReference,
        termsCollectionReference,
        adminsCollectionReference
    )

    @Singleton
    @Provides
    fun provideFirebaseStorageSource(storageReference: StorageReference) = FirebaseStorageSource(storageReference)
}