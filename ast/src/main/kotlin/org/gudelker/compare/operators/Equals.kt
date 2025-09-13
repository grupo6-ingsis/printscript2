package org.gudelker.compare.operators

class Equals(
    private val value: String = "==",
) : Comparator {
    override fun getValue(): String {
        return value
    }

    override fun performBinaryComparator(
        left: Any?,
        right: Any?,
    ): Result<Boolean> {
        return Result.success(left == right)
    }
}
