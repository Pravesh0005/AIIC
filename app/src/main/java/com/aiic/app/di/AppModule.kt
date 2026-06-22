package com.aiic.app.di

import android.content.Context
import com.aiic.app.core.analytics.AnalyticsTracker
import com.aiic.app.core.analytics.NoOpAnalyticsTracker
import com.aiic.app.core.base.DefaultDispatcherProvider
import com.aiic.app.core.base.DispatcherProvider
import com.aiic.app.core.network.ConnectivityNetworkMonitor
import com.aiic.app.core.network.NetworkMonitor
import com.aiic.app.data.local.PreferencesManager
import com.aiic.app.data.repository.FirebaseAuthRepository
import com.aiic.app.data.repository.FirestoreUserRepository
import com.aiic.app.data.repository.SessionRepositoryImpl
import com.aiic.app.domain.repository.AuthRepository
import com.aiic.app.domain.repository.SessionRepository
import com.aiic.app.domain.repository.UserRepository
import com.aiic.app.domain.repository.ResumeRepository
import com.aiic.app.data.repository.FirestoreResumeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Firebase
    @Provides @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    // Local
    @Provides @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager =
        PreferencesManager(context)

    // Repositories
    @Provides @Singleton
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository =
        FirebaseAuthRepository(auth)

    @Provides @Singleton
    fun provideUserRepository(firestore: FirebaseFirestore): UserRepository =
        FirestoreUserRepository(firestore)

    @Provides @Singleton
    fun provideSessionRepository(prefs: PreferencesManager): SessionRepository =
        SessionRepositoryImpl(prefs)

    @Provides @Singleton
    fun provideResumeRepository(
        firestore: FirebaseFirestore,
        storage: FirebaseStorage
    ): ResumeRepository = FirestoreResumeRepository(firestore, storage)

    @Provides @Singleton
    fun providePdfExtractionRepository(): com.aiic.app.domain.repository.PdfExtractionRepository = 
        com.aiic.app.data.repository.MockPdfExtractionRepository()

    @Provides @Singleton
    fun provideGenerativeAiRepository(): com.aiic.app.domain.repository.GenerativeAiRepository = 
        com.aiic.app.data.repository.GeminiGenerativeAiRepository()

    @Provides @Singleton
    fun provideResumeAnalysisRepository(
        firestore: FirebaseFirestore
    ): com.aiic.app.domain.repository.ResumeAnalysisRepository = 
        com.aiic.app.data.repository.FirestoreResumeAnalysisRepository(firestore)

    @Provides @Singleton
    fun provideInterviewSessionRepository(): com.aiic.app.domain.repository.InterviewSessionRepository =
        com.aiic.app.data.repository.FirestoreInterviewSessionRepository()

    @Provides @Singleton
    fun provideInterviewQuestionRepository(
        generativeAiRepository: com.aiic.app.domain.repository.GenerativeAiRepository
    ): com.aiic.app.domain.repository.InterviewQuestionRepository =
        com.aiic.app.data.repository.FirestoreInterviewQuestionRepository(generativeAiRepository)

    @Provides @Singleton
    fun provideInterviewAnswerRepository(
        generativeAiRepository: com.aiic.app.domain.repository.GenerativeAiRepository
    ): com.aiic.app.domain.repository.InterviewAnswerRepository =
        com.aiic.app.data.repository.FirestoreInterviewAnswerRepository(generativeAiRepository)

    // Infrastructure
    @Provides @Singleton
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()

    @Provides @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor =
        ConnectivityNetworkMonitor(context)

    @Provides @Singleton
    fun provideAnalyticsTracker(): AnalyticsTracker = NoOpAnalyticsTracker()
}
