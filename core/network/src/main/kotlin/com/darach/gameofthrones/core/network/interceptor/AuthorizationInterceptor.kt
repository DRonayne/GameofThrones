package com.darach.gameofthrones.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that adds the Authorization header to all API requests.
 *
 * @property token The bearer token for API authentication
 */
class AuthorizationInterceptor(private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestWithAuth = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(requestWithAuth)
    }
}
