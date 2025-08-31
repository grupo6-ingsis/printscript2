package org.gudelker.utils

import org.gudelker.rules.Rule

object Utils {
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

    fun generateSpaces(quantity: Int): String {
        return " ".repeat(quantity)
    }
}
