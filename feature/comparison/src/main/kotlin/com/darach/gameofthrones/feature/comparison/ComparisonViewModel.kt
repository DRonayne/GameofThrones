package com.darach.gameofthrones.feature.comparison

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darach.gameofthrones.core.analytics.AnalyticsEvents
import com.darach.gameofthrones.core.analytics.AnalyticsParams
import com.darach.gameofthrones.core.analytics.AnalyticsService
import com.darach.gameofthrones.core.domain.usecase.GetCharacterByIdUseCase
import com.darach.gameofthrones.feature.comparison.ComparisonDiffCalculator
import com.darach.gameofthrones.feature.comparison.ComparisonState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for comparison feature following MVI pattern.
 * Manages comparison result calculation for two characters.
 */
@HiltViewModel
class ComparisonViewModel @Inject constructor(
    private val diffCalculator: ComparisonDiffCalculator,
    private val getCharacterByIdUseCase: GetCharacterByIdUseCase,
    private val analyticsService: AnalyticsService
) : ViewModel() {

    private val _state = MutableStateFlow(ComparisonState())
    val state: StateFlow<ComparisonState> = _state.asStateFlow()

    init {
        analyticsService.logScreenView(
            screenName = "Comparison",
            screenClass = "ComparisonScreen"
        )
    }

    /**
     * Load and compare two characters by their IDs
     */
    fun compareCharacters(characterId1: String, characterId2: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                combine(
                    getCharacterByIdUseCase(characterId1),
                    getCharacterByIdUseCase(characterId2)
                ) { char1, char2 ->
                    Pair(char1, char2)
                }.collect { (char1, char2) ->
                    if (char1 != null && char2 != null) {
                        val characters = listOf(char1, char2)
                        val result = diffCalculator.calculate(characters)
                        _state.update {
                            it.copy(
                                comparisonResult = result,
                                selectedCharacters = characters,
                                isLoading = false,
                                error = null
                            )
                        }
                        analyticsService.logEvent(
                            AnalyticsEvents.COMPARISON_STARTED,
                            mapOf(AnalyticsParams.COMPARISON_COUNT to 2)
                        )
                    } else {
                        _state.update {
                            it.copy(
                                error = "Could not load characters for comparison",
                                isLoading = false
                            )
                        }
                    }
                }
            } catch (e: IllegalArgumentException) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Invalid character comparison",
                        isLoading = false
                    )
                }
            } catch (e: IllegalStateException) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Failed to compare characters",
                        isLoading = false
                    )
                }
            }
        }
    }
}
