package com.darach.gameofthrones.feature.characters.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.darach.gameofthrones.core.domain.usecase.CharacterFilter
import com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme

@Composable
fun ActiveFilterChipsRow(
    filter: CharacterFilter,
    onFilterChange: (CharacterFilter) -> Unit,
    onOpenAllFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActiveFilterChips(filter = filter, onFilterChange = onFilterChange)
        AllFiltersChip(onOpenAllFilters = onOpenAllFilters)
    }
}

@Composable
private fun ActiveFilterChips(filter: CharacterFilter, onFilterChange: (CharacterFilter) -> Unit) {
    if (filter.onlyFavorites) {
        DismissibleFilterChip(
            label = "Favorites",
            onDismiss = { onFilterChange(filter.copy(onlyFavorites = false)) },
            contentDescription = "Remove favorites filter",
            icon = Icons.Default.Favorite
        )
    }

    filter.isDead?.let { isDead ->
        DismissibleFilterChip(
            label = if (isDead) "Deceased" else "Alive",
            onDismiss = { onFilterChange(filter.copy(isDead = null)) },
            contentDescription = "Remove status filter"
        )
    }

    if (filter.hasAppearances == true) {
        DismissibleFilterChip(
            label = "TV Appearances",
            onDismiss = { onFilterChange(filter.copy(hasAppearances = null)) },
            contentDescription = "Remove TV appearances filter"
        )
    }

    filter.gender?.let { gender ->
        DismissibleFilterChip(
            label = gender,
            onDismiss = { onFilterChange(filter.copy(gender = null)) },
            contentDescription = "Remove gender filter"
        )
    }

    filter.culture?.let { culture ->
        DismissibleFilterChip(
            label = culture,
            onDismiss = { onFilterChange(filter.copy(culture = null)) },
            contentDescription = "Remove culture filter"
        )
    }

    filter.seasons.forEach { season ->
        DismissibleFilterChip(
            label = "Season $season",
            onDismiss = { onFilterChange(filter.copy(seasons = filter.seasons - season)) },
            contentDescription = "Remove season $season filter"
        )
    }
}

@Composable
private fun AllFiltersChip(onOpenAllFilters: () -> Unit) {
    FilterChip(
        selected = false,
        onClick = onOpenAllFilters,
        label = { androidx.compose.material3.Text("All Filters") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null
            )
        },
        modifier = Modifier.semantics {
            contentDescription = "Open all filters"
        }
    )
}

@Composable
private fun DismissibleFilterChip(
    label: String,
    onDismiss: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    FilterChip(
        selected = true,
        onClick = onDismiss,
        label = { androidx.compose.material3.Text(label) },
        leadingIcon = icon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null
                )
            }
        },
        trailingIcon = {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.semantics {
                    this.contentDescription = contentDescription
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null
                )
            }
        },
        modifier = modifier
    )
}

// Previews
@androidx.compose.ui.tooling.preview.Preview(
    name = "Active Filter Chips - Multiple Filters",
    showBackground = true
)
@Composable
private fun ActiveFilterChipsMultipleFiltersPreview() {
    GameOfThronesTheme {
        ActiveFilterChipsRow(
            filter = CharacterFilter(
                onlyFavorites = true,
                isDead = false,
                gender = "Male",
                hasAppearances = true,
                culture = "Northmen",
                seasons = listOf(1, 2, 3)
            ),
            onFilterChange = {},
            onOpenAllFilters = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Active Filter Chips - No Filters",
    showBackground = true
)
@Composable
private fun ActiveFilterChipsNoFiltersPreview() {
    GameOfThronesTheme {
        ActiveFilterChipsRow(
            filter = CharacterFilter(),
            onFilterChange = {},
            onOpenAllFilters = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Active Filter Chips - Dark Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ActiveFilterChipsDarkPreview() {
    GameOfThronesTheme {
        ActiveFilterChipsRow(
            filter = CharacterFilter(
                onlyFavorites = true,
                isDead = true,
                culture = "Dothraki"
            ),
            onFilterChange = {},
            onOpenAllFilters = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Active Filter Chips - Tablet",
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
private fun ActiveFilterChipsTabletPreview() {
    GameOfThronesTheme {
        ActiveFilterChipsRow(
            filter = CharacterFilter(
                gender = "Female",
                culture = "Valyrian",
                seasons = listOf(1, 2, 3, 4, 5, 6)
            ),
            onFilterChange = {},
            onOpenAllFilters = {}
        )
    }
}
