package com.darach.gameofthrones.feature.favorites

import app.cash.turbine.test
import com.darach.gameofthrones.core.analytics.AnalyticsService
import com.darach.gameofthrones.core.domain.usecase.GetFavoritesUseCase
import com.darach.gameofthrones.core.domain.usecase.ToggleFavoriteUseCase
import com.darach.gameofthrones.core.model.Character
import com.darach.gameofthrones.feature.favorites.FavoritesIntent
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
    private lateinit var analyticsService: AnalyticsService

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
        analyticsService = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct with empty favorites`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(emptyList())

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase, analyticsService)

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertTrue(state.isEmpty)
            assertEquals(emptyList<Character>(), state.favorites)
            assertEquals(emptySet<String>(), state.selectedIds)
        }
    }

    @Test
    fun `initial state is correct with favorites loaded`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase, analyticsService)

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertFalse(state.isEmpty)
            assertEquals(testFavorites, state.favorites)
            assertEquals(2, state.favorites.size)
        }
    }

    @Test
    fun `toggleSelection adds and removes items from selection`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase, analyticsService)

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
    fun `removeSelected calls toggleFavoriteUseCase for each selected item`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)
        coEvery { toggleFavoriteUseCase(any(), any()) } returns Unit

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase, analyticsService)

        // Select items
        viewModel.handleIntent(FavoritesIntent.ToggleSelection("1"))
        viewModel.handleIntent(FavoritesIntent.ToggleSelection("2"))

        // Remove selected
        viewModel.handleIntent(FavoritesIntent.RemoveSelected)

        coVerify { toggleFavoriteUseCase("1", false) }
        coVerify { toggleFavoriteUseCase("2", false) }
    }

    @Test
    fun `removeSelected clears selection`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)
        coEvery { toggleFavoriteUseCase(any(), any()) } returns Unit

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase, analyticsService)

        // Select items
        viewModel.handleIntent(FavoritesIntent.ToggleSelection("1"))

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(1, state.selectedIds.size)
        }

        // Remove selected
        viewModel.handleIntent(FavoritesIntent.RemoveSelected)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(emptySet<String>(), state.selectedIds)
        }
    }

    @Test
    fun `error from use case is handled gracefully`() = runTest {
        val errorMessage = "Failed to load favorites"
        every { getFavoritesUseCase() } returns kotlinx.coroutines.flow.flow {
            throw IllegalStateException(errorMessage)
        }

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase, analyticsService)

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

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase, analyticsService)

        viewModel.handleIntent(FavoritesIntent.LoadFavorites)

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `analytics tracks screen view on initialization`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase, analyticsService)

        io.mockk.verify {
            analyticsService.logScreenView(
                screenName = "Favorites",
                screenClass = "FavoritesScreen"
            )
        }
    }

    @Test
    fun `selecting same character twice toggles selection off`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase, analyticsService)

        // Select item
        viewModel.handleIntent(FavoritesIntent.ToggleSelection("1"))

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.selectedIds.contains("1"))
        }

        // Toggle same item again
        viewModel.handleIntent(FavoritesIntent.ToggleSelection("1"))

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.selectedIds.contains("1"))
            assertTrue(state.selectedIds.isEmpty())
        }
    }

    @Test
    fun `multiple characters can be selected simultaneously`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase, analyticsService)

        viewModel.handleIntent(FavoritesIntent.ToggleSelection("1"))
        viewModel.handleIntent(FavoritesIntent.ToggleSelection("2"))

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(2, state.selectedIds.size)
            assertTrue(state.selectedIds.contains("1"))
            assertTrue(state.selectedIds.contains("2"))
        }
    }

    @Test
    fun `compareSelected shows snackbar when no characters selected`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase, analyticsService)

        viewModel.handleIntent(FavoritesIntent.CompareSelected)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals("Select 2 characters to compare", state.snackbarMessage)
        }
    }

    @Test
    fun `compareSelected shows snackbar when only one character selected`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase, analyticsService)

        viewModel.handleIntent(FavoritesIntent.ToggleSelection("1"))
        viewModel.handleIntent(FavoritesIntent.CompareSelected)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals("Select 2 characters to compare", state.snackbarMessage)
        }
    }

    @Test
    fun `compareSelected shows no snackbar when exactly two characters selected`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase, analyticsService)

        viewModel.handleIntent(FavoritesIntent.ToggleSelection("1"))
        viewModel.handleIntent(FavoritesIntent.ToggleSelection("2"))
        viewModel.handleIntent(FavoritesIntent.CompareSelected)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(null, state.snackbarMessage)
        }
    }

    @Test
    fun `compareSelected shows snackbar when more than two characters selected`() = runTest {
        val threeTestFavorites = testFavorites + Character(
            id = "3",
            name = "Tyrion Lannister",
            gender = "Male",
            culture = "",
            born = "",
            died = "",
            titles = emptyList(),
            aliases = listOf("The Imp"),
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
        every { getFavoritesUseCase() } returns flowOf(threeTestFavorites)

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase, analyticsService)

        viewModel.handleIntent(FavoritesIntent.ToggleSelection("1"))
        viewModel.handleIntent(FavoritesIntent.ToggleSelection("2"))
        viewModel.handleIntent(FavoritesIntent.ToggleSelection("3"))
        viewModel.handleIntent(FavoritesIntent.CompareSelected)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals("You can only select 2 characters to compare", state.snackbarMessage)
        }
    }

    @Test
    fun `clearSnackbar clears the snackbar message`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase, analyticsService)

        viewModel.handleIntent(FavoritesIntent.CompareSelected)
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("Select 2 characters to compare", state.snackbarMessage)
        }

        viewModel.handleIntent(FavoritesIntent.ClearSnackbar)
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(null, state.snackbarMessage)
        }
    }

    @Test
    fun `clearSelection clears all selected characters`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase, analyticsService)

        viewModel.handleIntent(FavoritesIntent.ToggleSelection("1"))
        viewModel.handleIntent(FavoritesIntent.ToggleSelection("2"))

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(2, state.selectedIds.size)
        }

        viewModel.handleIntent(FavoritesIntent.ClearSelection)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(emptySet<String>(), state.selectedIds)
        }
    }

    @Test
    fun `enterSelectionMode sets selection mode true and selects initial character`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase, analyticsService)

        viewModel.handleIntent(FavoritesIntent.EnterSelectionMode("1"))

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isSelectionMode)
            assertTrue(state.selectedIds.contains("1"))
            assertEquals(1, state.selectedIds.size)
        }
    }

    @Test
    fun `exitSelectionMode sets selection mode false and clears selection`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase, analyticsService)

        // Enter selection mode
        viewModel.handleIntent(FavoritesIntent.EnterSelectionMode("1"))

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isSelectionMode)
        }

        // Exit selection mode
        viewModel.handleIntent(FavoritesIntent.ExitSelectionMode)

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isSelectionMode)
            assertEquals(emptySet<String>(), state.selectedIds)
        }
    }

    @Test
    fun `onCardClick enters selection mode when not in selection mode`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase, analyticsService)

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isSelectionMode)
        }

        viewModel.handleIntent(FavoritesIntent.OnCardClick("1"))

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isSelectionMode)
            assertTrue(state.selectedIds.contains("1"))
        }
    }

    @Test
    fun `onCardClick toggles selection when already in selection mode`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase, analyticsService)

        // Enter selection mode
        viewModel.handleIntent(FavoritesIntent.EnterSelectionMode("1"))

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isSelectionMode)
            assertTrue(state.selectedIds.contains("1"))
        }

        // Click another card while in selection mode
        viewModel.handleIntent(FavoritesIntent.OnCardClick("2"))

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isSelectionMode)
            assertTrue(state.selectedIds.contains("1"))
            assertTrue(state.selectedIds.contains("2"))
            assertEquals(2, state.selectedIds.size)
        }
    }

    @Test
    fun `selectAll selects all favorite characters`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase, analyticsService)

        viewModel.state.test {
            awaitItem() // Skip initial state

            viewModel.handleIntent(FavoritesIntent.SelectAll)

            val state = awaitItem()
            assertEquals(2, state.selectedIds.size)
            assertTrue(state.selectedIds.contains("1"))
            assertTrue(state.selectedIds.contains("2"))
        }
    }

    @Test
    fun `deselectAll clears all selected characters`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(testFavorites)

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase, analyticsService)

        viewModel.state.test {
            awaitItem() // Skip initial state

            // Select all first
            viewModel.handleIntent(FavoritesIntent.SelectAll)
            val selectedState = awaitItem()
            assertEquals(2, selectedState.selectedIds.size)

            // Deselect all
            viewModel.handleIntent(FavoritesIntent.DeselectAll)
            val deselectedState = awaitItem()
            assertEquals(emptySet<String>(), deselectedState.selectedIds)
        }
    }

    @Test
    fun `selectAll works with empty favorites list`() = runTest {
        every { getFavoritesUseCase() } returns flowOf(emptyList())

        viewModel = FavoritesViewModel(getFavoritesUseCase, toggleFavoriteUseCase, analyticsService)

        viewModel.handleIntent(FavoritesIntent.SelectAll)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(emptySet<String>(), state.selectedIds)
        }
    }
}
