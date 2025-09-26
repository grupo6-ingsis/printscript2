package org.gudelker.analyzer

import org.gudelker.expressions.LiteralNumber
import org.gudelker.formatter.DefaultFormatter
import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement

class LiteralNumberForAnalyzer : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is LiteralNumber
    }

    override fun format(
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
        formatter: DefaultFormatter,
    ): String {
        if (statement !is LiteralNumber) {
            return ""
        }
        return statement.value.toString()
    }
}
