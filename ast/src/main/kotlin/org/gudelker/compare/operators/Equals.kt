package org.gudelker.compare.operators

class Equals(
    private val value: String = "==",
) : Comparator {
    override fun getValue(): String {
        return value
    }
}
