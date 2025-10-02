package com.darach.gameofthrones.feature.favorites

import androidx.annotation.VisibleForTesting
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.darach.gameofthrones.core.model.Character
import com.darach.gameofthrones.feature.favorites.FavoritesViewModel
import com.darach.gameofthrones.feature.favorites.components.FavoriteCard
import com.darach.gameofthrones.feature.favorites.components.FavoriteCardCallbacks
import com.darach.gameofthrones.feature.favorites.components.FavoritesTopBarCallbacks

@Suppress("LongParameterList") // Compose screens require multiple callbacks and state parameters
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    @Suppress("UnusedParameter") onBackClick: () -> Unit,
    onBrowseCharactersClick: () -> Unit,
    modifier: Modifier = Modifier,
    onCompareCharacters: (String, String) -> Unit = { _, _ -> },
    sharedTransitionData: com.darach.gameofthrones.core.ui.transition.SharedTransitionData? = null,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.handleIntent(FavoritesIntent.ClearSnackbar)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            FavoritesTopBar(
                state = state,
                callbacks = FavoritesTopBarCallbacks(
                    onCompareClick = {
                        when (state.selectedIds.size) {
                            0, 1 -> viewModel.handleIntent(FavoritesIntent.CompareSelected)
                            2 -> {
                                val selectedList = state.selectedIds.toList()
                                onCompareCharacters(selectedList[0], selectedList[1])
                                viewModel.handleIntent(FavoritesIntent.ExitSelectionMode)
                            }
                            else -> viewModel.handleIntent(FavoritesIntent.CompareSelected)
                        }
                    },
                    onDeleteClick = { viewModel.handleIntent(FavoritesIntent.RemoveSelected) },
                    onCancelClick = { viewModel.handleIntent(FavoritesIntent.ExitSelectionMode) },
                    onSelectAllClick = { viewModel.handleIntent(FavoritesIntent.SelectAll) },
                    onDeselectAllClick = { viewModel.handleIntent(FavoritesIntent.DeselectAll) }
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        FavoritesContent(
            state = state,
            onCardClick = { id ->
                viewModel.handleIntent(FavoritesIntent.OnCardClick(id))
            },
            onBrowseCharactersClick = onBrowseCharactersClick,
            modifier = Modifier.padding(paddingValues),
            sharedTransitionData = sharedTransitionData
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoritesTopBar(
    state: FavoritesState,
    callbacks: FavoritesTopBarCallbacks,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = if (state.selectedIds.isEmpty()) {
                    "Favorites (${state.favorites.size})"
                } else {
                    "${state.selectedIds.size} selected"
                }
            )
        },
        navigationIcon = {
            if (state.isSelectionMode) {
                IconButton(onClick = callbacks.onCancelClick) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel selection"
                    )
                }
            }
        },
        actions = {
            if (state.selectedIds.isNotEmpty()) {
                TopBarActions(state = state, callbacks = callbacks)
            }
        }
    )
}

@Composable
private fun TopBarActions(state: FavoritesState, callbacks: FavoritesTopBarCallbacks) {
    Row {
        IconButton(
            onClick = callbacks.onCompareClick,
            modifier = Modifier.semantics {
                contentDescription = if (state.selectedIds.size == 2) {
                    "Compare ${state.selectedIds.size} selected characters"
                } else {
                    "Select 2 characters to compare, ${state.selectedIds.size} selected"
                }
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                contentDescription = null,
                tint = if (state.selectedIds.size == 2) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                }
            )
        }
        IconButton(
            onClick = callbacks.onDeleteClick,
            modifier = Modifier.semantics {
                contentDescription = "Remove ${state.selectedIds.size} selected favorites"
            }
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null
            )
        }
        SelectionOverflowMenu(state = state, callbacks = callbacks)
    }
}

@Composable
private fun SelectionOverflowMenu(state: FavoritesState, callbacks: FavoritesTopBarCallbacks) {
    var showMenu by remember { mutableStateOf(false) }
    val allSelected = state.selectedIds.size == state.favorites.size && state.favorites.isNotEmpty()

    Box {
        IconButton(onClick = { showMenu = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options"
            )
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            if (allSelected) {
                DropdownMenuItem(
                    text = { Text("Deselect All") },
                    onClick = {
                        callbacks.onDeselectAllClick()
                        showMenu = false
                    }
                )
            } else {
                DropdownMenuItem(
                    text = { Text("Select All") },
                    onClick = {
                        callbacks.onSelectAllClick()
                        showMenu = false
                    }
                )
            }
        }
    }
}

@VisibleForTesting
@Composable
internal fun FavoritesContent(
    state: FavoritesState,
    onCardClick: (String) -> Unit,
    onBrowseCharactersClick: () -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionData: com.darach.gameofthrones.core.ui.transition.SharedTransitionData? = null
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .semantics {
                            contentDescription = "Loading favorites"
                        }
                )
            }
            state.isEmpty -> {
                EmptyFavoritesState(
                    onBrowseCharactersClick = onBrowseCharactersClick,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            state.error != null -> {
                ErrorState(
                    message = state.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                FavoritesGrid(
                    favorites = state.favorites,
                    selectedIds = state.selectedIds,
                    onCardClick = onCardClick,
                    sharedTransitionData = sharedTransitionData
                )
            }
        }
    }
}

