package org.gudelker.analyzer

import org.gudelker.expressions.Unary
import org.gudelker.formatter.DefaultFormatter
import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement

class UnaryForAnalyzer : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is Unary
    }

    override fun format(
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
        formatter: DefaultFormatter,
    ): String {
        if (statement !is Unary) {
            return ""
        }
        val operator = getOperator(statement)
        val expression = getExpression(statement)

        val formattedExpression = formatter.format(expression, formatterRuleMap)

        return "${operator.value.getValue()}$formattedExpression"
    }

    private fun getExpression(statement: Unary) = statement.value

    private fun getOperator(statement: Unary) = statement.operator
}
