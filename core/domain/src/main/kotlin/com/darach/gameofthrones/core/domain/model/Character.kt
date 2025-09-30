package com.darach.gameofthrones.core.domain.model

data class Character(
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
    val tvSeriesSeasons: List<Int>,
    val playedBy: List<String>,
    val isFavorite: Boolean = false,
    val isDead: Boolean = died.isNotEmpty(),
    val lastUpdated: Long = System.currentTimeMillis()
)
