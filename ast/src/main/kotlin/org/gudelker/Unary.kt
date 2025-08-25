package org.gudelker

import org.gudelker.operator.Operator

data class Unary(
    val value: ExpressionStatement,
    val operator: Operator,
) : ExpressionStatement
