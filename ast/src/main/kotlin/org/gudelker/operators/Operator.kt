package org.gudelker.operators

sealed interface Operator {
    fun getValue(): String
}
