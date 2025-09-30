package com.darach.gameofthrones.core.common.crash

/**
 * Service for crash reporting and non-fatal error tracking.
 * Abstracts the underlying crash reporting implementation.
 */
interface CrashReportingService {
    /**
     * Log a non-fatal exception.
     *
     * @param throwable The exception to log
     */
    fun logException(throwable: Throwable)

    /**
     * Log a custom error message.
     *
     * @param message The error message
     */
    fun log(message: String)

    /**
     * Set a custom key-value pair for crash reports.
     *
     * @param key The key
     * @param value The value
     */
    fun setCustomKey(key: String, value: String)

    /**
     * Set a custom key-value pair for crash reports.
     *
     * @param key The key
     * @param value The value
     */
    fun setCustomKey(key: String, value: Boolean)

    /**
     * Set a custom key-value pair for crash reports.
     *
     * @param key The key
     * @param value The value
     */
    fun setCustomKey(key: String, value: Int)

    /**
     * Set a custom key-value pair for crash reports.
     *
     * @param key The key
     * @param value The value
     */
    fun setCustomKey(key: String, value: Long)

    /**
     * Set a custom key-value pair for crash reports.
     *
     * @param key The key
     * @param value The value
     */
    fun setCustomKey(key: String, value: Float)

    /**
     * Set a custom key-value pair for crash reports.
     *
     * @param key The key
     * @param value The value
     */
    fun setCustomKey(key: String, value: Double)

    /**
     * Set the user identifier for crash reports.
     *
     * @param identifier The user identifier
     */
    fun setUserId(identifier: String?)

    /**
     * Force a crash for testing purposes.
     * Should only be used in debug builds.
     */
    fun forceCrash()
}

/**
 * Crash reporting custom keys
 */
object CrashKeys {
    const val SCREEN_NAME = "screen_name"
    const val CHARACTER_ID = "character_id"
    const val SEARCH_QUERY = "search_query"
    const val FILTER_STATE = "filter_state"
    const val NETWORK_STATE = "network_state"
    const val DATABASE_VERSION = "database_version"
}
