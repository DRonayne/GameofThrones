package com.darach.gameofthrones.feature.characters.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darach.gameofthrones.core.domain.usecase.FilterCharactersUseCase
import com.darach.gameofthrones.core.domain.usecase.GetCharactersUseCase
import com.darach.gameofthrones.core.domain.usecase.RefreshCharactersUseCase
import com.darach.gameofthrones.core.domain.usecase.SearchCharactersUseCase
import com.darach.gameofthrones.core.domain.usecase.SortCharactersUseCase
import com.darach.gameofthrones.core.domain.usecase.ToggleFavoriteUseCase
import com.darach.gameofthrones.feature.characters.CharactersIntent
import com.darach.gameofthrones.feature.characters.CharactersState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase,
    private val searchCharactersUseCase: SearchCharactersUseCase,
    private val filterCharactersUseCase: FilterCharactersUseCase,
    private val sortCharactersUseCase: SortCharactersUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val refreshCharactersUseCase: RefreshCharactersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CharactersState())
    val state: StateFlow<CharactersState> = _state.asStateFlow()

    private val searchQueryFlow = MutableSharedFlow<String>(replay = 1)

    init {
        loadCharacters()
        setupSearchDebounce()
    }

    fun handleIntent(intent: CharactersIntent) {
        when (intent) {
            is CharactersIntent.LoadCharacters -> loadCharacters()
            is CharactersIntent.RefreshCharacters -> refreshCharacters()
            is CharactersIntent.SearchCharacters -> searchCharacters(intent.query)
            is CharactersIntent.ClearSearch -> clearSearch()
            is CharactersIntent.FilterCharacters -> filterCharacters(intent.filter)
            is CharactersIntent.SortCharacters -> sortCharacters(intent.sortOption)
            is CharactersIntent.ToggleFavorite -> toggleFavorite(intent.characterId)
            is CharactersIntent.RetryLoad -> loadCharacters()
        }
    }

    private fun loadCharacters() {
        _state.update { it.copy(isLoading = true, error = null) }

        getCharactersUseCase(forceRefresh = false)
            .onEach { result ->
                result.fold(
                    onSuccess = { characters ->
                        _state.update {
                            it.copy(
                                characters = characters,
                                filteredCharacters = applyFiltersAndSort(characters),
                                isLoading = false,
                                isEmpty = characters.isEmpty()
                            )
                        }
                    },
                    onFailure = { error ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = error.message ?: "Unknown error occurred"
                            )
                        }
                    }
                )
            }
            .catch { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "Unknown error occurred"
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun refreshCharacters() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, error = null) }

            refreshCharactersUseCase()
                .fold(
                    onSuccess = {
                        _state.update { it.copy(isRefreshing = false) }
                    },
                    onFailure = { error ->
                        _state.update {
                            it.copy(
                                isRefreshing = false,
                                error = error.message ?: "Failed to refresh"
                            )
                        }
                    }
                )
        }
    }

    private fun setupSearchDebounce() {
        searchQueryFlow
            .debounce(SEARCH_DEBOUNCE_MS)
            .distinctUntilChanged()
            .map { query ->
                if (query.isBlank()) {
                    _state.value.characters
                } else {
                    emptyList()
                }
            }
            .onEach { characters ->
                val currentState = _state.value
                val query = currentState.searchQuery

                if (query.isNotBlank()) {
                    searchCharactersUseCase(query)
                        .collect { searchResults ->
                            _state.update {
                                it.copy(
                                    filteredCharacters = applyFiltersAndSort(searchResults),
                                    searchHistory = addToSearchHistory(query)
                                )
                            }
                        }
                } else {
                    _state.update {
                        it.copy(filteredCharacters = applyFiltersAndSort(characters))
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun searchCharacters(query: String) {
        _state.update { it.copy(searchQuery = query) }
        viewModelScope.launch {
            searchQueryFlow.emit(query)
        }
    }

    private fun clearSearch() {
        _state.update {
            it.copy(
                searchQuery = "",
                filteredCharacters = applyFiltersAndSort(it.characters)
            )
        }
    }

    private fun filterCharacters(
        filter: com.darach.gameofthrones.core.domain.usecase.CharacterFilter
    ) {
        _state.update {
            it.copy(
                filter = filter,
                filteredCharacters = applyFiltersAndSort(it.characters)
            )
        }
    }

    private fun sortCharacters(
        sortOption: com.darach.gameofthrones.core.domain.usecase.SortOption
    ) {
        _state.update {
            it.copy(
                sortOption = sortOption,
                filteredCharacters = applyFiltersAndSort(it.characters)
            )
        }
    }

    private fun toggleFavorite(characterId: String) {
        viewModelScope.launch {
            val character = _state.value.characters.find { it.id == characterId } ?: return@launch
            toggleFavoriteUseCase(characterId, !character.isFavorite)
        }
    }

    private fun applyFiltersAndSort(
        characters: List<com.darach.gameofthrones.core.domain.model.Character>
    ): List<com.darach.gameofthrones.core.domain.model.Character> {
        val filtered = filterCharactersUseCase(characters, _state.value.filter)
        return sortCharactersUseCase(filtered, _state.value.sortOption)
    }

    private fun addToSearchHistory(query: String): List<String> {
        val currentHistory = _state.value.searchHistory.toMutableList()
        if (query.isNotBlank() && !currentHistory.contains(query)) {
            currentHistory.add(0, query)
            if (currentHistory.size > MAX_SEARCH_HISTORY) {
                currentHistory.removeAt(currentHistory.size - 1)
            }
        }
        return currentHistory
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 300L
        private const val MAX_SEARCH_HISTORY = 10
    }
}
