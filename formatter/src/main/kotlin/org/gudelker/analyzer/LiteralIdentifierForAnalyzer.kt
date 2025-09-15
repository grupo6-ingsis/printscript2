package org.gudelker.analyzer

import org.gudelker.expressions.LiteralIdentifier
import org.gudelker.formatter.DefaultFormatter
import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement

class LiteralIdentifierForAnalyzer : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is LiteralIdentifier
    }

    override fun format(
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
        formatter: DefaultFormatter,
    ): String {
        return (statement as LiteralIdentifier).value.value
    }
}
