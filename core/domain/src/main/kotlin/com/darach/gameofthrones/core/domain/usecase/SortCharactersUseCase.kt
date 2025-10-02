package com.darach.gameofthrones.core.domain.usecase

import com.darach.gameofthrones.core.model.Character
import javax.inject.Inject

enum class SortOption {
    NAME_ASC,
    NAME_DESC,
    CULTURE_ASC,
    CULTURE_DESC,
    DEATH_DATE_ASC,
    DEATH_DATE_DESC,
    SEASONS_COUNT_ASC,
    SEASONS_COUNT_DESC,
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
        SortOption.DEATH_DATE_ASC -> characters.sortedWith(
            compareBy(
                { it.died.isEmpty() },
                { extractYearFromDate(it.died) }
            )
        )
        SortOption.DEATH_DATE_DESC -> characters.sortedWith(
            compareByDescending<Character> { it.died.isNotEmpty() }
                .thenByDescending { extractYearFromDate(it.died) }
        )
        SortOption.SEASONS_COUNT_ASC -> characters.sortedBy { it.tvSeriesSeasons.size }
        SortOption.SEASONS_COUNT_DESC -> characters.sortedByDescending { it.tvSeriesSeasons.size }
        SortOption.FAVORITE_FIRST -> characters.sortedByDescending { it.isFavorite }
    }

    private fun extractYearFromDate(date: String): Int {
        // Extract numeric value from strings like "299 AC", "300AC", or "8000 BC"
        val numericPart = date.trim().takeWhile { it.isDigit() }
        return numericPart.toIntOrNull() ?: 0
    }
}
