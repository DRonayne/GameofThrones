package com.darach.gameofthrones.feature.characters.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darach.gameofthrones.core.domain.usecase.CharacterFilter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterChips(
    currentFilter: CharacterFilter,
    onFilterChange: (CharacterFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FavoritesFilterChip(currentFilter = currentFilter, onFilterChange = onFilterChange)
        DeathFilterChips(currentFilter = currentFilter, onFilterChange = onFilterChange)
        AppearancesFilterChip(currentFilter = currentFilter, onFilterChange = onFilterChange)
        GenderFilterChips(currentFilter = currentFilter, onFilterChange = onFilterChange)
    }
}

@Composable
private fun FavoritesFilterChip(
    currentFilter: CharacterFilter,
    onFilterChange: (CharacterFilter) -> Unit
) {
    SelectableFilterChip(
        selected = currentFilter.onlyFavorites,
        onClick = {
            onFilterChange(currentFilter.copy(onlyFavorites = !currentFilter.onlyFavorites))
        },
        label = "Favorites"
    )
}

@Composable
private fun DeathFilterChips(
    currentFilter: CharacterFilter,
    onFilterChange: (CharacterFilter) -> Unit
) {
    SelectableFilterChip(
        selected = currentFilter.isDead == true,
        onClick = {
            onFilterChange(
                currentFilter.copy(
                    isDead = if (currentFilter.isDead ==
                        true
                    ) {
                        null
                    } else {
                        true
                    }
                )
            )
        },
        label = "Deceased"
    )

    SelectableFilterChip(
        selected = currentFilter.isDead == false,
        onClick = {
            onFilterChange(
                currentFilter.copy(
                    isDead = if (currentFilter.isDead ==
                        false
                    ) {
                        null
                    } else {
                        false
                    }
                )
            )
        },
        label = "Alive"
    )
}

@Composable
private fun AppearancesFilterChip(
    currentFilter: CharacterFilter,
    onFilterChange: (CharacterFilter) -> Unit
) {
    SelectableFilterChip(
        selected = currentFilter.hasAppearances == true,
        onClick = {
            onFilterChange(
                currentFilter.copy(
                    hasAppearances = if (currentFilter.hasAppearances ==
                        true
                    ) {
                        null
                    } else {
                        true
                    }
                )
            )
        },
        label = "TV Appearances"
    )
}

@Composable
private fun GenderFilterChips(
    currentFilter: CharacterFilter,
    onFilterChange: (CharacterFilter) -> Unit
) {
    SelectableFilterChip(
        selected = currentFilter.gender == "Male",
        onClick = {
            onFilterChange(
                currentFilter.copy(
                    gender = if (currentFilter.gender ==
                        "Male"
                    ) {
                        null
                    } else {
                        "Male"
                    }
                )
            )
        },
        label = "Male"
    )

    SelectableFilterChip(
        selected = currentFilter.gender == "Female",
        onClick = {
            onFilterChange(
                currentFilter.copy(
                    gender = if (currentFilter.gender ==
                        "Female"
                    ) {
                        null
                    } else {
                        "Female"
                    }
                )
            )
        },
        label = "Female"
    )
}

@Composable
private fun SelectableFilterChip(selected: Boolean, onClick: () -> Unit, label: String) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
            }
        } else {
            null
        }
    )
}

// Previews
@androidx.compose.ui.tooling.preview.Preview(name = "Filter Chips", showBackground = true)
@Composable
private fun FilterChipsPreview() {
    FilterChips(
        currentFilter = CharacterFilter(onlyFavorites = true, isDead = false),
        onFilterChange = {}
    )
}
