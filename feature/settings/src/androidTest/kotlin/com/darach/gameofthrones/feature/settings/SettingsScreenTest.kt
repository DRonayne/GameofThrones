package com.darach.gameofthrones.feature.settings

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.darach.gameofthrones.core.data.preferences.ThemeMode
import com.darach.gameofthrones.core.data.preferences.UserPreferences
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val defaultState = SettingsState(
        userPreferences = UserPreferences(
            themeMode = ThemeMode.SYSTEM,
            useDynamicColors = false,
            cacheExpirationHours = 24,
            searchHistory = List(10) { "query$it" }
        ),
        appVersion = "1.0.0",
        buildNumber = "100"
    )

    @Test
    fun displaysTitle() {
        composeTestRule.setContent {
            SettingsContent(
                state = defaultState,
                onIntent = {},
                snackbarHostState = SnackbarHostState()
            )
        }

        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
    }

    @Test
    fun displaysThemeSection() {
        composeTestRule.setContent {
            SettingsContent(
                state = defaultState,
                onIntent = {},
                snackbarHostState = SnackbarHostState()
            )
        }

        composeTestRule.onNodeWithText("Theme").assertIsDisplayed()
        composeTestRule.onNodeWithText("Theme Mode").assertIsDisplayed()
        composeTestRule.onNodeWithText("Light").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dark").assertIsDisplayed()
        composeTestRule.onNodeWithText("System Default").assertIsDisplayed()
    }

    @Test
    fun themeModeSelectionCallsIntent() {
        val onIntent = mockk<(SettingsIntent) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            SettingsContent(
                state = defaultState,
                onIntent = onIntent,
                snackbarHostState = SnackbarHostState()
            )
        }

        composeTestRule.onNodeWithText("Light").performClick()

        verify { onIntent(SettingsIntent.UpdateThemeMode(ThemeMode.LIGHT)) }
    }

    @Test
    fun darkThemeModeSelectionCallsIntent() {
        val onIntent = mockk<(SettingsIntent) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            SettingsContent(
                state = defaultState,
                onIntent = onIntent,
                snackbarHostState = SnackbarHostState()
            )
        }

        composeTestRule.onNodeWithText("Dark").performClick()

        verify { onIntent(SettingsIntent.UpdateThemeMode(ThemeMode.DARK)) }
    }

    @Test
    fun displaysCacheManagementSection() {
        composeTestRule.setContent {
            SettingsContent(
                state = defaultState,
                onIntent = {},
                snackbarHostState = SnackbarHostState()
            )
        }

        composeTestRule.onNodeWithText("Cache Management").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cache Expiration").assertIsDisplayed()
        composeTestRule.onNodeWithText("24 hours").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clear Cache").assertIsDisplayed()
    }

    @Test
    fun clearCacheButtonShowsDialog() {
        composeTestRule.setContent {
            SettingsContent(
                state = defaultState,
                onIntent = {},
                snackbarHostState = SnackbarHostState()
            )
        }

        composeTestRule.onNodeWithText("Clear Cache").performClick()

        composeTestRule.onNodeWithText("Clear Cache?").assertIsDisplayed()
        composeTestRule.onNodeWithText(
            "This will remove all cached character data. The data will be re-downloaded when needed."
        ).assertIsDisplayed()
    }

    @Test
    fun clearCacheDialogConfirmCallsIntent() {
        val onIntent = mockk<(SettingsIntent) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            SettingsContent(
                state = defaultState,
                onIntent = onIntent,
                snackbarHostState = SnackbarHostState()
            )
        }

        composeTestRule.onNodeWithText("Clear Cache").performClick()
        composeTestRule.onNodeWithText("Confirm").performClick()

        verify { onIntent(SettingsIntent.ClearCache) }
    }

    @Test
    fun clearCacheDialogCancelDismissesDialog() {
        composeTestRule.setContent {
            SettingsContent(
                state = defaultState,
                onIntent = {},
                snackbarHostState = SnackbarHostState()
            )
        }

        composeTestRule.onNodeWithText("Clear Cache").performClick()
        composeTestRule.onNodeWithText("Cancel").performClick()

        composeTestRule.onNodeWithText("Clear Cache?").assertDoesNotExist()
    }


    @Test
    fun syncDataButtonCallsIntent() {
        val onIntent = mockk<(SettingsIntent) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            SettingsContent(
                state = defaultState,
                onIntent = onIntent,
                snackbarHostState = SnackbarHostState()
            )
        }

        composeTestRule.onNodeWithText("Sync Data").performClick()

        verify { onIntent(SettingsIntent.SyncData) }
    }

    @Test
    fun syncDataButtonDisabledWhenSyncing() {
        composeTestRule.setContent {
            SettingsContent(
                state = defaultState.copy(isSyncing = true),
                onIntent = {},
                snackbarHostState = SnackbarHostState()
            )
        }

        composeTestRule.onNodeWithText("Sync Data").assertIsNotEnabled()
    }






    @Test
    fun clearCacheButtonDisabledWhenLoading() {
        composeTestRule.setContent {
            SettingsContent(
                state = defaultState.copy(isLoading = true),
                onIntent = {},
                snackbarHostState = SnackbarHostState()
            )
        }

        composeTestRule.onNodeWithText("Clear Cache").assertIsNotEnabled()
    }

    @Test
    fun confirmationDialogDisplaysCorrectly() {
        composeTestRule.setContent {
            ConfirmationDialog(
                title = "Test Title",
                message = "Test Message",
                onConfirm = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Test Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Message").assertIsDisplayed()
        composeTestRule.onNodeWithText("Confirm").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun confirmationDialogConfirmCallsCallback() {
        var confirmed = false

        composeTestRule.setContent {
            ConfirmationDialog(
                title = "Test",
                message = "Test",
                onConfirm = { confirmed = true },
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Confirm").performClick()

        assert(confirmed)
    }

    @Test
    fun confirmationDialogCancelCallsCallback() {
        var dismissed = false

        composeTestRule.setContent {
            ConfirmationDialog(
                title = "Test",
                message = "Test",
                onConfirm = {},
                onDismiss = { dismissed = true }
            )
        }

        composeTestRule.onNodeWithText("Cancel").performClick()

        assert(dismissed)
    }
}
