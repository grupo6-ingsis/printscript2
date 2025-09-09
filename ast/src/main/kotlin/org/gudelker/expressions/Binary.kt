package org.gudelker.expressions

import org.gudelker.operators.Operator

data class Binary(
    val leftExpression: ExpressionStatement,
    val operator: Operator,
    val rightExpression: ExpressionStatement,
) : ExpressionStatement
