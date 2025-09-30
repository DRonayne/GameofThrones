package com.darach.gameofthrones.core.database.di

import android.content.Context
import androidx.room.Room
import com.darach.gameofthrones.core.database.GoTDatabase
import com.darach.gameofthrones.core.database.dao.CharacterDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing database dependencies.
 * Creates and configures the Room database instance and DAOs.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides the Room database instance.
     * Configured with fallback to destructive migration for development.
     */
    @Provides
    @Singleton
    fun provideGoTDatabase(@ApplicationContext context: Context): GoTDatabase =
        Room.databaseBuilder(
            context,
            GoTDatabase::class.java,
            GoTDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration(dropAllTables = false)
            .build()

    /**
     * Provides the CharacterDao from the database.
     */
    @Provides
    @Singleton
    fun provideCharacterDao(database: GoTDatabase): CharacterDao = database.characterDao()
}
