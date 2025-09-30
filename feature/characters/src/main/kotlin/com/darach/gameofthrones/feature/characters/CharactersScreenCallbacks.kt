package com.darach.gameofthrones.feature.characters

/**
 * Callbacks for the characters screen.
 */
data class CharactersScreenCallbacks(
    val onCharacterClick: (String) -> Unit,
    val onIntent: (CharactersIntent) -> Unit,
    val onFilterClick: () -> Unit
)
