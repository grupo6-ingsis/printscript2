package org.gudelker.analyzer

import org.gudelker.DefaultFormatter
import org.gudelker.LiteralIdentifier
import org.gudelker.Statement
import org.gudelker.rules.Rule

class LiteralIdentifierAnalyzer : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is LiteralIdentifier
    }

    override fun format(
        statement: Statement,
        ruleMap: Map<String, Rule>,
        formatter: DefaultFormatter,
    ): String {
        return (statement as LiteralIdentifier).value
    }
}
