package org.gudelker.analyzer

import org.gudelker.expressions.BooleanExpression
import org.gudelker.formatter.DefaultFormatter
import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement

class BooleanExprForAnalyzer : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is BooleanExpression
    }

    override fun format(
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
        formatter: DefaultFormatter,
    ): String {
        if (statement !is BooleanExpression) {
            return ""
        }
        val left = getLeft(statement)
        val operator = getOperator(statement)
        val right = getRight(statement)

        val formattedLeft = formatter.format(left, formatterRuleMap)
        val formattedRight = formatter.format(right, formatterRuleMap)

        return "$formattedLeft ${operator.getValue()} $formattedRight"
    }

    private fun getRight(statement: BooleanExpression) = statement.right

    private fun getOperator(statement: BooleanExpression) = statement.comparator

    private fun getLeft(statement: BooleanExpression) = statement.left
}
