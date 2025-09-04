package org.gudelker.analyzer

import org.gudelker.DefaultFormatter
import org.gudelker.Statement
import org.gudelker.rules.FormatterRule

interface Analyzer {
    fun canHandle(statement: Statement): Boolean

    fun format(
        statement: Statement,
        formatterRuleMap: Map<String, FormatterRule>,
        formatter: DefaultFormatter,
    ): String
}
