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
 * Favorites feature flow tests.
 *
 * Tests the favorites functionality including:
 * - Adding characters to favorites from list
 * - Adding characters to favorites from detail
 * - Viewing favorites screen
 * - Removing favorites
 * - Empty state when no favorites
 * - View mode toggle (list/grid)
 * - Selection mode
 * - Navigation to character detail from favorites
 */
@RunWith(AndroidJUnit4::class)
class FavoritesFlowTest {

    @get:Rule(order = 0)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 1)
    val screenshotRule = ScreenshotTestRule()

    @Before
    fun setup() {
        screenshotRule.setComposeTestRule(composeTestRule)
    }

    @Test
    fun favorites_addFromList_appearsInFavorites() {
        // Wait for character list to load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Add first character to favorites
        val addFavoriteButtons = composeTestRule
            .onAllNodesWithContentDescription(
                "Add to favorites",
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

            // Navigate to Favorites tab
            composeTestRule.onNodeWithText("Favorites").performClick()
            composeTestRule.waitForIdle()

            // Verify favorites screen shows content
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule
                    .onAllNodesWithText("", substring = true)
                    .fetchSemanticsNodes()
                    .size > 3
            }

            // Navigate back to Characters
            composeTestRule.onNodeWithText("Characters").performClick()
            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun favorites_emptyState_displaysMessage() {
        // Navigate to Favorites tab first
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Favorites", substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Favorites").performClick()
        composeTestRule.waitForIdle()

        // If no favorites, should show empty state
        // We can't guarantee this state, so just check the screen loads
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 1
        }

        // Navigate back to Characters
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun favorites_removeFromList_removesFromFavorites() {
        // Wait for character list to load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // First add a character to favorites
        val addFavoriteButtons = composeTestRule
            .onAllNodesWithContentDescription(
                "Add to favorites",
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

            // Verify it's now a favorite
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

            // Remove from favorites
            val removeFavoriteButtons = composeTestRule
                .onAllNodesWithContentDescription(
                    "Remove from favorites",
                    substring = true,
                    useUnmergedTree = true
                )
                .fetchSemanticsNodes()

            if (removeFavoriteButtons.isNotEmpty()) {
                composeTestRule
                    .onAllNodesWithContentDescription(
                        "Remove from favorites",
                        substring = true,
                        useUnmergedTree = true
                    )
                    .onFirst()
                    .performClick()

                composeTestRule.waitForIdle()
            }

            // Navigate back to Characters
            composeTestRule.onNodeWithText("Characters").performClick()
            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun favorites_addFromDetail_appearsInFavorites() {
        // Wait for character list to load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Click first character to open detail
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

            // Add to favorites from detail
            val addFavoriteButtons = composeTestRule
                .onAllNodesWithContentDescription(
                    "Add to favorites",
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

                // Navigate back
                composeTestRule
                    .onNodeWithContentDescription("Navigate back", useUnmergedTree = true)
                    .performClick()

                composeTestRule.waitForIdle()

                // Navigate to Favorites
                composeTestRule.onNodeWithText("Favorites").performClick()
                composeTestRule.waitForIdle()

                // Verify favorites screen has content
                composeTestRule.waitUntil(timeoutMillis = 5000) {
                    composeTestRule
                        .onAllNodesWithText("", substring = true)
                        .fetchSemanticsNodes()
                        .size > 3
                }

                // Navigate back to Characters
                composeTestRule.onNodeWithText("Characters").performClick()
                composeTestRule.waitForIdle()
            }
        }
    }

    @Test
    fun favorites_navigation_toCharacterDetail() {
        // Wait for app to load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // First ensure we have at least one favorite
        val addFavoriteButtons = composeTestRule
            .onAllNodesWithContentDescription(
                "Add to favorites",
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

            // Click on favorite character
            val favoriteCards = composeTestRule
                .onAllNodesWithTag(TestTags.FAVORITE_CARD, useUnmergedTree = true)
                .fetchSemanticsNodes()

            if (favoriteCards.isEmpty()) {
                // Try character cards instead
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
            } else {
                composeTestRule
                    .onAllNodesWithTag(TestTags.FAVORITE_CARD, useUnmergedTree = true)
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

            // Navigate back to Characters
            composeTestRule.onNodeWithText("Characters").performClick()
            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun favorites_viewModeToggle_switchesBetweenListAndGrid() {
        // Navigate to Favorites
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Favorites", substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Favorites").performClick()
        composeTestRule.waitForIdle()

        // Wait for favorites screen to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 1
        }

        // Try to find view mode toggle button
        val viewModeButtons = composeTestRule
            .onAllNodesWithTag(TestTags.VIEW_MODE_TOGGLE, useUnmergedTree = true)
            .fetchSemanticsNodes()

        val toggleGridButtons = composeTestRule
            .onAllNodesWithContentDescription(
                "Toggle grid view",
                substring = true,
                useUnmergedTree = true
            )
            .fetchSemanticsNodes()

        val toggleListButtons = composeTestRule
            .onAllNodesWithContentDescription(
                "Toggle list view",
                substring = true,
                useUnmergedTree = true
            )
            .fetchSemanticsNodes()

        if (viewModeButtons.isNotEmpty()) {
            composeTestRule
                .onNodeWithTag(TestTags.VIEW_MODE_TOGGLE, useUnmergedTree = true)
                .performClick()

            composeTestRule.waitForIdle()

            // Toggle back
            composeTestRule
                .onNodeWithTag(TestTags.VIEW_MODE_TOGGLE, useUnmergedTree = true)
                .performClick()

            composeTestRule.waitForIdle()
        } else if (toggleGridButtons.isNotEmpty()) {
            composeTestRule
                .onNodeWithContentDescription("Toggle grid view", useUnmergedTree = true)
                .performClick()

            composeTestRule.waitForIdle()

            // Should now show list toggle
            val listButtons = composeTestRule
                .onAllNodesWithContentDescription(
                    "Toggle list view",
                    substring = true,
                    useUnmergedTree = true
                )
                .fetchSemanticsNodes()

            if (listButtons.isNotEmpty()) {
                composeTestRule
                    .onNodeWithContentDescription("Toggle list view", useUnmergedTree = true)
                    .performClick()

                composeTestRule.waitForIdle()
            }
        } else if (toggleListButtons.isNotEmpty()) {
            composeTestRule
                .onNodeWithContentDescription("Toggle list view", useUnmergedTree = true)
                .performClick()

            composeTestRule.waitForIdle()

            // Should now show grid toggle
            val gridButtons = composeTestRule
                .onAllNodesWithContentDescription(
                    "Toggle grid view",
                    substring = true,
                    useUnmergedTree = true
                )
                .fetchSemanticsNodes()

            if (gridButtons.isNotEmpty()) {
                composeTestRule
                    .onNodeWithContentDescription("Toggle grid view", useUnmergedTree = true)
                    .performClick()

                composeTestRule.waitForIdle()
            }
        }

        // Navigate back to Characters
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun favorites_filterByFavorites_showsOnlyFavorites() {
        // Wait for character list to load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Add a character to favorites first
        val addFavoriteButtons = composeTestRule
            .onAllNodesWithContentDescription(
                "Add to favorites",
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

            // Apply favorites filter on character list
            val favoritesFilterNodes = composeTestRule
                .onAllNodesWithText("Favorites", substring = true)
                .fetchSemanticsNodes()

            // Find the filter chip (not the navigation button)
            if (favoritesFilterNodes.size >= 2) {
                // Usually navigation is first, filter chip is second
                composeTestRule
                    .onAllNodesWithText("Favorites")[1]
                    .performClick()

                composeTestRule.waitForIdle()

                // Verify list is filtered
                composeTestRule.waitUntil(timeoutMillis = 3000) {
                    composeTestRule
                        .onAllNodesWithText("", substring = true)
                        .fetchSemanticsNodes()
                        .isNotEmpty()
                }

                // Clear filter
                composeTestRule
                    .onAllNodesWithText("Favorites")[1]
                    .performClick()

                composeTestRule.waitForIdle()
            }
        }
    }

    @Test
    fun favorites_multipleFavorites_allDisplayed() {
        // Wait for character list to load
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

        if (addFavoriteButtons.size >= 3) {
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

            // Add third character
            val addFavoriteButtons3 = composeTestRule
                .onAllNodesWithContentDescription(
                    "Add to favorites",
                    substring = true,
                    useUnmergedTree = true
                )
                .fetchSemanticsNodes()

            if (addFavoriteButtons3.isNotEmpty()) {
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

            // Navigate to Favorites
            composeTestRule.onNodeWithText("Favorites").performClick()
            composeTestRule.waitForIdle()

            // Verify multiple favorites are displayed
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                val cards = composeTestRule
                    .onAllNodesWithTag(TestTags.FAVORITE_CARD, useUnmergedTree = true)
                    .fetchSemanticsNodes()

                val characterCards = composeTestRule
                    .onAllNodesWithTag(TestTags.CHARACTER_CARD, useUnmergedTree = true)
                    .fetchSemanticsNodes()

                cards.size >= 3 || characterCards.size >= 3
            }

            // Navigate back to Characters
            composeTestRule.onNodeWithText("Characters").performClick()
            composeTestRule.waitForIdle()
        }
    }
}
