package org.gudelker.analyzer

import org.gudelker.DefaultFormatter
import org.gudelker.Grouping
import org.gudelker.Statement
import org.gudelker.rules.Rule

class GroupingAnalyzer : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is Grouping
    }

    override fun format(
        statement: Statement,
        ruleMap: Map<String, Rule>,
        formatter: DefaultFormatter,
    ): String {
        val grouping = statement as Grouping
        val leftParen = grouping.openParenthesis
        val expression = grouping.expression
        val rightParen = grouping.closingParenthesis
        val formattedExpression = expression?.let { formatter.formatNode(it, ruleMap) }
        return "$leftParen$formattedExpression$rightParen"
    }
}
