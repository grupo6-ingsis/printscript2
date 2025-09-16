package org.gudelker.rulevalidator

import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement

class IfBraceSameLine : RuleValidatorFormatter {
    override fun matches(formatterRuleMap: Map<String, FormatterRule>) = formatterRuleMap["if-brace-same-line"]?.on == true

    override fun applyRule(
        string: String,
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
    ): String {
        val pattern = Regex("""(if\s*\([^)]+\))[ \t]*\n[ \t]*\{""")
        return pattern.replace(string) { matchResult ->
            "${matchResult.groupValues[1]} {"
        }
    }
}
