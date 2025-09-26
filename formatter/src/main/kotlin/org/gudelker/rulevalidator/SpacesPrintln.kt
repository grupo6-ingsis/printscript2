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

        if (statement is Callable && statement.functionName.value == "println") {
            val semicolonIndex = string.lastIndexOf(';')
            if (semicolonIndex != -1 && semicolonIndex < string.length) {
                val prefix = string.substring(0, semicolonIndex + 1)

                var restIndex = semicolonIndex + 1
                while (restIndex < string.length && string[restIndex] == '\n') {
                    restIndex++
                }

                val suffix = if (restIndex < string.length) string.substring(restIndex) else ""

                return "\n".repeat(requiredNewlines) + prefix + suffix
            }
        }

        return string
    }
}
