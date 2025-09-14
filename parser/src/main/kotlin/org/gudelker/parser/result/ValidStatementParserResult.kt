package org.gudelker.parser.result

import org.gudelker.statements.interfaces.Statement

data class ValidStatementParserResult(
    private val statement: Statement,
) : ParserResult {
    fun getStatement(): Statement = statement
}
