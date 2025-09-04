package org.gudelker.comparator

class LesserEquals(
    private val value: String = "<=",
) : Comparator {
    override fun getValue(): String {
        return value
    }
}
