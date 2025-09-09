package org.gudelker.operators

class MultiplyOperator(private val value: String = "*") : Operator {
    override fun getValue(): String {
        return value
    }
}
