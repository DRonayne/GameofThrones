package com.darach.gameofthrones.core.data.preferences

import kotlinx.coroutines.flow.Flow

/**
 * Interface for accessing user preferences data.
 * Provides reactive access to app settings and user preferences.
 */
interface PreferencesDataSource {

    /**
     * Flow of user preferences that emits whenever preferences change.
     */
    val userPreferences: Flow<UserPreferences>

    /**
     * Updates theme-related preferences (theme mode and dynamic colors).
     */
    suspend fun updateThemePreferences(
        themeMode: ThemeMode? = null,
        useDynamicColors: Boolean? = null
    )

    /**
     * Updates the cache expiration time in hours.
     */
    suspend fun updateCacheExpiration(hours: Int)

    /**
     * Adds a search query to the history.
     * Maintains a maximum history size by removing oldest entries.
     */
    suspend fun addSearchQuery(query: String)

    /**
     * Clears the search history.
     */
    suspend fun clearSearchHistory()

    /**
     * Updates filter preferences (sort, culture, gender, character status).
     */
    suspend fun updateFilterPreferences(
        sortOption: SortOption? = null,
        culture: String? = null,
        gender: String? = null,
        showDead: Boolean? = null,
        showAlive: Boolean? = null
    )

    /**
     * Clears all preferences and resets to defaults.
     */
    suspend fun clearAllPreferences()
}
