package org.gudelker.rulevalidator

import org.gudelker.Statement
import org.gudelker.rules.FormatterRule
import org.gudelker.utils.FormatterUtils

class SpacesAroundAssignation : RuleValidatorFormatter {
    override fun matches(formatterRuleMap: Map<String, FormatterRule>): Boolean {
        val ruleName = "assignDeclaration"
        val rule = formatterRuleMap[ruleName] ?: return false
        return formatterRuleMap.containsKey(ruleName) && rule.on
    }

    override fun applyRule(
        string: String,
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
    ): String {
        val assignSpaces = FormatterUtils.getAssignationSpaces("assignDeclaration", formatterRuleMap)
        return string.replace("=", "$assignSpaces=$assignSpaces")
    }
}
