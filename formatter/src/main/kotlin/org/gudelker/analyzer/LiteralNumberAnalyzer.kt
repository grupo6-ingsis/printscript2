package org.gudelker.analyzer

import org.gudelker.DefaultFormatter
import org.gudelker.LiteralNumber
import org.gudelker.Statement
import org.gudelker.rules.Rule

class LiteralNumberAnalyzer : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is LiteralNumber
    }

    override fun format(
        statement: Statement,
        ruleMap: Map<String, Rule>,
        formatter: DefaultFormatter,
    ): String {
        return (statement as LiteralNumber).value.toString()
    }
}
