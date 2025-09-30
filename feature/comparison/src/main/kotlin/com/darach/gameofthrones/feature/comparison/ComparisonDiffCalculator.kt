package com.darach.gameofthrones.feature.comparison

import com.darach.gameofthrones.core.domain.model.Character
import javax.inject.Inject

/**
 * Calculates differences between characters for comparison highlighting.
 * Processes all character attributes and marks differences.
 */
class ComparisonDiffCalculator @Inject constructor() {

    /**
     * Calculates comparison result with difference highlighting.
     * @param characters List of 2-3 characters to compare
     * @return ComparisonResult with all attributes and difference flags
     */
    fun calculate(characters: List<Character>): ComparisonResult {
        require(characters.size in 2..3) {
            "Comparison requires 2-3 characters, got ${characters.size}"
        }

        val attributes = buildList {
            add(createAttribute("Name", characters.map { it.name }))
            add(createAttribute("Gender", characters.map { it.gender }))
            add(createAttribute("Culture", characters.map { it.culture }))
            add(createAttribute("Born", characters.map { it.born }))
            add(createAttribute("Died", characters.map { it.died }))
            add(
                createAttribute(
                    "Status",
                    characters.map { if (it.isDead) "Dead" else "Alive" }
                )
            )
            add(createListAttribute("Titles", characters.map { it.titles }))
            add(createListAttribute("Aliases", characters.map { it.aliases }))
            add(createAttribute("Father", characters.map { it.father }))
            add(createAttribute("Mother", characters.map { it.mother }))
            add(createAttribute("Spouse", characters.map { it.spouse }))
            add(createListAttribute("Allegiances", characters.map { it.allegiances }))
            add(createListAttribute("Books", characters.map { it.books }))
            add(createListAttribute("POV Books", characters.map { it.povBooks }))
            add(createListAttribute("TV Series", characters.map { it.tvSeries }))
            add(
                createAttribute(
                    "TV Seasons",
                    characters.map { formatSeasons(it.tvSeriesSeasons) }
                )
            )
            add(createListAttribute("Played By", characters.map { it.playedBy }))
        }

        return ComparisonResult(
            characters = characters,
            attributes = attributes
        )
    }

    private fun createAttribute(name: String, values: List<String>): ComparisonAttribute {
        val normalizedValues = values.map { it.ifEmpty { "Unknown" } }
        val hasDifference = normalizedValues.toSet().size > 1

        return ComparisonAttribute(
            name = name,
            values = normalizedValues.map { value ->
                AttributeValue(
                    value = value,
                    isDifferent =
                    hasDifference &&
                        normalizedValues.count { it == value } < normalizedValues.size,
                    isEmpty = value == "Unknown"
                )
            },
            hasDifference = hasDifference
        )
    }

    private fun createListAttribute(name: String, lists: List<List<String>>): ComparisonAttribute {
        val formattedValues = lists.map { list ->
            if (list.isEmpty()) "None" else list.joinToString(", ")
        }

        val hasDifference = lists.map { it.toSet() }.toSet().size > 1

        return ComparisonAttribute(
            name = name,
            values = formattedValues.mapIndexed { index, value ->
                val isEmpty = lists[index].isEmpty()
                AttributeValue(
                    value = value,
                    isDifferent = hasDifference && !isEmpty,
                    isEmpty = isEmpty
                )
            },
            hasDifference = hasDifference
        )
    }

    private fun formatSeasons(seasons: List<Int>): String = if (seasons.isEmpty()) {
        "None"
    } else {
        seasons.sorted().joinToString(", ")
    }
}
