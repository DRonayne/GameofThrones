package com.darach.gameofthrones.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.darach.gameofthrones.core.database.model.CharacterEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Character entities.
 * Provides reactive database operations using Flow for real-time updates.
 */
@Dao
interface CharacterDao {

    /**
     * Observes all characters from the database.
     * Emits a new list whenever the data changes.
     */
    @Query("SELECT * FROM characters ORDER BY name ASC")
    fun observeAllCharacters(): Flow<List<CharacterEntity>>

    /**
     * Observes a single character by ID.
     * Emits null if the character doesn't exist.
     */
    @Query("SELECT * FROM characters WHERE id = :characterId")
    fun observeCharacter(characterId: String): Flow<CharacterEntity?>

    /**
     * Observes all favorite characters.
     * Emits a new list whenever favorites change.
     */
    @Query("SELECT * FROM characters WHERE isFavorite = 1 ORDER BY name ASC")
    fun observeFavoriteCharacters(): Flow<List<CharacterEntity>>

    /**
     * Searches characters by name, culture, or aliases.
     * Uses case-insensitive matching with LIKE operator.
     */
    @Query(
        """
        SELECT * FROM characters
        WHERE name LIKE '%' || :query || '%'
        OR culture LIKE '%' || :query || '%'
        OR aliases LIKE '%' || :query || '%'
        ORDER BY name ASC
        """
    )
    fun searchCharacters(query: String): Flow<List<CharacterEntity>>

    /**
     * Inserts multiple characters into the database.
     * Replaces characters if they already exist.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<CharacterEntity>)

    /**
     * Updates an existing character.
     */
    @Update
    suspend fun updateCharacter(character: CharacterEntity)

    /**
     * Deletes all characters from the database.
     */
    @Query("DELETE FROM characters")
    suspend fun deleteAllCharacters()

    /**
     * Updates the favorite status of a character.
     */
    @Query("UPDATE characters SET isFavorite = :isFavorite WHERE id = :characterId")
    suspend fun updateFavorite(characterId: String, isFavorite: Boolean)

    /**
     * Gets the count of characters in the database.
     */
    @Query("SELECT COUNT(*) FROM characters")
    suspend fun getCharacterCount(): Int

    /**
     * Clears the cache and inserts new characters atomically.
     * This is useful for refreshing the entire dataset.
     */
    @Transaction
    suspend fun refreshCharacters(characters: List<CharacterEntity>) {
        deleteAllCharacters()
        insertCharacters(characters)
    }
}
