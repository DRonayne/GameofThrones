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
import org.junit.Before
import org.junit.Test

class GetFavoritesUseCaseTest {

    private lateinit var repository: CharacterRepository
    private lateinit var useCase: GetFavoritesUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetFavoritesUseCase(repository)
    }

    @Test
    fun `invoke should return favorite characters`() = runTest {
        // Given
        val favorites = listOf(
            createTestCharacter("1", "Jon Snow", isFavorite = true),
            createTestCharacter("2", "Arya Stark", isFavorite = true)
        )
        every { repository.observeFavoriteCharacters() } returns flowOf(favorites)

        // When
        val result = useCase.invoke().first()

        // Then
        assertEquals(favorites, result)
        verify { repository.observeFavoriteCharacters() }
    }

    @Test
    fun `invoke should return empty list when no favorites`() = runTest {
        // Given
        every { repository.observeFavoriteCharacters() } returns flowOf(emptyList())

        // When
        val result = useCase.invoke().first()

        // Then
        assertEquals(emptyList<Character>(), result)
        verify { repository.observeFavoriteCharacters() }
    }

    private fun createTestCharacter(id: String, name: String, isFavorite: Boolean = false) =
        Character(
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
            isFavorite = isFavorite
        )
}
