package org.gudelker.utils

object FormatterUtils {
    fun generateSpaces(quantity: Int): String {
        return " ".repeat(quantity)
    }

    fun generateNewLines(quantity: Int): String {
        return "\n".repeat(quantity)
    }
}
