package org.gudelker

import org.gudelker.rules.FormatterRule
import org.gudelker.statements.interfaces.Statement

interface Formatter {
    fun format(
        statement: Statement,
        rules: Map<String, FormatterRule>,
    ): String
}
