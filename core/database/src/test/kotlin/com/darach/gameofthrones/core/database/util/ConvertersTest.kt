package com.darach.gameofthrones.core.database.util

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class ConvertersTest {

    private lateinit var converters: Converters

    @Before
    fun setup() {
        converters = Converters()
    }

    @Test
    fun `fromString converts empty string to empty list`() {
        val result = converters.fromString("")

        assertThat(result).isEmpty()
    }

    @Test
    fun `fromString converts single item string to list with one element`() {
        val result = converters.fromString("Item1")

        assertThat(result).containsExactly("Item1")
    }

    @Test
    fun `fromString converts multiple items string to list`() {
        val result = converters.fromString("Item1|||Item2|||Item3")

        assertThat(result).containsExactly("Item1", "Item2", "Item3").inOrder()
    }

    @Test
    fun `fromString trims whitespace from items`() {
        val result = converters.fromString(" Item1 ||| Item2 ||| Item3 ")

        assertThat(result).containsExactly("Item1", "Item2", "Item3").inOrder()
    }

    @Test
    fun `fromList converts empty list to empty string`() {
        val result = converters.fromList(emptyList())

        assertThat(result).isEmpty()
    }

    @Test
    fun `fromList converts single item list to string`() {
        val result = converters.fromList(listOf("Item1"))

        assertThat(result).isEqualTo("Item1")
    }

    @Test
    fun `fromList converts multiple items list to delimited string`() {
        val result = converters.fromList(listOf("Item1", "Item2", "Item3"))

        assertThat(result).isEqualTo("Item1|||Item2|||Item3")
    }

    @Test
    fun `round trip conversion preserves data`() {
        val original = listOf("House Stark", "House Lannister", "House Targaryen")

        val string = converters.fromList(original)
        val result = converters.fromString(string)

        assertThat(result).isEqualTo(original)
    }

    @Test
    fun `fromString handles items with special characters`() {
        val result = converters.fromString("O'Brien|||D'Angelo|||L'Hôpital")

        assertThat(result).containsExactly("O'Brien", "D'Angelo", "L'Hôpital").inOrder()
    }

    @Test
    fun `fromList handles items with special characters`() {
        val items = listOf("O'Brien", "D'Angelo", "L'Hôpital")

        val result = converters.fromList(items)

        assertThat(result).isEqualTo("O'Brien|||D'Angelo|||L'Hôpital")
    }
}
