package org.gudelker.analyzer

import org.gudelker.DefaultFormatter
import org.gudelker.LiteralString
import org.gudelker.Statement
import org.gudelker.rules.FormatterRule

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
