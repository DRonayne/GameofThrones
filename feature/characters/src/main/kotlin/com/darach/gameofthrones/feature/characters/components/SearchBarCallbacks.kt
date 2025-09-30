package com.darach.gameofthrones.feature.characters.components

data class SearchBarCallbacks(
    val onQueryChange: (String) -> Unit,
    val onSearch: (String) -> Unit,
    val onClearSearch: () -> Unit
)
