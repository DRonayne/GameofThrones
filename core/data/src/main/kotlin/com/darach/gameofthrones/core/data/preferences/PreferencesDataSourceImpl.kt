package com.darach.gameofthrones.core.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

/**
 * Implementation of PreferencesDataSource using DataStore.
 * Provides type-safe access to user preferences with reactive updates.
 */
@Singleton
class PreferencesDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PreferencesDataSource {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val USE_DYNAMIC_COLORS = booleanPreferencesKey("use_dynamic_colors")
        val CACHE_EXPIRATION_HOURS = intPreferencesKey("cache_expiration_hours")
        val SEARCH_HISTORY = stringPreferencesKey("search_history")
        val MAX_SEARCH_HISTORY_SIZE = intPreferencesKey("max_search_history_size")
        val DEFAULT_SORT_OPTION = stringPreferencesKey("default_sort_option")
        val DEFAULT_FILTER_CULTURE = stringPreferencesKey("default_filter_culture")
        val DEFAULT_FILTER_GENDER = stringPreferencesKey("default_filter_gender")
        val SHOW_DEAD_CHARACTERS = booleanPreferencesKey("show_dead_characters")
        val SHOW_ALIVE_CHARACTERS = booleanPreferencesKey("show_alive_characters")
    }

    override val userPreferences: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            mapPreferences(preferences)
        }

    override suspend fun updateThemePreferences(themeMode: ThemeMode?, useDynamicColors: Boolean?) {
        context.dataStore.edit { preferences ->
            themeMode?.let { preferences[PreferencesKeys.THEME_MODE] = it.name }
            useDynamicColors?.let { preferences[PreferencesKeys.USE_DYNAMIC_COLORS] = it }
        }
    }

    override suspend fun updateCacheExpiration(hours: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CACHE_EXPIRATION_HOURS] = hours
        }
    }

    override suspend fun addSearchQuery(query: String) {
        context.dataStore.edit { preferences ->
            val currentHistory = preferences[PreferencesKeys.SEARCH_HISTORY]
                ?.split(DELIMITER)
                ?.filter { it.isNotBlank() }
                ?.toMutableList()
                ?: mutableListOf()

            val maxSize = preferences[PreferencesKeys.MAX_SEARCH_HISTORY_SIZE]
                ?: UserPreferences.DEFAULT_MAX_SEARCH_HISTORY_SIZE

            // Remove if already exists to avoid duplicates
            currentHistory.remove(query)

            // Add to the beginning
            currentHistory.add(0, query)

            // Trim to max size
            val trimmedHistory = currentHistory.take(maxSize)

            preferences[PreferencesKeys.SEARCH_HISTORY] = trimmedHistory.joinToString(DELIMITER)
        }
    }

    override suspend fun clearSearchHistory() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.SEARCH_HISTORY)
        }
    }

    override suspend fun updateFilterPreferences(
        sortOption: SortOption?,
        culture: String?,
        gender: String?,
        showDead: Boolean?,
        showAlive: Boolean?
    ) {
        context.dataStore.edit { preferences ->
            sortOption?.let { preferences[PreferencesKeys.DEFAULT_SORT_OPTION] = it.name }

            if (culture != null) {
                preferences[PreferencesKeys.DEFAULT_FILTER_CULTURE] = culture
            }

            if (gender != null) {
                preferences[PreferencesKeys.DEFAULT_FILTER_GENDER] = gender
            }

            showDead?.let { preferences[PreferencesKeys.SHOW_DEAD_CHARACTERS] = it }
            showAlive?.let { preferences[PreferencesKeys.SHOW_ALIVE_CHARACTERS] = it }
        }
    }

    override suspend fun clearAllPreferences() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private fun mapPreferences(preferences: Preferences): UserPreferences {
        val themeMode = preferences[PreferencesKeys.THEME_MODE]?.let {
            runCatching { ThemeMode.valueOf(it) }.getOrDefault(ThemeMode.SYSTEM)
        } ?: ThemeMode.SYSTEM

        val useDynamicColors = preferences[PreferencesKeys.USE_DYNAMIC_COLORS] ?: true

        val cacheExpirationHours = preferences[PreferencesKeys.CACHE_EXPIRATION_HOURS]
            ?: UserPreferences.DEFAULT_CACHE_EXPIRATION_HOURS

        val searchHistory = preferences[PreferencesKeys.SEARCH_HISTORY]
            ?.split(DELIMITER)
            ?.filter { it.isNotBlank() }
            ?: emptyList()

        val maxSearchHistorySize = preferences[PreferencesKeys.MAX_SEARCH_HISTORY_SIZE]
            ?: UserPreferences.DEFAULT_MAX_SEARCH_HISTORY_SIZE

        val defaultSortOption = preferences[PreferencesKeys.DEFAULT_SORT_OPTION]?.let {
            runCatching { SortOption.valueOf(it) }.getOrDefault(SortOption.NAME_ASC)
        } ?: SortOption.NAME_ASC

        val defaultFilterCulture = preferences[PreferencesKeys.DEFAULT_FILTER_CULTURE]
        val defaultFilterGender = preferences[PreferencesKeys.DEFAULT_FILTER_GENDER]

        val showDeadCharacters = preferences[PreferencesKeys.SHOW_DEAD_CHARACTERS] ?: true
        val showAliveCharacters = preferences[PreferencesKeys.SHOW_ALIVE_CHARACTERS] ?: true

        return UserPreferences(
            themeMode = themeMode,
            useDynamicColors = useDynamicColors,
            cacheExpirationHours = cacheExpirationHours,
            searchHistory = searchHistory,
            maxSearchHistorySize = maxSearchHistorySize,
            defaultSortOption = defaultSortOption,
            defaultFilterCulture = defaultFilterCulture,
            defaultFilterGender = defaultFilterGender,
            showDeadCharacters = showDeadCharacters,
            showAliveCharacters = showAliveCharacters
        )
    }

    companion object {
        private const val DELIMITER = "|||"
    }
}
