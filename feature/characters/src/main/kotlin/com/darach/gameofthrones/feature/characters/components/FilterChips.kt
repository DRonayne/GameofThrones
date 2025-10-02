package com.darach.gameofthrones.feature.characters.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darach.gameofthrones.core.domain.usecase.CharacterFilter
import com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterChips(
    currentFilter: CharacterFilter,
    onFilterChange: (CharacterFilter) -> Unit,
    modifier: Modifier = Modifier,
    availableCultures: List<String> = emptyList(),
    availableSeasons: List<Int> = emptyList()
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        FilterHeader(currentFilter = currentFilter, onFilterChange = onFilterChange)

        Spacer(modifier = Modifier.height(8.dp))

        // Status filters
        FilterSection(title = "Status") {
            FavoritesFilterChip(currentFilter = currentFilter, onFilterChange = onFilterChange)
            DeathFilterChips(currentFilter = currentFilter, onFilterChange = onFilterChange)
            AppearancesFilterChip(currentFilter = currentFilter, onFilterChange = onFilterChange)
        }

        // Gender filters
        FilterSection(title = "Gender") {
            GenderFilterChips(currentFilter = currentFilter, onFilterChange = onFilterChange)
        }

        // Culture filters
        if (availableCultures.isNotEmpty()) {
            FilterSection(title = "Culture") {
                CultureFilterChips(
                    currentFilter = currentFilter,
                    onFilterChange = onFilterChange,
                    availableCultures = availableCultures
                )
            }
        }

        // Season filters
        if (availableSeasons.isNotEmpty()) {
            FilterSection(title = "Seasons") {
                SeasonFilterChips(
                    currentFilter = currentFilter,
                    onFilterChange = onFilterChange,
                    availableSeasons = availableSeasons
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterSection(title: String, content: @Composable FlowRowScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            content = content
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun FilterHeader(
    currentFilter: CharacterFilter,
    onFilterChange: (CharacterFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BadgedBox(
            badge = {
                if (currentFilter.activeFilterCount() > 0) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = currentFilter.activeFilterCount().toString(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        ) {
            Text(
                text = "Filters",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (currentFilter.isActive()) {
            TextButton(
                onClick = { onFilterChange(CharacterFilter()) }
            ) {
                Text(
                    text = "Clear all",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
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
private fun CultureFilterChips(
    currentFilter: CharacterFilter,
    onFilterChange: (CharacterFilter) -> Unit,
    availableCultures: List<String>
) {
    availableCultures.take(MAX_CULTURE_CHIPS).forEach { culture ->
        SelectableFilterChip(
            selected = currentFilter.culture == culture,
            onClick = {
                onFilterChange(
                    currentFilter.copy(
                        culture = if (currentFilter.culture == culture) null else culture
                    )
                )
            },
            label = culture
        )
    }
}

@Composable
private fun SeasonFilterChips(
    currentFilter: CharacterFilter,
    onFilterChange: (CharacterFilter) -> Unit,
    availableSeasons: List<Int>
) {
    availableSeasons.forEach { season ->
        SelectableFilterChip(
            selected = currentFilter.seasons.contains(season),
            onClick = {
                val newSeasons = if (currentFilter.seasons.contains(season)) {
                    currentFilter.seasons - season
                } else {
                    currentFilter.seasons + season
                }
                onFilterChange(currentFilter.copy(seasons = newSeasons))
            },
            label = "Season $season"
        )
    }
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

private const val MAX_CULTURE_CHIPS = 10

// Previews
@androidx.compose.ui.tooling.preview.Preview(
    name = "Filter Chips - No Filters",
    showBackground = true
)
@Composable
private fun FilterChipsNoFiltersPreview() {
    GameOfThronesTheme {
        FilterChips(
            currentFilter = CharacterFilter(),
            onFilterChange = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Filter Chips - Multiple Filters",
    showBackground = true
)
@Composable
private fun FilterChipsMultipleFiltersPreview() {
    GameOfThronesTheme {
        FilterChips(
            currentFilter = CharacterFilter(
                onlyFavorites = true,
                isDead = false,
                gender = "Male",
                hasAppearances = true
            ),
            onFilterChange = {},
            availableCultures = listOf("Northmen", "Dothraki", "Andal", "Valyrian"),
            availableSeasons = listOf(1, 2, 3, 4, 5, 6, 7, 8)
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Filter Chips - With Cultures and Seasons",
    showBackground = true
)
@Composable
private fun FilterChipsWithCulturesPreview() {
    GameOfThronesTheme {
        FilterChips(
            currentFilter = CharacterFilter(
                culture = "Northmen",
                seasons = listOf(1, 2, 3)
            ),
            onFilterChange = {},
            availableCultures = listOf(
                "Northmen",
                "Dothraki",
                "Andal",
                "Valyrian",
                "Ironborn",
                "Dornish"
            ),
            availableSeasons = listOf(1, 2, 3, 4, 5, 6, 7, 8)
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Filter Chips - Dark Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun FilterChipsDarkPreview() {
    GameOfThronesTheme {
        FilterChips(
            currentFilter = CharacterFilter(
                onlyFavorites = true,
                isDead = true,
                gender = "Female"
            ),
            onFilterChange = {},
            availableCultures = listOf("Northmen", "Dothraki"),
            availableSeasons = listOf(1, 2, 3, 4, 5)
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Filter Chips - Tablet",
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
private fun FilterChipsTabletPreview() {
    GameOfThronesTheme {
        FilterChips(
            currentFilter = CharacterFilter(
                gender = "Male",
                culture = "Valyrian",
                seasons = listOf(1, 2, 3, 4, 5, 6)
            ),
            onFilterChange = {},
            availableCultures = listOf(
                "Northmen",
                "Dothraki",
                "Andal",
                "Valyrian",
                "Ironborn",
                "Dornish",
                "First Men",
                "Wildling"
            ),
            availableSeasons = listOf(1, 2, 3, 4, 5, 6, 7, 8)
        )
    }
}
