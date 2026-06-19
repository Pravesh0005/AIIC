package com.aiic.app.di

import android.content.Context
import com.aiic.app.core.analytics.AnalyticsTracker
import com.aiic.app.core.analytics.NoOpAnalyticsTracker
import com.aiic.app.core.base.DefaultDispatcherProvider
import com.aiic.app.core.base.DispatcherProvider
import com.aiic.app.core.network.ConnectivityNetworkMonitor
import com.aiic.app.core.network.NetworkMonitor
import com.aiic.app.data.local.PreferencesManager
import com.aiic.app.data.repository.AuthRepositoryImpl
import com.aiic.app.data.repository.UserPreferencesRepositoryImpl
import com.aiic.app.domain.repository.AuthRepository
import com.aiic.app.domain.repository.UserPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager =
        PreferencesManager(context)

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        preferencesManager: PreferencesManager
    ): UserPreferencesRepository = UserPreferencesRepositoryImpl(preferencesManager)

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository = AuthRepositoryImpl()

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()

    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor =
        ConnectivityNetworkMonitor(context)

    @Provides
    @Singleton
    fun provideAnalyticsTracker(): AnalyticsTracker = NoOpAnalyticsTracker()
}
