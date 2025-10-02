package com.darach.gameofthrones.feature.characters.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.darach.gameofthrones.core.domain.usecase.SortOption
import com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme

@Composable
fun SortOptionsMenu(
    currentSortOption: SortOption,
    onSortOptionChange: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        SortMenuButton(
            onExpandChange = { expanded = it }
        )

        SortMenuDropdown(
            expanded = expanded,
            currentSortOption = currentSortOption,
            onDismiss = { expanded = false },
            onSortOptionChange = { sortOption ->
                onSortOptionChange(sortOption)
                expanded = false
            }
        )
    }
}

@Composable
private fun SortMenuButton(onExpandChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = { onExpandChange(true) },
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Sort,
            contentDescription = "Sort options"
        )
    }
}

@Composable
private fun SortMenuDropdown(
    expanded: Boolean,
    currentSortOption: SortOption,
    onDismiss: () -> Unit,
    onSortOptionChange: (SortOption) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        SortOption.entries.forEach { sortOption ->
            DropdownMenuItem(
                text = { Text(getSortOptionLabel(sortOption)) },
                onClick = { onSortOptionChange(sortOption) },
                leadingIcon = {
                    if (currentSortOption == sortOption) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )
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

// Previews
@androidx.compose.ui.tooling.preview.Preview(
    name = "Sort Menu - Name Ascending",
    showBackground = true
)
@Composable
private fun SortOptionsMenuNameAscPreview() {
    GameOfThronesTheme {
        SortOptionsMenu(
            currentSortOption = SortOption.NAME_ASC,
            onSortOptionChange = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Sort Menu - Favorite First",
    showBackground = true
)
@Composable
private fun SortOptionsMenuFavoritePreview() {
    GameOfThronesTheme {
        SortOptionsMenu(
            currentSortOption = SortOption.FAVORITE_FIRST,
            onSortOptionChange = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Sort Menu - Dark Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun SortOptionsMenuDarkPreview() {
    GameOfThronesTheme {
        SortOptionsMenu(
            currentSortOption = SortOption.SEASONS_COUNT_DESC,
            onSortOptionChange = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Sort Menu - Tablet",
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
private fun SortOptionsMenuTabletPreview() {
    GameOfThronesTheme {
        SortOptionsMenu(
            currentSortOption = SortOption.CULTURE_ASC,
            onSortOptionChange = {}
        )
    }
}
