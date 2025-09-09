package org.gudelker.analyzer

import org.gudelker.DefaultFormatter
import org.gudelker.expressions.LiteralString
import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement

class LiteralStringForAnalyzer : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is LiteralString
    }

    override fun format(
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
        formatter: DefaultFormatter,
    ): String {
        return "\"${(statement as LiteralString).value}\""
    }
}
