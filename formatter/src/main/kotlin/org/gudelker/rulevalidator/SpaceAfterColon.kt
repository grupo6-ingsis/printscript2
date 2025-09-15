package org.gudelker.rulevalidator

import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement

class SpaceAfterColon : RuleValidatorFormatter {
    override fun matches(formatterRuleMap: Map<String, FormatterRule>): Boolean {
        val ruleName = "enforce-spacing-after-colon-in-declaration"
        val rule = formatterRuleMap[ruleName] ?: return false
        return formatterRuleMap.containsKey(ruleName) && rule.on
    }

    override fun applyRule(
        string: String,
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
    ): String {
        val requiredSpaces = formatterRuleMap["enforce-spacing-after-colon-in-declaration"]?.quantity ?: 1
        val spaceString = " ".repeat(requiredSpaces)
        val pattern = ":( *)([^ ])".toRegex()
        return pattern.replace(string) { matchResult ->
            val currentSpaces = matchResult.groupValues[1]
            val char = matchResult.groupValues[2]
            if (currentSpaces.length == requiredSpaces) {
                matchResult.value
            } else {
                ":$spaceString$char"
            }
        }
    }
}
