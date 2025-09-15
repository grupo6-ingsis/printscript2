package org.gudelker.analyzer

import org.gudelker.formatter.DefaultFormatter
import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement

interface Analyzer {
    fun canHandle(statement: Statement): Boolean

    fun format(
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
        formatter: DefaultFormatter,
    ): String
}
