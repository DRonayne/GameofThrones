package com.darach.gameofthrones.feature.comparison

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.darach.gameofthrones.core.domain.model.Character
import com.darach.gameofthrones.feature.comparison.components.ComparisonSelectionBar
import com.darach.gameofthrones.feature.comparison.components.ComparisonSelectionBarCallbacks
import com.darach.gameofthrones.feature.comparison.components.ComparisonSelectionBarState
import com.darach.gameofthrones.feature.comparison.components.SelectableCharacterCard
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

    // SelectableCharacterCard Tests
    @Test
    fun selectableCharacterCard_displaysCharacterName() {
        composeTestRule.setContent {
            SelectableCharacterCard(
                character = testCharacters[0],
                isSelected = false,
                isSelectionEnabled = false,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("Jon Snow").assertIsDisplayed()
    }

    @Test
    fun selectableCharacterCard_displaysCulture() {
        composeTestRule.setContent {
            SelectableCharacterCard(
                character = testCharacters[0],
                isSelected = false,
                isSelectionEnabled = false,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("Northmen").assertIsDisplayed()
    }

    @Test
    fun selectableCharacterCard_displaysSeasonBadges() {
        composeTestRule.setContent {
            SelectableCharacterCard(
                character = testCharacters[0],
                isSelected = false,
                isSelectionEnabled = false,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("I").assertIsDisplayed()
    }

    @Test
    fun selectableCharacterCard_displaysAlias() {
        composeTestRule.setContent {
            SelectableCharacterCard(
                character = testCharacters[0],
                isSelected = false,
                isSelectionEnabled = false,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("Also known as: Lord Snow").assertIsDisplayed()
    }

    @Test
    fun selectableCharacterCard_displaysDeathIndicator() {
        val deadCharacter = testCharacters[0].copy(died = "300 AC", isDead = true)

        composeTestRule.setContent {
            SelectableCharacterCard(
                character = deadCharacter,
                isSelected = false,
                isSelectionEnabled = false,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Deceased").assertIsDisplayed()
    }

    @Test
    fun selectableCharacterCard_showsCheckIconWhenSelected() {
        composeTestRule.setContent {
            SelectableCharacterCard(
                character = testCharacters[0],
                isSelected = true,
                isSelectionEnabled = true,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Selected").assertIsDisplayed()
    }

    @Test
    fun selectableCharacterCard_hidesCheckIconWhenNotSelected() {
        composeTestRule.setContent {
            SelectableCharacterCard(
                character = testCharacters[0],
                isSelected = false,
                isSelectionEnabled = true,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Selected").assertDoesNotExist()
    }

    @Test
    fun selectableCharacterCard_clickCallsCallback() {
        var clicked = false

        composeTestRule.setContent {
            SelectableCharacterCard(
                character = testCharacters[0],
                isSelected = false,
                isSelectionEnabled = false,
                onClick = { clicked = true }
            )
        }

        composeTestRule.onNodeWithText("Jon Snow").performClick()

        assert(clicked)
    }

    // ComparisonSelectionBar Tests
    @Test
    fun comparisonSelectionBar_displaysSelectionCount() {
        composeTestRule.setContent {
            ComparisonSelectionBar(
                state = ComparisonSelectionBarState(
                    selectionCount = 2,
                    maxSelection = 3,
                    canCompare = true
                ),
                callbacks = ComparisonSelectionBarCallbacks(
                    onClearClick = {},
                    onCompareClick = {},
                    onCloseClick = {}
                )
            )
        }

        composeTestRule.onNodeWithText("2 / 3 selected").assertIsDisplayed()
    }

    @Test
    fun comparisonSelectionBar_displaysMaximumIndicator() {
        composeTestRule.setContent {
            ComparisonSelectionBar(
                state = ComparisonSelectionBarState(
                    selectionCount = 3,
                    maxSelection = 3,
                    canCompare = true
                ),
                callbacks = ComparisonSelectionBarCallbacks(
                    onClearClick = {},
                    onCompareClick = {},
                    onCloseClick = {}
                )
            )
        }

        composeTestRule.onNodeWithText("(Maximum)").assertIsDisplayed()
    }

    @Test
    fun comparisonSelectionBar_hidesMaximumIndicatorWhenNotAtMax() {
        composeTestRule.setContent {
            ComparisonSelectionBar(
                state = ComparisonSelectionBarState(
                    selectionCount = 2,
                    maxSelection = 3,
                    canCompare = true
                ),
                callbacks = ComparisonSelectionBarCallbacks(
                    onClearClick = {},
                    onCompareClick = {},
                    onCloseClick = {}
                )
            )
        }

        composeTestRule.onNodeWithText("(Maximum)").assertDoesNotExist()
    }

    @Test
    fun comparisonSelectionBar_clearButtonCallsCallback() {
        var clearClicked = false

        composeTestRule.setContent {
            ComparisonSelectionBar(
                state = ComparisonSelectionBarState(
                    selectionCount = 2,
                    maxSelection = 3,
                    canCompare = true
                ),
                callbacks = ComparisonSelectionBarCallbacks(
                    onClearClick = { clearClicked = true },
                    onCompareClick = {},
                    onCloseClick = {}
                )
            )
        }

        composeTestRule.onNodeWithText("Clear").performClick()

        assert(clearClicked)
    }

    @Test
    fun comparisonSelectionBar_compareButtonCallsCallback() {
        var compareClicked = false

        composeTestRule.setContent {
            ComparisonSelectionBar(
                state = ComparisonSelectionBarState(
                    selectionCount = 2,
                    maxSelection = 3,
                    canCompare = true
                ),
                callbacks = ComparisonSelectionBarCallbacks(
                    onClearClick = {},
                    onCompareClick = { compareClicked = true },
                    onCloseClick = {}
                )
            )
        }

        composeTestRule.onNodeWithText("Compare").performClick()

        assert(compareClicked)
    }

    @Test
    fun comparisonSelectionBar_closeButtonCallsCallback() {
        var closeClicked = false

        composeTestRule.setContent {
            ComparisonSelectionBar(
                state = ComparisonSelectionBarState(
                    selectionCount = 2,
                    maxSelection = 3,
                    canCompare = true
                ),
                callbacks = ComparisonSelectionBarCallbacks(
                    onClearClick = {},
                    onCompareClick = {},
                    onCloseClick = { closeClicked = true }
                )
            )
        }

        composeTestRule.onNodeWithContentDescription("Close selection mode").performClick()

        assert(closeClicked)
    }

    @Test
    fun comparisonSelectionBar_compareButtonDisabledWhenCannotCompare() {
        composeTestRule.setContent {
            ComparisonSelectionBar(
                state = ComparisonSelectionBarState(
                    selectionCount = 1,
                    maxSelection = 3,
                    canCompare = false
                ),
                callbacks = ComparisonSelectionBarCallbacks(
                    onClearClick = {},
                    onCompareClick = {},
                    onCloseClick = {}
                )
            )
        }

        composeTestRule.onNodeWithText("Compare").assertIsNotEnabled()
    }

    @Test
    fun comparisonSelectionBar_compareButtonEnabledWhenCanCompare() {
        composeTestRule.setContent {
            ComparisonSelectionBar(
                state = ComparisonSelectionBarState(
                    selectionCount = 2,
                    maxSelection = 3,
                    canCompare = true
                ),
                callbacks = ComparisonSelectionBarCallbacks(
                    onClearClick = {},
                    onCompareClick = {},
                    onCloseClick = {}
                )
            )
        }

        composeTestRule.onNodeWithText("Compare").assertIsEnabled()
    }

    @Test
    fun comparisonSelectionBar_hiddenWhenNoSelection() {
        composeTestRule.setContent {
            ComparisonSelectionBar(
                state = ComparisonSelectionBarState(
                    selectionCount = 0,
                    maxSelection = 3,
                    canCompare = false
                ),
                callbacks = ComparisonSelectionBarCallbacks(
                    onClearClick = {},
                    onCompareClick = {},
                    onCloseClick = {}
                )
            )
        }

        composeTestRule.onNodeWithText("Clear").assertDoesNotExist()
        composeTestRule.onNodeWithText("Compare").assertDoesNotExist()
    }
}
