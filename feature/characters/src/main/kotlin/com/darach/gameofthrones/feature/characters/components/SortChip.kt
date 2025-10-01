package com.darach.gameofthrones.feature.characters.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.darach.gameofthrones.core.domain.usecase.SortOption

@Composable
fun SortChip(
    currentSortOption: SortOption,
    onSortOptionChange: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    Box(modifier = modifier) {
        SortFilterChip(currentSortOption, haptic) { expanded = true }
        SortOptionsDropdown(expanded, currentSortOption, haptic, onSortOptionChange) {
            expanded = false
        }
    }
}

@Composable
private fun SortFilterChip(
    currentSortOption: SortOption,
    haptic: androidx.compose.ui.hapticfeedback.HapticFeedback,
    onExpand: () -> Unit
) {
    FilterChip(
        selected = currentSortOption != SortOption.NAME_ASC,
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onExpand()
        },
        label = {
            AnimatedContent(
                targetState = getSortOptionShortLabel(currentSortOption),
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "Sort label animation"
            ) { label ->
                Text("Sort: $label", style = MaterialTheme.typography.labelMedium)
            }
        },
        leadingIcon = {
            Icon(
                imageVector = if (currentSortOption != SortOption.NAME_ASC) {
                    Icons.Default.Check
                } else {
                    Icons.Default.UnfoldMore
                },
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        },
        modifier = Modifier.semantics {
            contentDescription = "Sort options"
        }
    )
}

@Composable
private fun SortOptionsDropdown(
    expanded: Boolean,
    currentSortOption: SortOption,
    haptic: androidx.compose.ui.hapticfeedback.HapticFeedback,
    onSortOptionChange: (SortOption) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        SortOption.entries.forEach { sortOption ->
            DropdownMenuItem(
                text = { Text(getSortOptionLabel(sortOption)) },
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSortOptionChange(sortOption)
                    onDismiss()
                },
                leadingIcon = {
                    if (currentSortOption == sortOption) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    }
                }
            )
        }
    }
}

private fun getSortOptionLabel(sortOption: SortOption): String = when (sortOption) {
    SortOption.NAME_ASC -> "Name (A-Z)"
    SortOption.NAME_DESC -> "Name (Z-A)"
    SortOption.CULTURE_ASC -> "Culture (A-Z)"
    SortOption.CULTURE_DESC -> "Culture (Z-A)"
    SortOption.DEATH_DATE_ASC -> "Death Date (Oldest First)"
    SortOption.DEATH_DATE_DESC -> "Death Date (Newest First)"
    SortOption.SEASONS_COUNT_ASC -> "Seasons Count (Fewest First)"
    SortOption.SEASONS_COUNT_DESC -> "Seasons Count (Most First)"
    SortOption.FAVORITE_FIRST -> "Favorites First"
}

private fun getSortOptionShortLabel(sortOption: SortOption): String = when (sortOption) {
    SortOption.NAME_ASC -> "Name A-Z"
    SortOption.NAME_DESC -> "Name Z-A"
    SortOption.CULTURE_ASC -> "Culture A-Z"
    SortOption.CULTURE_DESC -> "Culture Z-A"
    SortOption.DEATH_DATE_ASC -> "Death ↑"
    SortOption.DEATH_DATE_DESC -> "Death ↓"
    SortOption.SEASONS_COUNT_ASC -> "Seasons ↑"
    SortOption.SEASONS_COUNT_DESC -> "Seasons ↓"
    SortOption.FAVORITE_FIRST -> "Favorites"
}

// Previews
@androidx.compose.ui.tooling.preview.Preview(
    name = "Sort Chip - Name Ascending",
    showBackground = true
)
@Composable
private fun SortChipNameAscPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        SortChip(
            currentSortOption = SortOption.NAME_ASC,
            onSortOptionChange = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Sort Chip - Favorites",
    showBackground = true
)
@Composable
private fun SortChipFavoritesPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        SortChip(
            currentSortOption = SortOption.FAVORITE_FIRST,
            onSortOptionChange = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Sort Chip - Dark Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun SortChipDarkPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        SortChip(
            currentSortOption = SortOption.SEASONS_COUNT_DESC,
            onSortOptionChange = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Sort Chip - Tablet",
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
private fun SortChipTabletPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        SortChip(
            currentSortOption = SortOption.CULTURE_ASC,
            onSortOptionChange = {}
        )
    }
}
