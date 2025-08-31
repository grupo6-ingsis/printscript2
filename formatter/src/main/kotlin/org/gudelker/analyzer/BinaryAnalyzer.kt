package org.gudelker.analyzer

import org.gudelker.Binary
import org.gudelker.DefaultFormatter
import org.gudelker.Statement
import org.gudelker.rules.Rule

class BinaryAnalyzer : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is Binary
    }

    override fun format(
        statement: Statement,
        ruleMap: Map<String, Rule>,
        formatter: DefaultFormatter,
    ): String {
        val binary = statement as Binary
        val left = binary.leftExpression
        val operator = binary.operator
        val right = binary.rightExpression

        val formattedLeft = formatter.format(left, ruleMap)
        val formattedRight = formatter.format(right, ruleMap)

        return "$formattedLeft $operator $formattedRight"
    }
}
