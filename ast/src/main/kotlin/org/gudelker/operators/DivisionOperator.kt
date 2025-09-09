package org.gudelker.operators

class DivisionOperator(
    private val value: String = "/",
) : Operator {
    override fun getValue() = value
}
