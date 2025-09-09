package org.gudelker.expressions

import org.gudelker.compare.operators.Comparator

data class BooleanExpression(
    val left: ExpressionStatement,
    val comparator: Comparator,
    val right: ExpressionStatement,
) : BooleanExpressionStatement
