package com.darach.gameofthrones.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.darach.gameofthrones.GoTNavHost

@Composable
fun GoTApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val topLevelDestinations = TopLevelDestination.entries

    // NavigationSuiteScaffold automatically adapts the navigation UI based on window size:
    // - Compact screens (phones): Bottom navigation bar
    // - Medium screens (unfolded foldables, small tablets): Navigation rail
    // - Expanded screens (large tablets, desktops): Navigation drawer
    NavigationSuiteScaffold(
        modifier = modifier,
        navigationSuiteItems = {
            topLevelDestinations.forEach { destination ->
                val selected = currentDestination?.hasRoute(destination.toRoute()::class) == true

                item(
                    selected = selected,
                    onClick = { navController.navigateToTopLevelDestination(destination) },
                    icon = {
                        Icon(
                            imageVector = if (selected) {
                                destination.selectedIcon
                            } else {
                                destination.unselectedIcon
                            },
                            contentDescription = stringResource(destination.contentDescriptionRes)
                        )
                    },
                    label = { Text(stringResource(destination.labelRes)) }
                )
            }
        }
    ) {
        GoTNavHost(
            navController = navController,
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun NavHostController.navigateToTopLevelDestination(destination: TopLevelDestination) {
    navigate(destination.toRoute()) {
        popUpTo(graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

private fun TopLevelDestination.toRoute(): Any = when (this) {
    TopLevelDestination.CHARACTERS -> CharactersRoute
    TopLevelDestination.FAVORITES -> FavoritesRoute
    TopLevelDestination.SETTINGS -> SettingsRoute
}
