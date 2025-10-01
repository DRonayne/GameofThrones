package com.darach.gameofthrones.core.analytics

/**
 * Service for tracking analytics events throughout the app.
 * Abstracts the underlying analytics implementation.
 */
interface AnalyticsService {
    /**
     * Log a screen view event.
     *
     * @param screenName The name of the screen
     * @param screenClass The class name of the screen
     */
    fun logScreenView(screenName: String, screenClass: String)

    /**
     * Log a custom event with optional parameters.
     *
     * @param eventName The name of the event
     * @param params Optional parameters for the event
     */
    fun logEvent(eventName: String, params: Map<String, Any>? = null)

    /**
     * Set a user property.
     *
     * @param name The property name
     * @param value The property value
     */
    fun setUserProperty(name: String, value: String)

    /**
     * Set the user ID for analytics tracking.
     *
     * @param userId The user ID
     */
    fun setUserId(userId: String?)
}

/**
 * Analytics event names
 */
object AnalyticsEvents {
    // Screen views
    const val SCREEN_VIEW = "screen_view"

    // Character events
    const val CHARACTER_VIEWED = "character_viewed"
    const val CHARACTER_FAVORITED = "character_favorited"
    const val CHARACTER_UNFAVORITED = "character_unfavorited"

    // Search events
    const val SEARCH_QUERY = "search_query"
    const val SEARCH_CLEARED = "search_cleared"

    // Filter events
    const val FILTER_APPLIED = "filter_applied"
    const val FILTER_CLEARED = "filter_cleared"
    const val SORT_APPLIED = "sort_applied"

    // Comparison events
    const val COMPARISON_STARTED = "comparison_started"
    const val COMPARISON_CHARACTER_ADDED = "comparison_character_added"
    const val COMPARISON_CHARACTER_REMOVED = "comparison_character_removed"
    const val COMPARISON_CLEARED = "comparison_cleared"

    // Settings events
    const val THEME_CHANGED = "theme_changed"
    const val DYNAMIC_COLORS_TOGGLED = "dynamic_colors_toggled"

    // Data events
    const val DATA_REFRESH = "data_refresh"
    const val DATA_SYNC_SUCCESS = "data_sync_success"
    const val DATA_SYNC_FAILED = "data_sync_failed"
}

/**
 * Analytics parameter names
 */
object AnalyticsParams {
    // Character params
    const val CHARACTER_ID = "character_id"
    const val CHARACTER_NAME = "character_name"

    // Search params
    const val SEARCH_TERM = "search_term"
    const val SEARCH_RESULTS_COUNT = "results_count"

    // Filter params
    const val FILTER_TYPE = "filter_type"
    const val FILTER_VALUE = "filter_value"
    const val SORT_TYPE = "sort_type"

    // Comparison params
    const val COMPARISON_COUNT = "comparison_count"

    // Settings params
    const val THEME_MODE = "theme_mode"
    const val DYNAMIC_COLORS_ENABLED = "dynamic_colors_enabled"

    // Error params
    const val ERROR_MESSAGE = "error_message"
    const val ERROR_CODE = "error_code"
}

/**
 * User property names
 */
object UserProperties {
    const val THEME_PREFERENCE = "theme_preference"
    const val DYNAMIC_COLORS_PREFERENCE = "dynamic_colors_preference"
    const val FAVORITES_COUNT = "favorites_count"
}
