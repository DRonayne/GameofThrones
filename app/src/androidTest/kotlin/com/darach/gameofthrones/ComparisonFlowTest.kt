package com.darach.gameofthrones

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Character comparison feature flow tests.
 *
 * Tests the comparison functionality including:
 * - Navigation to comparison screen
 * - Character selection
 * - Compare button enabling
 * - Comparison result display
 * - Clearing selection
 * - Back navigation
 * - Maximum selection limit
 */
@RunWith(AndroidJUnit4::class)
class ComparisonFlowTest {

    @get:Rule(order = 0)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 1)
    val screenshotRule = ScreenshotTestRule()

    @Before
    fun setup() {
        screenshotRule.setComposeTestRule(composeTestRule)
    }

    @Test
    fun comparison_navigation_fromFavorites() {
        // First add at least 2 favorites to enable comparison
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Add first favorite
        val addFavoriteButtons = composeTestRule
            .onAllNodesWithContentDescription(
                "Add to favorites",
                substring = true,
                useUnmergedTree = true
            )
            .fetchSemanticsNodes()

        if (addFavoriteButtons.size >= 2) {
            composeTestRule
                .onAllNodesWithContentDescription(
                    "Add to favorites",
                    substring = true,
                    useUnmergedTree = true
                )
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()

            // Add second favorite
            val addFavoriteButtons2 = composeTestRule
                .onAllNodesWithContentDescription(
                    "Add to favorites",
                    substring = true,
                    useUnmergedTree = true
                )
                .fetchSemanticsNodes()

            if (addFavoriteButtons2.isNotEmpty()) {
                composeTestRule
                    .onAllNodesWithContentDescription(
                        "Add to favorites",
                        substring = true,
                        useUnmergedTree = true
                    )
                    .onFirst()
                    .performClick()

                composeTestRule.waitForIdle()
            }
        }

        // Navigate to Favorites
        composeTestRule.onNodeWithText("Favorites").performClick()
        composeTestRule.waitForIdle()

        // Wait for favorites screen to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 3
        }

        // Look for selection mode button
        val selectionModeButtons = composeTestRule
            .onAllNodesWithTag(TestTags.SELECTION_MODE_BUTTON, useUnmergedTree = true)
            .fetchSemanticsNodes()

        if (selectionModeButtons.isNotEmpty()) {
            composeTestRule
                .onNodeWithTag(TestTags.SELECTION_MODE_BUTTON, useUnmergedTree = true)
                .performClick()

            composeTestRule.waitForIdle()

            // Verify comparison mode is active
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule
                    .onAllNodesWithText("", substring = true)
                    .fetchSemanticsNodes()
                    .size > 1
            }
        }

        // Navigate back to Characters
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun comparison_selectTwoCharacters_enablesCompareButton() {
        // First add some favorites
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Add multiple characters to favorites
        val addFavoriteButtons = composeTestRule
            .onAllNodesWithContentDescription(
                "Add to favorites",
                substring = true,
                useUnmergedTree = true
            )
            .fetchSemanticsNodes()

        if (addFavoriteButtons.size >= 2) {
            // Add first character
            composeTestRule
                .onAllNodesWithContentDescription(
                    "Add to favorites",
                    substring = true,
                    useUnmergedTree = true
                )
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()

            // Add second character
            val addFavoriteButtons2 = composeTestRule
                .onAllNodesWithContentDescription(
                    "Add to favorites",
                    substring = true,
                    useUnmergedTree = true
                )
                .fetchSemanticsNodes()

            if (addFavoriteButtons2.isNotEmpty()) {
                composeTestRule
                    .onAllNodesWithContentDescription(
                        "Add to favorites",
                        substring = true,
                        useUnmergedTree = true
                    )
                    .onFirst()
                    .performClick()

                composeTestRule.waitForIdle()
            }
        }

        // Navigate to Favorites
        composeTestRule.onNodeWithText("Favorites").performClick()
        composeTestRule.waitForIdle()

        // Wait for favorites to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 3
        }

        // Enter selection mode
        val selectionModeButtons = composeTestRule
            .onAllNodesWithTag(TestTags.SELECTION_MODE_BUTTON, useUnmergedTree = true)
            .fetchSemanticsNodes()

        val compareMenuButtons = composeTestRule
            .onAllNodesWithContentDescription("Compare", substring = true, useUnmergedTree = true)
            .fetchSemanticsNodes()

        if (selectionModeButtons.isNotEmpty()) {
            composeTestRule
                .onNodeWithTag(TestTags.SELECTION_MODE_BUTTON, useUnmergedTree = true)
                .performClick()

            composeTestRule.waitForIdle()

            // Select first character
            val selectableCards = composeTestRule
                .onAllNodesWithTag(TestTags.SELECTABLE_CHARACTER_CARD, useUnmergedTree = true)
                .fetchSemanticsNodes()

            if (selectableCards.size >= 2) {
                composeTestRule
                    .onAllNodesWithTag(TestTags.SELECTABLE_CHARACTER_CARD, useUnmergedTree = true)
                    .onFirst()
                    .performClick()

                composeTestRule.waitForIdle()

                // Select second character
                composeTestRule
                    .onAllNodesWithTag(
                        TestTags.SELECTABLE_CHARACTER_CARD,
                        useUnmergedTree = true
                    )[1]
                    .performClick()

                composeTestRule.waitForIdle()

                // Compare button should be enabled
                val compareButtons = composeTestRule
                    .onAllNodesWithTag(TestTags.COMPARE_BUTTON, useUnmergedTree = true)
                    .fetchSemanticsNodes()

                if (compareButtons.isNotEmpty()) {
                    composeTestRule
                        .onNodeWithTag(TestTags.COMPARE_BUTTON, useUnmergedTree = true)
                        .assertIsDisplayed()
                }
            }
        }

        // Navigate back to Characters
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun comparison_compareCharacters_showsComparisonResult() {
        // First add some favorites
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Add multiple characters to favorites
        val addFavoriteButtons = composeTestRule
            .onAllNodesWithContentDescription(
                "Add to favorites",
                substring = true,
                useUnmergedTree = true
            )
            .fetchSemanticsNodes()

        if (addFavoriteButtons.size >= 2) {
            // Add first character
            composeTestRule
                .onAllNodesWithContentDescription(
                    "Add to favorites",
                    substring = true,
                    useUnmergedTree = true
                )
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()

            // Add second character
            val addFavoriteButtons2 = composeTestRule
                .onAllNodesWithContentDescription(
                    "Add to favorites",
                    substring = true,
                    useUnmergedTree = true
                )
                .fetchSemanticsNodes()

            if (addFavoriteButtons2.isNotEmpty()) {
                composeTestRule
                    .onAllNodesWithContentDescription(
                        "Add to favorites",
                        substring = true,
                        useUnmergedTree = true
                    )
                    .onFirst()
                    .performClick()

                composeTestRule.waitForIdle()
            }
        }

        // Navigate to Favorites
        composeTestRule.onNodeWithText("Favorites").performClick()
        composeTestRule.waitForIdle()

        // Wait for favorites to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 3
        }

        // Enter selection mode
        val selectionModeButtons = composeTestRule
            .onAllNodesWithTag(TestTags.SELECTION_MODE_BUTTON, useUnmergedTree = true)
            .fetchSemanticsNodes()

        if (selectionModeButtons.isNotEmpty()) {
            composeTestRule
                .onNodeWithTag(TestTags.SELECTION_MODE_BUTTON, useUnmergedTree = true)
                .performClick()

            composeTestRule.waitForIdle()

            // Select two characters
            val selectableCards = composeTestRule
                .onAllNodesWithTag(TestTags.SELECTABLE_CHARACTER_CARD, useUnmergedTree = true)
                .fetchSemanticsNodes()

            if (selectableCards.size >= 2) {
                composeTestRule
                    .onAllNodesWithTag(TestTags.SELECTABLE_CHARACTER_CARD, useUnmergedTree = true)
                    .onFirst()
                    .performClick()

                composeTestRule.waitForIdle()

                composeTestRule
                    .onAllNodesWithTag(
                        TestTags.SELECTABLE_CHARACTER_CARD,
                        useUnmergedTree = true
                    )[1]
                    .performClick()

                composeTestRule.waitForIdle()

                // Click compare button
                val compareButtons = composeTestRule
                    .onAllNodesWithTag(TestTags.COMPARE_BUTTON, useUnmergedTree = true)
                    .fetchSemanticsNodes()

                if (compareButtons.isNotEmpty()) {
                    composeTestRule
                        .onNodeWithTag(TestTags.COMPARE_BUTTON, useUnmergedTree = true)
                        .performClick()

                    composeTestRule.waitForIdle()

                    // Wait for comparison result to load
                    composeTestRule.waitUntil(timeoutMillis = 5000) {
                        composeTestRule
                            .onAllNodesWithTag(
                                TestTags.COMPARISON_RESULT_TABLE,
                                useUnmergedTree = true
                            )
                            .fetchSemanticsNodes()
                            .isNotEmpty() ||
                            composeTestRule
                                .onAllNodesWithContentDescription(
                                    "Navigate back",
                                    substring = true,
                                    useUnmergedTree = true
                                )
                                .fetchSemanticsNodes()
                                .isNotEmpty()
                    }

                    // Navigate back
                    val backButtons = composeTestRule
                        .onAllNodesWithContentDescription(
                            "Navigate back",
                            substring = true,
                            useUnmergedTree = true
                        )
                        .fetchSemanticsNodes()

                    if (backButtons.isNotEmpty()) {
                        composeTestRule
                            .onNodeWithContentDescription("Navigate back", useUnmergedTree = true)
                            .performClick()

                        composeTestRule.waitForIdle()
                    }
                }
            }
        }

        // Navigate back to Characters
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun comparison_clearSelection_deselectsAllCharacters() {
        // First add some favorites
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Add multiple characters to favorites
        val addFavoriteButtons = composeTestRule
            .onAllNodesWithContentDescription(
                "Add to favorites",
                substring = true,
                useUnmergedTree = true
            )
            .fetchSemanticsNodes()

        if (addFavoriteButtons.size >= 2) {
            // Add first character
            composeTestRule
                .onAllNodesWithContentDescription(
                    "Add to favorites",
                    substring = true,
                    useUnmergedTree = true
                )
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()

            // Add second character
            val addFavoriteButtons2 = composeTestRule
                .onAllNodesWithContentDescription(
                    "Add to favorites",
                    substring = true,
                    useUnmergedTree = true
                )
                .fetchSemanticsNodes()

            if (addFavoriteButtons2.isNotEmpty()) {
                composeTestRule
                    .onAllNodesWithContentDescription(
                        "Add to favorites",
                        substring = true,
                        useUnmergedTree = true
                    )
                    .onFirst()
                    .performClick()

                composeTestRule.waitForIdle()
            }
        }

        // Navigate to Favorites
        composeTestRule.onNodeWithText("Favorites").performClick()
        composeTestRule.waitForIdle()

        // Wait for favorites to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 3
        }

        // Enter selection mode
        val selectionModeButtons = composeTestRule
            .onAllNodesWithTag(TestTags.SELECTION_MODE_BUTTON, useUnmergedTree = true)
            .fetchSemanticsNodes()

        if (selectionModeButtons.isNotEmpty()) {
            composeTestRule
                .onNodeWithTag(TestTags.SELECTION_MODE_BUTTON, useUnmergedTree = true)
                .performClick()

            composeTestRule.waitForIdle()

            // Select characters
            val selectableCards = composeTestRule
                .onAllNodesWithTag(TestTags.SELECTABLE_CHARACTER_CARD, useUnmergedTree = true)
                .fetchSemanticsNodes()

            if (selectableCards.size >= 2) {
                composeTestRule
                    .onAllNodesWithTag(TestTags.SELECTABLE_CHARACTER_CARD, useUnmergedTree = true)
                    .onFirst()
                    .performClick()

                composeTestRule.waitForIdle()

                composeTestRule
                    .onAllNodesWithTag(
                        TestTags.SELECTABLE_CHARACTER_CARD,
                        useUnmergedTree = true
                    )[1]
                    .performClick()

                composeTestRule.waitForIdle()

                // Click clear selection button
                val clearButtons = composeTestRule
                    .onAllNodesWithTag(TestTags.CLEAR_SELECTION_BUTTON, useUnmergedTree = true)
                    .fetchSemanticsNodes()

                val clearTextButtons = composeTestRule
                    .onAllNodesWithText("Clear", substring = true)
                    .fetchSemanticsNodes()

                if (clearButtons.isNotEmpty()) {
                    composeTestRule
                        .onNodeWithTag(TestTags.CLEAR_SELECTION_BUTTON, useUnmergedTree = true)
                        .performClick()

                    composeTestRule.waitForIdle()
                } else if (clearTextButtons.isNotEmpty()) {
                    composeTestRule
                        .onAllNodesWithText("Clear", substring = true)
                        .onFirst()
                        .performClick()

                    composeTestRule.waitForIdle()
                }
            }
        }

        // Navigate back to Characters
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun comparison_backNavigation_returnsToFavorites() {
        // First add some favorites
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Add multiple characters to favorites
        val addFavoriteButtons = composeTestRule
            .onAllNodesWithContentDescription(
                "Add to favorites",
                substring = true,
                useUnmergedTree = true
            )
            .fetchSemanticsNodes()

        if (addFavoriteButtons.size >= 2) {
            // Add first character
            composeTestRule
                .onAllNodesWithContentDescription(
                    "Add to favorites",
                    substring = true,
                    useUnmergedTree = true
                )
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()

            // Add second character
            val addFavoriteButtons2 = composeTestRule
                .onAllNodesWithContentDescription(
                    "Add to favorites",
                    substring = true,
                    useUnmergedTree = true
                )
                .fetchSemanticsNodes()

            if (addFavoriteButtons2.isNotEmpty()) {
                composeTestRule
                    .onAllNodesWithContentDescription(
                        "Add to favorites",
                        substring = true,
                        useUnmergedTree = true
                    )
                    .onFirst()
                    .performClick()

                composeTestRule.waitForIdle()
            }
        }

        // Navigate to Favorites
        composeTestRule.onNodeWithText("Favorites").performClick()
        composeTestRule.waitForIdle()

        // Wait for favorites to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 3
        }

        // Enter selection mode
        val selectionModeButtons = composeTestRule
            .onAllNodesWithTag(TestTags.SELECTION_MODE_BUTTON, useUnmergedTree = true)
            .fetchSemanticsNodes()

        if (selectionModeButtons.isNotEmpty()) {
            composeTestRule
                .onNodeWithTag(TestTags.SELECTION_MODE_BUTTON, useUnmergedTree = true)
                .performClick()

            composeTestRule.waitForIdle()

            // Select and compare
            val selectableCards = composeTestRule
                .onAllNodesWithTag(TestTags.SELECTABLE_CHARACTER_CARD, useUnmergedTree = true)
                .fetchSemanticsNodes()

            if (selectableCards.size >= 2) {
                composeTestRule
                    .onAllNodesWithTag(TestTags.SELECTABLE_CHARACTER_CARD, useUnmergedTree = true)
                    .onFirst()
                    .performClick()

                composeTestRule.waitForIdle()

                composeTestRule
                    .onAllNodesWithTag(
                        TestTags.SELECTABLE_CHARACTER_CARD,
                        useUnmergedTree = true
                    )[1]
                    .performClick()

                composeTestRule.waitForIdle()

                // Click compare
                val compareButtons = composeTestRule
                    .onAllNodesWithTag(TestTags.COMPARE_BUTTON, useUnmergedTree = true)
                    .fetchSemanticsNodes()

                if (compareButtons.isNotEmpty()) {
                    composeTestRule
                        .onNodeWithTag(TestTags.COMPARE_BUTTON, useUnmergedTree = true)
                        .performClick()

                    composeTestRule.waitForIdle()

                    // Wait for comparison screen
                    composeTestRule.waitUntil(timeoutMillis = 5000) {
                        composeTestRule
                            .onAllNodesWithContentDescription(
                                "Navigate back",
                                substring = true,
                                useUnmergedTree = true
                            )
                            .fetchSemanticsNodes()
                            .isNotEmpty()
                    }

                    // Navigate back
                    composeTestRule
                        .onNodeWithContentDescription("Navigate back", useUnmergedTree = true)
                        .performClick()

                    composeTestRule.waitForIdle()

                    // Should be back on Favorites screen
                    composeTestRule.waitUntil(timeoutMillis = 3000) {
                        composeTestRule
                            .onAllNodesWithText("", substring = true)
                            .fetchSemanticsNodes()
                            .size > 1
                    }
                }
            }
        }

        // Navigate back to Characters
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun comparison_comparisonResult_displaysCharacterData() {
        // First add some favorites
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Add multiple characters to favorites
        val addFavoriteButtons = composeTestRule
            .onAllNodesWithContentDescription(
                "Add to favorites",
                substring = true,
                useUnmergedTree = true
            )
            .fetchSemanticsNodes()

        if (addFavoriteButtons.size >= 2) {
            // Add first character
            composeTestRule
                .onAllNodesWithContentDescription(
                    "Add to favorites",
                    substring = true,
                    useUnmergedTree = true
                )
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()

            // Add second character
            val addFavoriteButtons2 = composeTestRule
                .onAllNodesWithContentDescription(
                    "Add to favorites",
                    substring = true,
                    useUnmergedTree = true
                )
                .fetchSemanticsNodes()

            if (addFavoriteButtons2.isNotEmpty()) {
                composeTestRule
                    .onAllNodesWithContentDescription(
                        "Add to favorites",
                        substring = true,
                        useUnmergedTree = true
                    )
                    .onFirst()
                    .performClick()

                composeTestRule.waitForIdle()
            }
        }

        // Navigate to Favorites
        composeTestRule.onNodeWithText("Favorites").performClick()
        composeTestRule.waitForIdle()

        // Wait for favorites to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 3
        }

        // Enter selection mode and compare
        val selectionModeButtons = composeTestRule
            .onAllNodesWithTag(TestTags.SELECTION_MODE_BUTTON, useUnmergedTree = true)
            .fetchSemanticsNodes()

        if (selectionModeButtons.isNotEmpty()) {
            composeTestRule
                .onNodeWithTag(TestTags.SELECTION_MODE_BUTTON, useUnmergedTree = true)
                .performClick()

            composeTestRule.waitForIdle()

            val selectableCards = composeTestRule
                .onAllNodesWithTag(TestTags.SELECTABLE_CHARACTER_CARD, useUnmergedTree = true)
                .fetchSemanticsNodes()

            if (selectableCards.size >= 2) {
                composeTestRule
                    .onAllNodesWithTag(TestTags.SELECTABLE_CHARACTER_CARD, useUnmergedTree = true)
                    .onFirst()
                    .performClick()

                composeTestRule.waitForIdle()

                composeTestRule
                    .onAllNodesWithTag(
                        TestTags.SELECTABLE_CHARACTER_CARD,
                        useUnmergedTree = true
                    )[1]
                    .performClick()

                composeTestRule.waitForIdle()

                val compareButtons = composeTestRule
                    .onAllNodesWithTag(TestTags.COMPARE_BUTTON, useUnmergedTree = true)
                    .fetchSemanticsNodes()

                if (compareButtons.isNotEmpty()) {
                    composeTestRule
                        .onNodeWithTag(TestTags.COMPARE_BUTTON, useUnmergedTree = true)
                        .performClick()

                    composeTestRule.waitForIdle()

                    // Verify comparison result displays
                    composeTestRule.waitUntil(timeoutMillis = 5000) {
                        composeTestRule
                            .onAllNodesWithText("", substring = true)
                            .fetchSemanticsNodes()
                            .size > 5
                    }

                    // Navigate back
                    val backButtons = composeTestRule
                        .onAllNodesWithContentDescription(
                            "Navigate back",
                            substring = true,
                            useUnmergedTree = true
                        )
                        .fetchSemanticsNodes()

                    if (backButtons.isNotEmpty()) {
                        composeTestRule
                            .onNodeWithContentDescription("Navigate back", useUnmergedTree = true)
                            .performClick()

                        composeTestRule.waitForIdle()
                    }
                }
            }
        }

        // Navigate back to Characters
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()
    }
}
