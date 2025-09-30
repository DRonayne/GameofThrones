package com.darach.gameofthrones.core.data.mapper

import com.darach.gameofthrones.core.database.model.CharacterEntity
import com.darach.gameofthrones.core.domain.model.Character
import com.darach.gameofthrones.core.domain.util.RomanNumeralConverter
import com.darach.gameofthrones.core.network.model.CharacterDto

fun CharacterDto.toDomain(characterId: String): Character {
    val tvSeriesSeasons = tvSeries.mapNotNull { RomanNumeralConverter.extractSeasonNumber(it) }

    return Character(
        id = characterId,
        name = name,
        gender = gender,
        culture = culture,
        born = born,
        died = died,
        titles = titles,
        aliases = aliases,
        father = father,
        mother = mother,
        spouse = spouse,
        allegiances = allegiances,
        books = books,
        povBooks = povBooks,
        tvSeries = tvSeries,
        tvSeriesSeasons = tvSeriesSeasons,
        playedBy = playedBy,
        isFavorite = false,
        isDead = died.isNotEmpty()
    )
}

fun CharacterEntity.toDomain(): Character {
    val tvSeriesSeasons = tvSeries.mapNotNull { RomanNumeralConverter.extractSeasonNumber(it) }

    return Character(
        id = id,
        name = name,
        gender = gender,
        culture = culture,
        born = born,
        died = died,
        titles = titles,
        aliases = aliases,
        father = father,
        mother = mother,
        spouse = spouse,
        allegiances = allegiances,
        books = books,
        povBooks = povBooks,
        tvSeries = tvSeries,
        tvSeriesSeasons = tvSeriesSeasons,
        playedBy = playedBy,
        isFavorite = isFavorite,
        isDead = died.isNotEmpty(),
        lastUpdated = lastUpdated
    )
}

fun Character.toEntity(): CharacterEntity = CharacterEntity(
    id = id,
    name = name,
    gender = gender,
    culture = culture,
    born = born,
    died = died,
    titles = titles,
    aliases = aliases,
    father = father,
    mother = mother,
    spouse = spouse,
    allegiances = allegiances,
    books = books,
    povBooks = povBooks,
    tvSeries = tvSeries,
    playedBy = playedBy,
    isFavorite = isFavorite,
    lastUpdated = lastUpdated
)
