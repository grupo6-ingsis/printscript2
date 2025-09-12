package org.gudelker.rulevalidator

import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement

class IfIndentation : RuleValidatorFormatter {
    override fun matches(formatterRuleMap: Map<String, FormatterRule>): Boolean {
        val ruleName = "indent-inside-if"
        val rule = formatterRuleMap[ruleName] ?: return false
        return formatterRuleMap.containsKey(ruleName) && rule.on
    }

    override fun applyRule(
        string: String,
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
    ): String {
        val rule = formatterRuleMap["indent-inside-if"] ?: return string
        val indentSpaces = " ".repeat(rule.quantity)

        val lines = string.split("\n")
        val processedLines = mutableListOf<String>()

        var insideBlock = false

        for (line in lines) {
            when {
                line.trim().startsWith("if (") && line.trim().endsWith("{") -> {
                    processedLines.add(line)
                    insideBlock = true
                }
                line.trim() == "}" -> {
                    processedLines.add(line)
                    insideBlock = false
                }
                line.trim() == "} else {" -> {
                    processedLines.add(line)
                    insideBlock = true
                }
                insideBlock && line.trim().isNotEmpty() -> {
                    processedLines.add("$indentSpaces$line")
                }
                else -> {
                    processedLines.add(line)
                }
            }
        }

        return processedLines.joinToString("\n")
    }
}
