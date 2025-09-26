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
        if (statement !is Grouping) {
            return ""
        }
        val leftParen = getLeftParen(statement)
        val expression = getExpression(statement)
        val rightParen = getRightParen(statement)

        val formattedExpression = expression?.let { formatter.formatNode(it, formatterRuleMap) }

        return "$leftParen$formattedExpression$rightParen"
    }

    private fun getRightParen(statement: Grouping) = statement.closingParenthesis

    private fun getExpression(statement: Grouping) = statement.expression

    private fun getLeftParen(statement: Grouping) = statement.openParenthesis
}
