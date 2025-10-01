package com.darach.gameofthrones.feature.comparison

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darach.gameofthrones.core.model.Character
import com.darach.gameofthrones.feature.comparison.components.ComparisonSelectionBar
import com.darach.gameofthrones.feature.comparison.components.ComparisonSelectionBarCallbacks
import com.darach.gameofthrones.feature.comparison.components.ComparisonSelectionBarState
import com.darach.gameofthrones.feature.comparison.components.SelectableCharacterCard

/**
 * Screen for selecting characters to compare.
 * Allows selection of 2-3 characters with visual feedback.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonSelectionScreen(
    characters: List<Character>,
    selectedCharacters: List<Character>,
    callbacks: ComparisonSelectionCallbacks,
    modifier: Modifier = Modifier
) {
    val selectionCount = selectedCharacters.size
    val canCompare = selectionCount in 2..ComparisonState.MAX_SELECTION_SIZE
    val isMaxSelected = selectionCount >= ComparisonState.MAX_SELECTION_SIZE

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Select Characters to Compare") },
                navigationIcon = {
                    IconButton(onClick = callbacks.onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            ComparisonSelectionBar(
                state = ComparisonSelectionBarState(
                    selectionCount = selectionCount,
                    maxSelection = ComparisonState.MAX_SELECTION_SIZE,
                    canCompare = canCompare
                ),
                callbacks = ComparisonSelectionBarCallbacks(
                    onClearClick = callbacks.onClearSelection,
                    onCompareClick = callbacks.onCompareClick,
                    onCloseClick = callbacks.onBackClick
                )
            )
        }
    ) { paddingValues ->
        ComparisonSelectionContent(
            characters = characters,
            selectedCharacters = selectedCharacters,
            isMaxSelected = isMaxSelected,
            onCharacterToggle = callbacks.onCharacterToggle,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun ComparisonSelectionContent(
    characters: List<Character>,
    selectedCharacters: List<Character>,
    isMaxSelected: Boolean,
    onCharacterToggle: (Character) -> Unit,
    modifier: Modifier = Modifier
) {
    if (characters.isEmpty()) {
        EmptyState(modifier = modifier)
    } else {
        CharacterSelectionList(
            characters = characters,
            selectedCharacters = selectedCharacters,
            isMaxSelected = isMaxSelected,
            onCharacterToggle = onCharacterToggle,
            modifier = modifier
        )
    }
}

@Composable
private fun CharacterSelectionList(
    characters: List<Character>,
    selectedCharacters: List<Character>,
    isMaxSelected: Boolean,
    onCharacterToggle: (Character) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = characters,
            key = { it.id }
        ) { character ->
            val isSelected = selectedCharacters.any { it.id == character.id }
            val canSelect = !isMaxSelected || isSelected

            SelectableCharacterCard(
                character = character,
                isSelected = isSelected,
                isSelectionEnabled = canSelect,
                onClick = {
                    if (canSelect) {
                        onCharacterToggle(character)
                    }
                }
            )
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No characters available for comparison",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
