package org.gudelker.result

import org.gudelker.statements.interfaces.Statement

data class Valid(
    private val statements: List<Statement>,
) : ParserResult {
    fun getStatements(): List<Statement> = statements
}
