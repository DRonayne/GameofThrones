package com.darach.gameofthrones.feature.comparison

import com.darach.gameofthrones.core.model.Character
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ComparisonResultTest {

    private val testCharacter1 = Character(
        id = "1",
        name = "Jon Snow",
        gender = "Male",
        culture = "Northmen",
        born = "283 AC",
        died = "",
        titles = listOf("King in the North"),
        aliases = listOf("Lord Snow"),
        father = "Eddard Stark",
        mother = "",
        spouse = "",
        allegiances = listOf(),
        books = listOf(),
        povBooks = listOf(),
        tvSeries = listOf("Season 1"),
        tvSeriesSeasons = listOf(1),
        playedBy = listOf("Kit Harington")
    )

    private val testCharacter2 = testCharacter1.copy(id = "2", name = "Arya Stark")

    @Test
    fun `creates valid comparison result with two characters`() {
        val result = ComparisonResult(
            characters = listOf(testCharacter1, testCharacter2),
            attributes = emptyList()
        )

        assertEquals(2, result.characters.size)
        assertTrue(result.attributes.isEmpty())
    }

    @Test
    fun `throws exception with one character`() {
        assertThrows(IllegalArgumentException::class.java) {
            ComparisonResult(
                characters = listOf(testCharacter1),
                attributes = emptyList()
            )
        }
    }

    @Test
    fun `throws exception with four characters`() {
        val char3 = testCharacter1.copy(id = "3")
        val char4 = testCharacter1.copy(id = "4")

        assertThrows(IllegalArgumentException::class.java) {
            ComparisonResult(
                characters = listOf(testCharacter1, testCharacter2, char3, char4),
                attributes = emptyList()
            )
        }
    }

    @Test
    fun `creates valid comparison attribute`() {
        val attribute = ComparisonAttribute(
            name = "Name",
            values = listOf(
                AttributeValue("Jon Snow"),
                AttributeValue("Arya Stark")
            ),
            hasDifference = true
        )

        assertEquals("Name", attribute.name)
        assertEquals(2, attribute.values.size)
        assertTrue(attribute.hasDifference)
    }

    @Test
    fun `throws exception with empty attribute values`() {
        assertThrows(IllegalArgumentException::class.java) {
            ComparisonAttribute(
                name = "Name",
                values = emptyList(),
                hasDifference = false
            )
        }
    }

    @Test
    fun `attribute value has correct properties`() {
        val value = AttributeValue(
            value = "Jon Snow",
            isDifferent = true,
            isEmpty = false
        )

        assertEquals("Jon Snow", value.value)
        assertTrue(value.isDifferent)
        assertFalse(value.isEmpty)
    }

    @Test
    fun `attribute value defaults to not different`() {
        val value = AttributeValue(value = "Test")

        assertFalse(value.isDifferent)
        assertFalse(value.isEmpty)
    }
}
