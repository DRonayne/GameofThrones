package com.darach.gameofthrones.core.domain.usecase

import com.darach.gameofthrones.core.domain.repository.CharacterRepository
import com.darach.gameofthrones.core.model.Character
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetCharactersUseCase @Inject constructor(private val repository: CharacterRepository) {
    public operator fun invoke(forceRefresh: Boolean = false): Flow<Result<List<Character>>> =
        repository.observeCharacters(forceRefresh)
}
