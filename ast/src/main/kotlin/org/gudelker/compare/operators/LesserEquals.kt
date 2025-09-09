package org.gudelker.compare.operators

class LesserEquals(
    private val value: String = "<=",
) : Comparator {
    override fun getValue(): String {
        return value
    }
}
