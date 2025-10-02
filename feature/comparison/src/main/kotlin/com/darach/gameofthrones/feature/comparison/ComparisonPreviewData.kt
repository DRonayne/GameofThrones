package com.darach.gameofthrones.feature.comparison

import com.darach.gameofthrones.core.model.Character

/**
 * Lightweight preview data for ComparisonScreen previews.
 * Separated to prevent memory leaks in preview compilation.
 */
internal object ComparisonPreviewData {

    val jonSnow = Character(
        id = "583",
        name = "Jon Snow",
        gender = "Male",
        culture = "Northmen",
        born = "In 283 AC",
        died = "",
        titles = listOf("Lord Commander of the Night's Watch", "King in the North"),
        aliases = listOf("Lord Snow", "The White Wolf"),
        father = "",
        mother = "",
        spouse = "",
        allegiances = listOf(),
        books = listOf(),
        povBooks = listOf(),
        tvSeries = listOf("Season 1", "Season 2", "Season 3", "Season 4", "Season 5", "Season 6"),
        tvSeriesSeasons = listOf(1, 2, 3, 4, 5, 6, 7, 8),
        playedBy = listOf("Kit Harington"),
        isFavorite = true,
        isDead = false
    )

    val aryaStark = Character(
        id = "148",
        name = "Arya Stark",
        gender = "Female",
        culture = "Northmen",
        born = "In 289 AC",
        died = "",
        titles = listOf("Princess"),
        aliases = listOf("No One", "Cat of the Canals"),
        father = "",
        mother = "",
        spouse = "",
        allegiances = listOf(),
        books = listOf(),
        povBooks = listOf(),
        tvSeries = listOf("Season 1", "Season 2", "Season 3"),
        tvSeriesSeasons = listOf(1, 2, 3, 4, 5, 6, 7, 8),
        playedBy = listOf("Maisie Williams"),
        isFavorite = true,
        isDead = false
    )

    val comparisonResult = ComparisonResult(
        characters = listOf(jonSnow, aryaStark),
        attributes = listOf(
            ComparisonAttribute(
                name = "Gender",
                values = listOf(
                    AttributeValue("Male", isDifferent = true),
                    AttributeValue("Female", isDifferent = true)
                ),
                hasDifference = true
            ),
            ComparisonAttribute(
                name = "Culture",
                values = listOf(
                    AttributeValue("Northmen", isDifferent = false),
                    AttributeValue("Northmen", isDifferent = false)
                ),
                hasDifference = false
            ),
            ComparisonAttribute(
                name = "Born",
                values = listOf(
                    AttributeValue("In 283 AC", isDifferent = true),
                    AttributeValue("In 289 AC", isDifferent = true)
                ),
                hasDifference = true
            ),
            ComparisonAttribute(
                name = "Status",
                values = listOf(
                    AttributeValue("Alive", isDifferent = false),
                    AttributeValue("Alive", isDifferent = false)
                ),
                hasDifference = false
            ),
            ComparisonAttribute(
                name = "Seasons",
                values = listOf(
                    AttributeValue("8 seasons", isDifferent = false),
                    AttributeValue("8 seasons", isDifferent = false)
                ),
                hasDifference = false
            )
        )
    )
}
