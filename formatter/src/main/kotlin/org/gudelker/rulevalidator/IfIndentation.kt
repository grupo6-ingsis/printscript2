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
        val indentSpaces = rule.quantity

        val lines = string.split("\n")
        val processedLines = mutableListOf<String>()
        var indentLevel = 0

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.endsWith("{")) {
                processedLines.add(" ".repeat(indentLevel * indentSpaces) + trimmed)
                indentLevel++
            } else if (trimmed == "}") {
                indentLevel--
                processedLines.add(" ".repeat(indentLevel * indentSpaces) + trimmed)
            } else if (indentLevel > 0 && trimmed.isNotEmpty()) {
                processedLines.add(" ".repeat(indentLevel * indentSpaces) + trimmed)
            } else {
                processedLines.add(line)
            }
        }

        return processedLines.joinToString("\n")
    }
}
