package org.gudelker.comparator

class GreaterEquals(
    private val value: String = ">=",
) : Comparator {
    override fun getValue(): String {
        return value
    }
}
