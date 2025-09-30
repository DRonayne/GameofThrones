package com.darach.gameofthrones.core.database.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CharacterEntityTest {

    @Test
    fun `CharacterEntity has correct default values`() {
        val character = CharacterEntity(
            id = "1",
            name = "Jon Snow",
            gender = "Male",
            culture = "Northmen",
            born = "283 AC",
            died = "",
            titles = listOf("Lord Commander"),
            aliases = listOf("The Bastard of Winterfell"),
            father = "",
            mother = "",
            spouse = "",
            allegiances = listOf("House Stark"),
            books = emptyList(),
            povBooks = emptyList(),
            tvSeries = listOf("Season 1"),
            playedBy = listOf("Kit Harington")
        )

        assertThat(character.isFavorite).isFalse()
        assertThat(character.lastUpdated).isGreaterThan(0L)
    }

    @Test
    fun `CharacterEntity can be created with custom favorite status`() {
        val character = CharacterEntity(
            id = "1",
            name = "Jon Snow",
            gender = "Male",
            culture = "Northmen",
            born = "283 AC",
            died = "",
            titles = emptyList(),
            aliases = emptyList(),
            father = "",
            mother = "",
            spouse = "",
            allegiances = emptyList(),
            books = emptyList(),
            povBooks = emptyList(),
            tvSeries = emptyList(),
            playedBy = emptyList(),
            isFavorite = true
        )

        assertThat(character.isFavorite).isTrue()
    }

    @Test
    fun `CharacterEntity can be copied with modified fields`() {
        val original = CharacterEntity(
            id = "1",
            name = "Jon Snow",
            gender = "Male",
            culture = "Northmen",
            born = "283 AC",
            died = "",
            titles = emptyList(),
            aliases = emptyList(),
            father = "",
            mother = "",
            spouse = "",
            allegiances = emptyList(),
            books = emptyList(),
            povBooks = emptyList(),
            tvSeries = emptyList(),
            playedBy = emptyList(),
            isFavorite = false
        )

        val modified = original.copy(isFavorite = true)

        assertThat(modified.isFavorite).isTrue()
        assertThat(modified.name).isEqualTo(original.name)
        assertThat(modified.id).isEqualTo(original.id)
    }

    @Test
    fun `CharacterEntity handles empty lists correctly`() {
        val character = CharacterEntity(
            id = "1",
            name = "Jon Snow",
            gender = "Male",
            culture = "Northmen",
            born = "283 AC",
            died = "",
            titles = emptyList(),
            aliases = emptyList(),
            father = "",
            mother = "",
            spouse = "",
            allegiances = emptyList(),
            books = emptyList(),
            povBooks = emptyList(),
            tvSeries = emptyList(),
            playedBy = emptyList()
        )

        assertThat(character.titles).isEmpty()
        assertThat(character.aliases).isEmpty()
        assertThat(character.allegiances).isEmpty()
        assertThat(character.books).isEmpty()
        assertThat(character.povBooks).isEmpty()
        assertThat(character.tvSeries).isEmpty()
        assertThat(character.playedBy).isEmpty()
    }

    @Test
    fun `CharacterEntity handles populated lists correctly`() {
        val character = CharacterEntity(
            id = "1",
            name = "Jon Snow",
            gender = "Male",
            culture = "Northmen",
            born = "283 AC",
            died = "",
            titles = listOf("Lord Commander", "King in the North"),
            aliases = listOf("The Bastard of Winterfell", "The White Wolf"),
            father = "",
            mother = "",
            spouse = "",
            allegiances = listOf("House Stark", "Night's Watch"),
            books = listOf("A Game of Thrones"),
            povBooks = listOf("A Dance with Dragons"),
            tvSeries = listOf("Season 1", "Season 2"),
            playedBy = listOf("Kit Harington")
        )

        assertThat(character.titles).hasSize(2)
        assertThat(character.aliases).hasSize(2)
        assertThat(character.allegiances).hasSize(2)
        assertThat(character.books).hasSize(1)
        assertThat(character.povBooks).hasSize(1)
        assertThat(character.tvSeries).hasSize(2)
        assertThat(character.playedBy).hasSize(1)
    }
}
