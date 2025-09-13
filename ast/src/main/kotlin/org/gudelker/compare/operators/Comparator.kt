package org.gudelker.compare.operators

sealed interface Comparator {
    fun getValue(): String

    fun performBinaryComparator(
        left: Any?,
        right: Any?,
    ): Result<Boolean>
}
