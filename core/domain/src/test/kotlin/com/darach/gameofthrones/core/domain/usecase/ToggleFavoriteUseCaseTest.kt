package com.darach.gameofthrones.core.domain.usecase

import com.darach.gameofthrones.core.domain.repository.CharacterRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ToggleFavoriteUseCaseTest {

    private lateinit var repository: CharacterRepository
    private lateinit var useCase: ToggleFavoriteUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = ToggleFavoriteUseCase(repository)
    }

    @Test
    fun `invoke should update favorite status to true`() = runTest {
        // Given
        val characterId = "1"
        coEvery { repository.updateFavorite(characterId, true) } returns Unit

        // When
        useCase.invoke(characterId, true)

        // Then
        coVerify { repository.updateFavorite(characterId, true) }
    }

    @Test
    fun `invoke should update favorite status to false`() = runTest {
        // Given
        val characterId = "1"
        coEvery { repository.updateFavorite(characterId, false) } returns Unit

        // When
        useCase.invoke(characterId, false)

        // Then
        coVerify { repository.updateFavorite(characterId, false) }
    }
}
