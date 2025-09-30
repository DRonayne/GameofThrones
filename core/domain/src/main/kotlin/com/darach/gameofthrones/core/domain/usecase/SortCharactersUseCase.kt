package com.darach.gameofthrones.core.domain.usecase

import com.darach.gameofthrones.core.domain.model.Character
import javax.inject.Inject

enum class SortOption {
    NAME_ASC,
    NAME_DESC,
    CULTURE_ASC,
    CULTURE_DESC,
    FAVORITE_FIRST
}

class SortCharactersUseCase @Inject constructor() {
    public operator fun invoke(
        characters: List<Character>,
        sortOption: SortOption
    ): List<Character> = when (sortOption) {
        SortOption.NAME_ASC -> characters.sortedBy { it.name.lowercase() }
        SortOption.NAME_DESC -> characters.sortedByDescending { it.name.lowercase() }
        SortOption.CULTURE_ASC -> characters.sortedBy { it.culture.lowercase() }
        SortOption.CULTURE_DESC -> characters.sortedByDescending { it.culture.lowercase() }
        SortOption.FAVORITE_FIRST -> characters.sortedByDescending { it.isFavorite }
    }
}
