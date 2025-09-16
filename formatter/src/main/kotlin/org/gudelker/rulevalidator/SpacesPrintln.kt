package org.gudelker.rulevalidator

import org.gudelker.expressions.Callable
import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement

class SpacesPrintln : RuleValidatorFormatter {
    override fun matches(formatterRuleMap: Map<String, FormatterRule>): Boolean {
        val ruleName = "line-breaks-after-println"
        val rule = formatterRuleMap[ruleName] ?: return false
        return rule.on
    }

    override fun applyRule(
        string: String,
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
    ): String {
        val rule = formatterRuleMap["line-breaks-after-println"] ?: return string
        val requiredNewlines = rule.quantity

        // Direct handling for CallableCall statements
        if (statement is Callable && statement.functionName.value == "println") {
            val semicolonIndex = string.indexOf(';')
            if (semicolonIndex != -1) {
                // Count existing newlines after semicolon
                var afterIndex = semicolonIndex + 1
                while (afterIndex < string.length && string[afterIndex] == '\n') {
                    afterIndex++
                }

                // Add exactly the required number of newlines
                val prefix = string.substring(0, semicolonIndex + 1)
                val suffix = if (afterIndex < string.length) string.substring(afterIndex) else ""
                return prefix + "\n".repeat(requiredNewlines) + suffix
            }
        }

        // For other cases or multiple println statements in a string
        val pattern = "(println\\s*\\([^;]*;)(\\n*)".toRegex()

        return pattern.replace(string) { matchResult ->
            val printlnStmt = matchResult.groupValues[1] // The println statement with semicolon
            "$printlnStmt${"\n".repeat(requiredNewlines)}"
        }
    }
}
