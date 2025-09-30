package com.darach.gameofthrones.feature.characters

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.darach.gameofthrones.core.domain.model.Character
import com.darach.gameofthrones.core.domain.usecase.CharacterFilter
import com.darach.gameofthrones.core.domain.usecase.SortOption
import com.darach.gameofthrones.feature.characters.components.CharacterCard
import com.darach.gameofthrones.feature.characters.components.CharactersSearchBar
import com.darach.gameofthrones.feature.characters.components.FilterChips
import com.darach.gameofthrones.feature.characters.components.SearchBarCallbacks
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

class CharactersScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testCharacter = Character(
        id = "1",
        name = "Jon Snow",
        gender = "Male",
        culture = "Northmen",
        born = "",
        died = "",
        titles = emptyList(),
        aliases = listOf("King in the North"),
        father = "",
        mother = "",
        spouse = "",
        allegiances = emptyList(),
        books = emptyList(),
        povBooks = emptyList(),
        tvSeries = listOf("Season 1"),
        tvSeriesSeasons = listOf(1, 2, 3),
        playedBy = emptyList()
    )

    @Test
    fun characterCard_displaysCharacterName() {
        composeTestRule.setContent {
            CharacterCard(
                character = testCharacter,
                onFavoriteClick = {},
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("Jon Snow").assertIsDisplayed()
    }

    @Test
    fun characterCard_displaysCulture() {
        composeTestRule.setContent {
            CharacterCard(
                character = testCharacter,
                onFavoriteClick = {},
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("Northmen").assertIsDisplayed()
    }

    @Test
    fun characterCard_displaysSeasonBadges() {
        composeTestRule.setContent {
            CharacterCard(
                character = testCharacter,
                onFavoriteClick = {},
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("I").assertIsDisplayed()
        composeTestRule.onNodeWithText("II").assertIsDisplayed()
        composeTestRule.onNodeWithText("III").assertIsDisplayed()
    }

    @Test
    fun characterCard_favoriteButtonClickCallsCallback() {
        val onFavoriteClick = mockk<(String) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            CharacterCard(
                character = testCharacter,
                onFavoriteClick = onFavoriteClick,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Add to favorites").performClick()

        verify { onFavoriteClick("1") }
    }

    @Test
    fun characterCard_displaysDeathIndicator() {
        val deadCharacter = testCharacter.copy(died = "299 AC")

        composeTestRule.setContent {
            CharacterCard(
                character = deadCharacter,
                onFavoriteClick = {},
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("\u271D").assertIsDisplayed()
    }

    @Test
    fun searchBar_displaysPlaceholder() {
        composeTestRule.setContent {
            CharactersSearchBar(
                query = "",
                searchHistory = emptyList(),
                callbacks = SearchBarCallbacks(
                    onQueryChange = {},
                    onSearch = {},
                    onClearSearch = {}
                )
            )
        }

        composeTestRule.onNodeWithText(
            "Search characters, cultures, aliases..."
        ).assertIsDisplayed()
    }

    @Test
    fun searchBar_updateQueryCallsCallback() {
        val onQueryChange = mockk<(String) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            CharactersSearchBar(
                query = "",
                searchHistory = emptyList(),
                callbacks = SearchBarCallbacks(
                    onQueryChange = onQueryChange,
                    onSearch = {},
                    onClearSearch = {}
                )
            )
        }

        composeTestRule.onNodeWithText("Search characters, cultures, aliases...")
            .performClick()
            .performTextInput("Jon")

        verify { onQueryChange(any()) }
    }

    @Test
    fun searchBar_clearButtonIsVisibleWhenQueryNotEmpty() {
        composeTestRule.setContent {
            CharactersSearchBar(
                query = "Jon",
                searchHistory = emptyList(),
                callbacks = SearchBarCallbacks(
                    onQueryChange = {},
                    onSearch = {},
                    onClearSearch = {}
                )
            )
        }

        composeTestRule.onNodeWithContentDescription("Clear search").assertIsDisplayed()
    }

    @Test
    fun filterChips_displaysAllFilters() {
        composeTestRule.setContent {
            FilterChips(
                currentFilter = CharacterFilter(),
                onFilterChange = {}
            )
        }

        composeTestRule.onNodeWithText("Favorites").assertIsDisplayed()
        composeTestRule.onNodeWithText("Deceased").assertIsDisplayed()
        composeTestRule.onNodeWithText("Alive").assertIsDisplayed()
        composeTestRule.onNodeWithText("TV Appearances").assertIsDisplayed()
        composeTestRule.onNodeWithText("Male").assertIsDisplayed()
        composeTestRule.onNodeWithText("Female").assertIsDisplayed()
    }

    @Test
    fun filterChips_clickCallsCallback() {
        val onFilterChange = mockk<(CharacterFilter) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            FilterChips(
                currentFilter = CharacterFilter(),
                onFilterChange = onFilterChange
            )
        }

        composeTestRule.onNodeWithText("Favorites").performClick()

        verify { onFilterChange(any()) }
    }
}
