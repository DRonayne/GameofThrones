package com.darach.gameofthrones.core.data.di

import com.darach.gameofthrones.core.data.preferences.PreferencesDataSource
import com.darach.gameofthrones.core.data.preferences.PreferencesDataSourceImpl
import com.darach.gameofthrones.core.data.repository.CharacterRepositoryImpl
import com.darach.gameofthrones.core.domain.repository.CharacterRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindPreferencesDataSource(impl: PreferencesDataSourceImpl): PreferencesDataSource

    @Binds
    @Singleton
    abstract fun bindCharacterRepository(impl: CharacterRepositoryImpl): CharacterRepository
}
