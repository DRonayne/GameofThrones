package com.darach.gameofthrones.core.database.util

import androidx.room.TypeConverter

/**
 * Type converters for Room database to handle complex data types.
 * Converts between List<String> and String for storage in SQLite.
 */
class Converters {

    /**
     * Converts a comma-separated string to a List<String>.
     * Empty strings are converted to empty lists.
     */
    @TypeConverter
    fun fromString(value: String): List<String> = if (value.isEmpty()) {
        emptyList()
    } else {
        value.split(DELIMITER).map { it.trim() }
    }

    /**
     * Converts a List<String> to a comma-separated string.
     * Empty lists are converted to empty strings.
     */
    @TypeConverter
    fun fromList(list: List<String>): String = list.joinToString(separator = DELIMITER)

    /**
     * Converts a JSON string to a Map<String, String?>.
     * Empty strings are converted to empty maps.
     */
    @TypeConverter
    fun fromJsonString(value: String): Map<String, String?> {
        if (value.isEmpty()) return emptyMap()
        return value.split(ENTRY_DELIMITER).associate { entry ->
            val parts = entry.split(KEY_VALUE_DELIMITER, limit = 2)
            val key = parts[0]
            val valueStr = parts.getOrNull(1)
            key to if (valueStr == NULL_VALUE) null else valueStr
        }
    }

    /**
     * Converts a Map<String, String?> to a JSON string.
     * Empty maps are converted to empty strings.
     */
    @TypeConverter
    fun fromMap(map: Map<String, String?>): String =
        map.entries.joinToString(separator = ENTRY_DELIMITER) { (key, value) ->
            "$key$KEY_VALUE_DELIMITER${value ?: NULL_VALUE}"
        }

    companion object {
        private const val DELIMITER = "|||"
        private const val ENTRY_DELIMITER = ":::"
        private const val KEY_VALUE_DELIMITER = "==="
        private const val NULL_VALUE = "<<NULL>>"
    }
}