@Composable
private fun FavoritesGrid(
    favorites: List<Character>,
    selectedIds: Set<String>,
    onCardClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionData: com.darach.gameofthrones.core.ui.transition.SharedTransitionData? = null
) {
    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val columns = calculateGridColumns(windowAdaptiveInfo)

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier
            .fillMaxSize()
            .semantics {
                contentDescription = "${favorites.size} favorites"
            },
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = favorites,
            key = { it.id }
        ) { character ->
            FavoriteCard(
                character = character,
                isSelected = selectedIds.contains(character.id),
                callbacks = FavoriteCardCallbacks(
                    onToggleSelection = { onCardClick(character.id) }
                ),
                sharedTransitionData = sharedTransitionData
            )
        }
    }
}

/**
 * Calculates the number of grid columns based on window adaptive info.
 *
 * Breakpoints:
 * - Compact (< 600dp, phone portrait): 3 columns
 * - Medium (600-840dp, phone landscape/foldable unfolded/small tablet): 4 columns
 * - Expanded (>= 840dp, tablet): 6 columns
 *
 * These values ensure minimum touch target size (48dp) is maintained
 * while maximizing screen space utilization.
 */
@VisibleForTesting
internal fun calculateGridColumns(windowAdaptiveInfo: WindowAdaptiveInfo): Int {
    val windowSizeClass = windowAdaptiveInfo.windowSizeClass
    return when {
        !windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
        ) -> 3 // Compact (< 600dp)
        !windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
        ) -> 4 // Medium (600-840dp)
        else -> 6 // Expanded (>= 840dp)
    }
}

@Composable
private fun rememberHeartPulseAnimation(): Pair<Float, Float> {
    val infiniteTransition = rememberInfiniteTransition(label = "heart_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    return Pair(scale, alpha)
}

@Composable
private fun EmptyFavoritesState(
    onBrowseCharactersClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (scale, alpha) = rememberHeartPulseAnimation()

    Column(
        modifier = modifier
            .padding(32.dp)
            .semantics {
                contentDescription =
                    "No favorites yet. Discover and save your favorite Game of Thrones characters."
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier
                .size(96.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .alpha(alpha),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No Favorites Yet",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Discover and save your favorite Game of Thrones characters",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        FilledTonalButton(onClick = onBrowseCharactersClick) {
            Icon(
                imageVector = Icons.Outlined.Explore,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text("Browse Characters")
        }
    }
}

@Composable
private fun ErrorState(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(32.dp)
            .semantics {
                contentDescription = "Error loading favorites: $message"
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
    }
}

// Previews
@androidx.compose.ui.tooling.preview.Preview(
    name = "Favorites Content - Empty",
    showBackground = true
)
@Composable
private fun FavoritesContentEmptyPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        FavoritesContent(
            state = FavoritesState(isEmpty = true),
            onCardClick = {},
            onBrowseCharactersClick = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Favorites Content - Loading",
    showBackground = true
)
@Composable
private fun FavoritesContentLoadingPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        FavoritesContent(
            state = FavoritesState(isLoading = true),
            onCardClick = {},
            onBrowseCharactersClick = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Favorites Content - Error",
    showBackground = true
)
@Composable
private fun FavoritesContentErrorPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        FavoritesContent(
            state = FavoritesState(error = "Failed to load favorites"),
            onCardClick = {},
            onBrowseCharactersClick = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Favorites Grid - Compact (Phone Portrait)",
    showBackground = true,
    widthDp = 360,
    heightDp = 640
)
@Composable
private fun FavoritesGridCompactPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        FavoritesContent(
            state = FavoritesState(
                favorites = generateSampleCharacters(),
                isLoading = false
            ),
            onCardClick = {},
            onBrowseCharactersClick = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Favorites Grid - Medium (Phone Landscape/Foldable)",
    showBackground = true,
    widthDp = 640,
    heightDp = 360
)
@Composable
private fun FavoritesGridMediumPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        FavoritesContent(
            state = FavoritesState(
                favorites = generateSampleCharacters(),
                isLoading = false
            ),
            onCardClick = {},
            onBrowseCharactersClick = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Favorites Grid - Expanded (Tablet)",
    showBackground = true,
    widthDp = 1024,
    heightDp = 768
)
@Composable
private fun FavoritesGridExpandedPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        FavoritesContent(
            state = FavoritesState(
                favorites = generateSampleCharacters(),
                isLoading = false
            ),
            onCardClick = {},
            onBrowseCharactersClick = {}
        )
    }
}

private fun generateSampleCharacters(): List<Character> = List(12) { index ->
    Character(
        id = "character_$index",
        name = "Character ${index + 1}",
        gender = if (index % 2 == 0) "Male" else "Female",
        culture = "Sample Culture",
        born = "Born ${index + 100} AC",
        died = if (index % 2 == 0) "Died ${index + 150} AC" else "",
        titles = listOf("Sample Title"),
        aliases = emptyList(),
        father = "",
        mother = "",
        spouse = "",
        allegiances = emptyList(),
        books = emptyList(),
        povBooks = emptyList(),
        tvSeries = listOf("Season 1", "Season 2"),
        tvSeriesSeasons = listOf(1, 2),
        playedBy = listOf("Actor $index")
    )
}
