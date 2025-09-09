package org.gudelker.compare.operators

class Greater(
    private val value: String = ">",
) : Comparator {
    override fun getValue(): String {
        return value
    }
}
