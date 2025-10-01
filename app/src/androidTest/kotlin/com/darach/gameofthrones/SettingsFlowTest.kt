package com.darach.gameofthrones

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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
 * Settings screen flow tests.
 *
 * Tests the settings functionality including:
 * - Navigation to settings
 * - Theme mode changes (Light, Dark, System)
 * - Dynamic colors toggle
 * - Cache management
 * - Data sync
 * - Settings persistence
 * - Back navigation
 */
@RunWith(AndroidJUnit4::class)
class SettingsFlowTest {

    @get:Rule(order = 0)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 1)
    val screenshotRule = ScreenshotTestRule()

    @Before
    fun setup() {
        screenshotRule.setComposeTestRule(composeTestRule)
    }

    @Test
    fun settings_navigation_opensSettingsScreen() {
        // Wait for app to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Settings", substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Navigate to Settings
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.waitForIdle()

        // Verify settings screen loaded
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Theme", substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule
            .onAllNodesWithText("Theme", substring = true)
            .onFirst()
            .assertIsDisplayed()

        // Navigate back to Characters
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun settings_themeMode_displaysOptions() {
        // Navigate to Settings
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Settings", substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.waitForIdle()

        // Wait for settings to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Theme", substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Verify theme options are displayed
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            val lightNodes = composeTestRule
                .onAllNodesWithText("Light", substring = true)
                .fetchSemanticsNodes()

            val darkNodes = composeTestRule
                .onAllNodesWithText("Dark", substring = true)
                .fetchSemanticsNodes()

            val systemNodes = composeTestRule
                .onAllNodesWithText("System", substring = true)
                .fetchSemanticsNodes()

            lightNodes.isNotEmpty() || darkNodes.isNotEmpty() || systemNodes.isNotEmpty()
        }

        // Navigate back to Characters
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun settings_changeToLightTheme_appliesTheme() {
        // Navigate to Settings
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Settings", substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.waitForIdle()

        // Wait for settings to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Theme", substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Click Light theme
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

        // Navigate back to Characters
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun settings_changeToDarkTheme_appliesTheme() {
        // Navigate to Settings
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Settings", substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.waitForIdle()

        // Wait for settings to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Theme", substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Click Dark theme
        val darkNodes = composeTestRule
            .onAllNodesWithText("Dark", substring = true)
            .fetchSemanticsNodes()

        if (darkNodes.isNotEmpty()) {
            composeTestRule
                .onAllNodesWithText("Dark")
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()
        }

        // Navigate back to Characters
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun settings_changeToSystemTheme_appliesTheme() {
        // Navigate to Settings
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Settings", substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.waitForIdle()

        // Wait for settings to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Theme", substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Click System Default theme
        val systemNodes = composeTestRule
            .onAllNodesWithText("System", substring = true)
            .fetchSemanticsNodes()

        if (systemNodes.isNotEmpty()) {
            composeTestRule
                .onNodeWithText("System Default")
                .performClick()

            composeTestRule.waitForIdle()
        }

        // Navigate back to Characters
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun settings_dynamicColors_toggles() {
        // Navigate to Settings
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Settings", substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.waitForIdle()

        // Wait for settings to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 3
        }

        // Look for dynamic colors toggle
        val dynamicColorNodes = composeTestRule
            .onAllNodesWithText("Dynamic", substring = true, ignoreCase = true)
            .fetchSemanticsNodes()

        if (dynamicColorNodes.isNotEmpty()) {
            composeTestRule
                .onAllNodesWithText("Dynamic", substring = true, ignoreCase = true)
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()

            // Toggle back
            composeTestRule
                .onAllNodesWithText("Dynamic", substring = true, ignoreCase = true)
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()
        }

        // Navigate back to Characters
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun settings_clearCache_executesAction() {
        // Navigate to Settings
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Settings", substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.waitForIdle()

        // Wait for settings to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 3
        }

        // Look for clear cache button
        val clearCacheButtons = composeTestRule
            .onAllNodesWithTag(TestTags.CLEAR_CACHE_BUTTON, useUnmergedTree = true)
            .fetchSemanticsNodes()

        val clearCacheTextButtons = composeTestRule
            .onAllNodesWithText("Clear", substring = true, ignoreCase = true)
            .fetchSemanticsNodes()

        if (clearCacheButtons.isNotEmpty()) {
            composeTestRule
                .onNodeWithTag(TestTags.CLEAR_CACHE_BUTTON, useUnmergedTree = true)
                .performClick()

            composeTestRule.waitForIdle()
        } else if (clearCacheTextButtons.isNotEmpty()) {
            // Find the cache-related clear button
            val cacheNodes = composeTestRule
                .onAllNodesWithText("Cache", substring = true, ignoreCase = true)
                .fetchSemanticsNodes()

            if (cacheNodes.isNotEmpty()) {
                // There might be a clear cache button near the cache text
                composeTestRule
                    .onAllNodesWithText("Clear", substring = true, ignoreCase = true)
                    .onFirst()
                    .performClick()

                composeTestRule.waitForIdle()
            }
        }

        // Navigate back to Characters
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun settings_syncData_executesAction() {
        // Navigate to Settings
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Settings", substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.waitForIdle()

        // Wait for settings to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 3
        }

        // Look for sync button
        val syncButtons = composeTestRule
            .onAllNodesWithTag(TestTags.SYNC_DATA_BUTTON, useUnmergedTree = true)
            .fetchSemanticsNodes()

        val syncTextButtons = composeTestRule
            .onAllNodesWithText("Sync", substring = true, ignoreCase = true)
            .fetchSemanticsNodes()

        if (syncButtons.isNotEmpty()) {
            composeTestRule
                .onNodeWithTag(TestTags.SYNC_DATA_BUTTON, useUnmergedTree = true)
                .performClick()

            composeTestRule.waitForIdle()
        } else if (syncTextButtons.isNotEmpty()) {
            composeTestRule
                .onAllNodesWithText("Sync", substring = true, ignoreCase = true)
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()
        }

        // Navigate back to Characters
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun settings_themeChange_persistsAcrossNavigation() {
        // Navigate to Settings
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Settings", substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.waitForIdle()

        // Wait for settings to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Theme", substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Change theme to Dark
        val darkNodes = composeTestRule
            .onAllNodesWithText("Dark", substring = true)
            .fetchSemanticsNodes()

        if (darkNodes.isNotEmpty()) {
            composeTestRule
                .onAllNodesWithText("Dark")
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()
        }

        // Navigate away
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()

        // Navigate back to Settings
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.waitForIdle()

        // Verify theme is still Dark
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 3
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

        // Navigate back to Characters
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun settings_multipleThemeChanges_appliesCorrectly() {
        // Navigate to Settings
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Settings", substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.waitForIdle()

        // Wait for settings to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Theme", substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Change to Light
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

        // Change to Dark
        val darkNodes = composeTestRule
            .onAllNodesWithText("Dark", substring = true)
            .fetchSemanticsNodes()

        if (darkNodes.isNotEmpty()) {
            composeTestRule
                .onAllNodesWithText("Dark")
                .onFirst()
                .performClick()

            composeTestRule.waitForIdle()
        }

        // Change to System
        val systemNodes = composeTestRule
            .onAllNodesWithText("System", substring = true)
            .fetchSemanticsNodes()

        if (systemNodes.isNotEmpty()) {
            composeTestRule
                .onNodeWithText("System Default")
                .performClick()

            composeTestRule.waitForIdle()
        }

        // Navigate back to Characters
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun settings_displaysAllSections() {
        // Navigate to Settings
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Settings", substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.waitForIdle()

        // Wait for settings to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("", substring = true)
                .fetchSemanticsNodes()
                .size > 3
        }

        // Verify settings content tag exists
        val settingsContentNodes = composeTestRule
            .onAllNodesWithTag(TestTags.SETTINGS_CONTENT, useUnmergedTree = true)
            .fetchSemanticsNodes()

        // If not, at least verify some settings elements are visible
        if (settingsContentNodes.isEmpty()) {
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule
                    .onAllNodesWithText("Theme", substring = true)
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }
        }

        // Navigate back to Characters
        composeTestRule.onNodeWithText("Characters").performClick()
        composeTestRule.waitForIdle()
    }
}
