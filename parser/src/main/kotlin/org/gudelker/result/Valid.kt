package org.gudelker.result

import org.example.org.gudelker.Statement

class Valid(private val statements: List<Statement>) : Result {
    fun getStatements(): List<Statement> {
        return statements
    }
}
