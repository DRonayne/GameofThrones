package com.darach.gameofthrones.feature.favorites.components

data class FavoritesTopBarCallbacks(
    val onCompareClick: () -> Unit,
    val onDeleteClick: () -> Unit,
    val onCancelClick: () -> Unit,
    val onSelectAllClick: () -> Unit,
    val onDeselectAllClick: () -> Unit
)
