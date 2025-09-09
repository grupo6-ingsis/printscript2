package org.gudelker.analyzer

import org.gudelker.DefaultFormatter
import org.gudelker.expressions.LiteralBoolean
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
        val literalBoolean = statement as LiteralBoolean
        return literalBoolean.value.toString()
    }
}
