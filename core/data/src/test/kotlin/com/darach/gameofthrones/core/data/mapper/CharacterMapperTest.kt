package com.darach.gameofthrones.core.data.mapper

import com.darach.gameofthrones.core.database.model.CharacterEntity
import com.darach.gameofthrones.core.network.model.CharacterDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterMapperTest {

    @Test
    fun `toDomain from DTO maps all fields correctly`() {
        val dto = CharacterDto(
            name = "Jon Snow",
            gender = "Male",
            culture = "Northmen",
            born = "In 283 AC",
            died = "",
            titles = listOf("Lord Commander", "King in the North"),
            aliases = listOf("Lord Snow", "The Bastard of Winterfell"),
            father = "",
            mother = "",
            spouse = "",
            allegiances = listOf("House Stark"),
            books = listOf("A Game of Thrones"),
            povBooks = listOf("A Dance with Dragons"),
            tvSeries = listOf("Season 1", "Season 2", "Season 3"),
            playedBy = listOf("Kit Harington")
        )

        val domain = dto.toDomain("583")

        assertEquals("583", domain.id)
        assertEquals("Jon Snow", domain.name)
        assertEquals("Male", domain.gender)
        assertEquals("Northmen", domain.culture)
        assertEquals("In 283 AC", domain.born)
        assertEquals("", domain.died)
        assertEquals(listOf("Lord Commander", "King in the North"), domain.titles)
        assertEquals(listOf("Lord Snow", "The Bastard of Winterfell"), domain.aliases)
        assertEquals("", domain.father)
        assertEquals("", domain.mother)
        assertEquals("", domain.spouse)
        assertEquals(listOf("House Stark"), domain.allegiances)
        assertEquals(listOf("A Game of Thrones"), domain.books)
        assertEquals(listOf("A Dance with Dragons"), domain.povBooks)
        assertEquals(listOf("Season 1", "Season 2", "Season 3"), domain.tvSeries)
        assertEquals(listOf(1, 2, 3), domain.tvSeriesSeasons)
        assertEquals(listOf("Kit Harington"), domain.playedBy)
        assertFalse(domain.isFavorite)
        assertFalse(domain.isDead)
    }

    @Test
    fun `toDomain from DTO marks character as dead when died field is not empty`() {
        val dto = CharacterDto(
            name = "Ned Stark",
            died = "In 299 AC"
        )

        val domain = dto.toDomain("339")

        assertTrue(domain.isDead)
        assertEquals("In 299 AC", domain.died)
    }

    @Test
    fun `toDomain from DTO uses provided character ID`() {
        val dto = CharacterDto(name = "Test")

        assertEquals("583", dto.toDomain("583").id)
        assertEquals("1", dto.toDomain("1").id)
        assertEquals("12345", dto.toDomain("12345").id)
    }

    @Test
    fun `toDomain from Entity maps all fields correctly`() {
        val entity = CharacterEntity(
            id = "583",
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
            books = listOf("A Game of Thrones"),
            povBooks = listOf("A Dance with Dragons"),
            tvSeries = listOf("Season 1", "Season 2"),
            playedBy = listOf("Kit Harington"),
            isFavorite = true,
            lastUpdated = 123456789L
        )

        val domain = entity.toDomain()

        assertEquals("583", domain.id)
        assertEquals("Jon Snow", domain.name)
        assertEquals("Male", domain.gender)
        assertEquals("Northmen", domain.culture)
        assertEquals("In 283 AC", domain.born)
        assertEquals("", domain.died)
        assertEquals(listOf("Lord Commander"), domain.titles)
        assertEquals(listOf("Lord Snow"), domain.aliases)
        assertEquals("", domain.father)
        assertEquals("", domain.mother)
        assertEquals("", domain.spouse)
        assertEquals(listOf("House Stark"), domain.allegiances)
        assertEquals(listOf("A Game of Thrones"), domain.books)
        assertEquals(listOf("A Dance with Dragons"), domain.povBooks)
        assertEquals(listOf("Season 1", "Season 2"), domain.tvSeries)
        assertEquals(listOf(1, 2), domain.tvSeriesSeasons)
        assertEquals(listOf("Kit Harington"), domain.playedBy)
        assertTrue(domain.isFavorite)
        assertFalse(domain.isDead)
        assertEquals(123456789L, domain.lastUpdated)
    }

    @Test
    fun `toEntity from Domain maps all fields correctly`() {
        val domain = com.darach.gameofthrones.core.domain.model.Character(
            id = "583",
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
            books = listOf("A Game of Thrones"),
            povBooks = listOf("A Dance with Dragons"),
            tvSeries = listOf("Season 1", "Season 2"),
            tvSeriesSeasons = listOf(1, 2),
            playedBy = listOf("Kit Harington"),
            isFavorite = true,
            isDead = false,
            lastUpdated = 123456789L
        )

        val entity = domain.toEntity()

        assertEquals("583", entity.id)
        assertEquals("Jon Snow", entity.name)
        assertEquals("Male", entity.gender)
        assertEquals("Northmen", entity.culture)
        assertEquals("In 283 AC", entity.born)
        assertEquals("", entity.died)
        assertEquals(listOf("Lord Commander"), entity.titles)
        assertEquals(listOf("Lord Snow"), entity.aliases)
        assertEquals("", entity.father)
        assertEquals("", entity.mother)
        assertEquals("", entity.spouse)
        assertEquals(listOf("House Stark"), entity.allegiances)
        assertEquals(listOf("A Game of Thrones"), entity.books)
        assertEquals(listOf("A Dance with Dragons"), entity.povBooks)
        assertEquals(listOf("Season 1", "Season 2"), entity.tvSeries)
        assertEquals(listOf("Kit Harington"), entity.playedBy)
        assertTrue(entity.isFavorite)
        assertEquals(123456789L, entity.lastUpdated)
    }

    @Test
    fun `round trip conversion maintains data integrity`() {
        val originalEntity = CharacterEntity(
            id = "583",
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
            books = listOf("A Game of Thrones"),
            povBooks = listOf("A Dance with Dragons"),
            tvSeries = listOf("Season 1"),
            playedBy = listOf("Kit Harington"),
            isFavorite = true,
            lastUpdated = 123456789L
        )

        val domain = originalEntity.toDomain()
        val backToEntity = domain.toEntity()

        assertEquals(originalEntity, backToEntity)
    }
}
