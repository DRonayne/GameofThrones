package com.darach.gameofthrones.feature.favorites

import app.cash.turbine.test
import com.darach.gameofthrones.core.domain.usecase.GetFavoritesUseCase
import com.darach.gameofthrones.core.domain.usecase.ToggleFavoriteUseCase
import com.darach.gameofthrones.core.model.Character
import com.darach.gameofthrones.feature.favorites.FavoritesIntent
import com.darach.gameofthrones.feature.favorites.ViewMode
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private lateinit var viewModel: FavoritesViewModel
    private lateinit var getFavoritesUseCase: GetFavoritesUseCase
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testFavorites = listOf(
        Character(
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
            playedBy = emptyList(),
            isFavorite = true
        ),
        Character(
            id = "2",
            name = "Arya Stark",
            gender = "Female",
            culture = "Northmen",
            born = "",
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

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getFavoritesUseCase = mockk()
        toggleFavoriteUseCase = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct with empty favorites`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(emptyList())

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase)

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertTrue(state.isEmpty)
            assertEquals(emptyList<Character>(), state.favorites)
            assertEquals(ViewMode.GRID, state.viewMode)
            assertFalse(state.isSelectionMode)
            assertEquals(emptySet<String>(), state.selectedIds)
        }
    }

    @Test
    fun `initial state is correct with favorites loaded`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase)

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertFalse(state.isEmpty)
            assertEquals(testFavorites, state.favorites)
            assertEquals(2, state.favorites.size)
        }
    }

    @Test
    fun `toggleViewMode switches between GRID and LIST`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase)

        // Initial view mode is GRID
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(ViewMode.GRID, state.viewMode)
        }

        // Toggle to LIST
        viewModel.handleIntent(FavoritesIntent.ToggleViewMode)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(ViewMode.LIST, state.viewMode)
        }

        // Toggle back to GRID
        viewModel.handleIntent(FavoritesIntent.ToggleViewMode)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(ViewMode.GRID, state.viewMode)
        }
    }

    @Test
    fun `toggleSelectionMode enables and disables selection mode`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase)

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isSelectionMode)
        }

        // Enable selection mode
        viewModel.handleIntent(FavoritesIntent.ToggleSelectionMode)

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isSelectionMode)
        }

        // Disable selection mode
        viewModel.handleIntent(FavoritesIntent.ToggleSelectionMode)

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isSelectionMode)
        }
    }

    @Test
    fun `toggling selection mode off clears selections`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase)

        // Enable selection mode and select items
        viewModel.handleIntent(FavoritesIntent.ToggleSelectionMode)
        viewModel.handleIntent(FavoritesIntent.ToggleSelection("1"))

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.selectedIds.contains("1"))
        }

        // Disable selection mode
        viewModel.handleIntent(FavoritesIntent.ToggleSelectionMode)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(emptySet<String>(), state.selectedIds)
        }
    }

    @Test
    fun `toggleSelection adds and removes items from selection`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase)

        // Select first item
        viewModel.handleIntent(FavoritesIntent.ToggleSelection("1"))

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.selectedIds.contains("1"))
            assertEquals(1, state.selectedIds.size)
        }

        // Select second item
        viewModel.handleIntent(FavoritesIntent.ToggleSelection("2"))

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.selectedIds.contains("1"))
            assertTrue(state.selectedIds.contains("2"))
            assertEquals(2, state.selectedIds.size)
        }

        // Deselect first item
        viewModel.handleIntent(FavoritesIntent.ToggleSelection("1"))

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.selectedIds.contains("1"))
            assertTrue(state.selectedIds.contains("2"))
            assertEquals(1, state.selectedIds.size)
        }
    }

    @Test
    fun `selectAll selects all favorites`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase)

        viewModel.state.test {
            var state = awaitItem()
            // Initial state loaded
            assertEquals(2, state.favorites.size)

            viewModel.handleIntent(FavoritesIntent.SelectAll)

            state = awaitItem()
            assertEquals(2, state.selectedIds.size)
            assertTrue(state.selectedIds.contains("1"))
            assertTrue(state.selectedIds.contains("2"))
        }
    }

    @Test
    fun `deselectAll clears all selections`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase)

        viewModel.state.test {
            var state = awaitItem()
            // Initial state loaded

            // Select all
            viewModel.handleIntent(FavoritesIntent.SelectAll)

            state = awaitItem()
            assertEquals(2, state.selectedIds.size)

            // Deselect all
            viewModel.handleIntent(FavoritesIntent.DeselectAll)

            state = awaitItem()
            assertEquals(emptySet<String>(), state.selectedIds)
        }
    }

    @Test
    fun `removeFavorite calls toggleFavoriteUseCase with false`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)
        coEvery { toggleFavoriteUseCase(any(), any()) } returns Unit

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase)

        viewModel.handleIntent(FavoritesIntent.RemoveFavorite("1"))

        coVerify { toggleFavoriteUseCase("1", false) }
    }

    @Test
    fun `removeSelected calls toggleFavoriteUseCase for each selected item`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)
        coEvery { toggleFavoriteUseCase(any(), any()) } returns Unit

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase)

        // Select items
        viewModel.handleIntent(FavoritesIntent.ToggleSelection("1"))
        viewModel.handleIntent(FavoritesIntent.ToggleSelection("2"))
        viewModel.handleIntent(FavoritesIntent.ToggleSelectionMode)

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isSelectionMode)
        }

        // Remove selected
        viewModel.handleIntent(FavoritesIntent.RemoveSelected)

        coVerify { toggleFavoriteUseCase("1", false) }
        coVerify { toggleFavoriteUseCase("2", false) }
    }

    @Test
    fun `removeSelected clears selection and disables selection mode`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)
        coEvery { toggleFavoriteUseCase(any(), any()) } returns Unit

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase)

        // Enable selection mode and select items
        viewModel.handleIntent(FavoritesIntent.ToggleSelectionMode)
        viewModel.handleIntent(FavoritesIntent.ToggleSelection("1"))

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isSelectionMode)
            assertEquals(1, state.selectedIds.size)
        }

        // Remove selected
        viewModel.handleIntent(FavoritesIntent.RemoveSelected)

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isSelectionMode)
            assertEquals(emptySet<String>(), state.selectedIds)
        }
    }

    @Test
    fun `error from use case is handled gracefully`() = runTest {
        val errorMessage = "Failed to load favorites"
        every { getFavoritesUseCase() } returns kotlinx.coroutines.flow.flow {
            throw IllegalStateException(errorMessage)
        }

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase)

        // ViewModel should still initialize but handle error
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(errorMessage, state.error)
            assertTrue(state.isEmpty)
        }
    }

    @Test
    fun `loadFavorites intent sets loading to false`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase)

        viewModel.handleIntent(FavoritesIntent.LoadFavorites)

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
        }
    }
}
