package org.gudelker.rulevalidator

import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement

class LineBreakAfterStatement : RuleValidatorFormatter {
    override fun matches(formatterRuleMap: Map<String, FormatterRule>): Boolean {
        val ruleName = "mandatory-line-break-after-statement"
        val rule = formatterRuleMap[ruleName] ?: return false
        return formatterRuleMap.containsKey(ruleName) && rule.on
    }

    override fun applyRule(
        string: String,
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
    ): String {
        // Ensure each statement ends with the correct number of newlines
        val rule = formatterRuleMap["mandatory-line-break-after-statement"] ?: return string
        val lineBreaks = "\n".repeat(rule.quantity)

        // Replace single newlines with the correct number
        return if (string.endsWith("\n")) {
            string.substring(0, string.length - 1) + lineBreaks
        } else {
            string + lineBreaks
        }
    }
}
