package com.darach.gameofthrones.core.network.integration

import com.darach.gameofthrones.core.network.api.GoTApiService
import com.google.common.truth.Truth.assertThat
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * Integration test for network layer using MockWebServer.
 * Tests API service behavior with various network scenarios.
 */
class MockWebServerIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: GoTApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val json = Json { ignoreUnknownKeys = true }
        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(GoTApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test successful API response parsing`() = runTest {
        // Given: Valid JSON response
        val mockResponse = """
            [
                {
                    "name": "Eddard Stark",
                    "gender": "Male",
                    "culture": "Northmen",
                    "born": "In 263 AC",
                    "died": "In 299 AC",
                    "titles": ["Lord of Winterfell", "Warden of the North", "Hand of the King"],
                    "aliases": ["Ned"],
                    "father": "",
                    "mother": "",
                    "spouse": "",
                    "allegiances": ["House Stark"],
                    "books": [],
                    "povBooks": ["A Game of Thrones"],
                    "tvSeries": ["Season 1"],
                    "playedBy": ["Sean Bean", "Sebastian Croft", "Robert Aramayo"]
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
        )

        // When: Calling API
        val characters = apiService.getCharacters()

        // Then: Response should be correctly parsed
        assertThat(characters).hasSize(1)
        val character = characters.first()
        assertThat(character.name).isEqualTo("Eddard Stark")
        assertThat(character.gender).isEqualTo("Male")
        assertThat(character.culture).isEqualTo("Northmen")
        assertThat(character.born).isEqualTo("In 263 AC")
        assertThat(character.died).isEqualTo("In 299 AC")
        assertThat(character.titles).hasSize(3)
        assertThat(character.titles).contains("Lord of Winterfell")
        assertThat(character.aliases).containsExactly("Ned")
        assertThat(character.playedBy).hasSize(3)
        assertThat(character.tvSeries).containsExactly("Season 1")
    }

    @Test
    fun `test empty response`() = runTest {
        // Given: Empty array response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("[]")
        )

        // When: Calling API
        val characters = apiService.getCharacters()

        // Then: Should return empty list
        assertThat(characters).isEmpty()
    }

    @Test
    fun `test character with minimal data`() = runTest {
        // Given: Character with only required fields
        val mockResponse = """
            [
                {
                    "name": "Unknown Character",
                    "gender": "",
                    "culture": "",
                    "born": "",
                    "died": "",
                    "titles": [],
                    "aliases": [],
                    "father": "",
                    "mother": "",
                    "spouse": "",
                    "allegiances": [],
                    "books": [],
                    "povBooks": [],
                    "tvSeries": [],
                    "playedBy": []
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
        )

        // When: Calling API
        val characters = apiService.getCharacters()

        // Then: Should handle minimal data correctly
        assertThat(characters).hasSize(1)
        val character = characters.first()
        assertThat(character.name).isEqualTo("Unknown Character")
        assertThat(character.gender).isEmpty()
        assertThat(character.culture).isEmpty()
        assertThat(character.titles).isEmpty()
        assertThat(character.playedBy).isEmpty()
    }

    @Test
    fun `test multiple characters response`() = runTest {
        // Given: Multiple characters in response
        val mockResponse = """
            [
                {
                    "name": "Catelyn Stark",
                    "gender": "Female",
                    "culture": "Rivermen",
                    "born": "In 264 AC",
                    "died": "In 299 AC",
                    "titles": ["Lady of Winterfell"],
                    "aliases": ["Cat", "Lady Stoneheart"],
                    "father": "",
                    "mother": "",
                    "spouse": "",
                    "allegiances": ["House Tully", "House Stark"],
                    "books": [],
                    "povBooks": ["A Game of Thrones", "A Clash of Kings"],
                    "tvSeries": ["Season 1", "Season 2", "Season 3"],
                    "playedBy": ["Michelle Fairley"]
                },
                {
                    "name": "Robb Stark",
                    "gender": "Male",
                    "culture": "Northmen",
                    "born": "In 283 AC",
                    "died": "In 299 AC",
                    "titles": ["King in the North"],
                    "aliases": ["The Young Wolf"],
                    "father": "",
                    "mother": "",
                    "spouse": "",
                    "allegiances": ["House Stark"],
                    "books": [],
                    "povBooks": [],
                    "tvSeries": ["Season 1", "Season 2", "Season 3"],
                    "playedBy": ["Richard Madden"]
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
        )

        // When: Calling API
        val characters = apiService.getCharacters()

        // Then: Should parse all characters correctly
        assertThat(characters).hasSize(2)
        assertThat(characters.map { it.name }).containsExactly(
            "Catelyn Stark",
            "Robb Stark"
        )
    }

    @Test
    fun `test request sent to correct endpoint`() = runTest {
        // Given: Mock response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("[]")
        )

        // When: Calling API
        apiService.getCharacters()

        // Then: Should call correct endpoint
        val request = mockWebServer.takeRequest()
        assertThat(request.path).isEqualTo("/characters")
        assertThat(request.method).isEqualTo("GET")
    }

    @Test
    fun `test response with extra unknown fields`() = runTest {
        // Given: Response with unknown fields (should be ignored)
        val mockResponse = """
            [
                {
                    "name": "Theon Greyjoy",
                    "gender": "Male",
                    "culture": "Ironborn",
                    "born": "In 278 AC",
                    "died": "",
                    "titles": ["Prince of Winterfell"],
                    "aliases": ["Reek"],
                    "father": "",
                    "mother": "",
                    "spouse": "",
                    "allegiances": ["House Greyjoy"],
                    "books": [],
                    "povBooks": ["A Clash of Kings"],
                    "tvSeries": ["Season 1"],
                    "playedBy": ["Alfie Allen"],
                    "unknownField1": "ignored",
                    "unknownField2": 12345
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
        )

        // When: Calling API
        val characters = apiService.getCharacters()

        // Then: Should ignore unknown fields and parse known fields
        assertThat(characters).hasSize(1)
        assertThat(characters.first().name).isEqualTo("Theon Greyjoy")
    }

    @Test
    fun `test network error handling`() = runTest {
        // Given: Server error response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        // When/Then: Should throw exception
        try {
            apiService.getCharacters()
            throw AssertionError("Expected exception to be thrown")
        } catch (e: Exception) {
            assertThat(e).isNotNull()
        }
    }

    @Test
    fun `test timeout handling`() = runTest {
        // Given: Delayed response (simulating timeout)
        mockWebServer.enqueue(
            MockResponse()
                .setBody("[]")
                .throttleBody(1, 10, TimeUnit.SECONDS)
        )

        // Create API service with short timeout
        val shortTimeoutClient = OkHttpClient.Builder()
            .readTimeout(100, TimeUnit.MILLISECONDS)
            .build()

        val json = Json { ignoreUnknownKeys = true }
        val timeoutApiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(shortTimeoutClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(GoTApiService::class.java)

        // When/Then: Should timeout
        try {
            timeoutApiService.getCharacters()
            throw AssertionError("Expected timeout exception")
        } catch (e: Exception) {
            assertThat(e).isNotNull()
        }
    }

    @Test
    fun `test character with all TV seasons`() = runTest {
        // Given: Character appearing in multiple seasons
        val mockResponse = """
            [
                {
                    "name": "Jaime Lannister",
                    "gender": "Male",
                    "culture": "Westerlands",
                    "born": "In 266 AC",
                    "died": "",
                    "titles": ["Ser", "Lord Commander of the Kingsguard"],
                    "aliases": ["Kingslayer", "The Lion of Lannister"],
                    "father": "",
                    "mother": "",
                    "spouse": "",
                    "allegiances": ["House Lannister"],
                    "books": [],
                    "povBooks": ["A Storm of Swords"],
                    "tvSeries": ["Season 1", "Season 2", "Season 3", "Season 4", "Season 5", "Season 6", "Season 7", "Season 8"],
                    "playedBy": ["Nikolaj Coster-Waldau"]
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
        )

        // When: Calling API
        val characters = apiService.getCharacters()

        // Then: Should parse all TV seasons
        assertThat(characters).hasSize(1)
        val character = characters.first()
        assertThat(character.tvSeries).hasSize(8)
        assertThat(character.tvSeries).contains("Season 1")
        assertThat(character.tvSeries).contains("Season 8")
    }

    @Test
    fun `test 404 not found response`() = runTest {
        // Given: 404 response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("Not Found")
        )

        // When/Then: Should throw exception
        try {
            apiService.getCharacters()
            throw AssertionError("Expected 404 exception")
        } catch (e: Exception) {
            assertThat(e).isNotNull()
        }
    }

    @Test
    fun `test malformed JSON handling`() = runTest {
        // Given: Malformed JSON
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("{invalid json")
        )

        // When/Then: Should throw parsing exception
        try {
            apiService.getCharacters()
            throw AssertionError("Expected parsing exception")
        } catch (e: Exception) {
            assertThat(e).isNotNull()
        }
    }
}
