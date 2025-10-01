package com.darach.gameofthrones.feature.characters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darach.gameofthrones.core.ui.performance.TrackScreenLoadTime
import com.darach.gameofthrones.feature.characters.CharactersViewModel
import com.darach.gameofthrones.feature.characters.components.CharacterCard
import com.darach.gameofthrones.feature.characters.components.CharactersSearchBar
import com.darach.gameofthrones.feature.characters.components.FilterBottomSheet
import com.darach.gameofthrones.feature.characters.components.FilterBottomSheetState
import com.darach.gameofthrones.feature.characters.components.OfflineIndicator
import com.darach.gameofthrones.feature.characters.components.SearchBarCallbacks
import com.darach.gameofthrones.feature.characters.components.SortOptionsMenu

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
        modifier = modifier
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            CharactersContent(
                state = state,
                callbacks = CharactersScreenCallbacks(
                    onCharacterClick = onCharacterClick,
                    onIntent = viewModel::handleIntent,
                    onFilterClick = { showFilterSheet = true }
                )
            )

            OfflineIndicator(
                isOffline = !isOnline,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 16.dp)
            )
        }

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

@Composable
private fun CharactersContent(
    state: CharactersState,
    callbacks: CharactersScreenCallbacks,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        CharactersHeader(
            state = state,
            onIntent = callbacks.onIntent,
            onFilterClick = callbacks.onFilterClick
        )

        CharactersBody(
            state = state,
            onCharacterClick = callbacks.onCharacterClick,
            onIntent = callbacks.onIntent
        )
    }
}

@Composable
private fun CharactersHeader(
    state: CharactersState,
    onIntent: (CharactersIntent) -> Unit,
    onFilterClick: () -> Unit
) {
    val searchBarCallbacks = remember(onIntent) {
        SearchBarCallbacks(
            onQueryChange = { onIntent(CharactersIntent.SearchCharacters(it)) },
            onSearch = { onIntent(CharactersIntent.SearchCharacters(it)) },
            onClearSearch = { onIntent(CharactersIntent.ClearSearch) }
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CharactersSearchBar(
                query = state.searchQuery,
                searchHistory = state.searchHistory,
                callbacks = searchBarCallbacks,
                modifier = Modifier.weight(1f)
            )

            BadgedBox(
                badge = {
                    if (state.filter.activeFilterCount() > 0) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = state.filter.activeFilterCount().toString(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            ) {
                IconButton(onClick = onFilterClick) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter characters"
                    )
                }
            }

            SortOptionsMenu(
                currentSortOption = state.sortOption,
                onSortOptionChange = remember(onIntent) {
                    { option -> onIntent(CharactersIntent.SortCharacters(option)) }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
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
    val onRefresh = remember(onIntent) {
        { onIntent(CharactersIntent.RefreshCharacters) }
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = state.filteredCharacters,
                key = { it.id }
            ) { character ->
                CharacterCard(
                    character = character,
                    onFavoriteClick = remember(onIntent) {
                        { id -> onIntent(CharactersIntent.ToggleFavorite(id)) }
                    },
                    onClick = remember(onCharacterClick) {
                        { onCharacterClick(character.id) }
                    }
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
