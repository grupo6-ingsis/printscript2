package org.gudelker.result

import org.example.org.gudelker.Statement

class ValidStatementResult(
    private val statement: Statement,
    private val index: Int
): Result {
    fun getStatement(): Statement {
           return statement
    }

    fun getIndex(): Int {
        return index
    }
}