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
                    if (isDouble(left, right)) {
                        Result.success(left.toDouble() / right.toDouble())
                    } else {
                        val result = left.toDouble() / right.toDouble()
                        if (result == result.toInt().toDouble()) {
                            Result.success(result.toInt())
                        } else {
                            Result.success(result)
                        }
                    }
                }
            }
            else -> Result.failure(IllegalArgumentException("Tipos incompatibles para división"))
        }
    }

    private fun isDouble(
        left: Number,
        right: Number,
    ): Boolean {
        return (left is Double || right is Double)
    }
}
