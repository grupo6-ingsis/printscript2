package org.gudelker.compare.operators

class Lesser(
    private val value: String = "<",
) : Comparator {
    override fun getValue(): String {
        return value
    }
}
