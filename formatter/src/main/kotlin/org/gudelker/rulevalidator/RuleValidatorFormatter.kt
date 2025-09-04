package org.gudelker.rulevalidator

import org.gudelker.Statement
import org.gudelker.rules.FormatterRule

interface RuleValidatorFormatter {
    fun matches(formatterRuleMap: Map<String, FormatterRule>): Boolean

    fun applyRule(
        string: String,
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
    ): String
}
