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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darach.gameofthrones.feature.characters.components.CharacterCard
import com.darach.gameofthrones.feature.characters.components.CharactersSearchBar
import com.darach.gameofthrones.feature.characters.components.FilterBottomSheet
import com.darach.gameofthrones.feature.characters.components.FilterBottomSheetState
import com.darach.gameofthrones.feature.characters.components.SearchBarCallbacks
import com.darach.gameofthrones.feature.characters.components.SortOptionsMenu
import com.darach.gameofthrones.feature.characters.ui.CharactersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharactersScreen(
    onCharacterClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CharactersViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
    ) { paddingValues ->
        CharactersContent(
            state = state,
            onCharacterClick = onCharacterClick,
            onIntent = viewModel::handleIntent,
            onFilterClick = { showFilterSheet = true },
            modifier = Modifier.padding(paddingValues)
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

@Composable
private fun CharactersContent(
    state: CharactersState,
    onCharacterClick: (String) -> Unit,
    onIntent: (CharactersIntent) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        CharactersHeader(
            state = state,
            onIntent = onIntent,
            onFilterClick = onFilterClick
        )

        CharactersBody(
            state = state,
            onCharacterClick = onCharacterClick,
            onIntent = onIntent
        )
    }
}

@Composable
private fun CharactersHeader(
    state: CharactersState,
    onIntent: (CharactersIntent) -> Unit,
    onFilterClick: () -> Unit
) {
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
                callbacks = SearchBarCallbacks(
                    onQueryChange = { onIntent(CharactersIntent.SearchCharacters(it)) },
                    onSearch = { onIntent(CharactersIntent.SearchCharacters(it)) },
                    onClearSearch = { onIntent(CharactersIntent.ClearSearch) }
                ),
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
                onSortOptionChange = { onIntent(CharactersIntent.SortCharacters(it)) }
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
    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = { onIntent(CharactersIntent.RefreshCharacters) },
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
                    onFavoriteClick = { onIntent(CharactersIntent.ToggleFavorite(it)) },
                    onClick = { onCharacterClick(character.id) }
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
        CircularProgressIndicator()
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
