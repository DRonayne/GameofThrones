package com.darach.gameofthrones.core.data.repository

import com.darach.gameofthrones.core.data.mapper.toDomain
import com.darach.gameofthrones.core.data.mapper.toEntity
import com.darach.gameofthrones.core.database.dao.CharacterDao
import com.darach.gameofthrones.core.domain.model.Character
import com.darach.gameofthrones.core.domain.repository.CharacterRepository
import com.darach.gameofthrones.core.network.api.GoTApiService
import com.darach.gameofthrones.core.network.util.NetworkResult
import com.darach.gameofthrones.core.network.util.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

@Singleton
class CharacterRepositoryImpl @Inject constructor(
    private val apiService: GoTApiService,
    private val characterDao: CharacterDao
) : CharacterRepository {

    override fun observeCharacters(forceRefresh: Boolean): Flow<Result<List<Character>>> = flow {
        val cachedCount = characterDao.getCharacterCount()
        val shouldFetch = forceRefresh || cachedCount == 0

        if (shouldFetch) {
            when (val networkResult = safeApiCall { fetchCharactersFromApi() }) {
                is NetworkResult.Success -> {
                    val characters = networkResult.data
                    characterDao.refreshCharacters(characters.map { it.toEntity() })
                    emit(Result.success(characters))
                }
                is NetworkResult.Error -> {
                    if (cachedCount > 0) {
                        val cachedCharacters = characterDao.observeAllCharacters()
                            .map { entities -> entities.map { it.toDomain() } }
                        cachedCharacters.collect { emit(Result.success(it)) }
                    } else {
                        emit(Result.failure(Exception(networkResult.message)))
                    }
                }
                is NetworkResult.Loading -> {}
            }
        } else {
            characterDao.observeAllCharacters()
                .map { entities -> entities.map { it.toDomain() } }
                .collect { emit(Result.success(it)) }
        }
    }

    override fun observeCharacter(characterId: String): Flow<Character?> =
        characterDao.observeCharacter(characterId)
            .map { it?.toDomain() }

    override fun observeFavoriteCharacters(): Flow<List<Character>> =
        characterDao.observeFavoriteCharacters()
            .map { entities -> entities.map { it.toDomain() } }

    override fun searchCharacters(query: String): Flow<List<Character>> =
        characterDao.searchCharacters(query)
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun updateFavorite(characterId: String, isFavorite: Boolean) {
        characterDao.updateFavorite(characterId, isFavorite)
    }

    override suspend fun refreshCharacters(): Result<Unit> = when (
        val networkResult = safeApiCall {
            fetchCharactersFromApi()
        }
    ) {
        is NetworkResult.Success -> {
            val characters = networkResult.data
            characterDao.refreshCharacters(characters.map { it.toEntity() })
            Result.success(Unit)
        }
        is NetworkResult.Error -> {
            Result.failure(Exception(networkResult.message))
        }
        is NetworkResult.Loading -> {
            Result.failure(Exception("Loading state should not occur"))
        }
    }

    override suspend fun getCharacterCount(): Int = characterDao.getCharacterCount()

    private suspend fun fetchCharactersFromApi(): List<Character> {
        val dtos = apiService.getCharacters()
        return dtos.map { dto ->
            val characterId = generateCharacterId(dto.name)
            dto.toDomain(characterId)
        }
    }

    private fun generateCharacterId(name: String): String =
        name.hashCode().toString().replace("-", "")
}
