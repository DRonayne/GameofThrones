package com.darach.gameofthrones.core.network.interceptor

import com.google.common.truth.Truth.assertThat
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test

class AuthorizationInterceptorTest {
    private lateinit var server: MockWebServer
    private lateinit var interceptor: AuthorizationInterceptor
    private val testToken = "test_token_123"

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()
        interceptor = AuthorizationInterceptor(testToken)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `intercept adds authorization header`() {
        server.enqueue(MockResponse().setResponseCode(200))

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val request = Request.Builder()
            .url(server.url("/test"))
            .build()

        client.newCall(request).execute()

        val recordedRequest = server.takeRequest()
        assertThat(recordedRequest.getHeader("Authorization"))
            .isEqualTo("Bearer $testToken")
    }

    @Test
    fun `intercept preserves existing headers`() {
        server.enqueue(MockResponse().setResponseCode(200))

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val request = Request.Builder()
            .url(server.url("/test"))
            .addHeader("Custom-Header", "custom-value")
            .build()

        client.newCall(request).execute()

        val recordedRequest = server.takeRequest()
        assertThat(recordedRequest.getHeader("Custom-Header")).isEqualTo("custom-value")
        assertThat(recordedRequest.getHeader("Authorization")).isEqualTo("Bearer $testToken")
    }
}
