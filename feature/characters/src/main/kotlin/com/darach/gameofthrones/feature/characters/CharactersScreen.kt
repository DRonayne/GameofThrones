package com.darach.gameofthrones.feature.characters

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darach.gameofthrones.core.ui.performance.TrackScreenLoadTime
import com.darach.gameofthrones.feature.characters.CharactersViewModel
import com.darach.gameofthrones.feature.characters.components.CharacterGridCard
import com.darach.gameofthrones.feature.characters.components.CharactersSearchBar
import com.darach.gameofthrones.feature.characters.components.FilterBottomSheet
import com.darach.gameofthrones.feature.characters.components.FilterBottomSheetState
import com.darach.gameofthrones.feature.characters.components.OfflineIndicator
import com.darach.gameofthrones.feature.characters.components.SearchBarCallbacks
import com.darach.gameofthrones.feature.characters.components.SortChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharactersScreen(
    onCharacterClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CharactersViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    var showFilterSheet by remember { mutableStateOf(false) }

    // Track screen load time - when characters are loaded, the screen is ready
    TrackScreenLoadTime(
        screenName = "characters",
        performanceMonitor = viewModel.performanceMonitor,
        key = if (!state.isLoading && state.characters.isNotEmpty()) state.characters else null
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            SearchAndFilterControls(
                state = state,
                onIntent = viewModel::handleIntent,
                onOpenFilterSheet = { showFilterSheet = true }
            )
        }
    ) { paddingValues ->
        CharactersContent(
            contentState = CharactersContentState(
                state = state,
                isOnline = isOnline,
                onCharacterClick = onCharacterClick,
                onIntent = viewModel::handleIntent
            ),
            paddingValues = paddingValues
        )

        if (showFilterSheet) {
            FilterBottomSheet(
                state = FilterBottomSheetState(
                    currentFilter = state.filter,
                    availableCultures = state.availableCultures,
                    availableSeasons = state.availableSeasons
                ),
                onFilterChange = { viewModel.handleIntent(CharactersIntent.FilterCharacters(it)) },
                onDismiss = { showFilterSheet = false }
            )
        }
    }
}

data class CharactersContentState(
    val state: CharactersState,
    val isOnline: Boolean,
    val onCharacterClick: (String) -> Unit,
    val onIntent: (CharactersIntent) -> Unit
)

@Composable
private fun CharactersContent(contentState: CharactersContentState, paddingValues: PaddingValues) {
    Box(modifier = Modifier.padding(paddingValues)) {
        CharactersBody(
            state = contentState.state,
            onCharacterClick = contentState.onCharacterClick,
            onIntent = contentState.onIntent
        )

        OfflineIndicator(
            isOffline = !contentState.isOnline,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp)
        )
    }
}

@Composable
private fun SearchAndFilterControls(
    state: CharactersState,
    onIntent: (CharactersIntent) -> Unit,
    onOpenFilterSheet: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Search bar row
        CharactersSearchBar(
            query = state.searchQuery,
            searchHistory = state.searchHistory,
            callbacks = SearchBarCallbacks(
                onQueryChange = { onIntent(CharactersIntent.SearchCharacters(it)) },
                onSearch = { onIntent(CharactersIntent.SearchCharacters(it)) },
                onClearSearch = { onIntent(CharactersIntent.ClearSearch) }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        // Filter and sort chips row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActiveFilterChips(filter = state.filter, onFilterChange = onIntent)
            AllFiltersChip(onOpenFilterSheet = onOpenFilterSheet)
            SortChip(
                currentSortOption = state.sortOption,
                onSortOptionChange = { onIntent(CharactersIntent.SortCharacters(it)) }
            )
        }
    }
}

