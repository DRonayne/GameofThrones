package com.darach.gameofthrones.core.network.model

import kotlinx.serialization.Serializable

/**
 * Data transfer object representing a Game of Thrones character from the API.
 */
@Serializable
data class CharacterDto(
    val name: String = "",
    val gender: String = "",
    val culture: String = "",
    val born: String = "",
    val died: String = "",
    val titles: List<String> = emptyList(),
    val aliases: List<String> = emptyList(),
    val father: String = "",
    val mother: String = "",
    val spouse: String = "",
    val allegiances: List<String> = emptyList(),
    val books: List<String> = emptyList(),
    val povBooks: List<String> = emptyList(),
    val tvSeries: List<String> = emptyList(),
    val playedBy: List<String> = emptyList()
)
