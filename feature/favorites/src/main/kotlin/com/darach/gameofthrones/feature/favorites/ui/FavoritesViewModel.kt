package com.darach.gameofthrones.feature.favorites.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darach.gameofthrones.core.domain.model.Character
import com.darach.gameofthrones.core.domain.usecase.GetFavoritesUseCase
import com.darach.gameofthrones.core.domain.usecase.ToggleFavoriteUseCase
import com.darach.gameofthrones.feature.favorites.FavoritesIntent
import com.darach.gameofthrones.feature.favorites.FavoritesState
import com.darach.gameofthrones.feature.favorites.ViewMode
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
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val viewMode = MutableStateFlow(ViewMode.GRID)
    private val isSelectionMode = MutableStateFlow(false)
    private val selectedIds = MutableStateFlow<Set<String>>(emptySet())
    private val isLoading = MutableStateFlow(true)
    private val errorMessage = MutableStateFlow<String?>(null)

    val state: StateFlow<FavoritesState> = combine(
        getFavoritesUseCase().catch { throwable ->
            errorMessage.value = throwable.message ?: "Failed to load favorites"
            emit(emptyList())
        },
        viewMode,
        isSelectionMode,
        selectedIds,
        isLoading,
        errorMessage
    ) { flows ->
        val favorites = flows[0] as List<*>
        val mode = flows[1] as ViewMode
        val selectionMode = flows[2] as Boolean
        val selected = flows[3] as Set<*>
        val loading = flows[4] as Boolean
        val error = flows[5] as String?

        FavoritesState(
            favorites = favorites as List<Character>,
            isLoading = loading,
            error = error,
            isEmpty = favorites.isEmpty(),
            viewMode = mode,
            isSelectionMode = selectionMode,
            selectedIds = selected as Set<String>
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FavoritesState(isLoading = true)
    )

    init {
        loadFavorites()
    }

    fun handleIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.LoadFavorites -> loadFavorites()
            is FavoritesIntent.ToggleViewMode -> toggleViewMode()
            is FavoritesIntent.ToggleSelectionMode -> toggleSelectionMode()
            is FavoritesIntent.ToggleSelection -> toggleSelection(intent.characterId)
            is FavoritesIntent.SelectAll -> selectAll()
            is FavoritesIntent.DeselectAll -> deselectAll()
            is FavoritesIntent.RemoveSelected -> removeSelected()
            is FavoritesIntent.RemoveFavorite -> removeFavorite(intent.characterId)
        }
    }

    private fun loadFavorites() {
        isLoading.value = false
    }

    private fun toggleViewMode() {
        viewMode.value = when (viewMode.value) {
            ViewMode.GRID -> ViewMode.LIST
            ViewMode.LIST -> ViewMode.GRID
        }
    }

    private fun toggleSelectionMode() {
        isSelectionMode.value = !isSelectionMode.value
        if (!isSelectionMode.value) {
            selectedIds.value = emptySet()
        }
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

    private fun selectAll() {
        selectedIds.value = state.value.favorites.map { it.id }.toSet()
    }

    private fun deselectAll() {
        selectedIds.value = emptySet()
    }

    private fun removeSelected() {
        viewModelScope.launch {
            selectedIds.value.forEach { id ->
                toggleFavoriteUseCase(id, false)
            }
            selectedIds.value = emptySet()
            isSelectionMode.value = false
        }
    }

    private fun removeFavorite(characterId: String) {
        viewModelScope.launch {
            toggleFavoriteUseCase(characterId, false)
        }
    }
}
