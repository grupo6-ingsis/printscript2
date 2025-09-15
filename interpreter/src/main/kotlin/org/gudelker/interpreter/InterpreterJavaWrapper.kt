// InterpreterJavaWrapper.kt
package org.gudelker.interpreter

import org.gudelker.statements.interfaces.Statement

object InterpreterJavaWrapper {
    @JvmStatic
    fun interpretFromJava(
        interpreter: DefaultInterpreter,
        statements: List<Statement>,
    ): Result<List<Any?>> {
        return interpreter.interpret(statements)
    }
}
