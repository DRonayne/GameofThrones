package com.darach.gameofthrones.feature.comparison

import com.darach.gameofthrones.core.model.Character

/**
 * State model for the comparison feature.
 * Manages comparison view state for two characters.
 */
data class ComparisonState(
    val selectedCharacters: List<Character> = emptyList(),
    val comparisonResult: ComparisonResult? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
