package org.gudelker.rulevalidator

import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement

class SpaceBeforeColon : RuleValidatorFormatter {
    override fun matches(formatterRuleMap: Map<String, FormatterRule>): Boolean {
        val ruleName = "enforce-spacing-before-colon-in-declaration"
        val rule = formatterRuleMap[ruleName] ?: return false
        return formatterRuleMap.containsKey(ruleName) && rule.on
    }

    override fun applyRule(
        string: String,
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
    ): String {
        val requiredSpaces = formatterRuleMap["enforce-spacing-before-colon-in-declaration"]?.quantity ?: 0
        val spaceString = " ".repeat(requiredSpaces)
        val pattern = "([^ ])( *):".toRegex()
        return pattern.replace(string) { matchResult ->
            val char = matchResult.groupValues[1]
            val currentSpaces = matchResult.groupValues[2]
            if (currentSpaces.length == requiredSpaces) {
                matchResult.value
            } else {
                "$char$spaceString:"
            }
        }
    }
}
