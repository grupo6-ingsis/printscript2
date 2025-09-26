package org.gudelker.rulevalidator

import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement

class IfBraceBelowLine : RuleValidatorFormatter {
    override fun matches(formatterRuleMap: Map<String, FormatterRule>) = formatterRuleMap["if-brace-below-line"]?.on == true

    override fun applyRule(
        string: String,
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
    ): String {
        val pattern = Regex("if \\([^)]+\\)([ \\t]*)\\{")
        return pattern.replace(string) { match ->
            if (match.value.contains("\n{")) {
                match.value
            } else {
                match.value.replace(Regex("\\)([ \\t]*)\\{"), ")\n{")
            }
        }
    }
}
