package org.gudelker.rulevalidator

import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement

class SpacesAroundAssignation : RuleValidatorFormatter {
    override fun matches(formatterRuleMap: Map<String, FormatterRule>): Boolean {
        return formatterRuleMap.containsKey("enforce-spacing-around-equals") ||
            formatterRuleMap.containsKey("enforce-no-spacing-around-equals")
    }

    override fun applyRule(
        string: String,
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
    ): String {
        if (formatterRuleMap["enforce-no-spacing-around-equals"]?.on == true) {
            val pattern = "\\s*=\\s*".toRegex()
            return pattern.replace(string) { "=" }
        }

        val rule = formatterRuleMap["enforce-spacing-around-equals"] ?: return string
        if (!rule.on) return string

        val requiredSpaces = rule.quantity
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
