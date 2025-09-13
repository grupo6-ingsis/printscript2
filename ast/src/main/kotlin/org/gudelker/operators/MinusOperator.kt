package org.gudelker.operators

class MinusOperator(
    private val value: String = "-",
) : BinaryOperator, UnaryOperator {
    override fun getValue() = value

    override fun performBinaryOperation(
        left: Any?,
        right: Any?,
    ): Result<Any> {
        return when {
            left is Number && right is Number -> Result.success(left.toDouble() - right.toDouble())
            else -> Result.failure(IllegalArgumentException("Tipos incompatibles para resta"))
        }
    }

    override fun performUnaryOperation(value: Any?): Result<Any> {
        return when (value) {
            is Number -> Result.success(-value.toDouble())
            else -> Result.failure(IllegalArgumentException("Tipo incompatible para operador unario -"))
        }
    }
}
