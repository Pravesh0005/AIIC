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

    // Infrastructure
    @Provides @Singleton
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()

    @Provides @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor =
        ConnectivityNetworkMonitor(context)

    @Provides @Singleton
    fun provideAnalyticsTracker(): AnalyticsTracker = NoOpAnalyticsTracker()
}
