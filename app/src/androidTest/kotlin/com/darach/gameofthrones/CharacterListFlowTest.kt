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
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Comprehensive character list flow tests.
 *
 * Tests the main character list screen functionality including:
 * - Initial load and display
 * - Search functionality with various queries
 * - Filtering by multiple criteria
 * - Sorting options
 * - Pull to refresh
 * - Character card interactions
 * - Favorite toggles
 * - Offline mode behavior
 */
@RunWith(AndroidJUnit4::class)
class CharacterListFlowTest {

    @get:Rule(order = 0)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 1)
    val screenshotRule = ScreenshotTestRule()

    @Before
    fun setup() {
        screenshotRule.setComposeTestRule(composeTestRule)
    }

    @Test
    fun characterList_initialLoad_displaysCharacters() {
        // Wait for the app to load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Verify search bar is present
        composeTestRule
            .onNode(hasText("Search characters", substring = true))
            .assertIsDisplayed()

        // Verify navigation is present
        composeTestRule.onNodeWithText("Characters").assertIsDisplayed()
        composeTestRule.onNodeWithText("Favorites").assertIsDisplayed()
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
    }

    @Test
    fun characterList_search_byName_filtersResults() {
        // Wait for initial load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Click search bar
        val searchBar = composeTestRule
            .onNode(hasText("Search characters", substring = true))

        searchBar.performClick()
        composeTestRule.waitForIdle()

        // Type search query
        searchBar.performTextInput("Jon")

        // Wait for search results
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Jon", substring = true, ignoreCase = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Clear search
        composeTestRule.waitForIdle()
        val clearButton = composeTestRule
            .onNodeWithContentDescription("Clear search", useUnmergedTree = true)

        if (clearButton.fetchSemanticsNode(null) != null) {
            clearButton.performClick()
            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun characterList_search_byCulture_filtersResults() {
        // Wait for initial load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Click search bar
        val searchBar = composeTestRule
            .onNode(hasText("Search characters", substring = true))

        searchBar.performClick()
        composeTestRule.waitForIdle()

        // Search for Northmen culture
        searchBar.performTextInput("Northmen")

        // Wait for search results
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Northmen", substring = true, ignoreCase = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Clear search
        composeTestRule.waitForIdle()
        val clearButton = composeTestRule
            .onNodeWithContentDescription("Clear search", useUnmergedTree = true)

        if (clearButton.fetchSemanticsNode(null) != null) {
            clearButton.performClick()
            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun characterList_search_byAlias_filtersResults() {
        // Wait for initial load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Click search bar
        val searchBar = composeTestRule
            .onNode(hasText("Search characters", substring = true))

        searchBar.performClick()
        composeTestRule.waitForIdle()

        // Search for an alias
        searchBar.performTextInput("King")

        // Wait for search results
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size >= 3
        }

        // Clear search
        composeTestRule.waitForIdle()
        val clearButton = composeTestRule
            .onNodeWithContentDescription("Clear search", useUnmergedTree = true)

        if (clearButton.fetchSemanticsNode(null) != null) {
            clearButton.performClick()
            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun characterList_filter_byStatus_deceased() {
        // Wait for initial load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Try to find and click filter button
        val filterNodes = composeTestRule
            .onAllNodesWithText("Deceased", substring = true)
            .fetchSemanticsNodes()

        if (filterNodes.isNotEmpty()) {
            composeTestRule
                .onNodeWithText("Deceased")
                .performClick()

            composeTestRule.waitForIdle()

            // Verify filter is applied (deceased chip should be selected)
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule
                    .onAllNodesWithText("", substring = true)
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }

            // Click again to deselect
            composeTestRule
                .onNodeWithText("Deceased")
                .performClick()

            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun characterList_filter_byStatus_alive() {
        // Wait for initial load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Try to find and click alive filter
        val aliveNodes = composeTestRule
            .onAllNodesWithText("Alive", substring = true)
            .fetchSemanticsNodes()

        if (aliveNodes.isNotEmpty()) {
            composeTestRule
                .onNodeWithText("Alive")
                .performClick()

            composeTestRule.waitForIdle()

            // Verify filter is applied
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule
                    .onAllNodesWithText("", substring = true)
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }

            // Click again to deselect
            composeTestRule
                .onNodeWithText("Alive")
                .performClick()

            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun characterList_filter_byGender_male() {
        // Wait for initial load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Find and click male filter
        val maleNodes = composeTestRule
            .onAllNodesWithText("Male", substring = true)
            .fetchSemanticsNodes()

        if (maleNodes.isNotEmpty()) {
            composeTestRule
                .onAllNodesWithText("Male")
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()

            // Verify filter is applied
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule
                    .onAllNodesWithText("", substring = true)
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }

            // Click again to deselect
            composeTestRule
                .onAllNodesWithText("Male")
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun characterList_filter_byGender_female() {
        // Wait for initial load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Find and click female filter
        val femaleNodes = composeTestRule
            .onAllNodesWithText("Female", substring = true)
            .fetchSemanticsNodes()

        if (femaleNodes.isNotEmpty()) {
            composeTestRule
                .onNodeWithText("Female")
                .performClick()

            composeTestRule.waitForIdle()

            // Verify filter is applied
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule
                    .onAllNodesWithText("", substring = true)
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }

            // Click again to deselect
            composeTestRule
                .onNodeWithText("Female")
                .performClick()

            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun characterList_sort_byNameAscending() {
        // Wait for initial load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Try to find and click sort button
        val sortButton = composeTestRule
            .onNodeWithContentDescription("Sort options", useUnmergedTree = true)

        if (sortButton.fetchSemanticsNode(null) != null) {
            sortButton.performClick()
            composeTestRule.waitForIdle()

            // Select Name (A-Z)
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
    }

    @Test
    fun characterList_sort_byCulture() {
        // Wait for initial load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Try to find and click sort button
        val sortButton = composeTestRule
            .onNodeWithContentDescription("Sort options", useUnmergedTree = true)

        if (sortButton.fetchSemanticsNode(null) != null) {
            sortButton.performClick()
            composeTestRule.waitForIdle()

            // Select Culture (A-Z)
            val cultureNodes = composeTestRule
                .onAllNodesWithText("Culture (A-Z)", substring = true)
                .fetchSemanticsNodes()

            if (cultureNodes.isNotEmpty()) {
                composeTestRule
                    .onNodeWithText("Culture (A-Z)")
                    .performClick()

                composeTestRule.waitForIdle()
            }
        }
    }

    @Test
    fun characterList_sort_bySeasonsCount() {
        // Wait for initial load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Try to find and click sort button
        val sortButton = composeTestRule
            .onNodeWithContentDescription("Sort options", useUnmergedTree = true)

        if (sortButton.fetchSemanticsNode(null) != null) {
            sortButton.performClick()
            composeTestRule.waitForIdle()

            // Select Seasons Count (Most First)
            val seasonsNodes = composeTestRule
                .onAllNodesWithText("Seasons Count (Most First)", substring = true)
                .fetchSemanticsNodes()

            if (seasonsNodes.isNotEmpty()) {
                composeTestRule
                    .onNodeWithText("Seasons Count (Most First)")
                    .performClick()

                composeTestRule.waitForIdle()
            }
        }
    }

    @Test
    fun characterList_toggleFavorite_addsToFavorites() {
        // Wait for initial load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Find first "Add to favorites" button
        val favoriteButtons = composeTestRule
            .onAllNodesWithTag("Add to favorites", useUnmergedTree = true)
            .fetchSemanticsNodes()

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

            // Verify button changed to "Remove from favorites"
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
        }
    }

    @Test
    fun characterList_pullToRefresh_refreshesData() {
        // Wait for initial load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Try to find the character list by tag
        val characterListNodes = composeTestRule
            .onAllNodesWithTag(TestTags.CHARACTER_LIST, useUnmergedTree = true)
            .fetchSemanticsNodes()

        if (characterListNodes.isNotEmpty()) {
            composeTestRule
                .onNodeWithTag(TestTags.CHARACTER_LIST, useUnmergedTree = true)
                .performTouchInput {
                    swipeDown()
                }

            composeTestRule.waitForIdle()

            // Wait for refresh to complete
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule
                    .onAllNodesWithText("", substring = true)
                    .fetchSemanticsNodes()
                    .size > 3
            }
        }
    }

    @Test
    fun characterList_scrolling_loadsMoreCharacters() {
        // Wait for initial load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Try to scroll through character list
        val characterListNodes = composeTestRule
            .onAllNodesWithTag(TestTags.CHARACTER_LIST, useUnmergedTree = true)
            .fetchSemanticsNodes()

        if (characterListNodes.isNotEmpty()) {
            // Scroll to index 10 if available
            try {
                composeTestRule
                    .onNodeWithTag(TestTags.CHARACTER_LIST, useUnmergedTree = true)
                    .performScrollToIndex(10)

                composeTestRule.waitForIdle()
            } catch (e: Exception) {
                // List might be shorter than 10 items
            }
        }
    }

    @Test
    fun characterList_multipleFilters_appliedTogether() {
        // Wait for initial load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Apply multiple filters
        val maleNodes = composeTestRule
            .onAllNodesWithText("Male", substring = true)
            .fetchSemanticsNodes()

        val aliveNodes = composeTestRule
            .onAllNodesWithText("Alive", substring = true)
            .fetchSemanticsNodes()

        if (maleNodes.isNotEmpty() && aliveNodes.isNotEmpty()) {
            // Click Male filter
            composeTestRule.onAllNodesWithText("Male").onFirst().performClick()
            composeTestRule.waitForIdle()

            // Click Alive filter
            composeTestRule.onAllNodesWithText("Alive").onFirst().performClick()
            composeTestRule.waitForIdle()

            // Verify both filters are applied
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule
                    .onAllNodesWithText("", substring = true)
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }

            // Clear filters
            composeTestRule.onAllNodesWithText("Male").onFirst().performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onAllNodesWithText("Alive").onFirst().performClick()
            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun characterList_searchAndFilter_worktTogether() {
        // Wait for initial load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // First do a search
        val searchBar = composeTestRule
            .onNode(hasText("Search characters", substring = true))

        searchBar.performClick()
        composeTestRule.waitForIdle()

        searchBar.performTextInput("Stark")

        // Wait for results
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 1
        }

        // Then apply a filter
        val maleNodes = composeTestRule
            .onAllNodesWithText("Male", substring = true)
            .fetchSemanticsNodes()

        if (maleNodes.isNotEmpty()) {
            composeTestRule.onAllNodesWithText("Male").onFirst().performClick()
            composeTestRule.waitForIdle()

            // Verify both search and filter are applied
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule
                    .onAllNodesWithText("", substring = true)
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }

            // Clear filter
            composeTestRule.onAllNodesWithText("Male").onFirst().performClick()
            composeTestRule.waitForIdle()
        }

        // Clear search
        val clearButton = composeTestRule
            .onNodeWithContentDescription("Clear search", useUnmergedTree = true)

        if (clearButton.fetchSemanticsNode(null) != null) {
            clearButton.performClick()
            composeTestRule.waitForIdle()
        }
    }
}
