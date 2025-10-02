package com.darach.gameofthrones.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.darach.gameofthrones.R
import kotlinx.serialization.Serializable

@Serializable
object CharactersRoute

@Serializable
object FavoritesRoute

@Serializable
data class ComparisonRoute(val characterId1: String, val characterId2: String)

@Serializable
object SettingsRoute

enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val labelRes: Int,
    @StringRes val contentDescriptionRes: Int
) {
    CHARACTERS(
        selectedIcon = Icons.Filled.GridView,
        unselectedIcon = Icons.Outlined.GridView,
        labelRes = R.string.nav_characters,
        contentDescriptionRes = R.string.nav_characters_description
    ),
    FAVORITES(
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder,
        labelRes = R.string.nav_favorites,
        contentDescriptionRes = R.string.nav_favorites_description
    ),
    SETTINGS(
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        labelRes = R.string.nav_settings,
        contentDescriptionRes = R.string.nav_settings_description
    )
}
