package com.darach.gameofthrones.feature.characters

import app.cash.turbine.test
import com.darach.gameofthrones.core.domain.model.Character
import com.darach.gameofthrones.core.domain.usecase.CharacterFilter
import com.darach.gameofthrones.core.domain.usecase.FilterCharactersUseCase
import com.darach.gameofthrones.core.domain.usecase.GetCharactersUseCase
import com.darach.gameofthrones.core.domain.usecase.RefreshCharactersUseCase
import com.darach.gameofthrones.core.domain.usecase.SearchCharactersUseCase
import com.darach.gameofthrones.core.domain.usecase.SortCharactersUseCase
import com.darach.gameofthrones.core.domain.usecase.SortOption
import com.darach.gameofthrones.core.domain.usecase.ToggleFavoriteUseCase
import com.darach.gameofthrones.feature.characters.ui.CharactersViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
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
class CharactersViewModelTest {

    private lateinit var viewModel: CharactersViewModel
    private lateinit var getCharactersUseCase: GetCharactersUseCase
    private lateinit var searchCharactersUseCase: SearchCharactersUseCase
    private lateinit var filterCharactersUseCase: FilterCharactersUseCase
    private lateinit var sortCharactersUseCase: SortCharactersUseCase
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase
    private lateinit var refreshCharactersUseCase: RefreshCharactersUseCase

    private val testDispatcher = StandardTestDispatcher()

    private val testCharacters = listOf(
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
            playedBy = emptyList()
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
            playedBy = emptyList()
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getCharactersUseCase = mockk()
        searchCharactersUseCase = mockk()
        filterCharactersUseCase = mockk()
        sortCharactersUseCase = mockk()
        toggleFavoriteUseCase = mockk()
        refreshCharactersUseCase = mockk()

        every { filterCharactersUseCase(any(), any()) } answers { firstArg() }
        every { sortCharactersUseCase(any(), any()) } answers { firstArg() }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        every { getCharactersUseCase(any()) } returns flowOf(Result.success(emptyList()))

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals("", state.searchQuery)
        assertEquals(emptyList<String>(), state.searchHistory)
        assertEquals(SortOption.NAME_ASC, state.sortOption)
    }

    @Test
    fun `loadCharacters updates state with success`() = runTest {
        every { getCharactersUseCase(any()) } returns flowOf(Result.success(testCharacters))

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(testCharacters, state.characters)
            assertEquals(testCharacters, state.filteredCharacters)
            assertFalse(state.isLoading)
            assertEquals(null, state.error)
        }
    }

    @Test
    fun `loadCharacters updates state with error`() = runTest {
        val errorMessage = "Network error"
        every { getCharactersUseCase(any()) } returns
            flowOf(Result.failure(Exception(errorMessage)))

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(errorMessage, state.error)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `searchCharacters updates query`() = runTest {
        every { getCharactersUseCase(any()) } returns flowOf(Result.success(testCharacters))
        every { searchCharactersUseCase(any()) } returns flowOf(testCharacters)

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.handleIntent(CharactersIntent.SearchCharacters("Jon"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals("Jon", state.searchQuery)
        }
    }

    @Test
    fun `clearSearch resets query and filtered characters`() = runTest {
        every { getCharactersUseCase(any()) } returns flowOf(Result.success(testCharacters))
        every { searchCharactersUseCase(any()) } returns flowOf(listOf(testCharacters[0]))

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.handleIntent(CharactersIntent.SearchCharacters("Jon"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.handleIntent(CharactersIntent.ClearSearch)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals("", state.searchQuery)
            assertEquals(testCharacters, state.filteredCharacters)
        }
    }

    @Test
    fun `filterCharacters updates filter`() = runTest {
        every { getCharactersUseCase(any()) } returns flowOf(Result.success(testCharacters))

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val newFilter = CharacterFilter(onlyFavorites = true)
        viewModel.handleIntent(CharactersIntent.FilterCharacters(newFilter))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(newFilter, state.filter)
        }
    }

    @Test
    fun `sortCharacters updates sort option`() = runTest {
        every { getCharactersUseCase(any()) } returns flowOf(Result.success(testCharacters))

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.handleIntent(CharactersIntent.SortCharacters(SortOption.NAME_DESC))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(SortOption.NAME_DESC, state.sortOption)
        }
    }

    @Test
    fun `toggleFavorite calls use case`() = runTest {
        every { getCharactersUseCase(any()) } returns flowOf(Result.success(testCharacters))
        coEvery { toggleFavoriteUseCase(any(), any()) } returns Unit

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.handleIntent(CharactersIntent.ToggleFavorite("1"))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { toggleFavoriteUseCase("1", true) }
    }

    @Test
    fun `refreshCharacters updates isRefreshing state`() = runTest {
        every { getCharactersUseCase(any()) } returns flowOf(Result.success(testCharacters))
        coEvery { refreshCharactersUseCase() } returns Result.success(Unit)

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.handleIntent(CharactersIntent.RefreshCharacters)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isRefreshing)
    }

    @Test
    fun `search adds query to history`() = runTest {
        every { getCharactersUseCase(any()) } returns flowOf(Result.success(testCharacters))
        every { searchCharactersUseCase(any()) } returns flowOf(testCharacters)

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.handleIntent(CharactersIntent.SearchCharacters("Jon"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.searchHistory.contains("Jon"))
        }
    }

    private fun createViewModel() = CharactersViewModel(
        getCharactersUseCase = getCharactersUseCase,
        searchCharactersUseCase = searchCharactersUseCase,
        filterCharactersUseCase = filterCharactersUseCase,
        sortCharactersUseCase = sortCharactersUseCase,
        toggleFavoriteUseCase = toggleFavoriteUseCase,
        refreshCharactersUseCase = refreshCharactersUseCase
    )
}
