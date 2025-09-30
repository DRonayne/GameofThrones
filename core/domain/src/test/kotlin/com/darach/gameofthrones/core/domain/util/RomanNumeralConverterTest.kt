package com.darach.gameofthrones.core.domain.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RomanNumeralConverterTest {

    @Test
    fun `toRomanNumeral converts valid season numbers correctly`() {
        assertEquals("I", RomanNumeralConverter.toRomanNumeral(1))
        assertEquals("II", RomanNumeralConverter.toRomanNumeral(2))
        assertEquals("III", RomanNumeralConverter.toRomanNumeral(3))
        assertEquals("IV", RomanNumeralConverter.toRomanNumeral(4))
        assertEquals("V", RomanNumeralConverter.toRomanNumeral(5))
        assertEquals("VI", RomanNumeralConverter.toRomanNumeral(6))
        assertEquals("VII", RomanNumeralConverter.toRomanNumeral(7))
        assertEquals("VIII", RomanNumeralConverter.toRomanNumeral(8))
        assertEquals("IX", RomanNumeralConverter.toRomanNumeral(9))
        assertEquals("X", RomanNumeralConverter.toRomanNumeral(10))
    }

    @Test
    fun `toRomanNumeral returns number as string for out of range values`() {
        assertEquals("0", RomanNumeralConverter.toRomanNumeral(0))
        assertEquals("11", RomanNumeralConverter.toRomanNumeral(11))
        assertEquals("100", RomanNumeralConverter.toRomanNumeral(100))
        assertEquals("-1", RomanNumeralConverter.toRomanNumeral(-1))
    }

    @Test
    fun `extractSeasonNumber extracts season from valid format`() {
        assertEquals(1, RomanNumeralConverter.extractSeasonNumber("Season 1"))
        assertEquals(2, RomanNumeralConverter.extractSeasonNumber("Season 2"))
        assertEquals(10, RomanNumeralConverter.extractSeasonNumber("Season 10"))
    }

    @Test
    fun `extractSeasonNumber returns null for invalid format`() {
        assertNull(RomanNumeralConverter.extractSeasonNumber(""))
        assertNull(RomanNumeralConverter.extractSeasonNumber("Season"))
        assertNull(RomanNumeralConverter.extractSeasonNumber("Episode 1"))
        assertNull(RomanNumeralConverter.extractSeasonNumber("Season One"))
        assertNull(RomanNumeralConverter.extractSeasonNumber("series 1"))
    }

    @Test
    fun `extractSeasonNumber handles edge cases`() {
        assertNull(RomanNumeralConverter.extractSeasonNumber("Season "))
        assertNull(RomanNumeralConverter.extractSeasonNumber("Season abc"))
        assertEquals(123, RomanNumeralConverter.extractSeasonNumber("Season 123"))
    }
}
