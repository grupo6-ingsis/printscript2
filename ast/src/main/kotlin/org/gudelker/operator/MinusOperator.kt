package org.gudelker.operator

class MinusOperator(
    private val value: String = "-",
) : Operator {
    override fun getValue() = value
}
