package org.gudelker.analyzer

import org.gudelker.DefaultFormatter
import org.gudelker.Statement
import org.gudelker.Unary
import org.gudelker.rules.FormatterRule

class UnaryForAnalyzer : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is Unary
    }

    override fun format(
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
        formatter: DefaultFormatter,
    ): String {
        val unary = statement as Unary
        val operator = unary.operator
        val expression = unary.value
        val formattedExpression = formatter.format(expression, formatterRuleMap)
        return "${operator.getValue()}$formattedExpression"
    }
}
