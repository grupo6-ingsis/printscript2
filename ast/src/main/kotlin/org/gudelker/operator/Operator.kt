package org.gudelker.operator

sealed interface Operator {
    fun getValue(): String
}
