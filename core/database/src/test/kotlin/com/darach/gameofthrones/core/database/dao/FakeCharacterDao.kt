package com.darach.gameofthrones.core.database.dao

import com.darach.gameofthrones.core.database.model.CharacterEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeCharacterDao : CharacterDao {
    private val characters = MutableStateFlow<List<CharacterEntity>>(emptyList())

    override fun observeAllCharacters(): Flow<List<CharacterEntity>> =
        characters.map { it.sortedBy { char -> char.name } }

    override fun observeCharacter(characterId: String): Flow<CharacterEntity?> =
        characters.map { list -> list.find { it.id == characterId } }

    override fun observeFavoriteCharacters(): Flow<List<CharacterEntity>> = characters.map { list ->
        list.filter { it.isFavorite }.sortedBy { it.name }
    }

    override fun searchCharacters(query: String): Flow<List<CharacterEntity>> =
        characters.map { list ->
            list.filter {
                it.name.contains(query, ignoreCase = true) ||
                    it.culture.contains(query, ignoreCase = true) ||
                    it.aliases.any { alias -> alias.contains(query, ignoreCase = true) }
            }.sortedBy { it.name }
        }

    override suspend fun insertCharacters(characters: List<CharacterEntity>) {
        val existing = this.characters.value.toMutableList()
        characters.forEach { newChar ->
            val index = existing.indexOfFirst { it.id == newChar.id }
            if (index >= 0) {
                existing[index] = newChar
            } else {
                existing.add(newChar)
            }
        }
        this.characters.value = existing
    }

    override suspend fun updateCharacter(character: CharacterEntity) {
        val existing = characters.value.toMutableList()
        val index = existing.indexOfFirst { it.id == character.id }
        if (index >= 0) {
            existing[index] = character
            characters.value = existing
        }
    }

    override suspend fun deleteAllCharacters() {
        characters.value = emptyList()
    }

    override suspend fun updateFavorite(characterId: String, isFavorite: Boolean) {
        val existing = characters.value.toMutableList()
        val index = existing.indexOfFirst { it.id == characterId }
        if (index >= 0) {
            existing[index] = existing[index].copy(isFavorite = isFavorite)
            characters.value = existing
        }
    }

    override suspend fun getCharacterCount(): Int = characters.value.size

    override suspend fun getFavoriteCharacterIds(): List<String> =
        characters.value.filter { it.isFavorite }.map { it.id }

    suspend fun getFavoritesCount(): Int = characters.value.count { it.isFavorite }
}
