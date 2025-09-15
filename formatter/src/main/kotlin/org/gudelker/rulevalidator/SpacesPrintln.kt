package org.gudelker.rulevalidator

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

        // Pattern to match println statements and capture any following newlines
        val pattern = "(println\\s*\\([^;]*;)(\\n*)".toRegex()

        return pattern.replace(string) { matchResult ->
            val printlnStmt = matchResult.groupValues[1] // The println statement
            val existingNewlines = matchResult.groupValues[2] // Existing newlines

            val existingCount = existingNewlines.length

            if (existingCount >= requiredNewlines) {
                // If we already have enough newlines, keep them
                "$printlnStmt$existingNewlines"
            } else {
                // If we don't have enough, add only what's missing
                "$printlnStmt$existingNewlines${"\n".repeat(requiredNewlines - existingCount)}"
            }
        }
    }
}
