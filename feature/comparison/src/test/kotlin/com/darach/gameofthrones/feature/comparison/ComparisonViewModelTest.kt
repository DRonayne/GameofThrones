package com.darach.gameofthrones.feature.comparison

import com.darach.gameofthrones.core.analytics.AnalyticsService
import com.darach.gameofthrones.core.domain.usecase.GetCharacterByIdUseCase
import com.darach.gameofthrones.core.model.Character
import com.darach.gameofthrones.feature.comparison.ComparisonDiffCalculator
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ComparisonViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ComparisonViewModel
    private lateinit var diffCalculator: ComparisonDiffCalculator
    private lateinit var getCharacterByIdUseCase: GetCharacterByIdUseCase
    private lateinit var analyticsService: AnalyticsService

    private val testCharacter1 = Character(
        id = "1",
        name = "Jon Snow",
        gender = "Male",
        culture = "Northmen",
        born = "283 AC",
        died = "",
        titles = listOf("King in the North"),
        aliases = listOf("Lord Snow"),
        father = "Eddard Stark",
        mother = "",
        spouse = "",
        allegiances = listOf("House Stark"),
        books = listOf(),
        povBooks = listOf(),
        tvSeries = listOf("Season 1"),
        tvSeriesSeasons = listOf(1),
        playedBy = listOf("Kit Harington")
    )

    private val testCharacter2 = testCharacter1.copy(
        id = "2",
        name = "Arya Stark",
        gender = "Female"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        diffCalculator = ComparisonDiffCalculator()
        getCharacterByIdUseCase = mockk()
        analyticsService = mockk(relaxed = true)
        viewModel = ComparisonViewModel(diffCalculator, getCharacterByIdUseCase, analyticsService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() {
        val state = viewModel.state.value

        assertEquals(0, state.selectedCharacters.size)
        assertNull(state.comparisonResult)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `compareCharacters with valid IDs succeeds`() = runTest {
        every { getCharacterByIdUseCase("1") } returns flowOf(testCharacter1)
        every { getCharacterByIdUseCase("2") } returns flowOf(testCharacter2)

        viewModel.compareCharacters("1", "2")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertNotNull(state.comparisonResult)
        assertEquals(2, state.comparisonResult?.characters?.size)
        assertEquals(2, state.selectedCharacters.size)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `compareCharacters with null character shows error`() = runTest {
        every { getCharacterByIdUseCase("1") } returns flowOf(testCharacter1)
        every { getCharacterByIdUseCase("2") } returns flowOf(null)

        viewModel.compareCharacters("1", "2")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertNull(state.comparisonResult)
        assertNotNull(state.error)
        assertEquals("Could not load characters for comparison", state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun `compareCharacters handles calculator exception`() = runTest {
        val badCalculator = mockk<ComparisonDiffCalculator>()
        every { badCalculator.calculate(any()) } throws IllegalArgumentException("Test error")
        every { getCharacterByIdUseCase("1") } returns flowOf(testCharacter1)
        every { getCharacterByIdUseCase("2") } returns flowOf(testCharacter2)

        val testViewModel = ComparisonViewModel(
            badCalculator,
            getCharacterByIdUseCase,
            analyticsService
        )
        testViewModel.compareCharacters("1", "2")
        advanceUntilIdle()

        val state = testViewModel.state.value
        assertNull(state.comparisonResult)
        assertNotNull(state.error)
        assertFalse(state.isLoading)
    }
}
