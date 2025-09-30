package com.darach.gameofthrones.core.domain.util

object RomanNumeralConverter {
    private val romanNumerals = mapOf(
        1 to "I",
        2 to "II",
        3 to "III",
        4 to "IV",
        5 to "V",
        6 to "VI",
        7 to "VII",
        8 to "VIII",
        9 to "IX",
        10 to "X"
    )

    fun toRomanNumeral(season: Int): String = romanNumerals[season] ?: season.toString()

    fun extractSeasonNumber(tvSeriesEntry: String): Int? {
        val seasonPattern = """Season (\d+)""".toRegex()
        return seasonPattern.find(tvSeriesEntry)?.groupValues?.get(1)?.toIntOrNull()
    }
}
