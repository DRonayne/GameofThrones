package com.darach.gameofthrones.core.domain.usecase

import com.darach.gameofthrones.core.domain.model.Character
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FilterCharactersUseCaseTest {

    private lateinit var useCase: FilterCharactersUseCase

    @Before
    fun setup() {
        useCase = FilterCharactersUseCase()
    }

    @Test
    fun `invoke with culture filter should return matching characters`() {
        // Given
        val characters = listOf(
            createTestCharacter("1", "Jon Snow", culture = "Northmen"),
            createTestCharacter("2", "Cersei", culture = "Andals"),
            createTestCharacter("3", "Arya Stark", culture = "Northmen")
        )
        val filter = CharacterFilter(culture = "Northmen")

        // When
        val result = useCase.invoke(characters, filter)

        // Then
        assertEquals(2, result.size)
        assertEquals("Jon Snow", result[0].name)
        assertEquals("Arya Stark", result[1].name)
    }

    @Test
    fun `invoke with isDead filter should return only dead characters`() {
        // Given
        val characters = listOf(
            createTestCharacter("1", "Ned Stark", died = "298 AC"),
            createTestCharacter("2", "Jon Snow"),
            createTestCharacter("3", "Robb Stark", died = "299 AC")
        )
        val filter = CharacterFilter(isDead = true)

        // When
        val result = useCase.invoke(characters, filter)

        // Then
        assertEquals(2, result.size)
        assertEquals("Ned Stark", result[0].name)
        assertEquals("Robb Stark", result[1].name)
    }

    @Test
    fun `invoke with isDead false should return only alive characters`() {
        // Given
        val characters = listOf(
            createTestCharacter("1", "Ned Stark", died = "298 AC"),
            createTestCharacter("2", "Jon Snow"),
            createTestCharacter("3", "Arya Stark")
        )
        val filter = CharacterFilter(isDead = false)

        // When
        val result = useCase.invoke(characters, filter)

        // Then
        assertEquals(2, result.size)
        assertEquals("Jon Snow", result[0].name)
        assertEquals("Arya Stark", result[1].name)
    }

    @Test
    fun `invoke with hasAppearances filter should return characters with TV appearances`() {
        // Given
        val characters = listOf(
            createTestCharacter("1", "Jon Snow")
                .copy(tvSeries = listOf("Season 1")),
            createTestCharacter("2", "Unknown Character"),
            createTestCharacter("3", "Arya Stark")
                .copy(tvSeries = listOf("Season 1", "Season 2"))
        )
        val filter = CharacterFilter(hasAppearances = true)

        // When
        val result = useCase.invoke(characters, filter)

        // Then
        assertEquals(2, result.size)
        assertEquals("Jon Snow", result[0].name)
        assertEquals("Arya Stark", result[1].name)
    }

    @Test
    fun `invoke with hasAppearances false should return characters without TV appearances`() {
        // Given
        val characters = listOf(
            createTestCharacter("1", "Jon Snow")
                .copy(tvSeries = listOf("Season 1")),
            createTestCharacter("2", "Unknown Character"),
            createTestCharacter("3", "Arya Stark")
                .copy(tvSeries = listOf("Season 1"))
        )
        val filter = CharacterFilter(hasAppearances = false)

        // When
        val result = useCase.invoke(characters, filter)

        // Then
        assertEquals(1, result.size)
        assertEquals("Unknown Character", result[0].name)
    }

    @Test
    fun `invoke with gender filter should return matching characters`() {
        // Given
        val characters = listOf(
            createTestCharacter("1", "Jon Snow", gender = "Male"),
            createTestCharacter("2", "Arya Stark", gender = "Female"),
            createTestCharacter("3", "Tyrion", gender = "Male")
        )
        val filter = CharacterFilter(gender = "Female")

        // When
        val result = useCase.invoke(characters, filter)

        // Then
        assertEquals(1, result.size)
        assertEquals("Arya Stark", result[0].name)
    }

    @Test
    fun `invoke with onlyFavorites should return only favorite characters`() {
        // Given
        val characters = listOf(
            createTestCharacter("1", "Jon Snow").copy(isFavorite = true),
            createTestCharacter("2", "Arya Stark"),
            createTestCharacter("3", "Tyrion").copy(isFavorite = true)
        )
        val filter = CharacterFilter(onlyFavorites = true)

        // When
        val result = useCase.invoke(characters, filter)

        // Then
        assertEquals(2, result.size)
        assertEquals("Jon Snow", result[0].name)
        assertEquals("Tyrion", result[1].name)
    }

    @Test
    fun `invoke with multiple filters should apply all conditions`() {
        // Given
        val characters = listOf(
            createTestCharacter("1", "Jon Snow", culture = "Northmen", gender = "Male"),
            createTestCharacter("2", "Arya Stark", culture = "Northmen", gender = "Female"),
            createTestCharacter("3", "Cersei", culture = "Andals", gender = "Female")
        )
        val filter = CharacterFilter(culture = "Northmen", gender = "Female")

        // When
        val result = useCase.invoke(characters, filter)

        // Then
        assertEquals(1, result.size)
        assertEquals("Arya Stark", result[0].name)
    }

    @Test
    fun `invoke with no filters should return all characters`() {
        // Given
        val characters = listOf(
            createTestCharacter("1", "Jon Snow"),
            createTestCharacter("2", "Arya Stark"),
            createTestCharacter("3", "Tyrion")
        )
        val filter = CharacterFilter()

        // When
        val result = useCase.invoke(characters, filter)

        // Then
        assertEquals(3, result.size)
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
