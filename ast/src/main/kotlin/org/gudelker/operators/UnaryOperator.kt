package org.gudelker.operators

interface UnaryOperator : Operator {
    fun performUnaryOperation(value: Any?): Result<Any>
}
