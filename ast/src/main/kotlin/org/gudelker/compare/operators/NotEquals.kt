package org.gudelker.compare.operators

class NotEquals(
    private val value: String = "!=",
) : Comparator {
    override fun getValue(): String {
        return value
    }
}
