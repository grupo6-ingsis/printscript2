package org.gudelker.expressions

import org.gudelker.statements.interfaces.CallableStatement
import org.gudelker.stmtposition.ComboValuePosition

class Callable(
    val functionName: ComboValuePosition<String>,
    val expression: CanBeCallStatement,
) : CallableStatement {
    override fun toString(): String {
        return "Callable($functionName($expression))"
    }
}
