package org.gudelker

import org.gudelker.rules.Rule

interface Formatter {
    fun format(
        statement: Statement,
        rules: Map<String, Rule>,
    ): String
}
