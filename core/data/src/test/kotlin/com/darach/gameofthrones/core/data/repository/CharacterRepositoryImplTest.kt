package com.darach.gameofthrones.core.data.repository

import app.cash.turbine.test
import com.darach.gameofthrones.core.database.dao.CharacterDao
import com.darach.gameofthrones.core.database.model.CharacterEntity
import com.darach.gameofthrones.core.network.api.GoTApiService
import com.darach.gameofthrones.core.network.model.CharacterDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CharacterRepositoryImplTest {

    private lateinit var repository: CharacterRepositoryImpl
    private lateinit var apiService: GoTApiService
    private lateinit var characterDao: CharacterDao

    private val testDto = CharacterDto(
        name = "Jon Snow",
        gender = "Male",
        culture = "Northmen",
        born = "In 283 AC",
        died = "",
        titles = listOf("Lord Commander"),
        aliases = listOf("Lord Snow"),
        father = "",
        mother = "",
        spouse = "",
        allegiances = listOf("House Stark"),
        books = listOf(),
        povBooks = listOf(),
        tvSeries = listOf("Season 1", "Season 2"),
        playedBy = listOf("Kit Harington")
    )

    private val testEntity = CharacterEntity(
        id = "1",
        name = "Jon Snow",
        gender = "Male",
        culture = "Northmen",
        born = "In 283 AC",
        died = "",
        titles = listOf("Lord Commander"),
        aliases = listOf("Lord Snow"),
        father = "",
        mother = "",
        spouse = "",
        allegiances = listOf("House Stark"),
        books = listOf(),
        povBooks = listOf(),
        tvSeries = listOf("Season 1", "Season 2"),
        playedBy = listOf("Kit Harington"),
        isFavorite = false
    )

    @Before
    fun setup() {
        apiService = mockk()
        characterDao = mockk(relaxed = true)
        repository = CharacterRepositoryImpl(apiService, characterDao)
    }

    @Test
    fun `observeCharacters fetches from network when cache is empty`() = runTest {
        // Given
        coEvery { characterDao.getCharacterCount() } returns 0
        coEvery { apiService.getCharacters() } returns listOf(testDto)
        coEvery { characterDao.refreshCharacters(any()) } returns Unit

        // When
        repository.observeCharacters(forceRefresh = false).test {
            // Then
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(1, result.getOrNull()?.size)
            assertEquals("Jon Snow", result.getOrNull()?.first()?.name)
            coVerify { apiService.getCharacters() }
            coVerify { characterDao.refreshCharacters(any()) }
            awaitComplete()
        }
    }

    @Test
    fun `observeCharacters returns cached data when available and not forcing refresh`() = runTest {
        // Given
        coEvery { characterDao.getCharacterCount() } returns 1
        every { characterDao.observeAllCharacters() } returns flowOf(listOf(testEntity))

        // When
        repository.observeCharacters(forceRefresh = false).test {
            // Then
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(1, result.getOrNull()?.size)
            assertEquals("Jon Snow", result.getOrNull()?.first()?.name)
            coVerify(exactly = 0) { apiService.getCharacters() }
            awaitComplete()
        }
    }

    @Test
    fun `observeCharacters forces refresh when forceRefresh is true`() = runTest {
        // Given
        coEvery { characterDao.getCharacterCount() } returns 1
        coEvery { apiService.getCharacters() } returns listOf(testDto)
        coEvery { characterDao.refreshCharacters(any()) } returns Unit

        // When
        repository.observeCharacters(forceRefresh = true).test {
            // Then
            val result = awaitItem()
            assertTrue(result.isSuccess)
            coVerify { apiService.getCharacters() }
            coVerify { characterDao.refreshCharacters(any()) }
            awaitComplete()
        }
    }

    @Test
    fun `observeCharacters returns cached data on network failure when cache exists`() = runTest {
        // Given
        coEvery { characterDao.getCharacterCount() } returns 1
        coEvery { apiService.getCharacters() } throws IOException("Network error")
        every { characterDao.observeAllCharacters() } returns flowOf(listOf(testEntity))

        // When
        repository.observeCharacters(forceRefresh = true).test {
            // Then
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(1, result.getOrNull()?.size)
            awaitComplete()
        }
    }

    @Test
    fun `observeCharacters returns error on network failure when cache is empty`() = runTest {
        // Given
        coEvery { characterDao.getCharacterCount() } returns 0
        coEvery { apiService.getCharacters() } throws IOException("Network error")

        // When
        repository.observeCharacters(forceRefresh = false).test {
            // Then
            val result = awaitItem()
            assertTrue(result.isFailure)
            awaitComplete()
        }
    }

    @Test
    fun `observeCharacter returns character when found`() = runTest {
        // Given
        every { characterDao.observeCharacter("1") } returns flowOf(testEntity)

        // When
        repository.observeCharacter("1").test {
            // Then
            val character = awaitItem()
            assertNotNull(character)
            assertEquals("Jon Snow", character?.name)
            awaitComplete()
        }
    }

    @Test
    fun `observeCharacter returns null when not found`() = runTest {
        // Given
        every { characterDao.observeCharacter("999") } returns flowOf(null)

        // When
        repository.observeCharacter("999").test {
            // Then
            val character = awaitItem()
            assertNull(character)
            awaitComplete()
        }
    }

    @Test
    fun `observeFavoriteCharacters returns only favorites`() = runTest {
        // Given
        val favoriteEntity = testEntity.copy(isFavorite = true)
        every { characterDao.observeFavoriteCharacters() } returns flowOf(listOf(favoriteEntity))

        // When
        repository.observeFavoriteCharacters().test {
            // Then
            val favorites = awaitItem()
            assertEquals(1, favorites.size)
            assertTrue(favorites.first().isFavorite)
            awaitComplete()
        }
    }

    @Test
    fun `searchCharacters returns matching results`() = runTest {
        // Given
        every { characterDao.searchCharacters("Jon") } returns flowOf(listOf(testEntity))

        // When
        repository.searchCharacters("Jon").test {
            // Then
            val results = awaitItem()
            assertEquals(1, results.size)
            assertEquals("Jon Snow", results.first().name)
            awaitComplete()
        }
    }

    @Test
    fun `updateFavorite calls dao with correct parameters`() = runTest {
        // Given
        coEvery { characterDao.updateFavorite("1", true) } returns Unit

        // When
        repository.updateFavorite("1", true)

        // Then
        coVerify { characterDao.updateFavorite("1", true) }
    }

    @Test
    fun `refreshCharacters fetches and updates cache successfully`() = runTest {
        // Given
        coEvery { apiService.getCharacters() } returns listOf(testDto)
        coEvery { characterDao.refreshCharacters(any()) } returns Unit

        // When
        val result = repository.refreshCharacters()

        // Then
        assertTrue(result.isSuccess)
        coVerify { apiService.getCharacters() }
        coVerify { characterDao.refreshCharacters(any()) }
    }

    @Test
    fun `refreshCharacters returns error on network failure`() = runTest {
        // Given
        coEvery { apiService.getCharacters() } throws IOException("Network error")

        // When
        val result = repository.refreshCharacters()

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `getCharacterCount returns correct count`() = runTest {
        // Given
        coEvery { characterDao.getCharacterCount() } returns 42

        // When
        val count = repository.getCharacterCount()

        // Then
        assertEquals(42, count)
    }
}
