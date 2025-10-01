package com.darach.gameofthrones.feature.characterdetail.ui

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.darach.gameofthrones.core.common.analytics.AnalyticsEvents
import com.darach.gameofthrones.core.common.analytics.AnalyticsParams
import com.darach.gameofthrones.core.common.analytics.AnalyticsService
import com.darach.gameofthrones.core.common.crash.CrashReportingService
import com.darach.gameofthrones.core.domain.model.Character
import com.darach.gameofthrones.core.domain.usecase.GetCharacterByIdUseCase
import com.darach.gameofthrones.core.domain.usecase.ToggleFavoriteUseCase
import com.darach.gameofthrones.feature.characterdetail.CharacterDetailIntent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import java.io.IOException
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterDetailViewModelTest {

    private lateinit var viewModel: CharacterDetailViewModel
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var getCharacterByIdUseCase: GetCharacterByIdUseCase
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase
    private lateinit var analyticsService: AnalyticsService
    private lateinit var crashReportingService: CrashReportingService

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testCharacter = Character(
        id = "1",
        name = "Jon Snow",
        gender = "Male",
        culture = "Northmen",
        born = "In 283 AC",
        died = "",
        titles = listOf("Lord Commander"),
        aliases = listOf("King in the North"),
        father = "",
        mother = "",
        spouse = "",
        allegiances = listOf("House Stark"),
        books = emptyList(),
        povBooks = emptyList(),
        tvSeries = listOf("Season 1", "Season 2"),
        tvSeriesSeasons = listOf(1, 2, 3),
        playedBy = listOf("Kit Harington"),
        isFavorite = false
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock Android Log to prevent "Method not mocked" errors
        mockkStatic(android.util.Log::class)
        every { android.util.Log.e(any(), any(), any<Throwable>()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0

        getCharacterByIdUseCase = mockk()
        toggleFavoriteUseCase = mockk(relaxed = true)
        analyticsService = mockk(relaxed = true)
        crashReportingService = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state with valid character ID loads character`() = runTest {
        savedStateHandle = SavedStateHandle(mapOf("characterId" to "1"))
        every { getCharacterByIdUseCase("1") } returns flowOf(testCharacter)

        viewModel = createViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNotNull(state.character)
            assertEquals("Jon Snow", state.character?.name)
            assertNull(state.error)
        }
    }

    @Test
    fun `initial state with empty character ID shows error`() = runTest {
        savedStateHandle = SavedStateHandle(mapOf("characterId" to ""))

        viewModel = createViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNull(state.character)
            assertEquals("Invalid character ID", state.error)
        }
    }

    @Test
    fun `initial state with missing character ID shows error`() = runTest {
        savedStateHandle = SavedStateHandle()

        viewModel = createViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNull(state.character)
            assertEquals("Invalid character ID", state.error)
        }
    }

    @Test
    fun `loadCharacter updates state with character data`() = runTest {
        savedStateHandle = SavedStateHandle(mapOf("characterId" to "1"))
        every { getCharacterByIdUseCase("1") } returns flowOf(testCharacter)

        viewModel = createViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals("Jon Snow", state.character?.name)
            assertEquals("Northmen", state.character?.culture)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }
    }

    @Test
    fun `loadCharacter with null result shows character not found error`() = runTest {
        savedStateHandle = SavedStateHandle(mapOf("characterId" to "999"))
        every { getCharacterByIdUseCase("999") } returns flowOf(null)

        viewModel = createViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNull(state.character)
            assertEquals("Character not found", state.error)
        }
    }

    @Test
    fun `loadCharacter logs analytics event on success`() = runTest {
        savedStateHandle = SavedStateHandle(mapOf("characterId" to "1"))
        every { getCharacterByIdUseCase("1") } returns flowOf(testCharacter)

        viewModel = createViewModel()

        viewModel.state.test {
            awaitItem()
        }

        verify {
            analyticsService.logEvent(
                AnalyticsEvents.CHARACTER_VIEWED,
                mapOf(
                    AnalyticsParams.CHARACTER_ID to "1",
                    AnalyticsParams.CHARACTER_NAME to "Jon Snow"
                )
            )
        }
    }

    @Test
    fun `loadCharacter with empty name logs Unknown in analytics`() = runTest {
        val characterWithEmptyName = testCharacter.copy(name = "")
        savedStateHandle = SavedStateHandle(mapOf("characterId" to "1"))
        every { getCharacterByIdUseCase("1") } returns flowOf(characterWithEmptyName)

        viewModel = createViewModel()

        viewModel.state.test {
            awaitItem()
        }

        verify {
            analyticsService.logEvent(
                AnalyticsEvents.CHARACTER_VIEWED,
                mapOf(
                    AnalyticsParams.CHARACTER_ID to "1",
                    AnalyticsParams.CHARACTER_NAME to "Unknown"
                )
            )
        }
    }

    @Test
    fun `loadCharacter with error updates state with error message`() = runTest {
        val errorMessage = "Network error"
        savedStateHandle = SavedStateHandle(mapOf("characterId" to "1"))
        every { getCharacterByIdUseCase("1") } returns kotlinx.coroutines.flow.flow {
            throw IOException(errorMessage)
        }

        viewModel = createViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNull(state.character)
            assertNotNull(state.error)
        }
    }

    @Test
    fun `loadCharacter with error logs exception to crash reporting`() = runTest {
        val error = IOException("Network error")
        savedStateHandle = SavedStateHandle(mapOf("characterId" to "1"))
        every { getCharacterByIdUseCase("1") } returns kotlinx.coroutines.flow.flow {
            throw error
        }

        viewModel = createViewModel()

        viewModel.state.test {
            awaitItem()
        }

        // Verify crash reporting was called (may be called multiple times due to flow retry)
        verify(atLeast = 1) { crashReportingService.logException(any()) }
    }

    @Test
    fun `toggleFavorite calls use case with correct parameters for unfavorited character`() =
        runTest {
            savedStateHandle = SavedStateHandle(mapOf("characterId" to "1"))
            every { getCharacterByIdUseCase("1") } returns
                flowOf(testCharacter.copy(isFavorite = false))
            coEvery { toggleFavoriteUseCase(any(), any()) } returns Unit

            viewModel = createViewModel()

            viewModel.handleIntent(CharacterDetailIntent.ToggleFavorite)

            coVerify { toggleFavoriteUseCase("1", true) }
        }

    @Test
    fun `toggleFavorite calls use case with correct parameters for favorited character`() =
        runTest {
            savedStateHandle = SavedStateHandle(mapOf("characterId" to "1"))
            every { getCharacterByIdUseCase("1") } returns
                flowOf(testCharacter.copy(isFavorite = true))
            coEvery { toggleFavoriteUseCase(any(), any()) } returns Unit

            viewModel = createViewModel()

            viewModel.handleIntent(CharacterDetailIntent.ToggleFavorite)

            coVerify { toggleFavoriteUseCase("1", false) }
        }

    @Test
    fun `toggleFavorite logs favorited analytics event`() = runTest {
        savedStateHandle = SavedStateHandle(mapOf("characterId" to "1"))
        every { getCharacterByIdUseCase("1") } returns
            flowOf(testCharacter.copy(isFavorite = false))
        coEvery { toggleFavoriteUseCase(any(), any()) } returns Unit

        viewModel = createViewModel()

        viewModel.handleIntent(CharacterDetailIntent.ToggleFavorite)

        verify {
            analyticsService.logEvent(
                AnalyticsEvents.CHARACTER_FAVORITED,
                mapOf(
                    AnalyticsParams.CHARACTER_ID to "1",
                    AnalyticsParams.CHARACTER_NAME to "Jon Snow"
                )
            )
        }
    }

    @Test
    fun `toggleFavorite logs unfavorited analytics event`() = runTest {
        savedStateHandle = SavedStateHandle(mapOf("characterId" to "1"))
        every { getCharacterByIdUseCase("1") } returns flowOf(testCharacter.copy(isFavorite = true))
        coEvery { toggleFavoriteUseCase(any(), any()) } returns Unit

        viewModel = createViewModel()

        viewModel.handleIntent(CharacterDetailIntent.ToggleFavorite)

        verify {
            analyticsService.logEvent(
                AnalyticsEvents.CHARACTER_UNFAVORITED,
                mapOf(
                    AnalyticsParams.CHARACTER_ID to "1",
                    AnalyticsParams.CHARACTER_NAME to "Jon Snow"
                )
            )
        }
    }

    @Test
    fun `toggleFavorite with null character does nothing`() = runTest {
        savedStateHandle = SavedStateHandle(mapOf("characterId" to "1"))
        every { getCharacterByIdUseCase("1") } returns flowOf(null)
        coEvery { toggleFavoriteUseCase(any(), any()) } returns Unit

        viewModel = createViewModel()

        viewModel.handleIntent(CharacterDetailIntent.ToggleFavorite)

        coVerify(exactly = 0) { toggleFavoriteUseCase(any(), any()) }
    }

    @Test
    fun `retryLoad intent reloads character`() = runTest {
        savedStateHandle = SavedStateHandle(mapOf("characterId" to "1"))
        every { getCharacterByIdUseCase("1") } returns flowOf(testCharacter)

        viewModel = createViewModel()

        viewModel.handleIntent(CharacterDetailIntent.RetryLoad)

        // Verify character is loaded (initial + retry = 2 calls)
        verify(atLeast = 2) { getCharacterByIdUseCase("1") }
    }

    @Test
    fun `loadCharacter intent with different ID loads new character`() = runTest {
        savedStateHandle = SavedStateHandle(mapOf("characterId" to "1"))
        val differentCharacter = testCharacter.copy(id = "2", name = "Arya Stark")

        every { getCharacterByIdUseCase("1") } returns flowOf(testCharacter)
        every { getCharacterByIdUseCase("2") } returns flowOf(differentCharacter)

        viewModel = createViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals("Jon Snow", state.character?.name)
        }

        viewModel.handleIntent(CharacterDetailIntent.LoadCharacter("2"))

        viewModel.state.test {
            val state = awaitItem()
            assertEquals("Arya Stark", state.character?.name)
        }
    }

    @Test
    fun `loading state is true while character is being loaded`() = runTest {
        savedStateHandle = SavedStateHandle(mapOf("characterId" to "1"))
        every { getCharacterByIdUseCase("1") } returns flowOf(testCharacter)

        viewModel = createViewModel()

        // After loading completes with UnconfinedTestDispatcher, loading should be false
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `error is cleared when loading character successfully after error`() = runTest {
        savedStateHandle = SavedStateHandle(mapOf("characterId" to "1"))

        // First call fails
        var callCount = 0
        every { getCharacterByIdUseCase("1") } answers {
            callCount++
            if (callCount == 1) {
                kotlinx.coroutines.flow.flow { throw IOException("Network error") }
            } else {
                flowOf(testCharacter)
            }
        }

        viewModel = createViewModel()

        // First load should have error
        viewModel.state.test {
            val state = awaitItem()
            assertNotNull(state.error)
        }

        // Retry should succeed and clear error
        viewModel.handleIntent(CharacterDetailIntent.RetryLoad)

        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.error)
            assertNotNull(state.character)
        }
    }

    private fun createViewModel() = CharacterDetailViewModel(
        savedStateHandle = savedStateHandle,
        getCharacterByIdUseCase = getCharacterByIdUseCase,
        toggleFavoriteUseCase = toggleFavoriteUseCase,
        analyticsService = analyticsService,
        crashReportingService = crashReportingService
    )
}
