package org.gudelker.interpreter

import org.gudelker.result.InterpreterResult
import org.gudelker.statements.interfaces.Statement

interface SecondInterpreter {
    fun interpret(statements: List<Statement>): InterpreterResult
}
