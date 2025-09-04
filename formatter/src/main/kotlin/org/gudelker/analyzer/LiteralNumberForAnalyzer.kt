package org.gudelker.analyzer

import org.gudelker.DefaultFormatter
import org.gudelker.LiteralNumber
import org.gudelker.Statement
import org.gudelker.rules.FormatterRule

class LiteralNumberForAnalyzer : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is LiteralNumber
    }

    override fun format(
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
        formatter: DefaultFormatter,
    ): String {
        return (statement as LiteralNumber).value.toString()
    }
}
