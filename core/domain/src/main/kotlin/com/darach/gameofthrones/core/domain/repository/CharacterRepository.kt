package com.darach.gameofthrones.core.domain.repository

import com.darach.gameofthrones.core.model.Character
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {

    fun observeCharacters(forceRefresh: Boolean = false): Flow<Result<List<Character>>>

    fun observeCharacter(characterId: String): Flow<Character?>

    fun observeFavoriteCharacters(): Flow<List<Character>>

    fun searchCharacters(query: String): Flow<List<Character>>

    suspend fun updateFavorite(characterId: String, isFavorite: Boolean)

    suspend fun refreshCharacters(): Result<Unit>

    suspend fun getCharacterCount(): Int

    suspend fun clearCache(): Result<Unit>

    suspend fun clearAllData(): Result<Unit>
}
