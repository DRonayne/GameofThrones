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

    companion object {
        private const val DELIMITER = "|||"
    }
}
