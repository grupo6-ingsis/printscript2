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
            left is Number && right is Number ->
                Result.success(
                    if (isDouble(left, right)) {
                        left.toDouble() * right.toDouble()
                    } else {
                        left.toInt() * right.toInt()
                    },
                )
            else -> Result.failure(IllegalArgumentException("Tipos incompatibles para multiplicación"))
        }
    }

    private fun isDouble(
        left: Number,
        right: Number,
    ): Boolean {
        return (left is Double || right is Double)
    }
}
