package org.gudelker.compare.operators

class Lesser(
    private val value: String = "<",
) : Comparator {
    override fun getValue(): String {
        return value
    }

    override fun performBinaryComparator(
        left: Any?,
        right: Any?,
    ): Result<Boolean> {
        return when {
            left is Number && right is Number -> Result.success(left.toDouble() < right.toDouble())
            else -> Result.failure(IllegalArgumentException("Tipos incompatibles para comparación >"))
        }
    }
}
