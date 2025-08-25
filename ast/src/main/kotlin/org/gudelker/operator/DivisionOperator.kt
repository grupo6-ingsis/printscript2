package org.gudelker.operator

class DivisionOperator(
    private val value: String = "/",
) : Operator {
    override fun getValue() = value
}
