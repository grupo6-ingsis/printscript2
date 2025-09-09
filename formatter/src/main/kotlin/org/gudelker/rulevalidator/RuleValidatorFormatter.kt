package org.gudelker.rulevalidator

import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement

interface RuleValidatorFormatter {
    fun matches(formatterRuleMap: Map<String, FormatterRule>): Boolean

    fun applyRule(
        string: String,
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
    ): String
}
