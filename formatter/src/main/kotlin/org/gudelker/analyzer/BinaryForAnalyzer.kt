package org.gudelker.analyzer

import org.gudelker.DefaultFormatter
import org.gudelker.expressions.Binary
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
        val binary = statement as Binary
        val left = binary.leftExpression
        val operator = binary.operator
        val right = binary.rightExpression

        val formattedLeft = formatter.format(left, formatterRuleMap)
        val formattedRight = formatter.format(right, formatterRuleMap)

        return "$formattedLeft ${operator.value.getValue()} $formattedRight"
    }
}
