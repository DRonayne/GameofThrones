package com.darach.gameofthrones.core.domain.usecase

import com.darach.gameofthrones.core.domain.model.Character
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SortCharactersUseCaseTest {

    private lateinit var useCase: SortCharactersUseCase

    @Before
    fun setup() {
        useCase = SortCharactersUseCase()
    }

    @Test
    fun `invoke with NAME_ASC should sort alphabetically`() {
        // Given
        val characters = listOf(
            createTestCharacter("1", "Tyrion Lannister"),
            createTestCharacter("2", "Arya Stark"),
            createTestCharacter("3", "Jon Snow")
        )

        // When
        val result = useCase.invoke(characters, SortOption.NAME_ASC)

        // Then
        assertEquals("Arya Stark", result[0].name)
        assertEquals("Jon Snow", result[1].name)
        assertEquals("Tyrion Lannister", result[2].name)
    }

    @Test
    fun `invoke with NAME_DESC should sort reverse alphabetically`() {
        // Given
        val characters = listOf(
            createTestCharacter("1", "Arya Stark"),
            createTestCharacter("2", "Jon Snow"),
            createTestCharacter("3", "Tyrion Lannister")
        )

        // When
        val result = useCase.invoke(characters, SortOption.NAME_DESC)

        // Then
        assertEquals("Tyrion Lannister", result[0].name)
        assertEquals("Jon Snow", result[1].name)
        assertEquals("Arya Stark", result[2].name)
    }

    @Test
    fun `invoke with CULTURE_ASC should sort by culture ascending`() {
        // Given
        val characters = listOf(
            createTestCharacter("1", "Jon Snow", culture = "Northmen"),
            createTestCharacter("2", "Cersei", culture = "Andals"),
            createTestCharacter("3", "Tyrion", culture = "Lannister")
        )

        // When
        val result = useCase.invoke(characters, SortOption.CULTURE_ASC)

        // Then
        assertEquals("Andals", result[0].culture)
        assertEquals("Lannister", result[1].culture)
        assertEquals("Northmen", result[2].culture)
    }

    @Test
    fun `invoke with CULTURE_DESC should sort by culture descending`() {
        // Given
        val characters = listOf(
            createTestCharacter("1", "Jon Snow", culture = "Northmen"),
            createTestCharacter("2", "Cersei", culture = "Andals"),
            createTestCharacter("3", "Tyrion", culture = "Lannister")
        )

        // When
        val result = useCase.invoke(characters, SortOption.CULTURE_DESC)

        // Then
        assertEquals("Northmen", result[0].culture)
        assertEquals("Lannister", result[1].culture)
        assertEquals("Andals", result[2].culture)
    }

    @Test
    fun `invoke with FAVORITE_FIRST should put favorites first`() {
        // Given
        val characters = listOf(
            createTestCharacter("1", "Jon Snow", isFavorite = false),
            createTestCharacter("2", "Arya Stark", isFavorite = true),
            createTestCharacter("3", "Tyrion", isFavorite = false),
            createTestCharacter("4", "Daenerys", isFavorite = true)
        )

        // When
        val result = useCase.invoke(characters, SortOption.FAVORITE_FIRST)

        // Then
        assertEquals(true, result[0].isFavorite)
        assertEquals(true, result[1].isFavorite)
        assertEquals(false, result[2].isFavorite)
        assertEquals(false, result[3].isFavorite)
    }

    @Test
    fun `invoke should handle empty list`() {
        // Given
        val characters = emptyList<Character>()

        // When
        val result = useCase.invoke(characters, SortOption.NAME_ASC)

        // Then
        assertEquals(emptyList<Character>(), result)
    }

    @Test
    fun `invoke with DEATH_DATE_ASC should sort by death date with alive last`() {
        // Given
        val characters = listOf(
            createTestCharacter("1", "Ned Stark", died = "298 AC"),
            createTestCharacter("2", "Jon Snow", died = ""),
            createTestCharacter("3", "Robb Stark", died = "299 AC"),
            createTestCharacter("4", "Arya Stark", died = "")
        )

        // When
        val result = useCase.invoke(characters, SortOption.DEATH_DATE_ASC)

        // Then
        assertEquals("Ned Stark", result[0].name)
        assertEquals("Robb Stark", result[1].name)
        // Alive characters (empty died field) should be at the end
        assertEquals("", result[2].died)
        assertEquals("", result[3].died)
    }

    @Test
    fun `invoke with DEATH_DATE_DESC should sort by death date with dead first`() {
        // Given
        val characters = listOf(
            createTestCharacter("1", "Ned Stark", died = "298 AC"),
            createTestCharacter("2", "Jon Snow", died = ""),
            createTestCharacter("3", "Robb Stark", died = "299 AC")
        )

        // When
        val result = useCase.invoke(characters, SortOption.DEATH_DATE_DESC)

        // Then
        assertEquals("Robb Stark", result[0].name)
        assertEquals("Ned Stark", result[1].name)
        assertEquals("Jon Snow", result[2].name)
    }

    @Test
    fun `invoke with SEASONS_COUNT_ASC should sort by number of seasons ascending`() {
        // Given
        val characters = listOf(
            createTestCharacter("1", "Jon Snow").copy(tvSeriesSeasons = listOf(1, 2, 3, 4, 5)),
            createTestCharacter("2", "Arya Stark").copy(tvSeriesSeasons = listOf(1, 2)),
            createTestCharacter("3", "Tyrion").copy(tvSeriesSeasons = listOf(1, 2, 3, 4))
        )

        // When
        val result = useCase.invoke(characters, SortOption.SEASONS_COUNT_ASC)

        // Then
        assertEquals("Arya Stark", result[0].name)
        assertEquals(2, result[0].tvSeriesSeasons.size)
        assertEquals("Tyrion", result[1].name)
        assertEquals(4, result[1].tvSeriesSeasons.size)
        assertEquals("Jon Snow", result[2].name)
        assertEquals(5, result[2].tvSeriesSeasons.size)
    }

    @Test
    fun `invoke with SEASONS_COUNT_DESC should sort by number of seasons descending`() {
        // Given
        val characters = listOf(
            createTestCharacter("1", "Arya Stark").copy(tvSeriesSeasons = listOf(1, 2)),
            createTestCharacter("2", "Jon Snow").copy(tvSeriesSeasons = listOf(1, 2, 3, 4, 5)),
            createTestCharacter("3", "Tyrion").copy(tvSeriesSeasons = listOf(1, 2, 3))
        )

        // When
        val result = useCase.invoke(characters, SortOption.SEASONS_COUNT_DESC)

        // Then
        assertEquals("Jon Snow", result[0].name)
        assertEquals(5, result[0].tvSeriesSeasons.size)
        assertEquals("Tyrion", result[1].name)
        assertEquals(3, result[1].tvSeriesSeasons.size)
        assertEquals("Arya Stark", result[2].name)
        assertEquals(2, result[2].tvSeriesSeasons.size)
    }

    @Test
    fun `invoke with SEASONS_COUNT_ASC should handle characters with no seasons`() {
        // Given
        val characters = listOf(
            createTestCharacter("1", "Jon Snow").copy(tvSeriesSeasons = listOf(1, 2)),
            createTestCharacter("2", "Unknown").copy(tvSeriesSeasons = emptyList()),
            createTestCharacter("3", "Arya Stark").copy(tvSeriesSeasons = listOf(1))
        )

        // When
        val result = useCase.invoke(characters, SortOption.SEASONS_COUNT_ASC)

        // Then
        assertEquals("Unknown", result[0].name)
        assertEquals(0, result[0].tvSeriesSeasons.size)
    }

    private fun createTestCharacter(
        id: String,
        name: String,
        culture: String = "Northmen",
        isFavorite: Boolean = false,
        died: String = ""
    ) = Character(
        id = id,
        name = name,
        gender = "Male",
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
        isFavorite = isFavorite
    )
}
