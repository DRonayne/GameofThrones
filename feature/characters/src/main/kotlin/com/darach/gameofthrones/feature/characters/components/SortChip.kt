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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.darach.gameofthrones.core.domain.usecase.SortOption
import com.darach.gameofthrones.core.ui.haptics.rememberHapticFeedback
import com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme

@Composable
fun SortChip(
    currentSortOption: SortOption,
    onSortOptionChange: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val performHaptic = rememberHapticFeedback()

    Box(modifier = modifier) {
        SortFilterChip(currentSortOption, performHaptic) { expanded = true }
        SortOptionsDropdown(expanded, currentSortOption, performHaptic, onSortOptionChange) {
            expanded = false
        }
    }
}

@Composable
private fun SortFilterChip(
    currentSortOption: SortOption,
    performHaptic: () -> Unit,
    onExpand: () -> Unit
) {
    val sortOptionsDescription = stringResource(com.darach.gameofthrones.core.ui.R.string.sort_options)

    FilterChip(
        selected = currentSortOption != SortOption.NAME_ASC,
        onClick = {
            performHaptic()
            onExpand()
        },
        label = {
            AnimatedContent(
                targetState = currentSortOption,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "Sort label animation"
            ) { sortOption ->
                Text(
                    stringResource(
                        com.darach.gameofthrones.core.ui.R.string.sort_label,
                        getSortOptionShortLabel(sortOption)
                    ),
                    style = MaterialTheme.typography.labelMedium
                )
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
            contentDescription = sortOptionsDescription
        }
    )
}

@Composable
private fun SortOptionsDropdown(
    expanded: Boolean,
    currentSortOption: SortOption,
    performHaptic: () -> Unit,
    onSortOptionChange: (SortOption) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        SortOption.entries.forEach { sortOption ->
            DropdownMenuItem(
                text = { Text(getSortOptionLabel(sortOption)) },
                onClick = {
                    performHaptic()
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

@Composable
private fun getSortOptionLabel(sortOption: SortOption): String = when (sortOption) {
    SortOption.NAME_ASC -> stringResource(com.darach.gameofthrones.core.ui.R.string.sort_name_asc)
    SortOption.NAME_DESC -> stringResource(com.darach.gameofthrones.core.ui.R.string.sort_name_desc)
    SortOption.CULTURE_ASC -> stringResource(
        com.darach.gameofthrones.core.ui.R.string.sort_culture_asc
    )
    SortOption.CULTURE_DESC -> stringResource(
        com.darach.gameofthrones.core.ui.R.string.sort_culture_desc
    )
    SortOption.DEATH_DATE_ASC -> stringResource(
        com.darach.gameofthrones.core.ui.R.string.sort_death_oldest
    )
    SortOption.DEATH_DATE_DESC -> stringResource(
        com.darach.gameofthrones.core.ui.R.string.sort_death_newest
    )
    SortOption.SEASONS_COUNT_ASC -> stringResource(
        com.darach.gameofthrones.core.ui.R.string.sort_seasons_fewest
    )
    SortOption.SEASONS_COUNT_DESC -> stringResource(
        com.darach.gameofthrones.core.ui.R.string.sort_seasons_most
    )
    SortOption.FAVORITE_FIRST -> stringResource(
        com.darach.gameofthrones.core.ui.R.string.sort_favorites_first
    )
}

@Composable
private fun getSortOptionShortLabel(sortOption: SortOption): String = when (sortOption) {
    SortOption.NAME_ASC -> stringResource(
        com.darach.gameofthrones.core.ui.R.string.sort_name_asc_short
    )
    SortOption.NAME_DESC -> stringResource(
        com.darach.gameofthrones.core.ui.R.string.sort_name_desc_short
    )
    SortOption.CULTURE_ASC -> stringResource(
        com.darach.gameofthrones.core.ui.R.string.sort_culture_asc_short
    )
    SortOption.CULTURE_DESC -> stringResource(
        com.darach.gameofthrones.core.ui.R.string.sort_culture_desc_short
    )
    SortOption.DEATH_DATE_ASC -> stringResource(
        com.darach.gameofthrones.core.ui.R.string.sort_death_oldest_short
    )
    SortOption.DEATH_DATE_DESC -> stringResource(
        com.darach.gameofthrones.core.ui.R.string.sort_death_newest_short
    )
    SortOption.SEASONS_COUNT_ASC -> stringResource(
        com.darach.gameofthrones.core.ui.R.string.sort_seasons_fewest_short
    )
    SortOption.SEASONS_COUNT_DESC -> stringResource(
        com.darach.gameofthrones.core.ui.R.string.sort_seasons_most_short
    )
    SortOption.FAVORITE_FIRST -> stringResource(
        com.darach.gameofthrones.core.ui.R.string.sort_favorites_first_short
    )
}

// Previews
@androidx.compose.ui.tooling.preview.Preview(
    name = "Sort Chip - Name Ascending",
    showBackground = true
)
@Composable
private fun SortChipNameAscPreview() {
    GameOfThronesTheme {
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
    GameOfThronesTheme {
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
    GameOfThronesTheme {
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
    GameOfThronesTheme {
        SortChip(
            currentSortOption = SortOption.CULTURE_ASC,
            onSortOptionChange = {}
        )
    }
}
