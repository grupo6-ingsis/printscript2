package org.gudelker.parser.result

import org.gudelker.statements.interfaces.Statement

data class Valid(
    private val statements: List<Statement>,
) : ParserResult {
    fun getStatements(): List<Statement> = statements
}
