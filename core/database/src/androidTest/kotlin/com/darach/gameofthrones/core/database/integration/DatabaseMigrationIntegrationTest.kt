package com.darach.gameofthrones.core.database.integration

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.darach.gameofthrones.core.database.GoTDatabase
import com.darach.gameofthrones.core.database.dao.CharacterDao
import com.darach.gameofthrones.core.database.model.CharacterEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Integration test for database operations and migrations.
 * Tests Room database functionality, queries, and data integrity.
 */
class DatabaseMigrationIntegrationTest {

    private lateinit var database: GoTDatabase
    private lateinit var characterDao: CharacterDao

    @get:Rule
    val migrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        GoTDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GoTDatabase::class.java
        ).allowMainThreadQueries().build()

        characterDao = database.characterDao()
    }

    @Test
    fun test_database_creation_and_schema_validation() = runTest {
        // When: Creating database and inserting data
        val character = CharacterEntity(
            id = "1",
            name = "Test Character",
            gender = "Male",
            culture = "Northmen",
            born = "283 AC",
            died = "",
            titles = listOf("King in the North"),
            aliases = listOf("Test"),
            father = "",
            mother = "",
            spouse = "",
            allegiances = listOf("House Stark"),
            books = emptyList(),
            povBooks = emptyList(),
            tvSeries = listOf("Season 1"),
            playedBy = listOf("Actor Name"),
            isFavorite = false
        )

        characterDao.insertCharacters(listOf(character))

        // Then: Data should be correctly stored
        val count = characterDao.getCharacterCount()
        assertThat(count).isEqualTo(1)

        val retrieved = characterDao.observeCharacter("1").first()
        assertThat(retrieved).isNotNull()
        assertThat(retrieved?.name).isEqualTo("Test Character")
    }

    @Test
    fun test_insert_and_query_operations() = runTest {
        // Given: Multiple characters
        val characters = listOf(
            CharacterEntity(
                id = "1",
                name = "Jon Snow",
                gender = "Male",
                culture = "Northmen",
                born = "283 AC",
                died = "",
                titles = listOf("King in the North"),
                aliases = listOf("Lord Snow"),
                father = "",
                mother = "",
                spouse = "",
                allegiances = listOf("House Stark"),
                books = emptyList(),
                povBooks = emptyList(),
                tvSeries = listOf("Season 1"),
                playedBy = listOf("Kit Harington"),
                isFavorite = false
            ),
            CharacterEntity(
                id = "2",
                name = "Arya Stark",
                gender = "Female",
                culture = "Northmen",
                born = "289 AC",
                died = "",
                titles = emptyList(),
                aliases = listOf("No One"),
                father = "",
                mother = "",
                spouse = "",
                allegiances = listOf("House Stark"),
                books = emptyList(),
                povBooks = emptyList(),
                tvSeries = listOf("Season 1"),
                playedBy = listOf("Maisie Williams"),
                isFavorite = false
            )
        )

        // When: Inserting characters
        characterDao.insertCharacters(characters)

        // Then: All characters should be queryable
        val allCharacters = characterDao.observeAllCharacters().first()
        assertThat(allCharacters).hasSize(2)
        assertThat(allCharacters.map { it.name }).containsExactly("Arya Stark", "Jon Snow")
    }

    @Test
    fun test_update_operations() = runTest {
        // Given: Character in database
        val character = CharacterEntity(
            id = "1",
            name = "Sansa Stark",
            gender = "Female",
            culture = "Northmen",
            born = "286 AC",
            died = "",
            titles = listOf("Lady of Winterfell"),
            aliases = listOf("Little Bird"),
            father = "",
            mother = "",
            spouse = "",
            allegiances = listOf("House Stark"),
            books = emptyList(),
            povBooks = emptyList(),
            tvSeries = listOf("Season 1"),
            playedBy = listOf("Sophie Turner"),
            isFavorite = false
        )

        characterDao.insertCharacters(listOf(character))

        // When: Updating character
        val updated = character.copy(
            titles = listOf("Queen in the North"),
            isFavorite = true
        )
        characterDao.updateCharacter(updated)

        // Then: Character should be updated
        val retrieved = characterDao.observeCharacter("1").first()
        assertThat(retrieved?.titles).containsExactly("Queen in the North")
        assertThat(retrieved?.isFavorite).isTrue()
    }

    @Test
    fun test_favorite_operations() = runTest {
        // Given: Characters with different favorite status
        val characters = listOf(
            CharacterEntity(
                id = "1",
                name = "Tyrion Lannister",
                gender = "Male",
                culture = "Westerlands",
                born = "273 AC",
                died = "",
                titles = listOf("Hand of the Queen"),
                aliases = listOf("The Imp"),
                father = "",
                mother = "",
                spouse = "",
                allegiances = listOf("House Lannister"),
                books = emptyList(),
                povBooks = emptyList(),
                tvSeries = listOf("Season 1"),
                playedBy = listOf("Peter Dinklage"),
                isFavorite = true
            ),
            CharacterEntity(
                id = "2",
                name = "Cersei Lannister",
                gender = "Female",
                culture = "Westerlands",
                born = "266 AC",
                died = "",
                titles = listOf("Queen of the Seven Kingdoms"),
                aliases = emptyList(),
                father = "",
                mother = "",
                spouse = "",
                allegiances = listOf("House Lannister"),
                books = emptyList(),
                povBooks = emptyList(),
                tvSeries = listOf("Season 1"),
                playedBy = listOf("Lena Headey"),
                isFavorite = false
            )
        )

        characterDao.insertCharacters(characters)

        // When: Querying favorites
        val favorites = characterDao.observeFavoriteCharacters().first()

        // Then: Only favorites should be returned
        assertThat(favorites).hasSize(1)
        assertThat(favorites.first().name).isEqualTo("Tyrion Lannister")

        // When: Updating favorite status
        characterDao.updateFavorite("2", true)

        // Then: Both should be favorites
        val updatedFavorites = characterDao.observeFavoriteCharacters().first()
        assertThat(updatedFavorites).hasSize(2)
    }

    @Test
    fun test_search_operations() = runTest {
        // Given: Characters with searchable data
        val characters = listOf(
            CharacterEntity(
                id = "1",
                name = "Daenerys Targaryen",
                gender = "Female",
                culture = "Valyrian",
                born = "284 AC",
                died = "",
                titles = listOf("Mother of Dragons"),
                aliases = listOf("Dany", "Stormborn"),
                father = "",
                mother = "",
                spouse = "",
                allegiances = listOf("House Targaryen"),
                books = emptyList(),
                povBooks = emptyList(),
                tvSeries = listOf("Season 1"),
                playedBy = listOf("Emilia Clarke"),
                isFavorite = false
            ),
            CharacterEntity(
                id = "2",
                name = "Viserys Targaryen",
                gender = "Male",
                culture = "Valyrian",
                born = "276 AC",
                died = "298 AC",
                titles = emptyList(),
                aliases = listOf("The Beggar King"),
                father = "",
                mother = "",
                spouse = "",
                allegiances = listOf("House Targaryen"),
                books = emptyList(),
                povBooks = emptyList(),
                tvSeries = listOf("Season 1"),
                playedBy = listOf("Harry Lloyd"),
                isFavorite = false
            )
        )

        characterDao.insertCharacters(characters)

        // When: Searching by name
        val nameResults = characterDao.searchCharacters("Daenerys").first()
        assertThat(nameResults).hasSize(1)
        assertThat(nameResults.first().name).isEqualTo("Daenerys Targaryen")

        // When: Searching by culture
        val cultureResults = characterDao.searchCharacters("Valyrian").first()
        assertThat(cultureResults).hasSize(2)

        // When: Searching by alias
        val aliasResults = characterDao.searchCharacters("Dany").first()
        assertThat(aliasResults).hasSize(1)
    }

    @Test
    fun test_delete_operations() = runTest {
        // Given: Characters in database
        val characters = listOf(
            CharacterEntity(
                id = "1",
                name = "Joffrey Baratheon",
                gender = "Male",
                culture = "Crownlands",
                born = "286 AC",
                died = "300 AC",
                titles = listOf("King of the Andals"),
                aliases = emptyList(),
                father = "",
                mother = "",
                spouse = "",
                allegiances = listOf("House Baratheon"),
                books = emptyList(),
                povBooks = emptyList(),
                tvSeries = listOf("Season 1"),
                playedBy = listOf("Jack Gleeson"),
                isFavorite = false
            )
        )

        characterDao.insertCharacters(characters)
        assertThat(characterDao.getCharacterCount()).isEqualTo(1)

        // When: Deleting all characters
        characterDao.deleteAllCharacters()

        // Then: Database should be empty
        assertThat(characterDao.getCharacterCount()).isEqualTo(0)
    }

    @Test
    fun test_refresh_preserves_favorites() = runTest {
        // Given: Initial characters with one favorite
        val initialCharacters = listOf(
            CharacterEntity(
                id = "1",
                name = "Brienne of Tarth",
                gender = "Female",
                culture = "Westerlands",
                born = "280 AC",
                died = "",
                titles = listOf("Ser"),
                aliases = listOf("The Maid of Tarth"),
                father = "",
                mother = "",
                spouse = "",
                allegiances = listOf("House Tarth"),
                books = emptyList(),
                povBooks = emptyList(),
                tvSeries = listOf("Season 2"),
                playedBy = listOf("Gwendoline Christie"),
                isFavorite = true
            )
        )

        characterDao.insertCharacters(initialCharacters)

        // When: Refreshing with new data (same character, different data)
        val refreshedCharacters = listOf(
            CharacterEntity(
                id = "1",
                name = "Brienne of Tarth",
                gender = "Female",
                culture = "Westerlands",
                born = "280 AC",
                died = "",
                titles = listOf("Ser", "Lord Commander of the Kingsguard"),
                aliases = listOf("The Maid of Tarth"),
                father = "",
                mother = "",
                spouse = "",
                allegiances = listOf("House Tarth"),
                books = emptyList(),
                povBooks = emptyList(),
                tvSeries = listOf("Season 2", "Season 3"),
                playedBy = listOf("Gwendoline Christie"),
                isFavorite = false
            )
        )

        characterDao.refreshCharacters(refreshedCharacters)

        // Then: Favorite status should be preserved
        val character = characterDao.observeCharacter("1").first()
        assertThat(character?.isFavorite).isTrue()
        assertThat(character?.titles).hasSize(2)
    }

    @Test
    fun test_replace_strategy_on_conflict() = runTest {
        // Given: Character in database
        val original = CharacterEntity(
            id = "1",
            name = "Sandor Clegane",
            gender = "Male",
            culture = "Westerlands",
            born = "270 AC",
            died = "",
            titles = emptyList(),
            aliases = listOf("The Hound"),
            father = "",
            mother = "",
            spouse = "",
            allegiances = listOf("House Clegane"),
            books = emptyList(),
            povBooks = emptyList(),
            tvSeries = listOf("Season 1"),
            playedBy = listOf("Rory McCann"),
            isFavorite = false
        )

        characterDao.insertCharacters(listOf(original))

        // When: Inserting character with same ID but different data
        val updated = original.copy(
            died = "301 AC",
            tvSeries = listOf(
                "Season 1",
                "Season 2",
                "Season 3",
                "Season 4",
                "Season 5",
                "Season 6",
                "Season 7",
                "Season 8"
            )
        )

        characterDao.insertCharacters(listOf(updated))

        // Then: Should replace the original
        assertThat(characterDao.getCharacterCount()).isEqualTo(1)
        val character = characterDao.observeCharacter("1").first()
        assertThat(character?.died).isEqualTo("301 AC")
        assertThat(character?.tvSeries).hasSize(8)
    }

    @Test
    fun test_complex_list_fields() = runTest {
        // Given: Character with complex list fields
        val character = CharacterEntity(
            id = "1",
            name = "Aegon Targaryen",
            gender = "Male",
            culture = "Valyrian",
            born = "282 AC",
            died = "",
            titles = listOf(
                "Prince",
                "King of the Andals and the First Men",
                "Protector of the Realm"
            ),
            aliases = listOf("Jon Snow", "The White Wolf", "King Crow"),
            father = "Rhaegar Targaryen",
            mother = "Lyanna Stark",
            spouse = "Daenerys Targaryen",
            allegiances = listOf("House Targaryen", "House Stark", "Night's Watch"),
            books = listOf("A Dance with Dragons"),
            povBooks = listOf(
                "A Game of Thrones",
                "A Clash of Kings",
                "A Storm of Swords",
                "A Feast for Crows",
                "A Dance with Dragons"
            ),
            tvSeries = listOf(
                "Season 1",
                "Season 2",
                "Season 3",
                "Season 4",
                "Season 5",
                "Season 6",
                "Season 7",
                "Season 8"
            ),
            playedBy = listOf("Kit Harington"),
            characterImageUrl = "https://example.com/image.jpg",
            actorImageUrls = mapOf("Kit Harington" to "https://example.com/actor.jpg"),
            isFavorite = true
        )

        // When: Inserting and retrieving
        characterDao.insertCharacters(listOf(character))
        val retrieved = characterDao.observeCharacter("1").first()

        // Then: All complex fields should be preserved
        assertThat(retrieved).isNotNull()
        assertThat(retrieved?.titles).hasSize(3)
        assertThat(retrieved?.aliases).hasSize(3)
        assertThat(retrieved?.allegiances).hasSize(3)
        assertThat(retrieved?.povBooks).hasSize(5)
        assertThat(retrieved?.tvSeries).hasSize(8)
        assertThat(retrieved?.characterImageUrl).isEqualTo("https://example.com/image.jpg")
        assertThat(
            retrieved?.actorImageUrls
        ).containsEntry("Kit Harington", "https://example.com/actor.jpg")
    }

    @Test
    fun test_sorting_by_name() = runTest {
        // Given: Unsorted characters
        val characters = listOf(
            CharacterEntity(
                id = "3",
                name = "Zara",
                gender = "Female",
                culture = "",
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
                isFavorite = false
            ),
            CharacterEntity(
                id = "1",
                name = "Aaron",
                gender = "Male",
                culture = "",
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
                isFavorite = false
            ),
            CharacterEntity(
                id = "2",
                name = "Bella",
                gender = "Female",
                culture = "",
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
                isFavorite = false
            )
        )

        characterDao.insertCharacters(characters)

        // When: Querying all characters
        val sorted = characterDao.observeAllCharacters().first()

        // Then: Should be sorted alphabetically
        assertThat(sorted.map { it.name }).containsExactly("Aaron", "Bella", "Zara").inOrder()
    }

    @Test
    fun test_empty_database_queries() = runTest {
        // Given: Empty database
        // When: Querying
        val all = characterDao.observeAllCharacters().first()
        val favorites = characterDao.observeFavoriteCharacters().first()
        val search = characterDao.searchCharacters("test").first()
        val count = characterDao.getCharacterCount()

        // Then: Should return empty results
        assertThat(all).isEmpty()
        assertThat(favorites).isEmpty()
        assertThat(search).isEmpty()
        assertThat(count).isEqualTo(0)
    }
}
