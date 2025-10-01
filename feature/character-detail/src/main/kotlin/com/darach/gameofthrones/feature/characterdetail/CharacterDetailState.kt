package com.darach.gameofthrones.feature.characterdetail

import com.darach.gameofthrones.core.model.Character

data class CharacterDetailState(
    val character: Character? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
