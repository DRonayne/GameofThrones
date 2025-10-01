package com.darach.gameofthrones.feature.comparison

import com.darach.gameofthrones.core.model.Character

/**
 * Callbacks for the comparison selection screen.
 */
data class ComparisonSelectionCallbacks(
    val onCharacterToggle: (Character) -> Unit,
    val onCompareClick: () -> Unit,
    val onClearSelection: () -> Unit,
    val onBackClick: () -> Unit
)
