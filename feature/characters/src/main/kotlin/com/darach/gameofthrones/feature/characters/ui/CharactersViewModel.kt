package com.darach.gameofthrones.feature.characters.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darach.gameofthrones.core.domain.model.Character
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
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

    private val baseCharacters = MutableStateFlow<List<Character>>(emptyList())
    private val searchResultCharacters = MutableStateFlow<List<Character>>(emptyList())
    private val filterFlow =
        MutableStateFlow(com.darach.gameofthrones.core.domain.usecase.CharacterFilter())
    private val sortOptionFlow =
        MutableStateFlow(com.darach.gameofthrones.core.domain.usecase.SortOption.NAME_ASC)
    private val searchQueryStateFlow = MutableStateFlow("")
    private val isSearchActive = MutableStateFlow(false)
    private val isLoading = MutableStateFlow(false)
    private val isRefreshing = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val searchHistory = MutableStateFlow<List<String>>(emptyList())
    private val searchQuerySharedFlow = MutableSharedFlow<String>(replay = 1)

    private val filteredCharacters = combine(
        baseCharacters,
        searchResultCharacters,
        isSearchActive,
        filterFlow,
        sortOptionFlow
    ) { baseChars, searchChars, searchActive, filter, sortOption ->
        val sourceChars = if (searchActive && searchChars.isNotEmpty()) searchChars else baseChars
        val filtered = filterCharactersUseCase(sourceChars, filter)
        sortCharactersUseCase(filtered, sortOption)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val state: StateFlow<CharactersState> = combine(
        combine(
            baseCharacters,
            filteredCharacters,
            filterFlow,
            sortOptionFlow,
            searchQueryStateFlow
        ) {
                baseChars,
                filtered,
                filter,
                sortOption,
                searchQuery
            ->
            Quintet(baseChars, filtered, filter, sortOption, searchQuery)
        },
        combine(searchHistory, isLoading, isRefreshing, errorMessage) {
                history,
                loading,
                refreshing,
                error
            ->
            Quartet(history, loading, refreshing, error)
        }
    ) { data1, data2 ->
        CharactersState(
            characters = data1.first,
            filteredCharacters = data1.second,
            filter = data1.third,
            sortOption = data1.fourth,
            searchQuery = data1.fifth,
            searchHistory = data2.first,
            isLoading = data2.second,
            isRefreshing = data2.third,
            error = data2.fourth,
            isEmpty = data1.first.isEmpty(),
            availableCultures = extractUniqueCultures(data1.first),
            availableSeasons = extractUniqueSeasons(data1.first)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = CharactersState()
    )

    private data class Quintet<A, B, C, D, E>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D,
        val fifth: E
    )

    private data class Quartet<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

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
        isLoading.value = true
        errorMessage.value = null

        getCharactersUseCase(forceRefresh = false)
            .onEach { result ->
                result.fold(
                    onSuccess = { characters ->
                        baseCharacters.value = characters
                        isLoading.value = false
                    },
                    onFailure = { error ->
                        isLoading.value = false
                        errorMessage.value = error.message ?: "Unknown error occurred"
                        Log.e(TAG, "Failed to load characters", error)
                    }
                )
            }
            .catch { error ->
                isLoading.value = false
                errorMessage.value = error.message ?: "Unknown error occurred"
                Log.e(TAG, "Error in characters flow", error)
            }
            .launchIn(viewModelScope)
    }

    private fun refreshCharacters() {
        viewModelScope.launch {
            isRefreshing.value = true
            errorMessage.value = null

            refreshCharactersUseCase()
                .fold(
                    onSuccess = {
                        isRefreshing.value = false
                    },
                    onFailure = { error ->
                        isRefreshing.value = false
                        errorMessage.value = error.message ?: "Failed to refresh"
                        Log.e(TAG, "Failed to refresh characters", error)
                    }
                )
        }
    }

    private fun setupSearchDebounce() {
        searchQuerySharedFlow
            .debounce(SEARCH_DEBOUNCE_MS)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                if (query.isBlank()) {
                    isSearchActive.value = false
                    flowOf(baseCharacters.value)
                } else {
                    isSearchActive.value = true
                    searchCharactersUseCase(query)
                }
            }
            .onEach { searchResults ->
                val query = searchQueryStateFlow.value
                if (isSearchActive.value) {
                    searchResultCharacters.value = searchResults
                    if (query.isNotBlank()) {
                        searchHistory.value = addToSearchHistory(query)
                    }
                }
            }
            .catch { error ->
                errorMessage.value = error.message ?: "Search failed"
                Log.e(TAG, "Search error", error)
            }
            .launchIn(viewModelScope)
    }

    private fun searchCharacters(query: String) {
        searchQueryStateFlow.value = query
        viewModelScope.launch {
            searchQuerySharedFlow.emit(query)
        }
    }

    private fun clearSearch() {
        searchQueryStateFlow.value = ""
        isSearchActive.value = false
        searchResultCharacters.value = emptyList()
    }

    private fun filterCharacters(
        filter: com.darach.gameofthrones.core.domain.usecase.CharacterFilter
    ) {
        filterFlow.value = filter
    }

    private fun sortCharacters(
        sortOption: com.darach.gameofthrones.core.domain.usecase.SortOption
    ) {
        sortOptionFlow.value = sortOption
    }

    private fun toggleFavorite(characterId: String) {
        viewModelScope.launch {
            val character = baseCharacters.value.find { it.id == characterId } ?: return@launch
            toggleFavoriteUseCase(characterId, !character.isFavorite)
        }
    }

    private fun addToSearchHistory(query: String): List<String> {
        val currentHistory = searchHistory.value.toMutableList()
        if (query.isNotBlank() && !currentHistory.contains(query)) {
            currentHistory.add(0, query)
            if (currentHistory.size > MAX_SEARCH_HISTORY) {
                currentHistory.removeAt(currentHistory.size - 1)
            }
        }
        return currentHistory
    }

    private fun extractUniqueCultures(characters: List<Character>): List<String> =
        characters.mapNotNull {
            it.culture.takeIf { culture -> culture.isNotBlank() }
        }.distinct().sorted()

    private fun extractUniqueSeasons(characters: List<Character>): List<Int> =
        characters.flatMap { it.tvSeriesSeasons }.distinct().sorted()

    companion object {
        private const val TAG = "CharactersViewModel"
        private const val SEARCH_DEBOUNCE_MS = 300L
        private const val MAX_SEARCH_HISTORY = 10
    }
}
