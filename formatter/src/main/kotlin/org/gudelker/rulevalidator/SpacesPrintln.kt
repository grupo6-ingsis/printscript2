package org.gudelker.rulevalidator

import org.gudelker.Statement
import org.gudelker.rules.Rule
import org.gudelker.utils.FormatterUtils

class SpacesPrintln : RuleValidatorFormatter {
    override fun matches(ruleMap: Map<String, Rule>): Boolean {
        val ruleName = "println"
        val rule = ruleMap[ruleName] ?: return false
        return ruleMap.containsKey(ruleName) && rule.on
    }

    override fun applyRule(
        string: String,
        statement: Statement,
        ruleMap: Map<String, Rule>,
    ): String {
        val newLines = FormatterUtils.generateNewLines("println", ruleMap)
        return string.replace("println", "${newLines}println")
    }
}
