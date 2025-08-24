package org.example.org.gudelker.operator

import org.gudelker.operator.Operator

class MultiplyOperator(private val value: String = "*") : Operator {
    override fun getValue(): String {
        return value
    }
}
