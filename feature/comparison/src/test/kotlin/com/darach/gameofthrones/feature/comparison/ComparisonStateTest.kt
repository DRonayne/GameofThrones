package com.darach.gameofthrones.feature.comparison

import com.darach.gameofthrones.core.model.Character
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ComparisonStateTest {

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
        allegiances = listOf(),
        books = listOf(),
        povBooks = listOf(),
        tvSeries = listOf("Season 1"),
        tvSeriesSeasons = listOf(1),
        playedBy = listOf("Kit Harington")
    )

    private val testCharacter2 = testCharacter1.copy(id = "2", name = "Arya Stark")
    private val testCharacter3 = testCharacter1.copy(id = "3", name = "Sansa Stark")

    @Test
    fun `initial state has empty selections`() {
        val state = ComparisonState()

        assertEquals(0, state.selectionCount)
        assertFalse(state.canCompare)
        assertFalse(state.isSelectionMode)
        assertFalse(state.isMaxSelected)
    }

    @Test
    fun `canCompare is false with one character`() {
        val state = ComparisonState(selectedCharacters = listOf(testCharacter1))

        assertFalse(state.canCompare)
        assertEquals(1, state.selectionCount)
    }

    @Test
    fun `canCompare is true with two characters`() {
        val state = ComparisonState(
            selectedCharacters = listOf(testCharacter1, testCharacter2)
        )

        assertTrue(state.canCompare)
        assertEquals(2, state.selectionCount)
        assertTrue(state.isMaxSelected)
    }

    @Test
    fun `canCompare is false with three characters`() {
        val state = ComparisonState(
            selectedCharacters = listOf(testCharacter1, testCharacter2, testCharacter3)
        )

        assertFalse(state.canCompare)
        assertEquals(3, state.selectionCount)
    }

    @Test
    fun `isMaxSelected is true when at maximum`() {
        val state = ComparisonState(
            selectedCharacters = listOf(testCharacter1, testCharacter2)
        )

        assertTrue(state.isMaxSelected)
    }

    @Test
    fun `isMaxSelected is false when below maximum`() {
        val state = ComparisonState(
            selectedCharacters = listOf(testCharacter1)
        )

        assertFalse(state.isMaxSelected)
    }

    @Test
    fun `max selection size is two`() {
        assertEquals(2, ComparisonState.MAX_SELECTION_SIZE)
    }

    @Test
    fun `selection mode can be enabled`() {
        val state = ComparisonState(isSelectionMode = true)

        assertTrue(state.isSelectionMode)
    }

    @Test
    fun `comparison result can be set`() {
        val result = ComparisonResult(
            characters = listOf(testCharacter1, testCharacter2),
            attributes = emptyList()
        )
        val state = ComparisonState(comparisonResult = result)

        assertEquals(result, state.comparisonResult)
    }

    @Test
    fun `loading state can be set`() {
        val state = ComparisonState(isLoading = true)

        assertTrue(state.isLoading)
    }

    @Test
    fun `error can be set`() {
        val error = "Test error"
        val state = ComparisonState(error = error)

        assertEquals(error, state.error)
    }
}
