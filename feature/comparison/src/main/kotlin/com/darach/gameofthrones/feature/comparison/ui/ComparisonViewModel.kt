package com.darach.gameofthrones.feature.comparison.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darach.gameofthrones.core.domain.model.Character
import com.darach.gameofthrones.core.domain.usecase.GetFavoritesUseCase
import com.darach.gameofthrones.feature.comparison.ComparisonDiffCalculator
import com.darach.gameofthrones.feature.comparison.ComparisonIntent
import com.darach.gameofthrones.feature.comparison.ComparisonState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for comparison feature following MVI pattern.
 * Manages selection state and comparison result calculation.
 */
@HiltViewModel
class ComparisonViewModel @Inject constructor(
    private val diffCalculator: ComparisonDiffCalculator,
    private val getFavoritesUseCase: GetFavoritesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ComparisonState())
    val state: StateFlow<ComparisonState> = _state.asStateFlow()

    init {
        loadFavoriteCharacters()
    }

    private fun loadFavoriteCharacters() {
        viewModelScope.launch {
            getFavoritesUseCase().collect { favorites ->
                _state.update { it.copy(favoriteCharacters = favorites) }
            }
        }
    }

    fun handleIntent(intent: ComparisonIntent) {
        when (intent) {
            is ComparisonIntent.EnterSelectionMode -> enterSelectionMode()
            is ComparisonIntent.ExitSelectionMode -> exitSelectionMode()
            is ComparisonIntent.ToggleCharacterSelection -> toggleCharacterSelection(
                intent.character
            )
            is ComparisonIntent.ClearSelection -> clearSelection()
            is ComparisonIntent.StartComparison -> startComparison()
            is ComparisonIntent.ExitComparison -> exitComparison()
            is ComparisonIntent.SwitchCharacter -> switchCharacter(
                intent.oldCharacter,
                intent.newCharacter
            )
        }
    }

    private fun enterSelectionMode() {
        _state.update { it.copy(isSelectionMode = true) }
    }

    private fun exitSelectionMode() {
        _state.update {
            it.copy(
                isSelectionMode = false,
                selectedCharacters = emptyList()
            )
        }
    }

    private fun toggleCharacterSelection(character: Character) {
        _state.update { currentState ->
            val currentSelection = currentState.selectedCharacters
            val isSelected = currentSelection.any { it.id == character.id }

            val newSelection = if (isSelected) {
                currentSelection.filter { it.id != character.id }
            } else {
                if (currentSelection.size < ComparisonState.MAX_SELECTION_SIZE) {
                    currentSelection + character
                } else {
                    currentSelection
                }
            }

            currentState.copy(selectedCharacters = newSelection)
        }
    }

    private fun clearSelection() {
        _state.update { it.copy(selectedCharacters = emptyList()) }
    }

    private fun startComparison() {
        viewModelScope.launch {
            val selectedCharacters = _state.value.selectedCharacters

            if (selectedCharacters.size != ComparisonState.MAX_SELECTION_SIZE) {
                _state.update {
                    it.copy(error = "Please select 2 characters to compare")
                }
                return@launch
            }

            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val result = diffCalculator.calculate(selectedCharacters)
                _state.update {
                    it.copy(
                        comparisonResult = result,
                        isLoading = false
                    )
                }
            } catch (e: IllegalArgumentException) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Invalid comparison parameters",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun exitComparison() {
        _state.update {
            it.copy(
                comparisonResult = null,
                selectedCharacters = emptyList()
            )
        }
    }

    private fun switchCharacter(oldCharacter: Character, newCharacter: Character) {
        _state.update { currentState ->
            val updatedSelection = currentState.selectedCharacters.map {
                if (it.id == oldCharacter.id) newCharacter else it
            }
            currentState.copy(selectedCharacters = updatedSelection)
        }

        // Recalculate comparison with new character
        startComparison()
    }
}
