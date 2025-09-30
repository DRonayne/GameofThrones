package com.darach.gameofthrones.feature.comparison.ui

import com.darach.gameofthrones.core.domain.model.Character
import com.darach.gameofthrones.core.domain.usecase.GetFavoritesUseCase
import com.darach.gameofthrones.feature.comparison.ComparisonDiffCalculator
import com.darach.gameofthrones.feature.comparison.ComparisonIntent
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ComparisonViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ComparisonViewModel
    private lateinit var diffCalculator: ComparisonDiffCalculator
    private lateinit var getFavoritesUseCase: GetFavoritesUseCase

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

    private val testCharacter3 = testCharacter1.copy(
        id = "3",
        name = "Sansa Stark"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        diffCalculator = ComparisonDiffCalculator()
        getFavoritesUseCase = mockk()
        every { getFavoritesUseCase() } returns flowOf(emptyList())
        viewModel = ComparisonViewModel(diffCalculator, getFavoritesUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() {
        val state = viewModel.state.value

        assertEquals(0, state.selectedCharacters.size)
        assertFalse(state.isSelectionMode)
        assertNull(state.comparisonResult)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `enter selection mode updates state`() {
        viewModel.handleIntent(ComparisonIntent.EnterSelectionMode)

        assertTrue(viewModel.state.value.isSelectionMode)
    }

    @Test
    fun `exit selection mode clears selection`() {
        viewModel.handleIntent(ComparisonIntent.EnterSelectionMode)
        viewModel.handleIntent(ComparisonIntent.ToggleCharacterSelection(testCharacter1))
        viewModel.handleIntent(ComparisonIntent.ExitSelectionMode)

        val state = viewModel.state.value
        assertFalse(state.isSelectionMode)
        assertEquals(0, state.selectedCharacters.size)
    }

    @Test
    fun `toggle character adds to selection`() {
        viewModel.handleIntent(ComparisonIntent.ToggleCharacterSelection(testCharacter1))

        val state = viewModel.state.value
        assertEquals(1, state.selectedCharacters.size)
        assertEquals(testCharacter1.id, state.selectedCharacters[0].id)
    }

    @Test
    fun `toggle character removes from selection if already selected`() {
        viewModel.handleIntent(ComparisonIntent.ToggleCharacterSelection(testCharacter1))
        viewModel.handleIntent(ComparisonIntent.ToggleCharacterSelection(testCharacter1))

        assertEquals(0, viewModel.state.value.selectedCharacters.size)
    }

    @Test
    fun `cannot select more than maximum characters`() {
        viewModel.handleIntent(ComparisonIntent.ToggleCharacterSelection(testCharacter1))
        viewModel.handleIntent(ComparisonIntent.ToggleCharacterSelection(testCharacter2))

        assertEquals(2, viewModel.state.value.selectedCharacters.size)

        viewModel.handleIntent(ComparisonIntent.ToggleCharacterSelection(testCharacter3))

        assertEquals(2, viewModel.state.value.selectedCharacters.size)
    }

    @Test
    fun `clear selection empties selected characters`() {
        viewModel.handleIntent(ComparisonIntent.ToggleCharacterSelection(testCharacter1))
        viewModel.handleIntent(ComparisonIntent.ToggleCharacterSelection(testCharacter2))
        viewModel.handleIntent(ComparisonIntent.ClearSelection)

        assertEquals(0, viewModel.state.value.selectedCharacters.size)
    }

    @Test
    fun `start comparison with two characters succeeds`() = runTest {
        viewModel.handleIntent(ComparisonIntent.ToggleCharacterSelection(testCharacter1))
        viewModel.handleIntent(ComparisonIntent.ToggleCharacterSelection(testCharacter2))
        viewModel.handleIntent(ComparisonIntent.StartComparison)

        advanceUntilIdle()

        val state = viewModel.state.value
        assertNotNull(state.comparisonResult)
        assertEquals(2, state.comparisonResult?.characters?.size)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `start comparison with one character fails`() = runTest {
        viewModel.handleIntent(ComparisonIntent.ToggleCharacterSelection(testCharacter1))
        viewModel.handleIntent(ComparisonIntent.StartComparison)

        advanceUntilIdle()

        val state = viewModel.state.value
        assertNull(state.comparisonResult)
        assertNotNull(state.error)
        assertTrue(state.error!!.contains("2 characters"))
    }

    @Test
    fun `exit comparison clears result and selection`() = runTest {
        viewModel.handleIntent(ComparisonIntent.ToggleCharacterSelection(testCharacter1))
        viewModel.handleIntent(ComparisonIntent.ToggleCharacterSelection(testCharacter2))
        viewModel.handleIntent(ComparisonIntent.StartComparison)
        advanceUntilIdle()

        viewModel.handleIntent(ComparisonIntent.ExitComparison)

        val state = viewModel.state.value
        assertNull(state.comparisonResult)
        assertEquals(0, state.selectedCharacters.size)
    }

    @Test
    fun `switch character updates selection and recalculates comparison`() = runTest {
        viewModel.handleIntent(ComparisonIntent.ToggleCharacterSelection(testCharacter1))
        viewModel.handleIntent(ComparisonIntent.ToggleCharacterSelection(testCharacter2))
        viewModel.handleIntent(ComparisonIntent.StartComparison)
        advanceUntilIdle()

        viewModel.handleIntent(
            ComparisonIntent.SwitchCharacter(
                testCharacter1,
                testCharacter3
            )
        )
        advanceUntilIdle()

        val state = viewModel.state.value
        assertNotNull(state.comparisonResult)
        assertTrue(state.selectedCharacters.none { it.id == testCharacter1.id })
        assertTrue(state.selectedCharacters.any { it.id == testCharacter3.id })
    }

    @Test
    fun `start comparison completes and clears loading state`() = runTest {
        viewModel.handleIntent(ComparisonIntent.ToggleCharacterSelection(testCharacter1))
        viewModel.handleIntent(ComparisonIntent.ToggleCharacterSelection(testCharacter2))
        viewModel.handleIntent(ComparisonIntent.StartComparison)

        advanceUntilIdle()

        val state = viewModel.state.value
        assertNotNull(state.comparisonResult)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }
}
