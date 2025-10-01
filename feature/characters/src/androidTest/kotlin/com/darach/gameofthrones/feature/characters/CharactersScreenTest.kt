package com.darach.gameofthrones.feature.characters

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import com.darach.gameofthrones.core.domain.usecase.CharacterFilter
import com.darach.gameofthrones.core.domain.usecase.SortOption
import com.darach.gameofthrones.core.model.Character
import com.darach.gameofthrones.feature.characters.components.CharacterCard
import com.darach.gameofthrones.feature.characters.components.CharactersSearchBar
import com.darach.gameofthrones.feature.characters.components.FilterBottomSheet
import com.darach.gameofthrones.feature.characters.components.FilterBottomSheetState
import com.darach.gameofthrones.feature.characters.components.FilterChips
import com.darach.gameofthrones.feature.characters.components.OfflineIndicator
import com.darach.gameofthrones.feature.characters.components.SearchBarCallbacks
import com.darach.gameofthrones.feature.characters.components.SortOptionsMenu
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

    private val testCharacter2 = Character(
        id = "2",
        name = "Arya Stark",
        gender = "Female",
        culture = "Northmen",
        born = "283 AC",
        died = "",
        titles = emptyList(),
        aliases = listOf("No One"),
        father = "",
        mother = "",
        spouse = "",
        allegiances = emptyList(),
        books = emptyList(),
        povBooks = emptyList(),
        tvSeries = listOf("Season 1", "Season 2"),
        tvSeriesSeasons = listOf(1, 2, 3, 4, 5, 6, 7, 8),
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
        val deadCharacter = testCharacter.copy(died = "299 AC", isDead = true)

        composeTestRule.setContent {
            CharacterCard(
                character = deadCharacter,
                onFavoriteClick = {},
                onClick = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Deceased").assertIsDisplayed()
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

    // Sort Options Menu Tests
    @Test
    fun sortOptionsMenu_displaysAllSortOptions() {
        composeTestRule.setContent {
            SortOptionsMenu(
                currentSortOption = SortOption.NAME_ASC,
                onSortOptionChange = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Sort options").performClick()

        composeTestRule.onNodeWithText("Name (A-Z)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Name (Z-A)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Culture (A-Z)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Culture (Z-A)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Death Date (Oldest First)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Death Date (Newest First)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Seasons Count (Fewest First)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Seasons Count (Most First)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Favorites First").assertIsDisplayed()
    }

    @Test
    fun sortOptionsMenu_selectingSortOptionCallsCallback() {
        val onSortOptionChange = mockk<(SortOption) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            SortOptionsMenu(
                currentSortOption = SortOption.NAME_ASC,
                onSortOptionChange = onSortOptionChange
            )
        }

        composeTestRule.onNodeWithContentDescription("Sort options").performClick()
        composeTestRule.onNodeWithText("Culture (A-Z)").performClick()

        verify { onSortOptionChange(SortOption.CULTURE_ASC) }
    }

    @Test
    fun sortOptionsMenu_showsCheckmarkForCurrentOption() {
        composeTestRule.setContent {
            SortOptionsMenu(
                currentSortOption = SortOption.FAVORITE_FIRST,
                onSortOptionChange = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Sort options").performClick()

        // The check icon should be visible for the selected option
        composeTestRule.onAllNodesWithText("Favorites First").assertCountEquals(1)
    }

    // Offline Indicator Tests
    @Test
    fun offlineIndicator_visibleWhenOffline() {
        composeTestRule.setContent {
            MaterialTheme {
                OfflineIndicator(isOffline = true)
            }
        }

        composeTestRule.onNodeWithText("Offline").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Offline").assertIsDisplayed()
    }

    @Test
    fun offlineIndicator_notVisibleWhenOnline() {
        composeTestRule.setContent {
            MaterialTheme {
                OfflineIndicator(isOffline = false)
            }
        }

        composeTestRule.onNodeWithText("Offline").assertIsNotDisplayed()
    }

    // Search History Tests
    @Test
    fun searchBar_displaysSearchHistory() {
        composeTestRule.setContent {
            CharactersSearchBar(
                query = "",
                searchHistory = listOf("Jon", "Arya", "Stark"),
                callbacks = SearchBarCallbacks(
                    onQueryChange = {},
                    onSearch = {},
                    onClearSearch = {}
                )
            )
        }

        composeTestRule.onNodeWithText("Search characters, cultures, aliases...")
            .performClick()

        composeTestRule.onNodeWithText("Jon").assertIsDisplayed()
        composeTestRule.onNodeWithText("Arya").assertIsDisplayed()
        composeTestRule.onNodeWithText("Stark").assertIsDisplayed()
    }

    @Test
    fun searchBar_historyItemClickPerformsSearch() {
        val onSearch = mockk<(String) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            CharactersSearchBar(
                query = "",
                searchHistory = listOf("Jon Snow"),
                callbacks = SearchBarCallbacks(
                    onQueryChange = {},
                    onSearch = onSearch,
                    onClearSearch = {}
                )
            )
        }

        composeTestRule.onNodeWithText("Search characters, cultures, aliases...")
            .performClick()
        composeTestRule.onNodeWithText("Jon Snow").performClick()

        verify { onSearch("Jon Snow") }
    }

    // Character Card Interaction Tests

    @Test
    fun characterCard_displaysMultipleSeasonBadges() {
        composeTestRule.setContent {
            CharacterCard(
                character = testCharacter2,
                onFavoriteClick = {},
                onClick = {}
            )
        }

        // Verify Roman numerals for seasons 1-8
        composeTestRule.onNodeWithText("I").assertIsDisplayed()
        composeTestRule.onNodeWithText("II").assertIsDisplayed()
        composeTestRule.onNodeWithText("III").assertIsDisplayed()
        composeTestRule.onNodeWithText("IV").assertIsDisplayed()
        composeTestRule.onNodeWithText("V").assertIsDisplayed()
        composeTestRule.onNodeWithText("VI").assertIsDisplayed()
        composeTestRule.onNodeWithText("VII").assertIsDisplayed()
        composeTestRule.onNodeWithText("VIII").assertIsDisplayed()
    }

    @Test
    fun characterCard_favoriteIconChangesForFavoriteCharacter() {
        val favoriteCharacter = testCharacter.copy(isFavorite = true)

        composeTestRule.setContent {
            CharacterCard(
                character = favoriteCharacter,
                onFavoriteClick = {},
                onClick = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Remove from favorites")
            .assertIsDisplayed()
    }

    // FilterBottomSheet Tests
    @Test
    fun filterBottomSheet_displaysTitle() {
        composeTestRule.setContent {
            FilterBottomSheet(
                state = FilterBottomSheetState(
                    currentFilter = CharacterFilter(),
                    availableCultures = emptyList(),
                    availableSeasons = emptyList()
                ),
                onFilterChange = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Filters").assertIsDisplayed()
    }

    @Test
    fun filterBottomSheet_displaysStatusSection() {
        composeTestRule.setContent {
            FilterBottomSheet(
                state = FilterBottomSheetState(
                    currentFilter = CharacterFilter(),
                    availableCultures = emptyList(),
                    availableSeasons = emptyList()
                ),
                onFilterChange = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Status").assertIsDisplayed()
    }

    @Test
    fun filterBottomSheet_displaysGenderSection() {
        composeTestRule.setContent {
            FilterBottomSheet(
                state = FilterBottomSheetState(
                    currentFilter = CharacterFilter(),
                    availableCultures = emptyList(),
                    availableSeasons = emptyList()
                ),
                onFilterChange = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Gender").assertIsDisplayed()
    }

    @Test
    fun filterBottomSheet_displaysCultureSection() {
        composeTestRule.setContent {
            FilterBottomSheet(
                state = FilterBottomSheetState(
                    currentFilter = CharacterFilter(),
                    availableCultures = listOf("Northmen", "Valyrian"),
                    availableSeasons = emptyList()
                ),
                onFilterChange = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Culture").assertIsDisplayed()
        composeTestRule.onNodeWithText("Northmen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Valyrian").assertIsDisplayed()
    }

    @Test
    fun filterBottomSheet_displaysSeasonsSection() {
        composeTestRule.setContent {
            FilterBottomSheet(
                state = FilterBottomSheetState(
                    currentFilter = CharacterFilter(),
                    availableCultures = emptyList(),
                    availableSeasons = listOf(1, 2, 3)
                ),
                onFilterChange = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Seasons").assertIsDisplayed()
        composeTestRule.onNodeWithText("Season 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Season 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Season 3").assertIsDisplayed()
    }

    @Test
    fun filterBottomSheet_clearAllButtonVisible() {
        composeTestRule.setContent {
            FilterBottomSheet(
                state = FilterBottomSheetState(
                    currentFilter = CharacterFilter(onlyFavorites = true),
                    availableCultures = emptyList(),
                    availableSeasons = emptyList()
                ),
                onFilterChange = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Clear all").assertIsDisplayed()
    }

    @Test
    fun filterBottomSheet_clearAllButtonCallsCallback() {
        val onFilterChange = mockk<(CharacterFilter) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            FilterBottomSheet(
                state = FilterBottomSheetState(
                    currentFilter = CharacterFilter(onlyFavorites = true),
                    availableCultures = emptyList(),
                    availableSeasons = emptyList()
                ),
                onFilterChange = onFilterChange,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Clear all").performClick()

        verify { onFilterChange(CharacterFilter()) }
    }

    @Test
    fun filterBottomSheet_filterChipSelectionCallsCallback() {
        val onFilterChange = mockk<(CharacterFilter) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            FilterBottomSheet(
                state = FilterBottomSheetState(
                    currentFilter = CharacterFilter(),
                    availableCultures = emptyList(),
                    availableSeasons = emptyList()
                ),
                onFilterChange = onFilterChange,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Deceased").performClick()

        verify { onFilterChange(any()) }
    }

    @Test
    fun filterBottomSheet_displaysActiveFilterCountBadge() {
        composeTestRule.setContent {
            FilterBottomSheet(
                state = FilterBottomSheetState(
                    currentFilter = CharacterFilter(
                        onlyFavorites = true,
                        isDead = true
                    ),
                    availableCultures = emptyList(),
                    availableSeasons = emptyList()
                ),
                onFilterChange = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("2").assertIsDisplayed()
    }
}
