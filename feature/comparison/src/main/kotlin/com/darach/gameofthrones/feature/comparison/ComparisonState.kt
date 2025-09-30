package com.darach.gameofthrones.feature.comparison

import com.darach.gameofthrones.core.domain.model.Character

/**
 * State model for the comparison feature.
 * Manages selection mode and comparison view state.
 */
data class ComparisonState(
    val favoriteCharacters: List<Character> = emptyList(),
    val selectedCharacters: List<Character> = emptyList(),
    val isSelectionMode: Boolean = false,
    val comparisonResult: ComparisonResult? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val canCompare: Boolean
        get() = selectedCharacters.size == MAX_SELECTION_SIZE

    val selectionCount: Int
        get() = selectedCharacters.size

    val isMaxSelected: Boolean
        get() = selectedCharacters.size >= MAX_SELECTION_SIZE

    companion object {
        const val MAX_SELECTION_SIZE = 2
    }
}
