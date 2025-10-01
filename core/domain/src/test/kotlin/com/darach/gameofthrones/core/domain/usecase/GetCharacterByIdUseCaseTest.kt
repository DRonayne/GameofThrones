package com.darach.gameofthrones.core.domain.usecase

import com.darach.gameofthrones.core.domain.repository.CharacterRepository
import com.darach.gameofthrones.core.model.Character
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class GetCharacterByIdUseCaseTest {

    private lateinit var repository: CharacterRepository
    private lateinit var useCase: GetCharacterByIdUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetCharacterByIdUseCase(repository)
    }

    @Test
    fun `invoke should return character when found`() = runTest {
        // Given
        val characterId = "1"
        val character = createTestCharacter(characterId, "Jon Snow")
        every { repository.observeCharacter(characterId) } returns flowOf(character)

        // When
        val result = useCase.invoke(characterId).first()

        // Then
        assertEquals(character, result)
        verify { repository.observeCharacter(characterId) }
    }

    @Test
    fun `invoke should return null when character not found`() = runTest {
        // Given
        val characterId = "999"
        every { repository.observeCharacter(characterId) } returns flowOf(null)

        // When
        val result = useCase.invoke(characterId).first()

        // Then
        assertNull(result)
        verify { repository.observeCharacter(characterId) }
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
