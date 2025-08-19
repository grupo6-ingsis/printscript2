package org.gudelker.operator

class AdditionOperator(private val value: String) : Operator {
    override fun getValue() = value
}
