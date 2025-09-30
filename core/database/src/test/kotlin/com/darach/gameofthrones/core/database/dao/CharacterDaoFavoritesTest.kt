package com.darach.gameofthrones.core.database.dao

import com.darach.gameofthrones.core.database.model.CharacterEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CharacterDaoFavoritesTest {

    private lateinit var dao: FakeCharacterDao

    @Before
    fun setup() {
        dao = FakeCharacterDao()
    }

    @Test
    fun `observeFavoriteCharacters returns only favorite characters`() = runTest {
        val favorite = createCharacter("1", "Jon Snow", isFavorite = true)
        val notFavorite = createCharacter("2", "Arya Stark", isFavorite = false)

        dao.insertCharacters(listOf(favorite, notFavorite))

        val favorites = dao.observeFavoriteCharacters().first()

        assertEquals(1, favorites.size)
        assertEquals("Jon Snow", favorites.first().name)
        assertTrue(favorites.first().isFavorite)
    }

    @Test
    fun `observeFavoriteCharacters returns empty list when no favorites`() = runTest {
        val character1 = createCharacter("1", "Jon Snow", isFavorite = false)
        val character2 = createCharacter("2", "Arya Stark", isFavorite = false)

        dao.insertCharacters(listOf(character1, character2))

        val favorites = dao.observeFavoriteCharacters().first()

        assertTrue(favorites.isEmpty())
    }

    @Test
    fun `updateFavorite correctly updates character favorite status`() = runTest {
        val character = createCharacter("1", "Jon Snow", isFavorite = false)
        dao.insertCharacters(listOf(character))

        dao.updateFavorite("1", true)

        val favorites = dao.observeFavoriteCharacters().first()
        assertEquals(1, favorites.size)
        assertTrue(favorites.first().isFavorite)
    }

    @Test
    fun `updateFavorite can remove favorite status`() = runTest {
        val character = createCharacter("1", "Jon Snow", isFavorite = true)
        dao.insertCharacters(listOf(character))

        dao.updateFavorite("1", false)

        val favorites = dao.observeFavoriteCharacters().first()
        assertTrue(favorites.isEmpty())

        val allCharacters = dao.observeAllCharacters().first()
        assertEquals(1, allCharacters.size)
        assertFalse(allCharacters.first().isFavorite)
    }

    @Test
    fun `getFavoritesCount returns correct count`() = runTest {
        val favorite1 = createCharacter("1", "Jon Snow", isFavorite = true)
        val favorite2 = createCharacter("2", "Arya Stark", isFavorite = true)
        val notFavorite = createCharacter("3", "Ned Stark", isFavorite = false)

        dao.insertCharacters(listOf(favorite1, favorite2, notFavorite))

        val count = dao.getFavoritesCount()

        assertEquals(2, count)
    }

    @Test
    fun `getFavoritesCount returns zero when no favorites`() = runTest {
        val character1 = createCharacter("1", "Jon Snow", isFavorite = false)
        val character2 = createCharacter("2", "Arya Stark", isFavorite = false)

        dao.insertCharacters(listOf(character1, character2))

        val count = dao.getFavoritesCount()

        assertEquals(0, count)
    }

    @Test
    fun `observeFavoriteCharacters returns sorted by name`() = runTest {
        val favorite1 = createCharacter("1", "Tyrion", isFavorite = true)
        val favorite2 = createCharacter("2", "Arya", isFavorite = true)
        val favorite3 = createCharacter("3", "Jon", isFavorite = true)

        dao.insertCharacters(listOf(favorite1, favorite2, favorite3))

        val favorites = dao.observeFavoriteCharacters().first()

        assertEquals(3, favorites.size)
        assertEquals("Arya", favorites[0].name)
        assertEquals("Jon", favorites[1].name)
        assertEquals("Tyrion", favorites[2].name)
    }

    private fun createCharacter(id: String, name: String, isFavorite: Boolean = false) =
        CharacterEntity(
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
            playedBy = emptyList(),
            isFavorite = isFavorite
        )
}
