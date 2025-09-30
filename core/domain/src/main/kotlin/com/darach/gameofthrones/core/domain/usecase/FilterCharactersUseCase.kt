package com.darach.gameofthrones.core.domain.usecase

import com.darach.gameofthrones.core.domain.model.Character
import javax.inject.Inject

data class CharacterFilter(
    val culture: String? = null,
    val isDead: Boolean? = null,
    val hasAppearances: Boolean? = null,
    val gender: String? = null,
    val seasons: List<Int> = emptyList(),
    val onlyFavorites: Boolean = false
) {
    fun isActive(): Boolean = culture != null ||
        isDead != null ||
        hasAppearances != null ||
        gender != null ||
        seasons.isNotEmpty() ||
        onlyFavorites

    fun activeFilterCount(): Int = listOfNotNull(
        culture,
        isDead,
        hasAppearances,
        gender,
        if (seasons.isNotEmpty()) seasons else null,
        if (onlyFavorites) true else null
    ).size
}

class FilterCharactersUseCase @Inject constructor() {
    public operator fun invoke(
        characters: List<Character>,
        filter: CharacterFilter
    ): List<Character> = characters.filter { character ->
        matchesAllFilters(character, filter)
    }

    private fun matchesAllFilters(character: Character, filter: CharacterFilter): Boolean =
        matchesCulture(character, filter) &&
            matchesDeath(character, filter) &&
            matchesAppearances(character, filter) &&
            matchesGender(character, filter) &&
            matchesSeasons(character, filter) &&
            matchesFavorites(character, filter)

    private fun matchesCulture(character: Character, filter: CharacterFilter): Boolean =
        filter.culture == null || character.culture.equals(filter.culture, ignoreCase = true)

    private fun matchesDeath(character: Character, filter: CharacterFilter): Boolean =
        filter.isDead == null || character.isDead == filter.isDead

    private fun matchesAppearances(character: Character, filter: CharacterFilter): Boolean =
        filter.hasAppearances == null ||
            (filter.hasAppearances && character.tvSeries.isNotEmpty()) ||
            (!filter.hasAppearances && character.tvSeries.isEmpty())

    private fun matchesGender(character: Character, filter: CharacterFilter): Boolean =
        filter.gender == null || character.gender.equals(filter.gender, ignoreCase = true)

    private fun matchesSeasons(character: Character, filter: CharacterFilter): Boolean =
        filter.seasons.isEmpty() ||
            filter.seasons.any { season -> character.tvSeriesSeasons.contains(season) }

    private fun matchesFavorites(character: Character, filter: CharacterFilter): Boolean =
        !filter.onlyFavorites || character.isFavorite
}
