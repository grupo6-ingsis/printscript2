package org.gudelker.analyzer

import org.gudelker.BooleanExpression
import org.gudelker.DefaultFormatter
import org.gudelker.Statement
import org.gudelker.rules.FormatterRule

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
