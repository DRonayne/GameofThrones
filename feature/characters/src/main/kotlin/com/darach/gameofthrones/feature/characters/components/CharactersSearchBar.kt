package com.darach.gameofthrones.feature.characters.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharactersSearchBar(
    query: String,
    @Suppress("UnusedParameter") searchHistory: List<String>,
    callbacks: SearchBarCallbacks,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = callbacks.onQueryChange,
                onSearch = {
                    callbacks.onSearch(query)
                    keyboardController?.hide()
                    focusManager.clearFocus()
                },
                expanded = false,
                onExpandedChange = {},
                placeholder = { Text("Search characters") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = callbacks.onClearSearch) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                }
            )
        },
        expanded = false,
        onExpandedChange = {},
        modifier = modifier.fillMaxWidth(),
        content = {}
    )
}

// Previews
@androidx.compose.ui.tooling.preview.Preview(
    name = "Search Bar - Empty",
    showBackground = true
)
@Composable
private fun CharactersSearchBarEmptyPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        CharactersSearchBar(
            query = "",
            searchHistory = emptyList(),
            callbacks = SearchBarCallbacks(
                onQueryChange = {},
                onSearch = {},
                onClearSearch = {}
            )
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Search Bar - With Query",
    showBackground = true
)
@Composable
private fun CharactersSearchBarWithQueryPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        CharactersSearchBar(
            query = "Jon Snow",
            searchHistory = emptyList(),
            callbacks = SearchBarCallbacks(
                onQueryChange = {},
                onSearch = {},
                onClearSearch = {}
            )
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Search Bar - Dark Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun CharactersSearchBarDarkPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        CharactersSearchBar(
            query = "Arya Stark",
            searchHistory = emptyList(),
            callbacks = SearchBarCallbacks(
                onQueryChange = {},
                onSearch = {},
                onClearSearch = {}
            )
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Search Bar - Tablet",
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
private fun CharactersSearchBarTabletPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        CharactersSearchBar(
            query = "Daenerys Targaryen",
            searchHistory = emptyList(),
            callbacks = SearchBarCallbacks(
                onQueryChange = {},
                onSearch = {},
                onClearSearch = {}
            )
        )
    }
}
