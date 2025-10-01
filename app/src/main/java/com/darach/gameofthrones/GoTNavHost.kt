package com.darach.gameofthrones

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.darach.gameofthrones.feature.characterdetail.CharacterDetailScreen
import com.darach.gameofthrones.feature.characters.CharactersScreen
import com.darach.gameofthrones.feature.comparison.ComparisonIntent
import com.darach.gameofthrones.feature.comparison.ComparisonScreen
import com.darach.gameofthrones.feature.comparison.ComparisonSelectionCallbacks
import com.darach.gameofthrones.feature.comparison.ComparisonSelectionScreen
import com.darach.gameofthrones.feature.comparison.ComparisonViewModel
import com.darach.gameofthrones.feature.favorites.FavoritesScreen
import com.darach.gameofthrones.feature.settings.SettingsScreen
import com.darach.gameofthrones.navigation.CharacterDetailRoute
import com.darach.gameofthrones.navigation.CharactersRoute
import com.darach.gameofthrones.navigation.ComparisonRoute
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

        composable<ComparisonRoute> {
            ComparisonRouteContent(
                onNavigateUp = { navController.navigateUp() }
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
            SettingsScreen()
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

@Composable
private fun ComparisonRouteContent(
    onNavigateUp: () -> Unit,
    viewModel: ComparisonViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.comparisonResult != null) {
        ComparisonScreen(
            comparisonResult = state.comparisonResult,
            isLoading = state.isLoading,
            error = state.error,
            onBackClick = {
                viewModel.handleIntent(ComparisonIntent.ExitComparison)
            }
        )
    } else {
        ComparisonSelectionScreen(
            characters = state.favoriteCharacters,
            selectedCharacters = state.selectedCharacters,
            callbacks = ComparisonSelectionCallbacks(
                onCharacterToggle = { character ->
                    viewModel.handleIntent(
                        ComparisonIntent.ToggleCharacterSelection(character)
                    )
                },
                onClearSelection = {
                    viewModel.handleIntent(ComparisonIntent.ClearSelection)
                },
                onCompareClick = {
                    viewModel.handleIntent(ComparisonIntent.StartComparison)
                },
                onBackClick = onNavigateUp
            )
        )
    }
}
