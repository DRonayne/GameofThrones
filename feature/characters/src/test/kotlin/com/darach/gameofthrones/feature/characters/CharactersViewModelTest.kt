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
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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

        // Mock Android Log to prevent "Method not mocked" errors
        mockkStatic(android.util.Log::class)
        every { android.util.Log.e(any(), any(), any<Throwable>()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0

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

    @Test
    fun `rapid search queries cancel previous operations`() = runTest {
        every { getCharactersUseCase(any()) } returns flowOf(Result.success(testCharacters))

        var searchCallCount = 0
        every { searchCharactersUseCase(any()) } answers {
            searchCallCount++
            flow {
                delay(100) // Simulate slow search
                emit(testCharacters)
            }
        }

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Rapidly type multiple characters
        viewModel.handleIntent(CharactersIntent.SearchCharacters("J"))
        viewModel.handleIntent(CharactersIntent.SearchCharacters("Jo"))
        viewModel.handleIntent(CharactersIntent.SearchCharacters("Jon"))

        // Advance past debounce time for the last query only
        testDispatcher.scheduler.advanceTimeBy(400)
        testDispatcher.scheduler.advanceUntilIdle()

        // Only the last query should be executed due to debouncing
        verify(exactly = 1) { searchCharactersUseCase("Jon") }
        assertEquals("Jon", viewModel.state.value.searchQuery)
    }

    @Test
    fun `search results from cancelled queries are ignored`() = runTest {
        every { getCharactersUseCase(any()) } returns flowOf(Result.success(testCharacters))

        val jonResult = listOf(testCharacters[0])
        val aryaResult = listOf(testCharacters[1])

        every { searchCharactersUseCase("Jon") } returns flow {
            delay(200) // Slower search
            emit(jonResult)
        }

        every { searchCharactersUseCase("Arya") } returns flow {
            delay(50) // Faster search
            emit(aryaResult)
        }

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Search for "Jon" first
        viewModel.handleIntent(CharactersIntent.SearchCharacters("Jon"))
        testDispatcher.scheduler.advanceTimeBy(350)

        // Quickly change to "Arya" before "Jon" completes
        viewModel.handleIntent(CharactersIntent.SearchCharacters("Arya"))
        testDispatcher.scheduler.advanceTimeBy(350)

        testDispatcher.scheduler.advanceUntilIdle()

        // Should show results for "Arya" only, not "Jon"
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("Arya", state.searchQuery)
            assertEquals(aryaResult, state.filteredCharacters)
        }
    }

    @Test
    fun `search debounce waits for typing to stop`() = runTest {
        every { getCharactersUseCase(any()) } returns flowOf(Result.success(testCharacters))
        every { searchCharactersUseCase(any()) } returns flowOf(testCharacters)

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.handleIntent(CharactersIntent.SearchCharacters("Jon"))

        // Advance by less than debounce time
        testDispatcher.scheduler.advanceTimeBy(200)

        // Search should not have been executed yet
        verify(exactly = 0) { searchCharactersUseCase(any()) }

        // Advance past debounce time
        testDispatcher.scheduler.advanceTimeBy(200)
        testDispatcher.scheduler.advanceUntilIdle()

        // Now search should be executed
        verify(exactly = 1) { searchCharactersUseCase("Jon") }
    }

    @Test
    fun `search updates filteredCharacters with search results`() = runTest {
        every { getCharactersUseCase(any()) } returns flowOf(Result.success(testCharacters))
        val searchResults = listOf(testCharacters[0])
        every { searchCharactersUseCase("Jon") } returns flowOf(searchResults)

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.handleIntent(CharactersIntent.SearchCharacters("Jon"))
        testDispatcher.scheduler.advanceTimeBy(400)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(searchResults, state.filteredCharacters)
            assertEquals("Jon", state.searchQuery)
        }
    }

    @Test
    fun `filter is applied to search results`() = runTest {
        every { getCharactersUseCase(any()) } returns flowOf(Result.success(testCharacters))
        every { searchCharactersUseCase("Stark") } returns flowOf(testCharacters)
        every { filterCharactersUseCase(any(), any()) } answers {
            val chars = firstArg<List<Character>>()
            val filter = secondArg<CharacterFilter>()
            if (filter.gender == "Male") {
                chars.filter { it.gender == "Male" }
            } else {
                chars
            }
        }
        every { sortCharactersUseCase(any(), any()) } answers { firstArg() }

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // First search for "Stark"
        viewModel.handleIntent(CharactersIntent.SearchCharacters("Stark"))
        testDispatcher.scheduler.advanceTimeBy(400)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then apply gender filter
        viewModel.handleIntent(
            CharactersIntent.FilterCharacters(CharacterFilter(gender = "Male"))
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(1, state.filteredCharacters.size)
            assertEquals("Male", state.filteredCharacters[0].gender)
        }
    }

    @Test
    fun `sort is applied to search results`() = runTest {
        every { getCharactersUseCase(any()) } returns flowOf(Result.success(testCharacters))
        every { searchCharactersUseCase("Stark") } returns flowOf(testCharacters)
        every { filterCharactersUseCase(any(), any()) } answers { firstArg() }
        every { sortCharactersUseCase(any(), SortOption.NAME_DESC) } answers {
            val chars = firstArg<List<Character>>()
            chars.sortedByDescending { it.name }
        }

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Search first
        viewModel.handleIntent(CharactersIntent.SearchCharacters("Stark"))
        testDispatcher.scheduler.advanceTimeBy(400)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then sort
        viewModel.handleIntent(CharactersIntent.SortCharacters(SortOption.NAME_DESC))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(SortOption.NAME_DESC, state.sortOption)
            // Verify results are sorted by name descending
            val names = state.filteredCharacters.map { it.name }
            assertEquals(names.sortedDescending(), names)
        }
    }

    @Test
    fun `filter and sort are applied together to search results`() = runTest {
        every { getCharactersUseCase(any()) } returns flowOf(Result.success(testCharacters))
        every { searchCharactersUseCase(any()) } returns flowOf(testCharacters)
        every { filterCharactersUseCase(any(), any()) } answers {
            val chars = firstArg<List<Character>>()
            val filter = secondArg<CharacterFilter>()
            if (filter.culture == "Northmen") {
                chars.filter { it.culture == "Northmen" }
            } else {
                chars
            }
        }
        every { sortCharactersUseCase(any(), SortOption.NAME_DESC) } answers {
            val chars = firstArg<List<Character>>()
            chars.sortedByDescending { it.name }
        }

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Apply filter and sort before search
        viewModel.handleIntent(
            CharactersIntent.FilterCharacters(CharacterFilter(culture = "Northmen"))
        )
        viewModel.handleIntent(CharactersIntent.SortCharacters(SortOption.NAME_DESC))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then search
        viewModel.handleIntent(CharactersIntent.SearchCharacters("Stark"))
        testDispatcher.scheduler.advanceTimeBy(400)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            // Both filter and sort should be applied
            assertTrue(state.filteredCharacters.all { it.culture == "Northmen" })
            val names = state.filteredCharacters.map { it.name }
            assertEquals(names.sortedDescending(), names)
        }
    }

    @Test
    fun `clearing search restores base characters with current filter and sort`() = runTest {
        every { getCharactersUseCase(any()) } returns flowOf(Result.success(testCharacters))
        every { searchCharactersUseCase("Jon") } returns flowOf(listOf(testCharacters[0]))
        every { filterCharactersUseCase(any(), any()) } answers { firstArg() }
        every { sortCharactersUseCase(any(), any()) } answers { firstArg() }

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Search first
        viewModel.handleIntent(CharactersIntent.SearchCharacters("Jon"))
        testDispatcher.scheduler.advanceTimeBy(400)
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify search results
        assertEquals(1, viewModel.state.value.filteredCharacters.size)

        // Clear search
        viewModel.handleIntent(CharactersIntent.ClearSearch)
        testDispatcher.scheduler.advanceUntilIdle()

        // Should restore base characters
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("", state.searchQuery)
            assertEquals(testCharacters, state.filteredCharacters)
        }
    }

    @Test
    fun `search use case is called with correct query after debounce`() = runTest {
        every { getCharactersUseCase(any()) } returns flowOf(Result.success(testCharacters))
        every { searchCharactersUseCase(any()) } returns flowOf(testCharacters)

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val searchQuery = "Winter is coming"
        viewModel.handleIntent(CharactersIntent.SearchCharacters(searchQuery))
        testDispatcher.scheduler.advanceTimeBy(400)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(exactly = 1) { searchCharactersUseCase(searchQuery) }
    }

    @Test
    fun `filter changes trigger automatic recalculation without redundant calls`() = runTest {
        every { getCharactersUseCase(any()) } returns flowOf(Result.success(testCharacters))
        var filterCallCount = 0
        every { filterCharactersUseCase(any(), any()) } answers {
            filterCallCount++
            firstArg()
        }
        every { sortCharactersUseCase(any(), any()) } answers { firstArg() }

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val initialFilterCalls = filterCallCount

        // Change filter
        viewModel.handleIntent(
            CharactersIntent.FilterCharacters(CharacterFilter(onlyFavorites = true))
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Filter should be called again for the new filter
        assertTrue(filterCallCount > initialFilterCalls)
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
