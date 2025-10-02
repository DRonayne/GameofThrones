package com.darach.gameofthrones.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darach.gameofthrones.core.analytics.AnalyticsService
import com.darach.gameofthrones.core.domain.usecase.GetFavoritesUseCase
import com.darach.gameofthrones.core.domain.usecase.ToggleFavoriteUseCase
import com.darach.gameofthrones.core.model.Character
import com.darach.gameofthrones.feature.favorites.FavoritesIntent
import com.darach.gameofthrones.feature.favorites.FavoritesState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val analyticsService: AnalyticsService
) : ViewModel() {

    private val selectedIds = MutableStateFlow<Set<String>>(emptySet())
    private val isLoading = MutableStateFlow(true)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val snackbarMessage = MutableStateFlow<String?>(null)
    private val isSelectionMode = MutableStateFlow(false)

    val state: StateFlow<FavoritesState> = combine(
        getFavoritesUseCase().catch { throwable ->
            errorMessage.value = throwable.message ?: "Failed to load favorites"
            emit(emptyList())
        },
        selectedIds,
        isLoading,
        errorMessage,
        snackbarMessage,
        isSelectionMode
    ) { flows ->
        @Suppress("UNCHECKED_CAST")
        val favorites = flows[0] as List<Character>

        @Suppress("UNCHECKED_CAST")
        val selected = flows[1] as Set<String>
        val loading = flows[2] as Boolean
        val error = flows[3] as String?
        val snackbar = flows[4] as String?
        val selectionMode = flows[5] as Boolean

        FavoritesState(
            favorites = favorites,
            isLoading = loading,
            error = error,
            isEmpty = favorites.isEmpty(),
            selectedIds = selected,
            snackbarMessage = snackbar,
            isSelectionMode = selectionMode
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FavoritesState(isLoading = true)
    )

    init {
        analyticsService.logScreenView(
            screenName = "Favorites",
            screenClass = "FavoritesScreen"
        )
        loadFavorites()
    }

    fun handleIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.LoadFavorites -> loadFavorites()
            is FavoritesIntent.ToggleSelection -> toggleSelection(intent.characterId)
            is FavoritesIntent.RemoveSelected -> removeSelected()
            is FavoritesIntent.CompareSelected -> validateAndPrepareComparison()
            is FavoritesIntent.ClearSnackbar -> clearSnackbar()
            is FavoritesIntent.ClearSelection -> clearSelection()
            is FavoritesIntent.EnterSelectionMode -> enterSelectionMode(intent.initialCharacterId)
            is FavoritesIntent.ExitSelectionMode -> exitSelectionMode()
            is FavoritesIntent.OnCardClick -> handleCardClick(intent.characterId)
            is FavoritesIntent.SelectAll -> selectAll()
            is FavoritesIntent.DeselectAll -> deselectAll()
        }
    }

    private fun loadFavorites() {
        isLoading.value = false
    }

    private fun toggleSelection(characterId: String) {
        val current = selectedIds.value.toMutableSet()
        if (current.contains(characterId)) {
            current.remove(characterId)
        } else {
            current.add(characterId)
        }
        selectedIds.value = current
    }

    private fun removeSelected() {
        viewModelScope.launch {
            selectedIds.value.forEach { id ->
                toggleFavoriteUseCase(id, false)
            }
            selectedIds.value = emptySet()
        }
    }

    private fun validateAndPrepareComparison() {
        when (selectedIds.value.size) {
            0 -> snackbarMessage.value = "Select 2 characters to compare"
            1 -> snackbarMessage.value = "Select 2 characters to compare"
            2 -> {
                // Validation passed, navigation will be handled by the UI
            }
            else -> snackbarMessage.value = "You can only select 2 characters to compare"
        }
    }

    private fun clearSnackbar() {
        snackbarMessage.value = null
    }

    private fun clearSelection() {
        selectedIds.value = emptySet()
    }

    private fun enterSelectionMode(initialCharacterId: String) {
        isSelectionMode.value = true
        selectedIds.value = setOf(initialCharacterId)
    }

    private fun exitSelectionMode() {
        isSelectionMode.value = false
        selectedIds.value = emptySet()
    }

    private fun handleCardClick(characterId: String) {
        if (isSelectionMode.value) {
            toggleSelection(characterId)
        } else {
            enterSelectionMode(characterId)
        }
    }

    private fun selectAll() {
        val currentFavorites = state.value.favorites
        selectedIds.value = currentFavorites.map { it.id }.toSet()
    }

    private fun deselectAll() {
        selectedIds.value = emptySet()
    }
}
