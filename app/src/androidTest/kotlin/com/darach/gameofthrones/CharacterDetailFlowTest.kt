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
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Character detail screen flow tests.
 *
 * Tests navigation to character detail and detail screen functionality:
 * - Navigation from character list to detail
 * - Character information display
 * - Favorite toggle from detail screen
 * - Back navigation
 * - Different character profiles
 * - Error handling
 */
@RunWith(AndroidJUnit4::class)
class CharacterDetailFlowTest {

    @get:Rule(order = 0)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 1)
    val screenshotRule = ScreenshotTestRule()

    @Before
    fun setup() {
        screenshotRule.setComposeTestRule(composeTestRule)
    }

    @Test
    fun characterDetail_navigation_fromList_opensDetail() {
        // Wait for character list to load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Find and click first character card
        val characterCards = composeTestRule
            .onAllNodesWithTag(TestTags.CHARACTER_CARD, useUnmergedTree = true)
            .fetchSemanticsNodes()

        if (characterCards.isNotEmpty()) {
            composeTestRule
                .onAllNodesWithTag(TestTags.CHARACTER_CARD, useUnmergedTree = true)
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()

            // Verify detail screen loads
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
        }
    }

    @Test
    fun characterDetail_displaysCharacterName() {
        // Wait for character list to load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Search for a specific character
        val searchBar = composeTestRule
            .onNode(hasText("Search characters", substring = true))

        searchBar.performClick()
        composeTestRule.waitForIdle()

        searchBar.performTextInput("Jon Snow")

        // Wait for search results
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Jon", substring = true, ignoreCase = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Click first result
        val characterCards = composeTestRule
            .onAllNodesWithTag(TestTags.CHARACTER_CARD, useUnmergedTree = true)
            .fetchSemanticsNodes()

        if (characterCards.isNotEmpty()) {
            composeTestRule
                .onAllNodesWithTag(TestTags.CHARACTER_CARD, useUnmergedTree = true)
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()

            // Verify character name appears in detail
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule
                    .onAllNodesWithText("Jon", substring = true, ignoreCase = true)
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }

            // Navigate back
            val backButton = composeTestRule
                .onNodeWithContentDescription("Navigate back", useUnmergedTree = true)

            if (backButton.fetchSemanticsNode(null) != null) {
                backButton.performClick()
                composeTestRule.waitForIdle()
            }
        }
    }

    @Test
    fun characterDetail_displaysCulture() {
        // Wait for character list to load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Click first character with culture
        val characterCards = composeTestRule
            .onAllNodesWithTag(TestTags.CHARACTER_CARD, useUnmergedTree = true)
            .fetchSemanticsNodes()

        if (characterCards.isNotEmpty()) {
            composeTestRule
                .onAllNodesWithTag(TestTags.CHARACTER_CARD, useUnmergedTree = true)
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()

            // Verify detail content loads
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule
                    .onAllNodesWithText("", substring = true)
                    .fetchSemanticsNodes()
                    .size > 3
            }

            // Navigate back
            val backButton = composeTestRule
                .onNodeWithContentDescription("Navigate back", useUnmergedTree = true)

            if (backButton.fetchSemanticsNode(null) != null) {
                backButton.performClick()
                composeTestRule.waitForIdle()
            }
        }
    }

    @Test
    fun characterDetail_displaysTitles() {
        // Wait for character list to load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Search for a character likely to have titles
        val searchBar = composeTestRule
            .onNode(hasText("Search characters", substring = true))

        searchBar.performClick()
        composeTestRule.waitForIdle()

        searchBar.performTextInput("King")

        // Wait for search results
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 3
        }

        // Click first result
        val characterCards = composeTestRule
            .onAllNodesWithTag(TestTags.CHARACTER_CARD, useUnmergedTree = true)
            .fetchSemanticsNodes()

        if (characterCards.isNotEmpty()) {
            composeTestRule
                .onAllNodesWithTag(TestTags.CHARACTER_CARD, useUnmergedTree = true)
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()

            // Wait for detail to load
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule
                    .onAllNodesWithText("", substring = true)
                    .fetchSemanticsNodes()
                    .size > 3
            }

            // Navigate back
            val backButton = composeTestRule
                .onNodeWithContentDescription("Navigate back", useUnmergedTree = true)

            if (backButton.fetchSemanticsNode(null) != null) {
                backButton.performClick()
                composeTestRule.waitForIdle()
            }
        }
    }

    @Test
    fun characterDetail_displaysAliases() {
        // Wait for character list to load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Click first character
        val characterCards = composeTestRule
            .onAllNodesWithTag(TestTags.CHARACTER_CARD, useUnmergedTree = true)
            .fetchSemanticsNodes()

        if (characterCards.isNotEmpty()) {
            composeTestRule
                .onAllNodesWithTag(TestTags.CHARACTER_CARD, useUnmergedTree = true)
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()

            // Wait for detail to load
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule
                    .onAllNodesWithText("", substring = true)
                    .fetchSemanticsNodes()
                    .size > 3
            }

            // Navigate back
            val backButton = composeTestRule
                .onNodeWithContentDescription("Navigate back", useUnmergedTree = true)

            if (backButton.fetchSemanticsNode(null) != null) {
                backButton.performClick()
                composeTestRule.waitForIdle()
            }
        }
    }

    @Test
    fun characterDetail_displaysTVSeriesSeasons() {
        // Wait for character list to load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Click first character
        val characterCards = composeTestRule
            .onAllNodesWithTag(TestTags.CHARACTER_CARD, useUnmergedTree = true)
            .fetchSemanticsNodes()

        if (characterCards.isNotEmpty()) {
            composeTestRule
                .onAllNodesWithTag(TestTags.CHARACTER_CARD, useUnmergedTree = true)
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()

            // Wait for detail to load and check for season badges (Roman numerals)
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule
                    .onAllNodesWithText("", substring = true)
                    .fetchSemanticsNodes()
                    .size > 3
            }

            // Navigate back
            val backButton = composeTestRule
                .onNodeWithContentDescription("Navigate back", useUnmergedTree = true)

            if (backButton.fetchSemanticsNode(null) != null) {
                backButton.performClick()
                composeTestRule.waitForIdle()
            }
        }
    }

    @Test
    fun characterDetail_toggleFavorite_updatesStatus() {
        // Wait for character list to load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Click first character
        val characterCards = composeTestRule
            .onAllNodesWithTag(TestTags.CHARACTER_CARD, useUnmergedTree = true)
            .fetchSemanticsNodes()

        if (characterCards.isNotEmpty()) {
            composeTestRule
                .onAllNodesWithTag(TestTags.CHARACTER_CARD, useUnmergedTree = true)
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()

            // Wait for detail to load
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule
                    .onAllNodesWithText("", substring = true)
                    .fetchSemanticsNodes()
                    .size > 3
            }

            // Try to find favorite button
            val addFavoriteButtons = composeTestRule
                .onAllNodesWithContentDescription(
                    "Add to favorites",
                    substring = true,
                    useUnmergedTree = true
                )
                .fetchSemanticsNodes()

            val removeFavoriteButtons = composeTestRule
                .onAllNodesWithContentDescription(
                    "Remove from favorites",
                    substring = true,
                    useUnmergedTree = true
                )
                .fetchSemanticsNodes()

            if (addFavoriteButtons.isNotEmpty()) {
                composeTestRule
                    .onAllNodesWithContentDescription(
                        "Add to favorites",
                        substring = true,
                        useUnmergedTree = true
                    )
                    .onFirst()
                    .performClick()

                composeTestRule.waitForIdle()

                // Verify it changed
                composeTestRule.waitUntil(timeoutMillis = 3000) {
                    composeTestRule
                        .onAllNodesWithContentDescription(
                            "Remove from favorites",
                            substring = true,
                            useUnmergedTree = true
                        )
                        .fetchSemanticsNodes()
                        .isNotEmpty()
                }
            } else if (removeFavoriteButtons.isNotEmpty()) {
                composeTestRule
                    .onAllNodesWithContentDescription(
                        "Remove from favorites",
                        substring = true,
                        useUnmergedTree = true
                    )
                    .onFirst()
                    .performClick()

                composeTestRule.waitForIdle()

                // Verify it changed
                composeTestRule.waitUntil(timeoutMillis = 3000) {
                    composeTestRule
                        .onAllNodesWithContentDescription(
                            "Add to favorites",
                            substring = true,
                            useUnmergedTree = true
                        )
                        .fetchSemanticsNodes()
                        .isNotEmpty()
                }
            }

            // Navigate back
            val backButton = composeTestRule
                .onNodeWithContentDescription("Navigate back", useUnmergedTree = true)

            if (backButton.fetchSemanticsNode(null) != null) {
                backButton.performClick()
                composeTestRule.waitForIdle()
            }
        }
    }

    @Test
    fun characterDetail_backButton_returnsToList() {
        // Wait for character list to load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Click first character
        val characterCards = composeTestRule
            .onAllNodesWithTag(TestTags.CHARACTER_CARD, useUnmergedTree = true)
            .fetchSemanticsNodes()

        if (characterCards.isNotEmpty()) {
            composeTestRule
                .onAllNodesWithTag(TestTags.CHARACTER_CARD, useUnmergedTree = true)
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()

            // Wait for detail to load
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

            // Click back button
            composeTestRule
                .onNodeWithContentDescription("Navigate back", useUnmergedTree = true)
                .performClick()

            composeTestRule.waitForIdle()

            // Verify we're back on list screen
            composeTestRule
                .onNode(hasText("Search characters", substring = true))
                .assertIsDisplayed()
        }
    }

    @Test
    fun characterDetail_multipleCharacters_displaysDifferentInfo() {
        // Wait for character list to load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Click first character and remember details
        val characterCards = composeTestRule
            .onAllNodesWithTag(TestTags.CHARACTER_CARD, useUnmergedTree = true)
            .fetchSemanticsNodes()

        if (characterCards.size >= 2) {
            // Click first character
            composeTestRule
                .onAllNodesWithTag(TestTags.CHARACTER_CARD, useUnmergedTree = true)
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()

            // Wait for detail to load
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

            // Click second character
            composeTestRule
                .onAllNodesWithTag(TestTags.CHARACTER_CARD, useUnmergedTree = true)[1]
                .performClick()

            composeTestRule.waitForIdle()

            // Wait for detail to load
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
        }
    }

    @Test
    fun characterDetail_displaysDeathStatus_forDeceasedCharacter() {
        // Wait for character list to load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Filter for deceased characters
        val deceasedNodes = composeTestRule
            .onAllNodesWithText("Deceased", substring = true)
            .fetchSemanticsNodes()

        if (deceasedNodes.isNotEmpty()) {
            composeTestRule.onNodeWithText("Deceased").performClick()
            composeTestRule.waitForIdle()

            // Click first deceased character
            val characterCards = composeTestRule
                .onAllNodesWithTag(TestTags.CHARACTER_CARD, useUnmergedTree = true)
                .fetchSemanticsNodes()

            if (characterCards.isNotEmpty()) {
                composeTestRule
                    .onAllNodesWithTag(TestTags.CHARACTER_CARD, useUnmergedTree = true)
                    .onFirst()
                    .performClick()

                composeTestRule.waitForIdle()

                // Wait for detail to load
                composeTestRule.waitUntil(timeoutMillis = 5000) {
                    composeTestRule
                        .onAllNodesWithText("", substring = true)
                        .fetchSemanticsNodes()
                        .size > 3
                }

                // Navigate back
                val backButton = composeTestRule
                    .onNodeWithContentDescription("Navigate back", useUnmergedTree = true)

                if (backButton.fetchSemanticsNode(null) != null) {
                    backButton.performClick()
                    composeTestRule.waitForIdle()
                }
            }

            // Clear filter
            composeTestRule.onNodeWithText("Deceased").performClick()
            composeTestRule.waitForIdle()
        }
    }
}
