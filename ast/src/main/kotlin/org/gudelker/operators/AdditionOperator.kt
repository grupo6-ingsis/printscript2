package org.gudelker.operators

class AdditionOperator(
    private val value: String = "+",
) : BinaryOperator, UnaryOperator {
    override fun getValue() = value

    override fun performBinaryOperation(
        left: Any?,
        right: Any?,
    ): Result<Any> {
        return when {
            left is Number && right is Number -> Result.success(left.toDouble() + right.toDouble())
            left is String || right is String -> Result.success(left.toString() + right.toString())
            else -> Result.failure(IllegalArgumentException("Tipos incompatibles para suma"))
        }
    }

    override fun performUnaryOperation(value: Any?): Result<Any> {
        return when (value) {
            is Number -> Result.success(+value.toDouble())
            else -> Result.failure(IllegalArgumentException("Tipo incompatible para operador unario +"))
        }
    }
}