@Composable
private fun ActiveFilterChips(
    filter: com.darach.gameofthrones.core.domain.usecase.CharacterFilter,
    onFilterChange: (CharactersIntent) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    FavoritesFilterChip(filter, haptic, onFilterChange)
    StatusFilterChip(filter, haptic, onFilterChange)
    AppearancesFilterChip(filter, haptic, onFilterChange)
    GenderFilterChip(filter, haptic, onFilterChange)
    CultureFilterChip(filter, haptic, onFilterChange)
    SeasonsFilterChips(filter, haptic, onFilterChange)
}

@Composable
private fun FavoritesFilterChip(
    filter: com.darach.gameofthrones.core.domain.usecase.CharacterFilter,
    haptic: androidx.compose.ui.hapticfeedback.HapticFeedback,
    onFilterChange: (CharactersIntent) -> Unit
) {
    AnimatedVisibility(
        visible = filter.onlyFavorites,
        enter = expandHorizontally() + fadeIn(),
        exit = shrinkHorizontally() + fadeOut()
    ) {
        CompactFilterChip(
            label = "Favorites",
            icon = Icons.Default.Favorite,
            onDismiss = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onFilterChange(
                    CharactersIntent.FilterCharacters(filter.copy(onlyFavorites = false))
                )
            }
        )
    }
}

@Composable
private fun StatusFilterChip(
    filter: com.darach.gameofthrones.core.domain.usecase.CharacterFilter,
    haptic: androidx.compose.ui.hapticfeedback.HapticFeedback,
    onFilterChange: (CharactersIntent) -> Unit
) {
    filter.isDead?.let { isDead ->
        AnimatedVisibility(
            visible = true,
            enter = expandHorizontally() + fadeIn(),
            exit = shrinkHorizontally() + fadeOut()
        ) {
            CompactFilterChip(
                label = if (isDead) "Deceased" else "Alive",
                onDismiss = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onFilterChange(CharactersIntent.FilterCharacters(filter.copy(isDead = null)))
                }
            )
        }
    }
}

@Composable
private fun AppearancesFilterChip(
    filter: com.darach.gameofthrones.core.domain.usecase.CharacterFilter,
    haptic: androidx.compose.ui.hapticfeedback.HapticFeedback,
    onFilterChange: (CharactersIntent) -> Unit
) {
    AnimatedVisibility(
        visible = filter.hasAppearances == true,
        enter = expandHorizontally() + fadeIn(),
        exit = shrinkHorizontally() + fadeOut()
    ) {
        CompactFilterChip(
            label = "TV Appearances",
            onDismiss = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onFilterChange(
                    CharactersIntent.FilterCharacters(filter.copy(hasAppearances = null))
                )
            }
        )
    }
}

@Composable
private fun GenderFilterChip(
    filter: com.darach.gameofthrones.core.domain.usecase.CharacterFilter,
    haptic: androidx.compose.ui.hapticfeedback.HapticFeedback,
    onFilterChange: (CharactersIntent) -> Unit
) {
    filter.gender?.let { gender ->
        AnimatedVisibility(
            visible = true,
            enter = expandHorizontally() + fadeIn(),
            exit = shrinkHorizontally() + fadeOut()
        ) {
            CompactFilterChip(
                label = gender,
                onDismiss = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onFilterChange(CharactersIntent.FilterCharacters(filter.copy(gender = null)))
                }
            )
        }
    }
}

@Composable
private fun CultureFilterChip(
    filter: com.darach.gameofthrones.core.domain.usecase.CharacterFilter,
    haptic: androidx.compose.ui.hapticfeedback.HapticFeedback,
    onFilterChange: (CharactersIntent) -> Unit
) {
    filter.culture?.let { culture ->
        AnimatedVisibility(
            visible = true,
            enter = expandHorizontally() + fadeIn(),
            exit = shrinkHorizontally() + fadeOut()
        ) {
            CompactFilterChip(
                label = culture,
                onDismiss = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onFilterChange(CharactersIntent.FilterCharacters(filter.copy(culture = null)))
                }
            )
        }
    }
}

