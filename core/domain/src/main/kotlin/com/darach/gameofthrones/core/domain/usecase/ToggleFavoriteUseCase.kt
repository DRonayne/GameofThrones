package com.darach.gameofthrones.core.domain.usecase

import com.darach.gameofthrones.core.domain.repository.CharacterRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(private val repository: CharacterRepository) {
    public suspend operator fun invoke(characterId: String, isFavorite: Boolean) {
        repository.updateFavorite(characterId, isFavorite)
    }
}
