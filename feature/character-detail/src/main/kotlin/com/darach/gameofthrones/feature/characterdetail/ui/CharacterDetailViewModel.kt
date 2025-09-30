package com.darach.gameofthrones.feature.characterdetail.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darach.gameofthrones.core.domain.usecase.GetCharacterByIdUseCase
import com.darach.gameofthrones.core.domain.usecase.ToggleFavoriteUseCase
import com.darach.gameofthrones.feature.characterdetail.CharacterDetailIntent
import com.darach.gameofthrones.feature.characterdetail.CharacterDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCharacterByIdUseCase: GetCharacterByIdUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val characterId: String = savedStateHandle.get<String>("characterId") ?: ""

    private val _state = MutableStateFlow(CharacterDetailState(isLoading = true))
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = CharacterDetailState(isLoading = true)
    )

    init {
        if (characterId.isNotEmpty()) {
            loadCharacter(characterId)
        } else {
            _state.update { it.copy(isLoading = false, error = "Invalid character ID") }
        }
    }

    fun handleIntent(intent: CharacterDetailIntent) {
        when (intent) {
            is CharacterDetailIntent.LoadCharacter -> loadCharacter(intent.characterId)
            is CharacterDetailIntent.ToggleFavorite -> toggleFavorite()
            is CharacterDetailIntent.RetryLoad -> loadCharacter(characterId)
        }
    }

    private fun loadCharacter(id: String) {
        _state.update { it.copy(isLoading = true, error = null) }

        getCharacterByIdUseCase(id)
            .onEach { character ->
                if (character != null) {
                    _state.update {
                        it.copy(
                            character = character,
                            isLoading = false,
                            error = null
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Character not found"
                        )
                    }
                }
            }
            .catch { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "Unknown error occurred"
                    )
                }
                Log.e(TAG, "Failed to load character", error)
            }
            .launchIn(viewModelScope)
    }

    private fun toggleFavorite() {
        val currentCharacter = _state.value.character ?: return
        viewModelScope.launch {
            toggleFavoriteUseCase(currentCharacter.id, !currentCharacter.isFavorite)
        }
    }

    companion object {
        private const val TAG = "CharacterDetailViewModel"
        private const val TIMEOUT_MILLIS = 5000L
    }
}
