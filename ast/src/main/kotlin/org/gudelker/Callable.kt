package org.gudelker

class Callable(
    val functionName: String,
    val expression: ExpressionStatement,
) : CallableStatement {
    override fun toString(): String {
        return "Callable($functionName($expression))"
    }
}
