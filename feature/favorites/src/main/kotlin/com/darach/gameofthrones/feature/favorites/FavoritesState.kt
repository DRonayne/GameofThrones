package com.darach.gameofthrones.feature.favorites

import com.darach.gameofthrones.core.model.Character

enum class ViewMode {
    GRID,
    LIST
}

data class FavoritesState(
    val favorites: List<Character> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEmpty: Boolean = false,
    val viewMode: ViewMode = ViewMode.GRID,
    val isSelectionMode: Boolean = false,
    val selectedIds: Set<String> = emptySet()
)
