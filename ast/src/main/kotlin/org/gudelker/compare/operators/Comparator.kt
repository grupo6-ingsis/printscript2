package org.gudelker.compare.operators

sealed interface Comparator {
    fun getValue(): String
}
