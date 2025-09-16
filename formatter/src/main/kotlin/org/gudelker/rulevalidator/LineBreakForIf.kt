package org.gudelker.rulevalidator

import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement

class LineBreakForIf : RuleValidatorFormatter {
    override fun matches(formatterRuleMap: Map<String, FormatterRule>): Boolean {
        val ruleName = "mandatory-line-break-for-if"
        val rule = formatterRuleMap[ruleName] ?: return false
        return formatterRuleMap.containsKey(ruleName) && rule.on
    }

    override fun applyRule(
        string: String,
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
    ): String {
        val rule = formatterRuleMap["mandatory-line-break-for-if"] ?: return string
        val lineBreaks = "\n".repeat(rule.quantity)
        val result =
            if (string.endsWith("\n")) {
                string.substring(0, string.length - 1) + lineBreaks
            } else {
                string + lineBreaks
            }
        return result
    }
}
