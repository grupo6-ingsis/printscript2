package org.gudelker

import org.gudelker.rules.FormatterRule

interface Formatter {
    fun format(
        statement: Statement,
        rules: Map<String, FormatterRule>,
    ): String
}
