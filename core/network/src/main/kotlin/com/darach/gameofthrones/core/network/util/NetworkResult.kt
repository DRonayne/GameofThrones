package com.darach.gameofthrones.core.network.util

/**
 * A sealed class representing the result of a network operation.
 */
sealed class NetworkResult<out T> {
    /**
     * Represents a successful network operation with data.
     */
    data class Success<T>(val data: T) : NetworkResult<T>()

    /**
     * Represents a network operation failure with an error message.
     */
    data class Error(val message: String, val exception: Throwable? = null) :
        NetworkResult<Nothing>()

    /**
     * Represents a loading state for the network operation.
     */
    data object Loading : NetworkResult<Nothing>()
}

/**
 * Extension function to safely execute a network call and wrap the result in NetworkResult.
 * Handles common network-related exceptions.
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> = try {
    NetworkResult.Success(apiCall())
} catch (e: java.io.IOException) {
    NetworkResult.Error(
        message = e.message ?: "Network connection error",
        exception = e
    )
} catch (e: kotlinx.serialization.SerializationException) {
    NetworkResult.Error(
        message = e.message ?: "Data parsing error",
        exception = e
    )
} catch (e: retrofit2.HttpException) {
    NetworkResult.Error(
        message = "HTTP ${e.code()}: ${e.message()}",
        exception = e
    )
}
