package org.gudelker.analyzer

import org.gudelker.expressions.Binary
import org.gudelker.formatter.DefaultFormatter
import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement

class BinaryForAnalyzer : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is Binary
    }

    override fun format(
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
        formatter: DefaultFormatter,
    ): String {
        if (statement !is Binary) {
            return ""
        }
        val left = getLeft(statement)
        val operator = getOperator(statement)
        val right = getRight(statement)

        val formattedLeft = formatter.format(left, formatterRuleMap)
        val formattedRight = formatter.format(right, formatterRuleMap)

        return "$formattedLeft ${operator.value.getValue()} $formattedRight"
    }

    private fun getRight(statement: Binary) = statement.rightExpression

    private fun getOperator(statement: Binary) = statement.operator

    private fun getLeft(statement: Binary) = statement.leftExpression
}
