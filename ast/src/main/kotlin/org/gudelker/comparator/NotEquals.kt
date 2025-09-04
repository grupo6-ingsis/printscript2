package org.gudelker.comparator

class NotEquals(
    private val value: String = "!=",
) : Comparator {
    override fun getValue(): String {
        return value
    }
}
