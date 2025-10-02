package com.darach.gameofthrones.core.data.repository

import com.darach.gameofthrones.core.common.performance.PerformanceMonitor
import com.darach.gameofthrones.core.common.performance.trace
import com.darach.gameofthrones.core.data.mapper.toDomain
import com.darach.gameofthrones.core.data.mapper.toEntity
import com.darach.gameofthrones.core.database.dao.CharacterDao
import com.darach.gameofthrones.core.domain.repository.CharacterRepository
import com.darach.gameofthrones.core.model.Character
import com.darach.gameofthrones.core.network.api.GoTApiService
import com.darach.gameofthrones.core.network.util.NetworkResult
import com.darach.gameofthrones.core.network.util.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

@Singleton
class CharacterRepositoryImpl @Inject constructor(
    private val apiService: GoTApiService,
    private val characterDao: CharacterDao,
    private val performanceMonitor: PerformanceMonitor
) : CharacterRepository {

    override fun observeCharacters(forceRefresh: Boolean): Flow<Result<List<Character>>> = flow {
        val cachedCount = characterDao.getCharacterCount()
        val shouldFetch = forceRefresh || cachedCount == 0

        if (shouldFetch) {
            when (val networkResult = safeApiCall { fetchCharactersFromApi() }) {
                is NetworkResult.Success -> {
                    val characters = networkResult.data
                    characterDao.refreshCharacters(characters.map { it.toEntity() })
                    // After fetching and saving, observe the database for reactive updates
                    emitAll(
                        characterDao.observeAllCharacters()
                            .map { entities -> Result.success(entities.map { it.toDomain() }) }
                    )
                }
                is NetworkResult.Error -> {
                    if (cachedCount > 0) {
                        // Fallback to cache if network fails but we have cached data
                        emitAll(
                            characterDao.observeAllCharacters()
                                .map { entities -> Result.success(entities.map { it.toDomain() }) }
                        )
                    } else {
                        emit(Result.failure(Exception(networkResult.message)))
                    }
                }
                is NetworkResult.Loading -> {}
            }
        } else {
            // Observe database - if cache gets cleared (becomes empty), auto-refresh
            var hasRefreshed = false
            emitAll(
                characterDao.observeAllCharacters()
                    .onEach { entities ->
                        // If cache becomes empty and we haven't already refreshed, trigger refresh
                        if (entities.isEmpty() && !hasRefreshed) {
                            hasRefreshed = true
                            refreshCharacters()
                        }
                    }
                    .map { entities -> Result.success(entities.map { it.toDomain() }) }
            )
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
        performanceMonitor.trace(
            traceName = "update_favorite",
            attributes = mapOf("is_favorite" to isFavorite.toString())
        ) {
            characterDao.updateFavorite(characterId, isFavorite)
        }
    }

    override suspend fun refreshCharacters(): Result<Unit> = performanceMonitor.trace(
        traceName = "refresh_characters",
        attributes = mapOf("operation" to "refresh")
    ) {
        when (
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
    }

    override suspend fun getCharacterCount(): Int = characterDao.getCharacterCount()

    override suspend fun clearCache(): Result<Unit> = performanceMonitor.trace(
        traceName = "clear_cache",
        attributes = mapOf("operation" to "clear_cache")
    ) {
        runCatching {
            characterDao.deleteAllCharacters()
        }
    }

    override suspend fun clearAllData(): Result<Unit> = performanceMonitor.trace(
        traceName = "clear_all_data",
        attributes = mapOf("operation" to "clear_all_data")
    ) {
        runCatching {
            characterDao.deleteAllCharacters()
        }
    }

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
