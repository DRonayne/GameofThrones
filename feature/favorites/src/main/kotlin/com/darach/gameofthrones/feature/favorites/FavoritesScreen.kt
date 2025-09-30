package com.darach.gameofthrones.feature.favorites

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darach.gameofthrones.core.domain.model.Character
import com.darach.gameofthrones.feature.favorites.components.FavoriteCard
import com.darach.gameofthrones.feature.favorites.components.FavoriteCardCallbacks as CardCallbacks
import com.darach.gameofthrones.feature.favorites.ui.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onCharacterClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            FavoritesTopBar(
                state = state,
                callbacks = FavoritesTopBarCallbacks(
                    onBackClick = onBackClick,
                    onViewModeToggle = { viewModel.handleIntent(FavoritesIntent.ToggleViewMode) },
                    onSelectionModeToggle = {
                        viewModel.handleIntent(FavoritesIntent.ToggleSelectionMode)
                    },
                    onSelectAll = { viewModel.handleIntent(FavoritesIntent.SelectAll) },
                    onDeselectAll = { viewModel.handleIntent(FavoritesIntent.DeselectAll) }
                )
            )
        },
        floatingActionButton = {
            if (state.isSelectionMode && state.selectedIds.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { viewModel.handleIntent(FavoritesIntent.RemoveSelected) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove selected"
                    )
                }
            }
        }
    ) { paddingValues ->
        FavoritesContent(
            state = state,
            onCharacterClick = onCharacterClick,
            onToggleSelection = { id ->
                viewModel.handleIntent(FavoritesIntent.ToggleSelection(id))
            },
            onRemoveFavorite = { id -> viewModel.handleIntent(FavoritesIntent.RemoveFavorite(id)) },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

data class FavoritesTopBarCallbacks(
    val onBackClick: () -> Unit,
    val onViewModeToggle: () -> Unit,
    val onSelectionModeToggle: () -> Unit,
    val onSelectAll: () -> Unit,
    val onDeselectAll: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoritesTopBar(
    state: FavoritesState,
    callbacks: FavoritesTopBarCallbacks,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { FavoritesTopBarTitle(state) },
        navigationIcon = { FavoritesNavigationIcon(state, callbacks) },
        actions = { FavoritesTopBarActions(state, callbacks) },
        modifier = modifier
    )
}

@Composable
private fun FavoritesTopBarTitle(state: FavoritesState) {
    Text(
        text = if (state.isSelectionMode) {
            "${state.selectedIds.size} selected"
        } else {
            "Favorites (${state.favorites.size})"
        }
    )
}

@Composable
private fun FavoritesNavigationIcon(state: FavoritesState, callbacks: FavoritesTopBarCallbacks) {
    val onClick = if (state.isSelectionMode) {
        callbacks.onSelectionModeToggle
    } else {
        callbacks.onBackClick
    }
    val contentDesc = if (state.isSelectionMode) "Exit selection mode" else "Back"
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = contentDesc
        )
    }
}

@Composable
private fun FavoritesTopBarActions(state: FavoritesState, callbacks: FavoritesTopBarCallbacks) {
    if (state.isSelectionMode) {
        SelectionModeActions(state, callbacks)
    } else {
        NormalModeActions(state, callbacks)
    }
}

@Composable
private fun SelectionModeActions(state: FavoritesState, callbacks: FavoritesTopBarCallbacks) {
    val allSelected = state.selectedIds.size == state.favorites.size
    if (allSelected && state.favorites.isNotEmpty()) {
        IconButton(onClick = callbacks.onDeselectAll) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Deselect all"
            )
        }
    } else {
        IconButton(onClick = callbacks.onSelectAll) {
            Icon(
                imageVector = Icons.Default.SelectAll,
                contentDescription = "Select all"
            )
        }
    }
}

@Composable
private fun NormalModeActions(state: FavoritesState, callbacks: FavoritesTopBarCallbacks) {
    Row {
        IconButton(onClick = callbacks.onViewModeToggle) {
            Icon(
                imageVector = when (state.viewMode) {
                    ViewMode.GRID -> Icons.Default.ViewList
                    ViewMode.LIST -> Icons.Default.GridView
                },
                contentDescription = "Toggle view mode"
            )
        }
        if (state.favorites.isNotEmpty()) {
            IconButton(onClick = callbacks.onSelectionModeToggle) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Enter selection mode"
                )
            }
        }
    }
}

@Composable
private fun FavoritesContent(
    state: FavoritesState,
    onCharacterClick: (String) -> Unit,
    onToggleSelection: (String) -> Unit,
    onRemoveFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            state.isEmpty -> {
                EmptyFavoritesState(
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
                val callbacks = FavoritesCallbacks(
                    onCharacterClick = onCharacterClick,
                    onToggleSelection = onToggleSelection,
                    onRemoveFavorite = onRemoveFavorite
                )
                when (state.viewMode) {
                    ViewMode.GRID -> FavoritesGridView(
                        state = state,
                        callbacks = callbacks
                    )
                    ViewMode.LIST -> FavoritesListView(
                        state = state,
                        callbacks = callbacks
                    )
                }
            }
        }
    }
}

data class FavoritesCallbacks(
    val onCharacterClick: (String) -> Unit,
    val onToggleSelection: (String) -> Unit,
    val onRemoveFavorite: (String) -> Unit
)

@Composable
private fun FavoritesGridView(
    state: FavoritesState,
    callbacks: FavoritesCallbacks,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = state.favorites,
            key = { it.id }
        ) { character ->
            FavoriteCard(
                character = character,
                isSelectionMode = state.isSelectionMode,
                isSelected = state.selectedIds.contains(character.id),
                callbacks = CardCallbacks(
                    onCharacterClick = { callbacks.onCharacterClick(character.id) },
                    onToggleSelection = { callbacks.onToggleSelection(character.id) },
                    onRemoveFavorite = { callbacks.onRemoveFavorite(character.id) }
                )
            )
        }
    }
}

@Composable
private fun FavoritesListView(
    state: FavoritesState,
    callbacks: FavoritesCallbacks,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = state.favorites,
            key = { it.id }
        ) { character ->
            FavoriteCard(
                character = character,
                isSelectionMode = state.isSelectionMode,
                isSelected = state.selectedIds.contains(character.id),
                callbacks = CardCallbacks(
                    onCharacterClick = { callbacks.onCharacterClick(character.id) },
                    onToggleSelection = { callbacks.onToggleSelection(character.id) },
                    onRemoveFavorite = { callbacks.onRemoveFavorite(character.id) }
                )
            )
        }
    }
}

@Composable
private fun EmptyFavoritesState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "\u2665",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No favorites yet",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Start adding characters to your favorites",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorState(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
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
