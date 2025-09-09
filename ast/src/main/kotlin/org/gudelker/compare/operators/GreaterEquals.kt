package org.gudelker.compare.operators

class GreaterEquals(
    private val value: String = ">=",
) : Comparator {
    override fun getValue(): String {
        return value
    }
}
