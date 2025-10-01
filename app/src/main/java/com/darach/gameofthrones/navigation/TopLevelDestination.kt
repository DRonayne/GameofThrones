package com.darach.gameofthrones.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
object CharactersRoute

@Serializable
object FavoritesRoute

@Serializable
object ComparisonRoute

@Serializable
object SettingsRoute

enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String,
    val contentDescription: String
) {
    CHARACTERS(
        selectedIcon = Icons.Filled.GridView,
        unselectedIcon = Icons.Outlined.GridView,
        label = "Characters",
        contentDescription = "Browse characters"
    ),
    FAVORITES(
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder,
        label = "Favorites",
        contentDescription = "View favorite characters"
    ),
    COMPARISON(
        selectedIcon = Icons.AutoMirrored.Filled.CompareArrows,
        unselectedIcon = Icons.AutoMirrored.Outlined.CompareArrows,
        label = "Compare",
        contentDescription = "Compare characters"
    ),
    SETTINGS(
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        label = "Settings",
        contentDescription = "App settings"
    )
}
