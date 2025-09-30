package com.darach.gameofthrones.feature.characters.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Sort
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
            imageVector = Icons.Default.Sort,
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
    SortOption.FAVORITE_FIRST -> "Favorites First"
}
