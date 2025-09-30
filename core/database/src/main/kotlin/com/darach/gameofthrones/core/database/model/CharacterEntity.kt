package com.darach.gameofthrones.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a Game of Thrones character in the local database.
 * Stores comprehensive character information including biographical data,
 * family relationships, allegiances, and media appearances.
 */
@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val gender: String,
    val culture: String,
    val born: String,
    val died: String,
    val titles: List<String>,
    val aliases: List<String>,
    val father: String,
    val mother: String,
    val spouse: String,
    val allegiances: List<String>,
    val books: List<String>,
    val povBooks: List<String>,
    val tvSeries: List<String>,
    val playedBy: List<String>,
    val characterImageUrl: String? = null,
    val actorImageUrls: Map<String, String?> = emptyMap(),
    val isFavorite: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)
