package com.darach.gameofthrones.feature.favorites

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.darach.gameofthrones.core.domain.model.Character
import com.darach.gameofthrones.feature.favorites.components.FavoriteCard
import com.darach.gameofthrones.feature.favorites.components.FavoriteCardCallbacks
import io.mockk.mockk
import io.mockk.verify
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
                onCharacterClick = {},
                onToggleSelection = {},
                onRemoveFavorite = {}
            )
        }

        composeTestRule.onNodeWithText("No favorites yet").assertIsDisplayed()
        composeTestRule.onNodeWithText(
            "Start adding characters to your favorites"
        ).assertIsDisplayed()
    }

    @Test
    fun displaysLoadingState() {
        composeTestRule.setContent {
            FavoritesContent(
                state = FavoritesState(isLoading = true),
                onCharacterClick = {},
                onToggleSelection = {},
                onRemoveFavorite = {}
            )
        }

        // Loading indicator should be present, content should not
        composeTestRule.onNodeWithText("No favorites yet").assertDoesNotExist()
    }

    @Test
    fun displaysErrorState() {
        val errorMessage = "Failed to load favorites"

        composeTestRule.setContent {
            FavoritesContent(
                state = FavoritesState(error = errorMessage),
                onCharacterClick = {},
                onToggleSelection = {},
                onRemoveFavorite = {}
            )
        }

        composeTestRule.onNodeWithText("Error").assertIsDisplayed()
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun displaysFavoritesList() {
        composeTestRule.setContent {
            FavoritesContent(
                state = FavoritesState(
                    favorites = testCharacters,
                    viewMode = ViewMode.LIST
                ),
                onCharacterClick = {},
                onToggleSelection = {},
                onRemoveFavorite = {}
            )
        }

        composeTestRule.onNodeWithText("Jon Snow").assertIsDisplayed()
        composeTestRule.onNodeWithText("Arya Stark").assertIsDisplayed()
    }

    @Test
    fun displaysFavoritesGrid() {
        composeTestRule.setContent {
            FavoritesContent(
                state = FavoritesState(
                    favorites = testCharacters,
                    viewMode = ViewMode.GRID
                ),
                onCharacterClick = {},
                onToggleSelection = {},
                onRemoveFavorite = {}
            )
        }

        composeTestRule.onNodeWithText("Jon Snow").assertIsDisplayed()
        composeTestRule.onNodeWithText("Arya Stark").assertIsDisplayed()
    }

    @Test
    fun backButtonCallsCallback() {
        val onBackClick = mockk<() -> Unit>(relaxed = true)

        composeTestRule.setContent {
            FavoritesTopBar(
                state = FavoritesState(),
                callbacks = FavoritesTopBarCallbacks(
                    onBackClick = onBackClick,
                    onViewModeToggle = {},
                    onSelectionModeToggle = {},
                    onSelectAll = {},
                    onDeselectAll = {}
                )
            )
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()

        verify { onBackClick() }
    }

    @Test
    fun viewModeToggleButton() {
        val onViewModeToggle = mockk<() -> Unit>(relaxed = true)

        composeTestRule.setContent {
            FavoritesTopBar(
                state = FavoritesState(viewMode = ViewMode.LIST),
                callbacks = FavoritesTopBarCallbacks(
                    onBackClick = {},
                    onViewModeToggle = onViewModeToggle,
                    onSelectionModeToggle = {},
                    onSelectAll = {},
                    onDeselectAll = {}
                )
            )
        }

        composeTestRule.onNodeWithContentDescription("Toggle view mode").performClick()

        verify { onViewModeToggle() }
    }

    @Test
    fun selectionModeShowsSelectedCount() {
        composeTestRule.setContent {
            FavoritesTopBar(
                state = FavoritesState(
                    favorites = testCharacters,
                    isSelectionMode = true,
                    selectedIds = setOf("1")
                ),
                callbacks = FavoritesTopBarCallbacks(
                    onBackClick = {},
                    onViewModeToggle = {},
                    onSelectionModeToggle = {},
                    onSelectAll = {},
                    onDeselectAll = {}
                )
            )
        }

        composeTestRule.onNodeWithText("1 selected").assertIsDisplayed()
    }

    @Test
    fun selectionModeEnterButtonVisibleWhenNotEmpty() {
        composeTestRule.setContent {
            FavoritesTopBar(
                state = FavoritesState(favorites = testCharacters),
                callbacks = FavoritesTopBarCallbacks(
                    onBackClick = {},
                    onViewModeToggle = {},
                    onSelectionModeToggle = {},
                    onSelectAll = {},
                    onDeselectAll = {}
                )
            )
        }

        composeTestRule.onNodeWithContentDescription("Enter selection mode").assertIsDisplayed()
    }

    @Test
    fun selectionModeEnterButtonHiddenWhenEmpty() {
        composeTestRule.setContent {
            FavoritesTopBar(
                state = FavoritesState(favorites = emptyList()),
                callbacks = FavoritesTopBarCallbacks(
                    onBackClick = {},
                    onViewModeToggle = {},
                    onSelectionModeToggle = {},
                    onSelectAll = {},
                    onDeselectAll = {}
                )
            )
        }

        composeTestRule.onNodeWithContentDescription("Enter selection mode").assertDoesNotExist()
    }

    @Test
    fun selectionModeExitButton() {
        val onSelectionModeToggle = mockk<() -> Unit>(relaxed = true)

        composeTestRule.setContent {
            FavoritesTopBar(
                state = FavoritesState(isSelectionMode = true),
                callbacks = FavoritesTopBarCallbacks(
                    onBackClick = {},
                    onViewModeToggle = {},
                    onSelectionModeToggle = onSelectionModeToggle,
                    onSelectAll = {},
                    onDeselectAll = {}
                )
            )
        }

        composeTestRule.onNodeWithContentDescription("Exit selection mode").performClick()

        verify { onSelectionModeToggle() }
    }

    @Test
    fun selectAllButton() {
        val onSelectAll = mockk<() -> Unit>(relaxed = true)

        composeTestRule.setContent {
            FavoritesTopBar(
                state = FavoritesState(
                    favorites = testCharacters,
                    isSelectionMode = true,
                    selectedIds = emptySet()
                ),
                callbacks = FavoritesTopBarCallbacks(
                    onBackClick = {},
                    onViewModeToggle = {},
                    onSelectionModeToggle = {},
                    onSelectAll = onSelectAll,
                    onDeselectAll = {}
                )
            )
        }

        composeTestRule.onNodeWithContentDescription("Select all").performClick()

        verify { onSelectAll() }
    }

    @Test
    fun deselectAllButton() {
        val onDeselectAll = mockk<() -> Unit>(relaxed = true)

        composeTestRule.setContent {
            FavoritesTopBar(
                state = FavoritesState(
                    favorites = testCharacters,
                    isSelectionMode = true,
                    selectedIds = setOf("1", "2")
                ),
                callbacks = FavoritesTopBarCallbacks(
                    onBackClick = {},
                    onViewModeToggle = {},
                    onSelectionModeToggle = {},
                    onSelectAll = {},
                    onDeselectAll = onDeselectAll
                )
            )
        }

        composeTestRule.onNodeWithContentDescription("Deselect all").performClick()

        verify { onDeselectAll() }
    }

    @Test
    fun favoritesCountDisplayed() {
        composeTestRule.setContent {
            FavoritesTopBar(
                state = FavoritesState(favorites = testCharacters),
                callbacks = FavoritesTopBarCallbacks(
                    onBackClick = {},
                    onViewModeToggle = {},
                    onSelectionModeToggle = {},
                    onSelectAll = {},
                    onDeselectAll = {}
                )
            )
        }

        composeTestRule.onNodeWithText("Favorites (2)").assertIsDisplayed()
    }

    // FavoriteCard Tests
    @Test
    fun favoriteCard_displaysCharacterName() {
        composeTestRule.setContent {
            FavoriteCard(
                character = testCharacters[0],
                isSelectionMode = false,
                isSelected = false,
                callbacks = FavoriteCardCallbacks(
                    onCharacterClick = {},
                    onToggleSelection = {},
                    onRemoveFavorite = {}
                )
            )
        }

        composeTestRule.onNodeWithText("Jon Snow").assertIsDisplayed()
    }

    @Test
    fun favoriteCard_displaysSeasonBadges() {
        composeTestRule.setContent {
            FavoriteCard(
                character = testCharacters[0],
                isSelectionMode = false,
                isSelected = false,
                callbacks = FavoriteCardCallbacks(
                    onCharacterClick = {},
                    onToggleSelection = {},
                    onRemoveFavorite = {}
                )
            )
        }

        composeTestRule.onNodeWithText("I").assertIsDisplayed()
        composeTestRule.onNodeWithText("II").assertIsDisplayed()
        composeTestRule.onNodeWithText("III").assertIsDisplayed()
    }

    @Test
    fun favoriteCard_displaysCulture() {
        composeTestRule.setContent {
            FavoriteCard(
                character = testCharacters[0],
                isSelectionMode = false,
                isSelected = false,
                callbacks = FavoriteCardCallbacks(
                    onCharacterClick = {},
                    onToggleSelection = {},
                    onRemoveFavorite = {}
                )
            )
        }

        composeTestRule.onNodeWithText("Northmen").assertIsDisplayed()
    }

    @Test
    fun favoriteCard_showsRemoveButtonInNormalMode() {
        composeTestRule.setContent {
            FavoriteCard(
                character = testCharacters[0],
                isSelectionMode = false,
                isSelected = false,
                callbacks = FavoriteCardCallbacks(
                    onCharacterClick = {},
                    onToggleSelection = {},
                    onRemoveFavorite = {}
                )
            )
        }

        composeTestRule.onNodeWithContentDescription("Remove from favorites").assertIsDisplayed()
    }

    @Test
    fun favoriteCard_hidesRemoveButtonInSelectionMode() {
        composeTestRule.setContent {
            FavoriteCard(
                character = testCharacters[0],
                isSelectionMode = true,
                isSelected = false,
                callbacks = FavoriteCardCallbacks(
                    onCharacterClick = {},
                    onToggleSelection = {},
                    onRemoveFavorite = {}
                )
            )
        }

        composeTestRule.onNodeWithContentDescription("Remove from favorites").assertDoesNotExist()
    }

    @Test
    fun favoriteCard_removeButtonCallsCallback() {
        val onRemoveFavorite = mockk<() -> Unit>(relaxed = true)

        composeTestRule.setContent {
            FavoriteCard(
                character = testCharacters[0],
                isSelectionMode = false,
                isSelected = false,
                callbacks = FavoriteCardCallbacks(
                    onCharacterClick = {},
                    onToggleSelection = {},
                    onRemoveFavorite = onRemoveFavorite
                )
            )
        }

        composeTestRule.onNodeWithContentDescription("Remove from favorites").performClick()

        verify { onRemoveFavorite() }
    }

    @Test
    fun favoriteCard_displaysOverflowIndicatorForManySeasons() {
        val manySeasons = testCharacters[0].copy(tvSeriesSeasons = listOf(1, 2, 3, 4, 5, 6, 7, 8))

        composeTestRule.setContent {
            FavoriteCard(
                character = manySeasons,
                isSelectionMode = false,
                isSelected = false,
                callbacks = FavoriteCardCallbacks(
                    onCharacterClick = {},
                    onToggleSelection = {},
                    onRemoveFavorite = {}
                )
            )
        }

        composeTestRule.onNodeWithText("+4").assertIsDisplayed()
    }
}
