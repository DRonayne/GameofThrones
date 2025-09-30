package com.darach.gameofthrones.feature.characters

import com.darach.gameofthrones.core.domain.model.Character
import com.darach.gameofthrones.core.domain.usecase.CharacterFilter
import com.darach.gameofthrones.core.domain.usecase.SortOption

data class CharactersState(
    val characters: List<Character> = emptyList(),
    val filteredCharacters: List<Character> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val searchHistory: List<String> = emptyList(),
    val filter: CharacterFilter = CharacterFilter(),
    val sortOption: SortOption = SortOption.NAME_ASC,
    val isEmpty: Boolean = false,
    val availableCultures: List<String> = emptyList(),
    val availableSeasons: List<Int> = emptyList()
)
