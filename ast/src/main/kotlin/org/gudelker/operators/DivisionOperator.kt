package org.gudelker.operators

class DivisionOperator(
    private val value: String = "/",
) : BinaryOperator {
    override fun getValue() = value

    override fun performBinaryOperation(
        left: Any?,
        right: Any?,
    ): Result<Any> {
        return when {
            left is Number && right is Number -> {
                if (right.toDouble() == 0.0) {
                    Result.failure(ArithmeticException("División por cero"))
                } else {
                    Result.success(left.toDouble() / right.toDouble())
                }
            }
            else -> Result.failure(IllegalArgumentException("Tipos incompatibles para división"))
        }
    }
}
