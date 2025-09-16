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
            left is Number && right is Number ->
                Result.success(
                    if (isDouble(left, right)) {
                        left.toDouble() + right.toDouble()
                    } else {
                        left.toInt() + right.toInt()
                    },
                )
            left is Number && right is String -> Result.success(left.toInt().toString() + right)
            left is String && right is Number -> Result.success(left + right.toInt().toString())
            left is String || right is String -> Result.success(left.toString() + right.toString())
            else -> Result.failure(IllegalArgumentException("Tipos incompatibles para suma"))
        }
    }

    override fun performUnaryOperation(value: Any?): Result<Any> {
        return when (value) {
            is Double -> Result.success(+value)
            is Int -> Result.success(+value)
            else -> Result.failure(IllegalArgumentException("Tipo incompatible para operador unario +"))
        }
    }

    private fun isDouble(
        left: Number,
        right: Number,
    ): Boolean {
        return (left is Double || right is Double)
    }
}
