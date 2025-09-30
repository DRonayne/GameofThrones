package com.darach.gameofthrones.core.network.interceptor

import com.darach.gameofthrones.core.common.performance.NetworkMetric
import com.darach.gameofthrones.core.common.performance.PerformanceMonitor
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp interceptor that monitors network request performance using Firebase Performance.
 */
class PerformanceMonitoringInterceptor @Inject constructor(
    private val performanceMonitor: PerformanceMonitor
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        val method = request.method

        // Create network metric
        val metric: NetworkMetric = performanceMonitor.createNetworkMetric(url, method)

        // Track request payload size
        request.body?.contentLength()?.let { size ->
            if (size >= 0) {
                metric.setRequestPayloadSize(size)
            }
        }

        // Execute request and track response
        return try {
            val response = chain.proceed(request)

            // Track response metrics
            metric.setHttpResponseCode(response.code)

            response.body?.contentLength()?.let { size ->
                if (size >= 0) {
                    metric.setResponsePayloadSize(size)
                }
            }

            response.body?.contentType()?.toString()?.let { contentType ->
                metric.setResponseContentType(contentType)
            }

            response
        } finally {
            // Complete the metric whether successful or not
            metric.stop()
        }
    }
}
