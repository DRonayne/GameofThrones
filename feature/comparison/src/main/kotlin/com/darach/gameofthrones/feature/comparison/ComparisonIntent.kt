package com.darach.gameofthrones.feature.comparison

import com.darach.gameofthrones.core.domain.model.Character

/**
 * User intents for the comparison feature.
 * Follows MVI pattern for unidirectional data flow.
 */
sealed interface ComparisonIntent {
    /** Enter selection mode to choose characters for comparison */
    data object EnterSelectionMode : ComparisonIntent

    /** Exit selection mode and clear selections */
    data object ExitSelectionMode : ComparisonIntent

    /** Toggle character selection (add if not selected, remove if selected) */
    data class ToggleCharacterSelection(val character: Character) : ComparisonIntent

    /** Clear all selected characters */
    data object ClearSelection : ComparisonIntent

    /** Start comparison with currently selected characters */
    data object StartComparison : ComparisonIntent

    /** Exit comparison view and return to selection */
    data object ExitComparison : ComparisonIntent

    /** Switch a character in the comparison */
    data class SwitchCharacter(val oldCharacter: Character, val newCharacter: Character) :
        ComparisonIntent
}
