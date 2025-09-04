package org.gudelker.comparator

class Equals(
    private val value: String = "==",
) : Comparator {
    override fun getValue(): String {
        return value
    }
}
