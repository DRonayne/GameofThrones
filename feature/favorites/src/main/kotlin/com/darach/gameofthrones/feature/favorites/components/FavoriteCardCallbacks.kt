package com.darach.gameofthrones.feature.favorites.components

data class FavoriteCardCallbacks(
    val onCharacterClick: () -> Unit,
    val onToggleSelection: () -> Unit,
    val onRemoveFavorite: () -> Unit
)
