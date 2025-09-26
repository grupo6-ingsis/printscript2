package org.gudelker.analyzer

import org.gudelker.expressions.LiteralBoolean
import org.gudelker.formatter.DefaultFormatter
import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement

class LiteralBooleanForAnalyzer : Analyzer {
    override fun canHandle(statement: Statement): Boolean {
        return statement is LiteralBoolean
    }

    override fun format(
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
        formatter: DefaultFormatter,
    ): String {
        if (statement !is LiteralBoolean) {
            return ""
        }

        return statement.value.toString()
    }
}
