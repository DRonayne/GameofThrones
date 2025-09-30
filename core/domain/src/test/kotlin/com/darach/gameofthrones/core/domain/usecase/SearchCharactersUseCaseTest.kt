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
import org.junit.Before
import org.junit.Test

class SearchCharactersUseCaseTest {

    private lateinit var repository: CharacterRepository
    private lateinit var useCase: SearchCharactersUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = SearchCharactersUseCase(repository)
    }

    @Test
    fun `invoke should return matching characters`() = runTest {
        // Given
        val query = "Jon"
        val characters = listOf(
            createTestCharacter("1", "Jon Snow"),
            createTestCharacter("2", "Jon Arryn")
        )
        every { repository.searchCharacters(query) } returns flowOf(characters)

        // When
        val result = useCase.invoke(query).first()

        // Then
        assertEquals(characters, result)
        verify { repository.searchCharacters(query) }
    }

    @Test
    fun `invoke should return empty list when no matches found`() = runTest {
        // Given
        val query = "Unknown"
        every { repository.searchCharacters(query) } returns flowOf(emptyList())

        // When
        val result = useCase.invoke(query).first()

        // Then
        assertEquals(emptyList<Character>(), result)
        verify { repository.searchCharacters(query) }
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
