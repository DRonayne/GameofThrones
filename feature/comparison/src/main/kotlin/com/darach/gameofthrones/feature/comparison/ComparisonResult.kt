package com.darach.gameofthrones.feature.comparison

import com.darach.gameofthrones.core.domain.model.Character

/**
 * Result model for character comparison with difference highlighting.
 * Supports comparison of 2-3 characters simultaneously.
 */
data class ComparisonResult(
    val characters: List<Character>,
    val attributes: List<ComparisonAttribute>
) {
    init {
        require(characters.size in 2..3) {
            "Comparison requires 2-3 characters, got ${characters.size}"
        }
    }
}

/**
 * Represents a single attribute comparison across characters.
 * Contains the attribute name, values for each character, and difference highlighting.
 */
data class ComparisonAttribute(
    val name: String,
    val values: List<AttributeValue>,
    val hasDifference: Boolean
) {
    init {
        require(values.isNotEmpty()) {
            "Attribute values cannot be empty"
        }
    }
}

/**
 * Value of a single attribute for a character.
 * @param value The display value (can be a single value or comma-separated list)
 * @param isDifferent Whether this value differs from other characters
 * @param isEmpty Whether this value is empty/unknown
 */
data class AttributeValue(
    val value: String,
    val isDifferent: Boolean = false,
    val isEmpty: Boolean = false
)