@Composable
private fun SeasonsFilterChips(
    filter: com.darach.gameofthrones.core.domain.usecase.CharacterFilter,
    haptic: androidx.compose.ui.hapticfeedback.HapticFeedback,
    onFilterChange: (CharactersIntent) -> Unit
) {
    filter.seasons.forEach { season ->
        AnimatedVisibility(
            visible = true,
            enter = expandHorizontally() + fadeIn(),
            exit = shrinkHorizontally() + fadeOut(),
            label = "Season $season chip"
        ) {
            CompactFilterChip(
                label = "S$season",
                onDismiss = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onFilterChange(
                        CharactersIntent.FilterCharacters(
                            filter.copy(
                                seasons =
                                filter.seasons - season
                            )
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun CompactFilterChip(
    label: String,
    onDismiss: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    androidx.compose.material3.FilterChip(
        selected = true,
        onClick = onDismiss,
        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
        leadingIcon = icon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove $label filter",
                modifier = Modifier.size(16.dp)
            )
        }
    )
}

@Composable
private fun AllFiltersChip(onOpenFilterSheet: () -> Unit) {
    androidx.compose.material3.FilterChip(
        selected = false,
        onClick = onOpenFilterSheet,
        label = { Text("All Filters", style = MaterialTheme.typography.labelMedium) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        },
        modifier = Modifier.semantics {
            contentDescription = "Filter options"
        }
    )
}

// Previews
@androidx.compose.ui.tooling.preview.Preview(
    name = "Search and Filter Controls - No Filters",
    showBackground = true
)
@Composable
private fun SearchAndFilterControlsNoFiltersPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        SearchAndFilterControls(
            state = CharactersState(
                searchQuery = "",
                searchHistory = emptyList(),
                filter = com.darach.gameofthrones.core.domain.usecase.CharacterFilter(),
                sortOption = com.darach.gameofthrones.core.domain.usecase.SortOption.NAME_ASC
            ),
            onIntent = {},
            onOpenFilterSheet = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Search and Filter Controls - With Search",
    showBackground = true
)
@Composable
private fun SearchAndFilterControlsWithSearchPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        SearchAndFilterControls(
            state = CharactersState(
                searchQuery = "Jon Snow",
                searchHistory = listOf("Jon Snow", "Arya Stark"),
                filter = com.darach.gameofthrones.core.domain.usecase.CharacterFilter(),
                sortOption = com.darach.gameofthrones.core.domain.usecase.SortOption.NAME_ASC
            ),
            onIntent = {},
            onOpenFilterSheet = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Search and Filter Controls - Multiple Filters",
    showBackground = true
)
@Composable
private fun SearchAndFilterControlsMultipleFiltersPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        SearchAndFilterControls(
            state = CharactersState(
                searchQuery = "",
                searchHistory = emptyList(),
                filter = com.darach.gameofthrones.core.domain.usecase.CharacterFilter(
                    onlyFavorites = true,
                    isDead = false,
                    gender = "Female",
                    culture = "Northmen",
                    seasons = listOf(1, 2, 3)
                ),
                sortOption = com.darach.gameofthrones.core.domain.usecase.SortOption.FAVORITE_FIRST
            ),
            onIntent = {},
            onOpenFilterSheet = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Search and Filter Controls - Dark Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun SearchAndFilterControlsDarkPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        SearchAndFilterControls(
            state = CharactersState(
                searchQuery = "Daenerys",
                searchHistory = emptyList(),
                filter = com.darach.gameofthrones.core.domain.usecase.CharacterFilter(
                    isDead = true,
                    culture = "Valyrian"
                ),
                sortOption =
                com.darach.gameofthrones.core.domain.usecase.SortOption.SEASONS_COUNT_DESC
            ),
            onIntent = {},
            onOpenFilterSheet = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Search and Filter Controls - Tablet",
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
private fun SearchAndFilterControlsTabletPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        SearchAndFilterControls(
            state = CharactersState(
                searchQuery = "",
                searchHistory = emptyList(),
                filter = com.darach.gameofthrones.core.domain.usecase.CharacterFilter(
                    gender = "Male",
                    hasAppearances = true,
                    seasons = listOf(1, 2, 3, 4, 5, 6, 7, 8)
                ),
                sortOption = com.darach.gameofthrones.core.domain.usecase.SortOption.CULTURE_ASC
            ),
            onIntent = {},
            onOpenFilterSheet = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Search and Filter Controls - Many Filters (Scrolling)",
    showBackground = true
)
@Composable
private fun SearchAndFilterControlsScrollingPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        SearchAndFilterControls(
            state = CharactersState(
                searchQuery = "Stark",
                searchHistory = emptyList(),
                filter = com.darach.gameofthrones.core.domain.usecase.CharacterFilter(
                    onlyFavorites = true,
                    isDead = false,
                    gender = "Female",
                    culture = "Northmen",
                    hasAppearances = true,
                    seasons = listOf(1, 2, 3, 4, 5)
                ),
                sortOption = com.darach.gameofthrones.core.domain.usecase.SortOption.NAME_DESC
            ),
            onIntent = {},
            onOpenFilterSheet = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharactersBody(
    state: CharactersState,
    onCharacterClick: (String) -> Unit,
    onIntent: (CharactersIntent) -> Unit
) {
    when {
        state.isLoading && state.characters.isEmpty() -> LoadingState()
        state.error != null && state.characters.isEmpty() -> {
            ErrorState(
                error = state.error,
                onRetry = { onIntent(CharactersIntent.RetryLoad) }
            )
        }
        state.isEmpty && state.characters.isEmpty() -> EmptyState("No characters found")
        state.filteredCharacters.isEmpty() && state.characters.isNotEmpty() -> {
            EmptyState("No characters match your filters")
        }
        else -> {
            CharactersList(
                state = state,
                onCharacterClick = onCharacterClick,
                onIntent = onIntent
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharactersList(
    state: CharactersState,
    onCharacterClick: (String) -> Unit,
    onIntent: (CharactersIntent) -> Unit
) {
    val performHaptic = com.darach.gameofthrones.core.ui.haptics.rememberHapticFeedback()
    val onRefresh = remember(onIntent, performHaptic) {
        {
            performHaptic()
            onIntent(CharactersIntent.RefreshCharacters)
        }
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(minSize = 160.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp, bottom = 88.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalItemSpacing = 12.dp
        ) {
            items(
                items = state.filteredCharacters,
                key = { it.id }
            ) { character ->
                CharacterGridCard(
                    character = character,
                    onFavoriteClick = remember(onIntent) {
                        { id -> onIntent(CharactersIntent.ToggleFavorite(id)) }
                    },
                    onClick = remember(onCharacterClick) {
                        { onCharacterClick(character.id) }
                    },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.semantics {
                contentDescription = "Loading characters"
            }
        )
    }
}

@Composable
private fun ErrorState(error: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Error",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Previews
@androidx.compose.ui.tooling.preview.Preview(
    name = "Characters Screen - Loading",
    showBackground = true
)
@Composable
private fun LoadingStatePreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        LoadingState()
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Characters Screen - Error",
    showBackground = true
)
@Composable
private fun ErrorStatePreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        ErrorState(
            error = "Failed to load characters. Please check your connection and try again.",
            onRetry = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Characters Screen - Empty",
    showBackground = true
)
@Composable
private fun EmptyStatePreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        EmptyState(message = "No characters found")
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Characters Screen - Empty Dark Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun EmptyStateDarkPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        EmptyState(message = "No characters match your filters")
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Characters Screen - Error Tablet",
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
private fun ErrorStateTabletPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        ErrorState(
            error = "Network error occurred",
            onRetry = {}
        )
    }
}
