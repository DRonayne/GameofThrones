package com.darach.gameofthrones.core.domain.usecase

import com.darach.gameofthrones.core.domain.repository.CharacterRepository
import javax.inject.Inject

class RefreshCharactersUseCase @Inject constructor(private val repository: CharacterRepository) {
    public suspend operator fun invoke(): Result<Unit> = repository.refreshCharacters()
}
