package com.darach.gameofthrones.core.domain.usecase

import com.darach.gameofthrones.core.model.Character
import javax.inject.Inject

data class CharacterComparison(
    val character1: Character,
    val character2: Character,
    val commonTitles: List<String>,
    val commonAllegiances: List<String>,
    val commonBooks: List<String>,
    val commonTvSeries: List<String>,
    val sameCulture: Boolean,
    val sameGender: Boolean,
    val bothDead: Boolean,
    val bothAlive: Boolean
)

class CompareCharactersUseCase @Inject constructor() {
    public operator fun invoke(character1: Character, character2: Character): CharacterComparison =
        CharacterComparison(
            character1 = character1,
            character2 = character2,
            commonTitles = character1.titles.intersect(character2.titles.toSet()).toList(),
            commonAllegiances = character1.allegiances.intersect(
                character2.allegiances.toSet()
            ).toList(),
            commonBooks = character1.books.intersect(character2.books.toSet()).toList(),
            commonTvSeries = character1.tvSeries.intersect(character2.tvSeries.toSet()).toList(),
            sameCulture = character1.culture.isNotEmpty() &&
                character1.culture == character2.culture,
            sameGender = character1.gender.isNotEmpty() &&
                character1.gender == character2.gender,
            bothDead = character1.isDead && character2.isDead,
            bothAlive = !character1.isDead && !character2.isDead
        )
}
