package com.darach.gameofthrones.core.domain.usecase

import com.darach.gameofthrones.core.domain.repository.CharacterRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RefreshCharactersUseCaseTest {

    private lateinit var repository: CharacterRepository
    private lateinit var useCase: RefreshCharactersUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = RefreshCharactersUseCase(repository)
    }

    @Test
    fun `invoke should return success when repository refreshes successfully`() = runTest {
        // Given
        coEvery { repository.refreshCharacters() } returns Result.success(Unit)

        // When
        val result = useCase.invoke()

        // Then
        assertTrue(result.isSuccess)
        coVerify { repository.refreshCharacters() }
    }

    @Test
    fun `invoke should return failure when repository fails to refresh`() = runTest {
        // Given
        val error = Exception("Network error")
        coEvery { repository.refreshCharacters() } returns Result.failure(error)

        // When
        val result = useCase.invoke()

        // Then
        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
        coVerify { repository.refreshCharacters() }
    }
}
