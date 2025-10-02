package com.darach.gameofthrones.feature.favorites

sealed interface FavoritesIntent {
    data object LoadFavorites : FavoritesIntent
    data class ToggleSelection(val characterId: String) : FavoritesIntent
    data object RemoveSelected : FavoritesIntent
    data object CompareSelected : FavoritesIntent
    data object ClearSnackbar : FavoritesIntent
    data object ClearSelection : FavoritesIntent
    data class EnterSelectionMode(val initialCharacterId: String) : FavoritesIntent
    data object ExitSelectionMode : FavoritesIntent
    data class OnCardClick(val characterId: String) : FavoritesIntent
    data object SelectAll : FavoritesIntent
    data object DeselectAll : FavoritesIntent
}
