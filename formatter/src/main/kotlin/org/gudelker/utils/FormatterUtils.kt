package org.gudelker.utils

import org.gudelker.rules.Rule

object FormatterUtils {
    fun getDeclarationSpaces(
        ruleName: String,
        map: Map<String, Rule>,
    ): String {
        return if (map.containsKey(ruleName) && map[ruleName]!!.on) {
            generateSpaces(map[ruleName]!!.quantity)
        } else {
            generateSpaces(1)
        }
    }

    fun getAssignationSpaces(
        ruleName: String,
        map: Map<String, Rule>,
    ): String {
        return if (map.containsKey(ruleName) && map[ruleName]!!.on) {
            generateSpaces(map[ruleName]!!.quantity)
        } else {
            generateSpaces(1)
        }
    }

    private fun generateSpaces(quantity: Int): String {
        return " ".repeat(quantity)
    }

    fun generateNewLines(
        ruleName: String,
        map: Map<String, Rule>,
    ): String {
        return if (map.containsKey(ruleName) && map[ruleName]!!.on) {
            "\n".repeat(map[ruleName]!!.quantity)
        } else {
            "\n"
        }
    }
}
