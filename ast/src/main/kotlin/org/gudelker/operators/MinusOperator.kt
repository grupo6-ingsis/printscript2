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
            left is Number && right is Number ->
                Result.success(
                    if (isDouble(left, right)) {
                        left.toDouble() - right.toDouble()
                    } else {
                        left.toInt() - right.toInt()
                    },
                )
            else -> Result.failure(IllegalArgumentException("Tipos incompatibles para resta"))
        }
    }

    override fun performUnaryOperation(value: Any?): Result<Any> {
        return when (value) {
            is Double -> Result.success(-value)
            is Int -> Result.success(-value)
            else -> Result.failure(IllegalArgumentException("Tipo incompatible para operador unario -"))
        }
    }

    private fun isDouble(
        left: Number,
        right: Number,
    ): Boolean {
        return (left is Double || right is Double)
    }
}
