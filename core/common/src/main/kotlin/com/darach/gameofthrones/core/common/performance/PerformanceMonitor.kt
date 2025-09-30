package com.darach.gameofthrones.core.common.performance

/**
 * Interface for performance monitoring and tracing.
 */
interface PerformanceMonitor {
    /**
     * Starts a custom trace with the given name.
     * @return A [Trace] object that can be used to add metrics and stop the trace.
     */
    fun startTrace(traceName: String): Trace

    /**
     * Creates a network request metric.
     * @return A [NetworkMetric] object that can be used to track network requests.
     */
    fun createNetworkMetric(url: String, httpMethod: String): NetworkMetric

    /**
     * Records a screen rendering time.
     * @param screenName The name of the screen.
     * @param renderTimeMs The time it took to render the screen in milliseconds.
     */
    fun recordScreenRenderTime(screenName: String, renderTimeMs: Long)

    /**
     * Increments a counter metric.
     * @param metricName The name of the metric.
     * @param incrementBy The value to increment by (default 1).
     */
    fun incrementMetric(metricName: String, incrementBy: Long = 1)
}

/**
 * Represents a custom trace for measuring performance.
 */
interface Trace {
    /**
     * Adds a custom metric to the trace.
     * @param metricName The name of the metric.
     * @param value The value of the metric.
     */
    fun putMetric(metricName: String, value: Long)

    /**
     * Increments a metric within the trace.
     * @param metricName The name of the metric.
     * @param incrementBy The value to increment by (default 1).
     */
    fun incrementMetric(metricName: String, incrementBy: Long = 1)

    /**
     * Adds a custom attribute to the trace.
     * @param attributeName The name of the attribute.
     * @param value The value of the attribute.
     */
    fun putAttribute(attributeName: String, value: String)

    /**
     * Stops the trace.
     */
    fun stop()
}

/**
 * Represents a network request metric.
 */
interface NetworkMetric {
    /**
     * Sets the HTTP response code.
     */
    fun setHttpResponseCode(code: Int)

    /**
     * Sets the request payload size in bytes.
     */
    fun setRequestPayloadSize(bytes: Long)

    /**
     * Sets the response payload size in bytes.
     */
    fun setResponsePayloadSize(bytes: Long)

    /**
     * Sets the response content type.
     */
    fun setResponseContentType(contentType: String)

    /**
     * Marks the metric as complete and reports it.
     */
    fun stop()
}
