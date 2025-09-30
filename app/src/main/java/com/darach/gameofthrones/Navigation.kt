package com.darach.gameofthrones

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.darach.gameofthrones.feature.characterdetail.CharacterDetailScreen
import com.darach.gameofthrones.feature.characters.CharactersScreen
import kotlinx.serialization.Serializable

@Serializable
object CharactersRoute

@Serializable
data class CharacterDetailRoute(val characterId: String)

@Composable
fun GoTNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: CharactersRoute = CharactersRoute
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<CharactersRoute> {
            CharactersScreen(
                onCharacterClick = { characterId ->
                    navController.navigate(CharacterDetailRoute(characterId))
                }
            )
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
