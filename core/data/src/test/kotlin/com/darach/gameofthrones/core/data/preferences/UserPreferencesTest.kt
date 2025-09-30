package com.darach.gameofthrones.core.data.preferences

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class UserPreferencesTest {

    @Test
    fun `UserPreferences has correct default values`() {
        val preferences = UserPreferences()

        assertThat(preferences.themeMode).isEqualTo(ThemeMode.SYSTEM)
        assertThat(preferences.useDynamicColors).isTrue()
        assertThat(preferences.cacheExpirationHours).isEqualTo(24)
        assertThat(preferences.searchHistory).isEmpty()
        assertThat(preferences.maxSearchHistorySize).isEqualTo(10)
        assertThat(preferences.defaultSortOption).isEqualTo(SortOption.NAME_ASC)
        assertThat(preferences.defaultFilterCulture).isNull()
        assertThat(preferences.defaultFilterGender).isNull()
        assertThat(preferences.showDeadCharacters).isTrue()
        assertThat(preferences.showAliveCharacters).isTrue()
    }

    @Test
    fun `UserPreferences can be created with custom values`() {
        val preferences = UserPreferences(
            themeMode = ThemeMode.DARK,
            useDynamicColors = false,
            cacheExpirationHours = 48,
            searchHistory = listOf("Jon Snow", "Arya Stark"),
            maxSearchHistorySize = 20,
            defaultSortOption = SortOption.CULTURE_ASC,
            defaultFilterCulture = "Northmen",
            defaultFilterGender = "Male",
            showDeadCharacters = false,
            showAliveCharacters = true
        )

        assertThat(preferences.themeMode).isEqualTo(ThemeMode.DARK)
        assertThat(preferences.useDynamicColors).isFalse()
        assertThat(preferences.cacheExpirationHours).isEqualTo(48)
        assertThat(preferences.searchHistory).containsExactly("Jon Snow", "Arya Stark")
        assertThat(preferences.maxSearchHistorySize).isEqualTo(20)
        assertThat(preferences.defaultSortOption).isEqualTo(SortOption.CULTURE_ASC)
        assertThat(preferences.defaultFilterCulture).isEqualTo("Northmen")
        assertThat(preferences.defaultFilterGender).isEqualTo("Male")
        assertThat(preferences.showDeadCharacters).isFalse()
        assertThat(preferences.showAliveCharacters).isTrue()
    }

    @Test
    fun `UserPreferences can be copied with modified fields`() {
        val original = UserPreferences()
        val modified = original.copy(themeMode = ThemeMode.DARK, useDynamicColors = false)

        assertThat(modified.themeMode).isEqualTo(ThemeMode.DARK)
        assertThat(modified.useDynamicColors).isFalse()
        assertThat(modified.cacheExpirationHours).isEqualTo(original.cacheExpirationHours)
        assertThat(modified.searchHistory).isEqualTo(original.searchHistory)
    }

    @Test
    fun `ThemeMode enum has all expected values`() {
        val values = ThemeMode.entries

        assertThat(values).containsExactly(ThemeMode.LIGHT, ThemeMode.DARK, ThemeMode.SYSTEM)
    }

    @Test
    fun `SortOption enum has all expected values`() {
        val values = SortOption.entries

        assertThat(values).containsExactly(
            SortOption.NAME_ASC,
            SortOption.NAME_DESC,
            SortOption.CULTURE_ASC,
            SortOption.CULTURE_DESC,
            SortOption.RECENT
        )
    }
}
