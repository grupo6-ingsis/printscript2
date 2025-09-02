package org.gudelker.rulevalidator

import org.gudelker.Statement
import org.gudelker.rules.Rule

interface RuleValidatorFormatter {
    fun matches(ruleMap: Map<String, Rule>): Boolean

    fun applyRule(
        string: String,
        statement: Statement,
        ruleMap: Map<String, Rule>,
    ): String
}
