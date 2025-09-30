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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.darach.gameofthrones.core.domain.usecase.CharacterFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    state: FilterBottomSheetState,
    onFilterChange: (CharacterFilter) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            FilterHeader(currentFilter = state.currentFilter, onFilterChange = onFilterChange)

            Spacer(modifier = Modifier.height(16.dp))

            // Status filters
            FilterSection(title = "Status") {
                FavoritesFilterChip(
                    currentFilter = state.currentFilter,
                    onFilterChange = onFilterChange
                )
                DeathFilterChips(
                    currentFilter = state.currentFilter,
                    onFilterChange = onFilterChange
                )
                AppearancesFilterChip(
                    currentFilter = state.currentFilter,
                    onFilterChange = onFilterChange
                )
            }

            // Gender filters
            FilterSection(title = "Gender") {
                GenderFilterChips(
                    currentFilter = state.currentFilter,
                    onFilterChange = onFilterChange
                )
            }

            // Culture filters
            if (state.availableCultures.isNotEmpty()) {
                FilterSection(title = "Culture") {
                    CultureFilterChips(
                        currentFilter = state.currentFilter,
                        onFilterChange = onFilterChange,
                        availableCultures = state.availableCultures
                    )
                }
            }

            // Season filters
            if (state.availableSeasons.isNotEmpty()) {
                FilterSection(title = "Seasons") {
                    SeasonFilterChips(
                        currentFilter = state.currentFilter,
                        onFilterChange = onFilterChange,
                        availableSeasons = state.availableSeasons
                    )
                }
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
            modifier = Modifier.padding(bottom = 8.dp)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
        Spacer(modifier = Modifier.height(16.dp))
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
                style = MaterialTheme.typography.titleLarge,
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
                    isDead = if (currentFilter.isDead == true) null else true
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
                    isDead = if (currentFilter.isDead == false) null else false
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
                    hasAppearances = if (currentFilter.hasAppearances == true) null else true
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
                    gender = if (currentFilter.gender == "Male") null else "Male"
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
                    gender = if (currentFilter.gender == "Female") null else "Female"
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
