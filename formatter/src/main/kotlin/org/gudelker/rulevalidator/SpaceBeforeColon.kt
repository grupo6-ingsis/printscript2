package org.gudelker.rulevalidator

import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement
import org.gudelker.utils.FormatterUtils

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
        val spacesBefore = FormatterUtils.getDeclarationSpaces("enforce-spacing-before-colon-in-declaration", formatterRuleMap)
        return string.replace(":", "$spacesBefore:")
    }
}
