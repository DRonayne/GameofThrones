package com.darach.gameofthrones.feature.characterdetail

sealed interface CharacterDetailIntent {
    data class LoadCharacter(val characterId: String) : CharacterDetailIntent
    data object ToggleFavorite : CharacterDetailIntent
    data object RetryLoad : CharacterDetailIntent
}
