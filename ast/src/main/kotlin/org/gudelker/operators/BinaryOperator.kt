package org.gudelker.operators

interface BinaryOperator : Operator {
    fun performBinaryOperation(
        left: Any?,
        right: Any?,
    ): Result<Any>
}
