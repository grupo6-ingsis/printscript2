package org.gudelker.rulevalidator

import org.gudelker.rules.FormatterRule
import org.gudelker.statements.declarations.ConstDeclaration
import org.gudelker.statements.declarations.VariableDeclaration
import org.gudelker.statements.interfaces.Statement

class NormalizeDeclarationIndentation(
    private val declarationKeywords: Set<String> = setOf("let", "const"),
) : RuleValidatorFormatter {
    override fun matches(formatterRuleMap: Map<String, FormatterRule>): Boolean {
        val ruleName = "mandatory-line-break-after-statement"
        val rule = formatterRuleMap[ruleName] ?: return false
        return rule.on
    }

    override fun applyRule(
        string: String,
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
    ): String {
        if (statement !is VariableDeclaration && statement !is ConstDeclaration) {
            return string
        }
        val lines = string.split("\n")
        return lines.joinToString("\n") { line ->
            val trimmed = line.trim()
            if (declarationKeywords.any { keyword -> trimmed.startsWith(keyword) }) {
                trimmed
            } else {
                line
            }
        }
    }
}
