package com.darach.gameofthrones.feature.comparison

import com.darach.gameofthrones.core.model.Character
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ComparisonDiffCalculatorTest {

    private lateinit var calculator: ComparisonDiffCalculator

    private val jonSnow = Character(
        id = "1",
        name = "Jon Snow",
        gender = "Male",
        culture = "Northmen",
        born = "283 AC",
        died = "",
        titles = listOf("King in the North", "Lord Commander"),
        aliases = listOf("Lord Snow", "The White Wolf"),
        father = "Eddard Stark",
        mother = "",
        spouse = "",
        allegiances = listOf("House Stark"),
        books = listOf("A Game of Thrones"),
        povBooks = listOf("A Dance with Dragons"),
        tvSeries = listOf("Season 1", "Season 2"),
        tvSeriesSeasons = listOf(1, 2, 3),
        playedBy = listOf("Kit Harington")
    )

    private val aryaStark = Character(
        id = "2",
        name = "Arya Stark",
        gender = "Female",
        culture = "Northmen",
        born = "289 AC",
        died = "",
        titles = emptyList(),
        aliases = listOf("Arry", "Cat of the Canals"),
        father = "Eddard Stark",
        mother = "Catelyn Stark",
        spouse = "",
        allegiances = listOf("House Stark"),
        books = listOf("A Game of Thrones"),
        povBooks = listOf("A Game of Thrones"),
        tvSeries = listOf("Season 1", "Season 2"),
        tvSeriesSeasons = listOf(1, 2, 3),
        playedBy = listOf("Maisie Williams")
    )

    private val cerseiLannister = Character(
        id = "3",
        name = "Cersei Lannister",
        gender = "Female",
        culture = "Westerman",
        born = "266 AC",
        died = "303 AC",
        titles = listOf("Queen Regent", "Lady of Casterly Rock"),
        aliases = emptyList(),
        father = "Tywin Lannister",
        mother = "Joanna Lannister",
        spouse = "Robert Baratheon",
        allegiances = listOf("House Lannister"),
        books = listOf("A Game of Thrones"),
        povBooks = listOf("A Feast for Crows"),
        tvSeries = listOf("Season 1"),
        tvSeriesSeasons = listOf(1, 2),
        playedBy = listOf("Lena Headey")
    )

    @Before
    fun setup() {
        calculator = ComparisonDiffCalculator()
    }

    @Test
    fun `throws exception with one character`() {
        assertThrows(IllegalArgumentException::class.java) {
            calculator.calculate(listOf(jonSnow))
        }
    }

    @Test
    fun `throws exception with four characters`() {
        val char4 = jonSnow.copy(id = "4")
        assertThrows(IllegalArgumentException::class.java) {
            calculator.calculate(listOf(jonSnow, aryaStark, cerseiLannister, char4))
        }
    }

    @Test
    fun `calculates comparison for two characters`() {
        val result = calculator.calculate(listOf(jonSnow, aryaStark))

        assertEquals(2, result.characters.size)
        assertTrue(result.attributes.isNotEmpty())
    }

    @Test
    fun `calculates comparison for three characters`() {
        val result = calculator.calculate(listOf(jonSnow, aryaStark, cerseiLannister))

        assertEquals(3, result.characters.size)
        assertTrue(result.attributes.isNotEmpty())
    }

    @Test
    fun `marks different names as having difference`() {
        val result = calculator.calculate(listOf(jonSnow, aryaStark))
        val nameAttribute = result.attributes.find { it.name == "Name" }

        assertTrue(nameAttribute!!.hasDifference)
        assertEquals("Jon Snow", nameAttribute.values[0].value)
        assertEquals("Arya Stark", nameAttribute.values[1].value)
    }

    @Test
    fun `marks same culture as not having difference`() {
        val result = calculator.calculate(listOf(jonSnow, aryaStark))
        val cultureAttribute = result.attributes.find { it.name == "Culture" }

        assertFalse(cultureAttribute!!.hasDifference)
        assertEquals("Northmen", cultureAttribute.values[0].value)
        assertEquals("Northmen", cultureAttribute.values[1].value)
    }

    @Test
    fun `marks different cultures as having difference`() {
        val result = calculator.calculate(listOf(jonSnow, cerseiLannister))
        val cultureAttribute = result.attributes.find { it.name == "Culture" }

        assertTrue(cultureAttribute!!.hasDifference)
    }

    @Test
    fun `handles empty values as Unknown`() {
        val result = calculator.calculate(listOf(jonSnow, cerseiLannister))
        val motherAttribute = result.attributes.find { it.name == "Mother" }

        // Jon Snow has no mother (Unknown), but Cersei does, so attribute should be present
        assertEquals("Unknown", motherAttribute!!.values[0].value)
        assertTrue(motherAttribute.values[0].isEmpty)
        assertEquals("Joanna Lannister", motherAttribute.values[1].value)
        assertFalse(motherAttribute.values[1].isEmpty)
    }

    @Test
    fun `handles empty lists as None`() {
        val result = calculator.calculate(listOf(aryaStark, jonSnow))
        val titlesAttribute = result.attributes.find { it.name == "Titles" }

        assertEquals("None", titlesAttribute!!.values[0].value)
        assertTrue(titlesAttribute.values[0].isEmpty)
    }

    @Test
    fun `formats list values correctly`() {
        val result = calculator.calculate(listOf(jonSnow, aryaStark))
        val titlesAttribute = result.attributes.find { it.name == "Titles" }

        assertEquals("King in the North, Lord Commander", titlesAttribute!!.values[0].value)
    }

    @Test
    fun `marks different death status correctly`() {
        val result = calculator.calculate(listOf(jonSnow, cerseiLannister))
        val statusAttribute = result.attributes.find { it.name == "Status" }

        assertTrue(statusAttribute!!.hasDifference)
        assertEquals("Alive", statusAttribute.values[0].value)
        assertEquals("Dead", statusAttribute.values[1].value)
    }

    @Test
    fun `formats TV seasons as comma-separated`() {
        val result = calculator.calculate(listOf(jonSnow, aryaStark))
        val seasonsAttribute = result.attributes.find { it.name == "Seasons" }

        assertEquals("1, 2, 3", seasonsAttribute!!.values[0].value)
        assertEquals("1, 2, 3", seasonsAttribute.values[1].value)
    }

    @Test
    fun `includes all expected attributes`() {
        val result = calculator.calculate(listOf(jonSnow, aryaStark))
        val attributeNames = result.attributes.map { it.name }

        assertTrue(attributeNames.contains("Name"))
        assertTrue(attributeNames.contains("Gender"))
        assertTrue(attributeNames.contains("Culture"))
        assertTrue(attributeNames.contains("Born"))
        assertTrue(attributeNames.contains("Status"))
        assertTrue(attributeNames.contains("Titles"))
        assertTrue(attributeNames.contains("Aliases"))
        assertTrue(attributeNames.contains("Father"))
        assertTrue(attributeNames.contains("Mother"))
        assertTrue(attributeNames.contains("Allegiances"))
        assertTrue(attributeNames.contains("Books"))
        assertTrue(attributeNames.contains("POV Books"))
        assertTrue(attributeNames.contains("Seasons"))
        assertTrue(attributeNames.contains("Played By"))

        // Attributes filtered out because both values are empty
        assertFalse(attributeNames.contains("Died"))
        assertFalse(attributeNames.contains("Spouse"))
    }
}
