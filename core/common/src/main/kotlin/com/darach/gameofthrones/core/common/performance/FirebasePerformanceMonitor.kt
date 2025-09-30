package com.darach.gameofthrones.core.common.performance

import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.HttpMetric
import com.google.firebase.perf.metrics.Trace as FirebaseTrace
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase implementation of [PerformanceMonitor].
 */
@Singleton
class FirebasePerformanceMonitor @Inject constructor(
    private val firebasePerformance: FirebasePerformance
) : PerformanceMonitor {

    override fun startTrace(traceName: String): Trace {
        val firebaseTrace = firebasePerformance.newTrace(traceName)
        firebaseTrace.start()
        return FirebaseTraceWrapper(firebaseTrace)
    }

    override fun createNetworkMetric(url: String, httpMethod: String): NetworkMetric {
        val httpMetric = firebasePerformance.newHttpMetric(url, httpMethod)
        httpMetric.start()
        return FirebaseNetworkMetricWrapper(httpMetric)
    }

    override fun recordScreenRenderTime(screenName: String, renderTimeMs: Long) {
        val trace = startTrace("screen_render_$screenName")
        trace.putMetric("render_time_ms", renderTimeMs)
        trace.putAttribute("screen_name", screenName)
        trace.stop()
    }

    override fun incrementMetric(metricName: String, incrementBy: Long) {
        // Firebase Performance doesn't support global counters directly,
        // so we create a trace for each increment
        val trace = startTrace(metricName)
        trace.putMetric("count", incrementBy)
        trace.stop()
    }

    /**
     * Wrapper for Firebase Trace.
     */
    private class FirebaseTraceWrapper(private val firebaseTrace: FirebaseTrace) : Trace {

        override fun putMetric(metricName: String, value: Long) {
            firebaseTrace.putMetric(metricName, value)
        }

        override fun incrementMetric(metricName: String, incrementBy: Long) {
            firebaseTrace.incrementMetric(metricName, incrementBy)
        }

        override fun putAttribute(attributeName: String, value: String) {
            firebaseTrace.putAttribute(attributeName, value)
        }

        override fun stop() {
            firebaseTrace.stop()
        }
    }

    /**
     * Wrapper for Firebase HttpMetric.
     */
    private class FirebaseNetworkMetricWrapper(private val httpMetric: HttpMetric) : NetworkMetric {

        override fun setHttpResponseCode(code: Int) {
            httpMetric.setHttpResponseCode(code)
        }

        override fun setRequestPayloadSize(bytes: Long) {
            httpMetric.setRequestPayloadSize(bytes)
        }

        override fun setResponsePayloadSize(bytes: Long) {
            httpMetric.setResponsePayloadSize(bytes)
        }

        override fun setResponseContentType(contentType: String) {
            httpMetric.setResponseContentType(contentType)
        }

        override fun stop() {
            httpMetric.stop()
        }
    }
}
