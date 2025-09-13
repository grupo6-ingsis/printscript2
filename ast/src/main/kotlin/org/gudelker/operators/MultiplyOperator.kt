package org.gudelker.operators

class MultiplyOperator(private val value: String = "*") : BinaryOperator {
    override fun getValue(): String {
        return value
    }

    override fun performBinaryOperation(
        left: Any?,
        right: Any?,
    ): Result<Any> {
        return when {
            left is Number && right is Number -> Result.success(left.toDouble() * right.toDouble())
            else -> Result.failure(IllegalArgumentException("Tipos incompatibles para multiplicación"))
        }
    }
}
