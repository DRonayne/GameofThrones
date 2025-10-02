package com.darach.gameofthrones.feature.favorites

import com.darach.gameofthrones.core.model.Character

data class FavoritesState(
    val favorites: List<Character> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEmpty: Boolean = false,
    val selectedIds: Set<String> = emptySet(),
    val snackbarMessage: String? = null,
    val isSelectionMode: Boolean = false
)
