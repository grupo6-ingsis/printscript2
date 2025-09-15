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
        val booleanExpression = statement as BooleanExpression
        val left = booleanExpression.left
        val operator = booleanExpression.comparator
        val right = booleanExpression.right
        val formattedLeft = formatter.format(left, formatterRuleMap)
        val formattedRight = formatter.format(right, formatterRuleMap)

        return "$formattedLeft ${operator.getValue()} $formattedRight"
    }
}
