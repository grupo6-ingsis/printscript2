package org.gudelker.rulevalidator

import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement

class SpacesAroundAssignation : RuleValidatorFormatter {
    override fun matches(formatterRuleMap: Map<String, FormatterRule>): Boolean {
        val ruleName = "enforce-spacing-around-equals"
        val rule = formatterRuleMap[ruleName] ?: return false
        return formatterRuleMap.containsKey(ruleName) && rule.on
    }

    override fun applyRule(
        string: String,
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
    ): String {
        val requiredSpaces = formatterRuleMap["enforce-spacing-around-equals"]?.quantity ?: 1
        val spaceString = " ".repeat(requiredSpaces)
        var result = string
        val patternBefore = "([^ ])( *)=".toRegex()
        result =
            patternBefore.replace(result) { matchResult ->
                val char = matchResult.groupValues[1]
                "$char$spaceString="
            }
        val patternAfter = "=( *)([^ ])".toRegex()
        result =
            patternAfter.replace(result) { matchResult ->
                val char = matchResult.groupValues[2]
                "=$spaceString$char"
            }

        return result
    }
}
