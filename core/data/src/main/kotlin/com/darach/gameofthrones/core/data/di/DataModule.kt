package com.darach.gameofthrones.core.data.di

import com.darach.gameofthrones.core.data.preferences.PreferencesDataSource
import com.darach.gameofthrones.core.data.preferences.PreferencesDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing data layer dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    /**
     * Binds the PreferencesDataSource implementation.
     */
    @Binds
    @Singleton
    abstract fun bindPreferencesDataSource(impl: PreferencesDataSourceImpl): PreferencesDataSource
}
