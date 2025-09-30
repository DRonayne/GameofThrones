package com.darach.gameofthrones.core.network.model

import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.json.Json
import org.junit.Test

class CharacterDtoTest {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun `deserialize character JSON with all fields`() {
        val jsonString = """
            {
                "name":"Eddard Stark",
                "gender":"Male",
                "culture":"Northmen",
                "born":"In 263 AC, at Winterfell",
                "died":"In 299 AC, at Great Sept of Baelor in King's Landing",
                "titles":["Lord of Winterfell","Warden of the North"],
                "aliases":["Ned","The Ned"],
                "father":"",
                "mother":"",
                "spouse":"",
                "allegiances":[],
                "books":[],
                "povBooks":[],
                "tvSeries":["Season 1","Season 6"],
                "playedBy":["Sean Bean"]
            }
        """.trimIndent()

        val character = json.decodeFromString<CharacterDto>(jsonString)

        assertThat(character.name).isEqualTo("Eddard Stark")
        assertThat(character.gender).isEqualTo("Male")
        assertThat(character.culture).isEqualTo("Northmen")
        assertThat(character.born).isEqualTo("In 263 AC, at Winterfell")
        assertThat(character.died).isEqualTo("In 299 AC, at Great Sept of Baelor in King's Landing")
        assertThat(character.titles).containsExactly("Lord of Winterfell", "Warden of the North")
        assertThat(character.aliases).containsExactly("Ned", "The Ned")
        assertThat(character.tvSeries).containsExactly("Season 1", "Season 6")
        assertThat(character.playedBy).containsExactly("Sean Bean")
    }

    @Test
    fun `deserialize character JSON with minimal fields`() {
        val jsonString = """
            {
                "name":"Jon Snow"
            }
        """.trimIndent()

        val character = json.decodeFromString<CharacterDto>(jsonString)

        assertThat(character.name).isEqualTo("Jon Snow")
        assertThat(character.gender).isEmpty()
        assertThat(character.culture).isEmpty()
        assertThat(character.titles).isEmpty()
        assertThat(character.aliases).isEmpty()
    }

    @Test
    fun `deserialize character JSON with unknown fields`() {
        val jsonString = """
            {
                "name":"Tyrion Lannister",
                "unknownField":"some value",
                "anotherUnknown":123
            }
        """.trimIndent()

        val character = json.decodeFromString<CharacterDto>(jsonString)

        assertThat(character.name).isEqualTo("Tyrion Lannister")
    }

    @Test
    fun `serialize character to JSON`() {
        val character = CharacterDto(
            name = "Arya Stark",
            gender = "Female",
            culture = "Northmen",
            titles = listOf("Princess"),
            aliases = listOf("Arya Horseface", "Arry")
        )

        val jsonString = json.encodeToString(CharacterDto.serializer(), character)

        assertThat(jsonString).contains("\"name\":\"Arya Stark\"")
        assertThat(jsonString).contains("\"gender\":\"Female\"")
        assertThat(jsonString).contains("\"culture\":\"Northmen\"")
    }
}
