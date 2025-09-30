package com.darach.gameofthrones.core.domain.usecase

import com.darach.gameofthrones.core.domain.model.Character
import com.darach.gameofthrones.core.domain.repository.CharacterRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetCharactersUseCaseTest {

    private lateinit var repository: CharacterRepository
    private lateinit var useCase: GetCharactersUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetCharactersUseCase(repository)
    }

    @Test
    fun `invoke should return characters from repository`() = runTest {
        // Given
        val characters = listOf(
            createTestCharacter("1", "Jon Snow"),
            createTestCharacter("2", "Arya Stark")
        )
        every { repository.observeCharacters(any()) } returns flowOf(Result.success(characters))

        // When
        val result = useCase.invoke().first()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(characters, result.getOrNull())
        verify { repository.observeCharacters(false) }
    }

    @Test
    fun `invoke with forceRefresh true should pass parameter to repository`() = runTest {
        // Given
        val characters = listOf(createTestCharacter("1", "Jon Snow"))
        every { repository.observeCharacters(true) } returns flowOf(Result.success(characters))

        // When
        val result = useCase.invoke(forceRefresh = true).first()

        // Then
        assertTrue(result.isSuccess)
        verify { repository.observeCharacters(true) }
    }

    @Test
    fun `invoke should return failure when repository fails`() = runTest {
        // Given
        val error = Exception("Network error")
        every { repository.observeCharacters(any()) } returns flowOf(Result.failure(error))

        // When
        val result = useCase.invoke().first()

        // Then
        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }

    private fun createTestCharacter(id: String, name: String) = Character(
        id = id,
        name = name,
        gender = "Male",
        culture = "Northmen",
        born = "",
        died = "",
        titles = emptyList(),
        aliases = emptyList(),
        father = "",
        mother = "",
        spouse = "",
        allegiances = emptyList(),
        books = emptyList(),
        povBooks = emptyList(),
        tvSeries = emptyList(),
        tvSeriesSeasons = emptyList(),
        playedBy = emptyList(),
        isFavorite = false
    )
}
