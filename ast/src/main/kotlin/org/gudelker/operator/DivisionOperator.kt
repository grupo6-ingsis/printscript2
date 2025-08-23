package org.example.org.gudelker.operator

import org.gudelker.operator.Operator

class DivisionOperator(
    private val value: String = "/",
) : Operator {
    override fun getValue() = value
}
