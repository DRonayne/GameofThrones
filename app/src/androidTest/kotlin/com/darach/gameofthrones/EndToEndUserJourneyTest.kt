package com.darach.gameofthrones

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
 * End-to-end user journey tests.
 *
 * These tests simulate realistic user flows through the app:
 * - Complete onboarding and discovery flow
 * - Search, favorite, and compare workflow
 * - Theme customization journey
 * - Content exploration flow
 * - Power user workflow
 */
@RunWith(AndroidJUnit4::class)
class EndToEndUserJourneyTest {

    @get:Rule(order = 0)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 1)
    val screenshotRule = ScreenshotTestRule()

    @Before
    fun setup() {
        screenshotRule.setComposeTestRule(composeTestRule)
    }

    @Test
    fun completeUserJourney_newUser_discoversAndFavoritesCharacters() {
        // Step 1: Wait for app to load and display characters
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Step 2: Browse character list
        val characterCards = composeTestRule
            .onAllNodesWithTag(TestTags.CHARACTER_CARD)
            .fetchSemanticsNodes()

        if (characterCards.isNotEmpty()) {
            // Step 3: Click on first character to view details
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

            // Step 4: Add character to favorites from detail screen
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
            }

            // Step 5: Navigate back to list
            composeTestRule
                .onNodeWithContentDescription("Navigate back", useUnmergedTree = true)
                .performClick()

            composeTestRule.waitForIdle()

            // Step 6: Add another favorite from the list
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

            // Step 7: Navigate to Favorites to see collection
            composeTestRule.onNodeWithText("Favorites").performClick()
            composeTestRule.waitForIdle()

            // Wait for favorites to load
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule
                    .onAllNodesWithText("", substring = true)
                    .fetchSemanticsNodes()
                    .size > 3
            }

            // Step 8: Return to Characters
            composeTestRule.onNodeWithText("Characters").performClick()
            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun completeUserJourney_searchAndFilterToFindCharacter() {
        // Step 1: Wait for app to load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Step 2: Try to find a specific character using search
        val searchBar = composeTestRule
            .onNode(hasText("Search characters", substring = true))

        searchBar.performClick()
        composeTestRule.waitForIdle()

        // Step 3: Search for "Stark"
        searchBar.performTextInput("Stark")

        // Wait for search results
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Stark", substring = true, ignoreCase = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.waitForIdle()

        // Step 4: Apply additional filter (e.g., Female)
        val femaleNodes = composeTestRule
            .onAllNodesWithText("Female", substring = true)
            .fetchSemanticsNodes()

        if (femaleNodes.isNotEmpty()) {
            composeTestRule
                .onNodeWithText("Female")
                .performClick()

            composeTestRule.waitForIdle()
        }

        composeTestRule.waitForIdle()

        // Step 5: View a filtered character
        val characterCards = composeTestRule
            .onAllNodesWithTag(TestTags.CHARACTER_CARD)
            .fetchSemanticsNodes()

        if (characterCards.isNotEmpty()) {
            composeTestRule
                .onAllNodesWithTag(TestTags.CHARACTER_CARD)
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()

            // Wait for detail screen to load
            composeTestRule.waitUntil(timeoutMillis = 10000) {
                composeTestRule
                    .onAllNodesWithContentDescription("Navigate back", substring = true)
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }
        }
    }

    @Test
    fun completeUserJourney_customizeThemeAndBrowse() {
        // Step 1: Navigate to Settings
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Settings", substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.waitForIdle()

        // Step 2: Change theme to Dark
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Theme", substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        val darkNodes = composeTestRule
            .onAllNodesWithText("Dark", substring = true)
            .fetchSemanticsNodes()

        if (darkNodes.isNotEmpty()) {
            composeTestRule
                .onAllNodesWithText("Dark")
                .onFirst()
                .performClick()

            // Wait a bit for theme to apply
            Thread.sleep(500)
            composeTestRule.waitForIdle()
        }

        // Step 3: Navigate back to Characters
        composeTestRule.onNodeWithText("Characters").performClick()
        Thread.sleep(500)
        composeTestRule.waitForIdle()

        // Wait for characters to load with new theme
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Step 4: Browse with new theme
        val characterCards = composeTestRule
            .onAllNodesWithTag(TestTags.CHARACTER_CARD)
            .fetchSemanticsNodes()

        if (characterCards.isNotEmpty()) {
            composeTestRule
                .onAllNodesWithTag(TestTags.CHARACTER_CARD, useUnmergedTree = true)
                .onFirst()
                .performClick()

            Thread.sleep(500)
            composeTestRule.waitForIdle()

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

            composeTestRule
                .onNodeWithContentDescription("Navigate back", useUnmergedTree = true)
                .performClick()

            Thread.sleep(500)
            composeTestRule.waitForIdle()
        }

        // Step 5: Change back to System theme
        composeTestRule.onNodeWithText("Settings").performClick()
        Thread.sleep(500)
        composeTestRule.waitForIdle()

        val systemNodes = composeTestRule
            .onAllNodesWithText("System", substring = true)
            .fetchSemanticsNodes()

        if (systemNodes.isNotEmpty()) {
            composeTestRule
                .onNodeWithText("System Default")
                .performClick()

            Thread.sleep(500)
            composeTestRule.waitForIdle()
        }

        composeTestRule.onNodeWithText("Characters").performClick()
        Thread.sleep(500)
        composeTestRule.waitForIdle()
    }

    @Test
    fun completeUserJourney_exploreDifferentCultures() {
        // Step 1: Wait for app to load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Step 2: Search for Northmen culture
        val searchBar = composeTestRule
            .onNode(hasText("Search characters", substring = true))

        searchBar.performClick()
        composeTestRule.waitForIdle()

        searchBar.performTextInput("Northmen")

        // Wait for results
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Northmen", substring = true, ignoreCase = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.waitForIdle()

        // Step 3: View a Northmen character
        val characterCards = composeTestRule
            .onAllNodesWithTag(TestTags.CHARACTER_CARD)
            .fetchSemanticsNodes()

        if (characterCards.isNotEmpty()) {
            composeTestRule
                .onAllNodesWithTag(TestTags.CHARACTER_CARD)
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()

            composeTestRule.waitUntil(timeoutMillis = 10000) {
                composeTestRule
                    .onAllNodesWithContentDescription("Navigate back", substring = true)
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }

            composeTestRule
                .onNodeWithContentDescription("Navigate back")
                .performClick()

            composeTestRule.waitForIdle()
        }

        // Step 4: Clear and search for different culture
        val clearButton = composeTestRule
            .onNodeWithContentDescription("Clear search", useUnmergedTree = true)

        if (clearButton.fetchSemanticsNode(null) != null) {
            clearButton.performClick()
            composeTestRule.waitForIdle()
        }

        // Step 5: Search for another culture
        val searchBar2 = composeTestRule
            .onNode(hasText("Search characters", substring = true))

        searchBar2.performClick()
        composeTestRule.waitForIdle()

        searchBar2.performTextInput("Valyrian")

        // Wait for results
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 1
        }

        // Clear search
        val clearButton2 = composeTestRule
            .onNodeWithContentDescription("Clear search", useUnmergedTree = true)

        if (clearButton2.fetchSemanticsNode(null) != null) {
            clearButton2.performClick()
            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun completeUserJourney_compareFavoriteCharacters() {
        // Step 1: Wait for app to load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Step 2: Add multiple favorites
        var addedCount = 0
        while (addedCount < 3) {
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
                addedCount++
            } else {
                break
            }
        }

        // Step 3: Navigate to Favorites
        composeTestRule.onNodeWithText("Favorites").performClick()
        composeTestRule.waitForIdle()

        // Wait for favorites to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 3
        }

        // Step 4: Enter comparison mode
        val selectionModeButtons = composeTestRule
            .onAllNodesWithTag(TestTags.SELECTION_MODE_BUTTON, useUnmergedTree = true)
            .fetchSemanticsNodes()

        if (selectionModeButtons.isNotEmpty()) {
            composeTestRule
                .onNodeWithTag(TestTags.SELECTION_MODE_BUTTON, useUnmergedTree = true)
                .performClick()

            composeTestRule.waitForIdle()

            // Step 5: Select two characters
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

                // Step 6: Compare
                val compareButtons = composeTestRule
                    .onAllNodesWithTag(TestTags.COMPARE_BUTTON, useUnmergedTree = true)
                    .fetchSemanticsNodes()

                if (compareButtons.isNotEmpty()) {
                    composeTestRule
                        .onNodeWithTag(TestTags.COMPARE_BUTTON, useUnmergedTree = true)
                        .performClick()

                    composeTestRule.waitForIdle()

                    // Wait for comparison result
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

                    // Step 7: Navigate back
                    composeTestRule
                        .onNodeWithContentDescription("Navigate back", useUnmergedTree = true)
                        .performClick()

                    composeTestRule.waitForIdle()
                }
            }
        }

        // Step 8: Return to Characters
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun completeUserJourney_sortAndFilterWorkflow() {
        // Step 1: Wait for app to load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Step 2: Apply sort by name
        val sortButton = composeTestRule
            .onNodeWithContentDescription("Sort options", useUnmergedTree = true)

        if (sortButton.fetchSemanticsNode(null) != null) {
            sortButton.performClick()
            composeTestRule.waitForIdle()

            val nameAscNodes = composeTestRule
                .onAllNodesWithText("Name (A-Z)", substring = true)
                .fetchSemanticsNodes()

            if (nameAscNodes.isNotEmpty()) {
                composeTestRule
                    .onNodeWithText("Name (A-Z)")
                    .performClick()

                composeTestRule.waitForIdle()
            }
        }

        // Step 3: Apply filter for alive characters
        val aliveNodes = composeTestRule
            .onAllNodesWithText("Alive", substring = true)
            .fetchSemanticsNodes()

        if (aliveNodes.isNotEmpty()) {
            composeTestRule
                .onNodeWithText("Alive")
                .performClick()

            composeTestRule.waitForIdle()
        }

        // Step 4: Apply gender filter
        val maleNodes = composeTestRule
            .onAllNodesWithText("Male", substring = true)
            .fetchSemanticsNodes()

        if (maleNodes.isNotEmpty()) {
            composeTestRule
                .onAllNodesWithText("Male")
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()
        }

        // Step 5: View a filtered character
        val characterCards = composeTestRule
            .onAllNodesWithTag(TestTags.CHARACTER_CARD)
            .fetchSemanticsNodes()

        if (characterCards.isNotEmpty()) {
            composeTestRule
                .onAllNodesWithTag(TestTags.CHARACTER_CARD, useUnmergedTree = true)
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()

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

            composeTestRule
                .onNodeWithContentDescription("Navigate back", useUnmergedTree = true)
                .performClick()

            composeTestRule.waitForIdle()
        }

        // Step 6: Clear all filters
        if (aliveNodes.isNotEmpty()) {
            composeTestRule
                .onAllNodesWithText("Alive")
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()
        }

        if (maleNodes.isNotEmpty()) {
            composeTestRule
                .onAllNodesWithText("Male")
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun completeUserJourney_navigationBetweenAllScreens() {
        // Step 1: Start on Characters
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Step 2: Navigate to Favorites
        composeTestRule.onNodeWithText("Favorites").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 1
        }

        // Step 3: Navigate to Settings
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Theme", substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Step 4: Back to Characters
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()

        // Step 5: Open character detail
        val characterCards = composeTestRule
            .onAllNodesWithTag(TestTags.CHARACTER_CARD)
            .fetchSemanticsNodes()

        if (characterCards.isNotEmpty()) {
            composeTestRule
                .onAllNodesWithTag(TestTags.CHARACTER_CARD, useUnmergedTree = true)
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()

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

            // Step 6: Back to list
            composeTestRule
                .onNodeWithContentDescription("Navigate back", useUnmergedTree = true)
                .performClick()

            composeTestRule.waitForIdle()
        }

        // Step 7: Navigate to Favorites again
        composeTestRule.onNodeWithText("Favorites").performClick()
        composeTestRule.waitForIdle()

        // Step 8: Back to Characters
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()
    }
}
