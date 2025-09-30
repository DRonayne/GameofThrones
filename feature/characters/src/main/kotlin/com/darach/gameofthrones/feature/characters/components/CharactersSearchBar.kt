package com.darach.gameofthrones.feature.characters.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharactersSearchBar(
    query: String,
    searchHistory: List<String>,
    callbacks: SearchBarCallbacks,
    modifier: Modifier = Modifier
) {
    var isActive by remember { mutableStateOf(false) }

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = callbacks.onQueryChange,
                onSearch = { searchQuery ->
                    callbacks.onSearch(searchQuery)
                    isActive = false
                },
                expanded = isActive,
                onExpandedChange = { isActive = it },
                placeholder = { Text("Search characters, cultures, aliases...") },
                leadingIcon = { SearchLeadingIcon() },
                trailingIcon = {
                    SearchTrailingIcon(
                        query = query,
                        onClear = {
                            callbacks.onClearSearch()
                            isActive = false
                        }
                    )
                }
            )
        },
        expanded = isActive,
        onExpandedChange = { isActive = it },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        SearchHistoryContent(
            searchHistory = searchHistory,
            onHistoryItemClick = { item ->
                callbacks.onQueryChange(item)
                callbacks.onSearch(item)
                isActive = false
            }
        )
    }
}

@Composable
private fun SearchLeadingIcon() {
    Icon(
        imageVector = Icons.Default.Search,
        contentDescription = "Search"
    )
}

@Composable
private fun SearchTrailingIcon(query: String, onClear: () -> Unit) {
    if (query.isNotEmpty()) {
        IconButton(onClick = onClear) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Clear search"
            )
        }
    }
}

@Composable
private fun SearchHistoryContent(
    searchHistory: List<String>,
    onHistoryItemClick: (String) -> Unit
) {
    if (searchHistory.isNotEmpty()) {
        LazyColumn {
            items(searchHistory) { historyItem ->
                ListItem(
                    headlineContent = { Text(historyItem) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .clickable { onHistoryItemClick(historyItem) }
                        .fillMaxWidth()
                )
            }
        }
    }
}
