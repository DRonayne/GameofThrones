package com.darach.gameofthrones.feature.characters.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
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
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            FilterContent(
                state = state,
                onFilterChange = onFilterChange,
                scrollState = scrollState
            )
            CloseButton(
                onDismiss = onDismiss,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun FilterContent(
    state: FilterBottomSheetState,
    onFilterChange: (CharacterFilter) -> Unit,
    scrollState: androidx.compose.foundation.ScrollState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        FilterHeader(currentFilter = state.currentFilter, onFilterChange = onFilterChange)

        Spacer(modifier = Modifier.height(16.dp))

        FilterSection(
            title = stringResource(com.darach.gameofthrones.core.ui.R.string.filter_status)
        ) {
            FavoritesFilterChip(state.currentFilter, onFilterChange)
            DeathFilterChips(state.currentFilter, onFilterChange)
            AppearancesFilterChip(state.currentFilter, onFilterChange)
        }

        FilterSection(
            title = stringResource(com.darach.gameofthrones.core.ui.R.string.filter_gender)
        ) {
            GenderFilterChips(state.currentFilter, onFilterChange)
        }

        if (state.availableCultures.isNotEmpty()) {
            FilterSection(
                title = stringResource(com.darach.gameofthrones.core.ui.R.string.filter_culture)
            ) {
                CultureFilterChips(state.currentFilter, onFilterChange, state.availableCultures)
            }
        }

        if (state.availableSeasons.isNotEmpty()) {
            FilterSection(
                title = stringResource(com.darach.gameofthrones.core.ui.R.string.filter_seasons)
            ) {
                SeasonFilterChips(state.currentFilter, onFilterChange, state.availableSeasons)
            }
        }
    }
}

@Composable
private fun CloseButton(onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Button(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(stringResource(com.darach.gameofthrones.core.ui.R.string.close))
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
    val performHaptic = com.darach.gameofthrones.core.ui.haptics.rememberHapticFeedback()
    val filterCount = currentFilter.activeFilterCount()
    val badgeScale by animateFloatAsState(
        targetValue = if (filterCount > 0) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "Filter badge scale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BadgedBox(
            badge = {
                if (filterCount > 0) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.scale(badgeScale)
                    ) {
                        Text(
                            text = filterCount.toString(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        ) {
            Text(
                text = stringResource(com.darach.gameofthrones.core.ui.R.string.filters) + " ",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (currentFilter.isActive()) {
            TextButton(
                onClick = {
                    performHaptic()
                    onFilterChange(CharacterFilter())
                }
            ) {
                Text(
                    text = stringResource(com.darach.gameofthrones.core.ui.R.string.clear_all),
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
    val performHaptic = com.darach.gameofthrones.core.ui.haptics.rememberHapticFeedback()
    SelectableFilterChip(
        selected = currentFilter.onlyFavorites,
        onClick = {
            performHaptic()
            onFilterChange(currentFilter.copy(onlyFavorites = !currentFilter.onlyFavorites))
        },
        label = stringResource(com.darach.gameofthrones.core.ui.R.string.filter_favorites)
    )
}

@Composable
private fun DeathFilterChips(
    currentFilter: CharacterFilter,
    onFilterChange: (CharacterFilter) -> Unit
) {
    val performHaptic = com.darach.gameofthrones.core.ui.haptics.rememberHapticFeedback()
    SelectableFilterChip(
        selected = currentFilter.isDead == true,
        onClick = {
            performHaptic()
            onFilterChange(
                currentFilter.copy(
                    isDead = if (currentFilter.isDead == true) null else true
                )
            )
        },
        label = stringResource(com.darach.gameofthrones.core.ui.R.string.filter_deceased)
    )

    SelectableFilterChip(
        selected = currentFilter.isDead == false,
        onClick = {
            performHaptic()
            onFilterChange(
                currentFilter.copy(
                    isDead = if (currentFilter.isDead == false) null else false
                )
            )
        },
        label = stringResource(com.darach.gameofthrones.core.ui.R.string.filter_alive)
    )
}

@Composable
private fun AppearancesFilterChip(
    currentFilter: CharacterFilter,
    onFilterChange: (CharacterFilter) -> Unit
) {
    val performHaptic = com.darach.gameofthrones.core.ui.haptics.rememberHapticFeedback()
    SelectableFilterChip(
        selected = currentFilter.hasAppearances == true,
        onClick = {
            performHaptic()
            onFilterChange(
                currentFilter.copy(
                    hasAppearances = if (currentFilter.hasAppearances == true) null else true
                )
            )
        },
        label = stringResource(com.darach.gameofthrones.core.ui.R.string.filter_tv_appearances)
    )
}

@Composable
private fun GenderFilterChips(
    currentFilter: CharacterFilter,
    onFilterChange: (CharacterFilter) -> Unit
) {
    val performHaptic = com.darach.gameofthrones.core.ui.haptics.rememberHapticFeedback()
    SelectableFilterChip(
        selected = currentFilter.gender == "Male",
        onClick = {
            performHaptic()
            onFilterChange(
                currentFilter.copy(
                    gender = if (currentFilter.gender == "Male") null else "Male"
                )
            )
        },
        label = stringResource(com.darach.gameofthrones.core.ui.R.string.gender_male)
    )

    SelectableFilterChip(
        selected = currentFilter.gender == "Female",
        onClick = {
            performHaptic()
            onFilterChange(
                currentFilter.copy(
                    gender = if (currentFilter.gender == "Female") null else "Female"
                )
            )
        },
        label = stringResource(com.darach.gameofthrones.core.ui.R.string.gender_female)
    )
}

@Composable
private fun CultureFilterChips(
    currentFilter: CharacterFilter,
    onFilterChange: (CharacterFilter) -> Unit,
    availableCultures: List<String>
) {
    val performHaptic = com.darach.gameofthrones.core.ui.haptics.rememberHapticFeedback()
    availableCultures.take(MAX_CULTURE_CHIPS).forEach { culture ->
        SelectableFilterChip(
            selected = currentFilter.culture == culture,
            onClick = {
                performHaptic()
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
    val performHaptic = com.darach.gameofthrones.core.ui.haptics.rememberHapticFeedback()
    availableSeasons.forEach { season ->
        SelectableFilterChip(
            selected = currentFilter.seasons.contains(season),
            onClick = {
                performHaptic()
                val newSeasons = if (currentFilter.seasons.contains(season)) {
                    currentFilter.seasons - season
                } else {
                    currentFilter.seasons + season
                }
                onFilterChange(currentFilter.copy(seasons = newSeasons))
            },
            label = stringResource(
                com.darach.gameofthrones.core.ui.R.string.filter_season_label,
                season
            )
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
