package org.gudelker.comparator

class Greater(
    private val value: String = ">",
) : Comparator {
    override fun getValue(): String {
        return value
    }
}
