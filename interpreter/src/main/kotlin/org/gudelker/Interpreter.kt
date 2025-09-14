package org.gudelker

import org.gudelker.statements.interfaces.Statement

interface Interpreter {
    fun interpret(statements: List<Statement>): Result<List<Any?>>
}
