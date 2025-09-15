package org.gudelker.rulevalidator

import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement

class SpacesAroundAssignation : RuleValidatorFormatter {
    override fun matches(formatterRuleMap: Map<String, FormatterRule>): Boolean {
        // Check for either rule
        return formatterRuleMap.containsKey("enforce-spacing-around-equals") ||
            formatterRuleMap.containsKey("enforce-no-spacing-around-equals")
    }

    override fun applyRule(
        string: String,
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
    ): String {
        // Check if no-spacing rule is enabled
        if (formatterRuleMap["enforce-no-spacing-around-equals"]?.on == true) {
            var result = string

            // More precise pattern to handle spaces around equals sign
            // This pattern looks for a non-whitespace character, followed by spaces,
            // followed by an equals sign, optionally followed by spaces, followed by a non-whitespace character
            val pattern = "([^\\s])(\\s+)=(\\s*)([^\\s])".toRegex()

            result =
                pattern.replace(result) { matchResult ->
                    val before = matchResult.groupValues[1]
                    val after = matchResult.groupValues[4]
                    "$before=$after"
                }

            return result
        }

        // Original logic for adding spaces
        val rule = formatterRuleMap["enforce-spacing-around-equals"] ?: return string
        if (!rule.on) return string

        val requiredSpaces = rule.quantity
        val spaceString = " ".repeat(requiredSpaces)
        var result = string

        // Only apply if the required spacing doesn't already exist
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
