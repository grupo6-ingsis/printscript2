package org.gudelker.rulevalidator

import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement

class SpacesPrintln : RuleValidatorFormatter {
    override fun matches(formatterRuleMap: Map<String, FormatterRule>): Boolean {
        val ruleName = "line-breaks-after-println"
        val rule = formatterRuleMap[ruleName] ?: return false
        return formatterRuleMap.containsKey(ruleName) && rule.on
    }

    override fun applyRule(
        string: String,
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
    ): String {
        val requiredNewlines = formatterRuleMap["line-breaks-after-println"]?.quantity ?: 1

        // Find println statements and check existing newlines after them
        val pattern = "(println\\([^;]*;)(\\s*)".toRegex()

        return pattern.replace(string) { matchResult ->
            val printlnStmt = matchResult.groupValues[1] // The println statement including semicolon
            val existingWhitespace = matchResult.groupValues[2] // Any existing whitespace after the println

            val existingNewlines = existingWhitespace.count { it == '\n' }

            if (existingNewlines >= requiredNewlines) {
                matchResult.value // Keep original if newlines are already sufficient
            } else {
                val newLineString = "\n".repeat(requiredNewlines - existingNewlines)
                "$printlnStmt$existingWhitespace$newLineString" // Add only the additional newlines needed
            }
        }
    }
}
