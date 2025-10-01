package com.darach.gameofthrones.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import com.darach.gameofthrones.GoTNavHost

@Composable
fun GoTApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val useNavigationRail = adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(
        WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val topLevelDestinations = TopLevelDestination.entries

    val showBottomNav = currentDestination?.let { destination ->
        topLevelDestinations.any { destination.hasRoute(it.toRoute()::class) }
    } ?: true

    if (useNavigationRail && showBottomNav) {
        Row(modifier = modifier.fillMaxSize()) {
            GoTNavigationRail(
                destinations = topLevelDestinations,
                onNavigateToDestination = { destination ->
                    navController.navigateToTopLevelDestination(destination)
                },
                currentDestination = currentDestination
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                GoTNavHost(
                    navController = navController,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    } else {
        Scaffold(
            modifier = modifier,
            bottomBar = {
                if (showBottomNav) {
                    GoTBottomNavigationBar(
                        destinations = topLevelDestinations,
                        onNavigateToDestination = { destination ->
                            navController.navigateToTopLevelDestination(destination)
                        },
                        currentDestination = currentDestination
                    )
                }
            }
        ) { paddingValues ->
            GoTNavHost(
                navController = navController,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    }
}

@Composable
private fun GoTBottomNavigationBar(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: androidx.navigation.NavDestination?,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        destinations.forEach { destination ->
            val selected = currentDestination?.hasRoute(destination.toRoute()::class) == true
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        imageVector = if (selected) {
                            destination.selectedIcon
                        } else {
                            destination.unselectedIcon
                        },
                        contentDescription = destination.contentDescription
                    )
                },
                label = { Text(destination.label) }
            )
        }
    }
}

@Composable
private fun GoTNavigationRail(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: androidx.navigation.NavDestination?,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        destinations.forEach { destination ->
            val selected = currentDestination?.hasRoute(destination.toRoute()::class) == true
            NavigationRailItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        imageVector = if (selected) {
                            destination.selectedIcon
                        } else {
                            destination.unselectedIcon
                        },
                        contentDescription = destination.contentDescription
                    )
                },
                label = { Text(destination.label) }
            )
        }
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
    TopLevelDestination.COMPARISON -> ComparisonRoute
    TopLevelDestination.SETTINGS -> SettingsRoute
}
