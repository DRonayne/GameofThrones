package com.darach.gameofthrones.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.darach.gameofthrones.core.database.dao.CharacterDao
import com.darach.gameofthrones.core.database.model.CharacterEntity
import com.darach.gameofthrones.core.database.util.Converters

/**
 * The Room database for the Game of Thrones app.
 * Contains all the data for offline-first functionality.
 */
@Database(
    entities = [CharacterEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class GoTDatabase : RoomDatabase() {

    /**
     * Provides access to character data operations.
     */
    abstract fun characterDao(): CharacterDao

    companion object {
        const val DATABASE_NAME = "got_database"
    }
}
