package com.aiic.app.di

import android.content.Context
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
}
