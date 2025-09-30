package com.darach.gameofthrones.core.common.performance

/**
 * Extension function to trace the performance of a suspending operation.
 *
 * @param traceName The name of the trace.
 * @param attributes Optional attributes to add to the trace.
 * @param block The suspending operation to trace.
 * @return The result of the operation.
 */
suspend inline fun <T> PerformanceMonitor.trace(
    traceName: String,
    attributes: Map<String, String> = emptyMap(),
    block: () -> T
): T {
    val trace = startTrace(traceName)
    attributes.forEach { (key, value) ->
        trace.putAttribute(key, value)
    }

    return try {
        block()
    } finally {
        trace.stop()
    }
}

/**
 * Extension function to trace the performance of a blocking operation.
 *
 * @param traceName The name of the trace.
 * @param attributes Optional attributes to add to the trace.
 * @param block The blocking operation to trace.
 * @return The result of the operation.
 */
inline fun <T> PerformanceMonitor.traceSync(
    traceName: String,
    attributes: Map<String, String> = emptyMap(),
    block: () -> T
): T {
    val trace = startTrace(traceName)
    attributes.forEach { (key, value) ->
        trace.putAttribute(key, value)
    }

    return try {
        block()
    } finally {
        trace.stop()
    }
}
