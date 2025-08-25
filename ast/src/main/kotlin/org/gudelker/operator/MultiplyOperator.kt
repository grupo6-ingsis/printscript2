package org.gudelker.operator

class MultiplyOperator(private val value: String = "*") : Operator {
    override fun getValue(): String {
        return value
    }
}
