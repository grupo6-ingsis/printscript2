package org.gudelker.operators

class AdditionOperator(
    private val value: String = "+",
) : Operator {
    override fun getValue() = value
}
