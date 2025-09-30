package com.darach.gameofthrones

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.darach.gameofthrones.feature.characterdetail.CharacterDetailScreen
import com.darach.gameofthrones.feature.characters.CharactersScreen
import com.darach.gameofthrones.feature.favorites.FavoritesScreen
import com.darach.gameofthrones.navigation.CharacterDetailRoute
import com.darach.gameofthrones.navigation.CharactersRoute
import com.darach.gameofthrones.navigation.FavoritesRoute
import com.darach.gameofthrones.navigation.SettingsRoute

@Composable
fun GoTNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = CharactersRoute,
        modifier = modifier
    ) {
        composable<CharactersRoute> {
            CharactersScreen(
                onCharacterClick = { characterId ->
                    navController.navigate(CharacterDetailRoute(characterId))
                }
            )
        }

        composable<FavoritesRoute> {
            FavoritesScreen(
                onCharacterClick = { characterId ->
                    navController.navigate(CharacterDetailRoute(characterId))
                },
                onBackClick = { navController.navigateUp() }
            )
        }

        composable<SettingsRoute> {
            Text("Settings Screen - Coming Soon!")
        }

        composable<CharacterDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<CharacterDetailRoute>()
            CharacterDetailScreen(
                characterId = route.characterId,
                onBackClick = { navController.navigateUp() }
            )
        }
    }
}
