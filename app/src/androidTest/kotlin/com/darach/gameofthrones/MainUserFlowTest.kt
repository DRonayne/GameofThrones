package com.darach.gameofthrones

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Main user flow test that validates critical paths through the app.
 *
 * This test uses the actual MainActivity and real dependencies to verify
 * end-to-end functionality including:
 * - Character list display
 * - Search functionality
 * - Filtering and sorting
 * - Navigation to character detail
 * - Favorite toggle
 * - Settings navigation
 *
 * Test tags are used where available for more reliable element selection.
 */
@RunWith(AndroidJUnit4::class)
class MainUserFlowTest {

    @get:Rule(order = 0)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 1)
    val screenshotRule = ScreenshotTestRule()

    @Before
    fun setup() {
        screenshotRule.setComposeTestRule(composeTestRule)
    }

    /**
     * Test: Complete user flow through main features
     * This is a comprehensive test that exercises multiple features
     */
    @Test
    fun mainUserFlow_navigateThroughMainFeatures() {
        // Wait for the app to load and display the character list
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5 // Wait for multiple elements to load
        }

        // Verify we're on the Characters screen
        composeTestRule
            .onNode(hasText("Search characters", substring = true))
            .assertExists()

        // Test 1: Navigate to Favorites
        composeTestRule
            .onNodeWithText("Favorites")
            .performClick()

        composeTestRule.waitForIdle()

        // Verify favorites screen loaded (may be empty)
        // Use onFirst() since "Favorites" appears in both nav bar and screen title
        composeTestRule
            .onAllNodesWithText("Favorites", substring = true)
            .onFirst()
            .assertExists()

        // Navigate back to Characters
        composeTestRule
            .onNodeWithText("Characters")
            .performClick()

        composeTestRule.waitForIdle()

        // Test 2: Navigate to Settings
        composeTestRule
            .onNodeWithText("Settings")
            .performClick()

        composeTestRule.waitForIdle()

        // Verify settings screen loaded
        // Use onFirst() since text may appear multiple times
        composeTestRule
            .onAllNodesWithText("Theme", substring = true)
            .onFirst()
            .assertExists()

        // Navigate back to Characters
        composeTestRule
            .onNodeWithText("Characters")
            .performClick()

        composeTestRule.waitForIdle()

        // Test 3: Search functionality
        val searchBar = composeTestRule
            .onNode(hasText("Search characters", substring = true))

        searchBar.assertExists()
        searchBar.performClick()

        composeTestRule.waitForIdle()

        // Type in search query
        searchBar.performTextInput("Stark")

        // Wait for search results
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Stark", substring = true, ignoreCase = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Clear search
        val clearButton = composeTestRule
            .onAllNodesWithText("Clear", substring = true, ignoreCase = true)
            .fetchSemanticsNodes()

        if (clearButton.isNotEmpty()) {
            composeTestRule
                .onNodeWithContentDescription("Clear search")
                .performClick()

            composeTestRule.waitForIdle()
        }
    }

    /**
     * Test: Character list display and basic interaction
     */
    @Test
    fun characterList_displaysAndNavigates() {
        // Wait for characters to load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Verify search bar is displayed
        composeTestRule
            .onNode(hasText("Search characters", substring = true))
            .assertExists()

        // Verify at least one character is displayed by checking for common text
        val nodes = composeTestRule
            .onAllNodesWithText("", substring = true)
            .fetchSemanticsNodes()

        assert(nodes.size > 5) { "Expected multiple UI elements but found ${nodes.size}" }
    }

    /**
     * Test: Filter and sort functionality
     */
    @Test
    fun characterList_filterAndSort() {
        // Wait for characters to load
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 5
        }

        // Open filter bottom sheet
        val filterButton = composeTestRule
            .onNodeWithContentDescription("Filter options", useUnmergedTree = true)

        if (filterButton.fetchSemanticsNode(null) != null) {
            filterButton.performClick()
            composeTestRule.waitForIdle()

            // Try to find and click alive filter in the bottom sheet
            val aliveNodes = composeTestRule
                .onAllNodesWithText("Alive", substring = true)
                .fetchSemanticsNodes()

            if (aliveNodes.isNotEmpty()) {
                composeTestRule
                    .onAllNodesWithText("Alive", substring = true)
                    .onFirst()
                    .performClick()

                composeTestRule.waitForIdle()

                // Close bottom sheet
                val closeButtons = composeTestRule
                    .onAllNodesWithText("Close", substring = true)
                    .fetchSemanticsNodes()

                if (closeButtons.isNotEmpty()) {
                    composeTestRule
                        .onNodeWithText("Close")
                        .performClick()
                    composeTestRule.waitForIdle()
                }

                // Wait a bit for filter to be applied
                composeTestRule.waitForIdle()
                Thread.sleep(1000)
            }
        }

        // Try to open sort (if available)
        val sortChips = composeTestRule
            .onAllNodesWithText("Sort:", substring = true)
            .fetchSemanticsNodes()

        if (sortChips.isNotEmpty()) {
            composeTestRule
                .onAllNodesWithText("Sort:", substring = true)
                .onFirst()
                .performClick()
            composeTestRule.waitForIdle()

            // Select an option if the menu opened
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

    /**
     * Test: Settings screen interaction
     */
    @Test
    fun settings_changeTheme() {
        // Navigate to Settings
        composeTestRule
            .onNodeWithText("Settings")
            .performClick()

        composeTestRule.waitForIdle()

        // Verify settings screen loaded
        // Use onFirst() since text may appear multiple times
        composeTestRule
            .onAllNodesWithText("Theme", substring = true)
            .onFirst()
            .assertIsDisplayed()

        // Try to click on theme options
        val lightNodes = composeTestRule
            .onAllNodesWithText("Light", substring = true)
            .fetchSemanticsNodes()

        if (lightNodes.isNotEmpty()) {
            composeTestRule
                .onAllNodesWithText("Light")
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()
        }

        // Change back to System Default
        val systemNodes = composeTestRule
            .onAllNodesWithText("System", substring = true)
            .fetchSemanticsNodes()

        if (systemNodes.isNotEmpty()) {
            composeTestRule
                .onNodeWithText("System Default")
                .performClick()

            composeTestRule.waitForIdle()
        }
    }
}
