package com.darach.gameofthrones.feature.comparison

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.darach.gameofthrones.core.domain.model.Character
import org.junit.Rule
import org.junit.Test

class ComparisonScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

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
        allegiances = listOf("House Stark"),
        books = listOf("A Game of Thrones"),
        povBooks = listOf("A Dance with Dragons"),
        tvSeries = listOf("Season 1"),
        tvSeriesSeasons = listOf(1, 2),
        playedBy = listOf("Kit Harington")
    )

    private val testCharacter2 = Character(
        id = "2",
        name = "Arya Stark",
        gender = "Female",
        culture = "Northmen",
        born = "289 AC",
        died = "",
        titles = emptyList(),
        aliases = listOf("No One"),
        father = "Eddard Stark",
        mother = "Catelyn Stark",
        spouse = "",
        allegiances = listOf("House Stark"),
        books = listOf("A Game of Thrones"),
        povBooks = listOf("A Game of Thrones"),
        tvSeries = listOf("Season 1"),
        tvSeriesSeasons = listOf(1, 2),
        playedBy = listOf("Maisie Williams")
    )

    private val testComparisonResult = ComparisonResult(
        characters = listOf(testCharacter1, testCharacter2),
        attributes = listOf(
            ComparisonAttribute(
                name = "Name",
                values = listOf(
                    AttributeValue("Jon Snow", isDifferent = true),
                    AttributeValue("Arya Stark", isDifferent = true)
                ),
                hasDifference = true
            ),
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
            )
        )
    )

    @Test
    fun displaysTitle() {
        composeTestRule.setContent {
            ComparisonScreen(
                comparisonResult = testComparisonResult,
                isLoading = false,
                error = null,
                onBackClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("Character Comparison")
            .assertIsDisplayed()
    }

    @Test
    fun displaysCharacterNames() {
        composeTestRule.setContent {
            ComparisonScreen(
                comparisonResult = testComparisonResult,
                isLoading = false,
                error = null,
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithText("Jon Snow").assertIsDisplayed()
        composeTestRule.onNodeWithText("Arya Stark").assertIsDisplayed()
    }

    @Test
    fun displaysAttributeNames() {
        composeTestRule.setContent {
            ComparisonScreen(
                comparisonResult = testComparisonResult,
                isLoading = false,
                error = null,
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithText("Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gender").assertIsDisplayed()
        composeTestRule.onNodeWithText("Culture").assertIsDisplayed()
    }

    @Test
    fun displaysAttributeValues() {
        composeTestRule.setContent {
            ComparisonScreen(
                comparisonResult = testComparisonResult,
                isLoading = false,
                error = null,
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithText("Male").assertIsDisplayed()
        composeTestRule.onNodeWithText("Female").assertIsDisplayed()
        composeTestRule.onNodeWithText("Northmen").assertIsDisplayed()
    }

    @Test
    fun displaysLoadingState() {
        composeTestRule.setContent {
            ComparisonScreen(
                comparisonResult = null,
                isLoading = true,
                error = null,
                onBackClick = {}
            )
        }

        // CircularProgressIndicator should be displayed
        // We don't have a specific text to check, but we can verify
        // that the comparison content is not displayed
        composeTestRule
            .onNodeWithText("Attribute")
            .assertDoesNotExist()
    }

    @Test
    fun displaysErrorState() {
        val errorMessage = "Failed to compare characters"

        composeTestRule.setContent {
            ComparisonScreen(
                comparisonResult = null,
                isLoading = false,
                error = errorMessage,
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithText("Error").assertIsDisplayed()
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun displaysEmptyState() {
        composeTestRule.setContent {
            ComparisonScreen(
                comparisonResult = null,
                isLoading = false,
                error = null,
                onBackClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("No comparison data available")
            .assertIsDisplayed()
    }

    @Test
    fun backButtonCallsOnBackClick() {
        var backClicked = false

        composeTestRule.setContent {
            ComparisonScreen(
                comparisonResult = testComparisonResult,
                isLoading = false,
                error = null,
                onBackClick = { backClicked = true }
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        assert(backClicked)
    }

    @Test
    fun displaysAttributeHeader() {
        composeTestRule.setContent {
            ComparisonScreen(
                comparisonResult = testComparisonResult,
                isLoading = false,
                error = null,
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithText("Attribute").assertIsDisplayed()
    }
}
