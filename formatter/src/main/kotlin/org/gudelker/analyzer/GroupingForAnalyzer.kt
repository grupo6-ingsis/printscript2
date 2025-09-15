package org.gudelker.analyzer

import org.gudelker.expressions.Grouping
import org.gudelker.formatter.DefaultFormatter
import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement

class GroupingForAnalyzer : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is Grouping
    }

    override fun format(
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
        formatter: DefaultFormatter,
    ): String {
        val grouping = statement as Grouping
        val leftParen = grouping.openParenthesis
        val expression = grouping.expression
        val rightParen = grouping.closingParenthesis
        val formattedExpression = expression?.let { formatter.formatNode(it, formatterRuleMap) }
        return "$leftParen$formattedExpression$rightParen"
    }
}
