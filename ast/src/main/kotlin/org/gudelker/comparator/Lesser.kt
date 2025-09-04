package org.gudelker.comparator

class Lesser(
    private val value: String = "<",
) : Comparator {
    override fun getValue(): String {
        return value
    }
}
