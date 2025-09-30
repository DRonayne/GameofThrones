package com.darach.gameofthrones.feature.characters.components

import com.darach.gameofthrones.core.domain.usecase.CharacterFilter

data class FilterBottomSheetState(
    val currentFilter: CharacterFilter,
    val availableCultures: List<String> = emptyList(),
    val availableSeasons: List<Int> = emptyList()
)
