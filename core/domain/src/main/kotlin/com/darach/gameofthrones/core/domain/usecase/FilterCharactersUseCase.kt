package com.darach.gameofthrones.core.domain.usecase

import com.darach.gameofthrones.core.domain.model.Character
import javax.inject.Inject

data class CharacterFilter(
    val culture: String? = null,
    val isDead: Boolean? = null,
    val hasAppearances: Boolean? = null,
    val gender: String? = null,
    val onlyFavorites: Boolean = false
)

class FilterCharactersUseCase @Inject constructor() {
    public operator fun invoke(
        characters: List<Character>,
        filter: CharacterFilter
    ): List<Character> = characters.filter { character ->
        val matchesCulture = filter.culture == null ||
            character.culture.equals(filter.culture, ignoreCase = true)

        val matchesDeath = filter.isDead == null ||
            character.isDead == filter.isDead

        val matchesAppearances = filter.hasAppearances == null ||
            (filter.hasAppearances && character.tvSeries.isNotEmpty()) ||
            (!filter.hasAppearances && character.tvSeries.isEmpty())

        val matchesGender = filter.gender == null ||
            character.gender.equals(filter.gender, ignoreCase = true)

        val matchesFavorites = !filter.onlyFavorites || character.isFavorite

        matchesCulture && matchesDeath && matchesAppearances &&
            matchesGender && matchesFavorites
    }
}
