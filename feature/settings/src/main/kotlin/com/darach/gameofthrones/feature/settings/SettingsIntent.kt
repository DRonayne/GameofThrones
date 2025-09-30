package com.darach.gameofthrones.feature.settings

import com.darach.gameofthrones.core.data.preferences.ThemeMode

/**
 * Represents user intents/actions in the Settings screen.
 */
sealed interface SettingsIntent {
    data class UpdateThemeMode(val themeMode: ThemeMode) : SettingsIntent
    data class UpdateDynamicColors(val enabled: Boolean) : SettingsIntent
    data class UpdateCacheExpiration(val hours: Int) : SettingsIntent
    data object ClearCache : SettingsIntent
    data object ClearSearchHistory : SettingsIntent
    data object SyncData : SettingsIntent
    data object ClearAllData : SettingsIntent
}
