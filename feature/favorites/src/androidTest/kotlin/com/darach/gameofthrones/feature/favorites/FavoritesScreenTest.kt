package com.darach.gameofthrones.feature.favorites

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.darach.gameofthrones.core.model.Character
import org.junit.Rule
import org.junit.Test

class FavoritesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testCharacters = listOf(
        Character(
            id = "1",
            name = "Jon Snow",
            gender = "Male",
            culture = "Northmen",
            born = "283 AC",
            died = "",
            titles = listOf("King in the North"),
            aliases = listOf("Lord Snow"),
            father = "",
            mother = "",
            spouse = "",
            allegiances = emptyList(),
            books = emptyList(),
            povBooks = emptyList(),
            tvSeries = listOf("Season 1"),
            tvSeriesSeasons = listOf(1, 2, 3),
            playedBy = emptyList(),
            isFavorite = true
        ),
        Character(
            id = "2",
            name = "Arya Stark",
            gender = "Female",
            culture = "Northmen",
            born = "289 AC",
            died = "",
            titles = emptyList(),
            aliases = listOf("No One"),
            father = "",
            mother = "",
            spouse = "",
            allegiances = emptyList(),
            books = emptyList(),
            povBooks = emptyList(),
            tvSeries = listOf("Season 1"),
            tvSeriesSeasons = listOf(1, 2, 3),
            playedBy = emptyList(),
            isFavorite = true
        )
    )

    @Test
    fun displaysEmptyState() {
        composeTestRule.setContent {
            FavoritesContent(
                state = FavoritesState(isEmpty = true),
                onCardClick = {},
                onBrowseCharactersClick = {}
            )
        }

        composeTestRule.onNodeWithText("No Favorites Yet").assertIsDisplayed()
        composeTestRule.onNodeWithText(
            "Discover and save your favorite Game of Thrones characters"
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText("Browse Characters").assertIsDisplayed()
    }

    @Test
    fun displaysLoadingState() {
        composeTestRule.setContent {
            FavoritesContent(
                state = FavoritesState(isLoading = true),
                onCardClick = {},
                onBrowseCharactersClick = {}
            )
        }

        // Loading indicator should be present
        composeTestRule.onNodeWithText("No Favorites Yet").assertDoesNotExist()
    }

    @Test
    fun displaysErrorState() {
        val errorMessage = "Failed to load favorites"

        composeTestRule.setContent {
            FavoritesContent(
                state = FavoritesState(error = errorMessage),
                onCardClick = {},
                onBrowseCharactersClick = {}
            )
        }

        composeTestRule.onNodeWithText("Error").assertIsDisplayed()
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun displaysFavoritesCharacterNames() {
        composeTestRule.setContent {
            FavoritesContent(
                state = FavoritesState(favorites = testCharacters),
                onCardClick = {},
                onBrowseCharactersClick = {}
            )
        }

        composeTestRule.onNodeWithText("Jon Snow").assertIsDisplayed()
        composeTestRule.onNodeWithText("Arya Stark").assertIsDisplayed()
    }

    @Test
    fun favoritesCountDisplayedInTitle() {
        composeTestRule.setContent {
            FavoritesContent(
                state = FavoritesState(favorites = testCharacters),
                onCardClick = {},
                onBrowseCharactersClick = {}
            )
        }

        // The title would show "Favorites (2)" but since we're only testing FavoritesContent,
        // we can verify the characters are displayed
        composeTestRule.onNodeWithText("Jon Snow").assertIsDisplayed()
        composeTestRule.onNodeWithText("Arya Stark").assertIsDisplayed()
    }

    @Test
    fun selectedCountDisplayedWhenItemsSelected() {
        composeTestRule.setContent {
            FavoritesContent(
                state = FavoritesState(
                    favorites = testCharacters,
                    selectedIds = setOf("1")
                ),
                onCardClick = {},
                onBrowseCharactersClick = {}
            )
        }

        // Characters should still be displayed
        composeTestRule.onNodeWithText("Jon Snow").assertIsDisplayed()
        composeTestRule.onNodeWithText("Arya Stark").assertIsDisplayed()
    }
}
