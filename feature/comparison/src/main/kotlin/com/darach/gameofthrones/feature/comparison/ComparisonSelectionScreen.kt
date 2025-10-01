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

// Sample data for previews
private val sampleCharacters = listOf(
    Character(
        id = "583",
        name = "Jon Snow",
        gender = "Male",
        culture = "Northmen",
        born = "In 283 AC",
        died = "",
        titles = listOf("Lord Commander of the Night's Watch"),
        aliases = listOf("Lord Snow"),
        father = "",
        mother = "",
        spouse = "",
        allegiances = listOf(),
        books = listOf(),
        povBooks = listOf(),
        tvSeries = listOf(),
        tvSeriesSeasons = listOf(1, 2, 3, 4, 5, 6, 7, 8),
        playedBy = listOf("Kit Harington")
    ),
    Character(
        id = "148",
        name = "Arya Stark",
        gender = "Female",
        culture = "Northmen",
        born = "In 289 AC",
        died = "",
        titles = listOf("Princess"),
        aliases = listOf("No One"),
        father = "",
        mother = "",
        spouse = "",
        allegiances = listOf(),
        books = listOf(),
        povBooks = listOf(),
        tvSeries = listOf(),
        tvSeriesSeasons = listOf(1, 2, 3, 4, 5, 6, 7, 8),
        playedBy = listOf("Maisie Williams")
    ),
    Character(
        id = "1052",
        name = "Tyrion Lannister",
        gender = "Male",
        culture = "Andal",
        born = "In 273 AC",
        died = "",
        titles = listOf("Hand of the Queen"),
        aliases = listOf("The Imp"),
        father = "",
        mother = "",
        spouse = "",
        allegiances = listOf(),
        books = listOf(),
        povBooks = listOf(),
        tvSeries = listOf(),
        tvSeriesSeasons = listOf(1, 2, 3, 4, 5, 6, 7, 8),
        playedBy = listOf("Peter Dinklage")
    )
)

// Previews
@androidx.compose.ui.tooling.preview.Preview(
    name = "Comparison Selection - No Selection",
    showBackground = true
)
@Composable
private fun ComparisonSelectionScreenNoSelectionPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        ComparisonSelectionScreen(
            characters = sampleCharacters,
            selectedCharacters = emptyList(),
            callbacks = ComparisonSelectionCallbacks(
                onCharacterToggle = {},
                onCompareClick = {},
                onClearSelection = {},
                onBackClick = {}
            )
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Comparison Selection - Two Selected",
    showBackground = true
)
@Composable
private fun ComparisonSelectionScreenTwoSelectedPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        ComparisonSelectionScreen(
            characters = sampleCharacters,
            selectedCharacters = sampleCharacters.take(2),
            callbacks = ComparisonSelectionCallbacks(
                onCharacterToggle = {},
                onCompareClick = {},
                onClearSelection = {},
                onBackClick = {}
            )
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Comparison Selection - Max Selected",
    showBackground = true
)
@Composable
private fun ComparisonSelectionScreenMaxSelectedPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        ComparisonSelectionScreen(
            characters = sampleCharacters,
            selectedCharacters = sampleCharacters,
            callbacks = ComparisonSelectionCallbacks(
                onCharacterToggle = {},
                onCompareClick = {},
                onClearSelection = {},
                onBackClick = {}
            )
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Comparison Selection - Empty List",
    showBackground = true
)
@Composable
private fun ComparisonSelectionScreenEmptyPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        ComparisonSelectionScreen(
            characters = emptyList(),
            selectedCharacters = emptyList(),
            callbacks = ComparisonSelectionCallbacks(
                onCharacterToggle = {},
                onCompareClick = {},
                onClearSelection = {},
                onBackClick = {}
            )
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Comparison Selection - Dark Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ComparisonSelectionScreenDarkPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        ComparisonSelectionScreen(
            characters = sampleCharacters,
            selectedCharacters = sampleCharacters.take(2),
            callbacks = ComparisonSelectionCallbacks(
                onCharacterToggle = {},
                onCompareClick = {},
                onClearSelection = {},
                onBackClick = {}
            )
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Comparison Selection - Tablet",
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
private fun ComparisonSelectionScreenTabletPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        ComparisonSelectionScreen(
            characters = sampleCharacters,
            selectedCharacters = sampleCharacters.take(1),
            callbacks = ComparisonSelectionCallbacks(
                onCharacterToggle = {},
                onCompareClick = {},
                onClearSelection = {},
                onBackClick = {}
            )
        )
    }
}
