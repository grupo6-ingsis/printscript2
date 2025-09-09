package org.gudelker.operators

class MinusOperator(
    private val value: String = "-",
) : Operator {
    override fun getValue() = value
}
