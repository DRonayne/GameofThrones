package com.darach.gameofthrones.core.domain.usecase

import com.darach.gameofthrones.core.domain.model.Character
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CompareCharactersUseCaseTest {

    private lateinit var useCase: CompareCharactersUseCase

    @Before
    fun setup() {
        useCase = CompareCharactersUseCase()
    }

    @Test
    fun `invoke should find common titles`() {
        // Given
        val character1 = createTestCharacter("1", "Jon Snow")
            .copy(titles = listOf("King in the North", "Lord Commander"))
        val character2 = createTestCharacter("2", "Robb Stark")
            .copy(titles = listOf("King in the North", "Lord of Winterfell"))

        // When
        val result = useCase.invoke(character1, character2)

        // Then
        assertEquals(listOf("King in the North"), result.commonTitles)
    }

    @Test
    fun `invoke should find common allegiances`() {
        // Given
        val character1 = createTestCharacter("1", "Jon Snow")
            .copy(allegiances = listOf("House Stark", "Night's Watch"))
        val character2 = createTestCharacter("2", "Arya Stark")
            .copy(allegiances = listOf("House Stark"))

        // When
        val result = useCase.invoke(character1, character2)

        // Then
        assertEquals(listOf("House Stark"), result.commonAllegiances)
    }

    @Test
    fun `invoke should detect same culture`() {
        // Given
        val character1 = createTestCharacter("1", "Jon Snow", culture = "Northmen")
        val character2 = createTestCharacter("2", "Robb Stark", culture = "Northmen")

        // When
        val result = useCase.invoke(character1, character2)

        // Then
        assertTrue(result.sameCulture)
    }

    @Test
    fun `invoke should detect different culture`() {
        // Given
        val character1 = createTestCharacter("1", "Jon Snow", culture = "Northmen")
        val character2 = createTestCharacter("2", "Daenerys", culture = "Valyrian")

        // When
        val result = useCase.invoke(character1, character2)

        // Then
        assertFalse(result.sameCulture)
    }

    @Test
    fun `invoke should detect same gender`() {
        // Given
        val character1 = createTestCharacter("1", "Jon Snow", gender = "Male")
        val character2 = createTestCharacter("2", "Robb Stark", gender = "Male")

        // When
        val result = useCase.invoke(character1, character2)

        // Then
        assertTrue(result.sameGender)
    }

    @Test
    fun `invoke should detect both dead`() {
        // Given
        val character1 = createTestCharacter("1", "Ned Stark", died = "298 AC")
        val character2 = createTestCharacter("2", "Robb Stark", died = "299 AC")

        // When
        val result = useCase.invoke(character1, character2)

        // Then
        assertTrue(result.bothDead)
        assertFalse(result.bothAlive)
    }

    @Test
    fun `invoke should detect both alive`() {
        // Given
        val character1 = createTestCharacter("1", "Jon Snow")
        val character2 = createTestCharacter("2", "Arya Stark")

        // When
        val result = useCase.invoke(character1, character2)

        // Then
        assertTrue(result.bothAlive)
        assertFalse(result.bothDead)
    }

    @Test
    fun `invoke should find common books`() {
        // Given
        val character1 = createTestCharacter("1", "Jon Snow")
            .copy(books = listOf("A Game of Thrones", "A Clash of Kings"))
        val character2 = createTestCharacter("2", "Arya Stark")
            .copy(books = listOf("A Game of Thrones", "A Storm of Swords"))

        // When
        val result = useCase.invoke(character1, character2)

        // Then
        assertEquals(listOf("A Game of Thrones"), result.commonBooks)
    }

    @Test
    fun `invoke should find common TV series`() {
        // Given
        val character1 = createTestCharacter("1", "Jon Snow")
            .copy(tvSeries = listOf("Season 1", "Season 2"))
        val character2 = createTestCharacter("2", "Arya Stark")
            .copy(tvSeries = listOf("Season 1", "Season 3"))

        // When
        val result = useCase.invoke(character1, character2)

        // Then
        assertEquals(listOf("Season 1"), result.commonTvSeries)
    }

    private fun createTestCharacter(
        id: String,
        name: String,
        gender: String = "Male",
        culture: String = "Northmen",
        died: String = ""
    ) = Character(
        id = id,
        name = name,
        gender = gender,
        culture = culture,
        born = "",
        died = died,
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
