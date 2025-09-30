package com.darach.gameofthrones.feature.comparison

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.darach.gameofthrones.core.domain.model.Character
import org.junit.Rule
import org.junit.Test

class ComparisonSelectionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testCharacters = listOf(
        Character(
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
            allegiances = emptyList(),
            books = emptyList(),
            povBooks = emptyList(),
            tvSeries = listOf("Season 1"),
            tvSeriesSeasons = listOf(1),
            playedBy = listOf("Kit Harington")
        ),
        Character(
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
            allegiances = emptyList(),
            books = emptyList(),
            povBooks = emptyList(),
            tvSeries = listOf("Season 1"),
            tvSeriesSeasons = listOf(1),
            playedBy = listOf("Maisie Williams")
        ),
        Character(
            id = "3",
            name = "Sansa Stark",
            gender = "Female",
            culture = "Northmen",
            born = "286 AC",
            died = "",
            titles = listOf("Queen in the North"),
            aliases = listOf("Little Bird"),
            father = "",
            mother = "",
            spouse = "",
            allegiances = emptyList(),
            books = emptyList(),
            povBooks = emptyList(),
            tvSeries = listOf("Season 1"),
            tvSeriesSeasons = listOf(1),
            playedBy = listOf("Sophie Turner")
        )
    )

    @Test
    fun displaysTitle() {
        composeTestRule.setContent {
            ComparisonSelectionScreen(
                characters = testCharacters,
                selectedCharacters = emptyList(),
                callbacks = ComparisonSelectionCallbacks(
                    onCharacterToggle = {},
                    onCompareClick = {},
                    onClearSelection = {},
                    onBackClick = {}
                )
            )
        }

        composeTestRule
            .onNodeWithText("Select Characters to Compare")
            .assertIsDisplayed()
    }

    @Test
    fun displaysCharacterList() {
        composeTestRule.setContent {
            ComparisonSelectionScreen(
                characters = testCharacters,
                selectedCharacters = emptyList(),
                callbacks = ComparisonSelectionCallbacks(
                    onCharacterToggle = {},
                    onCompareClick = {},
                    onClearSelection = {},
                    onBackClick = {}
                )
            )
        }

        composeTestRule.onNodeWithText("Jon Snow").assertIsDisplayed()
        composeTestRule.onNodeWithText("Arya Stark").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sansa Stark").assertIsDisplayed()
    }

    @Test
    fun compareButtonIsDisabledWithNoSelection() {
        composeTestRule.setContent {
            ComparisonSelectionScreen(
                characters = testCharacters,
                selectedCharacters = emptyList(),
                callbacks = ComparisonSelectionCallbacks(
                    onCharacterToggle = {},
                    onCompareClick = {},
                    onClearSelection = {},
                    onBackClick = {}
                )
            )
        }

        // Selection bar should not be visible when no selection
        composeTestRule
            .onNodeWithText("Compare")
            .assertDoesNotExist()
    }

    @Test
    fun compareButtonIsDisabledWithOneSelection() {
        composeTestRule.setContent {
            ComparisonSelectionScreen(
                characters = testCharacters,
                selectedCharacters = listOf(testCharacters[0]),
                callbacks = ComparisonSelectionCallbacks(
                    onCharacterToggle = {},
                    onCompareClick = {},
                    onClearSelection = {},
                    onBackClick = {}
                )
            )
        }

        composeTestRule
            .onNodeWithText("Compare")
            .assertIsNotEnabled()
    }

    @Test
    fun compareButtonIsEnabledWithTwoSelections() {
        composeTestRule.setContent {
            ComparisonSelectionScreen(
                characters = testCharacters,
                selectedCharacters = listOf(testCharacters[0], testCharacters[1]),
                callbacks = ComparisonSelectionCallbacks(
                    onCharacterToggle = {},
                    onCompareClick = {},
                    onClearSelection = {},
                    onBackClick = {}
                )
            )
        }

        composeTestRule
            .onNodeWithText("Compare")
            .assertIsEnabled()
    }

    @Test
    fun displaysSelectionCounter() {
        composeTestRule.setContent {
            ComparisonSelectionScreen(
                characters = testCharacters,
                selectedCharacters = listOf(testCharacters[0], testCharacters[1]),
                callbacks = ComparisonSelectionCallbacks(
                    onCharacterToggle = {},
                    onCompareClick = {},
                    onClearSelection = {},
                    onBackClick = {}
                )
            )
        }

        composeTestRule
            .onNodeWithText("2 / 3 selected")
            .assertIsDisplayed()
    }

    @Test
    fun displaysMaximumIndicatorAtThreeSelections() {
        composeTestRule.setContent {
            ComparisonSelectionScreen(
                characters = testCharacters,
                selectedCharacters = testCharacters,
                callbacks = ComparisonSelectionCallbacks(
                    onCharacterToggle = {},
                    onCompareClick = {},
                    onClearSelection = {},
                    onBackClick = {}
                )
            )
        }

        composeTestRule
            .onNodeWithText("3 / 3 selected")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("(Maximum)")
            .assertIsDisplayed()
    }

    @Test
    fun clearButtonClearsSelection() {
        var clearClicked = false

        composeTestRule.setContent {
            ComparisonSelectionScreen(
                characters = testCharacters,
                selectedCharacters = listOf(testCharacters[0]),
                callbacks = ComparisonSelectionCallbacks(
                    onCharacterToggle = {},
                    onCompareClick = {},
                    onClearSelection = { clearClicked = true },
                    onBackClick = {}
                )
            )
        }

        composeTestRule
            .onNodeWithText("Clear")
            .performClick()

        assert(clearClicked)
    }

    @Test
    fun backButtonCallsOnBackClick() {
        var backClicked = false

        composeTestRule.setContent {
            ComparisonSelectionScreen(
                characters = testCharacters,
                selectedCharacters = emptyList(),
                callbacks = ComparisonSelectionCallbacks(
                    onCharacterToggle = {},
                    onCompareClick = {},
                    onClearSelection = {},
                    onBackClick = { backClicked = true }
                )
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        assert(backClicked)
    }

    @Test
    fun showsEmptyStateWithNoCharacters() {
        composeTestRule.setContent {
            ComparisonSelectionScreen(
                characters = emptyList(),
                selectedCharacters = emptyList(),
                callbacks = ComparisonSelectionCallbacks(
                    onCharacterToggle = {},
                    onCompareClick = {},
                    onClearSelection = {},
                    onBackClick = {}
                )
            )
        }

        composeTestRule
            .onNodeWithText("No characters available for comparison")
            .assertIsDisplayed()
    }
}
