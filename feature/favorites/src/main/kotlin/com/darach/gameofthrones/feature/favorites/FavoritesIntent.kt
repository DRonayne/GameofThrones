package com.darach.gameofthrones.feature.favorites

sealed interface FavoritesIntent {
    data object LoadFavorites : FavoritesIntent
    data object ToggleViewMode : FavoritesIntent
    data object ToggleSelectionMode : FavoritesIntent
    data class ToggleSelection(val characterId: String) : FavoritesIntent
    data object SelectAll : FavoritesIntent
    data object DeselectAll : FavoritesIntent
    data object RemoveSelected : FavoritesIntent
    data class RemoveFavorite(val characterId: String) : FavoritesIntent
}
