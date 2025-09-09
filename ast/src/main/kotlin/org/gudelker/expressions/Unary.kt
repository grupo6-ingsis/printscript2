package org.gudelker.expressions

import org.gudelker.operators.Operator

data class Unary(
    val value: ExpressionStatement,
    val operator: Operator,
) : ExpressionStatement
