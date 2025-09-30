package com.darach.gameofthrones.core.network.api

import com.darach.gameofthrones.core.network.model.CharacterDto
import retrofit2.http.GET

/**
 * Retrofit API service for Game of Thrones character endpoints.
 */
interface GoTApiService {
    /**
     * Fetches all Game of Thrones characters from the API.
     *
     * @return List of character DTOs
     */
    @GET("characters")
    suspend fun getCharacters(): List<CharacterDto>
}
