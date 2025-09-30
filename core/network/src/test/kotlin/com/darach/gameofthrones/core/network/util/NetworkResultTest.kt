package com.darach.gameofthrones.core.network.util

import com.google.common.truth.Truth.assertThat
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.Test

class NetworkResultTest {
    @Test
    fun `safeApiCall returns Success on successful call`() = runTest {
        val result = safeApiCall {
            "test data"
        }

        assertThat(result).isInstanceOf(NetworkResult.Success::class.java)
        assertThat((result as NetworkResult.Success).data).isEqualTo("test data")
    }

    @Test
    fun `safeApiCall returns Error on exception`() = runTest {
        val exceptionMessage = "Network error"
        val result = safeApiCall {
            throw IOException(exceptionMessage)
        }

        assertThat(result).isInstanceOf(NetworkResult.Error::class.java)
        val errorResult = result as NetworkResult.Error
        assertThat(errorResult.message).isEqualTo(exceptionMessage)
        assertThat(errorResult.exception).isInstanceOf(IOException::class.java)
    }

    @Test
    fun `safeApiCall handles serialization exception`() = runTest {
        val result = safeApiCall {
            throw kotlinx.serialization.SerializationException("Invalid JSON")
        }

        assertThat(result).isInstanceOf(NetworkResult.Error::class.java)
        val errorResult = result as NetworkResult.Error
        assertThat(errorResult.message).isEqualTo("Invalid JSON")
    }

    @Test
    fun `NetworkResult Success holds correct data type`() {
        val data = listOf(1, 2, 3)
        val result = NetworkResult.Success(data)

        assertThat(result.data).isEqualTo(data)
        assertThat(result.data).hasSize(3)
    }

    @Test
    fun `NetworkResult Error holds exception and message`() {
        val exception = IllegalStateException("Invalid state")
        val result = NetworkResult.Error("Custom error", exception)

        assertThat(result.message).isEqualTo("Custom error")
        assertThat(result.exception).isEqualTo(exception)
    }

    @Test
    fun `NetworkResult Loading is singleton object`() {
        val loading1 = NetworkResult.Loading
        val loading2 = NetworkResult.Loading

        assertThat(loading1).isSameInstanceAs(loading2)
    }
}
