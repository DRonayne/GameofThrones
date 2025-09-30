package com.darach.gameofthrones.feature.settings

import com.darach.gameofthrones.core.data.preferences.ThemeMode
import com.darach.gameofthrones.core.data.preferences.UserPreferences

/**
 * Represents the UI state for the Settings screen.
 */
data class SettingsState(
    val userPreferences: UserPreferences = UserPreferences(),
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val message: String? = null,
    val appVersion: String = "",
    val buildNumber: String = ""
) {
    val themeMode: ThemeMode
        get() = userPreferences.themeMode

    val useDynamicColors: Boolean
        get() = userPreferences.useDynamicColors

    val cacheExpirationHours: Int
        get() = userPreferences.cacheExpirationHours

    val searchHistorySize: Int
        get() = userPreferences.searchHistory.size
}
