package com.darach.gameofthrones.core.data.preferences

/**
 * Data class representing user preferences for the app.
 */
data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val useDynamicColors: Boolean = true,
    val cacheExpirationHours: Int = DEFAULT_CACHE_EXPIRATION_HOURS,
    val searchHistory: List<String> = emptyList(),
    val maxSearchHistorySize: Int = DEFAULT_MAX_SEARCH_HISTORY_SIZE,
    val defaultSortOption: SortOption = SortOption.NAME_ASC,
    val defaultFilterCulture: String? = null,
    val defaultFilterGender: String? = null,
    val showDeadCharacters: Boolean = true,
    val showAliveCharacters: Boolean = true
) {
    companion object {
        const val DEFAULT_CACHE_EXPIRATION_HOURS = 24
        const val DEFAULT_MAX_SEARCH_HISTORY_SIZE = 10
    }
}

/**
 * Theme mode options for the app.
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

/**
 * Sort options for character lists.
 */
enum class SortOption {
    NAME_ASC,
    NAME_DESC,
    CULTURE_ASC,
    CULTURE_DESC,
    RECENT
}
