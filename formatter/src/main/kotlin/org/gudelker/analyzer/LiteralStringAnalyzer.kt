package org.gudelker.analyzer

import org.gudelker.DefaultFormatter
import org.gudelker.LiteralString
import org.gudelker.Statement
import org.gudelker.rules.Rule

class LiteralStringAnalyzer : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is LiteralString
    }

    override fun format(
        statement: Statement,
        ruleMap: Map<String, Rule>,
        formatter: DefaultFormatter,
    ): String {
        return (statement as LiteralString).value
    }
}
