package org.gudelker.result

import org.example.org.gudelker.Statement

class ValidStatementResult(
    private val statement: Statement,
) : Result {
    fun getStatement(): Statement = statement
}
