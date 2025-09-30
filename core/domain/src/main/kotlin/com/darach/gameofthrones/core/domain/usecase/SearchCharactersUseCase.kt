package com.darach.gameofthrones.core.domain.usecase

import com.darach.gameofthrones.core.domain.model.Character
import com.darach.gameofthrones.core.domain.repository.CharacterRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class SearchCharactersUseCase @Inject constructor(private val repository: CharacterRepository) {
    public operator fun invoke(query: String): Flow<List<Character>> =
        repository.searchCharacters(query)
}
