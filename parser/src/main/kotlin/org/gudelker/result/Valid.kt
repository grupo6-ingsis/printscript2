package org.gudelker.result

import org.gudelker.Statement

class Valid(
    private val statements: List<Statement>,
) : ParserResult {
    fun getStatements(): List<Statement> = statements
}
