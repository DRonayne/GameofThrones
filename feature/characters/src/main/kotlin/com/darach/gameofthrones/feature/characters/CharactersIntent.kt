package com.darach.gameofthrones.feature.characters

import com.darach.gameofthrones.core.domain.usecase.CharacterFilter
import com.darach.gameofthrones.core.domain.usecase.SortOption

sealed interface CharactersIntent {
    data object LoadCharacters : CharactersIntent
    data object RefreshCharacters : CharactersIntent
    data class SearchCharacters(val query: String) : CharactersIntent
    data object ClearSearch : CharactersIntent
    data class FilterCharacters(val filter: CharacterFilter) : CharactersIntent
    data class SortCharacters(val sortOption: SortOption) : CharactersIntent
    data class ToggleFavorite(val characterId: String) : CharactersIntent
    data object RetryLoad : CharactersIntent
    data class RemoveSearchHistoryItem(val query: String) : CharactersIntent
    data object ClearSearchHistory : CharactersIntent
}
