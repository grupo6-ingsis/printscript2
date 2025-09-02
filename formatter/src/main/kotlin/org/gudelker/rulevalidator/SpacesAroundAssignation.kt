package org.gudelker.rulevalidator

import org.gudelker.Statement
import org.gudelker.rules.Rule
import org.gudelker.utils.FormatterUtils

class SpacesAroundAssignation : RuleValidatorFormatter {
    override fun matches(ruleMap: Map<String, Rule>): Boolean {
        val ruleName = "assignDeclaration"
        val rule = ruleMap[ruleName] ?: return false
        return ruleMap.containsKey(ruleName) && rule.on
    }

    override fun applyRule(
        string: String,
        statement: Statement,
        ruleMap: Map<String, Rule>,
    ): String {
        val assignSpaces = FormatterUtils.getAssignationSpaces("assignDeclaration", ruleMap)
        return string.replace("=", "$assignSpaces=$assignSpaces")
    }
}
