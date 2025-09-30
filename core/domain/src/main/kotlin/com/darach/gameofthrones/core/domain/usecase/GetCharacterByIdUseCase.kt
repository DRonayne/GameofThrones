package com.darach.gameofthrones.core.domain.usecase

import com.darach.gameofthrones.core.domain.model.Character
import com.darach.gameofthrones.core.domain.repository.CharacterRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetCharacterByIdUseCase @Inject constructor(private val repository: CharacterRepository) {
    public operator fun invoke(characterId: String): Flow<Character?> =
        repository.observeCharacter(characterId)
}
